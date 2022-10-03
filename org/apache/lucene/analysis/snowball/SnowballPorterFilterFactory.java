package org.apache.lucene.analysis.snowball;

import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.util.CharArraySet;
import org.tartarus.snowball.SnowballProgram;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class SnowballPorterFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    public static final String PROTECTED_TOKENS = "protected";
    private final String language;
    private final String wordFiles;
    private Class<? extends SnowballProgram> stemClass;
    private CharArraySet protectedWords;
    
    public SnowballPorterFilterFactory(final Map<String, String> args) {
        super(args);
        this.protectedWords = null;
        this.language = this.get(args, "language", "English");
        this.wordFiles = this.get(args, "protected");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        final String className = "org.tartarus.snowball.ext." + this.language + "Stemmer";
        this.stemClass = loader.newInstance(className, SnowballProgram.class).getClass();
        if (this.wordFiles != null) {
            this.protectedWords = this.getWordSet(loader, this.wordFiles, false);
        }
    }
    
    public TokenFilter create(TokenStream input) {
        SnowballProgram program;
        try {
            program = (SnowballProgram)this.stemClass.newInstance();
        }
        catch (final Exception e) {
            throw new RuntimeException("Error instantiating stemmer for language " + this.language + "from class " + this.stemClass, e);
        }
        if (this.protectedWords != null) {
            input = (TokenStream)new SetKeywordMarkerFilter(input, this.protectedWords);
        }
        return new SnowballFilter(input, program);
    }
}
