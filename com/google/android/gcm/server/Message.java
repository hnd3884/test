package com.google.android.gcm.server;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.io.Serializable;

public final class Message implements Serializable
{
    private final String collapseKey;
    private final Boolean delayWhileIdle;
    private final Integer timeToLive;
    private final Map<String, String> data;
    private final Boolean dryRun;
    private final String restrictedPackageName;
    private final String priority;
    private final Boolean contentAvailable;
    private final Notification notification;
    
    private Message(final Builder builder) {
        this.collapseKey = builder.collapseKey;
        this.delayWhileIdle = builder.delayWhileIdle;
        this.data = Collections.unmodifiableMap((Map<? extends String, ? extends String>)builder.data);
        this.timeToLive = builder.timeToLive;
        this.dryRun = builder.dryRun;
        this.restrictedPackageName = builder.restrictedPackageName;
        this.priority = builder.priority;
        this.contentAvailable = builder.contentAvailable;
        this.notification = builder.notification;
    }
    
    public String getCollapseKey() {
        return this.collapseKey;
    }
    
    public Boolean isDelayWhileIdle() {
        return this.delayWhileIdle;
    }
    
    public Integer getTimeToLive() {
        return this.timeToLive;
    }
    
    public Boolean isDryRun() {
        return this.dryRun;
    }
    
    public String getRestrictedPackageName() {
        return this.restrictedPackageName;
    }
    
    public String getPriority() {
        return this.priority;
    }
    
    public Boolean getContentAvailable() {
        return this.contentAvailable;
    }
    
    public Map<String, String> getData() {
        return this.data;
    }
    
    public Notification getNotification() {
        return this.notification;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Message(");
        if (this.priority != null) {
            builder.append("priority=").append(this.priority).append(", ");
        }
        if (this.contentAvailable != null) {
            builder.append("contentAvailable=").append(this.contentAvailable).append(", ");
        }
        if (this.collapseKey != null) {
            builder.append("collapseKey=").append(this.collapseKey).append(", ");
        }
        if (this.timeToLive != null) {
            builder.append("timeToLive=").append(this.timeToLive).append(", ");
        }
        if (this.delayWhileIdle != null) {
            builder.append("delayWhileIdle=").append(this.delayWhileIdle).append(", ");
        }
        if (this.dryRun != null) {
            builder.append("dryRun=").append(this.dryRun).append(", ");
        }
        if (this.restrictedPackageName != null) {
            builder.append("restrictedPackageName=").append(this.restrictedPackageName).append(", ");
        }
        if (this.notification != null) {
            builder.append("notification: ").append(this.notification).append(", ");
        }
        if (!this.data.isEmpty()) {
            builder.append("data: {");
            for (final Map.Entry<String, String> entry : this.data.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
            }
            builder.delete(builder.length() - 1, builder.length());
            builder.append("}");
        }
        if (builder.charAt(builder.length() - 1) == ' ') {
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(")");
        return builder.toString();
    }
    
    public enum Priority
    {
        NORMAL, 
        HIGH;
    }
    
    public static final class Builder
    {
        private final Map<String, String> data;
        private String collapseKey;
        private Boolean delayWhileIdle;
        private Integer timeToLive;
        private Boolean dryRun;
        private String restrictedPackageName;
        private String priority;
        private Boolean contentAvailable;
        private Notification notification;
        
        public Builder() {
            this.data = new LinkedHashMap<String, String>();
        }
        
        public Builder collapseKey(final String value) {
            this.collapseKey = value;
            return this;
        }
        
        public Builder delayWhileIdle(final boolean value) {
            this.delayWhileIdle = value;
            return this;
        }
        
        public Builder timeToLive(final int value) {
            this.timeToLive = value;
            return this;
        }
        
        public Builder addData(final String key, final String value) {
            this.data.put(key, value);
            return this;
        }
        
        public Builder dryRun(final boolean value) {
            this.dryRun = value;
            return this;
        }
        
        public Builder restrictedPackageName(final String value) {
            this.restrictedPackageName = value;
            return this;
        }
        
        public Builder priority(final Priority value) {
            switch (value) {
                case NORMAL: {
                    this.priority = "normal";
                    break;
                }
                case HIGH: {
                    this.priority = "high";
                    break;
                }
            }
            return this;
        }
        
        public Builder notification(final Notification value) {
            this.notification = value;
            return this;
        }
        
        public Builder contentAvailable(final Boolean value) {
            this.contentAvailable = value;
            return this;
        }
        
        public Message build() {
            return new Message(this, null);
        }
    }
}
