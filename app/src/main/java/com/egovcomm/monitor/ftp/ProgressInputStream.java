package com.egovcomm.monitor.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.egovcomm.monitor.utils.LogUtils;

public class ProgressInputStream extends InputStream {

	private static final String TAG=ProgressInputStream.class.getSimpleName();
    private static final int TEN_KILOBYTES = 1024 * 100;  //每上传100K返回一次

    private InputStream inputStream;

    private long progress;
    private long lastUpdate;
    private long fileSizeCount=1;

    private boolean closed;
    public static final int PROGRESSSTYLE_PERCENT=0;
    public static final int PROGRESSSTYLE_FILE_SIZE=1;
    private int progressStyle=PROGRESSSTYLE_PERCENT;
	/*
	 * 上传进度监听
	 */
	public interface UploadProgressListener {
		public void onUploadProgress(long uploadSize, File file);
	}

    private UploadProgressListener listener;
    private File localFile;

    public ProgressInputStream(File localFile,UploadProgressListener listener,int progressStyle) {
    	this.progressStyle=progressStyle;
    	if(localFile!=null&&localFile.exists()){
    		  try {
    			this.inputStream = new FileInputStream(localFile);
				fileSizeCount =this.inputStream.available();
    			} catch (Exception e) {
    				LogUtils.e(TAG,"创建流失败");
    			}
    	}else{
    		LogUtils.e(TAG,"文件为空");
    	}
        this.progress = 0;
        this.lastUpdate = 0;
        this.listener = listener;
        this.localFile = localFile;
        
        this.closed = false;
    }
    /**设置流样式，或者百分比*/
    public void setProgressstyle(int style){
    	this.progressStyle=style;
    }
    
    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed)
            throw new IOException("already closed");
        closed = true;
    }

    private int incrementCounterAndUpdateDisplay(int count) {
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        return count;
    }

    private long maybeUpdateDisplay(long progress, long lastUpdate) {
        if (progress - lastUpdate > TEN_KILOBYTES) {
            lastUpdate = progress;
            if(listener!=null){
            	if(progressStyle==PROGRESSSTYLE_FILE_SIZE){
            		this.listener.onUploadProgress(progress, this.localFile);
            	}else{
            		float p=progress/(fileSizeCount*1.0f);
            		progress=(long) (p*100);
            		//System.out.println(progress+"---"+fileSizeCount+"--"+p+"--"+progress);
            		this.listener.onUploadProgress(progress, this.localFile);
            	}
            }
        }
        return lastUpdate;
    }
    
  
    
}
