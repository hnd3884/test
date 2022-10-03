package com.sun.xml.internal.ws.model.wsdl;

import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;

public final class WSDLPartImpl extends AbstractObjectImpl implements EditableWSDLPart
{
    private final String name;
    private ParameterBinding binding;
    private int index;
    private final WSDLPartDescriptor descriptor;
    
    public WSDLPartImpl(final XMLStreamReader xsr, final String partName, final int index, final WSDLPartDescriptor descriptor) {
        super(xsr);
        this.name = partName;
        this.binding = ParameterBinding.UNBOUND;
        this.index = index;
        this.descriptor = descriptor;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public ParameterBinding getBinding() {
        return this.binding;
    }
    
    @Override
    public void setBinding(final ParameterBinding binding) {
        this.binding = binding;
    }
    
    @Override
    public int getIndex() {
        return this.index;
    }
    
    @Override
    public void setIndex(final int index) {
        this.index = index;
    }
    
    @Override
    public WSDLPartDescriptor getDescriptor() {
        return this.descriptor;
    }
}
