package org.apache.lucene.analysis.cz;

import java.io.IOException;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
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

public final class CzechAnalyzer extends StopwordAnalyzerBase
{
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private final CharArraySet stemExclusionTable;
    
    public static final CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_SET;
    }
    
    public CzechAnalyzer() {
        this(DefaultSetHolder.DEFAULT_SET);
    }
    
    public CzechAnalyzer(final CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }
    
    public CzechAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionTable) {
        super(stopwords);
        this.stemExclusionTable = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionTable));
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
        if (!this.stemExclusionTable.isEmpty()) {
            result = (TokenStream)new SetKeywordMarkerFilter(result, this.stemExclusionTable);
        }
        result = (TokenStream)new CzechStemFilter(result);
        return new Analyzer.TokenStreamComponents(source, result);
    }
    
    private static class DefaultSetHolder
    {
        private static final CharArraySet DEFAULT_SET;
        
        static {
            try {
                DEFAULT_SET = WordlistLoader.getWordSet(IOUtils.getDecodingReader((Class)CzechAnalyzer.class, "stopwords.txt", StandardCharsets.UTF_8), "#");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
