package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.message.Message;
import java.util.Iterator;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.text.MessageFormat;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.ArrayList;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.List;

public class OperationDispatcher
{
    private List<WSDLOperationFinder> opFinders;
    private WSBinding binding;
    
    public OperationDispatcher(@NotNull final WSDLPort wsdlModel, @NotNull final WSBinding binding, @Nullable final SEIModel seiModel) {
        this.binding = binding;
        this.opFinders = new ArrayList<WSDLOperationFinder>();
        if (binding.getAddressingVersion() != null) {
            this.opFinders.add(new ActionBasedOperationFinder(wsdlModel, binding, seiModel));
        }
        this.opFinders.add(new PayloadQNameBasedOperationFinder(wsdlModel, binding, seiModel));
        this.opFinders.add(new SOAPActionBasedOperationFinder(wsdlModel, binding, seiModel));
    }
    
    @NotNull
    @Deprecated
    public QName getWSDLOperationQName(final Packet request) throws DispatchException {
        final WSDLOperationMapping m = this.getWSDLOperationMapping(request);
        return (m != null) ? m.getOperationName() : null;
    }
    
    @NotNull
    public WSDLOperationMapping getWSDLOperationMapping(final Packet request) throws DispatchException {
        for (final WSDLOperationFinder finder : this.opFinders) {
            final WSDLOperationMapping opName = finder.getWSDLOperationMapping(request);
            if (opName != null) {
                return opName;
            }
        }
        final String err = MessageFormat.format("Request=[SOAPAction={0},Payload='{'{1}'}'{2}]", request.soapAction, request.getMessage().getPayloadNamespaceURI(), request.getMessage().getPayloadLocalPart());
        final String faultString = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(err);
        final Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), faultString, this.binding.getSOAPVersion().faultCodeClient);
        throw new DispatchException(faultMsg);
    }
}
