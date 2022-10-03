package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public abstract class AtomicDirContext extends ComponentDirContext
{
    protected AtomicDirContext() {
        this._contextType = 3;
    }
    
    protected abstract Attributes a_getAttributes(final String p0, final String[] p1, final Continuation p2) throws NamingException;
    
    protected abstract void a_modifyAttributes(final String p0, final int p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract void a_modifyAttributes(final String p0, final ModificationItem[] p1, final Continuation p2) throws NamingException;
    
    protected abstract void a_bind(final String p0, final Object p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract void a_rebind(final String p0, final Object p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract DirContext a_createSubcontext(final String p0, final Attributes p1, final Continuation p2) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> a_search(final Attributes p0, final String[] p1, final Continuation p2) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> a_search(final String p0, final String p1, final Object[] p2, final SearchControls p3, final Continuation p4) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> a_search(final String p0, final String p1, final SearchControls p2, final Continuation p3) throws NamingException;
    
    protected abstract DirContext a_getSchema(final Continuation p0) throws NamingException;
    
    protected abstract DirContext a_getSchemaClassDefinition(final Continuation p0) throws NamingException;
    
    protected Attributes a_getAttributes_nns(final String s, final String[] array, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
        return null;
    }
    
    protected void a_modifyAttributes_nns(final String s, final int n, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected void a_modifyAttributes_nns(final String s, final ModificationItem[] array, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected void a_bind_nns(final String s, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected void a_rebind_nns(final String s, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected DirContext a_createSubcontext_nns(final String s, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
        return null;
    }
    
    protected NamingEnumeration<SearchResult> a_search_nns(final Attributes attributes, final String[] array, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(continuation);
        return null;
    }
    
    protected NamingEnumeration<SearchResult> a_search_nns(final String s, final String s2, final Object[] array, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
        return null;
    }
    
    protected NamingEnumeration<SearchResult> a_search_nns(final String s, final String s2, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
        return null;
    }
    
    protected DirContext a_getSchema_nns(final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(continuation);
        return null;
    }
    
    protected DirContext a_getSchemaDefinition_nns(final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(continuation);
        return null;
    }
    
    @Override
    protected Attributes c_getAttributes(final Name name, final String[] array, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            return this.a_getAttributes(name.toString(), array, continuation);
        }
        return null;
    }
    
    @Override
    protected void c_modifyAttributes(final Name name, final int n, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_modifyAttributes(name.toString(), n, attributes, continuation);
        }
    }
    
    @Override
    protected void c_modifyAttributes(final Name name, final ModificationItem[] array, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_modifyAttributes(name.toString(), array, continuation);
        }
    }
    
    @Override
    protected void c_bind(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_bind(name.toString(), o, attributes, continuation);
        }
    }
    
    @Override
    protected void c_rebind(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_rebind(name.toString(), o, attributes, continuation);
        }
    }
    
    @Override
    protected DirContext c_createSubcontext(final Name name, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            return this.a_createSubcontext(name.toString(), attributes, continuation);
        }
        return null;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search(final Name name, final Attributes attributes, final String[] array, final Continuation continuation) throws NamingException {
        if (this.resolve_to_context(name, continuation)) {
            return this.a_search(attributes, array, continuation);
        }
        return null;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search(final Name name, final String s, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            return this.a_search(name.toString(), s, searchControls, continuation);
        }
        return null;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search(final Name name, final String s, final Object[] array, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            return this.a_search(name.toString(), s, array, searchControls, continuation);
        }
        return null;
    }
    
    @Override
    protected DirContext c_getSchema(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_context(name, continuation)) {
            return this.a_getSchema(continuation);
        }
        return null;
    }
    
    @Override
    protected DirContext c_getSchemaClassDefinition(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_context(name, continuation)) {
            return this.a_getSchemaClassDefinition(continuation);
        }
        return null;
    }
    
    @Override
    protected Attributes c_getAttributes_nns(final Name name, final String[] array, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            return this.a_getAttributes_nns(name.toString(), array, continuation);
        }
        return null;
    }
    
    @Override
    protected void c_modifyAttributes_nns(final Name name, final int n, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            this.a_modifyAttributes_nns(name.toString(), n, attributes, continuation);
        }
    }
    
    @Override
    protected void c_modifyAttributes_nns(final Name name, final ModificationItem[] array, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            this.a_modifyAttributes_nns(name.toString(), array, continuation);
        }
    }
    
    @Override
    protected void c_bind_nns(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            this.a_bind_nns(name.toString(), o, attributes, continuation);
        }
    }
    
    @Override
    protected void c_rebind_nns(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            this.a_rebind_nns(name.toString(), o, attributes, continuation);
        }
    }
    
    @Override
    protected DirContext c_createSubcontext_nns(final Name name, final Attributes attributes, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            return this.a_createSubcontext_nns(name.toString(), attributes, continuation);
        }
        return null;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search_nns(final Name name, final Attributes attributes, final String[] array, final Continuation continuation) throws NamingException {
        this.resolve_to_nns_and_continue(name, continuation);
        return null;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search_nns(final Name name, final String s, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            return this.a_search_nns(name.toString(), s, searchControls, continuation);
        }
        return null;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> c_search_nns(final Name name, final String s, final Object[] array, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            return this.a_search_nns(name.toString(), s, array, searchControls, continuation);
        }
        return null;
    }
    
    @Override
    protected DirContext c_getSchema_nns(final Name name, final Continuation continuation) throws NamingException {
        this.resolve_to_nns_and_continue(name, continuation);
        return null;
    }
    
    @Override
    protected DirContext c_getSchemaClassDefinition_nns(final Name name, final Continuation continuation) throws NamingException {
        this.resolve_to_nns_and_continue(name, continuation);
        return null;
    }
}
