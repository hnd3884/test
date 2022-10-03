package javax.naming.event;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.Name;
import javax.naming.directory.DirContext;

public interface EventDirContext extends EventContext, DirContext
{
    void addNamingListener(final Name p0, final String p1, final SearchControls p2, final NamingListener p3) throws NamingException;
    
    void addNamingListener(final String p0, final String p1, final SearchControls p2, final NamingListener p3) throws NamingException;
    
    void addNamingListener(final Name p0, final String p1, final Object[] p2, final SearchControls p3, final NamingListener p4) throws NamingException;
    
    void addNamingListener(final String p0, final String p1, final Object[] p2, final SearchControls p3, final NamingListener p4) throws NamingException;
}
