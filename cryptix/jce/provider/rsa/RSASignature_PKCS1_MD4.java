package cryptix.jce.provider.rsa;

public class RSASignature_PKCS1_MD4 extends RSASignature_PKCS1
{
    private static final byte[] MD4_ASN_DATA;
    
    protected byte[] getAlgorithmEncoding() {
        return RSASignature_PKCS1_MD4.MD4_ASN_DATA;
    }
    
    public RSASignature_PKCS1_MD4() {
        super("MD4");
    }
    
    static {
        MD4_ASN_DATA = new byte[] { 48, 32, 48, 12, 6, 8, 42, -122, 72, -122, -9, 13, 2, 4, 5, 0, 4, 16 };
    }
}
