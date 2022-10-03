package javax.naming.ldap;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.ReferralException;

public abstract class LdapReferralException extends ReferralException
{
    private static final long serialVersionUID = -1668992791764950804L;
    
    protected LdapReferralException(final String s) {
        super(s);
    }
    
    protected LdapReferralException() {
    }
    
    @Override
    public abstract Context getReferralContext() throws NamingException;
    
    @Override
    public abstract Context getReferralContext(final Hashtable<?, ?> p0) throws NamingException;
    
    public abstract Context getReferralContext(final Hashtable<?, ?> p0, final Control[] p1) throws NamingException;
}
