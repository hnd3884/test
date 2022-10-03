package org.apache.tika.parser;

import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.EncodingDetector;

public abstract class AbstractEncodingDetectorParser extends AbstractParser
{
    private EncodingDetector encodingDetector;
    
    public AbstractEncodingDetectorParser() {
        this.encodingDetector = new DefaultEncodingDetector();
    }
    
    public AbstractEncodingDetectorParser(final EncodingDetector encodingDetector) {
        this.encodingDetector = encodingDetector;
    }
    
    protected EncodingDetector getEncodingDetector(final ParseContext parseContext) {
        final EncodingDetector fromParseContext = parseContext.get(EncodingDetector.class);
        if (fromParseContext != null) {
            return fromParseContext;
        }
        return this.getEncodingDetector();
    }
    
    public EncodingDetector getEncodingDetector() {
        return this.encodingDetector;
    }
    
    public void setEncodingDetector(final EncodingDetector encodingDetector) {
        this.encodingDetector = encodingDetector;
    }
}
