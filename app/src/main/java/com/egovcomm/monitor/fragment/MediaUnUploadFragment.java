/**
 * 
 */
package com.egovcomm.monitor.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.activity.MediaDataActivity;
import com.egovcomm.monitor.activity.MediaModifyActivity;
import com.egovcomm.monitor.activity.PhotoShowActivity;
import com.egovcomm.monitor.activity.VideoPlayActivity;
import com.egovcomm.monitor.activity.VideoRecordActivity;
import com.egovcomm.monitor.adapter.EBBaseAdapter;
import com.egovcomm.monitor.adapter.MediaListAdapter;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.model.ReqUploadMediaData;
import com.egovcomm.monitor.model.RspGroupList;
import com.egovcomm.monitor.model.RspMedia;
import com.egovcomm.monitor.model.RspMediaGroup;
import com.egovcomm.monitor.model.RspUploadMedia;
import com.egovcomm.monitor.model.User;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.JsonUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.TimeUtils;
import com.egovcomm.monitor.utils.ToastUtils;

/**
 * @author mengjk
 *
 *         2015年7月3日
 */
public class MediaUnUploadFragment extends BaseListFragment<MonitorMedia> implements AdapterView.OnItemLongClickListener{
	private AlertDialog dialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		mListViewPulltorefreshLayout.setPull2RefreshEnable(false);
		mSearchBar.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		mListView.setOnItemLongClickListener(this);
		listViewRefresh();
		return view;
	}

	@Override
	public void dataBack(int id, Object obj) {//选择的数据
		((BaseActivity)getActivity()).hideLoading();
		List<MonitorMedia> mediaList = new ArrayList<MonitorMedia>();
		for (MonitorMedia media : dataList) {
			if (media.getCheck() == 1) {
				mediaList.add(media);
			}
		}
		switch (id){
			case RequestService.ID_GROUPLIST://组数据
				RspGroupList rspGroupList= (RspGroupList) obj;
				//把回来的数据插入到数据库，重复的就不会再添加
				if(rspGroupList!=null&&rspGroupList.getData()!=null&&rspGroupList.getData().getData()!=null&&rspGroupList.getData().getData().size()>0){
					List<RspMediaGroup> list=rspGroupList.getData().getData();
					for(MonitorMediaGroup group:list){
						DBHelper.getInstance(getActivity()).insertMonitorMediaGroup(group);
					}
					showGroupList(mediaList);
				}else{//为空，或者没有组，则直接创建分组
					createMediaGroup(mediaList,null);
				}
				break;
			case RequestService.ID_GROUPCREATE:
				RspUploadMedia rsp= (RspUploadMedia) obj;
				if(rsp!=null&&rsp.getData()!=null){
					// 创建分组
					MonitorMediaGroup g =rsp.getData();
					DBHelper.getInstance(getActivity()).insertMonitorMediaGroup(g);
					confirmMediaUpload(g, mediaList);
				}else{
					ToastUtils.toast(getActivity(),"创建分组失败!");
				}
				break;
			default:
				break;
		}

	}

	/**请求错误会调用这个方法*/
	protected void requestError(int id,Object obj){
		List<MonitorMedia> mediaList = new ArrayList<MonitorMedia>();
		for (MonitorMedia media : dataList) {
			if (media.getCheck() == 1) {
				mediaList.add(media);
			}
		}
		switch (id){
			case RequestService.ID_GROUPLIST://请求分组数据，错误，则会直接创建组
				createMediaGroup(mediaList,null);
				break;
			case RequestService.ID_GROUPCREATE://请求创建组失败，则终止
				ToastUtils.toast(getActivity(),"创建分组失败!");
				break;
			default:
				break;
		}
	}

	// 刷新
	@Override
	public void listViewRefresh() {
		if (getActivity() != null) {
			List<MonitorMedia> list = DBHelper.getInstance(getActivity()).listUnUploadMonitorMediaByUserId(
					SPUtils.getUser(getActivity()).getUserID(), mediaType);
			super.loadListView(list);
			((BaseActivity) getActivity()).hideLoading();
		} else {
			LogUtils.e(tag, "activity为空了!#######");
		}
	}

	// 加载更多
	@Override
	public void listViewLoadMore() {

	}

	// 关键字搜索
	@Override
	public List<MonitorMedia> onSearch(String key) {

		return dataList;
	}

	// 适配器
	@Override
	public EBBaseAdapter<MonitorMedia> initAdapter() {
		return new MediaListAdapter(getActivity());
	}

	@Override
	public void onListViewItemClick(final MonitorMedia item, int position) {
		if(mediaOperateView.getVisibility()==View.VISIBLE){//可选 就要选中
			if(item.getCheck()==1){
				item.setCheck(0);
			}else{
				item.setCheck(1);
			}
			mAdapter.notifyDataSetChanged();
		}else{
			try {
				if(!FileUtils.isFileExit(item.getPath())){//文件不存在提示
					new Builder(getActivity()).setTitle("应用在存储卡中检测不到此源文件，是否删除此记录？").setCancelable(true).setPositiveButton("删除", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 删除数据库
							DBHelper.getInstance(getActivity()).deleteMonitorMedia(item.getId());
							listViewRefresh();

						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create().show();
				}else{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("media", item);
					if (TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO + "", item.getMediaType())) {// 图片
						((BaseActivity)getActivity()).openActivity(PhotoShowActivity.class, map, false);
					} else {// 视频
						((BaseActivity)getActivity()).openActivity(VideoPlayActivity.class, map, false);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onFailRequest(int id,Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		List<MonitorMedia> list = new ArrayList<MonitorMedia>();
		for (MonitorMedia media : dataList) {
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
		case R.id.view_operate_upload://要上传，请求组数据
			((BaseActivity)getActivity()).showLoading(true);
			mEBikeRequestService.groupList(SPUtils.getUser(getActivity()).getUserID(),mediaType,1,1000);
			break;

		default:
			break;

		}
	}

	/** 删除数据 */
	private void deletedList(final List<MonitorMedia> list) {
		if(list!=null&&list.size()<=0){
			ToastUtils.toast(getActivity(), "请选择要操作的数据!");
			return;
		}
		new Builder(getActivity()).setTitle("确定要删除吗？").setCancelable(false).setPositiveButton("删除", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除数据库
				DBHelper.getInstance(getActivity()).deleteMonitorMediaList(list);
				// 删除本地文件
				for (MonitorMedia media : list) {
					try {
						File f = new File(media.getPath());
						f.delete();
					} catch (Exception e) {
						LogUtils.e(tag, "删除文件失败");
					}
				}
				showHideBottomBar();
				listViewRefresh();

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
				mediaOperateView.setVisibility(View.VISIBLE);
				showHideCheckBox(true);
			}
		}
	}


	private void showHideCheckBox(boolean show) {
		for (MonitorMedia media : dataList) {
			if (show) {
				media.setShowCheck(1);
			} else {
				media.setShowCheck(0);
			}
			media.setCheck(0);// 设置为默认值
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.view_operate_checkbox) {// 全选
			for (MonitorMedia media : dataList) {
				if (isChecked) {
					media.setCheck(1);// 选中
				} else {
					media.setCheck(0);// 取消选中
				}
			}

			mAdapter.notifyDataSetChanged();
		}
	}

	/** 弹出组选择对话框 */
	private void showGroupList(final List<MonitorMedia> list) {
		if(list!=null&&list.size()<=0){
			ToastUtils.toast(getActivity(), "请选择要操作的数据!");
			return;
		}
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		final List<MonitorMediaGroup> groupList = DBHelper.getInstance(getActivity()).listMonitorMediaGroup(
				SPUtils.getUser(getActivity()).getUserID(),mediaType);
		if (groupList.size() > 0) {
			for (MonitorMediaGroup group : groupList) {
				map = new HashMap<String, Object>();
				map.put("name", group.getRemark());
				map.put("time", group.getCreateTime());
				map.put("location", group.getCreateAddr());
				dataList.add(map);
			}

			ListView listView = new ListView(getActivity());
			listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataList, R.layout.item_dialog_group,
					new String[] { "name", "time", "location" }, new int[] { R.id.item_dialog_name,
							R.id.item_dialog_time, R.id.item_dialog_location });
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (dialog != null) {
						dialog.cancel();
					}
					//ToastUtils.toast(getActivity(), "点击的是第" + position + "个");
					MonitorMediaGroup group = groupList.get(position);
					LogUtils.i(tag, group.toString());
					// 上传
					uploadMediaGroup(group, list);
				}
			});

			dialog = new Builder(getActivity()).setTitle("选择已有分组或新建一个分组来完成数据上传").setView(listView)
					.setCancelable(false).setPositiveButton("新建分组", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							createMediaGroup(list,groupList);
						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).create();
			dialog.show();

		} else {
			createMediaGroup(list,groupList);
		}

	}

	/** 新建分组 */
	private void createMediaGroup(final List<MonitorMedia> list, final List<MonitorMediaGroup> groupList) {
		final EditText et = new EditText(getActivity());
		final TextView tip=new TextView(getActivity());
		tip.setTextColor(getResources().getColor(R.color.red));
		tip.setText("提示");
		tip.setVisibility(View.INVISIBLE);
		final LinearLayout layout=new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				LogUtils.i(tag,"afterTextChanged"+s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				LogUtils.i(tag,"afterTextChanged"+s.toString());
				tip.setVisibility(View.INVISIBLE);
				if(groupList!=null){
					for(MonitorMediaGroup group:groupList){
						if(TextUtils.equals(group.getRemark(),s.toString())){//有相同的
							tip.setVisibility(View.VISIBLE);
							tip.setText("此分组备注已存在，请更换！");
							break;
						}
					}
				}


			}
		});
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		et.setHint("请输入分组备注");

		layout.addView(tip);
		layout.addView(et);

		Builder builder = new Builder(getActivity());

		builder.setTitle("创建上传数据分组");
		// builder.setMessage("请输入分组名称，按确定完成创建!");
		builder.setView(layout);
		builder.setCancelable(false);
		builder.setPositiveButton("创建", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

				if(groupList!=null){
					for(MonitorMediaGroup group:groupList){
						if(TextUtils.equals(group.getRemark(),et.getText().toString())){//有相同的
							ToastUtils.toast(getActivity(),"此分组备注已存在，请更换！");
							createMediaGroup(list,groupList);
							return;
						}
					}
				}
				LogUtils.i(tag,"创建分组！");
				String data = "";
				ReqUploadMediaData req = new ReqUploadMediaData();
				User user = SPUtils.getUser(getActivity());
				req.setId("");
				req.setUserId(user.getUserID());
				req.setUserName(user.getUserName());
				req.setOrgId(user.getOrgID());
				req.setOrgName(user.getOrgName());
				req.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
				req.setCreateAddr(BaseApplication.address);
				req.setLongitude(BaseApplication.longitude+"");
				req.setLatitude(BaseApplication.latitude+"");
				req.setMediaType(mediaType);
				req.setRemark(et.getText().toString()+"");
				List<RspMedia> fileList = new ArrayList<RspMedia>();
				req.setFileList(fileList);
				data = JsonUtils.objectToJson(req, ReqUploadMediaData.class);
				LogUtils.i(tag, "创建分组的数据是：" + data);
				((BaseActivity) getActivity()).showLoading(true);
				mEBikeRequestService.groupCreate(data);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				((BaseActivity) getActivity()).hideLoading();
			}
		});
		builder.create().show();

	}

	/** 新建分组 */
	private void confirmMediaUpload(final MonitorMediaGroup group, final List<MonitorMedia> list) {
		Builder builder = new Builder(getActivity());

		builder.setTitle("提示");
		builder.setMessage("分组创建完成，是否立刻上传!");
		builder.setCancelable(false);
		builder.setPositiveButton("是", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				// 上传
				uploadMediaGroup(group, list);
			}
		});
		builder.setNegativeButton("否", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				((BaseActivity) getActivity()).hideLoading();
			}
		});
		builder.create().show();

	}

	/** 上传分组数据 */
	private void uploadMediaGroup(MonitorMediaGroup group, List<MonitorMedia> list) {
		((BaseActivity)getActivity()).hideLoading();
		// 存储组
		MonitorMediaGroupUpload uploadGroup = new MonitorMediaGroupUpload();
		uploadGroup.setId(UUID.randomUUID().toString());
		uploadGroup.setMediaGroup(group);
		uploadGroup.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING + "");
//		uploadGroup.setThumbnailPath(list.get(0).getThumbnailPath());
		FileUtils.saveMediaGroupThumbnail(getActivity(),list.get(0).getThumbnailPath(),uploadGroup.getId());
		uploadGroup.setRemoteDirectory(TimeUtils.getFormatNowTime("yyy-MM-dd"));//年月日为文件夹
		DBHelper.getInstance(getActivity()).insertMonitorMediaGroupUpload(uploadGroup);
		// 更改数据为在上传，并设置上传组
		for (MonitorMedia media : list) {
			media.setGroupUploadId(uploadGroup.getId());
			media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING + "");
		}
		DBHelper.getInstance(getActivity()).updateMonitorMediaList(list);
		showHideBottomBar();
		((MediaDataActivity) getActivity()).freshAllData();// 重新刷新所有数据
		((MediaDataActivity) getActivity()).changeFragmentPager(1);//切换到上传页
		// 调用FTP上传
		 FTPMediaUtil.mediaUpload(getActivity(), uploadGroup);
	}

	/** 数据发生改变 */
	public void dataNodify() {

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//		ToastUtils.toast(getActivity(),"onItemLongClick");
		if (dataList != null && position < dataList.size()) {
			MonitorMedia media=dataList.get(position);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("media", media);
			((BaseActivity)getActivity()).openActivity(MediaModifyActivity.class,map,false);
		}
		return true;
	}
}
