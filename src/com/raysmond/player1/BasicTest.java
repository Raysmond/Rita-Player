package com.raysmond.player1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.UIManager;

public class BasicTest extends JFrame implements ActionListener {
	private static final long serialVersionUID = 325456686245128040L;
	
	private BasicController player = new BasicPlayer();
	private JButton butPlay = new JButton("Play");
	private JButton butPause = new JButton("Pause");
	private JButton butBrowse = new JButton("Browse");
	private JButton butResume = new JButton("Resume");
	private JSlider sliderVolume = new JSlider();
	private JLabel labelFile = new JLabel();
	
	private JButton butVolumeIncrease = new JButton("Volume Increase");
	private JButton butVolumeDecrease = new JButton("Volume Decrease");
	private float gain = 0.5F;
	private JFileChooser chooser = null;
	private File file = null;
	
	
	public BasicTest(){
		init();
		setLayout(null);
		setSize(440,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	public void init(){
		
		butPlay.setBounds(5,5,80,24);
		butPause.setBounds(5+100,5,80,24);
		butResume.setBounds(5+200,5,100,24);
		butBrowse.setBounds(5+320,5,80,24);
		labelFile.setBounds(5, 80, 280, 24);
		
		butVolumeIncrease.setBounds(5, 40,150,24);
		butVolumeDecrease.setBounds(5+180, 40,150,24);
		
		butPlay.addActionListener(this);
		butPause.addActionListener(this);
		butBrowse.addActionListener(this);
		butResume.addActionListener(this);
		butVolumeIncrease.addActionListener(this);
		butVolumeDecrease.addActionListener(this);
		
		add(butPlay);
		add(butPause);
		add(butBrowse);
		add(labelFile);
		add(butResume);
		add(butVolumeIncrease);
		add(butVolumeDecrease);
	}
	public void browseFile(){
		if(chooser==null){
			chooser = new JFileChooser();
		}
		int state = chooser.showOpenDialog(this);
		if(state==JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
			labelFile.setText(file.getAbsolutePath());
		}
	}
	public boolean isSetAudio(){
		if(file!=null&&file.exists()) return true;
		else return false;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==butPlay){
			if(!isSetAudio()) return;
			try {
				player.stop();
				player.open(file);
				player.play();
				player.setGain(0.5);
				
			} catch (BasicPlayerException e) {
				e.printStackTrace();
			}
			
		}
		else if(arg0.getSource()==butPause){
			if(!isSetAudio()) return;
			try {
				player.pause();
			} catch (BasicPlayerException e) {
				e.printStackTrace();
			}
		}
		else if(arg0.getSource()==butBrowse){
			browseFile();
		}
		else if(arg0.getSource()==butResume){
			try {
				player.resume();
			} catch (BasicPlayerException e) {
				e.printStackTrace();
			}
		}
		else if(arg0.getSource()==butVolumeDecrease){
			if(gain>0.1)
				try {
					gain-=0.1;
					player.setGain(gain);
				} catch (BasicPlayerException e) {
					e.printStackTrace();
				}
		}
		else if(arg0.getSource()==butVolumeIncrease){
			if(gain<0.9)
				try {
					gain+=0.1;
					player.setGain(gain);
				} catch (BasicPlayerException e) {
					e.printStackTrace();
				}
		}
	}

	public static void main(String[] args) {
		new BasicTest();
	}
	

}
