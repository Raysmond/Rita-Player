package com.raysmond.internet;
import java.net.*;
import java.io.*;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.raysmond.song.DownloadSong;
import com.raysmond.view.SearchSongDialog;

/**
 * 文件下载线程类
 * 
 * @author Jiankun Lei
 *
 */
public class DownloadThread extends Thread
{
    private SearchSongDialog owner;   //引用
    private String inUrl;			  //下载链接
    private String outPath;			  //保存本地的目录位置
    private String outName;		      //保存到本地的文件名（包括后缀）
    private DownloadSong song;
    
    //太强了,居然还能远程码代码
    //iPad真牛逼
    
    public DownloadThread(SearchSongDialog owner,DownloadSong song,String path){
    	 setOwner(owner);
    	 this.song = song;
         inUrl = song.getDownloadUrl();
         outPath = path;
         outName = song.getFileName();
    }
    
    /**
     * 运行下载线程，开始下载
     */
    public void run()
    {
        boolean error = false;
        try
        {
            URL url = new URL(inUrl);
            //String filename = url.getFile();
            try
            {
                DataInputStream in = new DataInputStream(url.openConnection().getInputStream()); 
                FileOutputStream fOut = new FileOutputStream(outPath+"/"+outName);
                DataOutputStream out = new DataOutputStream(fOut);
                
                int chc;
                double downedsize = 0;
                while ((chc = in.read()) != -1)
                {
                    out.write(chc);
                    ++downedsize;
                    //System.out.println("downloaded: " + downedsize + " bytes." + " ------ " + (downedsize/1024) + " KB");
                }
                in.close();
                fOut.flush(); 
                fOut.close();
            }
            catch (IOException e)
            {
            	System.out.println("Unable to download");
                error = true;
            }
        }
        catch (MalformedURLException e)
        {
            System.out.println("Unable to download");
            error = true;
        }
        if (!error){
        	System.out.println("Download finished:" + this.inUrl);
        	owner.completeDownload(song);
        } 
        return;
    }

    
    //getters and setters
    
	public SearchSongDialog getOwner() {
		return owner;
	}

	public void setOwner(SearchSongDialog owner) {
		this.owner = owner;
	}
}
