package org.apache.tika.mime;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import java.io.ByteArrayInputStream;
import org.apache.tika.detect.MagicDetector;

class MagicMatch implements Clause
{
    private final MediaType mediaType;
    private final String type;
    private final String offset;
    private final String value;
    private final String mask;
    private MagicDetector detector;
    
    MagicMatch(final MediaType mediaType, final String type, final String offset, final String value, final String mask) {
        this.detector = null;
        this.mediaType = mediaType;
        this.type = type;
        this.offset = offset;
        this.value = value;
        this.mask = mask;
    }
    
    private synchronized MagicDetector getDetector() {
        if (this.detector == null) {
            this.detector = MagicDetector.parse(this.mediaType, this.type, this.offset, this.value, this.mask);
        }
        return this.detector;
    }
    
    @Override
    public boolean eval(final byte[] data) {
        try {
            return this.getDetector().detect(new ByteArrayInputStream(data), new Metadata()) != MediaType.OCTET_STREAM;
        }
        catch (final IOException e) {
            return false;
        }
    }
    
    @Override
    public int size() {
        return this.getDetector().getLength();
    }
    
    @Override
    public String toString() {
        return this.mediaType.toString() + " " + this.type + " " + this.offset + " " + this.value + " " + this.mask;
    }
}
