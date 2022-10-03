package com.me.tools.zcutil;

import java.util.Enumeration;
import org.w3c.dom.Element;
import java.util.Properties;
import java.util.HashMap;

public class ZCScheduler
{
    private ZCDataHandler customDataHandler;
    private ZCUtil zcu;
    private ProductConf prodConf;
    private HashMap<String, Properties> dataMap;
    
    public ZCScheduler() {
        this.customDataHandler = null;
        this.zcu = null;
        this.prodConf = null;
        this.dataMap = null;
    }
    
    public void zcScheduler(final Properties proxyProp, final boolean pushAllBaseDetails) {
        try {
            this.zcu = new ZCUtil();
            if (this.zcu.getConnectStatus()) {
                final Properties confProp = this.zcu.getConfValue();
                this.prodConf = this.zcu.getProductConf();
                if (this.zcu.getCreatorId() != null) {
                    this.updateCreator(confProp, proxyProp);
                    METrack.updateUserDetails(proxyProp, null, pushAllBaseDetails);
                }
                this.invokeCustomDataHandler();
                if (confProp.getProperty("keepalivepollinterval") != null && this.keepAlivePing()) {
                    final Properties aliveProp = new Properties();
                    aliveProp.setProperty("aliveupdate", Long.toString(System.currentTimeMillis()));
                    METrack.updateRecord(aliveProp, "inputform", proxyProp);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean keepAlivePing() {
        try {
            final long lastUpdateAt = this.zcu.getLasteUpdatedTime();
            if (lastUpdateAt == -1L) {
                return true;
            }
            final int daysAfterLastUpdate = (int)((System.currentTimeMillis() - lastUpdateAt) / 86400000L);
            final int pollinterval = Integer.parseInt(this.zcu.getConfValue().getProperty("keepalivepollinterval"));
            if (daysAfterLastUpdate >= pollinterval) {
                return true;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void invokeCustomDataHandler() {
        try {
            if (this.zcu.getConfValue().getProperty("dataHandler") != null) {
                (this.customDataHandler = (ZCDataHandler)Class.forName(this.zcu.getConfValue().getProperty("dataHandler")).newInstance()).uploadData();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void updateCreator(final Properties confProp, final Properties proxyProp) {
        final ApplicationData appData = new ApplicationData();
        this.dataMap = new HashMap<String, Properties>();
        if (METrack.loadQueryExc() && (!this.prodConf.isLoadQueryFromProduct() || !this.prodConf.isLicenseQueryFromProduct())) {
            final Element selectQueryEle = this.zcu.viewRecord(confProp.getProperty("queryview"), null, proxyProp).getDocumentElement();
            final LoadQuery lqry = new LoadQuery(selectQueryEle);
            this.setLoadData(lqry);
            this.setLicenseData(lqry);
        }
        if (this.prodConf.isLoadQueryFromProduct()) {
            this.setLoadData(new LoadQuery(this.prodConf.getLoadElement()));
        }
        if (this.prodConf.isLicenseQueryFromProduct()) {
            this.setLicenseData(new LoadQuery(this.prodConf.getLicenseElement()));
        }
        if (this.dataMap != null && this.dataMap.size() > 0) {
            appData.addRecords(this.dataMap);
            METrack.updateMultiFormdData(appData, proxyProp);
        }
    }
    
    private String updateForm(final Properties prop, final String appName, final String formName, final Properties proxyProp) {
        if (prop != null && prop.size() > 0) {
            prop.setProperty("customerid", this.zcu.getCreatorId());
            return this.zcu.addRecord(appName, formName, prop, proxyProp);
        }
        return null;
    }
    
    private void setLicenseData(final LoadQuery lqry) {
        if (lqry != null && lqry.getLoadQuery("license") != null) {
            String result = null;
            final Enumeration en = lqry.getLoadQuery("license").propertyNames();
            while (en.hasMoreElements()) {
                final String key = en.nextElement();
                result = this.zcu.getLicenseValue(lqry.getLoadQuery("license").getProperty(key));
                if (result == null) {
                    result = "-";
                }
                this.addDataToMap(key, result, lqry);
            }
        }
    }
    
    private void setLoadData(final LoadQuery lqry) {
        if (lqry != null && lqry.getAllAndDBQueryies(this.zcu.getInstallationDB()) != null) {
            String result = null;
            final RunSelectQuery rsq = new RunSelectQuery();
            final Enumeration en = lqry.getAllAndDBQueryies(this.zcu.getInstallationDB()).propertyNames();
            while (en.hasMoreElements()) {
                final String key = en.nextElement();
                result = rsq.getOneValue(lqry.getAllAndDBQueryies(this.zcu.getInstallationDB()).getProperty(key));
                if (result != null) {
                    this.addDataToMap(key, result, lqry);
                }
            }
        }
    }
    
    private void addDataToMap(final String column, final String value, final LoadQuery lqry) {
        if (this.dataMap.get(lqry.getFormName(column)) == null) {
            final Properties formDataProp = new Properties();
            this.dataMap.put(lqry.getFormName(column), formDataProp);
        }
        this.dataMap.get(lqry.getFormName(column)).setProperty(column, value);
    }
}
