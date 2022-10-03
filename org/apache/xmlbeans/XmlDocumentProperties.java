package org.apache.xmlbeans;

public abstract class XmlDocumentProperties
{
    public static final Object SOURCE_NAME;
    public static final Object ENCODING;
    public static final Object VERSION;
    public static final Object STANDALONE;
    public static final Object DOCTYPE_NAME;
    public static final Object DOCTYPE_PUBLIC_ID;
    public static final Object DOCTYPE_SYSTEM_ID;
    public static final Object MESSAGE_DIGEST;
    
    public void setSourceName(final String sourceName) {
        this.put(XmlDocumentProperties.SOURCE_NAME, sourceName);
    }
    
    public String getSourceName() {
        return (String)this.get(XmlDocumentProperties.SOURCE_NAME);
    }
    
    public void setEncoding(final String encoding) {
        this.put(XmlDocumentProperties.ENCODING, encoding);
    }
    
    public String getEncoding() {
        return (String)this.get(XmlDocumentProperties.ENCODING);
    }
    
    public void setVersion(final String version) {
        this.put(XmlDocumentProperties.VERSION, version);
    }
    
    public String getVersion() {
        return (String)this.get(XmlDocumentProperties.VERSION);
    }
    
    public void setStandalone(final boolean standalone) {
        this.put(XmlDocumentProperties.STANDALONE, standalone ? "true" : null);
    }
    
    public boolean getStandalone() {
        return this.get(XmlDocumentProperties.STANDALONE) != null;
    }
    
    public void setDoctypeName(final String doctypename) {
        this.put(XmlDocumentProperties.DOCTYPE_NAME, doctypename);
    }
    
    public String getDoctypeName() {
        return (String)this.get(XmlDocumentProperties.DOCTYPE_NAME);
    }
    
    public void setDoctypePublicId(final String publicid) {
        this.put(XmlDocumentProperties.DOCTYPE_PUBLIC_ID, publicid);
    }
    
    public String getDoctypePublicId() {
        return (String)this.get(XmlDocumentProperties.DOCTYPE_PUBLIC_ID);
    }
    
    public void setDoctypeSystemId(final String systemid) {
        this.put(XmlDocumentProperties.DOCTYPE_SYSTEM_ID, systemid);
    }
    
    public String getDoctypeSystemId() {
        return (String)this.get(XmlDocumentProperties.DOCTYPE_SYSTEM_ID);
    }
    
    public void setMessageDigest(final byte[] digest) {
        this.put(XmlDocumentProperties.MESSAGE_DIGEST, digest);
    }
    
    public byte[] getMessageDigest() {
        return (byte[])this.get(XmlDocumentProperties.MESSAGE_DIGEST);
    }
    
    public abstract Object put(final Object p0, final Object p1);
    
    public abstract Object get(final Object p0);
    
    public abstract Object remove(final Object p0);
    
    static {
        SOURCE_NAME = new Object();
        ENCODING = new Object();
        VERSION = new Object();
        STANDALONE = new Object();
        DOCTYPE_NAME = new Object();
        DOCTYPE_PUBLIC_ID = new Object();
        DOCTYPE_SYSTEM_ID = new Object();
        MESSAGE_DIGEST = new Object();
    }
}
