package cryptix.jce.provider.rsa;

public class RSASignature_PKCS1_RIPEMD160 extends RSASignature_PKCS1
{
    private static final byte[] RIPEMD160_ASN_DATA;
    
    protected byte[] getAlgorithmEncoding() {
        return RSASignature_PKCS1_RIPEMD160.RIPEMD160_ASN_DATA;
    }
    
    public RSASignature_PKCS1_RIPEMD160() {
        super("RIPEMD160");
    }
    
    static {
        RIPEMD160_ASN_DATA = new byte[] { 48, 33, 48, 9, 6, 5, 43, 36, 3, 2, 1, 5, 0, 4, 20 };
    }
}
