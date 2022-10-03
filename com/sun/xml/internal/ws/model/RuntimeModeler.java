package com.sun.xml.internal.ws.model;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import javax.xml.ws.WebFault;
import javax.xml.ws.FaultAction;
import javax.xml.ws.Action;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import java.util.TreeMap;
import java.lang.reflect.ParameterizedType;
import javax.xml.ws.Holder;
import javax.xml.ws.AsyncHandler;
import javax.xml.bind.annotation.XmlElement;
import javax.jws.WebResult;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import javax.jws.WebParam;
import java.lang.reflect.Type;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.RequestWrapper;
import java.util.concurrent.Future;
import javax.xml.ws.Response;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.MEP;
import javax.jws.Oneway;
import java.util.StringTokenizer;
import java.lang.reflect.Modifier;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.HashSet;
import javax.jws.WebMethod;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.oracle.webservices.internal.api.databinding.DatabindingMode;
import com.sun.xml.internal.ws.resources.ServerMessages;
import javax.jws.soap.SOAPBinding;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import javax.jws.WebService;
import java.util.Map;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.ws.BindingType;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import java.lang.annotation.Annotation;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.WebServiceFeature;
import com.sun.istack.internal.NotNull;
import java.util.logging.Logger;
import java.rmi.RemoteException;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import java.util.Set;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;

public class RuntimeModeler
{
    private final WebServiceFeatureList features;
    private BindingID bindingId;
    private WSBinding wsBinding;
    private final Class portClass;
    private AbstractSEIModelImpl model;
    private SOAPBindingImpl defaultBinding;
    private String packageName;
    private String targetNamespace;
    private boolean isWrapped;
    private ClassLoader classLoader;
    private final WSDLPort binding;
    private QName serviceName;
    private QName portName;
    private Set<Class> classUsesWebMethod;
    private DatabindingConfig config;
    private MetadataReader metadataReader;
    public static final String PD_JAXWS_PACKAGE_PD = ".jaxws.";
    public static final String JAXWS_PACKAGE_PD = "jaxws.";
    public static final String RESPONSE = "Response";
    public static final String RETURN = "return";
    public static final String BEAN = "Bean";
    public static final String SERVICE = "Service";
    public static final String PORT = "Port";
    public static final Class HOLDER_CLASS;
    public static final Class<RemoteException> REMOTE_EXCEPTION_CLASS;
    public static final Class<RuntimeException> RUNTIME_EXCEPTION_CLASS;
    public static final Class<Exception> EXCEPTION_CLASS;
    public static final String DecapitalizeExceptionBeanProperties = "com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties";
    public static final String SuppressDocLitWrapperGeneration = "com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration";
    public static final String DocWrappeeNamespapceQualified = "com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified";
    private static final Logger logger;
    
    public RuntimeModeler(@NotNull final DatabindingConfig config) {
        this.isWrapped = true;
        this.portClass = ((config.getEndpointClass() != null) ? config.getEndpointClass() : config.getContractClass());
        this.serviceName = config.getMappingInfo().getServiceName();
        this.binding = config.getWsdlPort();
        this.classLoader = config.getClassLoader();
        this.portName = config.getMappingInfo().getPortName();
        this.config = config;
        this.wsBinding = config.getWSBinding();
        this.metadataReader = config.getMetadataReader();
        this.targetNamespace = config.getMappingInfo().getTargetNamespace();
        if (this.metadataReader == null) {
            this.metadataReader = new ReflectAnnotationReader();
        }
        if (this.wsBinding != null) {
            this.bindingId = this.wsBinding.getBindingId();
            if (config.getFeatures() != null) {
                this.wsBinding.getFeatures().mergeFeatures(config.getFeatures(), false);
            }
            if (this.binding != null) {
                this.wsBinding.getFeatures().mergeFeatures(this.binding.getFeatures(), false);
            }
            this.features = WebServiceFeatureList.toList(this.wsBinding.getFeatures());
        }
        else {
            this.bindingId = config.getMappingInfo().getBindingID();
            this.features = WebServiceFeatureList.toList(config.getFeatures());
            if (this.binding != null) {
                this.bindingId = this.binding.getBinding().getBindingId();
            }
            if (this.bindingId == null) {
                this.bindingId = this.getDefaultBindingID();
            }
            if (!this.features.contains(MTOMFeature.class)) {
                final MTOM mtomAn = this.getAnnotation(this.portClass, MTOM.class);
                if (mtomAn != null) {
                    this.features.add(WebServiceFeatureList.getFeature(mtomAn));
                }
            }
            if (!this.features.contains(EnvelopeStyleFeature.class)) {
                final EnvelopeStyle es = this.getAnnotation(this.portClass, EnvelopeStyle.class);
                if (es != null) {
                    this.features.add(WebServiceFeatureList.getFeature(es));
                }
            }
            this.wsBinding = this.bindingId.createBinding(this.features);
        }
    }
    
    private BindingID getDefaultBindingID() {
        final BindingType bt = this.getAnnotation(this.portClass, BindingType.class);
        if (bt != null) {
            return BindingID.parse(bt.value());
        }
        final SOAPVersion ver = WebServiceFeatureList.getSoapVersion(this.features);
        final boolean mtomEnabled = this.features.isEnabled(MTOMFeature.class);
        if (SOAPVersion.SOAP_12.equals(ver)) {
            return mtomEnabled ? BindingID.SOAP12_HTTP_MTOM : BindingID.SOAP12_HTTP;
        }
        return mtomEnabled ? BindingID.SOAP11_HTTP_MTOM : BindingID.SOAP11_HTTP;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public void setPortName(final QName portName) {
        this.portName = portName;
    }
    
    private <T extends Annotation> T getAnnotation(final Class<?> clazz, final Class<T> T) {
        return this.metadataReader.getAnnotation(T, clazz);
    }
    
    private <T extends Annotation> T getAnnotation(final Method method, final Class<T> T) {
        return this.metadataReader.getAnnotation(T, method);
    }
    
    private Annotation[] getAnnotations(final Method method) {
        return this.metadataReader.getAnnotations(method);
    }
    
    private Annotation[] getAnnotations(final Class<?> c) {
        return this.metadataReader.getAnnotations(c);
    }
    
    private Annotation[][] getParamAnnotations(final Method method) {
        return this.metadataReader.getParameterAnnotations(method);
    }
    
    public AbstractSEIModelImpl buildRuntimeModel() {
        this.model = new SOAPSEIModel(this.features);
        this.model.contractClass = this.config.getContractClass();
        this.model.endpointClass = this.config.getEndpointClass();
        this.model.classLoader = this.classLoader;
        this.model.wsBinding = this.wsBinding;
        this.model.databindingInfo.setWsdlURL(this.config.getWsdlURL());
        this.model.databindingInfo.properties().putAll(this.config.properties());
        if (this.model.contractClass == null) {
            this.model.contractClass = this.portClass;
        }
        if (this.model.endpointClass == null && !this.portClass.isInterface()) {
            this.model.endpointClass = this.portClass;
        }
        Class<?> seiClass = this.portClass;
        this.metadataReader.getProperties(this.model.databindingInfo.properties(), this.portClass);
        final WebService webService = this.getAnnotation(this.portClass, WebService.class);
        if (webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { this.portClass.getCanonicalName() });
        }
        final Class<?> seiFromConfig = this.configEndpointInterface();
        if (webService.endpointInterface().length() > 0 || seiFromConfig != null) {
            if (seiFromConfig != null) {
                seiClass = seiFromConfig;
            }
            else {
                seiClass = this.getClass(webService.endpointInterface(), ModelerMessages.localizableRUNTIME_MODELER_CLASS_NOT_FOUND(webService.endpointInterface()));
            }
            this.model.contractClass = seiClass;
            this.model.endpointClass = this.portClass;
            final WebService seiService = this.getAnnotation(seiClass, WebService.class);
            if (seiService == null) {
                throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() });
            }
            final SOAPBinding sbPortClass = this.getAnnotation(this.portClass, SOAPBinding.class);
            final SOAPBinding sbSei = this.getAnnotation(seiClass, SOAPBinding.class);
            if (sbPortClass != null && (sbSei == null || sbSei.style() != sbPortClass.style() || sbSei.use() != sbPortClass.use())) {
                RuntimeModeler.logger.warning(ServerMessages.RUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL("@SOAPBinding", this.portClass.getName(), seiClass.getName()));
            }
        }
        if (this.serviceName == null) {
            this.serviceName = getServiceName(this.portClass, this.metadataReader);
        }
        this.model.setServiceQName(this.serviceName);
        if (this.portName == null) {
            this.portName = getPortName(this.portClass, this.metadataReader, this.serviceName.getNamespaceURI());
        }
        this.model.setPortName(this.portName);
        final DatabindingMode dbm2 = this.getAnnotation(this.portClass, DatabindingMode.class);
        if (dbm2 != null) {
            this.model.databindingInfo.setDatabindingMode(dbm2.value());
        }
        this.processClass(seiClass);
        if (this.model.getJavaMethods().size() == 0) {
            throw new RuntimeModelerException("runtime.modeler.no.operations", new Object[] { this.portClass.getName() });
        }
        this.model.postProcess();
        this.config.properties().put(BindingContext.class.getName(), this.model.bindingContext);
        if (this.binding != null) {
            this.model.freeze(this.binding);
        }
        return this.model;
    }
    
    private Class configEndpointInterface() {
        if (this.config.getEndpointClass() == null || this.config.getEndpointClass().isInterface()) {
            return null;
        }
        return this.config.getContractClass();
    }
    
    private Class getClass(final String className, final Localizable errorMessage) {
        try {
            if (this.classLoader == null) {
                return Thread.currentThread().getContextClassLoader().loadClass(className);
            }
            return this.classLoader.loadClass(className);
        }
        catch (final ClassNotFoundException e) {
            throw new RuntimeModelerException(errorMessage);
        }
    }
    
    private boolean noWrapperGen() {
        final Object o = this.config.properties().get("com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration");
        return o != null && o instanceof Boolean && (boolean)o;
    }
    
    private Class getRequestWrapperClass(final String className, final Method method, final QName reqElemName) {
        final ClassLoader loader = (this.classLoader == null) ? Thread.currentThread().getContextClassLoader() : this.classLoader;
        try {
            return loader.loadClass(className);
        }
        catch (final ClassNotFoundException e) {
            if (this.noWrapperGen()) {
                return WrapperComposite.class;
            }
            RuntimeModeler.logger.fine("Dynamically creating request wrapper Class " + className);
            return WrapperBeanGenerator.createRequestWrapperBean(className, method, reqElemName, loader);
        }
    }
    
    private Class getResponseWrapperClass(final String className, final Method method, final QName resElemName) {
        final ClassLoader loader = (this.classLoader == null) ? Thread.currentThread().getContextClassLoader() : this.classLoader;
        try {
            return loader.loadClass(className);
        }
        catch (final ClassNotFoundException e) {
            if (this.noWrapperGen()) {
                return WrapperComposite.class;
            }
            RuntimeModeler.logger.fine("Dynamically creating response wrapper bean Class " + className);
            return WrapperBeanGenerator.createResponseWrapperBean(className, method, resElemName, loader);
        }
    }
    
    private Class getExceptionBeanClass(final String className, final Class exception, final String name, final String namespace) {
        boolean decapitalizeExceptionBeanProperties = true;
        final Object o = this.config.properties().get("com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties");
        if (o != null && o instanceof Boolean) {
            decapitalizeExceptionBeanProperties = (boolean)o;
        }
        final ClassLoader loader = (this.classLoader == null) ? Thread.currentThread().getContextClassLoader() : this.classLoader;
        try {
            return loader.loadClass(className);
        }
        catch (final ClassNotFoundException e) {
            RuntimeModeler.logger.fine("Dynamically creating exception bean Class " + className);
            return WrapperBeanGenerator.createExceptionBean(className, exception, this.targetNamespace, name, namespace, loader, decapitalizeExceptionBeanProperties);
        }
    }
    
    protected void determineWebMethodUse(final Class clazz) {
        if (clazz == null) {
            return;
        }
        if (!clazz.isInterface()) {
            if (clazz == Object.class) {
                return;
            }
            for (final Method method : clazz.getMethods()) {
                if (method.getDeclaringClass() == clazz) {
                    final WebMethod webMethod = this.getAnnotation(method, WebMethod.class);
                    if (webMethod != null && !webMethod.exclude()) {
                        this.classUsesWebMethod.add(clazz);
                        break;
                    }
                }
            }
        }
        this.determineWebMethodUse(clazz.getSuperclass());
    }
    
    void processClass(final Class clazz) {
        this.classUsesWebMethod = new HashSet<Class>();
        this.determineWebMethodUse(clazz);
        final WebService webService = this.getAnnotation(clazz, WebService.class);
        final QName portTypeName = getPortTypeName(clazz, this.targetNamespace, this.metadataReader);
        this.packageName = "";
        if (clazz.getPackage() != null) {
            this.packageName = clazz.getPackage().getName();
        }
        this.targetNamespace = portTypeName.getNamespaceURI();
        this.model.setPortTypeName(portTypeName);
        this.model.setTargetNamespace(this.targetNamespace);
        this.model.defaultSchemaNamespaceSuffix = this.config.getMappingInfo().getDefaultSchemaNamespaceSuffix();
        this.model.setWSDLLocation(webService.wsdlLocation());
        final SOAPBinding soapBinding = this.getAnnotation(clazz, SOAPBinding.class);
        if (soapBinding != null) {
            if (soapBinding.style() == SOAPBinding.Style.RPC && soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE) {
                throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { soapBinding, clazz });
            }
            this.isWrapped = (soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.WRAPPED);
        }
        this.defaultBinding = this.createBinding(soapBinding);
        for (final Method method : clazz.getMethods()) {
            Label_0309: {
                if (!clazz.isInterface()) {
                    if (method.getDeclaringClass() == Object.class) {
                        break Label_0309;
                    }
                    if (!getBooleanSystemProperty("com.sun.xml.internal.ws.legacyWebMethod")) {
                        if (!this.isWebMethodBySpec(method, clazz)) {
                            break Label_0309;
                        }
                    }
                    else if (!this.isWebMethod(method)) {
                        break Label_0309;
                    }
                }
                this.processMethod(method);
            }
        }
        final XmlSeeAlso xmlSeeAlso = this.getAnnotation(clazz, XmlSeeAlso.class);
        if (xmlSeeAlso != null) {
            this.model.addAdditionalClasses(xmlSeeAlso.value());
        }
    }
    
    private boolean isWebMethodBySpec(final Method method, final Class clazz) {
        final int modifiers = method.getModifiers();
        final boolean staticFinal = Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers);
        assert Modifier.isPublic(modifiers);
        assert !clazz.isInterface();
        final WebMethod webMethod = this.getAnnotation(method, WebMethod.class);
        if (webMethod != null) {
            if (webMethod.exclude()) {
                return false;
            }
            if (staticFinal) {
                throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(method));
            }
            return true;
        }
        else {
            if (staticFinal) {
                return false;
            }
            final Class declClass = method.getDeclaringClass();
            return this.getAnnotation(declClass, WebService.class) != null;
        }
    }
    
    private boolean isWebMethod(final Method method) {
        final int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            return false;
        }
        final Class clazz = method.getDeclaringClass();
        final boolean declHasWebService = this.getAnnotation(clazz, WebService.class) != null;
        final WebMethod webMethod = this.getAnnotation(method, WebMethod.class);
        return (webMethod != null && !webMethod.exclude() && declHasWebService) || (declHasWebService && !this.classUsesWebMethod.contains(clazz));
    }
    
    protected SOAPBindingImpl createBinding(final SOAPBinding soapBinding) {
        final SOAPBindingImpl rtSOAPBinding = new SOAPBindingImpl();
        final SOAPBinding.Style style = (soapBinding != null) ? soapBinding.style() : SOAPBinding.Style.DOCUMENT;
        rtSOAPBinding.setStyle(style);
        assert this.bindingId != null;
        this.model.bindingId = this.bindingId;
        final SOAPVersion soapVersion = this.bindingId.getSOAPVersion();
        rtSOAPBinding.setSOAPVersion(soapVersion);
        return rtSOAPBinding;
    }
    
    public static String getNamespace(@NotNull final String packageName) {
        if (packageName.length() == 0) {
            return null;
        }
        final StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
        String[] tokens;
        if (tokenizer.countTokens() == 0) {
            tokens = new String[0];
        }
        else {
            tokens = new String[tokenizer.countTokens()];
            for (int i = tokenizer.countTokens() - 1; i >= 0; --i) {
                tokens[i] = tokenizer.nextToken();
            }
        }
        final StringBuilder namespace = new StringBuilder("http://");
        for (int j = 0; j < tokens.length; ++j) {
            if (j != 0) {
                namespace.append('.');
            }
            namespace.append(tokens[j]);
        }
        namespace.append('/');
        return namespace.toString();
    }
    
    private boolean isServiceException(final Class<?> exception) {
        return RuntimeModeler.EXCEPTION_CLASS.isAssignableFrom(exception) && !RuntimeModeler.RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception) && !RuntimeModeler.REMOTE_EXCEPTION_CLASS.isAssignableFrom(exception);
    }
    
    private void processMethod(final Method method) {
        final WebMethod webMethod = this.getAnnotation(method, WebMethod.class);
        if (webMethod != null && webMethod.exclude()) {
            return;
        }
        final String methodName = method.getName();
        final boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
        if (isOneway) {
            for (final Class<?> exception : method.getExceptionTypes()) {
                if (this.isServiceException(exception)) {
                    throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.checked.exceptions", new Object[] { this.portClass.getCanonicalName(), methodName, exception.getName() });
                }
            }
        }
        JavaMethodImpl javaMethod;
        if (method.getDeclaringClass() == this.portClass) {
            javaMethod = new JavaMethodImpl(this.model, method, method, this.metadataReader);
        }
        else {
            try {
                final Method tmpMethod = this.portClass.getMethod(method.getName(), (Class[])method.getParameterTypes());
                javaMethod = new JavaMethodImpl(this.model, tmpMethod, method, this.metadataReader);
            }
            catch (final NoSuchMethodException e) {
                throw new RuntimeModelerException("runtime.modeler.method.not.found", new Object[] { method.getName(), this.portClass.getName() });
            }
        }
        final MEP mep = this.getMEP(method);
        javaMethod.setMEP(mep);
        String action = null;
        String operationName = method.getName();
        if (webMethod != null) {
            action = webMethod.action();
            operationName = ((webMethod.operationName().length() > 0) ? webMethod.operationName() : operationName);
        }
        if (this.binding != null) {
            final WSDLBoundOperation bo = this.binding.getBinding().get(new QName(this.targetNamespace, operationName));
            if (bo != null) {
                final WSDLInput wsdlInput = bo.getOperation().getInput();
                final String wsaAction = wsdlInput.getAction();
                if (wsaAction != null && !wsdlInput.isDefaultAction()) {
                    action = wsaAction;
                }
                else {
                    action = bo.getSOAPAction();
                }
            }
        }
        javaMethod.setOperationQName(new QName(this.targetNamespace, operationName));
        SOAPBinding methodBinding = this.getAnnotation(method, SOAPBinding.class);
        if (methodBinding != null && methodBinding.style() == SOAPBinding.Style.RPC) {
            RuntimeModeler.logger.warning(ModelerMessages.RUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(methodBinding, method.getName(), method.getDeclaringClass().getName()));
        }
        else if (methodBinding == null && !method.getDeclaringClass().equals(this.portClass)) {
            methodBinding = this.getAnnotation(method.getDeclaringClass(), SOAPBinding.class);
            if (methodBinding != null && methodBinding.style() == SOAPBinding.Style.RPC && methodBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE) {
                throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { methodBinding, method.getDeclaringClass() });
            }
        }
        if (methodBinding != null && this.defaultBinding.getStyle() != methodBinding.style()) {
            throw new RuntimeModelerException("runtime.modeler.soapbinding.conflict", new Object[] { methodBinding.style(), method.getName(), this.defaultBinding.getStyle() });
        }
        boolean methodIsWrapped = this.isWrapped;
        SOAPBinding.Style style = this.defaultBinding.getStyle();
        if (methodBinding != null) {
            final SOAPBindingImpl mySOAPBinding = this.createBinding(methodBinding);
            style = mySOAPBinding.getStyle();
            if (action != null) {
                mySOAPBinding.setSOAPAction(action);
            }
            methodIsWrapped = methodBinding.parameterStyle().equals(SOAPBinding.ParameterStyle.WRAPPED);
            javaMethod.setBinding(mySOAPBinding);
        }
        else {
            final SOAPBindingImpl sb = new SOAPBindingImpl(this.defaultBinding);
            if (action != null) {
                sb.setSOAPAction(action);
            }
            else {
                final String defaults = (SOAPVersion.SOAP_11 == sb.getSOAPVersion()) ? "" : null;
                sb.setSOAPAction(defaults);
            }
            javaMethod.setBinding(sb);
        }
        if (!methodIsWrapped) {
            this.processDocBareMethod(javaMethod, operationName, method);
        }
        else if (style.equals(SOAPBinding.Style.DOCUMENT)) {
            this.processDocWrappedMethod(javaMethod, methodName, operationName, method);
        }
        else {
            this.processRpcMethod(javaMethod, methodName, operationName, method);
        }
        this.model.addJavaMethod(javaMethod);
    }
    
    private MEP getMEP(final Method m) {
        if (this.getAnnotation(m, Oneway.class) != null) {
            return MEP.ONE_WAY;
        }
        if (Response.class.isAssignableFrom(m.getReturnType())) {
            return MEP.ASYNC_POLL;
        }
        if (Future.class.isAssignableFrom(m.getReturnType())) {
            return MEP.ASYNC_CALLBACK;
        }
        return MEP.REQUEST_RESPONSE;
    }
    
    protected void processDocWrappedMethod(final JavaMethodImpl javaMethod, final String methodName, final String operationName, final Method method) {
        boolean methodHasHeaderParams = false;
        final boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
        final RequestWrapper reqWrapper = this.getAnnotation(method, RequestWrapper.class);
        final ResponseWrapper resWrapper = this.getAnnotation(method, ResponseWrapper.class);
        String beanPackage = this.packageName + ".jaxws.";
        if (this.packageName == null || this.packageName.length() == 0) {
            beanPackage = "jaxws.";
        }
        String requestClassName;
        if (reqWrapper != null && reqWrapper.className().length() > 0) {
            requestClassName = reqWrapper.className();
        }
        else {
            requestClassName = beanPackage + capitalize(method.getName());
        }
        String responseClassName;
        if (resWrapper != null && resWrapper.className().length() > 0) {
            responseClassName = resWrapper.className();
        }
        else {
            responseClassName = beanPackage + capitalize(method.getName()) + "Response";
        }
        String reqName = operationName;
        String reqNamespace = this.targetNamespace;
        String reqPartName = "parameters";
        if (reqWrapper != null) {
            if (reqWrapper.targetNamespace().length() > 0) {
                reqNamespace = reqWrapper.targetNamespace();
            }
            if (reqWrapper.localName().length() > 0) {
                reqName = reqWrapper.localName();
            }
            try {
                if (reqWrapper.partName().length() > 0) {
                    reqPartName = reqWrapper.partName();
                }
            }
            catch (final LinkageError linkageError) {}
        }
        final QName reqElementName = new QName(reqNamespace, reqName);
        javaMethod.setRequestPayloadName(reqElementName);
        final Class requestClass = this.getRequestWrapperClass(requestClassName, method, reqElementName);
        Class responseClass = null;
        String resName = operationName + "Response";
        String resNamespace = this.targetNamespace;
        QName resElementName = null;
        String resPartName = "parameters";
        if (!isOneway) {
            if (resWrapper != null) {
                if (resWrapper.targetNamespace().length() > 0) {
                    resNamespace = resWrapper.targetNamespace();
                }
                if (resWrapper.localName().length() > 0) {
                    resName = resWrapper.localName();
                }
                try {
                    if (resWrapper.partName().length() > 0) {
                        resPartName = resWrapper.partName();
                    }
                }
                catch (final LinkageError linkageError2) {}
            }
            resElementName = new QName(resNamespace, resName);
            responseClass = this.getResponseWrapperClass(responseClassName, method, resElementName);
        }
        TypeInfo typeRef = new TypeInfo(reqElementName, requestClass, new Annotation[0]);
        typeRef.setNillable(false);
        final WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
        requestWrapper.setPartName(reqPartName);
        requestWrapper.setBinding(ParameterBinding.BODY);
        javaMethod.addParameter(requestWrapper);
        WrapperParameter responseWrapper = null;
        if (!isOneway) {
            typeRef = new TypeInfo(resElementName, responseClass, new Annotation[0]);
            typeRef.setNillable(false);
            responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
            javaMethod.addParameter(responseWrapper);
            responseWrapper.setBinding(ParameterBinding.BODY);
        }
        final WebResult webResult = this.getAnnotation(method, WebResult.class);
        XmlElement xmlElem = this.getAnnotation(method, XmlElement.class);
        QName resultQName = getReturnQName(method, webResult, xmlElem);
        Class returnType = method.getReturnType();
        boolean isResultHeader = false;
        if (webResult != null) {
            isResultHeader = webResult.header();
            methodHasHeaderParams = (isResultHeader || methodHasHeaderParams);
            if (isResultHeader && xmlElem != null) {
                throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " as the return value is bound to header", new Object[0]);
            }
            if (resultQName.getNamespaceURI().length() == 0 && webResult.header()) {
                resultQName = new QName(this.targetNamespace, resultQName.getLocalPart());
            }
        }
        if (javaMethod.isAsync()) {
            returnType = this.getAsyncReturnType(method, returnType);
            resultQName = new QName("return");
        }
        resultQName = this.qualifyWrappeeIfNeeded(resultQName, resNamespace);
        if (!isOneway && returnType != null && !returnType.getName().equals("void")) {
            final Annotation[] rann = this.getAnnotations(method);
            if (resultQName.getLocalPart() != null) {
                final TypeInfo rTypeReference = new TypeInfo(resultQName, returnType, rann);
                this.metadataReader.getProperties(rTypeReference.properties(), method);
                rTypeReference.setGenericType(method.getGenericReturnType());
                final ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
                if (isResultHeader) {
                    returnParameter.setBinding(ParameterBinding.HEADER);
                    javaMethod.addParameter(returnParameter);
                }
                else {
                    returnParameter.setBinding(ParameterBinding.BODY);
                    responseWrapper.addWrapperChild(returnParameter);
                }
            }
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Type[] genericParameterTypes = method.getGenericParameterTypes();
        final Annotation[][] pannotations = this.getParamAnnotations(method);
        int pos = 0;
        for (Class clazzType : parameterTypes) {
            String partName = null;
            final String paramName = "arg" + pos;
            boolean isHeader = false;
            if (!javaMethod.isAsync() || !AsyncHandler.class.isAssignableFrom(clazzType)) {
                final boolean isHolder = RuntimeModeler.HOLDER_CLASS.isAssignableFrom(clazzType);
                if (isHolder && clazzType == Holder.class) {
                    clazzType = Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
                }
                WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
                WebParam webParam = null;
                xmlElem = null;
                for (final Annotation annotation : pannotations[pos]) {
                    if (annotation.annotationType() == WebParam.class) {
                        webParam = (WebParam)annotation;
                    }
                    else if (annotation.annotationType() == XmlElement.class) {
                        xmlElem = (XmlElement)annotation;
                    }
                }
                QName paramQName = getParameterQName(method, webParam, xmlElem, paramName);
                if (webParam != null) {
                    isHeader = webParam.header();
                    methodHasHeaderParams = (isHeader || methodHasHeaderParams);
                    if (isHeader && xmlElem != null) {
                        throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " parameter that is bound to header", new Object[0]);
                    }
                    if (webParam.partName().length() > 0) {
                        partName = webParam.partName();
                    }
                    else {
                        partName = paramQName.getLocalPart();
                    }
                    if (isHeader && paramQName.getNamespaceURI().equals("")) {
                        paramQName = new QName(this.targetNamespace, paramQName.getLocalPart());
                    }
                    paramMode = webParam.mode();
                    if (isHolder && paramMode == WebParam.Mode.IN) {
                        paramMode = WebParam.Mode.INOUT;
                    }
                }
                paramQName = this.qualifyWrappeeIfNeeded(paramQName, reqNamespace);
                typeRef = new TypeInfo(paramQName, clazzType, pannotations[pos]);
                this.metadataReader.getProperties(typeRef.properties(), method, pos);
                typeRef.setGenericType(genericParameterTypes[pos]);
                final ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
                if (isHeader) {
                    param.setBinding(ParameterBinding.HEADER);
                    javaMethod.addParameter(param);
                    param.setPartName(partName);
                }
                else {
                    param.setBinding(ParameterBinding.BODY);
                    if (paramMode != WebParam.Mode.OUT) {
                        requestWrapper.addWrapperChild(param);
                    }
                    if (paramMode != WebParam.Mode.IN) {
                        if (isOneway) {
                            throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { this.portClass.getCanonicalName(), methodName });
                        }
                        responseWrapper.addWrapperChild(param);
                    }
                }
            }
        }
        if (methodHasHeaderParams) {
            resPartName = "result";
        }
        if (responseWrapper != null) {
            responseWrapper.setPartName(resPartName);
        }
        this.processExceptions(javaMethod, method);
    }
    
    private QName qualifyWrappeeIfNeeded(final QName resultQName, final String ns) {
        final Object o = this.config.properties().get("com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified");
        final boolean qualified = o != null && o instanceof Boolean && (boolean)o;
        if (qualified && (resultQName.getNamespaceURI() == null || "".equals(resultQName.getNamespaceURI()))) {
            return new QName(ns, resultQName.getLocalPart());
        }
        return resultQName;
    }
    
    protected void processRpcMethod(final JavaMethodImpl javaMethod, final String methodName, final String operationName, final Method method) {
        final boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
        final Map<Integer, ParameterImpl> resRpcParams = new TreeMap<Integer, ParameterImpl>();
        final Map<Integer, ParameterImpl> reqRpcParams = new TreeMap<Integer, ParameterImpl>();
        String reqNamespace = this.targetNamespace;
        String respNamespace = this.targetNamespace;
        if (this.binding != null && SOAPBinding.Style.RPC.equals(this.binding.getBinding().getStyle())) {
            final QName opQName = new QName(this.binding.getBinding().getPortTypeName().getNamespaceURI(), operationName);
            final WSDLBoundOperation op = this.binding.getBinding().get(opQName);
            if (op != null) {
                if (op.getRequestNamespace() != null) {
                    reqNamespace = op.getRequestNamespace();
                }
                if (op.getResponseNamespace() != null) {
                    respNamespace = op.getResponseNamespace();
                }
            }
        }
        final QName reqElementName = new QName(reqNamespace, operationName);
        javaMethod.setRequestPayloadName(reqElementName);
        QName resElementName = null;
        if (!isOneway) {
            resElementName = new QName(respNamespace, operationName + "Response");
        }
        final Class wrapperType = WrapperComposite.class;
        TypeInfo typeRef = new TypeInfo(reqElementName, wrapperType, new Annotation[0]);
        final WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
        requestWrapper.setInBinding(ParameterBinding.BODY);
        javaMethod.addParameter(requestWrapper);
        WrapperParameter responseWrapper = null;
        if (!isOneway) {
            typeRef = new TypeInfo(resElementName, wrapperType, new Annotation[0]);
            responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
            responseWrapper.setOutBinding(ParameterBinding.BODY);
            javaMethod.addParameter(responseWrapper);
        }
        Class returnType = method.getReturnType();
        String resultName = "return";
        String resultTNS = this.targetNamespace;
        String resultPartName = resultName;
        boolean isResultHeader = false;
        final WebResult webResult = this.getAnnotation(method, WebResult.class);
        if (webResult != null) {
            isResultHeader = webResult.header();
            if (webResult.name().length() > 0) {
                resultName = webResult.name();
            }
            if (webResult.partName().length() > 0) {
                resultPartName = webResult.partName();
                if (!isResultHeader) {
                    resultName = resultPartName;
                }
            }
            else {
                resultPartName = resultName;
            }
            if (webResult.targetNamespace().length() > 0) {
                resultTNS = webResult.targetNamespace();
            }
            isResultHeader = webResult.header();
        }
        QName resultQName;
        if (isResultHeader) {
            resultQName = new QName(resultTNS, resultName);
        }
        else {
            resultQName = new QName(resultName);
        }
        if (javaMethod.isAsync()) {
            returnType = this.getAsyncReturnType(method, returnType);
        }
        if (!isOneway && returnType != null && returnType != Void.TYPE) {
            final Annotation[] rann = this.getAnnotations(method);
            final TypeInfo rTypeReference = new TypeInfo(resultQName, returnType, rann);
            this.metadataReader.getProperties(rTypeReference.properties(), method);
            rTypeReference.setGenericType(method.getGenericReturnType());
            final ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
            returnParameter.setPartName(resultPartName);
            if (isResultHeader) {
                returnParameter.setBinding(ParameterBinding.HEADER);
                javaMethod.addParameter(returnParameter);
                rTypeReference.setGlobalElement(true);
            }
            else {
                final ParameterBinding rb = this.getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
                returnParameter.setBinding(rb);
                if (rb.isBody()) {
                    rTypeReference.setGlobalElement(false);
                    final WSDLPart p = this.getPart(new QName(this.targetNamespace, operationName), resultPartName, WebParam.Mode.OUT);
                    if (p == null) {
                        resRpcParams.put(resRpcParams.size() + 10000, returnParameter);
                    }
                    else {
                        resRpcParams.put(p.getIndex(), returnParameter);
                    }
                }
                else {
                    javaMethod.addParameter(returnParameter);
                }
            }
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Type[] genericParameterTypes = method.getGenericParameterTypes();
        final Annotation[][] pannotations = this.getParamAnnotations(method);
        int pos = 0;
        for (Class clazzType : parameterTypes) {
            String paramName = "";
            String paramNamespace = "";
            String partName = "";
            boolean isHeader = false;
            if (!javaMethod.isAsync() || !AsyncHandler.class.isAssignableFrom(clazzType)) {
                final boolean isHolder = RuntimeModeler.HOLDER_CLASS.isAssignableFrom(clazzType);
                if (isHolder && clazzType == Holder.class) {
                    clazzType = Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
                }
                WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
                for (final Annotation annotation : pannotations[pos]) {
                    if (annotation.annotationType() == WebParam.class) {
                        final WebParam webParam = (WebParam)annotation;
                        paramName = webParam.name();
                        partName = webParam.partName();
                        isHeader = webParam.header();
                        WebParam.Mode mode = webParam.mode();
                        paramNamespace = webParam.targetNamespace();
                        if (isHolder && mode == WebParam.Mode.IN) {
                            mode = WebParam.Mode.INOUT;
                        }
                        paramMode = mode;
                        break;
                    }
                }
                if (paramName.length() == 0) {
                    paramName = "arg" + pos;
                }
                if (partName.length() == 0) {
                    partName = paramName;
                }
                else if (!isHeader) {
                    paramName = partName;
                }
                if (partName.length() == 0) {
                    partName = paramName;
                }
                QName paramQName;
                if (!isHeader) {
                    paramQName = new QName("", paramName);
                }
                else {
                    if (paramNamespace.length() == 0) {
                        paramNamespace = this.targetNamespace;
                    }
                    paramQName = new QName(paramNamespace, paramName);
                }
                typeRef = new TypeInfo(paramQName, clazzType, pannotations[pos]);
                this.metadataReader.getProperties(typeRef.properties(), method, pos);
                typeRef.setGenericType(genericParameterTypes[pos]);
                final ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
                param.setPartName(partName);
                if (paramMode == WebParam.Mode.INOUT) {
                    ParameterBinding pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
                    param.setInBinding(pb);
                    pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
                    param.setOutBinding(pb);
                }
                else if (isHeader) {
                    typeRef.setGlobalElement(true);
                    param.setBinding(ParameterBinding.HEADER);
                }
                else {
                    final ParameterBinding pb = this.getBinding(operationName, partName, false, paramMode);
                    param.setBinding(pb);
                }
                if (param.getInBinding().isBody()) {
                    typeRef.setGlobalElement(false);
                    if (!param.isOUT()) {
                        final WSDLPart p2 = this.getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.IN);
                        if (p2 == null) {
                            reqRpcParams.put(reqRpcParams.size() + 10000, param);
                        }
                        else {
                            reqRpcParams.put(p2.getIndex(), param);
                        }
                    }
                    if (!param.isIN()) {
                        if (isOneway) {
                            throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { this.portClass.getCanonicalName(), methodName });
                        }
                        final WSDLPart p2 = this.getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.OUT);
                        if (p2 == null) {
                            resRpcParams.put(resRpcParams.size() + 10000, param);
                        }
                        else {
                            resRpcParams.put(p2.getIndex(), param);
                        }
                    }
                }
                else {
                    javaMethod.addParameter(param);
                }
            }
        }
        for (final ParameterImpl p3 : reqRpcParams.values()) {
            requestWrapper.addWrapperChild(p3);
        }
        for (final ParameterImpl p3 : resRpcParams.values()) {
            responseWrapper.addWrapperChild(p3);
        }
        this.processExceptions(javaMethod, method);
    }
    
    protected void processExceptions(final JavaMethodImpl javaMethod, final Method method) {
        final Action actionAnn = this.getAnnotation(method, Action.class);
        FaultAction[] faultActions = new FaultAction[0];
        if (actionAnn != null) {
            faultActions = actionAnn.fault();
        }
        for (final Class<?> exception : method.getExceptionTypes()) {
            if (RuntimeModeler.EXCEPTION_CLASS.isAssignableFrom(exception)) {
                if (!RuntimeModeler.RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception)) {
                    if (!RuntimeModeler.REMOTE_EXCEPTION_CLASS.isAssignableFrom(exception)) {
                        final WebFault webFault = this.getAnnotation(exception, WebFault.class);
                        final Method faultInfoMethod = this.getWSDLExceptionFaultInfo(exception);
                        ExceptionType exceptionType = ExceptionType.WSDLException;
                        String namespace = this.targetNamespace;
                        String name = exception.getSimpleName();
                        String beanPackage = this.packageName + ".jaxws.";
                        if (this.packageName.length() == 0) {
                            beanPackage = "jaxws.";
                        }
                        String className = beanPackage + name + "Bean";
                        String messageName = exception.getSimpleName();
                        if (webFault != null) {
                            if (webFault.faultBean().length() > 0) {
                                className = webFault.faultBean();
                            }
                            if (webFault.name().length() > 0) {
                                name = webFault.name();
                            }
                            if (webFault.targetNamespace().length() > 0) {
                                namespace = webFault.targetNamespace();
                            }
                            if (webFault.messageName().length() > 0) {
                                messageName = webFault.messageName();
                            }
                        }
                        Class exceptionBean;
                        Annotation[] anns;
                        if (faultInfoMethod == null) {
                            exceptionBean = this.getExceptionBeanClass(className, exception, name, namespace);
                            exceptionType = ExceptionType.UserDefined;
                            anns = this.getAnnotations(exceptionBean);
                        }
                        else {
                            exceptionBean = faultInfoMethod.getReturnType();
                            anns = this.getAnnotations(faultInfoMethod);
                        }
                        final QName faultName = new QName(namespace, name);
                        final TypeInfo typeRef = new TypeInfo(faultName, exceptionBean, anns);
                        final CheckedExceptionImpl checkedException = new CheckedExceptionImpl(javaMethod, exception, typeRef, exceptionType);
                        checkedException.setMessageName(messageName);
                        for (final FaultAction fa : faultActions) {
                            if (fa.className().equals(exception) && !fa.value().equals("")) {
                                checkedException.setFaultAction(fa.value());
                                break;
                            }
                        }
                        javaMethod.addException(checkedException);
                    }
                }
            }
        }
    }
    
    protected Method getWSDLExceptionFaultInfo(final Class exception) {
        if (this.getAnnotation(exception, WebFault.class) == null) {
            return null;
        }
        try {
            return exception.getMethod("getFaultInfo", (Class[])new Class[0]);
        }
        catch (final NoSuchMethodException e) {
            return null;
        }
    }
    
    protected void processDocBareMethod(final JavaMethodImpl javaMethod, final String operationName, final Method method) {
        String resultName = operationName + "Response";
        String resultTNS = this.targetNamespace;
        String resultPartName = null;
        boolean isResultHeader = false;
        final WebResult webResult = this.getAnnotation(method, WebResult.class);
        if (webResult != null) {
            if (webResult.name().length() > 0) {
                resultName = webResult.name();
            }
            if (webResult.targetNamespace().length() > 0) {
                resultTNS = webResult.targetNamespace();
            }
            resultPartName = webResult.partName();
            isResultHeader = webResult.header();
        }
        Class returnType = method.getReturnType();
        final Type gReturnType = method.getGenericReturnType();
        if (javaMethod.isAsync()) {
            returnType = this.getAsyncReturnType(method, returnType);
        }
        if (returnType != null && !returnType.getName().equals("void")) {
            final Annotation[] rann = this.getAnnotations(method);
            if (resultName != null) {
                final QName responseQName = new QName(resultTNS, resultName);
                final TypeInfo rTypeReference = new TypeInfo(responseQName, returnType, rann);
                rTypeReference.setGenericType(gReturnType);
                this.metadataReader.getProperties(rTypeReference.properties(), method);
                final ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
                if (resultPartName == null || resultPartName.length() == 0) {
                    resultPartName = resultName;
                }
                returnParameter.setPartName(resultPartName);
                if (isResultHeader) {
                    returnParameter.setBinding(ParameterBinding.HEADER);
                }
                else {
                    final ParameterBinding rb = this.getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
                    returnParameter.setBinding(rb);
                }
                javaMethod.addParameter(returnParameter);
            }
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Type[] genericParameterTypes = method.getGenericParameterTypes();
        final Annotation[][] pannotations = this.getParamAnnotations(method);
        int pos = 0;
        for (Class clazzType : parameterTypes) {
            String paramName = operationName;
            String partName = null;
            String requestNamespace = this.targetNamespace;
            boolean isHeader = false;
            if (!javaMethod.isAsync() || !AsyncHandler.class.isAssignableFrom(clazzType)) {
                final boolean isHolder = RuntimeModeler.HOLDER_CLASS.isAssignableFrom(clazzType);
                if (isHolder && clazzType == Holder.class) {
                    clazzType = Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
                }
                WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
                final Annotation[] array2 = pannotations[pos];
                final int length2 = array2.length;
                int j = 0;
                while (j < length2) {
                    final Annotation annotation = array2[j];
                    if (annotation.annotationType() == WebParam.class) {
                        final WebParam webParam = (WebParam)annotation;
                        paramMode = webParam.mode();
                        if (isHolder && paramMode == WebParam.Mode.IN) {
                            paramMode = WebParam.Mode.INOUT;
                        }
                        isHeader = webParam.header();
                        if (isHeader) {
                            paramName = "arg" + pos;
                        }
                        if (paramMode == WebParam.Mode.OUT && !isHeader) {
                            paramName = operationName + "Response";
                        }
                        if (webParam.name().length() > 0) {
                            paramName = webParam.name();
                        }
                        partName = webParam.partName();
                        if (!webParam.targetNamespace().equals("")) {
                            requestNamespace = webParam.targetNamespace();
                            break;
                        }
                        break;
                    }
                    else {
                        ++j;
                    }
                }
                final QName requestQName = new QName(requestNamespace, paramName);
                if (!isHeader && paramMode != WebParam.Mode.OUT) {
                    javaMethod.setRequestPayloadName(requestQName);
                }
                final TypeInfo typeRef = new TypeInfo(requestQName, clazzType, pannotations[pos]);
                this.metadataReader.getProperties(typeRef.properties(), method, pos);
                typeRef.setGenericType(genericParameterTypes[pos]);
                final ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
                if (partName == null || partName.length() == 0) {
                    partName = paramName;
                }
                param.setPartName(partName);
                if (paramMode == WebParam.Mode.INOUT) {
                    ParameterBinding pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
                    param.setInBinding(pb);
                    pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
                    param.setOutBinding(pb);
                }
                else if (isHeader) {
                    param.setBinding(ParameterBinding.HEADER);
                }
                else {
                    final ParameterBinding pb = this.getBinding(operationName, partName, false, paramMode);
                    param.setBinding(pb);
                }
                javaMethod.addParameter(param);
            }
        }
        this.validateDocBare(javaMethod);
        this.processExceptions(javaMethod, method);
    }
    
    private void validateDocBare(final JavaMethodImpl javaMethod) {
        int numInBodyBindings = 0;
        for (final Parameter param : javaMethod.getRequestParameters()) {
            if (param.getBinding().equals(ParameterBinding.BODY) && param.isIN()) {
                ++numInBodyBindings;
            }
            if (numInBodyBindings > 1) {
                throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
            }
        }
        int numOutBodyBindings = 0;
        for (final Parameter param2 : javaMethod.getResponseParameters()) {
            if (param2.getBinding().equals(ParameterBinding.BODY) && param2.isOUT()) {
                ++numOutBodyBindings;
            }
            if (numOutBodyBindings > 1) {
                throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
            }
        }
    }
    
    private Class getAsyncReturnType(final Method method, final Class returnType) {
        if (Response.class.isAssignableFrom(returnType)) {
            final Type ret = method.getGenericReturnType();
            return Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)ret).getActualTypeArguments()[0]);
        }
        final Type[] types = method.getGenericParameterTypes();
        final Class[] params = method.getParameterTypes();
        int i = 0;
        for (final Class cls : params) {
            if (AsyncHandler.class.isAssignableFrom(cls)) {
                return Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)types[i]).getActualTypeArguments()[0]);
            }
            ++i;
        }
        return returnType;
    }
    
    public static String capitalize(final String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
    
    public static QName getServiceName(final Class<?> implClass) {
        return getServiceName(implClass, null);
    }
    
    public static QName getServiceName(final Class<?> implClass, final boolean isStandard) {
        return getServiceName(implClass, null, isStandard);
    }
    
    public static QName getServiceName(final Class<?> implClass, final MetadataReader reader) {
        return getServiceName(implClass, reader, true);
    }
    
    public static QName getServiceName(final Class<?> implClass, final MetadataReader reader, final boolean isStandard) {
        if (implClass.isInterface()) {
            throw new RuntimeModelerException("runtime.modeler.cannot.get.serviceName.from.interface", new Object[] { implClass.getCanonicalName() });
        }
        String name = implClass.getSimpleName() + "Service";
        String packageName = "";
        if (implClass.getPackage() != null) {
            packageName = implClass.getPackage().getName();
        }
        final WebService webService = getAnnotation(WebService.class, implClass, reader);
        if (isStandard && webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { implClass.getCanonicalName() });
        }
        if (webService != null && webService.serviceName().length() > 0) {
            name = webService.serviceName();
        }
        String targetNamespace = getNamespace(packageName);
        if (webService != null && webService.targetNamespace().length() > 0) {
            targetNamespace = webService.targetNamespace();
        }
        else if (targetNamespace == null) {
            throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { implClass.getName() });
        }
        return new QName(targetNamespace, name);
    }
    
    public static QName getPortName(final Class<?> implClass, final String targetNamespace) {
        return getPortName(implClass, null, targetNamespace);
    }
    
    public static QName getPortName(final Class<?> implClass, final String targetNamespace, final boolean isStandard) {
        return getPortName(implClass, null, targetNamespace, isStandard);
    }
    
    public static QName getPortName(final Class<?> implClass, final MetadataReader reader, final String targetNamespace) {
        return getPortName(implClass, reader, targetNamespace, true);
    }
    
    public static QName getPortName(final Class<?> implClass, final MetadataReader reader, String targetNamespace, final boolean isStandard) {
        final WebService webService = getAnnotation(WebService.class, implClass, reader);
        if (isStandard && webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { implClass.getCanonicalName() });
        }
        String name;
        if (webService != null && webService.portName().length() > 0) {
            name = webService.portName();
        }
        else if (webService != null && webService.name().length() > 0) {
            name = webService.name() + "Port";
        }
        else {
            name = implClass.getSimpleName() + "Port";
        }
        if (targetNamespace == null) {
            if (webService != null && webService.targetNamespace().length() > 0) {
                targetNamespace = webService.targetNamespace();
            }
            else {
                String packageName = null;
                if (implClass.getPackage() != null) {
                    packageName = implClass.getPackage().getName();
                }
                if (packageName != null) {
                    targetNamespace = getNamespace(packageName);
                }
                if (targetNamespace == null) {
                    throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { implClass.getName() });
                }
            }
        }
        return new QName(targetNamespace, name);
    }
    
    static <A extends Annotation> A getAnnotation(final Class<A> t, final Class<?> cls, final MetadataReader reader) {
        return (reader == null) ? cls.getAnnotation(t) : reader.getAnnotation(t, cls);
    }
    
    public static QName getPortTypeName(final Class<?> implOrSeiClass) {
        return getPortTypeName(implOrSeiClass, null, null);
    }
    
    public static QName getPortTypeName(final Class<?> implOrSeiClass, final MetadataReader metadataReader) {
        return getPortTypeName(implOrSeiClass, null, metadataReader);
    }
    
    public static QName getPortTypeName(final Class<?> implOrSeiClass, String tns, final MetadataReader reader) {
        assert implOrSeiClass != null;
        WebService webService = getAnnotation(WebService.class, implOrSeiClass, reader);
        Class<?> clazz = implOrSeiClass;
        if (webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { implOrSeiClass.getCanonicalName() });
        }
        if (!implOrSeiClass.isInterface()) {
            final String epi = webService.endpointInterface();
            if (epi.length() > 0) {
                try {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(epi);
                }
                catch (final ClassNotFoundException e) {
                    throw new RuntimeModelerException("runtime.modeler.class.not.found", new Object[] { epi });
                }
                final WebService ws = getAnnotation(WebService.class, clazz, reader);
                if (ws == null) {
                    throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() });
                }
            }
        }
        webService = getAnnotation(WebService.class, clazz, reader);
        String name = webService.name();
        if (name.length() == 0) {
            name = clazz.getSimpleName();
        }
        if (tns == null || "".equals(tns.trim())) {
            tns = webService.targetNamespace();
        }
        if (tns.length() == 0) {
            tns = getNamespace(clazz.getPackage().getName());
        }
        if (tns == null) {
            throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { clazz.getName() });
        }
        return new QName(tns, name);
    }
    
    private ParameterBinding getBinding(final String operation, final String part, final boolean isHeader, final WebParam.Mode mode) {
        if (this.binding != null) {
            final QName opName = new QName(this.binding.getBinding().getPortType().getName().getNamespaceURI(), operation);
            return this.binding.getBinding().getBinding(opName, part, mode);
        }
        if (isHeader) {
            return ParameterBinding.HEADER;
        }
        return ParameterBinding.BODY;
    }
    
    private WSDLPart getPart(final QName opName, final String partName, final WebParam.Mode mode) {
        if (this.binding != null) {
            final WSDLBoundOperation bo = this.binding.getBinding().get(opName);
            if (bo != null) {
                return bo.getPart(partName, mode);
            }
        }
        return null;
    }
    
    private static Boolean getBooleanSystemProperty(final String prop) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String value = System.getProperty(prop);
                return (value != null) ? Boolean.valueOf(value) : Boolean.FALSE;
            }
        });
    }
    
    private static QName getReturnQName(final Method method, final WebResult webResult, final XmlElement xmlElem) {
        String webResultName = null;
        if (webResult != null && webResult.name().length() > 0) {
            webResultName = webResult.name();
        }
        String xmlElemName = null;
        if (xmlElem != null && !xmlElem.name().equals("##default")) {
            xmlElemName = xmlElem.name();
        }
        if (xmlElemName != null && webResultName != null && !xmlElemName.equals(webResultName)) {
            throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebResult(name)=" + webResultName + " are different for method " + method, new Object[0]);
        }
        String localPart = "return";
        if (webResultName != null) {
            localPart = webResultName;
        }
        else if (xmlElemName != null) {
            localPart = xmlElemName;
        }
        String webResultNS = null;
        if (webResult != null && webResult.targetNamespace().length() > 0) {
            webResultNS = webResult.targetNamespace();
        }
        String xmlElemNS = null;
        if (xmlElem != null && !xmlElem.namespace().equals("##default")) {
            xmlElemNS = xmlElem.namespace();
        }
        if (xmlElemNS != null && webResultNS != null && !xmlElemNS.equals(webResultNS)) {
            throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebResult(targetNamespace)=" + webResultNS + " are different for method " + method, new Object[0]);
        }
        String ns = "";
        if (webResultNS != null) {
            ns = webResultNS;
        }
        else if (xmlElemNS != null) {
            ns = xmlElemNS;
        }
        return new QName(ns, localPart);
    }
    
    private static QName getParameterQName(final Method method, final WebParam webParam, final XmlElement xmlElem, final String paramDefault) {
        String webParamName = null;
        if (webParam != null && webParam.name().length() > 0) {
            webParamName = webParam.name();
        }
        String xmlElemName = null;
        if (xmlElem != null && !xmlElem.name().equals("##default")) {
            xmlElemName = xmlElem.name();
        }
        if (xmlElemName != null && webParamName != null && !xmlElemName.equals(webParamName)) {
            throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebParam(name)=" + webParamName + " are different for method " + method, new Object[0]);
        }
        String localPart = paramDefault;
        if (webParamName != null) {
            localPart = webParamName;
        }
        else if (xmlElemName != null) {
            localPart = xmlElemName;
        }
        String webParamNS = null;
        if (webParam != null && webParam.targetNamespace().length() > 0) {
            webParamNS = webParam.targetNamespace();
        }
        String xmlElemNS = null;
        if (xmlElem != null && !xmlElem.namespace().equals("##default")) {
            xmlElemNS = xmlElem.namespace();
        }
        if (xmlElemNS != null && webParamNS != null && !xmlElemNS.equals(webParamNS)) {
            throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebParam(targetNamespace)=" + webParamNS + " are different for method " + method, new Object[0]);
        }
        String ns = "";
        if (webParamNS != null) {
            ns = webParamNS;
        }
        else if (xmlElemNS != null) {
            ns = xmlElemNS;
        }
        return new QName(ns, localPart);
    }
    
    static {
        HOLDER_CLASS = Holder.class;
        REMOTE_EXCEPTION_CLASS = RemoteException.class;
        RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
        EXCEPTION_CLASS = Exception.class;
        logger = Logger.getLogger("com.sun.xml.internal.ws.server");
    }
}
