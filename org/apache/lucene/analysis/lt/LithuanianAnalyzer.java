package org.apache.lucene.analysis.lt;

import java.io.IOException;
import org.apache.lucene.analysis.Tokenizer;
import org.tartarus.snowball.SnowballProgram;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.tartarus.snowball.ext.LithuanianStemmer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.Analyzer;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class LithuanianAnalyzer extends StopwordAnalyzerBase
{
    private final CharArraySet stemExclusionSet;
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public LithuanianAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }
    
    public LithuanianAnalyzer(final CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }
    
    public LithuanianAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionSet) {
        super(stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = (TokenStream)new StandardFilter((TokenStream)source);
        result = (TokenStream)new LowerCaseFilter(result);
        result = (TokenStream)new StopFilter(result, this.stopwords);
        if (!this.stemExclusionSet.isEmpty()) {
            result = (TokenStream)new SetKeywordMarkerFilter(result, this.stemExclusionSet);
        }
        result = (TokenStream)new SnowballFilter(result, new LithuanianStemmer());
        return new Analyzer.TokenStreamComponents(source, result);
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        
        static {
            try {
                DEFAULT_STOP_SET = StopwordAnalyzerBase.loadStopwordSet(false, LithuanianAnalyzer.class, "stopwords.txt", "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
