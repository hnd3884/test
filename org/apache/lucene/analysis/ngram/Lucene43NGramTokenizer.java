package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

@Deprecated
public final class Lucene43NGramTokenizer extends Tokenizer
{
    public static final int DEFAULT_MIN_NGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;
    private int minGram;
    private int maxGram;
    private int gramSize;
    private int pos;
    private int inLen;
    private int charsRead;
    private String inStr;
    private boolean started;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    
    public Lucene43NGramTokenizer(final int minGram, final int maxGram) {
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.init(minGram, maxGram);
    }
    
    public Lucene43NGramTokenizer(final AttributeFactory factory, final int minGram, final int maxGram) {
        super(factory);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.init(minGram, maxGram);
    }
    
    public Lucene43NGramTokenizer() {
        this(1, 2);
    }
    
    private void init(final int minGram, final int maxGram) {
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
    }
    
    public boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (!this.started) {
            this.started = true;
            this.gramSize = this.minGram;
            final char[] chars = new char[1024];
            this.charsRead = 0;
            while (this.charsRead < chars.length) {
                final int inc = this.input.read(chars, this.charsRead, chars.length - this.charsRead);
                if (inc == -1) {
                    break;
                }
                this.charsRead += inc;
            }
            this.inStr = new String(chars, 0, this.charsRead).trim();
            if (this.charsRead == chars.length) {
                final char[] throwaway = new char[1024];
                while (true) {
                    final int inc2 = this.input.read(throwaway, 0, throwaway.length);
                    if (inc2 == -1) {
                        break;
                    }
                    this.charsRead += inc2;
                }
            }
            this.inLen = this.inStr.length();
            if (this.inLen == 0) {
                return false;
            }
        }
        if (this.pos + this.gramSize > this.inLen) {
            this.pos = 0;
            ++this.gramSize;
            if (this.gramSize > this.maxGram) {
                return false;
            }
            if (this.pos + this.gramSize > this.inLen) {
                return false;
            }
        }
        final int oldPos = this.pos;
        ++this.pos;
        this.termAtt.setEmpty().append((CharSequence)this.inStr, oldPos, oldPos + this.gramSize);
        this.offsetAtt.setOffset(this.correctOffset(oldPos), this.correctOffset(oldPos + this.gramSize));
        return true;
    }
    
    public void end() throws IOException {
        super.end();
        final int finalOffset = this.correctOffset(this.charsRead);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.started = false;
        this.pos = 0;
    }
}
