package org.apache.tomcat.util.net.openssl;

import java.security.cert.CertificateException;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.juli.logging.LogFactory;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import java.util.Iterator;
import java.net.Socket;
import java.security.Principal;
import java.util.Arrays;
import java.security.PrivateKey;
import javax.net.ssl.X509KeyManager;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import org.apache.tomcat.jni.CertificateVerifier;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import org.apache.tomcat.jni.SSL;
import javax.net.ssl.SSLException;
import org.apache.tomcat.jni.SSLConf;
import org.apache.tomcat.jni.Pool;
import javax.net.ssl.X509TrustManager;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLHostConfig;
import java.security.cert.CertificateFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.net.SSLContext;

public class OpenSSLContext implements SSLContext
{
    private static final Base64 BASE64_ENCODER;
    private static final Log log;
    private static final StringManager netSm;
    private static final StringManager sm;
    private static final String defaultProtocol = "TLS";
    private static final String BEGIN_KEY = "-----BEGIN PRIVATE KEY-----\n";
    private static final Object END_KEY;
    static final CertificateFactory X509_CERT_FACTORY;
    private final SSLHostConfig sslHostConfig;
    private final SSLHostConfigCertificate certificate;
    private final List<String> negotiableProtocols;
    private final long aprPool;
    private final AtomicInteger aprPoolDestroyed;
    protected final long cctx;
    protected final long ctx;
    private OpenSSLSessionContext sessionContext;
    private X509TrustManager x509TrustManager;
    private String enabledProtocol;
    private boolean initialized;
    
    public OpenSSLContext(final SSLHostConfigCertificate certificate, final List<String> negotiableProtocols) throws SSLException {
        this.aprPoolDestroyed = new AtomicInteger(0);
        this.initialized = false;
        this.sslHostConfig = certificate.getSSLHostConfig();
        this.certificate = certificate;
        this.aprPool = Pool.create(0L);
        boolean success = false;
        try {
            final OpenSSLConf openSslConf = this.sslHostConfig.getOpenSslConf();
            Label_0126: {
                if (openSslConf != null) {
                    try {
                        if (OpenSSLContext.log.isDebugEnabled()) {
                            OpenSSLContext.log.debug((Object)OpenSSLContext.sm.getString("openssl.makeConf"));
                        }
                        this.cctx = SSLConf.make(this.aprPool, 58);
                        break Label_0126;
                    }
                    catch (final Exception e) {
                        throw new SSLException(OpenSSLContext.sm.getString("openssl.errMakeConf"), e);
                    }
                }
                this.cctx = 0L;
            }
            this.sslHostConfig.setOpenSslConfContext(this.cctx);
            int value = 0;
            for (final String protocol : this.sslHostConfig.getEnabledProtocols()) {
                if (!"SSLv2Hello".equalsIgnoreCase(protocol)) {
                    if ("SSLv2".equalsIgnoreCase(protocol)) {
                        value |= 0x1;
                    }
                    else if ("SSLv3".equalsIgnoreCase(protocol)) {
                        value |= 0x2;
                    }
                    else if ("TLSv1".equalsIgnoreCase(protocol)) {
                        value |= 0x4;
                    }
                    else if ("TLSv1.1".equalsIgnoreCase(protocol)) {
                        value |= 0x8;
                    }
                    else if ("TLSv1.2".equalsIgnoreCase(protocol)) {
                        value |= 0x10;
                    }
                    else if ("TLSv1.3".equalsIgnoreCase(protocol)) {
                        value |= 0x20;
                    }
                    else {
                        if (!"all".equalsIgnoreCase(protocol)) {
                            throw new Exception(OpenSSLContext.netSm.getString("endpoint.apr.invalidSslProtocol", new Object[] { protocol }));
                        }
                        value |= SSL.SSL_PROTOCOL_ALL;
                    }
                }
            }
            try {
                this.ctx = org.apache.tomcat.jni.SSLContext.make(this.aprPool, value, 1);
            }
            catch (final Exception e2) {
                throw new Exception(OpenSSLContext.netSm.getString("endpoint.apr.failSslContextMake"), e2);
            }
            this.negotiableProtocols = negotiableProtocols;
            success = true;
        }
        catch (final Exception e3) {
            throw new SSLException(OpenSSLContext.sm.getString("openssl.errorSSLCtxInit"), e3);
        }
        finally {
            if (!success) {
                this.destroy();
            }
        }
    }
    
    public String getEnabledProtocol() {
        return this.enabledProtocol;
    }
    
    public void setEnabledProtocol(final String protocol) {
        this.enabledProtocol = ((protocol == null) ? "TLS" : protocol);
    }
    
    @Override
    public synchronized void destroy() {
        if (this.aprPoolDestroyed.compareAndSet(0, 1)) {
            if (this.ctx != 0L) {
                org.apache.tomcat.jni.SSLContext.free(this.ctx);
            }
            if (this.cctx != 0L) {
                SSLConf.free(this.cctx);
            }
            if (this.aprPool != 0L) {
                Pool.destroy(this.aprPool);
            }
        }
    }
    
    @Override
    public synchronized void init(final KeyManager[] kms, final TrustManager[] tms, final SecureRandom sr) {
        if (this.initialized) {
            OpenSSLContext.log.warn((Object)OpenSSLContext.sm.getString("openssl.doubleInit"));
            return;
        }
        try {
            if (this.sslHostConfig.getInsecureRenegotiation()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 262144);
            }
            else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 262144);
            }
            final String honorCipherOrderStr = this.sslHostConfig.getHonorCipherOrder();
            if (honorCipherOrderStr != null) {
                if (Boolean.parseBoolean(honorCipherOrderStr)) {
                    org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 4194304);
                }
                else {
                    org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 4194304);
                }
            }
            if (this.sslHostConfig.getDisableCompression()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 131072);
            }
            else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 131072);
            }
            if (this.sslHostConfig.getDisableSessionTickets()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 16384);
            }
            else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 16384);
            }
            org.apache.tomcat.jni.SSLContext.setCipherSuite(this.ctx, this.sslHostConfig.getCiphers());
            if (this.certificate.getCertificateFile() == null) {
                this.certificate.setCertificateKeyManager(OpenSSLUtil.chooseKeyManager(kms));
            }
            this.addCertificate(this.certificate);
            int value = 0;
            switch (this.sslHostConfig.getCertificateVerification()) {
                case NONE: {
                    value = 0;
                    break;
                }
                case OPTIONAL: {
                    value = 1;
                    break;
                }
                case OPTIONAL_NO_CA: {
                    value = 3;
                    break;
                }
                case REQUIRED: {
                    value = 2;
                    break;
                }
            }
            org.apache.tomcat.jni.SSLContext.setVerify(this.ctx, value, this.sslHostConfig.getCertificateVerificationDepth());
            if (tms != null) {
                this.x509TrustManager = chooseTrustManager(tms);
                org.apache.tomcat.jni.SSLContext.setCertVerifyCallback(this.ctx, (CertificateVerifier)new CertificateVerifier() {
                    public boolean verify(final long ssl, final byte[][] chain, final String auth) {
                        final X509Certificate[] peerCerts = certificates(chain);
                        try {
                            OpenSSLContext.this.x509TrustManager.checkClientTrusted(peerCerts, auth);
                            return true;
                        }
                        catch (final Exception e) {
                            OpenSSLContext.log.debug((Object)OpenSSLContext.sm.getString("openssl.certificateVerificationFailed"), (Throwable)e);
                            return false;
                        }
                    }
                });
                for (final X509Certificate caCert : this.x509TrustManager.getAcceptedIssuers()) {
                    org.apache.tomcat.jni.SSLContext.addClientCACertificateRaw(this.ctx, caCert.getEncoded());
                    if (OpenSSLContext.log.isDebugEnabled()) {
                        OpenSSLContext.log.debug((Object)OpenSSLContext.sm.getString("openssl.addedClientCaCert", new Object[] { caCert.toString() }));
                    }
                }
            }
            else {
                org.apache.tomcat.jni.SSLContext.setCACertificate(this.ctx, SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificateFile()), SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificatePath()));
            }
            if (this.negotiableProtocols != null && this.negotiableProtocols.size() > 0) {
                final List<String> protocols = new ArrayList<String>(this.negotiableProtocols);
                protocols.add("http/1.1");
                final String[] protocolsArray = protocols.toArray(new String[0]);
                org.apache.tomcat.jni.SSLContext.setAlpnProtos(this.ctx, protocolsArray, 0);
                org.apache.tomcat.jni.SSLContext.setNpnProtos(this.ctx, protocolsArray, 0);
            }
            final OpenSSLConf openSslConf = this.sslHostConfig.getOpenSslConf();
            if (openSslConf != null && this.cctx != 0L) {
                if (OpenSSLContext.log.isDebugEnabled()) {
                    OpenSSLContext.log.debug((Object)OpenSSLContext.sm.getString("openssl.checkConf"));
                }
                try {
                    if (!openSslConf.check(this.cctx)) {
                        OpenSSLContext.log.error((Object)OpenSSLContext.sm.getString("openssl.errCheckConf"));
                        throw new Exception(OpenSSLContext.sm.getString("openssl.errCheckConf"));
                    }
                }
                catch (final Exception e) {
                    throw new Exception(OpenSSLContext.sm.getString("openssl.errCheckConf"), e);
                }
                if (OpenSSLContext.log.isDebugEnabled()) {
                    OpenSSLContext.log.debug((Object)OpenSSLContext.sm.getString("openssl.applyConf"));
                }
                try {
                    if (!openSslConf.apply(this.cctx, this.ctx)) {
                        OpenSSLContext.log.error((Object)OpenSSLContext.sm.getString("openssl.errApplyConf"));
                        throw new SSLException(OpenSSLContext.sm.getString("openssl.errApplyConf"));
                    }
                }
                catch (final Exception e) {
                    throw new SSLException(OpenSSLContext.sm.getString("openssl.errApplyConf"), e);
                }
                final int opts = org.apache.tomcat.jni.SSLContext.getOptions(this.ctx);
                final List<String> enabled = new ArrayList<String>();
                enabled.add("SSLv2Hello");
                if ((opts & 0x4000000) == 0x0) {
                    enabled.add("TLSv1");
                }
                if ((opts & 0x10000000) == 0x0) {
                    enabled.add("TLSv1.1");
                }
                if ((opts & 0x8000000) == 0x0) {
                    enabled.add("TLSv1.2");
                }
                if ((opts & 0x1000000) == 0x0) {
                    enabled.add("SSLv2");
                }
                if ((opts & 0x2000000) == 0x0) {
                    enabled.add("SSLv3");
                }
                this.sslHostConfig.setEnabledProtocols(enabled.toArray(new String[0]));
                this.sslHostConfig.setEnabledCiphers(org.apache.tomcat.jni.SSLContext.getCiphers(this.ctx));
            }
            (this.sessionContext = new OpenSSLSessionContext(this)).setSessionIdContext(org.apache.tomcat.jni.SSLContext.DEFAULT_SESSION_ID_CONTEXT);
            this.sslHostConfig.setOpenSslContext(this.ctx);
            this.initialized = true;
        }
        catch (final Exception e2) {
            OpenSSLContext.log.warn((Object)OpenSSLContext.sm.getString("openssl.errorSSLCtxInit"), (Throwable)e2);
            this.destroy();
        }
    }
    
    public void addCertificate(final SSLHostConfigCertificate certificate) throws Exception {
        if (certificate.getCertificateFile() != null) {
            org.apache.tomcat.jni.SSLContext.setCertificate(this.ctx, SSLHostConfig.adjustRelativePath(certificate.getCertificateFile()), SSLHostConfig.adjustRelativePath(certificate.getCertificateKeyFile()), certificate.getCertificateKeyPassword(), getCertificateIndex(certificate));
            org.apache.tomcat.jni.SSLContext.setCertificateChainFile(this.ctx, SSLHostConfig.adjustRelativePath(certificate.getCertificateChainFile()), false);
            org.apache.tomcat.jni.SSLContext.setCARevocation(this.ctx, SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListFile()), SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListPath()));
        }
        else {
            String alias = certificate.getCertificateKeyAlias();
            final X509KeyManager x509KeyManager = certificate.getCertificateKeyManager();
            if (alias == null) {
                alias = "tomcat";
            }
            X509Certificate[] chain = x509KeyManager.getCertificateChain(alias);
            if (chain == null) {
                alias = findAlias(x509KeyManager, certificate);
                chain = x509KeyManager.getCertificateChain(alias);
            }
            final PrivateKey key = x509KeyManager.getPrivateKey(alias);
            final StringBuilder sb = new StringBuilder("-----BEGIN PRIVATE KEY-----\n");
            String encoded = OpenSSLContext.BASE64_ENCODER.encodeToString(key.getEncoded());
            if (encoded.endsWith("\n")) {
                encoded = encoded.substring(0, encoded.length() - 1);
            }
            sb.append(encoded);
            sb.append(OpenSSLContext.END_KEY);
            org.apache.tomcat.jni.SSLContext.setCertificateRaw(this.ctx, chain[0].getEncoded(), sb.toString().getBytes(StandardCharsets.US_ASCII), getCertificateIndex(certificate));
            for (int i = 1; i < chain.length; ++i) {
                org.apache.tomcat.jni.SSLContext.addChainCertificateRaw(this.ctx, chain[i].getEncoded());
            }
        }
    }
    
    private static int getCertificateIndex(final SSLHostConfigCertificate certificate) {
        int result;
        if (certificate.getType() == SSLHostConfigCertificate.Type.RSA || certificate.getType() == SSLHostConfigCertificate.Type.UNDEFINED) {
            result = 0;
        }
        else if (certificate.getType() == SSLHostConfigCertificate.Type.EC) {
            result = 3;
        }
        else if (certificate.getType() == SSLHostConfigCertificate.Type.DSA) {
            result = 1;
        }
        else {
            result = 4;
        }
        return result;
    }
    
    private static String findAlias(final X509KeyManager keyManager, final SSLHostConfigCertificate certificate) {
        final SSLHostConfigCertificate.Type type = certificate.getType();
        String result = null;
        final List<SSLHostConfigCertificate.Type> candidateTypes = new ArrayList<SSLHostConfigCertificate.Type>();
        if (SSLHostConfigCertificate.Type.UNDEFINED.equals(type)) {
            candidateTypes.addAll(Arrays.asList(SSLHostConfigCertificate.Type.values()));
            candidateTypes.remove(SSLHostConfigCertificate.Type.UNDEFINED);
        }
        else {
            candidateTypes.add(type);
        }
        for (Iterator<SSLHostConfigCertificate.Type> iter = candidateTypes.iterator(); result == null && iter.hasNext(); result = keyManager.chooseServerAlias(iter.next().toString(), null, null)) {}
        return result;
    }
    
    private static X509TrustManager chooseTrustManager(final TrustManager[] managers) {
        for (final TrustManager m : managers) {
            if (m instanceof X509TrustManager) {
                return (X509TrustManager)m;
            }
        }
        throw new IllegalStateException(OpenSSLContext.sm.getString("openssl.trustManagerMissing"));
    }
    
    private static X509Certificate[] certificates(final byte[][] chain) {
        final X509Certificate[] peerCerts = new X509Certificate[chain.length];
        for (int i = 0; i < peerCerts.length; ++i) {
            peerCerts[i] = new OpenSSLX509Certificate(chain[i]);
        }
        return peerCerts;
    }
    
    long getSSLContextID() {
        return this.ctx;
    }
    
    @Override
    public SSLSessionContext getServerSessionContext() {
        return this.sessionContext;
    }
    
    @Override
    public SSLEngine createSSLEngine() {
        return new OpenSSLEngine(this.ctx, "TLS", false, this.sessionContext, this.negotiableProtocols != null && this.negotiableProtocols.size() > 0, this.initialized, this.sslHostConfig.getCertificateVerificationDepth(), this.sslHostConfig.getCertificateVerification() == SSLHostConfig.CertificateVerification.OPTIONAL_NO_CA);
    }
    
    @Override
    public SSLServerSocketFactory getServerSocketFactory() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SSLParameters getSupportedSSLParameters() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] chain = null;
        final X509KeyManager x509KeyManager = this.certificate.getCertificateKeyManager();
        if (x509KeyManager != null) {
            if (alias == null) {
                alias = "tomcat";
            }
            chain = x509KeyManager.getCertificateChain(alias);
            if (chain == null) {
                alias = findAlias(x509KeyManager, this.certificate);
                chain = x509KeyManager.getCertificateChain(alias);
            }
        }
        return chain;
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] acceptedCerts = null;
        if (this.x509TrustManager != null) {
            acceptedCerts = this.x509TrustManager.getAcceptedIssuers();
        }
        return acceptedCerts;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.destroy();
        }
        finally {
            super.finalize();
        }
    }
    
    static {
        BASE64_ENCODER = new Base64(64, new byte[] { 10 });
        log = LogFactory.getLog((Class)OpenSSLContext.class);
        netSm = StringManager.getManager((Class)AbstractEndpoint.class);
        sm = StringManager.getManager((Class)OpenSSLContext.class);
        END_KEY = "\n-----END PRIVATE KEY-----";
        try {
            X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException e) {
            throw new IllegalStateException(OpenSSLContext.sm.getString("openssl.X509FactoryError"), e);
        }
    }
}
