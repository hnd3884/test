package jcifs.smb;

import java.util.Iterator;
import javax.security.auth.kerberos.KerberosTicket;
import java.security.Key;
import javax.security.auth.Subject;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSContext;

class Kerb5Context
{
    private static final String OID = "1.2.840.113554.1.2.2";
    private GSSContext gssContext;
    
    Kerb5Context(final String host, final String service, final String name, final int userLifetime, final int contextLifetime) throws GSSException {
        final GSSManager manager = GSSManager.getInstance();
        Oid oid = null;
        GSSName serviceName = null;
        GSSName clientName = null;
        GSSCredential clientCreds = null;
        oid = new Oid("1.2.840.113554.1.2.2");
        serviceName = manager.createName(service + "@" + host, GSSName.NT_HOSTBASED_SERVICE, oid);
        if (name != null) {
            clientName = manager.createName(name, GSSName.NT_USER_NAME, oid);
            clientCreds = manager.createCredential(clientName, userLifetime, oid, 1);
        }
        this.gssContext = manager.createContext(serviceName, oid, clientCreds, contextLifetime);
    }
    
    GSSContext getGSSContext() {
        return this.gssContext;
    }
    
    Key searchSessionKey(final Subject subject) throws GSSException {
        final MIEName src = new MIEName(this.gssContext.getSrcName().export());
        final MIEName targ = new MIEName(this.gssContext.getTargName().export());
        final Iterator iter = subject.getPrivateCredentials((Class<Object>)KerberosTicket.class).iterator();
        while (iter.hasNext()) {
            final KerberosTicket ticket = iter.next();
            final MIEName client = new MIEName(this.gssContext.getMech(), ticket.getClient().getName());
            final MIEName server = new MIEName(this.gssContext.getMech(), ticket.getServer().getName());
            if (src.equals(client) && targ.equals(server)) {
                return ticket.getSessionKey();
            }
        }
        return null;
    }
    
    public void dispose() throws GSSException {
        if (this.gssContext != null) {
            this.gssContext.dispose();
        }
    }
}
