package org.bouncycastle.asn1;

import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class BEROctetString extends ASN1OctetString
{
    private static final int MAX_LENGTH = 1000;
    private ASN1OctetString[] octs;
    
    private static byte[] toBytes(final ASN1OctetString[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != array.length; ++i) {
            try {
                byteArrayOutputStream.write(array[i].getOctets());
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException(array[i].getClass().getName() + " found in input should only contain DEROctetString");
            }
            catch (final IOException ex2) {
                throw new IllegalArgumentException("exception converting octets " + ex2.toString());
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public BEROctetString(final byte[] array) {
        super(array);
    }
    
    public BEROctetString(final ASN1OctetString[] octs) {
        super(toBytes(octs));
        this.octs = octs;
    }
    
    @Override
    public byte[] getOctets() {
        return this.string;
    }
    
    public Enumeration getObjects() {
        if (this.octs == null) {
            return this.generateOcts().elements();
        }
        return new Enumeration() {
            int counter = 0;
            
            public boolean hasMoreElements() {
                return this.counter < BEROctetString.this.octs.length;
            }
            
            public Object nextElement() {
                return BEROctetString.this.octs[this.counter++];
            }
        };
    }
    
    private Vector generateOcts() {
        final Vector vector = new Vector();
        for (int i = 0; i < this.string.length; i += 1000) {
            int length;
            if (i + 1000 > this.string.length) {
                length = this.string.length;
            }
            else {
                length = i + 1000;
            }
            final byte[] array = new byte[length - i];
            System.arraycopy(this.string, i, array, 0, array.length);
            vector.addElement(new DEROctetString(array));
        }
        return vector;
    }
    
    @Override
    boolean isConstructed() {
        return true;
    }
    
    @Override
    int encodedLength() throws IOException {
        int n = 0;
        final Enumeration objects = this.getObjects();
        while (objects.hasMoreElements()) {
            n += ((ASN1Encodable)objects.nextElement()).toASN1Primitive().encodedLength();
        }
        return 2 + n + 2;
    }
    
    public void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.write(36);
        asn1OutputStream.write(128);
        final Enumeration objects = this.getObjects();
        while (objects.hasMoreElements()) {
            asn1OutputStream.writeObject((ASN1Encodable)objects.nextElement());
        }
        asn1OutputStream.write(0);
        asn1OutputStream.write(0);
    }
    
    static BEROctetString fromSequence(final ASN1Sequence asn1Sequence) {
        final ASN1OctetString[] array = new ASN1OctetString[asn1Sequence.size()];
        final Enumeration objects = asn1Sequence.getObjects();
        int n = 0;
        while (objects.hasMoreElements()) {
            array[n++] = objects.nextElement();
        }
        return new BEROctetString(array);
    }
}
