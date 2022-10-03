package com.google.api.client.googleapis.services;

import java.io.IOException;

public class CommonGoogleClientRequestInitializer implements GoogleClientRequestInitializer
{
    private static final String REQUEST_REASON_HEADER_NAME = "X-Goog-Request-Reason";
    private static final String USER_PROJECT_HEADER_NAME = "X-Goog-User-Project";
    private final String key;
    private final String userIp;
    private final String userAgent;
    private final String requestReason;
    private final String userProject;
    
    @Deprecated
    public CommonGoogleClientRequestInitializer() {
        this(newBuilder());
    }
    
    @Deprecated
    public CommonGoogleClientRequestInitializer(final String key) {
        this(key, null);
    }
    
    @Deprecated
    public CommonGoogleClientRequestInitializer(final String key, final String userIp) {
        this(newBuilder().setKey(key).setUserIp(userIp));
    }
    
    protected CommonGoogleClientRequestInitializer(final Builder builder) {
        this.key = builder.getKey();
        this.userIp = builder.getUserIp();
        this.userAgent = builder.getUserAgent();
        this.requestReason = builder.getRequestReason();
        this.userProject = builder.getUserProject();
    }
    
    public static Builder newBuilder() {
        return new Builder();
    }
    
    @Override
    public void initialize(final AbstractGoogleClientRequest<?> request) throws IOException {
        if (this.key != null) {
            request.put("key", (Object)this.key);
        }
        if (this.userIp != null) {
            request.put("userIp", (Object)this.userIp);
        }
        if (this.userAgent != null) {
            request.getRequestHeaders().setUserAgent(this.userAgent);
        }
        if (this.requestReason != null) {
            request.getRequestHeaders().set("X-Goog-Request-Reason", (Object)this.requestReason);
        }
        if (this.userProject != null) {
            request.getRequestHeaders().set("X-Goog-User-Project", (Object)this.userProject);
        }
    }
    
    public final String getKey() {
        return this.key;
    }
    
    public final String getUserIp() {
        return this.userIp;
    }
    
    public final String getUserAgent() {
        return this.userAgent;
    }
    
    public final String getRequestReason() {
        return this.requestReason;
    }
    
    public final String getUserProject() {
        return this.userProject;
    }
    
    public static class Builder
    {
        private String key;
        private String userIp;
        private String userAgent;
        private String requestReason;
        private String userProject;
        
        public Builder setKey(final String key) {
            this.key = key;
            return this.self();
        }
        
        public String getKey() {
            return this.key;
        }
        
        public Builder setUserIp(final String userIp) {
            this.userIp = userIp;
            return this.self();
        }
        
        public String getUserIp() {
            return this.userIp;
        }
        
        public Builder setUserAgent(final String userAgent) {
            this.userAgent = userAgent;
            return this.self();
        }
        
        public String getUserAgent() {
            return this.userAgent;
        }
        
        public Builder setRequestReason(final String requestReason) {
            this.requestReason = requestReason;
            return this.self();
        }
        
        public String getRequestReason() {
            return this.requestReason;
        }
        
        public Builder setUserProject(final String userProject) {
            this.userProject = userProject;
            return this.self();
        }
        
        public String getUserProject() {
            return this.userProject;
        }
        
        public CommonGoogleClientRequestInitializer build() {
            return new CommonGoogleClientRequestInitializer(this);
        }
        
        protected Builder self() {
            return this;
        }
        
        protected Builder() {
        }
    }
}
