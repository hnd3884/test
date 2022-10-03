package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.Token;

public class TokenGroup
{
    private static final int MAX_NUM_TOKENS_PER_GROUP = 50;
    private Token[] tokens;
    private float[] scores;
    private int numTokens;
    private int startOffset;
    private int endOffset;
    private float tot;
    private int matchStartOffset;
    private int matchEndOffset;
    private OffsetAttribute offsetAtt;
    private CharTermAttribute termAtt;
    
    public TokenGroup(final TokenStream tokenStream) {
        this.tokens = new Token[50];
        this.scores = new float[50];
        this.numTokens = 0;
        this.startOffset = 0;
        this.endOffset = 0;
        this.offsetAtt = (OffsetAttribute)tokenStream.addAttribute((Class)OffsetAttribute.class);
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute((Class)CharTermAttribute.class);
    }
    
    void addToken(final float score) {
        if (this.numTokens < 50) {
            final int termStartOffset = this.offsetAtt.startOffset();
            final int termEndOffset = this.offsetAtt.endOffset();
            if (this.numTokens == 0) {
                final int n = termStartOffset;
                this.matchStartOffset = n;
                this.startOffset = n;
                final int n2 = termEndOffset;
                this.matchEndOffset = n2;
                this.endOffset = n2;
                this.tot += score;
            }
            else {
                this.startOffset = Math.min(this.startOffset, termStartOffset);
                this.endOffset = Math.max(this.endOffset, termEndOffset);
                if (score > 0.0f) {
                    if (this.tot == 0.0f) {
                        this.matchStartOffset = termStartOffset;
                        this.matchEndOffset = termEndOffset;
                    }
                    else {
                        this.matchStartOffset = Math.min(this.matchStartOffset, termStartOffset);
                        this.matchEndOffset = Math.max(this.matchEndOffset, termEndOffset);
                    }
                    this.tot += score;
                }
            }
            final Token token = new Token();
            token.setOffset(termStartOffset, termEndOffset);
            token.setEmpty().append(this.termAtt);
            this.tokens[this.numTokens] = token;
            this.scores[this.numTokens] = score;
            ++this.numTokens;
        }
    }
    
    boolean isDistinct() {
        return this.offsetAtt.startOffset() >= this.endOffset;
    }
    
    void clear() {
        this.numTokens = 0;
        this.tot = 0.0f;
    }
    
    public Token getToken(final int index) {
        return this.tokens[index];
    }
    
    public float getScore(final int index) {
        return this.scores[index];
    }
    
    public int getStartOffset() {
        return this.matchStartOffset;
    }
    
    public int getEndOffset() {
        return this.matchEndOffset;
    }
    
    public int getNumTokens() {
        return this.numTokens;
    }
    
    public float getTotalScore() {
        return this.tot;
    }
}
