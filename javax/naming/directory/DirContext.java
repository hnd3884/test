package javax.naming.directory;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.Context;

public interface DirContext extends Context
{
    public static final int ADD_ATTRIBUTE = 1;
    public static final int REPLACE_ATTRIBUTE = 2;
    public static final int REMOVE_ATTRIBUTE = 3;
    
    Attributes getAttributes(final Name p0) throws NamingException;
    
    Attributes getAttributes(final String p0) throws NamingException;
    
    Attributes getAttributes(final Name p0, final String[] p1) throws NamingException;
    
    Attributes getAttributes(final String p0, final String[] p1) throws NamingException;
    
    void modifyAttributes(final Name p0, final int p1, final Attributes p2) throws NamingException;
    
    void modifyAttributes(final String p0, final int p1, final Attributes p2) throws NamingException;
    
    void modifyAttributes(final Name p0, final ModificationItem[] p1) throws NamingException;
    
    void modifyAttributes(final String p0, final ModificationItem[] p1) throws NamingException;
    
    void bind(final Name p0, final Object p1, final Attributes p2) throws NamingException;
    
    void bind(final String p0, final Object p1, final Attributes p2) throws NamingException;
    
    void rebind(final Name p0, final Object p1, final Attributes p2) throws NamingException;
    
    void rebind(final String p0, final Object p1, final Attributes p2) throws NamingException;
    
    DirContext createSubcontext(final Name p0, final Attributes p1) throws NamingException;
    
    DirContext createSubcontext(final String p0, final Attributes p1) throws NamingException;
    
    DirContext getSchema(final Name p0) throws NamingException;
    
    DirContext getSchema(final String p0) throws NamingException;
    
    DirContext getSchemaClassDefinition(final Name p0) throws NamingException;
    
    DirContext getSchemaClassDefinition(final String p0) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final Name p0, final Attributes p1, final String[] p2) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final String p0, final Attributes p1, final String[] p2) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final Name p0, final Attributes p1) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final String p0, final Attributes p1) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final Name p0, final String p1, final SearchControls p2) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final String p0, final String p1, final SearchControls p2) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final Name p0, final String p1, final Object[] p2, final SearchControls p3) throws NamingException;
    
    NamingEnumeration<SearchResult> search(final String p0, final String p1, final Object[] p2, final SearchControls p3) throws NamingException;
}
