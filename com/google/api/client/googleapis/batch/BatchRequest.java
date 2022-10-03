package com.google.api.client.googleapis.batch;

import java.io.InputStream;
import com.google.api.client.http.HttpResponse;
import java.util.Iterator;
import java.io.BufferedInputStream;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpContent;
import java.util.logging.Level;
import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.Preconditions;
import java.util.ArrayList;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Sleeper;
import java.util.List;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.GenericUrl;
import java.util.logging.Logger;

public final class BatchRequest
{
    private static final String GLOBAL_BATCH_ENDPOINT = "https://www.googleapis.com/batch";
    private static final String GLOBAL_BATCH_ENDPOINT_WARNING = "You are using the global batch endpoint which will soon be shut down. Please instantiate your BatchRequest via your service client's `batch(HttpRequestInitializer)` method. For an example, please see https://github.com/googleapis/google-api-java-client#batching.";
    private static final Logger LOGGER;
    private GenericUrl batchUrl;
    private final HttpRequestFactory requestFactory;
    List<RequestInfo<?, ?>> requestInfos;
    private Sleeper sleeper;
    
    @Deprecated
    public BatchRequest(final HttpTransport transport, final HttpRequestInitializer httpRequestInitializer) {
        this.batchUrl = new GenericUrl("https://www.googleapis.com/batch");
        this.requestInfos = new ArrayList<RequestInfo<?, ?>>();
        this.sleeper = Sleeper.DEFAULT;
        this.requestFactory = ((httpRequestInitializer == null) ? transport.createRequestFactory() : transport.createRequestFactory(httpRequestInitializer));
    }
    
    public BatchRequest setBatchUrl(final GenericUrl batchUrl) {
        this.batchUrl = batchUrl;
        return this;
    }
    
    public GenericUrl getBatchUrl() {
        return this.batchUrl;
    }
    
    public Sleeper getSleeper() {
        return this.sleeper;
    }
    
    public BatchRequest setSleeper(final Sleeper sleeper) {
        this.sleeper = (Sleeper)Preconditions.checkNotNull((Object)sleeper);
        return this;
    }
    
    public <T, E> BatchRequest queue(final HttpRequest httpRequest, final Class<T> dataClass, final Class<E> errorClass, final BatchCallback<T, E> callback) throws IOException {
        Preconditions.checkNotNull((Object)httpRequest);
        Preconditions.checkNotNull((Object)callback);
        Preconditions.checkNotNull((Object)dataClass);
        Preconditions.checkNotNull((Object)errorClass);
        this.requestInfos.add(new RequestInfo<Object, Object>((BatchCallback<Object, Object>)callback, (Class<Object>)dataClass, (Class<Object>)errorClass, httpRequest));
        return this;
    }
    
    public int size() {
        return this.requestInfos.size();
    }
    
    public void execute() throws IOException {
        Preconditions.checkState(!this.requestInfos.isEmpty());
        if ("https://www.googleapis.com/batch".equals(this.batchUrl.toString())) {
            BatchRequest.LOGGER.log(Level.WARNING, "You are using the global batch endpoint which will soon be shut down. Please instantiate your BatchRequest via your service client's `batch(HttpRequestInitializer)` method. For an example, please see https://github.com/googleapis/google-api-java-client#batching.");
        }
        final HttpRequest batchRequest = this.requestFactory.buildPostRequest(this.batchUrl, (HttpContent)null);
        final HttpExecuteInterceptor originalInterceptor = batchRequest.getInterceptor();
        batchRequest.setInterceptor((HttpExecuteInterceptor)new BatchInterceptor(originalInterceptor));
        int retriesRemaining = batchRequest.getNumberOfRetries();
        boolean retryAllowed;
        do {
            retryAllowed = (retriesRemaining > 0);
            final MultipartContent batchContent = new MultipartContent();
            batchContent.getMediaType().setSubType("mixed");
            int contentId = 1;
            for (final RequestInfo<?, ?> requestInfo : this.requestInfos) {
                batchContent.addPart(new MultipartContent.Part(new HttpHeaders().setAcceptEncoding((String)null).set("Content-ID", (Object)(contentId++)), (HttpContent)new HttpRequestContent(requestInfo.request)));
            }
            batchRequest.setContent((HttpContent)batchContent);
            final HttpResponse response = batchRequest.execute();
            BatchUnparsedResponse batchResponse;
            try {
                final String boundary = "--" + response.getMediaType().getParameter("boundary");
                final InputStream contentStream = new BufferedInputStream(response.getContent());
                batchResponse = new BatchUnparsedResponse(contentStream, boundary, this.requestInfos, retryAllowed);
                while (batchResponse.hasNext) {
                    batchResponse.parseNextResponse();
                }
            }
            finally {
                response.disconnect();
            }
            final List<RequestInfo<?, ?>> unsuccessfulRequestInfos = batchResponse.unsuccessfulRequestInfos;
            if (unsuccessfulRequestInfos.isEmpty()) {
                break;
            }
            this.requestInfos = unsuccessfulRequestInfos;
            --retriesRemaining;
        } while (retryAllowed);
        this.requestInfos.clear();
    }
    
    static {
        LOGGER = Logger.getLogger(BatchRequest.class.getName());
    }
    
    static class RequestInfo<T, E>
    {
        final BatchCallback<T, E> callback;
        final Class<T> dataClass;
        final Class<E> errorClass;
        final HttpRequest request;
        
        RequestInfo(final BatchCallback<T, E> callback, final Class<T> dataClass, final Class<E> errorClass, final HttpRequest request) {
            this.callback = callback;
            this.dataClass = dataClass;
            this.errorClass = errorClass;
            this.request = request;
        }
    }
    
    class BatchInterceptor implements HttpExecuteInterceptor
    {
        private HttpExecuteInterceptor originalInterceptor;
        
        BatchInterceptor(final HttpExecuteInterceptor originalInterceptor) {
            this.originalInterceptor = originalInterceptor;
        }
        
        public void intercept(final HttpRequest batchRequest) throws IOException {
            if (this.originalInterceptor != null) {
                this.originalInterceptor.intercept(batchRequest);
            }
            for (final RequestInfo<?, ?> requestInfo : BatchRequest.this.requestInfos) {
                final HttpExecuteInterceptor interceptor = requestInfo.request.getInterceptor();
                if (interceptor != null) {
                    interceptor.intercept(requestInfo.request);
                }
            }
        }
    }
}
