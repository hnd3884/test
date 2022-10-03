package com.sun.xml.internal.bind.v2.runtime.property;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;

final class ArrayElementNodeProperty<BeanT, ListT, ItemT> extends ArrayElementProperty<BeanT, ListT, ItemT>
{
    public ArrayElementNodeProperty(final JAXBContextImpl p, final RuntimeElementPropertyInfo prop) {
        super(p, prop);
    }
    
    public void serializeItem(final JaxBeanInfo expected, final ItemT item, final XMLSerializer w) throws SAXException, IOException, XMLStreamException {
        if (item == null) {
            w.writeXsiNilTrue();
        }
        else {
            w.childAsXsiType(item, this.fieldName, expected, false);
        }
    }
}
