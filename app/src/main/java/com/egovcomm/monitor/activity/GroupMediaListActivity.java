/**
 * 
 */
package com.egovcomm.monitor.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.adapter.EBBaseAdapter;
import com.egovcomm.monitor.adapter.GroupListAdapter;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.model.ItemEntity;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.model.RspGroupList;
import com.egovcomm.monitor.model.RspMedia;
import com.egovcomm.monitor.model.RspMediaGroup;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.egovcomm.monitor.view.ListPopupWindow;
import com.egovcomm.monitor.view.ListPopupWindow.OnPopupListClickLstener;

/**
 * 
 * 
 * @author mengjk
 *
 *         2015年7月3日
 */
public class GroupMediaListActivity extends BaseListActivity<RspMediaGroup> {

	private ListPopupWindow popupWindow;
	private boolean isPopWindowShow = false;
	List<ItemEntity> filterList = new ArrayList<ItemEntity>();
	protected String mediaType = MonitorMediaGroup.TYPE_PHOTO;// 全部数据类型

	// 初始化view
	@Override
	public void initView() {
		LogUtils.i(tag, "回到子类初始化");
		mListViewPulltorefreshLayout.setPull2RefreshEnable(true);
		mSearchBar.setVisibility(View.GONE);
		mRightIv.setImageResource(R.drawable.bar_search);
		initData();
		showLoading(true);
		requestData();
	}

	private void initData() {
		mTitle.setText("我的数据");
		mRightTv.setText("图片");
		ItemEntity entity = null;
		filterList.clear();
//		entity = new ItemEntity();
//		entity.setTitle("全部");
//		entity.setValue("");
//		filterList.add(entity);
		entity = new ItemEntity();
		entity.setTitle("图片");
		entity.setValue(MonitorMediaGroup.TYPE_PHOTO);
		filterList.add(entity);
		entity = new ItemEntity();
		entity.setTitle("视频");
		entity.setValue(MonitorMediaGroup.TYPE_VIDEO);
		filterList.add(entity);
	}

	@Override
	public void dataBack(int id, Object obj) {
		LogUtils.i(tag, "请求数据，返回了!");
//		List<MonitorMediaGroup> list = new ArrayList<MonitorMediaGroup>();
		RspGroupList group = null;
		List<RspMediaGroup> list=new ArrayList<RspMediaGroup>();
		switch (id) {
		case RequestService.ID_GETPHOTOMEDIA:
			group = (RspGroupList) obj;
			if (group != null && group.getData() != null
					&& group.getData().getData() != null) {
				List<RspMediaGroup> listGroup = group.getData().getData();
				for(RspMediaGroup g:listGroup){
					g.setThumbnailPath(FileUtils.getAppStorageThumbnailDirectoryPath()+File.separator+g.getId()+".jpg");//用服务器回来的ID做缩略图
				}
				list.addAll(listGroup);

			}
			break;
		case RequestService.ID_GETVIDEOMEDIA:
			group = (RspGroupList) obj;
			if (group != null && group.getData() != null
					&& group.getData().getData() != null) {
				List<RspMediaGroup> listGroup = group.getData().getData();
				for(RspMediaGroup g:listGroup){
					g.setThumbnailPath(FileUtils.getAppStorageThumbnailDirectoryPath()+File.separator+g.getId()+".jpg");//用服务器回来的ID做缩略图
				}
				list.addAll(listGroup);
			}
			break;

		default:
			break;
		}

		super.loadListView(list);

	}


	// 刷新
	@Override
	public void listViewRefresh() {
		pageNow=1;
		operate=0;
		requestData();
	}

	// 加载更多
	@Override
	public void listViewLoadMore() {
		requestData();
	}

	private void requestData() {
//		ToastUtils.toast(GroupMediaListActivity.this,"搜索"+key);
		String data="{\"createAddr\":\""+key+"\",\"remark\":\""+key+"\"}";
		//领导的数据，不传ID，返回所有
		if (TextUtils.equals(mediaType, MonitorMediaGroup.TYPE_PHOTO)) {
			mEBikeRequestService.getPhotoMedia("",data, pageNow, pageSize);
		} else if (TextUtils.equals(mediaType, MonitorMediaGroup.TYPE_VIDEO)) {
			mEBikeRequestService.getVideoMedia("",data, pageNow, pageSize);
		} 
//		else {
//			mEBikeRequestService.getPhotoMedia(SPUtils.getUser(this)
//					.getUserID(), pageNow, pageSize);
//			mEBikeRequestService.getVideoMedia(SPUtils.getUser(this)
//					.getUserID(), pageNow, pageSize);
//		}
	}

	// 关键字搜索
	@Override
	public List<RspMediaGroup> onSearch(String key) {
		return dataList;
	}

	// 适配器
	@Override
	public EBBaseAdapter<RspMediaGroup> initAdapter() {
		return new GroupListAdapter(this);
	}

	// item点击
	@Override
	public void onListViewItemClick(RspMediaGroup item, int position) {
		MonitorMediaGroupUpload uploadGroup=new MonitorMediaGroupUpload();
		item.setMediaType(mediaType);
		uploadGroup.setMediaGroup(item);
		uploadGroup.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_SERVER_DATA);
		
		List<MonitorMedia> mediaList=new ArrayList<MonitorMedia>();
		List<RspMedia> rspList=item.getMediaFiles();
		//把当前id替换成mediaId
		for(RspMedia media:rspList){
			media.setMediaType(mediaType);
			media.setGroupUploadId(item.getId());//用serverGroup当做groupid
			media.setId(media.getMediaId());
			media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_SERVER_DATA);
		}
		mediaList.addAll(rspList);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uploadGroup", uploadGroup);
		map.put("mediaList", mediaList);
		openActivity(MediaListActivity.class, map, false);
	}

	/** 用来通知fragment数据请求失败，子类，如果需要监听可以复写此方法 */
	@Override
	public void onFailRequest(int id,Object obj) {
		// TODO Auto-generated method stub

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
			showHideSearchBar();
			break;

		default:
			break;
		}
	}


	/** 弹出框 */
	private void showHidePop(boolean show) {
		this.isPopWindowShow = show;
		if (!this.isPopWindowShow) {
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			mRightTv.setSelected(false);
		} else {
			mRightTv.setSelected(true);
			popupWindow = new ListPopupWindow(this);
			popupWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					isPopWindowShow = false;
				}
			});
			popupWindow.setListClickListener(new OnPopupListClickLstener() {

				@Override
				public void onPopupListClick(ItemEntity entity) {
					// ToastUtils.toast(getApplicationContext(),
					// entity.getTitle());
					// ToastUtils.toast(getApplicationContext(),
					// "pop"+entity.getValue());
					mRightTv.setText(entity.getTitle());
					mediaType = entity.getValue();
					showLoading(true);
					listViewRefresh();

				}
			});
			popupWindow.fresh(filterList);
			popupWindow.showAsDropDown(mTopBar);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}
}
