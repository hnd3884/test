package org.htmlparser.http;

import java.util.Date;
import java.io.Serializable;

public class Cookie implements Cloneable, Serializable
{
    private static final String SPECIALS = "()<>@,;:\\\"/[]?={} \t";
    protected String mName;
    protected String mValue;
    protected String mComment;
    protected String mDomain;
    protected Date mExpiry;
    protected String mPath;
    protected boolean mSecure;
    protected int mVersion;
    
    public Cookie(final String name, final String value) throws IllegalArgumentException {
        if (!this.isToken(name) || name.equalsIgnoreCase("Comment") || name.equalsIgnoreCase("Discard") || name.equalsIgnoreCase("Domain") || name.equalsIgnoreCase("Expires") || name.equalsIgnoreCase("Max-Age") || name.equalsIgnoreCase("Path") || name.equalsIgnoreCase("Secure") || name.equalsIgnoreCase("Version")) {
            throw new IllegalArgumentException("invalid cookie name: " + name);
        }
        this.mName = name;
        this.mValue = value;
        this.mComment = null;
        this.mDomain = null;
        this.mExpiry = null;
        this.mPath = "/";
        this.mSecure = false;
        this.mVersion = 0;
    }
    
    public void setComment(final String purpose) {
        this.mComment = purpose;
    }
    
    public String getComment() {
        return this.mComment;
    }
    
    public void setDomain(final String pattern) {
        this.mDomain = pattern.toLowerCase();
    }
    
    public String getDomain() {
        return this.mDomain;
    }
    
    public void setExpiryDate(final Date expiry) {
        this.mExpiry = expiry;
    }
    
    public Date getExpiryDate() {
        return this.mExpiry;
    }
    
    public void setPath(final String uri) {
        this.mPath = uri;
    }
    
    public String getPath() {
        return this.mPath;
    }
    
    public void setSecure(final boolean flag) {
        this.mSecure = flag;
    }
    
    public boolean getSecure() {
        return this.mSecure;
    }
    
    public String getName() {
        return this.mName;
    }
    
    public void setValue(final String newValue) {
        this.mValue = newValue;
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    public int getVersion() {
        return this.mVersion;
    }
    
    public void setVersion(final int version) {
        this.mVersion = version;
    }
    
    private boolean isToken(final String value) {
        boolean ret = true;
        for (int length = value.length(), i = 0; i < length && ret; ++i) {
            final char c = value.charAt(i);
            if (c < ' ' || c > '~' || "()<>@,;:\\\"/[]?={} \t".indexOf(c) != -1) {
                ret = false;
            }
        }
        return ret;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer(50);
        if (this.getSecure()) {
            ret.append("secure ");
        }
        if (0 != this.getVersion()) {
            ret.append("version ");
            ret.append(this.getVersion());
            ret.append(" ");
        }
        ret.append("cookie");
        if (null != this.getDomain()) {
            ret.append(" for ");
            ret.append(this.getDomain());
            if (null != this.getPath()) {
                ret.append(this.getPath());
            }
        }
        else if (null != this.getPath()) {
            ret.append(" (path ");
            ret.append(this.getPath());
            ret.append(")");
        }
        ret.append(": ");
        ret.append(this.getName());
        ret.append(this.getName().equals("") ? "" : "=");
        if (this.getValue().length() > 40) {
            ret.append(this.getValue().substring(1, 40));
            ret.append("...");
        }
        else {
            ret.append(this.getValue());
        }
        if (null != this.getComment()) {
            ret.append(" // ");
            ret.append(this.getComment());
        }
        return ret.toString();
    }
}
