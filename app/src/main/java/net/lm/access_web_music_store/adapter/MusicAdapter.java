package net.lm.access_web_music_store.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.lm.access_web_music_store.R;
import net.lm.access_web_music_store.app.AccessWebMusicStoreApplication;
import net.lm.access_web_music_store.app.AppConstants;
import net.lm.access_web_music_store.entity.Music;

import java.util.List;

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
