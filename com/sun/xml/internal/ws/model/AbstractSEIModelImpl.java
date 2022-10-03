package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.jws.WebParam;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import java.util.Collections;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.security.PrivilegedActionException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import java.security.AccessController;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
import java.util.logging.Level;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import java.util.Iterator;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.TypeReference;
import java.lang.reflect.Method;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.util.Pool;
import java.util.List;
import com.sun.xml.internal.ws.api.model.SEIModel;

public abstract class AbstractSEIModelImpl implements SEIModel
{
    private List<Class> additionalClasses;
    private Pool.Marshaller marshallers;
    @Deprecated
    protected JAXBRIContext jaxbContext;
    protected BindingContext bindingContext;
    private String wsdlLocation;
    private QName serviceName;
    private QName portName;
    private QName portTypeName;
    private Map<Method, JavaMethodImpl> methodToJM;
    private Map<QName, JavaMethodImpl> nameToJM;
    private Map<QName, JavaMethodImpl> wsdlOpToJM;
    private List<JavaMethodImpl> javaMethods;
    private final Map<TypeReference, Bridge> bridgeMap;
    private final Map<TypeInfo, XMLBridge> xmlBridgeMap;
    protected final QName emptyBodyName;
    private String targetNamespace;
    private List<String> knownNamespaceURIs;
    private WSDLPort port;
    private final WebServiceFeatureList features;
    private Databinding databinding;
    BindingID bindingId;
    protected Class contractClass;
    protected Class endpointClass;
    protected ClassLoader classLoader;
    protected WSBinding wsBinding;
    protected BindingInfo databindingInfo;
    protected String defaultSchemaNamespaceSuffix;
    private static final Logger LOGGER;
    
    protected AbstractSEIModelImpl(final WebServiceFeatureList features) {
        this.additionalClasses = new ArrayList<Class>();
        this.methodToJM = new HashMap<Method, JavaMethodImpl>();
        this.nameToJM = new HashMap<QName, JavaMethodImpl>();
        this.wsdlOpToJM = new HashMap<QName, JavaMethodImpl>();
        this.javaMethods = new ArrayList<JavaMethodImpl>();
        this.bridgeMap = new HashMap<TypeReference, Bridge>();
        this.xmlBridgeMap = new HashMap<TypeInfo, XMLBridge>();
        this.emptyBodyName = new QName("");
        this.targetNamespace = "";
        this.knownNamespaceURIs = null;
        this.classLoader = null;
        this.features = features;
        (this.databindingInfo = new BindingInfo()).setSEIModel(this);
    }
    
    void postProcess() {
        if (this.jaxbContext != null) {
            return;
        }
        this.populateMaps();
        this.createJAXBContext();
    }
    
    public void freeze(final WSDLPort port) {
        this.port = port;
        for (final JavaMethodImpl m : this.javaMethods) {
            m.freeze(port);
            this.putOp(m.getOperationQName(), m);
        }
        if (this.databinding != null) {
            ((DatabindingImpl)this.databinding).freeze(port);
        }
    }
    
    protected abstract void populateMaps();
    
    @Override
    public Pool.Marshaller getMarshallerPool() {
        return this.marshallers;
    }
    
    @Override
    @Deprecated
    public JAXBContext getJAXBContext() {
        final JAXBContext jc = this.bindingContext.getJAXBContext();
        if (jc != null) {
            return jc;
        }
        return this.jaxbContext;
    }
    
    public BindingContext getBindingContext() {
        return this.bindingContext;
    }
    
    public List<String> getKnownNamespaceURIs() {
        return this.knownNamespaceURIs;
    }
    
    @Deprecated
    public final Bridge getBridge(final TypeReference type) {
        final Bridge b = this.bridgeMap.get(type);
        assert b != null;
        return b;
    }
    
    public final XMLBridge getXMLBridge(final TypeInfo type) {
        final XMLBridge b = this.xmlBridgeMap.get(type);
        assert b != null;
        return b;
    }
    
    private void createJAXBContext() {
        final List<TypeInfo> types = this.getAllTypeInfos();
        final List<Class> cls = new ArrayList<Class>(types.size() + this.additionalClasses.size());
        cls.addAll(this.additionalClasses);
        for (final TypeInfo type : types) {
            cls.add((Class)type.type);
        }
        try {
            this.bindingContext = AccessController.doPrivileged((PrivilegedExceptionAction<BindingContext>)new PrivilegedExceptionAction<BindingContext>() {
                @Override
                public BindingContext run() throws Exception {
                    if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINEST)) {
                        AbstractSEIModelImpl.LOGGER.log(Level.FINEST, "Creating JAXBContext with classes={0} and types={1}", new Object[] { cls, types });
                    }
                    final UsesJAXBContextFeature f = AbstractSEIModelImpl.this.features.get(UsesJAXBContextFeature.class);
                    final DatabindingModeFeature dmf = AbstractSEIModelImpl.this.features.get(DatabindingModeFeature.class);
                    JAXBContextFactory factory = (f != null) ? f.getFactory() : null;
                    if (factory == null) {
                        factory = JAXBContextFactory.DEFAULT;
                    }
                    AbstractSEIModelImpl.this.databindingInfo.properties().put(JAXBContextFactory.class.getName(), factory);
                    if (dmf != null) {
                        if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINE)) {
                            AbstractSEIModelImpl.LOGGER.log(Level.FINE, "DatabindingModeFeature in SEI specifies mode: {0}", dmf.getMode());
                        }
                        AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode(dmf.getMode());
                    }
                    if (f != null) {
                        AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode("glassfish.jaxb");
                    }
                    AbstractSEIModelImpl.this.databindingInfo.setClassLoader(AbstractSEIModelImpl.this.classLoader);
                    AbstractSEIModelImpl.this.databindingInfo.contentClasses().addAll(cls);
                    AbstractSEIModelImpl.this.databindingInfo.typeInfos().addAll(types);
                    AbstractSEIModelImpl.this.databindingInfo.properties().put("c14nSupport", Boolean.FALSE);
                    AbstractSEIModelImpl.this.databindingInfo.setDefaultNamespace(AbstractSEIModelImpl.this.getDefaultSchemaNamespace());
                    final BindingContext bc = BindingContextFactory.create(AbstractSEIModelImpl.this.databindingInfo);
                    if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINE)) {
                        AbstractSEIModelImpl.LOGGER.log(Level.FINE, "Created binding context: " + bc.getClass().getName());
                    }
                    return bc;
                }
            });
            this.createBondMap(types);
        }
        catch (final PrivilegedActionException e) {
            throw new WebServiceException(ModelerMessages.UNABLE_TO_CREATE_JAXB_CONTEXT(), e);
        }
        this.knownNamespaceURIs = new ArrayList<String>();
        for (final String namespace : this.bindingContext.getKnownNamespaceURIs()) {
            if (namespace.length() > 0 && !namespace.equals("http://www.w3.org/2001/XMLSchema") && !namespace.equals("http://www.w3.org/XML/1998/namespace")) {
                this.knownNamespaceURIs.add(namespace);
            }
        }
        this.marshallers = new Pool.Marshaller(this.jaxbContext);
    }
    
    private List<TypeInfo> getAllTypeInfos() {
        final List<TypeInfo> types = new ArrayList<TypeInfo>();
        final Collection<JavaMethodImpl> methods = this.methodToJM.values();
        for (final JavaMethodImpl m : methods) {
            m.fillTypes(types);
        }
        return types;
    }
    
    private void createBridgeMap(final List<TypeReference> types) {
        for (final TypeReference type : types) {
            final Bridge bridge = this.jaxbContext.createBridge(type);
            this.bridgeMap.put(type, bridge);
        }
    }
    
    private void createBondMap(final List<TypeInfo> types) {
        for (final TypeInfo type : types) {
            final XMLBridge binding = this.bindingContext.createBridge(type);
            this.xmlBridgeMap.put(type, binding);
        }
    }
    
    public boolean isKnownFault(final QName name, final Method method) {
        final JavaMethodImpl m = this.getJavaMethod(method);
        for (final CheckedExceptionImpl ce : m.getCheckedExceptions()) {
            if (ce.getDetailType().tagName.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCheckedException(final Method m, final Class ex) {
        final JavaMethodImpl jm = this.getJavaMethod(m);
        for (final CheckedExceptionImpl ce : jm.getCheckedExceptions()) {
            if (ce.getExceptionClass().equals(ex)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public JavaMethodImpl getJavaMethod(final Method method) {
        return this.methodToJM.get(method);
    }
    
    @Override
    public JavaMethodImpl getJavaMethod(final QName name) {
        return this.nameToJM.get(name);
    }
    
    @Override
    public JavaMethod getJavaMethodForWsdlOperation(final QName operationName) {
        return this.wsdlOpToJM.get(operationName);
    }
    
    @Deprecated
    public QName getQNameForJM(final JavaMethodImpl jm) {
        for (final QName key : this.nameToJM.keySet()) {
            final JavaMethodImpl jmethod = this.nameToJM.get(key);
            if (jmethod.getOperationName().equals(jm.getOperationName())) {
                return key;
            }
        }
        return null;
    }
    
    @Override
    public final Collection<JavaMethodImpl> getJavaMethods() {
        return (Collection<JavaMethodImpl>)Collections.unmodifiableList((List<?>)this.javaMethods);
    }
    
    void addJavaMethod(final JavaMethodImpl jm) {
        if (jm != null) {
            this.javaMethods.add(jm);
        }
    }
    
    private List<ParameterImpl> applyRpcLitParamBinding(final JavaMethodImpl method, final WrapperParameter wrapperParameter, final WSDLBoundPortType boundPortType, final WebParam.Mode mode) {
        final QName opName = new QName(boundPortType.getPortTypeName().getNamespaceURI(), method.getOperationName());
        final WSDLBoundOperation bo = boundPortType.get(opName);
        final Map<Integer, ParameterImpl> bodyParams = new HashMap<Integer, ParameterImpl>();
        final List<ParameterImpl> unboundParams = new ArrayList<ParameterImpl>();
        final List<ParameterImpl> attachParams = new ArrayList<ParameterImpl>();
        for (final ParameterImpl param : wrapperParameter.wrapperChildren) {
            final String partName = param.getPartName();
            if (partName == null) {
                continue;
            }
            final ParameterBinding paramBinding = boundPortType.getBinding(opName, partName, mode);
            if (paramBinding == null) {
                continue;
            }
            if (mode == WebParam.Mode.IN) {
                param.setInBinding(paramBinding);
            }
            else if (mode == WebParam.Mode.OUT || mode == WebParam.Mode.INOUT) {
                param.setOutBinding(paramBinding);
            }
            if (paramBinding.isUnbound()) {
                unboundParams.add(param);
            }
            else if (paramBinding.isAttachment()) {
                attachParams.add(param);
            }
            else {
                if (!paramBinding.isBody()) {
                    continue;
                }
                if (bo != null) {
                    final WSDLPart p = bo.getPart(param.getPartName(), mode);
                    if (p != null) {
                        bodyParams.put(p.getIndex(), param);
                    }
                    else {
                        bodyParams.put(bodyParams.size(), param);
                    }
                }
                else {
                    bodyParams.put(bodyParams.size(), param);
                }
            }
        }
        wrapperParameter.clear();
        for (int i = 0; i < bodyParams.size(); ++i) {
            final ParameterImpl p2 = bodyParams.get(i);
            wrapperParameter.addWrapperChild(p2);
        }
        final Iterator<ParameterImpl> iterator2 = unboundParams.iterator();
        while (iterator2.hasNext()) {
            final ParameterImpl p2 = iterator2.next();
            wrapperParameter.addWrapperChild(p2);
        }
        return attachParams;
    }
    
    void put(final QName name, final JavaMethodImpl jm) {
        this.nameToJM.put(name, jm);
    }
    
    void put(final Method method, final JavaMethodImpl jm) {
        this.methodToJM.put(method, jm);
    }
    
    void putOp(final QName opName, final JavaMethodImpl jm) {
        this.wsdlOpToJM.put(opName, jm);
    }
    
    @Override
    public String getWSDLLocation() {
        return this.wsdlLocation;
    }
    
    void setWSDLLocation(final String location) {
        this.wsdlLocation = location;
    }
    
    @Override
    public QName getServiceQName() {
        return this.serviceName;
    }
    
    @Override
    public WSDLPort getPort() {
        return this.port;
    }
    
    @Override
    public QName getPortName() {
        return this.portName;
    }
    
    @Override
    public QName getPortTypeName() {
        return this.portTypeName;
    }
    
    void setServiceQName(final QName name) {
        this.serviceName = name;
    }
    
    void setPortName(final QName name) {
        this.portName = name;
    }
    
    void setPortTypeName(final QName name) {
        this.portTypeName = name;
    }
    
    void setTargetNamespace(final String namespace) {
        this.targetNamespace = namespace;
    }
    
    @Override
    public String getTargetNamespace() {
        return this.targetNamespace;
    }
    
    String getDefaultSchemaNamespace() {
        String defaultNamespace = this.getTargetNamespace();
        if (this.defaultSchemaNamespaceSuffix == null) {
            return defaultNamespace;
        }
        if (!defaultNamespace.endsWith("/")) {
            defaultNamespace += "/";
        }
        return defaultNamespace + this.defaultSchemaNamespaceSuffix;
    }
    
    @NotNull
    @Override
    public QName getBoundPortTypeName() {
        assert this.portName != null;
        return new QName(this.portName.getNamespaceURI(), this.portName.getLocalPart() + "Binding");
    }
    
    public void addAdditionalClasses(final Class... additionalClasses) {
        for (final Class cls : additionalClasses) {
            this.additionalClasses.add(cls);
        }
    }
    
    public Databinding getDatabinding() {
        return this.databinding;
    }
    
    public void setDatabinding(final Databinding wsRuntime) {
        this.databinding = wsRuntime;
    }
    
    public WSBinding getWSBinding() {
        return this.wsBinding;
    }
    
    public Class getContractClass() {
        return this.contractClass;
    }
    
    public Class getEndpointClass() {
        return this.endpointClass;
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractSEIModelImpl.class.getName());
    }
}
