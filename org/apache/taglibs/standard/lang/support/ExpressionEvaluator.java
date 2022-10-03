package org.apache.taglibs.standard.lang.support;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public interface ExpressionEvaluator
{
    String validate(final String p0, final String p1);
    
    Object evaluate(final String p0, final String p1, final Class p2, final Tag p3, final PageContext p4) throws JspException;
}
