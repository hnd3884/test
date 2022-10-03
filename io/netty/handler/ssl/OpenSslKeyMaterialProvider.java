package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import javax.net.ssl.SSLException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;

class OpenSslKeyMaterialProvider
{
    private final X509KeyManager keyManager;
    private final String password;
    
    OpenSslKeyMaterialProvider(final X509KeyManager keyManager, final String password) {
        this.keyManager = keyManager;
        this.password = password;
    }
    
    static void validateKeyMaterialSupported(final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword) throws SSLException {
        validateSupported(keyCertChain);
        validateSupported(key, keyPassword);
    }
    
    private static void validateSupported(final PrivateKey key, final String password) throws SSLException {
        if (key == null) {
            return;
        }
        long pkeyBio = 0L;
        long pkey = 0L;
        try {
            pkeyBio = ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, key);
            pkey = SSL.parsePrivateKey(pkeyBio, password);
        }
        catch (final Exception e) {
            throw new SSLException("PrivateKey type not supported " + key.getFormat(), e);
        }
        finally {
            SSL.freeBIO(pkeyBio);
            if (pkey != 0L) {
                SSL.freePrivateKey(pkey);
            }
        }
    }
    
    private static void validateSupported(final X509Certificate[] certificates) throws SSLException {
        if (certificates == null || certificates.length == 0) {
            return;
        }
        long chainBio = 0L;
        long chain = 0L;
        PemEncoded encoded = null;
        try {
            encoded = PemX509Certificate.toPEM(UnpooledByteBufAllocator.DEFAULT, true, certificates);
            chainBio = ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, encoded.retain());
            chain = SSL.parseX509Chain(chainBio);
        }
        catch (final Exception e) {
            throw new SSLException("Certificate type not supported", e);
        }
        finally {
            SSL.freeBIO(chainBio);
            if (chain != 0L) {
                SSL.freeX509Chain(chain);
            }
            if (encoded != null) {
                encoded.release();
            }
        }
    }
    
    X509KeyManager keyManager() {
        return this.keyManager;
    }
    
    OpenSslKeyMaterial chooseKeyMaterial(final ByteBufAllocator allocator, final String alias) throws Exception {
        final X509Certificate[] certificates = this.keyManager.getCertificateChain(alias);
        if (certificates == null || certificates.length == 0) {
            return null;
        }
        final PrivateKey key = this.keyManager.getPrivateKey(alias);
        final PemEncoded encoded = PemX509Certificate.toPEM(allocator, true, certificates);
        long chainBio = 0L;
        long pkeyBio = 0L;
        long chain = 0L;
        long pkey = 0L;
        try {
            chainBio = ReferenceCountedOpenSslContext.toBIO(allocator, encoded.retain());
            chain = SSL.parseX509Chain(chainBio);
            OpenSslKeyMaterial keyMaterial;
            if (key instanceof OpenSslPrivateKey) {
                keyMaterial = ((OpenSslPrivateKey)key).newKeyMaterial(chain, certificates);
            }
            else {
                pkeyBio = ReferenceCountedOpenSslContext.toBIO(allocator, key);
                pkey = ((key == null) ? 0L : SSL.parsePrivateKey(pkeyBio, this.password));
                keyMaterial = new DefaultOpenSslKeyMaterial(chain, pkey, certificates);
            }
            chain = 0L;
            pkey = 0L;
            return keyMaterial;
        }
        finally {
            SSL.freeBIO(chainBio);
            SSL.freeBIO(pkeyBio);
            if (chain != 0L) {
                SSL.freeX509Chain(chain);
            }
            if (pkey != 0L) {
                SSL.freePrivateKey(pkey);
            }
            encoded.release();
        }
    }
    
    void destroy() {
    }
}
