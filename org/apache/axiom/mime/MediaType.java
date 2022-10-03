package org.apache.axiom.mime;

import java.text.ParseException;

public final class MediaType
{
    public static final MediaType TEXT_XML;
    public static final MediaType APPLICATION_XML;
    public static final MediaType APPLICATION_SOAP_XML;
    public static final MediaType APPLICATION_XOP_XML;
    public static final MediaType MULTIPART_RELATED;
    private final String primaryType;
    private final String subType;
    
    public MediaType(final String primaryType, final String subType) {
        this.primaryType = primaryType;
        this.subType = subType;
    }
    
    public MediaType(final String type) throws ParseException {
        final ContentTypeTokenizer tokenizer = new ContentTypeTokenizer(type);
        this.primaryType = tokenizer.requireToken();
        tokenizer.require('/');
        this.subType = tokenizer.requireToken();
        tokenizer.requireEndOfString();
    }
    
    public String getPrimaryType() {
        return this.primaryType;
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0, l = this.primaryType.length(); i < l; ++i) {
            hash = 31 * hash + Character.toLowerCase(this.primaryType.charAt(i));
        }
        hash *= 31;
        for (int i = 0, l = this.subType.length(); i < l; ++i) {
            hash = 31 * hash + Character.toLowerCase(this.subType.charAt(i));
        }
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MediaType) {
            final MediaType other = (MediaType)obj;
            return this.primaryType.equalsIgnoreCase(other.primaryType) && this.subType.equalsIgnoreCase(other.subType);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.primaryType + "/" + this.subType;
    }
    
    static {
        TEXT_XML = new MediaType("text", "xml");
        APPLICATION_XML = new MediaType("application", "xml");
        APPLICATION_SOAP_XML = new MediaType("application", "soap+xml");
        APPLICATION_XOP_XML = new MediaType("application", "xop+xml");
        MULTIPART_RELATED = new MediaType("multipart", "related");
    }
}
