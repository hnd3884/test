package cryptix.jce.provider.mac;

public final class HMAC_SHA256 extends HMAC
{
    private static final int BLOCK_SIZE = 64;
    private static final int DIGEST_LEN = 32;
    
    public HMAC_SHA256() {
        super("SHA-256", 64, 32);
    }
}
