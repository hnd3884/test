package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.lang.reflect.Method;
import com.sun.org.apache.xml.internal.security.transforms.implementations.FuncHere;
import com.sun.org.apache.xpath.internal.Expression;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import org.w3c.dom.Document;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.slf4j.internal.Logger;

public class XalanXPathAPI implements XPathAPI
{
    private static final Logger LOG;
    private String xpathStr;
    private XPath xpath;
    private static FunctionTable funcTable;
    private static boolean installed;
    private XPathContext context;
    
    @Override
    public NodeList selectNodeList(final Node node, final Node node2, final String s, final Node node3) throws TransformerException {
        return this.eval(node, node2, s, node3).nodelist();
    }
    
    @Override
    public boolean evaluate(final Node node, final Node node2, final String s, final Node node3) throws TransformerException {
        return this.eval(node, node2, s, node3).bool();
    }
    
    @Override
    public void clear() {
        this.xpathStr = null;
        this.xpath = null;
        this.context = null;
    }
    
    public static synchronized boolean isInstalled() {
        return XalanXPathAPI.installed;
    }
    
    private XObject eval(final Node node, final Node owner, final String xpathStr, final Node node2) throws TransformerException {
        if (this.context == null) {
            (this.context = new XPathContext(owner)).setSecureProcessing(true);
        }
        final PrefixResolverDefault namespaceContext = new PrefixResolverDefault((node2.getNodeType() == 9) ? ((Document)node2).getDocumentElement() : node2);
        if (!xpathStr.equals(this.xpathStr)) {
            if (xpathStr.indexOf("here()") > 0) {
                this.context.reset();
            }
            this.xpath = this.createXPath(xpathStr, namespaceContext);
            this.xpathStr = xpathStr;
        }
        return this.xpath.execute(this.context, this.context.getDTMHandleFromNode(node), namespaceContext);
    }
    
    private XPath createXPath(final String exprString, final PrefixResolver prefixResolver) throws TransformerException {
        XPath xPath = null;
        final Class[] array = { String.class, SourceLocator.class, PrefixResolver.class, Integer.TYPE, ErrorListener.class, FunctionTable.class };
        final Object[] array2 = { exprString, null, prefixResolver, 0, null, XalanXPathAPI.funcTable };
        try {
            xPath = XPath.class.getConstructor((Class<?>[])array).newInstance(array2);
        }
        catch (final Exception ex) {
            XalanXPathAPI.LOG.debug(ex.getMessage(), ex);
        }
        if (xPath == null) {
            xPath = new XPath(exprString, null, prefixResolver, 0, null);
        }
        return xPath;
    }
    
    private static synchronized void fixupFunctionTable() {
        XalanXPathAPI.installed = false;
        if (new FunctionTable().functionAvailable("here")) {
            XalanXPathAPI.LOG.debug("Here function already registered");
            XalanXPathAPI.installed = true;
            return;
        }
        XalanXPathAPI.LOG.debug("Registering Here function");
        try {
            final Method method = FunctionTable.class.getMethod("installFunction", String.class, Expression.class);
            if ((method.getModifiers() & 0x8) != 0x0) {
                method.invoke(null, "here", new FuncHere());
                XalanXPathAPI.installed = true;
            }
        }
        catch (final Exception ex) {
            XalanXPathAPI.LOG.debug("Error installing function using the static installFunction method", ex);
        }
        if (!XalanXPathAPI.installed) {
            try {
                XalanXPathAPI.funcTable = new FunctionTable();
                FunctionTable.class.getMethod("installFunction", String.class, Class.class).invoke(XalanXPathAPI.funcTable, "here", FuncHere.class);
                XalanXPathAPI.installed = true;
            }
            catch (final Exception ex2) {
                XalanXPathAPI.LOG.debug("Error installing function using the static installFunction method", ex2);
            }
        }
        if (XalanXPathAPI.installed) {
            XalanXPathAPI.LOG.debug("Registered class {} for XPath function 'here()' function in internal table", FuncHere.class.getName());
        }
        else {
            XalanXPathAPI.LOG.debug("Unable to register class {} for XPath function 'here()' function in internal table", FuncHere.class.getName());
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(XalanXPathAPI.class);
        fixupFunctionTable();
    }
}
