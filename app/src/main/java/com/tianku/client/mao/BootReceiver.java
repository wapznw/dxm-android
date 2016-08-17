package com.tianku.client.mao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
	private boolean bootrun=false;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub 
		try{
			bootrun=(Boolean)Conf.get(context, "boot_run"); 
		}catch(Exception e){ 
			Conf.set(context, "boot_run", bootrun);
		}
		
		if(bootrun){
			Intent i=new Intent(context,Start.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			Conf.set(context, "autoboot", true);
		}else{
			System.exit(0);
		}
	}

}
