package com.egovcomm.monitor.model;

import java.io.Serializable;

/**
 * Created by mengjk on 2016/12/15.
 */

public class AppConfig implements Serializable{
    /**上传位置信息时间间隔 单位 秒*/
    private int uploadLocationSpaceTime;
    /**获取位置信息失败后的提示时间间隔 单位 秒*/
    private int localtionFailTipSpaceTime;
    public int getUploadLocationSpaceTime() {
        return uploadLocationSpaceTime;
    }

    public void setUploadLocationSpaceTime(int uploadLocationSpaceTime) {
        this.uploadLocationSpaceTime = uploadLocationSpaceTime;
    }

    public int getLocaltionFailTipSpaceTime() {
        return localtionFailTipSpaceTime;
    }

    public void setLocaltionFailTipSpaceTime(int localtionFailTipSpaceTime) {
        this.localtionFailTipSpaceTime = localtionFailTipSpaceTime;
    }



}
