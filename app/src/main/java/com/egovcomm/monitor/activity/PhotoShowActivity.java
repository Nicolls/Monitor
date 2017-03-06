package com.egovcomm.monitor.activity;

import java.io.File;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.egovcomm.monitor.view.ZoomImageView;

public class PhotoShowActivity extends BaseActivity {

	private ZoomImageView imageView;
	private MonitorMedia media;
	private List<MonitorMedia> mediaList;
	private String path;
	private View mBottomBar;
	private ImageView mIvDeleted;
	private View detailView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_show);



		media=getIntent().getParcelableExtra("media");
		mediaList=getIntent().getParcelableArrayListExtra("mediaList");
		path=media.getPath();

		mBottomBar=findViewById(R.id.photo_show_bottom_bar);
		
		imageView = (ZoomImageView) this.findViewById(R.id.imageView);
		imageView.setImageBitmap(BitmapFactory.decodeFile(path));

		detailView=findViewById(R.id.view_detail_scroll);

		mIvDeleted=(ImageView) findViewById(R.id.view_iv_deleted);
		
		if(TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD, media.getUploadState())
				||TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL, media.getUploadState())
				||TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL, media.getUploadState())
				){//只有未上传，上传失败，上传取消状态的可以被删除
			mIvDeleted.setVisibility(View.VISIBLE);
		}else{
			mIvDeleted.setVisibility(View.INVISIBLE);
		}
		//详情数据
		if(media!=null){
			((TextView)findViewById(R.id.item_name)).setText("标题："+media.getRemark());
			((TextView)findViewById(R.id.item_location)).setText("拍摄地点："+media.getShootingLocation());
			((TextView)findViewById(R.id.item_longitude)).setText("经度："+media.getLongitude());
			((TextView)findViewById(R.id.item_latitude)).setText("纬度："+media.getLatitude());
			((TextView)findViewById(R.id.item_size)).setText("文件大小："+ FileUtils.getFileSize(Long.parseLong(media.getFileSize())));
			((TextView)findViewById(R.id.item_create_time)).setText(media.getCreateTime()==null?"上传时间："+media.getUploadTime():"创建时间："+media.getCreateTime());
			((TextView)findViewById(R.id.item_time)).setText("触发时间："+(media.getTime()==null?"":media.getTime()));
			((TextView)findViewById(R.id.item_reason)).setText("事由："+(media.getReason()==null?"":media.getReason()));
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

	public void onBack(View view){
		finish();
	}
	
	public void onDeleted(View view){
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
	
	private final class TouchListener implements OnTouchListener {
		
		/** 记录是拖拉照片模式还是放大缩小照片模式 */
		private int mode = 0;// 初始状态  
		/** 拖拉照片模式 */
		private static final int MODE_DRAG = 1;
		/** 放大缩小照片模式 */
		private static final int MODE_ZOOM = 2;
		
		/** 用于记录开始时候的坐标位置 */
		private PointF startPoint = new PointF();
		/** 用于记录拖拉图片移动的坐标位置 */
		private Matrix matrix = new Matrix();
		/** 用于记录图片要进行拖拉时候的坐标位置 */
		private Matrix currentMatrix = new Matrix();
	
		/** 两个手指的开始距离 */
		private float startDis;
		/** 两个手指的中间点 */
		private PointF midPoint;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 手指压下屏幕
			case MotionEvent.ACTION_DOWN:
				mode = MODE_DRAG;
				// 记录ImageView当前的移动位置
				currentMatrix.set(imageView.getImageMatrix());
				startPoint.set(event.getX(), event.getY());
				break;
			// 手指在屏幕上移动，改事件会被不断触发
			case MotionEvent.ACTION_MOVE:
				// 拖拉图片
				if (mode == MODE_DRAG) {
					float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
					float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
					// 在没有移动之前的位置上进行移动
					matrix.set(currentMatrix);
					matrix.postTranslate(dx, dy);
				}
				// 放大缩小图片
				else if (mode == MODE_ZOOM) {
					float endDis = distance(event);// 结束距离
					if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
						float scale = endDis / startDis;// 得到缩放倍数
						matrix.set(currentMatrix);
						matrix.postScale(scale, scale,midPoint.x,midPoint.y);
					}
				}
				break;
			// 手指离开屏幕
			case MotionEvent.ACTION_UP:
				// 当触点离开屏幕，但是屏幕上还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
			// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = MODE_ZOOM;
				/** 计算两个手指间的距离 */
				startDis = distance(event);
				/** 计算两个手指间的中间点 */
				if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
					midPoint = mid(event);
					//记录当前ImageView的缩放倍数
					currentMatrix.set(imageView.getImageMatrix());
				}
				break;
			}
			imageView.setImageMatrix(matrix);
			return true;
		}

		/** 计算两个手指间的距离 */
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** 使用勾股定理返回两点之间的距离 */
			return (float) Math.sqrt(dx * dx + dy * dy);
		}

		/** 计算两个手指间的中间点 */
		private PointF mid(MotionEvent event) {
			float midX = (event.getX(1) + event.getX(0)) / 2;
			float midY = (event.getY(1) + event.getY(0)) / 2;
			return new PointF(midX, midY);
		}

	}

	@Override
	public void dateUpdate(int id, Object obj) {
		// TODO Auto-generated method stub
		
	}

}