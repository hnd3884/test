package javax.naming.event;

import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.Context;

public interface EventContext extends Context
{
    public static final int OBJECT_SCOPE = 0;
    public static final int ONELEVEL_SCOPE = 1;
    public static final int SUBTREE_SCOPE = 2;
    
    void addNamingListener(final Name p0, final int p1, final NamingListener p2) throws NamingException;
    
    void addNamingListener(final String p0, final int p1, final NamingListener p2) throws NamingException;
    
    void removeNamingListener(final NamingListener p0) throws NamingException;
    
    boolean targetMustExist() throws NamingException;
}
