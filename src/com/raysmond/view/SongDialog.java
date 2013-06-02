package com.raysmond.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.raysmond.song.Song;

public class SongDialog extends JDialog implements ActionListener{
	
	Song song = null;
	JFrame owner = null;
	
	JTextField fieldFileName = new JTextField(255);
	JTextField fieldTitle = new JTextField(64);
	JTextField fieldArtist = new JTextField(64);
	JTextField fieldAlbum = new JTextField(64);
	JTextField fieldYear = new JTextField(64);
	JTextField fieldComment = new JTextField(64);
	JTextField fieldDuration = new JTextField(64);
	JTextField fieldSize = new JTextField(30);
	
	JLabel labelFileName = new JLabel("文件名");
	JLabel labelTitle = new JLabel("歌曲名");
	JLabel labelArtist = new JLabel("歌手");
	JLabel labelAlbum = new JLabel("专辑");
	JLabel labelYear = new JLabel("年份");
	JLabel labelComment = new JLabel("备注");
	JLabel labelSize = new JLabel("大小");
	
	JButton butOk = new JButton("确定");
	JButton butCancel = new JButton("取消");
	
	public SongDialog(JFrame owner,boolean model){
		super(owner,model);
		setLayout(null);
		init();
	}
	public void init(){
		setSize(400,260);
		setLocationRelativeTo(owner);
		setComponentBounds();
		addComponent();
	}
	
	public void setComponentBounds(){
		labelFileName.setBounds(5, 10, 40, 24);
		labelTitle.setBounds(5, 40, 40, 24);
		labelArtist.setBounds(5, 70, 40, 24);
		labelAlbum.setBounds(5, 100, 40, 24);
		labelYear.setBounds(5, 130, 40, 24);
		labelComment.setBounds(5, 160, 40, 24);
		labelSize.setBounds(5, 190, 40, 24);
		
		fieldFileName.setBounds(60,10,300,24);
		fieldTitle.setBounds(60, 40, 200, 24);
		fieldArtist.setBounds(60, 70, 200, 24);
		fieldAlbum.setBounds(60, 100, 200, 24);
		fieldYear.setBounds(60, 130, 200, 24);
		fieldComment.setBounds(60, 160, 200, 24);
		fieldSize.setBounds(60, 190, 200, 24);
	}
	
	public void addComponent(){
		add(labelFileName);
		add(labelTitle);
		add(labelAlbum);
		add(labelArtist);
		add(labelYear);
		add(labelComment);
		add(labelSize);
		
		add(fieldFileName);
		add(fieldTitle);
		add(fieldAlbum);
		add(fieldArtist);
		add(fieldYear);
		add(fieldComment);
		add(fieldSize);
	}
	
	public void showDialog(Song song){
		setSong(song);
		setVisible(true);
	}
	
	
	public void setSong(Song song){
		this.song = song;
		fieldFileName.setText(this.song.getLocation());
		fieldTitle.setText(this.song.getTitle());
		fieldArtist.setText(this.song.getArtist());
		fieldAlbum.setText(this.song.getAlbum());
		fieldYear.setText(this.song.getYear());
		fieldComment.setText(this.song.getComment());
		fieldSize.setText(String.valueOf(this.song.getSizeByMb()) + " MB");
	}
	
	
	public Song getSong(){
		return song;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==butOk){
			
		}
		
	}
}
