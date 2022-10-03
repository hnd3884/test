package com.sun.org.apache.xpath.internal.jaxp;

import org.w3c.dom.traversal.NodeIterator;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathExpressionException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import org.w3c.dom.Node;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jdk.xml.internal.JdkXmlFeatures;
import com.sun.org.apache.xpath.internal.XPath;
import javax.xml.xpath.XPathVariableResolver;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathExpression;

public class XPathExpressionImpl implements XPathExpression
{
    private XPathFunctionResolver functionResolver;
    private XPathVariableResolver variableResolver;
    private JAXPPrefixResolver prefixResolver;
    private XPath xpath;
    private boolean featureSecureProcessing;
    boolean overrideDefaultParser;
    private final JdkXmlFeatures featureManager;
    static DocumentBuilderFactory dbf;
    static DocumentBuilder db;
    static Document d;
    
    protected XPathExpressionImpl() {
        this(null, null, null, null, false, new JdkXmlFeatures(false));
    }
    
    protected XPathExpressionImpl(final XPath xpath, final JAXPPrefixResolver prefixResolver, final XPathFunctionResolver functionResolver, final XPathVariableResolver variableResolver) {
        this(xpath, prefixResolver, functionResolver, variableResolver, false, new JdkXmlFeatures(false));
    }
    
    protected XPathExpressionImpl(final XPath xpath, final JAXPPrefixResolver prefixResolver, final XPathFunctionResolver functionResolver, final XPathVariableResolver variableResolver, final boolean featureSecureProcessing, final JdkXmlFeatures featureManager) {
        this.featureSecureProcessing = false;
        this.xpath = xpath;
        this.prefixResolver = prefixResolver;
        this.functionResolver = functionResolver;
        this.variableResolver = variableResolver;
        this.featureSecureProcessing = featureSecureProcessing;
        this.featureManager = featureManager;
        this.overrideDefaultParser = featureManager.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
    }
    
    public void setXPath(final XPath xpath) {
        this.xpath = xpath;
    }
    
    public Object eval(final Object item, final QName returnType) throws TransformerException {
        final XObject resultObject = this.eval(item);
        return this.getResultAsType(resultObject, returnType);
    }
    
    private XObject eval(final Object contextItem) throws TransformerException {
        XPathContext xpathSupport = null;
        if (this.functionResolver != null) {
            final JAXPExtensionsProvider jep = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing, this.featureManager);
            xpathSupport = new XPathContext(jep);
        }
        else {
            xpathSupport = new XPathContext();
        }
        xpathSupport.setVarStack(new JAXPVariableStack(this.variableResolver));
        XObject xobj = null;
        final Node contextNode = (Node)contextItem;
        if (contextNode == null) {
            xobj = this.xpath.execute(xpathSupport, -1, this.prefixResolver);
        }
        else {
            xobj = this.xpath.execute(xpathSupport, contextNode, this.prefixResolver);
        }
        return xobj;
    }
    
    @Override
    public Object evaluate(final Object item, final QName returnType) throws XPathExpressionException {
        if (returnType == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
            throw new NullPointerException(fmsg);
        }
        if (!this.isSupported(returnType)) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
            throw new IllegalArgumentException(fmsg);
        }
        try {
            return this.eval(item, returnType);
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
    
    @Override
    public String evaluate(final Object item) throws XPathExpressionException {
        return (String)this.evaluate(item, XPathConstants.STRING);
    }
    
    @Override
    public Object evaluate(final InputSource source, final QName returnType) throws XPathExpressionException {
        if (source == null || returnType == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL", null);
            throw new NullPointerException(fmsg);
        }
        if (!this.isSupported(returnType)) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
            throw new IllegalArgumentException(fmsg);
        }
        try {
            if (XPathExpressionImpl.dbf == null) {
                XPathExpressionImpl.dbf = JdkXmlUtils.getDOMFactory(this.overrideDefaultParser);
            }
            XPathExpressionImpl.db = XPathExpressionImpl.dbf.newDocumentBuilder();
            final Document document = XPathExpressionImpl.db.parse(source);
            return this.eval(document, returnType);
        }
        catch (final Exception e) {
            throw new XPathExpressionException(e);
        }
    }
    
    @Override
    public String evaluate(final InputSource source) throws XPathExpressionException {
        return (String)this.evaluate(source, XPathConstants.STRING);
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
    
    static {
        XPathExpressionImpl.dbf = null;
        XPathExpressionImpl.db = null;
        XPathExpressionImpl.d = null;
    }
}
