package org.bouncycastle.asn1;

import java.io.EOFException;
import org.bouncycastle.util.io.Streams;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.FilterInputStream;

public class ASN1InputStream extends FilterInputStream implements BERTags
{
    private final int limit;
    private final boolean lazyEvaluate;
    private final byte[][] tmpBuffers;
    
    public ASN1InputStream(final InputStream inputStream) {
        this(inputStream, StreamUtil.findLimit(inputStream));
    }
    
    public ASN1InputStream(final byte[] array) {
        this(new ByteArrayInputStream(array), array.length);
    }
    
    public ASN1InputStream(final byte[] array, final boolean b) {
        this(new ByteArrayInputStream(array), array.length, b);
    }
    
    public ASN1InputStream(final InputStream inputStream, final int n) {
        this(inputStream, n, false);
    }
    
    public ASN1InputStream(final InputStream inputStream, final boolean b) {
        this(inputStream, StreamUtil.findLimit(inputStream), b);
    }
    
    public ASN1InputStream(final InputStream inputStream, final int limit, final boolean lazyEvaluate) {
        super(inputStream);
        this.limit = limit;
        this.lazyEvaluate = lazyEvaluate;
        this.tmpBuffers = new byte[11][];
    }
    
    int getLimit() {
        return this.limit;
    }
    
    protected int readLength() throws IOException {
        return readLength(this, this.limit);
    }
    
    protected void readFully(final byte[] array) throws IOException {
        if (Streams.readFully(this, array) != array.length) {
            throw new EOFException("EOF encountered in middle of object");
        }
    }
    
    protected ASN1Primitive buildObject(final int n, final int n2, final int n3) throws IOException {
        final boolean b = (n & 0x20) != 0x0;
        final DefiniteLengthInputStream definiteLengthInputStream = new DefiniteLengthInputStream(this, n3);
        if ((n & 0x40) != 0x0) {
            return new DERApplicationSpecific(b, n2, definiteLengthInputStream.toByteArray());
        }
        if ((n & 0x80) != 0x0) {
            return new ASN1StreamParser(definiteLengthInputStream).readTaggedObject(b, n2);
        }
        if (!b) {
            return createPrimitiveDERObject(n2, definiteLengthInputStream, this.tmpBuffers);
        }
        switch (n2) {
            case 4: {
                final ASN1EncodableVector buildDEREncodableVector = this.buildDEREncodableVector(definiteLengthInputStream);
                final ASN1OctetString[] array = new ASN1OctetString[buildDEREncodableVector.size()];
                for (int i = 0; i != array.length; ++i) {
                    array[i] = (ASN1OctetString)buildDEREncodableVector.get(i);
                }
                return new BEROctetString(array);
            }
            case 16: {
                if (this.lazyEvaluate) {
                    return new LazyEncodedSequence(definiteLengthInputStream.toByteArray());
                }
                return DERFactory.createSequence(this.buildDEREncodableVector(definiteLengthInputStream));
            }
            case 17: {
                return DERFactory.createSet(this.buildDEREncodableVector(definiteLengthInputStream));
            }
            case 8: {
                return new DERExternal(this.buildDEREncodableVector(definiteLengthInputStream));
            }
            default: {
                throw new IOException("unknown tag " + n2 + " encountered");
            }
        }
    }
    
    ASN1EncodableVector buildEncodableVector() throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        ASN1Primitive object;
        while ((object = this.readObject()) != null) {
            asn1EncodableVector.add(object);
        }
        return asn1EncodableVector;
    }
    
    ASN1EncodableVector buildDEREncodableVector(final DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        return new ASN1InputStream(definiteLengthInputStream).buildEncodableVector();
    }
    
    public ASN1Primitive readObject() throws IOException {
        final int read = this.read();
        if (read <= 0) {
            if (read == 0) {
                throw new IOException("unexpected end-of-contents marker");
            }
            return null;
        }
        else {
            final int tagNumber = readTagNumber(this, read);
            final boolean b = (read & 0x20) != 0x0;
            final int length = this.readLength();
            if (length < 0) {
                if (!b) {
                    throw new IOException("indefinite-length primitive encoding encountered");
                }
                final ASN1StreamParser asn1StreamParser = new ASN1StreamParser(new IndefiniteLengthInputStream(this, this.limit), this.limit);
                if ((read & 0x40) != 0x0) {
                    return new BERApplicationSpecificParser(tagNumber, asn1StreamParser).getLoadedObject();
                }
                if ((read & 0x80) != 0x0) {
                    return new BERTaggedObjectParser(true, tagNumber, asn1StreamParser).getLoadedObject();
                }
                switch (tagNumber) {
                    case 4: {
                        return new BEROctetStringParser(asn1StreamParser).getLoadedObject();
                    }
                    case 16: {
                        return new BERSequenceParser(asn1StreamParser).getLoadedObject();
                    }
                    case 17: {
                        return new BERSetParser(asn1StreamParser).getLoadedObject();
                    }
                    case 8: {
                        return new DERExternalParser(asn1StreamParser).getLoadedObject();
                    }
                    default: {
                        throw new IOException("unknown BER object encountered");
                    }
                }
            }
            else {
                try {
                    return this.buildObject(read, tagNumber, length);
                }
                catch (final IllegalArgumentException ex) {
                    throw new ASN1Exception("corrupted stream detected", ex);
                }
            }
        }
    }
    
    static int readTagNumber(final InputStream inputStream, final int n) throws IOException {
        int n2 = n & 0x1F;
        if (n2 == 31) {
            int n3 = 0;
            int n4 = inputStream.read();
            if ((n4 & 0x7F) == 0x0) {
                throw new IOException("corrupted stream - invalid high tag number found");
            }
            while (n4 >= 0 && (n4 & 0x80) != 0x0) {
                n3 = (n3 | (n4 & 0x7F)) << 7;
                n4 = inputStream.read();
            }
            if (n4 < 0) {
                throw new EOFException("EOF found inside tag value.");
            }
            n2 = (n3 | (n4 & 0x7F));
        }
        return n2;
    }
    
    static int readLength(final InputStream inputStream, final int n) throws IOException {
        int read = inputStream.read();
        if (read < 0) {
            throw new EOFException("EOF found when length expected");
        }
        if (read == 128) {
            return -1;
        }
        if (read > 127) {
            final int n2 = read & 0x7F;
            if (n2 > 4) {
                throw new IOException("DER length more than 4 bytes: " + n2);
            }
            read = 0;
            for (int i = 0; i < n2; ++i) {
                final int read2 = inputStream.read();
                if (read2 < 0) {
                    throw new EOFException("EOF found reading length");
                }
                read = (read << 8) + read2;
            }
            if (read < 0) {
                throw new IOException("corrupted stream - negative length found");
            }
            if (read >= n) {
                throw new IOException("corrupted stream - out of bounds length found");
            }
        }
        return read;
    }
    
    private static byte[] getBuffer(final DefiniteLengthInputStream definiteLengthInputStream, final byte[][] array) throws IOException {
        final int remaining = definiteLengthInputStream.getRemaining();
        if (definiteLengthInputStream.getRemaining() < array.length) {
            byte[] array2 = array[remaining];
            if (array2 == null) {
                final int n = remaining;
                final byte[] array3 = new byte[remaining];
                array[n] = array3;
                array2 = array3;
            }
            Streams.readFully(definiteLengthInputStream, array2);
            return array2;
        }
        return definiteLengthInputStream.toByteArray();
    }
    
    private static char[] getBMPCharBuffer(final DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        final int n = definiteLengthInputStream.getRemaining() / 2;
        final char[] array = new char[n];
        int read;
        int read2;
        for (int i = 0; i < n; array[i++] = (char)(read << 8 | (read2 & 0xFF))) {
            read = definiteLengthInputStream.read();
            if (read < 0) {
                break;
            }
            read2 = definiteLengthInputStream.read();
            if (read2 < 0) {
                break;
            }
        }
        return array;
    }
    
    static ASN1Primitive createPrimitiveDERObject(final int n, final DefiniteLengthInputStream definiteLengthInputStream, final byte[][] array) throws IOException {
        switch (n) {
            case 3: {
                return ASN1BitString.fromInputStream(definiteLengthInputStream.getRemaining(), definiteLengthInputStream);
            }
            case 30: {
                return new DERBMPString(getBMPCharBuffer(definiteLengthInputStream));
            }
            case 1: {
                return ASN1Boolean.fromOctetString(getBuffer(definiteLengthInputStream, array));
            }
            case 10: {
                return ASN1Enumerated.fromOctetString(getBuffer(definiteLengthInputStream, array));
            }
            case 24: {
                return new ASN1GeneralizedTime(definiteLengthInputStream.toByteArray());
            }
            case 27: {
                return new DERGeneralString(definiteLengthInputStream.toByteArray());
            }
            case 22: {
                return new DERIA5String(definiteLengthInputStream.toByteArray());
            }
            case 2: {
                return new ASN1Integer(definiteLengthInputStream.toByteArray(), false);
            }
            case 5: {
                return DERNull.INSTANCE;
            }
            case 18: {
                return new DERNumericString(definiteLengthInputStream.toByteArray());
            }
            case 6: {
                return ASN1ObjectIdentifier.fromOctetString(getBuffer(definiteLengthInputStream, array));
            }
            case 4: {
                return new DEROctetString(definiteLengthInputStream.toByteArray());
            }
            case 19: {
                return new DERPrintableString(definiteLengthInputStream.toByteArray());
            }
            case 20: {
                return new DERT61String(definiteLengthInputStream.toByteArray());
            }
            case 28: {
                return new DERUniversalString(definiteLengthInputStream.toByteArray());
            }
            case 23: {
                return new ASN1UTCTime(definiteLengthInputStream.toByteArray());
            }
            case 12: {
                return new DERUTF8String(definiteLengthInputStream.toByteArray());
            }
            case 26: {
                return new DERVisibleString(definiteLengthInputStream.toByteArray());
            }
            case 25: {
                return new DERGraphicString(definiteLengthInputStream.toByteArray());
            }
            case 21: {
                return new DERVideotexString(definiteLengthInputStream.toByteArray());
            }
            default: {
                throw new IOException("unknown tag " + n + " encountered");
            }
        }
    }
}
