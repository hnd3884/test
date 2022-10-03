package org.apache.lucene.analysis.core;

import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import java.io.Reader;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class StopAnalyzer extends StopwordAnalyzerBase
{
    public static final CharArraySet ENGLISH_STOP_WORDS_SET;
    
    public StopAnalyzer() {
        this(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }
    
    public StopAnalyzer(final CharArraySet stopWords) {
        super(stopWords);
    }
    
    public StopAnalyzer(final Path stopwordsFile) throws IOException {
        this(StopwordAnalyzerBase.loadStopwordSet(stopwordsFile));
    }
    
    public StopAnalyzer(final Reader stopwords) throws IOException {
        this(StopwordAnalyzerBase.loadStopwordSet(stopwords));
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        final Tokenizer source = new LowerCaseTokenizer();
        return new Analyzer.TokenStreamComponents(source, (TokenStream)new StopFilter((TokenStream)source, this.stopwords));
    }
    
    static {
        final List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with");
        final CharArraySet stopSet = new CharArraySet(stopWords, false);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
    }
}
