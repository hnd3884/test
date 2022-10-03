package javax.script;

import java.io.Reader;

public abstract class AbstractScriptEngine implements ScriptEngine
{
    protected ScriptContext context;
    
    public AbstractScriptEngine() {
        this.context = new SimpleScriptContext();
    }
    
    public AbstractScriptEngine(final Bindings bindings) {
        this();
        if (bindings == null) {
            throw new NullPointerException("n is null");
        }
        this.context.setBindings(bindings, 100);
    }
    
    @Override
    public void setContext(final ScriptContext context) {
        if (context == null) {
            throw new NullPointerException("null context");
        }
        this.context = context;
    }
    
    @Override
    public ScriptContext getContext() {
        return this.context;
    }
    
    @Override
    public Bindings getBindings(final int n) {
        if (n == 200) {
            return this.context.getBindings(200);
        }
        if (n == 100) {
            return this.context.getBindings(100);
        }
        throw new IllegalArgumentException("Invalid scope value.");
    }
    
    @Override
    public void setBindings(final Bindings bindings, final int n) {
        if (n == 200) {
            this.context.setBindings(bindings, 200);
        }
        else {
            if (n != 100) {
                throw new IllegalArgumentException("Invalid scope value.");
            }
            this.context.setBindings(bindings, 100);
        }
    }
    
    @Override
    public void put(final String s, final Object o) {
        final Bindings bindings = this.getBindings(100);
        if (bindings != null) {
            bindings.put(s, o);
        }
    }
    
    @Override
    public Object get(final String s) {
        final Bindings bindings = this.getBindings(100);
        if (bindings != null) {
            return bindings.get(s);
        }
        return null;
    }
    
    @Override
    public Object eval(final Reader reader, final Bindings bindings) throws ScriptException {
        return this.eval(reader, this.getScriptContext(bindings));
    }
    
    @Override
    public Object eval(final String s, final Bindings bindings) throws ScriptException {
        return this.eval(s, this.getScriptContext(bindings));
    }
    
    @Override
    public Object eval(final Reader reader) throws ScriptException {
        return this.eval(reader, this.context);
    }
    
    @Override
    public Object eval(final String s) throws ScriptException {
        return this.eval(s, this.context);
    }
    
    protected ScriptContext getScriptContext(final Bindings bindings) {
        final SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
        final Bindings bindings2 = this.getBindings(200);
        if (bindings2 != null) {
            simpleScriptContext.setBindings(bindings2, 200);
        }
        if (bindings != null) {
            simpleScriptContext.setBindings(bindings, 100);
            simpleScriptContext.setReader(this.context.getReader());
            simpleScriptContext.setWriter(this.context.getWriter());
            simpleScriptContext.setErrorWriter(this.context.getErrorWriter());
            return simpleScriptContext;
        }
        throw new NullPointerException("Engine scope Bindings may not be null.");
    }
}
