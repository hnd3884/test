package javax.naming;

import java.util.Enumeration;
import java.io.Serializable;

public interface Name extends Cloneable, Serializable, Comparable<Object>
{
    public static final long serialVersionUID = -3617482732056931635L;
    
    Object clone();
    
    int compareTo(final Object p0);
    
    int size();
    
    boolean isEmpty();
    
    Enumeration<String> getAll();
    
    String get(final int p0);
    
    Name getPrefix(final int p0);
    
    Name getSuffix(final int p0);
    
    boolean startsWith(final Name p0);
    
    boolean endsWith(final Name p0);
    
    Name addAll(final Name p0) throws InvalidNameException;
    
    Name addAll(final int p0, final Name p1) throws InvalidNameException;
    
    Name add(final String p0) throws InvalidNameException;
    
    Name add(final int p0, final String p1) throws InvalidNameException;
    
    Object remove(final int p0) throws InvalidNameException;
}
