package cryptix.jce.provider.key;

public class TripleDESKeyGenerator extends RawKeyGenerator
{
    private static final int STRENGTH = 168;
    private static final int BIT_LEN = 192;
    
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return size == 168;
    }
    
    protected int strengthToBits(final int strength) {
        if (strength != 168) {
            throw new RuntimeException("Invalid strength value (" + strength + ")");
        }
        return 192;
    }
    
    protected byte[] fixUp(final byte[] key) {
        for (int i = 0; i < key.length; ++i) {
            final int b = key[i];
            key[i] = (byte)((b & 0xFE) | ((b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7) & 0x1));
        }
        return key;
    }
    
    public TripleDESKeyGenerator() {
        super("TripleDES", 168);
    }
}
