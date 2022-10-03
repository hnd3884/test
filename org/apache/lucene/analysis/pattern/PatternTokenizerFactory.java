package org.apache.lucene.analysis.pattern;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class PatternTokenizerFactory extends TokenizerFactory
{
    public static final String PATTERN = "pattern";
    public static final String GROUP = "group";
    protected final Pattern pattern;
    protected final int group;
    
    public PatternTokenizerFactory(final Map<String, String> args) {
        super(args);
        this.pattern = this.getPattern(args, "pattern");
        this.group = this.getInt(args, "group", -1);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public PatternTokenizer create(final AttributeFactory factory) {
        return new PatternTokenizer(factory, this.pattern, this.group);
    }
}
