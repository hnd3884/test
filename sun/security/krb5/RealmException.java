package sun.security.krb5;

public class RealmException extends KrbException
{
    private static final long serialVersionUID = -9100385213693792864L;
    
    public RealmException(final int n) {
        super(n);
    }
    
    public RealmException(final String s) {
        super(s);
    }
    
    public RealmException(final int n, final String s) {
        super(n, s);
    }
    
    public RealmException(final Throwable t) {
        super(t);
    }
}
