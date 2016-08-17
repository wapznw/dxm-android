package com.tianku.client.mao;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class Activitys extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static Activitys instance;

	private Activitys() {
	}

	// 单例模式中获取唯一的MyApplication实例
	public static Activitys getInstance() {
		if (null == instance) {
			instance = new Activitys();
		}
		return instance;

	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish

	public void exit() {

		for (Activity activity : activityList) {
			activity.finish();
		} 
		System.exit(0); 
	}
	
	public void exit(boolean bool) {

		for (Activity activity : activityList) {
			activity.finish();
		} 
		if(bool)System.exit(0);
	}
}