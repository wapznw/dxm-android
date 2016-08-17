package com.tianku.client.mao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import org.json.JSONException;
import org.json.JSONObject;

public class Start extends Activity {
    private String sid, user = null, pass = null;
    private static final String API_HOST = Conf.API_HOST;
    private boolean autologin = false;
    private TextView loading;
    private Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activitys.getInstance().addActivity(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        NetBase.context = this;

        //判断是否自动启动
        try {
            boolean autoboot = (Boolean) Conf.get(this, "autoboot");
            if (autoboot) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                startActivity(intent);
            }
            Conf.set(this, "autoboot", false);
        } catch (Exception e) {
            Conf.set(this, "autoboot", false);
        }
        //end

        setContentView(R.layout.start);
        this.init();

        //设置AppKey与AppChannel
        //StatService.setAppKey("c413a7e812");
        //StatService.setAppChannel("Baidu Market");
        //进行错误分析
        //setOn函数与manifest.xml中的配置BaiduMobAd_EXCEPTION_LOG是等效的，推荐使用配置文件。
        //StatService.setOn(this,StatService.EXCEPTION_LOG);
        //设置发送延迟
        //StatService.setLogSenderDelayed(10);

        //设置发送策略
        //setSendLogStrategy函数与配置文件中的BaiduMobAd_SEND_STRATEGY等是等效的，推荐使用配置文件。
        //StatService.setSendLogStrategy(this,SendStrategyEnum.APP_START, 1,false);

        //if(autologin && user!=null && pass!=null){
        new Thread(new Runnable() {
            public void run() {
                loading.setTextColor(Color.GREEN);
                try {
                    NetBase.Send(Conf.Api_Host(Start.this) + "/index.php/Mao/getCommandJson?_Sm=" + sid, "");
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                JSONObject json = new JSONObject(NetBase.Result);
                                if (json.getBoolean("STS")) {
                                    Conf.set(Start.this, "command", json.getString("MSG"));
                                    Toast.makeText(Start.this, json.getString("MSG"), Toast.LENGTH_LONG).show();
                                    startService(new Intent(Start.this, MaoService.class));
                                    startActivity(new Intent(Start.this, MaoActivity.class));
                                    Start.this.finish();
                                } else {
                                    loading.setTextColor(Color.RED);
                                    loading.setText(json.getString("MSG"));
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        loading.setText(e.toString());
                                    }
                                    Conf.set(Start.this, "command", "");
                                    startService(new Intent(Start.this, MaoService.class));
                                    startActivity(new Intent(Start.this, MaoActivity.class));
                                    finish();
                                }
                            } catch (JSONException e2) {
                                Toast.makeText(Start.this, e2.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } catch (Exception e) {
                    new Handler().post(new Runnable() {
                        public void run() {
                            loading.setTextColor(Color.RED);
                            loading.setText(R.string.login_status_network_or_connection_error);
                        }
                    });
                }
            }
        }).start();
        /*}else{
			handler.postDelayed(new Runnable(){
				public void run(){
					startActivity(new Intent(Start.this,Login.class));
					finish();
				}
			}, 500);
		}*/

    }

    //初始化。。。
    public void init() {
        loading = (TextView) findViewById(R.id.loading);
        try {
            sid = Conf.get(this, "sid").toString().trim();
        } catch (Exception e) {
            Conf.set(this, "sid", comm.MD5(comm.getCode(32) + comm.time()));
            sid = Conf.get(this, "sid").toString().trim();
        }

        if (sid == "" || sid.length() == 0) {
            Conf.set(this, "sid", comm.MD5(comm.getCode(32) + comm.time()));
            sid = Conf.get(this, "sid").toString().trim();
        }

        try {
            user = Conf.get(this, "username").toString().trim();
            pass = Conf.get(this, "passwrods").toString().trim();
        } catch (Exception e) {
            Conf.set(this, "autologin", false);
            autologin = (Boolean) Conf.get(this, "autologin");
        }

        try {
            autologin = (Boolean) Conf.get(this, "autologin");
        } catch (Exception e) {
            Conf.set(this, "autologin", false);
            autologin = (Boolean) Conf.get(this, "autologin");
        }
        try {
            Conf.get(this, "sms_model");
        } catch (Exception e) {
            Conf.set(this, "sms_model", true);
        }
        try {
            Conf.get(this, "sms_setread");
        } catch (Exception e) {
            Conf.set(this, "sms_setread", "1");
        }
        try {
            Conf.get(this, "sms_othernotice");
        } catch (Exception e) {
            Conf.set(this, "sms_othernotice", true);
        }
        try {
            Conf.get(this, "boot_run");
        } catch (Exception e) {
            Conf.set(this, "boot_run", true);
        }
        try {
            Conf.get(this, "command");
        } catch (Exception e) {
            Conf.set(this, "command", "");
        }

        try {
            String api_url = Conf.get(this, "api_url").toString();
            if (api_url.equals("") || api_url.length() == 0) {
                Conf.set(this, "api_url", API_HOST);
            }

            String apis[] = getResources().getStringArray(R.array.entries_val);
            boolean is_apis = false;
            for (int j = 0; j < apis.length; j++) {
                if (apis[j].equals(api_url)) {
                    is_apis = true;
                    break;
                }
            }
            if (!is_apis) {
                Conf.set(this, "api_url", API_HOST);
            }
        } catch (Exception e) {
            Conf.set(this, "api_url", API_HOST);
        }

        try {
            String api_url = Conf.get(this, "REVICE_SIZE").toString();
            if (api_url.equals("") || api_url.length() == 0) {
                Conf.set(this, "REVICE_SIZE", 0);
            }
        } catch (Exception e) {
            Conf.set(this, "REVICE_SIZE", 0);
        }

        try {
            String api_url = Conf.get(this, "SEND_SIZE").toString();
            if (api_url.equals("") || api_url.length() == 0) {
                Conf.set(this, "SEND_SIZE", 0);
            }
        } catch (Exception e) {
            Conf.set(this, "SEND_SIZE", 0);
        }

        try {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            Conf.set(this, "imei", tm.getSimSerialNumber() + "");
        } catch (Exception e) {
            Conf.set(this, "imei", "Null");
        }
        try {
            Conf.get(this, "CALL_STATUS").toString();
        } catch (Exception e) {
            Conf.set(this, "CALL_STATUS", false);
        }
        try {
            Conf.get(this, "AUTO_CUT").toString();
        } catch (Exception e) {
            Conf.set(this, "AUTO_CUT", false);
        }
        try {
            Conf.get(this, "Filter_Book").toString();
        } catch (Exception e) {
            Conf.set(this, "Filter_Book", false);
        }
        try {
            Conf.get(this, "sms_filter").toString();
        } catch (Exception e) {
            Conf.set(this, "sms_filter", false);
        }
        try {
            Conf.get(this, "REG_RECOVERY").toString();
        } catch (Exception e) {
            Conf.set(this, "REG_RECOVERY", false);
        }
        try {
            Conf.get(this, "AUTO_CHECK_UPDATE").toString();
        } catch (Exception e) {
            Conf.set(this, "AUTO_CHECK_UPDATE", true);
        }

        try {
            String REG_RECOVERY_STR = Conf.get(this, "REG_RECOVERY_STR").toString();
            if (REG_RECOVERY_STR.equals("") || REG_RECOVERY_STR.length() == 0) {
                Conf.set(this, "REG_RECOVERY_STR", "欢迎注册为天酷网络会员!\n账号:{$phone}\n密码:{$password}\n天酷网络:http://sms.8eoo.com");
            }
        } catch (Exception e) {
            Conf.set(this, "REG_RECOVERY_STR", "欢迎注册为天酷网络会员!\n账号:{$phone}\n密码:{$password}\n天酷网络:http://sms.8eoo.com");
        }
        try {
            Conf.get(this, "PASS_RECOVERY");
        } catch (Exception e) {
            Conf.set(this, "PASS_RECOVERY", false);
        }
        try {
            String PASS_RECOVERY_STR = Conf.get(this, "PASS_RECOVERY_STR").toString();
            if (PASS_RECOVERY_STR.equals("") || PASS_RECOVERY_STR.length() == 0) {
                Conf.set(this, "PASS_RECOVERY_STR", "你已成功修改密码!\n账号:{$phone}\n密码:{$password}\n天酷网络:http://sms.8eoo.com");
            }
        } catch (Exception e) {
            Conf.set(this, "PASS_RECOVERY_STR", "你已成功修改密码!\n账号:{$phone}\n密码:{$password}\n天酷网络:http://sms.8eoo.com");
        }
        NetBase.PHONE_IMEI = Conf.get(this, "imei").toString();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST, 1, "设置").setIcon(android.R.drawable.ic_menu_manage);
        menu.add(Menu.NONE, Menu.FIRST + 1, 2, "退出").setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                startActivity(new Intent(Start.this, Setting.class));
                break;
            case Menu.FIRST + 1:
                Activitys.getInstance().exit();
                break;
        }
        return false;
    }


    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
}
