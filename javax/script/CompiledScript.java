package javax.script;

public abstract class CompiledScript
{
    public abstract Object eval(final ScriptContext p0) throws ScriptException;
    
    public Object eval(final Bindings bindings) throws ScriptException {
        ScriptContext context = this.getEngine().getContext();
        if (bindings != null) {
            final SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
            simpleScriptContext.setBindings(bindings, 100);
            simpleScriptContext.setBindings(context.getBindings(200), 200);
            simpleScriptContext.setWriter(context.getWriter());
            simpleScriptContext.setReader(context.getReader());
            simpleScriptContext.setErrorWriter(context.getErrorWriter());
            context = simpleScriptContext;
        }
        return this.eval(context);
    }
    
    public Object eval() throws ScriptException {
        return this.eval(this.getEngine().getContext());
    }
    
    public abstract ScriptEngine getEngine();
}
