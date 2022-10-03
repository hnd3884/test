package org.apache.lucene.analysis.payloads;

import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class DelimitedPayloadTokenFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    public static final String ENCODER_ATTR = "encoder";
    public static final String DELIMITER_ATTR = "delimiter";
    private final String encoderClass;
    private final char delimiter;
    private PayloadEncoder encoder;
    
    public DelimitedPayloadTokenFilterFactory(final Map<String, String> args) {
        super(args);
        this.encoderClass = this.require(args, "encoder");
        this.delimiter = this.getChar(args, "delimiter", '|');
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public DelimitedPayloadTokenFilter create(final TokenStream input) {
        return new DelimitedPayloadTokenFilter(input, this.delimiter, this.encoder);
    }
    
    @Override
    public void inform(final ResourceLoader loader) {
        if (this.encoderClass.equals("float")) {
            this.encoder = new FloatEncoder();
        }
        else if (this.encoderClass.equals("integer")) {
            this.encoder = new IntegerEncoder();
        }
        else if (this.encoderClass.equals("identity")) {
            this.encoder = new IdentityEncoder();
        }
        else {
            this.encoder = loader.newInstance(this.encoderClass, PayloadEncoder.class);
        }
    }
}
