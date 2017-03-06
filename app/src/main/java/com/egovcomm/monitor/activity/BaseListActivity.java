/**
 * 
 */
package com.egovcomm.monitor.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.adapter.EBBaseAdapter;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.model.ItemEntity;
import com.egovcomm.monitor.utils.CommonViewUtils;
import com.egovcomm.monitor.utils.ErrorUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.ErrorUtils.ErrorListener;
import com.egovcomm.monitor.utils.ErrorUtils.SuccessListener;
import com.egovcomm.monitor.view.push2refreshlistview.PullToRefreshLayout;
import com.egovcomm.monitor.view.push2refreshlistview.PullToRefreshLayout.OnRefreshListener;
import com.egovcomm.monitor.view.push2refreshlistview.PullableListView;

/**
 * 页面基类
 * 
 * @param <T>数据实体类
 * @author mengjk
 *
 *         2015年5月15日
 */
public abstract class BaseListActivity<T> extends BaseActivity implements OnClickListener,OnCheckedChangeListener  {
	// 列表
	protected  static final int SEARCH_MAX_PAGE_SIZE=200;//搜索的size
	protected  static final int MAX_PAGE_SIZE=10;//普通的size
	protected PullableListView mListView;
	protected EBBaseAdapter<T> mAdapter;
	protected PullToRefreshListener mListViewPullTolistener;
	protected List<T> dataList = new ArrayList<T>();
	protected int operate = 0;// 0代表刷新，1代表加载更多
	protected int pageNow = 1;
	protected int pageSize = 20;
	protected EditText mSearchEt;
	protected View mSearchBar;
	protected PullToRefreshLayout mListViewPulltorefreshLayout;
	protected boolean isCanPullUp=true;//是否可以上拉
	/** 关键字 */
	protected String key = "";
	/** 大类 */
	protected String category = "";
	/** 等级数 */
	protected String num = "0";
	protected View mNoMoreData;
	
	protected ImageView mBack;
	protected TextView mTitle;
	protected TextView mRightTv;
	protected ImageView mRightIv;
	
	protected View mediaOperateView;
	protected TextView btnDeleted;
	protected TextView btnUpload;
	protected TextView btnCancel;
	protected CheckBox cbAllSelected;
	protected View mTopBar;
	protected ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initBaseListView();
		initView();
	}

	/** 初始化BaseList View */
	protected void initBaseListView() {
		LogUtils.i(tag, "父类初始化");
		//showLoading(true);
		mProgressBar=(ProgressBar) findViewById(R.id.view_progress);
		mTopBar=findViewById(R.id.nav_top_bar);
		mBack=(ImageView) findViewById(R.id.top_bar_left);
		mTitle=(TextView) findViewById(R.id.top_bar_title);
		mRightTv=(TextView) findViewById(R.id.top_bar_right_text);
		mRightIv=(ImageView) findViewById(R.id.top_bar_right_icon);
		mBack.setOnClickListener(this);
		mRightTv.setOnClickListener(this);
		mRightIv.setOnClickListener(this);
		
		mediaOperateView=findViewById(R.id.view_media_operate);
		cbAllSelected=(CheckBox) findViewById(R.id.view_operate_checkbox);
		cbAllSelected.setOnCheckedChangeListener(this);
		btnDeleted=(TextView)findViewById(R.id.view_operate_deleted);
		btnUpload=(TextView) findViewById(R.id.view_operate_upload);
		btnCancel=(TextView) findViewById(R.id.view_operate_cancel);
		btnDeleted.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		mNoMoreData = findViewById(R.id.view_no_more_data);
		mSearchBar = findViewById(R.id.view_search_bar);
		mSearchEt = (EditText) findViewById(R.id.view_search_et);
		mSearchEt.setOnEditorActionListener(new OnEditListener());
		mListView = (PullableListView) findViewById(R.id.content_view);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (dataList != null && position < dataList.size()) {
					onListViewItemClick(dataList.get(position), position);
				}
			}
		});
		mListViewPullTolistener = new PullToRefreshListener();
		mListViewPulltorefreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
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
	protected void showHideSearchBar() {
		if (mSearchBar.getVisibility() == View.VISIBLE) {
			mSearchEt.setText("");
			key="";
			listViewRefresh();
			mSearchBar.setVisibility(View.GONE);
		} else {
			mSearchBar.setVisibility(View.VISIBLE);
			mSearchEt.setFocusable(true);
		}
//		mSearchEt.setFocusable(true);
//		// 动画
//		int height = 0;
//		if (mSearchBar.getVisibility() == View.VISIBLE) {
//			height = (int) -mSearchBar.getHeight();
//		} else {
//			height = (int) mSearchBar.getHeight();
//		}
//		mListViewPulltorefreshLayout.setTop(mListViewPulltorefreshLayout.getTop() + height);
//		mListViewPulltorefreshLayout.yTranslate += height;
	}

	/** EditText监听 */
	class OnEditListener implements OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

			if (actionId == EditorInfo.IME_ACTION_SEARCH
					|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
				key = v.getText().toString();
//				v.setText("");
				operate = 0;
				pageNow = 1;
				showLoading(true);
				listViewRefresh();
				CommonViewUtils.hideSystemKeyBoard(BaseListActivity.this, mSearchEt);
				return true;
			}
			return false;
		}

	}
	

	@Override
	public void update(int id, Object obj) {
		ErrorUtils.handle(BaseListActivity.this, id, obj, new SuccessListener() {

			@Override
			public void successCompleted(int id, Object obj) {
				dateUpdate(id, obj);
			}
		},new ErrorListener() {
			

			@Override
			public void errorCompleted(int id,Object obj) {
				hideLoading();
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
		hideLoading();
		mListViewPullTolistener.finish(PullToRefreshLayout.SUCCEED);
	}

	/** 搜索 */
	public abstract void initView();
	
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
		if (operate == 0&&dataList!=null) {// 刷新
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
		if(dataList!=null&&list!=null){
			dataList.addAll(list);
		}


		if (dataList==null||dataList.size() <= 0) {
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


	public boolean isCanPullUp() {
		return isCanPullUp;
	}

	public void setCanPullUp(boolean isCanPullUp) {
		this.isCanPullUp = isCanPullUp;
	}
}
