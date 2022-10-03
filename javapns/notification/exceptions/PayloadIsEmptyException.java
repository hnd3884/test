package javapns.notification.exceptions;

public class PayloadIsEmptyException extends Exception
{
    private static final long serialVersionUID = 8142083854784121700L;
    
    public PayloadIsEmptyException() {
        super("Payload is empty");
    }
    
    public PayloadIsEmptyException(final String message) {
        super(message);
    }
}
