package org.apache.coyote.http11.upgrade;

import org.apache.juli.logging.LogFactory;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.juli.logging.Log;

public class UpgradeProcessorInternal extends UpgradeProcessorBase
{
    private static final Log log;
    private final InternalHttpUpgradeHandler internalHttpUpgradeHandler;
    
    public UpgradeProcessorInternal(final SocketWrapperBase<?> wrapper, final UpgradeToken upgradeToken, final UpgradeGroupInfo upgradeGroupInfo) {
        super(upgradeToken);
        this.internalHttpUpgradeHandler = (InternalHttpUpgradeHandler)upgradeToken.getHttpUpgradeHandler();
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
        this.internalHttpUpgradeHandler.setSocketWrapper(wrapper);
        final UpgradeInfo upgradeInfo = this.internalHttpUpgradeHandler.getUpgradeInfo();
        if (upgradeInfo != null && upgradeGroupInfo != null) {
            upgradeInfo.setGroupInfo(upgradeGroupInfo);
        }
    }
    
    public AbstractEndpoint.Handler.SocketState dispatch(final SocketEvent status) {
        return this.internalHttpUpgradeHandler.upgradeDispatch(status);
    }
    
    public final void setSslSupport(final SSLSupport sslSupport) {
        this.internalHttpUpgradeHandler.setSslSupport(sslSupport);
    }
    
    public void pause() {
        this.internalHttpUpgradeHandler.pause();
    }
    
    @Override
    protected Log getLog() {
        return UpgradeProcessorInternal.log;
    }
    
    @Override
    public void timeoutAsync(final long now) {
        this.internalHttpUpgradeHandler.timeoutAsync(now);
    }
    
    public void close() throws Exception {
        final UpgradeInfo upgradeInfo = this.internalHttpUpgradeHandler.getUpgradeInfo();
        if (upgradeInfo != null) {
            upgradeInfo.setGroupInfo(null);
        }
        this.internalHttpUpgradeHandler.destroy();
    }
    
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    
    static {
        log = LogFactory.getLog((Class)UpgradeProcessorInternal.class);
    }
}
