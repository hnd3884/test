package cryptix.jce.provider.key;

public class RC4KeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return key.length < 2 || (key[0] + key[1]) % 256 == 0;
    }
    
    protected boolean isValidSize(final int size) {
        return size >= 40 && size <= 1024 && size % 8 == 0;
    }
    
    public RC4KeyGenerator() {
        super("RC4", 128);
    }
}
