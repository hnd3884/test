package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Integers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Strings;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import org.bouncycastle.util.Shorts;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.io.Streams;
import java.io.EOFException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import java.io.OutputStream;
import java.io.IOException;

public class TlsUtils
{
    public static final byte[] EMPTY_BYTES;
    public static final short[] EMPTY_SHORTS;
    public static final int[] EMPTY_INTS;
    public static final long[] EMPTY_LONGS;
    public static final Integer EXT_signature_algorithms;
    static final byte[] SSL_CLIENT;
    static final byte[] SSL_SERVER;
    static final byte[][] SSL3_CONST;
    
    public static void checkUint8(final short n) throws IOException {
        if (!isValidUint8(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint8(final int n) throws IOException {
        if (!isValidUint8(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint8(final long n) throws IOException {
        if (!isValidUint8(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint16(final int n) throws IOException {
        if (!isValidUint16(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint16(final long n) throws IOException {
        if (!isValidUint16(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint24(final int n) throws IOException {
        if (!isValidUint24(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint24(final long n) throws IOException {
        if (!isValidUint24(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint32(final long n) throws IOException {
        if (!isValidUint32(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint48(final long n) throws IOException {
        if (!isValidUint48(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static void checkUint64(final long n) throws IOException {
        if (!isValidUint64(n)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public static boolean isValidUint8(final short n) {
        return (n & 0xFF) == n;
    }
    
    public static boolean isValidUint8(final int n) {
        return (n & 0xFF) == n;
    }
    
    public static boolean isValidUint8(final long n) {
        return (n & 0xFFL) == n;
    }
    
    public static boolean isValidUint16(final int n) {
        return (n & 0xFFFF) == n;
    }
    
    public static boolean isValidUint16(final long n) {
        return (n & 0xFFFFL) == n;
    }
    
    public static boolean isValidUint24(final int n) {
        return (n & 0xFFFFFF) == n;
    }
    
    public static boolean isValidUint24(final long n) {
        return (n & 0xFFFFFFL) == n;
    }
    
    public static boolean isValidUint32(final long n) {
        return (n & 0xFFFFFFFFL) == n;
    }
    
    public static boolean isValidUint48(final long n) {
        return (n & 0xFFFFFFFFFFFFL) == n;
    }
    
    public static boolean isValidUint64(final long n) {
        return true;
    }
    
    public static boolean isSSL(final TlsContext tlsContext) {
        return tlsContext.getServerVersion().isSSL();
    }
    
    public static boolean isTLSv11(final ProtocolVersion protocolVersion) {
        return ProtocolVersion.TLSv11.isEqualOrEarlierVersionOf(protocolVersion.getEquivalentTLSVersion());
    }
    
    public static boolean isTLSv11(final TlsContext tlsContext) {
        return isTLSv11(tlsContext.getServerVersion());
    }
    
    public static boolean isTLSv12(final ProtocolVersion protocolVersion) {
        return ProtocolVersion.TLSv12.isEqualOrEarlierVersionOf(protocolVersion.getEquivalentTLSVersion());
    }
    
    public static boolean isTLSv12(final TlsContext tlsContext) {
        return isTLSv12(tlsContext.getServerVersion());
    }
    
    public static void writeUint8(final short n, final OutputStream outputStream) throws IOException {
        outputStream.write(n);
    }
    
    public static void writeUint8(final int n, final OutputStream outputStream) throws IOException {
        outputStream.write(n);
    }
    
    public static void writeUint8(final short n, final byte[] array, final int n2) {
        array[n2] = (byte)n;
    }
    
    public static void writeUint8(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)n;
    }
    
    public static void writeUint16(final int n, final OutputStream outputStream) throws IOException {
        outputStream.write(n >>> 8);
        outputStream.write(n);
    }
    
    public static void writeUint16(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >>> 8);
        array[n2 + 1] = (byte)n;
    }
    
    public static void writeUint24(final int n, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(n >>> 16));
        outputStream.write((byte)(n >>> 8));
        outputStream.write((byte)n);
    }
    
    public static void writeUint24(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >>> 16);
        array[n2 + 1] = (byte)(n >>> 8);
        array[n2 + 2] = (byte)n;
    }
    
    public static void writeUint32(final long n, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(n >>> 24));
        outputStream.write((byte)(n >>> 16));
        outputStream.write((byte)(n >>> 8));
        outputStream.write((byte)n);
    }
    
    public static void writeUint32(final long n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >>> 24);
        array[n2 + 1] = (byte)(n >>> 16);
        array[n2 + 2] = (byte)(n >>> 8);
        array[n2 + 3] = (byte)n;
    }
    
    public static void writeUint48(final long n, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(n >>> 40));
        outputStream.write((byte)(n >>> 32));
        outputStream.write((byte)(n >>> 24));
        outputStream.write((byte)(n >>> 16));
        outputStream.write((byte)(n >>> 8));
        outputStream.write((byte)n);
    }
    
    public static void writeUint48(final long n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >>> 40);
        array[n2 + 1] = (byte)(n >>> 32);
        array[n2 + 2] = (byte)(n >>> 24);
        array[n2 + 3] = (byte)(n >>> 16);
        array[n2 + 4] = (byte)(n >>> 8);
        array[n2 + 5] = (byte)n;
    }
    
    public static void writeUint64(final long n, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(n >>> 56));
        outputStream.write((byte)(n >>> 48));
        outputStream.write((byte)(n >>> 40));
        outputStream.write((byte)(n >>> 32));
        outputStream.write((byte)(n >>> 24));
        outputStream.write((byte)(n >>> 16));
        outputStream.write((byte)(n >>> 8));
        outputStream.write((byte)n);
    }
    
    public static void writeUint64(final long n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >>> 56);
        array[n2 + 1] = (byte)(n >>> 48);
        array[n2 + 2] = (byte)(n >>> 40);
        array[n2 + 3] = (byte)(n >>> 32);
        array[n2 + 4] = (byte)(n >>> 24);
        array[n2 + 5] = (byte)(n >>> 16);
        array[n2 + 6] = (byte)(n >>> 8);
        array[n2 + 7] = (byte)n;
    }
    
    public static void writeOpaque8(final byte[] array, final OutputStream outputStream) throws IOException {
        checkUint8(array.length);
        writeUint8(array.length, outputStream);
        outputStream.write(array);
    }
    
    public static void writeOpaque16(final byte[] array, final OutputStream outputStream) throws IOException {
        checkUint16(array.length);
        writeUint16(array.length, outputStream);
        outputStream.write(array);
    }
    
    public static void writeOpaque24(final byte[] array, final OutputStream outputStream) throws IOException {
        checkUint24(array.length);
        writeUint24(array.length, outputStream);
        outputStream.write(array);
    }
    
    public static void writeUint8Array(final short[] array, final OutputStream outputStream) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            writeUint8(array[i], outputStream);
        }
    }
    
    public static void writeUint8Array(final short[] array, final byte[] array2, int n) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            writeUint8(array[i], array2, n);
            ++n;
        }
    }
    
    public static void writeUint8ArrayWithUint8Length(final short[] array, final OutputStream outputStream) throws IOException {
        checkUint8(array.length);
        writeUint8(array.length, outputStream);
        writeUint8Array(array, outputStream);
    }
    
    public static void writeUint8ArrayWithUint8Length(final short[] array, final byte[] array2, final int n) throws IOException {
        checkUint8(array.length);
        writeUint8(array.length, array2, n);
        writeUint8Array(array, array2, n + 1);
    }
    
    public static void writeUint16Array(final int[] array, final OutputStream outputStream) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            writeUint16(array[i], outputStream);
        }
    }
    
    public static void writeUint16Array(final int[] array, final byte[] array2, int n) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            writeUint16(array[i], array2, n);
            n += 2;
        }
    }
    
    public static void writeUint16ArrayWithUint16Length(final int[] array, final OutputStream outputStream) throws IOException {
        final int n = 2 * array.length;
        checkUint16(n);
        writeUint16(n, outputStream);
        writeUint16Array(array, outputStream);
    }
    
    public static void writeUint16ArrayWithUint16Length(final int[] array, final byte[] array2, final int n) throws IOException {
        final int n2 = 2 * array.length;
        checkUint16(n2);
        writeUint16(n2, array2, n);
        writeUint16Array(array, array2, n + 2);
    }
    
    public static byte[] encodeOpaque8(final byte[] array) throws IOException {
        checkUint8(array.length);
        return Arrays.prepend(array, (byte)array.length);
    }
    
    public static byte[] encodeUint8ArrayWithUint8Length(final short[] array) throws IOException {
        final byte[] array2 = new byte[1 + array.length];
        writeUint8ArrayWithUint8Length(array, array2, 0);
        return array2;
    }
    
    public static byte[] encodeUint16ArrayWithUint16Length(final int[] array) throws IOException {
        final byte[] array2 = new byte[2 + 2 * array.length];
        writeUint16ArrayWithUint16Length(array, array2, 0);
        return array2;
    }
    
    public static short readUint8(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        if (read < 0) {
            throw new EOFException();
        }
        return (short)read;
    }
    
    public static short readUint8(final byte[] array, final int n) {
        return (short)(array[n] & 0xFF);
    }
    
    public static int readUint16(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        if (read2 < 0) {
            throw new EOFException();
        }
        return read << 8 | read2;
    }
    
    public static int readUint16(final byte[] array, int n) {
        return (array[n] & 0xFF) << 8 | (array[++n] & 0xFF);
    }
    
    public static int readUint24(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        final int read3 = inputStream.read();
        if (read3 < 0) {
            throw new EOFException();
        }
        return read << 16 | read2 << 8 | read3;
    }
    
    public static int readUint24(final byte[] array, int n) {
        return (array[n] & 0xFF) << 16 | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF);
    }
    
    public static long readUint32(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        final int read3 = inputStream.read();
        final int read4 = inputStream.read();
        if (read4 < 0) {
            throw new EOFException();
        }
        return (long)(read << 24 | read2 << 16 | read3 << 8 | read4) & 0xFFFFFFFFL;
    }
    
    public static long readUint32(final byte[] array, int n) {
        return (long)((array[n] & 0xFF) << 24 | (array[++n] & 0xFF) << 16 | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF)) & 0xFFFFFFFFL;
    }
    
    public static long readUint48(final InputStream inputStream) throws IOException {
        return ((long)readUint24(inputStream) & 0xFFFFFFFFL) << 24 | ((long)readUint24(inputStream) & 0xFFFFFFFFL);
    }
    
    public static long readUint48(final byte[] array, final int n) {
        return ((long)readUint24(array, n) & 0xFFFFFFFFL) << 24 | ((long)readUint24(array, n + 3) & 0xFFFFFFFFL);
    }
    
    public static byte[] readAllOrNothing(final int n, final InputStream inputStream) throws IOException {
        if (n < 1) {
            return TlsUtils.EMPTY_BYTES;
        }
        final byte[] array = new byte[n];
        final int fully = Streams.readFully(inputStream, array);
        if (fully == 0) {
            return null;
        }
        if (fully != n) {
            throw new EOFException();
        }
        return array;
    }
    
    public static byte[] readFully(final int n, final InputStream inputStream) throws IOException {
        if (n < 1) {
            return TlsUtils.EMPTY_BYTES;
        }
        final byte[] array = new byte[n];
        if (n != Streams.readFully(inputStream, array)) {
            throw new EOFException();
        }
        return array;
    }
    
    public static void readFully(final byte[] array, final InputStream inputStream) throws IOException {
        final int length = array.length;
        if (length > 0 && length != Streams.readFully(inputStream, array)) {
            throw new EOFException();
        }
    }
    
    public static byte[] readOpaque8(final InputStream inputStream) throws IOException {
        return readFully(readUint8(inputStream), inputStream);
    }
    
    public static byte[] readOpaque16(final InputStream inputStream) throws IOException {
        return readFully(readUint16(inputStream), inputStream);
    }
    
    public static byte[] readOpaque24(final InputStream inputStream) throws IOException {
        return readFully(readUint24(inputStream), inputStream);
    }
    
    public static short[] readUint8Array(final int n, final InputStream inputStream) throws IOException {
        final short[] array = new short[n];
        for (int i = 0; i < n; ++i) {
            array[i] = readUint8(inputStream);
        }
        return array;
    }
    
    public static int[] readUint16Array(final int n, final InputStream inputStream) throws IOException {
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = readUint16(inputStream);
        }
        return array;
    }
    
    public static ProtocolVersion readVersion(final byte[] array, final int n) throws IOException {
        return ProtocolVersion.get(array[n] & 0xFF, array[n + 1] & 0xFF);
    }
    
    public static ProtocolVersion readVersion(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        if (read2 < 0) {
            throw new EOFException();
        }
        return ProtocolVersion.get(read, read2);
    }
    
    public static int readVersionRaw(final byte[] array, final int n) throws IOException {
        return array[n] << 8 | array[n + 1];
    }
    
    public static int readVersionRaw(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        if (read2 < 0) {
            throw new EOFException();
        }
        return read << 8 | read2;
    }
    
    public static ASN1Primitive readASN1Object(final byte[] array) throws IOException {
        final ASN1InputStream asn1InputStream = new ASN1InputStream(array);
        final ASN1Primitive object = asn1InputStream.readObject();
        if (null == object) {
            throw new TlsFatalAlert((short)50);
        }
        if (null != asn1InputStream.readObject()) {
            throw new TlsFatalAlert((short)50);
        }
        return object;
    }
    
    public static ASN1Primitive readDERObject(final byte[] array) throws IOException {
        final ASN1Primitive asn1Object = readASN1Object(array);
        if (!Arrays.areEqual(asn1Object.getEncoded("DER"), array)) {
            throw new TlsFatalAlert((short)50);
        }
        return asn1Object;
    }
    
    public static void writeGMTUnixTime(final byte[] array, final int n) {
        final int n2 = (int)(System.currentTimeMillis() / 1000L);
        array[n] = (byte)(n2 >>> 24);
        array[n + 1] = (byte)(n2 >>> 16);
        array[n + 2] = (byte)(n2 >>> 8);
        array[n + 3] = (byte)n2;
    }
    
    public static void writeVersion(final ProtocolVersion protocolVersion, final OutputStream outputStream) throws IOException {
        outputStream.write(protocolVersion.getMajorVersion());
        outputStream.write(protocolVersion.getMinorVersion());
    }
    
    public static void writeVersion(final ProtocolVersion protocolVersion, final byte[] array, final int n) {
        array[n] = (byte)protocolVersion.getMajorVersion();
        array[n + 1] = (byte)protocolVersion.getMinorVersion();
    }
    
    public static Vector getAllSignatureAlgorithms() {
        final Vector vector = new Vector(4);
        vector.addElement(Shorts.valueOf((short)0));
        vector.addElement(Shorts.valueOf((short)1));
        vector.addElement(Shorts.valueOf((short)2));
        vector.addElement(Shorts.valueOf((short)3));
        return vector;
    }
    
    public static Vector getDefaultDSSSignatureAlgorithms() {
        return vectorOfOne(new SignatureAndHashAlgorithm((short)2, (short)2));
    }
    
    public static Vector getDefaultECDSASignatureAlgorithms() {
        return vectorOfOne(new SignatureAndHashAlgorithm((short)2, (short)3));
    }
    
    public static Vector getDefaultRSASignatureAlgorithms() {
        return vectorOfOne(new SignatureAndHashAlgorithm((short)2, (short)1));
    }
    
    public static Vector getDefaultSupportedSignatureAlgorithms() {
        final short[] array = { 2, 3, 4, 5, 6 };
        final short[] array2 = { 1, 2, 3 };
        final Vector vector = new Vector();
        for (int i = 0; i < array2.length; ++i) {
            for (int j = 0; j < array.length; ++j) {
                vector.addElement(new SignatureAndHashAlgorithm(array[j], array2[i]));
            }
        }
        return vector;
    }
    
    public static SignatureAndHashAlgorithm getSignatureAndHashAlgorithm(final TlsContext tlsContext, final TlsSignerCredentials tlsSignerCredentials) throws IOException {
        SignatureAndHashAlgorithm signatureAndHashAlgorithm = null;
        if (isTLSv12(tlsContext)) {
            signatureAndHashAlgorithm = tlsSignerCredentials.getSignatureAndHashAlgorithm();
            if (signatureAndHashAlgorithm == null) {
                throw new TlsFatalAlert((short)80);
            }
        }
        return signatureAndHashAlgorithm;
    }
    
    public static byte[] getExtensionData(final Hashtable hashtable, final Integer n) {
        return (byte[])((hashtable == null) ? null : ((byte[])hashtable.get(n)));
    }
    
    public static boolean hasExpectedEmptyExtensionData(final Hashtable hashtable, final Integer n, final short n2) throws IOException {
        final byte[] extensionData = getExtensionData(hashtable, n);
        if (extensionData == null) {
            return false;
        }
        if (extensionData.length != 0) {
            throw new TlsFatalAlert(n2);
        }
        return true;
    }
    
    public static TlsSession importSession(final byte[] array, final SessionParameters sessionParameters) {
        return new TlsSessionImpl(array, sessionParameters);
    }
    
    public static boolean isSignatureAlgorithmsExtensionAllowed(final ProtocolVersion protocolVersion) {
        return ProtocolVersion.TLSv12.isEqualOrEarlierVersionOf(protocolVersion.getEquivalentTLSVersion());
    }
    
    public static void addSignatureAlgorithmsExtension(final Hashtable hashtable, final Vector vector) throws IOException {
        hashtable.put(TlsUtils.EXT_signature_algorithms, createSignatureAlgorithmsExtension(vector));
    }
    
    public static Vector getSignatureAlgorithmsExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = getExtensionData(hashtable, TlsUtils.EXT_signature_algorithms);
        return (extensionData == null) ? null : readSignatureAlgorithmsExtension(extensionData);
    }
    
    public static byte[] createSignatureAlgorithmsExtension(final Vector vector) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        encodeSupportedSignatureAlgorithms(vector, false, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static Vector readSignatureAlgorithmsExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final Vector supportedSignatureAlgorithms = parseSupportedSignatureAlgorithms(false, byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        return supportedSignatureAlgorithms;
    }
    
    public static void encodeSupportedSignatureAlgorithms(final Vector vector, final boolean b, final OutputStream outputStream) throws IOException {
        if (vector == null || vector.size() < 1 || vector.size() >= 32768) {
            throw new IllegalArgumentException("'supportedSignatureAlgorithms' must have length from 1 to (2^15 - 1)");
        }
        final int n = 2 * vector.size();
        checkUint16(n);
        writeUint16(n, outputStream);
        for (int i = 0; i < vector.size(); ++i) {
            final SignatureAndHashAlgorithm signatureAndHashAlgorithm = vector.elementAt(i);
            if (!b && signatureAndHashAlgorithm.getSignature() == 0) {
                throw new IllegalArgumentException("SignatureAlgorithm.anonymous MUST NOT appear in the signature_algorithms extension");
            }
            signatureAndHashAlgorithm.encode(outputStream);
        }
    }
    
    public static Vector parseSupportedSignatureAlgorithms(final boolean b, final InputStream inputStream) throws IOException {
        final int uint16 = readUint16(inputStream);
        if (uint16 < 2 || (uint16 & 0x1) != 0x0) {
            throw new TlsFatalAlert((short)50);
        }
        final int n = uint16 / 2;
        final Vector vector = new Vector<SignatureAndHashAlgorithm>(n);
        for (int i = 0; i < n; ++i) {
            final SignatureAndHashAlgorithm parse = SignatureAndHashAlgorithm.parse(inputStream);
            if (!b && parse.getSignature() == 0) {
                throw new TlsFatalAlert((short)47);
            }
            vector.addElement(parse);
        }
        return vector;
    }
    
    public static void verifySupportedSignatureAlgorithm(final Vector vector, final SignatureAndHashAlgorithm signatureAndHashAlgorithm) throws IOException {
        if (vector == null || vector.size() < 1 || vector.size() >= 32768) {
            throw new IllegalArgumentException("'supportedSignatureAlgorithms' must have length from 1 to (2^15 - 1)");
        }
        if (signatureAndHashAlgorithm == null) {
            throw new IllegalArgumentException("'signatureAlgorithm' cannot be null");
        }
        if (signatureAndHashAlgorithm.getSignature() != 0) {
            for (int i = 0; i < vector.size(); ++i) {
                final SignatureAndHashAlgorithm signatureAndHashAlgorithm2 = vector.elementAt(i);
                if (signatureAndHashAlgorithm2.getHash() == signatureAndHashAlgorithm.getHash() && signatureAndHashAlgorithm2.getSignature() == signatureAndHashAlgorithm.getSignature()) {
                    return;
                }
            }
        }
        throw new TlsFatalAlert((short)47);
    }
    
    public static byte[] PRF(final TlsContext tlsContext, final byte[] array, final String s, final byte[] array2, final int n) {
        if (tlsContext.getServerVersion().isSSL()) {
            throw new IllegalStateException("No PRF available for SSLv3 session");
        }
        final byte[] byteArray = Strings.toByteArray(s);
        final byte[] concat = concat(byteArray, array2);
        final int prfAlgorithm = tlsContext.getSecurityParameters().getPrfAlgorithm();
        if (prfAlgorithm == 0) {
            return PRF_legacy(array, byteArray, concat, n);
        }
        final Digest prfHash = createPRFHash(prfAlgorithm);
        final byte[] array3 = new byte[n];
        hmac_hash(prfHash, array, concat, array3);
        return array3;
    }
    
    public static byte[] PRF_legacy(final byte[] array, final String s, final byte[] array2, final int n) {
        final byte[] byteArray = Strings.toByteArray(s);
        return PRF_legacy(array, byteArray, concat(byteArray, array2), n);
    }
    
    static byte[] PRF_legacy(final byte[] array, final byte[] array2, final byte[] array3, final int n) {
        final int n2 = (array.length + 1) / 2;
        final byte[] array4 = new byte[n2];
        final byte[] array5 = new byte[n2];
        System.arraycopy(array, 0, array4, 0, n2);
        System.arraycopy(array, array.length - n2, array5, 0, n2);
        final byte[] array6 = new byte[n];
        final byte[] array7 = new byte[n];
        hmac_hash(createHash((short)1), array4, array3, array6);
        hmac_hash(createHash((short)2), array5, array3, array7);
        for (int i = 0; i < n; ++i) {
            final byte[] array8 = array6;
            final int n3 = i;
            array8[n3] ^= array7[i];
        }
        return array6;
    }
    
    static byte[] concat(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    static void hmac_hash(final Digest digest, final byte[] array, final byte[] array2, final byte[] array3) {
        final HMac hMac = new HMac(digest);
        hMac.init(new KeyParameter(array));
        byte[] array4 = array2;
        final int digestSize = digest.getDigestSize();
        final int n = (array3.length + digestSize - 1) / digestSize;
        final byte[] array5 = new byte[hMac.getMacSize()];
        final byte[] array6 = new byte[hMac.getMacSize()];
        for (int i = 0; i < n; ++i) {
            hMac.update(array4, 0, array4.length);
            hMac.doFinal(array5, 0);
            array4 = array5;
            hMac.update(array4, 0, array4.length);
            hMac.update(array2, 0, array2.length);
            hMac.doFinal(array6, 0);
            System.arraycopy(array6, 0, array3, digestSize * i, Math.min(digestSize, array3.length - digestSize * i));
        }
    }
    
    static void validateKeyUsage(final Certificate certificate, final int n) throws IOException {
        final Extensions extensions = certificate.getTBSCertificate().getExtensions();
        if (extensions != null) {
            final KeyUsage fromExtensions = KeyUsage.fromExtensions(extensions);
            if (fromExtensions != null && (fromExtensions.getBytes()[0] & 0xFF & n) != n) {
                throw new TlsFatalAlert((short)46);
            }
        }
    }
    
    static byte[] calculateKeyBlock(final TlsContext tlsContext, final int n) {
        final SecurityParameters securityParameters = tlsContext.getSecurityParameters();
        final byte[] masterSecret = securityParameters.getMasterSecret();
        final byte[] concat = concat(securityParameters.getServerRandom(), securityParameters.getClientRandom());
        if (isSSL(tlsContext)) {
            return calculateKeyBlock_SSL(masterSecret, concat, n);
        }
        return PRF(tlsContext, masterSecret, "key expansion", concat, n);
    }
    
    static byte[] calculateKeyBlock_SSL(final byte[] array, final byte[] array2, final int n) {
        final Digest hash = createHash((short)1);
        final Digest hash2 = createHash((short)2);
        final int digestSize = hash.getDigestSize();
        final byte[] array3 = new byte[hash2.getDigestSize()];
        final byte[] array4 = new byte[n + digestSize];
        for (int n2 = 0, i = 0; i < n; i += digestSize, ++n2) {
            final byte[] array5 = TlsUtils.SSL3_CONST[n2];
            hash2.update(array5, 0, array5.length);
            hash2.update(array, 0, array.length);
            hash2.update(array2, 0, array2.length);
            hash2.doFinal(array3, 0);
            hash.update(array, 0, array.length);
            hash.update(array3, 0, array3.length);
            hash.doFinal(array4, i);
        }
        return Arrays.copyOfRange(array4, 0, n);
    }
    
    static byte[] calculateMasterSecret(final TlsContext tlsContext, final byte[] array) {
        final SecurityParameters securityParameters = tlsContext.getSecurityParameters();
        byte[] array2;
        if (securityParameters.extendedMasterSecret) {
            array2 = securityParameters.getSessionHash();
        }
        else {
            array2 = concat(securityParameters.getClientRandom(), securityParameters.getServerRandom());
        }
        if (isSSL(tlsContext)) {
            return calculateMasterSecret_SSL(array, array2);
        }
        return PRF(tlsContext, array, securityParameters.extendedMasterSecret ? "extended master secret" : "master secret", array2, 48);
    }
    
    static byte[] calculateMasterSecret_SSL(final byte[] array, final byte[] array2) {
        final Digest hash = createHash((short)1);
        final Digest hash2 = createHash((short)2);
        final int digestSize = hash.getDigestSize();
        final byte[] array3 = new byte[hash2.getDigestSize()];
        final byte[] array4 = new byte[digestSize * 3];
        int n = 0;
        for (int i = 0; i < 3; ++i) {
            final byte[] array5 = TlsUtils.SSL3_CONST[i];
            hash2.update(array5, 0, array5.length);
            hash2.update(array, 0, array.length);
            hash2.update(array2, 0, array2.length);
            hash2.doFinal(array3, 0);
            hash.update(array, 0, array.length);
            hash.update(array3, 0, array3.length);
            hash.doFinal(array4, n);
            n += digestSize;
        }
        return array4;
    }
    
    static byte[] calculateVerifyData(final TlsContext tlsContext, final String s, final byte[] array) {
        if (isSSL(tlsContext)) {
            return array;
        }
        final SecurityParameters securityParameters = tlsContext.getSecurityParameters();
        return PRF(tlsContext, securityParameters.getMasterSecret(), s, array, securityParameters.getVerifyDataLength());
    }
    
    public static Digest createHash(final short n) {
        switch (n) {
            case 1: {
                return new MD5Digest();
            }
            case 2: {
                return new SHA1Digest();
            }
            case 3: {
                return new SHA224Digest();
            }
            case 4: {
                return new SHA256Digest();
            }
            case 5: {
                return new SHA384Digest();
            }
            case 6: {
                return new SHA512Digest();
            }
            default: {
                throw new IllegalArgumentException("unknown HashAlgorithm");
            }
        }
    }
    
    public static Digest createHash(final SignatureAndHashAlgorithm signatureAndHashAlgorithm) {
        return (signatureAndHashAlgorithm == null) ? new CombinedHash() : createHash(signatureAndHashAlgorithm.getHash());
    }
    
    public static Digest cloneHash(final short n, final Digest digest) {
        switch (n) {
            case 1: {
                return new MD5Digest((MD5Digest)digest);
            }
            case 2: {
                return new SHA1Digest((SHA1Digest)digest);
            }
            case 3: {
                return new SHA224Digest((SHA224Digest)digest);
            }
            case 4: {
                return new SHA256Digest((SHA256Digest)digest);
            }
            case 5: {
                return new SHA384Digest((SHA384Digest)digest);
            }
            case 6: {
                return new SHA512Digest((SHA512Digest)digest);
            }
            default: {
                throw new IllegalArgumentException("unknown HashAlgorithm");
            }
        }
    }
    
    public static Digest createPRFHash(final int n) {
        switch (n) {
            case 0: {
                return new CombinedHash();
            }
            default: {
                return createHash(getHashAlgorithmForPRFAlgorithm(n));
            }
        }
    }
    
    public static Digest clonePRFHash(final int n, final Digest digest) {
        switch (n) {
            case 0: {
                return new CombinedHash((CombinedHash)digest);
            }
            default: {
                return cloneHash(getHashAlgorithmForPRFAlgorithm(n), digest);
            }
        }
    }
    
    public static short getHashAlgorithmForPRFAlgorithm(final int n) {
        switch (n) {
            case 0: {
                throw new IllegalArgumentException("legacy PRF not a valid algorithm");
            }
            case 1: {
                return 4;
            }
            case 2: {
                return 5;
            }
            default: {
                throw new IllegalArgumentException("unknown PRFAlgorithm");
            }
        }
    }
    
    public static ASN1ObjectIdentifier getOIDForHashAlgorithm(final short n) {
        switch (n) {
            case 1: {
                return PKCSObjectIdentifiers.md5;
            }
            case 2: {
                return X509ObjectIdentifiers.id_SHA1;
            }
            case 3: {
                return NISTObjectIdentifiers.id_sha224;
            }
            case 4: {
                return NISTObjectIdentifiers.id_sha256;
            }
            case 5: {
                return NISTObjectIdentifiers.id_sha384;
            }
            case 6: {
                return NISTObjectIdentifiers.id_sha512;
            }
            default: {
                throw new IllegalArgumentException("unknown HashAlgorithm");
            }
        }
    }
    
    static short getClientCertificateType(final org.bouncycastle.crypto.tls.Certificate certificate, final org.bouncycastle.crypto.tls.Certificate certificate2) throws IOException {
        if (certificate.isEmpty()) {
            return -1;
        }
        final Certificate certificate3 = certificate.getCertificateAt(0);
        final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate3.getSubjectPublicKeyInfo();
        try {
            final AsymmetricKeyParameter key = PublicKeyFactory.createKey(subjectPublicKeyInfo);
            if (key.isPrivate()) {
                throw new TlsFatalAlert((short)80);
            }
            if (key instanceof RSAKeyParameters) {
                validateKeyUsage(certificate3, 128);
                return 1;
            }
            if (key instanceof DSAPublicKeyParameters) {
                validateKeyUsage(certificate3, 128);
                return 2;
            }
            if (key instanceof ECPublicKeyParameters) {
                validateKeyUsage(certificate3, 128);
                return 64;
            }
            throw new TlsFatalAlert((short)43);
        }
        catch (final Exception ex) {
            throw new TlsFatalAlert((short)43, ex);
        }
    }
    
    static void trackHashAlgorithms(final TlsHandshakeHash tlsHandshakeHash, final Vector vector) {
        if (vector != null) {
            for (int i = 0; i < vector.size(); ++i) {
                final short hash = vector.elementAt(i).getHash();
                if (!HashAlgorithm.isPrivate(hash)) {
                    tlsHandshakeHash.trackHashAlgorithm(hash);
                }
            }
        }
    }
    
    public static boolean hasSigningCapability(final short n) {
        switch (n) {
            case 1:
            case 2:
            case 64: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static TlsSigner createTlsSigner(final short n) {
        switch (n) {
            case 2: {
                return new TlsDSSSigner();
            }
            case 64: {
                return new TlsECDSASigner();
            }
            case 1: {
                return new TlsRSASigner();
            }
            default: {
                throw new IllegalArgumentException("'clientCertificateType' is not a type with signing capability");
            }
        }
    }
    
    private static byte[][] genSSL3Const() {
        final int n = 10;
        final byte[][] array = new byte[n][];
        for (int i = 0; i < n; ++i) {
            final byte[] array2 = new byte[i + 1];
            Arrays.fill(array2, (byte)(65 + i));
            array[i] = array2;
        }
        return array;
    }
    
    private static Vector vectorOfOne(final Object o) {
        final Vector vector = new Vector(1);
        vector.addElement(o);
        return vector;
    }
    
    public static int getCipherType(final int n) throws IOException {
        switch (getEncryptionAlgorithm(n)) {
            case 10:
            case 11:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 103:
            case 104: {
                return 2;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 12:
            case 13:
            case 14: {
                return 1;
            }
            case 0:
            case 1:
            case 2: {
                return 0;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public static int getEncryptionAlgorithm(final int n) throws IOException {
        switch (n) {
            case 10:
            case 13:
            case 16:
            case 19:
            case 22:
            case 27:
            case 139:
            case 143:
            case 147:
            case 49155:
            case 49160:
            case 49165:
            case 49170:
            case 49175:
            case 49178:
            case 49179:
            case 49180:
            case 49204: {
                return 7;
            }
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 60:
            case 62:
            case 63:
            case 64:
            case 103:
            case 108:
            case 140:
            case 144:
            case 148:
            case 174:
            case 178:
            case 182:
            case 49156:
            case 49161:
            case 49166:
            case 49171:
            case 49176:
            case 49181:
            case 49182:
            case 49183:
            case 49187:
            case 49189:
            case 49191:
            case 49193:
            case 49205:
            case 49207: {
                return 8;
            }
            case 49308:
            case 49310:
            case 49316:
            case 49318:
            case 49324: {
                return 15;
            }
            case 49312:
            case 49314:
            case 49320:
            case 49322:
            case 49326: {
                return 16;
            }
            case 156:
            case 158:
            case 160:
            case 162:
            case 164:
            case 166:
            case 168:
            case 170:
            case 172:
            case 49195:
            case 49197:
            case 49199:
            case 49201: {
                return 10;
            }
            case 65280:
            case 65282:
            case 65284:
            case 65296:
            case 65298:
            case 65300: {
                return 103;
            }
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 61:
            case 104:
            case 105:
            case 106:
            case 107:
            case 109:
            case 141:
            case 145:
            case 149:
            case 175:
            case 179:
            case 183:
            case 49157:
            case 49162:
            case 49167:
            case 49172:
            case 49177:
            case 49184:
            case 49185:
            case 49186:
            case 49188:
            case 49190:
            case 49192:
            case 49194:
            case 49206:
            case 49208: {
                return 9;
            }
            case 49309:
            case 49311:
            case 49317:
            case 49319:
            case 49325: {
                return 17;
            }
            case 49313:
            case 49315:
            case 49321:
            case 49323:
            case 49327: {
                return 18;
            }
            case 157:
            case 159:
            case 161:
            case 163:
            case 165:
            case 167:
            case 169:
            case 171:
            case 173:
            case 49196:
            case 49198:
            case 49200:
            case 49202: {
                return 11;
            }
            case 65281:
            case 65283:
            case 65285:
            case 65297:
            case 65299:
            case 65301: {
                return 104;
            }
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 49266:
            case 49268:
            case 49270:
            case 49272:
            case 49300:
            case 49302:
            case 49304:
            case 49306: {
                return 12;
            }
            case 49274:
            case 49276:
            case 49278:
            case 49280:
            case 49282:
            case 49284:
            case 49286:
            case 49288:
            case 49290:
            case 49292:
            case 49294:
            case 49296:
            case 49298: {
                return 19;
            }
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 49267:
            case 49269:
            case 49271:
            case 49273:
            case 49301:
            case 49303:
            case 49305:
            case 49307: {
                return 13;
            }
            case 49275:
            case 49277:
            case 49279:
            case 49281:
            case 49283:
            case 49285:
            case 49287:
            case 49289:
            case 49291:
            case 49293:
            case 49295:
            case 49297:
            case 49299: {
                return 20;
            }
            case 52392:
            case 52393:
            case 52394:
            case 52395:
            case 52396:
            case 52397:
            case 52398: {
                return 21;
            }
            case 1: {
                return 0;
            }
            case 2:
            case 44:
            case 45:
            case 46:
            case 49153:
            case 49158:
            case 49163:
            case 49168:
            case 49173:
            case 49209: {
                return 0;
            }
            case 59:
            case 176:
            case 180:
            case 184:
            case 49210: {
                return 0;
            }
            case 177:
            case 181:
            case 185:
            case 49211: {
                return 0;
            }
            case 4:
            case 24: {
                return 2;
            }
            case 5:
            case 138:
            case 142:
            case 146:
            case 49154:
            case 49159:
            case 49164:
            case 49169:
            case 49174:
            case 49203: {
                return 2;
            }
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155: {
                return 14;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public static int getKeyExchangeAlgorithm(final int n) throws IOException {
        switch (n) {
            case 24:
            case 27:
            case 52:
            case 58:
            case 70:
            case 108:
            case 109:
            case 137:
            case 155:
            case 166:
            case 167:
            case 191:
            case 197:
            case 49284:
            case 49285: {
                return 11;
            }
            case 13:
            case 48:
            case 54:
            case 62:
            case 66:
            case 104:
            case 133:
            case 151:
            case 164:
            case 165:
            case 187:
            case 193:
            case 49282:
            case 49283: {
                return 7;
            }
            case 16:
            case 49:
            case 55:
            case 63:
            case 67:
            case 105:
            case 134:
            case 152:
            case 160:
            case 161:
            case 188:
            case 194:
            case 49278:
            case 49279: {
                return 9;
            }
            case 19:
            case 50:
            case 56:
            case 64:
            case 68:
            case 106:
            case 135:
            case 153:
            case 162:
            case 163:
            case 189:
            case 195:
            case 49280:
            case 49281: {
                return 3;
            }
            case 45:
            case 142:
            case 143:
            case 144:
            case 145:
            case 170:
            case 171:
            case 178:
            case 179:
            case 180:
            case 181:
            case 49296:
            case 49297:
            case 49302:
            case 49303:
            case 49318:
            case 49319:
            case 49322:
            case 49323:
            case 52397:
            case 65298:
            case 65299: {
                return 14;
            }
            case 22:
            case 51:
            case 57:
            case 69:
            case 103:
            case 107:
            case 136:
            case 154:
            case 158:
            case 159:
            case 190:
            case 196:
            case 49276:
            case 49277:
            case 49310:
            case 49311:
            case 49314:
            case 49315:
            case 52394:
            case 65280:
            case 65281: {
                return 5;
            }
            case 49173:
            case 49174:
            case 49175:
            case 49176:
            case 49177: {
                return 20;
            }
            case 49153:
            case 49154:
            case 49155:
            case 49156:
            case 49157:
            case 49189:
            case 49190:
            case 49197:
            case 49198:
            case 49268:
            case 49269:
            case 49288:
            case 49289: {
                return 16;
            }
            case 49163:
            case 49164:
            case 49165:
            case 49166:
            case 49167:
            case 49193:
            case 49194:
            case 49201:
            case 49202:
            case 49272:
            case 49273:
            case 49292:
            case 49293: {
                return 18;
            }
            case 49158:
            case 49159:
            case 49160:
            case 49161:
            case 49162:
            case 49187:
            case 49188:
            case 49195:
            case 49196:
            case 49266:
            case 49267:
            case 49286:
            case 49287:
            case 49324:
            case 49325:
            case 49326:
            case 49327:
            case 52393:
            case 65284:
            case 65285: {
                return 17;
            }
            case 49203:
            case 49204:
            case 49205:
            case 49206:
            case 49207:
            case 49208:
            case 49209:
            case 49210:
            case 49211:
            case 49306:
            case 49307:
            case 52396:
            case 65300:
            case 65301: {
                return 24;
            }
            case 49168:
            case 49169:
            case 49170:
            case 49171:
            case 49172:
            case 49191:
            case 49192:
            case 49199:
            case 49200:
            case 49270:
            case 49271:
            case 49290:
            case 49291:
            case 52392:
            case 65282:
            case 65283: {
                return 19;
            }
            case 44:
            case 138:
            case 139:
            case 140:
            case 141:
            case 168:
            case 169:
            case 174:
            case 175:
            case 176:
            case 177:
            case 49294:
            case 49295:
            case 49300:
            case 49301:
            case 49316:
            case 49317:
            case 49320:
            case 49321:
            case 52395:
            case 65296:
            case 65297: {
                return 13;
            }
            case 1:
            case 2:
            case 4:
            case 5:
            case 10:
            case 47:
            case 53:
            case 59:
            case 60:
            case 61:
            case 65:
            case 132:
            case 150:
            case 156:
            case 157:
            case 186:
            case 192:
            case 49274:
            case 49275:
            case 49308:
            case 49309:
            case 49312:
            case 49313: {
                return 1;
            }
            case 46:
            case 146:
            case 147:
            case 148:
            case 149:
            case 172:
            case 173:
            case 182:
            case 183:
            case 184:
            case 185:
            case 49298:
            case 49299:
            case 49304:
            case 49305:
            case 52398: {
                return 15;
            }
            case 49178:
            case 49181:
            case 49184: {
                return 21;
            }
            case 49180:
            case 49183:
            case 49186: {
                return 22;
            }
            case 49179:
            case 49182:
            case 49185: {
                return 23;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public static int getMACAlgorithm(final int n) throws IOException {
        switch (n) {
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 49195:
            case 49196:
            case 49197:
            case 49198:
            case 49199:
            case 49200:
            case 49201:
            case 49202:
            case 49274:
            case 49275:
            case 49276:
            case 49277:
            case 49278:
            case 49279:
            case 49280:
            case 49281:
            case 49282:
            case 49283:
            case 49284:
            case 49285:
            case 49286:
            case 49287:
            case 49288:
            case 49289:
            case 49290:
            case 49291:
            case 49292:
            case 49293:
            case 49294:
            case 49295:
            case 49296:
            case 49297:
            case 49298:
            case 49299:
            case 49308:
            case 49309:
            case 49310:
            case 49311:
            case 49312:
            case 49313:
            case 49314:
            case 49315:
            case 49316:
            case 49317:
            case 49318:
            case 49319:
            case 49320:
            case 49321:
            case 49322:
            case 49323:
            case 49324:
            case 49325:
            case 49326:
            case 49327:
            case 52392:
            case 52393:
            case 52394:
            case 52395:
            case 52396:
            case 52397:
            case 52398:
            case 65280:
            case 65281:
            case 65282:
            case 65283:
            case 65284:
            case 65285:
            case 65296:
            case 65297:
            case 65298:
            case 65299:
            case 65300:
            case 65301: {
                return 0;
            }
            case 1:
            case 4:
            case 24: {
                return 1;
            }
            case 2:
            case 5:
            case 10:
            case 13:
            case 16:
            case 19:
            case 22:
            case 27:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 49153:
            case 49154:
            case 49155:
            case 49156:
            case 49157:
            case 49158:
            case 49159:
            case 49160:
            case 49161:
            case 49162:
            case 49163:
            case 49164:
            case 49165:
            case 49166:
            case 49167:
            case 49168:
            case 49169:
            case 49170:
            case 49171:
            case 49172:
            case 49173:
            case 49174:
            case 49175:
            case 49176:
            case 49177:
            case 49178:
            case 49179:
            case 49180:
            case 49181:
            case 49182:
            case 49183:
            case 49184:
            case 49185:
            case 49186:
            case 49203:
            case 49204:
            case 49205:
            case 49206:
            case 49209: {
                return 2;
            }
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 174:
            case 176:
            case 178:
            case 180:
            case 182:
            case 184:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 49187:
            case 49189:
            case 49191:
            case 49193:
            case 49207:
            case 49210:
            case 49266:
            case 49268:
            case 49270:
            case 49272:
            case 49300:
            case 49302:
            case 49304:
            case 49306: {
                return 3;
            }
            case 175:
            case 177:
            case 179:
            case 181:
            case 183:
            case 185:
            case 49188:
            case 49190:
            case 49192:
            case 49194:
            case 49208:
            case 49211:
            case 49267:
            case 49269:
            case 49271:
            case 49273:
            case 49301:
            case 49303:
            case 49305:
            case 49307: {
                return 4;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public static ProtocolVersion getMinimumVersion(final int n) {
        switch (n) {
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 49187:
            case 49188:
            case 49189:
            case 49190:
            case 49191:
            case 49192:
            case 49193:
            case 49194:
            case 49195:
            case 49196:
            case 49197:
            case 49198:
            case 49199:
            case 49200:
            case 49201:
            case 49202:
            case 49266:
            case 49267:
            case 49268:
            case 49269:
            case 49270:
            case 49271:
            case 49272:
            case 49273:
            case 49274:
            case 49275:
            case 49276:
            case 49277:
            case 49278:
            case 49279:
            case 49280:
            case 49281:
            case 49282:
            case 49283:
            case 49284:
            case 49285:
            case 49286:
            case 49287:
            case 49288:
            case 49289:
            case 49290:
            case 49291:
            case 49292:
            case 49293:
            case 49294:
            case 49295:
            case 49296:
            case 49297:
            case 49298:
            case 49299:
            case 49308:
            case 49309:
            case 49310:
            case 49311:
            case 49312:
            case 49313:
            case 49314:
            case 49315:
            case 49316:
            case 49317:
            case 49318:
            case 49319:
            case 49320:
            case 49321:
            case 49322:
            case 49323:
            case 49324:
            case 49325:
            case 49326:
            case 49327:
            case 52392:
            case 52393:
            case 52394:
            case 52395:
            case 52396:
            case 52397:
            case 52398:
            case 65280:
            case 65281:
            case 65282:
            case 65283:
            case 65284:
            case 65285:
            case 65296:
            case 65297:
            case 65298:
            case 65299:
            case 65300:
            case 65301: {
                return ProtocolVersion.TLSv12;
            }
            default: {
                return ProtocolVersion.SSLv3;
            }
        }
    }
    
    public static boolean isAEADCipherSuite(final int n) throws IOException {
        return 2 == getCipherType(n);
    }
    
    public static boolean isBlockCipherSuite(final int n) throws IOException {
        return 1 == getCipherType(n);
    }
    
    public static boolean isStreamCipherSuite(final int n) throws IOException {
        return 0 == getCipherType(n);
    }
    
    public static boolean isValidCipherSuiteForSignatureAlgorithms(final int n, final Vector vector) {
        int keyExchangeAlgorithm;
        try {
            keyExchangeAlgorithm = getKeyExchangeAlgorithm(n);
        }
        catch (final IOException ex) {
            return true;
        }
        switch (keyExchangeAlgorithm) {
            case 11:
            case 12:
            case 20: {
                return vector.contains(Shorts.valueOf((short)0));
            }
            case 5:
            case 6:
            case 19:
            case 23: {
                return vector.contains(Shorts.valueOf((short)1));
            }
            case 3:
            case 4:
            case 22: {
                return vector.contains(Shorts.valueOf((short)2));
            }
            case 17: {
                return vector.contains(Shorts.valueOf((short)3));
            }
            default: {
                return true;
            }
        }
    }
    
    public static boolean isValidCipherSuiteForVersion(final int n, final ProtocolVersion protocolVersion) {
        return getMinimumVersion(n).isEqualOrEarlierVersionOf(protocolVersion.getEquivalentTLSVersion());
    }
    
    public static Vector getUsableSignatureAlgorithms(final Vector vector) {
        if (vector == null) {
            return getAllSignatureAlgorithms();
        }
        final Vector vector2 = new Vector(4);
        vector2.addElement(Shorts.valueOf((short)0));
        for (int i = 0; i < vector.size(); ++i) {
            final Short value = Shorts.valueOf(vector.elementAt(i).getSignature());
            if (!vector2.contains(value)) {
                vector2.addElement(value);
            }
        }
        return vector2;
    }
    
    static {
        EMPTY_BYTES = new byte[0];
        EMPTY_SHORTS = new short[0];
        EMPTY_INTS = new int[0];
        EMPTY_LONGS = new long[0];
        EXT_signature_algorithms = Integers.valueOf(13);
        SSL_CLIENT = new byte[] { 67, 76, 78, 84 };
        SSL_SERVER = new byte[] { 83, 82, 86, 82 };
        SSL3_CONST = genSSL3Const();
    }
}
