package cryptix.jce.provider.md;

public final class SHA1 extends SHA implements Cloneable
{
    public Object clone() {
        return new SHA1(this);
    }
    
    protected void expand(final int[] W) {
        for (int i = 16; i < 80; ++i) {
            final int j = W[i - 16] ^ W[i - 14] ^ W[i - 8] ^ W[i - 3];
            W[i] = (j << 1 | j >>> -1);
        }
    }
    
    public SHA1() {
    }
    
    private SHA1(final SHA1 src) {
        super(src);
    }
}
