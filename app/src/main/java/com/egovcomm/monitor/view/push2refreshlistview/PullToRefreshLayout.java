package com.egovcomm.monitor.view.push2refreshlistview;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egovcomm.monitor.R;

/**
 * 用于刷新加载view的包装
 * 
 * @author mengjk
 */
public class PullToRefreshLayout extends RelativeLayout {
	public static final String TAG = "PullToRefreshLayout";
	// 初始状态
	public static final int INIT = 0;
	// 释放刷新
	public static final int RELEASE_TO_REFRESH = 1;
	// 正在刷新
	public static final int REFRESHING = 2;
	// 释放加载
	public static final int RELEASE_TO_LOAD = 3;
	// 正在加载
	public static final int LOADING = 4;
	// 操作完毕
	public static final int DONE = 5;
	// 刷新成功
	public static final int SUCCEED = 6;
	// 刷新失败
	public static final int FAIL = 7;


	// 没有更多数据
	public static final int list_no_more_data = 8;
	/** 设置整个Layout的top，用于当Layout平移时保存的位置 */
	public int yTranslate;
	// 回滚速度
	public float moveSpeed = 8;
	// 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
	public float pullDownY = 0;
	// 当前状态
	private int state = INIT;
	// 刷新回调接口
	private OnRefreshListener mListener;

	// 按下Y坐标，
	private float downY;
	// 上一个事件点Y坐标
	private float lastY;
	// 上拉的距离
	private float pullUpY = 0;

	// 释放刷新的距离
	private float refreshDist = 200;
	// 释放加载的距离
	private float loadmoreDist = 200;

	private MyTimer timer;
	// 第一次执行布局
	private boolean isLayout = false;
	// 在刷新过程中滑动操作
	private boolean isTouch = false;
	// 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
	private float radio = 2;

	// 下拉箭头的转180°动画
	private RotateAnimation rotateAnimation;
	// 均匀旋转动画
	private RotateAnimation refreshingAnimation;

	// 下拉头
	private View refreshView;
	// 下拉的箭头
	private View pullView;
	// 正在刷新的图标
	private View refreshingView;
	// 刷新结果图标
	private View refreshStateImageView;
	// 刷新结果：成功或失败
	private TextView refreshStateTextView;

	// 上拉头
	private View loadmoreView;
	// 上拉的箭头
	private View pullUpView;
	// 正在加载的图标
	private View loadingView;
	// 加载结果图标
	private View loadStateImageView;
	// 加载结果：成功或失败
	private TextView loadStateTextView;

	// 实现了Pullable接口的View
	private View pullableView;
	// 过滤多点触碰
	private int mEvents;
	// 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
	private boolean canPullDown = true;
	private boolean canPullUp = true;

	/** 是否开放上拉加载，下拉刷新功能 */
	private boolean isPullRefreshEnable = true;
	/** 是否可以拉 */
	private boolean isCanPull = true;

	private TextView timeTextView;
	/**
	 * 执行自动回滚的handler
	 */
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight()*(pullDownY + Math.abs(pullUpY))));// 回弹速度随下拉距离moveDeltaY增大而增大
			if (!isTouch) {// 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
				if (state == REFRESHING && pullDownY <= refreshDist) {
					pullDownY = refreshDist;
					timer.cancel();
				} else if (state == LOADING && -pullUpY <= loadmoreDist) {
					pullUpY = -loadmoreDist;timer.cancel();
				}
			}
			if (pullDownY > 0)
				pullDownY -= moveSpeed;
			else if (pullUpY < 0)
				pullUpY += moveSpeed;
			if (pullDownY < 0) {// 已完成回弹
				pullDownY = 0;
				pullView.clearAnimation();// 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
				if (state != REFRESHING && state != LOADING)
					changeState(INIT);timer.cancel();
			}
			if (pullUpY > 0) {
				pullUpY = 0;pullUpView.clearAnimation();
				if (state != REFRESHING && state != LOADING)// 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
					changeState(INIT);
				timer.cancel();
			}requestLayout();// 刷新布局,会自动调用onLayout
		}
	};

	public PullToRefreshLayout(Context context) {
		super(context);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PullToRefresh, 0, 0);
		try {
			isPullRefreshEnable = typeArray.getBoolean(
					R.styleable.PullToRefresh_isPullRefreshEnable, true);
			isCanPull = typeArray.getBoolean(R.styleable.PullToRefresh_isCanPull, true);
		} finally {
			typeArray.recycle();
		}
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PullToRefresh, 0, 0);
		try {
			isPullRefreshEnable = typeArray.getBoolean(
					R.styleable.PullToRefresh_isPullRefreshEnable, true);
			isCanPull = typeArray.getBoolean(R.styleable.PullToRefresh_isCanPull, true);
		} finally {
			typeArray.recycle();
		}
		initView(context);
	}

	/** 设置是否开放上下拉刷新操作 */
	public void setPull2RefreshEnable(boolean l) {
		if (l) {
			isPullRefreshEnable = true;
			isCanPull = true;
		} else {
			isPullRefreshEnable = false;
			isCanPull = false;
		}
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	private void initView(Context context) {
		timer = new MyTimer(updateHandler);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context,
				R.anim.reverse_anim);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context,
				R.anim.rotating);
		// 添加匀速转动动画
		LinearInterpolator lir = new LinearInterpolator();
		rotateAnimation.setInterpolator(lir);
		refreshingAnimation.setInterpolator(lir);
	}

	private void hide() {
		timer.schedule(5);
	}

	/**
	 * 完成刷新操作，显示刷新结果。注意：刷新完成后一定要调用这个方法
	 */
	/**
	 * @param refreshResult
	 *            PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
	 */
	public void refreshFinish(int refreshResult) {
		refreshingView.clearAnimation();
		refreshingView.setVisibility(View.GONE);
		switch (refreshResult) {
		case SUCCEED:
			// 刷新成功
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_succeed);
			refreshStateImageView.setBackgroundResource(R.drawable.refresh_succeed);
			break;
		case FAIL:
		default:
			// 刷新失败
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_fail);
			refreshStateImageView.setBackgroundResource(R.drawable.refresh_failed);
			break;
		}
		// 刷新结果停留1秒
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				changeState(DONE);
				hide();
			}
		}.sendEmptyMessageDelayed(0, 500);
	}

	/**
	 * 加载完毕，显示加载结果。注意：加载完成后一定要调用这个方法
	 * 
	 * @param refreshResult
	 *            PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
	 */
	public void loadmoreFinish(int refreshResult) {
		loadingView.clearAnimation();
		loadingView.setVisibility(View.GONE);
		switch (refreshResult) {
		case SUCCEED:
			// 加载成功
			loadStateImageView.setVisibility(View.VISIBLE);
			loadStateTextView.setText(R.string.load_succeed);
			loadStateImageView.setBackgroundResource(R.drawable.load_succeed);
			break;
		case FAIL:
		default:
			// 加载失败
			loadStateImageView.setVisibility(View.VISIBLE);
			loadStateTextView.setText(R.string.load_fail);
			loadStateImageView.setBackgroundResource(R.drawable.load_failed);
			break;
		}
		// 刷新结果停留1秒
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				changeState(DONE);
				hide();
			}
		}.sendEmptyMessageDelayed(0, 500);
	}

	private void changeState(int to) {
		state = to;
		switch (state) {
		case list_no_more_data:
			loadStateTextView.setText(R.string.list_no_more_data);
			loadStateImageView.setVisibility(View.GONE);
			pullUpView.clearAnimation();
			pullUpView.setVisibility(View.VISIBLE);
			break;
		case INIT:
			// 下拉布局初始状态
			refreshStateImageView.setVisibility(View.GONE);
			refreshStateTextView.setText(R.string.pull_to_refresh);
			pullView.clearAnimation();
			pullView.setVisibility(View.VISIBLE);
			// 上拉布局初始状态
			loadStateImageView.setVisibility(View.GONE);
			loadStateTextView.setText(R.string.pullup_to_load);
			pullUpView.clearAnimation();
			pullUpView.setVisibility(View.VISIBLE);
			// 没有更多数据时的初始状态
			if (((Pullable) pullableView).isNoMoreData()) {
				loadStateTextView.setText(R.string.list_no_more_data);
			}
			break;
		case RELEASE_TO_REFRESH:
			// 释放刷新状态
			refreshStateTextView.setText(R.string.release_to_refresh);
			pullView.startAnimation(rotateAnimation);
			break;
		case REFRESHING:
			// 正在刷新状态
			pullView.clearAnimation();
			refreshingView.setVisibility(View.VISIBLE);
			pullView.setVisibility(View.INVISIBLE);
			refreshingView.startAnimation(refreshingAnimation);
			refreshStateTextView.setText(R.string.refreshing);
			break;
		case RELEASE_TO_LOAD:
			// 释放加载状态
			loadStateTextView.setText(R.string.release_to_load);
			pullUpView.startAnimation(rotateAnimation);
			break;
		case LOADING:
			// 正在加载状态
			pullUpView.clearAnimation();
			loadingView.setVisibility(View.VISIBLE);
			pullUpView.setVisibility(View.INVISIBLE);
			loadingView.startAnimation(refreshingAnimation);
			loadStateTextView.setText(R.string.loading);
			break;
		case DONE:
			// 刷新或加载完毕，啥都不做
			state = INIT;
			break;
		}
	}

	/**
	 * 不限制上拉或下拉
	 */
	private void releasePull() {
		canPullDown = true;
		canPullUp = true;
	}

	/*
	 * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (isCanPull) {
			switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				downY = ev.getY();
				lastY = downY;
				timer.cancel();
				mEvents = 0;
				releasePull();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_UP:
				// 过滤多点触碰
				mEvents = -1;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mEvents == 0) {
					if (((Pullable) pullableView).canPullDown() && canPullDown && state != LOADING) {
						// 可以下拉，正在加载时不能下拉
						// 对实际滑动距离做缩小，造成用力拉的感觉
						pullDownY = pullDownY + (ev.getY() - lastY) / radio;
						if (pullDownY < 0) {
							pullDownY = 0;
							canPullDown = false;
							canPullUp = true;
						}
						if (pullDownY > getMeasuredHeight())
							pullDownY = getMeasuredHeight();
						if (state == REFRESHING) {
							// 正在刷新的时候触摸移动
							isTouch = true;
						}
					} else if (((Pullable) pullableView).canPullUp() && canPullUp
							&& state != REFRESHING) {
						// 可以上拉，正在刷新时不能上拉
						pullUpY = pullUpY + (ev.getY() - lastY) / radio;
						if (pullUpY > 0) {
							pullUpY = 0;
							canPullDown = true;
							canPullUp = false;
						}
						if (pullUpY < -getMeasuredHeight())
							pullUpY = -getMeasuredHeight();
						if (state == LOADING) {
							// 正在加载的时候触摸移动
							isTouch = true;
						}
					} else
						releasePull();
				} else
					mEvents = 0;
				lastY = ev.getY();
				// 根据下拉距离改变比例
				radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
						* (pullDownY + Math.abs(pullUpY))));
				requestLayout();
				if (!isPullRefreshEnable
						|| (pullDownY <= refreshDist && state == RELEASE_TO_REFRESH)) {
					// 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
					changeState(INIT);
				}
				if (isPullRefreshEnable && pullDownY >= refreshDist && state == INIT) {
					// 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
					changeState(RELEASE_TO_REFRESH);
				}
				// 下面是判断上拉加载的，同上，注意pullUpY是负值
				if (!isPullRefreshEnable || (-pullUpY <= loadmoreDist && state == RELEASE_TO_LOAD)) {
					changeState(INIT);
				}
				if (isPullRefreshEnable && -pullUpY >= loadmoreDist && state == INIT) {
					if (((Pullable) pullableView).isNoMoreData()) {
						changeState(list_no_more_data);
					} else {
						changeState(RELEASE_TO_LOAD);
					}

				}
				// 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
				// Math.abs(pullUpY))就可以不对当前状态作区分了
				if ((pullDownY + Math.abs(pullUpY)) > 8) {
					// 防止下拉过程中误触发长按事件和点击事件
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mListener != null){
					mListener.onSlide(ev.getY()-downY);
				}
				if (isPullRefreshEnable && (pullDownY > refreshDist || -pullUpY > loadmoreDist))
					// 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
					isTouch = false;
				if (state == RELEASE_TO_REFRESH) {
					changeState(REFRESHING);
					// 刷新操作
					if (mListener != null)
						mListener.onRefresh(this);
				} else if (state == RELEASE_TO_LOAD) {
					if (((Pullable) pullableView).isNoMoreData()) {
						loadmoreFinish(SUCCEED);
					} else {
						changeState(LOADING);
						// 加载操作
						if (mListener != null)
							mListener.onLoadMore(this);
					}

				}
				hide();
			default:
				break;
			}
		}
		// 事件分发交给父类
		super.dispatchTouchEvent(ev);
		return true;
	}

	private void initView() {
		// 初始化下拉布局
		pullView = refreshView.findViewById(R.id.pull_icon);
		refreshStateTextView = (TextView) refreshView.findViewById(R.id.state_tv);
		refreshingView = refreshView.findViewById(R.id.refreshing_icon);
		refreshStateImageView = refreshView.findViewById(R.id.state_iv);
		// 初始化上拉布局
		pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
		loadStateTextView = (TextView) loadmoreView.findViewById(R.id.loadstate_tv);
		loadingView = loadmoreView.findViewById(R.id.loading_icon);
		loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLayout) {
			// 这里是第一次进来的时候做一些初始化
			refreshView = getChildAt(0);
			timeTextView = (TextView) refreshView.findViewById(R.id.refresh_time);
			pullableView = getChildAt(1);
			loadmoreView = getChildAt(2);
			if (isPullRefreshEnable) {
				refreshView.setVisibility(View.VISIBLE);
				loadmoreView.setVisibility(View.VISIBLE);
			} else {
				refreshView.setVisibility(View.INVISIBLE);
				loadmoreView.setVisibility(View.INVISIBLE);
			}
			isLayout = true;
			initView();
			refreshDist = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
			loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0).getMeasuredHeight();
		}
		// 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分
		refreshView.layout(0, (int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
				refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
		pullableView.layout(0, (int) (pullDownY + pullUpY), pullableView.getMeasuredWidth(),
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight());
		loadmoreView.layout(
				0,
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
				loadmoreView.getMeasuredWidth(),
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
						+ loadmoreView.getMeasuredHeight());
		setTop(yTranslate + getTop());
	}

	/** 定时计 */
	class MyTimer {
		private Handler handler;
		private Timer timer;
		private MyTask mTask;

		public MyTimer(Handler handler) {
			this.handler = handler;
			timer = new Timer();
		}

		public void schedule(long period) {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			timer.schedule(mTask, 0, period);
		}

		public void cancel() {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		}

		/** 任务定时计 */
		class MyTask extends TimerTask {
			private Handler handler;

			public MyTask(Handler handler) {
				this.handler = handler;
			}

			@Override
			public void run() {
				handler.obtainMessage().sendToTarget();
			}

		}
	}

	/**
	 * 刷新加载回调接口
	 * 
	 * @author chenjing
	 * 
	 */
	public interface OnRefreshListener {
		/**
		 * 刷新操作
		 */
		void onRefresh(PullToRefreshLayout pullToRefreshLayout);

		/**
		 * 加载操作
		 */
		void onLoadMore(PullToRefreshLayout pullToRefreshLayout);

		/**
		 * 滑动操作，从down到up完成一次onSlide不包括move
		 * */
		void onSlide(float distance);
	}

	public TextView getTimeTextView() {
		return timeTextView;
	}
}