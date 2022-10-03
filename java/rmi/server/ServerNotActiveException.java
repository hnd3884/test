package java.rmi.server;

public class ServerNotActiveException extends Exception
{
    private static final long serialVersionUID = 4687940720827538231L;
    
    public ServerNotActiveException() {
    }
    
    public ServerNotActiveException(final String s) {
        super(s);
    }
}
