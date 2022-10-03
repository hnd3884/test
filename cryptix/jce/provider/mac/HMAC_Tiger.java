package cryptix.jce.provider.mac;

public final class HMAC_Tiger extends HMAC
{
    private static final int BLOCK_SIZE = 64;
    private static final int DIGEST_LEN = 24;
    
    public HMAC_Tiger() {
        super("Tiger", 64, 24);
    }
}
