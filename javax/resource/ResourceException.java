package javax.resource;

public class ResourceException extends Exception
{
    private String errorCode;
    private Exception linkedException;
    
    public ResourceException(final String reason) {
        super(reason);
        this.errorCode = null;
        this.linkedException = null;
    }
    
    public ResourceException(final String reason, final String errorCode) {
        super(reason);
        this.errorCode = null;
        this.linkedException = null;
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public void setLinkedException(final Exception linkedException) {
        this.linkedException = linkedException;
    }
    
    public Exception getLinkedException() {
        return this.linkedException;
    }
}
