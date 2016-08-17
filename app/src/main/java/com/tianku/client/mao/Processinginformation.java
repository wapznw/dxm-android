package com.tianku.client.mao;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.db.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class Processinginformation {
	private static boolean REG_RECOVERY = false, PASS_RECOVERY = false;
	private static String REG_RECOVERY_STR = null, PASS_RECOVERY_STR = null;
	private static DatabaseHelper dh;
	private static Context context;
	private static String address, Message, strRet, retstr;
	private Handler handler = new Handler();
	private  long insertId;

	enum NET_TYPE {
		CALL, SMS
	}
	public String exec(final Context contexts, final String addresss, final String Messages,
			final NET_TYPE type){
		return exec(contexts, addresss, Messages, type, 0);
	}
	public String exec(final Context contexts, final String addresss, final String Messages,
			final NET_TYPE type,final int id) {
		context = contexts;
		address = addresss;
		Message = Messages;
		retstr = null;
		insertId = id;
		try {
			REG_RECOVERY = (Boolean) Conf.get(context, "REG_RECOVERY");
			PASS_RECOVERY = (Boolean) Conf.get(context, "PASS_RECOVERY");
			REG_RECOVERY_STR = (String) Conf.get(context, "REG_RECOVERY_STR");
			PASS_RECOVERY_STR = (String) Conf.get(context, "PASS_RECOVERY_STR");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		dh = new DatabaseHelper(context);

		new Thread(new Runnable() {
			public void run() {
				if(insertId==0){
					insertId = dh.insert(context,
							"sms_phone=" + address + ",sms_text="
									+ Message
									+ ",sms_status=2,sms_result="
									+ "等待转发");
				}
				if (type == NET_TYPE.CALL) {
					strRet = SmsSend.P_Call(context, "phone=" + address
							+ "&message=" + Message);
				} else if (type == NET_TYPE.SMS) {
					strRet = SmsSend.P(context, "phone=" + address
							+ "&message=" + Message);
				}

				handler.post(new Runnable() {
					public void run() {
						try {
							final JSONObject json = new JSONObject(strRet);
							if (json.getBoolean("STS")) {
								/*dh.insert(
										context,
										"sms_phone="
												+ address
												+ ",sms_text="
												+ Message
												+ ",sms_status="
												+ ((json.getString("MSG")
														.indexOf("注册成功") != -1 || json
														.getString("MSG")
														.indexOf("设置密码成功") != -1) ? "1"
														: "2") + ",sms_result="
												+ json.getString("MSG"));*/
								/*dh.update(context, "sms_status="+ ((json.getString("MSG")
										.indexOf("注册成功") != -1 || json
										.getString("MSG")
										.indexOf("设置密码成功") != -1) ? "1"
										: "2") +",sms_result="+json.getString("MSG"), insertId);*/
								dh.update(context, "sms_status="+ (json.getBoolean("STS") ? "1"
										: "2") +",sms_result="+json.getString("MSG"), insertId);
								
								try {
									MaoActivity.listshow(context);
								} catch (Exception e) {
									Log.v("猫错误", "刷新显示失败");
								}
									
								retstr = json.getString("MSG");
								try {
									JSONObject arr = (JSONObject) json
											.get("RT");
									if (REG_RECOVERY
											&& json.getString("MSG").indexOf(
													"注册成功") != -1) {
										String Mess = REG_RECOVERY_STR.replace(
												"{$phone}",
												arr.getString("ACCOUNT"))
												.replace("{$password}",
														arr.getString("PASS"));
										SmsSend.SMS_SEND(context, address, Mess);
									}
									if (PASS_RECOVERY
											&& json.getString("MSG").indexOf(
													"设置密码成功") != -1) {
										String Mess = PASS_RECOVERY_STR
												.replace(
														"{$phone}",
														arr.getString("ACCOUNT"))
												.replace("{$password}",
														arr.getString("PASS"));// "设置密码成功!你的账号为"+phoneNr+" 密码为:"+phoneNr.substring(phoneNr.length()-4);
										SmsSend.SMS_SEND(context, address, Mess);
									}
								} catch (Exception e) {
									Toast.makeText(context, e.getMessage(),
											Toast.LENGTH_LONG).show();
								}
							} else {
								dh.update(context, "sms_status=2,sms_result="+(retstr=json.getString("MSG")), insertId);
								/*dh.insert(context,
										"sms_phone=" + address + ",sms_text="
												+ Message
												+ ",sms_status=2,sms_result="
												+ json.getString("MSG"));*/
								try {
									MaoActivity.listshow(context);
								} catch (Exception e) {
									Log.v("猫错误", "刷新显示失败");
								}

								try {
									Toast.makeText(context,
											json.getString("MSG"),
											Toast.LENGTH_LONG).show();
								} catch (JSONException e) {
									Toast.makeText(context, NetBase.Result,
											Toast.LENGTH_LONG).show();
								}
							}

						} catch (JSONException e) {
							retstr = "NETERROR";
							Toast.makeText(context, NetBase.Result,
									Toast.LENGTH_LONG).show();
							try {
								dh.update(context, "sms_status=2,sms_result=网络错误", insertId);
								/*dh.insert(context, "sms_phone=" + address
										+ ",sms_text=" + Message
										+ ",sms_status=2,sms_result=" + "网络错误");*/
								MaoActivity.listshow(context);
							} catch (Exception e1) {
								Log.v("猫错误", "刷新显示失败");
							}
							

							if(retstr!=null && retstr.equals("NETERROR")){
								Log.v("-----"+retstr, "net error!restart...");
								new Thread(new Runnable(){
									public void run(){
										try {
											System.gc();
											Thread.sleep(15000);
											exec(contexts, addresss, Messages,type,(int)insertId);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}).start();
							}

						}
					}
				});
			}
		}).start();
		

		return retstr;

	}

	public String exec(Context contexts, String addresss, String Messages) {
		return exec(contexts, addresss, Messages, NET_TYPE.SMS);
	}
}
