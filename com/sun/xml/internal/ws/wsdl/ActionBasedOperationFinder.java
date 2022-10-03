package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.namespace.QName;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import java.util.HashMap;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import java.util.Map;
import java.util.logging.Logger;

final class ActionBasedOperationFinder extends WSDLOperationFinder
{
    private static final Logger LOGGER;
    private final Map<ActionBasedOperationSignature, WSDLOperationMapping> uniqueOpSignatureMap;
    private final Map<String, WSDLOperationMapping> actionMap;
    @NotNull
    private final AddressingVersion av;
    
    public ActionBasedOperationFinder(final WSDLPort wsdlModel, final WSBinding binding, @Nullable final SEIModel seiModel) {
        super(wsdlModel, binding, seiModel);
        assert binding.getAddressingVersion() != null;
        this.av = binding.getAddressingVersion();
        this.uniqueOpSignatureMap = new HashMap<ActionBasedOperationSignature, WSDLOperationMapping>();
        this.actionMap = new HashMap<String, WSDLOperationMapping>();
        if (seiModel != null) {
            for (final JavaMethodImpl m : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                if (m.getMEP().isAsync) {
                    continue;
                }
                String action = m.getInputAction();
                QName payloadName = m.getRequestPayloadName();
                if (payloadName == null) {
                    payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
                }
                if ((action == null || action.equals("")) && m.getOperation() != null) {
                    action = m.getOperation().getOperation().getInput().getAction();
                }
                if (action == null) {
                    continue;
                }
                final ActionBasedOperationSignature opSignature = new ActionBasedOperationSignature(action, payloadName);
                if (this.uniqueOpSignatureMap.get(opSignature) != null) {
                    ActionBasedOperationFinder.LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(opSignature), m.getOperationQName(), action, payloadName));
                }
                this.uniqueOpSignatureMap.put(opSignature, this.wsdlOperationMapping(m));
                this.actionMap.put(action, this.wsdlOperationMapping(m));
            }
        }
        else {
            for (final WSDLBoundOperation wsdlOp : wsdlModel.getBinding().getBindingOperations()) {
                QName payloadName2 = wsdlOp.getRequestPayloadName();
                if (payloadName2 == null) {
                    payloadName2 = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
                }
                final String action2 = wsdlOp.getOperation().getInput().getAction();
                final ActionBasedOperationSignature opSignature = new ActionBasedOperationSignature(action2, payloadName2);
                if (this.uniqueOpSignatureMap.get(opSignature) != null) {
                    ActionBasedOperationFinder.LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(opSignature), wsdlOp.getName(), action2, payloadName2));
                }
                this.uniqueOpSignatureMap.put(opSignature, this.wsdlOperationMapping(wsdlOp));
                this.actionMap.put(action2, this.wsdlOperationMapping(wsdlOp));
            }
        }
    }
    
    @Override
    public WSDLOperationMapping getWSDLOperationMapping(final Packet request) throws DispatchException {
        final MessageHeaders hl = request.getMessage().getHeaders();
        final String action = AddressingUtils.getAction(hl, this.av, this.binding.getSOAPVersion());
        if (action == null) {
            return null;
        }
        final Message message = request.getMessage();
        final String localPart = message.getPayloadLocalPart();
        QName payloadName;
        if (localPart == null) {
            payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
        }
        else {
            String nsUri = message.getPayloadNamespaceURI();
            if (nsUri == null) {
                nsUri = "";
            }
            payloadName = new QName(nsUri, localPart);
        }
        WSDLOperationMapping opMapping = this.uniqueOpSignatureMap.get(new ActionBasedOperationSignature(action, payloadName));
        if (opMapping != null) {
            return opMapping;
        }
        opMapping = this.actionMap.get(action);
        if (opMapping != null) {
            return opMapping;
        }
        final Message result = Messages.create(action, this.av, this.binding.getSOAPVersion());
        throw new DispatchException(result);
    }
    
    static {
        LOGGER = Logger.getLogger(ActionBasedOperationFinder.class.getName());
    }
}
