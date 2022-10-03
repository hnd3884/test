package com.sun.org.apache.xpath.internal.jaxp;

import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.traversal.NodeIterator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.parsers.DocumentBuilder;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import org.w3c.dom.Document;
import jdk.xml.internal.JdkXmlFeatures;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import javax.xml.xpath.XPath;

public class XPathImpl implements XPath
{
    private XPathVariableResolver variableResolver;
    private XPathFunctionResolver functionResolver;
    private XPathVariableResolver origVariableResolver;
    private XPathFunctionResolver origFunctionResolver;
    private NamespaceContext namespaceContext;
    private JAXPPrefixResolver prefixResolver;
    private boolean featureSecureProcessing;
    private boolean overrideDefaultParser;
    private final JdkXmlFeatures featureManager;
    private static Document d;
    
    XPathImpl(final XPathVariableResolver vr, final XPathFunctionResolver fr) {
        this(vr, fr, false, new JdkXmlFeatures(false));
    }
    
    XPathImpl(final XPathVariableResolver vr, final XPathFunctionResolver fr, final boolean featureSecureProcessing, final JdkXmlFeatures featureManager) {
        this.namespaceContext = null;
        this.featureSecureProcessing = false;
        this.overrideDefaultParser = true;
        this.variableResolver = vr;
        this.origVariableResolver = vr;
        this.functionResolver = fr;
        this.origFunctionResolver = fr;
        this.featureSecureProcessing = featureSecureProcessing;
        this.featureManager = featureManager;
        this.overrideDefaultParser = featureManager.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
    }
    
    @Override
    public void setXPathVariableResolver(final XPathVariableResolver resolver) {
        if (resolver == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathVariableResolver" });
            throw new NullPointerException(fmsg);
        }
        this.variableResolver = resolver;
    }
    
    @Override
    public XPathVariableResolver getXPathVariableResolver() {
        return this.variableResolver;
    }
    
    @Override
    public void setXPathFunctionResolver(final XPathFunctionResolver resolver) {
        if (resolver == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathFunctionResolver" });
            throw new NullPointerException(fmsg);
        }
        this.functionResolver = resolver;
    }
    
    @Override
    public XPathFunctionResolver getXPathFunctionResolver() {
        return this.functionResolver;
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext nsContext) {
        if (nsContext == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "NamespaceContext" });
            throw new NullPointerException(fmsg);
        }
        this.namespaceContext = nsContext;
        this.prefixResolver = new JAXPPrefixResolver(nsContext);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }
    
    private DocumentBuilder getParser() {
        try {
            final DocumentBuilderFactory dbf = JdkXmlUtils.getDOMFactory(this.overrideDefaultParser);
            return dbf.newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            throw new Error(e);
        }
    }
    
    private XObject eval(final String expression, final Object contextItem) throws TransformerException {
        final com.sun.org.apache.xpath.internal.XPath xpath = new com.sun.org.apache.xpath.internal.XPath(expression, null, this.prefixResolver, 0);
        XPathContext xpathSupport = null;
        if (this.functionResolver != null) {
            final JAXPExtensionsProvider jep = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing, this.featureManager);
            xpathSupport = new XPathContext(jep);
        }
        else {
            xpathSupport = new XPathContext();
        }
        XObject xobj = null;
        xpathSupport.setVarStack(new JAXPVariableStack(this.variableResolver));
        if (contextItem instanceof Node) {
            xobj = xpath.execute(xpathSupport, (Node)contextItem, this.prefixResolver);
        }
        else {
            xobj = xpath.execute(xpathSupport, -1, this.prefixResolver);
        }
        return xobj;
    }
    
    @Override
    public Object evaluate(final String expression, final Object item, final QName returnType) throws XPathExpressionException {
        if (expression == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
            throw new NullPointerException(fmsg);
        }
        if (returnType == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
            throw new NullPointerException(fmsg);
        }
        if (!this.isSupported(returnType)) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
            throw new IllegalArgumentException(fmsg);
        }
        try {
            final XObject resultObject = this.eval(expression, item);
            return this.getResultAsType(resultObject, returnType);
        }
        catch (final NullPointerException npe) {
            throw new XPathExpressionException(npe);
        }
        catch (final TransformerException te) {
            final Throwable nestedException = te.getException();
            if (nestedException instanceof XPathFunctionException) {
                throw (XPathFunctionException)nestedException;
            }
            throw new XPathExpressionException(te);
        }
    }
    
    private boolean isSupported(final QName returnType) {
        return returnType.equals(XPathConstants.STRING) || returnType.equals(XPathConstants.NUMBER) || returnType.equals(XPathConstants.BOOLEAN) || returnType.equals(XPathConstants.NODE) || returnType.equals(XPathConstants.NODESET);
    }
    
    private Object getResultAsType(final XObject resultObject, final QName returnType) throws TransformerException {
        if (returnType.equals(XPathConstants.STRING)) {
            return resultObject.str();
        }
        if (returnType.equals(XPathConstants.NUMBER)) {
            return new Double(resultObject.num());
        }
        if (returnType.equals(XPathConstants.BOOLEAN)) {
            return new Boolean(resultObject.bool());
        }
        if (returnType.equals(XPathConstants.NODESET)) {
            return resultObject.nodelist();
        }
        if (returnType.equals(XPathConstants.NODE)) {
            final NodeIterator ni = resultObject.nodeset();
            return ni.nextNode();
        }
        final String fmsg = XPATHMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
        throw new IllegalArgumentException(fmsg);
    }
    
    @Override
    public String evaluate(final String expression, final Object item) throws XPathExpressionException {
        return (String)this.evaluate(expression, item, XPathConstants.STRING);
    }
    
    @Override
    public XPathExpression compile(final String expression) throws XPathExpressionException {
        if (expression == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
            throw new NullPointerException(fmsg);
        }
        try {
            final com.sun.org.apache.xpath.internal.XPath xpath = new com.sun.org.apache.xpath.internal.XPath(expression, null, this.prefixResolver, 0);
            final XPathExpressionImpl ximpl = new XPathExpressionImpl(xpath, this.prefixResolver, this.functionResolver, this.variableResolver, this.featureSecureProcessing, this.featureManager);
            return ximpl;
        }
        catch (final TransformerException te) {
            throw new XPathExpressionException(te);
        }
    }
    
    @Override
    public Object evaluate(final String expression, final InputSource source, final QName returnType) throws XPathExpressionException {
        if (source == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "source" });
            throw new NullPointerException(fmsg);
        }
        if (expression == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
            throw new NullPointerException(fmsg);
        }
        if (returnType == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
            throw new NullPointerException(fmsg);
        }
        if (!this.isSupported(returnType)) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
            throw new IllegalArgumentException(fmsg);
        }
        try {
            final Document document = this.getParser().parse(source);
            final XObject resultObject = this.eval(expression, document);
            return this.getResultAsType(resultObject, returnType);
        }
        catch (final SAXException e) {
            throw new XPathExpressionException(e);
        }
        catch (final IOException e2) {
            throw new XPathExpressionException(e2);
        }
        catch (final TransformerException te) {
            final Throwable nestedException = te.getException();
            if (nestedException instanceof XPathFunctionException) {
                throw (XPathFunctionException)nestedException;
            }
            throw new XPathExpressionException(te);
        }
    }
    
    @Override
    public String evaluate(final String expression, final InputSource source) throws XPathExpressionException {
        return (String)this.evaluate(expression, source, XPathConstants.STRING);
    }
    
    @Override
    public void reset() {
        this.variableResolver = this.origVariableResolver;
        this.functionResolver = this.origFunctionResolver;
        this.namespaceContext = null;
    }
    
    static {
        XPathImpl.d = null;
    }
}
