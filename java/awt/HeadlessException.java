package java.awt;

public class HeadlessException extends UnsupportedOperationException
{
    private static final long serialVersionUID = 167183644944358563L;
    
    public HeadlessException() {
    }
    
    public HeadlessException(final String s) {
        super(s);
    }
    
    @Override
    public String getMessage() {
        final String message = super.getMessage();
        final String headlessMessage = GraphicsEnvironment.getHeadlessMessage();
        if (message == null) {
            return headlessMessage;
        }
        if (headlessMessage == null) {
            return message;
        }
        return message + headlessMessage;
    }
}
