package com.tianku.client.mao;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

public class MaoService extends Service{ 
	//private static final String TAG="猫服务";
	public static final int ID = 1213;
    public static NotificationManager nm; 
    public static Context context;
    public static boolean status=false;
	@Override
	public IBinder onBind(Intent intent) { 
		 return null;  
	}
	@Override
	public void onCreate() {
		context=this;
		//Log.v(TAG, "onCreate");
		getContentResolver().query(Uri.parse("content://sms/"),
				new String[] { "_id", "address" }, " type=?",
				new String[] { "1" }, "date desc");
				SmsReceiver content = new SmsReceiver(this, new Handler());
				getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content); 
		status=true;
		//显示通知
		AddNotification(getString(R.string.app_name)+"运行中",
				"点击查看 ",
				Conf.JsLiuLiang((Integer)Conf.get(MaoService.this, "REVICE_SIZE"))
				,Conf.JsLiuLiang((Integer)Conf.get(MaoService.this, "SEND_SIZE")));		
	}
	public static  void AddNotification(String title,String cont,String REVICE_SIZE,String SEND_SIZE) {
		if(!status)return;
		// 添加通知到顶部任务栏
		// 获得NotificationManager实例
		String service = NOTIFICATION_SERVICE;
		// 实例化Notification
		Notification n = new Notification();
		// 设置显示图标
		int icon = R.drawable.mobile48;
		// 设置提示信息
		String tickerText = context.getString(R.string.app_name);
		// 显示时间
		long when = System.currentTimeMillis();

		n.icon = icon;
		n.tickerText = tickerText;
		n.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS;
		//n.defaults = Notification.DEFAULT_VIBRATE;
		//n.defaults = Notification.DEFAULT_LIGHTS;
		n.when = when;
		n.contentView = new RemoteViews(context.getPackageName(),
				R.layout.tongzhilan);
		n.contentView.setTextViewText(R.id.tong_title, title);
		n.contentView.setTextViewText(R.id.tongzhi_cont, cont);
	    n.contentView.setTextViewText(R.id.liuliang_jieshou,REVICE_SIZE);
	    n.contentView.setTextViewText(R.id.liuliang_fs,SEND_SIZE);
		// 显示在“正在进行中”
		// n.flags = Notification.FLAG_ONGOING_EVENT;
		n.flags |= Notification.FLAG_NO_CLEAR; // 自动终止
		n.flags |= Notification.FLAG_ONGOING_EVENT;
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.defaults = Notification.DEFAULT_LIGHTS;
		n.ledARGB = Color.BLUE;
		n.ledOnMS = 5000;
		// 实例化Intent
		Intent it = new Intent(context, MaoActivity.class);
		NotificationManager nm = (NotificationManager)context.getSystemService(service);
		// PendingIntent pi = PendingIntent.getActivity(this, 0, it,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// n.setLatestEventInfo(this,title, cont, pi);
		n.contentIntent = PendingIntent.getActivity(context, 0, it,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 发出通知
		nm.notify(MaoService.ID, n);
	}
	    
	@Override
	public void onDestroy() {
		//Log.v(TAG, "onDestroy");
		status=true;
	}

	@Override
	public void onStart(Intent intent, int startId) { 
		//Log.v(TAG, "onStart"); 
	} 
}
