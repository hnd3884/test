package com.sun.jndi.toolkit.ctx;

import javax.naming.LinkRef;
import javax.naming.spi.ResolveResult;
import javax.naming.InvalidNameException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.CompositeName;
import javax.naming.NameParser;
import javax.naming.Context;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Name;

public abstract class ComponentContext extends PartialCompositeContext
{
    private static int debug;
    protected static final byte USE_CONTINUATION = 1;
    protected static final byte TERMINAL_COMPONENT = 2;
    protected static final byte TERMINAL_NNS_COMPONENT = 3;
    
    protected ComponentContext() {
        this._contextType = 2;
    }
    
    protected abstract Object c_lookup(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract Object c_lookupLink(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract NamingEnumeration<NameClassPair> c_list(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract NamingEnumeration<Binding> c_listBindings(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract void c_bind(final Name p0, final Object p1, final Continuation p2) throws NamingException;
    
    protected abstract void c_rebind(final Name p0, final Object p1, final Continuation p2) throws NamingException;
    
    protected abstract void c_unbind(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract void c_destroySubcontext(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract Context c_createSubcontext(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract void c_rename(final Name p0, final Name p1, final Continuation p2) throws NamingException;
    
    protected abstract NameParser c_getNameParser(final Name p0, final Continuation p1) throws NamingException;
    
    protected HeadTail p_parseComponent(final Name name, final Continuation continuation) throws NamingException {
        int n;
        if (name.isEmpty() || name.get(0).equals("")) {
            n = 0;
        }
        else {
            n = 1;
        }
        Name name2;
        Name suffix;
        if (name instanceof CompositeName) {
            name2 = name.getPrefix(n);
            suffix = name.getSuffix(n);
        }
        else {
            name2 = new CompositeName().add(name.toString());
            suffix = null;
        }
        if (ComponentContext.debug > 2) {
            System.err.println("ORIG: " + name);
            System.err.println("PREFIX: " + name);
            System.err.println("SUFFIX: " + (Object)null);
        }
        return new HeadTail(name2, suffix);
    }
    
    protected Object c_resolveIntermediate_nns(final Name name, final Continuation continuation) throws NamingException {
        try {
            final Object c_lookup = this.c_lookup(name, continuation);
            if (c_lookup != null && this.getClass().isInstance(c_lookup)) {
                continuation.setContinueNNS(c_lookup, name, this);
                return null;
            }
            if (c_lookup != null && !(c_lookup instanceof Context)) {
                final Reference reference = new Reference("java.lang.Object", new RefAddr("nns") {
                    private static final long serialVersionUID = -8831204798861786362L;
                    
                    @Override
                    public Object getContent() {
                        return c_lookup;
                    }
                });
                final CompositeName compositeName = (CompositeName)name.clone();
                compositeName.add("");
                continuation.setContinue(reference, compositeName, this);
                return null;
            }
            return c_lookup;
        }
        catch (final NamingException ex) {
            ex.appendRemainingComponent("");
            throw ex;
        }
    }
    
    protected Object c_lookup_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected Object c_lookupLink_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected NamingEnumeration<NameClassPair> c_list_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected NamingEnumeration<Binding> c_listBindings_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected void c_bind_nns(final Name name, final Object o, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected void c_rebind_nns(final Name name, final Object o, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected void c_unbind_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected Context c_createSubcontext_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected void c_destroySubcontext_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected void c_rename_nns(final Name name, final Name name2, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
    }
    
    protected NameParser c_getNameParser_nns(final Name name, final Continuation continuation) throws NamingException {
        this.c_processJunction_nns(name, continuation);
        return null;
    }
    
    protected void c_processJunction_nns(final Name name, final Continuation continuation) throws NamingException {
        if (name.isEmpty()) {
            continuation.setContinue(new Reference("java.lang.Object", new RefAddr("nns") {
                private static final long serialVersionUID = -1389472957988053402L;
                
                @Override
                public Object getContent() {
                    return ComponentContext.this;
                }
            }), ComponentContext._NNS_NAME, this);
            return;
        }
        try {
            final Object c_lookup = this.c_lookup(name, continuation);
            if (continuation.isContinue()) {
                continuation.appendRemainingComponent("");
            }
            else {
                continuation.setContinueNNS(c_lookup, name, this);
            }
        }
        catch (final NamingException ex) {
            ex.appendRemainingComponent("");
            throw ex;
        }
    }
    
    protected HeadTail p_resolveIntermediate(final Name name, final Continuation continuation) throws NamingException {
        int status = 1;
        continuation.setSuccess();
        final HeadTail p_parseComponent = this.p_parseComponent(name, continuation);
        final Name tail = p_parseComponent.getTail();
        final Name head = p_parseComponent.getHead();
        Label_0314: {
            if (tail == null || tail.isEmpty()) {
                status = 2;
            }
            else {
                if (!tail.get(0).equals("")) {
                    try {
                        final Object c_resolveIntermediate_nns = this.c_resolveIntermediate_nns(head, continuation);
                        if (c_resolveIntermediate_nns != null) {
                            continuation.setContinue(c_resolveIntermediate_nns, head, this, tail);
                        }
                        else if (continuation.isContinue()) {
                            this.checkAndAdjustRemainingName(continuation.getRemainingName());
                            continuation.appendRemainingName(tail);
                        }
                        break Label_0314;
                    }
                    catch (final NamingException ex) {
                        this.checkAndAdjustRemainingName(ex.getRemainingName());
                        ex.appendRemainingName(tail);
                        throw ex;
                    }
                }
                if (tail.size() == 1) {
                    status = 3;
                }
                else if (head.isEmpty() || this.isAllEmpty(tail)) {
                    final Name suffix = tail.getSuffix(1);
                    try {
                        final Object c_lookup_nns = this.c_lookup_nns(head, continuation);
                        if (c_lookup_nns != null) {
                            continuation.setContinue(c_lookup_nns, head, this, suffix);
                        }
                        else if (continuation.isContinue()) {
                            continuation.appendRemainingName(suffix);
                        }
                    }
                    catch (final NamingException ex2) {
                        ex2.appendRemainingName(suffix);
                        throw ex2;
                    }
                }
                else {
                    try {
                        final Object c_resolveIntermediate_nns2 = this.c_resolveIntermediate_nns(head, continuation);
                        if (c_resolveIntermediate_nns2 != null) {
                            continuation.setContinue(c_resolveIntermediate_nns2, head, this, tail);
                        }
                        else if (continuation.isContinue()) {
                            this.checkAndAdjustRemainingName(continuation.getRemainingName());
                            continuation.appendRemainingName(tail);
                        }
                    }
                    catch (final NamingException ex3) {
                        this.checkAndAdjustRemainingName(ex3.getRemainingName());
                        ex3.appendRemainingName(tail);
                        throw ex3;
                    }
                }
            }
        }
        p_parseComponent.setStatus(status);
        return p_parseComponent;
    }
    
    void checkAndAdjustRemainingName(final Name name) throws InvalidNameException {
        final int size;
        if (name != null && (size = name.size()) > 1 && name.get(size - 1).equals("")) {
            name.remove(size - 1);
        }
    }
    
    protected boolean isAllEmpty(final Name name) {
        for (int size = name.size(), i = 0; i < size; ++i) {
            if (!name.get(i).equals("")) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected ResolveResult p_resolveToClass(final Name name, final Class<?> clazz, final Continuation continuation) throws NamingException {
        if (clazz.isInstance(this)) {
            continuation.setSuccess();
            return new ResolveResult(this, name);
        }
        ResolveResult resolveResult = null;
        switch (this.p_resolveIntermediate(name, continuation).getStatus()) {
            case 3: {
                final Object p_lookup = this.p_lookup(name, continuation);
                if (!continuation.isContinue() && clazz.isInstance(p_lookup)) {
                    resolveResult = new ResolveResult(p_lookup, ComponentContext._EMPTY_NAME);
                    break;
                }
                break;
            }
            case 2: {
                continuation.setSuccess();
                break;
            }
        }
        return resolveResult;
    }
    
    @Override
    protected Object p_lookup(final Name name, final Continuation continuation) throws NamingException {
        Object o = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                o = this.c_lookup_nns(p_resolveIntermediate.getHead(), continuation);
                if (o instanceof LinkRef) {
                    continuation.setContinue(o, p_resolveIntermediate.getHead(), this);
                    o = null;
                    break;
                }
                break;
            }
            case 2: {
                o = this.c_lookup(p_resolveIntermediate.getHead(), continuation);
                if (o instanceof LinkRef) {
                    continuation.setContinue(o, p_resolveIntermediate.getHead(), this);
                    o = null;
                    break;
                }
                break;
            }
        }
        return o;
    }
    
    @Override
    protected NamingEnumeration<NameClassPair> p_list(final Name name, final Continuation continuation) throws NamingException {
        NamingEnumeration<NameClassPair> namingEnumeration = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                if (ComponentContext.debug > 0) {
                    System.out.println("c_list_nns(" + p_resolveIntermediate.getHead() + ")");
                }
                namingEnumeration = this.c_list_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                if (ComponentContext.debug > 0) {
                    System.out.println("c_list(" + p_resolveIntermediate.getHead() + ")");
                }
                namingEnumeration = this.c_list(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
        return namingEnumeration;
    }
    
    @Override
    protected NamingEnumeration<Binding> p_listBindings(final Name name, final Continuation continuation) throws NamingException {
        NamingEnumeration<Binding> namingEnumeration = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                namingEnumeration = this.c_listBindings_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                namingEnumeration = this.c_listBindings(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
        return namingEnumeration;
    }
    
    @Override
    protected void p_bind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_bind_nns(p_resolveIntermediate.getHead(), o, continuation);
                break;
            }
            case 2: {
                this.c_bind(p_resolveIntermediate.getHead(), o, continuation);
                break;
            }
        }
    }
    
    @Override
    protected void p_rebind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_rebind_nns(p_resolveIntermediate.getHead(), o, continuation);
                break;
            }
            case 2: {
                this.c_rebind(p_resolveIntermediate.getHead(), o, continuation);
                break;
            }
        }
    }
    
    @Override
    protected void p_unbind(final Name name, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_unbind_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                this.c_unbind(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
    }
    
    @Override
    protected void p_destroySubcontext(final Name name, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_destroySubcontext_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                this.c_destroySubcontext(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
    }
    
    @Override
    protected Context p_createSubcontext(final Name name, final Continuation continuation) throws NamingException {
        Context context = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                context = this.c_createSubcontext_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                context = this.c_createSubcontext(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
        return context;
    }
    
    @Override
    protected void p_rename(final Name name, final Name name2, final Continuation continuation) throws NamingException {
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                this.c_rename_nns(p_resolveIntermediate.getHead(), name2, continuation);
                break;
            }
            case 2: {
                this.c_rename(p_resolveIntermediate.getHead(), name2, continuation);
                break;
            }
        }
    }
    
    @Override
    protected NameParser p_getNameParser(final Name name, final Continuation continuation) throws NamingException {
        NameParser nameParser = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                nameParser = this.c_getNameParser_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                nameParser = this.c_getNameParser(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
        return nameParser;
    }
    
    @Override
    protected Object p_lookupLink(final Name name, final Continuation continuation) throws NamingException {
        Object o = null;
        final HeadTail p_resolveIntermediate = this.p_resolveIntermediate(name, continuation);
        switch (p_resolveIntermediate.getStatus()) {
            case 3: {
                o = this.c_lookupLink_nns(p_resolveIntermediate.getHead(), continuation);
                break;
            }
            case 2: {
                o = this.c_lookupLink(p_resolveIntermediate.getHead(), continuation);
                break;
            }
        }
        return o;
    }
    
    static {
        ComponentContext.debug = 0;
    }
}
