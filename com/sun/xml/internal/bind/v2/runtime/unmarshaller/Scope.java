package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public final class Scope<BeanT, PropT, ItemT, PackT>
{
    public final UnmarshallingContext context;
    private BeanT bean;
    private Accessor<BeanT, PropT> acc;
    private PackT pack;
    private Lister<BeanT, PropT, ItemT, PackT> lister;
    
    Scope(final UnmarshallingContext context) {
        this.context = context;
    }
    
    public boolean hasStarted() {
        return this.bean != null;
    }
    
    public void reset() {
        if (this.bean != null) {
            this.bean = null;
            this.acc = null;
            this.pack = null;
            this.lister = null;
            return;
        }
        assert this.clean();
    }
    
    public void finish() throws AccessorException {
        if (this.hasStarted()) {
            this.lister.endPacking(this.pack, this.bean, this.acc);
            this.reset();
        }
        assert this.clean();
    }
    
    private boolean clean() {
        return this.bean == null && this.acc == null && this.pack == null && this.lister == null;
    }
    
    public void add(final Accessor<BeanT, PropT> acc, final Lister<BeanT, PropT, ItemT, PackT> lister, final ItemT value) throws SAXException {
        try {
            if (!this.hasStarted()) {
                this.bean = (BeanT)this.context.getCurrentState().getTarget();
                this.acc = acc;
                this.lister = lister;
                this.pack = lister.startPacking(this.bean, acc);
            }
            lister.addToPack(this.pack, value);
        }
        catch (final AccessorException e) {
            Loader.handleGenericException(e, true);
            this.lister = Lister.getErrorInstance();
            this.acc = Accessor.getErrorInstance();
        }
    }
    
    public void start(final Accessor<BeanT, PropT> acc, final Lister<BeanT, PropT, ItemT, PackT> lister) throws SAXException {
        try {
            if (!this.hasStarted()) {
                this.bean = (BeanT)this.context.getCurrentState().getTarget();
                this.acc = acc;
                this.lister = lister;
                this.pack = lister.startPacking(this.bean, acc);
            }
        }
        catch (final AccessorException e) {
            Loader.handleGenericException(e, true);
            this.lister = Lister.getErrorInstance();
            this.acc = Accessor.getErrorInstance();
        }
    }
}
