/**
 * 
 */
package com.egovcomm.monitor.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.egovcomm.monitor.net.DataUpdateListener;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.net.RequestServiceFactory;
import com.egovcomm.monitor.utils.ErrorUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.ErrorUtils.ErrorListener;
import com.egovcomm.monitor.utils.ErrorUtils.SuccessListener;

/**
 * Fragment基类
 * 
 * @author mengjk
 *
 *         2015年5月19日
 */
public abstract class BaseFragment extends Fragment implements DataUpdateListener {
	protected RequestService mEBikeRequestService;
	private String name;
	public String tag=this.getClass().getSimpleName();
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.i(tag, "onCreate");
		if(getArguments()!=null&&getArguments().get("name")!=null){
			name=getArguments().get("name").toString();
		}
		LogUtils.i(tag, "name is="+name);
		mEBikeRequestService = RequestServiceFactory.getInstance(getActivity()
				.getApplicationContext(), RequestServiceFactory.REQUEST_VOLLEY);
		LogUtils.i("setUpdateListener", this.getClass().getName());
		mEBikeRequestService.setUptateListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtils.i(tag, "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtils.i(tag, "onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		LogUtils.i(tag, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtils.i(tag, "onResume");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtils.i(tag, "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		LogUtils.i(tag, "onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtils.i(tag, "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.i(tag, "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		LogUtils.i(tag, "onDetach");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
				requestError(id,obj);
			}
		});
	}

	/**请求数据出错时会触发此方法*/
	protected void requestError(int id,Object obj){
		
	}
	/** 抽象方法，用来通知fragment数据已请求回来 */
	public abstract void dateUpdate(int id, Object obj);
}
