package org.apache.taglibs.standard.tag.common.xml;

import java.util.HashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.xpath.objects.XObjectFactory;
import javax.xml.transform.TransformerException;
import org.apache.taglibs.standard.resources.Resources;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPathContext;
import javax.servlet.jsp.PageContext;
import java.util.Map;
import org.apache.xpath.VariableStack;

public class JSTLVariableStack extends VariableStack
{
    private static final String PARAM_PREFIX = "param";
    private static final String HEADER_PREFIX = "header";
    private static final String COOKIE_PREFIX = "cookie";
    private static final String INITPARAM_PREFIX = "initParam";
    private static final String PAGE_PREFIX = "pageScope";
    private static final String REQUEST_PREFIX = "requestScope";
    private static final String SESSION_PREFIX = "sessionScope";
    private static final String APP_PREFIX = "applicationScope";
    private static final Map<String, Scope> SCOPES;
    private final PageContext pageContext;
    
    public JSTLVariableStack(final PageContext pageContext) {
        super(2);
        this.pageContext = pageContext;
    }
    
    public XObject getVariableOrParam(final XPathContext xctxt, final QName qname) throws TransformerException {
        final String prefix = qname.getNamespaceURI();
        final String name = qname.getLocalPart();
        final Object value = this.getValue(prefix, name);
        if (value == null) {
            final StringBuilder var = new StringBuilder();
            var.append('$');
            if (prefix != null) {
                var.append(prefix);
                var.append(':');
            }
            var.append(name);
            throw new TransformerException(Resources.getMessage("XPATH_UNABLE_TO_RESOLVE_VARIABLE", var.toString()));
        }
        return XObjectFactory.create(value, xctxt);
    }
    
    private Object getValue(final String prefix, final String name) {
        if (prefix == null) {
            return this.pageContext.findAttribute(name);
        }
        final Scope scope = JSTLVariableStack.SCOPES.get(prefix);
        switch (scope) {
            case PARAM: {
                return this.pageContext.getRequest().getParameter(name);
            }
            case HEADER: {
                return ((HttpServletRequest)this.pageContext.getRequest()).getHeader(name);
            }
            case COOKIE: {
                final Cookie[] cookies = ((HttpServletRequest)this.pageContext.getRequest()).getCookies();
                if (cookies != null) {
                    for (final Cookie cookie : cookies) {
                        if (cookie.getName().equals(name)) {
                            return cookie.getValue();
                        }
                    }
                }
                return null;
            }
            case INITPARAM: {
                return this.pageContext.getServletContext().getInitParameter(name);
            }
            case PAGE: {
                return this.pageContext.getAttribute(name, 1);
            }
            case REQUEST: {
                return this.pageContext.getAttribute(name, 2);
            }
            case SESSION: {
                return this.pageContext.getAttribute(name, 3);
            }
            case APPLICATION: {
                return this.pageContext.getAttribute(name, 4);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    static {
        (SCOPES = new HashMap<String, Scope>(8)).put("param", Scope.PARAM);
        JSTLVariableStack.SCOPES.put("header", Scope.HEADER);
        JSTLVariableStack.SCOPES.put("cookie", Scope.COOKIE);
        JSTLVariableStack.SCOPES.put("initParam", Scope.INITPARAM);
        JSTLVariableStack.SCOPES.put("pageScope", Scope.PAGE);
        JSTLVariableStack.SCOPES.put("requestScope", Scope.REQUEST);
        JSTLVariableStack.SCOPES.put("sessionScope", Scope.SESSION);
        JSTLVariableStack.SCOPES.put("applicationScope", Scope.APPLICATION);
    }
    
    private enum Scope
    {
        PARAM, 
        HEADER, 
        COOKIE, 
        INITPARAM, 
        PAGE, 
        REQUEST, 
        SESSION, 
        APPLICATION;
    }
}
