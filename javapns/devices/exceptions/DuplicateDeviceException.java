package javapns.devices.exceptions;

public class DuplicateDeviceException extends Exception
{
    private static final long serialVersionUID = -7116507420722667479L;
    private final String message;
    
    public DuplicateDeviceException() {
        this.message = "Client already exists";
    }
    
    public DuplicateDeviceException(final String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return this.message;
    }
}
