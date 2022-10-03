package cryptix.jce.provider.rsa;

public class RSASignature_PKCS1_RIPEMD128 extends RSASignature_PKCS1
{
    private static final byte[] RIPEMD128_ASN_DATA;
    
    protected byte[] getAlgorithmEncoding() {
        return RSASignature_PKCS1_RIPEMD128.RIPEMD128_ASN_DATA;
    }
    
    public RSASignature_PKCS1_RIPEMD128() {
        super("RIPEMD128");
    }
    
    static {
        RIPEMD128_ASN_DATA = new byte[] { 48, 33, 48, 9, 6, 5, 43, 36, 3, 2, 2, 5, 0, 4, 20 };
    }
}
