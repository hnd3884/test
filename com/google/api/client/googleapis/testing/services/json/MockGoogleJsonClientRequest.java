package com.google.api.client.googleapis.testing.services.json;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.util.Beta;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;

@Beta
public class MockGoogleJsonClientRequest<T> extends AbstractGoogleJsonClientRequest<T>
{
    public MockGoogleJsonClientRequest(final AbstractGoogleJsonClient client, final String method, final String uriTemplate, final Object content, final Class<T> responseClass) {
        super(client, method, uriTemplate, content, responseClass);
    }
    
    @Override
    public MockGoogleJsonClient getAbstractGoogleClient() {
        return (MockGoogleJsonClient)super.getAbstractGoogleClient();
    }
    
    @Override
    public MockGoogleJsonClientRequest<T> setDisableGZipContent(final boolean disableGZipContent) {
        return (MockGoogleJsonClientRequest)super.setDisableGZipContent(disableGZipContent);
    }
    
    @Override
    public MockGoogleJsonClientRequest<T> setRequestHeaders(final HttpHeaders headers) {
        return (MockGoogleJsonClientRequest)super.setRequestHeaders(headers);
    }
}
