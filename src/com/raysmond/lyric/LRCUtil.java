package com.raysmond.lyric;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.raysmond.internet.BaiduLyricsDownloader;

public class LRCUtil {
	
	public static ArrayList<LRCSearchResult> searchLRCFromBaidu(String title,String artist,int number){
		ArrayList<LRCSearchResult> results = new ArrayList<LRCSearchResult>();
		BaiduLyricsDownloader download = new BaiduLyricsDownloader();
		results = download.searchLyrics(title,number);
		return results;
	}
	
	public static DefaultTableModel getResultTableModel(ArrayList<LRCSearchResult> results){
		String[] columnName = {"±‡∫≈","±ÍÃ‚","∏Ë ÷"};
		DefaultTableModel model = new DefaultTableModel(null,columnName){
			private static final long serialVersionUID = -6482544884627144995L;
			public boolean isCellEditable(int row, int column) {
	            return false;
	        }
		}; 
		model.setNumRows(0);
		int counter = 0;
		Iterator<LRCSearchResult> iter = results.iterator();
		while(iter.hasNext()){
			LRCSearchResult next = iter.next();
			Vector row = new Vector();
			row.add(++counter);
			row.add(next.getSongTitle());
			row.add(next.getSongArtist());
			model.addRow(row);
		}
		return model;
	}
	
	/**
	 * Save lrc file to local directory
	 * @param dir directory where to save the lrc file
	 * @param lrcTitle the file title
	 * @param lrcText the content of lyric
	 */
	public static void saveLRC(String dir,String lrcTitle, String lrcText) {
		File saveDir = new File(dir);
		try{
			if(!saveDir.exists()){
				saveDir.mkdir();
			}
			File lrcFile = new File(dir + "/" + lrcTitle);
			if (!lrcFile.exists()) {
				lrcFile.createNewFile();
			}
			FileOutputStream fo = new FileOutputStream(lrcFile);
			fo.write(lrcText.getBytes("utf-8"));
			fo.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
}
