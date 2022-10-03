package org.apache.lucene.analysis.util;

import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import java.util.Set;
import java.util.Map;

public abstract class TokenizerFactory extends AbstractAnalysisFactory
{
    private static final AnalysisSPILoader<TokenizerFactory> loader;
    
    public static TokenizerFactory forName(final String name, final Map<String, String> args) {
        return TokenizerFactory.loader.newInstance(name, args);
    }
    
    public static Class<? extends TokenizerFactory> lookupClass(final String name) {
        return TokenizerFactory.loader.lookupClass(name);
    }
    
    public static Set<String> availableTokenizers() {
        return TokenizerFactory.loader.availableServices();
    }
    
    public static void reloadTokenizers(final ClassLoader classloader) {
        TokenizerFactory.loader.reload(classloader);
    }
    
    protected TokenizerFactory(final Map<String, String> args) {
        super(args);
    }
    
    public final Tokenizer create() {
        return this.create(TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
    }
    
    public abstract Tokenizer create(final AttributeFactory p0);
    
    static {
        loader = new AnalysisSPILoader<TokenizerFactory>(TokenizerFactory.class);
    }
}
