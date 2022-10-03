package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class OtherHash extends ASN1Object implements ASN1Choice
{
    private ASN1OctetString sha1Hash;
    private OtherHashAlgAndValue otherHash;
    
    public static OtherHash getInstance(final Object o) {
        if (o instanceof OtherHash) {
            return (OtherHash)o;
        }
        if (o instanceof ASN1OctetString) {
            return new OtherHash((ASN1OctetString)o);
        }
        return new OtherHash(OtherHashAlgAndValue.getInstance(o));
    }
    
    private OtherHash(final ASN1OctetString sha1Hash) {
        this.sha1Hash = sha1Hash;
    }
    
    public OtherHash(final OtherHashAlgAndValue otherHash) {
        this.otherHash = otherHash;
    }
    
    public OtherHash(final byte[] array) {
        this.sha1Hash = new DEROctetString(array);
    }
    
    public AlgorithmIdentifier getHashAlgorithm() {
        if (null == this.otherHash) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }
        return this.otherHash.getHashAlgorithm();
    }
    
    public byte[] getHashValue() {
        if (null == this.otherHash) {
            return this.sha1Hash.getOctets();
        }
        return this.otherHash.getHashValue().getOctets();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (null == this.otherHash) {
            return this.sha1Hash;
        }
        return this.otherHash.toASN1Primitive();
    }
}
