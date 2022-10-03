package sun.net.www.http;

import java.net.URL;

class KeepAliveKey
{
    private String protocol;
    private String host;
    private int port;
    private Object obj;
    
    public KeepAliveKey(final URL url, final Object obj) {
        this.protocol = null;
        this.host = null;
        this.port = 0;
        this.obj = null;
        this.protocol = url.getProtocol();
        this.host = url.getHost();
        this.port = url.getPort();
        this.obj = obj;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof KeepAliveKey)) {
            return false;
        }
        final KeepAliveKey keepAliveKey = (KeepAliveKey)o;
        return this.host.equals(keepAliveKey.host) && this.port == keepAliveKey.port && this.protocol.equals(keepAliveKey.protocol) && this.obj == keepAliveKey.obj;
    }
    
    @Override
    public int hashCode() {
        final String string = this.protocol + this.host + this.port;
        return (this.obj == null) ? string.hashCode() : (string.hashCode() + this.obj.hashCode());
    }
}
