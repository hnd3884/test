package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROctetString;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class PrivateKeyInfo extends ASN1Object
{
    private ASN1OctetString privKey;
    private AlgorithmIdentifier algId;
    private ASN1Set attributes;
    
    public static PrivateKeyInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static PrivateKeyInfo getInstance(final Object o) {
        if (o instanceof PrivateKeyInfo) {
            return (PrivateKeyInfo)o;
        }
        if (o != null) {
            return new PrivateKeyInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public PrivateKeyInfo(final AlgorithmIdentifier algorithmIdentifier, final ASN1Encodable asn1Encodable) throws IOException {
        this(algorithmIdentifier, asn1Encodable, null);
    }
    
    public PrivateKeyInfo(final AlgorithmIdentifier algId, final ASN1Encodable asn1Encodable, final ASN1Set attributes) throws IOException {
        this.privKey = new DEROctetString(asn1Encodable.toASN1Primitive().getEncoded("DER"));
        this.algId = algId;
        this.attributes = attributes;
    }
    
    @Deprecated
    public PrivateKeyInfo(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        if (((ASN1Integer)objects.nextElement()).getValue().intValue() != 0) {
            throw new IllegalArgumentException("wrong version for private key info");
        }
        this.algId = AlgorithmIdentifier.getInstance(objects.nextElement());
        this.privKey = ASN1OctetString.getInstance(objects.nextElement());
        if (objects.hasMoreElements()) {
            this.attributes = ASN1Set.getInstance((ASN1TaggedObject)objects.nextElement(), false);
        }
    }
    
    public AlgorithmIdentifier getPrivateKeyAlgorithm() {
        return this.algId;
    }
    
    @Deprecated
    public AlgorithmIdentifier getAlgorithmId() {
        return this.algId;
    }
    
    public ASN1Encodable parsePrivateKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.privKey.getOctets());
    }
    
    @Deprecated
    public ASN1Primitive getPrivateKey() {
        try {
            return this.parsePrivateKey().toASN1Primitive();
        }
        catch (final IOException ex) {
            throw new IllegalStateException("unable to parse private key");
        }
    }
    
    public ASN1Set getAttributes() {
        return this.attributes;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(0L));
        asn1EncodableVector.add(this.algId);
        asn1EncodableVector.add(this.privKey);
        if (this.attributes != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.attributes));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
