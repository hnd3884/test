package javax.naming.spi;

import javax.naming.InvalidNameException;
import javax.naming.CompositeName;
import javax.naming.Name;
import java.io.Serializable;

public class ResolveResult implements Serializable
{
    protected Object resolvedObj;
    protected Name remainingName;
    private static final long serialVersionUID = -4552108072002407559L;
    
    protected ResolveResult() {
        this.resolvedObj = null;
        this.remainingName = null;
    }
    
    public ResolveResult(final Object resolvedObj, final String s) {
        this.resolvedObj = resolvedObj;
        try {
            this.remainingName = new CompositeName(s);
        }
        catch (final InvalidNameException ex) {}
    }
    
    public ResolveResult(final Object resolvedObj, final Name remainingName) {
        this.resolvedObj = resolvedObj;
        this.setRemainingName(remainingName);
    }
    
    public Name getRemainingName() {
        return this.remainingName;
    }
    
    public Object getResolvedObj() {
        return this.resolvedObj;
    }
    
    public void setRemainingName(final Name name) {
        if (name != null) {
            this.remainingName = (Name)name.clone();
        }
        else {
            this.remainingName = null;
        }
    }
    
    public void appendRemainingName(final Name name) {
        if (name != null) {
            if (this.remainingName != null) {
                try {
                    this.remainingName.addAll(name);
                }
                catch (final InvalidNameException ex) {}
            }
            else {
                this.remainingName = (Name)name.clone();
            }
        }
    }
    
    public void appendRemainingComponent(final String s) {
        if (s != null) {
            final CompositeName compositeName = new CompositeName();
            try {
                compositeName.add(s);
            }
            catch (final InvalidNameException ex) {}
            this.appendRemainingName(compositeName);
        }
    }
    
    public void setResolvedObj(final Object resolvedObj) {
        this.resolvedObj = resolvedObj;
    }
}
