package javax.naming.directory;

import javax.naming.NamingException;

public class AttributeModificationException extends NamingException
{
    private ModificationItem[] unexecs;
    private static final long serialVersionUID = 8060676069678710186L;
    
    public AttributeModificationException(final String s) {
        super(s);
        this.unexecs = null;
    }
    
    public AttributeModificationException() {
        this.unexecs = null;
    }
    
    public void setUnexecutedModifications(final ModificationItem[] unexecs) {
        this.unexecs = unexecs;
    }
    
    public ModificationItem[] getUnexecutedModifications() {
        return this.unexecs;
    }
    
    @Override
    public String toString() {
        String s = super.toString();
        if (this.unexecs != null) {
            s = s + "First unexecuted modification: " + this.unexecs[0].toString();
        }
        return s;
    }
}
