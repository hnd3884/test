package org.apache.lucene.analysis.fa;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.CharFilter;
import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.CharFilterFactory;

public class PersianCharFilterFactory extends CharFilterFactory implements MultiTermAwareComponent
{
    public PersianCharFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public CharFilter create(final Reader input) {
        return new PersianCharFilter(input);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
