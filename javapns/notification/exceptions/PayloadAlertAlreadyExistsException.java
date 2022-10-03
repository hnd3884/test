package javapns.notification.exceptions;

import org.json.JSONException;

public class PayloadAlertAlreadyExistsException extends JSONException
{
    private static final long serialVersionUID = -4514511954076864373L;
    
    public PayloadAlertAlreadyExistsException() {
        super("Payload alert already exists");
    }
    
    public PayloadAlertAlreadyExistsException(final String message) {
        super(message);
    }
}
