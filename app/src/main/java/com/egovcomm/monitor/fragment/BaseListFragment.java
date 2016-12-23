/**
 * 
 */
package com.egovcomm.monitor.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.adapter.EBBaseAdapter;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseFragment;
import com.egovcomm.monitor.model.ItemEntity;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.utils.CommonViewUtils;
import com.egovcomm.monitor.utils.ErrorUtils;
import com.egovcomm.monitor.utils.ErrorUtils.ErrorListener;
import com.egovcomm.monitor.utils.ErrorUtils.SuccessListener;
import com.egovcomm.monitor.view.ListPopupWindow;
import com.egovcomm.monitor.view.push2refreshlistview.PullToRefreshLayout;
import com.egovcomm.monitor.view.push2refreshlistview.PullToRefreshLayout.OnRefreshListener;
import com.egovcomm.monitor.view.push2refreshlistview.PullableListView;

/**
 * 页面基类(包含webview跟listview)
 * 
 * @param <T>数据实体类
 * @author mengjk
 *
 *         2015年5月15日
 */
public abstract class BaseListFragment<T> extends BaseFragment implements OnClickListener,OnCheckedChangeListener {

	// 列表
	protected static final int SEARCH_MAX_PAGE_SIZE = 200;// 搜索的size
	protected static final int MAX_PAGE_SIZE = 10;// 普通的size
	protected PullableListView mListView;
	protected EBBaseAdapter<T> mAdapter;
	protected PullToRefreshListener mListViewPullTolistener;
	protected List<T> dataList = new ArrayList<T>();
	protected int operate = 0;// 0代表刷新，1代表加载更多
	protected int pageNow = 1;//从1开始
	protected int pageSize = 20;//默认20
	protected EditText mSearchEt;
	protected View mSearchBar;
	protected PullToRefreshLayout mListViewPulltorefreshLayout;
	protected boolean isCanPullUp = true;// 是否可以上拉
	/** 关键字 */
	protected String key = "";
	/** 大类 */
	protected String category = "";
	/** 等级数 */
	protected String num = "0";
	protected View mNoMoreData;
	private boolean isSearchBarVisble = false;
	private ListPopupWindow mPopupWindow;

	protected View mediaOperateView;
	protected Button btnDeleted;
	protected Button btnUpload;
	protected Button btnCancel;
	protected CheckBox cbAllSelected;
	
	protected String mediaType="";//数据类型,默认为全部
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fresh_list, null);
		initBaseListView(view);
		return view;
	}
	/**设置数据类型*/
	public void setMediaType(String mediaType){
		this.mediaType=mediaType;
	}
	
	/**设置数据类型*/
	public String getMediaType(){
		return this.mediaType;
	}

	/** 初始化BaseList View */
	protected void initBaseListView(View view) {
		((BaseActivity)getActivity()).showLoading(true);
		mediaOperateView=view.findViewById(R.id.view_media_operate);
		cbAllSelected=(CheckBox) view.findViewById(R.id.view_operate_checkbox);
		cbAllSelected.setOnCheckedChangeListener(this);
		btnDeleted=(Button) view.findViewById(R.id.view_operate_deleted);
		btnUpload=(Button) view.findViewById(R.id.view_operate_upload);
		btnCancel=(Button) view.findViewById(R.id.view_operate_cancel);
		btnDeleted.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		mNoMoreData = view.findViewById(R.id.view_no_more_data);
		mSearchBar =  view.findViewById(R.id.view_search_bar);
		mSearchEt = (EditText)view.findViewById(R.id.view_search_et);
		mSearchEt.setOnEditorActionListener(new OnEditListener());
		mListView = (PullableListView)view.findViewById(R.id.content_view);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (dataList != null && position < dataList.size()) {
					onListViewItemClick(dataList.get(position), position);
				}
			}
		});
		mListViewPullTolistener = new PullToRefreshListener();
		mListViewPulltorefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
		if(mListViewPulltorefreshLayout!=null){
			mListViewPulltorefreshLayout.setOnRefreshListener(mListViewPullTolistener);
		}
		mAdapter = initAdapter();
		mListView.setAdapter(mAdapter);
	}
	

	/** 设置list列表回来的pageSize */
	protected void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// 显示或者隐藏popupwindow
	protected void showPopupWindow(List<ItemEntity> list, boolean isShowSearch) {
		/*mNavRightButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rotate_arrow_down, 0, 0, 0);
		if (mPopupWindow == null) {
			mPopupWindow = new ListPopupWindow(this, 1, list, isShowSearch);
			mPopupWindow.setListClickListener(this);
			mPopupWindow.setEditorListener(new OnEditListener());
		}
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				mNavRightButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rotate_arrow, 0, 0, 0);
			}
		});
		mPopupWindow.show(mNavBack);*/
	}

	// 显示或者隐藏搜索框
	protected void showSearchBar() {
		mSearchEt.setFocusable(true);
		// 动画
		int height = 0;
		if (isSearchBarVisble) {
			height = (int) -mSearchBar.getHeight();
		} else {
			height = (int) mSearchBar.getHeight();
		}
		mListViewPulltorefreshLayout.setTop(mListViewPulltorefreshLayout.getTop() + height);
		mListViewPulltorefreshLayout.yTranslate += height;
		isSearchBarVisble = !isSearchBarVisble;
	}

	/** EditText监听 */
	class OnEditListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

			if (actionId == EditorInfo.IME_ACTION_SEARCH
					|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
				if (mPopupWindow != null) {
					mPopupWindow.dismiss();
				}
				key = v.getText().toString();
				v.setText("");
				// dataList=onSearch(v.getText().toString());
				// mAdapter.notifyDataSetChanged();
				mListView.canPull2LoadMore = true;
				operate = 0;
				pageNow = 1;
				if(!TextUtils.isEmpty(key)){
					setPageSize(SEARCH_MAX_PAGE_SIZE);//搜索的时候就把pagesize设置为100
				}
				doSearche(v);
				CommonViewUtils.hideSystemKeyBoard(getActivity(), mSearchEt);
				return true;
			}
			return false;
		}

	}
	
	/**执行搜索*/
	protected void doSearche(TextView editText){
		key=editText.getText().toString();
		editText.setText("");
		listViewRefresh();
	}

	@Override
	public void update(int id, Object obj) {
		ErrorUtils.handle(getActivity(), id, obj, new SuccessListener() {

			@Override
			public void successCompleted(int id, Object obj) {
				dateUpdate(id, obj);
			}
		},new ErrorListener() {
			

			@Override
			public void errorCompleted(int id,Object obj) {
				((BaseActivity)getActivity()).hideLoading();
				mListViewPullTolistener.finish(PullToRefreshLayout.FAIL);
				onFailRequest(id,obj);
			}
		});
	}
	
	/**请求错误会调用这个方法*/
	public abstract void onFailRequest(int id,Object obj);
	
	
	@Override
	public void dateUpdate(int id, Object obj) {
		dataBack(id, obj);
		((BaseActivity)getActivity()).hideLoading();
		mListViewPullTolistener.finish(PullToRefreshLayout.SUCCEED);
	}

	/** ListView点击 */
	public abstract void onListViewItemClick(T item, int position);

	/** 搜索 */
	public abstract List<T> onSearch(String key);

	/** 搜索 */
	public abstract EBBaseAdapter<T> initAdapter();

	/** 数据返回更新 */
	public abstract void dataBack(int id, Object obj);

	/** 刷新操作 */
	public abstract void listViewRefresh();

	/** 加载更多操作 */
	public abstract void listViewLoadMore();

	/** 加载listView */
	protected void loadListView(List<T> list) {
		if (operate == 0) {// 刷新
			dataList.clear();
		}
		if (list == null || list.size() < pageSize) {// 没有更多数据了
			mListView.isNoMoreData = true;
		} else {
			mListView.isNoMoreData = false;
		}
		if(pageSize==SEARCH_MAX_PAGE_SIZE){
			mListView.canPull2LoadMore = false;
			key="";
			setPageSize(MAX_PAGE_SIZE);
		}
		dataList.addAll(list);

		if (dataList.size() <= 0) {
			mNoMoreData.setVisibility(View.VISIBLE);
		} else {
			mNoMoreData.setVisibility(View.GONE);
		}
		if(!isCanPullUp){
			mListView.canPull2LoadMore = false;
		}
		mAdapter.setData(dataList);
		mAdapter.notifyDataSetChanged();
	}

	/** 列表操作监听器 */
	class PullToRefreshListener implements OnRefreshListener {
		PullToRefreshLayout pullToRefreshLayout;

		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			this.pullToRefreshLayout = pullToRefreshLayout;
			mListView.canPull2LoadMore = true;
			operate = 0;
			pageNow = 1;
			listViewRefresh();
		}

		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
			this.pullToRefreshLayout = pullToRefreshLayout;
			// 加载操作
			operate = 1;
			pageNow++;
			listViewLoadMore();
		}

		public void finish(int state) {
			if (pullToRefreshLayout != null) {
				if (operate == 0) {
					pullToRefreshLayout.refreshFinish(state);
				} else {
					pullToRefreshLayout.loadmoreFinish(state);
				}
			}
		}

		/* (non-Javadoc)
		 * @see com.dawning.gridview.view.push2refreshlistview.PullToRefreshLayout.OnRefreshListener#onSlide(float)
		 */
		@Override
		public void onSlide(float distance) {
			// TODO Auto-generated method stub
			
		}
	}

	// popup listview点击
	public void onPopupListClick(ItemEntity entity,EditText searchEdit) {
		key=searchEdit.getText().toString();
		category = entity.getValue();
		num = entity.getContent();
		mListView.canPull2LoadMore = true;
		operate = 0;
		pageNow = 1;
//		if(mNavRightButton!=null){
//			mNavRightButton.setText(entity.getTitle());
//		}
		listViewRefresh();
	}

	public boolean isCanPullUp() {
		return isCanPullUp;
	}

	public void setCanPullUp(boolean isCanPullUp) {
		this.isCanPullUp = isCanPullUp;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mediaOperateView!=null){
			mediaOperateView.setVisibility(View.GONE);
		}
	}
}
