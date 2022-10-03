package org.apache.tika.detect;

import org.apache.tika.config.Field;
import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

public class NonDetectingEncodingDetector implements EncodingDetector
{
    private Charset charset;
    
    public NonDetectingEncodingDetector() {
        this.charset = StandardCharsets.UTF_8;
    }
    
    public NonDetectingEncodingDetector(final Charset charset) {
        this.charset = StandardCharsets.UTF_8;
        this.charset = charset;
    }
    
    @Override
    public Charset detect(final InputStream input, final Metadata metadata) throws IOException {
        return this.charset;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    @Field
    private void setCharset(final String charsetName) {
        this.charset = Charset.forName(charsetName);
    }
}
