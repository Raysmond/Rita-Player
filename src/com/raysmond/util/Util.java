package com.raysmond.util;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * 通用类
 * 由于软件比较简单，功能不是很复杂。通用类的功能也不太多，所以基本通用的功能都放在这一个类中，
 * 而没有细分了。
 * @author Jiankun Lei
 *
 */
public class Util {
	public static final String windowTitle = "Rita Player 1.1 - 2013";
	public static final ImageIcon windowTitleIcon = new ImageIcon("./res/logo.png");
	
	//所有可选主题
	public static final String[] themes = {
		"blue_sky.png","bg8.jpg","white_sky.png","duomi.jpg",    //自然风景类
		"5.jpg","4.jpg","9.jpg","11.jpg",						 //红色系
		"7.jpg","8.jpg","12.jpg","3.jpg",						 //蓝、绿色系
		"2.jpg","6.jpg","10.jpg","13.jpg"};						 //其他色系
	
	public static int currentTheme = 0;							 //当前选择的主题
	public static final String themeDir = "./res/themes/";		 //主题背景存放目录
	
	public static final String configFilePath = "./data/config.dat";
	public static Config config = new Config();
	
	public static final Color defaultColor = Color.BLACK;
	public static final Color whiteColor = new Color(217,217,217);
	public static final Color[] themeDefaultColor = {
		  whiteColor,whiteColor,defaultColor,whiteColor,
		  defaultColor,defaultColor,defaultColor,defaultColor,
		  whiteColor,whiteColor,whiteColor,defaultColor,
		  defaultColor,whiteColor,defaultColor,defaultColor
	};
	public static final Color defaultHighlightColor = new Color(150,2,12);
	public static final Color[] themeHighlightColor = {
		 defaultHighlightColor,defaultHighlightColor,defaultHighlightColor,defaultHighlightColor,
		 defaultHighlightColor,defaultHighlightColor,defaultHighlightColor,defaultHighlightColor,
		 defaultHighlightColor,defaultHighlightColor,defaultHighlightColor,defaultHighlightColor,
		 defaultHighlightColor,defaultHighlightColor,defaultHighlightColor,defaultHighlightColor
	};
	
	public static final Color defaultListColor = Color.BLACK;
	public static final Color[] themeListColor = {
		 defaultListColor,defaultListColor,defaultListColor,whiteColor,
		 defaultListColor,defaultListColor,defaultListColor,defaultListColor,
		 defaultListColor,defaultListColor,defaultListColor,defaultListColor,
		 defaultListColor,defaultListColor,defaultListColor,defaultListColor,
	};
	
	
	public static final int windowWidth = 900;					 //主窗口宽
	public static final int windowHeight = 600;					 //主窗口高
	
	public static final ImageIcon windowIconClose = new ImageIcon("./res/close_24.png");
	public static final ImageIcon windowIconMinimize = new ImageIcon("./res/minimize_24.png");
	public static final ImageIcon windowIconCloseHover = new ImageIcon("./res/closeHover.png");
	public static final ImageIcon windowIconMinimizeHover = new ImageIcon("./res/minimizeHover.png");
	
	public static final File playList = new File("./data/playList.dat");
	public static final File likeList = new File("./data/likeList.dat");
	
	public static final int defaultImageWidth = 32;
	public static final int defaultImageHeight = 32;
	public static final int albumHeight = 100;
	
	public static final ImageIcon iconPlayOne = new ImageIcon("./res/playOne.png");
	public static final ImageIcon iconPlayOneLoop = new ImageIcon("./res/playOneLoop.png");
	public static final ImageIcon iconPlayList = new ImageIcon("./res/playIncrease.png");
	public static final ImageIcon iconPlayListLoop = new ImageIcon("./res/playListLoop.png");
	public static final ImageIcon iconPlayRandom = new ImageIcon("./res/playRandom.png");
	
	public static final ImageIcon iconBaiduMusic = new ImageIcon("./res/baiduMusic.png");
	public static final ImageIcon iconBaiduMusicHover = new ImageIcon("./res/baiduMusicHover.png");
	
	public static final Font songTitleFont = new Font("Microsoft YaHei",Font.BOLD,12);
	public static final Color songTitleColor = new Color(150,2,12);
	public static final Font songInfoFont = new Font("Microsoft YaHei",Font.PLAIN,12);
	
	public static final File lyricFile = new File("./data/lyric");
	public static final String lyricDirPath = "./data/lyric";
	
	public static final File albumDir = new File("./res/album");
	
	public static Color getThemeDefaultColor(){
		return themeDefaultColor[currentTheme];
	}
	public static Color getThemeHighlightColor(){
		return themeHighlightColor[currentTheme];
	}
	public static Color getThemeListColor(){
		return themeListColor[currentTheme];
	}
	
	public static int getSamplesOfEachFrameByFormat(String format){
		if(format.equalsIgnoreCase("MPEG-1 Layer 3")) return 1152;
		if(format.equalsIgnoreCase("MPEG-1 Layer 2")) return 1152;
		if(format.equalsIgnoreCase("MPEG-1 Layer 1")) return 384;
		
		if(format.equalsIgnoreCase("MPEG-2 Layer 1")) return 384;
		if(format.equalsIgnoreCase("MPEG-2 Layer 2")) return 1152;
		if(format.equalsIgnoreCase("MPEG-2 Layer 3")) return 576;

		if(format.equalsIgnoreCase("MPEG-2.5 Layer 1")) return 384;
		if(format.equalsIgnoreCase("MPEG-2.5 Layer 2")) return 1152;
		if(format.equalsIgnoreCase("MPEG-2.5 Layer 3")) return 576;
		return 0;
	} 
	
	public static Color getSongInfoColor(){
		String theme = Util.themes[Util.currentTheme];
		if(theme.equalsIgnoreCase("blue_sky.png")){
			return new Color(217,217,217);
		}
		else if(theme.equalsIgnoreCase("white_sky.png")){
			return Color.BLACK;
		}
		
		return new Color(150,2,12);
	}
	
	public static Color getSongTitleColor(){
		String theme = Util.themes[Util.currentTheme];
		if(theme.equalsIgnoreCase("blue_sky.png")){
			return Color.WHITE;
		}
		else if(theme.equalsIgnoreCase("white_sky.png")){
			return new Color(150,2,12);
		}
		return new Color(150,2,12);
	}
	
	public static Color getSongSelectedHoverColor(){
		String theme = Util.themes[Util.currentTheme];
		if(theme.equalsIgnoreCase("blue_sky.png")){
			return Color.WHITE;
		}
		else if(theme.equalsIgnoreCase("white_sky.png")){
			return Color.RED;
		}
		return Color.black;
	}
	
	/**
	 * 总持续时间 = 总帧数 * 每帧采样数 / 采样率 （结果为秒）
	 * @param frame
	 * @param format
	 * @param sampleRate
	 * @return
	 */
	public static long translateFrameToSeconds(long frame, String format, int sampleRate){
		long totalSeconds = frame * getSamplesOfEachFrameByFormat(format) / sampleRate;
		return totalSeconds;
	}
	public static String formatMicroseconds(long ms){
		StringBuffer time = new StringBuffer();
		long totalSeconds = ms/1000000;
		
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
			
		long tmp = totalSeconds/(60*60);
		if(tmp>0){
			 hours = (int)tmp;
			 totalSeconds %= (60*60); 
		}
		
		tmp = totalSeconds/(60);
		if(tmp>0){
			minutes = (int)tmp;
			totalSeconds%=(60);
			
		}
		tmp = totalSeconds;
		if(tmp>0){
			seconds = (int)tmp;
		}
		
		if(hours>0) time.append(hours>9?hours:("0" + hours) + ":");
		time.append(minutes>9?minutes:("0" + minutes) + ":");
		time.append(seconds>9?seconds:("0" + seconds));
		return time.toString();
	}
	public static String translateFrameToTime(long frame, String format, int sampleRate){
		StringBuffer time = new StringBuffer();
		int sff = getSamplesOfEachFrameByFormat(format);
		if(sff==0) return null;
		
		long totalSeconds = Util.translateFrameToSeconds(frame, format, sampleRate);
		
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
			
		long tmp = totalSeconds/(60*60);
		if(tmp>0){
			 hours = (int)tmp;
			 totalSeconds %= (60*60); 
		}
		
		tmp = totalSeconds/(60);
		if(tmp>0){
			minutes = (int)tmp;
			totalSeconds%=(60);
			
		}
		tmp = totalSeconds;
		if(tmp>0){
			seconds = (int)tmp;
		}
		
		if(hours>0) time.append(hours>9?hours:("0" + hours) + ":");
		time.append(minutes>9?minutes:("0" + minutes) + ":");
		time.append(seconds>9?seconds:("0" + seconds));
		return time.toString();
	}
	
	public static long tranlateTimeStrToLong(String timeStr){
		 // 因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位  
        // 1:使用：分割 2：使用.分割  
        String[] s = timeStr.split(":");  
        int min = Integer.parseInt(s[0]);  
        String[] ss = s[1].split("\\.");  
        int sec = Integer.parseInt(ss[0]);  
        int mill = Integer.parseInt(ss[1]);  
        return min * 60 * 1000 + sec * 1000 + mill * 10;  
	}
	
	
	public static boolean isLyricPanelClosed = false;
	
	public static boolean isLyricPanelClosed(){
		return isLyricPanelClosed;
	}
	
	public static void setLyricPanelClosed(boolean flag){
		Util.isLyricPanelClosed = flag;
	}
	
	
	/**
	 * Try to set look-and-view with the system's default look-and-view at the beginning
	 */
	public static void initLookAndView(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static HashMap<String,ImageIcon> iconMap = new HashMap<String,ImageIcon>();
	
	
	public static ImageIcon getImageIcon(String name){
		if(name==null||name.isEmpty()){
			return null;
		}
		if(iconMap.containsKey(name)){
			return iconMap.get(name);
		}
		File file = new File("./res/" + name);
		if(!file.exists()){
			System.err.println("Image " + "./res/" + name + " not exists.");
			return null;
		}
		ImageIcon icon = null;
		icon = new ImageIcon("./res/" + name);
		iconMap.put(name, icon);
		return icon;
	}
	
	public static String GBKString(String s){
		try {
			return new String(s.getBytes("iso8859-1"),"gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}
}
