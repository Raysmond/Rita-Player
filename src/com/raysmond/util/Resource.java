package com.raysmond.util;

import java.io.*;  
import java.net.URL;
public class Resource {  
	
    public static URL getResource(String path){  
    	return Resource.class.getClassLoader().getResource(path);
    }  
    
    public static InputStream getResourceAsStream(String path){
    	return Resource.class.getClassLoader().getResourceAsStream(path);
    }
    
    public static File getFile(String path)
    {
    	return new File(Resource.class.getClassLoader().getResource(path).getFile());
    }
        
}  