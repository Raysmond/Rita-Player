package com.raysmond.lyric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.raysmond.util.Util;

/**
 * Lyric parser implementation
 * @author Raysmond
 *
 */
public class LyricFileParserImpl implements LyricFileParser{
	private List<LyricLine> lines = new ArrayList<LyricLine>();
	
	@Override
	public List<LyricLine> parseLyric(String fileName){
		File file = new File(fileName);
		if(!file.exists()){
			System.err.println("No such file.");
			return null;
		}
		ArrayList<String> lyricsTmp = readLyricFromFile(file);
		for(String line : lyricsTmp){
			parserLine(line);
		}
		return lines;
	}
	
	/**
	 * Read content of the file
	 * @param file 
	 * @return String content
	 */
	public ArrayList<String> readLyricFromFile(File file){
		ArrayList<String> lyricList = new ArrayList<String>();
		String line = null;
		RandomAccessFile in = null;
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
	
	/**
	 * Parse a line, get line time and text
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
				
				LyricLine aline = new LyricLine(currentTime,currentContent);
				lines.add(aline);
			}
		}
	}
}
