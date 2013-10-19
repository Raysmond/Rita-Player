package com.raysmond.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.raysmond.lyric.LRCSearchResult;

public class BaiduLyricsDownloader {
	
	private String searchURL = "http://music.baidu.com/search/lrc";
	
	
	/**
	 * Download and get the content of one lyric file.
	 * @param urlStr lyric file URL
	 * @return lyric content
	 */
	public String downloadLRC(String urlStr) {
		String lyric = null;
		URL url = null;
		URLConnection conn = null;
		String nextLine = null;
		try {
			url = new URL(urlStr);
			conn = url.openConnection();
			conn.connect();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader( conn.getInputStream(), "utf-8"));
			StringBuffer buffer = new StringBuffer();
			while ((nextLine = reader.readLine()) != null) {
				buffer.append(nextLine + "\n");
			}
			lyric = new String(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lyric;
	}

	/**
	 * Analyze Baidu lyric search result and get lyrics' content
	 * @param title title of 
	 * @param artist
	 * @param number
	 * @return
	 */
	public ArrayList<LRCSearchResult> searchLyrics(String title, int number) {
		ArrayList<LRCSearchResult> results = new ArrayList<LRCSearchResult>();
		
		System.out.println("searching lyrics: " + title);
		try {
			Document doc = Jsoup.connect(this.searchURL + 
					"?key=" + URLEncoder.encode(title, "UTF-8")).get();
			Element s = doc.getElementById("lrc_list");
			Elements songList1 = s.getElementsByTag("ul").first().getElementsByTag("li");

			int counter = 0;
			for (Element e : songList1) {
				// get title element
				Element titleElement = e.getElementsByClass("song-title")
						.get(0).getElementsByTag("a").first();
				if (titleElement == null) continue;
				String titleStr = titleElement.attr("title");
				
				// get artist list element
				Element artistElement = e.getElementsByClass("artist-title")
						.get(0).getElementsByTag("a").first();
				String artistStr = artistElement.ownText();
				
				
				// get lyric download link
				Element download = e.getElementsMatchingOwnText("œ¬‘ÿLRC∏Ë¥ ").first();
				String linkData = download.className();
				String linkDownload = linkData.substring(linkData.indexOf("data2"),
								linkData.indexOf(".lrc") + 4);
				String downloadLink = "http://music.baidu.com/" + linkDownload;
				// download content
				String content = this.downloadLRC(downloadLink);
				
				System.out.println("result: " + counter);
				System.out.println("title: " + titleStr);
				System.out.println("artist: " + artistStr);
				System.out.println("download link: " + linkDownload);
				System.out.println("lyric content: " + content);
				
				LRCSearchResult lrc = new LRCSearchResult(titleStr,artistStr,linkDownload,content);
				results.add(lrc);
				
				if (++counter == number) break;
			}
		} catch (IOException e) {
			System.err.println("Network error. Search lyrics failed.");
			e.printStackTrace();
		}
		return results;
	}
	
	public static void main(String[] args){
		BaiduLyricsDownloader download = new BaiduLyricsDownloader();
		download.searchLyrics("π‚ª‘ÀÍ‘¬", 10);
	}
}
