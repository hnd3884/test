package java.rmi;

public class NotBoundException extends Exception
{
    private static final long serialVersionUID = -1857741824849069317L;
    
    public NotBoundException() {
    }
    
    public NotBoundException(final String s) {
        super(s);
    }
}
