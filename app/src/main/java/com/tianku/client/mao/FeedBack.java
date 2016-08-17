package com.tianku.client.mao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatService;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedBack extends Activity {
	private TextView back,send,num;
	private EditText cont;
	private AutoCompleteTextView mail;
	private Activity feed=this;
	private String data,JiXing,BanBen,IMEI,VERSION;
	private Handler handler=new Handler();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Activitys.getInstance().addActivity(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback);
		
		back=(TextView)findViewById(R.id.top_back);
		send=(TextView)findViewById(R.id.top_send_btn);
		num=(TextView)findViewById(R.id.chars_text);
		mail=(AutoCompleteTextView)findViewById(R.id.to_edit);
		cont=(EditText)findViewById(R.id.tweet_edit);
		
		back.setOnClickListener(new click());
		send.setOnClickListener(new click());
		
		NetBase.context=this;
		
		try {
			JiXing=android.os.Build.MODEL;
			BanBen=android.os.Build.VERSION.RELEASE;
			VERSION=getPackageManager().getPackageInfo(getPackageName(),0).versionName;
			IMEI=(String)Conf.get(feed, "imei");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		cont.addTextChangedListener(new TextWatcher(){
			public void onTextChanged(CharSequence s, int start, int before, int count) { 
				num.setText((1024-cont.getText().length())+"");
			}
			public void afterTextChanged(Editable arg0) { 
				
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) { 
			}
			
		});
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

	class click implements OnClickListener{ 
		public ProgressDialog progressdialog;
		public void onClick(View v) {
			 switch(v.getId()){
			 case R.id.top_back:
				 feed.finish();
				 break;
			 case R.id.top_send_btn:
				 String email,content;
				 email=mail.getText().toString();
				 content=cont.getText().toString();
				 if(email.indexOf("@")==-1 || email.indexOf(".")==-1){
					 ShowMsg("邮件格式不正确");
				 }else if(content.length()<5){
					 ShowMsg("抱歉\n反馈内容不能少于5字符");
				 }else{
					 progressdialog = ProgressDialog.show(feed, null, "提交反馈信息中...");
					data="mail="+email+"&bugcontent="+content+"&Mobile="+JiXing+"&MobileVer="+BanBen+"&Version="+VERSION+"&IMEI="+IMEI;
					new Thread(new Runnable(){
						public void run(){
							 try {
								NetBase.Send(Conf.Api_Host(feed)+"/index.php/Mao/BugJson?_Sm="+Conf.get(feed, "sid").toString(), data);
								 handler.post(new Runnable(){
									 public void run(){
										try {
											JSONObject json=new JSONObject(NetBase.Result);
											if(json.getBoolean("STS")){
												 new AlertDialog.Builder(feed).setTitle("反馈提示").setPositiveButton(R.string.general_lable_ok, new DialogInterface.OnClickListener(){
														public void onClick(DialogInterface dialog, int which) {
															 feed.finish();
														}
													}).setMessage(json.getString("MSG")).show();
												
											}else{
												 ShowMsg(json.getString("MSG"));
											}
										} catch (JSONException e) {
											ShowMsg(NetBase.Result);
										}
									 }
								 }); 
							/*	if(NetBase.Result.indexOf("")>-1){
									 handler.post(new Runnable(){
										 public void run(){
											 new AlertDialog.Builder(feed).setTitle("反馈提示").setPositiveButton(R.string.general_lable_ok, new DialogInterface.OnClickListener(){
													public void onClick(DialogInterface dialog, int which) {
														 feed.finish();
													}
												}).setMessage(NetBase.Result).show();
										 }
									 });
								 }else{
									 handler.post(new Runnable(){
										 public void run(){
											
										 }
									 });
								 }*/
							} catch (Exception e) { 
								ShowMsg("反馈时发生错误\n"+e.getMessage());
							}
							progressdialog.dismiss();
						}
					}).start();
				 }
				 
				 break;
			 }
		} 
	}
	
	public void ShowMsg(String str){
		new AlertDialog.Builder(this).setTitle("反馈提示").setPositiveButton(R.string.general_lable_ok, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				  
			}
		}).setMessage(str).show();
	}
}