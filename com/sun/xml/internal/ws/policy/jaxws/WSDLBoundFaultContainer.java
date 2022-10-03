package com.sun.xml.internal.ws.policy.jaxws;

import org.xml.sax.Locator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;

class WSDLBoundFaultContainer implements WSDLObject
{
    private final WSDLBoundFault boundFault;
    private final WSDLBoundOperation boundOperation;
    
    public WSDLBoundFaultContainer(final WSDLBoundFault fault, final WSDLBoundOperation operation) {
        this.boundFault = fault;
        this.boundOperation = operation;
    }
    
    @Override
    public Locator getLocation() {
        return null;
    }
    
    public WSDLBoundFault getBoundFault() {
        return this.boundFault;
    }
    
    public WSDLBoundOperation getBoundOperation() {
        return this.boundOperation;
    }
}
