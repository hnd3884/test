package javax.script;

import java.io.Reader;

public interface Compilable
{
    CompiledScript compile(final String p0) throws ScriptException;
    
    CompiledScript compile(final Reader p0) throws ScriptException;
}
