package com.me.mdm.chrome.agent.core;

import java.util.Iterator;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.core.communication.CommunicationManager;
import java.util.HashMap;
import com.me.mdm.chrome.agent.core.communication.CommunicationStatus;
import com.me.mdm.chrome.agent.Context;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MessageUtil
{
    Logger logger;
    public String messageType;
    public JSONObject msgData;
    public Object msgContent;
    public String msgStatus;
    public String errorMsg;
    public String remarks;
    public int errorCode;
    public int serviceType;
    private final String udid;
    private final Long customerId;
    private final Context context;
    
    public MessageUtil(final Context context) {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
        this.messageType = null;
        this.msgData = null;
        this.msgContent = null;
        this.msgStatus = null;
        this.errorMsg = null;
        this.remarks = null;
        this.errorCode = 0;
        this.udid = context.getUdid();
        this.customerId = context.getCustomerId();
        this.context = context;
    }
    
    public void setMessageData(final JSONObject msgData) {
        this.msgData = msgData;
    }
    
    public void setMessageContent(final Object msgContent) {
        this.msgContent = msgContent;
    }
    
    private void clearMsgContent() {
        this.msgContent = null;
    }
    
    private void clearMsgData() {
        this.msgData = null;
    }
    
    public void setMsgStatus(final String msgStatus) {
        this.msgStatus = msgStatus;
    }
    
    public String getMsgStatus(final String msgStatus) {
        return this.msgStatus;
    }
    
    private void clearMsgStatus() {
        this.msgStatus = null;
    }
    
    public void setMsgRemarks(final String msgRemarks) {
        this.remarks = msgRemarks;
    }
    
    private void clearMsgRemarks() {
        this.remarks = null;
    }
    
    private void clearServiceType() {
        this.serviceType = 0;
    }
    
    public void setErrorDetails(final int errCode, final String errMsg) {
        this.errorCode = errCode;
        this.errorMsg = errMsg;
    }
    
    public CommunicationStatus postMessageData() {
        CommunicationStatus status = new CommunicationStatus(1);
        try {
            final JSONObject jsonObject = this.initializeMessageDetails(this.udid);
            final HashMap<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("customerId", this.customerId.toString());
            status = CommunicationManager.getInstance().getCommunicationHandler().postData(jsonObject, paramsMap);
            this.clearMsgData();
            this.clearMsgContent();
            this.clearMsgStatus();
            this.clearMsgRemarks();
            this.clearServiceType();
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception while posting the Message Data", exp);
        }
        return status;
    }
    
    private JSONObject prepareMessageHeader(final String udid) {
        final JSONObject msgHeader = new JSONObject();
        try {
            msgHeader.put("AgentType", 4);
            msgHeader.put("UDID", (Object)udid);
            msgHeader.put("MessageVersion", (Object)"0.0.1 CMPA");
            msgHeader.put("MessageType", (Object)this.messageType);
            msgHeader.put("MsgRequestType", (Object)this.messageType);
            msgHeader.put("DevicePlatform", (Object)"Chrome OS");
            if (this.msgStatus != null) {
                msgHeader.put("Status", (Object)this.msgStatus);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in setting the Message Header", exp);
        }
        return msgHeader;
    }
    
    private JSONObject prepareErrorDetails() {
        final JSONObject errMsg = new JSONObject();
        try {
            if (this.msgStatus != null && this.msgStatus.equalsIgnoreCase("Error")) {
                errMsg.put("Status", (Object)this.msgStatus);
                errMsg.put("ErrorCode", this.errorCode);
                errMsg.put("ErrorMsg", (Object)this.errorMsg);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in setting the Message Error Details", exp);
        }
        return errMsg;
    }
    
    private JSONObject initializeMessageDetails(final String udid) throws JSONException {
        final JSONObject msgHeader = this.prepareMessageHeader(udid);
        JSONObject message = new JSONObject();
        if (this.msgData != null) {
            message = this.mergeJSONObject(this.msgData, msgHeader);
            message.put("MsgRequest", (Object)this.msgData);
        }
        else {
            message = msgHeader;
        }
        if (this.msgStatus != null && this.msgStatus.equalsIgnoreCase("Error")) {
            message = this.mergeJSONObject(message, this.prepareErrorDetails());
        }
        if (this.msgContent != null) {
            message.put("Message", this.msgContent);
        }
        if (this.remarks != null) {
            message.put("Remarks", (Object)this.remarks);
        }
        return message;
    }
    
    private JSONObject mergeJSONObject(final JSONObject jsonObject1, final JSONObject jsonObject2) {
        final JSONObject mergedJson = new JSONObject();
        try {
            final JSONObject[] array;
            final JSONObject[] objs = array = new JSONObject[] { jsonObject1, jsonObject2 };
            for (final JSONObject obj : array) {
                final Iterator<?> it = obj.keys();
                while (it.hasNext()) {
                    final String key = (String)it.next();
                    mergedJson.put(key, obj.get(key));
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred while merging two jsonobject{0}", exp.getMessage());
        }
        return mergedJson;
    }
}
