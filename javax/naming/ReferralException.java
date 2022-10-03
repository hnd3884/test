package javax.naming;

import java.util.Hashtable;

public abstract class ReferralException extends NamingException
{
    private static final long serialVersionUID = -2881363844695698876L;
    
    protected ReferralException(final String s) {
        super(s);
    }
    
    protected ReferralException() {
    }
    
    public abstract Object getReferralInfo();
    
    public abstract Context getReferralContext() throws NamingException;
    
    public abstract Context getReferralContext(final Hashtable<?, ?> p0) throws NamingException;
    
    public abstract boolean skipReferral();
    
    public abstract void retryReferral();
}
