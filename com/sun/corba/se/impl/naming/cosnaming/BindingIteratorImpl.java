package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.Binding;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingHolder;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.BindingIteratorPOA;

public abstract class BindingIteratorImpl extends BindingIteratorPOA
{
    protected ORB orb;
    
    public BindingIteratorImpl(final ORB orb) throws Exception {
        this.orb = orb;
    }
    
    @Override
    public synchronized boolean next_one(final BindingHolder bindingHolder) {
        return this.NextOne(bindingHolder);
    }
    
    @Override
    public synchronized boolean next_n(final int n, final BindingListHolder bindingListHolder) {
        if (n == 0) {
            throw new BAD_PARAM(" 'how_many' parameter is set to 0 which is invalid");
        }
        return this.list(n, bindingListHolder);
    }
    
    public boolean list(final int n, final BindingListHolder bindingListHolder) {
        final int min = Math.min(this.RemainingElements(), n);
        final Binding[] value = new Binding[min];
        BindingHolder bindingHolder;
        int n2;
        for (bindingHolder = new BindingHolder(), n2 = 0; n2 < min && this.NextOne(bindingHolder); ++n2) {
            value[n2] = bindingHolder.value;
        }
        if (n2 == 0) {
            bindingListHolder.value = new Binding[0];
            return false;
        }
        bindingListHolder.value = value;
        return true;
    }
    
    @Override
    public synchronized void destroy() {
        this.Destroy();
    }
    
    protected abstract boolean NextOne(final BindingHolder p0);
    
    protected abstract void Destroy();
    
    protected abstract int RemainingElements();
}
