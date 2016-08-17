package com.tianku.client.mao;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class FileDownload implements Runnable{
	private OnDataChangedListener listener = null;
	private URI path = null,savePath = null;
	private HttpClient client;
	private HttpGet httpGet;
	private boolean down = true;
	private int bufflen = 10240;
	
	FileDownload(String Path,String savePath){
		if(Path!=null)path = URI.create(Path);
		if(savePath != null){
			this.savePath = URI.create(savePath);
		}
	}
	
	FileDownload(String Path){
		this(Path,null);
	}
	
	FileDownload(){
		this(null);
	}
	
	public void setOnDataChangedListener(OnDataChangedListener listener){
		this.listener = listener;
	}
	
	public void setUrl(String url){
		this.path = URI.create(url);
	}
	
	public void setSavePath(String p){
		this.savePath = URI.create(p);
	}
	
	public void setBufferLen(int len){
		this.bufflen = len;
	}
	
	public void exec(){
		down = true;
		new Thread(this).start();
	}
	
	public void Stop(){
		down = false;
	}
	
	public void run(){
		try {
			httpGet = new HttpGet(path);
			client = new DefaultHttpClient();
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			long total = entity.getContentLength();
			if(listener!=null){
				listener.OnStart(total);
			}
			InputStream is = entity.getContent();
			File sPath = new File(savePath.getPath());
			if(!sPath.exists())sPath.mkdirs();
			File f = new File(path.getPath());
			String sFile = savePath.getPath()+"/"+f.getName()+".tmp";
			FileOutputStream fos = new FileOutputStream(sFile);
			
			byte[] buffer = new byte[bufflen];
			int offset=0,readnum=0;
			while((readnum = is.read(buffer))>0){
				if(!down){
					break;
				}
				offset+=readnum;
				if(listener!=null){
					listener.OnProgressChanged(total, offset);
				}
				fos.write(buffer, 0, readnum);
			}
			fos.flush();
			fos.close();
			is.close();
			response = null;
			httpGet.abort();
			httpGet = null;
			client = null;
			entity = null;
			if(listener!=null){
				listener.OnDownLoadComplate(sFile,total);
			}
		} catch (ClientProtocolException e) {
			if(listener!=null){
				listener.OnError(e);
			}
		} catch (IOException e) {
			if(listener!=null){
				listener.OnError(e);
			}
		}
	}
}

interface OnDataChangedListener{
	public void OnStart(long total);
	public void OnProgressChanged(long total,long current);
	public void OnDownLoadComplate(String path,long total);
	public void OnError(Exception e);
}