package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.std40.UAX29URLEmailTokenizer40;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public final class UAX29URLEmailAnalyzer extends StopwordAnalyzerBase
{
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength;
    public static final CharArraySet STOP_WORDS_SET;
    
    public UAX29URLEmailAnalyzer(final CharArraySet stopWords) {
        super(stopWords);
        this.maxTokenLength = 255;
    }
    
    public UAX29URLEmailAnalyzer() {
        this(UAX29URLEmailAnalyzer.STOP_WORDS_SET);
    }
    
    public UAX29URLEmailAnalyzer(final Reader stopwords) throws IOException {
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
            src = new UAX29URLEmailTokenizer();
            ((UAX29URLEmailTokenizer)src).setMaxTokenLength(this.maxTokenLength);
        }
        else {
            src = new UAX29URLEmailTokenizer40();
            ((UAX29URLEmailTokenizer40)src).setMaxTokenLength(this.maxTokenLength);
        }
        TokenStream tok = (TokenStream)new StandardFilter((TokenStream)src);
        tok = (TokenStream)new LowerCaseFilter(tok);
        tok = (TokenStream)new StopFilter(tok, this.stopwords);
        return new Analyzer.TokenStreamComponents(src, tok) {
            protected void setReader(final Reader reader) {
                final int m = UAX29URLEmailAnalyzer.this.maxTokenLength;
                if (src instanceof UAX29URLEmailTokenizer) {
                    ((UAX29URLEmailTokenizer)src).setMaxTokenLength(m);
                }
                else {
                    ((UAX29URLEmailTokenizer40)src).setMaxTokenLength(m);
                }
                super.setReader(reader);
            }
        };
    }
    
    static {
        STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
}
