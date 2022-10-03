package sun.security.jgss;

import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;

public class GSSExceptionImpl extends GSSException
{
    private static final long serialVersionUID = 4251197939069005575L;
    private String majorMessage;
    
    GSSExceptionImpl(final int n, final Oid oid) {
        super(n);
        this.majorMessage = super.getMajorString() + ": " + oid;
    }
    
    public GSSExceptionImpl(final int n, final String majorMessage) {
        super(n);
        this.majorMessage = majorMessage;
    }
    
    public GSSExceptionImpl(final int n, final Exception ex) {
        super(n);
        this.initCause(ex);
    }
    
    public GSSExceptionImpl(final int n, final String s, final Exception ex) {
        this(n, s);
        this.initCause(ex);
    }
    
    @Override
    public String getMessage() {
        if (this.majorMessage != null) {
            return this.majorMessage;
        }
        return super.getMessage();
    }
}
