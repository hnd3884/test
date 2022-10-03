package org.apache.lucene.analysis.util;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import java.util.Map;

public class ElisionFilterFactory extends TokenFilterFactory implements ResourceLoaderAware, MultiTermAwareComponent
{
    private final String articlesFile;
    private final boolean ignoreCase;
    private CharArraySet articles;
    
    public ElisionFilterFactory(final Map<String, String> args) {
        super(args);
        this.articlesFile = this.get(args, "articles");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.articlesFile == null) {
            this.articles = FrenchAnalyzer.DEFAULT_ARTICLES;
        }
        else {
            this.articles = this.getWordSet(loader, this.articlesFile, this.ignoreCase);
        }
    }
    
    public ElisionFilter create(final TokenStream input) {
        return new ElisionFilter(input, this.articles);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
