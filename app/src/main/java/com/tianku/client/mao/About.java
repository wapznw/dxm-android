package com.tianku.client.mao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import org.json.JSONObject;

import java.io.File;

public class About extends Activity{
	private Button ok_btn,feedback_btn,update;
	private TextView version;
	private int vers=0;
	public static Handler handler=new Handler();
	final Activity about =this;
	public ProgressDialog progressdialog;
	FileDownload fd;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 	super.onCreate(savedInstanceState);
		 	Activitys.getInstance().addActivity(this);
		 	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	setContentView(R.layout.about);
		 	ok_btn=(Button)findViewById(R.id.ok_btn);
		 	feedback_btn=(Button)findViewById(R.id.feedback_btn);
		 	update=(Button)findViewById(R.id.about_update);

			NetBase.context=about;
			
		 	version=(TextView)findViewById(R.id.version);
		 	String ver="Version";
			try {
				vers=getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
				ver += ":" + getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) { 
				ver = "程序版本 V";
			}
			try{
				Resources res =getResources();
				String[] entries1=res.getStringArray(R.array.entries);
				String[] entries2=res.getStringArray(R.array.entries_val);
				
				for(int i=0;i<entries2.length;i++){
					if(entries2[i].equals(Conf.Api_Host(this))){
						ver+="\n"+entries1[i];
						break;
					}
				}
				
			}catch(Exception e){}
		 	version.setText(ver);
		 	ok_btn.setOnClickListener(new clickListener());
		 	feedback_btn.setOnClickListener(new clickListener());
		 	update.setOnClickListener(new clickListener());
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
	 
	 private class clickListener implements OnClickListener{ 
		private JSONObject json;
		public void onClick(View view) { 
			switch(view.getId()){
				case R.id.ok_btn:
					about.finish();
					break;
				case R.id.feedback_btn:
					Toast.makeText(getBaseContext(),"此功能暂未开放!", Toast.LENGTH_LONG).show();
					/*startActivity(new Intent(about,FeedBack.class));*/
					break;
				case R.id.about_update:
					Toast.makeText(getBaseContext(),"此功能暂未开放!", Toast.LENGTH_LONG).show();
					/*progressdialog=ProgressDialog.show(About.this, null, "检测中..."); 
					new Thread(new Runnable(){
						public void run(){
							Log.v("更新", "开始");
							try {
								NetBase.Send(Conf.Api_Host(about)+"/index.php/Mao/UpdateJson?_Sm="+Conf.get(about, "sid").toString(),"version="+vers);
								handler.post(new Runnable(){
									public void run(){
										try {
											//Toast.makeText(about,NetBase.Result, Toast.LENGTH_LONG).show();
											json=new JSONObject(NetBase.Result);
											if(json.getBoolean("STS") && json.getBoolean("UPDATE")){
												new AlertDialog.Builder(about)
												.setTitle("检测更新")
												.setMessage(json.getString("MSG"))
												.setPositiveButton("马上更新", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														try {
															fd = new FileDownload(Conf.Api_Host(about)+json.getString("UPDATE_URL"),
																	"/sdcard/8eoo");
															progressdialog = new ProgressDialog(About.this);
															progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
															progressdialog.setTitle("下载中");
															progressdialog.setProgress(0);
															fd.setOnDataChangedListener(new OnDown());
															fd.exec();
															//about.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Conf.Api_Host(about)+json.getString("UPDATE_URL"))));
														} catch (JSONException e) {
															e.printStackTrace();
														}
													}
												})
												.setNegativeButton("下次再说", new DialogInterface.OnClickListener() { 
													public void onClick(DialogInterface dialog, int which) { 
													}
												})
												.show();
											}else{
												new AlertDialog.Builder(about)
												.setTitle("检测更新")
												.setMessage(json.getString("MSG"))
												.setPositiveButton("确认", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {}
												})
												.show();
											}
										} catch (JSONException e) {
											Toast.makeText(about,NetBase.Result, Toast.LENGTH_LONG).show();
										}
									}
								});
							} catch (Exception e) {
								handler.post(new Runnable(){
									public void run(){ 
										Toast.makeText(about,"检测更新发生错误", Toast.LENGTH_LONG).show();
									}
								});
							}
							progressdialog.dismiss();
						}
					}).start();*/
					break;
			}
		}
		 
	 }
	 
	 class OnDown implements OnDataChangedListener{

		public void OnStart(final long total) {
			handler.post(new Runnable(){
				public void run() {
					progressdialog.setMax((int)total);
					progressdialog.show();
				}
				
			});
		}

		public void OnProgressChanged(long total, long current) {
			progressdialog.setProgress((int)current);
		}

		public void OnDownLoadComplate(final String path, long total) {
			handler.post(new Runnable() {
				public void run() {
					try{
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
						startActivity(intent);
						progressdialog.dismiss();
					}catch (Exception e) {
						Toast.makeText(getBaseContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
		}

		public void OnError(Exception e) {
			Toast.makeText(about, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}

			 
	    	
	 }
	 
}
