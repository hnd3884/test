package com.sun.xml.internal.bind.v2.runtime.property;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListTransducedAccessorImpl;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.Name;

final class ListElementProperty<BeanT, ListT, ItemT> extends ArrayProperty<BeanT, ListT, ItemT>
{
    private final Name tagName;
    private final String defaultValue;
    private final TransducedAccessor<BeanT> xacc;
    
    public ListElementProperty(final JAXBContextImpl grammar, final RuntimeElementPropertyInfo prop) {
        super(grammar, prop);
        assert prop.isValueList();
        assert prop.getTypes().size() == 1;
        final RuntimeTypeRef ref = (RuntimeTypeRef)prop.getTypes().get(0);
        this.tagName = grammar.nameBuilder.createElementName(ref.getTagName());
        this.defaultValue = ref.getDefaultValue();
        final Transducer xducer = ref.getTransducer();
        this.xacc = (TransducedAccessor<BeanT>)new ListTransducedAccessorImpl(xducer, (Accessor<Object, Object>)this.acc, (Lister<Object, Object, Object, Object>)this.lister);
    }
    
    @Override
    public PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }
    
    @Override
    public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
        Loader l = new LeafPropertyLoader(this.xacc);
        l = new DefaultValueLoaderDecorator(l, this.defaultValue);
        handlers.put(this.tagName, new ChildLoader(l, null));
    }
    
    @Override
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        final ListT list = this.acc.get(o);
        if (list != null) {
            if (this.xacc.useNamespace()) {
                w.startElement(this.tagName, null);
                this.xacc.declareNamespace(o, w);
                w.endNamespaceDecls(list);
                w.endAttributes();
                this.xacc.writeText(w, o, this.fieldName);
                w.endElement();
            }
            else {
                this.xacc.writeLeafElement(w, this.tagName, o, this.fieldName);
            }
        }
    }
    
    @Override
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        if (this.tagName != null && this.tagName.equals(nsUri, localName)) {
            return this.acc;
        }
        return null;
    }
}
