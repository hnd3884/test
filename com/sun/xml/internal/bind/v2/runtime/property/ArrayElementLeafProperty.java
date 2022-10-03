package com.sun.xml.internal.bind.v2.runtime.property;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Transducer;

final class ArrayElementLeafProperty<BeanT, ListT, ItemT> extends ArrayElementProperty<BeanT, ListT, ItemT>
{
    private final Transducer<ItemT> xducer;
    
    public ArrayElementLeafProperty(final JAXBContextImpl p, final RuntimeElementPropertyInfo prop) {
        super(p, prop);
        assert prop.getTypes().size() == 1;
        this.xducer = ((RuntimeTypeRef)prop.getTypes().get(0)).getTransducer();
        assert this.xducer != null;
    }
    
    public void serializeItem(final JaxBeanInfo bi, final ItemT item, final XMLSerializer w) throws SAXException, AccessorException, IOException, XMLStreamException {
        this.xducer.declareNamespace(item, w);
        w.endNamespaceDecls(item);
        w.endAttributes();
        this.xducer.writeText(w, item, this.fieldName);
    }
}
