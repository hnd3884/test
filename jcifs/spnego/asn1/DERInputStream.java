package jcifs.spnego.asn1;

import java.io.EOFException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class DERInputStream extends FilterInputStream implements DERTags
{
    public DERInputStream(final InputStream is) {
        super(is);
    }
    
    protected int readLength() throws IOException {
        int length = this.read();
        if (length < 0) {
            throw new IOException("EOF found when length expected");
        }
        if (length == 128) {
            return -1;
        }
        if (length > 127) {
            final int size = length & 0x7F;
            length = 0;
            for (int i = 0; i < size; ++i) {
                final int next = this.read();
                if (next < 0) {
                    throw new IOException("EOF found reading length");
                }
                length = (length << 8) + next;
            }
        }
        return length;
    }
    
    protected void readFully(final byte[] bytes) throws IOException {
        int left = bytes.length;
        if (left == 0) {
            return;
        }
        while ((left -= this.read(bytes, bytes.length - left, left)) != 0) {}
    }
    
    protected DERObject buildObject(final int tag, final byte[] bytes) throws IOException {
        switch (tag) {
            case 5: {
                return null;
            }
            case 48: {
                final ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
                final BERInputStream dIn = new BERInputStream(bIn);
                final DERConstructedSequence seq = new DERConstructedSequence();
                try {
                    while (true) {
                        final DERObject obj = dIn.readObject();
                        seq.addObject(obj);
                    }
                }
                catch (final EOFException ex) {
                    return seq;
                }
            }
            case 49: {
                final ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
                final BERInputStream dIn = new BERInputStream(bIn);
                final ASN1EncodableVector v = new ASN1EncodableVector();
                try {
                    while (true) {
                        final DERObject obj2 = dIn.readObject();
                        v.add(obj2);
                    }
                }
                catch (final EOFException ex2) {
                    return new DERConstructedSet(v);
                }
            }
            case 1: {
                return new DERBoolean(bytes);
            }
            case 2: {
                return new DERInteger(bytes);
            }
            case 10: {
                return new DEREnumerated(bytes);
            }
            case 6: {
                return new DERObjectIdentifier(bytes);
            }
            case 3: {
                final int padBits = bytes[0];
                final byte[] data = new byte[bytes.length - 1];
                System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
                return new DERBitString(data, padBits);
            }
            case 12: {
                return new DERUTF8String(bytes);
            }
            case 19: {
                return new DERPrintableString(bytes);
            }
            case 22: {
                return new DERIA5String(bytes);
            }
            case 20: {
                return new DERT61String(bytes);
            }
            case 26: {
                return new DERVisibleString(bytes);
            }
            case 28: {
                return new DERUniversalString(bytes);
            }
            case 30: {
                return new DERBMPString(bytes);
            }
            case 4: {
                return new DEROctetString(bytes);
            }
            case 23: {
                return new DERUTCTime(bytes);
            }
            case 24: {
                return new DERGeneralizedTime(bytes);
            }
            default: {
                if ((tag & 0x80) != 0x0) {
                    if ((tag & 0x1F) == 0x1F) {
                        throw new IOException("unsupported high tag encountered");
                    }
                    if (bytes.length == 0) {
                        if ((tag & 0x20) == 0x0) {
                            return new DERTaggedObject(false, tag & 0x1F, new DERNull());
                        }
                        return new DERTaggedObject(false, tag & 0x1F, new DERConstructedSequence());
                    }
                    else {
                        if ((tag & 0x20) == 0x0) {
                            return new DERTaggedObject(false, tag & 0x1F, new DEROctetString(bytes));
                        }
                        final ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
                        final BERInputStream dIn = new BERInputStream(bIn);
                        DEREncodable dObj = dIn.readObject();
                        if (dIn.available() == 0) {
                            return new DERTaggedObject(tag & 0x1F, dObj);
                        }
                        final DERConstructedSequence seq = new DERConstructedSequence();
                        seq.addObject(dObj);
                        try {
                            while (true) {
                                dObj = dIn.readObject();
                                seq.addObject(dObj);
                            }
                        }
                        catch (final EOFException ex3) {
                            return new DERTaggedObject(false, tag & 0x1F, seq);
                        }
                    }
                }
                return new DERUnknownTag(tag, bytes);
            }
        }
    }
    
    public DERObject readObject() throws IOException {
        final int tag = this.read();
        if (tag == -1) {
            throw new EOFException();
        }
        final int length = this.readLength();
        final byte[] bytes = new byte[length];
        this.readFully(bytes);
        return this.buildObject(tag, bytes);
    }
}
