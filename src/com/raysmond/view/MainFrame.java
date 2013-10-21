package com.raysmond.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import com.raysmond.util.Config;
import com.raysmond.util.Util;

/**
 * 程序主界面类
 * 主要功能：定义程序界面的框架。
 * 定义和初始化播放面板、播放列表面板、歌词现实面板、以及界面主题。 
 * 实现“最小化”，“窗口移动”，“歌词面板显示与关闭”，“改变主题”。
 * @author Jiankun Lei
 *
 */
public class MainFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 3556851416942605888L;
	
	PlayListPanel panelPlayList = null;			//”歌曲列表“面板
	PlayPanel panelPlay = null;					//”正在播放和控制“面板
	LyricPanel panelLyric = null;				//”歌词显示“面板
	JButton butClose = null;					//“关闭”按钮
	JButton butMinimize = null;					//”最小化“按钮
	JButton butChangeTheme = null;				//改变主题的按钮
	JButton butToggleLyric = null;				//“歌词”显示与否的按钮
	JPanel titleBar = new JPanel();				//自定义标题栏
	
	JDialog themeDialog = null;					//主题选择的对话框
	ButtonGroup groupTheme = null;            	//按钮组
	JRadioButton[] radioThemes;   				//主题选择单选按钮组
	JScrollPane themePane = null;			
	JPanel themeWrapper = null;					
	
	public MainFrame(){
		init();
		setTitle(Util.windowTitle);
		setSize(Util.windowWidth,Util.windowHeight);
		initWindowBar();										//自定义标题栏，有标题、最小化和关闭
		setBackgroundImage();									//设置背景图片
		setLayout(null);										//设置布局
		setUndecorated(true);									//取消窗口默认的边框
		setResizable(false);									//不可改变大小
		setLocationRelativeTo(null);							//窗口居中
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	/**
	 * 加载配置信息
	 */
	public void loadConfig(){
		Config config = Config.loadConfig();
		if(config!=null){
			System.out.println("Current theme：" + config.getCurrentTheme());
			Util.config = config;
			Util.currentTheme = Util.config.getCurrentTheme();
		}
		else{
			Util.config.writeConfig();
		}
	}
	
	public void initConfig(){
		File appSaveDir = new File(Util.savePath);
		File configFile = new File(Util.configFilePath);
		File playListFile = new File(Util.playList);
		File likeListFile = new File(Util.likeList);
		File lyricDir = new File(Util.savePath + "/" + Util.lyricDirPath);
		if(!appSaveDir.exists()){
			appSaveDir.mkdir();
		}
		if(!lyricDir.exists()){
			lyricDir.mkdir();
		}
		try{
			if(!configFile.exists()){
				configFile.createNewFile();
			}
			if(!playListFile.exists()){
				playListFile.createNewFile();
			}
			if(!likeListFile.exists()){
				likeListFile.createNewFile();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		loadConfig();
	}
	
	public void init(){
		initConfig();				//加载主题配置信息
		initPlayPanel();			//初始化播放控制面板
		initPlayListPanel();		//初始化播放列表面板
		initLyricPanel();			//初始化歌词显示面板
		initPanelRelations();		//初始化各面板之间的关系
		System.out.println("Initialzed..");
	}
	
	public void initWindowBar(){
		titleBar = new JPanel(){
			private static final long serialVersionUID = 4756601082702648786L;
			//在标题栏上绘制一层很淡的半透明的渐变
			protected void paintComponent(Graphics g) {
	               if (g instanceof Graphics2D) {
	                   Paint p2 = new GradientPaint(450, 0, new Color(210, 210, 210, 0),
	                		   450, 100, new Color(224, 224, 224, 255), true);
	                   Graphics2D g2d = (Graphics2D)g;
	                   g2d.setPaint(p2);
	                   g2d.fillRect(0, 0, getWidth(), getHeight());
	               
	               } else {
	            	   super.paintComponent(g);
	               }
	           }
		};
		titleBar.setBounds(0, 0, Util.windowWidth, 32);
		titleBar.setLayout(null);
		titleBar.setOpaque(false);
		//titleBar.setBorder(new LineBorder(new Color(121,121,121)));
		JLabel title = new JLabel(Util.windowTitleIcon);
		title.setBounds(0, 0, 150, 32);
		titleBar.add(title);
		add(titleBar);
		
		//设置鼠标按住标题栏拖动窗口位置
		setDragable();
		dragWindow();
		
		//设置透明按钮
		butClose = new JButton(Util.windowIconClose);
		butClose.setBounds(Util.windowWidth-30,5,24,24);
		butClose.setBackground(null);
		butClose.setBorderPainted(false);
		butClose.setFocusPainted(false);
		butClose.setContentAreaFilled(false);
		butClose.setFocusable(true);
		
		butMinimize = new JButton(Util.windowIconMinimize);
		butMinimize.setBounds(Util.windowWidth-30-30,5,24,24);
		butMinimize.setBackground(null);
		butMinimize.setBorderPainted(false);
		butMinimize.setFocusPainted(false);
		butMinimize.setContentAreaFilled(false);
		butMinimize.setFocusable(true);
		
		butChangeTheme = new JButton(new ImageIcon(Util.getImage("res/theme.png")));
		butChangeTheme.setBounds(Util.windowWidth-30-30-30,5,24,24);
		butChangeTheme.setBackground(null);
		butChangeTheme.setBorderPainted(false);
		butChangeTheme.setFocusPainted(false);
		butChangeTheme.setContentAreaFilled(false);
		butChangeTheme.setFocusable(true);
		
		//butToggleLyric = new JButton(new ImageIcon("res/lyric.png"));
		butToggleLyric = new JButton(new ImageIcon(Util.getImage("res/lyric.png")));
		butToggleLyric.setBounds(Util.windowWidth-30-30-30-30,5,24,24);
		butToggleLyric.setBackground(null);
		butToggleLyric.setBorderPainted(false);
		butToggleLyric.setFocusPainted(false);
		butToggleLyric.setContentAreaFilled(false);
		butToggleLyric.setFocusable(true);
		
		//添加事件监听器
		butMinimize.addActionListener(this);
		butClose.addActionListener(this);
		butChangeTheme.addActionListener(this);
		butToggleLyric.addActionListener(this);
		
		titleBar.add(butToggleLyric);
		titleBar.add(butChangeTheme);
		titleBar.add(butClose);
		titleBar.add(butMinimize);
		
		
		//设置按钮鼠标移上去时的效果
		butClose.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				butClose.setIcon(Util.windowIconCloseHover);
			}
			public void mouseExited(MouseEvent arg0) {
				butClose.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butClose.setIcon(Util.windowIconClose);
				}
			});
		
		butMinimize.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butMinimize.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				butMinimize.setIcon(Util.windowIconMinimizeHover);
			}
			public void mouseExited(MouseEvent arg0) {
				butMinimize.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butMinimize.setIcon(Util.windowIconMinimize);
				}
			});
		
		butChangeTheme.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butChangeTheme.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				butChangeTheme.setIcon(new ImageIcon(Util.getImage("res/themeHover.png")));
			}
			public void mouseExited(MouseEvent arg0) {
				butChangeTheme.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butChangeTheme.setIcon(new ImageIcon(Util.getImage("res/theme.png")));
				}
			});
		
		butToggleLyric.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butToggleLyric.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				butToggleLyric.setIcon(new ImageIcon(Util.getImage("res/lyricHover.png")));
			}
			public void mouseExited(MouseEvent arg0) {
				butToggleLyric.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butToggleLyric.setIcon(new ImageIcon(Util.getImage("res/lyric.png")));
				}
			});
	}
	
	
	/**
	 * 显示“改变主题”的对话框
	 */
	public void showChangeThemeDialog(){
		if(themeDialog==null){
			themeDialog = new JDialog();
			themeDialog.setSize(400+110, 400);
			themeDialog.setLocationRelativeTo(this);
			themeDialog.setLayout(new FlowLayout());
			themeDialog.setTitle("选择主题");
			
			groupTheme = new ButtonGroup();
			radioThemes = new JRadioButton[Util.themes.length];
			
			themeWrapper = new JPanel();
			themeWrapper.setLayout(new FlowLayout());
			themeWrapper.setPreferredSize(new Dimension(470, 350));
			
			themePane = new JScrollPane(themeWrapper);
			themePane.setPreferredSize(new Dimension(480, 350));
			themePane.setLocation(0, 0);
			themePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			themePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			for(int i=0;i<radioThemes.length;i++){
				ImageIcon img = new ImageIcon(Util.getImage(Util.themeDir + Util.themes[i]));
				//scaleImage(src,((double)newHeight) / (double)toBufferedImage(src).getHeight());
				//Image image = PlayingSongPanel.fitSize(img.getImage(), 100);
				Image image = PlayingSongPanel.scaleImage(img.getImage(),
						(100.0) / (double)PlayingSongPanel.toBufferedImage(img.getImage()).getWidth());
				ImageIcon icon = new ImageIcon(image);
				radioThemes[i] = new JRadioButton(icon);
				radioThemes[i].addActionListener(this);
				radioThemes[i].setName(Util.themes[i]);
				groupTheme.add(radioThemes[i]);
				themeWrapper.add(radioThemes[i]);
			}
		}
		//themePane.getViewport().setView(themeWrapper);
		addThemeImageFocusListener();
		themeDialog.add(themePane);
		themeDialog.setVisible(true);
	}
	
	/**
	 * 给背景选项添加焦点监听器，主要是在鼠标移上去是发生效果
	 */
	public void addThemeImageFocusListener(){
		for(int i=0;i<radioThemes.length;i++)
		{
			radioThemes[i].addMouseListener(new MouseAdapter(){
				public void mouseEntered(MouseEvent arg0) {
					for(int i=0;i<radioThemes.length;i++){
						if(arg0.getSource()==radioThemes[i]){
							radioThemes[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
							radioThemes[i].setBackground(Color.LIGHT_GRAY);
						}
					}
				}
				public void mouseExited(MouseEvent arg0) {
					for(int i=0;i<radioThemes.length;i++){
						if(arg0.getSource()==radioThemes[i]){
							radioThemes[i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							radioThemes[i].setBackground(null);
						}
					}
				}
				});
		}
	}
	/**
	 * 绘制背景图片
	 * @param bg
	 */
	ImageIcon bg = new ImageIcon(Util.getImage(Util.themeDir + Util.themes[Util.currentTheme]));
	JLabel labelBg = null;
	JPanel imagePanel = null;
	public void setBackgroundImage(){
		if(labelBg!=null)this.getLayeredPane().remove(labelBg);
		bg = new ImageIcon(Util.getImage(Util.themeDir + Util.themes[Util.currentTheme]));
	    labelBg = new JLabel(bg);
	    labelBg.setBounds(0, 0, Util.windowWidth, Util.windowHeight);
		imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		this.getLayeredPane().add(labelBg, new Integer(Integer.MIN_VALUE));
	}
	
	/**
	 * 初始化面板之间的关系
	 */
	public void initPanelRelations(){
		panelPlayList.setPlayPanel(panelPlay);
		panelPlay.setPlayListPanel(panelPlayList);
		panelPlayList.setPanelLyric(panelLyric);
		panelPlay.setPanelLyric(panelLyric);
	}
	/**
	 * 初始化歌曲列表面板
	 */
	public void initPlayListPanel(){
		panelPlayList = new PlayListPanel();
		panelPlayList.setLocation(0, 200);
		add(panelPlayList);
	}
	
	/**
	 * 初始化歌词显示面板
	 */
	public void initLyricPanel(){
		panelLyric = new LyricPanel();
		panelLyric.setLocation(300, 32+10);
		add(panelLyric);
	}
	
	/**
	 * 初始化“正在播放“面板
	 */
	public void initPlayPanel(){
		panelPlay = new PlayPanel();
		panelPlay.setLocation(0, 32);
		add(panelPlay);
	}

	Point loc = null;			  //窗口要移动到的位置
    Point tmp = null;			  //窗口移动前位置
    boolean isDragged = false;	  //是否正在拖动标题栏标识
	private void setDragable() {
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent e) {
               isDragged = false;
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
               tmp = new Point(e.getX(), e.getY());
               isDragged = true;
            }
        });
        }
	/**
	 * 窗口移动
	 */
	public void dragWindow(){
		titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
               if(isDragged) {
                   loc = new Point(getLocation().x + e.getX() - tmp.x, getLocation().y + e.getY() - tmp.y);
                   setLocation(loc);
               }
            }
        });
	 }
	
	
	private int lyricWidth = 600;
	/**
	 * 调整按钮的位置。主要在歌词关闭和显示时调整横坐标
	 */
	public void ajustTitleBarButtonPosition(){
		if(Util.isLyricPanelClosed()){
			this.butChangeTheme.setLocation(this.butChangeTheme.getX()-lyricWidth, this.butChangeTheme.getY());
			this.butClose.setLocation(this.butClose.getX()-lyricWidth, this.butClose.getY());
			this.butMinimize.setLocation(this.butMinimize.getX()-lyricWidth, this.butMinimize.getY());
			this.butToggleLyric.setLocation(this.butToggleLyric.getX()-lyricWidth, this.butToggleLyric.getY());
		}
		else{
			this.butChangeTheme.setLocation(this.butChangeTheme.getX()+lyricWidth, this.butChangeTheme.getY());
			this.butClose.setLocation(this.butClose.getX()+lyricWidth, this.butClose.getY());
			this.butMinimize.setLocation(this.butMinimize.getX()+lyricWidth, this.butMinimize.getY());
			this.butToggleLyric.setLocation(this.butToggleLyric.getX()+lyricWidth, this.butToggleLyric.getY());
		}
		this.validate();
		this.setBackground(null);
	}
	/**
	 * 歌词面板的关闭和显示
	 */
	LyricPanel tmpLyricPanel = null;
	public void toggleLyric(){
		if(panelLyric==null){
			System.out.println("Trying to show lyric panel.");
			//initLyricPanel();
			//initPanelRelations();
			panelLyric = tmpLyricPanel;
			this.setSize(Util.windowWidth, Util.windowHeight);
			this.validate();
			this.setBackground(null);
			Util.setLyricPanelClosed(false);
			this.panelPlay.showLyric();
		}
		else{
			System.out.println("Trying to close lyric panel.");
			//panelLyric.removeAll();
			tmpLyricPanel = panelLyric;
			panelLyric = null;
			this.setSize(this.getWidth()-lyricWidth, this.getHeight());
			this.validate();
			this.setBackground(null);
			Util.setLyricPanelClosed(true);
		}
		ajustTitleBarButtonPosition();
	}
	/**
	 * 改变主题
	 */
	public void changeTheme(){
		Util.config.setCurrentTheme(Util.currentTheme);
		Util.config.writeConfig();
		this.setBackgroundImage();
		this.setBackground(null);
		this.panelPlay.panelSong.changeTheme();
		this.panelPlay.changeTheme();
		this.panelPlayList.changeTheme();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == butClose){				 //关闭
			System.exit(0);
		}
		else if(e.getSource() == butMinimize){
			setExtendedState(JFrame.ICONIFIED);  	 //最小化
		}
		else if(e.getSource() == butChangeTheme){	 //改变主题
			showChangeThemeDialog();	 
		}
		else if(e.getSource() == butToggleLyric){	 //关闭或显示歌词
			this.toggleLyric();
		}
		else{										 //选择主题
			for(int i=0;i<radioThemes.length;i++){
				if(e.getSource()==radioThemes[i]){
					System.out.println("change theme to : " + radioThemes[i].getName());
					Util.currentTheme = i;
					changeTheme();
					return;
				}
			}
		}
	}
}
