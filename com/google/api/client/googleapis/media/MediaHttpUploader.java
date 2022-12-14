package com.google.api.client.googleapis.media;

import com.google.api.client.util.Beta;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.ByteStreams;
import com.google.api.client.http.HttpEncoding;
import com.google.api.client.http.GZipEncoding;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.http.EmptyContent;
import java.io.BufferedInputStream;
import java.util.Map;
import java.util.Collection;
import java.util.Arrays;
import com.google.api.client.http.MultipartContent;
import java.io.IOException;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.Sleeper;
import java.io.InputStream;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.AbstractInputStreamContent;

public final class MediaHttpUploader
{
    public static final String CONTENT_LENGTH_HEADER = "X-Upload-Content-Length";
    public static final String CONTENT_TYPE_HEADER = "X-Upload-Content-Type";
    private UploadState uploadState;
    static final int MB = 1048576;
    private static final int KB = 1024;
    public static final int MINIMUM_CHUNK_SIZE = 262144;
    public static final int DEFAULT_CHUNK_SIZE = 10485760;
    private final AbstractInputStreamContent mediaContent;
    private final HttpRequestFactory requestFactory;
    private final HttpTransport transport;
    private HttpContent metadata;
    private long mediaContentLength;
    private boolean isMediaContentLengthCalculated;
    private String initiationRequestMethod;
    private HttpHeaders initiationHeaders;
    private HttpRequest currentRequest;
    private InputStream contentInputStream;
    private boolean directUploadEnabled;
    private MediaHttpUploaderProgressListener progressListener;
    String mediaContentLengthStr;
    private long totalBytesServerReceived;
    private int chunkSize;
    private Byte cachedByte;
    private long totalBytesClientSent;
    private int currentChunkLength;
    private byte[] currentRequestContentBuffer;
    private boolean disableGZipContent;
    Sleeper sleeper;
    
    public MediaHttpUploader(final AbstractInputStreamContent mediaContent, final HttpTransport transport, final HttpRequestInitializer httpRequestInitializer) {
        this.uploadState = UploadState.NOT_STARTED;
        this.initiationRequestMethod = "POST";
        this.initiationHeaders = new HttpHeaders();
        this.mediaContentLengthStr = "*";
        this.chunkSize = 10485760;
        this.sleeper = Sleeper.DEFAULT;
        this.mediaContent = (AbstractInputStreamContent)Preconditions.checkNotNull((Object)mediaContent);
        this.transport = (HttpTransport)Preconditions.checkNotNull((Object)transport);
        this.requestFactory = ((httpRequestInitializer == null) ? transport.createRequestFactory() : transport.createRequestFactory(httpRequestInitializer));
    }
    
    public HttpResponse upload(final GenericUrl initiationRequestUrl) throws IOException {
        Preconditions.checkArgument(this.uploadState == UploadState.NOT_STARTED);
        if (this.directUploadEnabled) {
            return this.directUpload(initiationRequestUrl);
        }
        return this.resumableUpload(initiationRequestUrl);
    }
    
    private HttpResponse directUpload(final GenericUrl initiationRequestUrl) throws IOException {
        this.updateStateAndNotifyListener(UploadState.MEDIA_IN_PROGRESS);
        HttpContent content = (HttpContent)this.mediaContent;
        if (this.metadata != null) {
            content = (HttpContent)new MultipartContent().setContentParts((Collection)Arrays.asList(this.metadata, (HttpContent)this.mediaContent));
            initiationRequestUrl.put("uploadType", (Object)"multipart");
        }
        else {
            initiationRequestUrl.put("uploadType", (Object)"media");
        }
        final HttpRequest request = this.requestFactory.buildRequest(this.initiationRequestMethod, initiationRequestUrl, content);
        request.getHeaders().putAll((Map)this.initiationHeaders);
        final HttpResponse response = this.executeCurrentRequest(request);
        boolean responseProcessed = false;
        try {
            if (this.isMediaLengthKnown()) {
                this.totalBytesServerReceived = this.getMediaContentLength();
            }
            this.updateStateAndNotifyListener(UploadState.MEDIA_COMPLETE);
            responseProcessed = true;
        }
        finally {
            if (!responseProcessed) {
                response.disconnect();
            }
        }
        return response;
    }
    
    private HttpResponse resumableUpload(final GenericUrl initiationRequestUrl) throws IOException {
        final HttpResponse initialResponse = this.executeUploadInitiation(initiationRequestUrl);
        if (!initialResponse.isSuccessStatusCode()) {
            return initialResponse;
        }
        GenericUrl uploadUrl;
        try {
            uploadUrl = new GenericUrl(initialResponse.getHeaders().getLocation());
        }
        finally {
            initialResponse.disconnect();
        }
        this.contentInputStream = this.mediaContent.getInputStream();
        if (!this.contentInputStream.markSupported() && this.isMediaLengthKnown()) {
            this.contentInputStream = new BufferedInputStream(this.contentInputStream);
        }
        while (true) {
            final ContentChunk contentChunk = this.buildContentChunk();
            (this.currentRequest = this.requestFactory.buildPutRequest(uploadUrl, (HttpContent)null)).setContent((HttpContent)contentChunk.getContent());
            this.currentRequest.getHeaders().setContentRange(contentChunk.getContentRange());
            new MediaUploadErrorHandler(this, this.currentRequest);
            HttpResponse response;
            if (this.isMediaLengthKnown()) {
                response = this.executeCurrentRequestWithoutGZip(this.currentRequest);
            }
            else {
                response = this.executeCurrentRequest(this.currentRequest);
            }
            boolean returningResponse = false;
            try {
                if (response.isSuccessStatusCode()) {
                    this.totalBytesServerReceived = this.getMediaContentLength();
                    if (this.mediaContent.getCloseInputStream()) {
                        this.contentInputStream.close();
                    }
                    this.updateStateAndNotifyListener(UploadState.MEDIA_COMPLETE);
                    returningResponse = true;
                    return response;
                }
                if (response.getStatusCode() != 308) {
                    if (this.mediaContent.getCloseInputStream()) {
                        this.contentInputStream.close();
                    }
                    returningResponse = true;
                    return response;
                }
                final String updatedUploadUrl = response.getHeaders().getLocation();
                if (updatedUploadUrl != null) {
                    uploadUrl = new GenericUrl(updatedUploadUrl);
                }
                final long newBytesServerReceived = this.getNextByteIndex(response.getHeaders().getRange());
                final long currentBytesServerReceived = newBytesServerReceived - this.totalBytesServerReceived;
                Preconditions.checkState(currentBytesServerReceived >= 0L && currentBytesServerReceived <= this.currentChunkLength);
                final long copyBytes = this.currentChunkLength - currentBytesServerReceived;
                if (this.isMediaLengthKnown()) {
                    if (copyBytes > 0L) {
                        this.contentInputStream.reset();
                        final long actualSkipValue = this.contentInputStream.skip(currentBytesServerReceived);
                        Preconditions.checkState(currentBytesServerReceived == actualSkipValue);
                    }
                }
                else if (copyBytes == 0L) {
                    this.currentRequestContentBuffer = null;
                }
                this.totalBytesServerReceived = newBytesServerReceived;
                this.updateStateAndNotifyListener(UploadState.MEDIA_IN_PROGRESS);
            }
            finally {
                if (!returningResponse) {
                    response.disconnect();
                }
            }
        }
    }
    
    private boolean isMediaLengthKnown() throws IOException {
        return this.getMediaContentLength() >= 0L;
    }
    
    private long getMediaContentLength() throws IOException {
        if (!this.isMediaContentLengthCalculated) {
            this.mediaContentLength = this.mediaContent.getLength();
            this.isMediaContentLengthCalculated = true;
        }
        return this.mediaContentLength;
    }
    
    private HttpResponse executeUploadInitiation(final GenericUrl initiationRequestUrl) throws IOException {
        this.updateStateAndNotifyListener(UploadState.INITIATION_STARTED);
        initiationRequestUrl.put("uploadType", (Object)"resumable");
        final HttpContent content = (HttpContent)((this.metadata == null) ? new EmptyContent() : this.metadata);
        final HttpRequest request = this.requestFactory.buildRequest(this.initiationRequestMethod, initiationRequestUrl, content);
        this.initiationHeaders.set("X-Upload-Content-Type", (Object)this.mediaContent.getType());
        if (this.isMediaLengthKnown()) {
            this.initiationHeaders.set("X-Upload-Content-Length", (Object)this.getMediaContentLength());
        }
        request.getHeaders().putAll((Map)this.initiationHeaders);
        final HttpResponse response = this.executeCurrentRequest(request);
        boolean notificationCompleted = false;
        try {
            this.updateStateAndNotifyListener(UploadState.INITIATION_COMPLETE);
            notificationCompleted = true;
        }
        finally {
            if (!notificationCompleted) {
                response.disconnect();
            }
        }
        return response;
    }
    
    private HttpResponse executeCurrentRequestWithoutGZip(final HttpRequest request) throws IOException {
        new MethodOverride().intercept(request);
        request.setThrowExceptionOnExecuteError(false);
        final HttpResponse response = request.execute();
        return response;
    }
    
    private HttpResponse executeCurrentRequest(final HttpRequest request) throws IOException {
        if (!this.disableGZipContent && !(request.getContent() instanceof EmptyContent)) {
            request.setEncoding((HttpEncoding)new GZipEncoding());
        }
        final HttpResponse response = this.executeCurrentRequestWithoutGZip(request);
        return response;
    }
    
    private ContentChunk buildContentChunk() throws IOException {
        int blockSize;
        if (this.isMediaLengthKnown()) {
            blockSize = (int)Math.min(this.chunkSize, this.getMediaContentLength() - this.totalBytesServerReceived);
        }
        else {
            blockSize = this.chunkSize;
        }
        int actualBlockSize = blockSize;
        AbstractInputStreamContent contentChunk;
        if (this.isMediaLengthKnown()) {
            this.contentInputStream.mark(blockSize);
            final InputStream limitInputStream = ByteStreams.limit(this.contentInputStream, (long)blockSize);
            contentChunk = (AbstractInputStreamContent)new InputStreamContent(this.mediaContent.getType(), limitInputStream).setRetrySupported(true).setLength((long)blockSize).setCloseInputStream(false);
            this.mediaContentLengthStr = String.valueOf(this.getMediaContentLength());
        }
        else {
            int copyBytes = 0;
            int bytesAllowedToRead;
            if (this.currentRequestContentBuffer == null) {
                bytesAllowedToRead = ((this.cachedByte == null) ? (blockSize + 1) : blockSize);
                this.currentRequestContentBuffer = new byte[blockSize + 1];
                if (this.cachedByte != null) {
                    this.currentRequestContentBuffer[0] = this.cachedByte;
                }
            }
            else {
                copyBytes = (int)(this.totalBytesClientSent - this.totalBytesServerReceived);
                System.arraycopy(this.currentRequestContentBuffer, this.currentChunkLength - copyBytes, this.currentRequestContentBuffer, 0, copyBytes);
                if (this.cachedByte != null) {
                    this.currentRequestContentBuffer[copyBytes] = this.cachedByte;
                }
                bytesAllowedToRead = blockSize - copyBytes;
            }
            final int actualBytesRead = ByteStreams.read(this.contentInputStream, this.currentRequestContentBuffer, blockSize + 1 - bytesAllowedToRead, bytesAllowedToRead);
            if (actualBytesRead < bytesAllowedToRead) {
                actualBlockSize = copyBytes + Math.max(0, actualBytesRead);
                if (this.cachedByte != null) {
                    ++actualBlockSize;
                    this.cachedByte = null;
                }
                if (this.mediaContentLengthStr.equals("*")) {
                    this.mediaContentLengthStr = String.valueOf(this.totalBytesServerReceived + actualBlockSize);
                }
            }
            else {
                this.cachedByte = this.currentRequestContentBuffer[blockSize];
            }
            contentChunk = (AbstractInputStreamContent)new ByteArrayContent(this.mediaContent.getType(), this.currentRequestContentBuffer, 0, actualBlockSize);
            this.totalBytesClientSent = this.totalBytesServerReceived + actualBlockSize;
        }
        this.currentChunkLength = actualBlockSize;
        String contentRange;
        if (actualBlockSize == 0) {
            contentRange = "bytes */" + this.mediaContentLengthStr;
        }
        else {
            contentRange = "bytes " + this.totalBytesServerReceived + "-" + (this.totalBytesServerReceived + actualBlockSize - 1L) + "/" + this.mediaContentLengthStr;
        }
        return new ContentChunk(contentChunk, contentRange);
    }
    
    @Beta
    void serverErrorCallback() throws IOException {
        Preconditions.checkNotNull((Object)this.currentRequest, (Object)"The current request should not be null");
        this.currentRequest.setContent((HttpContent)new EmptyContent());
        this.currentRequest.getHeaders().setContentRange("bytes */" + this.mediaContentLengthStr);
    }
    
    private long getNextByteIndex(final String rangeHeader) {
        if (rangeHeader == null) {
            return 0L;
        }
        return Long.parseLong(rangeHeader.substring(rangeHeader.indexOf(45) + 1)) + 1L;
    }
    
    public HttpContent getMetadata() {
        return this.metadata;
    }
    
    public MediaHttpUploader setMetadata(final HttpContent metadata) {
        this.metadata = metadata;
        return this;
    }
    
    public HttpContent getMediaContent() {
        return (HttpContent)this.mediaContent;
    }
    
    public HttpTransport getTransport() {
        return this.transport;
    }
    
    public MediaHttpUploader setDirectUploadEnabled(final boolean directUploadEnabled) {
        this.directUploadEnabled = directUploadEnabled;
        return this;
    }
    
    public boolean isDirectUploadEnabled() {
        return this.directUploadEnabled;
    }
    
    public MediaHttpUploader setProgressListener(final MediaHttpUploaderProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }
    
    public MediaHttpUploaderProgressListener getProgressListener() {
        return this.progressListener;
    }
    
    public MediaHttpUploader setChunkSize(final int chunkSize) {
        Preconditions.checkArgument(chunkSize > 0 && chunkSize % 262144 == 0, (Object)"chunkSize must be a positive multiple of 262144.");
        this.chunkSize = chunkSize;
        return this;
    }
    
    public int getChunkSize() {
        return this.chunkSize;
    }
    
    public boolean getDisableGZipContent() {
        return this.disableGZipContent;
    }
    
    public MediaHttpUploader setDisableGZipContent(final boolean disableGZipContent) {
        this.disableGZipContent = disableGZipContent;
        return this;
    }
    
    public Sleeper getSleeper() {
        return this.sleeper;
    }
    
    public MediaHttpUploader setSleeper(final Sleeper sleeper) {
        this.sleeper = sleeper;
        return this;
    }
    
    public String getInitiationRequestMethod() {
        return this.initiationRequestMethod;
    }
    
    public MediaHttpUploader setInitiationRequestMethod(final String initiationRequestMethod) {
        Preconditions.checkArgument(initiationRequestMethod.equals("POST") || initiationRequestMethod.equals("PUT") || initiationRequestMethod.equals("PATCH"));
        this.initiationRequestMethod = initiationRequestMethod;
        return this;
    }
    
    public MediaHttpUploader setInitiationHeaders(final HttpHeaders initiationHeaders) {
        this.initiationHeaders = initiationHeaders;
        return this;
    }
    
    public HttpHeaders getInitiationHeaders() {
        return this.initiationHeaders;
    }
    
    public long getNumBytesUploaded() {
        return this.totalBytesServerReceived;
    }
    
    private void updateStateAndNotifyListener(final UploadState uploadState) throws IOException {
        this.uploadState = uploadState;
        if (this.progressListener != null) {
            this.progressListener.progressChanged(this);
        }
    }
    
    public UploadState getUploadState() {
        return this.uploadState;
    }
    
    public double getProgress() throws IOException {
        Preconditions.checkArgument(this.isMediaLengthKnown(), (Object)"Cannot call getProgress() if the specified AbstractInputStreamContent has no content length. Use  getNumBytesUploaded() to denote progress instead.");
        return (this.getMediaContentLength() == 0L) ? 0.0 : (this.totalBytesServerReceived / (double)this.getMediaContentLength());
    }
    
    public enum UploadState
    {
        NOT_STARTED, 
        INITIATION_STARTED, 
        INITIATION_COMPLETE, 
        MEDIA_IN_PROGRESS, 
        MEDIA_COMPLETE;
    }
    
    private static class ContentChunk
    {
        private final AbstractInputStreamContent content;
        private final String contentRange;
        
        ContentChunk(final AbstractInputStreamContent content, final String contentRange) {
            this.content = content;
            this.contentRange = contentRange;
        }
        
        AbstractInputStreamContent getContent() {
            return this.content;
        }
        
        String getContentRange() {
            return this.contentRange;
        }
    }
}
