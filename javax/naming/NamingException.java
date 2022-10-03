package javax.naming;

public class NamingException extends Exception
{
    protected Name resolvedName;
    protected Object resolvedObj;
    protected Name remainingName;
    protected Throwable rootException;
    private static final long serialVersionUID = -1299181962103167177L;
    
    public NamingException(final String s) {
        super(s);
        this.rootException = null;
        final Name name = null;
        this.remainingName = name;
        this.resolvedName = name;
        this.resolvedObj = null;
    }
    
    public NamingException() {
        this.rootException = null;
        final Name name = null;
        this.remainingName = name;
        this.resolvedName = name;
        this.resolvedObj = null;
    }
    
    public Name getResolvedName() {
        return this.resolvedName;
    }
    
    public Name getRemainingName() {
        return this.remainingName;
    }
    
    public Object getResolvedObj() {
        return this.resolvedObj;
    }
    
    public String getExplanation() {
        return this.getMessage();
    }
    
    public void setResolvedName(final Name name) {
        if (name != null) {
            this.resolvedName = (Name)name.clone();
        }
        else {
            this.resolvedName = null;
        }
    }
    
    public void setRemainingName(final Name name) {
        if (name != null) {
            this.remainingName = (Name)name.clone();
        }
        else {
            this.remainingName = null;
        }
    }
    
    public void setResolvedObj(final Object resolvedObj) {
        this.resolvedObj = resolvedObj;
    }
    
    public void appendRemainingComponent(final String s) {
        if (s != null) {
            try {
                if (this.remainingName == null) {
                    this.remainingName = new CompositeName();
                }
                this.remainingName.add(s);
            }
            catch (final NamingException ex) {
                throw new IllegalArgumentException(ex.toString());
            }
        }
    }
    
    public void appendRemainingName(final Name name) {
        if (name == null) {
            return;
        }
        if (this.remainingName != null) {
            try {
                this.remainingName.addAll(name);
                return;
            }
            catch (final NamingException ex) {
                throw new IllegalArgumentException(ex.toString());
            }
        }
        this.remainingName = (Name)name.clone();
    }
    
    public Throwable getRootCause() {
        return this.rootException;
    }
    
    public void setRootCause(final Throwable rootException) {
        if (rootException != this) {
            this.rootException = rootException;
        }
    }
    
    @Override
    public Throwable getCause() {
        return this.getRootCause();
    }
    
    @Override
    public Throwable initCause(final Throwable rootCause) {
        super.initCause(rootCause);
        this.setRootCause(rootCause);
        return this;
    }
    
    @Override
    public String toString() {
        String s = super.toString();
        if (this.rootException != null) {
            s = s + " [Root exception is " + this.rootException + "]";
        }
        if (this.remainingName != null) {
            s = s + "; remaining name '" + this.remainingName + "'";
        }
        return s;
    }
    
    public String toString(final boolean b) {
        if (!b || this.resolvedObj == null) {
            return this.toString();
        }
        return this.toString() + "; resolved object " + this.resolvedObj;
    }
}
