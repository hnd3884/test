package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.jws.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;

public interface EditableWSDLBoundPortType extends WSDLBoundPortType
{
    @NotNull
    EditableWSDLModel getOwner();
    
    EditableWSDLBoundOperation get(final QName p0);
    
    EditableWSDLPortType getPortType();
    
    Iterable<? extends EditableWSDLBoundOperation> getBindingOperations();
    
    @Nullable
    EditableWSDLBoundOperation getOperation(final String p0, final String p1);
    
    void put(final QName p0, final EditableWSDLBoundOperation p1);
    
    void setBindingId(final BindingID p0);
    
    void setStyle(final SOAPBinding.Style p0);
    
    void freeze();
}
