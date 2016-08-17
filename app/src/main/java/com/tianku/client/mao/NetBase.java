package com.tianku.client.mao;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class NetBase extends Service{
	
	  public static String Result=null,version=" ver:1.8";
	  public static String PHONE_IMEI;
	  public static Context context;
	  public static HttpPost Request;
	    
		public static void Send(String url, List<NameValuePair> params,String mode) {
			
			Result=null; 
			//Log.v("数据发送",url);
			String ServerUri = url;
			Request = new HttpPost(ServerUri);

			try{
				version=" ver:"+context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
			}catch(Exception ex1){
				
			}
			try{
				Request.addHeader("User-Agent", "TK_SMSMODEL/"+android.os.Build.MODEL+";"+android.os.Build.VERSION.RELEASE+version);
				Request.addHeader("PHONE_X",android.os.Build.MODEL);
				//Request.addHeader("MAO_VERIFY","X046D853510BF531FB18BAFE23644926");
			}catch(Exception ex){
				Request.addHeader("User-Agent", "TK_SMSMODEL_M9/"+android.os.Build.MODEL+";"+android.os.Build.VERSION.RELEASE+version);
				Request.addHeader("PHONE_X",android.os.Build.MODEL);
				//Request.addHeader("MAO_VERIFY","X046D853510BF531FB18BAFE23644926");
			}

			try {
				NetBase.PHONE_IMEI=Conf.get(context, "imei").toString();
				Request.addHeader("IMEI",PHONE_IMEI);
			} catch (Exception e1) { 
				Request.addHeader("IMEI","Null");
			}
			
			try {
				//String user=Conf.get(context, "username").toString().trim();
				String pass=Conf.get(context, "auth_code").toString().trim();
				//Request.addHeader("User-Name",user);
				Request.addHeader("Client-Auth-Code",pass);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			if(mode!=null){
				Request.addHeader("MODE",mode);
			}
			int SEND_SIZE=0,REVICE_SIZE=0;
			try {
				Request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				String strSend=EntityUtils.toString(Request.getEntity());
				SEND_SIZE=SEND_SIZE+strSend.length();
				// 取得HTTP response
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(Request);
				// 若状态码为200 ok
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// 取出回应字串
					String strResult = EntityUtils.toString(httpResponse
							.getEntity());
					REVICE_SIZE=REVICE_SIZE+strResult.length();
					Result = strResult;
				} else { 
					String strResult =httpResponse.getStatusLine().toString();
					Result="Error Response:"
					+ strResult;
					REVICE_SIZE=REVICE_SIZE+strResult.length();
				}
				

				try {
					Conf.set(context, "REVICE_SIZE",(Integer)Conf.get(context, "REVICE_SIZE")+REVICE_SIZE);
					Conf.set(context, "SEND_SIZE",(Integer)Conf.get(context, "SEND_SIZE")+ SEND_SIZE);
					//Log.v("流量", "发送:"+SEND_SIZE+" 接收:"+REVICE_SIZE);
					 
					MaoService.AddNotification(context.getString(R.string.app_name)+
							"运行中","点击查看",
							Conf.JsLiuLiang((Integer)Conf.get(context, "REVICE_SIZE")),
							Conf.JsLiuLiang((Integer)Conf.get(context, "SEND_SIZE")));		
				} catch (ParseException e) {
					//Log.v("流量统计错误",e.toString());
				}
			} catch (UnsupportedEncodingException e) {
				Result="Unsupported Error:"+e.getMessage();
			} catch (ClientProtocolException e) {
				Result="ClientProtocol Error:"+e.getMessage();
			} catch (IOException e) {
				Result="网络错误";
			}
		}
		
		public static void Send(String url) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				Send(url,params,null);
			} catch (Exception e) {
				Result=e.toString();
			}
		}
		public static void Send(String url,String data){
			//Log.v("data", data);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try{
				if(data.contains("&")){
					String[] keyval=data.split("&");
					for(int i=1;i<=keyval.length;i++){
						String[] temp=keyval[i-1].split("=");
						params.add(new BasicNameValuePair(temp[0].trim(),temp[1].trim()));
					}
				}else if(data.contains("=")){
					String[] temp=data.split("=");
					params.add(new BasicNameValuePair(temp[0].trim(),temp[1].trim()));
				}else{
					Result="params Error!";
				}
				Send(url,params,null);
			}catch(Exception e){
				Result=e.toString();
			}
			//Result=""+params.size();
		}
		public static boolean Send(String url,String data,String mode){
			//Log.v("data", data);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try{
				if(data.contains("&")){
					String[] keyval=data.split("&");
					for(int i=1;i<=keyval.length;i++){
						String[] temp=keyval[i-1].split("=");
						params.add(new BasicNameValuePair(temp[0].trim(),temp[1].trim()));
					}
				}else if(data.contains("=")){
					String[] temp=data.split("=");
					params.add(new BasicNameValuePair(temp[0].trim(),temp[1].trim()));
				}else{
					Result="params Error!";
					return false;
				}
				Send(url,params,mode);
			}catch(Exception e){
				Result=e.toString();
			}
			//Result=""+params.size();
			return true;
		}

		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			return null;
		}
}
