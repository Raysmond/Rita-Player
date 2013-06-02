package com.raysmond.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.raysmond.lyric.LRCSearchResult;
import com.raysmond.lyric.LRCUtil;
import com.raysmond.song.Song;
import com.raysmond.util.Util;

/**
 * 歌词显示面板 主要功能是显示歌词。歌词是同步显示的。 附加功能：歌词本地匹配、歌词在线匹配（百度音乐）
 * 
 * @author Jiankun Lei
 * 
 */
public class LyricPanel extends JPanel implements Runnable, ActionListener {

	private static final long serialVersionUID = 7991482538335656273L;

	// 当前播放的歌曲
	Song song = null;

	public final Pattern pattern = Pattern.compile("(?<=//[).*?(?=//])");

	public final File lyricFile = Util.lyricFile;
	public final String dirPath = Util.lyricDirPath;
	static RandomAccessFile in = null;

	SimpleAttributeSet bSet = null;

	private ArrayList<JLabel> lyrics = new ArrayList<JLabel>();
	private ArrayList<String> times = new ArrayList<String>();
	private int currentLine = 0;

	private Color hoverColor = new Color(201, 13, 13);
	private Color defaultColor = Color.BLACK;
	private static final Font defaultFont = new Font("Microsoft YaHei",
			Font.PLAIN, 14);
	private static final Font hoverFont = new Font("Microsoft YaHei",
			Font.PLAIN, 16);

	private JPopupMenu popMenu = new JPopupMenu();
	private JMenuItem itemSearchLRC = new JMenuItem("Search lyric");

	Thread moveThread = null;

	// 歌词是否已经载入的标识。没有载入，禁止歌词的显示和移动。载入中，不允许移动
	private boolean lyricLoaded = false;
	// 歌词是否显示在界面上的标识，没有显示完毕前要禁止行的移动，否则间隔不对
	private boolean lyricShowFinished = false;

	JLabel labelTitle = new JLabel("标题");
	JLabel labelArtist = new JLabel("歌手");
	JTextField fieldTitle = new JTextField(30);
	JTextField fieldArtist = new JTextField(30);
	JTable resultTable = new JTable();
	JDialog dialog = null;
	JButton butSearch = new JButton("搜索");
	ArrayList<LRCSearchResult> results = new ArrayList<LRCSearchResult>();

	public LyricPanel() {
		setLayout(null);
		setSize(600, 530 - 10);
		setBackground(null);
		setOpaque(false);
		init();
	}

	public void init() {
		popMenu.add(itemSearchLRC);
		itemSearchLRC.addActionListener(this);
		this.addMouseListener(new MouseHandler(this));
	}

	private class MouseHandler extends MouseAdapter {
		private JPanel parentPanel;

		public MouseHandler(JPanel parentPanel) {
			this.parentPanel = parentPanel;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getModifiersEx() == 256) { // 点击右击
				popMenu.show(parentPanel, e.getX(), e.getY());
			}
			if (e.getSource() == resultTable && e.getClickCount() == 2) { // 双击
				int row = resultTable.getSelectedRow();
				LRCSearchResult result = results.get(row);
				System.out.println(result.getLrcText());
				saveLRC(Song.getFileNameNoEx(song.getFileName()) + ".lrc",
						result.getLrcText());
				loadLocalLyric();
				if (lyrics.size() == 0) {
					loadInfoWhenNoLyricFound();
				} else {
					lyricLoaded = true;
					displayLyrics();
				}
			}
		}
	}

	private void saveLRC(String lrcTitle, String lrcText) {
		File lrcFile = new File(Util.lyricDirPath + "/" + lrcTitle);
		if (!lrcFile.exists()) {
			try {
				lrcFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fo = new FileOutputStream(lrcFile);
			fo.write(lrcText.getBytes("utf-8"));
			fo.close();
		} catch (Exception e) {
		}
	}

	public void showSong(Song song) {
		lyricLoaded = false;
		lyricShowFinished = false;
		setSong(song);
		loadLocalLyric();
		if (lyrics.size() == 0) {
			loadInfoWhenNoLyricFound();
			matchingLyricOnLine();
		} else {
			lyricLoaded = true;
			displayLyrics();
			lyricShowFinished = true;
		}
	}

	/**
	 * Load lyric file from the local file system
	 */
	public void loadLocalLyric() {
		String songNameNoEx = Song.getFileNameNoEx(song.getFileName());
		File file = new File(dirPath + "/" + songNameNoEx + ".lrc");
		resetLyricPanel();
		if (file.exists()) {
			ArrayList<String> lyricsTmp = LyricPanel.readLyricFromFile(file);
			for (int i = 0; i < lyricsTmp.size(); i++) {
				parserLine(lyricsTmp.get(i));
			}
		}
	}

	public void resetLyricPanel() {
		lyrics.clear();
		times.clear();
		currentLine = 0;
		this.removeAll();
		this.validate();
		this.setBackground(null);
	}

	public void displayLyrics() {
		if (!lyricLoaded)
			return;
		lyricShowFinished = false; // 禁止移动行，直接转跳后才能移动
		int currentY = 230;
		for (int i = 0; i < lyrics.size(); i++) {
			add(lyrics.get(i));
			lyrics.get(i).setBounds(0, currentY, this.getWidth(), 30);
			lyrics.get(i).setBackground(null);
			currentY += 30;
		}
		lyricShowFinished = true;
	}

	public void displayLyrics(int lineBegin) {
		if (!lyricLoaded)
			return;
		lyricShowFinished = false; // 禁止移动行，直接转跳后才能移动
		int currentY = 230;
		lyrics.get(lineBegin).setForeground(hoverColor);
		lyrics.get(lineBegin).setFont(hoverFont);
		lyrics.get(lineBegin).setBounds(0, currentY, this.getWidth(), 30);
		currentY += 30;
		for (int i = lineBegin + 1; i < lyrics.size(); i++) {
			lyrics.get(i).setForeground(defaultColor);
			lyrics.get(i).setBackground(null);
			lyrics.get(i).setBounds(0, currentY, this.getWidth(), 30);
			currentY += 30;
		}
		currentY = 230 - 30;
		for (int i = lineBegin - 1; i >= 0; i--) {
			lyrics.get(i).setForeground(defaultColor);
			lyrics.get(i).setBackground(null);
			lyrics.get(i).setBounds(0, currentY, this.getWidth(), 30);
			currentY -= 30;
		}
		lyricShowFinished = true;
	}

	/**
	 * 自动在线匹配歌词，搜索第一个最佳的歌词
	 */
	public void matchingLyricOnLine() {
		if (!isSetSong())
			return;
		JLabel labelSearching = new JLabel("正在在线匹配歌词，请稍等...",
				(int) JLabel.LEFT_ALIGNMENT);
		labelSearching.setBounds(0, this.getHeight() / 2 - 30, this.getWidth(),
				50);
		labelSearching.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
		labelSearching.setForeground(new Color(121, 49, 0));

		this.removeAll();
		add(labelSearching);
		this.validate();
		this.setBackground(null);

		// 启动一个新的线程去搜索和下载歌词,以免主线程堵塞
		new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				String title = song.getTitle();
				// 如果从TAG信息中获取的标题为空，则把歌曲文件名（无后缀）作为查找的关键词
				if (title == null || title.isEmpty())
					title = Song.getFileNameNoEx(song.getFileName());
				String artist = song.getArtist();

				ArrayList<LRCSearchResult> results = LRCUtil
						.searchLRCFromBaidu(title, artist, 1);

				if (results != null && results.size() == 1) {
					saveLRC(Song.getFileNameNoEx(song.getFileName()) + ".lrc",
							results.get(0).getLrcText());
					loadLocalLyric();
					if (lyrics.size() != 0) {
						lyricLoaded = true;
						displayLyrics();
					} else {
						loadInfoWhenNoLyricFound();
					}
				} else {
					loadInfoWhenNoLyricFound();
				}
			}
		}).start();
	}

	/**
	 * 根据当前播放到的毫秒数和歌词来计算当前对应的是哪句歌词
	 * 
	 * @param seekTime
	 * @return
	 */
	public int getLineSeekingTo(long seekTime) {
		int i = 0;
		for (; i < lyrics.size(); i++) {
			if (Integer.valueOf(times.get(i)) >= seekTime) {
				break;
			}
		}
		return i == 0 ? 0 : i - 1;
	}

	/**
	 * 根据现在播放的时间位置调整歌词显示位置
	 * 
	 * @param seekNextTime
	 *            当前播放的毫秒数
	 */
	public void tryToShowNextLine(long seekNextTime) {
		if (!lyricLoaded)
			return; // 没有歌词，不需要调整
		if (!lyricShowFinished)
			return; // 歌词已经载入，但是没有渲染在界面上。如果此时移动，会导致歌词间隔不一致
		int lineToSeek = getLineSeekingTo(seekNextTime); // 获取需要转跳到的行
		gotoLine(lineToSeek); // 转跳到行
	}

	boolean isNeedToMove = true;

	/**
	 * 根据行号调整歌词显示位置
	 * 
	 * @param line
	 */
	public void gotoLine(int line) {
		if (lyrics == null || lyrics.size() == 0)
			return;
		int lineDistance = line - currentLine;
		if (line == 0 || lineDistance == 0) {
			// 如果当前在第一行则设置第一行颜色
			// 如果转跳句和当前句相同，则不用转跳
			isNeedToMove = false;
			if (line == 0) {
				lyrics.get(0).setForeground(hoverColor);
				lyrics.get(0).setFont(LyricPanel.hoverFont);
			}
			return;
		}

		if (lineDistance == 1) { // 向上移动一行
			isNeedToMove = true;
			currentLine = line;
			moveToNextLine();
		} else { // 直接转调到行
			isNeedToMove = false;
			if (moveThread != null)
				moveThread.stop();
			moveThread = null;
			currentLine = line;
			displayLyrics(line);
		}
	}

	public String parseLine(String line) {
		Matcher matcher = pattern.matcher(line);
		String str = null;
		while (matcher.find()) {
			String time = matcher.group();
			str = line.substring(line.indexOf(time) + time.length() + 1);
		}
		return str;
	}

	/**
	 * 歌词解析，并把解析结果加入歌词列表中
	 * 
	 * @param str
	 */
	private void parserLine(String str) {
		// 取得歌曲名信息
		if (str.startsWith("[ti:")) {
			String title = str.substring(4, str.length() - 1);
			System.out.println("title--->" + title);

		}// 取得歌手信息
		else if (str.startsWith("[ar:")) {
			String singer = str.substring(4, str.length() - 1);
			System.out.println("singer--->" + singer);

		}// 取得专辑信息
		else if (str.startsWith("[al:")) {
			String album = str.substring(4, str.length() - 1);
			System.out.println("album--->" + album);

		}// 通过正则取得每句歌词信息
		else {
			String reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})\\]";
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(str);
			long currentTime = 0;
			String currentContent = null;

			while (matcher.find()) {
				String msg = matcher.group();
				int start = matcher.start();
				int end = matcher.end();
				int groupCount = matcher.groupCount();
				for (int i = 0; i <= groupCount; i++) {
					String timeStr = matcher.group(i);
					if (i == 1) {
						currentTime = Util.tranlateTimeStrToLong(timeStr);
					}
				}

				String[] content = pattern.split(str);
				for (int i = 0; i < content.length; i++) {
					if (i == content.length - 1) {
						currentContent = content[i];
					}
				}
				System.out.println("时间：" + currentTime + "----歌词："
						+ currentContent);
				JLabel label = new JLabel(currentContent,
						(int) JLabel.LEFT_ALIGNMENT);
				label.setFont(defaultFont);
				label.setPreferredSize(new Dimension(this.getWidth(), 30));
				label.setBackground(null);
				lyrics.add(label);
				times.add(String.valueOf(currentTime));
			}
		}
	}

	public static ArrayList<String> readLyricFromFile(File file) {
		ArrayList<String> lyricList = new ArrayList<String>();
		String line = null;
		in = null;
		try {
			in = new RandomAccessFile(file.getAbsolutePath(), "r");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (in != null) {
			try {
				while ((line = in.readLine()) != null) {
					line = new String(line.getBytes("iso8859-1"), "utf-8");
					lyricList.add(line);
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lyricList;
	}

	public void moveToNextLine() {
		lyrics.get(currentLine - 1).setForeground(defaultColor);
		lyrics.get(currentLine - 1).setFont(defaultFont);
		lyrics.get(currentLine).setForeground(hoverColor);
		lyrics.get(currentLine).setFont(hoverFont);
		if (moveThread == null || !moveThread.isAlive()) {
			moveThread = new Thread(this);
		}
		if (moveThread.isAlive()) {
			System.out.println("我活着，休想杀死我！");
		} else
			moveThread.start();
	}

	public void loadInfoWhenNoLyricFound() {
		if (!isSetSong())
			return;
		this.removeAll();

		JLabel title = new JLabel(Song.getFileNameNoEx(song.getFileName()),
				(int) JLabel.LEFT_ALIGNMENT);
		title.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
		title.setForeground(new Color(121, 49, 0));

		JLabel artist = new JLabel(song.getArtist(),
				(int) JLabel.LEFT_ALIGNMENT);
		artist.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
		artist.setForeground(new Color(121, 49, 0));

		JPanel titlePane = new JPanel();
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		titlePane.setLayout(flow);
		titlePane.setOpaque(false);

		System.out.println(song.getArtist());
		titlePane.setBounds(0, this.getHeight() / 2 - 60, this.getWidth(), 100);

		titlePane.add(title);
		titlePane.add(artist);

		title.setPreferredSize(new Dimension(this.getWidth() - 10, 40));
		artist.setPreferredSize(new Dimension(this.getWidth() - 10, 40));
		add(titlePane);

		this.validate();
		this.setBackground(null);
	}

	@Override
	public synchronized void run() {
		if (Thread.currentThread() == moveThread) {
			int moveGap = 5;
			int countGap = 0;
			while (true/* &&isNeedToMove */) {
				for (int i = 0; i < lyrics.size(); i++) {
					lyrics.get(i).setLocation(lyrics.get(i).getX(),
							lyrics.get(i).getY() - moveGap);
				}
				countGap += moveGap;
				if (countGap >= 30)
					break;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isSetSong() {
		if (song != null)
			return true;
		else
			return false;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public void showSearchLRCDialog() {
		String title = song.getTitle();
		// 如果从TAG信息中获取的标题为空，则把歌曲文件名（无后缀）作为查找的关键词
		if (title == null || title.isEmpty())
			title = Song.getFileNameNoEx(song.getFileName());

		String artist = song.getArtist();

		if (dialog == null) {
			dialog = new JDialog();
			dialog.setSize(405, 300);
			dialog.setTitle("歌词搜索");
			dialog.setLayout(null);
			dialog.setLocationRelativeTo(this);
			JScrollPane pane = new JScrollPane(resultTable);

			labelTitle.setBounds(5, 5, 30, 24);
			fieldTitle.setBounds(40, 5, 120, 24);
			labelArtist.setBounds(165, 5, 30, 24);
			fieldArtist.setBounds(200, 5, 110, 24);
			butSearch.setBounds(315, 5, 70, 24);
			pane.setBounds(5, 35, 380, 220);

			dialog.add(labelTitle);
			dialog.add(fieldTitle);
			dialog.add(labelArtist);
			dialog.add(fieldArtist);
			dialog.add(pane);
			dialog.add(butSearch);

			butSearch.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					results.clear();
					results = LRCUtil.searchLRCFromBaidu(fieldTitle.getText()
							.trim(), fieldArtist.getText().trim(), 10);
					resultTable.setModel(LRCUtil.getResultTableModel(results));
					resultTable.validate();
					resultTable.addMouseListener(new MouseHandler(null));
				}
			});
		}
		fieldTitle.setText(title);
		fieldArtist.setText(artist);

		dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == itemSearchLRC) {
			showSearchLRCDialog();
		}
	}
}
