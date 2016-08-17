package com.tianku.client.mao;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SmsSend extends NetBase{

	public static String API_URL;
	
	public static String P(Activity a,String data,String m){
		//Log.v("发送数据",data);
		NetBase.context=a;
		API_URL=Conf.Api_Host(a)+"/index.php/Mao/ApiJson?_Sm="; 
		NetBase.Send(API_URL+Conf.get(a,"sid").toString(), data,m);
		return NetBase.Result;
	}
	
	public static String P(Activity a,String data){
		return P(a, data, "SEND");
	}
	
	public static String P(Context c,String data){
		//Log.v("发送数据",data);
		NetBase.context=c;
		API_URL=Conf.Api_Host(c)+"/index.php/Mao/ApiJson?_Sm="; 
		 NetBase.Send(API_URL+Conf.get(c,"sid"), data,"SMS");
		 return NetBase.Result;
	}
	
	public static String P_Call(Context c,String data){
		//Log.v("发送数据",data);
		NetBase.context=c;
		API_URL=Conf.Api_Host(c)+"/index.php/Mao/ApiJson?_Sm="; 
		 NetBase.Send(API_URL+Conf.get(c,"sid"), data,"CALL");
		 return NetBase.Result;
	}
	
	public static boolean SMS_SEND(Context context,String strNo,String Message){
		try {
			SmsManager smsManager = SmsManager.getDefault();
			PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
			smsManager.sendTextMessage(strNo, null, Message, sentIntent, null);
			Toast.makeText(context,"回复短信:\n"+Message, Toast.LENGTH_LONG).show();
			return true;
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}
	}
}
