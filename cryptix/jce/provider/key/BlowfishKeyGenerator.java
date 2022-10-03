package cryptix.jce.provider.key;

public class BlowfishKeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return (size & 0x7) == 0x0 && size >= 40 && size <= 448;
    }
    
    public BlowfishKeyGenerator() {
        super("Blowfish", 128);
    }
}
