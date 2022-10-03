package javax.script;

import java.util.List;

public interface ScriptEngineFactory
{
    String getEngineName();
    
    String getEngineVersion();
    
    List<String> getExtensions();
    
    List<String> getMimeTypes();
    
    List<String> getNames();
    
    String getLanguageName();
    
    String getLanguageVersion();
    
    Object getParameter(final String p0);
    
    String getMethodCallSyntax(final String p0, final String p1, final String... p2);
    
    String getOutputStatement(final String p0);
    
    String getProgram(final String... p0);
    
    ScriptEngine getScriptEngine();
}
