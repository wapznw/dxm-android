package com.tianku.client.mao;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.db.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MaoActivity extends Activity {
    /** Called when the activity is first created. */
	private static TextView title,btn_fav,btn_share,btn_exit,btn_logout,btn_about, btn_setting,phone,txt,_id,time,status,result,jieshou,chenggong,shibai,zhiling,text_view4;
	public static ListView log;  
	private static DatabaseHelper dh;
	private Handler handler=new Handler();
	private static ProgressDialog progr;
	private RelativeLayout head;
	private ImageView animo;
	public String data;
	private Animation myAnimation_Alpha;
	//private ScrollView scrollView;  
	//private Activity mao=this; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activitys.getInstance().addActivity(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        title = (TextView)findViewById(R.id.title);
        title.setText(R.string.app_name);
           
		NetBase.context=this;
		
        //top_refresh=(ImageButton)findViewById(R.id.top_refresh);
        btn_share=(TextView)findViewById(R.id.footer_btn_share);
        btn_fav=(TextView)findViewById(R.id.footer_btn_fav);
        btn_exit=(TextView)findViewById(R.id.footer_btn_exit);
        btn_logout=(TextView)findViewById(R.id.footer_btn_retweet);
        btn_about=(TextView)findViewById(R.id.footer_btn_about);
        btn_setting=(TextView)findViewById(R.id.top_setting);
        log=(ListView)findViewById(R.id.listView1);
        head=(RelativeLayout)findViewById(R.id.relativeLayout1);
    	jieshou=(TextView)findViewById(R.id.jieshou);
    	chenggong=(TextView)findViewById(R.id.chenggong);
    	shibai=(TextView)findViewById(R.id.shibai);
    	zhiling=(TextView)findViewById(R.id.zhiling);
    	text_view4 = (TextView)findViewById(R.id.textView4);
    	
        animo=(ImageView)findViewById(R.id.animo); 
        //top_refresh.setOnClickListener(new OnMenuClick());
        title.setOnClickListener(new clickListener());
        btn_share.setOnClickListener(new clickListener());
        btn_fav.setOnClickListener(new clickListener());
        btn_about.setOnClickListener(new clickListener());
        btn_logout.setOnClickListener(new clickListener());
        btn_setting.setOnClickListener(new clickListener()); 
        zhiling.setOnClickListener(new clickListener());
        text_view4.setOnClickListener(new clickListener());
         
        myAnimation_Alpha = AnimationUtils.loadAnimation(this,R.anim.rotate360);
		animo.startAnimation(myAnimation_Alpha);

        btn_exit.setOnClickListener(new OnMenuClick()); 
        listshow(this);
        
        this.registerForContextMenu(log);
        log.setOnItemLongClickListener(new OnItemLongClickListener(){
        	
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
					long id){
				_id=(TextView)view.findViewById(R.id._id);
				phone=(TextView)view.findViewById(R.id.log_phone);
				txt=(TextView)view.findViewById(R.id.log_cont);
				time=(TextView)view.findViewById(R.id.log_time);
				status=(TextView)view.findViewById(R.id.log_status);
				result=(TextView)view.findViewById(R.id.log_result);
				return false;
			}
 
        	
        });
        log.setOnItemClickListener(new OnItemClickListener(){ 
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {      
				_id=(TextView)view.findViewById(R.id._id);
				phone=(TextView)view.findViewById(R.id.log_phone);
				txt=(TextView)view.findViewById(R.id.log_cont);
				time=(TextView)view.findViewById(R.id.log_time);
				status=(TextView)view.findViewById(R.id.log_status);
				result=(TextView)view.findViewById(R.id.log_result);
				//Toast.makeText(getBaseContext(),val.toString(), Toast.LENGTH_LONG).show();	
				String text=getString(R.string.Number)+":"+phone.getText().toString()+"\n"+getString(R.string.Content)+":"+txt.getText().toString()+"\n"
				+getString(R.string.time)+":"+time.getText().toString()+"\n"+getString(R.string.status)+":"+status.getText().toString()+"\n"
				+getString(R.string.page_label_go_back)+":"+result.getText().toString();
				new AlertDialog.Builder(MaoActivity.this).setTitle(R.string.View_status)
						.setMessage(text)
						.setPositiveButton(R.string.general_lable_ok,
								new DialogInterface.OnClickListener() { 
									public void onClick(DialogInterface dialog,
											int which) { 
										 
									}
								})/*.setNegativeButton("取消", new DialogInterface.OnClickListener(){ 
									@Override
									public void onClick(DialogInterface dialog, int which) { 
										
									}
							})*/.show();
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
    
    public static void listshow(Context context){ 
        dh=new DatabaseHelper(context);
		Cursor c=dh.select(context);   
		//Toast.makeText(context, "number:"+c.getCount(), Toast.LENGTH_LONG).show();
		jieshou.setText(c.getCount()+context.getString(R.string.Article));
		zhiling.setText(Conf.get(context, "command").toString().replace("|", ","));
        SimpleAdapter adapter = new SimpleAdapter(context,getData(c),R.layout.listlog,
				new String[]{"time","info","img","phone","id","status","result"},
				new int[]{R.id.log_time,R.id.log_cont,R.id.log_img,R.id.log_phone,R.id._id,R.id.log_status,R.id.log_result});
        log.setAdapter(adapter);
    }
    private static List<Map<String, Object>> getData(Cursor c) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		int cg=0,sb=0;
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			map = new HashMap<String, Object>();
			if(c.getInt(c.getColumnIndex("sms_status"))==1){
				map.put("img", com.tianku.client.mao.R.drawable.update_finished);
				cg++;
			}else{
				map.put("img", com.tianku.client.mao.R.drawable.update_warning);
				sb++;
			}
			String[] status_str={"","成功","失败"};
			map.put("status", status_str[c.getInt(c.getColumnIndex("sms_status"))]);
			map.put("id", c.getString(c.getColumnIndex("_id")));
			map.put("phone", c.getString(c.getColumnIndex("sms_phone")).replace("+86", ""));
			map.put("info", c.getString(c.getColumnIndex("sms_text")));
			map.put("result", c.getString(c.getColumnIndex("sms_result")));
			Date epoch,loca;
			String day="";
			loca=new Date();
			try {
				epoch = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(c.getColumnIndex("sms_time")));
			} catch (ParseException e) { 
				epoch=new Date(); 
			} 
			if(loca.getDate()-epoch.getDate()==0){
				day="今天";
			}else if(loca.getDate()-epoch.getDate()==1){
				day="昨天";
			}else if(loca.getDate()-epoch.getDate()==2){
				day="前天";
			}else if(loca.getDate()-epoch.getDate()>2){
				day=(loca.getDate()-epoch.getDate())+"天前";
			}else if(loca.getYear()==epoch.getYear()){
				day=epoch.getMonth()+"月"+epoch.getDate()+"日";
			}else{
				day=epoch.getYear()+"年"+epoch.getMonth()+"月"+epoch.getDate()+"日";
			}
			String h=epoch.getHours()+"",m=epoch.getMinutes()+"",s=""+epoch.getSeconds();
			if(h.length()==1)h="0"+h;
			if(m.length()==1)m="0"+m; 
			if(s.length()==1)s="0"+s; 
			map.put("time",day+" "+h+":"+m+":"+s);
			list.add(map); 
			c.moveToNext();
		}
		chenggong.setText(cg+"条");
		shibai.setText(sb+"条");
		c.close();
		return list;
	}
    
	class clickListener implements OnClickListener {
		public ProgressDialog progressdialog;
		public View view;
		public void onClick(View v) {
			view=v;
			switch (v.getId()) {
			// 标题按钮
			case R.id.title:
				/*Web.url=Conf.API_HOST+"/index.php/user/index?_Sm="+Conf.get(MaoActivity.this, "sid");
				startActivity(new Intent(MaoActivity.this,Web.class));*/
				if(head.getVisibility()==RelativeLayout.GONE){
					head.setVisibility(RelativeLayout.VISIBLE);
				}else{
					head.setVisibility(RelativeLayout.GONE);
				}
				break;
			//分享按钮
			case R.id.footer_btn_share:
				 /*Intent intent=new Intent(Intent.ACTION_SEND);   
			      intent.setType("text/plain");   
			      intent.putExtra(Intent.EXTRA_TITLE,R.string.cmenu_share);
			      intent.putExtra(Intent.EXTRA_SUBJECT,R.string.cmenu_share);
			      intent.putExtra(Intent.EXTRA_TEXT, "我正在使用天酷短信猫,觉得很不错,推荐你也试试.http://sms.8eoo.com");   
			      startActivity(Intent.createChooser(intent, getTitle()));   */
				  startActivity(new Intent(MaoActivity.this,LocalSms.class));
				break;
			//收藏按钮
			case R.id.footer_btn_fav:
				Web.url=Conf.Api_Host(MaoActivity.this)+"/index.php/user/index?_Sm="+Conf.get(MaoActivity.this, "sid");
				startActivity(new Intent(MaoActivity.this,Web.class));
				 break; 
			//关于按钮
			case R.id.footer_btn_about:
				startActivity(new Intent(MaoActivity.this,About.class)); 
				break;
			//注销
			case R.id.footer_btn_retweet:
				/*progressdialog=ProgressDialog.show(MaoActivity.this, null, getString(R.string.login_status_logout_in));
				new Thread(new Runnable(){
					public void run(){
						try {
							NetBase.Send(Conf.Api_Host(MaoActivity.this)+"/index.php/Mao/Logout?_Sm="+Conf.get(view.getContext(), "sid"));
							//Toast.makeText(getBaseContext(),NetBase.Result, Toast.LENGTH_LONG).show();
							if(NetBase.Result.indexOf("Logout ok")>-1){
								startActivity(new Intent(MaoActivity.this,Login.class)); 
								MaoActivity.this.finish();
							}else{
								Toast.makeText(getBaseContext(),NetBase.Result, Toast.LENGTH_LONG).show();
							}
						} catch (Exception e1) {  
							handler.post(new Runnable(){
								public void run(){
									Toast.makeText(getBaseContext(),R.string.login_status_network_or_connection_error, Toast.LENGTH_LONG).show();
								}
							});
						}
						progressdialog.dismiss();
					}
				}).start();*/
				Toast.makeText(getBaseContext(),"此功能暂未开放!", Toast.LENGTH_LONG).show();
				break;
			case R.id.top_setting: //设置
	    		startActivity(new Intent(MaoActivity.this,Setting.class)); 
				break;
			case R.id.textView4:
			case R.id.zhiling://更新指令
				progressdialog=ProgressDialog.show(MaoActivity.this, null, getString(R.string.Update_instructions));
				new Thread(new Runnable(){
					public void run(){
						try {
							NetBase.Send(Conf.Api_Host(MaoActivity.this)+"/index.php/Mao/getCommandJson?_Sm="+Conf.get(view.getContext(), "sid"));
							JSONObject json=new JSONObject(NetBase.Result);
							if(json.getBoolean("STS")){
								Conf.set(view.getContext(), "command",json.getString("MSG"));
								handler.post(new Runnable(){
									public void run(){
										zhiling.setText(Conf.get(MaoActivity.this, "command").toString().replace("|", ","));
										Toast.makeText(getBaseContext(),getString(R.string.Update_command_is_successful)+"\n"+ Conf.get(view.getContext(), "command").toString().trim(), Toast.LENGTH_LONG).show();
									}
								});	
							}else{
								handler.post(new Runnable(){
									public void run(){
										JSONObject json;
										try {
											json = new JSONObject(NetBase.Result);
											Conf.set(view.getContext(), "command",json.getString("MSG"));
											zhiling.setText(Conf.get(MaoActivity.this, "command").toString().replace("|", ","));
											Toast.makeText(getBaseContext(),getString(R.string.Update_command_fails)+"\n"+ json.getString("MSG"), Toast.LENGTH_LONG).show();
											
										} catch (JSONException e) {
											try {
												Toast.makeText(getBaseContext(),NetBase.Result, Toast.LENGTH_LONG).show();
											} catch (Exception e9) {
												e9.printStackTrace();
											}
										}
										}
								});
							}			
						} catch (Exception e1) { 
							handler.post(new Runnable(){
								public void run(){
									Toast.makeText(getBaseContext(), getString(R.string.Update_command_fails), Toast.LENGTH_LONG).show();
									}
								});	
						}
						progressdialog.dismiss();
					}
				}).start();
				break;
			}
		}

	}
	  
	 public boolean onKeyDown(int keyCode,KeyEvent event){
		 if(keyCode==KeyEvent.KEYCODE_BACK){ 
			    Intent intent = new Intent();         
			    intent.setAction("android.intent.action.MAIN");  
			    intent.addCategory("android.intent.category.HOME");  
			    this.startActivity(intent);  
			 return true;
		 }else{
			 return false;
		 }
	 }
	 
	 private class HelloWebViewClient extends WebViewClient { 
	       @Override
	       public boolean shouldOverrideUrlLoading(WebView view, String url) { 
	           view.loadUrl(url); 
	           return true; 
	       } 
	       
	       public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    	     //Toast.makeText(act, "Oh no! " + description, Toast.LENGTH_SHORT).show();
	    	}
	       
	       public void onPageFinished(WebView view, String url){
	    	   if(view.getTitle()!="")title.setText(view.getTitle()); 
	       }
	   }
	 
	//添加上下文菜单   
	    @Override   
	    public void onCreateContextMenu(ContextMenu menu, View v,   
	            ContextMenuInfo menuInfo) {   
	        //此方法在每次调用上下文菜单时都会被调用一次   
	        //menu.setHeaderIcon(R.menu.car);    
	    	//Toast.makeText(getBaseContext(), ""+status.getText().toString(), Toast.LENGTH_LONG).show();
	        if (!status.getText().toString().equals("成功")) {   
	            menu.add(0, 1, 1,"重新发送");   
	            menu.add(0, 2, 2,"设为成功");    
	        }   else {   
	            menu.add(0, 3, 3,"设为失败");
	        }   
            menu.add(0, 4, 5,"删除本条");
            menu.add(0, 5, 6,"全部删除");  
            menu.add(0, 6, 7,"取消操作");
	        //menu.add(0, 7, 4,"编辑本条");
	    }   
	     
	    //响应上下文菜单   
	    @Override   
	    public boolean onContextItemSelected(MenuItem item) {    
	        switch (item.getItemId()) {   
	        case 1:
				progr=ProgressDialog.show(MaoActivity.this, null, "发送中...");
	        	new Thread(new Runnable(){
	        		public void run(){
	        			try {
	        				data="phone="+phone.getText().toString()+"&message="+txt.getText().toString();
	        				data=SmsSend.P(MaoActivity.this,data);
							dh=new DatabaseHelper(MaoActivity.this);
							handler.post(new Runnable(){
								public void run(){
									try {
										JSONObject json=new JSONObject(data);
										//data.equals("注册成功") || data.equals("设置密码成功")
										if(json.getBoolean("STS")){
											dh.update(MaoActivity.this, "sms_status=1,sms_result="+json.getString("MSG"), _id.getText().toString());
											listshow(MaoActivity.this);
										}else{
											dh.update(MaoActivity.this, "sms_result="+json.getString("MSG"), _id.getText().toString());
											listshow(MaoActivity.this);
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
	        	break;
	        case 2:
	        	dh=new DatabaseHelper(MaoActivity.this);
	        	dh.update(this, "sms_status=1", _id.getText().toString());
	        	listshow(this);
	        	break;
	        case 3:      
	        	dh=new DatabaseHelper(MaoActivity.this);
	        	dh.update(this, "sms_status=2", _id.getText().toString());
	        	listshow(this);
	            break;   
	        case 4:   
	        	dh=new DatabaseHelper(MaoActivity.this);
	        	dh.delete(this, _id.getText().toString());
	        	listshow(this);
	        	break;
	        case 5:       //
	        	new AlertDialog.Builder(this).setMessage("确定要删除所有记录?")
	        	.setPositiveButton(R.string.general_lable_ok,new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog,
							int which) { 
						dh=new DatabaseHelper(MaoActivity.this);
			        	dh.delete(MaoActivity.this);
			        	MaoActivity.listshow(MaoActivity.this);
					}
				}).setNegativeButton(R.string.general_lable_cancel,new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog,
							int which) {  
					}
				}).show();
	            break; 
	        case 6:   
	        	break;
	        	
	        case 7:
	        	WebView web=new WebView(this);
	        	web.loadUrl(Conf.Api_Host(this)+"/index.php/index/index?_Sm="+Conf.get(MaoActivity.this, "sid"));
	        	web.setWebViewClient(new HelloWebViewClient());
	        	new AlertDialog.Builder(MaoActivity.this).setTitle("编辑")
	        	.setView(web)
	        	.show();
	        	break;
	        }   
	        return true;   
	    }   
}