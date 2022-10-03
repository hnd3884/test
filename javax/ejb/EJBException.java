package javax.ejb;

import java.io.PrintWriter;
import java.io.PrintStream;

public class EJBException extends RuntimeException
{
    private Exception causeException;
    
    public EJBException() {
        this.causeException = null;
    }
    
    public EJBException(final Exception ex) {
        this.causeException = ex;
    }
    
    public EJBException(final String message) {
        super(message);
        this.causeException = null;
    }
    
    public EJBException(final String message, final Exception ex) {
        super(message);
        this.causeException = ex;
    }
    
    public Exception getCausedByException() {
        return this.causeException;
    }
    
    public String getMessage() {
        final StringBuffer s = new StringBuffer();
        s.append(super.getMessage());
        if (this.causeException != null) {
            s.append("; CausedByException is:\n\t");
            s.append(this.causeException.getMessage());
        }
        return s.toString();
    }
    
    public void printStackTrace() {
        super.printStackTrace();
        if (this.causeException != null) {
            this.causeException.printStackTrace();
        }
    }
    
    public void printStackTrace(final PrintStream ps) {
        super.printStackTrace(ps);
        if (this.causeException != null) {
            this.causeException.printStackTrace(ps);
        }
    }
    
    public void printStackTrace(final PrintWriter pw) {
        super.printStackTrace(pw);
        if (this.causeException != null) {
            this.causeException.printStackTrace(pw);
        }
    }
}
