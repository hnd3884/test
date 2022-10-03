package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class PBKDF2Params extends ASN1Object
{
    private static final AlgorithmIdentifier algid_hmacWithSHA1;
    private final ASN1OctetString octStr;
    private final ASN1Integer iterationCount;
    private final ASN1Integer keyLength;
    private final AlgorithmIdentifier prf;
    
    public static PBKDF2Params getInstance(final Object o) {
        if (o instanceof PBKDF2Params) {
            return (PBKDF2Params)o;
        }
        if (o != null) {
            return new PBKDF2Params(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public PBKDF2Params(final byte[] array, final int n) {
        this(array, n, 0);
    }
    
    public PBKDF2Params(final byte[] array, final int n, final int n2) {
        this(array, n, n2, null);
    }
    
    public PBKDF2Params(final byte[] array, final int n, final int n2, final AlgorithmIdentifier prf) {
        this.octStr = new DEROctetString(Arrays.clone(array));
        this.iterationCount = new ASN1Integer(n);
        if (n2 > 0) {
            this.keyLength = new ASN1Integer(n2);
        }
        else {
            this.keyLength = null;
        }
        this.prf = prf;
    }
    
    public PBKDF2Params(final byte[] array, final int n, final AlgorithmIdentifier algorithmIdentifier) {
        this(array, n, 0, algorithmIdentifier);
    }
    
    private PBKDF2Params(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.octStr = (ASN1OctetString)objects.nextElement();
        this.iterationCount = (ASN1Integer)objects.nextElement();
        if (objects.hasMoreElements()) {
            Object o = objects.nextElement();
            if (o instanceof ASN1Integer) {
                this.keyLength = ASN1Integer.getInstance(o);
                if (objects.hasMoreElements()) {
                    o = objects.nextElement();
                }
                else {
                    o = null;
                }
            }
            else {
                this.keyLength = null;
            }
            if (o != null) {
                this.prf = AlgorithmIdentifier.getInstance(o);
            }
            else {
                this.prf = null;
            }
        }
        else {
            this.keyLength = null;
            this.prf = null;
        }
    }
    
    public byte[] getSalt() {
        return this.octStr.getOctets();
    }
    
    public BigInteger getIterationCount() {
        return this.iterationCount.getValue();
    }
    
    public BigInteger getKeyLength() {
        if (this.keyLength != null) {
            return this.keyLength.getValue();
        }
        return null;
    }
    
    public boolean isDefaultPrf() {
        return this.prf == null || this.prf.equals(PBKDF2Params.algid_hmacWithSHA1);
    }
    
    public AlgorithmIdentifier getPrf() {
        if (this.prf != null) {
            return this.prf;
        }
        return PBKDF2Params.algid_hmacWithSHA1;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.octStr);
        asn1EncodableVector.add(this.iterationCount);
        if (this.keyLength != null) {
            asn1EncodableVector.add(this.keyLength);
        }
        if (this.prf != null && !this.prf.equals(PBKDF2Params.algid_hmacWithSHA1)) {
            asn1EncodableVector.add(this.prf);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        algid_hmacWithSHA1 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);
    }
}
