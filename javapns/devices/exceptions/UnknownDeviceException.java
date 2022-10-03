package javapns.devices.exceptions;

public class UnknownDeviceException extends Exception
{
    private static final long serialVersionUID = -322193098126184434L;
    private final String message;
    
    public UnknownDeviceException() {
        this.message = "Unknown client";
    }
    
    public UnknownDeviceException(final String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return this.message;
    }
}
