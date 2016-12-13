package com.egovcomm.monitor.ftp;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.Intent;

import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.LogUtils;
  
/** 
 * 上传任务 
 * @author chuer 
 * @date 2015年1月7日 下午2:30:46 
 */  
public class UploadTask implements Callable<String>{  
	private final static String TAG=UploadTask.class.getSimpleName();
    private String workingDirectory;
    private FTPConnection ftpConnection; 
    private MonitorMediaGroupUpload uploadMediaGroup;
    private List<MonitorMedia> mediaList;
    private Context context;
    public UploadTask(Context context,String workingDirectory,MonitorMediaGroupUpload uploadMediaGroup,List<MonitorMedia> mediaList){  
    	this.context=context;
    	this.workingDirectory = workingDirectory;  
    	this.uploadMediaGroup = uploadMediaGroup;  
    	this.mediaList = mediaList;  
    }  
      
      
    @Override  
    public String call() throws Exception {  
    	String result = "";  
        try{  
        	ftpConnection = new FTPConnection(context,this.uploadMediaGroup);
            ftpConnection.uploadFileList(mediaList);
            result="上传成功!";
        }catch(IOException ex){  
            result="上传失败!";
            LogUtils.e(TAG, "上传失败");
        }finally{
        	ftpConnection.disconnect();
        }
        return result;  
    }  
    /**取消*/
    public void cancel(){
    	LogUtils.i(TAG, "取消上传@@@@@");
    	if(ftpConnection!=null){
    		ftpConnection.cancel();
    	}
    }
    

}  
