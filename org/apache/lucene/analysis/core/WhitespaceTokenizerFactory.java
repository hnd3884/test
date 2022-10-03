package org.apache.lucene.analysis.core;

import java.util.Arrays;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import java.util.Collection;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class WhitespaceTokenizerFactory extends TokenizerFactory
{
    public static final String RULE_JAVA = "java";
    public static final String RULE_UNICODE = "unicode";
    private static final Collection<String> RULE_NAMES;
    private final String rule;
    
    public WhitespaceTokenizerFactory(final Map<String, String> args) {
        super(args);
        this.rule = this.get(args, "rule", WhitespaceTokenizerFactory.RULE_NAMES, "java");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public Tokenizer create(final AttributeFactory factory) {
        final String rule = this.rule;
        switch (rule) {
            case "java": {
                return new WhitespaceTokenizer(factory);
            }
            case "unicode": {
                return new UnicodeWhitespaceTokenizer(factory);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    static {
        RULE_NAMES = Arrays.asList("java", "unicode");
    }
}
