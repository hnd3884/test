package cryptix.jce.provider.key;

public class CAST5KeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return (size & 0x7) == 0x0 && size >= 40 && size <= 128;
    }
    
    public CAST5KeyGenerator() {
        super("CAST5", 128);
    }
}
