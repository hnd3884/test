package org.apache.lucene.analysis.pattern;

import org.apache.lucene.analysis.CharFilter;
import java.io.Reader;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.CharFilterFactory;

public class PatternReplaceCharFilterFactory extends CharFilterFactory
{
    private final Pattern pattern;
    private final String replacement;
    
    public PatternReplaceCharFilterFactory(final Map<String, String> args) {
        super(args);
        this.pattern = this.getPattern(args, "pattern");
        this.replacement = this.get(args, "replacement", "");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public CharFilter create(final Reader input) {
        return new PatternReplaceCharFilter(this.pattern, this.replacement, input);
    }
}
