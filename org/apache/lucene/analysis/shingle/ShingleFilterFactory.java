package org.apache.lucene.analysis.shingle;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ShingleFilterFactory extends TokenFilterFactory
{
    private final int minShingleSize;
    private final int maxShingleSize;
    private final boolean outputUnigrams;
    private final boolean outputUnigramsIfNoShingles;
    private final String tokenSeparator;
    private final String fillerToken;
    
    public ShingleFilterFactory(final Map<String, String> args) {
        super(args);
        this.maxShingleSize = this.getInt(args, "maxShingleSize", 2);
        if (this.maxShingleSize < 2) {
            throw new IllegalArgumentException("Invalid maxShingleSize (" + this.maxShingleSize + ") - must be at least 2");
        }
        this.minShingleSize = this.getInt(args, "minShingleSize", 2);
        if (this.minShingleSize < 2) {
            throw new IllegalArgumentException("Invalid minShingleSize (" + this.minShingleSize + ") - must be at least 2");
        }
        if (this.minShingleSize > this.maxShingleSize) {
            throw new IllegalArgumentException("Invalid minShingleSize (" + this.minShingleSize + ") - must be no greater than maxShingleSize (" + this.maxShingleSize + ")");
        }
        this.outputUnigrams = this.getBoolean(args, "outputUnigrams", true);
        this.outputUnigramsIfNoShingles = this.getBoolean(args, "outputUnigramsIfNoShingles", false);
        this.tokenSeparator = this.get(args, "tokenSeparator", " ");
        this.fillerToken = this.get(args, "fillerToken", "_");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public ShingleFilter create(final TokenStream input) {
        final ShingleFilter r = new ShingleFilter(input, this.minShingleSize, this.maxShingleSize);
        r.setOutputUnigrams(this.outputUnigrams);
        r.setOutputUnigramsIfNoShingles(this.outputUnigramsIfNoShingles);
        r.setTokenSeparator(this.tokenSeparator);
        r.setFillerToken(this.fillerToken);
        return r;
    }
}
