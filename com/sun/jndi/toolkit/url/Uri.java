package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;

public class Uri
{
    protected String uri;
    protected String scheme;
    protected String host;
    protected int port;
    protected boolean hasAuthority;
    protected String path;
    protected String query;
    
    public Uri(final String s) throws MalformedURLException {
        this.host = null;
        this.port = -1;
        this.query = null;
        this.init(s);
    }
    
    protected Uri() {
        this.host = null;
        this.port = -1;
        this.query = null;
    }
    
    protected void init(final String uri) throws MalformedURLException {
        this.parse(this.uri = uri);
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getQuery() {
        return this.query;
    }
    
    @Override
    public String toString() {
        return this.uri;
    }
    
    private void parse(final String s) throws MalformedURLException {
        int index = s.indexOf(58);
        if (index < 0) {
            throw new MalformedURLException("Invalid URI: " + s);
        }
        this.scheme = s.substring(0, index);
        ++index;
        this.hasAuthority = s.startsWith("//", index);
        if (this.hasAuthority) {
            index += 2;
            int n = s.indexOf(47, index);
            if (n < 0) {
                n = s.length();
            }
            int n2;
            if (s.startsWith("[", index)) {
                final int index2 = s.indexOf(93, index + 1);
                if (index2 < 0 || index2 > n) {
                    throw new MalformedURLException("Invalid URI: " + s);
                }
                this.host = s.substring(index, index2 + 1);
                n2 = index2 + 1;
            }
            else {
                final int index3 = s.indexOf(58, index);
                final int n3 = (index3 < 0 || index3 > n) ? n : index3;
                if (index < n3) {
                    this.host = s.substring(index, n3);
                }
                n2 = n3;
            }
            if (n2 + 1 < n && s.startsWith(":", n2)) {
                ++n2;
                this.port = Integer.parseInt(s.substring(n2, n));
            }
            index = n;
        }
        final int index4 = s.indexOf(63, index);
        if (index4 < 0) {
            this.path = s.substring(index);
        }
        else {
            this.path = s.substring(index, index4);
            this.query = s.substring(index4);
        }
    }
}
