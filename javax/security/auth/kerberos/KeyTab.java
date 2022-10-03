package javax.security.auth.kerberos;

import sun.security.krb5.JavaxSecurityAuthKerberosAccess;
import sun.security.krb5.KerberosSecrets;
import java.util.Objects;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.RealmException;
import sun.security.krb5.PrincipalName;
import java.security.AccessControlException;
import java.io.File;

public final class KeyTab
{
    private final File file;
    private final KerberosPrincipal princ;
    private final boolean bound;
    
    private KeyTab(final KerberosPrincipal princ, final File file, final boolean bound) {
        this.princ = princ;
        this.file = file;
        this.bound = bound;
    }
    
    public static KeyTab getInstance(final File file) {
        if (file == null) {
            throw new NullPointerException("file must be non null");
        }
        return new KeyTab(null, file, true);
    }
    
    public static KeyTab getUnboundInstance(final File file) {
        if (file == null) {
            throw new NullPointerException("file must be non null");
        }
        return new KeyTab(null, file, false);
    }
    
    public static KeyTab getInstance(final KerberosPrincipal kerberosPrincipal, final File file) {
        if (kerberosPrincipal == null) {
            throw new NullPointerException("princ must be non null");
        }
        if (file == null) {
            throw new NullPointerException("file must be non null");
        }
        return new KeyTab(kerberosPrincipal, file, true);
    }
    
    public static KeyTab getInstance() {
        return new KeyTab(null, null, true);
    }
    
    public static KeyTab getUnboundInstance() {
        return new KeyTab(null, null, false);
    }
    
    public static KeyTab getInstance(final KerberosPrincipal kerberosPrincipal) {
        if (kerberosPrincipal == null) {
            throw new NullPointerException("princ must be non null");
        }
        return new KeyTab(kerberosPrincipal, null, true);
    }
    
    sun.security.krb5.internal.ktab.KeyTab takeSnapshot() {
        try {
            return sun.security.krb5.internal.ktab.KeyTab.getInstance(this.file);
        }
        catch (final AccessControlException ex) {
            if (this.file != null) {
                throw ex;
            }
            final AccessControlException ex2 = new AccessControlException("Access to default keytab denied (modified exception)");
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }
    
    public KerberosKey[] getKeys(final KerberosPrincipal kerberosPrincipal) {
        try {
            if (this.princ != null && !kerberosPrincipal.equals(this.princ)) {
                return new KerberosKey[0];
            }
            final EncryptionKey[] serviceKeys = this.takeSnapshot().readServiceKeys(new PrincipalName(kerberosPrincipal.getName()));
            final KerberosKey[] array = new KerberosKey[serviceKeys.length];
            for (int i = 0; i < array.length; ++i) {
                final Integer keyVersionNumber = serviceKeys[i].getKeyVersionNumber();
                array[i] = new KerberosKey(kerberosPrincipal, serviceKeys[i].getBytes(), serviceKeys[i].getEType(), (keyVersionNumber == null) ? 0 : keyVersionNumber);
                serviceKeys[i].destroy();
            }
            return array;
        }
        catch (final RealmException ex) {
            return new KerberosKey[0];
        }
    }
    
    EncryptionKey[] getEncryptionKeys(final PrincipalName principalName) {
        return this.takeSnapshot().readServiceKeys(principalName);
    }
    
    public boolean exists() {
        return !this.takeSnapshot().isMissing();
    }
    
    @Override
    public String toString() {
        final String s = (this.file == null) ? "Default keytab" : this.file.toString();
        if (!this.bound) {
            return s;
        }
        if (this.princ == null) {
            return s + " for someone";
        }
        return s + " for " + this.princ;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.file, this.princ, this.bound);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyTab)) {
            return false;
        }
        final KeyTab keyTab = (KeyTab)o;
        return Objects.equals(keyTab.princ, this.princ) && Objects.equals(keyTab.file, this.file) && this.bound == keyTab.bound;
    }
    
    public KerberosPrincipal getPrincipal() {
        return this.princ;
    }
    
    public boolean isBound() {
        return this.bound;
    }
    
    static {
        KerberosSecrets.setJavaxSecurityAuthKerberosAccess(new JavaxSecurityAuthKerberosAccessImpl());
    }
}
