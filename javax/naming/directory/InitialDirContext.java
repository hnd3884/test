package javax.naming.directory;

import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NotContextException;
import javax.naming.NoInitialContextException;
import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.InitialContext;

public class InitialDirContext extends InitialContext implements DirContext
{
    protected InitialDirContext(final boolean b) throws NamingException {
        super(b);
    }
    
    public InitialDirContext() throws NamingException {
    }
    
    public InitialDirContext(final Hashtable<?, ?> hashtable) throws NamingException {
        super(hashtable);
    }
    
    private DirContext getURLOrDefaultInitDirCtx(final String s) throws NamingException {
        final Context urlOrDefaultInitCtx = this.getURLOrDefaultInitCtx(s);
        if (urlOrDefaultInitCtx instanceof DirContext) {
            return (DirContext)urlOrDefaultInitCtx;
        }
        if (urlOrDefaultInitCtx == null) {
            throw new NoInitialContextException();
        }
        throw new NotContextException("Not an instance of DirContext");
    }
    
    private DirContext getURLOrDefaultInitDirCtx(final Name name) throws NamingException {
        final Context urlOrDefaultInitCtx = this.getURLOrDefaultInitCtx(name);
        if (urlOrDefaultInitCtx instanceof DirContext) {
            return (DirContext)urlOrDefaultInitCtx;
        }
        if (urlOrDefaultInitCtx == null) {
            throw new NoInitialContextException();
        }
        throw new NotContextException("Not an instance of DirContext");
    }
    
    @Override
    public Attributes getAttributes(final String s) throws NamingException {
        return this.getAttributes(s, null);
    }
    
    @Override
    public Attributes getAttributes(final String s, final String[] array) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).getAttributes(s, array);
    }
    
    @Override
    public Attributes getAttributes(final Name name) throws NamingException {
        return this.getAttributes(name, null);
    }
    
    @Override
    public Attributes getAttributes(final Name name, final String[] array) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).getAttributes(name, array);
    }
    
    @Override
    public void modifyAttributes(final String s, final int n, final Attributes attributes) throws NamingException {
        this.getURLOrDefaultInitDirCtx(s).modifyAttributes(s, n, attributes);
    }
    
    @Override
    public void modifyAttributes(final Name name, final int n, final Attributes attributes) throws NamingException {
        this.getURLOrDefaultInitDirCtx(name).modifyAttributes(name, n, attributes);
    }
    
    @Override
    public void modifyAttributes(final String s, final ModificationItem[] array) throws NamingException {
        this.getURLOrDefaultInitDirCtx(s).modifyAttributes(s, array);
    }
    
    @Override
    public void modifyAttributes(final Name name, final ModificationItem[] array) throws NamingException {
        this.getURLOrDefaultInitDirCtx(name).modifyAttributes(name, array);
    }
    
    @Override
    public void bind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.getURLOrDefaultInitDirCtx(s).bind(s, o, attributes);
    }
    
    @Override
    public void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        this.getURLOrDefaultInitDirCtx(name).bind(name, o, attributes);
    }
    
    @Override
    public void rebind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.getURLOrDefaultInitDirCtx(s).rebind(s, o, attributes);
    }
    
    @Override
    public void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        this.getURLOrDefaultInitDirCtx(name).rebind(name, o, attributes);
    }
    
    @Override
    public DirContext createSubcontext(final String s, final Attributes attributes) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).createSubcontext(s, attributes);
    }
    
    @Override
    public DirContext createSubcontext(final Name name, final Attributes attributes) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).createSubcontext(name, attributes);
    }
    
    @Override
    public DirContext getSchema(final String s) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).getSchema(s);
    }
    
    @Override
    public DirContext getSchema(final Name name) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).getSchema(name);
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final String s) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).getSchemaClassDefinition(s);
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final Name name) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).getSchemaClassDefinition(name);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).search(s, attributes);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).search(name, attributes);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes, final String[] array) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).search(s, attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes, final String[] array) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).search(name, attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final SearchControls searchControls) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).search(s, s2, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final SearchControls searchControls) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).search(name, s, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final Object[] array, final SearchControls searchControls) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(s).search(s, s2, array, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        return this.getURLOrDefaultInitDirCtx(name).search(name, s, array, searchControls);
    }
}
