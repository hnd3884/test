package cryptix.jce.provider.mac;

public final class HMAC_MD2 extends HMAC
{
    private static final int BLOCK_SIZE = 16;
    private static final int DIGEST_LEN = 16;
    
    public HMAC_MD2() {
        super("MD2", 16, 16);
    }
}
