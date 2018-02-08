package net.lm.access_web_music_store.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import net.lm.access_web_music_store.entity.Music;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * 访问网络乐库应用程序类
 * Created by lenovo on 2018/1/24.
 */

public class AccessWebMusicStoreApplication extends Application implements AppConstants {
    /**
     * 简单日期格式
     */
    private SimpleDateFormat sdf;
    /**
     * 音乐列表
     */
    private List<Music> musicList;
    /**
     * 当前音乐索引
     */
    private int currentMusicIndex;
    /**
     * 播放模式
     */
    private int playMode;
    /**
     * 专辑图片数组
     */
    private Bitmap[] albums;
    /**
     * 专辑图片索引
     */
    private int albumIndex;

    public int getProgressChangedByUser() {
        return progressChangedByUser;
    }

    public void setProgressChangedByUser(int progressChangedByUser) {
        this.progressChangedByUser = progressChangedByUser;
    }

    /**
     * 用户修改的播放进度
     */
    private int progressChangedByUser;


    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * 音乐播放进度值
     */
    private int currentPosition;

    @Override
    public void onCreate() {
        super.onCreate();
        // 创建简单日期格式对象
        sdf = new SimpleDateFormat("mm:ss");
        // 定义获取音乐列表的网址
        String strGetMusicListUrl = MUSIC_SERVER_URL + "/getMusicList";
        // 执行获取音乐列表异步任务，传入一个参数：获取音乐列表的网址
        new GetMusicListTask().execute(strGetMusicListUrl);
    }

    /**
     * 编码字符串  39
     */

    /**
     * 获取音乐列表异步任务类
     */
    private class GetMusicListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // 定义结果字符串
            String result = "";
            // 创建get请求
            HttpGet request = new HttpGet(params[0]);
            // 创建http客户端
            HttpClient client = new DefaultHttpClient();
            try {
                // 执行get请求，返回响应对象
                HttpResponse response = client.execute(request);
                // 根据响应对象里的状态码判断是否请求成功
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 获取响应数据实体
                    HttpEntity entity = response.getEntity();
                    // 将响应数据实体转换成字符串作为返回值
                    result = EntityUtils.toString(entity, "gbk");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // 返回结果字符串
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 判断结果是否为空
            if (!result.equals("")) {
                /* 将获取json字符串转换成数组列表 */
                // 创建音乐列表
                musicList = new ArrayList<>();
                try {
                    // 基于json字符串创建json数组
                    JSONArray array = new JSONArray(result);
                    // 创建专辑图片数组
                    albums = new Bitmap[array.length()];
                    // 创建json对象
                    JSONObject object;
                    // 遍历json数组
                    for (int i = 0; i < array.length(); i++) {
                        // 获取json数组元素
                        object = array.getJSONObject(i);
                        // 创建音乐实体
                        Music music = new Music();
                        // 设置音乐实体属性
                        music.setId(object.getInt("id"));
                        music.setData(object.getString("data"));
                        music.setTitle(object.getString("title"));
                        music.setArtist(object.getString("artist"));
                        music.setDuration(object.getInt("duration"));
                        music.setAlbum(object.getString("album"));
                        // 执行获取专辑图片异步任务，传入两个参数（专辑图片索引，专辑图片路径）
                        new GetAlbumTask().execute(String.valueOf(i), music.getAlbum());
                        // 将音乐实体添加到音乐列表
                        musicList.add(music);
                    }
                    //读取排序设置
                    SharedPreferences sp = getSharedPreferences(USER_CONFIG, MODE_PRIVATE);
                    String orderBy = sp.getString("order-by","default");
                    if (orderBy.equals("dedault")) {  //默认排序
                        Collections.sort(musicList, new Comparator<Music>() {
                            @Override
                            public int compare(Music lhs, Music rhs) {
                                return (int) (lhs.getId() - rhs.getId());
                            }
                        });
                    } else if (orderBy.equals("title")) { //按标题排序
                        Collections.sort(musicList, new Comparator<Music>() {
                            @Override
                            public int compare(Music lhs, Music rhs) {
                                return lhs.getTitle().compareTo(rhs.getTitle());
                            }
                        });
                    } else if (orderBy.equals("duration")) {  //按音乐时长排序
                        Collections.sort(musicList, new Comparator<Music>() {
                            @Override
                            public int compare(Music lhs, Music rhs) {
                                return lhs.getDuration() - rhs.getDuration();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取专辑图片异步任务
     */
    private class GetAlbumTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            // 获取专辑图片索引
            albumIndex = Integer.parseInt(params[0]);
            // 获取专辑图片路径
            String albumPath = params[1];
            // 声明URL连接
            HttpURLConnection conn = null;
            // 声明位图对象
            Bitmap bitmap = null;
            try {
                // 定义URL对象
                URL url = new URL(MUSIC_SERVER_URL + albumPath);
                // 打开URL连接
                conn = (HttpURLConnection) url.openConnection();
                // 获取响应码
                int responseCode = conn.getResponseCode();
                // 根据响应码执行不同操作
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 由连接获取字节输入流
                    InputStream in = conn.getInputStream();
                    // 利用位图工厂生成位图对象
                    bitmap = BitmapFactory.decodeStream(in);
                } else {
                    Log.d(TAG, "没有得到响应数据。");
                }
                // 设置专辑图片数组元素值
                albums[albumIndex] = bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /**
     * 获取格式化时间
     *
     * @param time
     * @return
     */
    public String getFormatTime(int time) {
        return sdf.format(time);
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public Bitmap[] getAlbums() {
        return albums;
    }

    public int getCurrentMusicIndex() {
        return currentMusicIndex;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setCurrentMusicIndex(int currentMusicIndex) {
        this.currentMusicIndex = currentMusicIndex;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }
}
