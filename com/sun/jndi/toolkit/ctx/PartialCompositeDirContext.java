package com.sun.jndi.toolkit.ctx;

import javax.naming.NameParser;
import javax.naming.Context;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.OperationNotSupportedException;
import javax.naming.NotContextException;
import javax.naming.CannotProceedException;
import javax.naming.spi.DirectoryManager;
import javax.naming.CompositeName;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.directory.ModificationItem;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.Name;
import javax.naming.directory.DirContext;

public abstract class PartialCompositeDirContext extends AtomicContext implements DirContext
{
    protected PartialCompositeDirContext() {
        this._contextType = 1;
    }
    
    protected abstract Attributes p_getAttributes(final Name p0, final String[] p1, final Continuation p2) throws NamingException;
    
    protected abstract void p_modifyAttributes(final Name p0, final int p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract void p_modifyAttributes(final Name p0, final ModificationItem[] p1, final Continuation p2) throws NamingException;
    
    protected abstract void p_bind(final Name p0, final Object p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract void p_rebind(final Name p0, final Object p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract DirContext p_createSubcontext(final Name p0, final Attributes p1, final Continuation p2) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> p_search(final Name p0, final Attributes p1, final String[] p2, final Continuation p3) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> p_search(final Name p0, final String p1, final SearchControls p2, final Continuation p3) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> p_search(final Name p0, final String p1, final Object[] p2, final SearchControls p3, final Continuation p4) throws NamingException;
    
    protected abstract DirContext p_getSchema(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract DirContext p_getSchemaClassDefinition(final Name p0, final Continuation p1) throws NamingException;
    
    @Override
    public Attributes getAttributes(final String s) throws NamingException {
        return this.getAttributes(s, null);
    }
    
    @Override
    public Attributes getAttributes(final Name name) throws NamingException {
        return this.getAttributes(name, null);
    }
    
    @Override
    public Attributes getAttributes(final String s, final String[] array) throws NamingException {
        return this.getAttributes(new CompositeName(s), array);
    }
    
    @Override
    public Attributes getAttributes(final Name name, final String[] array) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        Attributes attributes;
        try {
            attributes = this.p_getAttributes(name, array, continuation);
            while (continuation.isContinue()) {
                attributes = getPCDirContext(continuation).p_getAttributes(continuation.getRemainingName(), array, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            attributes = DirectoryManager.getContinuationDirContext(ex).getAttributes(ex.getRemainingName(), array);
        }
        return attributes;
    }
    
    @Override
    public void modifyAttributes(final String s, final int n, final Attributes attributes) throws NamingException {
        this.modifyAttributes(new CompositeName(s), n, attributes);
    }
    
    @Override
    public void modifyAttributes(final Name name, final int n, final Attributes attributes) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_modifyAttributes(name, n, attributes, continuation);
            while (continuation.isContinue()) {
                getPCDirContext(continuation).p_modifyAttributes(continuation.getRemainingName(), n, attributes, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            DirectoryManager.getContinuationDirContext(ex).modifyAttributes(ex.getRemainingName(), n, attributes);
        }
    }
    
    @Override
    public void modifyAttributes(final String s, final ModificationItem[] array) throws NamingException {
        this.modifyAttributes(new CompositeName(s), array);
    }
    
    @Override
    public void modifyAttributes(final Name name, final ModificationItem[] array) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_modifyAttributes(name, array, continuation);
            while (continuation.isContinue()) {
                getPCDirContext(continuation).p_modifyAttributes(continuation.getRemainingName(), array, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            DirectoryManager.getContinuationDirContext(ex).modifyAttributes(ex.getRemainingName(), array);
        }
    }
    
    @Override
    public void bind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.bind(new CompositeName(s), o, attributes);
    }
    
    @Override
    public void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_bind(name, o, attributes, continuation);
            while (continuation.isContinue()) {
                getPCDirContext(continuation).p_bind(continuation.getRemainingName(), o, attributes, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            DirectoryManager.getContinuationDirContext(ex).bind(ex.getRemainingName(), o, attributes);
        }
    }
    
    @Override
    public void rebind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.rebind(new CompositeName(s), o, attributes);
    }
    
    @Override
    public void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_rebind(name, o, attributes, continuation);
            while (continuation.isContinue()) {
                getPCDirContext(continuation).p_rebind(continuation.getRemainingName(), o, attributes, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            DirectoryManager.getContinuationDirContext(ex).rebind(ex.getRemainingName(), o, attributes);
        }
    }
    
    @Override
    public DirContext createSubcontext(final String s, final Attributes attributes) throws NamingException {
        return this.createSubcontext(new CompositeName(s), attributes);
    }
    
    @Override
    public DirContext createSubcontext(final Name name, final Attributes attributes) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        DirContext dirContext;
        try {
            dirContext = this.p_createSubcontext(name, attributes, continuation);
            while (continuation.isContinue()) {
                dirContext = getPCDirContext(continuation).p_createSubcontext(continuation.getRemainingName(), attributes, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            dirContext = DirectoryManager.getContinuationDirContext(ex).createSubcontext(ex.getRemainingName(), attributes);
        }
        return dirContext;
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes) throws NamingException {
        return this.search(s, attributes, null);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes) throws NamingException {
        return this.search(name, attributes, null);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes, final String[] array) throws NamingException {
        return this.search(new CompositeName(s), attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes, final String[] array) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        NamingEnumeration<SearchResult> namingEnumeration;
        try {
            namingEnumeration = this.p_search(name, attributes, array, continuation);
            while (continuation.isContinue()) {
                namingEnumeration = getPCDirContext(continuation).p_search(continuation.getRemainingName(), attributes, array, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            namingEnumeration = DirectoryManager.getContinuationDirContext(ex).search(ex.getRemainingName(), attributes, array);
        }
        return namingEnumeration;
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final SearchControls searchControls) throws NamingException {
        return this.search(new CompositeName(s), s2, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final SearchControls searchControls) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        NamingEnumeration<SearchResult> namingEnumeration;
        try {
            namingEnumeration = this.p_search(name, s, searchControls, continuation);
            while (continuation.isContinue()) {
                namingEnumeration = getPCDirContext(continuation).p_search(continuation.getRemainingName(), s, searchControls, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            namingEnumeration = DirectoryManager.getContinuationDirContext(ex).search(ex.getRemainingName(), s, searchControls);
        }
        return namingEnumeration;
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final Object[] array, final SearchControls searchControls) throws NamingException {
        return this.search(new CompositeName(s), s2, array, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        NamingEnumeration<SearchResult> namingEnumeration;
        try {
            namingEnumeration = this.p_search(name, s, array, searchControls, continuation);
            while (continuation.isContinue()) {
                namingEnumeration = getPCDirContext(continuation).p_search(continuation.getRemainingName(), s, array, searchControls, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            namingEnumeration = DirectoryManager.getContinuationDirContext(ex).search(ex.getRemainingName(), s, array, searchControls);
        }
        return namingEnumeration;
    }
    
    @Override
    public DirContext getSchema(final String s) throws NamingException {
        return this.getSchema(new CompositeName(s));
    }
    
    @Override
    public DirContext getSchema(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        DirContext dirContext;
        try {
            dirContext = this.p_getSchema(name, continuation);
            while (continuation.isContinue()) {
                dirContext = getPCDirContext(continuation).p_getSchema(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            dirContext = DirectoryManager.getContinuationDirContext(ex).getSchema(ex.getRemainingName());
        }
        return dirContext;
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final String s) throws NamingException {
        return this.getSchemaClassDefinition(new CompositeName(s));
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        DirContext dirContext;
        try {
            dirContext = this.p_getSchemaClassDefinition(name, continuation);
            while (continuation.isContinue()) {
                dirContext = getPCDirContext(continuation).p_getSchemaClassDefinition(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            dirContext = DirectoryManager.getContinuationDirContext(ex).getSchemaClassDefinition(ex.getRemainingName());
        }
        return dirContext;
    }
    
    protected static PartialCompositeDirContext getPCDirContext(final Continuation continuation) throws NamingException {
        final PartialCompositeContext pcContext = PartialCompositeContext.getPCContext(continuation);
        if (!(pcContext instanceof PartialCompositeDirContext)) {
            throw continuation.fillInException(new NotContextException("Resolved object is not a DirContext."));
        }
        return (PartialCompositeDirContext)pcContext;
    }
    
    @Override
    protected StringHeadTail c_parseComponent(final String s, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected Object a_lookup(final String s, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected Object a_lookupLink(final String s, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected NamingEnumeration<NameClassPair> a_list(final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected NamingEnumeration<Binding> a_listBindings(final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected void a_bind(final String s, final Object o, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected void a_rebind(final String s, final Object o, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected void a_unbind(final String s, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected void a_destroySubcontext(final String s, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected Context a_createSubcontext(final String s, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected void a_rename(final String s, final Name name, final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    protected NameParser a_getNameParser(final Continuation continuation) throws NamingException {
        throw continuation.fillInException(new OperationNotSupportedException());
    }
}
