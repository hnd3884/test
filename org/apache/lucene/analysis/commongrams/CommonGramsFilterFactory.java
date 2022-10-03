package org.apache.lucene.analysis.commongrams;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CommonGramsFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private CharArraySet commonWords;
    private final String commonWordFiles;
    private final String format;
    private final boolean ignoreCase;
    
    public CommonGramsFilterFactory(final Map<String, String> args) {
        super(args);
        this.commonWordFiles = this.get(args, "words");
        this.format = this.get(args, "format");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.commonWordFiles != null) {
            if ("snowball".equalsIgnoreCase(this.format)) {
                this.commonWords = this.getSnowballWordSet(loader, this.commonWordFiles, this.ignoreCase);
            }
            else {
                this.commonWords = this.getWordSet(loader, this.commonWordFiles, this.ignoreCase);
            }
        }
        else {
            this.commonWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
        }
    }
    
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    public CharArraySet getCommonWords() {
        return this.commonWords;
    }
    
    public TokenFilter create(final TokenStream input) {
        final CommonGramsFilter commonGrams = new CommonGramsFilter(input, this.commonWords);
        return commonGrams;
    }
}
