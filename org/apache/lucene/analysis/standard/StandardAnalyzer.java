package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class StandardAnalyzer extends StopwordAnalyzerBase
{
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength;
    public static final CharArraySet STOP_WORDS_SET;
    
    public StandardAnalyzer(final CharArraySet stopWords) {
        super(stopWords);
        this.maxTokenLength = 255;
    }
    
    public StandardAnalyzer() {
        this(StandardAnalyzer.STOP_WORDS_SET);
    }
    
    public StandardAnalyzer(final Reader stopwords) throws IOException {
        this(StopwordAnalyzerBase.loadStopwordSet(stopwords));
    }
    
    public void setMaxTokenLength(final int length) {
        this.maxTokenLength = length;
    }
    
    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        Tokenizer src;
        if (this.getVersion().onOrAfter(Version.LUCENE_4_7_0)) {
            final StandardTokenizer t = new StandardTokenizer();
            t.setMaxTokenLength(this.maxTokenLength);
            src = t;
        }
        else {
            final StandardTokenizer40 t2 = new StandardTokenizer40();
            t2.setMaxTokenLength(this.maxTokenLength);
            src = t2;
        }
        TokenStream tok = (TokenStream)new StandardFilter((TokenStream)src);
        tok = (TokenStream)new LowerCaseFilter(tok);
        tok = (TokenStream)new StopFilter(tok, this.stopwords);
        return new Analyzer.TokenStreamComponents(src, tok) {
            protected void setReader(final Reader reader) {
                final int m = StandardAnalyzer.this.maxTokenLength;
                if (src instanceof StandardTokenizer) {
                    ((StandardTokenizer)src).setMaxTokenLength(m);
                }
                else {
                    ((StandardTokenizer40)src).setMaxTokenLength(m);
                }
                super.setReader(reader);
            }
        };
    }
    
    static {
        STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
}
