package com.raysmond.song;

/**
 * 歌曲类，用于搜索结果中
 * @author Jiankun Lei
 *
 */
public class DownloadSong {
	private String downloadUrl = null;		//mp3下载链接
	private String fileName = null;		//文件名
	private double fileSize = 0;			//文件大小 
	private String title = null;			//歌名
	private String artist = null;			//歌手
	private String albumTitle = null;		//专辑名
	
	/**
	 * 构造一个歌曲实例
	 */
	public DownloadSong(String title,String artist,String fileName,double fileSize,String downloadUrl,String albumTitle){
		this.downloadUrl = downloadUrl;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.title = title;
		this.artist = artist;
		this.albumTitle = albumTitle;
	}
	
	//getters and setters
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public double getFileSize() {
		return fileSize;
	}
	public void setFileSize(double fileSize) {
		this.fileSize = fileSize;
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

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}
}
