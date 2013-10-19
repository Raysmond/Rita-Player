package com.raysmond.song;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.DecimalFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.raysmond.tag.JAudioTagger;
import com.raysmond.tag.TagInfo;

/**
 * 歌曲类
 * 包含歌曲文件的详细信息
 * @author Jiankun Lei
 *
 */
public class Song implements TagInfo,Comparable {
	
	protected String fileName = null;	    //歌曲对应本地的文件名
	protected String title = null;			//歌名
	protected String artist = null;		//歌手
	protected String album = null;			//专辑名
	protected String year = null;			//发布年份
	protected String comment = null;   	//备注
	protected String type = null;      	//文件类型
	protected String location = null;  	//歌曲本地路径
	protected String track = null;     	//track
	protected long trackLength = -1;		//track长度（时长）
	protected String duration = "00:00";  	//歌曲时长（字符串）
	protected long size = -1;          	//歌曲大小
	protected int bitRate = -1; 			//比特率
	protected String channel = null;		//
	protected String format = null;		//歌曲类型，不同的类型对应的每帧采样数不一样
	protected int sampleRate = -1;			//每帧采样数
	protected boolean like = false;		//是否标注为喜欢
	
	private static final long serialVersionUID = -1579945086126849773L;
	
	public Song(){}
	
	@Override
	public void load(InputStream input) {
		
	}

	/**
	 * 从文件读取歌曲信息，暂时只限制为mp3文件
	 */
	@Override
	public void load(File input) throws IOException, UnsupportedAudioFileException  {
        if (null == input || !input.exists()||!input.getName().endsWith(".mp3"))
        {
            return;
        }
        //加载文件相关信息
        size = input.length();
        location = input.getPath();
        fileName = input.getName();
        
        //加载TAG信息
        loadTag(input);
	}
	
	/**
	 * 从文件读取TAG信息
	 * @param input
	 */
	public void loadTag(File input){
		JAudioTagger tag = null;
		if(input.getName().endsWith(".mp3")){
			tag = new JAudioTagger();
			tag.loadMP3File(input);
			title = tag.getTitle();
			artist = tag.getArtist();
			album = tag.getAlbum();
			year = tag.getYear();
			comment = tag.getComment();
			track = tag.getTrack();
			bitRate = tag.getBitRate();
			channel = tag.getChannels();
			trackLength = tag.getTrackLength();
			duration = tag.getPlayTime();
			sampleRate = tag.getSampleRate();
			format = tag.getFormat();
		}
		
	}
	
	
	/**
	 * 另一个加载TAG信息。直接从歌曲的中读取字节
	 * @param input
	 * @throws IOException
	 */
	public void loadInfo(File input) throws IOException{
		byte[] data = new byte[128];
		RandomAccessFile ran = new RandomAccessFile(input, "r");
        ran.seek(ran.length() - 128);
        ran.read(data);
		if (data.length != 128) {
			throw new RuntimeException("数据长度不合法:" + data.length);
			}
		String tag = new String(data, 0, 3);
		if (tag.equalsIgnoreCase("TAG")) {
			title = new String(data, 3, 30).trim();
			artist = new String(data, 33, 30).trim();
			album = new String(data, 63, 30).trim();
			year = new String(data, 93, 4).trim();
			comment = new String(data, 97, 28).trim();
			//r1 = data[125];
			//r2 = data[126];
			//r3 = data[127];
			} else {}
	}
	
	/**
	 * Java文件操作 获取文件扩展名
	 */
    public static String getExtensionName(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length() - 1))) { 
                return filename.substring(dot + 1); 
            } 
        } 
        return filename; 
    } 
    /**
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length()))) { 
                return filename.substring(0, dot); 
            } 
        } 
        return filename; 
    } 
    
	public String toString(){
		StringBuffer song = new StringBuffer();
		song.append("title: " + getTitle() + "\n");
		song.append("artist: " + getArtist() + "\n");
		song.append("album:" + getAlbum() + "\n");
		song.append("year:" + getYear() + "\n");
		song.append("comment:" + getComment() + "\n");
		song.append("duration: " + getPlayTime() + "\n");
		song.append("size: " + getSize() + "\n");
		song.append("track length: " + getTrackLength() + "\n");
		song.append("sample rate: " + getSampleRate() + "\n");
		song.append("format: " + getFormat() + "\n");
		return song.toString();
	}

	public long getSize() {
		return size;
	}
	public double getSizeByMb(){
		DecimalFormat df = new DecimalFormat("##.00");   
		return Double.parseDouble(df.format((double)this.getSize()/(1024.0*1024.0)));
	}
	@Override
	public void load(URL input) {
		
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getPlayTime() {
		return duration;
	}
	
	public long getTrackLength(){
		return trackLength;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getArtist() {
		return artist;
	}

	@Override
	public String getAlbum() {
		return album;
	}

	@Override
	public String getTrack() {
		return track;
	}

	@Override
	public String getYear() {
		return year;
	}

	@Override
	public String getComment() {
		return comment;
	}

	public String getChannels() {
		return channel;
	}

	@Override
	public int getBitRate() {
		return bitRate;
	}
	
	@Override
	public String getLocation(){
		return location;
	}
	
	@Override
	public String getFileName(){
		return fileName;
	}
    
	@Override
	public int getSampleRate() {
		return sampleRate;
	}

	@Override
	public String getFormat() {
		return format;
	}
	
	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}
	
	public boolean equals(Song song){
		if(this.fileName.equals(song.fileName)){
			return this.getLocation().equals(song.getLocation());
		}
		return false;
	}
	
	@Override
	public int compareTo(Object arg0) {
		if(arg0 instanceof Song){
			if(equals(arg0)) return 0;
		}
		return -1;
	}
}

