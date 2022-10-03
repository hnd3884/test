package com.me.tools.zcutil;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;

public class FormData
{
    private StringBuffer formData;
    private static final String PREFIX = "<form name='";
    private static final String SUFFIX = "</form>";
    private String formName;
    private static final Pattern PATTERN;
    private String userID;
    
    public FormData(final String formName, final String userID) {
        this.formData = null;
        this.formName = null;
        this.userID = null;
        this.userID = userID;
        this.formData = new StringBuffer();
        this.formName = formName;
    }
    
    public FormData(final String formName) {
        this.formData = null;
        this.formName = null;
        this.userID = null;
        this.formData = new StringBuffer();
        this.formName = formName;
        this.userID = METrack.getCustomerID();
    }
    
    protected void addInstallation(final Properties row) {
        this.formData.append("<add>");
        final Enumeration en = row.propertyNames();
        while (en.hasMoreElements()) {
            final String fieldName = en.nextElement();
            this.formData.append("<field name='");
            this.formData.append(fieldName);
            this.formData.append("'><value>");
            this.formData.append(this.getValue(((Hashtable<K, Object>)row).get(fieldName).toString()));
            this.formData.append("</value></field>");
        }
        this.formData.append("</add>");
    }
    
    public void addRecord(final Properties row) {
        this.formData.append("<add>");
        this.formData.append("<field name='customerid'><value>");
        this.formData.append(this.userID);
        this.formData.append("</value></field>");
        final Enumeration en = row.propertyNames();
        while (en.hasMoreElements()) {
            final String fieldName = en.nextElement();
            this.formData.append("<field name='");
            this.formData.append(fieldName);
            this.formData.append("'><value>");
            this.formData.append(this.getValue(((Hashtable<K, Object>)row).get(fieldName).toString()));
            this.formData.append("</value></field>");
        }
        this.formData.append("</add>");
    }
    
    private String getValue(final String value) {
        if (!FormData.PATTERN.matcher(value).matches()) {
            return "<![CDATA[" + value + "]]>";
        }
        return value;
    }
    
    private String getKeyTag() {
        final String key = METrack.getFormKey(this.formName);
        if (key != null) {
            return "privateKey='" + key + "'";
        }
        return "";
    }
    
    public String getFormRecords() {
        return "<form name='" + this.formName + "' " + this.getKeyTag() + ">" + this.formData.toString() + "</form>";
    }
    
    static {
        PATTERN = Pattern.compile("[a-z A-Z0-9]*");
    }
}
