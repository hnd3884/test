package com.maverick.crypto.asn1;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class DERInputStream extends FilterInputStream implements DERTags
{
    public DERInputStream(final InputStream inputStream) {
        super(inputStream);
    }
    
    protected int readLength() throws IOException {
        int read = this.read();
        if (read < 0) {
            throw new IOException("EOF found when length expected");
        }
        if (read == 128) {
            return -1;
        }
        if (read > 127) {
            final int n = read & 0x7F;
            read = 0;
            for (int i = 0; i < n; ++i) {
                final int read2 = this.read();
                if (read2 < 0) {
                    throw new IOException("EOF found reading length");
                }
                read = (read << 8) + read2;
            }
        }
        return read;
    }
    
    protected void readFully(final byte[] array) throws IOException {
        int i = array.length;
        if (i == 0) {
            return;
        }
        while (i > 0) {
            final int read = this.read(array, array.length - i, i);
            if (read < 0) {
                throw new EOFException("unexpected end of stream");
            }
            i -= read;
        }
    }
    
    protected DERObject buildObject(final int n, final byte[] array) throws IOException {
        switch (n) {
            case 5: {
                return null;
            }
            case 48: {
                final BERInputStream berInputStream = new BERInputStream(new ByteArrayInputStream(array));
                final DERConstructedSequence derConstructedSequence = new DERConstructedSequence();
                try {
                    while (true) {
                        derConstructedSequence.addObject(berInputStream.readObject());
                    }
                }
                catch (final EOFException ex) {
                    return derConstructedSequence;
                }
            }
            case 49: {
                final BERInputStream berInputStream2 = new BERInputStream(new ByteArrayInputStream(array));
                final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                try {
                    while (true) {
                        asn1EncodableVector.add(berInputStream2.readObject());
                    }
                }
                catch (final EOFException ex2) {
                    return new DERConstructedSet(asn1EncodableVector);
                }
            }
            case 1: {
                return new DERBoolean(array);
            }
            case 2: {
                return new DERInteger(array);
            }
            case 10: {
                return new DEREnumerated(array);
            }
            case 6: {
                return new DERObjectIdentifier(array);
            }
            case 3: {
                final byte b = array[0];
                final byte[] array2 = new byte[array.length - 1];
                System.arraycopy(array, 1, array2, 0, array.length - 1);
                return new DERBitString(array2, b);
            }
            case 12: {
                return new DERUTF8String(array);
            }
            case 19: {
                return new DERPrintableString(array);
            }
            case 22: {
                return new DERIA5String(array);
            }
            case 20: {
                return new DERT61String(array);
            }
            case 26: {
                return new DERVisibleString(array);
            }
            case 28: {
                return new DERUniversalString(array);
            }
            case 27: {
                return new DERGeneralString(array);
            }
            case 30: {
                return new DERBMPString(array);
            }
            case 4: {
                return new DEROctetString(array);
            }
            case 23: {
                return new DERUTCTime(array);
            }
            case 24: {
                return new DERGeneralizedTime(array);
            }
            default: {
                if ((n & 0x80) != 0x0) {
                    if ((n & 0x1F) == 0x1F) {
                        throw new IOException("unsupported high tag encountered");
                    }
                    if (array.length == 0) {
                        if ((n & 0x20) == 0x0) {
                            return new DERTaggedObject(false, n & 0x1F, new DERNull());
                        }
                        return new DERTaggedObject(false, n & 0x1F, new DERConstructedSequence());
                    }
                    else {
                        if ((n & 0x20) == 0x0) {
                            return new DERTaggedObject(false, n & 0x1F, new DEROctetString(array));
                        }
                        final BERInputStream berInputStream3 = new BERInputStream(new ByteArrayInputStream(array));
                        final DERObject object = berInputStream3.readObject();
                        if (berInputStream3.available() == 0) {
                            return new DERTaggedObject(n & 0x1F, object);
                        }
                        final DERConstructedSequence derConstructedSequence2 = new DERConstructedSequence();
                        derConstructedSequence2.addObject(object);
                        try {
                            while (true) {
                                derConstructedSequence2.addObject(berInputStream3.readObject());
                            }
                        }
                        catch (final EOFException ex3) {
                            return new DERTaggedObject(false, n & 0x1F, derConstructedSequence2);
                        }
                    }
                }
                return new DERUnknownTag(n, array);
            }
        }
    }
    
    public DERObject readObject() throws IOException {
        final int read = this.read();
        if (read == -1) {
            throw new EOFException();
        }
        final byte[] array = new byte[this.readLength()];
        this.readFully(array);
        return this.buildObject(read, array);
    }
}
