package org.apache.lucene.analysis.el;

import java.io.IOException;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class GreekAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    
    public static final CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_SET;
    }
    
    public GreekAnalyzer() {
        this(DefaultSetHolder.DEFAULT_SET);
    }
    
    public GreekAnalyzer(final CharArraySet stopwords) {
        super(stopwords);
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        Tokenizer source;
        if (this.getVersion().onOrAfter(Version.LUCENE_4_7_0)) {
            source = new StandardTokenizer();
        }
        else {
            source = new StandardTokenizer40();
        }
        TokenStream result = (TokenStream)new GreekLowerCaseFilter((TokenStream)source);
        result = (TokenStream)new StandardFilter(result);
        result = (TokenStream)new StopFilter(result, this.stopwords);
        result = (TokenStream)new GreekStemFilter(result);
        return new Analyzer.TokenStreamComponents(source, result);
    }
    
    private static class DefaultSetHolder
    {
        private static final CharArraySet DEFAULT_SET;
        
        static {
            try {
                DEFAULT_SET = StopwordAnalyzerBase.loadStopwordSet(false, GreekAnalyzer.class, "stopwords.txt", "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
