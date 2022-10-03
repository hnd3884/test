package com.google.api.client.googleapis.notifications;

import java.nio.charset.Charset;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.util.ObjectParser;
import java.io.IOException;
import com.google.api.client.util.Beta;

@Beta
public abstract class TypedNotificationCallback<T> implements UnparsedNotificationCallback
{
    private static final long serialVersionUID = 1L;
    
    protected abstract void onNotification(final StoredChannel p0, final TypedNotification<T> p1) throws IOException;
    
    protected abstract ObjectParser getObjectParser() throws IOException;
    
    protected abstract Class<T> getDataClass() throws IOException;
    
    @Override
    public final void onNotification(final StoredChannel storedChannel, final UnparsedNotification notification) throws IOException {
        final TypedNotification<T> typedNotification = new TypedNotification<T>(notification);
        final String contentType = notification.getContentType();
        if (contentType != null) {
            final Charset charset = new HttpMediaType(contentType).getCharsetParameter();
            final Class<T> dataClass = (Class<T>)Preconditions.checkNotNull((Object)this.getDataClass());
            typedNotification.setContent((T)this.getObjectParser().parseAndClose(notification.getContentStream(), charset, (Class)dataClass));
        }
        this.onNotification(storedChannel, typedNotification);
    }
}
