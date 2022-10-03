package com.me.tools.zcutil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Properties;

public class ApplicationData
{
    private StringBuffer xmlString;
    private String userID;
    
    public ApplicationData(final String userID) {
        this.xmlString = null;
        this.userID = null;
        this.xmlString = new StringBuffer();
        this.userID = userID;
    }
    
    public ApplicationData() {
        this.xmlString = null;
        this.userID = null;
        this.xmlString = new StringBuffer();
        this.userID = METrack.getCustomerID();
    }
    
    protected void addInstallation(final String formName, final Properties record) {
        final FormData fdata = new FormData(formName, null);
        fdata.addInstallation(record);
        this.xmlString.append(fdata.getFormRecords());
    }
    
    public void addRecord(final String formName, final Properties record) {
        final FormData fdata = new FormData(formName, this.userID);
        fdata.addRecord(record);
        this.xmlString.append(fdata.getFormRecords());
    }
    
    public void addRecords(final String formName, final Vector<Properties> records) {
        final FormData fdata = new FormData(formName, this.userID);
        for (final Properties record : records) {
            fdata.addRecord(record);
        }
        this.xmlString.append(fdata.getFormRecords());
    }
    
    public void addRecords(final HashMap<String, Properties> recordsMap) {
        for (final String key : recordsMap.keySet()) {
            this.addRecord(key, recordsMap.get(key));
        }
    }
    
    public void addFormData(final FormData formData) {
        this.xmlString.append(formData.getFormRecords());
    }
    
    public String getUploadData() {
        return "<ZohoCreator><applicationlist><application name='" + METrack.getAppName() + "'><formlist>" + this.xmlString.toString() + "</formlist></application></applicationlist></ZohoCreator>";
    }
    
    public String getUploadData(final String appName) {
        return "<ZohoCreator><applicationlist><application name='" + appName + "'><formlist>" + this.xmlString.toString() + "</formlist></application></applicationlist></ZohoCreator>";
    }
}
