package org.apache.lucene.search.highlight;

public class TextFragment
{
    CharSequence markedUpText;
    int fragNum;
    int textStartPos;
    int textEndPos;
    float score;
    
    public TextFragment(final CharSequence markedUpText, final int textStartPos, final int fragNum) {
        this.markedUpText = markedUpText;
        this.textStartPos = textStartPos;
        this.fragNum = fragNum;
    }
    
    void setScore(final float score) {
        this.score = score;
    }
    
    public float getScore() {
        return this.score;
    }
    
    public void merge(final TextFragment frag2) {
        this.textEndPos = frag2.textEndPos;
        this.score = Math.max(this.score, frag2.score);
    }
    
    public boolean follows(final TextFragment fragment) {
        return this.textStartPos == fragment.textEndPos;
    }
    
    public int getFragNum() {
        return this.fragNum;
    }
    
    @Override
    public String toString() {
        return this.markedUpText.subSequence(this.textStartPos, this.textEndPos).toString();
    }
}
