package org.apache.xml.security.utils;

import org.apache.xpath.Expression;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Method;
import org.apache.xml.security.transforms.implementations.FuncHere;
import org.w3c.dom.Text;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.w3c.dom.Document;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.security.transforms.implementations.FuncHereContext;
import org.apache.commons.logging.Log;

public class CachedXPathFuncHereAPI
{
    static Log log;
    FuncHereContext _funcHereContext;
    DTMManager _dtmManager;
    XPathContext _context;
    String xpathStr;
    XPath xpath;
    static FunctionTable _funcTable;
    
    public FuncHereContext getFuncHereContext() {
        return this._funcHereContext;
    }
    
    private CachedXPathFuncHereAPI() {
        this._funcHereContext = null;
        this._dtmManager = null;
        this._context = null;
        this.xpathStr = null;
        this.xpath = null;
    }
    
    public CachedXPathFuncHereAPI(final XPathContext context) {
        this._funcHereContext = null;
        this._dtmManager = null;
        this._context = null;
        this.xpathStr = null;
        this.xpath = null;
        this._dtmManager = context.getDTMManager();
        this._context = context;
    }
    
    public CachedXPathFuncHereAPI(final CachedXPathAPI cachedXPathAPI) {
        this._funcHereContext = null;
        this._dtmManager = null;
        this._context = null;
        this.xpathStr = null;
        this.xpath = null;
        this._dtmManager = cachedXPathAPI.getXPathContext().getDTMManager();
        this._context = cachedXPathAPI.getXPathContext();
    }
    
    public Node selectSingleNode(final Node node, final Node node2) throws TransformerException {
        return this.selectSingleNode(node, node2, node);
    }
    
    public Node selectSingleNode(final Node node, final Node node2, final Node node3) throws TransformerException {
        return this.selectNodeIterator(node, node2, node3).nextNode();
    }
    
    public NodeIterator selectNodeIterator(final Node node, final Node node2) throws TransformerException {
        return this.selectNodeIterator(node, node2, node);
    }
    
    public NodeIterator selectNodeIterator(final Node node, final Node node2, final Node node3) throws TransformerException {
        return this.eval(node, node2, getStrFromNode(node2), node3).nodeset();
    }
    
    public NodeList selectNodeList(final Node node, final Node node2) throws TransformerException {
        return this.selectNodeList(node, node2, getStrFromNode(node2), node);
    }
    
    public NodeList selectNodeList(final Node node, final Node node2, final String s, final Node node3) throws TransformerException {
        return this.eval(node, node2, s, node3).nodelist();
    }
    
    public XObject eval(final Node node, final Node node2) throws TransformerException {
        return this.eval(node, node2, getStrFromNode(node2), node);
    }
    
    public XObject eval(final Node node, final Node node2, final String xpathStr, final Node node3) throws TransformerException {
        if (this._funcHereContext == null) {
            this._funcHereContext = new FuncHereContext(node2, this._dtmManager);
        }
        final PrefixResolverDefault prefixResolverDefault = new PrefixResolverDefault((node3.getNodeType() == 9) ? ((Document)node3).getDocumentElement() : node3);
        if (xpathStr != this.xpathStr) {
            if (xpathStr.indexOf("here()") > 0) {
                this._context.reset();
                this._dtmManager = this._context.getDTMManager();
            }
            this.xpath = this.createXPath(xpathStr, (PrefixResolver)prefixResolverDefault);
            this.xpathStr = xpathStr;
        }
        return this.xpath.execute((XPathContext)this._funcHereContext, this._funcHereContext.getDTMHandleFromNode(node), (PrefixResolver)prefixResolverDefault);
    }
    
    public XObject eval(final Node node, final Node node2, final String xpathStr, final PrefixResolver prefixResolver) throws TransformerException {
        if (xpathStr != this.xpathStr) {
            if (xpathStr.indexOf("here()") > 0) {
                this._context.reset();
                this._dtmManager = this._context.getDTMManager();
            }
            try {
                this.xpath = this.createXPath(xpathStr, prefixResolver);
            }
            catch (final TransformerException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof ClassNotFoundException && cause.getMessage().indexOf("FuncHere") > 0) {
                    throw new RuntimeException(I18n.translate("endorsed.jdk1.4.0") + ex);
                }
                throw ex;
            }
            this.xpathStr = xpathStr;
        }
        if (this._funcHereContext == null) {
            this._funcHereContext = new FuncHereContext(node2, this._dtmManager);
        }
        return this.xpath.execute((XPathContext)this._funcHereContext, this._funcHereContext.getDTMHandleFromNode(node), prefixResolver);
    }
    
    private XPath createXPath(final String s, final PrefixResolver prefixResolver) throws TransformerException {
        XPath xPath = null;
        final Class[] array = { String.class, SourceLocator.class, PrefixResolver.class, Integer.TYPE, ErrorListener.class, FunctionTable.class };
        final Object[] array2 = { s, null, prefixResolver, new Integer(0), null, CachedXPathFuncHereAPI._funcTable };
        try {
            xPath = XPath.class.getConstructor((Class<?>[])array).newInstance(array2);
        }
        catch (final Throwable t) {}
        if (xPath == null) {
            xPath = new XPath(s, (SourceLocator)null, prefixResolver, 0, (ErrorListener)null);
        }
        return xPath;
    }
    
    public static String getStrFromNode(final Node node) {
        if (node.getNodeType() == 3) {
            final StringBuffer sb = new StringBuffer();
            for (Node node2 = node.getParentNode().getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2.getNodeType() == 3) {
                    sb.append(((Text)node2).getData());
                }
            }
            return sb.toString();
        }
        if (node.getNodeType() == 2) {
            return node.getNodeValue();
        }
        if (node.getNodeType() == 7) {
            return node.getNodeValue();
        }
        return null;
    }
    
    private static void fixupFunctionTable() {
        int n = 0;
        CachedXPathFuncHereAPI.log.info((Object)"Registering Here function");
        try {
            final Method method = FunctionTable.class.getMethod("installFunction", String.class, Expression.class);
            if ((method.getModifiers() & 0x8) != 0x0) {
                method.invoke(null, "here", new FuncHere());
                n = 1;
            }
        }
        catch (final Throwable t) {
            CachedXPathFuncHereAPI.log.debug((Object)"Error installing function using the static installFunction method", t);
        }
        if (n == 0) {
            try {
                CachedXPathFuncHereAPI._funcTable = new FunctionTable();
                FunctionTable.class.getMethod("installFunction", String.class, Class.class).invoke(CachedXPathFuncHereAPI._funcTable, "here", FuncHere.class);
                n = 1;
            }
            catch (final Throwable t2) {
                CachedXPathFuncHereAPI.log.debug((Object)"Error installing function using the static installFunction method", t2);
            }
        }
        if (CachedXPathFuncHereAPI.log.isDebugEnabled()) {
            if (n != 0) {
                CachedXPathFuncHereAPI.log.debug((Object)("Registered class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table"));
            }
            else {
                CachedXPathFuncHereAPI.log.debug((Object)("Unable to register class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table"));
            }
        }
    }
    
    static {
        CachedXPathFuncHereAPI.log = LogFactory.getLog(CachedXPathFuncHereAPI.class.getName());
        CachedXPathFuncHereAPI._funcTable = null;
        fixupFunctionTable();
    }
}
