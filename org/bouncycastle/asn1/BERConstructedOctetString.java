package org.bouncycastle.asn1;

import java.util.Enumeration;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class BERConstructedOctetString extends BEROctetString
{
    private static final int MAX_LENGTH = 1000;
    private Vector octs;
    
    private static byte[] toBytes(final Vector vector) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != vector.size(); ++i) {
            try {
                byteArrayOutputStream.write(((DEROctetString)vector.elementAt(i)).getOctets());
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException(vector.elementAt(i).getClass().getName() + " found in input should only contain DEROctetString");
            }
            catch (final IOException ex2) {
                throw new IllegalArgumentException("exception converting octets " + ex2.toString());
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public BERConstructedOctetString(final byte[] array) {
        super(array);
    }
    
    public BERConstructedOctetString(final Vector octs) {
        super(toBytes(octs));
        this.octs = octs;
    }
    
    public BERConstructedOctetString(final ASN1Primitive asn1Primitive) {
        super(toByteArray(asn1Primitive));
    }
    
    private static byte[] toByteArray(final ASN1Primitive asn1Primitive) {
        try {
            return asn1Primitive.getEncoded();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("Unable to encode object");
        }
    }
    
    public BERConstructedOctetString(final ASN1Encodable asn1Encodable) {
        this(asn1Encodable.toASN1Primitive());
    }
    
    @Override
    public byte[] getOctets() {
        return this.string;
    }
    
    @Override
    public Enumeration getObjects() {
        if (this.octs == null) {
            return this.generateOcts().elements();
        }
        return this.octs.elements();
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
    
    public static BEROctetString fromSequence(final ASN1Sequence asn1Sequence) {
        final Vector vector = new Vector();
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            vector.addElement(objects.nextElement());
        }
        return new BERConstructedOctetString(vector);
    }
}
