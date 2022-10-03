package org.apache.lucene.analysis.compound;

import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class DictionaryCompoundWordTokenFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private CharArraySet dictionary;
    private final String dictFile;
    private final int minWordSize;
    private final int minSubwordSize;
    private final int maxSubwordSize;
    private final boolean onlyLongestMatch;
    
    public DictionaryCompoundWordTokenFilterFactory(final Map<String, String> args) {
        super(args);
        this.dictFile = this.require(args, "dictionary");
        this.minWordSize = this.getInt(args, "minWordSize", 5);
        this.minSubwordSize = this.getInt(args, "minSubwordSize", 2);
        this.maxSubwordSize = this.getInt(args, "maxSubwordSize", 15);
        this.onlyLongestMatch = this.getBoolean(args, "onlyLongestMatch", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        this.dictionary = super.getWordSet(loader, this.dictFile, false);
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        if (this.dictionary == null) {
            return input;
        }
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return (TokenStream)new DictionaryCompoundWordTokenFilter(input, this.dictionary, this.minWordSize, this.minSubwordSize, this.maxSubwordSize, this.onlyLongestMatch);
        }
        return (TokenStream)new Lucene43DictionaryCompoundWordTokenFilter(input, this.dictionary, this.minWordSize, this.minSubwordSize, this.maxSubwordSize, this.onlyLongestMatch);
    }
}
