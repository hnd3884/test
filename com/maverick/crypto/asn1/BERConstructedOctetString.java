package com.maverick.crypto.asn1;

import java.util.Enumeration;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class BERConstructedOctetString extends DEROctetString
{
    private Vector fc;
    
    private static byte[] b(final Vector vector) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != vector.size(); ++i) {
            final DEROctetString derOctetString = vector.elementAt(i);
            try {
                byteArrayOutputStream.write(derOctetString.getOctets());
            }
            catch (final IOException ex) {
                throw new RuntimeException("exception converting octets " + ex.toString());
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public BERConstructedOctetString(final byte[] array) {
        super(array);
    }
    
    public BERConstructedOctetString(final Vector fc) {
        super(b(fc));
        this.fc = fc;
    }
    
    public BERConstructedOctetString(final DERObject derObject) {
        super(derObject);
    }
    
    public BERConstructedOctetString(final DEREncodable derEncodable) {
        super(derEncodable.getDERObject());
    }
    
    public byte[] getOctets() {
        return super.ec;
    }
    
    public Enumeration getObjects() {
        if (this.fc == null) {
            return this.g().elements();
        }
        return this.fc.elements();
    }
    
    private Vector g() {
        int n = 0;
        int n2 = 0;
        final Vector vector = new Vector();
        while (n2 + 1 < super.ec.length) {
            if (super.ec[n2] == 0 && super.ec[n2 + 1] == 0) {
                final byte[] array = new byte[n2 - n + 1];
                System.arraycopy(super.ec, n, array, 0, array.length);
                vector.addElement(new DEROctetString(array));
                n = n2 + 1;
            }
            ++n2;
        }
        final byte[] array2 = new byte[super.ec.length - n];
        System.arraycopy(super.ec, n, array2, 0, array2.length);
        vector.addElement(new DEROctetString(array2));
        return vector;
    }
    
    public void encode(final DEROutputStream derOutputStream) throws IOException {
        if (derOutputStream instanceof ASN1OutputStream || derOutputStream instanceof BEROutputStream) {
            derOutputStream.write(36);
            derOutputStream.write(128);
            if (this.fc != null) {
                for (int i = 0; i != this.fc.size(); ++i) {
                    derOutputStream.writeObject(this.fc.elementAt(i));
                }
            }
            else {
                int n = 0;
                for (int n2 = 0; n2 + 1 < super.ec.length; ++n2) {
                    if (super.ec[n2] == 0 && super.ec[n2 + 1] == 0) {
                        final byte[] array = new byte[n2 - n + 1];
                        System.arraycopy(super.ec, n, array, 0, array.length);
                        derOutputStream.writeObject(new DEROctetString(array));
                        n = n2 + 1;
                    }
                }
                final byte[] array2 = new byte[super.ec.length - n];
                System.arraycopy(super.ec, n, array2, 0, array2.length);
                derOutputStream.writeObject(new DEROctetString(array2));
            }
            derOutputStream.write(0);
            derOutputStream.write(0);
        }
        else {
            super.encode(derOutputStream);
        }
    }
}
