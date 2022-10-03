package org.bouncycastle.asn1;

import java.util.Enumeration;
import java.io.IOException;

class LazyEncodedSequence extends ASN1Sequence
{
    private byte[] encoded;
    
    LazyEncodedSequence(final byte[] encoded) throws IOException {
        this.encoded = encoded;
    }
    
    private void parse() {
        final LazyConstructionEnumeration lazyConstructionEnumeration = new LazyConstructionEnumeration(this.encoded);
        while (lazyConstructionEnumeration.hasMoreElements()) {
            this.seq.addElement(lazyConstructionEnumeration.nextElement());
        }
        this.encoded = null;
    }
    
    @Override
    public synchronized ASN1Encodable getObjectAt(final int n) {
        if (this.encoded != null) {
            this.parse();
        }
        return super.getObjectAt(n);
    }
    
    @Override
    public synchronized Enumeration getObjects() {
        if (this.encoded == null) {
            return super.getObjects();
        }
        return new LazyConstructionEnumeration(this.encoded);
    }
    
    @Override
    public synchronized int size() {
        if (this.encoded != null) {
            this.parse();
        }
        return super.size();
    }
    
    @Override
    ASN1Primitive toDERObject() {
        if (this.encoded != null) {
            this.parse();
        }
        return super.toDERObject();
    }
    
    @Override
    ASN1Primitive toDLObject() {
        if (this.encoded != null) {
            this.parse();
        }
        return super.toDLObject();
    }
    
    @Override
    int encodedLength() throws IOException {
        if (this.encoded != null) {
            return 1 + StreamUtil.calculateBodyLength(this.encoded.length) + this.encoded.length;
        }
        return super.toDLObject().encodedLength();
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        if (this.encoded != null) {
            asn1OutputStream.writeEncoded(48, this.encoded);
        }
        else {
            super.toDLObject().encode(asn1OutputStream);
        }
    }
}
