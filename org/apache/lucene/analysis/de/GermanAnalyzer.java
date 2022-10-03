package org.apache.lucene.analysis.de;

import java.io.IOException;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class GermanAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "german_stop.txt";
    private final CharArraySet exclusionSet;
    
    public static final CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_SET;
    }
    
    public GermanAnalyzer() {
        this(DefaultSetHolder.DEFAULT_SET);
    }
    
    public GermanAnalyzer(final CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }
    
    public GermanAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionSet) {
        super(stopwords);
        this.exclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
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
        result = (TokenStream)new LowerCaseFilter(result);
        result = (TokenStream)new StopFilter(result, this.stopwords);
        result = (TokenStream)new SetKeywordMarkerFilter(result, this.exclusionSet);
        result = (TokenStream)new GermanNormalizationFilter(result);
        result = (TokenStream)new GermanLightStemFilter(result);
        return new Analyzer.TokenStreamComponents(source, result);
    }
    
    private static class DefaultSetHolder
    {
        private static final CharArraySet DEFAULT_SET;
        
        static {
            try {
                DEFAULT_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader((Class)SnowballFilter.class, "german_stop.txt", StandardCharsets.UTF_8));
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
