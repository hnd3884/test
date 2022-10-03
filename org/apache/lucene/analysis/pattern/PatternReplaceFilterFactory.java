package org.apache.lucene.analysis.pattern;

import org.apache.lucene.analysis.TokenStream;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class PatternReplaceFilterFactory extends TokenFilterFactory
{
    final Pattern pattern;
    final String replacement;
    final boolean replaceAll;
    
    public PatternReplaceFilterFactory(final Map<String, String> args) {
        super(args);
        this.pattern = this.getPattern(args, "pattern");
        this.replacement = this.get(args, "replacement");
        this.replaceAll = "all".equals(this.get(args, "replace", Arrays.asList("all", "first"), "all"));
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public PatternReplaceFilter create(final TokenStream input) {
        return new PatternReplaceFilter(input, this.pattern, this.replacement, this.replaceAll);
    }
}
