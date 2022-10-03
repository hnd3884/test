package javax.security.auth.kerberos;

import sun.security.util.DerValue;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Permission;
import sun.security.krb5.Realm;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import java.io.Serializable;
import java.security.Principal;

public final class KerberosPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -7374788026156829911L;
    public static final int KRB_NT_UNKNOWN = 0;
    public static final int KRB_NT_PRINCIPAL = 1;
    public static final int KRB_NT_SRV_INST = 2;
    public static final int KRB_NT_SRV_HST = 3;
    public static final int KRB_NT_SRV_XHST = 4;
    public static final int KRB_NT_UID = 5;
    static final int KRB_NT_ENTERPRISE = 10;
    private transient String fullName;
    private transient String realm;
    private transient int nameType;
    
    public KerberosPrincipal(final String s) {
        this(s, 1);
    }
    
    public KerberosPrincipal(final String s, final int nameType) {
        PrincipalName principalName;
        try {
            principalName = new PrincipalName(s, nameType);
        }
        catch (final KrbException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        if (principalName.isRealmDeduced() && !Realm.AUTODEDUCEREALM) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                try {
                    securityManager.checkPermission(new ServicePermission("@" + principalName.getRealmAsString(), "-"));
                }
                catch (final SecurityException ex2) {
                    throw new SecurityException("Cannot read realm info");
                }
            }
        }
        this.nameType = nameType;
        this.fullName = principalName.toString();
        this.realm = principalName.getRealmString();
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof KerberosPrincipal && this.getName().equals(((KerberosPrincipal)o).getName()));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        try {
            final PrincipalName principalName = new PrincipalName(this.fullName, this.nameType);
            objectOutputStream.writeObject(principalName.asn1Encode());
            objectOutputStream.writeObject(principalName.getRealm().asn1Encode());
        }
        catch (final Exception ex) {
            throw new IOException(ex);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final byte[] array = (byte[])objectInputStream.readObject();
        final byte[] array2 = (byte[])objectInputStream.readObject();
        try {
            final Realm realm = new Realm(new DerValue(array2));
            final PrincipalName principalName = new PrincipalName(new DerValue(array), realm);
            this.realm = realm.toString();
            this.fullName = principalName.toString();
            this.nameType = principalName.getNameType();
        }
        catch (final Exception ex) {
            throw new IOException(ex);
        }
    }
    
    @Override
    public String getName() {
        return this.fullName;
    }
    
    public int getNameType() {
        return this.nameType;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
}
