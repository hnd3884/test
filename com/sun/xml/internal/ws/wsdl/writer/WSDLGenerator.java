package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.ws.util.RuntimeVersion;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.xml.internal.bind.v2.schemagen.Util;
import com.sun.xml.internal.txw2.output.TXWResult;
import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;
import com.sun.xml.internal.ws.wsdl.writer.document.Port;
import com.sun.xml.internal.ws.wsdl.writer.document.Service;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPAddress;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.Header;
import com.sun.xml.internal.ws.wsdl.writer.document.Fault;
import com.sun.xml.internal.ws.wsdl.writer.document.BindingOperationType;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPFault;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.Body;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.BodyType;
import com.sun.xml.internal.ws.wsdl.writer.document.Binding;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import java.util.Collection;
import com.sun.xml.internal.ws.wsdl.writer.document.FaultType;
import com.sun.xml.internal.ws.wsdl.writer.document.Operation;
import com.sun.xml.internal.ws.wsdl.writer.document.PortType;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.wsdl.writer.document.Message;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Element;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;
import java.util.HashMap;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import java.util.Iterator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.ws.wsdl.writer.document.Import;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.txw2.TXW;
import javax.xml.transform.Result;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import java.io.File;
import javax.xml.ws.Holder;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLGeneratorExtension;
import java.util.ArrayList;
import java.util.HashSet;
import com.sun.xml.internal.ws.api.server.Container;
import java.util.List;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.xml.internal.ws.wsdl.writer.document.Types;
import com.sun.xml.internal.ws.wsdl.writer.document.Definitions;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;

public class WSDLGenerator
{
    private JAXWSOutputSchemaResolver resolver;
    private WSDLResolver wsdlResolver;
    private AbstractSEIModelImpl model;
    private Definitions serviceDefinitions;
    private Definitions portDefinitions;
    private Types types;
    private static final String DOT_WSDL = ".wsdl";
    private static final String RESPONSE = "Response";
    private static final String PARAMETERS = "parameters";
    private static final String RESULT = "parameters";
    private static final String UNWRAPPABLE_RESULT = "result";
    private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    private static final String XSD_PREFIX = "xsd";
    private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";
    private static final String SOAP_PREFIX = "soap";
    private static final String SOAP12_PREFIX = "soap12";
    private static final String TNS_PREFIX = "tns";
    private static final String DOCUMENT = "document";
    private static final String RPC = "rpc";
    private static final String LITERAL = "literal";
    private static final String REPLACE_WITH_ACTUAL_URL = "REPLACE_WITH_ACTUAL_URL";
    private Set<QName> processedExceptions;
    private WSBinding binding;
    private String wsdlLocation;
    private String portWSDLID;
    private String schemaPrefix;
    private WSDLGeneratorExtension extension;
    List<WSDLGeneratorExtension> extensionHandlers;
    private String endpointAddress;
    private Container container;
    private final Class implType;
    private boolean inlineSchemas;
    private final boolean disableXmlSecurity;
    
    public WSDLGenerator(final AbstractSEIModelImpl model, final WSDLResolver wsdlResolver, final WSBinding binding, final Container container, final Class implType, final boolean inlineSchemas, final WSDLGeneratorExtension... extensions) {
        this(model, wsdlResolver, binding, container, implType, inlineSchemas, false, extensions);
    }
    
    public WSDLGenerator(final AbstractSEIModelImpl model, final WSDLResolver wsdlResolver, final WSBinding binding, final Container container, final Class implType, final boolean inlineSchemas, final boolean disableXmlSecurity, final WSDLGeneratorExtension... extensions) {
        this.wsdlResolver = null;
        this.processedExceptions = new HashSet<QName>();
        this.endpointAddress = "REPLACE_WITH_ACTUAL_URL";
        this.model = model;
        this.resolver = new JAXWSOutputSchemaResolver();
        this.wsdlResolver = wsdlResolver;
        this.binding = binding;
        this.container = container;
        this.implType = implType;
        this.extensionHandlers = new ArrayList<WSDLGeneratorExtension>();
        this.inlineSchemas = inlineSchemas;
        this.disableXmlSecurity = disableXmlSecurity;
        this.register(new W3CAddressingWSDLGeneratorExtension());
        this.register(new W3CAddressingMetadataWSDLGeneratorExtension());
        this.register(new PolicyWSDLGeneratorExtension());
        if (container != null) {
            final WSDLGeneratorExtension[] wsdlGeneratorExtensions = container.getSPI(WSDLGeneratorExtension[].class);
            if (wsdlGeneratorExtensions != null) {
                for (final WSDLGeneratorExtension wsdlGeneratorExtension : wsdlGeneratorExtensions) {
                    this.register(wsdlGeneratorExtension);
                }
            }
        }
        for (final WSDLGeneratorExtension w : extensions) {
            this.register(w);
        }
        this.extension = new WSDLGeneratorExtensionFacade((WSDLGeneratorExtension[])this.extensionHandlers.toArray(new WSDLGeneratorExtension[0]));
    }
    
    public void setEndpointAddress(final String address) {
        this.endpointAddress = address;
    }
    
    protected String mangleName(final String name) {
        return BindingHelper.mangleNameToClassName(name);
    }
    
    public void doGeneration() {
        XmlSerializer portWriter = null;
        final String fileName = this.mangleName(this.model.getServiceQName().getLocalPart());
        Result result = this.wsdlResolver.getWSDL(fileName + ".wsdl");
        this.wsdlLocation = result.getSystemId();
        final XmlSerializer serviceWriter = new CommentFilter(ResultFactory.createSerializer(result));
        if (this.model.getServiceQName().getNamespaceURI().equals(this.model.getTargetNamespace())) {
            portWriter = serviceWriter;
            this.schemaPrefix = fileName + "_";
        }
        else {
            String wsdlName = this.mangleName(this.model.getPortTypeName().getLocalPart());
            if (wsdlName.equals(fileName)) {
                wsdlName += "PortType";
            }
            final Holder<String> absWSDLName = new Holder<String>();
            absWSDLName.value = wsdlName + ".wsdl";
            result = this.wsdlResolver.getAbstractWSDL(absWSDLName);
            if (result != null) {
                this.portWSDLID = result.getSystemId();
                if (this.portWSDLID.equals(this.wsdlLocation)) {
                    portWriter = serviceWriter;
                }
                else {
                    portWriter = new CommentFilter(ResultFactory.createSerializer(result));
                }
            }
            else {
                this.portWSDLID = absWSDLName.value;
            }
            this.schemaPrefix = new File(this.portWSDLID).getName();
            final int idx = this.schemaPrefix.lastIndexOf(46);
            if (idx > 0) {
                this.schemaPrefix = this.schemaPrefix.substring(0, idx);
            }
            this.schemaPrefix = this.mangleName(this.schemaPrefix) + "_";
        }
        this.generateDocument(serviceWriter, portWriter);
    }
    
    private void generateDocument(final XmlSerializer serviceStream, final XmlSerializer portStream) {
        (this.serviceDefinitions = TXW.create(Definitions.class, serviceStream))._namespace("http://schemas.xmlsoap.org/wsdl/", "");
        this.serviceDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
        this.serviceDefinitions.targetNamespace(this.model.getServiceQName().getNamespaceURI());
        this.serviceDefinitions._namespace(this.model.getServiceQName().getNamespaceURI(), "tns");
        if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
            this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap12/", "soap12");
        }
        else {
            this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap/", "soap");
        }
        this.serviceDefinitions.name(this.model.getServiceQName().getLocalPart());
        final WSDLGenExtnContext serviceCtx = new WSDLGenExtnContext(this.serviceDefinitions, this.model, this.binding, this.container, this.implType);
        this.extension.start(serviceCtx);
        if (serviceStream != portStream && portStream != null) {
            (this.portDefinitions = TXW.create(Definitions.class, portStream))._namespace("http://schemas.xmlsoap.org/wsdl/", "");
            this.portDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
            if (this.model.getTargetNamespace() != null) {
                this.portDefinitions.targetNamespace(this.model.getTargetNamespace());
                this.portDefinitions._namespace(this.model.getTargetNamespace(), "tns");
            }
            final String schemaLoc = relativize(this.portWSDLID, this.wsdlLocation);
            final Import _import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
            _import.location(schemaLoc);
        }
        else if (portStream != null) {
            this.portDefinitions = this.serviceDefinitions;
        }
        else {
            final String schemaLoc = relativize(this.portWSDLID, this.wsdlLocation);
            final Import _import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
            _import.location(schemaLoc);
        }
        this.extension.addDefinitionsExtension(this.serviceDefinitions);
        if (this.portDefinitions != null) {
            this.generateTypes();
            this.generateMessages();
            this.generatePortType();
        }
        this.generateBinding();
        this.generateService();
        this.extension.end(serviceCtx);
        this.serviceDefinitions.commit();
        if (this.portDefinitions != null && this.portDefinitions != this.serviceDefinitions) {
            this.portDefinitions.commit();
        }
    }
    
    protected void generateTypes() {
        this.types = this.portDefinitions.types();
        if (this.model.getBindingContext() != null) {
            if (this.inlineSchemas && this.model.getBindingContext().getClass().getName().indexOf("glassfish") == -1) {
                this.resolver.nonGlassfishSchemas = new ArrayList<DOMResult>();
            }
            try {
                this.model.getBindingContext().generateSchema(this.resolver);
            }
            catch (final IOException e) {
                throw new WebServiceException(e.getMessage());
            }
        }
        if (this.resolver.nonGlassfishSchemas != null) {
            final TransformerFactory tf = XmlUtil.newTransformerFactory(!this.disableXmlSecurity);
            try {
                final Transformer t = tf.newTransformer();
                for (final DOMResult xsd : this.resolver.nonGlassfishSchemas) {
                    final Document doc = (Document)xsd.getNode();
                    final SAXResult sax = new SAXResult(new TXWContentHandler(this.types));
                    t.transform(new DOMSource(doc.getDocumentElement()), sax);
                }
            }
            catch (final TransformerConfigurationException e2) {
                throw new WebServiceException(e2.getMessage(), e2);
            }
            catch (final TransformerException e3) {
                throw new WebServiceException(e3.getMessage(), e3);
            }
        }
        this.generateWrappers();
    }
    
    void generateWrappers() {
        final List<WrapperParameter> wrappers = new ArrayList<WrapperParameter>();
        for (final JavaMethodImpl method : this.model.getJavaMethods()) {
            if (method.getBinding().isRpcLit()) {
                continue;
            }
            for (final ParameterImpl p : method.getRequestParameters()) {
                if (p instanceof WrapperParameter && WrapperComposite.class.equals(p.getTypeInfo().type)) {
                    wrappers.add((WrapperParameter)p);
                }
            }
            for (final ParameterImpl p : method.getResponseParameters()) {
                if (p instanceof WrapperParameter && WrapperComposite.class.equals(p.getTypeInfo().type)) {
                    wrappers.add((WrapperParameter)p);
                }
            }
        }
        if (wrappers.isEmpty()) {
            return;
        }
        final HashMap<String, Schema> xsds = new HashMap<String, Schema>();
        for (final WrapperParameter wp : wrappers) {
            final String tns = wp.getName().getNamespaceURI();
            Schema xsd = xsds.get(tns);
            if (xsd == null) {
                xsd = this.types.schema();
                xsd.targetNamespace(tns);
                xsds.put(tns, xsd);
            }
            final Element e = xsd._element(Element.class);
            e._attribute("name", wp.getName().getLocalPart());
            e.type(wp.getName());
            final ComplexType ct = xsd._element(ComplexType.class);
            ct._attribute("name", wp.getName().getLocalPart());
            final ExplicitGroup sq = ct.sequence();
            for (final ParameterImpl p2 : wp.getWrapperChildren()) {
                if (p2.getBinding().isBody()) {
                    final LocalElement le = sq.element();
                    le._attribute("name", p2.getName().getLocalPart());
                    TypeInfo typeInfo = p2.getItemType();
                    boolean repeatedElement = false;
                    if (typeInfo == null) {
                        typeInfo = p2.getTypeInfo();
                    }
                    else {
                        repeatedElement = true;
                    }
                    final QName type = this.model.getBindingContext().getTypeName(typeInfo);
                    le.type(type);
                    if (!repeatedElement) {
                        continue;
                    }
                    le.minOccurs(0);
                    le.maxOccurs("unbounded");
                }
            }
        }
    }
    
    protected void generateMessages() {
        for (final JavaMethodImpl method : this.model.getJavaMethods()) {
            this.generateSOAPMessages(method, method.getBinding());
        }
    }
    
    protected void generateSOAPMessages(final JavaMethodImpl method, final SOAPBinding binding) {
        final boolean isDoclit = binding.isDocLit();
        Message message = this.portDefinitions.message().name(method.getRequestMessageName());
        this.extension.addInputMessageExtension(message, method);
        final BindingContext jaxbContext = this.model.getBindingContext();
        boolean unwrappable = true;
        for (final ParameterImpl param : method.getRequestParameters()) {
            if (isDoclit) {
                if (this.isHeaderParameter(param)) {
                    unwrappable = false;
                }
                final Part part = message.part().name(param.getPartName());
                part.element(param.getName());
            }
            else if (param.isWrapperStyle()) {
                for (final ParameterImpl childParam : ((WrapperParameter)param).getWrapperChildren()) {
                    final Part part = message.part().name(childParam.getPartName());
                    part.type(jaxbContext.getTypeName(childParam.getXMLBridge().getTypeInfo()));
                }
            }
            else {
                final Part part = message.part().name(param.getPartName());
                part.element(param.getName());
            }
        }
        if (method.getMEP() != MEP.ONE_WAY) {
            message = this.portDefinitions.message().name(method.getResponseMessageName());
            this.extension.addOutputMessageExtension(message, method);
            for (final ParameterImpl param : method.getResponseParameters()) {
                if (isDoclit) {
                    final Part part = message.part().name(param.getPartName());
                    part.element(param.getName());
                }
                else if (param.isWrapperStyle()) {
                    for (final ParameterImpl childParam : ((WrapperParameter)param).getWrapperChildren()) {
                        final Part part = message.part().name(childParam.getPartName());
                        part.type(jaxbContext.getTypeName(childParam.getXMLBridge().getTypeInfo()));
                    }
                }
                else {
                    final Part part = message.part().name(param.getPartName());
                    part.element(param.getName());
                }
            }
        }
        for (final CheckedExceptionImpl exception : method.getCheckedExceptions()) {
            final QName tagName = exception.getDetailType().tagName;
            final String messageName = exception.getMessageName();
            final QName messageQName = new QName(this.model.getTargetNamespace(), messageName);
            if (this.processedExceptions.contains(messageQName)) {
                continue;
            }
            message = this.portDefinitions.message().name(messageName);
            this.extension.addFaultMessageExtension(message, method, exception);
            final Part part = message.part().name("fault");
            part.element(tagName);
            this.processedExceptions.add(messageQName);
        }
    }
    
    protected void generatePortType() {
        final PortType portType = this.portDefinitions.portType().name(this.model.getPortTypeName().getLocalPart());
        this.extension.addPortTypeExtension(portType);
        for (final JavaMethodImpl method : this.model.getJavaMethods()) {
            final Operation operation = portType.operation().name(method.getOperationName());
            this.generateParameterOrder(operation, method);
            this.extension.addOperationExtension(operation, method);
            switch (method.getMEP()) {
                case REQUEST_RESPONSE: {
                    this.generateInputMessage(operation, method);
                    this.generateOutputMessage(operation, method);
                    break;
                }
                case ONE_WAY: {
                    this.generateInputMessage(operation, method);
                    break;
                }
            }
            for (final CheckedExceptionImpl exception : method.getCheckedExceptions()) {
                final QName messageName = new QName(this.model.getTargetNamespace(), exception.getMessageName());
                final FaultType paramType = operation.fault().message(messageName).name(exception.getMessageName());
                this.extension.addOperationFaultExtension(paramType, method, exception);
            }
        }
    }
    
    protected boolean isWrapperStyle(final JavaMethodImpl method) {
        if (method.getRequestParameters().size() > 0) {
            final ParameterImpl param = method.getRequestParameters().iterator().next();
            return param.isWrapperStyle();
        }
        return false;
    }
    
    protected boolean isRpcLit(final JavaMethodImpl method) {
        return method.getBinding().getStyle() == javax.jws.soap.SOAPBinding.Style.RPC;
    }
    
    protected void generateParameterOrder(final Operation operation, final JavaMethodImpl method) {
        if (method.getMEP() == MEP.ONE_WAY) {
            return;
        }
        if (this.isRpcLit(method)) {
            this.generateRpcParameterOrder(operation, method);
        }
        else {
            this.generateDocumentParameterOrder(operation, method);
        }
    }
    
    protected void generateRpcParameterOrder(final Operation operation, final JavaMethodImpl method) {
        final StringBuilder paramOrder = new StringBuilder();
        final Set<String> partNames = new HashSet<String>();
        final List<ParameterImpl> sortedParams = this.sortMethodParameters(method);
        int i = 0;
        for (final ParameterImpl parameter : sortedParams) {
            if (parameter.getIndex() >= 0) {
                final String partName = parameter.getPartName();
                if (partNames.contains(partName)) {
                    continue;
                }
                if (i++ > 0) {
                    paramOrder.append(' ');
                }
                paramOrder.append(partName);
                partNames.add(partName);
            }
        }
        if (i > 1) {
            operation.parameterOrder(paramOrder.toString());
        }
    }
    
    protected void generateDocumentParameterOrder(final Operation operation, final JavaMethodImpl method) {
        final StringBuilder paramOrder = new StringBuilder();
        final Set<String> partNames = new HashSet<String>();
        final List<ParameterImpl> sortedParams = this.sortMethodParameters(method);
        int i = 0;
        for (final ParameterImpl parameter : sortedParams) {
            if (parameter.getIndex() < 0) {
                continue;
            }
            final String partName = parameter.getPartName();
            if (partNames.contains(partName)) {
                continue;
            }
            if (i++ > 0) {
                paramOrder.append(' ');
            }
            paramOrder.append(partName);
            partNames.add(partName);
        }
        if (i > 1) {
            operation.parameterOrder(paramOrder.toString());
        }
    }
    
    protected List<ParameterImpl> sortMethodParameters(final JavaMethodImpl method) {
        final Set<ParameterImpl> paramSet = new HashSet<ParameterImpl>();
        final List<ParameterImpl> sortedParams = new ArrayList<ParameterImpl>();
        if (this.isRpcLit(method)) {
            for (final ParameterImpl param : method.getRequestParameters()) {
                if (param instanceof WrapperParameter) {
                    paramSet.addAll(((WrapperParameter)param).getWrapperChildren());
                }
                else {
                    paramSet.add(param);
                }
            }
            for (final ParameterImpl param : method.getResponseParameters()) {
                if (param instanceof WrapperParameter) {
                    paramSet.addAll(((WrapperParameter)param).getWrapperChildren());
                }
                else {
                    paramSet.add(param);
                }
            }
        }
        else {
            paramSet.addAll(method.getRequestParameters());
            paramSet.addAll(method.getResponseParameters());
        }
        final Iterator<ParameterImpl> params = paramSet.iterator();
        if (paramSet.isEmpty()) {
            return sortedParams;
        }
        ParameterImpl param = params.next();
        sortedParams.add(param);
        for (int i = 1; i < paramSet.size(); ++i) {
            param = params.next();
            int pos;
            for (pos = 0; pos < i; ++pos) {
                final ParameterImpl sortedParam = sortedParams.get(pos);
                if (param.getIndex() == sortedParam.getIndex() && param instanceof WrapperParameter) {
                    break;
                }
                if (param.getIndex() < sortedParam.getIndex()) {
                    break;
                }
            }
            sortedParams.add(pos, param);
        }
        return sortedParams;
    }
    
    protected boolean isBodyParameter(final ParameterImpl parameter) {
        final ParameterBinding paramBinding = parameter.getBinding();
        return paramBinding.isBody();
    }
    
    protected boolean isHeaderParameter(final ParameterImpl parameter) {
        final ParameterBinding paramBinding = parameter.getBinding();
        return paramBinding.isHeader();
    }
    
    protected boolean isAttachmentParameter(final ParameterImpl parameter) {
        final ParameterBinding paramBinding = parameter.getBinding();
        return paramBinding.isAttachment();
    }
    
    protected void generateBinding() {
        final Binding newBinding = this.serviceDefinitions.binding().name(this.model.getBoundPortTypeName().getLocalPart());
        this.extension.addBindingExtension(newBinding);
        newBinding.type(this.model.getPortTypeName());
        boolean first = true;
        for (final JavaMethodImpl method : this.model.getJavaMethods()) {
            if (first) {
                final SOAPBinding sBinding = method.getBinding();
                final SOAPVersion soapVersion = sBinding.getSOAPVersion();
                if (soapVersion == SOAPVersion.SOAP_12) {
                    final com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding soapBinding = newBinding.soap12Binding();
                    soapBinding.transport(this.binding.getBindingId().getTransport());
                    if (sBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.DOCUMENT)) {
                        soapBinding.style("document");
                    }
                    else {
                        soapBinding.style("rpc");
                    }
                }
                else {
                    final com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding soapBinding2 = newBinding.soapBinding();
                    soapBinding2.transport(this.binding.getBindingId().getTransport());
                    if (sBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.DOCUMENT)) {
                        soapBinding2.style("document");
                    }
                    else {
                        soapBinding2.style("rpc");
                    }
                }
                first = false;
            }
            if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
                this.generateSOAP12BindingOperation(method, newBinding);
            }
            else {
                this.generateBindingOperation(method, newBinding);
            }
        }
    }
    
    protected void generateBindingOperation(final JavaMethodImpl method, final Binding binding) {
        final BindingOperationType operation = binding.operation().name(method.getOperationName());
        this.extension.addBindingOperationExtension(operation, method);
        final String targetNamespace = this.model.getTargetNamespace();
        final QName requestMessage = new QName(targetNamespace, method.getOperationName());
        final List<ParameterImpl> bodyParams = new ArrayList<ParameterImpl>();
        final List<ParameterImpl> headerParams = new ArrayList<ParameterImpl>();
        this.splitParameters(bodyParams, headerParams, method.getRequestParameters());
        final SOAPBinding soapBinding = method.getBinding();
        operation.soapOperation().soapAction(soapBinding.getSOAPAction());
        final TypedXmlWriter input = operation.input();
        this.extension.addBindingOperationInputExtension(input, method);
        BodyType body = input._element(Body.class);
        final boolean isRpc = soapBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.RPC);
        if (soapBinding.getUse() == javax.jws.soap.SOAPBinding.Use.LITERAL) {
            body.use("literal");
            if (headerParams.size() > 0) {
                if (bodyParams.size() > 0) {
                    final ParameterImpl param = bodyParams.iterator().next();
                    if (isRpc) {
                        final StringBuilder parts = new StringBuilder();
                        int i = 0;
                        for (final ParameterImpl parameter : ((WrapperParameter)param).getWrapperChildren()) {
                            if (i++ > 0) {
                                parts.append(' ');
                            }
                            parts.append(parameter.getPartName());
                        }
                        body.parts(parts.toString());
                    }
                    else {
                        body.parts(param.getPartName());
                    }
                }
                else {
                    body.parts("");
                }
                this.generateSOAPHeaders(input, headerParams, requestMessage);
            }
            if (isRpc) {
                body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
            }
            if (method.getMEP() != MEP.ONE_WAY) {
                bodyParams.clear();
                headerParams.clear();
                this.splitParameters(bodyParams, headerParams, method.getResponseParameters());
                final TypedXmlWriter output = operation.output();
                this.extension.addBindingOperationOutputExtension(output, method);
                body = output._element(Body.class);
                body.use("literal");
                if (headerParams.size() > 0) {
                    StringBuilder parts = new StringBuilder();
                    if (bodyParams.size() > 0) {
                        final ParameterImpl param2 = bodyParams.iterator().hasNext() ? bodyParams.iterator().next() : null;
                        if (param2 != null) {
                            if (isRpc) {
                                int j = 0;
                                for (final ParameterImpl parameter2 : ((WrapperParameter)param2).getWrapperChildren()) {
                                    if (j++ > 0) {
                                        parts.append(" ");
                                    }
                                    parts.append(parameter2.getPartName());
                                }
                            }
                            else {
                                parts = new StringBuilder(param2.getPartName());
                            }
                        }
                    }
                    body.parts(parts.toString());
                    final QName responseMessage = new QName(targetNamespace, method.getResponseMessageName());
                    this.generateSOAPHeaders(output, headerParams, responseMessage);
                }
                if (isRpc) {
                    body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
                }
            }
            for (final CheckedExceptionImpl exception : method.getCheckedExceptions()) {
                final Fault fault = operation.fault().name(exception.getMessageName());
                this.extension.addBindingOperationFaultExtension(fault, method, exception);
                final SOAPFault soapFault = fault._element(SOAPFault.class).name(exception.getMessageName());
                soapFault.use("literal");
            }
            return;
        }
        throw new WebServiceException("encoded use is not supported");
    }
    
    protected void generateSOAP12BindingOperation(final JavaMethodImpl method, final Binding binding) {
        final BindingOperationType operation = binding.operation().name(method.getOperationName());
        this.extension.addBindingOperationExtension(operation, method);
        final String targetNamespace = this.model.getTargetNamespace();
        final QName requestMessage = new QName(targetNamespace, method.getOperationName());
        final ArrayList<ParameterImpl> bodyParams = new ArrayList<ParameterImpl>();
        final ArrayList<ParameterImpl> headerParams = new ArrayList<ParameterImpl>();
        this.splitParameters(bodyParams, headerParams, method.getRequestParameters());
        final SOAPBinding soapBinding = method.getBinding();
        final String soapAction = soapBinding.getSOAPAction();
        if (soapAction != null) {
            operation.soap12Operation().soapAction(soapAction);
        }
        final TypedXmlWriter input = operation.input();
        this.extension.addBindingOperationInputExtension(input, method);
        com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType body = input._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
        final boolean isRpc = soapBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.RPC);
        if (soapBinding.getUse().equals(javax.jws.soap.SOAPBinding.Use.LITERAL)) {
            body.use("literal");
            if (headerParams.size() > 0) {
                if (bodyParams.size() > 0) {
                    final ParameterImpl param = bodyParams.iterator().next();
                    if (isRpc) {
                        final StringBuilder parts = new StringBuilder();
                        int i = 0;
                        for (final ParameterImpl parameter : ((WrapperParameter)param).getWrapperChildren()) {
                            if (i++ > 0) {
                                parts.append(' ');
                            }
                            parts.append(parameter.getPartName());
                        }
                        body.parts(parts.toString());
                    }
                    else {
                        body.parts(param.getPartName());
                    }
                }
                else {
                    body.parts("");
                }
                this.generateSOAP12Headers(input, headerParams, requestMessage);
            }
            if (isRpc) {
                body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
            }
            if (method.getMEP() != MEP.ONE_WAY) {
                bodyParams.clear();
                headerParams.clear();
                this.splitParameters(bodyParams, headerParams, method.getResponseParameters());
                final TypedXmlWriter output = operation.output();
                this.extension.addBindingOperationOutputExtension(output, method);
                body = output._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
                body.use("literal");
                if (headerParams.size() > 0) {
                    if (bodyParams.size() > 0) {
                        final ParameterImpl param2 = bodyParams.iterator().next();
                        if (isRpc) {
                            final StringBuilder parts2 = new StringBuilder();
                            int j = 0;
                            for (final ParameterImpl parameter2 : ((WrapperParameter)param2).getWrapperChildren()) {
                                if (j++ > 0) {
                                    parts2.append(" ");
                                }
                                parts2.append(parameter2.getPartName());
                            }
                            body.parts(parts2.toString());
                        }
                        else {
                            body.parts(param2.getPartName());
                        }
                    }
                    else {
                        body.parts("");
                    }
                    final QName responseMessage = new QName(targetNamespace, method.getResponseMessageName());
                    this.generateSOAP12Headers(output, headerParams, responseMessage);
                }
                if (isRpc) {
                    body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
                }
            }
            for (final CheckedExceptionImpl exception : method.getCheckedExceptions()) {
                final Fault fault = operation.fault().name(exception.getMessageName());
                this.extension.addBindingOperationFaultExtension(fault, method, exception);
                final com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault soapFault = fault._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault.class).name(exception.getMessageName());
                soapFault.use("literal");
            }
            return;
        }
        throw new WebServiceException("encoded use is not supported");
    }
    
    protected void splitParameters(final List<ParameterImpl> bodyParams, final List<ParameterImpl> headerParams, final List<ParameterImpl> params) {
        for (final ParameterImpl parameter : params) {
            if (this.isBodyParameter(parameter)) {
                bodyParams.add(parameter);
            }
            else {
                headerParams.add(parameter);
            }
        }
    }
    
    protected void generateSOAPHeaders(final TypedXmlWriter writer, final List<ParameterImpl> parameters, final QName message) {
        for (final ParameterImpl headerParam : parameters) {
            final Header header = writer._element(Header.class);
            header.message(message);
            header.part(headerParam.getPartName());
            header.use("literal");
        }
    }
    
    protected void generateSOAP12Headers(final TypedXmlWriter writer, final List<ParameterImpl> parameters, final QName message) {
        for (final ParameterImpl headerParam : parameters) {
            final com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header header = writer._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header.class);
            header.message(message);
            header.part(headerParam.getPartName());
            header.use("literal");
        }
    }
    
    protected void generateService() {
        final QName portQName = this.model.getPortName();
        final QName serviceQName = this.model.getServiceQName();
        final Service service = this.serviceDefinitions.service().name(serviceQName.getLocalPart());
        this.extension.addServiceExtension(service);
        final Port port = service.port().name(portQName.getLocalPart());
        port.binding(this.model.getBoundPortTypeName());
        this.extension.addPortExtension(port);
        if (this.model.getJavaMethods().isEmpty()) {
            return;
        }
        if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
            final SOAPAddress address = port._element(SOAPAddress.class);
            address.location(this.endpointAddress);
        }
        else {
            final com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress address2 = port._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress.class);
            address2.location(this.endpointAddress);
        }
    }
    
    protected void generateInputMessage(final Operation operation, final JavaMethodImpl method) {
        final ParamType paramType = operation.input();
        this.extension.addOperationInputExtension(paramType, method);
        paramType.message(new QName(this.model.getTargetNamespace(), method.getRequestMessageName()));
    }
    
    protected void generateOutputMessage(final Operation operation, final JavaMethodImpl method) {
        final ParamType paramType = operation.output();
        this.extension.addOperationOutputExtension(paramType, method);
        paramType.message(new QName(this.model.getTargetNamespace(), method.getResponseMessageName()));
    }
    
    public Result createOutputFile(final String namespaceUri, final String suggestedFileName) throws IOException {
        if (namespaceUri == null) {
            return null;
        }
        final Holder<String> fileNameHolder = new Holder<String>();
        fileNameHolder.value = this.schemaPrefix + suggestedFileName;
        final Result result = this.wsdlResolver.getSchemaOutput(namespaceUri, fileNameHolder);
        String schemaLoc;
        if (result == null) {
            schemaLoc = fileNameHolder.value;
        }
        else {
            schemaLoc = relativize(result.getSystemId(), this.wsdlLocation);
        }
        final boolean isEmptyNs = namespaceUri.trim().equals("");
        if (!isEmptyNs) {
            final com.sun.xml.internal.ws.wsdl.writer.document.xsd.Import _import = this.types.schema()._import();
            _import.namespace(namespaceUri);
            _import.schemaLocation(schemaLoc);
        }
        return result;
    }
    
    private Result createInlineSchema(final String namespaceUri, final String suggestedFileName) throws IOException {
        if (namespaceUri.equals("")) {
            return null;
        }
        final Result result = new TXWResult(this.types);
        result.setSystemId("");
        return result;
    }
    
    protected static String relativize(final String uri, final String baseUri) {
        try {
            assert uri != null;
            if (baseUri == null) {
                return uri;
            }
            final URI theUri = new URI(Util.escapeURI(uri));
            final URI theBaseUri = new URI(Util.escapeURI(baseUri));
            if (theUri.isOpaque() || theBaseUri.isOpaque()) {
                return uri;
            }
            if (!Util.equalsIgnoreCase(theUri.getScheme(), theBaseUri.getScheme()) || !Util.equal(theUri.getAuthority(), theBaseUri.getAuthority())) {
                return uri;
            }
            final String uriPath = theUri.getPath();
            String basePath = theBaseUri.getPath();
            if (!basePath.endsWith("/")) {
                basePath = Util.normalizeUriPath(basePath);
            }
            if (uriPath.equals(basePath)) {
                return ".";
            }
            final String relPath = calculateRelativePath(uriPath, basePath);
            if (relPath == null) {
                return uri;
            }
            final StringBuilder relUri = new StringBuilder();
            relUri.append(relPath);
            if (theUri.getQuery() != null) {
                relUri.append('?').append(theUri.getQuery());
            }
            if (theUri.getFragment() != null) {
                relUri.append('#').append(theUri.getFragment());
            }
            return relUri.toString();
        }
        catch (final URISyntaxException e) {
            throw new InternalError("Error escaping one of these uris:\n\t" + uri + "\n\t" + baseUri);
        }
    }
    
    private static String calculateRelativePath(final String uri, final String base) {
        if (base == null) {
            return null;
        }
        if (uri.startsWith(base)) {
            return uri.substring(base.length());
        }
        return "../" + calculateRelativePath(uri, Util.getParentUriPath(base));
    }
    
    private void register(final WSDLGeneratorExtension h) {
        this.extensionHandlers.add(h);
    }
    
    private static class CommentFilter implements XmlSerializer
    {
        final XmlSerializer serializer;
        private static final String VERSION_COMMENT;
        
        CommentFilter(final XmlSerializer serializer) {
            this.serializer = serializer;
        }
        
        @Override
        public void startDocument() {
            this.serializer.startDocument();
            this.comment(new StringBuilder(CommentFilter.VERSION_COMMENT));
            this.text(new StringBuilder("\n"));
        }
        
        @Override
        public void beginStartTag(final String uri, final String localName, final String prefix) {
            this.serializer.beginStartTag(uri, localName, prefix);
        }
        
        @Override
        public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
            this.serializer.writeAttribute(uri, localName, prefix, value);
        }
        
        @Override
        public void writeXmlns(final String prefix, final String uri) {
            this.serializer.writeXmlns(prefix, uri);
        }
        
        @Override
        public void endStartTag(final String uri, final String localName, final String prefix) {
            this.serializer.endStartTag(uri, localName, prefix);
        }
        
        @Override
        public void endTag() {
            this.serializer.endTag();
        }
        
        @Override
        public void text(final StringBuilder text) {
            this.serializer.text(text);
        }
        
        @Override
        public void cdata(final StringBuilder text) {
            this.serializer.cdata(text);
        }
        
        @Override
        public void comment(final StringBuilder comment) {
            this.serializer.comment(comment);
        }
        
        @Override
        public void endDocument() {
            this.serializer.endDocument();
        }
        
        @Override
        public void flush() {
            this.serializer.flush();
        }
        
        static {
            VERSION_COMMENT = " Generated by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
        }
    }
    
    protected class JAXWSOutputSchemaResolver extends SchemaOutputResolver
    {
        ArrayList<DOMResult> nonGlassfishSchemas;
        
        protected JAXWSOutputSchemaResolver() {
            this.nonGlassfishSchemas = null;
        }
        
        @Override
        public Result createOutput(final String namespaceUri, final String suggestedFileName) throws IOException {
            return WSDLGenerator.this.inlineSchemas ? ((this.nonGlassfishSchemas != null) ? this.nonGlassfishSchemaResult(namespaceUri, suggestedFileName) : WSDLGenerator.this.createInlineSchema(namespaceUri, suggestedFileName)) : WSDLGenerator.this.createOutputFile(namespaceUri, suggestedFileName);
        }
        
        private Result nonGlassfishSchemaResult(final String namespaceUri, final String suggestedFileName) throws IOException {
            final DOMResult result = new DOMResult();
            result.setSystemId("");
            this.nonGlassfishSchemas.add(result);
            return result;
        }
    }
}
