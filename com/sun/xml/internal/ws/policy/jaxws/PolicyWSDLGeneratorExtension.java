package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.encoding.policy.MtomPolicyMapConfigurator;
import com.sun.xml.internal.ws.addressing.policy.AddressingPolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.Policy;
import java.util.Iterator;
import java.util.Set;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;
import java.util.HashSet;
import com.sun.xml.internal.ws.api.policy.ModelGenerator;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.PolicyException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.policy.PolicyMapUtil;
import java.util.Arrays;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.PolicyMerger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.internal.ws.policy.PolicySubject;
import java.util.Collection;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

public class PolicyWSDLGeneratorExtension extends WSDLGeneratorExtension
{
    private static final PolicyLogger LOGGER;
    private PolicyMap policyMap;
    private SEIModel seiModel;
    private final Collection<PolicySubject> subjects;
    private final PolicyModelMarshaller marshaller;
    private final PolicyMerger merger;
    
    public PolicyWSDLGeneratorExtension() {
        this.subjects = new LinkedList<PolicySubject>();
        this.marshaller = PolicyModelMarshaller.getXmlMarshaller(true);
        this.merger = PolicyMerger.getMerger();
    }
    
    @Override
    public void start(final WSDLGenExtnContext context) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        try {
            this.seiModel = context.getModel();
            final PolicyMapConfigurator[] policyMapConfigurators = this.loadConfigurators();
            final PolicyMapExtender[] extenders = new PolicyMapExtender[policyMapConfigurators.length];
            for (int i = 0; i < policyMapConfigurators.length; ++i) {
                extenders[i] = PolicyMapExtender.createPolicyMapExtender();
            }
            this.policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(this.policyMap, context.getContainer(), context.getEndpointClass(), false, (PolicyMapMutator[])extenders));
            if (this.policyMap == null) {
                PolicyWSDLGeneratorExtension.LOGGER.fine(PolicyMessages.WSP_1019_CREATE_EMPTY_POLICY_MAP());
                this.policyMap = PolicyMap.createPolicyMap(Arrays.asList(extenders));
            }
            final WSBinding binding = context.getBinding();
            try {
                final Collection<PolicySubject> policySubjects = new LinkedList<PolicySubject>();
                for (int j = 0; j < policyMapConfigurators.length; ++j) {
                    policySubjects.addAll(policyMapConfigurators[j].update(this.policyMap, this.seiModel, binding));
                    extenders[j].disconnect();
                }
                PolicyMapUtil.insertPolicies(this.policyMap, policySubjects, this.seiModel.getServiceQName(), this.seiModel.getPortName());
            }
            catch (final PolicyException e) {
                throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1017_MAP_UPDATE_FAILED(), e));
            }
            final TypedXmlWriter root = context.getRoot();
            root._namespace(NamespaceVersion.v1_2.toString(), NamespaceVersion.v1_2.getDefaultNamespacePrefix());
            root._namespace(NamespaceVersion.v1_5.toString(), NamespaceVersion.v1_5.getDefaultNamespacePrefix());
            root._namespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
        }
        finally {
            PolicyWSDLGeneratorExtension.LOGGER.exiting();
        }
    }
    
    @Override
    public void addDefinitionsExtension(final TypedXmlWriter definitions) {
        try {
            PolicyWSDLGeneratorExtension.LOGGER.entering();
            if (this.policyMap == null) {
                PolicyWSDLGeneratorExtension.LOGGER.fine(PolicyMessages.WSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL());
            }
            else {
                this.subjects.addAll(this.policyMap.getPolicySubjects());
                final PolicyModelGenerator generator = ModelGenerator.getGenerator();
                final Set<String> policyIDsOrNamesWritten = new HashSet<String>();
                for (final PolicySubject subject : this.subjects) {
                    if (subject.getSubject() == null) {
                        PolicyWSDLGeneratorExtension.LOGGER.fine(PolicyMessages.WSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(subject));
                    }
                    else {
                        Policy policy;
                        try {
                            policy = subject.getEffectivePolicy(this.merger);
                        }
                        catch (final PolicyException e) {
                            throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(subject.toString()), e));
                        }
                        if (null == policy.getIdOrName() || policyIDsOrNamesWritten.contains(policy.getIdOrName())) {
                            PolicyWSDLGeneratorExtension.LOGGER.fine(PolicyMessages.WSP_1016_POLICY_ID_NULL_OR_DUPLICATE(policy));
                        }
                        else {
                            try {
                                final PolicySourceModel policyInfoset = generator.translate(policy);
                                this.marshaller.marshal(policyInfoset, definitions);
                            }
                            catch (final PolicyException e) {
                                throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1018_FAILED_TO_MARSHALL_POLICY(policy.getIdOrName()), e));
                            }
                            policyIDsOrNamesWritten.add(policy.getIdOrName());
                        }
                    }
                }
            }
        }
        finally {
            PolicyWSDLGeneratorExtension.LOGGER.exiting();
        }
    }
    
    @Override
    public void addServiceExtension(final TypedXmlWriter service) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String serviceName = (null == this.seiModel) ? null : this.seiModel.getServiceQName().getLocalPart();
        this.selectAndProcessSubject(service, WSDLService.class, ScopeType.SERVICE, serviceName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addPortExtension(final TypedXmlWriter port) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String portName = (null == this.seiModel) ? null : this.seiModel.getPortName().getLocalPart();
        this.selectAndProcessSubject(port, WSDLPort.class, ScopeType.ENDPOINT, portName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addPortTypeExtension(final TypedXmlWriter portType) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String portTypeName = (null == this.seiModel) ? null : this.seiModel.getPortTypeName().getLocalPart();
        this.selectAndProcessSubject(portType, WSDLPortType.class, ScopeType.ENDPOINT, portTypeName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addBindingExtension(final TypedXmlWriter binding) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final QName bindingName = (null == this.seiModel) ? null : this.seiModel.getBoundPortTypeName();
        this.selectAndProcessBindingSubject(binding, WSDLBoundPortType.class, ScopeType.ENDPOINT, bindingName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addOperationExtension(final TypedXmlWriter operation, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        this.selectAndProcessSubject(operation, WSDLOperation.class, ScopeType.OPERATION, (String)null);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addBindingOperationExtension(final TypedXmlWriter operation, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final QName operationName = (method == null) ? null : new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
        this.selectAndProcessBindingSubject(operation, WSDLBoundOperation.class, ScopeType.OPERATION, operationName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addInputMessageExtension(final TypedXmlWriter message, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String messageName = (null == method) ? null : method.getRequestMessageName();
        this.selectAndProcessSubject(message, WSDLMessage.class, ScopeType.INPUT_MESSAGE, messageName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addOutputMessageExtension(final TypedXmlWriter message, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String messageName = (null == method) ? null : method.getResponseMessageName();
        this.selectAndProcessSubject(message, WSDLMessage.class, ScopeType.OUTPUT_MESSAGE, messageName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addFaultMessageExtension(final TypedXmlWriter message, final JavaMethod method, final CheckedException exception) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String messageName = (null == exception) ? null : exception.getMessageName();
        this.selectAndProcessSubject(message, WSDLMessage.class, ScopeType.FAULT_MESSAGE, messageName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String messageName = (null == method) ? null : method.getRequestMessageName();
        this.selectAndProcessSubject(input, WSDLInput.class, ScopeType.INPUT_MESSAGE, messageName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String messageName = (null == method) ? null : method.getResponseMessageName();
        this.selectAndProcessSubject(output, WSDLOutput.class, ScopeType.OUTPUT_MESSAGE, messageName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addOperationFaultExtension(final TypedXmlWriter fault, final JavaMethod method, final CheckedException exception) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final String messageName = (null == exception) ? null : exception.getMessageName();
        this.selectAndProcessSubject(fault, WSDLFault.class, ScopeType.FAULT_MESSAGE, messageName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addBindingOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final QName operationName = new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
        this.selectAndProcessBindingSubject(input, WSDLBoundOperation.class, ScopeType.INPUT_MESSAGE, operationName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addBindingOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
        PolicyWSDLGeneratorExtension.LOGGER.entering();
        final QName operationName = new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
        this.selectAndProcessBindingSubject(output, WSDLBoundOperation.class, ScopeType.OUTPUT_MESSAGE, operationName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    @Override
    public void addBindingOperationFaultExtension(final TypedXmlWriter writer, final JavaMethod method, final CheckedException exception) {
        PolicyWSDLGeneratorExtension.LOGGER.entering(writer, method, exception);
        if (this.subjects != null) {
            for (final PolicySubject subject : this.subjects) {
                if (this.policyMap.isFaultMessageSubject(subject)) {
                    final Object concreteSubject = subject.getSubject();
                    if (concreteSubject == null) {
                        continue;
                    }
                    final String exceptionName = (exception == null) ? null : exception.getMessageName();
                    if (exceptionName == null) {
                        this.writePolicyOrReferenceIt(subject, writer);
                    }
                    if (WSDLBoundFaultContainer.class.isInstance(concreteSubject)) {
                        final WSDLBoundFaultContainer faultContainer = (WSDLBoundFaultContainer)concreteSubject;
                        final WSDLBoundFault fault = faultContainer.getBoundFault();
                        final WSDLBoundOperation operation = faultContainer.getBoundOperation();
                        if (!exceptionName.equals(fault.getName()) || !operation.getName().getLocalPart().equals(method.getOperationName())) {
                            continue;
                        }
                        this.writePolicyOrReferenceIt(subject, writer);
                    }
                    else {
                        if (!WsdlBindingSubject.class.isInstance(concreteSubject)) {
                            continue;
                        }
                        final WsdlBindingSubject wsdlSubject = (WsdlBindingSubject)concreteSubject;
                        if (wsdlSubject.getMessageType() != WsdlBindingSubject.WsdlMessageType.FAULT || !exception.getOwner().getTargetNamespace().equals(wsdlSubject.getName().getNamespaceURI()) || !exceptionName.equals(wsdlSubject.getName().getLocalPart())) {
                            continue;
                        }
                        this.writePolicyOrReferenceIt(subject, writer);
                    }
                }
            }
        }
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    private void selectAndProcessSubject(final TypedXmlWriter xmlWriter, final Class clazz, final ScopeType scopeType, final QName bindingName) {
        PolicyWSDLGeneratorExtension.LOGGER.entering(xmlWriter, clazz, scopeType, bindingName);
        if (bindingName == null) {
            this.selectAndProcessSubject(xmlWriter, clazz, scopeType, (String)null);
        }
        else {
            if (this.subjects != null) {
                for (final PolicySubject subject : this.subjects) {
                    if (bindingName.equals(subject.getSubject())) {
                        this.writePolicyOrReferenceIt(subject, xmlWriter);
                    }
                }
            }
            this.selectAndProcessSubject(xmlWriter, clazz, scopeType, bindingName.getLocalPart());
        }
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    private void selectAndProcessBindingSubject(final TypedXmlWriter xmlWriter, final Class clazz, final ScopeType scopeType, final QName bindingName) {
        PolicyWSDLGeneratorExtension.LOGGER.entering(xmlWriter, clazz, scopeType, bindingName);
        if (this.subjects != null && bindingName != null) {
            for (final PolicySubject subject : this.subjects) {
                if (subject.getSubject() instanceof WsdlBindingSubject) {
                    final WsdlBindingSubject wsdlSubject = (WsdlBindingSubject)subject.getSubject();
                    if (!bindingName.equals(wsdlSubject.getName())) {
                        continue;
                    }
                    this.writePolicyOrReferenceIt(subject, xmlWriter);
                }
            }
        }
        this.selectAndProcessSubject(xmlWriter, clazz, scopeType, bindingName);
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    private void selectAndProcessSubject(final TypedXmlWriter xmlWriter, final Class clazz, final ScopeType scopeType, final String wsdlName) {
        PolicyWSDLGeneratorExtension.LOGGER.entering(xmlWriter, clazz, scopeType, wsdlName);
        if (this.subjects != null) {
            for (final PolicySubject subject : this.subjects) {
                if (isCorrectType(this.policyMap, subject, scopeType)) {
                    final Object concreteSubject = subject.getSubject();
                    if (concreteSubject == null || !clazz.isInstance(concreteSubject)) {
                        continue;
                    }
                    if (null == wsdlName) {
                        this.writePolicyOrReferenceIt(subject, xmlWriter);
                    }
                    else {
                        try {
                            final Method getNameMethod = clazz.getDeclaredMethod("getName", (Class[])new Class[0]);
                            if (!this.stringEqualsToStringOrQName(wsdlName, getNameMethod.invoke(concreteSubject, new Object[0]))) {
                                continue;
                            }
                            this.writePolicyOrReferenceIt(subject, xmlWriter);
                        }
                        catch (final NoSuchMethodException e) {
                            throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), e));
                        }
                        catch (final IllegalAccessException e2) {
                            throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), e2));
                        }
                        catch (final InvocationTargetException e3) {
                            throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), e3));
                        }
                    }
                }
            }
        }
        PolicyWSDLGeneratorExtension.LOGGER.exiting();
    }
    
    private static boolean isCorrectType(final PolicyMap map, final PolicySubject subject, final ScopeType type) {
        switch (type) {
            case OPERATION: {
                return !map.isInputMessageSubject(subject) && !map.isOutputMessageSubject(subject) && !map.isFaultMessageSubject(subject);
            }
            case INPUT_MESSAGE: {
                return map.isInputMessageSubject(subject);
            }
            case OUTPUT_MESSAGE: {
                return map.isOutputMessageSubject(subject);
            }
            case FAULT_MESSAGE: {
                return map.isFaultMessageSubject(subject);
            }
            default: {
                return true;
            }
        }
    }
    
    private boolean stringEqualsToStringOrQName(final String first, final Object second) {
        return (second instanceof QName) ? first.equals(((QName)second).getLocalPart()) : first.equals(second);
    }
    
    private void writePolicyOrReferenceIt(final PolicySubject subject, final TypedXmlWriter writer) {
        Policy policy;
        try {
            policy = subject.getEffectivePolicy(this.merger);
        }
        catch (final PolicyException e) {
            throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(subject.toString()), e));
        }
        if (policy != null) {
            if (null == policy.getIdOrName()) {
                final PolicyModelGenerator generator = ModelGenerator.getGenerator();
                try {
                    final PolicySourceModel policyInfoset = generator.translate(policy);
                    this.marshaller.marshal(policyInfoset, writer);
                }
                catch (final PolicyException pe) {
                    throw PolicyWSDLGeneratorExtension.LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE(), pe));
                }
            }
            else {
                final TypedXmlWriter policyReference = writer._element(policy.getNamespaceVersion().asQName(XmlToken.PolicyReference), TypedXmlWriter.class);
                policyReference._attribute(XmlToken.Uri.toString(), '#' + policy.getIdOrName());
            }
        }
    }
    
    private PolicyMapConfigurator[] loadConfigurators() {
        final Collection<PolicyMapConfigurator> configurators = new LinkedList<PolicyMapConfigurator>();
        configurators.add(new AddressingPolicyMapConfigurator());
        configurators.add(new MtomPolicyMapConfigurator());
        PolicyUtil.addServiceProviders(configurators, PolicyMapConfigurator.class);
        return configurators.toArray(new PolicyMapConfigurator[configurators.size()]);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyWSDLGeneratorExtension.class);
    }
    
    enum ScopeType
    {
        SERVICE, 
        ENDPOINT, 
        OPERATION, 
        INPUT_MESSAGE, 
        OUTPUT_MESSAGE, 
        FAULT_MESSAGE;
    }
}
