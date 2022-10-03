package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DERExternal extends ASN1Primitive
{
    private ASN1ObjectIdentifier directReference;
    private ASN1Integer indirectReference;
    private ASN1Primitive dataValueDescriptor;
    private int encoding;
    private ASN1Primitive externalContent;
    
    public DERExternal(final ASN1EncodableVector asn1EncodableVector) {
        int n = 0;
        ASN1Primitive dataValueDescriptor = this.getObjFromVector(asn1EncodableVector, n);
        if (dataValueDescriptor instanceof ASN1ObjectIdentifier) {
            this.directReference = (ASN1ObjectIdentifier)dataValueDescriptor;
            ++n;
            dataValueDescriptor = this.getObjFromVector(asn1EncodableVector, n);
        }
        if (dataValueDescriptor instanceof ASN1Integer) {
            this.indirectReference = (ASN1Integer)dataValueDescriptor;
            ++n;
            dataValueDescriptor = this.getObjFromVector(asn1EncodableVector, n);
        }
        if (!(dataValueDescriptor instanceof ASN1TaggedObject)) {
            this.dataValueDescriptor = dataValueDescriptor;
            ++n;
            dataValueDescriptor = this.getObjFromVector(asn1EncodableVector, n);
        }
        if (asn1EncodableVector.size() != n + 1) {
            throw new IllegalArgumentException("input vector too large");
        }
        if (!(dataValueDescriptor instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("No tagged object found in vector. Structure doesn't seem to be of type External");
        }
        final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)dataValueDescriptor;
        this.setEncoding(asn1TaggedObject.getTagNo());
        this.externalContent = asn1TaggedObject.getObject();
    }
    
    private ASN1Primitive getObjFromVector(final ASN1EncodableVector asn1EncodableVector, final int n) {
        if (asn1EncodableVector.size() <= n) {
            throw new IllegalArgumentException("too few objects in input vector");
        }
        return asn1EncodableVector.get(n).toASN1Primitive();
    }
    
    public DERExternal(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Integer asn1Integer, final ASN1Primitive asn1Primitive, final DERTaggedObject derTaggedObject) {
        this(asn1ObjectIdentifier, asn1Integer, asn1Primitive, derTaggedObject.getTagNo(), derTaggedObject.toASN1Primitive());
    }
    
    public DERExternal(final ASN1ObjectIdentifier directReference, final ASN1Integer indirectReference, final ASN1Primitive dataValueDescriptor, final int encoding, final ASN1Primitive asn1Primitive) {
        this.setDirectReference(directReference);
        this.setIndirectReference(indirectReference);
        this.setDataValueDescriptor(dataValueDescriptor);
        this.setEncoding(encoding);
        this.setExternalContent(asn1Primitive.toASN1Primitive());
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.directReference != null) {
            hashCode = this.directReference.hashCode();
        }
        if (this.indirectReference != null) {
            hashCode ^= this.indirectReference.hashCode();
        }
        if (this.dataValueDescriptor != null) {
            hashCode ^= this.dataValueDescriptor.hashCode();
        }
        return hashCode ^ this.externalContent.hashCode();
    }
    
    @Override
    boolean isConstructed() {
        return true;
    }
    
    @Override
    int encodedLength() throws IOException {
        return this.getEncoded().length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (this.directReference != null) {
            byteArrayOutputStream.write(this.directReference.getEncoded("DER"));
        }
        if (this.indirectReference != null) {
            byteArrayOutputStream.write(this.indirectReference.getEncoded("DER"));
        }
        if (this.dataValueDescriptor != null) {
            byteArrayOutputStream.write(this.dataValueDescriptor.getEncoded("DER"));
        }
        byteArrayOutputStream.write(new DERTaggedObject(true, this.encoding, this.externalContent).getEncoded("DER"));
        asn1OutputStream.writeEncoded(32, 8, byteArrayOutputStream.toByteArray());
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        if (!(asn1Primitive instanceof DERExternal)) {
            return false;
        }
        if (this == asn1Primitive) {
            return true;
        }
        final DERExternal derExternal = (DERExternal)asn1Primitive;
        return (this.directReference == null || (derExternal.directReference != null && derExternal.directReference.equals(this.directReference))) && (this.indirectReference == null || (derExternal.indirectReference != null && derExternal.indirectReference.equals(this.indirectReference))) && (this.dataValueDescriptor == null || (derExternal.dataValueDescriptor != null && derExternal.dataValueDescriptor.equals(this.dataValueDescriptor))) && this.externalContent.equals(derExternal.externalContent);
    }
    
    public ASN1Primitive getDataValueDescriptor() {
        return this.dataValueDescriptor;
    }
    
    public ASN1ObjectIdentifier getDirectReference() {
        return this.directReference;
    }
    
    public int getEncoding() {
        return this.encoding;
    }
    
    public ASN1Primitive getExternalContent() {
        return this.externalContent;
    }
    
    public ASN1Integer getIndirectReference() {
        return this.indirectReference;
    }
    
    private void setDataValueDescriptor(final ASN1Primitive dataValueDescriptor) {
        this.dataValueDescriptor = dataValueDescriptor;
    }
    
    private void setDirectReference(final ASN1ObjectIdentifier directReference) {
        this.directReference = directReference;
    }
    
    private void setEncoding(final int encoding) {
        if (encoding < 0 || encoding > 2) {
            throw new IllegalArgumentException("invalid encoding value: " + encoding);
        }
        this.encoding = encoding;
    }
    
    private void setExternalContent(final ASN1Primitive externalContent) {
        this.externalContent = externalContent;
    }
    
    private void setIndirectReference(final ASN1Integer indirectReference) {
        this.indirectReference = indirectReference;
    }
}
