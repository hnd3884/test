package javax.security.auth.x500;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.security.auth.Destroyable;

public final class X500PrivateCredential implements Destroyable
{
    private X509Certificate cert;
    private PrivateKey key;
    private String alias;
    
    public X500PrivateCredential(final X509Certificate cert, final PrivateKey key) {
        if (cert == null || key == null) {
            throw new IllegalArgumentException();
        }
        this.cert = cert;
        this.key = key;
        this.alias = null;
    }
    
    public X500PrivateCredential(final X509Certificate cert, final PrivateKey key, final String alias) {
        if (cert == null || key == null || alias == null) {
            throw new IllegalArgumentException();
        }
        this.cert = cert;
        this.key = key;
        this.alias = alias;
    }
    
    public X509Certificate getCertificate() {
        return this.cert;
    }
    
    public PrivateKey getPrivateKey() {
        return this.key;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    @Override
    public void destroy() {
        this.cert = null;
        this.key = null;
        this.alias = null;
    }
    
    @Override
    public boolean isDestroyed() {
        return this.cert == null && this.key == null && this.alias == null;
    }
}
