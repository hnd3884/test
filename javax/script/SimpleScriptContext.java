package javax.script;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.List;
import java.io.Reader;
import java.io.Writer;

public class SimpleScriptContext implements ScriptContext
{
    protected Writer writer;
    protected Writer errorWriter;
    protected Reader reader;
    protected Bindings engineScope;
    protected Bindings globalScope;
    private static List<Integer> scopes;
    
    public SimpleScriptContext() {
        this.engineScope = new SimpleBindings();
        this.globalScope = null;
        this.reader = new InputStreamReader(System.in);
        this.writer = new PrintWriter(System.out, true);
        this.errorWriter = new PrintWriter(System.err, true);
    }
    
    @Override
    public void setBindings(final Bindings bindings, final int n) {
        switch (n) {
            case 100: {
                if (bindings == null) {
                    throw new NullPointerException("Engine scope cannot be null.");
                }
                this.engineScope = bindings;
                break;
            }
            case 200: {
                this.globalScope = bindings;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid scope value.");
            }
        }
    }
    
    @Override
    public Object getAttribute(final String s) {
        this.checkName(s);
        if (this.engineScope.containsKey(s)) {
            return this.getAttribute(s, 100);
        }
        if (this.globalScope != null && this.globalScope.containsKey(s)) {
            return this.getAttribute(s, 200);
        }
        return null;
    }
    
    @Override
    public Object getAttribute(final String s, final int n) {
        this.checkName(s);
        switch (n) {
            case 100: {
                return this.engineScope.get(s);
            }
            case 200: {
                if (this.globalScope != null) {
                    return this.globalScope.get(s);
                }
                return null;
            }
            default: {
                throw new IllegalArgumentException("Illegal scope value.");
            }
        }
    }
    
    @Override
    public Object removeAttribute(final String s, final int n) {
        this.checkName(s);
        switch (n) {
            case 100: {
                if (this.getBindings(100) != null) {
                    return this.getBindings(100).remove(s);
                }
                return null;
            }
            case 200: {
                if (this.getBindings(200) != null) {
                    return this.getBindings(200).remove(s);
                }
                return null;
            }
            default: {
                throw new IllegalArgumentException("Illegal scope value.");
            }
        }
    }
    
    @Override
    public void setAttribute(final String s, final Object o, final int n) {
        this.checkName(s);
        switch (n) {
            case 100: {
                this.engineScope.put(s, o);
                return;
            }
            case 200: {
                if (this.globalScope != null) {
                    this.globalScope.put(s, o);
                }
                return;
            }
            default: {
                throw new IllegalArgumentException("Illegal scope value.");
            }
        }
    }
    
    @Override
    public Writer getWriter() {
        return this.writer;
    }
    
    @Override
    public Reader getReader() {
        return this.reader;
    }
    
    @Override
    public void setReader(final Reader reader) {
        this.reader = reader;
    }
    
    @Override
    public void setWriter(final Writer writer) {
        this.writer = writer;
    }
    
    @Override
    public Writer getErrorWriter() {
        return this.errorWriter;
    }
    
    @Override
    public void setErrorWriter(final Writer errorWriter) {
        this.errorWriter = errorWriter;
    }
    
    @Override
    public int getAttributesScope(final String s) {
        this.checkName(s);
        if (this.engineScope.containsKey(s)) {
            return 100;
        }
        if (this.globalScope != null && this.globalScope.containsKey(s)) {
            return 200;
        }
        return -1;
    }
    
    @Override
    public Bindings getBindings(final int n) {
        if (n == 100) {
            return this.engineScope;
        }
        if (n == 200) {
            return this.globalScope;
        }
        throw new IllegalArgumentException("Illegal scope value.");
    }
    
    @Override
    public List<Integer> getScopes() {
        return SimpleScriptContext.scopes;
    }
    
    private void checkName(final String s) {
        Objects.requireNonNull(s);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("name cannot be empty");
        }
    }
    
    static {
        (SimpleScriptContext.scopes = new ArrayList<Integer>(2)).add(100);
        SimpleScriptContext.scopes.add(200);
        SimpleScriptContext.scopes = Collections.unmodifiableList((List<? extends Integer>)SimpleScriptContext.scopes);
    }
}
