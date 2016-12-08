package com.egovcomm.monitor.model;

import java.util.List;

/**
 * @author Nicolls
 * @Description 用户信息返回实体
 * @date 2015年10月31日
 */
public class RspVideoList extends AppResponse {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields data 用户信息
	 */
	private ResultData data;
	public class ResultData{
		private int total;
		private List<RspMediaGroup> data;
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public List<RspMediaGroup> getData() {
			return data;
		}
		public void setData(List<RspMediaGroup> data) {
			this.data = data;
		}
	}
	public ResultData getData() {
		return data;
	}
	public void setData(ResultData data) {
		this.data = data;
	}
	
}
