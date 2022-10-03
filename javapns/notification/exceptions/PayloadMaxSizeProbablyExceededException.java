package javapns.notification.exceptions;

import org.json.JSONException;

public class PayloadMaxSizeProbablyExceededException extends JSONException
{
    private static final long serialVersionUID = 580227446786047134L;
    
    public PayloadMaxSizeProbablyExceededException() {
        super("Total payload size will most likely exceed allowed limit");
    }
    
    public PayloadMaxSizeProbablyExceededException(final int maxSize) {
        super(String.format("Total payload size will most likely exceed allowed limit (%s bytes max)", maxSize));
    }
    
    public PayloadMaxSizeProbablyExceededException(final int maxSize, final int estimatedSize) {
        super(String.format("Total payload size will most likely exceed allowed limit (estimated to become %s bytes, limit is %s)", estimatedSize, maxSize));
    }
    
    public PayloadMaxSizeProbablyExceededException(final String message) {
        super(message);
    }
}
