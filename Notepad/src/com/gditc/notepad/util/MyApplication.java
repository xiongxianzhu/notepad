package com.gditc.notepad.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application{

	private List<Activity> activityList = new LinkedList<Activity>();
	private static MyApplication instance;

	public MyApplication() {
		super();
	}

	/**
	 * 单例模式中获取唯一的MyApplication实例
	 * @return
	 */
	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	/**
	 * 添加Activity到activityList中
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 遍历所有Activity并finish
	 */
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	/**
	 * 过滤掉不安全的字符
	 * @param str
	 * @return
	 */
	public String FilteSQLStr(String str){
		String s = str.replace("'", "");
		
		return s; 
	}
}
