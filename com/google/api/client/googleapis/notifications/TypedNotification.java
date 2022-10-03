package com.google.api.client.googleapis.notifications;

import com.google.api.client.util.Beta;

@Beta
public class TypedNotification<T> extends AbstractNotification
{
    private T content;
    
    public TypedNotification(final long messageNumber, final String resourceState, final String resourceId, final String resourceUri, final String channelId) {
        super(messageNumber, resourceState, resourceId, resourceUri, channelId);
    }
    
    public TypedNotification(final UnparsedNotification sourceNotification) {
        super(sourceNotification);
    }
    
    public final T getContent() {
        return this.content;
    }
    
    public TypedNotification<T> setContent(final T content) {
        this.content = content;
        return this;
    }
    
    @Override
    public TypedNotification<T> setMessageNumber(final long messageNumber) {
        return (TypedNotification)super.setMessageNumber(messageNumber);
    }
    
    @Override
    public TypedNotification<T> setResourceState(final String resourceState) {
        return (TypedNotification)super.setResourceState(resourceState);
    }
    
    @Override
    public TypedNotification<T> setResourceId(final String resourceId) {
        return (TypedNotification)super.setResourceId(resourceId);
    }
    
    @Override
    public TypedNotification<T> setResourceUri(final String resourceUri) {
        return (TypedNotification)super.setResourceUri(resourceUri);
    }
    
    @Override
    public TypedNotification<T> setChannelId(final String channelId) {
        return (TypedNotification)super.setChannelId(channelId);
    }
    
    @Override
    public TypedNotification<T> setChannelExpiration(final String channelExpiration) {
        return (TypedNotification)super.setChannelExpiration(channelExpiration);
    }
    
    @Override
    public TypedNotification<T> setChannelToken(final String channelToken) {
        return (TypedNotification)super.setChannelToken(channelToken);
    }
    
    @Override
    public TypedNotification<T> setChanged(final String changed) {
        return (TypedNotification)super.setChanged(changed);
    }
    
    @Override
    public String toString() {
        return super.toStringHelper().add("content", (Object)this.content).toString();
    }
}
