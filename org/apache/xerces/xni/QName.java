package org.apache.xerces.xni;

public class QName implements Cloneable
{
    public String prefix;
    public String localpart;
    public String rawname;
    public String uri;
    
    public QName() {
        this.clear();
    }
    
    public QName(final String s, final String s2, final String s3, final String s4) {
        this.setValues(s, s2, s3, s4);
    }
    
    public QName(final QName values) {
        this.setValues(values);
    }
    
    public void setValues(final QName qName) {
        this.prefix = qName.prefix;
        this.localpart = qName.localpart;
        this.rawname = qName.rawname;
        this.uri = qName.uri;
    }
    
    public void setValues(final String prefix, final String localpart, final String rawname, final String uri) {
        this.prefix = prefix;
        this.localpart = localpart;
        this.rawname = rawname;
        this.uri = uri;
    }
    
    public void clear() {
        this.prefix = null;
        this.localpart = null;
        this.rawname = null;
        this.uri = null;
    }
    
    public Object clone() {
        return new QName(this);
    }
    
    public int hashCode() {
        if (this.uri != null) {
            return this.uri.hashCode() + ((this.localpart != null) ? this.localpart.hashCode() : 0);
        }
        return (this.rawname != null) ? this.rawname.hashCode() : 0;
    }
    
    public boolean equals(final Object o) {
        if (o instanceof QName) {
            final QName qName = (QName)o;
            if (qName.uri != null) {
                return this.uri == qName.uri && this.localpart == qName.localpart;
            }
            if (this.uri == null) {
                return this.rawname == qName.rawname;
            }
        }
        return false;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        if (this.prefix != null) {
            sb.append("prefix=\"").append(this.prefix).append('\"');
            n = 1;
        }
        if (this.localpart != null) {
            if (n != 0) {
                sb.append(',');
            }
            sb.append("localpart=\"").append(this.localpart).append('\"');
            n = 1;
        }
        if (this.rawname != null) {
            if (n != 0) {
                sb.append(',');
            }
            sb.append("rawname=\"").append(this.rawname).append('\"');
            n = 1;
        }
        if (this.uri != null) {
            if (n != 0) {
                sb.append(',');
            }
            sb.append("uri=\"").append(this.uri).append('\"');
        }
        return sb.toString();
    }
}
