package cryptix.jce.provider.key;

public class SquareKeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return false;
    }
    
    protected boolean isValidSize(final int size) {
        return size == 128;
    }
    
    public SquareKeyGenerator() {
        super("Square", 128);
    }
}
