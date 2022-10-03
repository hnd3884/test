package com.me.mdm.server.notification.pushnotification;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;

public class DeviceAppNotification
{
    private JSONObject customPayload;
    private int messageType;
    private int category;
    private String i18nTitle;
    private String i18nTitleArgs;
    private String i18nMessage;
    private String i18nMessageArgs;
    private String title;
    private String body;
    
    public DeviceAppNotification(final int messageType, final int category, final JSONObject customPayload) {
        this.messageType = messageType;
        this.category = category;
        this.customPayload = customPayload;
    }
    
    public JSONObject getCustomPayload() {
        return this.customPayload;
    }
    
    public void setCustomPayload(final JSONObject customPayload) {
        this.customPayload = customPayload;
    }
    
    public int getMessageType() {
        return this.messageType;
    }
    
    public void setMessageType(final int messageType) {
        this.messageType = messageType;
    }
    
    public int getCategory() {
        return this.category;
    }
    
    public void setCategory(final int category) {
        this.category = category;
    }
    
    public String getI18nTitle() {
        return this.i18nTitle;
    }
    
    public void setI18nTitle(final String i18nTitle) {
        this.i18nTitle = i18nTitle;
    }
    
    public String getI18nTitleArgs() {
        return this.i18nTitleArgs;
    }
    
    public void setI18nTitleArgs(final String i18nTitleArgs) {
        this.i18nTitleArgs = i18nTitleArgs;
    }
    
    public String getI18nMessage() {
        return this.i18nMessage;
    }
    
    public void setI18nMessage(final String i18nMessage) {
        this.i18nMessage = i18nMessage;
    }
    
    public String getI18nMessageArgs() {
        return this.i18nMessageArgs;
    }
    
    public void setI18nMessageArgs(final String i18nMessageArgs) {
        this.i18nMessageArgs = i18nMessageArgs;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getBody() {
        return this.body;
    }
    
    public void setBody(final String body) {
        this.body = body;
    }
    
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put("type", this.messageType);
        json.put("category", this.category);
        if (this.messageType == 1) {
            json.put("title_key", (Object)this.i18nTitle);
            if (!MDMStringUtils.isEmpty(this.i18nTitleArgs)) {
                json.put("title_args", (Object)this.i18nTitleArgs);
            }
            json.put("message_key", (Object)this.i18nMessage);
            if (!MDMStringUtils.isEmpty(this.i18nMessageArgs)) {
                json.put("message_args", (Object)this.i18nMessageArgs);
            }
        }
        else {
            json.put("title", (Object)this.title);
            json.put("message", (Object)this.body);
        }
        return json;
    }
}
