package com.raysmond.internet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GetIntenet {

	/**
	 * 获得网页中的超链接
	 * @author letthinking
	 * @param urlStr url 例如：http://blog.csdn.net/yue19870813?viewmode=list
	 * @return List<String>
	 */
	public List<String> getInternet(String urlStr){
		List<String> list = new ArrayList<String>();
		URL url = null;
		URLConnection conn = null;
		String nextLine = null;
		StringTokenizer tokenizer = null;
		try{
			//获得网页资源
			url = new URL(urlStr);
			//获得资源连接
			conn = url.openConnection();
			conn.connect();
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(conn.getInputStream()));
			//开始读取网页信息解析出网页中的超链接
			while((nextLine = reader.readLine()) != null ){
				tokenizer = new StringTokenizer(nextLine);
				while(tokenizer.hasMoreTokens()){
					String urlToken = tokenizer.nextToken();
					if(isUrl(urlToken)){
						list.add(getHttp(urlToken));
					}
				}
			}
			
		}catch(Exception e){
			
		}
		return list;
	}
	
	/**
	 * 判断字符串中是否含有超链接
	 * @author letthinking
	 * @param urlToken 
	 * @return 
	 */
	public boolean isUrl(String urlToken){
		if(urlToken.indexOf("data-songitem") != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 将字符串中超链接提取出来
	 * @author letthinking
	 * @param urlToken
	 * @return
	 */
	public String getHttp(String urlToken){
		int start = urlToken.indexOf("data-songitem");
		int end = urlToken.length();
		String tempStr = urlToken.substring(start,end);
		end = tempStr.indexOf("}\"");
		if(end == -1){
			end = tempStr.length();
		}
		return tempStr.substring(0,end);
	}
	
	public static void main(String[] args){
		GetIntenet g = new GetIntenet();
		List<String> list = g.getInternet("http://music.baidu.com/search/song?key=%E4%B8%80%E4%B8%9D%E4%B8%8D%E6%8C%82");
		System.out.println("开始输出超链接"); 
		for(String str:list){
			System.out.println(str); 
		}
		//System.out.println(g.getHttp("<link rel=\"stylesheet\" href=\"http://csdnimg.cn/www/css/main_new.css?20110813\" type=\"text/css\" media=\"all\" />"));
	}
}
