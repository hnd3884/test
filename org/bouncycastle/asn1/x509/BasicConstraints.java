package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Object;

public class BasicConstraints extends ASN1Object
{
    ASN1Boolean cA;
    ASN1Integer pathLenConstraint;
    
    public static BasicConstraints getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static BasicConstraints getInstance(final Object o) {
        if (o instanceof BasicConstraints) {
            return (BasicConstraints)o;
        }
        if (o instanceof X509Extension) {
            return getInstance(X509Extension.convertValueToObject((X509Extension)o));
        }
        if (o != null) {
            return new BasicConstraints(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static BasicConstraints fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.basicConstraints));
    }
    
    private BasicConstraints(final ASN1Sequence asn1Sequence) {
        this.cA = ASN1Boolean.getInstance(false);
        this.pathLenConstraint = null;
        if (asn1Sequence.size() == 0) {
            this.cA = null;
            this.pathLenConstraint = null;
        }
        else {
            if (asn1Sequence.getObjectAt(0) instanceof ASN1Boolean) {
                this.cA = ASN1Boolean.getInstance(asn1Sequence.getObjectAt(0));
            }
            else {
                this.cA = null;
                this.pathLenConstraint = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
            }
            if (asn1Sequence.size() > 1) {
                if (this.cA == null) {
                    throw new IllegalArgumentException("wrong sequence in constructor");
                }
                this.pathLenConstraint = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1));
            }
        }
    }
    
    public BasicConstraints(final boolean b) {
        this.cA = ASN1Boolean.getInstance(false);
        this.pathLenConstraint = null;
        if (b) {
            this.cA = ASN1Boolean.getInstance(true);
        }
        else {
            this.cA = null;
        }
        this.pathLenConstraint = null;
    }
    
    public BasicConstraints(final int n) {
        this.cA = ASN1Boolean.getInstance(false);
        this.pathLenConstraint = null;
        this.cA = ASN1Boolean.getInstance(true);
        this.pathLenConstraint = new ASN1Integer(n);
    }
    
    public boolean isCA() {
        return this.cA != null && this.cA.isTrue();
    }
    
    public BigInteger getPathLenConstraint() {
        if (this.pathLenConstraint != null) {
            return this.pathLenConstraint.getValue();
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.cA != null) {
            asn1EncodableVector.add(this.cA);
        }
        if (this.pathLenConstraint != null) {
            asn1EncodableVector.add(this.pathLenConstraint);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        if (this.pathLenConstraint != null) {
            return "BasicConstraints: isCa(" + this.isCA() + "), pathLenConstraint = " + this.pathLenConstraint.getValue();
        }
        if (this.cA == null) {
            return "BasicConstraints: isCa(false)";
        }
        return "BasicConstraints: isCa(" + this.isCA() + ")";
    }
}
