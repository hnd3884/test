package cryptix.jce.provider.rsa;

public class RSASignature_PKCS1_SHA256 extends RSASignature_PKCS1
{
    private static final byte[] SHA256_ASN_DATA;
    
    protected byte[] getAlgorithmEncoding() {
        return RSASignature_PKCS1_SHA256.SHA256_ASN_DATA;
    }
    
    public RSASignature_PKCS1_SHA256() {
        super("SHA-256");
    }
    
    static {
        SHA256_ASN_DATA = new byte[] { 48, 49, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 1, 5, 0, 4, 32 };
    }
}
