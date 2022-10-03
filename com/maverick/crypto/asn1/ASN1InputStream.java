package com.maverick.crypto.asn1;

import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ASN1InputStream extends DERInputStream
{
    private DERObject db;
    boolean cb;
    
    public ASN1InputStream(final InputStream inputStream) {
        super(inputStream);
        this.db = new DERObject() {
            void encode(final DEROutputStream derOutputStream) throws IOException {
                throw new IOException("Eeek!");
            }
        };
        this.cb = false;
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
        int length = array.length;
        if (length == 0) {
            return;
        }
        int read;
        while ((read = this.read(array, array.length - length, length)) > 0) {
            if ((length -= read) == 0) {
                return;
            }
        }
        if (length != 0) {
            throw new EOFException("EOF encountered in middle of object");
        }
    }
    
    protected DERObject buildObject(final int n, byte[] array) throws IOException {
        if ((n & 0x40) != 0x0) {
            return new DERApplicationSpecific(n, array);
        }
        switch (n) {
            case 5: {
                return new DERNull();
            }
            case 48: {
                final ASN1InputStream asn1InputStream = new ASN1InputStream(new ByteArrayInputStream(array));
                final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                for (DERObject derObject = asn1InputStream.readObject(); derObject != null; derObject = asn1InputStream.readObject()) {
                    asn1EncodableVector.add(derObject);
                }
                return new DERSequence(asn1EncodableVector);
            }
            case 49: {
                final ASN1InputStream asn1InputStream2 = new ASN1InputStream(new ByteArrayInputStream(array));
                final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
                for (DERObject derObject2 = asn1InputStream2.readObject(); derObject2 != null; derObject2 = asn1InputStream2.readObject()) {
                    asn1EncodableVector2.add(derObject2);
                }
                return new DERSet(asn1EncodableVector2);
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
            case 27: {
                return new DERGeneralString(array);
            }
            case 28: {
                return new DERUniversalString(array);
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
                if ((n & 0x80) == 0x0) {
                    return new DERUnknownTag(n, array);
                }
                int n2 = n & 0x1F;
                if (n2 == 31) {
                    int n3;
                    int n4;
                    for (n3 = 0, n4 = 0; (array[n3] & 0x80) != 0x0; n4 = (n4 | (array[n3++] & 0x7F)) << 7) {}
                    n2 = (n4 | (array[n3] & 0x7F));
                    final byte[] array3 = array;
                    array = new byte[array3.length - (n3 + 1)];
                    System.arraycopy(array3, n3 + 1, array, 0, array.length);
                }
                if (array.length == 0) {
                    if ((n & 0x20) == 0x0) {
                        return new DERTaggedObject(false, n2, new DERNull());
                    }
                    return new DERTaggedObject(false, n2, new DERSequence());
                }
                else {
                    if ((n & 0x20) == 0x0) {
                        return new DERTaggedObject(false, n2, new DEROctetString(array));
                    }
                    final ASN1InputStream asn1InputStream3 = new ASN1InputStream(new ByteArrayInputStream(array));
                    DERObject derObject3 = asn1InputStream3.readObject();
                    if (asn1InputStream3.available() == 0) {
                        return new DERTaggedObject(n2, derObject3);
                    }
                    final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
                    while (derObject3 != null) {
                        asn1EncodableVector3.add(derObject3);
                        derObject3 = asn1InputStream3.readObject();
                    }
                    return new DERTaggedObject(false, n2, new DERSequence(asn1EncodableVector3));
                }
                break;
            }
        }
    }
    
    private byte[] c() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read2;
        for (int read = this.read(); (read2 = this.read()) >= 0 && (read != 0 || read2 != 0); read = read2) {
            byteArrayOutputStream.write(read);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private BERConstructedOctetString b() throws IOException {
        final Vector vector = new Vector();
        while (true) {
            final DERObject object = this.readObject();
            if (object == this.db) {
                break;
            }
            vector.addElement(object);
        }
        return new BERConstructedOctetString(vector);
    }
    
    public DERObject readObject() throws IOException {
        final int read = this.read();
        if (read == -1) {
            if (this.cb) {
                throw new EOFException("attempt to read past end of file.");
            }
            this.cb = true;
            return null;
        }
        else {
            final int length = this.readLength();
            if (length < 0) {
                switch (read) {
                    case 5: {
                        return new BERNull();
                    }
                    case 48: {
                        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                        while (true) {
                            final DERObject object = this.readObject();
                            if (object == this.db) {
                                break;
                            }
                            asn1EncodableVector.add(object);
                        }
                        return new BERSequence(asn1EncodableVector);
                    }
                    case 49: {
                        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
                        while (true) {
                            final DERObject object2 = this.readObject();
                            if (object2 == this.db) {
                                break;
                            }
                            asn1EncodableVector2.add(object2);
                        }
                        return new BERSet(asn1EncodableVector2);
                    }
                    case 36: {
                        return this.b();
                    }
                    default: {
                        if ((read & 0x80) == 0x0) {
                            throw new IOException("unknown BER object encountered");
                        }
                        int n = read & 0x1F;
                        if (n == 31) {
                            int n2 = this.read();
                            int n3 = 0;
                            while (n2 >= 0 && (n2 & 0x80) != 0x0) {
                                n3 = (n3 | (n2 & 0x7F)) << 7;
                                n2 = this.read();
                            }
                            n = (n3 | (n2 & 0x7F));
                        }
                        if ((read & 0x20) == 0x0) {
                            return new BERTaggedObject(false, n, new DEROctetString(this.c()));
                        }
                        final DERObject object3 = this.readObject();
                        if (object3 == this.db) {
                            return new DERTaggedObject(n);
                        }
                        DERObject derObject = this.readObject();
                        if (derObject == this.db) {
                            return new BERTaggedObject(n, object3);
                        }
                        final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
                        asn1EncodableVector3.add(object3);
                        do {
                            asn1EncodableVector3.add(derObject);
                            derObject = this.readObject();
                        } while (derObject != this.db);
                        return new BERTaggedObject(false, n, new BERSequence(asn1EncodableVector3));
                    }
                }
            }
            else {
                if (read == 0 && length == 0) {
                    return this.db;
                }
                final byte[] array = new byte[length];
                this.readFully(array);
                return this.buildObject(read, array);
            }
        }
    }
}
