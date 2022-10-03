package sun.security.jgss.krb5;

import sun.security.krb5.Credentials;
import sun.security.jgss.spi.GSSCredentialSpi;
import javax.security.auth.DestroyFailedException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import java.security.Provider;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import java.security.PrivilegedActionException;
import org.ietf.jgss.GSSException;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import sun.security.jgss.GSSCaller;

public class Krb5AcceptCredential implements Krb5CredElement
{
    private final Krb5NameElement name;
    private final ServiceCreds screds;
    
    private Krb5AcceptCredential(final Krb5NameElement name, final ServiceCreds screds) {
        this.name = name;
        this.screds = screds;
    }
    
    static Krb5AcceptCredential getInstance(final GSSCaller gssCaller, Krb5NameElement instance) throws GSSException {
        final String s = (instance == null) ? null : instance.getKrb5PrincipalName().getName();
        final AccessControlContext context = AccessController.getContext();
        ServiceCreds serviceCreds;
        try {
            serviceCreds = AccessController.doPrivileged((PrivilegedExceptionAction<ServiceCreds>)new PrivilegedExceptionAction<ServiceCreds>() {
                @Override
                public ServiceCreds run() throws Exception {
                    return Krb5Util.getServiceCreds((gssCaller == GSSCaller.CALLER_UNKNOWN) ? GSSCaller.CALLER_ACCEPT : gssCaller, s, context);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            final GSSException ex2 = new GSSException(13, -1, "Attempt to obtain new ACCEPT credentials failed!");
            ex2.initCause(ex.getException());
            throw ex2;
        }
        if (serviceCreds == null) {
            throw new GSSException(13, -1, "Failed to find any Kerberos credentails");
        }
        if (instance == null) {
            final String name = serviceCreds.getName();
            if (name != null) {
                instance = Krb5NameElement.getInstance(name, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
            }
        }
        return new Krb5AcceptCredential(instance, serviceCreds);
    }
    
    @Override
    public final GSSNameSpi getName() throws GSSException {
        return this.name;
    }
    
    @Override
    public int getInitLifetime() throws GSSException {
        return 0;
    }
    
    @Override
    public int getAcceptLifetime() throws GSSException {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean isInitiatorCredential() throws GSSException {
        return false;
    }
    
    @Override
    public boolean isAcceptorCredential() throws GSSException {
        return true;
    }
    
    @Override
    public final Oid getMechanism() {
        return Krb5MechFactory.GSS_KRB5_MECH_OID;
    }
    
    @Override
    public final Provider getProvider() {
        return Krb5MechFactory.PROVIDER;
    }
    
    public EncryptionKey[] getKrb5EncryptionKeys(final PrincipalName principalName) {
        return this.screds.getEKeys(principalName);
    }
    
    @Override
    public void dispose() throws GSSException {
        try {
            this.destroy();
        }
        catch (final DestroyFailedException ex) {
            new GSSException(11, -1, "Could not destroy credentials - " + ex.getMessage()).initCause(ex);
        }
    }
    
    public void destroy() throws DestroyFailedException {
        this.screds.destroy();
    }
    
    @Override
    public GSSCredentialSpi impersonate(final GSSNameSpi gssNameSpi) throws GSSException {
        final Credentials initCred = this.screds.getInitCred();
        if (initCred != null) {
            return Krb5InitCredential.getInstance(this.name, initCred).impersonate(gssNameSpi);
        }
        throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
    }
}
