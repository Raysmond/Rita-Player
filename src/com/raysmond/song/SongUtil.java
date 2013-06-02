package com.raysmond.song;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import com.raysmond.internet.JsoupTest;


public class SongUtil {
	
	public static ArrayList<DownloadSong> searchSong(String title,String artist,int number){
		return JsoupTest.getSongSearchResultFromBaidu(title, artist, number);
	}
	
	public static DefaultTableModel getResultTableModel(ArrayList<DownloadSong> results){
		String[] columnName = {"编号","标题","艺术家","专辑","大小"};
		
		DefaultTableModel model = new DefaultTableModel(null,columnName){
			private static final long serialVersionUID = -698538350695277049L;
			public boolean isCellEditable(int row, int column) { 
	            return false;
	        }
		}; 
		model.setNumRows(0);
		int counter = 0;
		Iterator<DownloadSong> iter = results.iterator();
		while(iter.hasNext()){
			DownloadSong next = iter.next();
			Vector row = new Vector();
			row.add(++counter);
			row.add(next.getTitle());
			row.add(next.getArtist());
			row.add(next.getAlbumTitle());
			row.add(next.getFileSize());
			model.addRow(row);
		}
		return model;
	}
}
