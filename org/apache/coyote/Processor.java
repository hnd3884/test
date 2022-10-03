package org.apache.coyote;

import java.nio.ByteBuffer;
import org.apache.tomcat.util.net.SSLSupport;
import java.io.IOException;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

public interface Processor
{
    AbstractEndpoint.Handler.SocketState process(final SocketWrapperBase<?> p0, final SocketEvent p1) throws IOException;
    
    UpgradeToken getUpgradeToken();
    
    boolean isUpgrade();
    
    boolean isAsync();
    
    void timeoutAsync(final long p0);
    
    Request getRequest();
    
    void recycle();
    
    void setSslSupport(final SSLSupport p0);
    
    ByteBuffer getLeftoverInput();
    
    void pause();
    
    boolean checkAsyncTimeoutGeneration();
}
