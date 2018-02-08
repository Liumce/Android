package net.lm.access_web_music_store.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import net.lm.access_web_music_store.R;


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
