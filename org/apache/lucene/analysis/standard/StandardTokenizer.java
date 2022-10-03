package org.apache.lucene.analysis.standard;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

public final class StandardTokenizer extends Tokenizer
{
    private StandardTokenizerImpl scanner;
    public static final int ALPHANUM = 0;
    @Deprecated
    public static final int APOSTROPHE = 1;
    @Deprecated
    public static final int ACRONYM = 2;
    @Deprecated
    public static final int COMPANY = 3;
    public static final int EMAIL = 4;
    @Deprecated
    public static final int HOST = 5;
    public static final int NUM = 6;
    @Deprecated
    public static final int CJ = 7;
    @Deprecated
    public static final int ACRONYM_DEP = 8;
    public static final int SOUTHEAST_ASIAN = 9;
    public static final int IDEOGRAPHIC = 10;
    public static final int HIRAGANA = 11;
    public static final int KATAKANA = 12;
    public static final int HANGUL = 13;
    public static final String[] TOKEN_TYPES;
    public static final int MAX_TOKEN_LENGTH_LIMIT = 1048576;
    private int skippedPositions;
    private int maxTokenLength;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final TypeAttribute typeAtt;
    
    public void setMaxTokenLength(final int length) {
        if (length < 1) {
            throw new IllegalArgumentException("maxTokenLength must be greater than zero");
        }
        if (length > 1048576) {
            throw new IllegalArgumentException("maxTokenLength may not exceed 1048576");
        }
        if (length != this.maxTokenLength) {
            this.maxTokenLength = length;
            this.scanner.setBufferSize(length);
        }
    }
    
    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }
    
    public StandardTokenizer() {
        this.maxTokenLength = 255;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.init();
    }
    
    public StandardTokenizer(final AttributeFactory factory) {
        super(factory);
        this.maxTokenLength = 255;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.init();
    }
    
    private void init() {
        this.scanner = new StandardTokenizerImpl(this.input);
    }
    
    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        this.skippedPositions = 0;
        while (true) {
            final int tokenType = this.scanner.getNextToken();
            if (tokenType == -1) {
                return false;
            }
            if (this.scanner.yylength() <= this.maxTokenLength) {
                this.posIncrAtt.setPositionIncrement(this.skippedPositions + 1);
                this.scanner.getText(this.termAtt);
                final int start = this.scanner.yychar();
                this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(start + this.termAtt.length()));
                this.typeAtt.setType(StandardTokenizer.TOKEN_TYPES[tokenType]);
                return true;
            }
            ++this.skippedPositions;
        }
    }
    
    public final void end() throws IOException {
        super.end();
        final int finalOffset = this.correctOffset(this.scanner.yychar() + this.scanner.yylength());
        this.offsetAtt.setOffset(finalOffset, finalOffset);
        this.posIncrAtt.setPositionIncrement(this.posIncrAtt.getPositionIncrement() + this.skippedPositions);
    }
    
    public void close() throws IOException {
        super.close();
        this.scanner.yyreset(this.input);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.scanner.yyreset(this.input);
        this.skippedPositions = 0;
    }
    
    static {
        TOKEN_TYPES = new String[] { "<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>", "<SOUTHEAST_ASIAN>", "<IDEOGRAPHIC>", "<HIRAGANA>", "<KATAKANA>", "<HANGUL>" };
    }
}
