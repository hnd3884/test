package sun.security.jgss.krb5;

import org.ietf.jgss.GSSName;
import sun.security.jgss.SunProvider;
import sun.security.jgss.spi.GSSContextSpi;
import java.security.Permission;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import java.util.Vector;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.GSSCaller;
import org.ietf.jgss.Oid;
import java.security.Provider;
import sun.security.jgss.spi.MechanismFactory;

public final class Krb5MechFactory implements MechanismFactory
{
    private static final boolean DEBUG;
    static final Provider PROVIDER;
    static final Oid GSS_KRB5_MECH_OID;
    static final Oid NT_GSS_KRB5_PRINCIPAL;
    private static Oid[] nameTypes;
    private final GSSCaller caller;
    
    private static Krb5CredElement getCredFromSubject(final GSSNameSpi gssNameSpi, final boolean b) throws GSSException {
        final Vector<GSSCredentialSpi> searchSubject = GSSUtil.searchSubject(gssNameSpi, Krb5MechFactory.GSS_KRB5_MECH_OID, b, (Class<? extends GSSCredentialSpi>)(b ? Krb5InitCredential.class : Krb5AcceptCredential.class));
        final Krb5CredElement krb5CredElement = (searchSubject == null || searchSubject.isEmpty()) ? null : searchSubject.firstElement();
        if (krb5CredElement != null) {
            if (b) {
                checkInitCredPermission((Krb5NameElement)krb5CredElement.getName());
            }
            else {
                checkAcceptCredPermission((Krb5NameElement)krb5CredElement.getName(), gssNameSpi);
            }
        }
        return krb5CredElement;
    }
    
    public Krb5MechFactory(final GSSCaller caller) {
        this.caller = caller;
    }
    
    @Override
    public GSSNameSpi getNameElement(final String s, final Oid oid) throws GSSException {
        return Krb5NameElement.getInstance(s, oid);
    }
    
    @Override
    public GSSNameSpi getNameElement(final byte[] array, final Oid oid) throws GSSException {
        return Krb5NameElement.getInstance(new String(array), oid);
    }
    
    @Override
    public GSSCredentialSpi getCredentialElement(GSSNameSpi instance, final int n, final int n2, final int n3) throws GSSException {
        if (instance != null && !(instance instanceof Krb5NameElement)) {
            instance = Krb5NameElement.getInstance(instance.toString(), instance.getStringNameType());
        }
        Krb5CredElement krb5CredElement = getCredFromSubject(instance, n3 != 2);
        if (krb5CredElement == null) {
            if (n3 == 1 || n3 == 0) {
                krb5CredElement = Krb5ProxyCredential.tryImpersonation(this.caller, Krb5InitCredential.getInstance(this.caller, (Krb5NameElement)instance, n));
                checkInitCredPermission((Krb5NameElement)krb5CredElement.getName());
            }
            else {
                if (n3 != 2) {
                    throw new GSSException(11, -1, "Unknown usage mode requested");
                }
                krb5CredElement = Krb5AcceptCredential.getInstance(this.caller, (Krb5NameElement)instance);
                checkAcceptCredPermission((Krb5NameElement)krb5CredElement.getName(), instance);
            }
        }
        return krb5CredElement;
    }
    
    public static void checkInitCredPermission(final Krb5NameElement krb5NameElement) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final String realmAsString = krb5NameElement.getKrb5PrincipalName().getRealmAsString();
            final ServicePermission servicePermission = new ServicePermission(new String("krbtgt/" + realmAsString + '@' + realmAsString), "initiate");
            try {
                securityManager.checkPermission(servicePermission);
            }
            catch (final SecurityException ex) {
                if (Krb5MechFactory.DEBUG) {
                    System.out.println("Permission to initiatekerberos init credential" + ex.getMessage());
                }
                throw ex;
            }
        }
    }
    
    public static void checkAcceptCredPermission(final Krb5NameElement krb5NameElement, final GSSNameSpi gssNameSpi) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null && krb5NameElement != null) {
            final ServicePermission servicePermission = new ServicePermission(krb5NameElement.getKrb5PrincipalName().getName(), "accept");
            try {
                securityManager.checkPermission(servicePermission);
            }
            catch (final SecurityException ex) {
                if (gssNameSpi == null) {
                    ex = new SecurityException("No permission to acquire Kerberos accept credential");
                }
                throw ex;
            }
        }
    }
    
    @Override
    public GSSContextSpi getMechanismContext(GSSNameSpi instance, GSSCredentialSpi credentialElement, final int n) throws GSSException {
        if (instance != null && !(instance instanceof Krb5NameElement)) {
            instance = Krb5NameElement.getInstance(instance.toString(), instance.getStringNameType());
        }
        if (credentialElement == null) {
            credentialElement = this.getCredentialElement(null, n, 0, 1);
        }
        return new Krb5Context(this.caller, (Krb5NameElement)instance, (Krb5CredElement)credentialElement, n);
    }
    
    @Override
    public GSSContextSpi getMechanismContext(GSSCredentialSpi credentialElement) throws GSSException {
        if (credentialElement == null) {
            credentialElement = this.getCredentialElement(null, 0, Integer.MAX_VALUE, 2);
        }
        return new Krb5Context(this.caller, (Krb5CredElement)credentialElement);
    }
    
    @Override
    public GSSContextSpi getMechanismContext(final byte[] array) throws GSSException {
        return new Krb5Context(this.caller, array);
    }
    
    @Override
    public final Oid getMechanismOid() {
        return Krb5MechFactory.GSS_KRB5_MECH_OID;
    }
    
    @Override
    public Provider getProvider() {
        return Krb5MechFactory.PROVIDER;
    }
    
    @Override
    public Oid[] getNameTypes() {
        return Krb5MechFactory.nameTypes;
    }
    
    private static Oid createOid(final String s) {
        Oid oid = null;
        try {
            oid = new Oid(s);
        }
        catch (final GSSException ex) {}
        return oid;
    }
    
    static {
        DEBUG = Krb5Util.DEBUG;
        PROVIDER = new SunProvider();
        GSS_KRB5_MECH_OID = createOid("1.2.840.113554.1.2.2");
        NT_GSS_KRB5_PRINCIPAL = createOid("1.2.840.113554.1.2.2.1");
        Krb5MechFactory.nameTypes = new Oid[] { GSSName.NT_USER_NAME, GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL };
    }
}
