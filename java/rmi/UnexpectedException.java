package java.rmi;

public class UnexpectedException extends RemoteException
{
    private static final long serialVersionUID = 1800467484195073863L;
    
    public UnexpectedException(final String s) {
        super(s);
    }
    
    public UnexpectedException(final String s, final Exception ex) {
        super(s, ex);
    }
}
