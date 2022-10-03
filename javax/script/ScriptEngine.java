package javax.script;

import java.io.Reader;

public interface ScriptEngine
{
    public static final String ARGV = "javax.script.argv";
    public static final String FILENAME = "javax.script.filename";
    public static final String ENGINE = "javax.script.engine";
    public static final String ENGINE_VERSION = "javax.script.engine_version";
    public static final String NAME = "javax.script.name";
    public static final String LANGUAGE = "javax.script.language";
    public static final String LANGUAGE_VERSION = "javax.script.language_version";
    
    Object eval(final String p0, final ScriptContext p1) throws ScriptException;
    
    Object eval(final Reader p0, final ScriptContext p1) throws ScriptException;
    
    Object eval(final String p0) throws ScriptException;
    
    Object eval(final Reader p0) throws ScriptException;
    
    Object eval(final String p0, final Bindings p1) throws ScriptException;
    
    Object eval(final Reader p0, final Bindings p1) throws ScriptException;
    
    void put(final String p0, final Object p1);
    
    Object get(final String p0);
    
    Bindings getBindings(final int p0);
    
    void setBindings(final Bindings p0, final int p1);
    
    Bindings createBindings();
    
    ScriptContext getContext();
    
    void setContext(final ScriptContext p0);
    
    ScriptEngineFactory getFactory();
}
