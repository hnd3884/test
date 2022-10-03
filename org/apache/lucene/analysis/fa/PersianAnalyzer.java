package org.apache.lucene.analysis.fa;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.core.DecimalDigitFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class PersianAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    public static final String STOPWORDS_COMMENT = "#";
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public PersianAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }
    
    public PersianAnalyzer(final CharArraySet stopwords) {
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
        TokenStream result = (TokenStream)new LowerCaseFilter((TokenStream)source);
        if (this.getVersion().onOrAfter(Version.LUCENE_5_4_0)) {
            result = (TokenStream)new DecimalDigitFilter(result);
        }
        result = (TokenStream)new ArabicNormalizationFilter(result);
        result = (TokenStream)new PersianNormalizationFilter(result);
        return new Analyzer.TokenStreamComponents(source, (TokenStream)new StopFilter(result, this.stopwords));
    }
    
    protected Reader initReader(final String fieldName, final Reader reader) {
        return (Reader)new PersianCharFilter(reader);
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        
        static {
            try {
                DEFAULT_STOP_SET = StopwordAnalyzerBase.loadStopwordSet(false, PersianAnalyzer.class, "stopwords.txt", "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
