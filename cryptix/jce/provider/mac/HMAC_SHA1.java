package cryptix.jce.provider.mac;

public final class HMAC_SHA1 extends HMAC
{
    private static final int BLOCK_SIZE = 64;
    private static final int DIGEST_LEN = 20;
    
    public HMAC_SHA1() {
        super("SHA", 64, 20);
    }
}
