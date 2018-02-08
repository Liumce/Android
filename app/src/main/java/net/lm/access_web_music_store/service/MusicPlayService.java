package net.lm.access_web_music_store.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import net.lm.access_web_music_store.R;
import net.lm.access_web_music_store.app.AccessWebMusicStoreApplication;
import net.lm.access_web_music_store.app.AppConstants;
import net.lm.access_web_music_store.entity.Music;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * 功能：音乐播放服务类
 * Created by lenovo on 2018/1/19.
 */
public class MusicPlayService extends Service implements AppConstants {
    /**
     * 媒体播放器
     */
    private MediaPlayer mp;
    /**
     * 音乐列表（数据源）
     */
    private List<Music> musicList;
    /**
     * 音乐文件名
     */
    private String musicName;
    /**
     * 更新音乐播放进度的线程
     */
    private Thread thread;
    /**
     * 线程循环控制变量
     */
    private boolean isRunning;
    /**
     * 音乐播放器应用程序
     */
    private AccessWebMusicStoreApplication app;
    /**
     * 音乐广播接收器
     */
    private MusicReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取获得音乐播放器应用程序对象
        app = (AccessWebMusicStoreApplication) getApplication();

        // 获取音乐列表（数据源）
        musicList = app.getMusicList();

        // 创建媒体播放器
        mp = new MediaPlayer();
        // 给媒体播放器起注册完成监听器
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 切换到下一首音乐
                nextMusic();
            }
        });


        // 设置线程循环控制变量为真
        isRunning = true;
        // 创建线程更新播放进度
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    // 判断音乐是否在播放
                    if (mp.isPlaying()) {
                        // 设置音乐当前播放位置
                        app.setCurrentPosition(mp.getCurrentPosition());
                        // 创建意图
                        Intent intent = new Intent();
                        // 设置广播频道：更新播放进度
                        intent.setAction(INTENT_ACTION_UPDATE_PROGRESS);
                        // 让意图携带播放时长
                        intent.putExtra(DURATION, mp.getDuration());
                        // 让意图携带控制图标（暂停图标）
                        intent.putExtra(CONTROL_ICON, R.drawable.pause_button_selector);
                        // 按意图发送广播
                        sendBroadcast(intent);
                    }
                    // 让线程睡眠500毫秒
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 启动线程
        thread.start();

        // 创建音乐广播接收者
        receiver = new MusicReceiver();
        // 创建意图过滤器
        IntentFilter filter = new IntentFilter();
        // 通过意图过滤器添加广播频道
        filter.addAction(INTENT_ACTION_PLAY_OR_PAUSE);
        filter.addAction(INTENT_ACTION_PLAY);
        filter.addAction(INTENT_ACTION_PREVIOUS);
        filter.addAction(INTENT_ACTION_NEXT);
        filter.addAction(INTENT_ACTION_USER_CHANCG_PROGRESS);
        // 注册广播接收者
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 返回非粘性服务
        return Service.START_NOT_STICKY;
    }

    /**
     * 上一首音乐
     */
    private void previousMusic() {
        // 更新音乐索引
        if (app.getCurrentMusicIndex() > 0) {
            app.setCurrentMusicIndex(app.getCurrentMusicIndex() - 1);
        } else {
            app.setCurrentMusicIndex(musicList.size() - 1);
        }
        // 当前播放位置归零
        app.setCurrentPosition(0);
        // 调用播放方法
        play();
    }

    /**
     * 下一首音乐
     */
    private void nextMusic() {
        // 根据播放模式来更新音乐索引
        switch (app.getPlayMode()) {
            // 顺序播放模式
            case PLAY_MODE_ORDER:
                if (app.getCurrentMusicIndex() < musicList.size() - 1) {
                    app.setCurrentMusicIndex(app.getCurrentMusicIndex() + 1);
                } else {
                    app.setCurrentMusicIndex(0);
                }
                break;
            // 随机播放模式
            case PLAY_MODE_RANDOM:
                // 随机设置索引
                app.setCurrentMusicIndex(new Random().nextInt(app.getMusicList().size()));
                break;
            // 单曲循环模式
            case PLAY_MODE_LOOP:
                // 音乐索引保持不变
                break;
        }
        // 当前播放位置归零
        app.setCurrentPosition(0);
        // 调用播放方法
        play();
    }

    /**
     * 播放方法
     */
    private void play() {
        try {
            // 重置播放器
            mp.reset();
            // 获取当前播放的音乐名
            musicName = musicList.get(app.getCurrentMusicIndex()).getData();
            // 设置播放源
            mp.setDataSource(musicName);
            // 缓冲播放源，加载到内存
            mp.prepare();
            // 定位到暂停时的播放位置
            mp.seekTo(app.getCurrentPosition());
            // 启动音乐的播放
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停方法
     */
    private void pause() {
        // 暂停播放
        mp.pause();
        // 保存当前音乐播放位置
        app.setCurrentPosition(mp.getCurrentPosition());
        /* 发送广播给前台MainActivity，更改图标、更改播放进度 */
        // 创建意图
        Intent intent = new Intent();
        // 设置广播频道：更新播放进度
        intent.setAction(INTENT_ACTION_UPDATE_PROGRESS);
        // 让意图携带播放时长
        intent.putExtra(DURATION, mp.getDuration());
        // 让意图携带控制图标（播放图标）
        intent.putExtra(CONTROL_ICON, R.drawable.play_button_selector);
        // 按意图发送广播
        sendBroadcast(intent);
    }

    /**
     * 音乐广播接收者
     */
    private class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取意图动作（广播频道）
            String action = intent.getAction();
            // 当广播频道非空时进行判断
            if (action != null) {
                // 根据不同广播频道执行不同的操作
                switch (action) {
                    case INTENT_ACTION_PLAY:
                        // 播放进度值归零
                        app.setCurrentPosition(0);
                        // 调用播放方法
                        play();
                        break;
                    case INTENT_ACTION_PLAY_OR_PAUSE:
                        // 判断音乐是否在播放
                        if (mp.isPlaying()) {
                            pause();
                        } else {
                            play();
                        }
                        break;
                    case INTENT_ACTION_PREVIOUS:
                        // 切换到上一首音乐
                        previousMusic();
                        break;
                    case INTENT_ACTION_NEXT:
                        // 切换到下一首音乐
                        nextMusic();
                        break;
                    case INTENT_ACTION_USER_CHANCG_PROGRESS:
                        // 根据拖拽条的进度值计算当前播放位置
                        app.setCurrentPosition(app.getProgressChangedByUser() * mp.getDuration() / 100);
                        // 根据音乐当前播放位置开始播放音乐
                        play();
                        break;
                }
            }
        }
    }

    /**
     * 销毁回调方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放媒体播放器
        if (mp != null) {
            mp.release();
            mp = null;
        }
        // 注销广播接收者
        unregisterReceiver(receiver);
        // 设置线程循环控制变量
        isRunning = false;
        // 销毁子线程
        thread = null;
    }
}
