package javapns.devices.exceptions;

public class NullIdException extends Exception
{
    private static final long serialVersionUID = -2842793759970312540L;
    private final String message;
    
    public NullIdException() {
        this.message = "Client already exists";
    }
    
    public NullIdException(final String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return this.message;
    }
}
