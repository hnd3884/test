package javax.naming.ldap;

import javax.naming.Context;
import javax.naming.NotContextException;
import javax.naming.NoInitialContextException;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.directory.InitialDirContext;

public class InitialLdapContext extends InitialDirContext implements LdapContext
{
    private static final String BIND_CONTROLS_PROPERTY = "java.naming.ldap.control.connect";
    
    public InitialLdapContext() throws NamingException {
        super(null);
    }
    
    public InitialLdapContext(final Hashtable<?, ?> hashtable, final Control[] array) throws NamingException {
        super(true);
        final Hashtable hashtable2 = (Hashtable)((hashtable == null) ? new Hashtable(11) : hashtable.clone());
        if (array != null) {
            final Control[] array2 = new Control[array.length];
            System.arraycopy(array, 0, array2, 0, array.length);
            hashtable2.put("java.naming.ldap.control.connect", array2);
        }
        hashtable2.put("java.naming.ldap.version", "3");
        this.init(hashtable2);
    }
    
    private LdapContext getDefaultLdapInitCtx() throws NamingException {
        final Context defaultInitCtx = this.getDefaultInitCtx();
        if (defaultInitCtx instanceof LdapContext) {
            return (LdapContext)defaultInitCtx;
        }
        if (defaultInitCtx == null) {
            throw new NoInitialContextException();
        }
        throw new NotContextException("Not an instance of LdapContext");
    }
    
    @Override
    public ExtendedResponse extendedOperation(final ExtendedRequest extendedRequest) throws NamingException {
        return this.getDefaultLdapInitCtx().extendedOperation(extendedRequest);
    }
    
    @Override
    public LdapContext newInstance(final Control[] array) throws NamingException {
        return this.getDefaultLdapInitCtx().newInstance(array);
    }
    
    @Override
    public void reconnect(final Control[] array) throws NamingException {
        this.getDefaultLdapInitCtx().reconnect(array);
    }
    
    @Override
    public Control[] getConnectControls() throws NamingException {
        return this.getDefaultLdapInitCtx().getConnectControls();
    }
    
    @Override
    public void setRequestControls(final Control[] requestControls) throws NamingException {
        this.getDefaultLdapInitCtx().setRequestControls(requestControls);
    }
    
    @Override
    public Control[] getRequestControls() throws NamingException {
        return this.getDefaultLdapInitCtx().getRequestControls();
    }
    
    @Override
    public Control[] getResponseControls() throws NamingException {
        return this.getDefaultLdapInitCtx().getResponseControls();
    }
}
