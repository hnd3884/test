package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import javax.jws.soap.SOAPBinding;
import java.util.Map;
import com.sun.istack.internal.Nullable;
import javax.jws.WebParam;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;

public interface EditableWSDLBoundOperation extends WSDLBoundOperation
{
    @NotNull
    EditableWSDLOperation getOperation();
    
    @NotNull
    EditableWSDLBoundPortType getBoundPortType();
    
    @Nullable
    EditableWSDLPart getPart(@NotNull final String p0, @NotNull final WebParam.Mode p1);
    
    @NotNull
    Map<String, ? extends EditableWSDLPart> getInParts();
    
    @NotNull
    Map<String, ? extends EditableWSDLPart> getOutParts();
    
    @NotNull
    Iterable<? extends EditableWSDLBoundFault> getFaults();
    
    void addPart(final EditableWSDLPart p0, final WebParam.Mode p1);
    
    void addFault(@NotNull final EditableWSDLBoundFault p0);
    
    void setAnonymous(final ANONYMOUS p0);
    
    void setInputExplicitBodyParts(final boolean p0);
    
    void setOutputExplicitBodyParts(final boolean p0);
    
    void setFaultExplicitBodyParts(final boolean p0);
    
    void setRequestNamespace(final String p0);
    
    void setResponseNamespace(final String p0);
    
    void setSoapAction(final String p0);
    
    void setStyle(final SOAPBinding.Style p0);
    
    void freeze(final EditableWSDLModel p0);
}
