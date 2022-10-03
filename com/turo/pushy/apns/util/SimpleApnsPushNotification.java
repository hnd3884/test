package com.turo.pushy.apns.util;

import java.util.concurrent.TimeUnit;
import com.eatthepath.uuid.FastUUID;
import java.util.Objects;
import java.util.UUID;
import com.turo.pushy.apns.PushType;
import com.turo.pushy.apns.DeliveryPriority;
import java.util.Date;
import com.turo.pushy.apns.ApnsPushNotification;

public class SimpleApnsPushNotification implements ApnsPushNotification
{
    private final String token;
    private final String payload;
    private final Date invalidationTime;
    private final DeliveryPriority priority;
    private final PushType pushType;
    private final String topic;
    private final String collapseId;
    private final UUID apnsId;
    public static final long DEFAULT_EXPIRATION_PERIOD_MILLIS;
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload) {
        this(token, topic, payload, new Date(System.currentTimeMillis() + SimpleApnsPushNotification.DEFAULT_EXPIRATION_PERIOD_MILLIS), DeliveryPriority.IMMEDIATE, null, null, null);
    }
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime) {
        this(token, topic, payload, invalidationTime, DeliveryPriority.IMMEDIATE, null, null, null);
    }
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime, final DeliveryPriority priority) {
        this(token, topic, payload, invalidationTime, priority, null, null, null);
    }
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime, final DeliveryPriority priority, final PushType pushType) {
        this(token, topic, payload, invalidationTime, priority, pushType, null, null);
    }
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime, final DeliveryPriority priority, final String collapseId) {
        this(token, topic, payload, invalidationTime, priority, null, collapseId, null);
    }
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime, final DeliveryPriority priority, final PushType pushType, final String collapseId) {
        this(token, topic, payload, invalidationTime, priority, pushType, collapseId, null);
    }
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime, final DeliveryPriority priority, final String collapseId, final UUID apnsId) {
        this(token, topic, payload, invalidationTime, priority, null, collapseId, apnsId);
    }
    
    public SimpleApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime, final DeliveryPriority priority, final PushType pushType, final String collapseId, final UUID apnsId) {
        this.token = Objects.requireNonNull(token, "Destination device token must not be null.");
        this.topic = Objects.requireNonNull(topic, "Destination topic must not be null.");
        this.payload = Objects.requireNonNull(payload, "Payload must not be null.");
        this.invalidationTime = invalidationTime;
        this.priority = priority;
        this.pushType = pushType;
        this.collapseId = collapseId;
        this.apnsId = apnsId;
    }
    
    @Override
    public String getToken() {
        return this.token;
    }
    
    @Override
    public String getPayload() {
        return this.payload;
    }
    
    @Override
    public Date getExpiration() {
        return this.invalidationTime;
    }
    
    @Override
    public DeliveryPriority getPriority() {
        return this.priority;
    }
    
    @Override
    public PushType getPushType() {
        return this.pushType;
    }
    
    @Override
    public String getTopic() {
        return this.topic;
    }
    
    @Override
    public String getCollapseId() {
        return this.collapseId;
    }
    
    @Override
    public UUID getApnsId() {
        return this.apnsId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.invalidationTime == null) ? 0 : this.invalidationTime.hashCode());
        result = 31 * result + ((this.payload == null) ? 0 : this.payload.hashCode());
        result = 31 * result + ((this.priority == null) ? 0 : this.priority.hashCode());
        result = 31 * result + ((this.pushType == null) ? 0 : this.pushType.hashCode());
        result = 31 * result + ((this.token == null) ? 0 : this.token.hashCode());
        result = 31 * result + ((this.topic == null) ? 0 : this.topic.hashCode());
        result = 31 * result + ((this.collapseId == null) ? 0 : this.collapseId.hashCode());
        result = 31 * result + ((this.apnsId == null) ? 0 : this.apnsId.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SimpleApnsPushNotification)) {
            return false;
        }
        final SimpleApnsPushNotification other = (SimpleApnsPushNotification)obj;
        if (this.invalidationTime == null) {
            if (other.invalidationTime != null) {
                return false;
            }
        }
        else if (!this.invalidationTime.equals(other.invalidationTime)) {
            return false;
        }
        if (this.payload == null) {
            if (other.payload != null) {
                return false;
            }
        }
        else if (!this.payload.equals(other.payload)) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        if (this.pushType != other.pushType) {
            return false;
        }
        if (this.token == null) {
            if (other.token != null) {
                return false;
            }
        }
        else if (!this.token.equals(other.token)) {
            return false;
        }
        if (this.topic == null) {
            if (other.topic != null) {
                return false;
            }
        }
        else if (!this.topic.equals(other.topic)) {
            return false;
        }
        if (Objects.equals(this.collapseId, null)) {
            if (!Objects.equals(other.collapseId, null)) {
                return false;
            }
        }
        else if (!this.collapseId.equals(other.collapseId)) {
            return false;
        }
        if (Objects.equals(this.apnsId, null)) {
            if (!Objects.equals(other.apnsId, null)) {
                return false;
            }
        }
        else if (!this.apnsId.equals(other.apnsId)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "SimpleApnsPushNotification{token='" + this.token + '\'' + ", payload='" + this.payload + '\'' + ", invalidationTime=" + this.invalidationTime + ", priority=" + this.priority + ", pushType=" + this.pushType + ", topic='" + this.topic + '\'' + ", collapseId='" + this.collapseId + '\'' + ", apnsId=" + ((this.apnsId != null) ? FastUUID.toString(this.apnsId) : null) + '}';
    }
    
    static {
        DEFAULT_EXPIRATION_PERIOD_MILLIS = TimeUnit.DAYS.toMillis(1L);
    }
}
