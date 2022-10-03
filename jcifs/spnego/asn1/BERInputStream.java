package jcifs.spnego.asn1;

import java.io.EOFException;
import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BERInputStream extends DERInputStream
{
    private DERObject END_OF_STREAM;
    
    public BERInputStream(final InputStream is) {
        super(is);
        this.END_OF_STREAM = new DERObject() {
            void encode(final DEROutputStream out) throws IOException {
                throw new IOException("Eeek!");
            }
        };
    }
    
    private byte[] readIndefiniteLengthFully() throws IOException {
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        int b2;
        for (int b1 = this.read(); (b2 = this.read()) >= 0 && (b1 != 0 || b2 != 0); b1 = b2) {
            bOut.write(b1);
        }
        return bOut.toByteArray();
    }
    
    private BERConstructedOctetString buildConstructedOctetString() throws IOException {
        final Vector octs = new Vector();
        while (true) {
            final DERObject o = this.readObject();
            if (o == this.END_OF_STREAM) {
                break;
            }
            octs.addElement(o);
        }
        return new BERConstructedOctetString(octs);
    }
    
    public DERObject readObject() throws IOException {
        final int tag = this.read();
        if (tag == -1) {
            throw new EOFException();
        }
        final int length = this.readLength();
        if (length < 0) {
            switch (tag) {
                case 5: {
                    return null;
                }
                case 48: {
                    final BERConstructedSequence seq = new BERConstructedSequence();
                    while (true) {
                        final DERObject obj = this.readObject();
                        if (obj == this.END_OF_STREAM) {
                            break;
                        }
                        seq.addObject(obj);
                    }
                    return seq;
                }
                case 36: {
                    return this.buildConstructedOctetString();
                }
                case 49: {
                    final ASN1EncodableVector v = new ASN1EncodableVector();
                    while (true) {
                        final DERObject obj2 = this.readObject();
                        if (obj2 == this.END_OF_STREAM) {
                            break;
                        }
                        v.add(obj2);
                    }
                    return new BERSet(v);
                }
                default: {
                    if ((tag & 0x80) == 0x0) {
                        throw new IOException("unknown BER object encountered");
                    }
                    if ((tag & 0x1F) == 0x1F) {
                        throw new IOException("unsupported high tag encountered");
                    }
                    if ((tag & 0x20) == 0x0) {
                        final byte[] bytes = this.readIndefiniteLengthFully();
                        return new BERTaggedObject(false, tag & 0x1F, new DEROctetString(bytes));
                    }
                    final DERObject dObj = this.readObject();
                    if (dObj == this.END_OF_STREAM) {
                        return new DERTaggedObject(tag & 0x1F);
                    }
                    DERObject next = this.readObject();
                    if (next == this.END_OF_STREAM) {
                        return new BERTaggedObject(tag & 0x1F, dObj);
                    }
                    final BERConstructedSequence seq = new BERConstructedSequence();
                    seq.addObject(dObj);
                    do {
                        seq.addObject(next);
                        next = this.readObject();
                    } while (next != this.END_OF_STREAM);
                    return new BERTaggedObject(false, tag & 0x1F, seq);
                }
            }
        }
        else {
            if (tag == 0 && length == 0) {
                return this.END_OF_STREAM;
            }
            final byte[] bytes2 = new byte[length];
            this.readFully(bytes2);
            return this.buildObject(tag, bytes2);
        }
    }
}
