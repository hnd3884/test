package org.apache.coyote.http11.upgrade;

import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.coyote.ContainerThreadMarker;
import java.io.IOException;
import javax.servlet.ReadListener;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import javax.servlet.ServletInputStream;

public class UpgradeServletInputStream extends ServletInputStream
{
    private static final Log log;
    private static final StringManager sm;
    private final UpgradeProcessorBase processor;
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private volatile boolean closed;
    private volatile boolean eof;
    private volatile Boolean ready;
    private volatile ReadListener listener;
    
    public UpgradeServletInputStream(final UpgradeProcessorBase processor, final SocketWrapperBase<?> socketWrapper, final UpgradeInfo upgradeInfo) {
        this.closed = false;
        this.eof = false;
        this.ready = Boolean.TRUE;
        this.listener = null;
        this.processor = processor;
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
    }
    
    public final boolean isFinished() {
        if (this.listener == null) {
            throw new IllegalStateException(UpgradeServletInputStream.sm.getString("upgrade.sis.isFinished.ise"));
        }
        return this.eof;
    }
    
    public final boolean isReady() {
        if (this.listener == null) {
            throw new IllegalStateException(UpgradeServletInputStream.sm.getString("upgrade.sis.isReady.ise"));
        }
        if (this.eof || this.closed) {
            return false;
        }
        if (this.ready != null) {
            return this.ready;
        }
        try {
            this.ready = this.socketWrapper.isReadyForRead();
        }
        catch (final IOException e) {
            this.onError(e);
        }
        return this.ready;
    }
    
    public final void setReadListener(final ReadListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(UpgradeServletInputStream.sm.getString("upgrade.sis.readListener.null"));
        }
        if (this.listener != null) {
            throw new IllegalArgumentException(UpgradeServletInputStream.sm.getString("upgrade.sis.readListener.set"));
        }
        if (this.closed) {
            throw new IllegalStateException(UpgradeServletInputStream.sm.getString("upgrade.sis.read.closed"));
        }
        this.listener = listener;
        if (ContainerThreadMarker.isContainerThread()) {
            this.processor.addDispatch(DispatchType.NON_BLOCKING_READ);
        }
        else {
            this.socketWrapper.registerReadInterest();
        }
        this.ready = null;
    }
    
    public final int read() throws IOException {
        this.preReadChecks();
        return this.readInternal();
    }
    
    public final int readLine(final byte[] b, int off, final int len) throws IOException {
        this.preReadChecks();
        if (len <= 0) {
            return 0;
        }
        int count = 0;
        int c;
        while ((c = this.readInternal()) != -1) {
            b[off++] = (byte)c;
            ++count;
            if (c == 10 || count == len) {
                break;
            }
        }
        if (count > 0) {
            this.upgradeInfo.addBytesReceived(count);
            return count;
        }
        return -1;
    }
    
    public final int read(final byte[] b, final int off, final int len) throws IOException {
        this.preReadChecks();
        try {
            final int result = this.socketWrapper.read(this.listener == null, b, off, len);
            if (result == -1) {
                this.eof = true;
            }
            else {
                this.upgradeInfo.addBytesReceived(result);
            }
            return result;
        }
        catch (final IOException ioe) {
            this.close();
            throw ioe;
        }
    }
    
    public void close() throws IOException {
        this.eof = true;
        this.closed = true;
    }
    
    private void preReadChecks() {
        if (this.listener != null && (this.ready == null || !this.ready)) {
            throw new IllegalStateException(UpgradeServletInputStream.sm.getString("upgrade.sis.read.ise"));
        }
        if (this.closed) {
            throw new IllegalStateException(UpgradeServletInputStream.sm.getString("upgrade.sis.read.closed"));
        }
        this.ready = null;
    }
    
    private int readInternal() throws IOException {
        final byte[] b = { 0 };
        int result;
        try {
            result = this.socketWrapper.read(this.listener == null, b, 0, 1);
        }
        catch (final IOException ioe) {
            this.close();
            throw ioe;
        }
        if (result == 0) {
            return -1;
        }
        if (result == -1) {
            this.eof = true;
            return -1;
        }
        this.upgradeInfo.addBytesReceived(1L);
        return b[0] & 0xFF;
    }
    
    final void onDataAvailable() {
        try {
            if (this.listener == null || !this.socketWrapper.isReadyForRead()) {
                return;
            }
        }
        catch (final IOException e) {
            this.onError(e);
        }
        this.ready = Boolean.TRUE;
        final ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, (ClassLoader)null);
        try {
            if (!this.eof) {
                this.listener.onDataAvailable();
            }
            if (this.eof) {
                this.listener.onAllDataRead();
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.onError(t);
        }
        finally {
            this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
        }
    }
    
    private final void onError(final Throwable t) {
        if (this.listener == null) {
            return;
        }
        final ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, (ClassLoader)null);
        try {
            this.listener.onError(t);
        }
        catch (final Throwable t2) {
            ExceptionUtils.handleThrowable(t2);
            UpgradeServletInputStream.log.warn((Object)UpgradeServletInputStream.sm.getString("upgrade.sis.onErrorFail"), t2);
        }
        finally {
            this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
        }
        try {
            this.close();
        }
        catch (final IOException ioe) {
            if (UpgradeServletInputStream.log.isDebugEnabled()) {
                UpgradeServletInputStream.log.debug((Object)UpgradeServletInputStream.sm.getString("upgrade.sis.errorCloseFail"), (Throwable)ioe);
            }
        }
        this.ready = Boolean.FALSE;
    }
    
    final boolean isClosed() {
        return this.closed;
    }
    
    static {
        log = LogFactory.getLog((Class)UpgradeServletInputStream.class);
        sm = StringManager.getManager((Class)UpgradeServletInputStream.class);
    }
}
