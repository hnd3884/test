package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.util.ArrayList;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.List;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.util.QNameMap;
import javax.xml.namespace.QName;
import java.util.logging.Logger;

final class PayloadQNameBasedOperationFinder extends WSDLOperationFinder
{
    private static final Logger LOGGER;
    public static final String EMPTY_PAYLOAD_LOCAL = "";
    public static final String EMPTY_PAYLOAD_NSURI = "";
    public static final QName EMPTY_PAYLOAD;
    private final QNameMap<WSDLOperationMapping> methodHandlers;
    private final QNameMap<List<String>> unique;
    
    public PayloadQNameBasedOperationFinder(final WSDLPort wsdlModel, final WSBinding binding, @Nullable final SEIModel seiModel) {
        super(wsdlModel, binding, seiModel);
        this.methodHandlers = new QNameMap<WSDLOperationMapping>();
        this.unique = new QNameMap<List<String>>();
        if (seiModel != null) {
            for (final JavaMethodImpl m : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                if (m.getMEP().isAsync) {
                    continue;
                }
                QName name = m.getRequestPayloadName();
                if (name == null) {
                    name = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
                }
                List<String> methods = this.unique.get(name);
                if (methods == null) {
                    methods = new ArrayList<String>();
                    this.unique.put(name, methods);
                }
                methods.add(m.getMethod().getName());
            }
            for (final QNameMap.Entry<List<String>> e : this.unique.entrySet()) {
                if (e.getValue().size() > 1) {
                    PayloadQNameBasedOperationFinder.LOGGER.warning(ServerMessages.NON_UNIQUE_DISPATCH_QNAME(e.getValue(), e.createQName()));
                }
            }
            for (final JavaMethodImpl m : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                QName name = m.getRequestPayloadName();
                if (name == null) {
                    name = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
                }
                if (this.unique.get(name).size() == 1) {
                    this.methodHandlers.put(name, this.wsdlOperationMapping(m));
                }
            }
        }
        else {
            for (final WSDLBoundOperation wsdlOp : wsdlModel.getBinding().getBindingOperations()) {
                QName name = wsdlOp.getRequestPayloadName();
                if (name == null) {
                    name = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
                }
                this.methodHandlers.put(name, this.wsdlOperationMapping(wsdlOp));
            }
        }
    }
    
    @Override
    public WSDLOperationMapping getWSDLOperationMapping(final Packet request) throws DispatchException {
        final Message message = request.getMessage();
        String localPart = message.getPayloadLocalPart();
        String nsUri;
        if (localPart == null) {
            localPart = "";
            nsUri = "";
        }
        else {
            nsUri = message.getPayloadNamespaceURI();
            if (nsUri == null) {
                nsUri = "";
            }
        }
        final WSDLOperationMapping op = this.methodHandlers.get(nsUri, localPart);
        if (op == null && !this.unique.containsKey(nsUri, localPart)) {
            final String dispatchKey = "{" + nsUri + "}" + localPart;
            final String faultString = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(dispatchKey);
            throw new DispatchException(SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), faultString, this.binding.getSOAPVersion().faultCodeClient));
        }
        return op;
    }
    
    static {
        LOGGER = Logger.getLogger(PayloadQNameBasedOperationFinder.class.getName());
        EMPTY_PAYLOAD = new QName("", "");
    }
}
