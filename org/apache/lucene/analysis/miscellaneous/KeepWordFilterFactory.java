package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.util.Version;
import java.util.Map;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class KeepWordFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private final boolean ignoreCase;
    private final String wordFiles;
    private CharArraySet words;
    private boolean enablePositionIncrements;
    
    public KeepWordFilterFactory(final Map<String, String> args) {
        super(args);
        this.wordFiles = this.get(args, "words");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
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
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.wordFiles != null) {
            this.words = this.getWordSet(loader, this.wordFiles, this.ignoreCase);
        }
    }
    
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    public CharArraySet getWords() {
        return this.words;
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        if (this.words == null) {
            return input;
        }
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return (TokenStream)new KeepWordFilter(input, this.words);
        }
        final TokenStream filter = (TokenStream)new Lucene43KeepWordFilter(this.enablePositionIncrements, input, this.words);
        return filter;
    }
}
