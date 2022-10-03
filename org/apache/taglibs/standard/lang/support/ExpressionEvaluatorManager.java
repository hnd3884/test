package org.apache.taglibs.standard.lang.support;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.taglibs.standard.lang.jstl.Evaluator;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.apache.taglibs.standard.lang.jstl.Coercions;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.jstl.Logger;
import java.util.concurrent.ConcurrentMap;

public class ExpressionEvaluatorManager
{
    public static final String EVALUATOR_CLASS = "org.apache.taglibs.standard.lang.jstl.Evaluator";
    private static final ExpressionEvaluator EVALUATOR;
    private static final ConcurrentMap<String, ExpressionEvaluator> nameMap;
    private static final Logger logger;
    
    public static Object evaluate(final String attributeName, final String expression, final Class expectedType, final Tag tag, final PageContext pageContext) throws JspException {
        return ExpressionEvaluatorManager.EVALUATOR.evaluate(attributeName, expression, expectedType, tag, pageContext);
    }
    
    public static Object evaluate(final String attributeName, final String expression, final Class expectedType, final PageContext pageContext) throws JspException {
        return evaluate(attributeName, expression, expectedType, null, pageContext);
    }
    
    @Deprecated
    public static ExpressionEvaluator getEvaluatorByName(final String name) throws JspException {
        try {
            ExpressionEvaluator evaluator = ExpressionEvaluatorManager.nameMap.get(name);
            if (evaluator == null) {
                ExpressionEvaluatorManager.nameMap.putIfAbsent(name, (ExpressionEvaluator)Class.forName(name).newInstance());
                evaluator = ExpressionEvaluatorManager.nameMap.get(name);
            }
            return evaluator;
        }
        catch (final ClassCastException ex) {
            throw new JspException("invalid ExpressionEvaluator: " + name, (Throwable)ex);
        }
        catch (final ClassNotFoundException ex2) {
            throw new JspException("couldn't find ExpressionEvaluator: " + name, (Throwable)ex2);
        }
        catch (final IllegalAccessException ex3) {
            throw new JspException("couldn't access ExpressionEvaluator: " + name, (Throwable)ex3);
        }
        catch (final InstantiationException ex4) {
            throw new JspException("couldn't instantiate ExpressionEvaluator: " + name, (Throwable)ex4);
        }
    }
    
    public static Object coerce(final Object value, final Class classe) throws JspException {
        try {
            return Coercions.coerce(value, classe, ExpressionEvaluatorManager.logger);
        }
        catch (final ELException ex) {
            throw new JspException((Throwable)ex);
        }
    }
    
    public static String validate(final String attributeName, final String expression) {
        return ExpressionEvaluatorManager.EVALUATOR.validate(attributeName, expression);
    }
    
    static {
        EVALUATOR = new Evaluator();
        (nameMap = new ConcurrentHashMap<String, ExpressionEvaluator>()).put("org.apache.taglibs.standard.lang.jstl.Evaluator", ExpressionEvaluatorManager.EVALUATOR);
        logger = new Logger(System.out);
    }
}
