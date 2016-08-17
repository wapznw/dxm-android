package com.tianku.client.mao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.db.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalSms extends Activity {
	static final String[] SMS_PROJECTION=new String[]{
        "_id","address","body","read","type","date"
	};
	Button back = null;
	ListView list = null;
	private Handler handler=new Handler();
	String data;
	
	public void onCreate(Bundle savedInstanceState)
	{
		Activitys.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localsms);
		back = (Button)findViewById(R.id.top_back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		list = (ListView)findViewById(R.id.listView1);
		list.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> av, View v, int arg2,
					long arg3) {
				final TextView phone=(TextView)v.findViewById(R.id.log_phone);
				final TextView body=(TextView)v.findViewById(R.id.log_cont);
				try {
					new AlertDialog.Builder(LocalSms.this)
					.setTitle("是否转发")
					.setMessage("号码:"+phone.getText()+
							"\n内容:"+body.getText())
					.setNeutralButton(R.string.general_lable_ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							final ProgressDialog progr=ProgressDialog.show(LocalSms.this, null, "发送中...");
				        	new Thread(new Runnable(){
				        		public void run(){
				        			try {
				        				final DatabaseHelper dh=new DatabaseHelper(LocalSms.this);
				        				final long insertId = dh.insert(LocalSms.this,
				        						"sms_phone=" + phone.getText().toString() + ",sms_text="
				        								+ body.getText().toString()
				        								+ ",sms_status=2,sms_result="
				        								+ "等待转发");
				        				if(phone.getText().toString().indexOf(" ") != -1)
				        					data="phone="+phone.getText().toString().replaceAll("[\\s]", "")+"&message="+body.getText().toString();
				        				else
				        					data="phone="+phone.getText().toString()+"&message="+body.getText().toString();
				        				data=SmsSend.P(LocalSms.this,data,"LOCAL");
										handler.post(new Runnable(){
											public void run(){
												try {
													JSONObject json=new JSONObject(data);
													//data.equals("注册成功") || data.equals("设置密码成功")
													if(json.getBoolean("STS")){
														dh.update(LocalSms.this, "sms_status=1,sms_result="+json.getString("MSG"), insertId);
														MaoActivity.listshow(LocalSms.this);
													}else{
														dh.update(LocalSms.this, "sms_result="+json.getString("MSG"), insertId);
														MaoActivity.listshow(LocalSms.this);
													}
													Toast.makeText(getBaseContext(), json.getString("MSG"), Toast.LENGTH_LONG).show();
												} catch (JSONException e) {
													Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_LONG).show();
												}
											}
										});
									} catch (Exception e) { 
										e.printStackTrace();
									}
									progr.dismiss();
				        		}
				        	}).start();
						}
					}).setNegativeButton(R.string.general_lable_cancel, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					})
					.show();
				} catch (Exception e) {
					Toast.makeText(LocalSms.this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
		try{
			Cursor cur = getContentResolver().query(Uri.parse("content://sms/"), SMS_PROJECTION, null, null, "date desc");
			Toast.makeText(getApplicationContext(), "共计:"+cur.getCount()+"条", Toast.LENGTH_LONG).show();
			SimpleAdapter adapter = new SimpleAdapter(this, 
					getData(cur), 
					R.layout.listlocal, 
					new String[]{"date","address","body"}, 
					new int[]{R.id.log_time,R.id.log_phone,R.id.log_cont});
			list.setAdapter(adapter);
		}catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "error:"+e.getLocalizedMessage()+","+e.hashCode(), Toast.LENGTH_LONG).show();
		}
	}
	

	public void onResume(){
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		StatService.onPause(this);
	}
	
	private List<Map<String, Object>> getData(Cursor c) {
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		c.moveToFirst();
		for(int i=0;i<c.getCount();i++){
			map = new HashMap<String, Object>();
			map.put("address", c.getString(c.getColumnIndex("address")));
			map.put("body",c.getString(c.getColumnIndex("body")));
			map.put("date",format.format(new Date(c.getLong(c.getColumnIndex("date")))));
			l.add(map);
			c.moveToNext();
		}
		c.close();
		return l;
	}
}
