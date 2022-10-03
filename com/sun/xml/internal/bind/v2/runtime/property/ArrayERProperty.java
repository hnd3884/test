package com.sun.xml.internal.bind.v2.runtime.property;

import java.util.Collection;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;

abstract class ArrayERProperty<BeanT, ListT, ItemT> extends ArrayProperty<BeanT, ListT, ItemT>
{
    protected final Name wrapperTagName;
    protected final boolean isWrapperNillable;
    
    protected ArrayERProperty(final JAXBContextImpl grammar, final RuntimePropertyInfo prop, final QName tagName, final boolean isWrapperNillable) {
        super(grammar, prop);
        if (tagName == null) {
            this.wrapperTagName = null;
        }
        else {
            this.wrapperTagName = grammar.nameBuilder.createElementName(tagName);
        }
        this.isWrapperNillable = isWrapperNillable;
    }
    
    @Override
    public final void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        final ListT list = this.acc.get(o);
        if (list != null) {
            if (this.wrapperTagName != null) {
                w.startElement(this.wrapperTagName, null);
                w.endNamespaceDecls(list);
                w.endAttributes();
            }
            this.serializeListBody(o, w, list);
            if (this.wrapperTagName != null) {
                w.endElement();
            }
        }
        else if (this.isWrapperNillable) {
            w.startElement(this.wrapperTagName, null);
            w.writeXsiNilTrue();
            w.endElement();
        }
    }
    
    protected abstract void serializeListBody(final BeanT p0, final XMLSerializer p1, final ListT p2) throws IOException, XMLStreamException, SAXException, AccessorException;
    
    protected abstract void createBodyUnmarshaller(final UnmarshallerChain p0, final QNameMap<ChildLoader> p1);
    
    @Override
    public final void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> loaders) {
        if (this.wrapperTagName != null) {
            final UnmarshallerChain c = new UnmarshallerChain(chain.context);
            final QNameMap<ChildLoader> m = new QNameMap<ChildLoader>();
            this.createBodyUnmarshaller(c, m);
            Loader loader = new ItemsLoader(this.acc, this.lister, m);
            if (this.isWrapperNillable || chain.context.allNillable) {
                loader = new XsiNilLoader(loader);
            }
            loaders.put(this.wrapperTagName, new ChildLoader(loader, null));
        }
        else {
            this.createBodyUnmarshaller(chain, loaders);
        }
    }
    
    private static final class ItemsLoader extends Loader
    {
        private final Accessor acc;
        private final Lister lister;
        private final QNameMap<ChildLoader> children;
        
        public ItemsLoader(final Accessor acc, final Lister lister, final QNameMap<ChildLoader> children) {
            super(false);
            this.acc = acc;
            this.lister = lister;
            this.children = children;
        }
        
        @Override
        public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
            final UnmarshallingContext context = state.getContext();
            context.startScope(1);
            state.setTarget(state.getPrev().getTarget());
            context.getScope(0).start(this.acc, this.lister);
        }
        
        @Override
        public void childElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
            ChildLoader child = this.children.get(ea.uri, ea.local);
            if (child == null) {
                child = this.children.get(StructureLoaderBuilder.CATCH_ALL);
            }
            if (child == null) {
                super.childElement(state, ea);
                return;
            }
            state.setLoader(child.loader);
            state.setReceiver(child.receiver);
        }
        
        @Override
        public void leaveElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
            state.getContext().endScope(1);
        }
        
        @Override
        public Collection<QName> getExpectedChildElements() {
            return this.children.keySet();
        }
    }
    
    protected final class ReceiverImpl implements Receiver
    {
        private final int offset;
        
        protected ReceiverImpl(final int offset) {
            this.offset = offset;
        }
        
        @Override
        public void receive(final UnmarshallingContext.State state, final Object o) throws SAXException {
            state.getContext().getScope(this.offset).add(ArrayERProperty.this.acc, ArrayERProperty.this.lister, o);
        }
    }
}
