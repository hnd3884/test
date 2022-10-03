package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.ReferenceCounted;
import java.security.cert.X509Certificate;
import io.netty.internal.tcnative.SSL;
import io.netty.util.IllegalReferenceCountException;
import java.security.PrivateKey;
import io.netty.util.AbstractReferenceCounted;

final class OpenSslPrivateKey extends AbstractReferenceCounted implements PrivateKey
{
    private long privateKeyAddress;
    
    OpenSslPrivateKey(final long privateKeyAddress) {
        this.privateKeyAddress = privateKeyAddress;
    }
    
    @Override
    public String getAlgorithm() {
        return "unknown";
    }
    
    @Override
    public String getFormat() {
        return null;
    }
    
    @Override
    public byte[] getEncoded() {
        return null;
    }
    
    private long privateKeyAddress() {
        if (this.refCnt() <= 0) {
            throw new IllegalReferenceCountException();
        }
        return this.privateKeyAddress;
    }
    
    @Override
    protected void deallocate() {
        SSL.freePrivateKey(this.privateKeyAddress);
        this.privateKeyAddress = 0L;
    }
    
    @Override
    public OpenSslPrivateKey retain() {
        super.retain();
        return this;
    }
    
    @Override
    public OpenSslPrivateKey retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public OpenSslPrivateKey touch() {
        super.touch();
        return this;
    }
    
    @Override
    public OpenSslPrivateKey touch(final Object hint) {
        return this;
    }
    
    @Override
    public void destroy() {
        this.release(this.refCnt());
    }
    
    @Override
    public boolean isDestroyed() {
        return this.refCnt() == 0;
    }
    
    OpenSslKeyMaterial newKeyMaterial(final long certificateChain, final X509Certificate[] chain) {
        return new OpenSslPrivateKeyMaterial(certificateChain, chain);
    }
    
    final class OpenSslPrivateKeyMaterial extends AbstractReferenceCounted implements OpenSslKeyMaterial
    {
        long certificateChain;
        private final X509Certificate[] x509CertificateChain;
        
        OpenSslPrivateKeyMaterial(final long certificateChain, final X509Certificate[] x509CertificateChain) {
            this.certificateChain = certificateChain;
            this.x509CertificateChain = ((x509CertificateChain == null) ? EmptyArrays.EMPTY_X509_CERTIFICATES : x509CertificateChain);
            OpenSslPrivateKey.this.retain();
        }
        
        @Override
        public X509Certificate[] certificateChain() {
            return this.x509CertificateChain.clone();
        }
        
        @Override
        public long certificateChainAddress() {
            if (this.refCnt() <= 0) {
                throw new IllegalReferenceCountException();
            }
            return this.certificateChain;
        }
        
        @Override
        public long privateKeyAddress() {
            if (this.refCnt() <= 0) {
                throw new IllegalReferenceCountException();
            }
            return OpenSslPrivateKey.this.privateKeyAddress();
        }
        
        @Override
        public OpenSslKeyMaterial touch(final Object hint) {
            OpenSslPrivateKey.this.touch(hint);
            return this;
        }
        
        @Override
        public OpenSslKeyMaterial retain() {
            super.retain();
            return this;
        }
        
        @Override
        public OpenSslKeyMaterial retain(final int increment) {
            super.retain(increment);
            return this;
        }
        
        @Override
        public OpenSslKeyMaterial touch() {
            OpenSslPrivateKey.this.touch();
            return this;
        }
        
        @Override
        protected void deallocate() {
            this.releaseChain();
            OpenSslPrivateKey.this.release();
        }
        
        private void releaseChain() {
            SSL.freeX509Chain(this.certificateChain);
            this.certificateChain = 0L;
        }
    }
}
