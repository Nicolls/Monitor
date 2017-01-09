/**
 * 
 */
package com.egovcomm.monitor.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;

import com.egovcomm.monitor.model.MonitorMedia;

/**
 * 
 * @author Nicolls
 *
 *         2015年7月24日
 */
public class MediaDownLoadAsyncTask extends AsyncTask<String, Integer, MonitorMedia> {
	private MonitorMedia media;
	private MediaDownloadListener mListener;
	private Context context;
	public MediaDownLoadAsyncTask(Context context,MonitorMedia media, MediaDownloadListener listener) {
		this.context=context;
		this.media=media;
		this.mListener = listener;
	}


	/** 应用更新完成监听器 */
	public interface MediaDownloadListener {
		void downLoadCompleted(MonitorMedia media);
		void downLoading(MonitorMedia media);
		void downLoadFail(MonitorMedia media);
	}
	
	@Override
	protected MonitorMedia doInBackground(String... arg0) {
		try {
			LogUtils.i("MediaDownLoadTask", "url:"+arg0[0]);
			URL url = new URL(arg0[0]);
			HttpURLConnection con;
			File mediaFile=null;
			try {
				con = (HttpURLConnection) url.openConnection();
				con.connect();
				int code = con.getResponseCode();
				if (code == 200) {
					InputStream is = con.getInputStream();
					byte[] buf = new byte[1024];
					int length = con.getContentLength();
					int downloadCount = length / buf.length;
					int spacing = downloadCount / 100;
					int m = 0;
					mediaFile=new File(FileUtils.getAppStorageOriginalDirectoryPath(context)+File.separator+media.getFileName());
					mediaFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(mediaFile);
					int len = 0;
					int value = 0;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
						if (m >= spacing) {
							m = 0;
							value++;
							this.publishProgress(value);
						}
						m++;
					}
					fos.flush();
					fos.close(); // 使用流完毕之后,记得关闭流
					
					media.setPath(mediaFile.getPath());
					media.setThumbnailPath(FileUtils.saveMediaThumbnail(context, mediaFile.getPath(), media.getMediaType(), false));
					if(!FileUtils.isFileExit(FileUtils.getAppStorageThumbnailDirectoryPath(context)+File.separator+media.getGroupUploadId()+".jpg")){
						FileUtils.saveMediaGroupThumbnail(context, media.getThumbnailPath(), media.getGroupUploadId());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(mediaFile!=null&&mediaFile.exists()){
					mediaFile.delete();
				}
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		return this.media;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if(mListener!=null){
			media.setDownloadState(MonitorMedia.DOWNLOAD_STATE_DOWNLOADING);
			media.setProgress(values[0]);
			mListener.downLoading(media);
		}

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected void onPostExecute(MonitorMedia result) {
		super.onPostExecute(result);
		if(mListener!=null){
			if(result!=null){
				result.setDownloadState(MonitorMedia.DOWNLOAD_STATE_DOWNLOADED);
				mListener.downLoadCompleted(result);
			}else{
				media.setDownloadState(MonitorMedia.DOWNLOAD_STATE_DOWNLOAD_FAIL);
				mListener.downLoadFail(media);
			}
		}
	}



}
