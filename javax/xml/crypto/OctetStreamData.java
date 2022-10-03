package javax.xml.crypto;

import java.io.InputStream;

public class OctetStreamData implements Data
{
    private InputStream octetStream;
    private String uri;
    private String mimeType;
    
    public OctetStreamData(final InputStream octetStream) {
        if (octetStream == null) {
            throw new NullPointerException("octetStream is null");
        }
        this.octetStream = octetStream;
    }
    
    public OctetStreamData(final InputStream octetStream, final String uri, final String mimeType) {
        if (octetStream == null) {
            throw new NullPointerException("octetStream is null");
        }
        this.octetStream = octetStream;
        this.uri = uri;
        this.mimeType = mimeType;
    }
    
    public InputStream getOctetStream() {
        return this.octetStream;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
}
