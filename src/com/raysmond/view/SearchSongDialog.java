package com.raysmond.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


import com.raysmond.internet.DownloadThread;
import com.raysmond.song.DownloadSong;
import com.raysmond.song.SongUtil;

public class SearchSongDialog {
	
	JLabel labelTitle = new JLabel("标题");
	JLabel labelArtist = new JLabel("歌手");
	JTextField fieldTitle = new JTextField(30);
	JTextField fieldArtist = new JTextField(30);
	JTable resultTable = new JTable();
	JDialog dialog = null;
	JButton butSearch = new JButton("搜索");	
	
	String title = null;								//搜索的歌曲名
	String artist = null;								//搜索的歌手
	
	//用于存放搜索到的歌曲
	ArrayList<DownloadSong> results = new ArrayList<DownloadSong>();
	HashMap<String,DownloadSong> downloading = new HashMap<String,DownloadSong>(); 
	
	public SearchSongDialog(){
		if(dialog==null){
			dialog = new JDialog();
			dialog.setSize(405, 300);
			dialog.setTitle("从百度音乐上搜索mp3");
			dialog.setLayout(null);
			dialog.setLocationRelativeTo(null);
			labelTitle.setBounds(5, 5, 30, 24);
			fieldTitle.setBounds(40, 5, 120, 24);
			labelArtist.setBounds(165, 5, 30, 24);
			fieldArtist.setBounds(200, 5, 110, 24);
			butSearch.setBounds(315, 5, 70, 24);
			
			JScrollPane pane = new JScrollPane(resultTable);
			pane.setBounds(5, 35, 380, 220);
			
			dialog.add(labelTitle);
			dialog.add(fieldTitle);
			dialog.add(labelArtist);
			dialog.add(fieldArtist);
			dialog.add(pane);
			dialog.add(butSearch);
			
			final SearchSongDialog sdialog = this;
			
			butSearch.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					results.clear();
					results = SongUtil.searchSong(fieldTitle.getText().trim(), fieldArtist.getText().trim(),10);
					resultTable.setModel(SongUtil.getResultTableModel(results));
					resultTable.validate();
					resultTable.addMouseListener(new MouseHandler(sdialog));
				}
			});
		}
	} 
	
	/**
	 * 显示搜索对话框
	 * @param inTitle
	 * @param inArtist
	 */
	public void showSearchLRCDialog(String inTitle,String inArtist){
		if(inTitle!=null) title = inTitle;
		if(inArtist!=null) artist = inArtist;
		fieldTitle.setText(title);
		fieldArtist.setText(artist);
		dialog.setVisible(true);
	}
	
	public synchronized void completeDownload(DownloadSong song){
		if(!downloading.containsKey(song.getFileName())) return;
		downloading.remove(song.getFileName());
	}
	
	/**
	 * 内部监听器. 双击歌曲进行下载
	 * @author Jiankun Lei
	 *
	 */
	private class MouseHandler extends MouseAdapter {
		private SearchSongDialog parentPanel;

		public MouseHandler(SearchSongDialog parentPanel) {
			this.parentPanel = parentPanel;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getModifiersEx() == 256) { 						//点击右击
			}
			if(e.getClickCount()==2){ 								//双击
				 int row = resultTable.getSelectedRow();
				 DownloadSong result = results.get(row);
				 if(downloading.containsKey(result.getFileName())){
					 System.err.println("The song is downloading...");
					 return;
				 }
				 downloading.put(result.getFileName(), result);
				 System.out.println("点击下载："  + result.getFileName());
				 System.out.println("下载链接："  + result.getDownloadUrl());
				 
				 File file = new File(System.getProperty("user.dir")+"//data//mp3//" + result.getFileName());
				 if(file.exists()){
					 result.setFileName(result.getArtist() + result.getTitle() + "_" + Math.random()*1000 + ".mp3");
				 }
				 
				 //启动下载线程
				 //DownloadThread thread = new DownloadThread(parentPanel, result.getDownloadUrl(), "./data/mp3/",result.getFileName());
				 DownloadThread thread = new DownloadThread(parentPanel, result,System.getProperty("user.dir")+"/data/mp3/");
				 thread.start();
			}
		}
	}
}
