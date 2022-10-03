package java.rmi;

public class AlreadyBoundException extends Exception
{
    private static final long serialVersionUID = 9218657361741657110L;
    
    public AlreadyBoundException() {
    }
    
    public AlreadyBoundException(final String s) {
        super(s);
    }
}
