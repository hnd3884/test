package javax.imageio.stream;

public class IIOByteBuffer
{
    private byte[] data;
    private int offset;
    private int length;
    
    public IIOByteBuffer(final byte[] data, final int offset, final int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public void setOffset(final int offset) {
        this.offset = offset;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
}
