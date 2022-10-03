package sun.net.www;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.util.HashMap;

public abstract class URLConnection extends java.net.URLConnection
{
    private String contentType;
    private int contentLength;
    protected MessageHeader properties;
    private static HashMap<String, Void> proxiedHosts;
    
    public URLConnection(final URL url) {
        super(url);
        this.contentLength = -1;
        this.properties = new MessageHeader();
    }
    
    public MessageHeader getProperties() {
        return this.properties;
    }
    
    public void setProperties(final MessageHeader properties) {
        this.properties = properties;
    }
    
    @Override
    public void setRequestProperty(final String s, final String s2) {
        if (this.connected) {
            throw new IllegalAccessError("Already connected");
        }
        if (s == null) {
            throw new NullPointerException("key cannot be null");
        }
        this.properties.set(s, s2);
    }
    
    @Override
    public void addRequestProperty(final String s, final String s2) {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        }
        if (s == null) {
            throw new NullPointerException("key is null");
        }
    }
    
    @Override
    public String getRequestProperty(final String s) {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        }
        return null;
    }
    
    @Override
    public Map<String, List<String>> getRequestProperties() {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        }
        return Collections.emptyMap();
    }
    
    @Override
    public String getHeaderField(final String s) {
        try {
            this.getInputStream();
        }
        catch (final Exception ex) {
            return null;
        }
        return (this.properties == null) ? null : this.properties.findValue(s);
    }
    
    @Override
    public String getHeaderFieldKey(final int n) {
        try {
            this.getInputStream();
        }
        catch (final Exception ex) {
            return null;
        }
        final MessageHeader properties = this.properties;
        return (properties == null) ? null : properties.getKey(n);
    }
    
    @Override
    public String getHeaderField(final int n) {
        try {
            this.getInputStream();
        }
        catch (final Exception ex) {
            return null;
        }
        final MessageHeader properties = this.properties;
        return (properties == null) ? null : properties.getValue(n);
    }
    
    @Override
    public String getContentType() {
        if (this.contentType == null) {
            this.contentType = this.getHeaderField("content-type");
        }
        if (this.contentType == null) {
            String contentType = null;
            try {
                contentType = java.net.URLConnection.guessContentTypeFromStream(this.getInputStream());
            }
            catch (final IOException ex) {}
            final String value = this.properties.findValue("content-encoding");
            if (contentType == null) {
                contentType = this.properties.findValue("content-type");
                if (contentType == null) {
                    if (this.url.getFile().endsWith("/")) {
                        contentType = "text/html";
                    }
                    else {
                        contentType = java.net.URLConnection.guessContentTypeFromName(this.url.getFile());
                    }
                }
            }
            if (contentType == null || (value != null && !value.equalsIgnoreCase("7bit") && !value.equalsIgnoreCase("8bit") && !value.equalsIgnoreCase("binary"))) {
                contentType = "content/unknown";
            }
            this.setContentType(contentType);
        }
        return this.contentType;
    }
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
        this.properties.set("content-type", contentType);
    }
    
    @Override
    public int getContentLength() {
        try {
            this.getInputStream();
        }
        catch (final Exception ex) {
            return -1;
        }
        int contentLength = this.contentLength;
        if (contentLength < 0) {
            try {
                contentLength = Integer.parseInt(this.properties.findValue("content-length"));
                this.setContentLength(contentLength);
            }
            catch (final Exception ex2) {}
        }
        return contentLength;
    }
    
    protected void setContentLength(final int contentLength) {
        this.contentLength = contentLength;
        this.properties.set("content-length", String.valueOf(contentLength));
    }
    
    public boolean canCache() {
        return this.url.getFile().indexOf(63) < 0;
    }
    
    public void close() {
        this.url = null;
    }
    
    public static synchronized void setProxiedHost(final String s) {
        URLConnection.proxiedHosts.put(s.toLowerCase(), null);
    }
    
    public static synchronized boolean isProxiedHost(final String s) {
        return URLConnection.proxiedHosts.containsKey(s.toLowerCase());
    }
    
    static {
        URLConnection.proxiedHosts = new HashMap<String, Void>();
    }
}
