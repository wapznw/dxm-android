package com.tianku.client.mao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts.Phones;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class PhoneReceiver extends BroadcastReceiver {
	private boolean call_status=false,auto_cut=false,Filter_Book=false;
	private Handler handler = new Handler();
	public static String data,com,phoneNr,REG_RECOVERY_STR,PASS_RECOVERY_STR;
	public static String[] command;
	private Context contexts;
	private TelephonyManager telephony;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		try {
			call_status=(Boolean)Conf.get(context, "CALL_STATUS");
			auto_cut=(Boolean)Conf.get(context, "AUTO_CUT");
			Filter_Book=(Boolean)Conf.get(context, "Filter_Book");
			REG_RECOVERY_STR=(String)Conf.get(context, "REG_RECOVERY_STR");
			PASS_RECOVERY_STR=(String)Conf.get(context, "PASS_RECOVERY_STR");
			if(!call_status)return;
		} catch (Exception e2) {
			Toast.makeText(context, e2.getMessage()+"读配置错误", Toast.LENGTH_LONG).show();
		}

		Bundle bundle = intent.getExtras();
		phoneNr= bundle.getString("incoming_number");
		try {
			if(phoneNr!=null){
				if (phoneNr.indexOf(" ") != -1)
					phoneNr = phoneNr.replaceAll("[\\s]", "");
			}
		} catch (Exception e) {
			Toast.makeText(context, "phoneNr:"+phoneNr+"\nError:"+e.getMessage(), Toast.LENGTH_LONG).show();
		}
		try {
			/********检测来电号码是否是通讯录中号码*********/
			if(Filter_Book){
				Cursor cursor = context.getContentResolver().query(Phones.CONTENT_URI, null, null, null, null);
				while(cursor.moveToNext()){
					try {
						// cursor.getString(cursor.getColumnIndex(Phones.NUMBER));
						if(phoneNr!=null){
							String bookphone=cursor.getString(cursor.getColumnIndex(Phones.NUMBER)).replaceAll("[\\s]", "");
							if (bookphone.equals(phoneNr.replaceAll("\\+\\d{2}",""))) {
								//Toast.makeText(context, "天酷短信猫:\n不注册号码:"+phoneNr, Toast.LENGTH_LONG).show();
								return;
							}
						}
					} catch (Exception e) {
						Toast.makeText(context, e.getMessage()+"Book Error!", Toast.LENGTH_LONG).show();
					}
				} 
			}
			/********检测来电号码是否是通讯录中号码*********/
		} catch (Exception e4) {
			Toast.makeText(context, "检测来电号码是否是通讯录中号码错误", Toast.LENGTH_LONG).show();
		}

		try {
			telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			/****自动挂断*****/
			if(auto_cut && telephony.getCallState()!=TelephonyManager.CALL_STATE_OFFHOOK){
				try {
					ITelephony iTelephony=getITelephony(telephony);
					iTelephony.endCall();//自动挂断电话   
				} catch (Exception e) {
					Toast.makeText(context, e.getMessage()+"自动挂断电话 错误", Toast.LENGTH_LONG).show();
				}
			}
			/****自动挂断*****/   
			if(telephony.getCallState()!=TelephonyManager.CALL_STATE_IDLE)return;
		} catch (Exception e3) {
			Toast.makeText(context,"自动挂断错误", Toast.LENGTH_LONG).show();
		}
		this.contexts=context;
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
		if(phoneNr!=null && command.length>0){
			Toast.makeText(context, context.getString(R.string.app_name)+":\n来电号码:"+phoneNr, Toast.LENGTH_LONG).show();
			new Thread(new Runnable(){

				public void run() {
					// TODO Auto-generated method stub
					/****提交短信开始********/
					//data=SmsSend.P_Call(contexts,  "phone="+phoneNr+"&message="+command[0]);
					handler.post(new Runnable(){
						public void run() {  
							if(phoneNr.length()>11)phoneNr=phoneNr.substring(phoneNr.length()-11, phoneNr.length());
							Processinginformation p=new Processinginformation();
   							p.exec(contexts,phoneNr, command[0],Processinginformation.NET_TYPE.CALL);
							
						}
					});
					/****提交短信结束**********/
				}
				
			}).start();
		}
	}
	/**
	 * 根据传入的TelephonyManager来取得系统的ITelephony实例.
	 * @param telephony
	 * @return 系统的ITelephony实例
	 * @throws Exception
	 */
	public static ITelephony getITelephony(TelephonyManager telephony) throws Exception { 
        Method getITelephonyMethod = telephony.getClass().getDeclaredMethod("getITelephony"); 
        getITelephonyMethod.setAccessible(true);//私有化函数也能使用 
        return (ITelephony)getITelephonyMethod.invoke(telephony); 
    }
}
