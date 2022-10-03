package org.apache.taglibs.standard.lang.jstl;

import javax.servlet.jsp.PageContext;

public class JSTLVariableResolver implements VariableResolver
{
    public Object resolveVariable(final String pName, final Object pContext) throws ELException {
        final PageContext ctx = (PageContext)pContext;
        if ("pageContext".equals(pName)) {
            return ctx;
        }
        if ("pageScope".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getPageScopeMap();
        }
        if ("requestScope".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getRequestScopeMap();
        }
        if ("sessionScope".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getSessionScopeMap();
        }
        if ("applicationScope".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getApplicationScopeMap();
        }
        if ("param".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getParamMap();
        }
        if ("paramValues".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getParamsMap();
        }
        if ("header".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getHeaderMap();
        }
        if ("headerValues".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getHeadersMap();
        }
        if ("initParam".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getInitParamMap();
        }
        if ("cookie".equals(pName)) {
            return ImplicitObjects.getImplicitObjects(ctx).getCookieMap();
        }
        return ctx.findAttribute(pName);
    }
}
