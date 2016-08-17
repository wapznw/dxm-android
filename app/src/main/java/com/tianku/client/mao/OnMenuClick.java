package com.tianku.client.mao;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
class OnMenuClick implements OnClickListener{ 
	public static View view;
	public void onClick(View v) {  
		view=v;
		switch(v.getId()){
			//退出程序
			case R.id.footer_btn_exit:
				new AlertDialog.Builder(v.getContext()).setTitle(R.string.exit_info)
				.setMessage(R.string.exit_canm)
				.setPositiveButton(R.string.background_run,new DialogInterface.OnClickListener(){ 
					public void onClick(DialogInterface dialog, int which) {  
						//Intent intent = new Intent();         
					  //  intent.setAction("android.intent.action.MAIN");  
					  //  intent.addCategory("android.intent.category.HOME");  
					  //  view.getContext().startActivity(intent);  
						try {
							//view.getContext().startService(new Intent(view.getContext(),MaoService.class));
							Activitys.getInstance().exit();
						} catch (Exception e) { 
							e.printStackTrace();
						}
					} 
				}).setNegativeButton(R.string.all_exit,new DialogInterface.OnClickListener(){ 
					public void onClick(DialogInterface dialog, int which) {  
						try {
							NotificationManager notificationManager = (NotificationManager)view.getContext()
		                    .getSystemService(MaoService.NOTIFICATION_SERVICE); 
							notificationManager.cancel(MaoService.ID);
							//MaoService.nm.cancel(MaoService.ID);
							//Log.v("停止",MaoService.ID+"");
						} catch (Exception e) {
							//Log.v("停止", e.toString());
							//Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_LONG).show();
						}
						Intent i  = new Intent();  
			            i.setClass(view.getContext(), MaoService.class);  
						if(view.getContext().stopService(i))
						Activitys.getInstance().exit();  
						else{
							view.getContext().stopService(i);
							Activitys.getInstance().exit();  
						}
					}
					
				}).show();
			break;
		}
	}
	
} 