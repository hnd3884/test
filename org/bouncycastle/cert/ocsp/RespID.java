package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class RespID
{
    public static final AlgorithmIdentifier HASH_SHA1;
    ResponderID id;
    
    public RespID(final ResponderID id) {
        this.id = id;
    }
    
    public RespID(final X500Name x500Name) {
        this.id = new ResponderID(x500Name);
    }
    
    public RespID(final SubjectPublicKeyInfo subjectPublicKeyInfo, final DigestCalculator digestCalculator) throws OCSPException {
        try {
            if (!digestCalculator.getAlgorithmIdentifier().equals((Object)RespID.HASH_SHA1)) {
                throw new IllegalArgumentException("only SHA-1 can be used with RespID - found: " + digestCalculator.getAlgorithmIdentifier().getAlgorithm());
            }
            final OutputStream outputStream = digestCalculator.getOutputStream();
            outputStream.write(subjectPublicKeyInfo.getPublicKeyData().getBytes());
            outputStream.close();
            this.id = new ResponderID((ASN1OctetString)new DEROctetString(digestCalculator.getDigest()));
        }
        catch (final Exception ex) {
            throw new OCSPException("problem creating ID: " + ex, ex);
        }
    }
    
    public ResponderID toASN1Primitive() {
        return this.id;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof RespID && this.id.equals((Object)((RespID)o).id);
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    static {
        HASH_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
    }
}
