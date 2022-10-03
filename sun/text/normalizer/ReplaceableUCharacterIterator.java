package sun.text.normalizer;

public class ReplaceableUCharacterIterator extends UCharacterIterator
{
    private Replaceable replaceable;
    private int currentIndex;
    
    public ReplaceableUCharacterIterator(final String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        this.replaceable = new ReplaceableString(s);
        this.currentIndex = 0;
    }
    
    public ReplaceableUCharacterIterator(final StringBuffer sb) {
        if (sb == null) {
            throw new IllegalArgumentException();
        }
        this.replaceable = new ReplaceableString(sb);
        this.currentIndex = 0;
    }
    
    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Override
    public int current() {
        if (this.currentIndex < this.replaceable.length()) {
            return this.replaceable.charAt(this.currentIndex);
        }
        return -1;
    }
    
    @Override
    public int getLength() {
        return this.replaceable.length();
    }
    
    @Override
    public int getIndex() {
        return this.currentIndex;
    }
    
    @Override
    public int next() {
        if (this.currentIndex < this.replaceable.length()) {
            return this.replaceable.charAt(this.currentIndex++);
        }
        return -1;
    }
    
    @Override
    public int previous() {
        if (this.currentIndex > 0) {
            final Replaceable replaceable = this.replaceable;
            final int currentIndex = this.currentIndex - 1;
            this.currentIndex = currentIndex;
            return replaceable.charAt(currentIndex);
        }
        return -1;
    }
    
    @Override
    public void setIndex(final int currentIndex) {
        if (currentIndex < 0 || currentIndex > this.replaceable.length()) {
            throw new IllegalArgumentException();
        }
        this.currentIndex = currentIndex;
    }
    
    @Override
    public int getText(final char[] array, final int n) {
        final int length = this.replaceable.length();
        if (n < 0 || n + length > array.length) {
            throw new IndexOutOfBoundsException(Integer.toString(length));
        }
        this.replaceable.getChars(0, length, array, n);
        return length;
    }
}
