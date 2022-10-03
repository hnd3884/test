package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.util.UtilException;
import java.util.Iterator;
import java.util.Collection;
import javax.xml.ws.handler.PortInfo;
import java.lang.reflect.Method;
import java.util.Set;
import javax.annotation.PostConstruct;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import java.util.HashSet;
import javax.xml.ws.handler.Handler;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.StringTokenizer;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.logging.Logger;

public class HandlerChainsModel
{
    private static final Logger logger;
    private Class annotatedClass;
    private List<HandlerChainType> handlerChains;
    private String id;
    public static final String PROTOCOL_SOAP11_TOKEN = "##SOAP11_HTTP";
    public static final String PROTOCOL_SOAP12_TOKEN = "##SOAP12_HTTP";
    public static final String PROTOCOL_XML_TOKEN = "##XML_HTTP";
    public static final String NS_109 = "http://java.sun.com/xml/ns/javaee";
    public static final QName QNAME_CHAIN_PORT_PATTERN;
    public static final QName QNAME_CHAIN_PROTOCOL_BINDING;
    public static final QName QNAME_CHAIN_SERVICE_PATTERN;
    public static final QName QNAME_HANDLER_CHAIN;
    public static final QName QNAME_HANDLER_CHAINS;
    public static final QName QNAME_HANDLER;
    public static final QName QNAME_HANDLER_NAME;
    public static final QName QNAME_HANDLER_CLASS;
    public static final QName QNAME_HANDLER_PARAM;
    public static final QName QNAME_HANDLER_PARAM_NAME;
    public static final QName QNAME_HANDLER_PARAM_VALUE;
    public static final QName QNAME_HANDLER_HEADER;
    public static final QName QNAME_HANDLER_ROLE;
    
    private HandlerChainsModel(final Class annotatedClass) {
        this.annotatedClass = annotatedClass;
    }
    
    private List<HandlerChainType> getHandlerChain() {
        if (this.handlerChains == null) {
            this.handlerChains = new ArrayList<HandlerChainType>();
        }
        return this.handlerChains;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String value) {
        this.id = value;
    }
    
    public static HandlerChainsModel parseHandlerConfigFile(final Class annotatedClass, final XMLStreamReader reader) {
        ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER_CHAINS);
        final HandlerChainsModel handlerModel = new HandlerChainsModel(annotatedClass);
        final List<HandlerChainType> hChains = handlerModel.getHandlerChain();
        XMLStreamReaderUtil.nextElementContent(reader);
        while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAIN)) {
            final HandlerChainType hChain = new HandlerChainType();
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getName().equals(HandlerChainsModel.QNAME_CHAIN_PORT_PATTERN)) {
                final QName portNamePattern = XMLStreamReaderUtil.getElementQName(reader);
                hChain.setPortNamePattern(portNamePattern);
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            else if (reader.getName().equals(HandlerChainsModel.QNAME_CHAIN_PROTOCOL_BINDING)) {
                final String bindingList = XMLStreamReaderUtil.getElementText(reader);
                final StringTokenizer stk = new StringTokenizer(bindingList);
                while (stk.hasMoreTokens()) {
                    final String token = stk.nextToken();
                    hChain.addProtocolBinding(token);
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            else if (reader.getName().equals(HandlerChainsModel.QNAME_CHAIN_SERVICE_PATTERN)) {
                final QName serviceNamepattern = XMLStreamReaderUtil.getElementQName(reader);
                hChain.setServiceNamePattern(serviceNamepattern);
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            final List<HandlerType> handlers = hChain.getHandlers();
            while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER)) {
                final HandlerType handler = new HandlerType();
                XMLStreamReaderUtil.nextContent(reader);
                if (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_NAME)) {
                    final String handlerName = XMLStreamReaderUtil.getElementText(reader).trim();
                    handler.setHandlerName(handlerName);
                    XMLStreamReaderUtil.nextContent(reader);
                }
                ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER_CLASS);
                final String handlerClass = XMLStreamReaderUtil.getElementText(reader).trim();
                handler.setHandlerClass(handlerClass);
                XMLStreamReaderUtil.nextContent(reader);
                while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_PARAM)) {
                    skipInitParamElement(reader);
                }
                while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_HEADER)) {
                    skipTextElement(reader);
                }
                while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_ROLE)) {
                    final List<String> soapRoles = handler.getSoapRoles();
                    soapRoles.add(XMLStreamReaderUtil.getElementText(reader));
                    XMLStreamReaderUtil.nextContent(reader);
                }
                handlers.add(handler);
                ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER);
                XMLStreamReaderUtil.nextContent(reader);
            }
            ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER_CHAIN);
            hChains.add(hChain);
            XMLStreamReaderUtil.nextContent(reader);
        }
        return handlerModel;
    }
    
    public static HandlerAnnotationInfo parseHandlerFile(final XMLStreamReader reader, final ClassLoader classLoader, final QName serviceName, final QName portName, final WSBinding wsbinding) {
        ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER_CHAINS);
        final String bindingId = wsbinding.getBindingId().toString();
        final HandlerAnnotationInfo info = new HandlerAnnotationInfo();
        XMLStreamReaderUtil.nextElementContent(reader);
        final List<Handler> handlerChain = new ArrayList<Handler>();
        final Set<String> roles = new HashSet<String>();
        while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAIN)) {
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getName().equals(HandlerChainsModel.QNAME_CHAIN_PORT_PATTERN)) {
                if (portName == null) {
                    HandlerChainsModel.logger.warning("handler chain sepcified for port but port QName passed to parser is null");
                }
                final boolean parseChain = JAXWSUtils.matchQNames(portName, XMLStreamReaderUtil.getElementQName(reader));
                if (!parseChain) {
                    skipChain(reader);
                    continue;
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            else if (reader.getName().equals(HandlerChainsModel.QNAME_CHAIN_PROTOCOL_BINDING)) {
                if (bindingId == null) {
                    HandlerChainsModel.logger.warning("handler chain sepcified for bindingId but bindingId passed to parser is null");
                }
                final String bindingConstraint = XMLStreamReaderUtil.getElementText(reader);
                boolean skipThisChain = true;
                final StringTokenizer stk = new StringTokenizer(bindingConstraint);
                final List<String> bindingList = new ArrayList<String>();
                while (stk.hasMoreTokens()) {
                    String tokenOrURI = stk.nextToken();
                    tokenOrURI = DeploymentDescriptorParser.getBindingIdForToken(tokenOrURI);
                    final String binding = BindingID.parse(tokenOrURI).toString();
                    bindingList.add(binding);
                }
                if (bindingList.contains(bindingId)) {
                    skipThisChain = false;
                }
                if (skipThisChain) {
                    skipChain(reader);
                    continue;
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            else if (reader.getName().equals(HandlerChainsModel.QNAME_CHAIN_SERVICE_PATTERN)) {
                if (serviceName == null) {
                    HandlerChainsModel.logger.warning("handler chain sepcified for service but service QName passed to parser is null");
                }
                final boolean parseChain = JAXWSUtils.matchQNames(serviceName, XMLStreamReaderUtil.getElementQName(reader));
                if (!parseChain) {
                    skipChain(reader);
                    continue;
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER)) {
                XMLStreamReaderUtil.nextContent(reader);
                if (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_NAME)) {
                    skipTextElement(reader);
                }
                ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER_CLASS);
                Handler handler;
                try {
                    handler = loadClass(classLoader, XMLStreamReaderUtil.getElementText(reader).trim()).newInstance();
                }
                catch (final InstantiationException ie) {
                    throw new RuntimeException(ie);
                }
                catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                XMLStreamReaderUtil.nextContent(reader);
                while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_PARAM)) {
                    skipInitParamElement(reader);
                }
                while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_HEADER)) {
                    skipTextElement(reader);
                }
                while (reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_ROLE)) {
                    roles.add(XMLStreamReaderUtil.getElementText(reader));
                    XMLStreamReaderUtil.nextContent(reader);
                }
                for (final Method method : handler.getClass().getMethods()) {
                    if (method.getAnnotation(PostConstruct.class) != null) {
                        try {
                            method.invoke(handler, new Object[0]);
                            break;
                        }
                        catch (final Exception e2) {
                            throw new RuntimeException(e2);
                        }
                    }
                }
                handlerChain.add(handler);
                ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER);
                XMLStreamReaderUtil.nextContent(reader);
            }
            ensureProperName(reader, HandlerChainsModel.QNAME_HANDLER_CHAIN);
            XMLStreamReaderUtil.nextContent(reader);
        }
        info.setHandlers(handlerChain);
        info.setRoles(roles);
        return info;
    }
    
    public HandlerAnnotationInfo getHandlersForPortInfo(final PortInfo info) {
        final HandlerAnnotationInfo handlerInfo = new HandlerAnnotationInfo();
        final List<Handler> handlerClassList = new ArrayList<Handler>();
        final Set<String> roles = new HashSet<String>();
        for (final HandlerChainType hchain : this.handlerChains) {
            boolean hchainMatched = false;
            if (!hchain.isConstraintSet() || JAXWSUtils.matchQNames(info.getServiceName(), hchain.getServiceNamePattern()) || JAXWSUtils.matchQNames(info.getPortName(), hchain.getPortNamePattern()) || hchain.getProtocolBindings().contains(info.getBindingID())) {
                hchainMatched = true;
            }
            if (hchainMatched) {
                for (final HandlerType handler : hchain.getHandlers()) {
                    try {
                        final Handler handlerClass = loadClass(this.annotatedClass.getClassLoader(), handler.getHandlerClass()).newInstance();
                        callHandlerPostConstruct(handlerClass);
                        handlerClassList.add(handlerClass);
                    }
                    catch (final InstantiationException ie) {
                        throw new RuntimeException(ie);
                    }
                    catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    roles.addAll(handler.getSoapRoles());
                }
            }
        }
        handlerInfo.setHandlers(handlerClassList);
        handlerInfo.setRoles(roles);
        return handlerInfo;
    }
    
    private static Class loadClass(final ClassLoader loader, final String name) {
        try {
            return Class.forName(name, true, loader);
        }
        catch (final ClassNotFoundException e) {
            throw new UtilException("util.handler.class.not.found", new Object[] { name });
        }
    }
    
    private static void callHandlerPostConstruct(final Object handlerClass) {
        for (final Method method : handlerClass.getClass().getMethods()) {
            if (method.getAnnotation(PostConstruct.class) != null) {
                try {
                    method.invoke(handlerClass, new Object[0]);
                    break;
                }
                catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    private static void skipChain(final XMLStreamReader reader) {
        while (true) {
            if (XMLStreamReaderUtil.nextContent(reader) == 2) {
                if (!reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAIN)) {
                    continue;
                }
                break;
            }
        }
        XMLStreamReaderUtil.nextElementContent(reader);
    }
    
    private static void skipTextElement(final XMLStreamReader reader) {
        XMLStreamReaderUtil.nextContent(reader);
        XMLStreamReaderUtil.nextElementContent(reader);
        XMLStreamReaderUtil.nextElementContent(reader);
    }
    
    private static void skipInitParamElement(final XMLStreamReader reader) {
        int state;
        do {
            state = XMLStreamReaderUtil.nextContent(reader);
        } while (state != 2 || !reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_PARAM));
        XMLStreamReaderUtil.nextElementContent(reader);
    }
    
    private static void ensureProperName(final XMLStreamReader reader, final QName expectedName) {
        if (!reader.getName().equals(expectedName)) {
            failWithLocalName("util.parser.wrong.element", reader, expectedName.getLocalPart());
        }
    }
    
    static void ensureProperName(final XMLStreamReader reader, final String expectedName) {
        if (!reader.getLocalName().equals(expectedName)) {
            failWithLocalName("util.parser.wrong.element", reader, expectedName);
        }
    }
    
    private static void failWithLocalName(final String key, final XMLStreamReader reader, final String arg) {
        throw new UtilException(key, new Object[] { Integer.toString(reader.getLocation().getLineNumber()), reader.getLocalName(), arg });
    }
    
    static {
        logger = Logger.getLogger("com.sun.xml.internal.ws.util");
        QNAME_CHAIN_PORT_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "port-name-pattern");
        QNAME_CHAIN_PROTOCOL_BINDING = new QName("http://java.sun.com/xml/ns/javaee", "protocol-bindings");
        QNAME_CHAIN_SERVICE_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "service-name-pattern");
        QNAME_HANDLER_CHAIN = new QName("http://java.sun.com/xml/ns/javaee", "handler-chain");
        QNAME_HANDLER_CHAINS = new QName("http://java.sun.com/xml/ns/javaee", "handler-chains");
        QNAME_HANDLER = new QName("http://java.sun.com/xml/ns/javaee", "handler");
        QNAME_HANDLER_NAME = new QName("http://java.sun.com/xml/ns/javaee", "handler-name");
        QNAME_HANDLER_CLASS = new QName("http://java.sun.com/xml/ns/javaee", "handler-class");
        QNAME_HANDLER_PARAM = new QName("http://java.sun.com/xml/ns/javaee", "init-param");
        QNAME_HANDLER_PARAM_NAME = new QName("http://java.sun.com/xml/ns/javaee", "param-name");
        QNAME_HANDLER_PARAM_VALUE = new QName("http://java.sun.com/xml/ns/javaee", "param-value");
        QNAME_HANDLER_HEADER = new QName("http://java.sun.com/xml/ns/javaee", "soap-header");
        QNAME_HANDLER_ROLE = new QName("http://java.sun.com/xml/ns/javaee", "soap-role");
    }
    
    static class HandlerChainType
    {
        QName serviceNamePattern;
        QName portNamePattern;
        List<String> protocolBindings;
        boolean constraintSet;
        List<HandlerType> handlers;
        String id;
        
        public HandlerChainType() {
            this.constraintSet = false;
            this.protocolBindings = new ArrayList<String>();
        }
        
        public void setServiceNamePattern(final QName value) {
            this.serviceNamePattern = value;
            this.constraintSet = true;
        }
        
        public QName getServiceNamePattern() {
            return this.serviceNamePattern;
        }
        
        public void setPortNamePattern(final QName value) {
            this.portNamePattern = value;
            this.constraintSet = true;
        }
        
        public QName getPortNamePattern() {
            return this.portNamePattern;
        }
        
        public List<String> getProtocolBindings() {
            return this.protocolBindings;
        }
        
        public void addProtocolBinding(String tokenOrURI) {
            tokenOrURI = DeploymentDescriptorParser.getBindingIdForToken(tokenOrURI);
            final String binding = BindingID.parse(tokenOrURI).toString();
            this.protocolBindings.add(binding);
            this.constraintSet = true;
        }
        
        public boolean isConstraintSet() {
            return this.constraintSet || !this.protocolBindings.isEmpty();
        }
        
        public String getId() {
            return this.id;
        }
        
        public void setId(final String value) {
            this.id = value;
        }
        
        public List<HandlerType> getHandlers() {
            if (this.handlers == null) {
                this.handlers = new ArrayList<HandlerType>();
            }
            return this.handlers;
        }
    }
    
    static class HandlerType
    {
        String handlerName;
        String handlerClass;
        List<String> soapRoles;
        String id;
        
        public HandlerType() {
        }
        
        public String getHandlerName() {
            return this.handlerName;
        }
        
        public void setHandlerName(final String value) {
            this.handlerName = value;
        }
        
        public String getHandlerClass() {
            return this.handlerClass;
        }
        
        public void setHandlerClass(final String value) {
            this.handlerClass = value;
        }
        
        public String getId() {
            return this.id;
        }
        
        public void setId(final String value) {
            this.id = value;
        }
        
        public List<String> getSoapRoles() {
            if (this.soapRoles == null) {
                this.soapRoles = new ArrayList<String>();
            }
            return this.soapRoles;
        }
    }
}
