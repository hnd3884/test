package org.apache.commons.httpclient;

public class HttpVersion implements Comparable
{
    private int major;
    private int minor;
    public static final HttpVersion HTTP_0_9;
    public static final HttpVersion HTTP_1_0;
    public static final HttpVersion HTTP_1_1;
    
    public HttpVersion(final int major, final int minor) {
        this.major = 0;
        this.minor = 0;
        if (major < 0) {
            throw new IllegalArgumentException("HTTP major version number may not be negative");
        }
        this.major = major;
        if (minor < 0) {
            throw new IllegalArgumentException("HTTP minor version number may not be negative");
        }
        this.minor = minor;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public int hashCode() {
        return this.major * 100000 + this.minor;
    }
    
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof HttpVersion && this.equals((HttpVersion)obj));
    }
    
    public int compareTo(final HttpVersion anotherVer) {
        if (anotherVer == null) {
            throw new IllegalArgumentException("Version parameter may not be null");
        }
        int delta = this.getMajor() - anotherVer.getMajor();
        if (delta == 0) {
            delta = this.getMinor() - anotherVer.getMinor();
        }
        return delta;
    }
    
    public int compareTo(final Object o) {
        return this.compareTo((HttpVersion)o);
    }
    
    public boolean equals(final HttpVersion version) {
        return this.compareTo(version) == 0;
    }
    
    public boolean greaterEquals(final HttpVersion version) {
        return this.compareTo(version) >= 0;
    }
    
    public boolean lessEquals(final HttpVersion version) {
        return this.compareTo(version) <= 0;
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/");
        buffer.append(this.major);
        buffer.append('.');
        buffer.append(this.minor);
        return buffer.toString();
    }
    
    public static HttpVersion parse(final String s) throws ProtocolException {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        if (!s.startsWith("HTTP/")) {
            throw new ProtocolException("Invalid HTTP version string: " + s);
        }
        int i1 = "HTTP/".length();
        int i2 = s.indexOf(".", i1);
        if (i2 == -1) {
            throw new ProtocolException("Invalid HTTP version number: " + s);
        }
        int major;
        try {
            major = Integer.parseInt(s.substring(i1, i2));
        }
        catch (final NumberFormatException e) {
            throw new ProtocolException("Invalid HTTP major version number: " + s);
        }
        i1 = i2 + 1;
        i2 = s.length();
        int minor;
        try {
            minor = Integer.parseInt(s.substring(i1, i2));
        }
        catch (final NumberFormatException e) {
            throw new ProtocolException("Invalid HTTP minor version number: " + s);
        }
        return new HttpVersion(major, minor);
    }
    
    static {
        HTTP_0_9 = new HttpVersion(0, 9);
        HTTP_1_0 = new HttpVersion(1, 0);
        HTTP_1_1 = new HttpVersion(1, 1);
    }
}
