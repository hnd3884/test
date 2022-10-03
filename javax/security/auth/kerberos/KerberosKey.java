package javax.security.auth.kerberos;

import java.util.Arrays;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.crypto.SecretKey;

public class KerberosKey implements SecretKey, Destroyable
{
    private static final long serialVersionUID = -4625402278148246993L;
    private KerberosPrincipal principal;
    private int versionNum;
    private KeyImpl key;
    private transient boolean destroyed;
    
    public KerberosKey(final KerberosPrincipal principal, final byte[] array, final int n, final int versionNum) {
        this.destroyed = false;
        this.principal = principal;
        this.versionNum = versionNum;
        this.key = new KeyImpl(array, n);
    }
    
    public KerberosKey(final KerberosPrincipal principal, final char[] array, final String s) {
        this.destroyed = false;
        this.principal = principal;
        this.key = new KeyImpl(principal, array, s);
    }
    
    public final KerberosPrincipal getPrincipal() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.principal;
    }
    
    public final int getVersionNumber() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.versionNum;
    }
    
    public final int getKeyType() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.key.getKeyType();
    }
    
    @Override
    public final String getAlgorithm() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.key.getAlgorithm();
    }
    
    @Override
    public final String getFormat() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.key.getFormat();
    }
    
    @Override
    public final byte[] getEncoded() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.key.getEncoded();
    }
    
    @Override
    public void destroy() throws DestroyFailedException {
        if (!this.destroyed) {
            this.key.destroy();
            this.principal = null;
            this.destroyed = true;
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }
    
    @Override
    public String toString() {
        if (this.destroyed) {
            return "Destroyed Principal";
        }
        return "Kerberos Principal " + this.principal.toString() + "Key Version " + this.versionNum + "key " + this.key.toString();
    }
    
    @Override
    public int hashCode() {
        final int n = 17;
        if (this.isDestroyed()) {
            return n;
        }
        int n2 = 37 * (37 * n + Arrays.hashCode(this.getEncoded())) + this.getKeyType();
        if (this.principal != null) {
            n2 = 37 * n2 + this.principal.hashCode();
        }
        return n2 * 37 + this.versionNum;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KerberosKey)) {
            return false;
        }
        final KerberosKey kerberosKey = (KerberosKey)o;
        if (this.isDestroyed() || kerberosKey.isDestroyed()) {
            return false;
        }
        if (this.versionNum != kerberosKey.getVersionNumber() || this.getKeyType() != kerberosKey.getKeyType() || !Arrays.equals(this.getEncoded(), kerberosKey.getEncoded())) {
            return false;
        }
        if (this.principal == null) {
            if (kerberosKey.getPrincipal() != null) {
                return false;
            }
        }
        else if (!this.principal.equals(kerberosKey.getPrincipal())) {
            return false;
        }
        return true;
    }
}
