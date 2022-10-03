package com.sun.jndi.toolkit.ctx;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.LinkRef;
import javax.naming.CompositeName;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public abstract class AtomicContext extends ComponentContext
{
    private static int debug;
    
    protected AtomicContext() {
        this._contextType = 3;
    }
    
    protected abstract Object a_lookup(final String p0, final Continuation p1) throws NamingException;
    
    protected abstract Object a_lookupLink(final String p0, final Continuation p1) throws NamingException;
    
    protected abstract NamingEnumeration<NameClassPair> a_list(final Continuation p0) throws NamingException;
    
    protected abstract NamingEnumeration<Binding> a_listBindings(final Continuation p0) throws NamingException;
    
    protected abstract void a_bind(final String p0, final Object p1, final Continuation p2) throws NamingException;
    
    protected abstract void a_rebind(final String p0, final Object p1, final Continuation p2) throws NamingException;
    
    protected abstract void a_unbind(final String p0, final Continuation p1) throws NamingException;
    
    protected abstract void a_destroySubcontext(final String p0, final Continuation p1) throws NamingException;
    
    protected abstract Context a_createSubcontext(final String p0, final Continuation p1) throws NamingException;
    
    protected abstract void a_rename(final String p0, final Name p1, final Continuation p2) throws NamingException;
    
    protected abstract NameParser a_getNameParser(final Continuation p0) throws NamingException;
    
    protected abstract StringHeadTail c_parseComponent(final String p0, final Continuation p1) throws NamingException;
    
    protected Object a_resolveIntermediate_nns(final String s, final Continuation continuation) throws NamingException {
        try {
            final Object a_lookup = this.a_lookup(s, continuation);
            if (a_lookup != null && this.getClass().isInstance(a_lookup)) {
                continuation.setContinueNNS(a_lookup, s, this);
                return null;
            }
            if (a_lookup != null && !(a_lookup instanceof Context)) {
                final Reference reference = new Reference("java.lang.Object", new RefAddr("nns") {
                    private static final long serialVersionUID = -3399518522645918499L;
                    
                    @Override
                    public Object getContent() {
                        return a_lookup;
                    }
                });
                final CompositeName compositeName = new CompositeName();
                compositeName.add(s);
                compositeName.add("");
                continuation.setContinue(reference, compositeName, this);
                return null;
            }
            return a_lookup;
        }
        catch (final NamingException ex) {
            ex.appendRemainingComponent("");
            throw ex;
        }
    }
    
    protected Object a_lookup_nns(final String s, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
        return null;
    }
    
    protected Object a_lookupLink_nns(final String s, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
        return null;
    }
    
    protected NamingEnumeration<NameClassPair> a_list_nns(final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(continuation);
        return null;
    }
    
    protected NamingEnumeration<Binding> a_listBindings_nns(final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(continuation);
        return null;
    }
    
    protected void a_bind_nns(final String s, final Object o, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected void a_rebind_nns(final String s, final Object o, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected void a_unbind_nns(final String s, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected Context a_createSubcontext_nns(final String s, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
        return null;
    }
    
    protected void a_destroySubcontext_nns(final String s, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected void a_rename_nns(final String s, final Name name, final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(s, continuation);
    }
    
    protected NameParser a_getNameParser_nns(final Continuation continuation) throws NamingException {
        this.a_processJunction_nns(continuation);
        return null;
    }
    
    protected boolean isEmpty(final String s) {
        return s == null || s.equals("");
    }
    
    @Override
    protected Object c_lookup(final Name name, final Continuation continuation) throws NamingException {
        Object a_lookup = null;
        if (this.resolve_to_penultimate_context(name, continuation)) {
            a_lookup = this.a_lookup(name.toString(), continuation);
            if (a_lookup != null && a_lookup instanceof LinkRef) {
                continuation.setContinue(a_lookup, name, this);
                a_lookup = null;
            }
        }
        return a_lookup;
    }
    
    @Override
    protected Object c_lookupLink(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            return this.a_lookupLink(name.toString(), continuation);
        }
        return null;
    }
    
    @Override
    protected NamingEnumeration<NameClassPair> c_list(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_context(name, continuation)) {
            return this.a_list(continuation);
        }
        return null;
    }
    
    @Override
    protected NamingEnumeration<Binding> c_listBindings(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_context(name, continuation)) {
            return this.a_listBindings(continuation);
        }
        return null;
    }
    
    @Override
    protected void c_bind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_bind(name.toString(), o, continuation);
        }
    }
    
    @Override
    protected void c_rebind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_rebind(name.toString(), o, continuation);
        }
    }
    
    @Override
    protected void c_unbind(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_unbind(name.toString(), continuation);
        }
    }
    
    @Override
    protected void c_destroySubcontext(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_destroySubcontext(name.toString(), continuation);
        }
    }
    
    @Override
    protected Context c_createSubcontext(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            return this.a_createSubcontext(name.toString(), continuation);
        }
        return null;
    }
    
    @Override
    protected void c_rename(final Name name, final Name name2, final Continuation continuation) throws NamingException {
        if (this.resolve_to_penultimate_context(name, continuation)) {
            this.a_rename(name.toString(), name2, continuation);
        }
    }
    
    @Override
    protected NameParser c_getNameParser(final Name name, final Continuation continuation) throws NamingException {
        if (this.resolve_to_context(name, continuation)) {
            return this.a_getNameParser(continuation);
        }
        return null;
    }
    
    @Override
    protected Object c_resolveIntermediate_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            Object a_resolveIntermediate_nns = null;
            if (this.resolve_to_penultimate_context_nns(name, continuation)) {
                a_resolveIntermediate_nns = this.a_resolveIntermediate_nns(name.toString(), continuation);
                if (a_resolveIntermediate_nns != null && a_resolveIntermediate_nns instanceof LinkRef) {
                    continuation.setContinue(a_resolveIntermediate_nns, name, this);
                    a_resolveIntermediate_nns = null;
                }
            }
            return a_resolveIntermediate_nns;
        }
        return super.c_resolveIntermediate_nns(name, continuation);
    }
    
    @Override
    protected Object c_lookup_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            Object a_lookup_nns = null;
            if (this.resolve_to_penultimate_context_nns(name, continuation)) {
                a_lookup_nns = this.a_lookup_nns(name.toString(), continuation);
                if (a_lookup_nns != null && a_lookup_nns instanceof LinkRef) {
                    continuation.setContinue(a_lookup_nns, name, this);
                    a_lookup_nns = null;
                }
            }
            return a_lookup_nns;
        }
        return super.c_lookup_nns(name, continuation);
    }
    
    @Override
    protected Object c_lookupLink_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            this.resolve_to_nns_and_continue(name, continuation);
            return null;
        }
        return super.c_lookupLink_nns(name, continuation);
    }
    
    @Override
    protected NamingEnumeration<NameClassPair> c_list_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            this.resolve_to_nns_and_continue(name, continuation);
            return null;
        }
        return super.c_list_nns(name, continuation);
    }
    
    @Override
    protected NamingEnumeration<Binding> c_listBindings_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            this.resolve_to_nns_and_continue(name, continuation);
            return null;
        }
        return super.c_listBindings_nns(name, continuation);
    }
    
    @Override
    protected void c_bind_nns(final Name name, final Object o, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            if (this.resolve_to_penultimate_context_nns(name, continuation)) {
                this.a_bind_nns(name.toString(), o, continuation);
            }
        }
        else {
            super.c_bind_nns(name, o, continuation);
        }
    }
    
    @Override
    protected void c_rebind_nns(final Name name, final Object o, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            if (this.resolve_to_penultimate_context_nns(name, continuation)) {
                this.a_rebind_nns(name.toString(), o, continuation);
            }
        }
        else {
            super.c_rebind_nns(name, o, continuation);
        }
    }
    
    @Override
    protected void c_unbind_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            if (this.resolve_to_penultimate_context_nns(name, continuation)) {
                this.a_unbind_nns(name.toString(), continuation);
            }
        }
        else {
            super.c_unbind_nns(name, continuation);
        }
    }
    
    @Override
    protected Context c_createSubcontext_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType != 3) {
            return super.c_createSubcontext_nns(name, continuation);
        }
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            return this.a_createSubcontext_nns(name.toString(), continuation);
        }
        return null;
    }
    
    @Override
    protected void c_destroySubcontext_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            if (this.resolve_to_penultimate_context_nns(name, continuation)) {
                this.a_destroySubcontext_nns(name.toString(), continuation);
            }
        }
        else {
            super.c_destroySubcontext_nns(name, continuation);
        }
    }
    
    @Override
    protected void c_rename_nns(final Name name, final Name name2, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            if (this.resolve_to_penultimate_context_nns(name, continuation)) {
                this.a_rename_nns(name.toString(), name2, continuation);
            }
        }
        else {
            super.c_rename_nns(name, name2, continuation);
        }
    }
    
    @Override
    protected NameParser c_getNameParser_nns(final Name name, final Continuation continuation) throws NamingException {
        if (this._contextType == 3) {
            this.resolve_to_nns_and_continue(name, continuation);
            return null;
        }
        return super.c_getNameParser_nns(name, continuation);
    }
    
    protected void a_processJunction_nns(final String s, final Continuation continuation) throws NamingException {
        if (s.equals("")) {
            final NameNotFoundException ex = new NameNotFoundException();
            continuation.setErrorNNS(this, s);
            throw continuation.fillInException(ex);
        }
        try {
            final Object a_lookup = this.a_lookup(s, continuation);
            if (continuation.isContinue()) {
                continuation.appendRemainingComponent("");
            }
            else {
                continuation.setContinueNNS(a_lookup, s, this);
            }
        }
        catch (final NamingException ex2) {
            ex2.appendRemainingComponent("");
            throw ex2;
        }
    }
    
    protected void a_processJunction_nns(final Continuation continuation) throws NamingException {
        continuation.setContinue(new Reference("java.lang.Object", new RefAddr("nns") {
            private static final long serialVersionUID = 3449785852664978312L;
            
            @Override
            public Object getContent() {
                return AtomicContext.this;
            }
        }), AtomicContext._NNS_NAME, this);
    }
    
    protected boolean resolve_to_context(final Name name, final Continuation continuation) throws NamingException {
        final String string = name.toString();
        final StringHeadTail c_parseComponent = this.c_parseComponent(string, continuation);
        final String tail = c_parseComponent.getTail();
        final String head = c_parseComponent.getHead();
        if (AtomicContext.debug > 0) {
            System.out.println("RESOLVE TO CONTEXT(" + string + ") = {" + head + ", " + tail + "}");
        }
        if (head == null) {
            throw continuation.fillInException(new InvalidNameException());
        }
        if (!this.isEmpty(head)) {
            try {
                final Object a_lookup = this.a_lookup(head, continuation);
                if (a_lookup != null) {
                    continuation.setContinue(a_lookup, head, this, (tail == null) ? "" : tail);
                }
                else if (continuation.isContinue()) {
                    continuation.appendRemainingComponent(tail);
                }
                return false;
            }
            catch (final NamingException ex) {
                ex.appendRemainingComponent(tail);
                throw ex;
            }
        }
        continuation.setSuccess();
        return true;
    }
    
    protected boolean resolve_to_penultimate_context(final Name name, final Continuation continuation) throws NamingException {
        final String string = name.toString();
        if (AtomicContext.debug > 0) {
            System.out.println("RESOLVE TO PENULTIMATE" + string);
        }
        final StringHeadTail c_parseComponent = this.c_parseComponent(string, continuation);
        final String tail = c_parseComponent.getTail();
        final String head = c_parseComponent.getHead();
        if (head == null) {
            throw continuation.fillInException(new InvalidNameException());
        }
        if (!this.isEmpty(tail)) {
            try {
                final Object a_lookup = this.a_lookup(head, continuation);
                if (a_lookup != null) {
                    continuation.setContinue(a_lookup, head, this, tail);
                }
                else if (continuation.isContinue()) {
                    continuation.appendRemainingComponent(tail);
                }
                return false;
            }
            catch (final NamingException ex) {
                ex.appendRemainingComponent(tail);
                throw ex;
            }
        }
        continuation.setSuccess();
        return true;
    }
    
    protected boolean resolve_to_penultimate_context_nns(final Name name, final Continuation continuation) throws NamingException {
        try {
            if (AtomicContext.debug > 0) {
                System.out.println("RESOLVE TO PENULTIMATE NNS" + name.toString());
            }
            final boolean resolve_to_penultimate_context = this.resolve_to_penultimate_context(name, continuation);
            if (continuation.isContinue()) {
                continuation.appendRemainingComponent("");
            }
            return resolve_to_penultimate_context;
        }
        catch (final NamingException ex) {
            ex.appendRemainingComponent("");
            throw ex;
        }
    }
    
    protected void resolve_to_nns_and_continue(final Name name, final Continuation continuation) throws NamingException {
        if (AtomicContext.debug > 0) {
            System.out.println("RESOLVE TO NNS AND CONTINUE" + name.toString());
        }
        if (this.resolve_to_penultimate_context_nns(name, continuation)) {
            final Object a_lookup_nns = this.a_lookup_nns(name.toString(), continuation);
            if (a_lookup_nns != null) {
                continuation.setContinue(a_lookup_nns, name, this);
            }
        }
    }
    
    static {
        AtomicContext.debug = 0;
    }
}
