package javax.tools;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.logging.Level;
import java.lang.ref.Reference;
import java.util.Map;

public class ToolProvider
{
    private static final String propertyName = "sun.tools.ToolProvider";
    private static final String loggerName = "javax.tools";
    private static final String defaultJavaCompilerName = "com.sun.tools.javac.api.JavacTool";
    private static final String defaultDocumentationToolName = "com.sun.tools.javadoc.api.JavadocTool";
    private static ToolProvider instance;
    private Map<String, Reference<Class<?>>> toolClasses;
    private Reference<ClassLoader> refToolClassLoader;
    private static final String[] defaultToolsLocation;
    
    static <T> T trace(final Level level, final Object o) {
        try {
            if (System.getProperty("sun.tools.ToolProvider") != null) {
                final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String format = "???";
                String s = ToolProvider.class.getName();
                if (stackTrace.length > 2) {
                    final StackTraceElement stackTraceElement = stackTrace[2];
                    format = String.format(null, "%s(%s:%s)", stackTraceElement.getMethodName(), stackTraceElement.getFileName(), stackTraceElement.getLineNumber());
                    s = stackTraceElement.getClassName();
                }
                final Logger logger = Logger.getLogger("javax.tools");
                if (o instanceof Throwable) {
                    logger.logp(level, s, format, o.getClass().getName(), (Throwable)o);
                }
                else {
                    logger.logp(level, s, format, String.valueOf(o));
                }
            }
        }
        catch (final SecurityException ex) {
            System.err.format(null, "%s: %s; %s%n", ToolProvider.class.getName(), o, ex.getLocalizedMessage());
        }
        return null;
    }
    
    public static JavaCompiler getSystemJavaCompiler() {
        return instance().getSystemTool(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool");
    }
    
    public static DocumentationTool getSystemDocumentationTool() {
        return instance().getSystemTool(DocumentationTool.class, "com.sun.tools.javadoc.api.JavadocTool");
    }
    
    public static ClassLoader getSystemToolClassLoader() {
        try {
            return instance().getSystemToolClass(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool").getClassLoader();
        }
        catch (final Throwable t) {
            return trace(Level.WARNING, t);
        }
    }
    
    private static synchronized ToolProvider instance() {
        if (ToolProvider.instance == null) {
            ToolProvider.instance = new ToolProvider();
        }
        return ToolProvider.instance;
    }
    
    private ToolProvider() {
        this.toolClasses = new HashMap<String, Reference<Class<?>>>();
        this.refToolClassLoader = null;
    }
    
    private <T> T getSystemTool(final Class<T> clazz, final String s) {
        final Class<? extends T> systemToolClass = this.getSystemToolClass(clazz, s);
        try {
            return (T)systemToolClass.asSubclass(clazz).newInstance();
        }
        catch (final Throwable t) {
            trace(Level.WARNING, t);
            return null;
        }
    }
    
    private <T> Class<? extends T> getSystemToolClass(final Class<T> clazz, final String s) {
        final Reference reference = this.toolClasses.get(s);
        Class<?> systemToolClass = (reference == null) ? null : ((Class)reference.get());
        if (systemToolClass == null) {
            try {
                systemToolClass = this.findSystemToolClass(s);
            }
            catch (final Throwable t) {
                return trace(Level.WARNING, t);
            }
            this.toolClasses.put(s, new WeakReference<Class<?>>(systemToolClass));
        }
        return systemToolClass.asSubclass(clazz);
    }
    
    private Class<?> findSystemToolClass(final String s) throws MalformedURLException, ClassNotFoundException {
        try {
            return Class.forName(s, false, null);
        }
        catch (final ClassNotFoundException ex) {
            trace(Level.FINE, ex);
            ClassLoader instance = (this.refToolClassLoader == null) ? null : this.refToolClassLoader.get();
            if (instance == null) {
                File parentFile = new File(System.getProperty("java.home"));
                if (parentFile.getName().equalsIgnoreCase("jre")) {
                    parentFile = parentFile.getParentFile();
                }
                final String[] defaultToolsLocation = ToolProvider.defaultToolsLocation;
                for (int length = defaultToolsLocation.length, i = 0; i < length; ++i) {
                    parentFile = new File(parentFile, defaultToolsLocation[i]);
                }
                if (!parentFile.exists()) {
                    throw ex;
                }
                final URL[] array = { parentFile.toURI().toURL() };
                trace(Level.FINE, array[0].toString());
                instance = URLClassLoader.newInstance(array);
                this.refToolClassLoader = new WeakReference<ClassLoader>(instance);
            }
            return Class.forName(s, false, instance);
        }
    }
    
    static {
        defaultToolsLocation = new String[] { "lib", "tools.jar" };
    }
}
