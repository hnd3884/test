package cryptix.jce.provider.mac;

public final class HMAC_RIPEMD128 extends HMAC
{
    private static final int BLOCK_SIZE = 64;
    private static final int DIGEST_LEN = 16;
    
    public HMAC_RIPEMD128() {
        super("RIPEMD128", 64, 16);
    }
}
