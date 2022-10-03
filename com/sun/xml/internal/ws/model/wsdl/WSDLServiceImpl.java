package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import org.xml.sax.Locator;
import com.sun.istack.internal.Nullable;
import java.util.Iterator;
import com.sun.istack.internal.NotNull;
import java.util.LinkedHashMap;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;

public final class WSDLServiceImpl extends AbstractExtensibleImpl implements EditableWSDLService
{
    private final QName name;
    private final Map<QName, EditableWSDLPort> ports;
    private final EditableWSDLModel parent;
    
    public WSDLServiceImpl(final XMLStreamReader xsr, final EditableWSDLModel parent, final QName name) {
        super(xsr);
        this.parent = parent;
        this.name = name;
        this.ports = new LinkedHashMap<QName, EditableWSDLPort>();
    }
    
    @NotNull
    @Override
    public EditableWSDLModel getParent() {
        return this.parent;
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    @Override
    public EditableWSDLPort get(final QName portName) {
        return this.ports.get(portName);
    }
    
    @Override
    public EditableWSDLPort getFirstPort() {
        if (this.ports.isEmpty()) {
            return null;
        }
        return this.ports.values().iterator().next();
    }
    
    @Override
    public Iterable<EditableWSDLPort> getPorts() {
        return this.ports.values();
    }
    
    @Nullable
    @Override
    public EditableWSDLPort getMatchingPort(final QName portTypeName) {
        for (final EditableWSDLPort port : this.getPorts()) {
            final QName ptName = port.getBinding().getPortTypeName();
            assert ptName != null;
            if (ptName.equals(portTypeName)) {
                return port;
            }
        }
        return null;
    }
    
    @Override
    public void put(final QName portName, final EditableWSDLPort port) {
        if (portName == null || port == null) {
            throw new NullPointerException();
        }
        this.ports.put(portName, port);
    }
    
    @Override
    public void freeze(final EditableWSDLModel root) {
        for (final EditableWSDLPort port : this.ports.values()) {
            port.freeze(root);
        }
    }
}
