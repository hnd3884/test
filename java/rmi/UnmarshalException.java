package java.rmi;

public class UnmarshalException extends RemoteException
{
    private static final long serialVersionUID = 594380845140740218L;
    
    public UnmarshalException(final String s) {
        super(s);
    }
    
    public UnmarshalException(final String s, final Exception ex) {
        super(s, ex);
    }
}
