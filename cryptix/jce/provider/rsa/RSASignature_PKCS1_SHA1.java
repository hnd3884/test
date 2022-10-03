package cryptix.jce.provider.rsa;

public class RSASignature_PKCS1_SHA1 extends RSASignature_PKCS1
{
    private static final byte[] SHA1_ASN_DATA;
    
    protected byte[] getAlgorithmEncoding() {
        return RSASignature_PKCS1_SHA1.SHA1_ASN_DATA;
    }
    
    public RSASignature_PKCS1_SHA1() {
        super("SHA-1");
    }
    
    static {
        SHA1_ASN_DATA = new byte[] { 48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20 };
    }
}
