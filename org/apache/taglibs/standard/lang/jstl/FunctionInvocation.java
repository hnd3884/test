package org.apache.taglibs.standard.lang.jstl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

public class FunctionInvocation extends Expression
{
    private String functionName;
    private List argumentList;
    
    public String getFunctionName() {
        return this.functionName;
    }
    
    public void setFunctionName(final String f) {
        this.functionName = f;
    }
    
    public List getArgumentList() {
        return this.argumentList;
    }
    
    public void setArgumentList(final List l) {
        this.argumentList = l;
    }
    
    public FunctionInvocation(final String functionName, final List argumentList) {
        this.functionName = functionName;
        this.argumentList = argumentList;
    }
    
    @Override
    public String getExpressionString() {
        final StringBuffer b = new StringBuffer();
        b.append(this.functionName);
        b.append("(");
        final Iterator i = this.argumentList.iterator();
        while (i.hasNext()) {
            b.append(i.next().getExpressionString());
            if (i.hasNext()) {
                b.append(", ");
            }
        }
        b.append(")");
        return b.toString();
    }
    
    @Override
    public Object evaluate(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        if (functions == null) {
            pLogger.logError(Constants.UNKNOWN_FUNCTION, this.functionName);
        }
        String functionName = this.functionName;
        if (functionName.indexOf(":") == -1) {
            if (defaultPrefix == null) {
                pLogger.logError(Constants.UNKNOWN_FUNCTION, functionName);
            }
            functionName = defaultPrefix + ":" + functionName;
        }
        final Method target = functions.get(functionName);
        if (target == null) {
            pLogger.logError(Constants.UNKNOWN_FUNCTION, functionName);
        }
        final Class[] params = target.getParameterTypes();
        if (params.length != this.argumentList.size()) {
            pLogger.logError(Constants.INAPPROPRIATE_FUNCTION_ARG_COUNT, new Integer(params.length), new Integer(this.argumentList.size()));
        }
        final Object[] arguments = new Object[this.argumentList.size()];
        for (int i = 0; i < params.length; ++i) {
            arguments[i] = this.argumentList.get(i).evaluate(pContext, pResolver, functions, defaultPrefix, pLogger);
            arguments[i] = Coercions.coerce(arguments[i], params[i], pLogger);
        }
        try {
            return target.invoke(null, arguments);
        }
        catch (final InvocationTargetException ex) {
            pLogger.logError(Constants.FUNCTION_INVOCATION_ERROR, ex.getTargetException(), functionName);
            return null;
        }
        catch (final Exception ex2) {
            pLogger.logError(Constants.FUNCTION_INVOCATION_ERROR, ex2, functionName);
            return null;
        }
    }
}
