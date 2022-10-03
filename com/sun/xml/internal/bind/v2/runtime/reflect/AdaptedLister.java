package com.sun.xml.internal.bind.v2.runtime.reflect;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class AdaptedLister<BeanT, PropT, InMemItemT, OnWireItemT, PackT> extends Lister<BeanT, PropT, OnWireItemT, PackT>
{
    private final Lister<BeanT, PropT, InMemItemT, PackT> core;
    private final Class<? extends XmlAdapter<OnWireItemT, InMemItemT>> adapter;
    
    AdaptedLister(final Lister<BeanT, PropT, InMemItemT, PackT> core, final Class<? extends XmlAdapter<OnWireItemT, InMemItemT>> adapter) {
        this.core = core;
        this.adapter = adapter;
    }
    
    private XmlAdapter<OnWireItemT, InMemItemT> getAdapter() {
        return Coordinator._getInstance().getAdapter(this.adapter);
    }
    
    @Override
    public ListIterator<OnWireItemT> iterator(final PropT prop, final XMLSerializer context) {
        return new ListIteratorImpl(this.core.iterator(prop, context), context);
    }
    
    @Override
    public PackT startPacking(final BeanT bean, final Accessor<BeanT, PropT> accessor) throws AccessorException {
        return this.core.startPacking(bean, accessor);
    }
    
    @Override
    public void addToPack(final PackT pack, final OnWireItemT item) throws AccessorException {
        InMemItemT r;
        try {
            r = this.getAdapter().unmarshal(item);
        }
        catch (final Exception e) {
            throw new AccessorException(e);
        }
        this.core.addToPack(pack, r);
    }
    
    @Override
    public void endPacking(final PackT pack, final BeanT bean, final Accessor<BeanT, PropT> accessor) throws AccessorException {
        this.core.endPacking(pack, bean, accessor);
    }
    
    @Override
    public void reset(final BeanT bean, final Accessor<BeanT, PropT> accessor) throws AccessorException {
        this.core.reset(bean, accessor);
    }
    
    private final class ListIteratorImpl implements ListIterator<OnWireItemT>
    {
        private final ListIterator<InMemItemT> core;
        private final XMLSerializer serializer;
        
        public ListIteratorImpl(final ListIterator<InMemItemT> core, final XMLSerializer serializer) {
            this.core = core;
            this.serializer = serializer;
        }
        
        @Override
        public boolean hasNext() {
            return this.core.hasNext();
        }
        
        @Override
        public OnWireItemT next() throws SAXException, JAXBException {
            final InMemItemT next = this.core.next();
            try {
                return AdaptedLister.this.getAdapter().marshal(next);
            }
            catch (final Exception e) {
                this.serializer.reportError(null, e);
                return null;
            }
        }
    }
}
