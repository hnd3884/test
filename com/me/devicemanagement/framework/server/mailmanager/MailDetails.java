package com.me.devicemanagement.framework.server.mailmanager;

import org.json.JSONObject;

public class MailDetails
{
    public String fromAddress;
    public String senderDisplayName;
    public String toAddress;
    public String ccAddress;
    public String subject;
    public String bodyContent;
    public String[] attachment;
    public String callBackHandler;
    public JSONObject additionalParams;
    
    public MailDetails(final String fromAdd, final String toAdd) {
        this.fromAddress = null;
        this.senderDisplayName = null;
        this.toAddress = null;
        this.ccAddress = null;
        this.subject = null;
        this.bodyContent = null;
        this.attachment = null;
        this.callBackHandler = null;
        this.additionalParams = new JSONObject();
        this.fromAddress = fromAdd;
        this.toAddress = toAdd;
    }
    
    @Override
    public String toString() {
        return "{\"fromAddress\":\"" + this.fromAddress + "\", \"senderDisplayName\":\"" + this.senderDisplayName + "\", \"toAddress\":\"" + this.toAddress + "\", \"ccAddress\":\"" + this.ccAddress + "\",\"subject\":\"" + this.subject + "\",\"bodyContent\":\"" + this.bodyContent + "\", \"attachment\":\"" + this.attachment + "\", \"callBackHandler\":\"" + this.callBackHandler + "\", \"additionalParams\":\"" + this.additionalParams.toString() + "\"}";
    }
    
    public String getNonSensitiveDataAsString() {
        return "{\"fromAddress\":\"" + this.fromAddress + "\", \"senderDisplayName\":\"" + this.senderDisplayName + "\", \"attachment\":\"" + this.attachment + "\", \"callBackHandler\":\"" + this.callBackHandler + "\", \"additionalParams\":\"" + this.additionalParams.toString() + "\"}";
    }
}
