package org.apache.lucene.analysis.compound;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.Version;
import org.xml.sax.InputSource;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.compound.hyphenation.HyphenationTree;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class HyphenationCompoundWordTokenFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private CharArraySet dictionary;
    private HyphenationTree hyphenator;
    private final String dictFile;
    private final String hypFile;
    private final String encoding;
    private final int minWordSize;
    private final int minSubwordSize;
    private final int maxSubwordSize;
    private final boolean onlyLongestMatch;
    
    public HyphenationCompoundWordTokenFilterFactory(final Map<String, String> args) {
        super(args);
        this.dictFile = this.get(args, "dictionary");
        this.encoding = this.get(args, "encoding");
        this.hypFile = this.require(args, "hyphenator");
        this.minWordSize = this.getInt(args, "minWordSize", 5);
        this.minSubwordSize = this.getInt(args, "minSubwordSize", 2);
        this.maxSubwordSize = this.getInt(args, "maxSubwordSize", 15);
        this.onlyLongestMatch = this.getBoolean(args, "onlyLongestMatch", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        InputStream stream = null;
        try {
            if (this.dictFile != null) {
                this.dictionary = this.getWordSet(loader, this.dictFile, false);
            }
            stream = loader.openResource(this.hypFile);
            final InputSource is = new InputSource(stream);
            is.setEncoding(this.encoding);
            is.setSystemId(this.hypFile);
            if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
                this.hyphenator = HyphenationCompoundWordTokenFilter.getHyphenationTree(is);
            }
            else {
                this.hyphenator = Lucene43HyphenationCompoundWordTokenFilter.getHyphenationTree(is);
            }
        }
        finally {
            IOUtils.closeWhileHandlingException(new Closeable[] { stream });
        }
    }
    
    public TokenFilter create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return new HyphenationCompoundWordTokenFilter(input, this.hyphenator, this.dictionary, this.minWordSize, this.minSubwordSize, this.maxSubwordSize, this.onlyLongestMatch);
        }
        return new Lucene43HyphenationCompoundWordTokenFilter(input, this.hyphenator, this.dictionary, this.minWordSize, this.minSubwordSize, this.maxSubwordSize, this.onlyLongestMatch);
    }
}
