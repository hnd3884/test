package org.apache.commons.net.io;

import java.util.EventListener;

public interface CopyStreamListener extends EventListener
{
    void bytesTransferred(final CopyStreamEvent p0);
    
    void bytesTransferred(final long p0, final int p1, final long p2);
}
