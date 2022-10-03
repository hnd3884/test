package com.google.api.client.googleapis.services.json;

import com.google.api.client.util.GenericData;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponse;
import java.io.IOException;
import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonErrorContainer;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpContent;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;

public abstract class AbstractGoogleJsonClientRequest<T> extends AbstractGoogleClientRequest<T>
{
    private final Object jsonContent;
    
    protected AbstractGoogleJsonClientRequest(final AbstractGoogleJsonClient abstractGoogleJsonClient, final String requestMethod, final String uriTemplate, final Object jsonContent, final Class<T> responseClass) {
        super(abstractGoogleJsonClient, requestMethod, uriTemplate, (HttpContent)((jsonContent == null) ? null : new JsonHttpContent(abstractGoogleJsonClient.getJsonFactory(), jsonContent).setWrapperKey(abstractGoogleJsonClient.getObjectParser().getWrapperKeys().isEmpty() ? null : "data")), responseClass);
        this.jsonContent = jsonContent;
    }
    
    @Override
    public AbstractGoogleJsonClient getAbstractGoogleClient() {
        return (AbstractGoogleJsonClient)super.getAbstractGoogleClient();
    }
    
    @Override
    public AbstractGoogleJsonClientRequest<T> setDisableGZipContent(final boolean disableGZipContent) {
        return (AbstractGoogleJsonClientRequest)super.setDisableGZipContent(disableGZipContent);
    }
    
    @Override
    public AbstractGoogleJsonClientRequest<T> setRequestHeaders(final HttpHeaders headers) {
        return (AbstractGoogleJsonClientRequest)super.setRequestHeaders(headers);
    }
    
    public final void queue(final BatchRequest batchRequest, final JsonBatchCallback<T> callback) throws IOException {
        super.queue(batchRequest, GoogleJsonErrorContainer.class, callback);
    }
    
    protected GoogleJsonResponseException newExceptionOnError(final HttpResponse response) {
        return GoogleJsonResponseException.from(this.getAbstractGoogleClient().getJsonFactory(), response);
    }
    
    public Object getJsonContent() {
        return this.jsonContent;
    }
    
    @Override
    public AbstractGoogleJsonClientRequest<T> set(final String fieldName, final Object value) {
        return (AbstractGoogleJsonClientRequest)super.set(fieldName, value);
    }
}
