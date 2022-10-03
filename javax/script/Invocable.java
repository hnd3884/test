package javax.script;

public interface Invocable
{
    Object invokeMethod(final Object p0, final String p1, final Object... p2) throws ScriptException, NoSuchMethodException;
    
    Object invokeFunction(final String p0, final Object... p1) throws ScriptException, NoSuchMethodException;
    
     <T> T getInterface(final Class<T> p0);
    
     <T> T getInterface(final Object p0, final Class<T> p1);
}
