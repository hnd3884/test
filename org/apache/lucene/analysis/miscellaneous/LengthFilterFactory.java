package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LengthFilterFactory extends TokenFilterFactory
{
    final int min;
    final int max;
    public static final String MIN_KEY = "min";
    public static final String MAX_KEY = "max";
    private boolean enablePositionIncrements;
    
    public LengthFilterFactory(final Map<String, String> args) {
        super(args);
        this.min = this.requireInt(args, "min");
        this.max = this.requireInt(args, "max");
        if (!this.luceneMatchVersion.onOrAfter(Version.LUCENE_5_0_0)) {
            final boolean defaultValue = this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0);
            this.enablePositionIncrements = this.getBoolean(args, "enablePositionIncrements", defaultValue);
            if (!this.enablePositionIncrements && this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
                throw new IllegalArgumentException("enablePositionIncrements=false is not supported anymore as of Lucene 4.4");
            }
        }
        else if (args.containsKey("enablePositionIncrements")) {
            throw new IllegalArgumentException("enablePositionIncrements is not a valid option as of Lucene 5.0");
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public TokenFilter create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return new LengthFilter(input, this.min, this.max);
        }
        final TokenFilter filter = new Lucene43LengthFilter(this.enablePositionIncrements, input, this.min, this.max);
        return filter;
    }
}
