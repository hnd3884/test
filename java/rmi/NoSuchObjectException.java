package java.rmi;

public class NoSuchObjectException extends RemoteException
{
    private static final long serialVersionUID = 6619395951570472985L;
    
    public NoSuchObjectException(final String s) {
        super(s);
    }
}
