package sun.security.jgss.krb5;

import sun.security.krb5.Credentials;
import javax.security.auth.kerberos.KerberosTicket;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import javax.security.auth.DestroyFailedException;
import java.security.Provider;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import sun.security.krb5.internal.Ticket;

public class Krb5ProxyCredential implements Krb5CredElement
{
    public final Krb5InitCredential self;
    private final Krb5NameElement client;
    public final Ticket tkt;
    
    Krb5ProxyCredential(final Krb5InitCredential self, final Krb5NameElement client, final Ticket tkt) {
        this.self = self;
        this.tkt = tkt;
        this.client = client;
    }
    
    @Override
    public final Krb5NameElement getName() throws GSSException {
        return this.client;
    }
    
    @Override
    public int getInitLifetime() throws GSSException {
        return this.self.getInitLifetime();
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
    
    @Override
    public void dispose() throws GSSException {
        try {
            this.self.destroy();
        }
        catch (final DestroyFailedException ex) {
            new GSSException(11, -1, "Could not destroy credentials - " + ex.getMessage()).initCause(ex);
        }
    }
    
    @Override
    public GSSCredentialSpi impersonate(final GSSNameSpi gssNameSpi) throws GSSException {
        throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
    }
    
    static Krb5CredElement tryImpersonation(final GSSCaller gssCaller, final Krb5InitCredential krb5InitCredential) throws GSSException {
        try {
            final KerberosTicket proxyTicket = krb5InitCredential.proxyTicket;
            if (proxyTicket != null) {
                final Credentials ticketToCreds = Krb5Util.ticketToCreds(proxyTicket);
                return new Krb5ProxyCredential(krb5InitCredential, Krb5NameElement.getInstance(ticketToCreds.getClient()), ticketToCreds.getTicket());
            }
            return krb5InitCredential;
        }
        catch (final KrbException | IOException ex) {
            throw new GSSException(9, -1, "Cannot create proxy credential");
        }
    }
}
