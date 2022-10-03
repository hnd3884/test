package org.apache.lucene.search.suggest.analyzing;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class SuggestStopFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    public static final String FORMAT_WORDSET = "wordset";
    public static final String FORMAT_SNOWBALL = "snowball";
    private CharArraySet stopWords;
    private final String stopWordFiles;
    private final String format;
    private final boolean ignoreCase;
    
    public SuggestStopFilterFactory(final Map<String, String> args) {
        super((Map)args);
        this.stopWordFiles = this.get((Map)args, "words");
        this.format = this.get((Map)args, "format", (null == this.stopWordFiles) ? null : "wordset");
        this.ignoreCase = this.getBoolean((Map)args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
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
            this.stopWords = new CharArraySet((Collection)StopAnalyzer.ENGLISH_STOP_WORDS_SET, this.ignoreCase);
        }
    }
    
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    public CharArraySet getStopWords() {
        return this.stopWords;
    }
    
    public TokenStream create(final TokenStream input) {
        final SuggestStopFilter suggestStopFilter = new SuggestStopFilter(input, this.stopWords);
        return (TokenStream)suggestStopFilter;
    }
}
