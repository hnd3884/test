package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Locale;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;

final class HandshakeHash
{
    private int version;
    private ByteArrayOutputStream data;
    private MessageDigest md5;
    private MessageDigest sha;
    private final int clonesNeeded;
    private MessageDigest finMD;
    
    HandshakeHash(final boolean needCertificateVerify) {
        this.version = -1;
        this.data = new ByteArrayOutputStream();
        this.clonesNeeded = (needCertificateVerify ? 5 : 4);
    }
    
    void update(final byte[] b, final int offset, final int len) {
        switch (this.version) {
            case 1: {
                this.md5.update(b, offset, len);
                this.sha.update(b, offset, len);
                break;
            }
            default: {
                if (this.finMD != null) {
                    this.finMD.update(b, offset, len);
                }
                this.data.write(b, offset, len);
                break;
            }
        }
    }
    
    void reset() {
        if (this.version != -1) {
            throw new RuntimeException("reset() can be only be called before protocolDetermined");
        }
        this.data.reset();
    }
    
    void protocolDetermined(final ProtocolVersion pv) {
        if (this.version != -1) {
            return;
        }
        switch (this.version = ((pv.compareTo(ProtocolVersion.TLS12) >= 0) ? 2 : 1)) {
            case 1: {
                try {
                    this.md5 = CloneableDigest.getDigest("MD5", this.clonesNeeded);
                    this.sha = CloneableDigest.getDigest("SHA", this.clonesNeeded);
                }
                catch (final NoSuchAlgorithmException e) {
                    throw new RuntimeException("Algorithm MD5 or SHA not available", e);
                }
                final byte[] bytes = this.data.toByteArray();
                this.update(bytes, 0, bytes.length);
                break;
            }
        }
    }
    
    MessageDigest getMD5Clone() {
        if (this.version != 1) {
            throw new RuntimeException("getMD5Clone() can be only be called for TLS 1.1");
        }
        return cloneDigest(this.md5);
    }
    
    MessageDigest getSHAClone() {
        if (this.version != 1) {
            throw new RuntimeException("getSHAClone() can be only be called for TLS 1.1");
        }
        return cloneDigest(this.sha);
    }
    
    private static MessageDigest cloneDigest(final MessageDigest digest) {
        try {
            return (MessageDigest)digest.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException("Could not clone digest", e);
        }
    }
    
    private static String normalizeAlgName(String alg) {
        alg = alg.toUpperCase(Locale.US);
        if (alg.startsWith("SHA")) {
            if (alg.length() == 3) {
                return "SHA-1";
            }
            if (alg.charAt(3) != '-') {
                return "SHA-" + alg.substring(3);
            }
        }
        return alg;
    }
    
    void setFinishedAlg(final String s) {
        if (s == null) {
            throw new RuntimeException("setFinishedAlg's argument cannot be null");
        }
        if (this.finMD != null) {
            return;
        }
        try {
            this.finMD = CloneableDigest.getDigest(normalizeAlgName(s), 4);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new Error(e);
        }
        this.finMD.update(this.data.toByteArray());
    }
    
    byte[] getAllHandshakeMessages() {
        return this.data.toByteArray();
    }
    
    byte[] getFinishedHash() {
        try {
            return cloneDigest(this.finMD).digest();
        }
        catch (final Exception e) {
            throw new Error("Error during hash calculation", e);
        }
    }
}
