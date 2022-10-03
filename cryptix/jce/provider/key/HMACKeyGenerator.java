package cryptix.jce.provider.key;

public class HMACKeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return true;
    }
    
    public HMACKeyGenerator() {
        super("HMAC", 128);
    }
}
