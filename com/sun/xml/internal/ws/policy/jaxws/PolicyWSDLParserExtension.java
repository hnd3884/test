package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import javax.xml.namespace.QName;
import java.util.HashSet;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import java.io.InputStream;
import java.io.Closeable;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.net.URL;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModelContext;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.Collection;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import java.util.LinkedList;
import java.util.List;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Map;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;

public final class PolicyWSDLParserExtension extends WSDLParserExtension
{
    private static final PolicyLogger LOGGER;
    private static final StringBuffer AnonymnousPolicyIdPrefix;
    private int anonymousPoliciesCount;
    private final SafePolicyReader policyReader;
    private SafePolicyReader.PolicyRecord expandQueueHead;
    private Map<String, SafePolicyReader.PolicyRecord> policyRecordsPassedBy;
    private Map<String, PolicySourceModel> anonymousPolicyModels;
    private List<String> unresolvedUris;
    private final LinkedList<String> urisNeeded;
    private final Map<String, PolicySourceModel> modelsNeeded;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4ServiceMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortTypeMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BoundOperationMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OperationMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4MessageMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4InputMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OutputMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4FaultMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingInputOpMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingOutputOpMap;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingFaultOpMap;
    private PolicyMapBuilder policyBuilder;
    
    private boolean isPolicyProcessed(final String policyUri) {
        return this.modelsNeeded.containsKey(policyUri);
    }
    
    private void addNewPolicyNeeded(final String policyUri, final PolicySourceModel policyModel) {
        if (!this.modelsNeeded.containsKey(policyUri)) {
            this.modelsNeeded.put(policyUri, policyModel);
            this.urisNeeded.addFirst(policyUri);
        }
    }
    
    private Map<String, PolicySourceModel> getPolicyModels() {
        return this.modelsNeeded;
    }
    
    private Map<String, SafePolicyReader.PolicyRecord> getPolicyRecordsPassedBy() {
        if (null == this.policyRecordsPassedBy) {
            this.policyRecordsPassedBy = new HashMap<String, SafePolicyReader.PolicyRecord>();
        }
        return this.policyRecordsPassedBy;
    }
    
    private Map<String, PolicySourceModel> getAnonymousPolicyModels() {
        if (null == this.anonymousPolicyModels) {
            this.anonymousPolicyModels = new HashMap<String, PolicySourceModel>();
        }
        return this.anonymousPolicyModels;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4ServiceMap() {
        if (null == this.handlers4ServiceMap) {
            this.handlers4ServiceMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4ServiceMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortMap() {
        if (null == this.handlers4PortMap) {
            this.handlers4PortMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4PortMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortTypeMap() {
        if (null == this.handlers4PortTypeMap) {
            this.handlers4PortTypeMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4PortTypeMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingMap() {
        if (null == this.handlers4BindingMap) {
            this.handlers4BindingMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OperationMap() {
        if (null == this.handlers4OperationMap) {
            this.handlers4OperationMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4OperationMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BoundOperationMap() {
        if (null == this.handlers4BoundOperationMap) {
            this.handlers4BoundOperationMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BoundOperationMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4MessageMap() {
        if (null == this.handlers4MessageMap) {
            this.handlers4MessageMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4MessageMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4InputMap() {
        if (null == this.handlers4InputMap) {
            this.handlers4InputMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4InputMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OutputMap() {
        if (null == this.handlers4OutputMap) {
            this.handlers4OutputMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4OutputMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4FaultMap() {
        if (null == this.handlers4FaultMap) {
            this.handlers4FaultMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4FaultMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingInputOpMap() {
        if (null == this.handlers4BindingInputOpMap) {
            this.handlers4BindingInputOpMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingInputOpMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingOutputOpMap() {
        if (null == this.handlers4BindingOutputOpMap) {
            this.handlers4BindingOutputOpMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingOutputOpMap;
    }
    
    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingFaultOpMap() {
        if (null == this.handlers4BindingFaultOpMap) {
            this.handlers4BindingFaultOpMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingFaultOpMap;
    }
    
    private List<String> getUnresolvedUris(final boolean emptyListNeeded) {
        if (null == this.unresolvedUris || emptyListNeeded) {
            this.unresolvedUris = new LinkedList<String>();
        }
        return this.unresolvedUris;
    }
    
    private void policyRecToExpandQueue(final SafePolicyReader.PolicyRecord policyRec) {
        if (null == this.expandQueueHead) {
            this.expandQueueHead = policyRec;
        }
        else {
            this.expandQueueHead = this.expandQueueHead.insert(policyRec);
        }
    }
    
    public PolicyWSDLParserExtension() {
        this.policyReader = new SafePolicyReader();
        this.expandQueueHead = null;
        this.policyRecordsPassedBy = null;
        this.anonymousPolicyModels = null;
        this.unresolvedUris = null;
        this.urisNeeded = new LinkedList<String>();
        this.modelsNeeded = new HashMap<String, PolicySourceModel>();
        this.handlers4ServiceMap = null;
        this.handlers4PortMap = null;
        this.handlers4PortTypeMap = null;
        this.handlers4BindingMap = null;
        this.handlers4BoundOperationMap = null;
        this.handlers4OperationMap = null;
        this.handlers4MessageMap = null;
        this.handlers4InputMap = null;
        this.handlers4OutputMap = null;
        this.handlers4FaultMap = null;
        this.handlers4BindingInputOpMap = null;
        this.handlers4BindingOutputOpMap = null;
        this.handlers4BindingFaultOpMap = null;
        this.policyBuilder = new PolicyMapBuilder();
    }
    
    private PolicyRecordHandler readSinglePolicy(final SafePolicyReader.PolicyRecord policyRec, final boolean inner) {
        PolicyRecordHandler handler = null;
        String policyId = policyRec.policyModel.getPolicyId();
        if (policyId == null) {
            policyId = policyRec.policyModel.getPolicyName();
        }
        if (policyId != null) {
            handler = new PolicyRecordHandler(HandlerType.PolicyUri, policyRec.getUri());
            this.getPolicyRecordsPassedBy().put(policyRec.getUri(), policyRec);
            this.policyRecToExpandQueue(policyRec);
        }
        else if (inner) {
            final String anonymousId = PolicyWSDLParserExtension.AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString();
            handler = new PolicyRecordHandler(HandlerType.AnonymousPolicyId, anonymousId);
            this.getAnonymousPolicyModels().put(anonymousId, policyRec.policyModel);
            if (null != policyRec.unresolvedURIs) {
                this.getUnresolvedUris(false).addAll(policyRec.unresolvedURIs);
            }
        }
        return handler;
    }
    
    private void addHandlerToMap(final Map<WSDLObject, Collection<PolicyRecordHandler>> map, final WSDLObject key, final PolicyRecordHandler handler) {
        if (map.containsKey(key)) {
            map.get(key).add(handler);
        }
        else {
            final Collection<PolicyRecordHandler> newSet = new LinkedList<PolicyRecordHandler>();
            newSet.add(handler);
            map.put(key, newSet);
        }
    }
    
    private String getBaseUrl(final String policyUri) {
        if (null == policyUri) {
            return null;
        }
        final int fragmentIdx = policyUri.indexOf(35);
        return (fragmentIdx == -1) ? policyUri : policyUri.substring(0, fragmentIdx);
    }
    
    private void processReferenceUri(final String policyUri, final WSDLObject element, final XMLStreamReader reader, final Map<WSDLObject, Collection<PolicyRecordHandler>> map) {
        if (null == policyUri || policyUri.length() == 0) {
            return;
        }
        if ('#' != policyUri.charAt(0)) {
            this.getUnresolvedUris(false).add(policyUri);
        }
        this.addHandlerToMap(map, element, new PolicyRecordHandler(HandlerType.PolicyUri, SafePolicyReader.relativeToAbsoluteUrl(policyUri, reader.getLocation().getSystemId())));
    }
    
    private boolean processSubelement(final WSDLObject element, final XMLStreamReader reader, final Map<WSDLObject, Collection<PolicyRecordHandler>> map) {
        if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.PolicyReference) {
            this.processReferenceUri(this.policyReader.readPolicyReferenceElement(reader), element, reader, map);
            return true;
        }
        if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.Policy) {
            final PolicyRecordHandler handler = this.readSinglePolicy(this.policyReader.readPolicyElement(reader, (null == reader.getLocation().getSystemId()) ? "" : reader.getLocation().getSystemId()), true);
            if (null != handler) {
                this.addHandlerToMap(map, element, handler);
            }
            return true;
        }
        return false;
    }
    
    private void processAttributes(final WSDLObject element, final XMLStreamReader reader, final Map<WSDLObject, Collection<PolicyRecordHandler>> map) {
        final String[] uriArray = this.getPolicyURIsFromAttr(reader);
        if (null != uriArray) {
            for (final String policyUri : uriArray) {
                this.processReferenceUri(policyUri, element, reader, map);
            }
        }
    }
    
    @Override
    public boolean portElements(final EditableWSDLPort port, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(port, reader, this.getHandlers4PortMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void portAttributes(final EditableWSDLPort port, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(port, reader, this.getHandlers4PortMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean serviceElements(final EditableWSDLService service, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(service, reader, this.getHandlers4ServiceMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void serviceAttributes(final EditableWSDLService service, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(service, reader, this.getHandlers4ServiceMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean definitionsElements(final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.Policy) {
            this.readSinglePolicy(this.policyReader.readPolicyElement(reader, (null == reader.getLocation().getSystemId()) ? "" : reader.getLocation().getSystemId()), false);
            PolicyWSDLParserExtension.LOGGER.exiting();
            return true;
        }
        PolicyWSDLParserExtension.LOGGER.exiting();
        return false;
    }
    
    @Override
    public boolean bindingElements(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(binding, reader, this.getHandlers4BindingMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void bindingAttributes(final EditableWSDLBoundPortType binding, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(binding, reader, this.getHandlers4BindingMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean portTypeElements(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(portType, reader, this.getHandlers4PortTypeMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void portTypeAttributes(final EditableWSDLPortType portType, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(portType, reader, this.getHandlers4PortTypeMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean portTypeOperationElements(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(operation, reader, this.getHandlers4OperationMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void portTypeOperationAttributes(final EditableWSDLOperation operation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(operation, reader, this.getHandlers4OperationMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean bindingOperationElements(final EditableWSDLBoundOperation boundOperation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(boundOperation, reader, this.getHandlers4BoundOperationMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void bindingOperationAttributes(final EditableWSDLBoundOperation boundOperation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(boundOperation, reader, this.getHandlers4BoundOperationMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean messageElements(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(msg, reader, this.getHandlers4MessageMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void messageAttributes(final EditableWSDLMessage msg, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(msg, reader, this.getHandlers4MessageMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean portTypeOperationInputElements(final EditableWSDLInput input, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(input, reader, this.getHandlers4InputMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void portTypeOperationInputAttributes(final EditableWSDLInput input, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(input, reader, this.getHandlers4InputMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean portTypeOperationOutputElements(final EditableWSDLOutput output, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(output, reader, this.getHandlers4OutputMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void portTypeOperationOutputAttributes(final EditableWSDLOutput output, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(output, reader, this.getHandlers4OutputMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean portTypeOperationFaultElements(final EditableWSDLFault fault, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(fault, reader, this.getHandlers4FaultMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void portTypeOperationFaultAttributes(final EditableWSDLFault fault, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(fault, reader, this.getHandlers4FaultMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean bindingOperationInputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(operation, reader, this.getHandlers4BindingInputOpMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void bindingOperationInputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(operation, reader, this.getHandlers4BindingInputOpMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean bindingOperationOutputElements(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(operation, reader, this.getHandlers4BindingOutputOpMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
        return result;
    }
    
    @Override
    public void bindingOperationOutputAttributes(final EditableWSDLBoundOperation operation, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(operation, reader, this.getHandlers4BindingOutputOpMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public boolean bindingOperationFaultElements(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        final boolean result = this.processSubelement(fault, reader, this.getHandlers4BindingFaultOpMap());
        PolicyWSDLParserExtension.LOGGER.exiting(result);
        return result;
    }
    
    @Override
    public void bindingOperationFaultAttributes(final EditableWSDLBoundFault fault, final XMLStreamReader reader) {
        PolicyWSDLParserExtension.LOGGER.entering();
        this.processAttributes(fault, reader, this.getHandlers4BindingFaultOpMap());
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    private PolicyMapBuilder getPolicyMapBuilder() {
        if (null == this.policyBuilder) {
            this.policyBuilder = new PolicyMapBuilder();
        }
        return this.policyBuilder;
    }
    
    private Collection<String> getPolicyURIs(final Collection<PolicyRecordHandler> handlers, final PolicySourceModelContext modelContext) throws PolicyException {
        final Collection<String> result = new ArrayList<String>(handlers.size());
        for (final PolicyRecordHandler handler : handlers) {
            String policyUri = handler.handler;
            if (HandlerType.AnonymousPolicyId == handler.type) {
                final PolicySourceModel policyModel = this.getAnonymousPolicyModels().get(policyUri);
                policyModel.expand(modelContext);
                while (this.getPolicyModels().containsKey(policyUri)) {
                    policyUri = PolicyWSDLParserExtension.AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString();
                }
                this.getPolicyModels().put(policyUri, policyModel);
            }
            result.add(policyUri);
        }
        return result;
    }
    
    private boolean readExternalFile(final String fileUrl) {
        InputStream ios = null;
        XMLStreamReader reader = null;
        try {
            final URL xmlURL = new URL(fileUrl);
            ios = xmlURL.openStream();
            reader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(ios);
            while (reader.hasNext()) {
                if (reader.isStartElement() && NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.Policy) {
                    this.readSinglePolicy(this.policyReader.readPolicyElement(reader, fileUrl), false);
                }
                reader.next();
            }
            return true;
        }
        catch (final IOException ioe) {
            return false;
        }
        catch (final XMLStreamException xmlse) {
            return false;
        }
        finally {
            PolicyUtils.IO.closeResource(reader);
            PolicyUtils.IO.closeResource(ios);
        }
    }
    
    @Override
    public void finished(final WSDLParserExtensionContext context) {
        PolicyWSDLParserExtension.LOGGER.entering(context);
        if (null != this.expandQueueHead) {
            final List<String> externalUris = this.getUnresolvedUris(false);
            this.getUnresolvedUris(true);
            final LinkedList<String> baseUnresolvedUris = new LinkedList<String>();
            for (SafePolicyReader.PolicyRecord currentRec = this.expandQueueHead; null != currentRec; currentRec = currentRec.next) {
                baseUnresolvedUris.addFirst(currentRec.getUri());
            }
            this.getUnresolvedUris(false).addAll(baseUnresolvedUris);
            this.expandQueueHead = null;
            this.getUnresolvedUris(false).addAll(externalUris);
        }
        while (!this.getUnresolvedUris(false).isEmpty()) {
            final List<String> urisToBeSolvedList = this.getUnresolvedUris(false);
            this.getUnresolvedUris(true);
            for (final String currentUri : urisToBeSolvedList) {
                if (!this.isPolicyProcessed(currentUri)) {
                    final SafePolicyReader.PolicyRecord prefetchedRecord = this.getPolicyRecordsPassedBy().get(currentUri);
                    if (null == prefetchedRecord) {
                        if (this.policyReader.getUrlsRead().contains(this.getBaseUrl(currentUri))) {
                            PolicyWSDLParserExtension.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1014_CAN_NOT_FIND_POLICY(currentUri)));
                        }
                        else {
                            if (!this.readExternalFile(this.getBaseUrl(currentUri))) {
                                continue;
                            }
                            this.getUnresolvedUris(false).add(currentUri);
                        }
                    }
                    else {
                        if (null != prefetchedRecord.unresolvedURIs) {
                            this.getUnresolvedUris(false).addAll(prefetchedRecord.unresolvedURIs);
                        }
                        this.addNewPolicyNeeded(currentUri, prefetchedRecord.policyModel);
                    }
                }
            }
        }
        final PolicySourceModelContext modelContext = PolicySourceModelContext.createContext();
        for (final String policyUri : this.urisNeeded) {
            final PolicySourceModel sourceModel = this.modelsNeeded.get(policyUri);
            try {
                sourceModel.expand(modelContext);
                modelContext.addModel(new URI(policyUri), sourceModel);
            }
            catch (final URISyntaxException e) {
                PolicyWSDLParserExtension.LOGGER.logSevereException(e);
            }
            catch (final PolicyException e2) {
                PolicyWSDLParserExtension.LOGGER.logSevereException(e2);
            }
        }
        try {
            final HashSet<BuilderHandlerMessageScope> messageSet = new HashSet<BuilderHandlerMessageScope>();
            for (final EditableWSDLService service : context.getWSDLModel().getServices().values()) {
                if (this.getHandlers4ServiceMap().containsKey(service)) {
                    this.getPolicyMapBuilder().registerHandler(new BuilderHandlerServiceScope(this.getPolicyURIs(this.getHandlers4ServiceMap().get(service), modelContext), this.getPolicyModels(), service, service.getName()));
                }
                for (final EditableWSDLPort port : service.getPorts()) {
                    if (this.getHandlers4PortMap().containsKey(port)) {
                        this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs(this.getHandlers4PortMap().get(port), modelContext), this.getPolicyModels(), port, port.getOwner().getName(), port.getName()));
                    }
                    if (null != port.getBinding()) {
                        if (this.getHandlers4BindingMap().containsKey(port.getBinding())) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs(this.getHandlers4BindingMap().get(port.getBinding()), modelContext), this.getPolicyModels(), port.getBinding(), service.getName(), port.getName()));
                        }
                        if (this.getHandlers4PortTypeMap().containsKey(port.getBinding().getPortType())) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs(this.getHandlers4PortTypeMap().get(port.getBinding().getPortType()), modelContext), this.getPolicyModels(), port.getBinding().getPortType(), service.getName(), port.getName()));
                        }
                        for (final EditableWSDLBoundOperation boundOperation : port.getBinding().getBindingOperations()) {
                            final EditableWSDLOperation operation = boundOperation.getOperation();
                            final QName operationName = new QName(boundOperation.getBoundPortType().getName().getNamespaceURI(), boundOperation.getName().getLocalPart());
                            if (this.getHandlers4BoundOperationMap().containsKey(boundOperation)) {
                                this.getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(this.getPolicyURIs(this.getHandlers4BoundOperationMap().get(boundOperation), modelContext), this.getPolicyModels(), boundOperation, service.getName(), port.getName(), operationName));
                            }
                            if (this.getHandlers4OperationMap().containsKey(operation)) {
                                this.getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(this.getPolicyURIs(this.getHandlers4OperationMap().get(operation), modelContext), this.getPolicyModels(), operation, service.getName(), port.getName(), operationName));
                            }
                            final EditableWSDLInput input = operation.getInput();
                            if (null != input) {
                                final EditableWSDLMessage inputMsg = input.getMessage();
                                if (inputMsg != null && this.getHandlers4MessageMap().containsKey(inputMsg)) {
                                    messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4MessageMap().get(inputMsg), modelContext), this.getPolicyModels(), inputMsg, BuilderHandlerMessageScope.Scope.InputMessageScope, service.getName(), port.getName(), operationName, null));
                                }
                            }
                            if (this.getHandlers4BindingInputOpMap().containsKey(boundOperation)) {
                                this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4BindingInputOpMap().get(boundOperation), modelContext), this.getPolicyModels(), boundOperation, BuilderHandlerMessageScope.Scope.InputMessageScope, service.getName(), port.getName(), operationName, null));
                            }
                            if (null != input && this.getHandlers4InputMap().containsKey(input)) {
                                this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4InputMap().get(input), modelContext), this.getPolicyModels(), input, BuilderHandlerMessageScope.Scope.InputMessageScope, service.getName(), port.getName(), operationName, null));
                            }
                            final EditableWSDLOutput output = operation.getOutput();
                            if (null != output) {
                                final EditableWSDLMessage outputMsg = output.getMessage();
                                if (outputMsg != null && this.getHandlers4MessageMap().containsKey(outputMsg)) {
                                    messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4MessageMap().get(outputMsg), modelContext), this.getPolicyModels(), outputMsg, BuilderHandlerMessageScope.Scope.OutputMessageScope, service.getName(), port.getName(), operationName, null));
                                }
                            }
                            if (this.getHandlers4BindingOutputOpMap().containsKey(boundOperation)) {
                                this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4BindingOutputOpMap().get(boundOperation), modelContext), this.getPolicyModels(), boundOperation, BuilderHandlerMessageScope.Scope.OutputMessageScope, service.getName(), port.getName(), operationName, null));
                            }
                            if (null != output && this.getHandlers4OutputMap().containsKey(output)) {
                                this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4OutputMap().get(output), modelContext), this.getPolicyModels(), output, BuilderHandlerMessageScope.Scope.OutputMessageScope, service.getName(), port.getName(), operationName, null));
                            }
                            for (final EditableWSDLBoundFault boundFault : boundOperation.getFaults()) {
                                final EditableWSDLFault fault = boundFault.getFault();
                                if (fault == null) {
                                    PolicyWSDLParserExtension.LOGGER.warning(PolicyMessages.WSP_1021_FAULT_NOT_BOUND(boundFault.getName()));
                                }
                                else {
                                    final EditableWSDLMessage faultMessage = fault.getMessage();
                                    final QName faultName = new QName(boundOperation.getBoundPortType().getName().getNamespaceURI(), boundFault.getName());
                                    if (faultMessage != null && this.getHandlers4MessageMap().containsKey(faultMessage)) {
                                        messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4MessageMap().get(faultMessage), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(boundFault, boundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, service.getName(), port.getName(), operationName, faultName));
                                    }
                                    if (this.getHandlers4FaultMap().containsKey(fault)) {
                                        messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4FaultMap().get(fault), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(boundFault, boundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, service.getName(), port.getName(), operationName, faultName));
                                    }
                                    if (!this.getHandlers4BindingFaultOpMap().containsKey(boundFault)) {
                                        continue;
                                    }
                                    messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4BindingFaultOpMap().get(boundFault), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(boundFault, boundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, service.getName(), port.getName(), operationName, faultName));
                                }
                            }
                        }
                    }
                }
            }
            for (final BuilderHandlerMessageScope scopeHandler : messageSet) {
                this.getPolicyMapBuilder().registerHandler(scopeHandler);
            }
        }
        catch (final PolicyException e3) {
            PolicyWSDLParserExtension.LOGGER.logSevereException(e3);
        }
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    @Override
    public void postFinished(final WSDLParserExtensionContext context) {
        final EditableWSDLModel wsdlModel = context.getWSDLModel();
        PolicyMap effectiveMap;
        try {
            if (context.isClientSide()) {
                effectiveMap = context.getPolicyResolver().resolve(new PolicyResolver.ClientContext(this.policyBuilder.getPolicyMap(new PolicyMapMutator[0]), context.getContainer()));
            }
            else {
                effectiveMap = context.getPolicyResolver().resolve(new PolicyResolver.ServerContext(this.policyBuilder.getPolicyMap(new PolicyMapMutator[0]), context.getContainer(), null, new PolicyMapMutator[0]));
            }
            wsdlModel.setPolicyMap(effectiveMap);
        }
        catch (final PolicyException e) {
            PolicyWSDLParserExtension.LOGGER.logSevereException(e);
            throw PolicyWSDLParserExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL(), e));
        }
        try {
            PolicyUtil.configureModel(wsdlModel, effectiveMap);
        }
        catch (final PolicyException e) {
            PolicyWSDLParserExtension.LOGGER.logSevereException(e);
            throw PolicyWSDLParserExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1012_FAILED_CONFIGURE_WSDL_MODEL(), e));
        }
        PolicyWSDLParserExtension.LOGGER.exiting();
    }
    
    private String[] getPolicyURIsFromAttr(final XMLStreamReader reader) {
        final StringBuilder policyUriBuffer = new StringBuilder();
        for (final NamespaceVersion version : NamespaceVersion.values()) {
            final String value = reader.getAttributeValue(version.toString(), XmlToken.PolicyUris.toString());
            if (value != null) {
                policyUriBuffer.append(value).append(" ");
            }
        }
        return (String[])((policyUriBuffer.length() > 0) ? policyUriBuffer.toString().split("[\\n ]+") : null);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyWSDLParserExtension.class);
        AnonymnousPolicyIdPrefix = new StringBuffer("#__anonymousPolicy__ID");
    }
    
    enum HandlerType
    {
        PolicyUri, 
        AnonymousPolicyId;
    }
    
    static final class PolicyRecordHandler
    {
        String handler;
        HandlerType type;
        
        PolicyRecordHandler(final HandlerType type, final String handler) {
            this.type = type;
            this.handler = handler;
        }
        
        HandlerType getType() {
            return this.type;
        }
        
        String getHandler() {
            return this.handler;
        }
    }
}
