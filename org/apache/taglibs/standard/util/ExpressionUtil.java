package org.apache.taglibs.standard.util;

import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.jsp.PageContext;

public class ExpressionUtil
{
    public static ValueExpression createValueExpression(final PageContext pageContext, final String expression, final Class<?> expectedType) {
        final ExpressionFactory factory = getExpressionFactory(pageContext);
        return factory.createValueExpression(pageContext.getELContext(), expression, (Class)expectedType);
    }
    
    public static ExpressionFactory getExpressionFactory(final PageContext pageContext) {
        final JspApplicationContext appContext = JspFactory.getDefaultFactory().getJspApplicationContext(pageContext.getServletContext());
        return appContext.getExpressionFactory();
    }
    
    public static <T> T evaluate(final ValueExpression expression, final PageContext pageContext) {
        if (expression == null) {
            return null;
        }
        final T value = (T)expression.getValue(pageContext.getELContext());
        return value;
    }
    
    public static boolean evaluate(final ValueExpression expression, final PageContext pageContext, final boolean fallback) {
        if (expression == null) {
            return fallback;
        }
        final Boolean result = (Boolean)expression.getValue(pageContext.getELContext());
        return (result == null) ? fallback : result;
    }
    
    public static int evaluate(final ValueExpression expression, final PageContext pageContext, final int fallback) {
        if (expression == null) {
            return fallback;
        }
        final Integer result = (Integer)expression.getValue(pageContext.getELContext());
        return (result == null) ? fallback : result;
    }
}
