package cryptix.jce.provider.key;

public class SKIPJACKKeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return size == 80;
    }
    
    public SKIPJACKKeyGenerator() {
        super("SKIPJACK", 80);
    }
}
