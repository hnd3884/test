package org.apache.lucene.analysis.ar;

import java.io.IOException;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.DecimalDigitFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class ArabicAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private final CharArraySet stemExclusionSet;
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public ArabicAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }
    
    public ArabicAnalyzer(final CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }
    
    public ArabicAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionSet) {
        super(stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
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
        result = (TokenStream)new StopFilter(result, this.stopwords);
        result = (TokenStream)new ArabicNormalizationFilter(result);
        if (!this.stemExclusionSet.isEmpty()) {
            result = (TokenStream)new SetKeywordMarkerFilter(result, this.stemExclusionSet);
        }
        return new Analyzer.TokenStreamComponents(source, (TokenStream)new ArabicStemFilter(result));
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        
        static {
            try {
                DEFAULT_STOP_SET = StopwordAnalyzerBase.loadStopwordSet(false, ArabicAnalyzer.class, "stopwords.txt", "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
