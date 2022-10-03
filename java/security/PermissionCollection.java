package java.security;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.io.Serializable;

public abstract class PermissionCollection implements Serializable
{
    private static final long serialVersionUID = -6727011328946861783L;
    private volatile boolean readOnly;
    
    public abstract void add(final Permission p0);
    
    public abstract boolean implies(final Permission p0);
    
    public abstract Enumeration<Permission> elements();
    
    public void setReadOnly() {
        this.readOnly = true;
    }
    
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    @Override
    public String toString() {
        final Enumeration<Permission> elements = this.elements();
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString() + " (\n");
        while (elements.hasMoreElements()) {
            try {
                sb.append(" ");
                sb.append(elements.nextElement().toString());
                sb.append("\n");
            }
            catch (final NoSuchElementException ex) {}
        }
        sb.append(")\n");
        return sb.toString();
    }
}
