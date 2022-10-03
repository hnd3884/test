package javax.naming.directory;

import javax.naming.NamingEnumeration;
import java.io.Serializable;

public interface Attributes extends Cloneable, Serializable
{
    boolean isCaseIgnored();
    
    int size();
    
    Attribute get(final String p0);
    
    NamingEnumeration<? extends Attribute> getAll();
    
    NamingEnumeration<String> getIDs();
    
    Attribute put(final String p0, final Object p1);
    
    Attribute put(final Attribute p0);
    
    Attribute remove(final String p0);
    
    Object clone();
}
