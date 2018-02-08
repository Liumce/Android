package net.lm.access_web_music_store.entity;

/**
 * 音乐实体类
 */
public class Music {
    /**
     * 音乐标识
     */
    private Integer id;
    /**
     * 音乐名
     */
    private String musicName;
    /**
     * 音乐数据（路径 + 文件名）
     */
    private String data;
    /**
     * 音乐标题
     */
    private String title;
    /**
     * 演唱者
     */
    private String artist;
    /**
     * 播放时长
     */
    private int duration;
    /**
     * 音乐专辑图片 （路径 + 文件名）
     */
    private String album;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    @Override
    public String toString() {
        return "Music{" +
                "id=" + id +
                ", musicName='" + musicName + '\'' +
                ", data='" + data + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", album='" + album + '\'' +
                '}';
    }
}
