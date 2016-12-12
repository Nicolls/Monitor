package com.egovcomm.monitor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.activity.SigninActivity;
import com.egovcomm.monitor.activity.WelcomeActivity;
import com.egovcomm.monitor.model.RspVersion;
import com.egovcomm.monitor.model.User;
import com.egovcomm.monitor.model.AppRequest;
import com.egovcomm.monitor.service.UpdateAPPService;

/**
 * Created by mengjk on 2016/12/9.
 */

public class AppUpdateUtils {

    public interface AppUpdateChargeListener{
        void chargeResult(RspVersion version,boolean isUpdate);
    }

    /** 判断更新 */
    public static void chargeUpdate(final Activity activity, final RspVersion version,final AppUpdateChargeListener lis) {
        if (version != null&&version.getData()!=null) {
            if(version.getData().isCanUpdate()){//需要更新
                if(version.getData().isForceUpdate()){//强制更新
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setTitle(activity.getString(R.string.app_update));
                    builder.setMessage(activity.getString(R.string.app_update_force_tip));

                    builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if(lis!=null){
                                lis.chargeResult(version,true);
                            }
                            updateApk(activity,version.getData().getLatestVersioUri(),true);
                            MyActivityManager.getAppManager().appExit(activity);
                        }
                    });
                    builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if(lis!=null){
                                lis.chargeResult(version,false);
                            }
                            MyActivityManager.getAppManager().appExit(activity);
                        }
                    });
                    builder.create().show();
                }else{//非强制更新
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setTitle(activity.getString(R.string.app_update));
                    builder.setMessage(activity.getString(R.string.app_update_tip));

                    builder.setPositiveButton("马上更新", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if(lis!=null){
                                lis.chargeResult(version,true);
                            }
                            updateApk(activity,version.getData().getLatestVersioUri(),false);
                        }
                    });
                    builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if(lis!=null){
                                lis.chargeResult(version,false);
                            }
                        }
                    });
                    builder.create().show();
                }
            }else{//不需要更新
                if(lis!=null){
                    lis.chargeResult(version,false);
                }
            }
        } else {
            if(lis!=null){
                lis.chargeResult(version,false);
            }
        }
    }

    /** 版本更新 */
    private static void updateApk(Activity activity,String downloadUrl, boolean isfinish) {
        downloadUrl=downloadUrl.replace(" ","");
		AppRequest ebReq = new AppRequest(downloadUrl);
		downloadUrl=ebReq.getReqeustURL();
        ToastUtils.toast(activity, activity.getString(R.string.start_download));
        Intent intent = new Intent(activity,UpdateAPPService.class);
        intent.putExtra(UpdateAPPService.INTENT_DOWNLOAD_URL, downloadUrl);
        activity.startService(intent);
        if (isfinish) {
            activity.finish();
        }
    }

}
