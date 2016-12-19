package com.egovcomm.monitor.utils;

import java.util.HashMap;
import java.util.Set;

import com.egovcomm.monitor.activity.SigninActivity;
import com.egovcomm.monitor.common.AppConstant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 */
public class MyActivityManager {
	public static HashMap<String, Activity> activitys;
	private static MyActivityManager instance;
	private long clickCurrenTime = 0L;

	private MyActivityManager() {
	}

	/**
	 * 单一实例
	 */
	public static MyActivityManager getAppManager() {
		if (instance == null) {
			instance = new MyActivityManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activitys == null) {
			activitys = new HashMap<String, Activity>();
		}

		String key = activity.getClass().getSimpleName();
		activitys.put(key, activity);
		LogUtils.i(MyActivityManager.class.getSimpleName(), "将"
				+ activity.getClass().getSimpleName() + "添加到管理栈中");
	}

	/**
	 * 结束指定的Activity
	 */
	public void removeActivity(Activity activity) {
		if (activity != null) {
			String key = activity.getClass().getSimpleName();
			// if(activity.getClass() ==MainFragmentActivity.class){//点击多次，退出应用
			// Long currenTime=System.currentTimeMillis();
			// if(currenTime-clickCurrenTime<1500){//1.5秒内点击有用
			// activitys.remove(key);
			// activity.finish();
			// activity = null;
			// }else{
			// Toast.makeText(activity, "再按一次，退出应用", 0).show();
			// clickCurrenTime=currenTime;
			// }
			// }else{
			LogUtils.i(MyActivityManager.class.getSimpleName(), "从管理栈中移除"
					+ activity.getClass().getSimpleName());
			activitys.remove(key);
			// }
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void removeActivity(Class<?> cls) {
		Set<String> set = activitys.keySet();
		for (String key : set) {
			if (cls.getSimpleName().equals(key)) {
				activitys.remove(key);
			}
		}

	}

	/**
	 * 结束所有Activity
	 */
	public void removeAllActivity() {
		Set<String> set = activitys.keySet();
		for (String key : set) {
			Activity a = activitys.get(key);
			if (a != null && !a.isFinishing()) {
				a.finish();
			}
		}
		activitys.clear();
	}

	/** 重新登录 */
	public void reLogin(final Context context, boolean isCleanData) {
		LogUtils.writeLogtoFile("mjk：","进入reLogin方法，并发送广播");
		Intent intent=new Intent(AppConstant.BROAD_CAST_DESTROY_LOCATION);
		context.sendBroadcast(intent);//发送停止位置的广播
		LogUtils.writeLogtoFile("mjk：","进入reLogin方法，发送广播完成");

		if (isCleanData) {
			SPUtils.cleanLocalData(context);
		}
		LogUtils.writeLogtoFile("mjk：","进入reLogin方法，执行打开登录界面方法");
		Intent reIntent = new Intent(context, SigninActivity.class);
		reIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(reIntent);
		LogUtils.writeLogtoFile("mjk：","进入reLogin方法，完成打开登录界面方法");

	}


	/**
	 *
	public void reLogin(final Activity context, boolean isCleanData) {
		final int WAIT_DESTROY_LOCATION_TIME=6;
		Intent intent=new Intent(AppConstant.BROAD_CAST_DESTROY_LOCATION);
		context.sendBroadcast(intent);//发送停止位置的广播
		if (isCleanData) {
			SPUtils.cleanLocalData(context);
		}
		new Thread(){
			@Override
			public void run() {
				super.run();
				int time=0;
				while(true){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(time>=WAIT_DESTROY_LOCATION_TIME||!CommonUtil.isActivityExit(context,MainUserActivity.class)){
						LogUtils.i("myActivityManager","时间到或者已经销毁");
						break;
					}
					time++;
				}
				context.finish();
				Intent intent = new Intent(context, SigninActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				context.startActivity(intent);

			}
		}.start();


	}
	 *
	 *
	 *
	 * */

	/**
	 * 退出应用程序
	 */
	public void appExit(Context context) {
		// try {
		removeAllActivity();
		System.exit(0);
		// } catch (Exception e) {
		// }
	}
}