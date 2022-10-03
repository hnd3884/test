package com.adventnet.util.script;

import org.python.core.PyException;
import java.util.StringTokenizer;
import java.util.Properties;
import org.python.util.PythonInterpreter;

public class PythonScriptRunnerImpl implements RunScriptInterface
{
    PythonInterpreter interp;
    
    public PythonScriptRunnerImpl() {
        this.interp = null;
        this.interp = new PythonInterpreter();
    }
    
    public void init(final Properties properties) throws ScriptHandlerException {
        try {
            this.interp.exec("import sys");
            if (properties != null) {
                final String property = properties.getProperty("module");
                if (property != null) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(property);
                    while (stringTokenizer.hasMoreTokens()) {
                        this.interp.exec("import " + stringTokenizer.nextToken());
                    }
                }
            }
        }
        catch (final PyException ex) {
            ex.printStackTrace();
            throw new ScriptHandlerException(((Throwable)ex).getMessage());
        }
    }
    
    public void executeScript(final String s) throws ScriptHandlerException {
        try {
            this.interp.execfile(s);
        }
        catch (final PyException ex) {
            ex.printStackTrace();
            throw new ScriptHandlerException(((Throwable)ex).getMessage());
        }
    }
    
    public void executeScript(final String s, final String[] array) throws ScriptHandlerException {
        try {
            this.fillArguments(array);
            this.interp.execfile(s);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ScriptHandlerException(ex.getMessage());
        }
    }
    
    public void executeLine(final String s) throws ScriptHandlerException {
        try {
            this.interp.exec(s);
        }
        catch (final PyException ex) {
            ex.printStackTrace();
            throw new ScriptHandlerException(((Throwable)ex).getMessage());
        }
    }
    
    private void fillArguments(final String[] array) throws Exception {
        if (array != null) {
            for (int intValue = (int)this.interp.eval("len(sys.argv)").__tojava__((Class)Class.forName("java.lang.Integer")), i = 0; i < intValue; ++i) {
                this.interp.exec("del sys.argv[0]");
            }
            for (int j = 0; j < array.length; ++j) {
                this.interp.set("tempArg", (Object)array[j]);
                this.interp.exec("sys.argv.append(tempArg)");
            }
        }
    }
}
