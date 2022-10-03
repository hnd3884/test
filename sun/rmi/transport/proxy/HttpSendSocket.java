package sun.rmi.transport.proxy;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.io.IOException;
import sun.rmi.runtime.Log;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.net.SocketImpl;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import java.net.Socket;

class HttpSendSocket extends Socket implements RMISocketInfo
{
    protected String host;
    protected int port;
    protected URL url;
    protected URLConnection conn;
    protected InputStream in;
    protected OutputStream out;
    protected HttpSendInputStream inNotifier;
    protected HttpSendOutputStream outNotifier;
    private String lineSeparator;
    
    public HttpSendSocket(final String host, final int port, final URL url) throws IOException {
        super((SocketImpl)null);
        this.conn = null;
        this.in = null;
        this.out = null;
        this.lineSeparator = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("line.separator"));
        if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
            RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "host = " + host + ", port = " + port + ", url = " + url);
        }
        this.host = host;
        this.port = port;
        this.url = url;
        this.inNotifier = new HttpSendInputStream(null, this);
        this.outNotifier = new HttpSendOutputStream(this.writeNotify(), this);
    }
    
    public HttpSendSocket(final String s, final int n) throws IOException {
        this(s, n, new URL("http", s, n, "/"));
    }
    
    public HttpSendSocket(final InetAddress inetAddress, final int n) throws IOException {
        this(inetAddress.getHostName(), n);
    }
    
    @Override
    public boolean isReusable() {
        return false;
    }
    
    public synchronized OutputStream writeNotify() throws IOException {
        if (this.conn != null) {
            throw new IOException("attempt to write on HttpSendSocket after request has been sent");
        }
        (this.conn = this.url.openConnection()).setDoOutput(true);
        this.conn.setUseCaches(false);
        this.conn.setRequestProperty("Content-type", "application/octet-stream");
        this.inNotifier.deactivate();
        this.in = null;
        return this.out = this.conn.getOutputStream();
    }
    
    public synchronized InputStream readNotify() throws IOException {
        RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "sending request and activating input stream");
        this.outNotifier.deactivate();
        this.out.close();
        this.out = null;
        try {
            this.in = this.conn.getInputStream();
        }
        catch (final IOException ex) {
            RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "failed to get input stream, exception: ", ex);
            throw new IOException("HTTP request failed");
        }
        final String contentType = this.conn.getContentType();
        if (contentType == null || !this.conn.getContentType().equals("application/octet-stream")) {
            if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
                String s;
                if (contentType == null) {
                    s = "missing content type in response" + this.lineSeparator;
                }
                else {
                    s = "invalid content type in response: " + contentType + this.lineSeparator;
                }
                String s2 = s + "HttpSendSocket.readNotify: response body: ";
                try {
                    String line;
                    while ((line = new BufferedReader(new InputStreamReader(this.in)).readLine()) != null) {
                        s2 = s2 + line + this.lineSeparator;
                    }
                }
                catch (final IOException ex2) {}
                RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, s2);
            }
            throw new IOException("HTTP request failed");
        }
        return this.in;
    }
    
    @Override
    public InetAddress getInetAddress() {
        try {
            return InetAddress.getByName(this.host);
        }
        catch (final UnknownHostException ex) {
            return null;
        }
    }
    
    @Override
    public InetAddress getLocalAddress() {
        try {
            return InetAddress.getLocalHost();
        }
        catch (final UnknownHostException ex) {
            return null;
        }
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    @Override
    public int getLocalPort() {
        return -1;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this.inNotifier;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.outNotifier;
    }
    
    @Override
    public void setTcpNoDelay(final boolean b) throws SocketException {
    }
    
    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return false;
    }
    
    @Override
    public void setSoLinger(final boolean b, final int n) throws SocketException {
    }
    
    @Override
    public int getSoLinger() throws SocketException {
        return -1;
    }
    
    @Override
    public synchronized void setSoTimeout(final int n) throws SocketException {
    }
    
    @Override
    public synchronized int getSoTimeout() throws SocketException {
        return 0;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (this.out != null) {
            this.out.close();
        }
    }
    
    @Override
    public String toString() {
        return "HttpSendSocket[host=" + this.host + ",port=" + this.port + ",url=" + this.url + "]";
    }
}
