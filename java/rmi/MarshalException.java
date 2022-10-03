package java.rmi;

public class MarshalException extends RemoteException
{
    private static final long serialVersionUID = 6223554758134037936L;
    
    public MarshalException(final String s) {
        super(s);
    }
    
    public MarshalException(final String s, final Exception ex) {
        super(s, ex);
    }
}
