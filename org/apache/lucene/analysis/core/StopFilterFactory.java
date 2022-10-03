package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.util.Version;
import java.util.Map;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StopFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    public static final String FORMAT_WORDSET = "wordset";
    public static final String FORMAT_SNOWBALL = "snowball";
    private CharArraySet stopWords;
    private final String stopWordFiles;
    private final String format;
    private final boolean ignoreCase;
    private boolean enablePositionIncrements;
    
    public StopFilterFactory(final Map<String, String> args) {
        super(args);
        this.stopWordFiles = this.get(args, "words");
        this.format = this.get(args, "format", (null == this.stopWordFiles) ? null : "wordset");
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
        if (this.stopWordFiles != null) {
            if ("wordset".equalsIgnoreCase(this.format)) {
                this.stopWords = this.getWordSet(loader, this.stopWordFiles, this.ignoreCase);
            }
            else {
                if (!"snowball".equalsIgnoreCase(this.format)) {
                    throw new IllegalArgumentException("Unknown 'format' specified for 'words' file: " + this.format);
                }
                this.stopWords = this.getSnowballWordSet(loader, this.stopWordFiles, this.ignoreCase);
            }
        }
        else {
            if (null != this.format) {
                throw new IllegalArgumentException("'format' can not be specified w/o an explicit 'words' file: " + this.format);
            }
            this.stopWords = new CharArraySet(StopAnalyzer.ENGLISH_STOP_WORDS_SET, this.ignoreCase);
        }
    }
    
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    public CharArraySet getStopWords() {
        return this.stopWords;
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return (TokenStream)new StopFilter(input, this.stopWords);
        }
        final TokenStream filter = (TokenStream)new Lucene43StopFilter(this.enablePositionIncrements, input, this.stopWords);
        return filter;
    }
}
