package javax.script;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import java.util.HashMap;
import java.util.HashSet;

public class ScriptEngineManager
{
    private static final boolean DEBUG = false;
    private HashSet<ScriptEngineFactory> engineSpis;
    private HashMap<String, ScriptEngineFactory> nameAssociations;
    private HashMap<String, ScriptEngineFactory> extensionAssociations;
    private HashMap<String, ScriptEngineFactory> mimeTypeAssociations;
    private Bindings globalScope;
    
    public ScriptEngineManager() {
        this.init(Thread.currentThread().getContextClassLoader());
    }
    
    public ScriptEngineManager(final ClassLoader classLoader) {
        this.init(classLoader);
    }
    
    private void init(final ClassLoader classLoader) {
        this.globalScope = new SimpleBindings();
        this.engineSpis = new HashSet<ScriptEngineFactory>();
        this.nameAssociations = new HashMap<String, ScriptEngineFactory>();
        this.extensionAssociations = new HashMap<String, ScriptEngineFactory>();
        this.mimeTypeAssociations = new HashMap<String, ScriptEngineFactory>();
        this.initEngines(classLoader);
    }
    
    private ServiceLoader<ScriptEngineFactory> getServiceLoader(final ClassLoader classLoader) {
        if (classLoader != null) {
            return ServiceLoader.load(ScriptEngineFactory.class, classLoader);
        }
        return ServiceLoader.loadInstalled(ScriptEngineFactory.class);
    }
    
    private void initEngines(final ClassLoader classLoader) {
        Iterator iterator;
        try {
            iterator = AccessController.doPrivileged((PrivilegedAction<ServiceLoader>)new PrivilegedAction<ServiceLoader<ScriptEngineFactory>>() {
                @Override
                public ServiceLoader<ScriptEngineFactory> run() {
                    return ScriptEngineManager.this.getServiceLoader(classLoader);
                }
            }).iterator();
        }
        catch (final ServiceConfigurationError serviceConfigurationError) {
            System.err.println("Can't find ScriptEngineFactory providers: " + serviceConfigurationError.getMessage());
            return;
        }
        try {
            while (iterator.hasNext()) {
                try {
                    this.engineSpis.add((ScriptEngineFactory)iterator.next());
                }
                catch (final ServiceConfigurationError serviceConfigurationError2) {
                    System.err.println("ScriptEngineManager providers.next(): " + serviceConfigurationError2.getMessage());
                }
            }
        }
        catch (final ServiceConfigurationError serviceConfigurationError3) {
            System.err.println("ScriptEngineManager providers.hasNext(): " + serviceConfigurationError3.getMessage());
        }
    }
    
    public void setBindings(final Bindings globalScope) {
        if (globalScope == null) {
            throw new IllegalArgumentException("Global scope cannot be null.");
        }
        this.globalScope = globalScope;
    }
    
    public Bindings getBindings() {
        return this.globalScope;
    }
    
    public void put(final String s, final Object o) {
        this.globalScope.put(s, o);
    }
    
    public Object get(final String s) {
        return this.globalScope.get(s);
    }
    
    public ScriptEngine getEngineByName(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        final ScriptEngineFactory value;
        if (null != (value = this.nameAssociations.get(s))) {
            final ScriptEngineFactory scriptEngineFactory = value;
            try {
                final ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
                scriptEngine.setBindings(this.getBindings(), 200);
                return scriptEngine;
            }
            catch (final Exception ex) {}
        }
        for (final ScriptEngineFactory scriptEngineFactory2 : this.engineSpis) {
            List<String> names = null;
            try {
                names = scriptEngineFactory2.getNames();
            }
            catch (final Exception ex2) {}
            if (names != null) {
                final Iterator<String> iterator2 = names.iterator();
                while (iterator2.hasNext()) {
                    if (s.equals(iterator2.next())) {
                        try {
                            final ScriptEngine scriptEngine2 = scriptEngineFactory2.getScriptEngine();
                            scriptEngine2.setBindings(this.getBindings(), 200);
                            return scriptEngine2;
                        }
                        catch (final Exception ex3) {}
                    }
                }
            }
        }
        return null;
    }
    
    public ScriptEngine getEngineByExtension(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        final ScriptEngineFactory value;
        if (null != (value = this.extensionAssociations.get(s))) {
            final ScriptEngineFactory scriptEngineFactory = value;
            try {
                final ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
                scriptEngine.setBindings(this.getBindings(), 200);
                return scriptEngine;
            }
            catch (final Exception ex) {}
        }
        for (final ScriptEngineFactory scriptEngineFactory2 : this.engineSpis) {
            List<String> extensions = null;
            try {
                extensions = scriptEngineFactory2.getExtensions();
            }
            catch (final Exception ex2) {}
            if (extensions == null) {
                continue;
            }
            final Iterator<String> iterator2 = extensions.iterator();
            while (iterator2.hasNext()) {
                if (s.equals(iterator2.next())) {
                    try {
                        final ScriptEngine scriptEngine2 = scriptEngineFactory2.getScriptEngine();
                        scriptEngine2.setBindings(this.getBindings(), 200);
                        return scriptEngine2;
                    }
                    catch (final Exception ex3) {}
                }
            }
        }
        return null;
    }
    
    public ScriptEngine getEngineByMimeType(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        final ScriptEngineFactory value;
        if (null != (value = this.mimeTypeAssociations.get(s))) {
            final ScriptEngineFactory scriptEngineFactory = value;
            try {
                final ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
                scriptEngine.setBindings(this.getBindings(), 200);
                return scriptEngine;
            }
            catch (final Exception ex) {}
        }
        for (final ScriptEngineFactory scriptEngineFactory2 : this.engineSpis) {
            List<String> mimeTypes = null;
            try {
                mimeTypes = scriptEngineFactory2.getMimeTypes();
            }
            catch (final Exception ex2) {}
            if (mimeTypes == null) {
                continue;
            }
            final Iterator<String> iterator2 = mimeTypes.iterator();
            while (iterator2.hasNext()) {
                if (s.equals(iterator2.next())) {
                    try {
                        final ScriptEngine scriptEngine2 = scriptEngineFactory2.getScriptEngine();
                        scriptEngine2.setBindings(this.getBindings(), 200);
                        return scriptEngine2;
                    }
                    catch (final Exception ex3) {}
                }
            }
        }
        return null;
    }
    
    public List<ScriptEngineFactory> getEngineFactories() {
        final ArrayList list = new ArrayList(this.engineSpis.size());
        final Iterator<ScriptEngineFactory> iterator = this.engineSpis.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return (List<ScriptEngineFactory>)Collections.unmodifiableList((List<?>)list);
    }
    
    public void registerEngineName(final String s, final ScriptEngineFactory scriptEngineFactory) {
        if (s == null || scriptEngineFactory == null) {
            throw new NullPointerException();
        }
        this.nameAssociations.put(s, scriptEngineFactory);
    }
    
    public void registerEngineMimeType(final String s, final ScriptEngineFactory scriptEngineFactory) {
        if (s == null || scriptEngineFactory == null) {
            throw new NullPointerException();
        }
        this.mimeTypeAssociations.put(s, scriptEngineFactory);
    }
    
    public void registerEngineExtension(final String s, final ScriptEngineFactory scriptEngineFactory) {
        if (s == null || scriptEngineFactory == null) {
            throw new NullPointerException();
        }
        this.extensionAssociations.put(s, scriptEngineFactory);
    }
}
