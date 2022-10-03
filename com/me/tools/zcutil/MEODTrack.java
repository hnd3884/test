package com.me.tools.zcutil;

import java.util.Properties;

public class MEODTrack
{
    private ZCUtil zcUtil;
    private Properties proxyProp;
    private String productName;
    
    public MEODTrack(final String productName, final String confDir, final Properties proxyProp) {
        this.zcUtil = null;
        this.proxyProp = null;
        this.productName = null;
        this.proxyProp = proxyProp;
        this.productName = productName;
        METrack.setConfDir(confDir);
        METrack.setIsOd(true);
        METrack.setZCUtil(this.zcUtil = new ZCUtil(productName));
        METrack.setConfProp(this.zcUtil.getConfValue());
    }
    
    public ZCUtil getZCUtil() {
        return this.zcUtil;
    }
    
    public String addNewUser(final AddUser add) {
        add.setIsCloud("true");
        add.setProduct(this.productName);
        return this.zcUtil.addNewUser(add, this.zcUtil.getAppName(), this.zcUtil.getDataForm(), this.proxyProp);
    }
    
    public String updateMultiFormdData(final ApplicationData appData) {
        final Properties queryString = new Properties();
        queryString.setProperty("zc_ownername", this.zcUtil.getConfValue().getProperty("zowner"));
        queryString.setProperty("XMLString", appData.getUploadData(this.zcUtil.getAppName()));
        queryString.setProperty("apikey", this.zcUtil.getConfValue().getProperty("key"));
        return this.zcUtil.connect(this.zcUtil.getConfValue().getProperty("url") + "api/xml/write?", queryString, this.proxyProp).toString();
    }
}
