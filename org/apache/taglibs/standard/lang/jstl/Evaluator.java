package org.apache.taglibs.standard.lang.jstl;

import javax.servlet.jsp.JspException;
import java.util.Map;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.text.MessageFormat;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluator;

public class Evaluator implements ExpressionEvaluator
{
    static ELEvaluator sEvaluator;
    
    public String validate(final String pAttributeName, final String pAttributeValue) {
        try {
            Evaluator.sEvaluator.setBypassCache(true);
            Evaluator.sEvaluator.parseExpressionString(pAttributeValue);
            Evaluator.sEvaluator.setBypassCache(false);
            return null;
        }
        catch (final ELException exc) {
            return MessageFormat.format(Constants.ATTRIBUTE_PARSE_EXCEPTION, "" + pAttributeName, "" + pAttributeValue, exc.getMessage());
        }
    }
    
    public Object evaluate(final String pAttributeName, final String pAttributeValue, final Class pExpectedType, final Tag pTag, final PageContext pPageContext, final Map functions, final String defaultPrefix) throws JspException {
        try {
            return Evaluator.sEvaluator.evaluate(pAttributeValue, pPageContext, pExpectedType, functions, defaultPrefix);
        }
        catch (final ELException exc) {
            throw new JspException(MessageFormat.format(Constants.ATTRIBUTE_EVALUATION_EXCEPTION, "" + pAttributeName, "" + pAttributeValue, exc.getMessage(), exc.getRootCause()), exc.getRootCause());
        }
    }
    
    public Object evaluate(final String pAttributeName, final String pAttributeValue, final Class pExpectedType, final Tag pTag, final PageContext pPageContext) throws JspException {
        return this.evaluate(pAttributeName, pAttributeValue, pExpectedType, pTag, pPageContext, null, null);
    }
    
    public static String parseAndRender(final String pAttributeValue) throws JspException {
        try {
            return Evaluator.sEvaluator.parseAndRender(pAttributeValue);
        }
        catch (final ELException exc) {
            throw new JspException(MessageFormat.format(Constants.ATTRIBUTE_PARSE_EXCEPTION, "test", "" + pAttributeValue, exc.getMessage()));
        }
    }
    
    static {
        Evaluator.sEvaluator = new ELEvaluator(new JSTLVariableResolver());
    }
}
