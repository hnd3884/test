package org.apache.lucene.analysis.util;

import org.apache.lucene.analysis.TokenStream;
import java.util.Set;
import java.util.Map;

public abstract class TokenFilterFactory extends AbstractAnalysisFactory
{
    private static final AnalysisSPILoader<TokenFilterFactory> loader;
    
    public static TokenFilterFactory forName(final String name, final Map<String, String> args) {
        return TokenFilterFactory.loader.newInstance(name, args);
    }
    
    public static Class<? extends TokenFilterFactory> lookupClass(final String name) {
        return TokenFilterFactory.loader.lookupClass(name);
    }
    
    public static Set<String> availableTokenFilters() {
        return TokenFilterFactory.loader.availableServices();
    }
    
    public static void reloadTokenFilters(final ClassLoader classloader) {
        TokenFilterFactory.loader.reload(classloader);
    }
    
    protected TokenFilterFactory(final Map<String, String> args) {
        super(args);
    }
    
    public abstract TokenStream create(final TokenStream p0);
    
    static {
        loader = new AnalysisSPILoader<TokenFilterFactory>(TokenFilterFactory.class, new String[] { "TokenFilterFactory", "FilterFactory" });
    }
}
