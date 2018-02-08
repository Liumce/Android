# Android
基于网络媒体库实现音乐播放器


2.1、第一阶段功能要求
[1]	数据源来自扫描存储卡MP3音乐获得的列表
[2]	播放列表显示音乐文件(音乐名)
[3]	利用自定义Service类来实现音乐的播放与暂停以及进度的更新
[4]	界面类(Activity)与服务类(Service)之间通过广播接收者进行通信
[5]	主界面包括播放列表、可设置播放模式、显示当前音乐名、拖拽条显示和设置播放进度、显示播放进度值和播放时长、包含上一首按钮、下一首按钮、播放|暂停按钮

public class SplashScreenActivity extends Activity {
    /**
     * 动画对象
     */
    private Animation animation;
    /**
     * 音乐图标图像控件
     */
    private ImageView ivMusicIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 利用布局资源文件设置用户界面
        setContentView(R.layout.activity_splash_screen);

        // 通过资源标识获得控件实例
        ivMusicIcon = (ImageView) findViewById(R.id.iv_music_icon);

        // 加载动画资源文件，创建动画对象
        animation = AnimationUtils.loadAnimation(this, R.anim.animator);
        // 让音乐图标图像控件启动动画
        ivMusicIcon.startAnimation(animation);
        // 给动画对象设置监听器
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 启动主界面
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                // 关闭启动界面
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}


2.2、第二阶段功能要求
[1]	数据源来自媒体库音频
[2]	列表显示音乐文件(专辑封面图片、音乐名、演唱者、播放时长)
[3]	音乐列表可按音乐标识符、标题或播放时长排序
[4]	利用自定义Service类来实现音乐的播放与暂停以及进度的更新
[5]	界面类(Activity)与服务类(Service) 之间通过广播接收者进行通信
[6]	主界面包含播放播放列表、可设置播放模式、显示当前音乐名、拖拽条显示和设置播放进度、显示播放进度值和播放时长、包含上一首按钮、下一首按钮、播放 | 暂停按钮

/**
 * 音乐适配器
 * Created by lenovo on 2018/1/22.
 */

public class MusicAdapter extends BaseAdapter implements AppConstants {
    /**
     * 上下文环境
     */
    private Context context;
    /**
     * 音乐列表
     */
    private List<Music> musicList;
    /**
     * 访问网络乐库应用程序
     */
    private AccessWebMusicStoreApplication app;

    /**
     * 构造方法
     *
     * @param context
     * @param musicList
     */
    public MusicAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
        // 获取访问网络乐库应用程序对象
        app = (AccessWebMusicStoreApplication) ((Activity) context).getApplication();
    }

    /**
     * 获取列表项个数
     */
    @Override
    public int getCount() {
        return musicList.size();
    }

    /**
     * 获取列表项对象
     */
    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    /**
     * 获取列表项标识符
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 声明视图容器
        ViewHolder holder;

        // 判断转换视图是否为空
        if (convertView == null) {
            // 将音乐列表项模板映射成转换视图
            convertView = LayoutInflater.from(context).inflate(R.layout.music_list_item, null);
            // 实例化视图容器
            holder = new ViewHolder();
            // 获取视图容器各控件实例
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvMusicName = (TextView) convertView.findViewById(R.id.tv_music_name);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
            holder.tvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
            // 将视图容器附加到转换视图
            convertView.setTag(holder);
        } else {
            // 从转换视图里获取视图容器
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取音乐实体作为数据源
        Music music = musicList.get(position);
        // 设置音乐专辑图片
        if (app.getAlbums()[position] != null) {
            holder.ivIcon.setImageBitmap(app.getAlbums()[position]);
        } else {
            holder.ivIcon.setImageResource(R.mipmap.music);
        }

        // 设置音乐名
        holder.tvMusicName.setText(music.getTitle());
        // 设置演唱者
        holder.tvArtist.setText(music.getArtist());
        // 设置音乐时长
        holder.tvDuration.setText(app.getFormatTime(music.getDuration()));
        // 返回转换视图
        return convertView;
    }
    /**
     * 视图容器
     */
    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvMusicName;
        TextView tvDuration;
        TextView tvArtist;
    }

}



2.3、第三阶段功能要求
[6]	数据源来自服务器音乐库(数据库保存专辑图片与音乐路径)
[7]	列表显示音乐文件(专辑封面图片、音乐名、演唱者、播放时长)
[8]	音乐列表可按音乐标识符、标题或播放时长排序
[9]	利用自定义Service类来实现音乐的播放与暂停以及进度的更新
[10]	界面类(Activity)与服务类(Service)之间通过广播接收者进行通信
[11]	主界面包含播放列表、了设置播放模式、显示当前音乐名、拖拽条、显示和设置播放进度、显示播放进度值和播放时长、包含上一首按钮、下一首按钮、播放|暂停按钮
前台客户端： 基于网络乐库音乐播放器V0.7(安卓)
后台服务器端：MusicServier(Web)



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


该音乐播放器分成三个阶段完成，代码中是第三阶段整合前两个阶段完成的。效果基本实现。
