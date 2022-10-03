package org.apache.lucene.analysis;

import org.apache.lucene.store.AlreadyClosedException;
import java.util.HashMap;
import java.util.Map;
import java.io.Reader;
import org.apache.lucene.util.CloseableThreadLocal;
import org.apache.lucene.util.Version;
import java.io.Closeable;

public abstract class Analyzer implements Closeable
{
    private final ReuseStrategy reuseStrategy;
    private Version version;
    CloseableThreadLocal<Object> storedValue;
    public static final ReuseStrategy GLOBAL_REUSE_STRATEGY;
    public static final ReuseStrategy PER_FIELD_REUSE_STRATEGY;
    
    public Analyzer() {
        this(Analyzer.GLOBAL_REUSE_STRATEGY);
    }
    
    public Analyzer(final ReuseStrategy reuseStrategy) {
        this.version = Version.LATEST;
        this.storedValue = new CloseableThreadLocal<Object>();
        this.reuseStrategy = reuseStrategy;
    }
    
    protected abstract TokenStreamComponents createComponents(final String p0);
    
    public final TokenStream tokenStream(final String fieldName, final Reader reader) {
        TokenStreamComponents components = this.reuseStrategy.getReusableComponents(this, fieldName);
        final Reader r = this.initReader(fieldName, reader);
        if (components == null) {
            components = this.createComponents(fieldName);
            this.reuseStrategy.setReusableComponents(this, fieldName, components);
        }
        components.setReader(r);
        return components.getTokenStream();
    }
    
    public final TokenStream tokenStream(final String fieldName, final String text) {
        TokenStreamComponents components = this.reuseStrategy.getReusableComponents(this, fieldName);
        final ReusableStringReader strReader = (components == null || components.reusableStringReader == null) ? new ReusableStringReader() : components.reusableStringReader;
        strReader.setValue(text);
        final Reader r = this.initReader(fieldName, strReader);
        if (components == null) {
            components = this.createComponents(fieldName);
            this.reuseStrategy.setReusableComponents(this, fieldName, components);
        }
        components.setReader(r);
        components.reusableStringReader = strReader;
        return components.getTokenStream();
    }
    
    protected Reader initReader(final String fieldName, final Reader reader) {
        return reader;
    }
    
    public int getPositionIncrementGap(final String fieldName) {
        return 0;
    }
    
    public int getOffsetGap(final String fieldName) {
        return 1;
    }
    
    public final ReuseStrategy getReuseStrategy() {
        return this.reuseStrategy;
    }
    
    public void setVersion(final Version v) {
        this.version = v;
    }
    
    public Version getVersion() {
        return this.version;
    }
    
    @Override
    public void close() {
        if (this.storedValue != null) {
            this.storedValue.close();
            this.storedValue = null;
        }
    }
    
    static {
        GLOBAL_REUSE_STRATEGY = new ReuseStrategy() {
            @Override
            public TokenStreamComponents getReusableComponents(final Analyzer analyzer, final String fieldName) {
                return (TokenStreamComponents)this.getStoredValue(analyzer);
            }
            
            @Override
            public void setReusableComponents(final Analyzer analyzer, final String fieldName, final TokenStreamComponents components) {
                this.setStoredValue(analyzer, components);
            }
        };
        PER_FIELD_REUSE_STRATEGY = new ReuseStrategy() {
            @Override
            public TokenStreamComponents getReusableComponents(final Analyzer analyzer, final String fieldName) {
                final Map<String, TokenStreamComponents> componentsPerField = (Map<String, TokenStreamComponents>)this.getStoredValue(analyzer);
                return (componentsPerField != null) ? componentsPerField.get(fieldName) : null;
            }
            
            @Override
            public void setReusableComponents(final Analyzer analyzer, final String fieldName, final TokenStreamComponents components) {
                Map<String, TokenStreamComponents> componentsPerField = (Map<String, TokenStreamComponents>)this.getStoredValue(analyzer);
                if (componentsPerField == null) {
                    componentsPerField = new HashMap<String, TokenStreamComponents>();
                    this.setStoredValue(analyzer, componentsPerField);
                }
                componentsPerField.put(fieldName, components);
            }
        };
    }
    
    public static class TokenStreamComponents
    {
        protected final Tokenizer source;
        protected final TokenStream sink;
        transient ReusableStringReader reusableStringReader;
        
        public TokenStreamComponents(final Tokenizer source, final TokenStream result) {
            this.source = source;
            this.sink = result;
        }
        
        public TokenStreamComponents(final Tokenizer source) {
            this.source = source;
            this.sink = source;
        }
        
        protected void setReader(final Reader reader) {
            this.source.setReader(reader);
        }
        
        public TokenStream getTokenStream() {
            return this.sink;
        }
        
        public Tokenizer getTokenizer() {
            return this.source;
        }
    }
    
    public abstract static class ReuseStrategy
    {
        public abstract TokenStreamComponents getReusableComponents(final Analyzer p0, final String p1);
        
        public abstract void setReusableComponents(final Analyzer p0, final String p1, final TokenStreamComponents p2);
        
        protected final Object getStoredValue(final Analyzer analyzer) {
            if (analyzer.storedValue == null) {
                throw new AlreadyClosedException("this Analyzer is closed");
            }
            return analyzer.storedValue.get();
        }
        
        protected final void setStoredValue(final Analyzer analyzer, final Object storedValue) {
            if (analyzer.storedValue == null) {
                throw new AlreadyClosedException("this Analyzer is closed");
            }
            analyzer.storedValue.set(storedValue);
        }
    }
}
