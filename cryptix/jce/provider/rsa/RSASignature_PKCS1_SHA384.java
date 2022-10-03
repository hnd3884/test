package cryptix.jce.provider.rsa;

public class RSASignature_PKCS1_SHA384 extends RSASignature_PKCS1
{
    private static final byte[] SHA384_ASN_DATA;
    
    protected byte[] getAlgorithmEncoding() {
        return RSASignature_PKCS1_SHA384.SHA384_ASN_DATA;
    }
    
    public RSASignature_PKCS1_SHA384() {
        super("SHA-384");
    }
    
    static {
        SHA384_ASN_DATA = new byte[] { 48, 65, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 2, 5, 0, 4, 48 };
    }
}
