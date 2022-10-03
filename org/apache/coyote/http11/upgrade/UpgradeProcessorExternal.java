package org.apache.coyote.http11.upgrade;

import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class UpgradeProcessorExternal extends UpgradeProcessorBase
{
    private static final Log log;
    private static final StringManager sm;
    private final UpgradeServletInputStream upgradeServletInputStream;
    private final UpgradeServletOutputStream upgradeServletOutputStream;
    private final UpgradeInfo upgradeInfo;
    
    public UpgradeProcessorExternal(final SocketWrapperBase<?> wrapper, final UpgradeToken upgradeToken, final UpgradeGroupInfo upgradeGroupInfo) {
        super(upgradeToken);
        upgradeGroupInfo.addUpgradeInfo(this.upgradeInfo = new UpgradeInfo());
        this.upgradeServletInputStream = new UpgradeServletInputStream(this, wrapper, this.upgradeInfo);
        this.upgradeServletOutputStream = new UpgradeServletOutputStream(this, wrapper, this.upgradeInfo);
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
    }
    
    @Override
    protected Log getLog() {
        return UpgradeProcessorExternal.log;
    }
    
    public void close() throws Exception {
        this.upgradeServletInputStream.close();
        this.upgradeServletOutputStream.close();
        this.upgradeInfo.setGroupInfo(null);
    }
    
    public ServletInputStream getInputStream() throws IOException {
        return this.upgradeServletInputStream;
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        return this.upgradeServletOutputStream;
    }
    
    public final AbstractEndpoint.Handler.SocketState dispatch(final SocketEvent status) {
        if (status == SocketEvent.OPEN_READ) {
            this.upgradeServletInputStream.onDataAvailable();
        }
        else if (status == SocketEvent.OPEN_WRITE) {
            this.upgradeServletOutputStream.onWritePossible();
        }
        else {
            if (status == SocketEvent.STOP) {
                if (UpgradeProcessorExternal.log.isDebugEnabled()) {
                    UpgradeProcessorExternal.log.debug((Object)UpgradeProcessorExternal.sm.getString("upgradeProcessor.stop"));
                }
                try {
                    this.upgradeServletInputStream.close();
                }
                catch (final IOException ioe) {
                    UpgradeProcessorExternal.log.debug((Object)UpgradeProcessorExternal.sm.getString("upgradeProcessor.isCloseFail", new Object[] { ioe }));
                }
                try {
                    this.upgradeServletOutputStream.close();
                }
                catch (final IOException ioe) {
                    UpgradeProcessorExternal.log.debug((Object)UpgradeProcessorExternal.sm.getString("upgradeProcessor.osCloseFail", new Object[] { ioe }));
                }
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            if (UpgradeProcessorExternal.log.isDebugEnabled()) {
                UpgradeProcessorExternal.log.debug((Object)UpgradeProcessorExternal.sm.getString("upgradeProcessor.unexpectedState"));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.upgradeServletInputStream.isClosed() && this.upgradeServletOutputStream.isClosed()) {
            if (UpgradeProcessorExternal.log.isDebugEnabled()) {
                UpgradeProcessorExternal.log.debug((Object)UpgradeProcessorExternal.sm.getString("upgradeProcessor.requiredClose", new Object[] { this.upgradeServletInputStream.isClosed(), this.upgradeServletOutputStream.isClosed() }));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.UPGRADED;
    }
    
    public final void setSslSupport(final SSLSupport sslSupport) {
    }
    
    public void pause() {
    }
    
    static {
        log = LogFactory.getLog((Class)UpgradeProcessorExternal.class);
        sm = StringManager.getManager((Class)UpgradeProcessorExternal.class);
    }
}
