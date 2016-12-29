package com.egovcomm.monitor.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.CommonViewUtils;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.ToastUtils;

/**
 * 视频
 * 
 * @author 胡汉三
 *
 */
public class VideoPlayActivity extends BaseActivity implements
		SurfaceHolder.Callback,SeekBar.OnSeekBarChangeListener {

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
	private SeekBar seekBar;
	private boolean isPlaying=false;
	private int currentPosition=0;
	private View playBar;
	private View detailView;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_play);
		media = getIntent().getParcelableExtra("media");
		mediaList = getIntent().getParcelableArrayListExtra("mediaList");
		path = media.getPath();
		seekBar= (SeekBar) findViewById(R.id.video_seekbar);
		seekBar.setOnSeekBarChangeListener(this);
		detailView=findViewById(R.id.view_detail_scroll);
		playBar=findViewById(R.id.video_play_bottom_bar);
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
//		surface.setOnClickListener(new OnClickListener() {//点击隐藏
//			@Override
//			public void onClick(View v) {
//				playBar.setVisibility(View.VISIBLE);
//				if(isPlaying){
//					hideBarHandler.sendEmptyMessageDelayed(0,4000);
//				}
//			}
//		});

		//详情数据
		if(media!=null){
			((TextView)findViewById(R.id.item_name)).setText("标题："+media.getRemark());
			((TextView)findViewById(R.id.item_location)).setText("拍摄地点："+media.getShootingLocation());
			((TextView)findViewById(R.id.item_size)).setText("文件大小："+FileUtils.getFileSize(Long.parseLong(media.getFileSize())));
			((TextView)findViewById(R.id.item_create_time)).setText("创建时间："+media.getCreateTime());
			((TextView)findViewById(R.id.item_time)).setText("触发时间："+(media.getTime()==null?"":media.getTime()));
			((TextView)findViewById(R.id.item_reason)).setText("事由："+(media.getReason()==null?"":media.getReason()));
		}

	}

	private Handler hideBarHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			playBar.setVisibility(View.GONE);
		}
	};

	public void onDeleted(View view) {
		if(media!=null){
			player.stop();
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
			isPlaying=true;
			player.start();
			handler.sendEmptyMessage(1);//播放是1
		} else {
			videoIcon.setVisibility(View.VISIBLE);
			isPlaying=false;
			player.pause();//开始是暂停是0
			handler.sendEmptyMessage(0);//暂停是0
		}
	}
	//显示详情
	public void onDetail(View view){
		if(detailView.getVisibility()==View.VISIBLE){
			detailView.setVisibility(View.GONE);
		}else{
			detailView.setVisibility(View.VISIBLE);
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
				isPlaying=false;
			}
		});
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(surfaceHolder);
		// 设置显示视频显示在SurfaceView上
		try {
			// 新建Bundle对象
			player.setDataSource(path);
			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// 按照初始位置播放
				player.seekTo(currentPosition);
				// 设置进度条的最大进度为视频流的最大播放时长

				LogUtils.i(tag,"视频时长为："+player.getDuration()+"");
				seekBar.setMax(player.getDuration());
				seekBar.setProgress(currentPosition);
				}
			});
			player.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1){
				LogUtils.i(tag,"播放");
				if(player!=null&&isPlaying&&player.isPlaying()){
					int progress=player.getCurrentPosition();
					LogUtils.i(tag,"当前播放到："+progress+"");
					seekBar.setProgress(progress);
					handler.sendEmptyMessageDelayed(1,300);
				}
			}else if(msg.what==0){
				LogUtils.i(tag,"暂停");
				handler.removeMessages(1);
			}
		}
	};

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		LogUtils.i(tag,"surfaceDestroyed");
		if(player!=null){
			currentPosition=player.getCurrentPosition();
			videoIcon.setVisibility(View.VISIBLE);
			playImageView.setSelected(false);
			handler.sendEmptyMessage(0);//暂停
			if (player.isPlaying()) {
				player.stop();
			}
			isPlaying=false;
			player.release();


		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		LogUtils.i(tag,"移动到："+progress+"");
		if (player != null&&!player.isPlaying()) {
			player.seekTo(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		if(player!=null&&isPlaying){
			player.pause();
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(player!=null&&isPlaying){
			player.start();
			handler.sendEmptyMessage(1);//播放是1
		}
	}
}
