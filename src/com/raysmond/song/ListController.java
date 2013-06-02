package com.raysmond.song;

import java.io.File;
import java.util.LinkedList;

public interface ListController {
	
	public void open(File file);
	
	public void save();
	
	public void add(Song song);
	
	public void add(Song[] song);
	
	public void delete(Song song);
	
	public void delete(Song[] song);
	
	public int size();
	
	public Song get(int i);
	
	public void setPlayModel(int model);
	
	public int getPlayModel();
	
	public Song playNext();
	
	public Song playPrev();
	
	public Song getCurrentSong();
	
	public LinkedList<Song> getList();
	
}
