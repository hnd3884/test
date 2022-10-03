package sun.security.jgss.krb5;

import sun.security.jgss.spi.GSSCredentialSpi;
import java.security.PrivilegedActionException;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import javax.security.auth.DestroyFailedException;
import java.security.Provider;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptionKey;
import sun.security.jgss.GSSCaller;
import java.io.IOException;
import sun.security.krb5.KrbException;
import org.ietf.jgss.GSSException;
import sun.security.krb5.KerberosSecrets;
import java.net.InetAddress;
import java.util.Date;
import javax.security.auth.kerberos.KerberosPrincipal;
import sun.security.krb5.Credentials;
import javax.security.auth.kerberos.KerberosTicket;

public class Krb5InitCredential extends KerberosTicket implements Krb5CredElement
{
    private static final long serialVersionUID = 7723415700837898232L;
    private Krb5NameElement name;
    private Credentials krb5Credentials;
    public KerberosTicket proxyTicket;
    
    private Krb5InitCredential(final Krb5NameElement name, final byte[] array, final KerberosPrincipal kerberosPrincipal, final KerberosPrincipal kerberosPrincipal2, final KerberosPrincipal kerberosPrincipal3, final KerberosPrincipal kerberosPrincipal4, final byte[] array2, final int n, final boolean[] array3, final Date date, final Date date2, final Date date3, final Date date4, final InetAddress[] array4) throws GSSException {
        super(array, kerberosPrincipal, kerberosPrincipal3, array2, n, array3, date, date2, date3, date4, array4);
        KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketSetClientAlias(this, kerberosPrincipal2);
        KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketSetServerAlias(this, kerberosPrincipal4);
        this.name = name;
        try {
            this.krb5Credentials = new Credentials(array, kerberosPrincipal.getName(), (kerberosPrincipal2 != null) ? kerberosPrincipal2.getName() : null, kerberosPrincipal3.getName(), (kerberosPrincipal4 != null) ? kerberosPrincipal4.getName() : null, array2, n, array3, date, date2, date3, date4, array4);
        }
        catch (final KrbException ex) {
            throw new GSSException(13, -1, ex.getMessage());
        }
        catch (final IOException ex2) {
            throw new GSSException(13, -1, ex2.getMessage());
        }
    }
    
    private Krb5InitCredential(final Krb5NameElement name, final Credentials krb5Credentials, final byte[] array, final KerberosPrincipal kerberosPrincipal, final KerberosPrincipal kerberosPrincipal2, final KerberosPrincipal kerberosPrincipal3, final KerberosPrincipal kerberosPrincipal4, final byte[] array2, final int n, final boolean[] array3, final Date date, final Date date2, final Date date3, final Date date4, final InetAddress[] array4) throws GSSException {
        super(array, kerberosPrincipal, kerberosPrincipal3, array2, n, array3, date, date2, date3, date4, array4);
        KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketSetClientAlias(this, kerberosPrincipal2);
        KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketSetServerAlias(this, kerberosPrincipal4);
        this.name = name;
        this.krb5Credentials = krb5Credentials;
    }
    
    static Krb5InitCredential getInstance(final GSSCaller gssCaller, Krb5NameElement instance, final int n) throws GSSException {
        final KerberosTicket tgt = getTgt(gssCaller, instance, n);
        if (tgt == null) {
            throw new GSSException(13, -1, "Failed to find any Kerberos tgt");
        }
        if (instance == null) {
            instance = Krb5NameElement.getInstance(tgt.getClient().getName(), Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
        }
        final Krb5InitCredential krb5InitCredential = new Krb5InitCredential(instance, tgt.getEncoded(), tgt.getClient(), KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketGetClientAlias(tgt), tgt.getServer(), KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketGetServerAlias(tgt), tgt.getSessionKey().getEncoded(), tgt.getSessionKeyType(), tgt.getFlags(), tgt.getAuthTime(), tgt.getStartTime(), tgt.getEndTime(), tgt.getRenewTill(), tgt.getClientAddresses());
        krb5InitCredential.proxyTicket = KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketGetProxy(tgt);
        return krb5InitCredential;
    }
    
    static Krb5InitCredential getInstance(final Krb5NameElement krb5NameElement, final Credentials credentials) throws GSSException {
        final EncryptionKey sessionKey = credentials.getSessionKey();
        final PrincipalName client = credentials.getClient();
        final PrincipalName clientAlias = credentials.getClientAlias();
        final PrincipalName server = credentials.getServer();
        final PrincipalName serverAlias = credentials.getServerAlias();
        KerberosPrincipal kerberosPrincipal = null;
        KerberosPrincipal kerberosPrincipal2 = null;
        KerberosPrincipal kerberosPrincipal3 = null;
        KerberosPrincipal kerberosPrincipal4 = null;
        Krb5NameElement instance = null;
        if (client != null) {
            final String name = client.getName();
            instance = Krb5NameElement.getInstance(name, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
            kerberosPrincipal = new KerberosPrincipal(name);
        }
        if (clientAlias != null) {
            kerberosPrincipal2 = new KerberosPrincipal(clientAlias.getName());
        }
        if (server != null) {
            kerberosPrincipal3 = new KerberosPrincipal(server.getName(), 2);
        }
        if (serverAlias != null) {
            kerberosPrincipal4 = new KerberosPrincipal(serverAlias.getName());
        }
        return new Krb5InitCredential(instance, credentials, credentials.getEncoded(), kerberosPrincipal, kerberosPrincipal2, kerberosPrincipal3, kerberosPrincipal4, sessionKey.getBytes(), sessionKey.getEType(), credentials.getFlags(), credentials.getAuthTime(), credentials.getStartTime(), credentials.getEndTime(), credentials.getRenewTill(), credentials.getClientAddresses());
    }
    
    @Override
    public final GSSNameSpi getName() throws GSSException {
        return this.name;
    }
    
    @Override
    public int getInitLifetime() throws GSSException {
        final Date endTime = this.getEndTime();
        if (endTime == null) {
            return 0;
        }
        return (int)((endTime.getTime() - System.currentTimeMillis()) / 1000L);
    }
    
    @Override
    public int getAcceptLifetime() throws GSSException {
        return 0;
    }
    
    @Override
    public boolean isInitiatorCredential() throws GSSException {
        return true;
    }
    
    @Override
    public boolean isAcceptorCredential() throws GSSException {
        return false;
    }
    
    @Override
    public final Oid getMechanism() {
        return Krb5MechFactory.GSS_KRB5_MECH_OID;
    }
    
    @Override
    public final Provider getProvider() {
        return Krb5MechFactory.PROVIDER;
    }
    
    Credentials getKrb5Credentials() {
        return this.krb5Credentials;
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
    
    private static KerberosTicket getTgt(final GSSCaller gssCaller, final Krb5NameElement krb5NameElement, final int n) throws GSSException {
        String name;
        if (krb5NameElement != null) {
            name = krb5NameElement.getKrb5PrincipalName().getName();
        }
        else {
            name = null;
        }
        final AccessControlContext context = AccessController.getContext();
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<KerberosTicket>)new PrivilegedExceptionAction<KerberosTicket>() {
                final /* synthetic */ GSSCaller val$realCaller = (gssCaller == GSSCaller.CALLER_UNKNOWN) ? GSSCaller.CALLER_INITIATE : gssCaller;
                
                @Override
                public KerberosTicket run() throws Exception {
                    return Krb5Util.getInitialTicket(this.val$realCaller, name, context);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            final GSSException ex2 = new GSSException(13, -1, "Attempt to obtain new INITIATE credentials failed! (" + ex.getMessage() + ")");
            ex2.initCause(ex.getException());
            throw ex2;
        }
    }
    
    @Override
    public GSSCredentialSpi impersonate(final GSSNameSpi gssNameSpi) throws GSSException {
        try {
            final Krb5NameElement krb5NameElement = (Krb5NameElement)gssNameSpi;
            return new Krb5ProxyCredential(this, krb5NameElement, Credentials.acquireS4U2selfCreds(krb5NameElement.getKrb5PrincipalName(), this.krb5Credentials).getTicket());
        }
        catch (final IOException | KrbException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Attempt to obtain S4U2self credentials failed!");
            ex2.initCause((Throwable)ex);
            throw ex2;
        }
    }
}
