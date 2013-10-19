package com.raysmond.lyric;

/**
 * 歌词类
 * 歌词搜索结果中使用
 * @author Jiankun Lei
 *
 */
public class LRCSearchResult {
	private String songTitle = null;	//歌名
	private String songArtist = null;	//歌手
	private String downloadUrl = null;	//下载链接
	private String lrcText = null;		//歌词内容
	
	public LRCSearchResult(){
		
	}
	
	public LRCSearchResult(String title,String artist,String downloadLink,String content){
		this.songTitle = title;
		this.songArtist = artist;
		this.downloadUrl = downloadLink;
		this.lrcText = content;
	}
	
	public String getSongTitle() {
		return songTitle;
	}


	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}


	public String getSongArtist() {
		return songArtist;
	}


	public void setSongArtist(String songArtist) {
		this.songArtist = songArtist;
	}


	public String getDownloadUrl() {
		return downloadUrl;
	}


	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}


	public String getLrcText() {
		return lrcText;
	}


	public void setLrcText(String lrcText) {
		this.lrcText = lrcText;
	}

}
