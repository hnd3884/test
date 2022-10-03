package org.openjsse.sun.security.ssl;

import java.util.Arrays;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.io.IOException;

abstract class HelloCookieManager
{
    abstract byte[] createCookie(final ServerHandshakeContext p0, final ClientHello.ClientHelloMessage p1) throws IOException;
    
    abstract boolean isCookieValid(final ServerHandshakeContext p0, final ClientHello.ClientHelloMessage p1, final byte[] p2) throws IOException;
    
    static class Builder
    {
        final SecureRandom secureRandom;
        private volatile D10HelloCookieManager d10HelloCookieManager;
        private volatile D13HelloCookieManager d13HelloCookieManager;
        private volatile T13HelloCookieManager t13HelloCookieManager;
        
        Builder(final SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
        }
        
        HelloCookieManager valueOf(final ProtocolVersion protocolVersion) {
            if (protocolVersion.isDTLS) {
                if (protocolVersion.useTLS13PlusSpec()) {
                    if (this.d13HelloCookieManager != null) {
                        return this.d13HelloCookieManager;
                    }
                    synchronized (this) {
                        if (this.d13HelloCookieManager == null) {
                            this.d13HelloCookieManager = new D13HelloCookieManager(this.secureRandom);
                        }
                    }
                    return this.d13HelloCookieManager;
                }
                else {
                    if (this.d10HelloCookieManager != null) {
                        return this.d10HelloCookieManager;
                    }
                    synchronized (this) {
                        if (this.d10HelloCookieManager == null) {
                            this.d10HelloCookieManager = new D10HelloCookieManager(this.secureRandom);
                        }
                    }
                    return this.d10HelloCookieManager;
                }
            }
            else {
                if (!protocolVersion.useTLS13PlusSpec()) {
                    return null;
                }
                if (this.t13HelloCookieManager != null) {
                    return this.t13HelloCookieManager;
                }
                synchronized (this) {
                    if (this.t13HelloCookieManager == null) {
                        this.t13HelloCookieManager = new T13HelloCookieManager(this.secureRandom);
                    }
                }
                return this.t13HelloCookieManager;
            }
        }
    }
    
    private static final class D10HelloCookieManager extends HelloCookieManager
    {
        final SecureRandom secureRandom;
        private int cookieVersion;
        private byte[] cookieSecret;
        private byte[] legacySecret;
        
        D10HelloCookieManager(final SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
            this.cookieVersion = secureRandom.nextInt();
            this.cookieSecret = new byte[32];
            this.legacySecret = new byte[32];
            secureRandom.nextBytes(this.cookieSecret);
            System.arraycopy(this.cookieSecret, 0, this.legacySecret, 0, 32);
        }
        
        @Override
        byte[] createCookie(final ServerHandshakeContext context, final ClientHello.ClientHelloMessage clientHello) throws IOException {
            final int version;
            final byte[] secret;
            synchronized (this) {
                version = this.cookieVersion;
                secret = this.cookieSecret;
                if ((this.cookieVersion & 0xFFFFFF) == 0x0) {
                    System.arraycopy(this.cookieSecret, 0, this.legacySecret, 0, 32);
                    this.secureRandom.nextBytes(this.cookieSecret);
                }
                ++this.cookieVersion;
            }
            final MessageDigest md = JsseJce.getMessageDigest("SHA-256");
            final byte[] helloBytes = clientHello.getHelloCookieBytes();
            md.update(helloBytes);
            final byte[] cookie = md.digest(secret);
            cookie[0] = (byte)(version >> 24 & 0xFF);
            return cookie;
        }
        
        @Override
        boolean isCookieValid(final ServerHandshakeContext context, final ClientHello.ClientHelloMessage clientHello, final byte[] cookie) throws IOException {
            if (cookie == null || cookie.length != 32) {
                return false;
            }
            byte[] secret;
            synchronized (this) {
                if ((this.cookieVersion >> 24 & 0xFF) == cookie[0]) {
                    secret = this.cookieSecret;
                }
                else {
                    secret = this.legacySecret;
                }
            }
            final MessageDigest md = JsseJce.getMessageDigest("SHA-256");
            final byte[] helloBytes = clientHello.getHelloCookieBytes();
            md.update(helloBytes);
            final byte[] target = md.digest(secret);
            target[0] = cookie[0];
            return Arrays.equals(target, cookie);
        }
    }
    
    private static final class D13HelloCookieManager extends HelloCookieManager
    {
        D13HelloCookieManager(final SecureRandom secureRandom) {
        }
        
        @Override
        byte[] createCookie(final ServerHandshakeContext context, final ClientHello.ClientHelloMessage clientHello) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        boolean isCookieValid(final ServerHandshakeContext context, final ClientHello.ClientHelloMessage clientHello, final byte[] cookie) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private static final class T13HelloCookieManager extends HelloCookieManager
    {
        final SecureRandom secureRandom;
        private int cookieVersion;
        private final byte[] cookieSecret;
        private final byte[] legacySecret;
        
        T13HelloCookieManager(final SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
            this.cookieVersion = secureRandom.nextInt();
            this.cookieSecret = new byte[64];
            this.legacySecret = new byte[64];
            secureRandom.nextBytes(this.cookieSecret);
            System.arraycopy(this.cookieSecret, 0, this.legacySecret, 0, 64);
        }
        
        @Override
        byte[] createCookie(final ServerHandshakeContext context, final ClientHello.ClientHelloMessage clientHello) throws IOException {
            final int version;
            final byte[] secret;
            synchronized (this) {
                version = this.cookieVersion;
                secret = this.cookieSecret;
                if ((this.cookieVersion & 0xFFFFFF) == 0x0) {
                    System.arraycopy(this.cookieSecret, 0, this.legacySecret, 0, 64);
                    this.secureRandom.nextBytes(this.cookieSecret);
                }
                ++this.cookieVersion;
            }
            final MessageDigest md = JsseJce.getMessageDigest(context.negotiatedCipherSuite.hashAlg.name);
            final byte[] headerBytes = clientHello.getHeaderBytes();
            md.update(headerBytes);
            final byte[] headerCookie = md.digest(secret);
            context.handshakeHash.update();
            final byte[] clientHelloHash = context.handshakeHash.digest();
            final byte[] prefix = { (byte)(context.negotiatedCipherSuite.id >> 8 & 0xFF), (byte)(context.negotiatedCipherSuite.id & 0xFF), (byte)(version >> 24 & 0xFF) };
            final byte[] cookie = Arrays.copyOf(prefix, prefix.length + headerCookie.length + clientHelloHash.length);
            System.arraycopy(headerCookie, 0, cookie, prefix.length, headerCookie.length);
            System.arraycopy(clientHelloHash, 0, cookie, prefix.length + headerCookie.length, clientHelloHash.length);
            return cookie;
        }
        
        @Override
        boolean isCookieValid(final ServerHandshakeContext context, final ClientHello.ClientHelloMessage clientHello, final byte[] cookie) throws IOException {
            if (cookie == null || cookie.length <= 32) {
                return false;
            }
            final int csId = (cookie[0] & 0xFF) << 8 | (cookie[1] & 0xFF);
            final CipherSuite cs = CipherSuite.valueOf(csId);
            if (cs == null || cs.hashAlg == null || cs.hashAlg.hashLength == 0) {
                return false;
            }
            final int hashLen = cs.hashAlg.hashLength;
            if (cookie.length != 3 + hashLen * 2) {
                return false;
            }
            final byte[] prevHeadCookie = Arrays.copyOfRange(cookie, 3, 3 + hashLen);
            final byte[] prevClientHelloHash = Arrays.copyOfRange(cookie, 3 + hashLen, cookie.length);
            byte[] secret;
            synchronized (this) {
                if ((byte)(this.cookieVersion >> 24 & 0xFF) == cookie[2]) {
                    secret = this.cookieSecret;
                }
                else {
                    secret = this.legacySecret;
                }
            }
            final MessageDigest md = JsseJce.getMessageDigest(cs.hashAlg.name);
            final byte[] headerBytes = clientHello.getHeaderBytes();
            md.update(headerBytes);
            final byte[] headerCookie = md.digest(secret);
            if (!Arrays.equals(headerCookie, prevHeadCookie)) {
                return false;
            }
            final byte[] hrrMessage = ServerHello.hrrReproducer.produce(context, clientHello);
            context.handshakeHash.push(hrrMessage);
            final byte[] hashedClientHello = new byte[4 + hashLen];
            hashedClientHello[0] = SSLHandshake.MESSAGE_HASH.id;
            hashedClientHello[2] = (hashedClientHello[1] = 0);
            hashedClientHello[3] = (byte)(hashLen & 0xFF);
            System.arraycopy(prevClientHelloHash, 0, hashedClientHello, 4, hashLen);
            context.handshakeHash.push(hashedClientHello);
            return true;
        }
    }
}
