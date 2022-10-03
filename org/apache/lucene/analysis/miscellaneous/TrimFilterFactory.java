package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TrimFilterFactory extends TokenFilterFactory
{
    private boolean updateOffsets;
    
    public TrimFilterFactory(final Map<String, String> args) {
        super(args);
        if (!this.luceneMatchVersion.onOrAfter(Version.LUCENE_5_0_0)) {
            this.updateOffsets = this.getBoolean(args, "updateOffsets", false);
            if (this.updateOffsets && this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
                throw new IllegalArgumentException("updateOffsets=true is not supported anymore as of Lucene 4.4");
            }
        }
        else if (args.containsKey("updateOffsets")) {
            throw new IllegalArgumentException("updateOffsets is not a valid option as of Lucene 5.0");
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public TokenFilter create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return new TrimFilter(input);
        }
        final Lucene43TrimFilter filter = new Lucene43TrimFilter(input, this.updateOffsets);
        return filter;
    }
}
