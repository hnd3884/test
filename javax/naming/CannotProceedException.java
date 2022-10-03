package javax.naming;

import java.util.Hashtable;

public class CannotProceedException extends NamingException
{
    protected Name remainingNewName;
    protected Hashtable<?, ?> environment;
    protected Name altName;
    protected Context altNameCtx;
    private static final long serialVersionUID = 1219724816191576813L;
    
    public CannotProceedException(final String s) {
        super(s);
        this.remainingNewName = null;
        this.environment = null;
        this.altName = null;
        this.altNameCtx = null;
    }
    
    public CannotProceedException() {
        this.remainingNewName = null;
        this.environment = null;
        this.altName = null;
        this.altNameCtx = null;
    }
    
    public Hashtable<?, ?> getEnvironment() {
        return this.environment;
    }
    
    public void setEnvironment(final Hashtable<?, ?> environment) {
        this.environment = environment;
    }
    
    public Name getRemainingNewName() {
        return this.remainingNewName;
    }
    
    public void setRemainingNewName(final Name name) {
        if (name != null) {
            this.remainingNewName = (Name)name.clone();
        }
        else {
            this.remainingNewName = null;
        }
    }
    
    public Name getAltName() {
        return this.altName;
    }
    
    public void setAltName(final Name altName) {
        this.altName = altName;
    }
    
    public Context getAltNameCtx() {
        return this.altNameCtx;
    }
    
    public void setAltNameCtx(final Context altNameCtx) {
        this.altNameCtx = altNameCtx;
    }
}
