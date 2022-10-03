package cryptix.jce.provider.key;

public class RC2KeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return size == 128;
    }
    
    public RC2KeyGenerator() {
        super("RC2", 128);
    }
}
