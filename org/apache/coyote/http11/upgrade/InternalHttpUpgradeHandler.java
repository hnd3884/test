package org.apache.coyote.http11.upgrade;

import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import javax.servlet.http.HttpUpgradeHandler;

public interface InternalHttpUpgradeHandler extends HttpUpgradeHandler
{
    AbstractEndpoint.Handler.SocketState upgradeDispatch(final SocketEvent p0);
    
    void timeoutAsync(final long p0);
    
    void setSocketWrapper(final SocketWrapperBase<?> p0);
    
    void setSslSupport(final SSLSupport p0);
    
    void pause();
    
    UpgradeInfo getUpgradeInfo();
}
