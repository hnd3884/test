package org.bouncycastle.est.jcajce;

import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.SSLSocket;
import org.bouncycastle.est.LimitedSource;
import org.bouncycastle.est.TLSUniqueProvider;
import javax.net.ssl.SSLSession;
import org.bouncycastle.est.Source;

class LimitedSSLSocketSource implements Source<SSLSession>, TLSUniqueProvider, LimitedSource
{
    protected final SSLSocket socket;
    private final ChannelBindingProvider bindingProvider;
    private final Long absoluteReadLimit;
    
    public LimitedSSLSocketSource(final SSLSocket socket, final ChannelBindingProvider bindingProvider, final Long absoluteReadLimit) {
        this.socket = socket;
        this.bindingProvider = bindingProvider;
        this.absoluteReadLimit = absoluteReadLimit;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }
    
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }
    
    public SSLSession getSession() {
        return this.socket.getSession();
    }
    
    public byte[] getTLSUnique() {
        if (this.isTLSUniqueAvailable()) {
            return this.bindingProvider.getChannelBinding(this.socket, "tls-unique");
        }
        throw new IllegalStateException("No binding provider.");
    }
    
    public boolean isTLSUniqueAvailable() {
        return this.bindingProvider.canAccessChannelBinding(this.socket);
    }
    
    public void close() throws IOException {
        this.socket.close();
    }
    
    public Long getAbsoluteReadLimit() {
        return this.absoluteReadLimit;
    }
}
