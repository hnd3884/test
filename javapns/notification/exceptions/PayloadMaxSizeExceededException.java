package javapns.notification.exceptions;

public class PayloadMaxSizeExceededException extends Exception
{
    private static final long serialVersionUID = 2896151447959250527L;
    
    public PayloadMaxSizeExceededException() {
        super("Total payload size exceeds allowed limit");
    }
    
    public PayloadMaxSizeExceededException(final int maxSize) {
        super(String.format("Total payload size exceeds allowed limit (%s bytes max)", maxSize));
    }
    
    public PayloadMaxSizeExceededException(final int maxSize, final int currentSize) {
        super(String.format("Total payload size exceeds allowed limit (payload is %s bytes, limit is %s)", currentSize, maxSize));
    }
    
    public PayloadMaxSizeExceededException(final String message) {
        super(message);
    }
}
