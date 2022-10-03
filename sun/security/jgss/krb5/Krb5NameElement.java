package sun.security.jgss.krb5;

import java.security.Provider;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.security.Permission;
import javax.security.auth.kerberos.ServicePermission;
import sun.security.krb5.Realm;
import sun.security.krb5.KrbException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.krb5.PrincipalName;
import sun.security.jgss.spi.GSSNameSpi;

public class Krb5NameElement implements GSSNameSpi
{
    private PrincipalName krb5PrincipalName;
    private String gssNameStr;
    private Oid gssNameType;
    private static String CHAR_ENCODING;
    
    private Krb5NameElement(final PrincipalName krb5PrincipalName, final String gssNameStr, final Oid gssNameType) {
        this.gssNameStr = null;
        this.gssNameType = null;
        this.krb5PrincipalName = krb5PrincipalName;
        this.gssNameStr = gssNameStr;
        this.gssNameType = gssNameType;
    }
    
    static Krb5NameElement getInstance(final String s, Oid nt_GSS_KRB5_PRINCIPAL) throws GSSException {
        if (nt_GSS_KRB5_PRINCIPAL == null) {
            nt_GSS_KRB5_PRINCIPAL = Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL;
        }
        else if (!nt_GSS_KRB5_PRINCIPAL.equals(GSSName.NT_USER_NAME) && !nt_GSS_KRB5_PRINCIPAL.equals(GSSName.NT_HOSTBASED_SERVICE) && !nt_GSS_KRB5_PRINCIPAL.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL) && !nt_GSS_KRB5_PRINCIPAL.equals(GSSName.NT_EXPORT_NAME)) {
            throw new GSSException(4, -1, nt_GSS_KRB5_PRINCIPAL.toString() + " is an unsupported nametype");
        }
        PrincipalName principalName;
        try {
            if (nt_GSS_KRB5_PRINCIPAL.equals(GSSName.NT_EXPORT_NAME) || nt_GSS_KRB5_PRINCIPAL.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL)) {
                principalName = new PrincipalName(s, 1);
            }
            else {
                final String[] components = getComponents(s);
                if (nt_GSS_KRB5_PRINCIPAL.equals(GSSName.NT_USER_NAME)) {
                    principalName = new PrincipalName(s, 1);
                }
                else {
                    String s2 = null;
                    final String s3 = components[0];
                    if (components.length >= 2) {
                        s2 = components[1];
                    }
                    principalName = new PrincipalName(getHostBasedInstance(s3, s2), 3);
                }
            }
        }
        catch (final KrbException ex) {
            throw new GSSException(3, -1, ex.getMessage());
        }
        if (principalName.isRealmDeduced() && !Realm.AUTODEDUCEREALM) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                try {
                    securityManager.checkPermission(new ServicePermission("@" + principalName.getRealmAsString(), "-"));
                }
                catch (final SecurityException ex2) {
                    throw new GSSException(11);
                }
            }
        }
        return new Krb5NameElement(principalName, s, nt_GSS_KRB5_PRINCIPAL);
    }
    
    public static Krb5NameElement getInstance(final PrincipalName principalName) {
        return new Krb5NameElement(principalName, principalName.getName(), Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
    }
    
    private static String[] getComponents(final String s) throws GSSException {
        int lastIndex = s.lastIndexOf(64, s.length());
        if (lastIndex > 0 && s.charAt(lastIndex - 1) == '\\' && (lastIndex - 2 < 0 || s.charAt(lastIndex - 2) != '\\')) {
            lastIndex = -1;
        }
        String[] array;
        if (lastIndex > 0) {
            array = new String[] { s.substring(0, lastIndex), s.substring(lastIndex + 1) };
        }
        else {
            array = new String[] { s };
        }
        return array;
    }
    
    private static String getHostBasedInstance(final String s, String s2) throws GSSException {
        final StringBuffer sb = new StringBuffer(s);
        try {
            if (s2 == null) {
                s2 = InetAddress.getLocalHost().getHostName();
            }
        }
        catch (final UnknownHostException ex) {}
        s2 = s2.toLowerCase(Locale.ENGLISH);
        return sb.append('/').append(s2).toString();
    }
    
    public final PrincipalName getKrb5PrincipalName() {
        return this.krb5PrincipalName;
    }
    
    @Override
    public boolean equals(final GSSNameSpi gssNameSpi) throws GSSException {
        return gssNameSpi == this || (gssNameSpi instanceof Krb5NameElement && this.krb5PrincipalName.getName().equals(((Krb5NameElement)gssNameSpi).krb5PrincipalName.getName()));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        try {
            if (o instanceof Krb5NameElement) {
                return this.equals((GSSNameSpi)o);
            }
        }
        catch (final GSSException ex) {}
        return false;
    }
    
    @Override
    public int hashCode() {
        return 629 + this.krb5PrincipalName.getName().hashCode();
    }
    
    @Override
    public byte[] export() throws GSSException {
        byte[] bytes = null;
        try {
            bytes = this.krb5PrincipalName.getName().getBytes(Krb5NameElement.CHAR_ENCODING);
        }
        catch (final UnsupportedEncodingException ex) {}
        return bytes;
    }
    
    @Override
    public Oid getMechanism() {
        return Krb5MechFactory.GSS_KRB5_MECH_OID;
    }
    
    @Override
    public String toString() {
        return this.gssNameStr;
    }
    
    public Oid getGSSNameType() {
        return this.gssNameType;
    }
    
    @Override
    public Oid getStringNameType() {
        return this.gssNameType;
    }
    
    @Override
    public boolean isAnonymousName() {
        return this.gssNameType.equals(GSSName.NT_ANONYMOUS);
    }
    
    @Override
    public Provider getProvider() {
        return Krb5MechFactory.PROVIDER;
    }
    
    static {
        Krb5NameElement.CHAR_ENCODING = "UTF-8";
    }
}
