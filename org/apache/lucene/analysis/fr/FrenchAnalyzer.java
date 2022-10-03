package org.apache.lucene.analysis.fr;

import java.io.IOException;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import java.util.Collection;
import java.util.Arrays;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class FrenchAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "french_stop.txt";
    public static final CharArraySet DEFAULT_ARTICLES;
    private final CharArraySet excltable;
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public FrenchAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }
    
    public FrenchAnalyzer(final CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }
    
    public FrenchAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclutionSet) {
        super(stopwords);
        this.excltable = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclutionSet));
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
        result = (TokenStream)new ElisionFilter(result, FrenchAnalyzer.DEFAULT_ARTICLES);
        result = (TokenStream)new LowerCaseFilter(result);
        result = (TokenStream)new StopFilter(result, this.stopwords);
        if (!this.excltable.isEmpty()) {
            result = (TokenStream)new SetKeywordMarkerFilter(result, this.excltable);
        }
        result = (TokenStream)new FrenchLightStemFilter(result);
        return new Analyzer.TokenStreamComponents(source, result);
    }
    
    static {
        DEFAULT_ARTICLES = CharArraySet.unmodifiableSet(new CharArraySet(Arrays.asList("l", "m", "t", "qu", "n", "s", "j", "d", "c", "jusqu", "quoiqu", "lorsqu", "puisqu"), true));
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        
        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader((Class)SnowballFilter.class, "french_stop.txt", StandardCharsets.UTF_8));
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
