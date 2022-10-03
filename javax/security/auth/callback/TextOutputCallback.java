package javax.security.auth.callback;

import java.io.Serializable;

public class TextOutputCallback implements Callback, Serializable
{
    private static final long serialVersionUID = 1689502495511663102L;
    public static final int INFORMATION = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    private int messageType;
    private String message;
    
    public TextOutputCallback(final int messageType, final String message) {
        if ((messageType != 0 && messageType != 1 && messageType != 2) || message == null || message.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.messageType = messageType;
        this.message = message;
    }
    
    public int getMessageType() {
        return this.messageType;
    }
    
    public String getMessage() {
        return this.message;
    }
}
