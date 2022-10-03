package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DEREncodableVector;
import com.maverick.crypto.asn1.DERSequence;
import com.maverick.crypto.asn1.DEREncodable;
import com.maverick.crypto.asn1.ASN1EncodableVector;
import com.maverick.crypto.asn1.DERObject;
import java.math.BigInteger;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.DERInteger;
import com.maverick.crypto.asn1.DERBoolean;
import com.maverick.crypto.asn1.ASN1Encodable;

public class BasicConstraints extends ASN1Encodable
{
    DERBoolean lc;
    DERInteger mc;
    
    public static BasicConstraints getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static BasicConstraints getInstance(final Object o) {
        if (o == null || o instanceof BasicConstraints) {
            return (BasicConstraints)o;
        }
        if (o instanceof ASN1Sequence) {
            return new BasicConstraints((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("unknown object in factory");
    }
    
    public BasicConstraints(final ASN1Sequence asn1Sequence) {
        this.lc = new DERBoolean(false);
        this.mc = null;
        if (asn1Sequence.size() == 0) {
            this.lc = null;
            this.mc = null;
        }
        else {
            this.lc = (DERBoolean)asn1Sequence.getObjectAt(0);
            if (asn1Sequence.size() > 1) {
                this.mc = (DERInteger)asn1Sequence.getObjectAt(1);
            }
        }
    }
    
    public BasicConstraints(final boolean b, final int n) {
        this.lc = new DERBoolean(false);
        this.mc = null;
        if (b) {
            this.lc = new DERBoolean(b);
            this.mc = new DERInteger(n);
        }
        else {
            this.lc = null;
            this.mc = null;
        }
    }
    
    public BasicConstraints(final boolean b) {
        this.lc = new DERBoolean(false);
        this.mc = null;
        if (b) {
            this.lc = new DERBoolean(true);
        }
        else {
            this.lc = null;
        }
        this.mc = null;
    }
    
    public BasicConstraints(final int n) {
        this.lc = new DERBoolean(false);
        this.mc = null;
        this.lc = new DERBoolean(true);
        this.mc = new DERInteger(n);
    }
    
    public boolean isCA() {
        return this.lc != null && this.lc.isTrue();
    }
    
    public BigInteger getPathLenConstraint() {
        if (this.mc != null) {
            return this.mc.getValue();
        }
        return null;
    }
    
    public DERObject toASN1Object() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.lc != null) {
            asn1EncodableVector.add(this.lc);
            if (this.mc != null) {
                asn1EncodableVector.add(this.mc);
            }
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public String toString() {
        if (this.mc != null) {
            return "BasicConstraints: isCa(" + this.isCA() + "), pathLenConstraint = " + this.mc.getValue();
        }
        if (this.lc == null) {
            return "BasicConstraints: isCa(false)";
        }
        return "BasicConstraints: isCa(" + this.isCA() + ")";
    }
}
