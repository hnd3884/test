package sun.security.provider;

import java.security.PublicKey;
import java.security.Key;
import sun.security.util.KeyUtil;
import jdk.internal.event.EventHelper;
import jdk.jfr.events.X509CertificateEvent;
import sun.security.util.Pem;
import sun.security.pkcs.ParsingException;
import java.util.Arrays;
import sun.security.pkcs.PKCS7;
import java.util.ArrayList;
import java.io.PushbackInputStream;
import java.security.cert.CRL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import sun.security.provider.certpath.X509CertPath;
import java.io.ByteArrayInputStream;
import java.security.cert.CertPath;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import sun.security.provider.certpath.X509CertificatePair;
import java.security.cert.Certificate;
import java.io.InputStream;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;
import sun.security.util.Cache;
import java.security.cert.CertificateFactorySpi;

public class X509Factory extends CertificateFactorySpi
{
    public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERT = "-----END CERTIFICATE-----";
    private static final int ENC_MAX_LENGTH = 4194304;
    private static final Cache<Object, X509CertImpl> certCache;
    private static final Cache<Object, X509CRLImpl> crlCache;
    
    @Override
    public Certificate engineGenerateCertificate(final InputStream inputStream) throws CertificateException {
        if (inputStream == null) {
            X509Factory.certCache.clear();
            X509CertificatePair.clearCache();
            throw new CertificateException("Missing input stream");
        }
        try {
            final byte[] oneBlock = readOneBlock(inputStream);
            if (oneBlock == null) {
                throw new IOException("Empty input");
            }
            final X509CertImpl x509CertImpl = getFromCache(X509Factory.certCache, oneBlock);
            if (x509CertImpl != null) {
                return x509CertImpl;
            }
            final X509CertImpl x509CertImpl2 = new X509CertImpl(oneBlock);
            addToCache(X509Factory.certCache, x509CertImpl2.getEncodedInternal(), x509CertImpl2);
            this.commitEvent(x509CertImpl2);
            return x509CertImpl2;
        }
        catch (final IOException ex) {
            throw new CertificateException("Could not parse certificate: " + ex.toString(), ex);
        }
    }
    
    private static int readFully(final InputStream inputStream, final ByteArrayOutputStream byteArrayOutputStream, int i) throws IOException {
        int n = 0;
        final byte[] array = new byte[2048];
        while (i > 0) {
            final int read = inputStream.read(array, 0, (i < 2048) ? i : 2048);
            if (read <= 0) {
                break;
            }
            byteArrayOutputStream.write(array, 0, read);
            n += read;
            i -= read;
        }
        return n;
    }
    
    public static synchronized X509CertImpl intern(final X509Certificate x509Certificate) throws CertificateException {
        if (x509Certificate == null) {
            return null;
        }
        final boolean b = x509Certificate instanceof X509CertImpl;
        byte[] array;
        if (b) {
            array = ((X509CertImpl)x509Certificate).getEncodedInternal();
        }
        else {
            array = x509Certificate.getEncoded();
        }
        final X509CertImpl x509CertImpl = getFromCache(X509Factory.certCache, array);
        if (x509CertImpl != null) {
            return x509CertImpl;
        }
        X509Certificate x509Certificate2;
        if (b) {
            x509Certificate2 = x509Certificate;
        }
        else {
            x509Certificate2 = new X509CertImpl(array);
            array = ((X509CertImpl)x509Certificate2).getEncodedInternal();
        }
        addToCache(X509Factory.certCache, array, (X509CertImpl)x509Certificate2);
        return (X509CertImpl)x509Certificate2;
    }
    
    public static synchronized X509CRLImpl intern(final X509CRL x509CRL) throws CRLException {
        if (x509CRL == null) {
            return null;
        }
        final boolean b = x509CRL instanceof X509CRLImpl;
        byte[] array;
        if (b) {
            array = ((X509CRLImpl)x509CRL).getEncodedInternal();
        }
        else {
            array = x509CRL.getEncoded();
        }
        final X509CRLImpl x509CRLImpl = getFromCache(X509Factory.crlCache, array);
        if (x509CRLImpl != null) {
            return x509CRLImpl;
        }
        X509CRL x509CRL2;
        if (b) {
            x509CRL2 = x509CRL;
        }
        else {
            x509CRL2 = new X509CRLImpl(array);
            array = ((X509CRLImpl)x509CRL2).getEncodedInternal();
        }
        addToCache(X509Factory.crlCache, array, (X509CRLImpl)x509CRL2);
        return (X509CRLImpl)x509CRL2;
    }
    
    private static synchronized <K, V> V getFromCache(final Cache<K, V> cache, final byte[] array) {
        return cache.get(new Cache.EqualByteArray(array));
    }
    
    private static synchronized <V> void addToCache(final Cache<Object, V> cache, final byte[] array, final V v) {
        if (array.length > 4194304) {
            return;
        }
        cache.put(new Cache.EqualByteArray(array), v);
    }
    
    @Override
    public CertPath engineGenerateCertPath(final InputStream inputStream) throws CertificateException {
        if (inputStream == null) {
            throw new CertificateException("Missing input stream");
        }
        try {
            final byte[] oneBlock = readOneBlock(inputStream);
            if (oneBlock != null) {
                return new X509CertPath(new ByteArrayInputStream(oneBlock));
            }
            throw new IOException("Empty input");
        }
        catch (final IOException ex) {
            throw new CertificateException(ex.getMessage());
        }
    }
    
    @Override
    public CertPath engineGenerateCertPath(final InputStream inputStream, final String s) throws CertificateException {
        if (inputStream == null) {
            throw new CertificateException("Missing input stream");
        }
        try {
            final byte[] oneBlock = readOneBlock(inputStream);
            if (oneBlock != null) {
                return new X509CertPath(new ByteArrayInputStream(oneBlock), s);
            }
            throw new IOException("Empty input");
        }
        catch (final IOException ex) {
            throw new CertificateException(ex.getMessage());
        }
    }
    
    @Override
    public CertPath engineGenerateCertPath(final List<? extends Certificate> list) throws CertificateException {
        return new X509CertPath(list);
    }
    
    @Override
    public Iterator<String> engineGetCertPathEncodings() {
        return X509CertPath.getEncodingsStatic();
    }
    
    @Override
    public Collection<? extends Certificate> engineGenerateCertificates(final InputStream inputStream) throws CertificateException {
        if (inputStream == null) {
            throw new CertificateException("Missing input stream");
        }
        try {
            return this.parseX509orPKCS7Cert(inputStream);
        }
        catch (final IOException ex) {
            throw new CertificateException(ex);
        }
    }
    
    @Override
    public CRL engineGenerateCRL(final InputStream inputStream) throws CRLException {
        if (inputStream == null) {
            X509Factory.crlCache.clear();
            throw new CRLException("Missing input stream");
        }
        try {
            final byte[] oneBlock = readOneBlock(inputStream);
            if (oneBlock == null) {
                throw new IOException("Empty input");
            }
            final X509CRLImpl x509CRLImpl = getFromCache(X509Factory.crlCache, oneBlock);
            if (x509CRLImpl != null) {
                return x509CRLImpl;
            }
            final X509CRLImpl x509CRLImpl2 = new X509CRLImpl(oneBlock);
            addToCache(X509Factory.crlCache, x509CRLImpl2.getEncodedInternal(), x509CRLImpl2);
            return x509CRLImpl2;
        }
        catch (final IOException ex) {
            throw new CRLException(ex.getMessage());
        }
    }
    
    @Override
    public Collection<? extends CRL> engineGenerateCRLs(final InputStream inputStream) throws CRLException {
        if (inputStream == null) {
            throw new CRLException("Missing input stream");
        }
        try {
            return this.parseX509orPKCS7CRL(inputStream);
        }
        catch (final IOException ex) {
            throw new CRLException(ex.getMessage());
        }
    }
    
    private Collection<? extends Certificate> parseX509orPKCS7Cert(final InputStream inputStream) throws CertificateException, IOException {
        final PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
        final ArrayList list = new ArrayList();
        final int read = pushbackInputStream.read();
        if (read == -1) {
            return new ArrayList<Certificate>(0);
        }
        pushbackInputStream.unread(read);
        byte[] array = readOneBlock(pushbackInputStream);
        if (array == null) {
            throw new CertificateException("No certificate data found");
        }
        try {
            final X509Certificate[] certificates = new PKCS7(array).getCertificates();
            if (certificates != null) {
                return Arrays.asList(certificates);
            }
            return new ArrayList<Certificate>(0);
        }
        catch (final ParsingException ex) {
            while (array != null) {
                list.add(new X509CertImpl(array));
                array = readOneBlock(pushbackInputStream);
            }
            return list;
        }
    }
    
    private Collection<? extends CRL> parseX509orPKCS7CRL(final InputStream inputStream) throws CRLException, IOException {
        final PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
        final ArrayList list = new ArrayList();
        final int read = pushbackInputStream.read();
        if (read == -1) {
            return new ArrayList<CRL>(0);
        }
        pushbackInputStream.unread(read);
        byte[] array = readOneBlock(pushbackInputStream);
        if (array == null) {
            throw new CRLException("No CRL data found");
        }
        try {
            final X509CRL[] crLs = new PKCS7(array).getCRLs();
            if (crLs != null) {
                return Arrays.asList(crLs);
            }
            return new ArrayList<CRL>(0);
        }
        catch (final ParsingException ex) {
            while (array != null) {
                list.add(new X509CRLImpl(array));
                array = readOneBlock(pushbackInputStream);
            }
            return list;
        }
    }
    
    private static byte[] readOneBlock(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        if (read == -1) {
            return null;
        }
        if (read == 48) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
            byteArrayOutputStream.write(read);
            readBERInternal(inputStream, byteArrayOutputStream, read);
            return byteArrayOutputStream.toByteArray();
        }
        char[] copy = new char[2048];
        int n = 0;
        int n2 = (read == 45) ? 1 : 0;
        int n3 = (read == 45) ? -1 : read;
        while (true) {
            final int read2 = inputStream.read();
            if (read2 == -1) {
                return null;
            }
            if (read2 == 45) {
                ++n2;
            }
            else {
                n2 = 0;
                n3 = read2;
            }
            if (n2 != 5 || (n3 != -1 && n3 != 13 && n3 != 10)) {
                continue;
            }
            final StringBuilder sb = new StringBuilder("-----");
            int n4;
            while (true) {
                final int read3 = inputStream.read();
                if (read3 == -1) {
                    throw new IOException("Incomplete data");
                }
                if (read3 == 10) {
                    n4 = 10;
                    break;
                }
                if (read3 == 13) {
                    final int read4 = inputStream.read();
                    if (read4 == -1) {
                        throw new IOException("Incomplete data");
                    }
                    if (read4 == 10) {
                        n4 = 10;
                        break;
                    }
                    n4 = 13;
                    copy[n++] = (char)read4;
                    break;
                }
                else {
                    sb.append((char)read3);
                }
            }
            while (true) {
                final int read5 = inputStream.read();
                if (read5 == -1) {
                    throw new IOException("Incomplete data");
                }
                if (read5 == 45) {
                    final StringBuilder sb2 = new StringBuilder("-");
                    while (true) {
                        final int read6 = inputStream.read();
                        if (read6 == -1 || read6 == n4 || read6 == 10) {
                            break;
                        }
                        if (read6 == 13) {
                            continue;
                        }
                        sb2.append((char)read6);
                    }
                    checkHeaderFooter(sb.toString(), sb2.toString());
                    return Pem.decode(new String(copy, 0, n));
                }
                copy[n++] = (char)read5;
                if (n < copy.length) {
                    continue;
                }
                copy = Arrays.copyOf(copy, copy.length + 1024);
            }
        }
    }
    
    private static void checkHeaderFooter(final String s, final String s2) throws IOException {
        if (s.length() < 16 || !s.startsWith("-----BEGIN ") || !s.endsWith("-----")) {
            throw new IOException("Illegal header: " + s);
        }
        if (s2.length() < 14 || !s2.startsWith("-----END ") || !s2.endsWith("-----")) {
            throw new IOException("Illegal footer: " + s2);
        }
        if (!s.substring(11, s.length() - 5).equals(s2.substring(9, s2.length() - 5))) {
            throw new IOException("Header and footer do not match: " + s + " " + s2);
        }
    }
    
    private static int readBERInternal(final InputStream inputStream, final ByteArrayOutputStream byteArrayOutputStream, int read) throws IOException {
        if (read == -1) {
            read = inputStream.read();
            if (read == -1) {
                throw new IOException("BER/DER tag info absent");
            }
            if ((read & 0x1F) == 0x1F) {
                throw new IOException("Multi octets tag not supported");
            }
            byteArrayOutputStream.write(read);
        }
        final int read2 = inputStream.read();
        if (read2 == -1) {
            throw new IOException("BER/DER length info absent");
        }
        byteArrayOutputStream.write(read2);
        if (read2 == 128) {
            if ((read & 0x20) != 0x20) {
                throw new IOException("Non constructed encoding must have definite length");
            }
            while (readBERInternal(inputStream, byteArrayOutputStream, -1) != 0) {}
        }
        else {
            int read3;
            if (read2 < 128) {
                read3 = read2;
            }
            else if (read2 == 129) {
                read3 = inputStream.read();
                if (read3 == -1) {
                    throw new IOException("Incomplete BER/DER length info");
                }
                byteArrayOutputStream.write(read3);
            }
            else if (read2 == 130) {
                final int read4 = inputStream.read();
                final int read5 = inputStream.read();
                if (read5 == -1) {
                    throw new IOException("Incomplete BER/DER length info");
                }
                byteArrayOutputStream.write(read4);
                byteArrayOutputStream.write(read5);
                read3 = (read4 << 8 | read5);
            }
            else if (read2 == 131) {
                final int read6 = inputStream.read();
                final int read7 = inputStream.read();
                final int read8 = inputStream.read();
                if (read8 == -1) {
                    throw new IOException("Incomplete BER/DER length info");
                }
                byteArrayOutputStream.write(read6);
                byteArrayOutputStream.write(read7);
                byteArrayOutputStream.write(read8);
                read3 = (read6 << 16 | read7 << 8 | read8);
            }
            else {
                if (read2 != 132) {
                    throw new IOException("Invalid BER/DER data (too huge?)");
                }
                final int read9 = inputStream.read();
                final int read10 = inputStream.read();
                final int read11 = inputStream.read();
                final int read12 = inputStream.read();
                if (read12 == -1) {
                    throw new IOException("Incomplete BER/DER length info");
                }
                if (read9 > 127) {
                    throw new IOException("Invalid BER/DER data (a little huge?)");
                }
                byteArrayOutputStream.write(read9);
                byteArrayOutputStream.write(read10);
                byteArrayOutputStream.write(read11);
                byteArrayOutputStream.write(read12);
                read3 = (read9 << 24 | read10 << 16 | read11 << 8 | read12);
            }
            if (readFully(inputStream, byteArrayOutputStream, read3) != read3) {
                throw new IOException("Incomplete BER/DER data");
            }
        }
        return read;
    }
    
    private void commitEvent(final X509CertImpl x509CertImpl) {
        final X509CertificateEvent x509CertificateEvent = new X509CertificateEvent();
        if (x509CertificateEvent.shouldCommit() || EventHelper.isLoggingSecurity()) {
            final PublicKey publicKey = x509CertImpl.getPublicKey();
            final String sigAlgName = x509CertImpl.getSigAlgName();
            final String string = x509CertImpl.getSerialNumber().toString(16);
            final String name = x509CertImpl.getSubjectDN().getName();
            final String name2 = x509CertImpl.getIssuerDN().getName();
            final String algorithm = publicKey.getAlgorithm();
            final int keySize = KeyUtil.getKeySize(publicKey);
            final int hashCode = x509CertImpl.hashCode();
            final long time = x509CertImpl.getNotBefore().getTime();
            final long time2 = x509CertImpl.getNotAfter().getTime();
            if (x509CertificateEvent.shouldCommit()) {
                x509CertificateEvent.algorithm = sigAlgName;
                x509CertificateEvent.serialNumber = string;
                x509CertificateEvent.subject = name;
                x509CertificateEvent.issuer = name2;
                x509CertificateEvent.keyType = algorithm;
                x509CertificateEvent.keyLength = keySize;
                x509CertificateEvent.certificateId = hashCode;
                x509CertificateEvent.validFrom = time;
                x509CertificateEvent.validUntil = time2;
                x509CertificateEvent.commit();
            }
            if (EventHelper.isLoggingSecurity()) {
                EventHelper.logX509CertificateEvent(sigAlgName, string, name, name2, algorithm, keySize, hashCode, time, time2);
            }
        }
    }
    
    static {
        certCache = Cache.newSoftMemoryCache(750);
        crlCache = Cache.newSoftMemoryCache(750);
    }
}
