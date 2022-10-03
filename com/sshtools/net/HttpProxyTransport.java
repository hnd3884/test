package com.sshtools.net;

import java.util.Enumeration;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.Hashtable;
import com.maverick.ssh.SshTransport;
import java.net.Socket;

public class HttpProxyTransport extends Socket implements SshTransport
{
    private String bb;
    private int eb;
    private String x;
    private int ab;
    private HttpResponse v;
    private String z;
    private String cb;
    private String db;
    private HttpRequest y;
    private Hashtable w;
    
    private HttpProxyTransport(final String x, final int ab, final String bb, final int eb) throws IOException, UnknownHostException {
        super(bb, eb);
        this.y = new HttpRequest();
        this.bb = bb;
        this.eb = eb;
        this.x = x;
        this.ab = ab;
    }
    
    public static HttpProxyTransport connectViaProxy(final String s, final int n, final String s2, final int n2, final String s3, final String s4, final String s5) throws IOException, UnknownHostException {
        return connectViaProxy(s, n, s2, n2, s3, s4, s5, null);
    }
    
    public static HttpProxyTransport connectViaProxy(final String s, final int n, final String s2, final int n2, final String z, final String cb, final String db, final Hashtable w) throws IOException, UnknownHostException {
        HttpProxyTransport httpProxyTransport = new HttpProxyTransport(s, n, s2, n2);
        httpProxyTransport.z = z;
        httpProxyTransport.cb = cb;
        httpProxyTransport.db = db;
        httpProxyTransport.w = w;
        int status;
        try {
            final InputStream inputStream = httpProxyTransport.getInputStream();
            final OutputStream outputStream = httpProxyTransport.getOutputStream();
            httpProxyTransport.y.setHeaderBegin("CONNECT " + s + ":" + n + " HTTP/1.0");
            httpProxyTransport.y.setHeaderField("User-Agent", db);
            httpProxyTransport.y.setHeaderField("Pragma", "No-Cache");
            httpProxyTransport.y.setHeaderField("Host", s);
            httpProxyTransport.y.setHeaderField("Proxy-Connection", "Keep-Alive");
            if (w != null) {
                final Enumeration keys = w.keys();
                while (keys.hasMoreElements()) {
                    final String s3 = (String)keys.nextElement();
                    httpProxyTransport.y.setHeaderField(s3, (String)w.get(s3));
                }
            }
            outputStream.write(httpProxyTransport.y.toString().getBytes());
            outputStream.flush();
            httpProxyTransport.v = new HttpResponse(inputStream);
            if (httpProxyTransport.v.getStatus() == 407) {
                final String authenticationRealm = httpProxyTransport.v.getAuthenticationRealm();
                final String authenticationMethod = httpProxyTransport.v.getAuthenticationMethod();
                if (authenticationRealm == null) {}
                if (authenticationMethod.equalsIgnoreCase("basic")) {
                    httpProxyTransport.close();
                    httpProxyTransport = new HttpProxyTransport(s, n, s2, n2);
                    final InputStream inputStream2 = httpProxyTransport.getInputStream();
                    final OutputStream outputStream2 = httpProxyTransport.getOutputStream();
                    httpProxyTransport.y.setBasicAuthentication(z, cb);
                    outputStream2.write(httpProxyTransport.y.toString().getBytes());
                    outputStream2.flush();
                    httpProxyTransport.v = new HttpResponse(inputStream2);
                }
                else {
                    if (authenticationMethod.equalsIgnoreCase("digest")) {
                        throw new IOException("Digest authentication is not supported");
                    }
                    throw new IOException("'" + authenticationMethod + "' is not supported");
                }
            }
            status = httpProxyTransport.v.getStatus();
        }
        catch (final SocketException ex) {
            throw new SocketException("Error communicating with proxy server " + s2 + ":" + n2 + " (" + ex.getMessage() + ")");
        }
        if (status < 200 || status > 299) {
            throw new IOException("Proxy tunnel setup failed: " + httpProxyTransport.v.getStartLine());
        }
        return httpProxyTransport;
    }
    
    public String toString() {
        return "HTTPProxySocket [Proxy IP=" + this.getInetAddress() + ",Proxy Port=" + this.getPort() + ",localport=" + this.getLocalPort() + "Remote Host=" + this.x + "Remote Port=" + String.valueOf(this.ab) + "]";
    }
    
    public String getHost() {
        return this.x;
    }
    
    public SshTransport duplicate() throws IOException {
        return connectViaProxy(this.x, this.ab, this.bb, this.eb, this.z, this.cb, this.db, this.w);
    }
}
