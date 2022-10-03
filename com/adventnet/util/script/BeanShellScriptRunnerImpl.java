package com.adventnet.util.script;

import bsh.EvalError;
import java.util.StringTokenizer;
import java.util.Properties;
import bsh.Interpreter;

public class BeanShellScriptRunnerImpl implements RunScriptInterface
{
    Interpreter interp;
    
    public BeanShellScriptRunnerImpl() {
        this.interp = null;
        this.interp = new Interpreter();
    }
    
    public void init(final Properties properties) throws ScriptHandlerException {
        try {
            if (properties != null) {
                final String property = properties.getProperty("modules");
                if (property != null) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(property);
                    while (stringTokenizer.hasMoreTokens()) {
                        this.interp.eval("import " + stringTokenizer.nextToken());
                    }
                }
            }
        }
        catch (final EvalError evalError) {
            ((Throwable)evalError).printStackTrace();
            throw new ScriptHandlerException(evalError.getMessage());
        }
    }
    
    public void executeScript(final String s) throws ScriptHandlerException {
        try {
            this.interp.source(s);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ScriptHandlerException(ex.getMessage());
        }
    }
    
    public void executeScript(final String s, final String[] array) throws ScriptHandlerException {
        try {
            if (array != null) {
                if (this.interp.get("args") != null) {
                    this.interp.unset("args");
                }
                this.interp.set("args", (Object)array);
            }
            this.interp.source(s);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ScriptHandlerException(ex.getMessage());
        }
    }
    
    public void executeLine(final String s) throws ScriptHandlerException {
        try {
            this.interp.eval(s);
        }
        catch (final EvalError evalError) {
            ((Throwable)evalError).printStackTrace();
            throw new ScriptHandlerException(evalError.getMessage());
        }
    }
}
