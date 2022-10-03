package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import javax.net.ssl.SSLException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import sun.security.util.KeyUtil;
import javax.net.ssl.SSLProtocolException;
import java.security.PrivateKey;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import javax.net.ssl.SSLKeyException;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.SecretKey;

final class RSAClientKeyExchange extends HandshakeMessage
{
    private ProtocolVersion protocolVersion;
    SecretKey preMaster;
    private byte[] encrypted;
    
    RSAClientKeyExchange(final ProtocolVersion protocolVersion, final ProtocolVersion maxVersion, final SecureRandom generator, final PublicKey publicKey) throws IOException {
        if (!publicKey.getAlgorithm().equals("RSA")) {
            throw new SSLKeyException("Public key not of type RSA: " + publicKey.getAlgorithm());
        }
        this.protocolVersion = protocolVersion;
        try {
            final String s = (protocolVersion.v >= ProtocolVersion.TLS12.v) ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret";
            final KeyGenerator kg = JsseJce.getKeyGenerator(s);
            kg.init(new TlsRsaPremasterSecretParameterSpec(maxVersion.v, protocolVersion.v), generator);
            this.preMaster = kg.generateKey();
            final Cipher cipher = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
            cipher.init(3, publicKey, generator);
            this.encrypted = cipher.wrap(this.preMaster);
        }
        catch (final GeneralSecurityException e) {
            throw (SSLKeyException)new SSLKeyException("RSA premaster secret error").initCause(e);
        }
    }
    
    private static String safeProviderName(final Cipher cipher) {
        try {
            return cipher.getProvider().toString();
        }
        catch (final Exception e) {
            if (RSAClientKeyExchange.debug != null && Debug.isOn("handshake")) {
                System.out.println("Retrieving The Cipher provider name caused exception " + e.getMessage());
            }
            try {
                return cipher.toString() + " (provider name not available)";
            }
            catch (final Exception e) {
                if (RSAClientKeyExchange.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Retrieving The Cipher name caused exception " + e.getMessage());
                }
                return "(cipher/provider names not available)";
            }
        }
    }
    
    RSAClientKeyExchange(final ProtocolVersion currentVersion, final ProtocolVersion maxVersion, final SecureRandom generator, final HandshakeInStream input, final int messageSize, final PrivateKey privateKey) throws IOException {
        if (!privateKey.getAlgorithm().equals("RSA")) {
            throw new SSLKeyException("Private key not of type RSA: " + privateKey.getAlgorithm());
        }
        if (currentVersion.v >= ProtocolVersion.TLS10.v) {
            this.encrypted = input.getBytes16();
        }
        else {
            this.encrypted = new byte[messageSize];
            if (input.read(this.encrypted) != messageSize) {
                throw new SSLProtocolException("SSL: read PreMasterSecret: short read");
            }
        }
        byte[] encoded = null;
        try {
            boolean needFailover = false;
            Cipher cipher = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
            try {
                cipher.init(4, privateKey, new TlsRsaPremasterSecretParameterSpec(maxVersion.v, currentVersion.v), generator);
                needFailover = !KeyUtil.isOracleJCEProvider(cipher.getProvider().getName());
            }
            catch (final InvalidKeyException | UnsupportedOperationException iue) {
                if (RSAClientKeyExchange.debug != null && Debug.isOn("handshake")) {
                    System.out.println("The Cipher provider " + safeProviderName(cipher) + " caused exception: " + iue.getMessage());
                }
                needFailover = true;
            }
            if (needFailover) {
                cipher = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
                cipher.init(2, privateKey);
                boolean failed = false;
                try {
                    encoded = cipher.doFinal(this.encrypted);
                }
                catch (final BadPaddingException bpe) {
                    failed = true;
                }
                encoded = KeyUtil.checkTlsPreMasterSecretKey(maxVersion.v, currentVersion.v, generator, encoded, failed);
                this.preMaster = generatePreMasterSecret(maxVersion.v, currentVersion.v, encoded, generator);
            }
            else {
                this.preMaster = (SecretKey)cipher.unwrap(this.encrypted, "TlsRsaPremasterSecret", 3);
            }
        }
        catch (final InvalidKeyException ibk) {
            throw new SSLException("Unable to process PreMasterSecret", ibk);
        }
        catch (final Exception e) {
            if (RSAClientKeyExchange.debug != null && Debug.isOn("handshake")) {
                System.out.println("RSA premaster secret decryption error:");
                e.printStackTrace(System.out);
            }
            throw new RuntimeException("Could not generate dummy secret", e);
        }
    }
    
    private static SecretKey generatePreMasterSecret(final int clientVersion, final int serverVersion, final byte[] encodedSecret, final SecureRandom generator) {
        if (RSAClientKeyExchange.debug != null && Debug.isOn("handshake")) {
            System.out.println("Generating a premaster secret");
        }
        try {
            final String s = (clientVersion >= ProtocolVersion.TLS12.v) ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret";
            final KeyGenerator kg = JsseJce.getKeyGenerator(s);
            kg.init(new TlsRsaPremasterSecretParameterSpec(clientVersion, serverVersion, encodedSecret), generator);
            return kg.generateKey();
        }
        catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException iae) {
            if (RSAClientKeyExchange.debug != null && Debug.isOn("handshake")) {
                System.out.println("RSA premaster secret generation error:");
                iae.printStackTrace(System.out);
            }
            throw new RuntimeException("Could not generate premaster secret", iae);
        }
    }
    
    @Override
    int messageType() {
        return 16;
    }
    
    @Override
    int messageLength() {
        if (this.protocolVersion.v >= ProtocolVersion.TLS10.v) {
            return this.encrypted.length + 2;
        }
        return this.encrypted.length;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        if (this.protocolVersion.v >= ProtocolVersion.TLS10.v) {
            s.putBytes16(this.encrypted);
        }
        else {
            s.write(this.encrypted);
        }
    }
    
    @Override
    void print(final PrintStream s) throws IOException {
        String version = "version not available/extractable";
        final byte[] ba = this.preMaster.getEncoded();
        if (ba != null && ba.length >= 2) {
            version = ProtocolVersion.valueOf(ba[0], ba[1]).name;
        }
        s.println("*** ClientKeyExchange, RSA PreMasterSecret, " + version);
    }
}
