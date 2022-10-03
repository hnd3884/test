package org.apache.lucene.analysis.cjk;

import java.io.IOException;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class CJKAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public CJKAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }
    
    public CJKAnalyzer(final CharArraySet stopwords) {
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
        TokenStream result = (TokenStream)new CJKWidthFilter((TokenStream)source);
        result = (TokenStream)new LowerCaseFilter(result);
        result = (TokenStream)new CJKBigramFilter(result);
        return new Analyzer.TokenStreamComponents(source, (TokenStream)new StopFilter(result, this.stopwords));
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        
        static {
            try {
                DEFAULT_STOP_SET = StopwordAnalyzerBase.loadStopwordSet(false, CJKAnalyzer.class, "stopwords.txt", "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
