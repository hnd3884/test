package com.sun.xml.internal.ws.api.model.wsdl;

import java.util.Map;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.istack.internal.Nullable;
import javax.jws.WebParam;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public interface WSDLBoundOperation extends WSDLObject, WSDLExtensible
{
    @NotNull
    QName getName();
    
    @NotNull
    String getSOAPAction();
    
    @NotNull
    WSDLOperation getOperation();
    
    @NotNull
    WSDLBoundPortType getBoundPortType();
    
    ANONYMOUS getAnonymous();
    
    @Nullable
    WSDLPart getPart(@NotNull final String p0, @NotNull final WebParam.Mode p1);
    
    ParameterBinding getInputBinding(final String p0);
    
    ParameterBinding getOutputBinding(final String p0);
    
    ParameterBinding getFaultBinding(final String p0);
    
    String getMimeTypeForInputPart(final String p0);
    
    String getMimeTypeForOutputPart(final String p0);
    
    String getMimeTypeForFaultPart(final String p0);
    
    @NotNull
    Map<String, ? extends WSDLPart> getInParts();
    
    @NotNull
    Map<String, ? extends WSDLPart> getOutParts();
    
    @NotNull
    Iterable<? extends WSDLBoundFault> getFaults();
    
    Map<String, ParameterBinding> getInputParts();
    
    Map<String, ParameterBinding> getOutputParts();
    
    Map<String, ParameterBinding> getFaultParts();
    
    @Nullable
    QName getRequestPayloadName();
    
    @Nullable
    QName getResponsePayloadName();
    
    String getRequestNamespace();
    
    String getResponseNamespace();
    
    public enum ANONYMOUS
    {
        optional, 
        required, 
        prohibited;
    }
}
