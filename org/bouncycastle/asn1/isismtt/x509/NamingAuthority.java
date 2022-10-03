package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class NamingAuthority extends ASN1Object
{
    public static final ASN1ObjectIdentifier id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern;
    private ASN1ObjectIdentifier namingAuthorityId;
    private String namingAuthorityUrl;
    private DirectoryString namingAuthorityText;
    
    public static NamingAuthority getInstance(final Object o) {
        if (o == null || o instanceof NamingAuthority) {
            return (NamingAuthority)o;
        }
        if (o instanceof ASN1Sequence) {
            return new NamingAuthority((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static NamingAuthority getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    private NamingAuthority(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        if (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable = objects.nextElement();
            if (asn1Encodable instanceof ASN1ObjectIdentifier) {
                this.namingAuthorityId = (ASN1ObjectIdentifier)asn1Encodable;
            }
            else if (asn1Encodable instanceof DERIA5String) {
                this.namingAuthorityUrl = DERIA5String.getInstance(asn1Encodable).getString();
            }
            else {
                if (!(asn1Encodable instanceof ASN1String)) {
                    throw new IllegalArgumentException("Bad object encountered: " + ((ASN1ObjectIdentifier)asn1Encodable).getClass());
                }
                this.namingAuthorityText = DirectoryString.getInstance(asn1Encodable);
            }
        }
        if (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable2 = objects.nextElement();
            if (asn1Encodable2 instanceof DERIA5String) {
                this.namingAuthorityUrl = DERIA5String.getInstance(asn1Encodable2).getString();
            }
            else {
                if (!(asn1Encodable2 instanceof ASN1String)) {
                    throw new IllegalArgumentException("Bad object encountered: " + asn1Encodable2.getClass());
                }
                this.namingAuthorityText = DirectoryString.getInstance(asn1Encodable2);
            }
        }
        if (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable3 = objects.nextElement();
            if (!(asn1Encodable3 instanceof ASN1String)) {
                throw new IllegalArgumentException("Bad object encountered: " + asn1Encodable3.getClass());
            }
            this.namingAuthorityText = DirectoryString.getInstance(asn1Encodable3);
        }
    }
    
    public ASN1ObjectIdentifier getNamingAuthorityId() {
        return this.namingAuthorityId;
    }
    
    public DirectoryString getNamingAuthorityText() {
        return this.namingAuthorityText;
    }
    
    public String getNamingAuthorityUrl() {
        return this.namingAuthorityUrl;
    }
    
    public NamingAuthority(final ASN1ObjectIdentifier namingAuthorityId, final String namingAuthorityUrl, final DirectoryString namingAuthorityText) {
        this.namingAuthorityId = namingAuthorityId;
        this.namingAuthorityUrl = namingAuthorityUrl;
        this.namingAuthorityText = namingAuthorityText;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.namingAuthorityId != null) {
            asn1EncodableVector.add(this.namingAuthorityId);
        }
        if (this.namingAuthorityUrl != null) {
            asn1EncodableVector.add(new DERIA5String(this.namingAuthorityUrl, true));
        }
        if (this.namingAuthorityText != null) {
            asn1EncodableVector.add(this.namingAuthorityText);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern = new ASN1ObjectIdentifier(ISISMTTObjectIdentifiers.id_isismtt_at_namingAuthorities + ".1");
    }
}
