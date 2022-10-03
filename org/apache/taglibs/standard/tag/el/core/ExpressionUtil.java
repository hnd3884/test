package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public class ExpressionUtil
{
    public static Object evalNotNull(final String tagName, final String attributeName, final String expression, final Class expectedType, final Tag tag, final PageContext pageContext) throws JspException {
        if (expression == null) {
            return null;
        }
        final Object r = ExpressionEvaluatorManager.evaluate(attributeName, expression, expectedType, tag, pageContext);
        if (r == null) {
            throw new NullAttributeException(tagName, attributeName);
        }
        return r;
    }
}
