package javax.security.auth.kerberos;

import sun.security.krb5.JavaxSecurityAuthKerberosAccess;

class JavaxSecurityAuthKerberosAccessImpl implements JavaxSecurityAuthKerberosAccess
{
    @Override
    public sun.security.krb5.internal.ktab.KeyTab keyTabTakeSnapshot(final KeyTab keyTab) {
        return keyTab.takeSnapshot();
    }
    
    @Override
    public KerberosPrincipal kerberosTicketGetClientAlias(final KerberosTicket kerberosTicket) {
        return kerberosTicket.clientAlias;
    }
    
    @Override
    public void kerberosTicketSetClientAlias(final KerberosTicket kerberosTicket, final KerberosPrincipal clientAlias) {
        kerberosTicket.clientAlias = clientAlias;
    }
    
    @Override
    public KerberosPrincipal kerberosTicketGetServerAlias(final KerberosTicket kerberosTicket) {
        return kerberosTicket.serverAlias;
    }
    
    @Override
    public void kerberosTicketSetServerAlias(final KerberosTicket kerberosTicket, final KerberosPrincipal serverAlias) {
        kerberosTicket.serverAlias = serverAlias;
    }
    
    @Override
    public KerberosTicket kerberosTicketGetProxy(final KerberosTicket kerberosTicket) {
        return kerberosTicket.proxy;
    }
    
    @Override
    public void kerberosTicketSetProxy(final KerberosTicket kerberosTicket, final KerberosTicket proxy) {
        kerberosTicket.proxy = proxy;
    }
}
