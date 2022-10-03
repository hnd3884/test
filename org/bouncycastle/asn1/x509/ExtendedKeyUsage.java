package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Object;

public class ExtendedKeyUsage extends ASN1Object
{
    Hashtable usageTable;
    ASN1Sequence seq;
    
    public static ExtendedKeyUsage getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static ExtendedKeyUsage getInstance(final Object o) {
        if (o instanceof ExtendedKeyUsage) {
            return (ExtendedKeyUsage)o;
        }
        if (o != null) {
            return new ExtendedKeyUsage(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static ExtendedKeyUsage fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.extendedKeyUsage));
    }
    
    public ExtendedKeyUsage(final KeyPurposeId keyPurposeId) {
        this.usageTable = new Hashtable();
        this.seq = new DERSequence(keyPurposeId);
        this.usageTable.put(keyPurposeId, keyPurposeId);
    }
    
    private ExtendedKeyUsage(final ASN1Sequence seq) {
        this.usageTable = new Hashtable();
        this.seq = seq;
        final Enumeration objects = seq.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable = objects.nextElement();
            if (!(asn1Encodable.toASN1Primitive() instanceof ASN1ObjectIdentifier)) {
                throw new IllegalArgumentException("Only ASN1ObjectIdentifiers allowed in ExtendedKeyUsage.");
            }
            this.usageTable.put(asn1Encodable, asn1Encodable);
        }
    }
    
    public ExtendedKeyUsage(final KeyPurposeId[] array) {
        this.usageTable = new Hashtable();
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            asn1EncodableVector.add(array[i]);
            this.usageTable.put(array[i], array[i]);
        }
        this.seq = new DERSequence(asn1EncodableVector);
    }
    
    @Deprecated
    public ExtendedKeyUsage(final Vector vector) {
        this.usageTable = new Hashtable();
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            final KeyPurposeId instance = KeyPurposeId.getInstance(elements.nextElement());
            asn1EncodableVector.add(instance);
            this.usageTable.put(instance, instance);
        }
        this.seq = new DERSequence(asn1EncodableVector);
    }
    
    public boolean hasKeyPurposeId(final KeyPurposeId keyPurposeId) {
        return this.usageTable.get(keyPurposeId) != null;
    }
    
    public KeyPurposeId[] getUsages() {
        final KeyPurposeId[] array = new KeyPurposeId[this.seq.size()];
        int n = 0;
        final Enumeration objects = this.seq.getObjects();
        while (objects.hasMoreElements()) {
            array[n++] = KeyPurposeId.getInstance(objects.nextElement());
        }
        return array;
    }
    
    public int size() {
        return this.usageTable.size();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
