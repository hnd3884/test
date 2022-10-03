package java.rmi.server;

public class ServerCloneException extends CloneNotSupportedException
{
    public Exception detail;
    private static final long serialVersionUID = 6617456357664815945L;
    
    public ServerCloneException(final String s) {
        super(s);
        this.initCause(null);
    }
    
    public ServerCloneException(final String s, final Exception detail) {
        super(s);
        this.initCause(null);
        this.detail = detail;
    }
    
    @Override
    public String getMessage() {
        if (this.detail == null) {
            return super.getMessage();
        }
        return super.getMessage() + "; nested exception is: \n\t" + this.detail.toString();
    }
    
    @Override
    public Throwable getCause() {
        return this.detail;
    }
}
