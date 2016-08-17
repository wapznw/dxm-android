package com.tianku.client.mao;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

public class Setting  extends PreferenceActivity implements OnPreferenceChangeListener{
	 /** Called when the activity is first created. */  
	EditTextPreference username, passwrod, smscommand, apiurl, smskey;
	CheckBoxPreference autologin,sms_model;
	ListPreference sms_setread;
	//ListView listview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        Activitys.getInstance().addActivity(this);
        addPreferencesFromResource(R.xml.setting);
        
		
       // listview  = getListView();
        //listview.setBackgroundColor(Color.WHITE);
        //listview.setDrawingCacheBackgroundColor(Color.WHITE);
        //listview.setCacheColorHint(Color.WHITE);
        this.setTitleColor(Color.WHITE);
        sms_model=(CheckBoxPreference)findPreference("sms_model"); 
        sms_setread=(ListPreference)findPreference("sms_setread"); 
        sms_model.setOnPreferenceChangeListener(this);
        sms_setread.setOnPreferenceChangeListener(this);
      
        try {
			if(sms_setread.getValue().equals("1")){
				sms_setread.setSummary("标记为已读");
			}else if(sms_setread.getValue().equals("2")){
				sms_setread.setSummary("直接删除");
			}else{
				sms_setread.setSummary("不进行处理");
			}
		} catch (Exception e) { 
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
    }

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub  
		//Toast.makeText(getBaseContext(), key, Toast.LENGTH_LONG).show();
		/*if(key.equals("username")){
			if(newValue.toString().length()>20){
				new AlertDialog.Builder(this).setTitle("设置失败").setMessage("设置失败!用户名不能大于20字符").setPositiveButton("确定",
						new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub 
					}
				}).show();
				return false;
			}
			username.setSummary(newValue.toString());
		}else if(key.equals("passwrods")){
			if(newValue.toString().length()>255){
				new AlertDialog.Builder(this).setTitle("设置失败").setMessage("设置失败!密码不能大于255字符").setPositiveButton("确定",
						new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub 
					}
				}).show();
				return false;
			}
			passwrod.setSummary(newValue.toString());
		}else if(key.equals("smscommand")){
			if(newValue.toString().length()>10){
				new AlertDialog.Builder(this).setTitle("设置失败").setMessage("设置失败!指令不能大于10字符").setPositiveButton("确定",
						new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub 
					}
				}).show();
				return false;
			}
			smscommand.setSummary(newValue.toString());
		}else if(key.equals("apiurl")){
			apiurl.setSummary(newValue.toString());
		}else if(key.equals("smskey")){
			if(newValue.toString().length()>255){
				new AlertDialog.Builder(this).setTitle("设置失败").setMessage("设置失败!通信密令不能大于255字符").setPositiveButton("确定",
						new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub 
					}
				}).show();
				return false;
			}
			smskey.setSummary(newValue.toString());
		}else */ 
		if(preference==sms_model){ 
			if((Boolean)newValue==false){
				Conf.set(this, "sms_setread", "1");
				sms_setread.setSummary("标记为已读");
			}
		}else if(preference==sms_setread){
			
			if(newValue.equals("1")){
				sms_setread.setSummary("标记为已读");
			}else if(newValue.equals("2")){
				sms_setread.setSummary("直接删除");
			}else{
				if((Boolean) Conf.get(this, "sms_model")==false){
					Toast.makeText(getBaseContext(),"设置失败!监听数据库模式不能设置成不处理!", Toast.LENGTH_LONG).show();
					return false;
				}else{
					sms_setread.setSummary("不处理");
				}
			}
		}else{
			return false;
		}
		return true;
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
}
