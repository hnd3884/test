package org.bouncycastle.asn1;

import java.io.IOException;

public class DERNull extends ASN1Null
{
    public static final DERNull INSTANCE;
    private static final byte[] zeroBytes;
    
    @Deprecated
    public DERNull() {
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() {
        return 2;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.writeEncoded(5, DERNull.zeroBytes);
    }
    
    static {
        INSTANCE = new DERNull();
        zeroBytes = new byte[0];
    }
}
