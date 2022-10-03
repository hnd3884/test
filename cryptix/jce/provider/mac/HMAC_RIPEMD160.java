package cryptix.jce.provider.mac;

public final class HMAC_RIPEMD160 extends HMAC
{
    private static final int BLOCK_SIZE = 64;
    private static final int DIGEST_LEN = 20;
    
    public HMAC_RIPEMD160() {
        super("RIPEMD160", 64, 20);
    }
}
