package com.tianku.client.mao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity{
	private Button signin_button;
	public static EditText username,passwrod; 
	public static TextView progress,reglink;
	private CheckBox box_pass,box_auto;
	private String user=null,pass=null;
	private Handler handler=new Handler();
	private boolean autologin=false;
	private boolean savepass=false;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 Activitys.getInstance().addActivity(this);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 setContentView(R.layout.login);
		 

		 NetBase.context=this;
		 
		 username=(EditText)findViewById(R.id.username_edit);
		 passwrod=(EditText)findViewById(R.id.password_edit); 
		 progress=(TextView)findViewById(R.id.progress_text);
		 reglink=(TextView)findViewById(R.id.register_link);
		 box_pass=(CheckBox)findViewById(R.id.checkBox1);
		 box_auto=(CheckBox)findViewById(R.id.checkBox2);
		 
		 try{
				user=Conf.get(this, "username").toString().trim();
				pass=Conf.get(this, "passwrods").toString().trim();
				if(user!="" && pass!=""){ 
					username.setText(user);
					passwrod.setText(pass);
				}
			}catch(Exception e){}
		
		 signin_button = (Button)findViewById(R.id.signin_button); 
		 signin_button.setOnClickListener(new clickListener());
		 reglink.setOnClickListener(new clickListener());
		 

		 
		try{
			autologin=(Boolean)Conf.get(this, "autologin");
			savepass=(Boolean)Conf.get(this, "savepass");
		}catch(Exception e){}

		box_pass.setChecked(savepass);
		box_pass.setOnCheckedChangeListener(new OnCheckedChangeListener(){ 
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) { 
				Conf.set(Login.this, "savepass", isChecked);
				savepass=(Boolean)Conf.get(Login.this, "savepass");
				if(!savepass){
					Conf.set(Login.this, "autologin", false);
					box_auto.setChecked(false);
				}
			}
			
		});
		
		box_auto.setChecked(autologin); 
		box_auto.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton btn, boolean val) {
				if(!savepass){
					Conf.set(Login.this, "autologin", false);
					btn.setChecked(false);
					if(val){
						Toast.makeText(getBaseContext(),R.string.Check_the_Save_Password_Auto_Login, Toast.LENGTH_LONG).show();
					}
				}else{ 
					Conf.set(Login.this, "autologin", val);
					autologin=(Boolean)Conf.get(Login.this, "autologin");
				}
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
	 
	public boolean onCreateOptionsMenu(Menu menu) {
		//清屏
		menu.add(Menu.NONE, Menu.FIRST, 1,R.string.omenu_settings).setIcon(
				android.R.drawable.ic_menu_manage);
		menu.add(Menu.NONE,Menu.FIRST+1,2,R.string.omenu_exit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			startActivity(new Intent(Login.this,Setting.class));
			break;
		case Menu.FIRST+1:
			Activitys.getInstance().exit();  
			break;
		}
		return false;
	}
	
	 public boolean onKeyDown(int keyCode,KeyEvent event){
		 if(keyCode==KeyEvent.KEYCODE_BACK){ 
			 Activitys.getInstance().exit();
			 return true;
		 }else{
			 return false;
		 }
	 }
	 
	class clickListener implements OnClickListener {
		public ProgressDialog progressdialog;
		public void onClick(View v) {
			switch (v.getId()) {
			//登陆按钮
			case R.id.signin_button: 
				user=Login.username.getText().toString().trim();
				pass=Login.passwrod.getText().toString().trim();
				if(user.length()>0 && pass.length()>0){ 
				progressdialog=ProgressDialog.show(Login.this, null,getString(R.string.login_status_logging_in));
					new Thread(new Runnable(){ 
						public void run() {
							try{
								String data="username="+user+"&password="+pass;
								NetBase.Send(Conf.Api_Host(Login.this)+"/index.php/Mao/LoginJson?_Sm="+Conf.get(Login.this, "sid"),data);
								handler.post(new Runnable(){ 
									public void run() {  
										try {
											JSONObject json=new JSONObject(NetBase.Result);
											if(json.getBoolean("STS")){
												//保存账号密码
												if(savepass){
													Conf.set(Login.this, "username", user);
													Conf.set(Login.this, "passwrods", pass);
												}else{
													Conf.set(Login.this, "username", null);
													Conf.set(Login.this, "passwrods", null);
												}
												Conf.set(Login.this, "command",json.getString("COM"));
												Toast.makeText(Login.this, json.getString("COM"), Toast.LENGTH_LONG).show();
												startActivity(new Intent(Login.this,MaoActivity.class));
												startService(new Intent(Login.this,MaoService.class));
												finish();
											}
											StatService.onEvent(Login.this, "user_login_app",  user+" "+json.getString("MSG"), 1);
											progress.setText(json.getString("MSG"));
										} catch (JSONException e2) {
											Toast.makeText(getBaseContext(),NetBase.Result, Toast.LENGTH_LONG).show();
										}
										
										/*if(NetBase.Result.equals("loginSuccess")){
											progress.setText(R.string.login_status_ok);
											try {
												//保存账号密码
												if(savepass){
													Conf.set(Login.this, "username", user);
													Conf.set(Login.this, "passwrods", pass);
												}else{
													Conf.set(Login.this, "username", null);
													Conf.set(Login.this, "passwrods", null);
												}
												try {
													NetBase.Send(Conf.Api_Host(Login.this)+"/index.php/Mao/getCommand?_Sm="+Conf.get(Login.this, "sid"));
													Conf.set(Login.this, "command",NetBase.Result);
													Toast.makeText(getBaseContext(), Conf.get(Login.this, "command").toString().trim(), Toast.LENGTH_LONG).show();
												} catch (Exception e1) { 
													Conf.set(Login.this, "command","");
													e1.printStackTrace();
												}
												startActivity(new Intent(Login.this,MaoActivity.class));
												startService(new Intent(Login.this,MaoService.class));
												finish();
											} catch (Exception e) { 
												Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
											}
										}else{
											progress.setText(NetBase.Result);
										}*/
									} 
								}); 
								progressdialog.dismiss();	 
							}catch(Exception e){ 
								handler.post(new Runnable(){ 
									public void run() {
										progress.setText(R.string.login_status_network_or_connection_error);				 		
									} 
								}); 
								progressdialog.dismiss();
								}
						}
						
					}).start();
				}else{
					progress.setText(R.string.login_status_null_username_or_password);
				}
				break;
			//注册连接
			case R.id.register_link:
				Web.url=Conf.Api_Host(Login.this)+"/index.php/Register/index?_Sm="+Conf.get(Login.this, "sid");
				startActivity(new Intent(Login.this,Web.class));
				break;
			}
		}

	}
} 