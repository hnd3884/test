package org.apache.lucene.analysis.tr;

import java.io.IOException;
import org.apache.lucene.analysis.Tokenizer;
import org.tartarus.snowball.SnowballProgram;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.tartarus.snowball.ext.TurkishStemmer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class TurkishAnalyzer extends StopwordAnalyzerBase
{
    private final CharArraySet stemExclusionSet;
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private static final String STOPWORDS_COMMENT = "#";
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public TurkishAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }
    
    public TurkishAnalyzer(final CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }
    
    public TurkishAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionSet) {
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
        TokenStream result = (TokenStream)new StandardFilter((TokenStream)source);
        if (this.getVersion().onOrAfter(Version.LUCENE_4_8_0)) {
            result = (TokenStream)new ApostropheFilter(result);
        }
        result = (TokenStream)new TurkishLowerCaseFilter(result);
        result = (TokenStream)new StopFilter(result, this.stopwords);
        if (!this.stemExclusionSet.isEmpty()) {
            result = (TokenStream)new SetKeywordMarkerFilter(result, this.stemExclusionSet);
        }
        result = (TokenStream)new SnowballFilter(result, new TurkishStemmer());
        return new Analyzer.TokenStreamComponents(source, result);
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        
        static {
            try {
                DEFAULT_STOP_SET = StopwordAnalyzerBase.loadStopwordSet(false, TurkishAnalyzer.class, "stopwords.txt", "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
