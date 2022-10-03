package cryptix.jce.provider.key;

public class IDEAKeyGenerator extends RawKeyGenerator
{
    protected boolean isWeak(final byte[] key) {
        return key[0] == 0 && key[1] == 0 && key[2] == 0 && (key[3] & 0xC0) == 0x0 && (key[5] & 0x7F) == 0x0 && key[6] == 0 && key[7] == 0 && key[8] == 0 && (key[10] & 0xF) == 0x0 && key[11] == 0 && (key[12] & 0xE0) == 0x0 && (((key[3] & 0x7) == 0x0 && key[4] == 0 && key[5] == 0 && (key[9] & 0x1F) == 0x0 && key[10] == 0 && key[12] == 0 && (key[13] & 0xFE) == 0x0) || (key[15] & 0x1F) == 0x0);
    }
    
    protected boolean isValidSize(final int size) {
        return size == 128;
    }
    
    public IDEAKeyGenerator() {
        super("IDEA", 128);
    }
}
