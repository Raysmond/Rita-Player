package com.raysmond.song;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;

public class SongList implements ListController {
	protected File listFile = null;
	protected LinkedList<Song> list = new LinkedList<Song>();
	protected Song current = null;
	protected int currentIndex = 0;
	
	public static final int MODEL_ONE = 0;
	public static final int MODEL_ONE_LOOP = 1;
	public static final int MODEL_LIST_ONCE = 2;
	public static final int MODEL_LIST_LOOP = 3;
	public static final int MODEL_RANDOM = 4;
	protected int currentModel = MODEL_ONE;
	
	public SongList(File file){
		setListFile(file);
	}
	
	public void setListFile(File file){
		this.listFile = file;
	}
	
	public File getListFile(){
		return listFile;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void open(File file) {
		setListFile(file);
		if(!listFile.exists()){
			try {
				listFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileInputStream fi = null;
		try {
			fi = new FileInputStream(listFile);
			ObjectInputStream oi = new ObjectInputStream(fi);
			list.clear();
			list = (LinkedList<Song>)oi.readObject();
			fi.close();
			oi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public synchronized void save() {
		if(!listFile.exists()){
			try {
				listFile.createNewFile();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		try{
			FileOutputStream fo = new FileOutputStream(listFile);
			ObjectOutputStream out = new ObjectOutputStream(fo);
			out.writeObject(list);
			out.close();
			fo.close();
		}catch(Exception e){}
	}
	
	@Override
	public void add(Song song) {
		if(!isNewSong(song)) return;
		list.add(song);
	}
	
	@Override
	public void add(Song[] song) {
		for(Song s: song){
			add(s);
		}
	}
	public boolean isNewSong(Song song){
		ListIterator<Song> it  = list.listIterator();
		Song tmp = null;
		while(it.hasNext()){
			tmp = it.next();
			if(tmp.equals(song)) return false;
		}
		return true;
	}
	@Override
	public void delete(Song song) {
		list.remove(song);
	}
	@Override
	public void delete(Song[] song) {
		for(Song s:song) delete(song);
		
	}
	@Override
	public int size() {
		return list.size();
	}

	public Song get(int i){
		if(i>=0&&i<=list.size()){
			setCurrent(list.get(i));
			currentIndex = i;
			return getCurrentSong();
		} 
		return null;
	}
	
	@Override
	public void setPlayModel(int model) {
		if(model>=0&&model<=4) currentModel = model;
	}
	
	@Override
	public int getPlayModel(){
		return currentModel;
	}

	@Override
	public Song playPrev() {
		switch(currentModel){
			case SongList.MODEL_ONE:
				setCurrent(null);
				return getCurrentSong();
			case SongList.MODEL_ONE_LOOP: 
				return getCurrentSong();
			case SongList.MODEL_LIST_ONCE: 
				--currentIndex;
				if(currentIndex<0) ++currentIndex;
				setCurrent(list.get(currentIndex));
				return getCurrentSong();
			case SongList.MODEL_LIST_LOOP: 
				--currentIndex;
				if(currentIndex<0) currentIndex = list.size() - 1;
				setCurrent(list.get(currentIndex));
				return getCurrentSong();
			case SongList.MODEL_RANDOM: 
				currentIndex = ((int)(Math.random()*list.size()))%list.size();
				setCurrent(list.get(currentIndex));
				return getCurrentSong();
		}
		return null;
	}
	
	@Override
	public Song playNext() {
		switch(currentModel){
			case SongList.MODEL_ONE:
				setCurrent(null);
				return getCurrentSong();
			case SongList.MODEL_ONE_LOOP: 
				return getCurrentSong();
			case SongList.MODEL_LIST_ONCE: 
				++currentIndex;
				if(currentIndex>=list.size()) --currentIndex;
				setCurrent(list.get(currentIndex));
				return getCurrentSong();
			case SongList.MODEL_LIST_LOOP: 
				currentIndex = (++currentIndex)%list.size();
				setCurrent(list.get(currentIndex));
				return getCurrentSong();
			case SongList.MODEL_RANDOM: 
				currentIndex = ((int)(Math.random()*list.size()))%list.size();
				setCurrent(list.get(currentIndex));
				return getCurrentSong();
		}
		return null;
	}

	public void setCurrent(Song song){
		this.current = song;
	}
	@Override
	public Song getCurrentSong() {
		return current;
	}

	@Override
	public LinkedList<Song> getList() {
		return list;
	}

	
}
