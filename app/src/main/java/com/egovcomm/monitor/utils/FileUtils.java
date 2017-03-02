/**
 * 
 */
package com.egovcomm.monitor.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;

/**
 * 文件操作类
 * 
 * @author Nicolls
 *
 *         2015年9月6日
 */
public class FileUtils {
	private static final String TAG = FileUtils.class.getSimpleName();

	/**
	 * 通过传入的字符串在指定路径下创建一个txt文件，会阻塞线程
	 * */
	public static void createTXTFile(String fileName, String filePath, String txt) {
		File dir = new File(filePath);
		dir.mkdirs();
		File file = new File(filePath + "/" + fileName);
		FileWriter fw = null;
		BufferedReader br = null;
		try {
			file.createNewFile();
			br = new BufferedReader(new StringReader(txt));

			fw = new FileWriter(file);
			char[] buf = new char[512];
			int len = 0;
			while ((len = br.read(buf)) != -1) {
				fw.write(buf, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fw.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}


	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	
	/**获取缩略图*/
	public static Bitmap getMediaThumbnail(Context context,String path,String mediaType) {
		Bitmap bitmap = null;
		boolean loadDefault=false;
		try {
			if(TextUtils.isEmpty(path)){
				loadDefault=true;
			}else{
				File file=new File(path);
				if(!file.exists()){
					loadDefault=true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(loadDefault){
			if(TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO, mediaType)){
				bitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.holder_image);
			}else if(TextUtils.equals(MonitorMediaGroup.TYPE_VIDEO, mediaType)){
				bitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.holder_video);
			}
		}else{
			bitmap=BitmapFactory.decodeFile(path);
		}
		return bitmap;
	}
	
	/**保存缩略图
	 * 
	 * @param isLocalFile 存储的是否是本地数据，true是，false则表示是服务器上回来的
	 * */
	public static String saveMediaThumbnail(Context context,String path,String mediaType,boolean isLocalFile) {
		String thumpPath=path;
		try {
		Bitmap bitmap = null;
		int media_width;
		int media_height;
		media_width=context.getResources().getDimensionPixelSize(R.dimen.item_media_view_width);
		media_height=context.getResources().getDimensionPixelSize(R.dimen.item_media_view_height);
		File f=new File(path);
		
		if(isLocalFile){
			File fdir=new File(getAppStorageThumbnailDirectoryPath(context));
			if(fdir!=null){
				thumpPath=fdir.getAbsolutePath()+File.separator+getFileNameNoEx(f.getName())+".jpg";
			}
		}else{
			File fdir=new File(getAppStorageThumbnailDirectoryPath(context));
			if(fdir!=null){
				thumpPath=fdir.getAbsolutePath()+File.separator+getFileNameNoEx(f.getName())+".jpg";
			}
		}
		
		if(TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO, mediaType)){
			bitmap=getImageThumbnail(path, media_width, media_height);
		}else if(TextUtils.equals(MonitorMediaGroup.TYPE_VIDEO, mediaType)){
			bitmap=getVideoThumbnail(path, media_width, media_height, MediaStore.Images.Thumbnails.MICRO_KIND);
			
		}
		File thumpFile=new File(thumpPath);
		//写入
		
			thumpFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(thumpFile);
			bitmap.compress(CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			LogUtils.e(FileUtils.class.getSimpleName(), e.getMessage());
		}
		return thumpPath;
	}

	/**保存缩略图
	 *
	 * */
	public static String saveMediaGroupThumbnail(Context context,String mediaThmbnailPath,String groupId) {
		String groupThumbnailPath="";
		//写入
		try {
			File mediaFile=new File(mediaThmbnailPath);
			File fdir=new File(getAppStorageThumbnailDirectoryPath(context));
			File groupFile=new File(fdir.getAbsolutePath()+File.separator+groupId+".jpg");
			if(!groupFile.exists()){
				groupFile.createNewFile();
				FileInputStream fis=new FileInputStream(mediaFile);
				FileOutputStream fos = new FileOutputStream(groupFile);
				byte[] buf=new byte[1024];
				int len=0;
				while((len=fis.read(buf))!=-1){
					fos.write(buf,0,len);
				}
				fos.flush();
				fos.close();
				fis.close();
			}
			groupThumbnailPath=groupFile.getAbsolutePath();
		} catch (IOException e) {
			LogUtils.e(FileUtils.class.getSimpleName(), e.getMessage());
		}
		return groupThumbnailPath;
	}

	/**
	 * Creates a media file in the {@code Environment.DIRECTORY_PICTURES}
	 * directory. The directory is persistent and available to other
	 * applications like gallery.
	 *
	 * @param type
	 *            Media type. Can be video or image.
	 * @return A file object pointing to the newly created file.
	 */
	public static File getOutputMediaFile(Context context,int type) {
		File mediaStorageDir=new File(FileUtils.getAppStorageOriginalDirectoryPath(context));
		// Create a media file name
		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String timeStamp =UUID.randomUUID().toString();
		File mediaFile;
		if (type == CameraHelper.MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator  + timeStamp + ".jpg");
		} else if (type == CameraHelper.MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator  + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	public static File saveData(Context context,byte[] data,int mediaType,int screenOrientation){
		String path="";
		File saveFile=null;
		saveFile=getOutputMediaFile(context,mediaType);
		path=saveFile.getAbsolutePath();
		if(mediaType==CameraHelper.MEDIA_TYPE_IMAGE){
			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);
			}
			if(null != b){
				if(screenOrientation==CameraHelper.SCREEN_PORTRAIT){
					b = getRotateBitmap(b, 90.0f);
				}
				try {
					FileOutputStream fos=new FileOutputStream(saveFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();

				} catch (Exception e) {
					LogUtils.e(TAG, "保存图片数据的时候有问题");
				}
			}


		}else if(mediaType==CameraHelper.MEDIA_TYPE_VIDEO){

		}

		return saveFile;
	}

	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
		Matrix matrix = new Matrix();
		matrix.postRotate((float)rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	/**复制文件*/
	public static void copyFile(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} catch (Exception e){
			LogUtils.e(TAG,e.getMessage());
		}finally {
			input.close();
			output.close();
		}
	}


	/*
	 * Java文件操作 获取文件扩展名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/*
	 * Java文件操作 获取不带扩展名的文件名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	/** 返回文件大小，用KB，MB，GB等表示 */
	public static String getFileSize(File file) {
		String size = "";
		if (file.exists() && file.isFile()) {
			long fileS = file.length();
			DecimalFormat df = new DecimalFormat("#.00");
			if (fileS < 1024) {
				size = df.format((double) fileS) + "BT";
			} else if (fileS < 1048576) {
				size = df.format((double) fileS / 1024) + "KB";
			} else if (fileS < 1073741824) {
				size = df.format((double) fileS / 1048576) + "MB";
			} else {
				size = df.format((double) fileS / 1073741824) + "GB";
			}
		} else if (file.exists() && file.isDirectory()) {
			size = "";
		} else {
			size = "0BT";
		}
		return size;
	}
	
	/** 返回文件大小，用KB，MB，GB等表示 */
	public static String getFileSize(long fileS) {
		String size = "";
		if (fileS>0) {
			DecimalFormat df = new DecimalFormat("#.00");
			if (fileS < 1024) {
				size = df.format((double) fileS) + "BT";
			} else if (fileS < 1048576) {
				size = df.format((double) fileS / 1024) + "KB";
			} else if (fileS < 1073741824) {
				size = df.format((double) fileS / 1048576) + "MB";
			} else {
				size = df.format((double) fileS / 1073741824) + "GB";
			}
		} else {
			size = "0BT";
		}
		return size;
	}
	/**修改文件名称 */
	public static File modifyFileName(String filePath,String fileName,String newName){
		
		File f=null;
		try {
			f=new File(filePath);
			filePath.indexOf(".",-1);
			String suffix=getExtensionName(fileName);
			newName=newName+suffix;
			String filePath2=filePath.replace("fileName", newName);
			File f2=new File(filePath2);
			f.renameTo(f2);
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return f;
	}
	
	public static boolean isFileExit(String path){
		boolean isExit=true;
		try {
			if(TextUtils.isEmpty(path)){
				isExit=false;
			}else{
				File f=new File(path);
				if(!f.exists()){
					isExit=false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isExit;
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	
	/**统一获取要保存的文件路径
	 * 
	 * @param childPath 统一存储路径下的子路径，像 /local/test 则会在统一路径下新建一个/local/test的文件夹，并返回此目录的路径,如果为空则返回统一路径
	 * */
	public static File getAppStorageDirectory(Context context,String childPath) {
		File f=null;
		String storePath="";
		long _10MB=10*1024*1024L;//10MB
		try {

		if (!isExternalStorageWritable()) {//外部存储不可用，则获取内部的存储
			if(context!=null){
				long size=context.getFilesDir().getFreeSpace();//单位是Byte
				LogUtils.i("FileUtils","剩余存储大小："+size);//单位是Byte
				if(size<_10MB){//小于10MB则提示存储空间不足
					ToastUtils.toast(context,"存储空间不足!请适当清理文件");
				}
				storePath=context.getFilesDir().getAbsolutePath();
			}
		}else{//外部存储可用
			if(context!=null){//获取一个应用卸载时会自动删除的目录
				long size=context.getFilesDir().getFreeSpace();
				LogUtils.i("FileUtils","剩余存储大小："+size);//单位是Byte
				if(size<_10MB){//小于10MB则提示存储空间不足
					ToastUtils.toast(context,"存储空间不足!请适当清理文件");
				}
				storePath=context.getExternalFilesDir(null).getAbsolutePath();
			}else{
				storePath=Environment.getExternalStorageDirectory().getAbsolutePath();
			}
		}
		LogUtils.i("FileUtils","本应用存储文件的目录是："+storePath);
		if(!TextUtils.isEmpty(childPath)){
			if(childPath.startsWith(File.separator)){
				f = new File(storePath+childPath);
			}else{
				f = new File(storePath+File.separator+childPath);
			}
		}
		
		// Create the storage directory if it does not exist
		if (!f.exists()) {
			if (!f.mkdirs()) {
				LogUtils.d("FileUtils", "failed to create directory");
				return null;
			}
		}
		}catch (Exception e){
			e.printStackTrace();
		}
		return f;
	}
	/**获取存储文件的路径*/
	public static String getAppStorageOriginalDirectoryPath(Context context) {
		File f=getAppStorageDirectory(context,AppConstant.FILE_DIR_ORIGINAL);
		if(f!=null){
			return f.getAbsolutePath();
		}else{
			return "";
		}
	}
	
	/**获取存储文件缩略图的路径*/
	public static String getAppStorageThumbnailDirectoryPath(Context context) {
		File f=getAppStorageDirectory(context,AppConstant.FILE_DIR_THUMBNAIL);
		if(f!=null){
			return f.getAbsolutePath();
		}else{
			return "";
		}
	}

}
