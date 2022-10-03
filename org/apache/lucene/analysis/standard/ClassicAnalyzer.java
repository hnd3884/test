package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class ClassicAnalyzer extends StopwordAnalyzerBase
{
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength;
    public static final CharArraySet STOP_WORDS_SET;
    
    public ClassicAnalyzer(final CharArraySet stopWords) {
        super(stopWords);
        this.maxTokenLength = 255;
    }
    
    public ClassicAnalyzer() {
        this(ClassicAnalyzer.STOP_WORDS_SET);
    }
    
    public ClassicAnalyzer(final Reader stopwords) throws IOException {
        this(StopwordAnalyzerBase.loadStopwordSet(stopwords));
    }
    
    public void setMaxTokenLength(final int length) {
        this.maxTokenLength = length;
    }
    
    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        final ClassicTokenizer src = new ClassicTokenizer();
        src.setMaxTokenLength(this.maxTokenLength);
        TokenStream tok = (TokenStream)new ClassicFilter((TokenStream)src);
        tok = (TokenStream)new LowerCaseFilter(tok);
        tok = (TokenStream)new StopFilter(tok, this.stopwords);
        return new Analyzer.TokenStreamComponents(src, tok) {
            protected void setReader(final Reader reader) {
                src.setMaxTokenLength(ClassicAnalyzer.this.maxTokenLength);
                super.setReader(reader);
            }
        };
    }
    
    static {
        STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
}
