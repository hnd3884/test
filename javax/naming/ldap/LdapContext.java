package javax.naming.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public interface LdapContext extends DirContext
{
    public static final String CONTROL_FACTORIES = "java.naming.factory.control";
    
    ExtendedResponse extendedOperation(final ExtendedRequest p0) throws NamingException;
    
    LdapContext newInstance(final Control[] p0) throws NamingException;
    
    void reconnect(final Control[] p0) throws NamingException;
    
    Control[] getConnectControls() throws NamingException;
    
    void setRequestControls(final Control[] p0) throws NamingException;
    
    Control[] getRequestControls() throws NamingException;
    
    Control[] getResponseControls() throws NamingException;
}
