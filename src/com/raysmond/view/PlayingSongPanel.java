package com.raysmond.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


import com.raysmond.song.Song;
import com.raysmond.tag.JAudioTagger;
import com.raysmond.util.Util;

/**
 * 用于显示正在播放的歌曲信息
 * 信息: 名字,歌手,专辑封面
 * @author Jiankun Lei
 *
 */
public class PlayingSongPanel extends JPanel {
	private static final long serialVersionUID = 9210352850971415856L;
	
	Song song = null;
	JLabel album = null;
	JLabel labelTitle = new JLabel("歌曲标题");
	JLabel labelArtist = new JLabel("歌手");
	JLabel labelAlbumName = new JLabel("专辑");
	
	private Color infoColor = Util.getThemeDefaultColor();
	private Color titleColor = Util.getThemeHighlightColor();
	
	public static int WIDTH = 300;
	public static int HEIGHT = 120;
	
	public PlayingSongPanel(){
		init();
		showInfo(null);
	}
	
	public PlayingSongPanel(Song song){
		init();
		showInfo(song);
	}
	
	/**
	 * 显示一首正在播放的歌曲入口
	 * @param song
	 */
	public void showInfo(Song song){
		setSong(song);
		loadSongInfo();
		loadDefaultAlbumImage();
		loadAlbumImage();
	}
	
	/**
	 * 加载正在播放的歌曲的标题,歌手,专辑
	 */
	private void loadSongInfo() {
		labelTitle.setText(null);
		if(song==null)return;
		if(song.getTitle()!=null&&!song.getTitle().isEmpty()){
			labelTitle.setText(song.getTitle());
		}
		else{
			labelTitle.setText(Song.getFileNameNoEx(song.getFileName()));
		}
		labelTitle.setToolTipText(Song.getFileNameNoEx(song.getFileName()));
		String artist = song.getArtist();
		if(artist!=null){
			if(!artist.isEmpty()) artist = "歌手：" + artist;
			else artist = "歌手：未知";
		}
		else artist = "歌手：未知";
		String albumName = song.getAlbum();
		if(albumName!=null){
			if(!albumName.isEmpty()) albumName = "专辑：" + albumName;
			else albumName = "歌手：未知";
		}
		else albumName = "歌手：未知";
		labelArtist.setText(artist);
		labelAlbumName.setText(albumName);
		
		//设置提示信息，主要是考虑到有时专辑名字太长
		labelAlbumName.setToolTipText(albumName);
		
	}

	public void init(){
		this.setOpaque(false);
		setLayout(null);
		setSize(WIDTH,HEIGHT);
		
		album = new JLabel();
		JPanel albumPanel = new JPanel();
		albumPanel.setOpaque(false);
		
		final JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		albumPanel.add(album);
		titlePanel.add(labelTitle);
		titlePanel.add(labelArtist);
		titlePanel.add(labelAlbumName);
		
		albumPanel.setBounds(10, 1, 100, 100);
		titlePanel.setBounds(120, 0, 180, 100);
		
		labelAlbumName.setPreferredSize(new Dimension(180,24));
		labelAlbumName.setFont(Util.songInfoFont);
		
		labelArtist.setPreferredSize(new Dimension(180,24));
		labelArtist.setFont(Util.songInfoFont);
		
		labelTitle.setPreferredSize(new Dimension(180,24));
		labelTitle.setFont(Util.songTitleFont);
		labelTitle.setForeground(titleColor);
		labelTitle.setVisible(true);
		
		labelArtist.setForeground(infoColor);
		labelAlbumName.setForeground(infoColor);
		
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		titlePanel.setLayout(flow);
		
		add(albumPanel);
		add(titlePanel);
		
		album.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				labelTitle.setForeground(Color.red);
				}
			public void mouseExited(MouseEvent arg0) {
				labelTitle.setForeground(Util.songTitleColor);
				}
			});
	}
	
	/**
	 * 加载专辑图片
	 */
	public void loadAlbumImage(){
		Image img = null;
		try{
			img = JAudioTagger.getAlbumArt(new File(song.getLocation()));
			}catch(Exception e){
			}
		
		//上面可能获取不到专辑图片，或者有异常抛出
		if(img==null) {return;}
		
		//改变图片尺寸
		img = fitSize(img,Util.albumHeight);
		album.setIcon(new ImageIcon(img));
	}
	

	/**
	 * 根据高度按比例改变图片尺寸
	 * @param src
	 * @param newHeight
	 * @return
	 */
	public static Image fitSize(Image src, int newHeight){
		return scaleImage(src,((double)newHeight) / (double)toBufferedImage(src).getHeight());
	}
	
	/**
	 * 根据比例改变图片尺寸
	 * @param src
	 * @param scale
	 * @return
	 */
	public static Image scaleImage(Image src, double scale){
		Image des = null;
		BufferedImage bufferImg = toBufferedImage(src);
		int width = (int) (bufferImg.getWidth() * scale);
		int height = (int) (bufferImg.getHeight() * scale);
		des = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		return des;
	}
	/**
	 * 把一个Image类型的对象转为BufferedImage类型对象
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
		    return (BufferedImage)image;
		 }
		 image = new ImageIcon(image).getImage();
		 BufferedImage bimage = null;
		 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
		     int transparency = Transparency.OPAQUE;
		     GraphicsDevice gs = ge.getDefaultScreenDevice();
		     GraphicsConfiguration gc = gs.getDefaultConfiguration();
		     bimage = gc.createCompatibleImage(
			 image.getWidth(null), image.getHeight(null), transparency);
		 } catch (HeadlessException e) {
		 }

		if (bimage == null) {
		    int type = BufferedImage.TYPE_INT_RGB;
		     bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		 }

		 Graphics g = bimage.createGraphics();

		 g.drawImage(image, 0, 0, null);
		 g.dispose();
		return bimage;
	}
	
	/**
	 * 从默认的专辑文件夹中随机选取一张
	 * @return
	 */
	public ImageIcon randomDefaultAlbum(){
		//列出默认专辑里面的所有文件
		File[] files = Util.albumDir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith("jpg");  
			}});
		
		System.out.println("默认的专辑数："+files.length);
		if(files.length==0) return null;
		int ramIndex = (int)(Math.random()*(files.length-1));
		System.out.println("随机选择的专辑是：" + files[ramIndex].getPath());
		return new ImageIcon(files[ramIndex].getPath());
	}
	/**
	 * 加载默认专辑封面
	 */
	public void loadDefaultAlbumImage(){
		ImageIcon defaultAlbumImage = randomDefaultAlbum();
		album.setIcon(defaultAlbumImage);
	}
	
	
	public boolean isSetSong(){
		if(song!=null) return true;
		return false;
	}
	
	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}
	
	public void changeTheme(){
		titleColor = Util.getThemeHighlightColor();
		infoColor = Util.getThemeDefaultColor();
		if(labelTitle!=null) labelTitle.setForeground(titleColor);
		if(labelArtist!=null) labelArtist.setForeground(infoColor);
		if(labelAlbumName!=null) labelAlbumName.setForeground(infoColor);
	}
}
