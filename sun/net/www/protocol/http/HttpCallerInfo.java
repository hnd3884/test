package sun.net.www.protocol.http;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.URL;

public final class HttpCallerInfo
{
    public final URL url;
    public final String host;
    public final String protocol;
    public final String prompt;
    public final String scheme;
    public final int port;
    public final InetAddress addr;
    public final Authenticator.RequestorType authType;
    
    public HttpCallerInfo(final HttpCallerInfo httpCallerInfo, final String scheme) {
        this.url = httpCallerInfo.url;
        this.host = httpCallerInfo.host;
        this.protocol = httpCallerInfo.protocol;
        this.prompt = httpCallerInfo.prompt;
        this.port = httpCallerInfo.port;
        this.addr = httpCallerInfo.addr;
        this.authType = httpCallerInfo.authType;
        this.scheme = scheme;
    }
    
    public HttpCallerInfo(final URL url) {
        this.url = url;
        this.prompt = "";
        this.host = url.getHost();
        final int port = url.getPort();
        if (port == -1) {
            this.port = url.getDefaultPort();
        }
        else {
            this.port = port;
        }
        InetAddress byName;
        try {
            byName = InetAddress.getByName(url.getHost());
        }
        catch (final Exception ex) {
            byName = null;
        }
        this.addr = byName;
        this.protocol = url.getProtocol();
        this.authType = Authenticator.RequestorType.SERVER;
        this.scheme = "";
    }
    
    public HttpCallerInfo(final URL url, final String host, final int port) {
        this.url = url;
        this.host = host;
        this.port = port;
        this.prompt = "";
        this.addr = null;
        this.protocol = url.getProtocol();
        this.authType = Authenticator.RequestorType.PROXY;
        this.scheme = "";
    }
}
