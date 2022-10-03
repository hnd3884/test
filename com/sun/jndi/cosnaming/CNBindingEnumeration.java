package com.sun.jndi.cosnaming;

import javax.naming.Name;
import javax.naming.Context;
import javax.naming.spi.NamingManager;
import com.sun.jndi.toolkit.corba.CorbaUtils;
import javax.naming.NamingException;
import java.util.NoSuchElementException;
import org.omg.CosNaming.BindingIteratorHolder;
import java.util.Hashtable;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingListHolder;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;

final class CNBindingEnumeration implements NamingEnumeration<Binding>
{
    private static final int DEFAULT_BATCHSIZE = 100;
    private BindingListHolder _bindingList;
    private BindingIterator _bindingIter;
    private int counter;
    private int batchsize;
    private CNCtx _ctx;
    private Hashtable<?, ?> _env;
    private boolean more;
    private boolean isLookedUpCtx;
    
    CNBindingEnumeration(final CNCtx ctx, final boolean isLookedUpCtx, final Hashtable<?, ?> env) {
        this.batchsize = 100;
        this.more = false;
        this.isLookedUpCtx = false;
        final String s = (env != null) ? ((String)env.get("java.naming.batchsize")) : null;
        if (s != null) {
            try {
                this.batchsize = Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                throw new IllegalArgumentException("Batch size not numeric: " + s);
            }
        }
        (this._ctx = ctx).incEnumCount();
        this.isLookedUpCtx = isLookedUpCtx;
        this._env = env;
        this._bindingList = new BindingListHolder();
        final BindingIteratorHolder bindingIteratorHolder = new BindingIteratorHolder();
        this._ctx._nc.list(0, this._bindingList, bindingIteratorHolder);
        this._bindingIter = bindingIteratorHolder.value;
        if (this._bindingIter != null) {
            this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
        }
        else {
            this.more = false;
        }
        this.counter = 0;
    }
    
    @Override
    public Binding next() throws NamingException {
        if (this.more && this.counter >= this._bindingList.value.length) {
            this.getMore();
        }
        if (this.more && this.counter < this._bindingList.value.length) {
            final org.omg.CosNaming.Binding binding = this._bindingList.value[this.counter];
            ++this.counter;
            return this.mapBinding(binding);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public boolean hasMore() throws NamingException {
        return this.more && (this.counter < this._bindingList.value.length || this.getMore());
    }
    
    @Override
    public boolean hasMoreElements() {
        try {
            return this.hasMore();
        }
        catch (final NamingException ex) {
            return false;
        }
    }
    
    @Override
    public Binding nextElement() {
        try {
            return this.next();
        }
        catch (final NamingException ex) {
            throw new NoSuchElementException();
        }
    }
    
    @Override
    public void close() throws NamingException {
        this.more = false;
        if (this._bindingIter != null) {
            this._bindingIter.destroy();
            this._bindingIter = null;
        }
        if (this._ctx != null) {
            this._ctx.decEnumCount();
            if (this.isLookedUpCtx) {
                this._ctx.close();
            }
            this._ctx = null;
        }
    }
    
    @Override
    protected void finalize() {
        try {
            this.close();
        }
        catch (final NamingException ex) {}
    }
    
    private boolean getMore() throws NamingException {
        try {
            this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
            this.counter = 0;
        }
        catch (final Exception rootCause) {
            this.more = false;
            final NamingException ex = new NamingException("Problem getting binding list");
            ex.setRootCause(rootCause);
            throw ex;
        }
        return this.more;
    }
    
    private Binding mapBinding(final org.omg.CosNaming.Binding binding) throws NamingException {
        Object o = this._ctx.callResolve(binding.binding_name);
        final Name cosNameToName = CNNameParser.cosNameToName(binding.binding_name);
        try {
            if (CorbaUtils.isObjectFactoryTrusted(o)) {
                o = NamingManager.getObjectInstance(o, cosNameToName, this._ctx, this._env);
            }
        }
        catch (final NamingException ex) {
            throw ex;
        }
        catch (final Exception rootCause) {
            final NamingException ex2 = new NamingException("problem generating object using object factory");
            ex2.setRootCause(rootCause);
            throw ex2;
        }
        final Binding binding2 = new Binding(cosNameToName.toString(), o);
        binding2.setNameInNamespace(CNNameParser.cosNameToInsString(this._ctx.makeFullName(binding.binding_name)));
        return binding2;
    }
}
