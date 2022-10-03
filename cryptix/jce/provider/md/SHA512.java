package cryptix.jce.provider.md;

public final class SHA512 extends SHA512Base implements Cloneable
{
    private static final int HASH_SIZE = 64;
    
    public Object clone() {
        return new SHA512(this);
    }
    
    protected void loadInitialValues(final long[] context) {
        context[0] = 7640891576956012808L;
        context[1] = -4942790177534073029L;
        context[2] = 4354685564936845355L;
        context[3] = -6534734903238641935L;
        context[4] = 5840696475078001361L;
        context[5] = -7276294671716946913L;
        context[6] = 2270897969802886507L;
        context[7] = 6620516959819538809L;
    }
    
    protected void generateDigest(final long[] context, final byte[] buf, final int off) {
        for (int i = 0; i < context.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                buf[off + (i * 8 + (7 - j))] = (byte)(context[i] >>> 8 * j);
            }
        }
    }
    
    public SHA512() {
        super(64);
    }
    
    private SHA512(final SHA512 src) {
        super(src);
    }
}
