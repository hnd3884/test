package com.google.api.client.googleapis.testing.services;

import com.google.api.client.util.GenericData;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpContent;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.util.Beta;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;

@Beta
public class MockGoogleClientRequest<T> extends AbstractGoogleClientRequest<T>
{
    public MockGoogleClientRequest(final AbstractGoogleClient client, final String method, final String uriTemplate, final HttpContent content, final Class<T> responseClass) {
        super(client, method, uriTemplate, content, responseClass);
    }
    
    @Override
    public MockGoogleClientRequest<T> setDisableGZipContent(final boolean disableGZipContent) {
        return (MockGoogleClientRequest)super.setDisableGZipContent(disableGZipContent);
    }
    
    @Override
    public MockGoogleClientRequest<T> setRequestHeaders(final HttpHeaders headers) {
        return (MockGoogleClientRequest)super.setRequestHeaders(headers);
    }
    
    @Override
    public MockGoogleClientRequest<T> set(final String fieldName, final Object value) {
        return (MockGoogleClientRequest)super.set(fieldName, value);
    }
}
