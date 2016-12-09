package com.egovcomm.monitor.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.CommonViewUtils;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.ToastUtils;

/**
 * 视频
 * 
 * @author 胡汉三
 *
 */
public class VideoPlayActivity extends BaseActivity implements
		SurfaceHolder.Callback {

	/** Called when the activity is first created. */
	MediaPlayer player;
	SurfaceView surface;
	SurfaceHolder surfaceHolder;
	private MonitorMedia media;
	private List<MonitorMedia> mediaList;
	private String path;
	private ImageView imageView;
	private ImageView videoIcon;
	private ImageView playImageView;
	private ImageView mIvDeleted;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_play);
		media = getIntent().getParcelableExtra("media");
		mediaList = getIntent().getParcelableArrayListExtra("mediaList");
		path = media.getPath();
		playImageView = (ImageView) findViewById(R.id.video_play_pause_iv);
		imageView = (ImageView) findViewById(R.id.video_play_im);
		videoIcon = (ImageView) findViewById(R.id.view_iv_video_icon);
		imageView.setImageBitmap(FileUtils.getVideoThumbnail(path,
				CommonViewUtils.getDisplaySize(this).x,
				CommonViewUtils.getDisplaySize(this).y,
				MediaStore.Images.Thumbnails.MINI_KIND));
		surface = (SurfaceView) findViewById(R.id.surface);

		surfaceHolder = surface.getHolder(); // SurfaceHolder是SurfaceView的控制接口
		surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// Surface类型
		surface.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		videoIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onPlayPause(playImageView);
			}
		});

		mIvDeleted = (ImageView) findViewById(R.id.view_iv_deleted);

		if (TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD,
				media.getUploadState())
				|| TextUtils.equals(
						MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL,
						media.getUploadState())
				|| TextUtils.equals(
						MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL,
						media.getUploadState())) {// 只有未上传，上传失败，上传取消状态的可以被删除
			mIvDeleted.setVisibility(View.VISIBLE);
		} else {
			mIvDeleted.setVisibility(View.GONE);
		}
	}

	public void onDeleted(View view) {
		if(media!=null){
			DBHelper.getInstance(getApplicationContext()).deleteMonitorMedia(media.getId());
			try {
				File f=new File(media.getPath());
				f.delete();
				ToastUtils.toast(getApplicationContext(), "删除成功!");
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onPlayPause(View view) {
		view.setSelected(!view.isSelected());
		if (view.isSelected()) {
			imageView.setVisibility(View.GONE);
			videoIcon.setVisibility(View.GONE);
			player.start();
		} else {
			videoIcon.setVisibility(View.VISIBLE);
			player.pause();
		}
	}

	public void onBack(View view) {
		finish();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@SuppressLint("SdCardPath")
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
		player = new MediaPlayer();
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				videoIcon.setVisibility(View.VISIBLE);
				playImageView.setSelected(false);
			}
		});
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(surfaceHolder);
		// 设置显示视频显示在SurfaceView上
		try {
			// 新建Bundle对象
			player.setDataSource(path);
			player.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public Bitmap getVideoThumbnail(String filePath) {
	// Bitmap bitmap = null;
	// MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	// try {
	// retriever.setDataSource(filePath);
	// bitmap = retriever.getFrameAtTime();
	// } catch (IllegalArgumentException e) {
	// e.printStackTrace();
	// } catch (RuntimeException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// retriever.release();
	// } catch (RuntimeException e) {
	// e.printStackTrace();
	// }
	// }
	// return bitmap;
	// }

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (player.isPlaying()) {
			player.stop();
		}
		player.release();
		// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		// TODO Auto-generated method stub

	}

}
