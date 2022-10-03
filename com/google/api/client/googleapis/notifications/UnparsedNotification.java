package com.google.api.client.googleapis.notifications;

import java.io.InputStream;
import com.google.api.client.util.Beta;

@Beta
public class UnparsedNotification extends AbstractNotification
{
    private String contentType;
    private InputStream contentStream;
    
    public UnparsedNotification(final long messageNumber, final String resourceState, final String resourceId, final String resourceUri, final String channelId) {
        super(messageNumber, resourceState, resourceId, resourceUri, channelId);
    }
    
    public final String getContentType() {
        return this.contentType;
    }
    
    public UnparsedNotification setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }
    
    public final InputStream getContentStream() {
        return this.contentStream;
    }
    
    public UnparsedNotification setContentStream(final InputStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }
    
    @Override
    public UnparsedNotification setMessageNumber(final long messageNumber) {
        return (UnparsedNotification)super.setMessageNumber(messageNumber);
    }
    
    @Override
    public UnparsedNotification setResourceState(final String resourceState) {
        return (UnparsedNotification)super.setResourceState(resourceState);
    }
    
    @Override
    public UnparsedNotification setResourceId(final String resourceId) {
        return (UnparsedNotification)super.setResourceId(resourceId);
    }
    
    @Override
    public UnparsedNotification setResourceUri(final String resourceUri) {
        return (UnparsedNotification)super.setResourceUri(resourceUri);
    }
    
    @Override
    public UnparsedNotification setChannelId(final String channelId) {
        return (UnparsedNotification)super.setChannelId(channelId);
    }
    
    @Override
    public UnparsedNotification setChannelExpiration(final String channelExpiration) {
        return (UnparsedNotification)super.setChannelExpiration(channelExpiration);
    }
    
    @Override
    public UnparsedNotification setChannelToken(final String channelToken) {
        return (UnparsedNotification)super.setChannelToken(channelToken);
    }
    
    @Override
    public UnparsedNotification setChanged(final String changed) {
        return (UnparsedNotification)super.setChanged(changed);
    }
    
    @Override
    public String toString() {
        return super.toStringHelper().add("contentType", (Object)this.contentType).toString();
    }
}
