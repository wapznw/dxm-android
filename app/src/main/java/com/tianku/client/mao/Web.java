package com.tianku.client.mao;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import java.net.URLDecoder;

public class Web extends Activity{
	private TextView title; 
	private ImageButton top_refresh,top_back,top_forward;
	private LinearLayout LinearLayout1;
	public static WebView web;
	public static String url="http://sms.8eoo.com";
	public static ProgressBar progress_bar,refresh_progressBar; 
	final Activity act=this;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activitys.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.web);
        title = (TextView)findViewById(R.id.title);
        title.setText(R.string.app_name);
        top_refresh=(ImageButton)findViewById(R.id.top_refresh);
        top_back=(ImageButton)findViewById(R.id.writeMessage);
        top_forward=(ImageButton)findViewById(R.id.search);
        progress_bar=(ProgressBar)findViewById(R.id.progress_bar);
        refresh_progressBar=(ProgressBar)findViewById(R.id.top_refresh_progressBar);

	    progress_bar.setMax(100);
        top_refresh.setOnClickListener(new clickListener());
        top_back.setOnClickListener(new clickListener());
        top_forward.setOnClickListener(new clickListener());
        title.setOnClickListener(new clickListener());
        
        LinearLayout1 = (LinearLayout)findViewById(R.id.linearLayout1);
        web=new WebView(this);
        web.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        WebSettings webSet = web.getSettings();  
		webSet.setJavaScriptEnabled(true); 
		//webSet.setPluginsEnabled(true);
		webSet.setUserAgentString("TK_SMSMAO");
        web.setWebViewClient(new HelloWebViewClient());
        web.setWebChromeClient(new helloWebChromeClient());
        web.setScrollBarStyle(0);
        web.loadUrl(url);  
        LinearLayout1.addView(web);
        
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
		menu.add(Menu.NONE, Menu.FIRST+10, 1,R.string.omenu_settings).setIcon(
				android.R.drawable.ic_menu_manage);
		menu.add(Menu.NONE,Menu.FIRST+11,2,R.string.omenu_exit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST+10:
			startActivity(new Intent(Web.this,MaoActivity.class));
			break;
		case Menu.FIRST+11:
			Activitys.getInstance().exit();  
			break;
		}
		return false;
	}
		
	 //Web视图 
	   private class HelloWebViewClient extends WebViewClient { 
	       @Override
	       public boolean shouldOverrideUrlLoading(WebView view, String url) { 
	    	  
    		   Intent intent;
    		   if(url.startsWith("wtai:")){
	    		   intent = new Intent(Intent.ACTION_CALL,Uri.parse(url.replace("wtai://wp/mc;", "tel:")));
    		   }else if(url.startsWith("sms:")){
    			   try{
    				   	String par[] = url.split("\\?");
						String uri = par[0];
						String body = URLDecoder.decode(par[1].split("=")[1]);
						intent = new Intent(Intent.ACTION_SENDTO,Uri.parse(uri));
						intent.putExtra("sms_body", body);
    			   }catch(Exception e){
    				   Toast.makeText(getBaseContext(), url+"\n"+e.getMessage(), Toast.LENGTH_LONG).show();
    				   intent =null;
    			   }
    		   }else{
    			   intent = null;
    			   view.loadUrl(url);
    		   }
    		   if(intent!=null)startActivity(intent);
	            
	           return true; 
	       } 
	       
	       public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    	     Toast.makeText(act, "Error:" + description, Toast.LENGTH_SHORT).show();
	    	}
	       
	       public void onPageFinished(WebView view, String url){
	    	   if(view.getTitle()!="")title.setText(view.getTitle()); 
	       }
	   }
	   
	   private class helloWebChromeClient extends WebChromeClient{
		   //进度改变
		   public void onProgressChanged(WebView view, int progress) { 
			   top_refresh.setVisibility(TextView.GONE);
			   refresh_progressBar.setVisibility(ProgressBar.VISIBLE);
			   progress_bar.setProgress(progress);
			   if(progress==100){
				   refresh_progressBar.setVisibility(ProgressBar.GONE);
		    	   top_refresh.setVisibility(TextView.VISIBLE);
			   }
			   }
	   }
	   
	class clickListener implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			// 标题按钮
			case R.id.title:
				v.getContext().startActivity(
						new Intent(v.getContext(), MaoActivity.class));
				break;
			// 刷新按钮
			case R.id.top_refresh:
				Web.web.reload();
				Toast.makeText(v.getContext(), R.string.Refresh_in, Toast.LENGTH_LONG).show();
				break;
			// 后退
			case R.id.writeMessage:
				if(web.canGoBack()){
					Web.web.goBack();
				}else{
					Toast.makeText(getBaseContext(), "已到最后,不能再后退了!", Toast.LENGTH_LONG).show();
				}
				break;
			// 前进
			case R.id.search:
				if(web.canGoForward()){
					Web.web.goForward();
				}else{
					Toast.makeText(getBaseContext(), "没有访问历史!", Toast.LENGTH_LONG).show();
				}
				break;
			}
		}

	}
	/**
	 * 按下某键
	 */
	 public boolean onKeyDown(int keyCode,KeyEvent event){
		 if(keyCode==KeyEvent.KEYCODE_BACK){  
			   if(web.canGoBack()){
				   web.goBack();
			   }else{
				   this.finish();
				   super.onKeyDown(keyCode, event);
			   }
			 return true;
		 }else if(keyCode==KeyEvent.KEYCODE_MENU){
			 super.onKeyDown(keyCode, event);
			 return true;
		 }else{
		    return false;
		 } 
	 }
	 /**
	  * 连续按键
	  */
	 public boolean onKeyMultiple(int keyCode,int repeatCount, KeyEvent event){
		 if(keyCode==KeyEvent.KEYCODE_BACK && repeatCount==2){
			 Toast.makeText(getBaseContext(), "返回程序", Toast.LENGTH_LONG).show();
			 super.onKeyDown(keyCode, event);
		 }else{
			 return false;
		 }
		 return true;
	 }	
}
