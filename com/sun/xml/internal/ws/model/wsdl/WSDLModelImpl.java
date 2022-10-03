package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import java.util.List;
import org.xml.sax.Locator;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import java.util.Iterator;
import javax.jws.WebParam;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashMap;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;

public final class WSDLModelImpl extends AbstractExtensibleImpl implements EditableWSDLModel
{
    private final Map<QName, EditableWSDLMessage> messages;
    private final Map<QName, EditableWSDLPortType> portTypes;
    private final Map<QName, EditableWSDLBoundPortType> bindings;
    private final Map<QName, EditableWSDLService> services;
    private PolicyMap policyMap;
    private final Map<QName, EditableWSDLBoundPortType> unmBindings;
    
    public WSDLModelImpl(@NotNull final String systemId) {
        super(systemId, -1);
        this.messages = new HashMap<QName, EditableWSDLMessage>();
        this.portTypes = new HashMap<QName, EditableWSDLPortType>();
        this.bindings = new HashMap<QName, EditableWSDLBoundPortType>();
        this.services = new LinkedHashMap<QName, EditableWSDLService>();
        this.unmBindings = Collections.unmodifiableMap((Map<? extends QName, ? extends EditableWSDLBoundPortType>)this.bindings);
    }
    
    public WSDLModelImpl() {
        super(null, -1);
        this.messages = new HashMap<QName, EditableWSDLMessage>();
        this.portTypes = new HashMap<QName, EditableWSDLPortType>();
        this.bindings = new HashMap<QName, EditableWSDLBoundPortType>();
        this.services = new LinkedHashMap<QName, EditableWSDLService>();
        this.unmBindings = Collections.unmodifiableMap((Map<? extends QName, ? extends EditableWSDLBoundPortType>)this.bindings);
    }
    
    @Override
    public void addMessage(final EditableWSDLMessage msg) {
        this.messages.put(msg.getName(), msg);
    }
    
    @Override
    public EditableWSDLMessage getMessage(final QName name) {
        return this.messages.get(name);
    }
    
    @Override
    public void addPortType(final EditableWSDLPortType pt) {
        this.portTypes.put(pt.getName(), pt);
    }
    
    @Override
    public EditableWSDLPortType getPortType(final QName name) {
        return this.portTypes.get(name);
    }
    
    @Override
    public void addBinding(final EditableWSDLBoundPortType boundPortType) {
        assert !this.bindings.containsValue(boundPortType);
        this.bindings.put(boundPortType.getName(), boundPortType);
    }
    
    @Override
    public EditableWSDLBoundPortType getBinding(final QName name) {
        return this.bindings.get(name);
    }
    
    @Override
    public void addService(final EditableWSDLService svc) {
        this.services.put(svc.getName(), svc);
    }
    
    @Override
    public EditableWSDLService getService(final QName name) {
        return this.services.get(name);
    }
    
    @Override
    public Map<QName, EditableWSDLMessage> getMessages() {
        return this.messages;
    }
    
    @NotNull
    @Override
    public Map<QName, EditableWSDLPortType> getPortTypes() {
        return this.portTypes;
    }
    
    @NotNull
    @Override
    public Map<QName, ? extends EditableWSDLBoundPortType> getBindings() {
        return this.unmBindings;
    }
    
    @NotNull
    @Override
    public Map<QName, EditableWSDLService> getServices() {
        return this.services;
    }
    
    @Override
    public QName getFirstServiceName() {
        if (this.services.isEmpty()) {
            return null;
        }
        return this.services.values().iterator().next().getName();
    }
    
    @Override
    public EditableWSDLBoundPortType getBinding(final QName serviceName, final QName portName) {
        final EditableWSDLService service = this.services.get(serviceName);
        if (service != null) {
            final EditableWSDLPort port = service.get(portName);
            if (port != null) {
                return port.getBinding();
            }
        }
        return null;
    }
    
    @Override
    public void finalizeRpcLitBinding(final EditableWSDLBoundPortType boundPortType) {
        assert boundPortType != null;
        final QName portTypeName = boundPortType.getPortTypeName();
        if (portTypeName == null) {
            return;
        }
        final WSDLPortType pt = this.portTypes.get(portTypeName);
        if (pt == null) {
            return;
        }
        for (final EditableWSDLBoundOperation bop : boundPortType.getBindingOperations()) {
            final WSDLOperation pto = pt.get(bop.getName().getLocalPart());
            final WSDLMessage inMsgName = pto.getInput().getMessage();
            if (inMsgName == null) {
                continue;
            }
            final EditableWSDLMessage inMsg = this.messages.get(inMsgName.getName());
            int bodyindex = 0;
            if (inMsg != null) {
                for (final EditableWSDLPart part : inMsg.parts()) {
                    final String name = part.getName();
                    final ParameterBinding pb = bop.getInputBinding(name);
                    if (pb.isBody()) {
                        part.setIndex(bodyindex++);
                        part.setBinding(pb);
                        bop.addPart(part, WebParam.Mode.IN);
                    }
                }
            }
            bodyindex = 0;
            if (pto.isOneWay()) {
                continue;
            }
            final WSDLMessage outMsgName = pto.getOutput().getMessage();
            if (outMsgName == null) {
                continue;
            }
            final EditableWSDLMessage outMsg = this.messages.get(outMsgName.getName());
            if (outMsg == null) {
                continue;
            }
            for (final EditableWSDLPart part2 : outMsg.parts()) {
                final String name2 = part2.getName();
                final ParameterBinding pb2 = bop.getOutputBinding(name2);
                if (pb2.isBody()) {
                    part2.setIndex(bodyindex++);
                    part2.setBinding(pb2);
                    bop.addPart(part2, WebParam.Mode.OUT);
                }
            }
        }
    }
    
    @Override
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }
    
    @Override
    public void setPolicyMap(final PolicyMap policyMap) {
        this.policyMap = policyMap;
    }
    
    @Override
    public void freeze() {
        for (final EditableWSDLService service : this.services.values()) {
            service.freeze(this);
        }
        for (final EditableWSDLBoundPortType bp : this.bindings.values()) {
            bp.freeze();
        }
        for (final EditableWSDLPortType pt : this.portTypes.values()) {
            pt.freeze();
        }
    }
}
