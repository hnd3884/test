package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.crypto.KeyGenerator;
import java.security.DigestException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.internal.spec.TlsPrfParameterSpec;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;
import java.security.MessageDigestSpi;
import java.lang.reflect.AccessibleObject;
import java.security.MessageDigest;
import javax.crypto.SecretKey;
import java.util.Map;
import java.lang.reflect.Field;
import javax.security.auth.x500.X500Principal;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.ECPoint;
import java.security.spec.ECParameterSpec;
import java.security.interfaces.ECPublicKey;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLHandshakeException;
import sun.security.util.KeyUtil;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.GeneralSecurityException;
import java.security.spec.RSAPublicKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.Signature;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.util.Iterator;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLProtocolException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.util.Collection;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.security.SecureRandom;
import java.io.PrintStream;
import java.io.IOException;
import javax.net.ssl.SSLException;
import java.util.Arrays;
import java.math.BigInteger;

public abstract class HandshakeMessage
{
    static final byte ht_hello_request = 0;
    static final byte ht_client_hello = 1;
    static final byte ht_server_hello = 2;
    static final byte ht_certificate = 11;
    static final byte ht_server_key_exchange = 12;
    static final byte ht_certificate_request = 13;
    static final byte ht_server_hello_done = 14;
    static final byte ht_certificate_verify = 15;
    static final byte ht_client_key_exchange = 16;
    static final byte ht_finished = 20;
    static final byte ht_not_applicable = -1;
    public static final Debug debug;
    static final byte[] MD5_pad1;
    static final byte[] MD5_pad2;
    static final byte[] SHA_pad1;
    static final byte[] SHA_pad2;
    
    HandshakeMessage() {
    }
    
    static byte[] toByteArray(final BigInteger bi) {
        byte[] b = bi.toByteArray();
        if (b.length > 1 && b[0] == 0) {
            final int n = b.length - 1;
            final byte[] newarray = new byte[n];
            System.arraycopy(b, 1, newarray, 0, n);
            b = newarray;
        }
        return b;
    }
    
    private static byte[] genPad(final int b, final int count) {
        final byte[] padding = new byte[count];
        Arrays.fill(padding, (byte)b);
        return padding;
    }
    
    final void write(final HandshakeOutStream s) throws IOException {
        final int len = this.messageLength();
        if (len >= 16777216) {
            throw new SSLException("Handshake message too big, type = " + this.messageType() + ", len = " + len);
        }
        s.write(this.messageType());
        s.putInt24(len);
        this.send(s);
    }
    
    abstract int messageType();
    
    abstract int messageLength();
    
    abstract void send(final HandshakeOutStream p0) throws IOException;
    
    abstract void print(final PrintStream p0) throws IOException;
    
    static {
        debug = Debug.getInstance("ssl");
        MD5_pad1 = genPad(54, 48);
        MD5_pad2 = genPad(92, 48);
        SHA_pad1 = genPad(54, 40);
        SHA_pad2 = genPad(92, 40);
    }
    
    static final class HelloRequest extends HandshakeMessage
    {
        @Override
        int messageType() {
            return 0;
        }
        
        HelloRequest() {
        }
        
        HelloRequest(final HandshakeInStream in) throws IOException {
        }
        
        @Override
        int messageLength() {
            return 0;
        }
        
        @Override
        void send(final HandshakeOutStream out) throws IOException {
        }
        
        @Override
        void print(final PrintStream out) throws IOException {
            out.println("*** HelloRequest (empty)");
        }
    }
    
    static final class ClientHello extends HandshakeMessage
    {
        ProtocolVersion protocolVersion;
        RandomCookie clnt_random;
        SessionId sessionId;
        private CipherSuiteList cipherSuites;
        byte[] compression_methods;
        HelloExtensions extensions;
        private static final byte[] NULL_COMPRESSION;
        
        ClientHello(final SecureRandom generator, final ProtocolVersion protocolVersion, final SessionId sessionId, final CipherSuiteList cipherSuites) {
            this.extensions = new HelloExtensions();
            this.protocolVersion = protocolVersion;
            this.sessionId = sessionId;
            this.cipherSuites = cipherSuites;
            this.clnt_random = new RandomCookie(generator);
            this.compression_methods = ClientHello.NULL_COMPRESSION;
        }
        
        ClientHello(final HandshakeInStream s, final int messageLength) throws IOException {
            this.extensions = new HelloExtensions();
            this.protocolVersion = ProtocolVersion.valueOf(s.getInt8(), s.getInt8());
            this.clnt_random = new RandomCookie(s);
            (this.sessionId = new SessionId(s.getBytes8())).checkLength(this.protocolVersion);
            this.cipherSuites = new CipherSuiteList(s);
            this.compression_methods = s.getBytes8();
            if (this.messageLength() != messageLength) {
                this.extensions = new HelloExtensions(s);
            }
        }
        
        CipherSuiteList getCipherSuites() {
            return this.cipherSuites;
        }
        
        void addRenegotiationInfoExtension(final byte[] clientVerifyData) {
            final HelloExtension renegotiationInfo = new RenegotiationInfoExtension(clientVerifyData, new byte[0]);
            this.extensions.add(renegotiationInfo);
        }
        
        void addSNIExtension(final List<SNIServerName> serverNames) {
            try {
                this.extensions.add(new ServerNameExtension(serverNames));
            }
            catch (final IOException ex) {}
        }
        
        void addSignatureAlgorithmsExtension(final Collection<SignatureAndHashAlgorithm> algorithms) {
            final HelloExtension signatureAlgorithm = new SignatureAlgorithmsExtension(algorithms);
            this.extensions.add(signatureAlgorithm);
        }
        
        void addExtendedMasterSecretExtension() {
            this.extensions.add(new ExtendedMasterSecretExtension());
        }
        
        void addALPNExtension(final String[] applicationProtocols) throws SSLException {
            this.extensions.add(new ALPNExtension(applicationProtocols));
        }
        
        @Override
        int messageType() {
            return 1;
        }
        
        @Override
        int messageLength() {
            return 38 + this.sessionId.length() + this.cipherSuites.size() * 2 + this.compression_methods.length + this.extensions.length();
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
            s.putInt8(this.protocolVersion.major);
            s.putInt8(this.protocolVersion.minor);
            this.clnt_random.send(s);
            s.putBytes8(this.sessionId.getId());
            this.cipherSuites.send(s);
            s.putBytes8(this.compression_methods);
            this.extensions.send(s);
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** ClientHello, " + this.protocolVersion);
            if (ClientHello.debug != null && Debug.isOn("verbose")) {
                s.print("RandomCookie:  ");
                this.clnt_random.print(s);
                s.print("Session ID:  ");
                s.println(this.sessionId);
                s.println("Cipher Suites: " + this.cipherSuites);
                Debug.println(s, "Compression Methods", this.compression_methods);
                this.extensions.print(s);
                s.println("***");
            }
        }
        
        static {
            NULL_COMPRESSION = new byte[] { 0 };
        }
    }
    
    static final class ServerHello extends HandshakeMessage
    {
        ProtocolVersion protocolVersion;
        RandomCookie svr_random;
        SessionId sessionId;
        CipherSuite cipherSuite;
        byte compression_method;
        HelloExtensions extensions;
        
        @Override
        int messageType() {
            return 2;
        }
        
        ServerHello() {
            this.extensions = new HelloExtensions();
        }
        
        ServerHello(final HandshakeInStream input, final int messageLength) throws IOException {
            this.extensions = new HelloExtensions();
            this.protocolVersion = ProtocolVersion.valueOf(input.getInt8(), input.getInt8());
            this.svr_random = new RandomCookie(input);
            (this.sessionId = new SessionId(input.getBytes8())).checkLength(this.protocolVersion);
            this.cipherSuite = CipherSuite.valueOf(input.getInt8(), input.getInt8());
            this.compression_method = (byte)input.getInt8();
            if (this.messageLength() != messageLength) {
                this.extensions = new HelloExtensions(input);
            }
        }
        
        @Override
        int messageLength() {
            return 38 + this.sessionId.length() + this.extensions.length();
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
            s.putInt8(this.protocolVersion.major);
            s.putInt8(this.protocolVersion.minor);
            this.svr_random.send(s);
            s.putBytes8(this.sessionId.getId());
            s.putInt8(this.cipherSuite.id >> 8);
            s.putInt8(this.cipherSuite.id & 0xFF);
            s.putInt8(this.compression_method);
            this.extensions.send(s);
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** ServerHello, " + this.protocolVersion);
            if (ServerHello.debug != null && Debug.isOn("verbose")) {
                s.print("RandomCookie:  ");
                this.svr_random.print(s);
                s.print("Session ID:  ");
                s.println(this.sessionId);
                s.println("Cipher Suite: " + this.cipherSuite);
                s.println("Compression Method: " + this.compression_method);
                this.extensions.print(s);
                s.println("***");
            }
        }
    }
    
    static final class CertificateMsg extends HandshakeMessage
    {
        private X509Certificate[] chain;
        private List<byte[]> encodedChain;
        private int messageLength;
        static final int maxCertificateChainLength;
        
        @Override
        int messageType() {
            return 11;
        }
        
        CertificateMsg(final X509Certificate[] certs) {
            this.chain = certs;
        }
        
        CertificateMsg(final HandshakeInStream input) throws IOException {
            int chainLen = input.getInt24();
            final List<Certificate> v = new ArrayList<Certificate>(4);
            CertificateFactory cf = null;
            while (chainLen > 0) {
                final byte[] cert = input.getBytes24();
                chainLen -= 3 + cert.length;
                try {
                    if (cf == null) {
                        cf = CertificateFactory.getInstance("X.509");
                    }
                    v.add(cf.generateCertificate(new ByteArrayInputStream(cert)));
                    if (v.size() > CertificateMsg.maxCertificateChainLength) {
                        throw new SSLProtocolException("The certificate chain length (" + v.size() + ") exceeds the maximum allowed length (" + CertificateMsg.maxCertificateChainLength + ")");
                    }
                    continue;
                }
                catch (final CertificateException e) {
                    throw (SSLProtocolException)new SSLProtocolException(e.getMessage()).initCause(e);
                }
            }
            this.chain = v.toArray(new X509Certificate[v.size()]);
        }
        
        @Override
        int messageLength() {
            if (this.encodedChain == null) {
                this.messageLength = 3;
                this.encodedChain = new ArrayList<byte[]>(this.chain.length);
                try {
                    for (final X509Certificate cert : this.chain) {
                        final byte[] b = cert.getEncoded();
                        this.encodedChain.add(b);
                        this.messageLength += b.length + 3;
                    }
                }
                catch (final CertificateEncodingException e) {
                    this.encodedChain = null;
                    throw new RuntimeException("Could not encode certificates", e);
                }
            }
            return this.messageLength;
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
            s.putInt24(this.messageLength() - 3);
            for (final byte[] b : this.encodedChain) {
                s.putBytes24(b);
            }
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** Certificate chain");
            if (this.chain.length == 0) {
                s.println("<Empty>");
            }
            else if (CertificateMsg.debug != null && Debug.isOn("verbose")) {
                for (int i = 0; i < this.chain.length; ++i) {
                    s.println("chain [" + i + "] = " + this.chain[i]);
                }
            }
            s.println("***");
        }
        
        X509Certificate[] getCertificateChain() {
            return this.chain.clone();
        }
        
        static {
            maxCertificateChainLength = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.maxCertificateChainLength", 10));
        }
    }
    
    abstract static class ServerKeyExchange extends HandshakeMessage
    {
        @Override
        int messageType() {
            return 12;
        }
    }
    
    static final class RSA_ServerKeyExchange extends ServerKeyExchange
    {
        private byte[] rsa_modulus;
        private byte[] rsa_exponent;
        private Signature signature;
        private byte[] signatureBytes;
        
        private void updateSignature(final byte[] clntNonce, final byte[] svrNonce) throws SignatureException {
            this.signature.update(clntNonce);
            this.signature.update(svrNonce);
            int tmp = this.rsa_modulus.length;
            this.signature.update((byte)(tmp >> 8));
            this.signature.update((byte)(tmp & 0xFF));
            this.signature.update(this.rsa_modulus);
            tmp = this.rsa_exponent.length;
            this.signature.update((byte)(tmp >> 8));
            this.signature.update((byte)(tmp & 0xFF));
            this.signature.update(this.rsa_exponent);
        }
        
        RSA_ServerKeyExchange(final PublicKey ephemeralKey, final PrivateKey privateKey, final RandomCookie clntNonce, final RandomCookie svrNonce, final SecureRandom sr) throws GeneralSecurityException {
            final RSAPublicKeySpec rsaKey = JsseJce.getRSAPublicKeySpec(ephemeralKey);
            this.rsa_modulus = HandshakeMessage.toByteArray(rsaKey.getModulus());
            this.rsa_exponent = HandshakeMessage.toByteArray(rsaKey.getPublicExponent());
            (this.signature = RSASignature.getInstance()).initSign(privateKey, sr);
            this.updateSignature(clntNonce.random_bytes, svrNonce.random_bytes);
            this.signatureBytes = this.signature.sign();
        }
        
        RSA_ServerKeyExchange(final HandshakeInStream input) throws IOException, NoSuchAlgorithmException {
            this.signature = RSASignature.getInstance();
            this.rsa_modulus = input.getBytes16();
            this.rsa_exponent = input.getBytes16();
            this.signatureBytes = input.getBytes16();
        }
        
        PublicKey getPublicKey() {
            try {
                final KeyFactory kfac = JsseJce.getKeyFactory("RSA");
                final RSAPublicKeySpec kspec = new RSAPublicKeySpec(new BigInteger(1, this.rsa_modulus), new BigInteger(1, this.rsa_exponent));
                return kfac.generatePublic(kspec);
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        boolean verify(final PublicKey certifiedKey, final RandomCookie clntNonce, final RandomCookie svrNonce) throws GeneralSecurityException {
            this.signature.initVerify(certifiedKey);
            this.updateSignature(clntNonce.random_bytes, svrNonce.random_bytes);
            return this.signature.verify(this.signatureBytes);
        }
        
        @Override
        int messageLength() {
            return 6 + this.rsa_modulus.length + this.rsa_exponent.length + this.signatureBytes.length;
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
            s.putBytes16(this.rsa_modulus);
            s.putBytes16(this.rsa_exponent);
            s.putBytes16(this.signatureBytes);
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** RSA ServerKeyExchange");
            if (RSA_ServerKeyExchange.debug != null && Debug.isOn("verbose")) {
                Debug.println(s, "RSA Modulus", this.rsa_modulus);
                Debug.println(s, "RSA Public Exponent", this.rsa_exponent);
            }
        }
    }
    
    static final class DH_ServerKeyExchange extends ServerKeyExchange
    {
        private static final boolean dhKeyExchangeFix;
        private byte[] dh_p;
        private byte[] dh_g;
        private byte[] dh_Ys;
        private byte[] signature;
        ProtocolVersion protocolVersion;
        private SignatureAndHashAlgorithm preferableSignatureAlgorithm;
        
        DH_ServerKeyExchange(final DHCrypt obj, final ProtocolVersion protocolVersion) {
            this.protocolVersion = protocolVersion;
            this.preferableSignatureAlgorithm = null;
            this.setValues(obj);
            this.signature = null;
        }
        
        DH_ServerKeyExchange(final DHCrypt obj, final PrivateKey key, final byte[] clntNonce, final byte[] svrNonce, final SecureRandom sr, final SignatureAndHashAlgorithm signAlgorithm, final ProtocolVersion protocolVersion) throws GeneralSecurityException {
            this.protocolVersion = protocolVersion;
            this.setValues(obj);
            Signature sig;
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                this.preferableSignatureAlgorithm = signAlgorithm;
                sig = JsseJce.getSignature(signAlgorithm.getAlgorithmName());
            }
            else {
                this.preferableSignatureAlgorithm = null;
                if (key.getAlgorithm().equals("DSA")) {
                    sig = JsseJce.getSignature("DSA");
                }
                else {
                    sig = RSASignature.getInstance();
                }
            }
            sig.initSign(key, sr);
            this.updateSignature(sig, clntNonce, svrNonce);
            this.signature = sig.sign();
        }
        
        DH_ServerKeyExchange(final HandshakeInStream input, final ProtocolVersion protocolVersion) throws IOException, GeneralSecurityException {
            this.protocolVersion = protocolVersion;
            this.preferableSignatureAlgorithm = null;
            this.dh_p = input.getBytes16();
            this.dh_g = input.getBytes16();
            this.dh_Ys = input.getBytes16();
            KeyUtil.validate(new DHPublicKeySpec(new BigInteger(1, this.dh_Ys), new BigInteger(1, this.dh_p), new BigInteger(1, this.dh_g)));
            this.signature = null;
        }
        
        DH_ServerKeyExchange(final HandshakeInStream input, final PublicKey publicKey, final byte[] clntNonce, final byte[] svrNonce, int messageSize, final Collection<SignatureAndHashAlgorithm> localSupportedSignAlgs, final ProtocolVersion protocolVersion) throws IOException, GeneralSecurityException {
            this.protocolVersion = protocolVersion;
            this.dh_p = input.getBytes16();
            this.dh_g = input.getBytes16();
            this.dh_Ys = input.getBytes16();
            KeyUtil.validate(new DHPublicKeySpec(new BigInteger(1, this.dh_Ys), new BigInteger(1, this.dh_p), new BigInteger(1, this.dh_g)));
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                final int hash = input.getInt8();
                final int signature = input.getInt8();
                this.preferableSignatureAlgorithm = SignatureAndHashAlgorithm.valueOf(hash, signature, 0);
                if (!localSupportedSignAlgs.contains(this.preferableSignatureAlgorithm)) {
                    throw new SSLHandshakeException("Unsupported SignatureAndHashAlgorithm in ServerKeyExchange message: " + this.preferableSignatureAlgorithm);
                }
            }
            else {
                this.preferableSignatureAlgorithm = null;
            }
            byte[] signature2;
            if (DH_ServerKeyExchange.dhKeyExchangeFix) {
                signature2 = input.getBytes16();
            }
            else {
                messageSize -= this.dh_p.length + 2;
                messageSize -= this.dh_g.length + 2;
                messageSize -= this.dh_Ys.length + 2;
                signature2 = new byte[messageSize];
                input.read(signature2);
            }
            final String algorithm = publicKey.getAlgorithm();
            Signature sig = null;
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                sig = JsseJce.getSignature(this.preferableSignatureAlgorithm.getAlgorithmName());
            }
            else {
                final String s = algorithm;
                switch (s) {
                    case "DSA": {
                        sig = JsseJce.getSignature("DSA");
                        break;
                    }
                    case "RSA": {
                        sig = RSASignature.getInstance();
                        break;
                    }
                    default: {
                        throw new SSLKeyException("neither an RSA or a DSA key: " + algorithm);
                    }
                }
            }
            sig.initVerify(publicKey);
            this.updateSignature(sig, clntNonce, svrNonce);
            if (!sig.verify(signature2)) {
                throw new SSLKeyException("Server D-H key verification failed");
            }
        }
        
        BigInteger getModulus() {
            return new BigInteger(1, this.dh_p);
        }
        
        BigInteger getBase() {
            return new BigInteger(1, this.dh_g);
        }
        
        BigInteger getServerPublicKey() {
            return new BigInteger(1, this.dh_Ys);
        }
        
        private void updateSignature(final Signature sig, final byte[] clntNonce, final byte[] svrNonce) throws SignatureException {
            sig.update(clntNonce);
            sig.update(svrNonce);
            int tmp = this.dh_p.length;
            sig.update((byte)(tmp >> 8));
            sig.update((byte)(tmp & 0xFF));
            sig.update(this.dh_p);
            tmp = this.dh_g.length;
            sig.update((byte)(tmp >> 8));
            sig.update((byte)(tmp & 0xFF));
            sig.update(this.dh_g);
            tmp = this.dh_Ys.length;
            sig.update((byte)(tmp >> 8));
            sig.update((byte)(tmp & 0xFF));
            sig.update(this.dh_Ys);
        }
        
        private void setValues(final DHCrypt obj) {
            this.dh_p = HandshakeMessage.toByteArray(obj.getModulus());
            this.dh_g = HandshakeMessage.toByteArray(obj.getBase());
            this.dh_Ys = HandshakeMessage.toByteArray(obj.getPublicKey());
        }
        
        @Override
        int messageLength() {
            int temp = 6;
            temp += this.dh_p.length;
            temp += this.dh_g.length;
            temp += this.dh_Ys.length;
            if (this.signature != null) {
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    temp += SignatureAndHashAlgorithm.sizeInRecord();
                }
                temp += this.signature.length;
                if (DH_ServerKeyExchange.dhKeyExchangeFix) {
                    temp += 2;
                }
            }
            return temp;
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
            s.putBytes16(this.dh_p);
            s.putBytes16(this.dh_g);
            s.putBytes16(this.dh_Ys);
            if (this.signature != null) {
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    s.putInt8(this.preferableSignatureAlgorithm.getHashValue());
                    s.putInt8(this.preferableSignatureAlgorithm.getSignatureValue());
                }
                if (DH_ServerKeyExchange.dhKeyExchangeFix) {
                    s.putBytes16(this.signature);
                }
                else {
                    s.write(this.signature);
                }
            }
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** Diffie-Hellman ServerKeyExchange");
            if (DH_ServerKeyExchange.debug != null && Debug.isOn("verbose")) {
                Debug.println(s, "DH Modulus", this.dh_p);
                Debug.println(s, "DH Base", this.dh_g);
                Debug.println(s, "Server DH Public Key", this.dh_Ys);
                if (this.signature == null) {
                    s.println("Anonymous");
                }
                else {
                    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                        s.println("Signature Algorithm " + this.preferableSignatureAlgorithm.getAlgorithmName());
                    }
                    s.println("Signed with a DSA or RSA public key");
                }
            }
        }
        
        static {
            dhKeyExchangeFix = Debug.getBooleanProperty("com.sun.net.ssl.dhKeyExchangeFix", true);
        }
    }
    
    static final class ECDH_ServerKeyExchange extends ServerKeyExchange
    {
        private static final int CURVE_EXPLICIT_PRIME = 1;
        private static final int CURVE_EXPLICIT_CHAR2 = 2;
        private static final int CURVE_NAMED_CURVE = 3;
        private int curveId;
        private byte[] pointBytes;
        private byte[] signatureBytes;
        private ECPublicKey publicKey;
        ProtocolVersion protocolVersion;
        private SignatureAndHashAlgorithm preferableSignatureAlgorithm;
        
        ECDH_ServerKeyExchange(final ECDHCrypt obj, final PrivateKey privateKey, final byte[] clntNonce, final byte[] svrNonce, final SecureRandom sr, final SignatureAndHashAlgorithm signAlgorithm, final ProtocolVersion protocolVersion) throws GeneralSecurityException {
            this.protocolVersion = protocolVersion;
            this.publicKey = (ECPublicKey)obj.getPublicKey();
            final ECParameterSpec params = this.publicKey.getParams();
            final ECPoint point = this.publicKey.getW();
            this.pointBytes = JsseJce.encodePoint(point, params.getCurve());
            this.curveId = EllipticCurvesExtension.getCurveIndex(params);
            if (privateKey == null) {
                return;
            }
            Signature sig;
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                this.preferableSignatureAlgorithm = signAlgorithm;
                sig = JsseJce.getSignature(signAlgorithm.getAlgorithmName());
            }
            else {
                sig = getSignature(privateKey.getAlgorithm());
            }
            sig.initSign(privateKey, sr);
            this.updateSignature(sig, clntNonce, svrNonce);
            this.signatureBytes = sig.sign();
        }
        
        ECDH_ServerKeyExchange(final HandshakeInStream input, final PublicKey signingKey, final byte[] clntNonce, final byte[] svrNonce, final Collection<SignatureAndHashAlgorithm> localSupportedSignAlgs, final ProtocolVersion protocolVersion) throws IOException, GeneralSecurityException {
            this.protocolVersion = protocolVersion;
            final int curveType = input.getInt8();
            if (curveType != 3) {
                throw new SSLHandshakeException("Unsupported ECCurveType: " + curveType);
            }
            this.curveId = input.getInt16();
            if (!EllipticCurvesExtension.isSupported(this.curveId)) {
                throw new SSLHandshakeException("Unsupported curveId: " + this.curveId);
            }
            final String curveOid = EllipticCurvesExtension.getCurveOid(this.curveId);
            if (curveOid == null) {
                throw new SSLHandshakeException("Unknown named curve: " + this.curveId);
            }
            final ECParameterSpec parameters = JsseJce.getECParameterSpec(curveOid);
            if (parameters == null) {
                throw new SSLHandshakeException("Unsupported curve: " + curveOid);
            }
            this.pointBytes = input.getBytes8();
            final ECPoint point = JsseJce.decodePoint(this.pointBytes, parameters.getCurve());
            final KeyFactory factory = JsseJce.getKeyFactory("EC");
            this.publicKey = (ECPublicKey)factory.generatePublic(new ECPublicKeySpec(point, parameters));
            if (signingKey == null) {
                return;
            }
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                final int hash = input.getInt8();
                final int signature = input.getInt8();
                this.preferableSignatureAlgorithm = SignatureAndHashAlgorithm.valueOf(hash, signature, 0);
                if (!localSupportedSignAlgs.contains(this.preferableSignatureAlgorithm)) {
                    throw new SSLHandshakeException("Unsupported SignatureAndHashAlgorithm in ServerKeyExchange message: " + this.preferableSignatureAlgorithm);
                }
            }
            this.signatureBytes = input.getBytes16();
            Signature sig;
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                sig = JsseJce.getSignature(this.preferableSignatureAlgorithm.getAlgorithmName());
            }
            else {
                sig = getSignature(signingKey.getAlgorithm());
            }
            sig.initVerify(signingKey);
            this.updateSignature(sig, clntNonce, svrNonce);
            if (!sig.verify(this.signatureBytes)) {
                throw new SSLKeyException("Invalid signature on ECDH server key exchange message");
            }
        }
        
        ECPublicKey getPublicKey() {
            return this.publicKey;
        }
        
        private static Signature getSignature(final String keyAlgorithm) throws NoSuchAlgorithmException {
            switch (keyAlgorithm) {
                case "EC": {
                    return JsseJce.getSignature("SHA1withECDSA");
                }
                case "RSA": {
                    return RSASignature.getInstance();
                }
                default: {
                    throw new NoSuchAlgorithmException("neither an RSA or a EC key : " + keyAlgorithm);
                }
            }
        }
        
        private void updateSignature(final Signature sig, final byte[] clntNonce, final byte[] svrNonce) throws SignatureException {
            sig.update(clntNonce);
            sig.update(svrNonce);
            sig.update((byte)3);
            sig.update((byte)(this.curveId >> 8));
            sig.update((byte)this.curveId);
            sig.update((byte)this.pointBytes.length);
            sig.update(this.pointBytes);
        }
        
        @Override
        int messageLength() {
            int sigLen = 0;
            if (this.signatureBytes != null) {
                sigLen = 2 + this.signatureBytes.length;
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    sigLen += SignatureAndHashAlgorithm.sizeInRecord();
                }
            }
            return 4 + this.pointBytes.length + sigLen;
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
            s.putInt8(3);
            s.putInt16(this.curveId);
            s.putBytes8(this.pointBytes);
            if (this.signatureBytes != null) {
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    s.putInt8(this.preferableSignatureAlgorithm.getHashValue());
                    s.putInt8(this.preferableSignatureAlgorithm.getSignatureValue());
                }
                s.putBytes16(this.signatureBytes);
            }
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** ECDH ServerKeyExchange");
            if (ECDH_ServerKeyExchange.debug != null && Debug.isOn("verbose")) {
                if (this.signatureBytes == null) {
                    s.println("Anonymous");
                }
                else if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    s.println("Signature Algorithm " + this.preferableSignatureAlgorithm.getAlgorithmName());
                }
                s.println("Server key: " + this.publicKey);
            }
        }
    }
    
    static final class DistinguishedName
    {
        byte[] name;
        
        DistinguishedName(final HandshakeInStream input) throws IOException {
            this.name = input.getBytes16();
        }
        
        DistinguishedName(final X500Principal dn) {
            this.name = dn.getEncoded();
        }
        
        X500Principal getX500Principal() throws IOException {
            try {
                return new X500Principal(this.name);
            }
            catch (final IllegalArgumentException e) {
                throw (SSLProtocolException)new SSLProtocolException(e.getMessage()).initCause(e);
            }
        }
        
        int length() {
            return 2 + this.name.length;
        }
        
        void send(final HandshakeOutStream output) throws IOException {
            output.putBytes16(this.name);
        }
        
        void print(final PrintStream output) throws IOException {
            final X500Principal principal = new X500Principal(this.name);
            output.println("<" + principal.toString() + ">");
        }
    }
    
    static final class CertificateRequest extends HandshakeMessage
    {
        static final int cct_rsa_sign = 1;
        static final int cct_dss_sign = 2;
        static final int cct_rsa_fixed_dh = 3;
        static final int cct_dss_fixed_dh = 4;
        static final int cct_rsa_ephemeral_dh = 5;
        static final int cct_dss_ephemeral_dh = 6;
        static final int cct_ecdsa_sign = 64;
        static final int cct_rsa_fixed_ecdh = 65;
        static final int cct_ecdsa_fixed_ecdh = 66;
        private static final byte[] TYPES_NO_ECC;
        private static final byte[] TYPES_ECC;
        byte[] types;
        DistinguishedName[] authorities;
        ProtocolVersion protocolVersion;
        private Collection<SignatureAndHashAlgorithm> algorithms;
        private int algorithmsLen;
        
        CertificateRequest(final X509Certificate[] ca, final CipherSuite.KeyExchange keyExchange, final Collection<SignatureAndHashAlgorithm> signAlgs, final ProtocolVersion protocolVersion) throws IOException {
            this.protocolVersion = protocolVersion;
            this.authorities = new DistinguishedName[ca.length];
            for (int i = 0; i < ca.length; ++i) {
                final X500Principal x500Principal = ca[i].getSubjectX500Principal();
                this.authorities[i] = new DistinguishedName(x500Principal);
            }
            this.types = (JsseJce.isEcAvailable() ? CertificateRequest.TYPES_ECC : CertificateRequest.TYPES_NO_ECC);
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                if (signAlgs == null || signAlgs.isEmpty()) {
                    throw new SSLProtocolException("No supported signature algorithms");
                }
                this.algorithms = new ArrayList<SignatureAndHashAlgorithm>(signAlgs);
                this.algorithmsLen = SignatureAndHashAlgorithm.sizeInRecord() * this.algorithms.size();
            }
            else {
                this.algorithms = new ArrayList<SignatureAndHashAlgorithm>();
                this.algorithmsLen = 0;
            }
        }
        
        CertificateRequest(final HandshakeInStream input, final ProtocolVersion protocolVersion) throws IOException {
            this.protocolVersion = protocolVersion;
            this.types = input.getBytes8();
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                this.algorithmsLen = input.getInt16();
                if (this.algorithmsLen < 2) {
                    throw new SSLProtocolException("Invalid supported_signature_algorithms field: " + this.algorithmsLen);
                }
                this.algorithms = new ArrayList<SignatureAndHashAlgorithm>();
                int remains = this.algorithmsLen;
                int sequence = 0;
                while (remains > 1) {
                    final int hash = input.getInt8();
                    final int signature = input.getInt8();
                    final SignatureAndHashAlgorithm algorithm = SignatureAndHashAlgorithm.valueOf(hash, signature, ++sequence);
                    this.algorithms.add(algorithm);
                    remains -= 2;
                }
                if (remains != 0) {
                    throw new SSLProtocolException("Invalid supported_signature_algorithms field. remains: " + remains);
                }
            }
            else {
                this.algorithms = new ArrayList<SignatureAndHashAlgorithm>();
                this.algorithmsLen = 0;
            }
            int len = input.getInt16();
            final ArrayList<DistinguishedName> v = new ArrayList<DistinguishedName>();
            while (len >= 3) {
                final DistinguishedName dn = new DistinguishedName(input);
                v.add(dn);
                len -= dn.length();
            }
            if (len != 0) {
                throw new SSLProtocolException("Bad CertificateRequest DN length: " + len);
            }
            this.authorities = v.toArray(new DistinguishedName[v.size()]);
        }
        
        X500Principal[] getAuthorities() throws IOException {
            final X500Principal[] ret = new X500Principal[this.authorities.length];
            for (int i = 0; i < this.authorities.length; ++i) {
                ret[i] = this.authorities[i].getX500Principal();
            }
            return ret;
        }
        
        Collection<SignatureAndHashAlgorithm> getSignAlgorithms() {
            return this.algorithms;
        }
        
        @Override
        int messageType() {
            return 13;
        }
        
        @Override
        int messageLength() {
            int len = 1 + this.types.length + 2;
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                len += this.algorithmsLen + 2;
            }
            for (int i = 0; i < this.authorities.length; ++i) {
                len += this.authorities[i].length();
            }
            return len;
        }
        
        @Override
        void send(final HandshakeOutStream output) throws IOException {
            output.putBytes8(this.types);
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                output.putInt16(this.algorithmsLen);
                for (final SignatureAndHashAlgorithm algorithm : this.algorithms) {
                    output.putInt8(algorithm.getHashValue());
                    output.putInt8(algorithm.getSignatureValue());
                }
            }
            int len = 0;
            for (int i = 0; i < this.authorities.length; ++i) {
                len += this.authorities[i].length();
            }
            output.putInt16(len);
            for (int i = 0; i < this.authorities.length; ++i) {
                this.authorities[i].send(output);
            }
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** CertificateRequest");
            if (CertificateRequest.debug != null && Debug.isOn("verbose")) {
                s.print("Cert Types: ");
                for (int i = 0; i < this.types.length; ++i) {
                    switch (this.types[i]) {
                        case 1: {
                            s.print("RSA");
                            break;
                        }
                        case 2: {
                            s.print("DSS");
                            break;
                        }
                        case 3: {
                            s.print("Fixed DH (RSA sig)");
                            break;
                        }
                        case 4: {
                            s.print("Fixed DH (DSS sig)");
                            break;
                        }
                        case 5: {
                            s.print("Ephemeral DH (RSA sig)");
                            break;
                        }
                        case 6: {
                            s.print("Ephemeral DH (DSS sig)");
                            break;
                        }
                        case 64: {
                            s.print("ECDSA");
                            break;
                        }
                        case 65: {
                            s.print("Fixed ECDH (RSA sig)");
                            break;
                        }
                        case 66: {
                            s.print("Fixed ECDH (ECDSA sig)");
                            break;
                        }
                        default: {
                            s.print("Type-" + (this.types[i] & 0xFF));
                            break;
                        }
                    }
                    if (i != this.types.length - 1) {
                        s.print(", ");
                    }
                }
                s.println();
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    final StringBuffer buffer = new StringBuffer();
                    boolean opened = false;
                    for (final SignatureAndHashAlgorithm signAlg : this.algorithms) {
                        if (opened) {
                            buffer.append(", " + signAlg.getAlgorithmName());
                        }
                        else {
                            buffer.append(signAlg.getAlgorithmName());
                            opened = true;
                        }
                    }
                    s.println("Supported Signature Algorithms: " + (Object)buffer);
                }
                s.println("Cert Authorities:");
                if (this.authorities.length == 0) {
                    s.println("<Empty>");
                }
                else {
                    for (int i = 0; i < this.authorities.length; ++i) {
                        this.authorities[i].print(s);
                    }
                }
            }
        }
        
        static {
            TYPES_NO_ECC = new byte[] { 1, 2 };
            TYPES_ECC = new byte[] { 1, 2, 64 };
        }
    }
    
    static final class ServerHelloDone extends HandshakeMessage
    {
        @Override
        int messageType() {
            return 14;
        }
        
        ServerHelloDone() {
        }
        
        ServerHelloDone(final HandshakeInStream input) {
        }
        
        @Override
        int messageLength() {
            return 0;
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** ServerHelloDone");
        }
    }
    
    static final class CertificateVerify extends HandshakeMessage
    {
        private byte[] signature;
        ProtocolVersion protocolVersion;
        private SignatureAndHashAlgorithm preferableSignatureAlgorithm;
        private static final Class<?> delegate;
        private static final Field spiField;
        private static final Object NULL_OBJECT;
        private static final Map<Class<?>, Object> methodCache;
        
        CertificateVerify(final ProtocolVersion protocolVersion, final HandshakeHash handshakeHash, final PrivateKey privateKey, final SecretKey masterSecret, final SecureRandom sr, final SignatureAndHashAlgorithm signAlgorithm) throws GeneralSecurityException {
            this.preferableSignatureAlgorithm = null;
            this.protocolVersion = protocolVersion;
            final String algorithm = privateKey.getAlgorithm();
            Signature sig = null;
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                this.preferableSignatureAlgorithm = signAlgorithm;
                sig = JsseJce.getSignature(signAlgorithm.getAlgorithmName());
            }
            else {
                sig = getSignature(protocolVersion, algorithm);
            }
            sig.initSign(privateKey, sr);
            updateSignature(sig, protocolVersion, handshakeHash, algorithm, masterSecret);
            this.signature = sig.sign();
        }
        
        CertificateVerify(final HandshakeInStream input, final Collection<SignatureAndHashAlgorithm> localSupportedSignAlgs, final ProtocolVersion protocolVersion) throws IOException {
            this.preferableSignatureAlgorithm = null;
            this.protocolVersion = protocolVersion;
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                final int hashAlg = input.getInt8();
                final int signAlg = input.getInt8();
                this.preferableSignatureAlgorithm = SignatureAndHashAlgorithm.valueOf(hashAlg, signAlg, 0);
                if (!localSupportedSignAlgs.contains(this.preferableSignatureAlgorithm)) {
                    throw new SSLHandshakeException("Unsupported SignatureAndHashAlgorithm in CertificateVerify message: " + this.preferableSignatureAlgorithm);
                }
            }
            this.signature = input.getBytes16();
        }
        
        SignatureAndHashAlgorithm getPreferableSignatureAlgorithm() {
            return this.preferableSignatureAlgorithm;
        }
        
        boolean verify(final ProtocolVersion protocolVersion, final HandshakeHash handshakeHash, final PublicKey publicKey, final SecretKey masterSecret) throws GeneralSecurityException {
            final String algorithm = publicKey.getAlgorithm();
            Signature sig = null;
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                sig = JsseJce.getSignature(this.preferableSignatureAlgorithm.getAlgorithmName());
            }
            else {
                sig = getSignature(protocolVersion, algorithm);
            }
            sig.initVerify(publicKey);
            updateSignature(sig, protocolVersion, handshakeHash, algorithm, masterSecret);
            return sig.verify(this.signature);
        }
        
        private static Signature getSignature(final ProtocolVersion protocolVersion, final String algorithm) throws GeneralSecurityException {
            switch (algorithm) {
                case "RSA": {
                    return RSASignature.getInternalInstance();
                }
                case "DSA": {
                    return JsseJce.getSignature("RawDSA");
                }
                case "EC": {
                    return JsseJce.getSignature("NONEwithECDSA");
                }
                default: {
                    throw new SignatureException("Unrecognized algorithm: " + algorithm);
                }
            }
        }
        
        private static void updateSignature(final Signature sig, final ProtocolVersion protocolVersion, final HandshakeHash handshakeHash, final String algorithm, final SecretKey masterKey) throws SignatureException {
            if (algorithm.equals("RSA")) {
                if (protocolVersion.v < ProtocolVersion.TLS12.v) {
                    final MessageDigest md5Clone = handshakeHash.getMD5Clone();
                    final MessageDigest shaClone = handshakeHash.getSHAClone();
                    if (protocolVersion.v < ProtocolVersion.TLS10.v) {
                        updateDigest(md5Clone, CertificateVerify.MD5_pad1, CertificateVerify.MD5_pad2, masterKey);
                        updateDigest(shaClone, CertificateVerify.SHA_pad1, CertificateVerify.SHA_pad2, masterKey);
                    }
                    RSASignature.setHashes(sig, md5Clone, shaClone);
                }
                else {
                    sig.update(handshakeHash.getAllHandshakeMessages());
                }
            }
            else if (protocolVersion.v < ProtocolVersion.TLS12.v) {
                final MessageDigest shaClone2 = handshakeHash.getSHAClone();
                if (protocolVersion.v < ProtocolVersion.TLS10.v) {
                    updateDigest(shaClone2, CertificateVerify.SHA_pad1, CertificateVerify.SHA_pad2, masterKey);
                }
                sig.update(shaClone2.digest());
            }
            else {
                sig.update(handshakeHash.getAllHandshakeMessages());
            }
        }
        
        private static void updateDigest(final MessageDigest md, final byte[] pad1, final byte[] pad2, final SecretKey masterSecret) {
            final byte[] keyBytes = (byte[])("RAW".equals(masterSecret.getFormat()) ? masterSecret.getEncoded() : null);
            if (keyBytes != null) {
                md.update(keyBytes);
            }
            else {
                digestKey(md, masterSecret);
            }
            md.update(pad1);
            final byte[] temp = md.digest();
            if (keyBytes != null) {
                md.update(keyBytes);
            }
            else {
                digestKey(md, masterSecret);
            }
            md.update(pad2);
            md.update(temp);
        }
        
        private static void makeAccessible(final AccessibleObject o) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    o.setAccessible(true);
                    return null;
                }
            });
        }
        
        private static void digestKey(final MessageDigest md, final SecretKey key) {
            try {
                if (md.getClass() != CertificateVerify.delegate) {
                    throw new Exception("Digest is not a MessageDigestSpi");
                }
                final MessageDigestSpi spi = (MessageDigestSpi)CertificateVerify.spiField.get(md);
                final Class<?> clazz = spi.getClass();
                Object r = CertificateVerify.methodCache.get(clazz);
                if (r == null) {
                    try {
                        r = clazz.getDeclaredMethod("implUpdate", SecretKey.class);
                        makeAccessible((AccessibleObject)r);
                    }
                    catch (final NoSuchMethodException e) {
                        r = CertificateVerify.NULL_OBJECT;
                    }
                    CertificateVerify.methodCache.put(clazz, r);
                }
                if (r == CertificateVerify.NULL_OBJECT) {
                    throw new Exception("Digest does not support implUpdate(SecretKey)");
                }
                final Method update = (Method)r;
                update.invoke(spi, key);
            }
            catch (final Exception e2) {
                throw new RuntimeException("Could not obtain encoded key and MessageDigest cannot digest key", e2);
            }
        }
        
        @Override
        int messageType() {
            return 15;
        }
        
        @Override
        int messageLength() {
            int temp = 2;
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                temp += SignatureAndHashAlgorithm.sizeInRecord();
            }
            return temp + this.signature.length;
        }
        
        @Override
        void send(final HandshakeOutStream s) throws IOException {
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                s.putInt8(this.preferableSignatureAlgorithm.getHashValue());
                s.putInt8(this.preferableSignatureAlgorithm.getSignatureValue());
            }
            s.putBytes16(this.signature);
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** CertificateVerify");
            if (CertificateVerify.debug != null && Debug.isOn("verbose") && this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                s.println("Signature Algorithm " + this.preferableSignatureAlgorithm.getAlgorithmName());
            }
        }
        
        static {
            try {
                delegate = Class.forName("java.security.MessageDigest$Delegate");
                spiField = CertificateVerify.delegate.getDeclaredField("digestSpi");
            }
            catch (final Exception e) {
                throw new RuntimeException("Reflection failed", e);
            }
            makeAccessible(CertificateVerify.spiField);
            NULL_OBJECT = new Object();
            methodCache = new ConcurrentHashMap<Class<?>, Object>();
        }
    }
    
    static final class Finished extends HandshakeMessage
    {
        static final int CLIENT = 1;
        static final int SERVER = 2;
        private static final byte[] SSL_CLIENT;
        private static final byte[] SSL_SERVER;
        private byte[] verifyData;
        private ProtocolVersion protocolVersion;
        private CipherSuite cipherSuite;
        
        Finished(final ProtocolVersion protocolVersion, final HandshakeHash handshakeHash, final int sender, final SecretKey master, final CipherSuite cipherSuite) {
            this.protocolVersion = protocolVersion;
            this.cipherSuite = cipherSuite;
            this.verifyData = this.getFinished(handshakeHash, sender, master);
        }
        
        Finished(final ProtocolVersion protocolVersion, final HandshakeInStream input, final CipherSuite cipherSuite) throws IOException {
            this.protocolVersion = protocolVersion;
            this.cipherSuite = cipherSuite;
            final int msgLen = (protocolVersion.v >= ProtocolVersion.TLS10.v) ? 12 : 36;
            input.read(this.verifyData = new byte[msgLen]);
        }
        
        boolean verify(final HandshakeHash handshakeHash, final int sender, final SecretKey master) {
            final byte[] myFinished = this.getFinished(handshakeHash, sender, master);
            return MessageDigest.isEqual(myFinished, this.verifyData);
        }
        
        private byte[] getFinished(final HandshakeHash handshakeHash, final int sender, final SecretKey masterKey) {
            byte[] sslLabel;
            String tlsLabel;
            if (sender == 1) {
                sslLabel = Finished.SSL_CLIENT;
                tlsLabel = "client finished";
            }
            else {
                if (sender != 2) {
                    throw new RuntimeException("Invalid sender: " + sender);
                }
                sslLabel = Finished.SSL_SERVER;
                tlsLabel = "server finished";
            }
            if (this.protocolVersion.v >= ProtocolVersion.TLS10.v) {
                try {
                    byte[] seed;
                    String prfAlg;
                    CipherSuite.PRF prf;
                    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                        seed = handshakeHash.getFinishedHash();
                        prfAlg = "SunTls12Prf";
                        prf = this.cipherSuite.prfAlg;
                    }
                    else {
                        final MessageDigest md5Clone = handshakeHash.getMD5Clone();
                        final MessageDigest shaClone = handshakeHash.getSHAClone();
                        seed = new byte[36];
                        md5Clone.digest(seed, 0, 16);
                        shaClone.digest(seed, 16, 20);
                        prfAlg = "SunTlsPrf";
                        prf = CipherSuite.PRF.P_NONE;
                    }
                    final String prfHashAlg = prf.getPRFHashAlg();
                    final int prfHashLength = prf.getPRFHashLength();
                    final int prfBlockSize = prf.getPRFBlockSize();
                    final TlsPrfParameterSpec spec = new TlsPrfParameterSpec(masterKey, tlsLabel, seed, 12, prfHashAlg, prfHashLength, prfBlockSize);
                    final KeyGenerator kg = JsseJce.getKeyGenerator(prfAlg);
                    kg.init(spec);
                    final SecretKey prfKey = kg.generateKey();
                    if (!"RAW".equals(prfKey.getFormat())) {
                        throw new ProviderException("Invalid PRF output, format must be RAW. Format received: " + prfKey.getFormat());
                    }
                    final byte[] finished = prfKey.getEncoded();
                    return finished;
                }
                catch (final GeneralSecurityException e) {
                    throw new RuntimeException("PRF failed", e);
                }
            }
            final MessageDigest md5Clone2 = handshakeHash.getMD5Clone();
            final MessageDigest shaClone2 = handshakeHash.getSHAClone();
            updateDigest(md5Clone2, sslLabel, Finished.MD5_pad1, Finished.MD5_pad2, masterKey);
            updateDigest(shaClone2, sslLabel, Finished.SHA_pad1, Finished.SHA_pad2, masterKey);
            final byte[] finished2 = new byte[36];
            try {
                md5Clone2.digest(finished2, 0, 16);
                shaClone2.digest(finished2, 16, 20);
            }
            catch (final DigestException e2) {
                throw new RuntimeException("Digest failed", e2);
            }
            return finished2;
        }
        
        private static void updateDigest(final MessageDigest md, final byte[] sender, final byte[] pad1, final byte[] pad2, final SecretKey masterSecret) {
            md.update(sender);
            updateDigest(md, pad1, pad2, masterSecret);
        }
        
        byte[] getVerifyData() {
            return this.verifyData;
        }
        
        @Override
        int messageType() {
            return 20;
        }
        
        @Override
        int messageLength() {
            return this.verifyData.length;
        }
        
        @Override
        void send(final HandshakeOutStream out) throws IOException {
            out.write(this.verifyData);
        }
        
        @Override
        void print(final PrintStream s) throws IOException {
            s.println("*** Finished");
            if (Finished.debug != null && Debug.isOn("verbose")) {
                Debug.println(s, "verify_data", this.verifyData);
                s.println("***");
            }
        }
        
        static {
            SSL_CLIENT = new byte[] { 67, 76, 78, 84 };
            SSL_SERVER = new byte[] { 83, 82, 86, 82 };
        }
    }
}
