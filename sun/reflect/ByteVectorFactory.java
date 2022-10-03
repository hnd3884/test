package sun.reflect;

class ByteVectorFactory
{
    static ByteVector create() {
        return new ByteVectorImpl();
    }
    
    static ByteVector create(final int n) {
        return new ByteVectorImpl(n);
    }
}
