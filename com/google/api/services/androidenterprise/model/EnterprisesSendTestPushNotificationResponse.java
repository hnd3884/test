package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class EnterprisesSendTestPushNotificationResponse extends GenericJson
{
    @Key
    private String messageId;
    @Key
    private String topicName;
    
    public String getMessageId() {
        return this.messageId;
    }
    
    public EnterprisesSendTestPushNotificationResponse setMessageId(final String messageId) {
        this.messageId = messageId;
        return this;
    }
    
    public String getTopicName() {
        return this.topicName;
    }
    
    public EnterprisesSendTestPushNotificationResponse setTopicName(final String topicName) {
        this.topicName = topicName;
        return this;
    }
    
    public EnterprisesSendTestPushNotificationResponse set(final String fieldName, final Object value) {
        return (EnterprisesSendTestPushNotificationResponse)super.set(fieldName, value);
    }
    
    public EnterprisesSendTestPushNotificationResponse clone() {
        return (EnterprisesSendTestPushNotificationResponse)super.clone();
    }
}
