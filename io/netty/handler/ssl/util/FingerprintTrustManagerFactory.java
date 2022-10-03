package io.netty.handler.ssl.util;

import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;
import java.util.Iterator;
import io.netty.util.internal.StringUtil;
import java.util.List;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.EmptyArrays;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import java.util.Arrays;
import javax.net.ssl.TrustManager;
import java.security.MessageDigest;
import io.netty.util.concurrent.FastThreadLocal;
import java.util.regex.Pattern;

public final class FingerprintTrustManagerFactory extends SimpleTrustManagerFactory
{
    private static final Pattern FINGERPRINT_PATTERN;
    private static final Pattern FINGERPRINT_STRIP_PATTERN;
    private final FastThreadLocal<MessageDigest> tlmd;
    private final TrustManager tm;
    private final byte[][] fingerprints;
    
    public static FingerprintTrustManagerFactoryBuilder builder(final String algorithm) {
        return new FingerprintTrustManagerFactoryBuilder(algorithm);
    }
    
    @Deprecated
    public FingerprintTrustManagerFactory(final Iterable<String> fingerprints) {
        this("SHA1", toFingerprintArray(fingerprints));
    }
    
    @Deprecated
    public FingerprintTrustManagerFactory(final String... fingerprints) {
        this("SHA1", toFingerprintArray(Arrays.asList(fingerprints)));
    }
    
    @Deprecated
    public FingerprintTrustManagerFactory(final byte[]... fingerprints) {
        this("SHA1", fingerprints);
    }
    
    FingerprintTrustManagerFactory(final String algorithm, final byte[][] fingerprints) {
        this.tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String s) throws CertificateException {
                this.checkTrusted("client", chain);
            }
            
            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String s) throws CertificateException {
                this.checkTrusted("server", chain);
            }
            
            private void checkTrusted(final String type, final X509Certificate[] chain) throws CertificateException {
                final X509Certificate cert = chain[0];
                final byte[] fingerprint = this.fingerprint(cert);
                boolean found = false;
                for (final byte[] allowedFingerprint : FingerprintTrustManagerFactory.this.fingerprints) {
                    if (Arrays.equals(fingerprint, allowedFingerprint)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new CertificateException(type + " certificate with unknown fingerprint: " + cert.getSubjectDN());
                }
            }
            
            private byte[] fingerprint(final X509Certificate cert) throws CertificateEncodingException {
                final MessageDigest md = FingerprintTrustManagerFactory.this.tlmd.get();
                md.reset();
                return md.digest(cert.getEncoded());
            }
            
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return EmptyArrays.EMPTY_X509_CERTIFICATES;
            }
        };
        ObjectUtil.checkNotNull(algorithm, "algorithm");
        ObjectUtil.checkNotNull(fingerprints, "fingerprints");
        if (fingerprints.length == 0) {
            throw new IllegalArgumentException("No fingerprints provided");
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(String.format("Unsupported hash algorithm: %s", algorithm), e);
        }
        final int hashLength = md.getDigestLength();
        final List<byte[]> list = new ArrayList<byte[]>(fingerprints.length);
        for (final byte[] f : fingerprints) {
            if (f == null) {
                break;
            }
            if (f.length != hashLength) {
                throw new IllegalArgumentException(String.format("malformed fingerprint (length is %d but expected %d): %s", f.length, hashLength, ByteBufUtil.hexDump(Unpooled.wrappedBuffer(f))));
            }
            list.add(f.clone());
        }
        this.tlmd = new FastThreadLocal<MessageDigest>() {
            @Override
            protected MessageDigest initialValue() {
                try {
                    return MessageDigest.getInstance(algorithm);
                }
                catch (final NoSuchAlgorithmException e) {
                    throw new IllegalArgumentException(String.format("Unsupported hash algorithm: %s", algorithm), e);
                }
            }
        };
        this.fingerprints = list.toArray(new byte[0][]);
    }
    
    static byte[][] toFingerprintArray(final Iterable<String> fingerprints) {
        ObjectUtil.checkNotNull(fingerprints, "fingerprints");
        final List<byte[]> list = new ArrayList<byte[]>();
        for (String f : fingerprints) {
            if (f == null) {
                break;
            }
            if (!FingerprintTrustManagerFactory.FINGERPRINT_PATTERN.matcher(f).matches()) {
                throw new IllegalArgumentException("malformed fingerprint: " + f);
            }
            f = FingerprintTrustManagerFactory.FINGERPRINT_STRIP_PATTERN.matcher(f).replaceAll("");
            list.add(StringUtil.decodeHexDump(f));
        }
        return list.toArray(new byte[0][]);
    }
    
    @Override
    protected void engineInit(final KeyStore keyStore) throws Exception {
    }
    
    @Override
    protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }
    
    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[] { this.tm };
    }
    
    static {
        FINGERPRINT_PATTERN = Pattern.compile("^[0-9a-fA-F:]+$");
        FINGERPRINT_STRIP_PATTERN = Pattern.compile(":");
    }
}
