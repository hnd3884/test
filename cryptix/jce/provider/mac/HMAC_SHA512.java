package cryptix.jce.provider.mac;

public final class HMAC_SHA512 extends HMAC
{
    private static final int BLOCK_SIZE = 128;
    private static final int DIGEST_LEN = 64;
    
    public HMAC_SHA512() {
        super("SHA-512", 128, 64);
    }
}
