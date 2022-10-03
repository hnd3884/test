package sun.security.krb5;

import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;

public interface JavaxSecurityAuthKerberosAccess
{
    sun.security.krb5.internal.ktab.KeyTab keyTabTakeSnapshot(final KeyTab p0);
    
    KerberosPrincipal kerberosTicketGetClientAlias(final KerberosTicket p0);
    
    void kerberosTicketSetClientAlias(final KerberosTicket p0, final KerberosPrincipal p1);
    
    KerberosPrincipal kerberosTicketGetServerAlias(final KerberosTicket p0);
    
    void kerberosTicketSetServerAlias(final KerberosTicket p0, final KerberosPrincipal p1);
    
    KerberosTicket kerberosTicketGetProxy(final KerberosTicket p0);
    
    void kerberosTicketSetProxy(final KerberosTicket p0, final KerberosTicket p1);
}
