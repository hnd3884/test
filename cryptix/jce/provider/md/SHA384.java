package cryptix.jce.provider.md;

public final class SHA384 extends SHA512Base implements Cloneable
{
    private static final int HASH_SIZE = 48;
    
    public Object clone() {
        return new SHA384(this);
    }
    
    protected void loadInitialValues(final long[] context) {
        context[0] = -3766243637369397544L;
        context[1] = 7105036623409894663L;
        context[2] = -7973340178411365097L;
        context[3] = 1526699215303891257L;
        context[4] = 7436329637833083697L;
        context[5] = -8163818279084223215L;
        context[6] = -2662702644619276377L;
        context[7] = 5167115440072839076L;
    }
    
    protected void generateDigest(final long[] context, final byte[] buf, final int off) {
        for (int i = 0; i < context.length - 2; ++i) {
            for (int j = 0; j < 8; ++j) {
                buf[off + (i * 8 + (7 - j))] = (byte)(context[i] >>> 8 * j);
            }
        }
    }
    
    public SHA384() {
        super(48);
    }
    
    private SHA384(final SHA384 src) {
        super(src);
    }
}
