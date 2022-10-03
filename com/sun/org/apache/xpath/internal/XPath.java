package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.Node;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.XPathParser;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import javax.xml.transform.ErrorListener;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import java.io.Serializable;

public class XPath implements Serializable, ExpressionOwner
{
    static final long serialVersionUID = 3976493477939110553L;
    private Expression m_mainExp;
    private transient FunctionTable m_funcTable;
    String m_patternString;
    public static final int SELECT = 0;
    public static final int MATCH = 1;
    private static final boolean DEBUG_MATCHES = false;
    public static final double MATCH_SCORE_NONE = Double.NEGATIVE_INFINITY;
    public static final double MATCH_SCORE_QNAME = 0.0;
    public static final double MATCH_SCORE_NSWILD = -0.25;
    public static final double MATCH_SCORE_NODETEST = -0.5;
    public static final double MATCH_SCORE_OTHER = 0.5;
    
    private void initFunctionTable() {
        this.m_funcTable = new FunctionTable();
    }
    
    @Override
    public Expression getExpression() {
        return this.m_mainExp;
    }
    
    public void fixupVariables(final Vector vars, final int globalsSize) {
        this.m_mainExp.fixupVariables(vars, globalsSize);
    }
    
    @Override
    public void setExpression(final Expression exp) {
        if (null != this.m_mainExp) {
            exp.exprSetParent(this.m_mainExp.exprGetParent());
        }
        this.m_mainExp = exp;
    }
    
    public SourceLocator getLocator() {
        return this.m_mainExp;
    }
    
    public String getPatternString() {
        return this.m_patternString;
    }
    
    public XPath(final String exprString, final SourceLocator locator, final PrefixResolver prefixResolver, final int type, ErrorListener errorListener) throws TransformerException {
        this.m_funcTable = null;
        this.initFunctionTable();
        if (null == errorListener) {
            errorListener = new DefaultErrorHandler();
        }
        this.m_patternString = exprString;
        final XPathParser parser = new XPathParser(errorListener, locator);
        final Compiler compiler = new Compiler(errorListener, locator, this.m_funcTable);
        if (0 == type) {
            parser.initXPath(compiler, exprString, prefixResolver);
        }
        else {
            if (1 != type) {
                throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_CANNOT_DEAL_XPATH_TYPE", new Object[] { Integer.toString(type) }));
            }
            parser.initMatchPattern(compiler, exprString, prefixResolver);
        }
        final Expression expr = compiler.compileExpression(0);
        this.setExpression(expr);
        if (null != locator && locator instanceof ExpressionNode) {
            expr.exprSetParent((ExpressionNode)locator);
        }
    }
    
    public XPath(final String exprString, final SourceLocator locator, final PrefixResolver prefixResolver, final int type, ErrorListener errorListener, final FunctionTable aTable) throws TransformerException {
        this.m_funcTable = null;
        this.m_funcTable = aTable;
        if (null == errorListener) {
            errorListener = new DefaultErrorHandler();
        }
        this.m_patternString = exprString;
        final XPathParser parser = new XPathParser(errorListener, locator);
        final Compiler compiler = new Compiler(errorListener, locator, this.m_funcTable);
        if (0 == type) {
            parser.initXPath(compiler, exprString, prefixResolver);
        }
        else {
            if (1 != type) {
                throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_CANNOT_DEAL_XPATH_TYPE", new Object[] { Integer.toString(type) }));
            }
            parser.initMatchPattern(compiler, exprString, prefixResolver);
        }
        final Expression expr = compiler.compileExpression(0);
        this.setExpression(expr);
        if (null != locator && locator instanceof ExpressionNode) {
            expr.exprSetParent((ExpressionNode)locator);
        }
    }
    
    public XPath(final String exprString, final SourceLocator locator, final PrefixResolver prefixResolver, final int type) throws TransformerException {
        this(exprString, locator, prefixResolver, type, null);
    }
    
    public XPath(final Expression expr) {
        this.m_funcTable = null;
        this.setExpression(expr);
        this.initFunctionTable();
    }
    
    public XObject execute(final XPathContext xctxt, final Node contextNode, final PrefixResolver namespaceContext) throws TransformerException {
        return this.execute(xctxt, xctxt.getDTMHandleFromNode(contextNode), namespaceContext);
    }
    
    public XObject execute(final XPathContext xctxt, final int contextNode, final PrefixResolver namespaceContext) throws TransformerException {
        xctxt.pushNamespaceContext(namespaceContext);
        xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);
        XObject xobj = null;
        try {
            xobj = this.m_mainExp.execute(xctxt);
        }
        catch (final TransformerException te) {
            te.setLocator(this.getLocator());
            final ErrorListener el = xctxt.getErrorListener();
            if (null != el) {
                el.error(te);
                return xobj;
            }
            throw te;
        }
        catch (final Exception e) {
            while (e instanceof WrappedRuntimeException) {
                e = ((WrappedRuntimeException)e).getException();
            }
            String msg = e.getMessage();
            if (msg == null || msg.length() == 0) {
                msg = XPATHMessages.createXPATHMessage("ER_XPATH_ERROR", null);
            }
            final TransformerException te2 = new TransformerException(msg, this.getLocator(), e);
            final ErrorListener el2 = xctxt.getErrorListener();
            if (null != el2) {
                el2.fatalError(te2);
                return xobj;
            }
            throw te2;
        }
        finally {
            xctxt.popNamespaceContext();
            xctxt.popCurrentNodeAndExpression();
        }
        return xobj;
    }
    
    public boolean bool(final XPathContext xctxt, final int contextNode, final PrefixResolver namespaceContext) throws TransformerException {
        xctxt.pushNamespaceContext(namespaceContext);
        xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);
        try {
            return this.m_mainExp.bool(xctxt);
        }
        catch (final TransformerException te) {
            te.setLocator(this.getLocator());
            final ErrorListener el = xctxt.getErrorListener();
            if (null != el) {
                el.error(te);
                return false;
            }
            throw te;
        }
        catch (final Exception e) {
            while (e instanceof WrappedRuntimeException) {
                e = ((WrappedRuntimeException)e).getException();
            }
            String msg = e.getMessage();
            if (msg == null || msg.length() == 0) {
                msg = XPATHMessages.createXPATHMessage("ER_XPATH_ERROR", null);
            }
            final TransformerException te2 = new TransformerException(msg, this.getLocator(), e);
            final ErrorListener el2 = xctxt.getErrorListener();
            if (null != el2) {
                el2.fatalError(te2);
                return false;
            }
            throw te2;
        }
        finally {
            xctxt.popNamespaceContext();
            xctxt.popCurrentNodeAndExpression();
        }
        return false;
    }
    
    public double getMatchScore(final XPathContext xctxt, final int context) throws TransformerException {
        xctxt.pushCurrentNode(context);
        xctxt.pushCurrentExpressionNode(context);
        try {
            final XObject score = this.m_mainExp.execute(xctxt);
            return score.num();
        }
        finally {
            xctxt.popCurrentNode();
            xctxt.popCurrentExpressionNode();
        }
    }
    
    public void warn(final XPathContext xctxt, final int sourceNode, final String msg, final Object[] args) throws TransformerException {
        final String fmsg = XPATHMessages.createXPATHWarning(msg, args);
        final ErrorListener ehandler = xctxt.getErrorListener();
        if (null != ehandler) {
            ehandler.warning(new TransformerException(fmsg, xctxt.getSAXLocator()));
        }
    }
    
    public void assertion(final boolean b, final String msg) {
        if (!b) {
            final String fMsg = XPATHMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { msg });
            throw new RuntimeException(fMsg);
        }
    }
    
    public void error(final XPathContext xctxt, final int sourceNode, final String msg, final Object[] args) throws TransformerException {
        final String fmsg = XPATHMessages.createXPATHMessage(msg, args);
        final ErrorListener ehandler = xctxt.getErrorListener();
        if (null != ehandler) {
            ehandler.fatalError(new TransformerException(fmsg, xctxt.getSAXLocator()));
        }
        else {
            final SourceLocator slocator = xctxt.getSAXLocator();
            System.out.println(fmsg + "; file " + slocator.getSystemId() + "; line " + slocator.getLineNumber() + "; column " + slocator.getColumnNumber());
        }
    }
    
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        this.m_mainExp.callVisitors(this, visitor);
    }
}
