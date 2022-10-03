package sun.net.www.protocol.mailto;

import java.net.SocketPermission;
import sun.net.www.ParseUtil;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import sun.net.www.MessageHeader;
import java.net.URL;
import java.security.Permission;
import sun.net.smtp.SmtpClient;
import java.io.OutputStream;
import java.io.InputStream;
import sun.net.www.URLConnection;

public class MailToURLConnection extends URLConnection
{
    InputStream is;
    OutputStream os;
    SmtpClient client;
    Permission permission;
    private int connectTimeout;
    private int readTimeout;
    
    MailToURLConnection(final URL url) {
        super(url);
        this.is = null;
        this.os = null;
        this.connectTimeout = -1;
        this.readTimeout = -1;
        final MessageHeader properties = new MessageHeader();
        properties.add("content-type", "text/html");
        this.setProperties(properties);
    }
    
    String getFromAddress() {
        String s = System.getProperty("user.fromaddr");
        if (s == null) {
            final String property = System.getProperty("user.name");
            if (property != null) {
                String s2 = System.getProperty("mail.host");
                if (s2 == null) {
                    try {
                        s2 = InetAddress.getLocalHost().getHostName();
                    }
                    catch (final UnknownHostException ex) {}
                }
                s = property + "@" + s2;
            }
            else {
                s = "";
            }
        }
        return s;
    }
    
    @Override
    public void connect() throws IOException {
        (this.client = new SmtpClient(this.connectTimeout)).setReadTimeout(this.readTimeout);
    }
    
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (this.os != null) {
            return this.os;
        }
        if (this.is != null) {
            throw new IOException("Cannot write output after reading input.");
        }
        this.connect();
        final String decode = ParseUtil.decode(this.url.getPath());
        this.client.from(this.getFromAddress());
        this.client.to(decode);
        return this.os = this.client.startMessage();
    }
    
    @Override
    public Permission getPermission() throws IOException {
        if (this.permission == null) {
            this.connect();
            this.permission = new SocketPermission(this.client.getMailHost() + ":" + 25, "connect");
        }
        return this.permission;
    }
    
    @Override
    public void setConnectTimeout(final int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        this.connectTimeout = connectTimeout;
    }
    
    @Override
    public int getConnectTimeout() {
        return (this.connectTimeout < 0) ? 0 : this.connectTimeout;
    }
    
    @Override
    public void setReadTimeout(final int readTimeout) {
        if (readTimeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        this.readTimeout = readTimeout;
    }
    
    @Override
    public int getReadTimeout() {
        return (this.readTimeout < 0) ? 0 : this.readTimeout;
    }
}
