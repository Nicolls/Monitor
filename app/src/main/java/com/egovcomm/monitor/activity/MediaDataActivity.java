/**
 * 
 */
package com.egovcomm.monitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow.OnDismissListener;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.adapter.CommonFragmentPagerAdapter;
import com.egovcomm.monitor.common.BaseFragment;
import com.egovcomm.monitor.fragment.BaseListFragment;
import com.egovcomm.monitor.fragment.MediaCompletedFragment;
import com.egovcomm.monitor.fragment.MediaUnUploadFragment;
import com.egovcomm.monitor.fragment.MediaUploadingFragment;
import com.egovcomm.monitor.ftp.FTPService;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.model.ItemEntity;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.egovcomm.monitor.view.ListPopupWindow;
import com.egovcomm.monitor.view.ListPopupWindow.OnPopupListClickLstener;
import com.egovcomm.monitor.view.TabBarView.OnPagerChangeListener;

/**
 * ViewPager+FagmentMent实现的可滑动的fragment
 * 
 * @author mengjk
 *
 *         2015年5月18日
 */
public class MediaDataActivity extends CommonPagerActivity implements OnClickListener,OnPagerChangeListener{

	private CommonFragmentPagerAdapter<BaseFragment> mAdapter;
	public MediaUnUploadFragment unUploadFragment =null;
	public MediaCompletedFragment completedFragment = null;
	public MediaUploadingFragment uploadingFragment = null;
	//private String mediaType="0";//当前视频类型
	private List<BaseFragment> dataList = new ArrayList<BaseFragment>();
	private int currentPage=0;
	
	private ListPopupWindow popupWindow;
	private boolean isPopWindowShow=false;
	List<ItemEntity> filterList=new ArrayList<ItemEntity>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBroadCastListener();
		mTitle.setText("我的数据");
		mBack.setOnClickListener(this);
		mRightTv.setOnClickListener(this);
		mRightIv.setOnClickListener(this);
		
		dataList.clear();
		mTabBar.setVisibility(View.VISIBLE);
		mPagerTab.setVisibility(View.GONE);
		mSlidingTabLayout.setVisibility(View.GONE);
		setOnPagerChangeListener(this);
		mPagerTab.setTabIndicatorColor(getResources().getColor(R.color.monitor_green));
		mPagerTab.setBackgroundColor(getResources().getColor(R.color.white));
		mPagerTab.setDrawFullUnderline(true);
		mAdapter = new CommonFragmentPagerAdapter<BaseFragment>(getSupportFragmentManager());
		Bundle b=null;
		
		unUploadFragment = new MediaUnUploadFragment();
		
		b=new Bundle();
		b.putString("name", "未上传");
		unUploadFragment.setArguments(b);
		
		uploadingFragment = new MediaUploadingFragment();
		b=new Bundle();
		b.putString("name", "上传中");
		uploadingFragment.setArguments(b);
		
		completedFragment = new MediaCompletedFragment();
		b=new Bundle();
		b.putString("name", "完成");
		completedFragment.setArguments(b);
		
		
		
		dataList.add(unUploadFragment);
		dataList.add(uploadingFragment);
		dataList.add(completedFragment);
		List<String> tabs = new ArrayList<String>();
		for (BaseFragment f : dataList) {
			tabs.add(f.getArguments().getString("name"));
		}
		mTabBar.setTabCount(tabs);
		mAdapter.setData(dataList);
		mViewPager.setAdapter(mAdapter);
		mSlidingTabLayout.setViewPager(mViewPager);
		mTabBar.setViewPager(mViewPager);
		setPagerScoll(true, true);
		
		
		String mediaType=getIntent().getStringExtra("mediaType");
		setTitleType(mediaType);
		ItemEntity entity=null;
		filterList.clear();
		entity=new ItemEntity();
		entity.setTitle("图片");
		entity.setValue(MonitorMediaGroup.TYPE_PHOTO);
		filterList.add(entity);
		entity=new ItemEntity();
		entity.setTitle("视频");
		entity.setValue(MonitorMediaGroup.TYPE_VIDEO);
		filterList.add(entity);
		unUploadFragment.setMediaType(mediaType);
	}
	
	private void setTitleType(String mediaType){
		if(TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO, mediaType)){
			mRightTv.setText("图片");
		}else if(TextUtils.equals(MonitorMediaGroup.TYPE_VIDEO, mediaType)){
			mRightTv.setText("视频");
		}else{
			mRightTv.setText("全部");
		}
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.top_bar_left:
			finish();
			break;
		case R.id.top_bar_right_text:
			arg0.setSelected(!arg0.isSelected());
			showHidePop(!isPopWindowShow);
			
			break;
		case R.id.top_bar_right_icon:
			showHidePop(false);
			if(currentPage==0){
				unUploadFragment.showHideBottomBar();
			}else if(currentPage==1){
				uploadingFragment.showHideBottomBar();
			}else if(currentPage==2){
				completedFragment.showHideBottomBar();
			}
			break;

		default:
			break;
		}
	}
	/**弹出框*/
	private void showHidePop(boolean show){
		this.isPopWindowShow=show;
		if(!this.isPopWindowShow){
			if(popupWindow!=null){
				popupWindow.dismiss();
			}
			mRightTv.setSelected(false);
		}else{
			mRightTv.setSelected(true);
			popupWindow=new ListPopupWindow(MediaDataActivity.this);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					MediaDataActivity.this.isPopWindowShow=false;
					mRightTv.setSelected(false);
				}
			});
			popupWindow.setListClickListener(new OnPopupListClickLstener() {
				
				@Override
				public void onPopupListClick(ItemEntity entity) {
					//ToastUtils.toast(getApplicationContext(), entity.getTitle());
					//ToastUtils.toast(getApplicationContext(), "pop"+entity.getValue());
					mRightTv.setSelected(false);
					mRightTv.setText(entity.getTitle());
					if(currentPage==0){
						unUploadFragment.setMediaType(entity.getValue());
					}else if(currentPage==1){
						uploadingFragment.setMediaType(entity.getValue());
					}else if(currentPage==2){
						completedFragment.setMediaType(entity.getValue());
					}
					freshAllData();
					
				}
			});
			popupWindow.fresh(filterList);
			popupWindow.showAsDropDown(mTopBar);
		}
	}

	/**广播监听*/
	public void setBroadCastListener(){
		IntentFilter filter=new IntentFilter(FTPService.FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD);
		registerReceiver(receiver, filter);
	}
	
	/**监听FTP发送的广播
	 **/
	private BroadcastReceiver receiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent!=null&&intent.getAction()!=null&&TextUtils.equals(FTPService.FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD, intent.getAction())){
				LogUtils.i(TAG, "收到FTP广播");
				int code=intent.getIntExtra(FTPService.FTP_KEY_CODE,FTPService.FTP_CODE_SUCCESS);
				String groupId=intent.getStringExtra(FTPService.FTP_KEY_GROUP_ID);
				String mediaId=intent.getStringExtra(FTPService.FTP_KEY_MEDIA_ID);
				String message=intent.getStringExtra(FTPService.FTP_KEY_MESSAGE);
				int progress=intent.getIntExtra(FTPService.FTP_KEY_PROGRESS,0);
				LogUtils.i(TAG, code+"-"+groupId+"-"+mediaId+"-"+message+"-"+progress);
				if(code==FTPService.FTP_CODE_UPLOADING_GROUP){//正在上传
					if(uploadingFragment!=null){
						uploadingFragment.dataNodify(groupId, MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING, progress);
					}
				}else if(code==FTPService.FTP_CODE_UPLOAD_GROUP_SUCCESS){//上传完成
					if(uploadingFragment!=null){
						uploadingFragment.dataNodify(groupId, MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED, progress);
					}
				}else if(code==FTPService.FTP_CODE_UPLOAD_GROUP_ERROR){//上传失败
					if(MediaDataActivity.this!=null){
						ToastUtils.toast(MediaDataActivity.this,"上传失败，请检查网络设置");
					}
					if(uploadingFragment!=null){
						uploadingFragment.dataNodify(groupId, MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL, progress);
					}
				}
			}
		}
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	/**刷新数据*/
	public void freshAllData(){
		
		if(unUploadFragment!=null){
			unUploadFragment.listViewRefresh();
		}
		if(completedFragment!=null){
			completedFragment.listViewRefresh();
		}
		if(uploadingFragment!=null){
			uploadingFragment.listViewRefresh();
		}
	}

	@Override
	public void pageChange(int index) {
		mRightTv.setSelected(false);
		ItemEntity entity=null;
		currentPage=index;
		if(currentPage==0){
			filterList.clear();
			entity=new ItemEntity();
			entity.setTitle("图片");
			entity.setValue(MonitorMediaGroup.TYPE_PHOTO);
			filterList.add(entity);
			entity=new ItemEntity();
			entity.setTitle("视频");
			entity.setValue(MonitorMediaGroup.TYPE_VIDEO);
			filterList.add(entity);
		}else if(currentPage==1){
			filterList.clear();
			entity=new ItemEntity();
			entity.setTitle("全部");
			entity.setValue("");
			filterList.add(entity);
			
			entity=new ItemEntity();
			entity.setTitle("图片");
			entity.setValue(MonitorMediaGroup.TYPE_PHOTO);
			filterList.add(entity);
			entity=new ItemEntity();
			entity.setTitle("视频");
			entity.setValue(MonitorMediaGroup.TYPE_VIDEO);
			filterList.add(entity);
		}else if(currentPage==2){
			filterList.clear();
			entity=new ItemEntity();
			entity.setTitle("图片");
			entity.setValue(MonitorMediaGroup.TYPE_PHOTO);
			filterList.add(entity);
			entity=new ItemEntity();
			entity.setTitle("视频");
			entity.setValue(MonitorMediaGroup.TYPE_VIDEO);
			filterList.add(entity);
		}
		
		BaseListFragment<Object> f=(BaseListFragment<Object>) dataList.get(currentPage);
		String mediaType=f.getMediaType();
		//ToastUtils.toast(getApplicationContext(), mediaType);
		setTitleType(mediaType);
	}
	/**切换页*/
	public void changeFragmentPager(int position){
		mViewPager.setCurrentItem(position, true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//重新刷新一次页面
		freshAllData();
	}

	
}
