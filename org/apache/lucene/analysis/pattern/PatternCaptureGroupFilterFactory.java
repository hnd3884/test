package org.apache.lucene.analysis.pattern;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class PatternCaptureGroupFilterFactory extends TokenFilterFactory
{
    private Pattern pattern;
    private boolean preserveOriginal;
    
    public PatternCaptureGroupFilterFactory(final Map<String, String> args) {
        super(args);
        this.preserveOriginal = true;
        this.pattern = this.getPattern(args, "pattern");
        this.preserveOriginal = (!args.containsKey("preserve_original") || Boolean.parseBoolean(args.get("preserve_original")));
    }
    
    public PatternCaptureGroupTokenFilter create(final TokenStream input) {
        return new PatternCaptureGroupTokenFilter(input, this.preserveOriginal, new Pattern[] { this.pattern });
    }
}
