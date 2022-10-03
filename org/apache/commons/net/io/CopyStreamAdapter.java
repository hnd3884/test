package org.apache.commons.net.io;

import java.util.Iterator;
import java.util.EventListener;
import org.apache.commons.net.util.ListenerList;

public class CopyStreamAdapter implements CopyStreamListener
{
    private final ListenerList internalListeners;
    
    public CopyStreamAdapter() {
        this.internalListeners = new ListenerList();
    }
    
    @Override
    public void bytesTransferred(final CopyStreamEvent event) {
        for (final EventListener listener : this.internalListeners) {
            ((CopyStreamListener)listener).bytesTransferred(event);
        }
    }
    
    @Override
    public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
        for (final EventListener listener : this.internalListeners) {
            ((CopyStreamListener)listener).bytesTransferred(totalBytesTransferred, bytesTransferred, streamSize);
        }
    }
    
    public void addCopyStreamListener(final CopyStreamListener listener) {
        this.internalListeners.addListener(listener);
    }
    
    public void removeCopyStreamListener(final CopyStreamListener listener) {
        this.internalListeners.removeListener(listener);
    }
}
