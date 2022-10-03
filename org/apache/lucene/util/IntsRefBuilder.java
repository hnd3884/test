package org.apache.lucene.util;

public class IntsRefBuilder
{
    private final IntsRef ref;
    
    public IntsRefBuilder() {
        this.ref = new IntsRef();
    }
    
    public int[] ints() {
        return this.ref.ints;
    }
    
    public int length() {
        return this.ref.length;
    }
    
    public void setLength(final int length) {
        this.ref.length = length;
    }
    
    public void clear() {
        this.setLength(0);
    }
    
    public int intAt(final int offset) {
        return this.ref.ints[offset];
    }
    
    public void setIntAt(final int offset, final int b) {
        this.ref.ints[offset] = b;
    }
    
    public void append(final int i) {
        this.grow(this.ref.length + 1);
        this.ref.ints[this.ref.length++] = i;
    }
    
    public void grow(final int newLength) {
        this.ref.ints = ArrayUtil.grow(this.ref.ints, newLength);
    }
    
    public void copyInts(final int[] otherInts, final int otherOffset, final int otherLength) {
        this.grow(otherLength);
        System.arraycopy(otherInts, otherOffset, this.ref.ints, 0, otherLength);
        this.ref.length = otherLength;
    }
    
    public void copyInts(final IntsRef ints) {
        this.copyInts(ints.ints, ints.offset, ints.length);
    }
    
    public void copyUTF8Bytes(final BytesRef bytes) {
        this.grow(bytes.length);
        this.ref.length = UnicodeUtil.UTF8toUTF32(bytes, this.ref.ints);
    }
    
    public IntsRef get() {
        assert this.ref.offset == 0 : "Modifying the offset of the returned ref is illegal";
        return this.ref;
    }
    
    public IntsRef toIntsRef() {
        return IntsRef.deepCopyOf(this.get());
    }
    
    @Override
    public boolean equals(final Object obj) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
