package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.util.CharArraySet;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class KeywordMarkerFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    public static final String PROTECTED_TOKENS = "protected";
    public static final String PATTERN = "pattern";
    private final String wordFiles;
    private final String stringPattern;
    private final boolean ignoreCase;
    private Pattern pattern;
    private CharArraySet protectedWords;
    
    public KeywordMarkerFilterFactory(final Map<String, String> args) {
        super(args);
        this.wordFiles = this.get(args, "protected");
        this.stringPattern = this.get(args, "pattern");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.wordFiles != null) {
            this.protectedWords = this.getWordSet(loader, this.wordFiles, this.ignoreCase);
        }
        if (this.stringPattern != null) {
            this.pattern = (this.ignoreCase ? Pattern.compile(this.stringPattern, 66) : Pattern.compile(this.stringPattern));
        }
    }
    
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    @Override
    public TokenStream create(TokenStream input) {
        if (this.pattern != null) {
            input = (TokenStream)new PatternKeywordMarkerFilter(input, this.pattern);
        }
        if (this.protectedWords != null) {
            input = (TokenStream)new SetKeywordMarkerFilter(input, this.protectedWords);
        }
        return input;
    }
}
