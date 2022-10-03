package javax.naming.spi;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.Attributes;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Name;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.directory.DirContext;

class ContinuationDirContext extends ContinuationContext implements DirContext
{
    ContinuationDirContext(final CannotProceedException ex, final Hashtable<?, ?> hashtable) {
        super(ex, hashtable);
    }
    
    protected DirContextNamePair getTargetContext(final Name name) throws NamingException {
        if (this.cpe.getResolvedObj() == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
        }
        final Context context = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
        if (context == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
        }
        if (context instanceof DirContext) {
            return new DirContextNamePair((DirContext)context, name);
        }
        if (context instanceof Resolver) {
            final ResolveResult resolveToClass = ((Resolver)context).resolveToClass(name, DirContext.class);
            return new DirContextNamePair((DirContext)resolveToClass.getResolvedObj(), resolveToClass.getRemainingName());
        }
        final Object lookup = context.lookup(name);
        if (lookup instanceof DirContext) {
            return new DirContextNamePair((DirContext)lookup, new CompositeName());
        }
        throw (NamingException)this.cpe.fillInStackTrace();
    }
    
    protected DirContextStringPair getTargetContext(final String s) throws NamingException {
        if (this.cpe.getResolvedObj() == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
        }
        final Context context = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
        if (context instanceof DirContext) {
            return new DirContextStringPair((DirContext)context, s);
        }
        if (context instanceof Resolver) {
            final ResolveResult resolveToClass = ((Resolver)context).resolveToClass(s, DirContext.class);
            final DirContext dirContext = (DirContext)resolveToClass.getResolvedObj();
            final Name remainingName = resolveToClass.getRemainingName();
            return new DirContextStringPair(dirContext, (remainingName != null) ? remainingName.toString() : "");
        }
        final Object lookup = context.lookup(s);
        if (lookup instanceof DirContext) {
            return new DirContextStringPair((DirContext)lookup, "");
        }
        throw (NamingException)this.cpe.fillInStackTrace();
    }
    
    @Override
    public Attributes getAttributes(final String s) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().getAttributes(targetContext.getString());
    }
    
    @Override
    public Attributes getAttributes(final String s, final String[] array) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().getAttributes(targetContext.getString(), array);
    }
    
    @Override
    public Attributes getAttributes(final Name name) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().getAttributes(targetContext.getName());
    }
    
    @Override
    public Attributes getAttributes(final Name name, final String[] array) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().getAttributes(targetContext.getName(), array);
    }
    
    @Override
    public void modifyAttributes(final Name name, final int n, final Attributes attributes) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        targetContext.getDirContext().modifyAttributes(targetContext.getName(), n, attributes);
    }
    
    @Override
    public void modifyAttributes(final String s, final int n, final Attributes attributes) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        targetContext.getDirContext().modifyAttributes(targetContext.getString(), n, attributes);
    }
    
    @Override
    public void modifyAttributes(final Name name, final ModificationItem[] array) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        targetContext.getDirContext().modifyAttributes(targetContext.getName(), array);
    }
    
    @Override
    public void modifyAttributes(final String s, final ModificationItem[] array) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        targetContext.getDirContext().modifyAttributes(targetContext.getString(), array);
    }
    
    @Override
    public void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        targetContext.getDirContext().bind(targetContext.getName(), o, attributes);
    }
    
    @Override
    public void bind(final String s, final Object o, final Attributes attributes) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        targetContext.getDirContext().bind(targetContext.getString(), o, attributes);
    }
    
    @Override
    public void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        targetContext.getDirContext().rebind(targetContext.getName(), o, attributes);
    }
    
    @Override
    public void rebind(final String s, final Object o, final Attributes attributes) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        targetContext.getDirContext().rebind(targetContext.getString(), o, attributes);
    }
    
    @Override
    public DirContext createSubcontext(final Name name, final Attributes attributes) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().createSubcontext(targetContext.getName(), attributes);
    }
    
    @Override
    public DirContext createSubcontext(final String s, final Attributes attributes) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().createSubcontext(targetContext.getString(), attributes);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes, final String[] array) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().search(targetContext.getName(), attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes, final String[] array) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().search(targetContext.getString(), attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().search(targetContext.getName(), attributes);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().search(targetContext.getString(), attributes);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final SearchControls searchControls) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().search(targetContext.getName(), s, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final SearchControls searchControls) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().search(targetContext.getString(), s2, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().search(targetContext.getName(), s, array, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final Object[] array, final SearchControls searchControls) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().search(targetContext.getString(), s2, array, searchControls);
    }
    
    @Override
    public DirContext getSchema(final String s) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().getSchema(targetContext.getString());
    }
    
    @Override
    public DirContext getSchema(final Name name) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().getSchema(targetContext.getName());
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final String s) throws NamingException {
        final DirContextStringPair targetContext = this.getTargetContext(s);
        return targetContext.getDirContext().getSchemaClassDefinition(targetContext.getString());
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final Name name) throws NamingException {
        final DirContextNamePair targetContext = this.getTargetContext(name);
        return targetContext.getDirContext().getSchemaClassDefinition(targetContext.getName());
    }
}
