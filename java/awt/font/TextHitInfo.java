package java.awt.font;

public final class TextHitInfo
{
    private int charIndex;
    private boolean isLeadingEdge;
    
    private TextHitInfo(final int charIndex, final boolean isLeadingEdge) {
        this.charIndex = charIndex;
        this.isLeadingEdge = isLeadingEdge;
    }
    
    public int getCharIndex() {
        return this.charIndex;
    }
    
    public boolean isLeadingEdge() {
        return this.isLeadingEdge;
    }
    
    public int getInsertionIndex() {
        return this.isLeadingEdge ? this.charIndex : (this.charIndex + 1);
    }
    
    @Override
    public int hashCode() {
        return this.charIndex;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof TextHitInfo && this.equals((TextHitInfo)o);
    }
    
    public boolean equals(final TextHitInfo textHitInfo) {
        return textHitInfo != null && this.charIndex == textHitInfo.charIndex && this.isLeadingEdge == textHitInfo.isLeadingEdge;
    }
    
    @Override
    public String toString() {
        return "TextHitInfo[" + this.charIndex + (this.isLeadingEdge ? "L" : "T") + "]";
    }
    
    public static TextHitInfo leading(final int n) {
        return new TextHitInfo(n, true);
    }
    
    public static TextHitInfo trailing(final int n) {
        return new TextHitInfo(n, false);
    }
    
    public static TextHitInfo beforeOffset(final int n) {
        return new TextHitInfo(n - 1, false);
    }
    
    public static TextHitInfo afterOffset(final int n) {
        return new TextHitInfo(n, true);
    }
    
    public TextHitInfo getOtherHit() {
        if (this.isLeadingEdge) {
            return trailing(this.charIndex - 1);
        }
        return leading(this.charIndex + 1);
    }
    
    public TextHitInfo getOffsetHit(final int n) {
        return new TextHitInfo(this.charIndex + n, this.isLeadingEdge);
    }
}
