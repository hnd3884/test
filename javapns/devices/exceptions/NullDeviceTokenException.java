package javapns.devices.exceptions;

public class NullDeviceTokenException extends Exception
{
    private static final long serialVersionUID = 208339461070934305L;
    private final String message;
    
    public NullDeviceTokenException() {
        this.message = "Client already exists";
    }
    
    public NullDeviceTokenException(final String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return this.message;
    }
}
