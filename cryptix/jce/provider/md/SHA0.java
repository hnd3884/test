package cryptix.jce.provider.md;

public final class SHA0 extends SHA implements Cloneable
{
    public Object clone() {
        return new SHA0(this);
    }
    
    protected void expand(final int[] W) {
        for (int i = 16; i < 80; ++i) {
            W[i] = (W[i - 16] ^ W[i - 14] ^ W[i - 8] ^ W[i - 3]);
        }
    }
    
    public SHA0() {
    }
    
    private SHA0(final SHA0 src) {
        super(src);
    }
}
