package cryptix.jce.provider.mac;

public final class HMAC_MD5 extends HMAC
{
    private static final int BLOCK_SIZE = 64;
    private static final int DIGEST_LEN = 16;
    
    public HMAC_MD5() {
        super("MD5", 64, 16);
    }
}
