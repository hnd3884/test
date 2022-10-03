package com.google.api.services.directory;

import java.io.IOException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;

public class DirectoryRequestInitializer extends CommonGoogleJsonClientRequestInitializer
{
    public DirectoryRequestInitializer() {
    }
    
    public DirectoryRequestInitializer(final String key) {
        super(key);
    }
    
    public DirectoryRequestInitializer(final String key, final String userIp) {
        super(key, userIp);
    }
    
    public final void initializeJsonRequest(final AbstractGoogleJsonClientRequest<?> request) throws IOException {
        super.initializeJsonRequest((AbstractGoogleJsonClientRequest)request);
        this.initializeDirectoryRequest((DirectoryRequest)request);
    }
    
    protected void initializeDirectoryRequest(final DirectoryRequest<?> request) throws IOException {
    }
}
