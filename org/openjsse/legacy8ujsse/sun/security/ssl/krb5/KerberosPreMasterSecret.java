package org.openjsse.legacy8ujsse.sun.security.ssl.krb5;

import java.util.Arrays;
import org.openjsse.legacy8ujsse.sun.security.ssl.Debug;
import org.openjsse.legacy8ujsse.sun.security.ssl.HandshakeMessage;
import org.openjsse.legacy8ujsse.sun.security.ssl.HandshakeInStream;
import sun.security.krb5.KrbException;
import javax.net.ssl.SSLKeyException;
import sun.security.krb5.EncryptedData;
import java.io.IOException;
import sun.security.krb5.EncryptionKey;
import java.security.SecureRandom;
import org.openjsse.legacy8ujsse.sun.security.ssl.ProtocolVersion;

final class KerberosPreMasterSecret
{
    private ProtocolVersion protocolVersion;
    private byte[] preMaster;
    private byte[] encrypted;
    
    KerberosPreMasterSecret(final ProtocolVersion protocolVersion, final SecureRandom generator, final EncryptionKey sessionKey) throws IOException {
        if (sessionKey.getEType() == 16) {
            throw new IOException("session keys with des3-cbc-hmac-sha1-kd encryption type are not supported for TLS Kerberos cipher suites");
        }
        this.protocolVersion = protocolVersion;
        this.preMaster = generatePreMaster(generator, protocolVersion);
        try {
            final EncryptedData eData = new EncryptedData(sessionKey, this.preMaster, 0);
            this.encrypted = eData.getBytes();
        }
        catch (final KrbException e) {
            throw (SSLKeyException)new SSLKeyException("Kerberos premaster secret error").initCause(e);
        }
    }
    
    KerberosPreMasterSecret(final ProtocolVersion currentVersion, final ProtocolVersion clientVersion, final SecureRandom generator, final HandshakeInStream input, final EncryptionKey sessionKey) throws IOException {
        this.encrypted = input.getBytes16();
        if (HandshakeMessage.debug != null && Debug.isOn("handshake") && this.encrypted != null) {
            Debug.println(System.out, "encrypted premaster secret", this.encrypted);
        }
        if (sessionKey.getEType() == 16) {
            throw new IOException("session keys with des3-cbc-hmac-sha1-kd encryption type are not supported for TLS Kerberos cipher suites");
        }
        try {
            final EncryptedData data = new EncryptedData(sessionKey.getEType(), null, this.encrypted);
            byte[] temp = data.decrypt(sessionKey, 0);
            if (HandshakeMessage.debug != null && Debug.isOn("handshake") && this.encrypted != null) {
                Debug.println(System.out, "decrypted premaster secret", temp);
            }
            if (temp.length == 52 && data.getEType() == 1) {
                if (paddingByteIs(temp, 52, (byte)4) || paddingByteIs(temp, 52, (byte)0)) {
                    temp = Arrays.copyOf(temp, 48);
                }
            }
            else if (temp.length == 56 && data.getEType() == 3 && paddingByteIs(temp, 56, (byte)8)) {
                temp = Arrays.copyOf(temp, 48);
            }
            this.preMaster = temp;
            this.protocolVersion = ProtocolVersion.valueOf(this.preMaster[0], this.preMaster[1]);
            if (HandshakeMessage.debug != null && Debug.isOn("handshake")) {
                System.out.println("Kerberos PreMasterSecret version: " + this.protocolVersion);
            }
        }
        catch (final Exception e) {
            this.preMaster = null;
            this.protocolVersion = currentVersion;
        }
        boolean versionMismatch = this.protocolVersion.v != clientVersion.v;
        if (versionMismatch && clientVersion.v <= 769) {
            versionMismatch = (this.protocolVersion.v != currentVersion.v);
        }
        if (this.preMaster == null || this.preMaster.length != 48 || versionMismatch) {
            if (HandshakeMessage.debug != null && Debug.isOn("handshake")) {
                System.out.println("Kerberos PreMasterSecret error, generating random secret");
                if (this.preMaster != null) {
                    Debug.println(System.out, "Invalid secret", this.preMaster);
                }
            }
            this.preMaster = generatePreMaster(generator, clientVersion);
            this.protocolVersion = clientVersion;
        }
    }
    
    private static boolean paddingByteIs(final byte[] data, final int len, final byte b) {
        for (int i = 48; i < len; ++i) {
            if (data[i] != b) {
                return false;
            }
        }
        return true;
    }
    
    KerberosPreMasterSecret(final ProtocolVersion protocolVersion, final SecureRandom generator) {
        this.protocolVersion = protocolVersion;
        this.preMaster = generatePreMaster(generator, protocolVersion);
    }
    
    private static byte[] generatePreMaster(final SecureRandom rand, final ProtocolVersion ver) {
        final byte[] pm = new byte[48];
        rand.nextBytes(pm);
        pm[0] = ver.major;
        pm[1] = ver.minor;
        return pm;
    }
    
    byte[] getUnencrypted() {
        return this.preMaster;
    }
    
    byte[] getEncrypted() {
        return this.encrypted;
    }
}
