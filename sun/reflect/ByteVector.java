package sun.reflect;

interface ByteVector
{
    int getLength();
    
    byte get(final int p0);
    
    void put(final int p0, final byte p1);
    
    void add(final byte p0);
    
    void trim();
    
    byte[] getData();
}
