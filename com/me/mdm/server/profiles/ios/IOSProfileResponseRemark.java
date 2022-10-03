package com.me.mdm.server.profiles.ios;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.profiles.ProfileResponseRemark;

public class IOSProfileResponseRemark extends ProfileResponseRemark
{
    @Override
    public void appendRemark(final String remark) {
        final String identifier = "@@@";
        final String remarkString = identifier + remark;
        if (this.dynamicRemark == null) {
            this.dynamicRemark = remarkString;
        }
        else {
            this.dynamicRemark += remarkString;
        }
    }
    
    @Override
    public String getFinalizedRemark() {
        final String identifier = "@@@";
        String unsuperRemark = null;
        if (!MDMStringUtils.isEmpty(this.staticRemark)) {
            unsuperRemark = this.staticRemark;
        }
        if (!MDMStringUtils.isEmpty(this.dynamicRemark)) {
            final String[] remarks = this.dynamicRemark.split(identifier);
            String splitedRemarks = "";
            final int length = remarks.length - 1;
            for (int i = 0; i < length; ++i) {
                splitedRemarks += ",";
                splitedRemarks = splitedRemarks + "{" + i + "}";
            }
            splitedRemarks = splitedRemarks.substring(1, splitedRemarks.length());
            splitedRemarks = splitedRemarks + " {" + length + "}";
            unsuperRemark = splitedRemarks + this.dynamicRemark + identifier + "mdm.profile.ios.supervised.only";
            this.setErrorCode(29000);
        }
        return unsuperRemark;
    }
}
