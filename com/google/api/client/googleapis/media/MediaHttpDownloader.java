package com.google.api.client.googleapis.media;

import com.google.api.client.http.HttpRequest;
import com.google.common.io.ByteStreams;
import java.util.Map;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.MoreObjects;
import java.io.IOException;
import com.google.api.client.http.HttpHeaders;
import java.io.OutputStream;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpRequestFactory;

public final class MediaHttpDownloader
{
    public static final int MAXIMUM_CHUNK_SIZE = 33554432;
    private final HttpRequestFactory requestFactory;
    private final HttpTransport transport;
    private boolean directDownloadEnabled;
    private MediaHttpDownloaderProgressListener progressListener;
    private int chunkSize;
    private long mediaContentLength;
    private DownloadState downloadState;
    private long bytesDownloaded;
    private long lastBytePos;
    
    public MediaHttpDownloader(final HttpTransport transport, final HttpRequestInitializer httpRequestInitializer) {
        this.directDownloadEnabled = false;
        this.chunkSize = 33554432;
        this.downloadState = DownloadState.NOT_STARTED;
        this.lastBytePos = -1L;
        this.transport = (HttpTransport)Preconditions.checkNotNull((Object)transport);
        this.requestFactory = ((httpRequestInitializer == null) ? transport.createRequestFactory() : transport.createRequestFactory(httpRequestInitializer));
    }
    
    public void download(final GenericUrl requestUrl, final OutputStream outputStream) throws IOException {
        this.download(requestUrl, null, outputStream);
    }
    
    public void download(final GenericUrl requestUrl, final HttpHeaders requestHeaders, final OutputStream outputStream) throws IOException {
        Preconditions.checkArgument(this.downloadState == DownloadState.NOT_STARTED);
        requestUrl.put("alt", (Object)"media");
        if (this.directDownloadEnabled) {
            this.updateStateAndNotifyListener(DownloadState.MEDIA_IN_PROGRESS);
            final HttpResponse response = this.executeCurrentRequest(this.lastBytePos, requestUrl, requestHeaders, outputStream);
            this.mediaContentLength = (long)MoreObjects.firstNonNull((Object)response.getHeaders().getContentLength(), (Object)this.mediaContentLength);
            this.bytesDownloaded = this.mediaContentLength;
            this.updateStateAndNotifyListener(DownloadState.MEDIA_COMPLETE);
            return;
        }
        while (true) {
            long currentRequestLastBytePos = this.bytesDownloaded + this.chunkSize - 1L;
            if (this.lastBytePos != -1L) {
                currentRequestLastBytePos = Math.min(this.lastBytePos, currentRequestLastBytePos);
            }
            final HttpResponse response2 = this.executeCurrentRequest(currentRequestLastBytePos, requestUrl, requestHeaders, outputStream);
            final String contentRange = response2.getHeaders().getContentRange();
            final long nextByteIndex = this.getNextByteIndex(contentRange);
            this.setMediaContentLength(contentRange);
            if (this.lastBytePos != -1L && this.lastBytePos <= nextByteIndex) {
                this.bytesDownloaded = this.lastBytePos;
                this.updateStateAndNotifyListener(DownloadState.MEDIA_COMPLETE);
                return;
            }
            if (this.mediaContentLength <= nextByteIndex) {
                this.bytesDownloaded = this.mediaContentLength;
                this.updateStateAndNotifyListener(DownloadState.MEDIA_COMPLETE);
                return;
            }
            this.bytesDownloaded = nextByteIndex;
            this.updateStateAndNotifyListener(DownloadState.MEDIA_IN_PROGRESS);
        }
    }
    
    private HttpResponse executeCurrentRequest(final long currentRequestLastBytePos, final GenericUrl requestUrl, final HttpHeaders requestHeaders, final OutputStream outputStream) throws IOException {
        final HttpRequest request = this.requestFactory.buildGetRequest(requestUrl);
        if (requestHeaders != null) {
            request.getHeaders().putAll((Map)requestHeaders);
        }
        if (this.bytesDownloaded != 0L || currentRequestLastBytePos != -1L) {
            final StringBuilder rangeHeader = new StringBuilder();
            rangeHeader.append("bytes=").append(this.bytesDownloaded).append("-");
            if (currentRequestLastBytePos != -1L) {
                rangeHeader.append(currentRequestLastBytePos);
            }
            request.getHeaders().setRange(rangeHeader.toString());
        }
        final HttpResponse response = request.execute();
        try {
            ByteStreams.copy(response.getContent(), outputStream);
        }
        finally {
            response.disconnect();
        }
        return response;
    }
    
    private long getNextByteIndex(final String rangeHeader) {
        if (rangeHeader == null) {
            return 0L;
        }
        return Long.parseLong(rangeHeader.substring(rangeHeader.indexOf(45) + 1, rangeHeader.indexOf(47))) + 1L;
    }
    
    public MediaHttpDownloader setBytesDownloaded(final long bytesDownloaded) {
        Preconditions.checkArgument(bytesDownloaded >= 0L);
        this.bytesDownloaded = bytesDownloaded;
        return this;
    }
    
    public MediaHttpDownloader setContentRange(final long firstBytePos, final long lastBytePos) {
        Preconditions.checkArgument(lastBytePos >= firstBytePos);
        this.setBytesDownloaded(firstBytePos);
        this.lastBytePos = lastBytePos;
        return this;
    }
    
    @Deprecated
    public MediaHttpDownloader setContentRange(final long firstBytePos, final int lastBytePos) {
        return this.setContentRange(firstBytePos, (long)lastBytePos);
    }
    
    private void setMediaContentLength(final String rangeHeader) {
        if (rangeHeader == null) {
            return;
        }
        if (this.mediaContentLength == 0L) {
            this.mediaContentLength = Long.parseLong(rangeHeader.substring(rangeHeader.indexOf(47) + 1));
        }
    }
    
    public boolean isDirectDownloadEnabled() {
        return this.directDownloadEnabled;
    }
    
    public MediaHttpDownloader setDirectDownloadEnabled(final boolean directDownloadEnabled) {
        this.directDownloadEnabled = directDownloadEnabled;
        return this;
    }
    
    public MediaHttpDownloader setProgressListener(final MediaHttpDownloaderProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }
    
    public MediaHttpDownloaderProgressListener getProgressListener() {
        return this.progressListener;
    }
    
    public HttpTransport getTransport() {
        return this.transport;
    }
    
    public MediaHttpDownloader setChunkSize(final int chunkSize) {
        Preconditions.checkArgument(chunkSize > 0 && chunkSize <= 33554432);
        this.chunkSize = chunkSize;
        return this;
    }
    
    public int getChunkSize() {
        return this.chunkSize;
    }
    
    public long getNumBytesDownloaded() {
        return this.bytesDownloaded;
    }
    
    public long getLastBytePosition() {
        return this.lastBytePos;
    }
    
    private void updateStateAndNotifyListener(final DownloadState downloadState) throws IOException {
        this.downloadState = downloadState;
        if (this.progressListener != null) {
            this.progressListener.progressChanged(this);
        }
    }
    
    public DownloadState getDownloadState() {
        return this.downloadState;
    }
    
    public double getProgress() {
        return (this.mediaContentLength == 0L) ? 0.0 : (this.bytesDownloaded / (double)this.mediaContentLength);
    }
    
    public enum DownloadState
    {
        NOT_STARTED, 
        MEDIA_IN_PROGRESS, 
        MEDIA_COMPLETE;
    }
}
