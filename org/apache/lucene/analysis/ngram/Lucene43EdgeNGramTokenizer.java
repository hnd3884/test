package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

@Deprecated
public final class Lucene43EdgeNGramTokenizer extends Tokenizer
{
    public static final Side DEFAULT_SIDE;
    public static final int DEFAULT_MAX_GRAM_SIZE = 1;
    public static final int DEFAULT_MIN_GRAM_SIZE = 1;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private int minGram;
    private int maxGram;
    private int gramSize;
    private Side side;
    private boolean started;
    private int inLen;
    private int charsRead;
    private String inStr;
    
    public Lucene43EdgeNGramTokenizer(final Side side, final int minGram, final int maxGram) {
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.init(side, minGram, maxGram);
    }
    
    public Lucene43EdgeNGramTokenizer(final AttributeFactory factory, final Side side, final int minGram, final int maxGram) {
        super(factory);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.init(side, minGram, maxGram);
    }
    
    public Lucene43EdgeNGramTokenizer(final AttributeFactory factory, final String sideLabel, final int minGram, final int maxGram) {
        this(factory, Side.getSide(sideLabel), minGram, maxGram);
    }
    
    public Lucene43EdgeNGramTokenizer(final int minGram, final int maxGram) {
        this(Side.FRONT, minGram, maxGram);
    }
    
    @Deprecated
    public Lucene43EdgeNGramTokenizer(final String sideLabel, final int minGram, final int maxGram) {
        this(Side.getSide(sideLabel), minGram, maxGram);
    }
    
    public Lucene43EdgeNGramTokenizer(final AttributeFactory factory, final int minGram, final int maxGram) {
        this(factory, Side.FRONT, minGram, maxGram);
    }
    
    private void init(final Side side, final int minGram, int maxGram) {
        if (side == null) {
            throw new IllegalArgumentException("sideLabel must be either front or back");
        }
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        maxGram = Math.min(maxGram, 1024);
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.side = side;
    }
    
    public boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (!this.started) {
            this.started = true;
            this.gramSize = this.minGram;
            final int limit = (this.side == Side.FRONT) ? this.maxGram : 1024;
            char[] chars = new char[Math.min(1024, limit)];
            this.charsRead = 0;
            boolean exhausted = false;
            while (this.charsRead < limit) {
                final int inc = this.input.read(chars, this.charsRead, chars.length - this.charsRead);
                if (inc == -1) {
                    exhausted = true;
                    break;
                }
                this.charsRead += inc;
                if (this.charsRead != chars.length || this.charsRead >= limit) {
                    continue;
                }
                chars = ArrayUtil.grow(chars);
            }
            this.inStr = new String(chars, 0, this.charsRead);
            this.inStr = this.inStr.trim();
            if (!exhausted) {
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
            this.posIncrAtt.setPositionIncrement(1);
        }
        else {
            this.posIncrAtt.setPositionIncrement(0);
        }
        if (this.gramSize > this.inLen) {
            return false;
        }
        if (this.gramSize > this.maxGram || this.gramSize > this.inLen) {
            return false;
        }
        final int start = (this.side == Side.FRONT) ? 0 : (this.inLen - this.gramSize);
        final int end = start + this.gramSize;
        this.termAtt.setEmpty().append((CharSequence)this.inStr, start, end);
        this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(end));
        ++this.gramSize;
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
    }
    
    static {
        DEFAULT_SIDE = Side.FRONT;
    }
    
    public enum Side
    {
        FRONT {
            @Override
            public String getLabel() {
                return "front";
            }
        }, 
        BACK {
            @Override
            public String getLabel() {
                return "back";
            }
        };
        
        public abstract String getLabel();
        
        public static Side getSide(final String sideName) {
            if (Side.FRONT.getLabel().equals(sideName)) {
                return Side.FRONT;
            }
            if (Side.BACK.getLabel().equals(sideName)) {
                return Side.BACK;
            }
            return null;
        }
    }
}
