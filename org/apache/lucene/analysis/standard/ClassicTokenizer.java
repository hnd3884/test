package org.apache.lucene.analysis.standard;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

public final class ClassicTokenizer extends Tokenizer
{
    private ClassicTokenizerImpl scanner;
    public static final int ALPHANUM = 0;
    public static final int APOSTROPHE = 1;
    public static final int ACRONYM = 2;
    public static final int COMPANY = 3;
    public static final int EMAIL = 4;
    public static final int HOST = 5;
    public static final int NUM = 6;
    public static final int CJ = 7;
    public static final int ACRONYM_DEP = 8;
    public static final String[] TOKEN_TYPES;
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
        this.maxTokenLength = length;
    }
    
    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }
    
    public ClassicTokenizer() {
        this.maxTokenLength = 255;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.init();
    }
    
    public ClassicTokenizer(final AttributeFactory factory) {
        super(factory);
        this.maxTokenLength = 255;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.typeAtt = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.init();
    }
    
    private void init() {
        this.scanner = new ClassicTokenizerImpl(this.input);
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
                if (tokenType == 8) {
                    this.typeAtt.setType(ClassicTokenizer.TOKEN_TYPES[5]);
                    this.termAtt.setLength(this.termAtt.length() - 1);
                }
                else {
                    this.typeAtt.setType(ClassicTokenizer.TOKEN_TYPES[tokenType]);
                }
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
        TOKEN_TYPES = new String[] { "<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>" };
    }
}
