package sun.text;

import java.text.CharacterIterator;

public abstract class CodePointIterator
{
    public static final int DONE = -1;
    
    public abstract void setToStart();
    
    public abstract void setToLimit();
    
    public abstract int next();
    
    public abstract int prev();
    
    public abstract int charIndex();
    
    public static CodePointIterator create(final char[] array) {
        return new CharArrayCodePointIterator(array);
    }
    
    public static CodePointIterator create(final char[] array, final int n, final int n2) {
        return new CharArrayCodePointIterator(array, n, n2);
    }
    
    public static CodePointIterator create(final CharSequence charSequence) {
        return new CharSequenceCodePointIterator(charSequence);
    }
    
    public static CodePointIterator create(final CharacterIterator characterIterator) {
        return new CharacterIteratorCodePointIterator(characterIterator);
    }
}
