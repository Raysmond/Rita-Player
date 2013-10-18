package com.raysmond.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalScrollPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.raysmond.song.ListController;
import com.raysmond.song.Song;
import com.raysmond.song.SongList;
import com.raysmond.util.Util;

/**
 * 播放列表面板模块
 * 主要功能是控制播放列表的显示、刷新、定位、UI、添加歌曲、设置播放模式（单曲、循环、随机等）
 * 另外还添加一个播放控制线程，这个线程在每次一首歌播放完成后通过interrupt激活，根据当前的播放模式决定下一首播放曲目
 * @author Jiankun Lei
 *
 */
public class PlayListPanel extends JPanel implements ActionListener,Runnable {
	private static final long serialVersionUID = -689999949667356863L;
	
	private ListController list = new SongList(Util.playList);
	private static final File playListFile = Util.playList;				//播放列表存放文件
	private LinkedList<Song> playList = new LinkedList<Song>();			//播放列表（包含所有歌曲）
	private LinkedList<Song> likeList = new LinkedList<Song>();			//喜欢列表，是播放列表的子集
	private LinkedList<Song> currentList = null;						//当前列表的引用
	private int currentListFlag = 0;									//0 for playList; 1 for likeList
	
	String[] header = {"编号","标题","歌手"};
	JScrollPane tableWrapper = null;
	DefaultTableModel tableModel;
	JTable table;
	
	JPopupMenu popup;
	JMenuItem itemPlay;
	JMenuItem itemView,itemAdd,itemAddFolder,itemUpdate,itemDelete,itemLike;
	
	JPopupMenu popupAdd = new JPopupMenu();
	JMenuItem itemAddFiles = new JMenuItem("添加本地歌曲..");
	JMenuItem itemAddFolder1 = new JMenuItem("添加本地歌曲文件夹..");
	
	JPanel viewPanel = new JPanel();
	JPanel viewListPanel = new JPanel();
	PlayPanel panelPlay = null;     //对播放面板的引用
	LyricPanel panelLyric = null;   //对歌词面板的引用
	
	JPanel panelControl = new JPanel();
	String iconAdd = "./res/add.png";
	String iconAddHover = "./res/add_hover.png";
	JButton butAdd = new JButton(new ImageIcon(iconAdd));
	JButton butPlayChoice = new JButton(Util.iconPlayListLoop);
	JButton butDownload = new JButton(Util.iconBaiduMusic);
	
	Thread controlThread = null;
	
	JButton butMyMusicList = new JButton(new ImageIcon("./res/mymusic.png"));
	JButton butMyLikeList = new JButton(new ImageIcon("./res/mylike.png"));
	
	//歌曲列表字体颜色
	private static Color listColor = Util.getThemeDefaultColor();
	
	private SearchSongDialog searchDialog = null;
	
	public static final int WIDTH = 300;
	public static final int HEIGHT = 400;
	
	public PlayListPanel(){
		setOpaque(false);
		setSize(WIDTH, HEIGHT);
		setLayout(null);
		initTopControlBar();
		
		viewPanel.setLayout(new BorderLayout());
		viewPanel.setBounds(0, 30, 300, 335);
		viewPanel.setOpaque(false);
				
		list.open(playListFile);;
		showPlayList();
		
		add(viewPanel);
		
		
		initPopupMenu();
		initListControlPanel();
		initPlayChoices();
		startControlThread();
		currentList = playList;
		currentListFlag = 0;
		
	}
	
	public void setPlayPanel(PlayPanel p){
		this.panelPlay = p;
	}
	
	public void setPanelLyric(LyricPanel lyricPanel){
		this.panelLyric = lyricPanel;
	}
	
	public void initPlayChoices(){
		list.setPlayModel(Util.config.getPlayMode());
		switch(list.getPlayModel()){
		case 0: 
			butPlayChoice.setIcon(Util.iconPlayOne);
			butPlayChoice.setToolTipText("单曲播放");
			break;
		case 1: 
			butPlayChoice.setIcon(Util.iconPlayOneLoop);
			butPlayChoice.setToolTipText("单曲循环");
			break;
		case 2: 
			butPlayChoice.setIcon(Util.iconPlayList);
			butPlayChoice.setToolTipText("列表播放");
			break;
		case 3: 
			butPlayChoice.setIcon(Util.iconPlayListLoop);
			butPlayChoice.setToolTipText("列表循环");
			break;
		case 4: 
			butPlayChoice.setIcon(Util.iconPlayRandom);
			butPlayChoice.setToolTipText("随机播放");
			break;
			default:
		}
		System.out.println("Current play mode: " + list.getPlayModel());
	}
	/**
	 * 改变主题的时候调用，设置当前主题相关的风格
	 */
	public void changeTheme(){
		listColor = Util.getThemeListColor();
		if(table!=null){
			 table.setForeground(listColor);
			 table.setSelectionForeground(Util.getThemeHighlightColor());
		}
	}
	/**
	 * 初始化歌词列表上方的控制按钮：我的音乐、我喜欢的
	 */
	public void initTopControlBar(){
		
		JPanel panelBar = new JPanel();
		panelBar.setOpaque(false);
		panelBar.setLayout(null);
		
		butMyMusicList.setBackground(null);
		butMyMusicList.setBorderPainted(false);
		butMyMusicList.setFocusPainted(false);
		butMyMusicList.setContentAreaFilled(false);
		butMyMusicList.setFocusable(true);
		
		butMyLikeList.setBackground(null);
		butMyLikeList.setBorderPainted(false);
		butMyLikeList.setFocusPainted(false);
		butMyLikeList.setContentAreaFilled(false);
		butMyLikeList.setFocusable(true);
		
		butMyMusicList.setBounds(20,0,120, 30);
		butMyLikeList.setBounds(140,0,80, 30);
		
		butMyMusicList.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butMyMusicList.setIcon(Util.getImageIcon("mymusicHover.png"));
				butMyMusicList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent arg0) {
				butMyMusicList.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butMyMusicList.setIcon(Util.getImageIcon("mymusic.png"));
			}
			});

		butMyLikeList.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butMyLikeList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				butMyLikeList.setIcon(Util.getImageIcon("mylikeHover.png"));
			}
			public void mouseExited(MouseEvent arg0) {
				butMyLikeList.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butMyLikeList.setIcon(Util.getImageIcon("mylike.png"));
			}
			});
		//设置透明背景
		butMyMusicList.setOpaque(false);
		butMyLikeList.setOpaque(false);
		
		//添加到面板
		add(butMyMusicList);
		add(butMyLikeList);
		
		//设置监听器
		butMyMusicList.addActionListener(this);
		butMyLikeList.addActionListener(this);
		
	}
	
	/**
	 * 初始化歌曲列表右键弹出菜单
	 */
	public void initPopupMenu(){
		popup = new JPopupMenu();
		itemView = new JMenuItem("查看");
		itemAdd = new JMenuItem("添加文件...");
		itemAddFolder = new JMenuItem("添加文件夹");
		itemUpdate = new JMenuItem("更新");
		itemDelete = new JMenuItem("删除");
		itemLike = new JMenuItem("喜欢");
		itemPlay = new JMenuItem("播放");
		
		popup.add(itemLike);
		popup.add(itemView);
		popup.add(itemAdd);
		popup.add(itemAddFolder);
		popup.add(itemUpdate);
		popup.add(itemDelete);
		popup.add(itemPlay);
		
		itemView.addActionListener(this);
		itemAdd.addActionListener(this);
		itemAddFolder.addActionListener(this);
		itemUpdate.addActionListener(this);
		itemDelete.addActionListener(this);
		itemLike.addActionListener(this);
		itemPlay.addActionListener(this);
		
		popupAdd.add(itemAddFiles);
		popupAdd.add(itemAddFolder1);
		itemAddFiles.addActionListener(this);
		itemAddFolder1.addActionListener(this);
	}
	
	/**
	 * 初始化歌词列表下方的控制面板：添加歌曲、播放方式
	 */
	public void initListControlPanel(){
		panelControl.setBounds(0,365,300,35);
		panelControl.setBackground(null);
		
		butAdd.setBackground(null);
		butAdd.setBorderPainted(false);
		butAdd.setFocusPainted(false);
		butAdd.setContentAreaFilled(false);
		butAdd.setFocusable(true);
		
		butPlayChoice.setBackground(null);
		butPlayChoice.setBorderPainted(false);
		butPlayChoice.setFocusPainted(false);
		butPlayChoice.setContentAreaFilled(false);
		butPlayChoice.setFocusable(true);
		
		butDownload.setBackground(null);
		butDownload.setBorderPainted(false);
		butDownload.setFocusPainted(false);
		butDownload.setContentAreaFilled(false);
		butDownload.setFocusable(true);
		
		
		/*FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		panelControl.setLayout(flow);*/
		panelControl.setLayout(null);
		butAdd.setBounds(5, 0, 32, 32);
		butPlayChoice.setBounds(40, 0, 32, 32);
		
		butDownload.setBounds(40+40, 0, 32, 32);
		
		panelControl.add(butAdd);
		panelControl.add(butPlayChoice);
		panelControl.add(butDownload);
		panelControl.setBorder(null);
		panelControl.setOpaque(false);
		
		
		butDownload.addActionListener(this);
		butDownload.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butDownload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				butDownload.setIcon(Util.iconBaiduMusicHover);
			}
			public void mouseExited(MouseEvent arg0) {
				butDownload.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butDownload.setIcon(Util.iconBaiduMusic);
			}
			});
	    
		
		butAdd.addActionListener(this);
		butAdd.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				butAdd.setIcon(new ImageIcon(iconAddHover));
			}
			public void mouseExited(MouseEvent arg0) {
				butAdd.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				butAdd.setIcon(new ImageIcon(iconAdd));
			}
			});
		
		butPlayChoice.addActionListener(this);
		butPlayChoice.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent arg0) {
				butPlayChoice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent arg0) {
				butPlayChoice.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			});
		add(panelControl);
	}
	
	/**
	 * 显示添加文件对话框
	 */
	JFileChooser fileChooser = null;
	public void showAddDialog() throws IOException, UnsupportedAudioFileException{
		if(fileChooser==null){
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setFileFilter(new FileNameExtensionFilter("mp3 files","mp3"));
		}
		int state = fileChooser.showOpenDialog(this);
		if(state == JFileChooser.APPROVE_OPTION){
			final File[] files = fileChooser.getSelectedFiles();
			new Thread(){
				public void run(){
					for(File file : files){ 
						Song song = new Song();
						try {
							song.load(file);
						} catch (IOException e) {
							e.printStackTrace();
							continue;
						} catch (UnsupportedAudioFileException e) {
							e.printStackTrace();
							continue;
						}
						list.add(song);
						System.out.println("Add song:\n" + song.toString());
					}
					list.save();
					showPlayList();
				}
			}.start();
		}
	}
	
	/**
	 * 显示添加文件夹对话框
	 */
	JFileChooser dirDialog = null;
	public void showAddFolderDialog() throws IOException, UnsupportedAudioFileException{
		if(dirDialog==null){
			dirDialog = new JFileChooser();
			dirDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		int state = dirDialog.showOpenDialog(this);
		if(state == JFileChooser.APPROVE_OPTION){
			File dir = dirDialog.getSelectedFile();
			final File[] files = dir.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith("mp3");
				}
			});
			new Thread(){
				public void run(){
					for(File file : files){ 
						Song song = new Song();
						try {
							song.load(file);
						} catch (IOException e) {
							e.printStackTrace();
							continue;
						} catch (UnsupportedAudioFileException e) {
							e.printStackTrace();
							continue;
						}
						list.add(song);
						System.out.println("Add song:\n" + song.toString());
					}
					list.save();
					showPlayList();
				}
			}.start();
		}
	}
	
	/**
	 * 从所有歌曲列表中获取喜欢歌曲列表，存在likeList中
	 */
	public void getLikeList(){
		if(playList==null||playList.size()==0) return;
		else{
			likeList.clear(); //清空当前的喜欢列表
			ListIterator<Song> it  = playList.listIterator();
			Song tmp = null;
			while(it.hasNext()){ //遍历歌曲列表，找出喜欢的歌曲
				tmp = it.next();
				if(tmp.isLike()){
					System.out.println("like: " + tmp.getTitle());
					likeList.add(tmp);
				}
			  }
		}
	}
	
	public void switchToLikePlayList(){
		if(currentListFlag==1)return; /////
		getLikeList();
		currentListFlag = 1;
		currentList = likeList;
		showPlayList();
		this.gotoPlaying();
	}
	
	/**
	 * 切换到播放列表（所有歌曲）
	 */
	public void switchToPlayList(){
		if(currentListFlag==0) return;
		currentListFlag = 0;
		currentList = playList;
		showPlayList();	
		this.gotoPlaying();
	}
	
	public void showPlayList(){
		viewPanel.removeAll();
		list.open(playListFile);
		playList = list.getList();
		if(currentList==null){
			currentList = playList;
			currentListFlag = 0;
		}
		if(currentListFlag==0) currentList = playList;
		table = new JTable(getTableModel(currentList));
		table.setTableHeader(null);   //不显示表头
		viewPanel.setOpaque(false);
		viewPanel.setBackground(null);
		
		tableWrapper = new JScrollPane(table);
		
		//设置列表外面的JScrollPane透明
		tableWrapper.setOpaque(false);
		tableWrapper.setBackground(null);
		tableWrapper.setBorder(null);
		tableWrapper.getViewport().setBorder(null);
		tableWrapper.getViewport().setOpaque(false);
	    //tableWrapper.getVerticalScrollBar().setUI();
		tableWrapper.getVerticalScrollBar().setUI(new MyScrollBarUI());
		
		viewPanel.add(tableWrapper);
		
		//设置列不等宽
		int[] width = {30,210,40};
		table.setColumnModel(getColumn(table, width));
		
		//设置第一列居中对齐
		DefaultTableCellRenderer centerAlignment = new DefaultTableCellRenderer();   
		centerAlignment.setOpaque(false);
		centerAlignment.setHorizontalAlignment(JLabel.CENTER);   
		TableColumn col = table.getColumn(table.getColumnName(0));  
		col.setCellRenderer(centerAlignment);
		
		table.setRowHeight(24);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setShowGrid(false);
		table.setOpaque(false);  
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();   
        render.setOpaque(false); 
        table.setDefaultRenderer(Object.class,render); 
        
        //设置显示范围  
        Dimension viewSize = new Dimension();   
        viewSize.width = table.getColumnModel().getTotalColumnWidth();   
        viewSize.height = 10 * table.getRowHeight();   
        table.setPreferredScrollableViewportSize(viewSize);   
        
        //设置列表主题颜色
        changeTheme();
        
		//刷新歌曲列表
		table.repaint();
		viewPanel.validate();
		table.addMouseListener(new MouseHandler(this));
	}
	
	
	public static TableColumnModel getColumn(JTable table, int[] width) {  
	    TableColumnModel columns = table.getColumnModel();  
	    for (int i = 0; i < width.length; i++) {  
	        TableColumn column = columns.getColumn(i);  
	        column.setPreferredWidth(width[i]);  
	    }  
	    return columns;  
	}  
	
	/**
	 * 从链表中获取一个DefaultTableModel
	 * @param list
	 * @return 包含歌曲基本信息的table model
	 */
	public static DefaultTableModel getTableModel(LinkedList<Song> list){
		String[] columnName = {"Number","Title","Artist"};
		DefaultTableModel model = new DefaultTableModel(null,columnName){
			public boolean isCellEditable(int row, int column) {
	            return false;
	        }
		}; 
		model.setNumRows(0);
		int counter = 0;
		for (int i=0;i<list.size();i++) {
			Song m = list.get(i);
			Vector row = new Vector();
			row.add(++counter);
			row.add(m.getFileNameNoEx(m.getFileName()));
			if(m.getPlayTime()!=null&&!m.getPlayTime().isEmpty()){
				row.add(m.getPlayTime());
			}
			else row.add(m.getArtist());
			model.addRow(row);
		}
		return model;
	}
	
	/**
	 * 把播放列表信息写回文件中
	 */
	public void writePlayList(){
		if(playList.size()==0) return;
		if(!playListFile.exists()){
			try {
				playListFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			FileOutputStream fo = new FileOutputStream(playListFile);
			ObjectOutputStream out = new ObjectOutputStream(fo);
			out.writeObject(playList);
			out.close();
			fo.close();
		}catch(Exception e){}
	}
	
	/**
	 * 把播放列表信息写回文件中
	 */
	public void writeLikeList(){
		if(playList.size()==0) return;
		if(!playListFile.exists()){
			try {
				playListFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			FileOutputStream fo = new FileOutputStream(playListFile);
			ObjectOutputStream out = new ObjectOutputStream(fo);
			out.writeObject(playList);
			out.close();
			fo.close();
		}catch(Exception e){}
	}
	
	public void playNext(){
		try {
			Song next = list.playNext();
			if(null==next) return;
			panelPlay.play(next);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void playPrev(){
		try {
			Song prev = list.playPrev();
			if(null==prev) return;
			panelPlay.play(prev);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 往播放列表中添加一个歌曲信息
	 * @param song
	 */
	public void Add(Song song){
		playList.add(song);
		writePlayList();
	}
	//*************************
	public void DeleteSong(){
		if(currentListFlag==1) return;   /////////////在喜欢列表中不进行删除
		int[] rows = table.getSelectedRows();
		for(int row : rows){
			playList.remove(row);
		}
		writePlayList();
		list.open(playListFile);;
		showPlayList();
	}
	
	public void gotoPlaying(){
		 int index = getRowNumberOfSong(list.getCurrentSong());
		 Rectangle rect = new Rectangle(0, table.getHeight(), 20, 20);
         table.scrollRectToVisible(rect);
         table.setRowSelectionInterval(index, table.getRowCount() - 1);
         table.grabFocus();
         table.changeSelection(index, 0, false, true);
	}
	
	/**
	 * 把一首歌标记为喜欢
	 * @param song
	 */
	public void handleLike(Song song){
		if(song.isLike()) return;
		else{
			System.out.println("Add song: \"" + song.getTitle() + "\" to like list.");
			int row = getRowNumberOfSong(song);
			playList.get(row).setLike(true);
			this.writePlayList();
		}
	}
	private int getRowNumberOfSong(Song song) {
		int i=0,size = currentList.size();
		for(;i<size;++i){
			if(currentList.get(i).equals(song)) return i;
		}
		return 0;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==itemAdd||e.getSource()==itemAddFiles){
			try {
				try {
					showAddDialog();
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
				}
			} catch (IOException /*| UnsupportedAudioFileException*/ e1) {
				e1.printStackTrace();
			}
			list.open(playListFile);;
			showPlayList();
		}
		else if(e.getSource()==butAdd){
			popupAdd.show(this,5,this.getHeight()-83);
		}
		else if(e.getSource()==butDownload){
			if(searchDialog==null) searchDialog = new SearchSongDialog();
			searchDialog.showSearchLRCDialog(null,null);
		}
		else if(e.getSource()==butPlayChoice){
			int model = (list.getPlayModel()+1)%5;
			Util.config.setPlayMode(model);
			list.setPlayModel(model);
			switch(model){
			case 0: 
				butPlayChoice.setIcon(Util.iconPlayOne);
				butPlayChoice.setToolTipText("单曲播放");
				break;
			case 1: 
				butPlayChoice.setIcon(Util.iconPlayOneLoop);
				butPlayChoice.setToolTipText("单曲循环");
				break;
			case 2: 
				butPlayChoice.setIcon(Util.iconPlayList);
				butPlayChoice.setToolTipText("列表播放");
				break;
			case 3: 
				butPlayChoice.setIcon(Util.iconPlayListLoop);
				butPlayChoice.setToolTipText("列表循环");
				break;
			case 4: 
				butPlayChoice.setIcon(Util.iconPlayRandom);
				butPlayChoice.setToolTipText("随机播放");
				break;
				default:
			}
		}
		else if(e.getSource()==itemAddFolder1){
			try {
				try {
					this.showAddFolderDialog();
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
				}
			} catch (IOException /*| UnsupportedAudioFileException*/ e1) {
				e1.printStackTrace();
			}
			list.open(playListFile);;
			showPlayList();
		}
		else if(e.getSource()==itemView){
			int row = this.table.getSelectedRow();
			if(row>=0){
				SongDialog dialog = new SongDialog(null,false);
				dialog.showDialog(currentList.get(row));
			}
		}
		else if(e.getSource()==itemAddFolder){
			try {
				try {
					this.showAddFolderDialog();
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
				}
			} catch (IOException /* | UnsupportedAudioFileException */ e1) {
				e1.printStackTrace();
			}
			list.open(playListFile);;
			showPlayList();
		}
		else if(e.getSource()==itemUpdate){
			JOptionPane.showMessageDialog(this, "Hello");
		}
		else if(e.getSource()==itemDelete){
			DeleteSong();
		}
		else if(e.getSource()==itemPlay){
			playSelectedSong();
		}
		else if(e.getSource()==itemLike){
			if(currentListFlag==1) return;
			int row = table.getSelectedRow();
			if(row<0) return;
			Song song = playList.get(row);
			handleLike(song);
		}
		else if(e.getSource()==butMyMusicList){
			System.out.println("Switch to play list(all)");
			this.switchToPlayList();
		}
		else if(e.getSource()==butMyLikeList){
			System.out.println("Switch to like list");
			this.switchToLikePlayList();
		}
	}
	
	public void playSelectedSong(){
		int index = table.getSelectedRow();
		System.out.println("Row: " + index);
		if(index==-1) index = 0;
		if(panelPlay!=null){
			try {
				Song song = list.get(index);
				System.out.println("Select " + song.getTitle() + " to play.");
				panelPlay.play(song);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	private class MouseHandler extends MouseAdapter {
		private JPanel parentPanel;

		public MouseHandler(JPanel parentPanel) {
			this.parentPanel = parentPanel;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getModifiersEx() == 256) { 						//点击右击
				popup.show(parentPanel, e.getX(), e.getY());
			}
			 if(e.getClickCount()==2){ 								//双击
				 playSelectedSong();
		        }
		}
	}
	public void startControlThread(){
		if(controlThread==null){
			controlThread = new Thread(this);
		}
		controlThread.start();
	}
	
	public void stopControlThread(){
		if(controlThread!=null){
			controlThread.stop();
			controlThread = null;
		}
	}
	public void controlThreadAction(){
		System.out.println("中断了。。");
		if(controlThread!=null){
			controlThread.interrupt();
		}
	}
	
	@Override
	public  void run(){
		if(Thread.currentThread()==controlThread){
			while(true){
				try {
					Thread.sleep(1000*60);
				} catch (InterruptedException e1) {
					System.out.println("正常中断检查，选择歌曲：");
					if(!panelPlay.isStoped()) continue;
					else{
						try {
							Song next = list.playNext();
							if(null==next) return;
							panelPlay.play(next);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
