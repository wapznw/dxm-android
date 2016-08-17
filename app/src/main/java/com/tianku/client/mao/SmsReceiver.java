package com.tianku.client.mao;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.db.DatabaseHelper;

import java.net.URLDecoder;

public class SmsReceiver extends ContentObserver{
	static final String[] SMS_PROJECTION=new String[]{
        "_id","address","body","read","type"
	};
	private Cursor cursor = null;
	static String body;
    static String address,type,read;
    static String[] command;
    static int id=0;
    private static Context context;
    private Handler handler = new Handler();  
	public String data,REG_RECOVERY_STR,PASS_RECOVERY_STR;
	private DatabaseHelper dh;
	private String sms_setread;
	private boolean sms_model,sms_filter;
	
	public SmsReceiver(Context context,Handler handler) {
		super(handler);
		SmsReceiver.context=context;
	}
	/**
	 * @Description 当短信表发送改变时，调用该方法 
	 * 				需要两种权限
	 * 				android.permission.READ_SMS读取短信
	 * 				android.permission.WRITE_SMS写短信
	 * @Author Snake
	 * @Date 2010-1-12
	 */
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		
		try{
			sms_model=(Boolean) Conf.get(context, "sms_model");
		}catch(Exception e){
			sms_model=true;
		}
		try{
			sms_filter=(Boolean) Conf.get(context, "sms_filter");
		}catch(Exception e){
			sms_filter=false;
		}
		if(!sms_model){ 
			try{
				sms_setread=Conf.get(context, "sms_setread").toString();
			}catch(Exception e){
				sms_setread="3";
			}
			
			String com;
			try {
				com = Conf.get(context, "command").toString();
				if(com.indexOf("|")!=-1)
					command=com.split("\\|");
				else
					command=new String[]{com};
			} catch (Exception e1) {
				com="";
				Toast.makeText(context, "未登录,无法获得短信指令", Toast.LENGTH_LONG).show();
			}
			cursor=context.getContentResolver().query(Uri.parse("content://sms/"),SMS_PROJECTION, " type=? and read=?", new String[]{"1","0"}, "date desc");
			 
			 if (cursor!=null) {
	             if (cursor.moveToFirst()) {
	                     address=cursor.getString(cursor.getColumnIndex("address")).toString().replaceAll("[\\s]", "");
	                     body=cursor.getString(cursor.getColumnIndex("body")).toString();
	                     type=cursor.getString(cursor.getColumnIndex("type")).toString();
	                     read=cursor.getString(cursor.getColumnIndex("read")).toString();
	                     int _id=cursor.getInt(cursor.getColumnIndex("_id"));
	                     if(body.indexOf('%')!=-1){
								body=URLDecoder.decode(body);
						 }
	                     if(_id!=id){
	                    	 id=_id;
	                    	 _id=0; 
	                    	 if(sms_filter){
	                    		 for(int i=0;i<command.length;i++){ 
		                    		 if(body.toLowerCase().indexOf(command[i].toLowerCase())!=-1){
		                            	 Toast.makeText(context,context.getString(R.string.app_name)+":\n"+context.getString(R.string.Number)+":"+ address+ "\n"+context.getString(R.string.Content)+":" + body,Toast.LENGTH_LONG).show();
		                            	 new Thread(new xRunnalbe()).start();
		                            	break;
		                    		 }
		                    	 }
	                    	 }else{
	                    		 Toast.makeText(context,context.getString(R.string.app_name)+":\n"+context.getString(R.string.Number)+":"+ address+ "\n"+context.getString(R.string.Content)+":" + body,Toast.LENGTH_LONG).show();
	                    		 new Thread(new xRunnalbe()).start();
	                    	 }  
	                     }
	             }
	         cursor.close();
	     }
		}
	}
	
	class xRunnalbe implements Runnable{
		
		public void run(){

				try { 
					handler.post(new Runnable(){
						public void run() {
							Processinginformation p=new Processinginformation();
							if(address.length()>11)address=address.substring(address.length()-11, address.length());
							p.exec(context,address, body);
						}
					});
					
				} catch (Exception ex) {
					//Log.v("短信猫3", "入口3");
					handler.post(new Runnable(){
						public void run() {
							data=context.getString(R.string.login_status_network_or_connection_error);
							dh=new DatabaseHelper(context);
			        		dh.insert(context, "sms_phone="+address+",sms_text="+body+",sms_status=2,sms_result="+data);
			        		try {
							MaoActivity.listshow(context);
						} catch (Exception e) { 
							Log.v("猫错误","刷新显示失败");
						}
							Toast.makeText(context, data,
									Toast.LENGTH_LONG).show();
						}                   								
					});
				} 
				if(sms_setread.equals("1")){
					try {
					/****收到短信设为已读代码开始*******/
					ContentValues values = new ContentValues();
					values.put("read", "1");
					context.getContentResolver().update(Uri.parse("content://sms/"), values, "_id=?", new String[]{id+""});
					/****收到短信设为已读代码结束*******/
				} catch (Exception e) {
					Log.v("短信猫错误", "短信设为已读失败");
				}
				}else if(sms_setread.equals("2")){
					try {
					/****收到短信删除代码开始*******/
					context.getContentResolver().delete(Uri.parse("content://sms/"), "_id=?", new String[]{id+""});
					/****收到短信删除代码结束*******/
				} catch (Exception e) {
					Log.v("短信猫错误", "短信删除失败");
				}
				}else{
					Log.v("短信猫错误", "不处理");
				}
		}
	}
}