package com.btr.proxy.selector.pac;

import java.lang.reflect.Method;
import javax.script.ScriptException;
import com.btr.proxy.util.Logger;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

public class JavaxPacScriptParser implements PacScriptParser
{
    static final String SCRIPT_METHODS_OBJECT = "__pacutil";
    private final PacScriptSource source;
    private final ScriptEngine engine;
    
    public JavaxPacScriptParser(final PacScriptSource source) throws ProxyEvaluationException {
        this.source = source;
        this.engine = this.setupEngine();
    }
    
    private ScriptEngine setupEngine() throws ProxyEvaluationException {
        final ScriptEngineManager mng = new ScriptEngineManager();
        final ScriptEngine engine = mng.getEngineByMimeType("text/javascript");
        engine.put("__pacutil", new PacScriptMethods());
        final Class<?> scriptMethodsClazz = ScriptMethods.class;
        final Method[] arr$;
        final Method[] scriptMethods = arr$ = scriptMethodsClazz.getMethods();
        for (final Method method : arr$) {
            final String name = method.getName();
            final int args = method.getParameterTypes().length;
            final StringBuilder toEval = new StringBuilder(name).append(" = function(");
            for (int i = 0; i < args; ++i) {
                if (i > 0) {
                    toEval.append(",");
                }
                toEval.append("arg").append(i);
            }
            toEval.append(") {return ").append("__pacutil").append(".").append(name).append("(");
            for (int i = 0; i < args; ++i) {
                if (i > 0) {
                    toEval.append(",");
                }
                toEval.append("arg").append(i);
            }
            toEval.append("); }");
            try {
                engine.eval(toEval.toString());
            }
            catch (final ScriptException e) {
                Logger.log(this.getClass(), Logger.LogLevel.ERROR, "JS evaluation error when creating alias for " + name + ".", e);
                throw new ProxyEvaluationException("Error setting up script engine", e);
            }
        }
        return engine;
    }
    
    public PacScriptSource getScriptSource() {
        return this.source;
    }
    
    public String evaluate(final String url, final String host) throws ProxyEvaluationException {
        try {
            final StringBuilder script = new StringBuilder(this.source.getScriptContent());
            final String evalMethod = " ;FindProxyForURL (\"" + url + "\",\"" + host + "\")";
            script.append(evalMethod);
            final Object result = this.engine.eval(script.toString());
            return (String)result;
        }
        catch (final Exception e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "JS evaluation error.", e);
            throw new ProxyEvaluationException("Error while executing PAC script: " + e.getMessage(), e);
        }
    }
}
