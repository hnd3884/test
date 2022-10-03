package javax.script;

import java.util.List;
import java.io.Reader;
import java.io.Writer;

public interface ScriptContext
{
    public static final int ENGINE_SCOPE = 100;
    public static final int GLOBAL_SCOPE = 200;
    
    void setBindings(final Bindings p0, final int p1);
    
    Bindings getBindings(final int p0);
    
    void setAttribute(final String p0, final Object p1, final int p2);
    
    Object getAttribute(final String p0, final int p1);
    
    Object removeAttribute(final String p0, final int p1);
    
    Object getAttribute(final String p0);
    
    int getAttributesScope(final String p0);
    
    Writer getWriter();
    
    Writer getErrorWriter();
    
    void setWriter(final Writer p0);
    
    void setErrorWriter(final Writer p0);
    
    Reader getReader();
    
    void setReader(final Reader p0);
    
    List<Integer> getScopes();
}
