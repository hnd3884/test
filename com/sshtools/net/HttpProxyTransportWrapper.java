package com.sshtools.net;

import com.maverick.ssh.SshTransport;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;

public class HttpProxyTransportWrapper extends SocketWrapper
{
    private String o;
    private int s;
    private String n;
    private int r;
    private HttpResponse u;
    private String t;
    private String p;
    private String q;
    
    private HttpProxyTransportWrapper(final String n, final int r, final String o, final int s) throws IOException, UnknownHostException {
        super(new Socket(o, s));
        this.o = o;
        this.s = s;
        this.n = n;
        this.r = r;
    }
    
    public static HttpProxyTransportWrapper connectViaProxy(final String s, final int n, final String s2, final int n2, final String t, final String p7, final String q) throws IOException, UnknownHostException {
        HttpProxyTransportWrapper httpProxyTransportWrapper = new HttpProxyTransportWrapper(s, n, s2, n2);
        httpProxyTransportWrapper.t = t;
        httpProxyTransportWrapper.p = p7;
        httpProxyTransportWrapper.q = q;
        int status;
        try {
            final InputStream inputStream = httpProxyTransportWrapper.getInputStream();
            final OutputStream outputStream = httpProxyTransportWrapper.getOutputStream();
            final HttpRequest httpRequest = new HttpRequest();
            httpRequest.setHeaderBegin("CONNECT " + s + ":" + n + " HTTP/1.0");
            httpRequest.setHeaderField("User-Agent", q);
            httpRequest.setHeaderField("Pragma", "No-Cache");
            httpRequest.setHeaderField("Host", s);
            httpRequest.setHeaderField("Proxy-Connection", "Keep-Alive");
            outputStream.write(httpRequest.toString().getBytes());
            outputStream.flush();
            httpProxyTransportWrapper.u = new HttpResponse(inputStream);
            if (httpProxyTransportWrapper.u.getStatus() == 407) {
                final String authenticationRealm = httpProxyTransportWrapper.u.getAuthenticationRealm();
                final String authenticationMethod = httpProxyTransportWrapper.u.getAuthenticationMethod();
                if (authenticationRealm == null) {}
                if (authenticationMethod.equalsIgnoreCase("basic")) {
                    httpProxyTransportWrapper.close();
                    httpProxyTransportWrapper = new HttpProxyTransportWrapper(s, n, s2, n2);
                    final InputStream inputStream2 = httpProxyTransportWrapper.getInputStream();
                    final OutputStream outputStream2 = httpProxyTransportWrapper.getOutputStream();
                    httpRequest.setBasicAuthentication(t, p7);
                    outputStream2.write(httpRequest.toString().getBytes());
                    outputStream2.flush();
                    httpProxyTransportWrapper.u = new HttpResponse(inputStream2);
                }
                else {
                    if (authenticationMethod.equalsIgnoreCase("digest")) {
                        throw new IOException("Digest authentication is not supported");
                    }
                    throw new IOException("'" + authenticationMethod + "' is not supported");
                }
            }
            status = httpProxyTransportWrapper.u.getStatus();
        }
        catch (final SocketException ex) {
            throw new SocketException("Error communicating with proxy server " + s2 + ":" + n2 + " (" + ex.getMessage() + ")");
        }
        if (status < 200 || status > 299) {
            throw new IOException("Proxy tunnel setup failed: " + httpProxyTransportWrapper.u.getStartLine());
        }
        return httpProxyTransportWrapper;
    }
    
    public String toString() {
        return "HTTPProxySocket [Proxy IP=" + super.socket.getInetAddress() + ",Proxy Port=" + this.getPort() + ",localport=" + super.socket.getLocalPort() + "Remote Host=" + this.n + "Remote Port=" + String.valueOf(this.r) + "]";
    }
    
    public String getHost() {
        return this.n;
    }
    
    public SshTransport duplicate() throws IOException {
        return connectViaProxy(this.n, this.r, this.o, this.s, this.t, this.p, this.q);
    }
}
