package com.adventnet.webclient.util;

import java.util.Hashtable;
import org.w3c.dom.NamedNodeMap;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.io.File;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Vector;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.webclient.ClientException;
import java.util.Properties;

public class FrameWorkUtil
{
    private static FrameWorkUtil frameWorkUtil;
    private Properties dtdMappings;
    
    private FrameWorkUtil() {
        this.dtdMappings = new Properties();
    }
    
    public void registerDtdMapping(final String publicId, String location) throws ClientException {
        if (!this.dtdMappings.containsKey(publicId)) {
            if (!location.startsWith("/")) {
                location = "/" + location;
            }
            this.dtdMappings.setProperty(publicId, location);
        }
    }
    
    public Properties getDtdMappings() {
        return this.dtdMappings;
    }
    
    public static FrameWorkUtil getInstance() {
        if (FrameWorkUtil.frameWorkUtil == null) {
            FrameWorkUtil.frameWorkUtil = new FrameWorkUtil();
        }
        return FrameWorkUtil.frameWorkUtil;
    }
    
    public Properties getParameters(final HttpServletRequest request) {
        final Properties parameters = new Properties();
        final Enumeration enum1 = request.getParameterNames();
        while (enum1.hasMoreElements()) {
            final String param = enum1.nextElement();
            final String value = request.getParameter(param);
            if (value != null && !value.equalsIgnoreCase("null") && !value.equalsIgnoreCase("")) {
                ((Hashtable<String, String>)parameters).put(param, value);
            }
        }
        return parameters;
    }
    
    public Vector getExceptionsThrown(final String className, final String methodName) throws ClientException {
        final Vector exceptionsThrown = new Vector();
        Class[] exceptionTypes = null;
        Class cls = null;
        try {
            cls = Class.forName(className);
        }
        catch (final ClassNotFoundException e) {
            throw new ClientException("ClassNotFoundException " + e.getMessage());
        }
        final Method[] methods = cls.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methods[i].getName().equals(methodName)) {
                exceptionTypes = methods[i].getExceptionTypes();
            }
        }
        if (exceptionTypes != null) {
            for (int i = 0; i < exceptionTypes.length; ++i) {
                exceptionsThrown.addElement(exceptionTypes[i].getName());
            }
        }
        return exceptionsThrown;
    }
    
    public Element getRootElement(final InputStream xmlStream, final InputStream dtdStream, final boolean validation) throws ClientException {
        final DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        domBuilderFactory.setValidating(validation);
        try {
            domBuilder = domBuilderFactory.newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            throw new ClientException("Exception while getting reference to DocumentBuilder", e);
        }
        Document document = null;
        Element rootElement = null;
        if (xmlStream == null) {
            throw new ClientException("Illegal Argument");
        }
        try {
            EntityResolver entityResolver = null;
            if (dtdStream != null) {
                entityResolver = new EntityResolverImpl(dtdStream, this.dtdMappings);
            }
            else {
                entityResolver = new EntityResolverImpl(this.dtdMappings);
            }
            domBuilder.setEntityResolver(entityResolver);
            final ErrorHandler errHandler = new ErrorHandlerImpl();
            domBuilder.setErrorHandler(errHandler);
            document = domBuilder.parse(xmlStream);
        }
        catch (final SAXException e2) {
            throw new ClientException("SAXException while parsing the document", e2);
        }
        catch (final IOException e3) {
            throw new ClientException("IOException while parsing the document", e3);
        }
        rootElement = document.getDocumentElement();
        rootElement.normalize();
        return rootElement;
    }
    
    public Element getRootElement(final String absoluteFileName, final String dtdFileName, final boolean validation) throws ClientException {
        final DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        domBuilderFactory.setValidating(validation);
        try {
            domBuilder = domBuilderFactory.newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            throw new ClientException("Exception while getting reference to DocumentBuilder", e);
        }
        Document document = null;
        Element rootElement = null;
        final File file = new File(absoluteFileName);
        try {
            EntityResolver entityResolver = null;
            if (dtdFileName != null) {
                entityResolver = new EntityResolverImpl(dtdFileName);
            }
            else {
                entityResolver = new EntityResolverImpl(this.dtdMappings);
            }
            domBuilder.setEntityResolver(entityResolver);
            final ErrorHandler errHandler = new ErrorHandlerImpl();
            domBuilder.setErrorHandler(errHandler);
            document = domBuilder.parse(file);
        }
        catch (final SAXException e2) {
            throw new ClientException("SAXException while parsing the document", e2);
        }
        catch (final IOException e3) {
            throw new ClientException("IOException while parsing the document", e3);
        }
        rootElement = document.getDocumentElement();
        rootElement.normalize();
        return rootElement;
    }
    
    public void writeXml(final String absoluteFileName, final Element rootElement, final Properties outputProperties) throws ClientException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        }
        catch (final TransformerConfigurationException e) {
            throw new ClientException("Exception while getting reference to Transformer", e);
        }
        transformer.setOutputProperty("indent", "yes");
        final ErrorListener errorListener = new ErrorListenerImpl();
        transformer.setErrorListener(errorListener);
        final File file = new File(absoluteFileName);
        if (outputProperties != null) {
            final Enumeration propertyNames = outputProperties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                final String propertyName = propertyNames.nextElement();
                final String propertyValue = outputProperties.getProperty(propertyName);
                transformer.setOutputProperty(propertyName, propertyValue);
            }
        }
        final DOMSource domSource = new DOMSource(rootElement);
        final StreamResult streamResult = new StreamResult(file);
        try {
            transformer.transform(domSource, streamResult);
        }
        catch (final TransformerException e2) {
            throw new ClientException(e2.getMessage());
        }
    }
    
    public Properties getAllAttributes(final Node node) {
        if (node == null) {
            return null;
        }
        final Properties attributes = new Properties();
        final NamedNodeMap nodeMap = node.getAttributes();
        for (int len = nodeMap.getLength(), j = 0; j < len; ++j) {
            final Node attributeNode = nodeMap.item(j);
            final String attributeName = attributeNode.getNodeName();
            final String attributeValue = attributeNode.getNodeValue();
            attributes.setProperty(attributeName, attributeValue);
        }
        return attributes;
    }
    
    public Object createInstance(final String className) throws ClientException {
        Class klass = null;
        Object obj = null;
        try {
            final Thread currentThread = Thread.currentThread();
            final ClassLoader loader = currentThread.getContextClassLoader();
            klass = loader.loadClass(className);
        }
        catch (final ClassNotFoundException e) {
            final ClientException ce = new ClientException("ClassNotFoundException : " + e.getMessage());
            throw ce;
        }
        try {
            obj = klass.newInstance();
        }
        catch (final InstantiationException e2) {
            final ClientException ce = new ClientException("InstantiationException : " + e2.getMessage());
            throw ce;
        }
        catch (final IllegalAccessException e3) {
            final ClientException ce = new ClientException("IllegalAccessException : " + e3.getMessage());
            throw ce;
        }
        return obj;
    }
    
    static {
        FrameWorkUtil.frameWorkUtil = getInstance();
    }
}
