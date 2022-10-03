package cryptix.jce.provider.key;

public class DESKeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return this.isWeak(key, 0);
    }
    
    private boolean isWeak(final byte[] key, final int offset) {
        final int a = (key[offset] & 0xFE) << 8 | (key[offset + 1] & 0xFE);
        final int b = (key[offset + 2] & 0xFE) << 8 | (key[offset + 3] & 0xFE);
        final int c = (key[offset + 4] & 0xFE) << 8 | (key[offset + 5] & 0xFE);
        final int d = (key[offset + 6] & 0xFE) << 8 | (key[offset + 7] & 0xFE);
        return (a == 0 || a == 65278) && (b == 0 || b == 65278) && (c == 0 || c == 65278) && (d == 0 || d == 65278);
    }
    
    protected boolean isValidSize(final int size) {
        return size == 56;
    }
    
    protected int strengthToBits(final int strength) {
        if (strength != 56) {
            throw new RuntimeException("Invalid strength value");
        }
        return 64;
    }
    
    protected byte[] fixUp(final byte[] key) {
        for (int i = 0; i < key.length; ++i) {
            final int b = key[i];
            key[i] = (byte)((b & 0xFE) | ((b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7) & 0x1));
        }
        return key;
    }
    
    public DESKeyGenerator() {
        super("DES", 56);
    }
}
