package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.crypto.digests.SHA1Digest;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class AuthorityKeyIdentifier extends ASN1Object
{
    ASN1OctetString keyidentifier;
    GeneralNames certissuer;
    ASN1Integer certserno;
    
    public static AuthorityKeyIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static AuthorityKeyIdentifier getInstance(final Object o) {
        if (o instanceof AuthorityKeyIdentifier) {
            return (AuthorityKeyIdentifier)o;
        }
        if (o != null) {
            return new AuthorityKeyIdentifier(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static AuthorityKeyIdentifier fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.authorityKeyIdentifier));
    }
    
    protected AuthorityKeyIdentifier(final ASN1Sequence asn1Sequence) {
        this.keyidentifier = null;
        this.certissuer = null;
        this.certserno = null;
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            switch (instance.getTagNo()) {
                case 0: {
                    this.keyidentifier = ASN1OctetString.getInstance(instance, false);
                    continue;
                }
                case 1: {
                    this.certissuer = GeneralNames.getInstance(instance, false);
                    continue;
                }
                case 2: {
                    this.certserno = ASN1Integer.getInstance(instance, false);
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("illegal tag");
                }
            }
        }
    }
    
    @Deprecated
    public AuthorityKeyIdentifier(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.keyidentifier = null;
        this.certissuer = null;
        this.certserno = null;
        final SHA1Digest sha1Digest = new SHA1Digest();
        final byte[] array = new byte[sha1Digest.getDigestSize()];
        final byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        sha1Digest.update(bytes, 0, bytes.length);
        sha1Digest.doFinal(array, 0);
        this.keyidentifier = new DEROctetString(array);
    }
    
    @Deprecated
    public AuthorityKeyIdentifier(final SubjectPublicKeyInfo subjectPublicKeyInfo, final GeneralNames generalNames, final BigInteger bigInteger) {
        this.keyidentifier = null;
        this.certissuer = null;
        this.certserno = null;
        final SHA1Digest sha1Digest = new SHA1Digest();
        final byte[] array = new byte[sha1Digest.getDigestSize()];
        final byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        sha1Digest.update(bytes, 0, bytes.length);
        sha1Digest.doFinal(array, 0);
        this.keyidentifier = new DEROctetString(array);
        this.certissuer = GeneralNames.getInstance(generalNames.toASN1Primitive());
        this.certserno = new ASN1Integer(bigInteger);
    }
    
    public AuthorityKeyIdentifier(final GeneralNames generalNames, final BigInteger bigInteger) {
        this((byte[])null, generalNames, bigInteger);
    }
    
    public AuthorityKeyIdentifier(final byte[] array) {
        this(array, null, null);
    }
    
    public AuthorityKeyIdentifier(final byte[] array, final GeneralNames certissuer, final BigInteger bigInteger) {
        this.keyidentifier = null;
        this.certissuer = null;
        this.certserno = null;
        this.keyidentifier = ((array != null) ? new DEROctetString(array) : null);
        this.certissuer = certissuer;
        this.certserno = ((bigInteger != null) ? new ASN1Integer(bigInteger) : null);
    }
    
    public byte[] getKeyIdentifier() {
        if (this.keyidentifier != null) {
            return this.keyidentifier.getOctets();
        }
        return null;
    }
    
    public GeneralNames getAuthorityCertIssuer() {
        return this.certissuer;
    }
    
    public BigInteger getAuthorityCertSerialNumber() {
        if (this.certserno != null) {
            return this.certserno.getValue();
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.keyidentifier != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.keyidentifier));
        }
        if (this.certissuer != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.certissuer));
        }
        if (this.certserno != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 2, this.certserno));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        return "AuthorityKeyIdentifier: KeyID(" + this.keyidentifier.getOctets() + ")";
    }
}
