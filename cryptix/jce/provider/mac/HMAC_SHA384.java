package cryptix.jce.provider.mac;

public final class HMAC_SHA384 extends HMAC
{
    private static final int BLOCK_SIZE = 128;
    private static final int DIGEST_LEN = 48;
    
    public HMAC_SHA384() {
        super("SHA-384", 128, 48);
    }
}
