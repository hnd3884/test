package java.rmi;

import java.io.IOException;

public class RemoteException extends IOException
{
    private static final long serialVersionUID = -5148567311918794206L;
    public Throwable detail;
    
    public RemoteException() {
        this.initCause(null);
    }
    
    public RemoteException(final String s) {
        super(s);
        this.initCause(null);
    }
    
    public RemoteException(final String s, final Throwable detail) {
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
