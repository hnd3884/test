package com.google.api.client.googleapis.services;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.googleapis.batch.BatchRequest;
import java.io.IOException;
import com.google.api.client.util.Strings;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.http.HttpRequestFactory;
import java.util.logging.Logger;

public abstract class AbstractGoogleClient
{
    private static final Logger logger;
    private final HttpRequestFactory requestFactory;
    private final GoogleClientRequestInitializer googleClientRequestInitializer;
    private final String rootUrl;
    private final String servicePath;
    private final String batchPath;
    private final String applicationName;
    private final ObjectParser objectParser;
    private final boolean suppressPatternChecks;
    private final boolean suppressRequiredParameterChecks;
    
    protected AbstractGoogleClient(final Builder builder) {
        this.googleClientRequestInitializer = builder.googleClientRequestInitializer;
        this.rootUrl = normalizeRootUrl(builder.rootUrl);
        this.servicePath = normalizeServicePath(builder.servicePath);
        this.batchPath = builder.batchPath;
        if (Strings.isNullOrEmpty(builder.applicationName)) {
            AbstractGoogleClient.logger.warning("Application name is not set. Call Builder#setApplicationName.");
        }
        this.applicationName = builder.applicationName;
        this.requestFactory = ((builder.httpRequestInitializer == null) ? builder.transport.createRequestFactory() : builder.transport.createRequestFactory(builder.httpRequestInitializer));
        this.objectParser = builder.objectParser;
        this.suppressPatternChecks = builder.suppressPatternChecks;
        this.suppressRequiredParameterChecks = builder.suppressRequiredParameterChecks;
    }
    
    public final String getRootUrl() {
        return this.rootUrl;
    }
    
    public final String getServicePath() {
        return this.servicePath;
    }
    
    public final String getBaseUrl() {
        return this.rootUrl + this.servicePath;
    }
    
    public final String getApplicationName() {
        return this.applicationName;
    }
    
    public final HttpRequestFactory getRequestFactory() {
        return this.requestFactory;
    }
    
    public final GoogleClientRequestInitializer getGoogleClientRequestInitializer() {
        return this.googleClientRequestInitializer;
    }
    
    public ObjectParser getObjectParser() {
        return this.objectParser;
    }
    
    protected void initialize(final AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        if (this.getGoogleClientRequestInitializer() != null) {
            this.getGoogleClientRequestInitializer().initialize(httpClientRequest);
        }
    }
    
    public final BatchRequest batch() {
        return this.batch(null);
    }
    
    public final BatchRequest batch(final HttpRequestInitializer httpRequestInitializer) {
        final BatchRequest batch = new BatchRequest(this.getRequestFactory().getTransport(), httpRequestInitializer);
        if (Strings.isNullOrEmpty(this.batchPath)) {
            batch.setBatchUrl(new GenericUrl(this.getRootUrl() + "batch"));
        }
        else {
            batch.setBatchUrl(new GenericUrl(this.getRootUrl() + this.batchPath));
        }
        return batch;
    }
    
    public final boolean getSuppressPatternChecks() {
        return this.suppressPatternChecks;
    }
    
    public final boolean getSuppressRequiredParameterChecks() {
        return this.suppressRequiredParameterChecks;
    }
    
    static String normalizeRootUrl(String rootUrl) {
        Preconditions.checkNotNull((Object)rootUrl, (Object)"root URL cannot be null.");
        if (!rootUrl.endsWith("/")) {
            rootUrl += "/";
        }
        return rootUrl;
    }
    
    static String normalizeServicePath(String servicePath) {
        Preconditions.checkNotNull((Object)servicePath, (Object)"service path cannot be null");
        if (servicePath.length() == 1) {
            Preconditions.checkArgument("/".equals(servicePath), (Object)"service path must equal \"/\" if it is of length 1.");
            servicePath = "";
        }
        else if (servicePath.length() > 0) {
            if (!servicePath.endsWith("/")) {
                servicePath += "/";
            }
            if (servicePath.startsWith("/")) {
                servicePath = servicePath.substring(1);
            }
        }
        return servicePath;
    }
    
    static {
        logger = Logger.getLogger(AbstractGoogleClient.class.getName());
    }
    
    public abstract static class Builder
    {
        final HttpTransport transport;
        GoogleClientRequestInitializer googleClientRequestInitializer;
        HttpRequestInitializer httpRequestInitializer;
        final ObjectParser objectParser;
        String rootUrl;
        String servicePath;
        String batchPath;
        String applicationName;
        boolean suppressPatternChecks;
        boolean suppressRequiredParameterChecks;
        
        protected Builder(final HttpTransport transport, final String rootUrl, final String servicePath, final ObjectParser objectParser, final HttpRequestInitializer httpRequestInitializer) {
            this.transport = (HttpTransport)Preconditions.checkNotNull((Object)transport);
            this.objectParser = objectParser;
            this.setRootUrl(rootUrl);
            this.setServicePath(servicePath);
            this.httpRequestInitializer = httpRequestInitializer;
        }
        
        public abstract AbstractGoogleClient build();
        
        public final HttpTransport getTransport() {
            return this.transport;
        }
        
        public ObjectParser getObjectParser() {
            return this.objectParser;
        }
        
        public final String getRootUrl() {
            return this.rootUrl;
        }
        
        public Builder setRootUrl(final String rootUrl) {
            this.rootUrl = AbstractGoogleClient.normalizeRootUrl(rootUrl);
            return this;
        }
        
        public final String getServicePath() {
            return this.servicePath;
        }
        
        public Builder setServicePath(final String servicePath) {
            this.servicePath = AbstractGoogleClient.normalizeServicePath(servicePath);
            return this;
        }
        
        public Builder setBatchPath(final String batchPath) {
            this.batchPath = batchPath;
            return this;
        }
        
        public final GoogleClientRequestInitializer getGoogleClientRequestInitializer() {
            return this.googleClientRequestInitializer;
        }
        
        public Builder setGoogleClientRequestInitializer(final GoogleClientRequestInitializer googleClientRequestInitializer) {
            this.googleClientRequestInitializer = googleClientRequestInitializer;
            return this;
        }
        
        public final HttpRequestInitializer getHttpRequestInitializer() {
            return this.httpRequestInitializer;
        }
        
        public Builder setHttpRequestInitializer(final HttpRequestInitializer httpRequestInitializer) {
            this.httpRequestInitializer = httpRequestInitializer;
            return this;
        }
        
        public final String getApplicationName() {
            return this.applicationName;
        }
        
        public Builder setApplicationName(final String applicationName) {
            this.applicationName = applicationName;
            return this;
        }
        
        public final boolean getSuppressPatternChecks() {
            return this.suppressPatternChecks;
        }
        
        public Builder setSuppressPatternChecks(final boolean suppressPatternChecks) {
            this.suppressPatternChecks = suppressPatternChecks;
            return this;
        }
        
        public final boolean getSuppressRequiredParameterChecks() {
            return this.suppressRequiredParameterChecks;
        }
        
        public Builder setSuppressRequiredParameterChecks(final boolean suppressRequiredParameterChecks) {
            this.suppressRequiredParameterChecks = suppressRequiredParameterChecks;
            return this;
        }
        
        public Builder setSuppressAllChecks(final boolean suppressAllChecks) {
            return this.setSuppressPatternChecks(true).setSuppressRequiredParameterChecks(true);
        }
    }
}
