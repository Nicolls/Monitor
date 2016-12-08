package com.egovcomm.monitor.model;

import java.io.Serializable;

/**
 * @author Nicolls
 * @Description 用户信息
 * @date 2015年10月31日
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields email 邮箱
	 */
	private String email;
	private String fullName;
	private String loginType;
	private String orderID;
	private String orgAdminFlag;
	private String orgHiberarchy;
	private String orgID;
	private String orgLeaderFlag;
	private String orgName;
	private String orgStatusFlag;
	private String phone;
	private String publicRoleFlag;
	private String relaStateFlag;
	private String relaTypeFlag;
	private String remark;
	private String userAccount;
	private String userID;
	private String userName;
	private String userOrgID;
	private String userTypeFlag;
	private String password;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getOrgAdminFlag() {
		return orgAdminFlag;
	}
	public void setOrgAdminFlag(String orgAdminFlag) {
		this.orgAdminFlag = orgAdminFlag;
	}
	public String getOrgHiberarchy() {
		return orgHiberarchy;
	}
	public void setOrgHiberarchy(String orgHiberarchy) {
		this.orgHiberarchy = orgHiberarchy;
	}
	public String getOrgID() {
		return orgID;
	}
	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}
	public String getOrgLeaderFlag() {
		return orgLeaderFlag;
	}
	public void setOrgLeaderFlag(String orgLeaderFlag) {
		this.orgLeaderFlag = orgLeaderFlag;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgStatusFlag() {
		return orgStatusFlag;
	}
	public void setOrgStatusFlag(String orgStatusFlag) {
		this.orgStatusFlag = orgStatusFlag;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPublicRoleFlag() {
		return publicRoleFlag;
	}
	public void setPublicRoleFlag(String publicRoleFlag) {
		this.publicRoleFlag = publicRoleFlag;
	}
	public String getRelaStateFlag() {
		return relaStateFlag;
	}
	public void setRelaStateFlag(String relaStateFlag) {
		this.relaStateFlag = relaStateFlag;
	}
	public String getRelaTypeFlag() {
		return relaTypeFlag;
	}
	public void setRelaTypeFlag(String relaTypeFlag) {
		this.relaTypeFlag = relaTypeFlag;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserOrgID() {
		return userOrgID;
	}
	public void setUserOrgID(String userOrgID) {
		this.userOrgID = userOrgID;
	}
	public String getUserTypeFlag() {
		return userTypeFlag;
	}
	public void setUserTypeFlag(String userTypeFlag) {
		this.userTypeFlag = userTypeFlag;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
