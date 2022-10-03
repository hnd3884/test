package eu.medsea.mimeutil;

import java.util.regex.Pattern;
import java.io.Serializable;

public class MimeType implements Comparable, Serializable
{
    private static final long serialVersionUID = -1324243127744494894L;
    private static final Pattern mimeSplitter;
    protected String mediaType;
    protected String subType;
    private int specificity;
    
    static {
        mimeSplitter = Pattern.compile("[/;]++");
    }
    
    public MimeType(final MimeType mimeType) {
        this.mediaType = "*";
        this.subType = "*";
        this.specificity = 1;
        this.mediaType = mimeType.mediaType;
        this.subType = mimeType.subType;
        this.specificity = mimeType.specificity;
    }
    
    public MimeType(final String mimeType) throws MimeException {
        this.mediaType = "*";
        this.subType = "*";
        this.specificity = 1;
        if (mimeType == null || mimeType.trim().length() == 0) {
            throw new MimeException("Invalid MimeType [" + mimeType + "]");
        }
        final String[] parts = MimeType.mimeSplitter.split(mimeType.trim());
        if (parts.length > 0) {
            this.mediaType = this.getValidMediaType(parts[0]);
        }
        if (parts.length > 1) {
            this.subType = this.getValidSubType(parts[1]);
        }
    }
    
    public String getMediaType() {
        return this.mediaType;
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    private boolean match(final String mimeType) {
        return this.toString().equals(mimeType);
    }
    
    public int hashCode() {
        return 31 * this.mediaType.hashCode() + this.subType.hashCode();
    }
    
    public boolean equals(final Object o) {
        if (o instanceof MimeType) {
            if (this.mediaType.equals(((MimeType)o).mediaType) && this.subType.equals(((MimeType)o).subType)) {
                return true;
            }
        }
        else if (o instanceof String) {
            return this.match((String)o);
        }
        return false;
    }
    
    public String toString() {
        return String.valueOf(this.mediaType) + "/" + this.subType;
    }
    
    public int getSpecificity() {
        return this.specificity;
    }
    
    void setSpecificity(final int specificity) {
        this.specificity = specificity;
    }
    
    private String getValidMediaType(final String mediaType) {
        if (mediaType == null || mediaType.trim().length() == 0) {
            return "*";
        }
        return mediaType;
    }
    
    private String getValidSubType(final String subType) {
        if (subType == null || subType.trim().length() == 0 || "*".equals(this.mediaType)) {
            return "*";
        }
        return subType;
    }
    
    public int compareTo(final Object arg0) {
        if (arg0 instanceof MimeType) {
            return this.toString().compareTo(((MimeType)arg0).toString());
        }
        return 0;
    }
}
