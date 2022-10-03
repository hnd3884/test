package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KrbErrException extends KrbException
{
    private static final long serialVersionUID = 2186533836785448317L;
    
    public KrbErrException(final int n) {
        super(n);
    }
    
    public KrbErrException(final int n, final String s) {
        super(n, s);
    }
}
