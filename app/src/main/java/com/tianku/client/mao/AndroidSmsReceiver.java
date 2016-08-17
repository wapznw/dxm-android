package com.tianku.client.mao;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.db.DatabaseHelper;

import java.net.URLDecoder;

public class AndroidSmsReceiver extends BroadcastReceiver {
    private Handler handler = new Handler();
    private Context contexts;
    private boolean sms_model, sms_othernotice, sms_filter;
    public String sid, data, sms_setread;
    static String body, address;
    public static String[] command;
    private DatabaseHelper dh;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            sms_setread = Conf.get(context, "sms_setread").toString();
        } catch (Exception e) {
            sms_setread = "1";
        }
        try {
            sms_model = (Boolean) Conf.get(context, "sms_model");
        } catch (Exception e) {
            sms_model = true;
        }
        try {
            sms_filter = (Boolean) Conf.get(context, "sms_filter");
        } catch (Exception e) {
            sms_filter = false;
        }
        try {
            sms_othernotice = (Boolean) Conf.get(context, "sms_othernotice");
        } catch (Exception e) {
            sms_othernotice = false;
        }
        if (sms_model) {//判断是否开机启动和模块正确
            sid = Conf.get(context, "sid").toString();
            this.contexts = context;
            String com;
            try {
                com = Conf.get(context, "command").toString();
                if (com.indexOf("|") != -1)
                    command = com.split("\\|");
                else
                    command = new String[]{com};
            } catch (Exception e1) {
                com = "";
                Toast.makeText(context, "未登录,无法获得短信指令", Toast.LENGTH_LONG).show();
            }
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object messages[] = (Object[]) bundle.get("pdus");
                SmsMessage smsMessage[] = new SmsMessage[messages.length];
                for (int n = 0; n < messages.length; n++) {
                    smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                    try {
                        address = smsMessage[0].getDisplayOriginatingAddress().replaceAll("[\\s]", "");

                        body = smsMessage[0].getMessageBody();
                        if (body.indexOf('%') != -1) {
                            body = URLDecoder.decode(body);
                        }
                        if (sms_filter) {
                            for (int i = 0; i < command.length; i++) {
                                if (body.toLowerCase().contains(command[i].toLowerCase())) {
                                    Toast.makeText(context, context.getString(R.string.app_name) + ":\n" + context.getString(R.string.Number) + ":" + address + "\n" + context.getString(R.string.Content) + ":" + body, Toast.LENGTH_LONG).show();

                                    if (sms_othernotice) this.abortBroadcast();//终止广播


                                    new Thread(new xRunnable()).start();
                                    break;//跳出循环
                                }
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.app_name) + ":\n" + context.getString(R.string.Number) + ":" + address + "\n" + context.getString(R.string.Content) + ":" + body, Toast.LENGTH_LONG).show();

                            if (sms_othernotice) this.abortBroadcast();//终止广播


                            new Thread(new xRunnable()).start();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    class xRunnable implements Runnable {
        public void run() {
            try {
                handler.post(new Runnable() {
                    public void run() {
                        if (address.length() > 11)
                            address = address.substring(address.length() - 11, address.length());
                        Processinginformation p = new Processinginformation();
                        p.exec(contexts, address, body);
                    }
                });
                /****提交短信开始********
                 data=SmsSend.P(contexts,  "phone="+address+"&message="+body);
                 data=data.replace(",", "，");
                 handler.post(new Runnable(){
                 public void run() {
                 if(data.equals("注册成功") || data.equals("设置密码成功")){
                 dh=new DatabaseHelper(contexts);
                 dh.insert(contexts, "sms_phone="+address+",sms_text="+body+",sms_status=1,sms_result="+data);
                 try {
                 MaoActivity.listshow(contexts);
                 } catch (Exception e) {
                 Log.v("猫错误","(普通)刷新显示失败1");
                 }
                 }else{
                 dh=new DatabaseHelper(contexts);
                 dh.insert(contexts, "sms_phone="+address+",sms_text="+body+",sms_status=2,sms_result="+data);
                 try {
                 MaoActivity.listshow(contexts);
                 } catch (Exception e) {
                 Log.v("猫错误","(普通)刷新显示失败5");
                 }
                 }
                 Toast.makeText(contexts,data,
                 Toast.LENGTH_LONG).show();
                 }
                 });
                 ****提交短信结束**********/
            } catch (Exception e) {
                //Log.v("短信猫3", "入口3");
                handler.post(new Runnable() {
                    public void run() {
                        dh = new DatabaseHelper(contexts);
                        data = contexts.getString(R.string.login_status_network_or_connection_error);
                        dh.insert(contexts, "sms_phone=" + address + ",sms_text=" + body + ",sms_status=2,sms_result=" + data);
                        try {
                            MaoActivity.listshow(contexts);
                        } catch (Exception e) {
                            Log.v("猫错误", "(普通)刷新显示失败3");
                        }
                        Toast.makeText(contexts, data,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            if (sms_setread.equals("1")) {
                try {
                    /****收到短信设为已读代码开始*******/
                    ContentValues values = new ContentValues();
                    values.put("read", "1");
                    contexts.getContentResolver().update(Uri.parse("content://sms/"), values, "address=? and body=? and read=?", new String[]{address, body, "0"});
                    /****收到短信设为已读代码结束*******/
                } catch (Exception e) {
                    Log.v("短信猫错误", "(普通)短信设为已读失败");
                }
            } else if (sms_setread.equals("2")) {
                try {
                    /****收到短信删除代码开始*******/
                    contexts.getContentResolver().delete(Uri.parse("content://sms/"), "address=? and body=? and read=?", new String[]{address, body, "0"});
                    /****收到短信删除代码结束*******/
                } catch (Exception e) {
                    Log.v("短信猫错误", "短信删除失败");
                }
            } else {
                Log.v("短信猫错误", "不处理");
            }

        }
    }
}
