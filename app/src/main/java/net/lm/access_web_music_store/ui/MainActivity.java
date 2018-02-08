package net.lm.access_web_music_store.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.lm.access_web_music_store.R;
import net.lm.access_web_music_store.adapter.MusicAdapter;
import net.lm.access_web_music_store.app.AccessWebMusicStoreApplication;
import net.lm.access_web_music_store.app.AppConstants;
import net.lm.access_web_music_store.entity.Music;
import net.lm.access_web_music_store.service.MusicPlayService;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity implements AppConstants {
    /**
     * 音乐列表控件
     */
    private ListView lvMusic;
    /**
     * 媒体播放器
     */
    private MediaPlayer mp;
    /**
     * 音乐网址字符串
     */
    private String strMusicUrl;
    /**
     * 访问网络乐库应用程序
     */
    private AccessWebMusicStoreApplication app;
    /**
     * 音乐文件名
     */
    private String musicName;
    /**
     * 显示音乐名的标签
     */
    private TextView tvMusicName;
    /**
     * 播放|暂停按钮
     */
    private Button btnPlayOrPause;
    /**
     * 显示当前播放位置的标签
     */
    private TextView tvCurrentPosition;
    /**
     * 显示音乐播放时长的标签
     */
    private TextView tvDuration;
    /**
     * 音乐播放拖拽条
     */
    private SeekBar sbMusicProgress;
    /**
     * 音乐名列表控件
     */
    private ListView lvMusicList;
    /**
     * 音乐列表（数据源）
     */
    private List<Music> musicList;
    /**
     * 音乐适配器
     */
    private MusicAdapter adapter;
    /**
     * 播放模式单选按钮组
     */
    private RadioGroup rgPlayMode;
    /**
     * 音乐广播接收器
     */
    private MusicReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 利用布局资源文件设置用户界面
        setContentView(R.layout.activity_main);

        // 获取访问网络乐库应用程序
        app = (AccessWebMusicStoreApplication) getApplication();

        // 通过资源标识获得控件实例
        lvMusicList = (ListView) findViewById(R.id.lv_music_name);
        tvMusicName = (TextView) findViewById(R.id.tv_music_name);
        btnPlayOrPause = (Button) findViewById(R.id.btn_play_pause);
        tvCurrentPosition = (TextView) findViewById(R.id.tv_current_position);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        sbMusicProgress = (SeekBar) findViewById(R.id.sb_music_progress);
        rgPlayMode = (RadioGroup) findViewById(R.id.rg_play_mode);




        // 获取音乐列表（数据源）
        musicList = app.getMusicList();
        // 判断音乐列表里是否有元素
        if (musicList.size() > 0) {
            // 创建音乐适配器
            adapter = new MusicAdapter(this, musicList);
            // 给音乐列表设置适配器
            lvMusicList.setAdapter(adapter);

            // 给播放模式单选按钮组注册监听器
            rgPlayMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    // 判断用户选择何种播放模式
                    switch (checkedId) {
                        // 顺序播放模式
                        case R.id.rb_order:
                            app.setPlayMode(PLAY_MODE_ORDER);
                            break;
                        // 随机播放模式
                        case R.id.rb_random:
                            app.setPlayMode(PLAY_MODE_RANDOM);
                            break;
                        // 单曲循环模式
                        case R.id.rb_loop:
                            app.setPlayMode(PLAY_MODE_LOOP);
                            break;
                    }
                }
            });
            // 实例化媒体播放器
            mp = new MediaPlayer();

            // 给音乐列表控件注册监听器
            lvMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 获取音乐网址字符串
                    String strMusicUrl = MUSIC_SERVER_URL + musicList.get(position).getData();

                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                    // 重置媒体播放器
                    mp.reset();
                    try {
                        // 设置播放源
                        mp.setDataSource(MainActivity.this, Uri.parse(strMusicUrl));
                        // 缓存播放源（从服务器端下载音乐文件到内存）
                        mp.prepare();
                        // 播放音乐
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            // 给音乐播放拖拽条注册监听器
            sbMusicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 判断进度是否为用户修改
                    if (fromUser) {
                        // 设置用户修改的播放进度
                        app.setProgressChangedByUser(progress);
                        // 创建意图
                        Intent intent = new Intent();
                        // 设置广播频道：用户修改播放进度
                        intent.setAction(INTENT_ACTION_USER_CHANCG_PROGRESS);
                        // 按意图发送广播
                        sendBroadcast(intent);
                        //Toast.makeText(MainActivity.this,progress+"",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            // 启动音乐服务
            startService(new Intent(MainActivity.this, MusicPlayService.class));
            // 创建音乐广播接收器
            receiver = new MusicReceiver();
            // 创建意图过滤器
            IntentFilter filter = new IntentFilter();
            // 通过意图过滤器添加广播频道
            filter.addAction(INTENT_ACTION_UPDATE_PROGRESS);
            // 注册音乐广播接收器
            registerReceiver(receiver, filter);
            // 设置音乐名标签内容，去掉路径和扩展名
            String musicName = app.getMusicList().get(app.getCurrentMusicIndex()).getData();
            int duration = app.getMusicList().get(app.getCurrentMusicIndex()).getDuration();
            tvMusicName.setText("No. " + (app.getCurrentMusicIndex() + 1) + " " + musicName.substring(
                    musicName.lastIndexOf('/') + 1, musicName.lastIndexOf(".")));
            tvCurrentPosition.setText(app.getFormatTime(0));
            tvDuration.setText(app.getFormatTime(duration));
        } else {
            Toast.makeText(this, "网络乐库里没有音乐！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 音乐广播接收者
     */
    private class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取广播频道
            String action = intent.getAction();
            // 判断广播频道是否为空
            if (action != null) {
                // 根据不同广播频道执行不同操作
                if (INTENT_ACTION_UPDATE_PROGRESS.equals(action)) {
                    // 获取播放时长
                    int duration = intent.getIntExtra(DURATION, 0);
                    // 获取播放控制图标
                    int controlIcon = intent.getIntExtra(CONTROL_ICON,
                            R.drawable.play_button_selector);
                    // 计算进度值
                    int progress = app.getCurrentPosition() * 100 / duration;
                    // 获取音乐名
                    musicName = app.getMusicList().get(app.getCurrentMusicIndex()).getMusicName();
                    // 设置正在播放的文件名（去掉扩展名）
                    tvMusicName.setText("No." + (app.getCurrentMusicIndex() + 1) + "  "
                            + musicName.substring(musicName.lastIndexOf("/") + 1, musicName.lastIndexOf(".")));
                    // 设置播放进度值标签
                    tvCurrentPosition.setText(app.getFormatTime(app.getCurrentPosition()));
                    // 设置播放时长标签
                    tvDuration.setText(app.getFormatTime(duration));
                    // 设置播放拖拽条的进度值
                    sbMusicProgress.setProgress(progress);
                    // 设置【播放|暂停】按钮显示的图标
                    btnPlayOrPause.setBackgroundResource(controlIcon);
                }
            }
        }
    }


    /**
     * 创建选项菜单
     * TODO
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 默认排序
            case R.id.action_default_order:
                Collections.sort(app.getMusicList(), new Comparator<Music>() {
                    @Override
                    public int compare(Music lhs, Music rhs) {
                        return (lhs.getId() - rhs.getId());
                    }
                });
                // TODO
                savaDefaultOrderBy("default");
                break;
            // 按标题排序
            case R.id.action_title_order:
                Collections.sort(app.getMusicList(), new Comparator<Music>() {
                    @Override
                    public int compare(Music lhs, Music rhs) {
                        return lhs.getTitle().compareTo(rhs.getTitle());
                    }
                });

                //TODO
                savaDefaultOrderBy("title");
                break;
            // 按时长排序
            case R.id.action_duration_order:
                Collections.sort(app.getMusicList(), new Comparator<Music>() {
                    @Override
                    public int compare(Music lhs, Music rhs) {
                        return (lhs.getDuration() - rhs.getDuration());
                    }
                });
                //
                savaDefaultOrderBy("duration");
                break;
        }
        adapter.notifyDataSetInvalidated();
        return true;
    }


    private void savaDefaultOrderBy(String orderBy) {
        SharedPreferences sp = getSharedPreferences(USER_CONFIG,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("order-by", orderBy);
        editor.commit();
    }


    /**
     * 播放|暂停按钮单击事件处理方法
     *
     * @param view
     */
    public void doPlayOrPause(View view) {
        // 创建意图
        Intent intent = new Intent();
        // 设置广播频道
        intent.setAction(INTENT_ACTION_PLAY_OR_PAUSE);
        // 按意图发送广播
        sendBroadcast(intent);
    }
    /**
     * 上一首音乐按钮单击事件处理方法
     *
     * @param view
     */
    public void doPrevious(View view) {
        // 创建意图
        Intent intent = new Intent();
        // 设置广播频道
        intent.setAction(INTENT_ACTION_PREVIOUS);
        // 按意图发送广播
        sendBroadcast(intent);
    }

    /**
     * 下一首音乐按钮单击事件处理方法
     *
     * @param view
     */
    public void doNext(View view) {
        // 创建意图
        Intent intent = new Intent();
        // 设置广播频道
        intent.setAction(INTENT_ACTION_NEXT);
        // 按意图发送广播
        sendBroadcast(intent);
    }


    /**
     * 销毁方法
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止音乐播放服务
        stopService(new Intent(MainActivity.this, MusicPlayService.class));
        // 注销广播接收者
        unregisterReceiver(receiver);
    }
}
