package com.raysmond.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.raysmond.lyric.LRCSearchResult;
import com.raysmond.song.DownloadSong;

/**
 * 使用Jsoup这个开源的网页HTML解析器对百度音乐的搜索结果进行解析 功能 从百度音乐搜索一个首歌的歌词，并返回搜索结果
 * 从百度音乐搜索一首歌，并返回搜索结果（解析出标题、歌手、专辑名和mp3下载链接）
 * 
 * @author Jiankun Lei
 * 
 */
public class JsoupTest {

	public static void main(String[] args) {
		getSongSearchResultFromBaidu("单车", "陈奕迅", 5);
	}

	/**
	 * 从百度音乐搜索一首歌，解析搜索页面HTML，得到结果
	 * 
	 * @param title 歌名
	 * @param artist 歌手
	 * @param number 指定需要搜索的结果数
	 * @return
	 */
	public static ArrayList<DownloadSong> getSongSearchResultFromBaidu(
			String title, String artist, int number) {
		ArrayList<DownloadSong> results = new ArrayList<DownloadSong>();
		try {
			System.out.println("searching: " + title);
			Document doc = Jsoup.connect(
					"http://music.baidu.com/search/song?key="
							+ URLEncoder.encode(title, "UTF-8")).get();
			Element s = doc.getElementById("result_container").getElementsByClass("song-list").first();
			Elements songList1 = s.getElementsByTag("ul").first().getElementsByTag("li");
			int counter = 0;
			for (Element e : songList1) {
				Element songInfo = e.getElementsByClass("song-item").first();
				Element titleElement = e.getElementsByClass("song-title")
						.first().getElementsByTag("a").first();
				if (titleElement == null) continue;
				
				// song title
				String titleStr = titleElement.attr("title");
				
				// song id
				String songId = titleElement.attr("href").substring(6);
		
				if (number == 1) { // 得到第一个精确一点的歌词
					if (!titleStr.equalsIgnoreCase(title))
						continue;
				}
				
				Element artistElement = e.getElementsByClass("singer").first().getElementsByTag("a").first();
				Element albumElement = e.getElementsByClass("album-title").first().getElementsByTag("a").first();

				String artistStr = null;
				String albumStr = null;
				
				if (artistElement != null) artistStr = artistElement.attr("title");
				if (albumElement != null) albumStr = albumElement.attr("title");
				
				System.out.println("title:" + titleStr);
				System.out.println("artist:" + artistStr);
				System.out.println("album:" + albumStr);
				System.out.println("id:" + songId);
				
				String downloadUrlPage = "http://music.baidu.com/song/" + songId + "/download";
				System.out.println(downloadUrlPage);
				String downData = null;
				try{
					Document downloadPage = Jsoup.connect(downloadUrlPage).get();
					Element downloadBut = downloadPage.getElementById("download");
					if (downloadBut == null) continue;
					downData = downloadBut.attr("href");
					downData = "http://music.baidu.com" + downData;
				}catch(Exception e1){
					e1.printStackTrace();
					continue;
				}
				System.out.println(downData);
				
				/*
				 * Elements downloads =
				 * downloadPage.getElementById("form").getElementsByTag
				 * ("ul").first().getElementsByTag("li");
				 * 
				 * if(downloads.size()>=2){ downData =
				 * downloads.get(1).attr("data-data");
				 * System.out.println("downData:" +
				 * downloads.get(1).toString()); } else if(downloads.size()==1){
				 * downData = downloads.first().attr("data-data"); }
				 */
				
				String rate = null;
				String link = null;
				if (downData != null) {
					 rate = downData.substring(downData.indexOf("\"rate\":") + 7, downData.indexOf(","));
					 link = downData.substring(downData.indexOf("\"link\":\"") + 8, downData.indexOf("\"}"));
				} else
					continue;

				System.out.println("download:" + downloadUrlPage);
				System.out.println("link:" + link);
				System.out.println("rate:" + rate);

				DownloadSong song = new DownloadSong(titleStr, artistStr,
						(artistStr + " - " + titleStr + ".mp3"), 0, downData,
						albumStr);
				results.add(song);
				counter++;
				if (counter >= number)
					return results;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

}
