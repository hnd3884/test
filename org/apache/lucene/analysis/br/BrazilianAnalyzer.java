package org.apache.lucene.analysis.br;

import java.io.IOException;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class BrazilianAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private CharArraySet excltable;
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public BrazilianAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }
    
    public BrazilianAnalyzer(final CharArraySet stopwords) {
        super(stopwords);
        this.excltable = CharArraySet.EMPTY_SET;
    }
    
    public BrazilianAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionSet) {
        this(stopwords);
        this.excltable = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
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
        result = (TokenStream)new StandardFilter(result);
        result = (TokenStream)new StopFilter(result, this.stopwords);
        if (this.excltable != null && !this.excltable.isEmpty()) {
            result = (TokenStream)new SetKeywordMarkerFilter(result, this.excltable);
        }
        return new Analyzer.TokenStreamComponents(source, (TokenStream)new BrazilianStemFilter(result));
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        
        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getWordSet(IOUtils.getDecodingReader((Class)BrazilianAnalyzer.class, "stopwords.txt", StandardCharsets.UTF_8), "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
