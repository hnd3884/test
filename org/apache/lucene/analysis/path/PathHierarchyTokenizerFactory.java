package org.apache.lucene.analysis.path;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class PathHierarchyTokenizerFactory extends TokenizerFactory
{
    private final char delimiter;
    private final char replacement;
    private final boolean reverse;
    private final int skip;
    
    public PathHierarchyTokenizerFactory(final Map<String, String> args) {
        super(args);
        this.delimiter = this.getChar(args, "delimiter", '/');
        this.replacement = this.getChar(args, "replace", this.delimiter);
        this.reverse = this.getBoolean(args, "reverse", false);
        this.skip = this.getInt(args, "skip", 0);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public Tokenizer create(final AttributeFactory factory) {
        if (this.reverse) {
            return new ReversePathHierarchyTokenizer(factory, this.delimiter, this.replacement, this.skip);
        }
        return new PathHierarchyTokenizer(factory, this.delimiter, this.replacement, this.skip);
    }
}
