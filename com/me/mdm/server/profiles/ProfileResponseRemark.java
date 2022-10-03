package com.me.mdm.server.profiles;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.profiles.ios.IOSProfileResponseRemark;

public class ProfileResponseRemark
{
    protected String dynamicRemark;
    protected String staticRemark;
    protected Integer errorCode;
    protected Integer status;
    
    public ProfileResponseRemark() {
        this.dynamicRemark = null;
        this.staticRemark = null;
        this.errorCode = null;
        this.status = 6;
    }
    
    public static ProfileResponseRemark getInstance(final int platformType) {
        switch (platformType) {
            case 1: {
                return new IOSProfileResponseRemark();
            }
            default: {
                return new ProfileResponseRemark();
            }
        }
    }
    
    public void appendRemark(final String remark) {
        if (this.dynamicRemark == null) {
            this.dynamicRemark = remark;
        }
        else {
            this.dynamicRemark = this.dynamicRemark + ", " + remark;
        }
    }
    
    public void addRemark(final String remark) {
        if (this.staticRemark == null) {
            this.staticRemark = remark;
        }
        else {
            this.staticRemark = this.staticRemark + ". " + remark;
        }
    }
    
    public String getFinalizedRemark() {
        String finalizedRemark = null;
        if (!MDMStringUtils.isEmpty(this.dynamicRemark)) {
            finalizedRemark = this.dynamicRemark;
        }
        if (!MDMStringUtils.isEmpty(this.staticRemark)) {
            finalizedRemark = this.staticRemark;
        }
        if (!MDMStringUtils.isEmpty(this.dynamicRemark) && !MDMStringUtils.isEmpty(this.staticRemark)) {
            finalizedRemark = this.dynamicRemark + this.staticRemark;
        }
        return finalizedRemark;
    }
    
    public boolean changeRemarkStatus(final Integer status) {
        if (status == 6 || this.status == status) {
            this.status = status;
            return true;
        }
        return false;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setErrorCode(final Integer errorCode) {
        this.errorCode = errorCode;
    }
    
    public Integer getErrorCode() {
        return this.errorCode;
    }
}
