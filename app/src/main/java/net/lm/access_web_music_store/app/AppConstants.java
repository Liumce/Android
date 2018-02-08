package net.lm.access_web_music_store.app;

/**
 * 应用程序常量接口
 * Created by lenovo on 2018/1/24.
 */

public interface AppConstants {
    /**
     * 应用程序标记
     */
    String TAG = "access_web_music_store";
    /**
     * 音乐服务器网址
     */
    String MUSIC_SERVER_URL = "http://10.0.48.31:8080";
    /**
     * 广播频道常量：播放上一首
     */
    String INTENT_ACTION_PREVIOUS = TAG + ".intent.action.PREVIOUS";
    /**
     * 广播频道常量：播放下一首
     */
    String INTENT_ACTION_NEXT = TAG + ".intent.action.NEXT";
    /**
     * 广播频道常量：播放或暂停
     */
    String INTENT_ACTION_PLAY_OR_PAUSE = TAG + ".intent.action.PLAY_OR_PAUSE";
    /**
     * 广播频道常量：播放
     */
    String INTENT_ACTION_PLAY = TAG + ".intent.action.PLAY";
    /**
     * 广播频道常量：更新播放进度
     */
    String INTENT_ACTION_UPDATE_PROGRESS = TAG + ".intent.action.UPDATE_PROGRESS";
    /**
     * 广播频道常量：用户改变播放进度
     */
    String INTENT_ACTION_USER_CHANCG_PROGRESS = TAG + ".intent.action.USER_CHANCG_PROGRESS";
    /**
     * 控制图标常量：播放或暂停
     */
    String CONTROL_ICON = "control_icon";
    /**
     * 播放时长常量
     */
    String DURATION = "duration";
    /**
     * 播放模式
     */
    String PLAY_MODE = "play_mode";
    /**
     * 播放模式：顺序播放
     */
    int PLAY_MODE_ORDER = 0;
    /**
     * 播放模式：随机播放
     */
    int PLAY_MODE_RANDOM = 1;
    /**
     * 播放模式：单曲循环
     */
    int PLAY_MODE_LOOP = 2;
    /**
     * 用户配置文件名
     */
    String USER_CONFIG = "user-config";
}
