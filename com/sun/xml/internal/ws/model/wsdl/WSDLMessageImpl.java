package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import org.xml.sax.Locator;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;

public final class WSDLMessageImpl extends AbstractExtensibleImpl implements EditableWSDLMessage
{
    private final QName name;
    private final ArrayList<EditableWSDLPart> parts;
    
    public WSDLMessageImpl(final XMLStreamReader xsr, final QName name) {
        super(xsr);
        this.name = name;
        this.parts = new ArrayList<EditableWSDLPart>();
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    @Override
    public void add(final EditableWSDLPart part) {
        this.parts.add(part);
    }
    
    @Override
    public Iterable<EditableWSDLPart> parts() {
        return this.parts;
    }
}
