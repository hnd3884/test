package com.sun.jndi.toolkit.ctx;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.Name;

public abstract class ComponentDirContext extends PartialCompositeDirContext
{
    protected ComponentDirContext() {
        this._contextType = 2;
    }
    
    protected abstract Attributes c_getAttributes(final Name p0, final String[] p1, final Continuation p2) throws NamingException;
    
    protected abstract void c_modifyAttributes(final Name p0, final int p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract void c_modifyAttributes(final Name p0, final ModificationItem[] p1, final Continuation p2) throws NamingException;
    
    protected abstract void c_bind(final Name p0, final Object p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract void c_rebind(final Name p0, final Object p1, final Attributes p2, final Continuation p3) throws NamingException;
    
    protected abstract DirContext c_createSubcontext(final Name p0, final Attributes p1, final Continuation p2) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> c_search(final Name p0, final Attributes p1, final String[] p2, final Continuation p3) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> c_search(final Name p0, final String p1, final SearchControls p2, final Continuation p3) throws NamingException;
    
    protected abstract NamingEnumeration<SearchResult> c_search(final Name p0, final String p1, final Object[] p2, final SearchControls p3, final Continuation p4) throws NamingException;
    
    protected abstract DirContext c_getSchema(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract DirContext c_getSchemaClassDefinition(final Name p0, final Continuation p1) throws NamingException;
    
    protected Attributes c_getAttributes_nns(final Name name, final String[] array, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected void c_modifyAttributes_nns(final Name name, final int n, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected void c_modifyAttributes_nns(final Name name, final ModificationItem[] array, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected void c_bind_nns(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected void c_rebind_nns(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected DirContext c_createSubcontext_nns(final Name name, final Attributes attributes, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected NamingEnumeration<SearchResult> c_search_nns(final Name name, final Attributes attributes, final String[] array, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected NamingEnumeration<SearchResult> c_search_nns(final Name name, final String s, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected NamingEnumeration<SearchResult> c_search_nns(final Name name, final String s, final Object[] array, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected DirContext c_getSchema_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected DirContext c_getSchemaClassDefinition_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    @Override
    protected Attributes p_getAttributes(final Name name, final String[] array, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        Attributes attributes = null;
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                attributes = this.c_getAttributes_nns(p_resolveIntermediate.getHead(), array, continuation);
                break;
            }
            case 2: {
                attributes = this.c_getAttributes(p_resolveIntermediate.getHead(), array, continuation);
                break;
            }
        }
        return attributes;
    }
    
    @Override
    protected void p_modifyAttributes(final Name name, final int n, final Attributes attributes, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_modifyAttributes_nns(p_resolveIntermediate.getHead(), n, attributes, continuation);
                break;
            }
            case 2: {
                this.c_modifyAttributes(p_resolveIntermediate.getHead(), n, attributes, continuation);
                break;
            }
        }
    }
    
    @Override
    protected void p_modifyAttributes(final Name name, final ModificationItem[] array, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_modifyAttributes_nns(p_resolveIntermediate.getHead(), array, continuation);
                break;
            }
            case 2: {
                this.c_modifyAttributes(p_resolveIntermediate.getHead(), array, continuation);
                break;
            }
        }
    }
    
    @Override
    protected void p_bind(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_bind_nns(p_resolveIntermediate.getHead(), o, attributes, continuation);
                break;
            }
            case 2: {
                this.c_bind(p_resolveIntermediate.getHead(), o, attributes, continuation);
                break;
            }
        }
    }
    
    @Override
    protected void p_rebind(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_rebind_nns(p_resolveIntermediate.getHead(), o, attributes, continuation);
                break;
            }
            case 2: {
                this.c_rebind(p_resolveIntermediate.getHead(), o, attributes, continuation);
                break;
            }
        }
    }
    
    @Override
    protected DirContext p_createSubcontext(final Name name, final Attributes attributes, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        DirContext dirContext = null;
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                dirContext = this.c_createSubcontext_nns(p_resolveIntermediate.getHead(), attributes, continuation);
                break;
            }
            case 2: {
                dirContext = this.c_createSubcontext(p_resolveIntermediate.getHead(), attributes, continuation);
                break;
            }
        }
        return dirContext;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> p_search(final Name name, final Attributes attributes, final String[] array, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        NamingEnumeration<SearchResult> namingEnumeration = null;
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                namingEnumeration = this.c_search_nns(p_resolveIntermediate.getHead(), attributes, array, continuation);
                break;
            }
            case 2: {
                namingEnumeration = this.c_search(p_resolveIntermediate.getHead(), attributes, array, continuation);
                break;
            }
        }
        return namingEnumeration;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> p_search(final Name name, final String s, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        NamingEnumeration<SearchResult> namingEnumeration = null;
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                namingEnumeration = this.c_search_nns(p_resolveIntermediate.getHead(), s, searchControls, continuation);
                break;
            }
            case 2: {
                namingEnumeration = this.c_search(p_resolveIntermediate.getHead(), s, searchControls, continuation);
                break;
            }
        }
        return namingEnumeration;
    }
    
    @Override
    protected NamingEnumeration<SearchResult> p_search(final Name name, final String s, final Object[] array, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        NamingEnumeration<SearchResult> namingEnumeration = null;
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                namingEnumeration = this.c_search_nns(p_resolveIntermediate.getHead(), s, array, searchControls, continuation);
                break;
            }
            case 2: {
                namingEnumeration = this.c_search(p_resolveIntermediate.getHead(), s, array, searchControls, continuation);
                break;
            }
        }
        return namingEnumeration;
    }
    
    @Override
    protected DirContext p_getSchema(final Name name, final Continuation continuation) throws NamingException {
        DirContext dirContext = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                dirContext = this.c_getSchema_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                dirContext = this.c_getSchema(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
        return dirContext;
    }
    
    @Override
    protected DirContext p_getSchemaClassDefinition(final Name name, final Continuation continuation) throws NamingException {
        DirContext dirContext = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                dirContext = this.c_getSchemaClassDefinition_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                dirContext = this.c_getSchemaClassDefinition(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
        return dirContext;
    }
}
