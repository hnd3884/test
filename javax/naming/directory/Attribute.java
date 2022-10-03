package javax.naming.directory;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import java.io.Serializable;

public interface Attribute extends Cloneable, Serializable
{
    public static final long serialVersionUID = 8707690322213556804L;
    
    NamingEnumeration<?> getAll() throws NamingException;
    
    Object get() throws NamingException;
    
    int size();
    
    String getID();
    
    boolean contains(final Object p0);
    
    boolean add(final Object p0);
    
    boolean remove(final Object p0);
    
    void clear();
    
    DirContext getAttributeSyntaxDefinition() throws NamingException;
    
    DirContext getAttributeDefinition() throws NamingException;
    
    Object clone();
    
    boolean isOrdered();
    
    Object get(final int p0) throws NamingException;
    
    Object remove(final int p0);
    
    void add(final int p0, final Object p1);
    
    Object set(final int p0, final Object p1);
}
