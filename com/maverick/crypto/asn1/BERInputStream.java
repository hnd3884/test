package com.maverick.crypto.asn1;

import java.io.EOFException;
import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BERInputStream extends DERInputStream
{
    private DERObject eb;
    
    public BERInputStream(final InputStream inputStream) {
        super(inputStream);
        this.eb = new DERObject() {
            void encode(final DEROutputStream derOutputStream) throws IOException {
                throw new IOException("Eeek!");
            }
        };
    }
    
    private byte[] e() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read2;
        for (int read = this.read(); (read2 = this.read()) >= 0 && (read != 0 || read2 != 0); read = read2) {
            byteArrayOutputStream.write(read);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private BERConstructedOctetString d() throws IOException {
        final Vector vector = new Vector();
        while (true) {
            final DERObject object = this.readObject();
            if (object == this.eb) {
                break;
            }
            vector.addElement(object);
        }
        return new BERConstructedOctetString(vector);
    }
    
    public DERObject readObject() throws IOException {
        final int read = this.read();
        if (read == -1) {
            throw new EOFException();
        }
        final int length = this.readLength();
        if (length < 0) {
            switch (read) {
                case 5: {
                    return null;
                }
                case 48: {
                    final BERConstructedSequence berConstructedSequence = new BERConstructedSequence();
                    while (true) {
                        final DERObject object = this.readObject();
                        if (object == this.eb) {
                            break;
                        }
                        berConstructedSequence.addObject(object);
                    }
                    return berConstructedSequence;
                }
                case 36: {
                    return this.d();
                }
                case 49: {
                    final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                    while (true) {
                        final DERObject object2 = this.readObject();
                        if (object2 == this.eb) {
                            break;
                        }
                        asn1EncodableVector.add(object2);
                    }
                    return new BERSet(asn1EncodableVector);
                }
                default: {
                    if ((read & 0x80) == 0x0) {
                        throw new IOException("unknown BER object encountered");
                    }
                    if ((read & 0x1F) == 0x1F) {
                        throw new IOException("unsupported high tag encountered");
                    }
                    if ((read & 0x20) == 0x0) {
                        return new BERTaggedObject(false, read & 0x1F, new DEROctetString(this.e()));
                    }
                    final DERObject object3 = this.readObject();
                    if (object3 == this.eb) {
                        return new DERTaggedObject(read & 0x1F);
                    }
                    DERObject derObject = this.readObject();
                    if (derObject == this.eb) {
                        return new BERTaggedObject(read & 0x1F, object3);
                    }
                    final BERConstructedSequence berConstructedSequence2 = new BERConstructedSequence();
                    berConstructedSequence2.addObject(object3);
                    do {
                        berConstructedSequence2.addObject(derObject);
                        derObject = this.readObject();
                    } while (derObject != this.eb);
                    return new BERTaggedObject(false, read & 0x1F, berConstructedSequence2);
                }
            }
        }
        else {
            if (read == 0 && length == 0) {
                return this.eb;
            }
            final byte[] array = new byte[length];
            this.readFully(array);
            return this.buildObject(read, array);
        }
    }
}
