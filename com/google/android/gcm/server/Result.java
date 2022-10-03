package com.google.android.gcm.server;

import java.util.List;
import java.io.Serializable;

public final class Result implements Serializable
{
    private final String messageId;
    private final String canonicalRegistrationId;
    private final String errorCode;
    private final Integer success;
    private final Integer failure;
    private final List<String> failedRegistrationIds;
    
    private Result(final Builder builder) {
        this.canonicalRegistrationId = builder.canonicalRegistrationId;
        this.messageId = builder.messageId;
        this.errorCode = builder.errorCode;
        this.success = builder.success;
        this.failure = builder.failure;
        this.failedRegistrationIds = builder.failedRegistrationIds;
    }
    
    public String getMessageId() {
        return this.messageId;
    }
    
    public String getCanonicalRegistrationId() {
        return this.canonicalRegistrationId;
    }
    
    public String getErrorCodeName() {
        return this.errorCode;
    }
    
    public Integer getSuccess() {
        return this.success;
    }
    
    public Integer getFailure() {
        return this.failure;
    }
    
    public List<String> getFailedRegistrationIds() {
        return this.failedRegistrationIds;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("[");
        if (this.messageId != null) {
            builder.append(" messageId=").append(this.messageId);
        }
        if (this.canonicalRegistrationId != null) {
            builder.append(" canonicalRegistrationId=").append(this.canonicalRegistrationId);
        }
        if (this.errorCode != null) {
            builder.append(" errorCode=").append(this.errorCode);
        }
        if (this.success != null) {
            builder.append(" groupSuccess=").append(this.success);
        }
        if (this.failure != null) {
            builder.append(" groupFailure=").append(this.failure);
        }
        if (this.failedRegistrationIds != null) {
            builder.append(" failedRegistrationIds=").append(this.failedRegistrationIds);
        }
        return builder.append(" ]").toString();
    }
    
    public static final class Builder
    {
        private String messageId;
        private String canonicalRegistrationId;
        private String errorCode;
        private Integer success;
        private Integer failure;
        private List<String> failedRegistrationIds;
        
        public Builder canonicalRegistrationId(final String value) {
            this.canonicalRegistrationId = value;
            return this;
        }
        
        public Builder messageId(final String value) {
            this.messageId = value;
            return this;
        }
        
        public Builder errorCode(final String value) {
            this.errorCode = value;
            return this;
        }
        
        public Builder success(final Integer value) {
            this.success = value;
            return this;
        }
        
        public Builder failure(final Integer value) {
            this.failure = value;
            return this;
        }
        
        public Builder failedRegistrationIds(final List<String> value) {
            this.failedRegistrationIds = value;
            return this;
        }
        
        public Result build() {
            return new Result(this, null);
        }
    }
}
