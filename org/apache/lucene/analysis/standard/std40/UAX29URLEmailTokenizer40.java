package org.apache.lucene.analysis.standard.std40;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

@Deprecated
public final class UAX29URLEmailTokenizer40 extends Tokenizer
{
    private final UAX29URLEmailTokenizerImpl40 scanner;
    public static final int ALPHANUM = 0;
    public static final int NUM = 1;
    public static final int SOUTHEAST_ASIAN = 2;
    public static final int IDEOGRAPHIC = 3;
    public static final int HIRAGANA = 4;
    public static final int KATAKANA = 5;
    public static final int HANGUL = 6;
    public static final int URL = 7;
    public static final int EMAIL = 8;
    public static final String[] TOKEN_TYPES;
    private int maxTokenLength;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final TypeAttribute typeAtt;
    
    public void setMaxTokenLength(final int length) {
        this.maxTokenLength = length;
    }
    
    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }
    
    public UAX29URLEmailTokenizer40() {
        this.maxTokenLength = 255;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.scanner = new UAX29URLEmailTokenizerImpl40(this.input);
    }
    
    public UAX29URLEmailTokenizer40(final AttributeFactory factory) {
        super(factory);
        this.maxTokenLength = 255;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.scanner = new UAX29URLEmailTokenizerImpl40(this.input);
    }
    
    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        int posIncr = 1;
        while (true) {
            final int tokenType = this.scanner.getNextToken();
            if (tokenType == -1) {
                return false;
            }
            if (this.scanner.yylength() <= this.maxTokenLength) {
                this.posIncrAtt.setPositionIncrement(posIncr);
                this.scanner.getText(this.termAtt);
                final int start = this.scanner.yychar();
                this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(start + this.termAtt.length()));
                this.typeAtt.setType(UAX29URLEmailTokenizer40.TOKEN_TYPES[tokenType]);
                return true;
            }
            ++posIncr;
        }
    }
    
    public final void end() throws IOException {
        super.end();
        final int finalOffset = this.correctOffset(this.scanner.yychar() + this.scanner.yylength());
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }
    
    public void close() throws IOException {
        super.close();
        this.scanner.yyreset(this.input);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.scanner.yyreset(this.input);
    }
    
    static {
        TOKEN_TYPES = new String[] { StandardTokenizer40.TOKEN_TYPES[0], StandardTokenizer40.TOKEN_TYPES[6], StandardTokenizer40.TOKEN_TYPES[9], StandardTokenizer40.TOKEN_TYPES[10], StandardTokenizer40.TOKEN_TYPES[11], StandardTokenizer40.TOKEN_TYPES[12], StandardTokenizer40.TOKEN_TYPES[13], "<URL>", "<EMAIL>" };
    }
}
