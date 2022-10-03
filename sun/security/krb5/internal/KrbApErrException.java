package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KrbApErrException extends KrbException
{
    private static final long serialVersionUID = 7545264413323118315L;
    
    public KrbApErrException(final int n) {
        super(n);
    }
    
    public KrbApErrException(final int n, final String s) {
        super(n, s);
    }
}
