package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Map;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;

public interface EditableWSDLModel extends WSDLModel
{
    EditableWSDLPortType getPortType(@NotNull final QName p0);
    
    void addBinding(final EditableWSDLBoundPortType p0);
    
    EditableWSDLBoundPortType getBinding(@NotNull final QName p0);
    
    EditableWSDLBoundPortType getBinding(@NotNull final QName p0, @NotNull final QName p1);
    
    EditableWSDLService getService(@NotNull final QName p0);
    
    @NotNull
    Map<QName, ? extends EditableWSDLMessage> getMessages();
    
    void addMessage(final EditableWSDLMessage p0);
    
    @NotNull
    Map<QName, ? extends EditableWSDLPortType> getPortTypes();
    
    void addPortType(final EditableWSDLPortType p0);
    
    @NotNull
    Map<QName, ? extends EditableWSDLBoundPortType> getBindings();
    
    @NotNull
    Map<QName, ? extends EditableWSDLService> getServices();
    
    void addService(final EditableWSDLService p0);
    
    EditableWSDLMessage getMessage(final QName p0);
    
    @Deprecated
    void setPolicyMap(final PolicyMap p0);
    
    void finalizeRpcLitBinding(final EditableWSDLBoundPortType p0);
    
    void freeze();
}
