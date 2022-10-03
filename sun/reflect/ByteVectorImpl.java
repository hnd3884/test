package sun.reflect;

class ByteVectorImpl implements ByteVector
{
    private byte[] data;
    private int pos;
    
    public ByteVectorImpl() {
        this(100);
    }
    
    public ByteVectorImpl(final int n) {
        this.data = new byte[n];
        this.pos = -1;
    }
    
    @Override
    public int getLength() {
        return this.pos + 1;
    }
    
    @Override
    public byte get(final int pos) {
        if (pos >= this.data.length) {
            this.resize(pos);
            this.pos = pos;
        }
        return this.data[pos];
    }
    
    @Override
    public void put(final int pos, final byte b) {
        if (pos >= this.data.length) {
            this.resize(pos);
            this.pos = pos;
        }
        this.data[pos] = b;
    }
    
    @Override
    public void add(final byte b) {
        if (++this.pos >= this.data.length) {
            this.resize(this.pos);
        }
        this.data[this.pos] = b;
    }
    
    @Override
    public void trim() {
        if (this.pos != this.data.length - 1) {
            final byte[] data = new byte[this.pos + 1];
            System.arraycopy(this.data, 0, data, 0, this.pos + 1);
            this.data = data;
        }
    }
    
    @Override
    public byte[] getData() {
        return this.data;
    }
    
    private void resize(int n) {
        if (n <= 2 * this.data.length) {
            n = 2 * this.data.length;
        }
        final byte[] data = new byte[n];
        System.arraycopy(this.data, 0, data, 0, this.data.length);
        this.data = data;
    }
}
