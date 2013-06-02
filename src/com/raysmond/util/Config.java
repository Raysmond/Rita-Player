package com.raysmond.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户对软件的设置
 * 主题，背景，颜色等设置
 * @author Jiankun Lei
 *
 */
public class Config implements Serializable{
	private static final long serialVersionUID = -5413358103910013170L;
	//private static final String configFilePath = "./data/config.dat";
	private int currentTheme = 0;
	private Date lastUseAppTime = null;
	public static enum PlayMode{ONCE,ONE_LOOP,LIST_ONE,LIST_LOOP,RANDOM};
	private PlayMode playMode = PlayMode.LIST_ONE;
	private float volumeGain = 1.0F;
	public Config(){}
	
	public int getPlayMode() {
		if(playMode==PlayMode.ONCE) return 0;
		else if(playMode==PlayMode.ONE_LOOP) return 1;
		else if(playMode==PlayMode.LIST_ONE) return 2;
		else if(playMode==PlayMode.LIST_LOOP) return 3;
		else if(playMode==PlayMode.RANDOM) return 4;
		else return 0;
	}

	public void setPlayMode(int playChoices) {
		switch(playChoices){
			case 0: this.playMode = PlayMode.ONCE; break;
			case 1: this.playMode = PlayMode.ONE_LOOP; break;
			case 2: this.playMode = PlayMode.LIST_ONE; break;
			case 3: this.playMode = PlayMode.LIST_LOOP; break;
			case 4: this.playMode = PlayMode.RANDOM; break;
			default:
		}
		writeConfig();
	}

	/**
	 * 从本地配置文件config.data中加载配置信息
	 * @return
	 */
	public static Config loadConfig(){
		Config config = null;
		File configFile = new File(Util.configFilePath);
		FileInputStream fi;
		if(!configFile.exists()){
			try {
				configFile.createNewFile();
				Util.config.writeConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fi = new FileInputStream(configFile);
			ObjectInputStream oi = new ObjectInputStream(fi);
			config = (Config)oi.readObject();
			fi.close();
			oi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}
	
	/**
	 * 把配置信息写入本地文件
	 */
	public void writeConfig(){
		File configFile = new File(Util.configFilePath);
		if(!configFile.exists()){
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			FileOutputStream fo = new FileOutputStream(configFile);
			ObjectOutputStream out = new ObjectOutputStream(fo);
			out.writeObject(this);
			out.close();
			fo.close();
		}catch(Exception e){}
	}
	
	//getters and setters
	public int getCurrentTheme() {
		return currentTheme;
	}

	public void setCurrentTheme(int currentTheme) {
		this.currentTheme = currentTheme;
	}

	public Date getLastUseAppTime() {
		return lastUseAppTime;
	}

	public void setLastUseAppTime(Date lastUseAppTime) {
		this.lastUseAppTime = lastUseAppTime;
	}
	
	public float getVolume(){
		return volumeGain;
	}
	
	public void setVolume(float v){
		this.volumeGain = v;
	}

	
}
