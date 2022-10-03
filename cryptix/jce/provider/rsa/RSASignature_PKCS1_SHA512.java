package cryptix.jce.provider.rsa;

public class RSASignature_PKCS1_SHA512 extends RSASignature_PKCS1
{
    private static final byte[] SHA512_ASN_DATA;
    
    protected byte[] getAlgorithmEncoding() {
        return RSASignature_PKCS1_SHA512.SHA512_ASN_DATA;
    }
    
    public RSASignature_PKCS1_SHA512() {
        super("SHA-512");
    }
    
    static {
        SHA512_ASN_DATA = new byte[] { 48, 81, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 3, 5, 0, 4, 64 };
    }
}
