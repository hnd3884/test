package cryptix.jce.provider.key;

public final class TwofishKeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return size == 128 || size == 192 || size == 256;
    }
    
    public TwofishKeyGenerator() {
        super("Twofish", 256);
    }
}
