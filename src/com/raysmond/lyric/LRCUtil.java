package com.raysmond.lyric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

import com.raysmond.internet.JsoupTest;

public class LRCUtil {
	
	public static ArrayList<LRCSearchResult> searchLRCFromBaidu(String title,String artist,int number){
		ArrayList<LRCSearchResult> results = new ArrayList<LRCSearchResult>();
		results = JsoupTest.getSearchResultFromBaidu(title,artist,number);
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
}
