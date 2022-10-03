package org.apache.lucene.analysis.util;

import java.io.Reader;
import java.util.Set;
import java.util.Map;

public abstract class CharFilterFactory extends AbstractAnalysisFactory
{
    private static final AnalysisSPILoader<CharFilterFactory> loader;
    
    public static CharFilterFactory forName(final String name, final Map<String, String> args) {
        return CharFilterFactory.loader.newInstance(name, args);
    }
    
    public static Class<? extends CharFilterFactory> lookupClass(final String name) {
        return CharFilterFactory.loader.lookupClass(name);
    }
    
    public static Set<String> availableCharFilters() {
        return CharFilterFactory.loader.availableServices();
    }
    
    public static void reloadCharFilters(final ClassLoader classloader) {
        CharFilterFactory.loader.reload(classloader);
    }
    
    protected CharFilterFactory(final Map<String, String> args) {
        super(args);
    }
    
    public abstract Reader create(final Reader p0);
    
    static {
        loader = new AnalysisSPILoader<CharFilterFactory>(CharFilterFactory.class);
    }
}
