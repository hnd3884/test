package eu.medsea.mimeutil;

import eu.medsea.util.EncodingGuesser;

public class TextMimeType extends MimeType
{
    private static final long serialVersionUID = -4798584119063522367L;
    private String encoding;
    
    public TextMimeType(final String mimeType, final String encoding) {
        super(mimeType);
        this.encoding = "Unknown";
        this.encoding = this.getValidEncoding(encoding);
    }
    
    public TextMimeType(final MimeType mimeType, final String encoding) {
        super(mimeType);
        this.encoding = "Unknown";
        this.encoding = this.getValidEncoding(encoding);
    }
    
    public TextMimeType(final MimeType mimeType) {
        super(mimeType);
        this.encoding = "Unknown";
    }
    
    public void setMimeType(final MimeType mimeType) {
        this.mediaType = mimeType.mediaType;
        this.subType = mimeType.subType;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String toString() {
        return String.valueOf(super.toString()) + ";charset=" + this.getEncoding();
    }
    
    private boolean isKnownEncoding(final String encoding) {
        return EncodingGuesser.isKnownEncoding(encoding);
    }
    
    private String getValidEncoding(final String encoding) {
        if (this.isKnownEncoding(encoding)) {
            return encoding;
        }
        return "Unknown";
    }
    
    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }
    
    public void setSubType(final String subType) {
        this.subType = subType;
    }
}
