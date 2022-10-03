package java.rmi;

public class UnknownHostException extends RemoteException
{
    private static final long serialVersionUID = -8152710247442114228L;
    
    public UnknownHostException(final String s) {
        super(s);
    }
    
    public UnknownHostException(final String s, final Exception ex) {
        super(s, ex);
    }
}
