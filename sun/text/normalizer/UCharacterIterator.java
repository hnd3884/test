package sun.text.normalizer;

import java.text.CharacterIterator;

public abstract class UCharacterIterator implements Cloneable
{
    public static final int DONE = -1;
    
    protected UCharacterIterator() {
    }
    
    public static final UCharacterIterator getInstance(final String s) {
        return new ReplaceableUCharacterIterator(s);
    }
    
    public static final UCharacterIterator getInstance(final StringBuffer sb) {
        return new ReplaceableUCharacterIterator(sb);
    }
    
    public static final UCharacterIterator getInstance(final CharacterIterator characterIterator) {
        return new CharacterIteratorWrapper(characterIterator);
    }
    
    public abstract int current();
    
    public abstract int getLength();
    
    public abstract int getIndex();
    
    public abstract int next();
    
    public int nextCodePoint() {
        final int next = this.next();
        if (UTF16.isLeadSurrogate((char)next)) {
            final int next2 = this.next();
            if (UTF16.isTrailSurrogate((char)next2)) {
                return UCharacterProperty.getRawSupplementary((char)next, (char)next2);
            }
            if (next2 != -1) {
                this.previous();
            }
        }
        return next;
    }
    
    public abstract int previous();
    
    public abstract void setIndex(final int p0);
    
    public abstract int getText(final char[] p0, final int p1);
    
    public final int getText(final char[] array) {
        return this.getText(array, 0);
    }
    
    public String getText() {
        final char[] array = new char[this.getLength()];
        this.getText(array);
        return new String(array);
    }
    
    public int moveIndex(final int n) {
        final int max = Math.max(0, Math.min(this.getIndex() + n, this.getLength()));
        this.setIndex(max);
        return max;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
