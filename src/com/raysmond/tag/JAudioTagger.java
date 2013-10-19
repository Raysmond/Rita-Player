package com.raysmond.tag;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import com.raysmond.util.Util;


/**
 * 读取mp3的TAG信息、头信息
 * 主要给Song的实例提供TAG信息和头信息。
 * 使用了开源的TAG处理包：JAudioTagger
 * @author Jiankun Lei
 *
 */
public class JAudioTagger implements TagInfo {
	
	private static final long serialVersionUID = -5824299267341947671L;
	
	AudioFile audioFile = null;
	Tag tag = null;
	AudioHeader audioHeader = null;
	
	protected String title = null;
	protected String artist = null;
	protected String album = null;
	protected String year = null;
	protected String comment = null;   
	protected String type = null;           //类型
	protected String location = null;       //歌曲位置
	protected String track = null;  
	protected int trackLength = -1;         //时长，以track计算
	protected String duration = "00:00";    //歌曲时长，字符串显示，比如03：56
	protected long size = -1;               //文件大小
	protected int bitRate = -1; 
	protected String channel = null;
	protected int sampleRate = -1;    		//每帧采样数
	protected String format = null;   		//格式。不同的格式，sampleRate不一样
	
	public JAudioTagger(File file){
		try {
			audioFile = AudioFileIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tag = audioFile.getTag();
		audioHeader = audioFile.getAudioHeader();
		
		readTag();
		readHeader();
	}
	
	public JAudioTagger(){
		
	}
	
	@Override
	public void load(File input) throws UnsupportedAudioFileException,
			IOException {
		try {
			audioFile = AudioFileIO.read(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tag = audioFile.getTag();
		audioHeader = audioFile.getAudioHeader();
		readTag();
	}
	
	/**
	 * 读取一个mp3文件的TAG信息、头信息
	 * @param file
	 */
	public void loadMP3File(File file){
		if(!file.getName().endsWith(".mp3")) return;
		MP3File f = null;
		try {
			f = (MP3File) AudioFileIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		MP3AudioHeader mp3AudioHeader = (MP3AudioHeader) f.getAudioHeader();
		trackLength = mp3AudioHeader.getTrackLength();
		duration = mp3AudioHeader.getTrackLengthAsString();
		channel = mp3AudioHeader.getChannels();
		bitRate = (int) mp3AudioHeader.getBitRateAsNumber();
		sampleRate = mp3AudioHeader.getSampleRateAsNumber();
		format = mp3AudioHeader.getFormat();
		
		audioFile = f;
		tag = f.getTag();
		readTag();
	}
	
	public void readTag(){
		if(tag==null) return;
		artist = (tag.getFirst(FieldKey.ARTIST));
		album = (tag.getFirst(FieldKey.ALBUM));
		title = (tag.getFirst(FieldKey.TITLE));
		comment = (tag.getFirst(FieldKey.COMMENT));
		year = (tag.getFirst(FieldKey.YEAR));
		track = tag.getFirst(FieldKey.TRACK);
		tag.getFirst(FieldKey.DISC_NO);
		tag.getFirst(FieldKey.COMPOSER);
		tag.getFirst(FieldKey.ARTIST_SORT);
	}
	
	public void readHeader(){
		if(audioHeader==null) return;
		channel = audioHeader.getChannels();
		bitRate = (int) audioHeader.getBitRateAsNumber();
		trackLength = audioHeader.getTrackLength();
		sampleRate = audioHeader.getSampleRateAsNumber();
		format = audioHeader.getFormat();
	}
	
	/**
	 * 静态方法，获取歌曲专辑封面
	 * @param sourceFile
	 * @return
	 */
	public static Image getAlbumArt(File sourceFile){
		if(!sourceFile.getName().endsWith(".mp3")) return null;
		MP3File mp3file = null;
		Image img = null;
		try {
			mp3file = new MP3File(sourceFile);
		} catch (Exception e) {
			e.printStackTrace();
		}  
        AbstractID3v2Tag tag = mp3file.getID3v2Tag();  
        AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");  
        FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();  
        byte[] imageData = body.getImageData();  
        img = Toolkit.getDefaultToolkit().createImage(imageData, 0,imageData.length); 
        return img;
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
		song.append("track: " + getTrack() + "\n");
		song.append("bitrate: " + getBitRate() + "\n");
		return song.toString();
	}

	public int getSize() {
		return -1;
	}

	@Override
	public void load(InputStream input) {
		
	}


	@Override
	public void load(URL input) {
		
	}

	@Override
	public String getType() {
		return null;
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

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getFileName() {
		return null;
	}

	@Override
	public String getChannels() {
		return channel;
	}

	@Override
	public int getBitRate() {
		return bitRate;
	}
	
	@Override
	public int getSampleRate() {
		return sampleRate;
	}

	@Override
	public String getFormat() {
		return format;
	}
	
}
