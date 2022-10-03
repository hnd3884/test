package org.bouncycastle.jcajce.spec;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.spec.PBEKeySpec;

public class PBKDF2KeySpec extends PBEKeySpec
{
    private static final AlgorithmIdentifier defaultPRF;
    private AlgorithmIdentifier prf;
    
    public PBKDF2KeySpec(final char[] array, final byte[] array2, final int n, final int n2, final AlgorithmIdentifier prf) {
        super(array, array2, n, n2);
        this.prf = prf;
    }
    
    public boolean isDefaultPrf() {
        return PBKDF2KeySpec.defaultPRF.equals(this.prf);
    }
    
    public AlgorithmIdentifier getPrf() {
        return this.prf;
    }
    
    static {
        defaultPRF = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);
    }
}
