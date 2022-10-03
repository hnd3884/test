package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import java.util.HashMap;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import java.util.Map;

final class SOAPActionBasedOperationFinder extends WSDLOperationFinder
{
    private final Map<String, WSDLOperationMapping> methodHandlers;
    
    public SOAPActionBasedOperationFinder(final WSDLPort wsdlModel, final WSBinding binding, @Nullable final SEIModel seiModel) {
        super(wsdlModel, binding, seiModel);
        this.methodHandlers = new HashMap<String, WSDLOperationMapping>();
        final Map<String, Integer> unique = new HashMap<String, Integer>();
        if (seiModel != null) {
            for (final JavaMethodImpl m : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                final String soapAction = m.getSOAPAction();
                Integer count = unique.get(soapAction);
                if (count == null) {
                    unique.put(soapAction, 1);
                }
                else {
                    unique.put(soapAction, ++count);
                }
            }
            for (final JavaMethodImpl m : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                final String soapAction = m.getSOAPAction();
                if (unique.get(soapAction) == 1) {
                    this.methodHandlers.put('\"' + soapAction + '\"', this.wsdlOperationMapping(m));
                }
            }
        }
        else {
            for (final WSDLBoundOperation wsdlOp : wsdlModel.getBinding().getBindingOperations()) {
                this.methodHandlers.put(wsdlOp.getSOAPAction(), this.wsdlOperationMapping(wsdlOp));
            }
        }
    }
    
    @Override
    public WSDLOperationMapping getWSDLOperationMapping(final Packet request) throws DispatchException {
        return (request.soapAction == null) ? null : this.methodHandlers.get(request.soapAction);
    }
}
