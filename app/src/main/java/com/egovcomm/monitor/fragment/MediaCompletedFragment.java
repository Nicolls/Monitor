/**
 * 
 */
package com.egovcomm.monitor.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.activity.MediaDataActivity;
import com.egovcomm.monitor.activity.MediaListActivity;
import com.egovcomm.monitor.adapter.EBBaseAdapter;
import com.egovcomm.monitor.adapter.GroupUploadListAdapter;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.ToastUtils;

/**
 * @author mengjk
 *
 *         2015年7月3日
 */
public class MediaCompletedFragment extends
		BaseListFragment<MonitorMediaGroupUpload> {
	private AlertDialog dialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		mListViewPulltorefreshLayout.setPull2RefreshEnable(false);
		mSearchBar.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		// btnDeleted.setVisibility(View.GONE);
		listViewRefresh();
		return view;
	}

	@Override
	public void dataBack(int id, Object obj) {

	}

	// 刷新
	@Override
	public void listViewRefresh() {
		if (getActivity() != null) {
			List<MonitorMediaGroupUpload> list = DBHelper.getInstance(
					getActivity()).listMonitorMediaGroupUploadByUploadState(
					SPUtils.getUser(getActivity()).getUserID(),
					new String[] {
							MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED + "",
							MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL
									+ "",
							MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL
									+ "" });

			Iterator<MonitorMediaGroupUpload> it = list.iterator();
			while (it.hasNext()) {
				MonitorMediaGroupUpload group = it.next();
				group.setThumbnailPath(FileUtils.getAppStorageLocalThumbnailDirectoryPath()+File.separator+group.getId()+".jpg");//用组ID做缩略图
				if (!TextUtils.isEmpty(mediaType)&&!TextUtils.equals(mediaType, group.getMediaGroup()
						.getMediaType())) {
					it.remove();
				}
			}
			super.loadListView(list);
			((BaseActivity) getActivity()).hideLoading();
		} else {
			LogUtils.e(TAG, "activity为空了!#######");
		}
	}

	// 加载更多
	@Override
	public void listViewLoadMore() {

	}

	// 关键字搜索
	@Override
	public List<MonitorMediaGroupUpload> onSearch(String key) {

		return dataList;
	}

	// 适配器
	@Override
	public EBBaseAdapter<MonitorMediaGroupUpload> initAdapter() {
		return new GroupUploadListAdapter(getActivity());
	}

	@Override
	public void onListViewItemClick(MonitorMediaGroupUpload item, int position) {
		if(item.getUploadState()!=MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED&&mediaOperateView.getVisibility()==View.VISIBLE){//可选 就要选中
			if(item.getCheck()==1){
				item.setCheck(0);
			}else{
				item.setCheck(1);
			}
			mAdapter.notifyDataSetChanged();
		}else{
			List<MonitorMedia> mediaList = DBHelper.getInstance(getActivity()).listMonitorMediaByGroupUploadId(
					item.getId());
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("uploadGroup", item);
			map.put("mediaList", mediaList);
			((BaseActivity)getActivity()).openActivity(MediaListActivity.class, map, false);
		}
	}

	@Override
	public void onFailRequest(int id,Object obj) {
		
	}

	@Override
	public void onClick(View v) {
		List<MonitorMediaGroupUpload> list = new ArrayList<MonitorMediaGroupUpload>();
		for (MonitorMediaGroupUpload media : dataList) {
			if (media.getCheck() == 1) {
				list.add(media);
			}
		}
		switch (v.getId()) {

		case R.id.view_operate_cancel:

			break;
		case R.id.view_operate_deleted:
			deletedList(list);
			break;
		case R.id.view_operate_upload:
			uploadGroupList(list);
			break;

		default:
			break;

		}
	}

	/** 删除数据 */
	private void deletedList(final List<MonitorMediaGroupUpload> list) {
		if (list != null && list.size() <= 0) {
			ToastUtils.toast(getActivity(), "请选择要操作的数据!");
			return;
		}
		new Builder(getActivity()).setTitle("确定要删除吗？").setPositiveButton("删除", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//删除数据,把组跟文件都删除掉
				for (MonitorMediaGroupUpload upload : list) {
					List<MonitorMedia> mediaList = DBHelper.getInstance(getActivity()).listMonitorMediaByGroupUploadId(
							upload.getId());
					for (MonitorMedia media : mediaList) {//删除文件
						try {
							File f=new File(media.getPath());
							if(f.exists()){
								f.delete();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					DBHelper.getInstance(getActivity()).deleteMonitorMediaList(mediaList);
				}
				// 再删除组
				DBHelper.getInstance(getActivity()).deleteMonitorMediaGroupUploadList(list);
				
				// 不删除单一数据，单一数据的组值设置为空，使及重新成为未上传数据，但是把组的数据删除
				/*
				for (MonitorMediaGroupUpload upload : list) {
					List<MonitorMedia> mediaList = DBHelper.getInstance(getActivity()).listMonitorMediaByGroupUploadId(
							upload.getId());
					// 设置为未上传
					for (MonitorMedia media : mediaList) {
						media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD + "");
						media.setGroupUploadId(MonitorTable.NULL_VALUE);
					}
					DBHelper.getInstance(getActivity()).updateMonitorMediaList(mediaList);
				}
				DBHelper.getInstance(getActivity()).deleteMonitorMediaGroupUploadList(list);
				*/
				showHideBottomBar();
				((MediaDataActivity) getActivity()).freshAllData();// 重新刷新所有数据
				((MediaDataActivity) getActivity()).changeFragmentPager(0);// 切换到未上传页
			}
		}).setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create().show();
	}

	/** 显示底部栏，View.visibility */
	public void showHideBottomBar() {
		if (mediaOperateView != null) {
			if (mediaOperateView.getVisibility() == View.VISIBLE) {
				mediaOperateView.setVisibility(View.GONE);
				cbAllSelected.setChecked(false);
				showHideCheckBox(false);
			} else {
				boolean isShow=false;
				for (MonitorMediaGroupUpload group : dataList) {
					if (!TextUtils.equals(
							MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED + "",
							group.getUploadState())) {//有不是上传成功的，就显示，否则不显示
						isShow=true;
						break;
					}
				}
				if(isShow){
					mediaOperateView.setVisibility(View.VISIBLE);
					showHideCheckBox(true);
				}else{
					ToastUtils.toast(getActivity(), "没有可以操作的状态!");
				}
			}
		}
	}

	private void showHideCheckBox(boolean show) {
		for (MonitorMediaGroupUpload group : dataList) {
			if (show) {
				if (!TextUtils.equals(
						MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED + "",
						group.getUploadState())) {
					group.setShowCheck(1);
				} else {
					group.setShowCheck(0);
				}
			} else {
				group.setShowCheck(0);
			}
			group.setCheck(0);// 设置为默认值
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.view_operate_checkbox) {// 全选
			for (MonitorMediaGroupUpload media : dataList) {
				if (isChecked) {
					if(!TextUtils.equals(media.getUploadState(), MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED)){
						media.setCheck(1);// 选中
					}else{
						media.setCheck(0);// 选中
					}
				} else {
					media.setCheck(0);// 取消选中
				}
			}

			mAdapter.notifyDataSetChanged();
		}
	}

	/** 上传分组数据 */
	private void uploadGroupList(List<MonitorMediaGroupUpload> list) {
		if (list != null && list.size() <= 0) {
			ToastUtils.toast(getActivity(), "请选择要操作的数据!");
			return;
		}
		// 将组下的数据都

		// 更改数据为在上传
		for (MonitorMediaGroupUpload group : list) {
			group.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING
					+ "");

			List<MonitorMedia> mediaList = DBHelper.getInstance(getActivity())
					.listMonitorMediaByGroupUploadId(group.getId());
			// DBHelper.getInstance(getActivity()).deleteMonitorMediaList(mediaList);//先删除
			for (MonitorMedia media : mediaList) {
				media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING
						+ "");
				// media.setId(UUID.randomUUID().toString());//失败的时候已经重新设置过了
			}
			DBHelper.getInstance(getActivity()).updateMonitorMediaList(
					mediaList);
			DBHelper.getInstance(getActivity()).updateMonitorMediaGroupUpload(
					group);

			// DBHelper.getInstance(getActivity()).insertMonitorMediaList(mediaList);//生成UUID后再添加回来
			// 调用FTP上传
			FTPMediaUtil.mediaUpload(getActivity(), group);
		}
		showHideBottomBar();
		((MediaDataActivity) getActivity()).freshAllData();// 重新刷新所有数据
		((MediaDataActivity) getActivity()).changeFragmentPager(1);// 切换到上传页

	}

	/** 数据发生改变 */
	public void dataNodify() {

	}

}
