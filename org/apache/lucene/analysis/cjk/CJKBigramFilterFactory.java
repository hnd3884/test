package org.apache.lucene.analysis.cjk;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CJKBigramFilterFactory extends TokenFilterFactory
{
    final int flags;
    final boolean outputUnigrams;
    
    public CJKBigramFilterFactory(final Map<String, String> args) {
        super(args);
        int flags = 0;
        if (this.getBoolean(args, "han", true)) {
            flags |= 0x1;
        }
        if (this.getBoolean(args, "hiragana", true)) {
            flags |= 0x2;
        }
        if (this.getBoolean(args, "katakana", true)) {
            flags |= 0x4;
        }
        if (this.getBoolean(args, "hangul", true)) {
            flags |= 0x8;
        }
        this.flags = flags;
        this.outputUnigrams = this.getBoolean(args, "outputUnigrams", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new CJKBigramFilter(input, this.flags, this.outputUnigrams);
    }
}
