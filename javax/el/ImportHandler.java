package javax.el;

import java.util.HashSet;
import java.util.HashMap;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Iterator;
import java.util.Collections;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Map;

public class ImportHandler
{
    private static final boolean IS_SECURITY_ENABLED;
    private static final Map<String, Set<String>> standardPackages;
    private Map<String, Set<String>> packageNames;
    private ConcurrentHashMap<String, String> classNames;
    private Map<String, Class<?>> clazzes;
    private Map<String, Class<?>> statics;
    
    public ImportHandler() {
        this.packageNames = new ConcurrentHashMap<String, Set<String>>();
        this.classNames = new ConcurrentHashMap<String, String>();
        this.clazzes = new ConcurrentHashMap<String, Class<?>>();
        this.statics = new ConcurrentHashMap<String, Class<?>>();
        this.importPackage("java.lang");
    }
    
    public void importStatic(final String name) throws ELException {
        final int lastPeriod = name.lastIndexOf(46);
        if (lastPeriod < 0) {
            throw new ELException(Util.message(null, "importHandler.invalidStaticName", name));
        }
        final String className = name.substring(0, lastPeriod);
        final String fieldOrMethodName = name.substring(lastPeriod + 1);
        final Class<?> clazz = this.findClass(className, true);
        if (clazz == null) {
            throw new ELException(Util.message(null, "importHandler.invalidClassNameForStatic", className, name));
        }
        boolean found = false;
        for (final Field field : clazz.getFields()) {
            if (field.getName().equals(fieldOrMethodName)) {
                final int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            for (final Method method : clazz.getMethods()) {
                if (method.getName().equals(fieldOrMethodName)) {
                    final int modifiers = method.getModifiers();
                    if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found) {
            throw new ELException(Util.message(null, "importHandler.staticNotFound", fieldOrMethodName, className, name));
        }
        final Class<?> conflict = this.statics.get(fieldOrMethodName);
        if (conflict != null) {
            throw new ELException(Util.message(null, "importHandler.ambiguousStaticImport", name, conflict.getName() + '.' + fieldOrMethodName));
        }
        this.statics.put(fieldOrMethodName, clazz);
    }
    
    public void importClass(final String name) throws ELException {
        final int lastPeriodIndex = name.lastIndexOf(46);
        if (lastPeriodIndex < 0) {
            throw new ELException(Util.message(null, "importHandler.invalidClassName", name));
        }
        final String unqualifiedName = name.substring(lastPeriodIndex + 1);
        final String currentName = this.classNames.putIfAbsent(unqualifiedName, name);
        if (currentName != null && !currentName.equals(name)) {
            throw new ELException(Util.message(null, "importHandler.ambiguousImport", name, currentName));
        }
    }
    
    public void importPackage(final String name) {
        Set<String> preloaded = ImportHandler.standardPackages.get(name);
        if (preloaded == null) {
            preloaded = Collections.emptySet();
        }
        this.packageNames.put(name, preloaded);
    }
    
    public Class<?> resolveClass(final String name) {
        if (name == null || name.contains(".")) {
            return null;
        }
        Class<?> result = this.clazzes.get(name);
        if (result == null) {
            String className = this.classNames.get(name);
            if (className != null) {
                final Class<?> clazz = this.findClass(className, true);
                if (clazz != null) {
                    this.clazzes.put(name, clazz);
                    return clazz;
                }
            }
            for (final Map.Entry<String, Set<String>> entry : this.packageNames.entrySet()) {
                if (!entry.getValue().isEmpty() && !entry.getValue().contains(name)) {
                    continue;
                }
                className = entry.getKey() + '.' + name;
                final Class<?> clazz2 = this.findClass(className, false);
                if (clazz2 == null) {
                    continue;
                }
                if (result != null) {
                    throw new ELException(Util.message(null, "importHandler.ambiguousImport", className, result.getName()));
                }
                result = clazz2;
            }
            if (result == null) {
                this.clazzes.put(name, NotFound.class);
            }
            else {
                this.clazzes.put(name, result);
            }
            return result;
        }
        if (NotFound.class.equals(result)) {
            return null;
        }
        return result;
    }
    
    public Class<?> resolveStatic(final String name) {
        return this.statics.get(name);
    }
    
    private Class<?> findClass(final String name, final boolean throwException) {
        final ClassLoader cl = Util.getContextClassLoader();
        final String path = name.replace('.', '/') + ".class";
        try {
            if (ImportHandler.IS_SECURITY_ENABLED) {
                if (!AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedResourceExists(cl, path))) {
                    return null;
                }
            }
            else if (cl.getResource(path) == null) {
                return null;
            }
        }
        catch (final ClassCircularityError classCircularityError) {}
        Class<?> clazz;
        try {
            clazz = cl.loadClass(name);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
        final JreCompat jreCompat = JreCompat.getInstance();
        final int modifiers = clazz.getModifiers();
        if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && jreCompat.isExported(clazz)) {
            return clazz;
        }
        if (throwException) {
            throw new ELException(Util.message(null, "importHandler.invalidClass", name));
        }
        return null;
    }
    
    static {
        IS_SECURITY_ENABLED = (System.getSecurityManager() != null);
        standardPackages = new HashMap<String, Set<String>>();
        final Set<String> servletClassNames = new HashSet<String>();
        servletClassNames.add("AsyncContext");
        servletClassNames.add("AsyncListener");
        servletClassNames.add("Filter");
        servletClassNames.add("FilterChain");
        servletClassNames.add("FilterConfig");
        servletClassNames.add("FilterRegistration");
        servletClassNames.add("FilterRegistration.Dynamic");
        servletClassNames.add("ReadListener");
        servletClassNames.add("Registration");
        servletClassNames.add("Registration.Dynamic");
        servletClassNames.add("RequestDispatcher");
        servletClassNames.add("Servlet");
        servletClassNames.add("ServletConfig");
        servletClassNames.add("ServletContainerInitializer");
        servletClassNames.add("ServletContext");
        servletClassNames.add("ServletContextAttributeListener");
        servletClassNames.add("ServletContextListener");
        servletClassNames.add("ServletRegistration");
        servletClassNames.add("ServletRegistration.Dynamic");
        servletClassNames.add("ServletRequest");
        servletClassNames.add("ServletRequestAttributeListener");
        servletClassNames.add("ServletRequestListener");
        servletClassNames.add("ServletResponse");
        servletClassNames.add("SessionCookieConfig");
        servletClassNames.add("SingleThreadModel");
        servletClassNames.add("WriteListener");
        servletClassNames.add("AsyncEvent");
        servletClassNames.add("GenericFilter");
        servletClassNames.add("GenericServlet");
        servletClassNames.add("HttpConstraintElement");
        servletClassNames.add("HttpMethodConstraintElement");
        servletClassNames.add("MultipartConfigElement");
        servletClassNames.add("ServletContextAttributeEvent");
        servletClassNames.add("ServletContextEvent");
        servletClassNames.add("ServletInputStream");
        servletClassNames.add("ServletOutputStream");
        servletClassNames.add("ServletRequestAttributeEvent");
        servletClassNames.add("ServletRequestEvent");
        servletClassNames.add("ServletRequestWrapper");
        servletClassNames.add("ServletResponseWrapper");
        servletClassNames.add("ServletSecurityElement");
        servletClassNames.add("DispatcherType");
        servletClassNames.add("SessionTrackingMode");
        servletClassNames.add("ServletException");
        servletClassNames.add("UnavailableException");
        ImportHandler.standardPackages.put("javax.servlet", servletClassNames);
        final Set<String> servletHttpClassNames = new HashSet<String>();
        servletHttpClassNames.add("HttpServletMapping");
        servletHttpClassNames.add("HttpServletRequest");
        servletHttpClassNames.add("HttpServletResponse");
        servletHttpClassNames.add("HttpSession");
        servletHttpClassNames.add("HttpSessionActivationListener");
        servletHttpClassNames.add("HttpSessionAttributeListener");
        servletHttpClassNames.add("HttpSessionBindingListener");
        servletHttpClassNames.add("HttpSessionContext");
        servletHttpClassNames.add("HttpSessionIdListener");
        servletHttpClassNames.add("HttpSessionListener");
        servletHttpClassNames.add("HttpUpgradeHandler");
        servletHttpClassNames.add("Part");
        servletHttpClassNames.add("PushBuilder");
        servletHttpClassNames.add("WebConnection");
        servletHttpClassNames.add("Cookie");
        servletHttpClassNames.add("HttpFilter");
        servletHttpClassNames.add("HttpServlet");
        servletHttpClassNames.add("HttpServletRequestWrapper");
        servletHttpClassNames.add("HttpServletResponseWrapper");
        servletHttpClassNames.add("HttpSessionBindingEvent");
        servletHttpClassNames.add("HttpSessionEvent");
        servletHttpClassNames.add("HttpUtils");
        servletHttpClassNames.add("ApplicationMappingMatch");
        ImportHandler.standardPackages.put("javax.servlet.http", servletHttpClassNames);
        final Set<String> servletJspClassNames = new HashSet<String>();
        servletJspClassNames.add("HttpJspPage");
        servletJspClassNames.add("JspApplicationContext");
        servletJspClassNames.add("JspPage");
        servletJspClassNames.add("ErrorData");
        servletJspClassNames.add("JspContext");
        servletJspClassNames.add("JspEngineInfo");
        servletJspClassNames.add("JspFactory");
        servletJspClassNames.add("JspWriter");
        servletJspClassNames.add("PageContext");
        servletJspClassNames.add("Exceptions");
        servletJspClassNames.add("JspException");
        servletJspClassNames.add("JspTagException");
        servletJspClassNames.add("SkipPageException");
        ImportHandler.standardPackages.put("javax.servlet.jsp", servletJspClassNames);
        final Set<String> javaLangClassNames = new HashSet<String>();
        javaLangClassNames.add("Appendable");
        javaLangClassNames.add("AutoCloseable");
        javaLangClassNames.add("CharSequence");
        javaLangClassNames.add("Cloneable");
        javaLangClassNames.add("Comparable");
        javaLangClassNames.add("Iterable");
        javaLangClassNames.add("ProcessHandle");
        javaLangClassNames.add("ProcessHandle.Info");
        javaLangClassNames.add("Readable");
        javaLangClassNames.add("Runnable");
        javaLangClassNames.add("StackWalker.StackFrame");
        javaLangClassNames.add("System.Logger");
        javaLangClassNames.add("Thread.UncaughtExceptionHandler");
        javaLangClassNames.add("Boolean");
        javaLangClassNames.add("Byte");
        javaLangClassNames.add("Character");
        javaLangClassNames.add("Character.Subset");
        javaLangClassNames.add("Character.UnicodeBlock");
        javaLangClassNames.add("Class");
        javaLangClassNames.add("ClassLoader");
        javaLangClassNames.add("ClassValue");
        javaLangClassNames.add("Compiler");
        javaLangClassNames.add("Double");
        javaLangClassNames.add("Enum");
        javaLangClassNames.add("Enum.EnumDesc");
        javaLangClassNames.add("Float");
        javaLangClassNames.add("InheritableThreadLocal");
        javaLangClassNames.add("Integer");
        javaLangClassNames.add("Long");
        javaLangClassNames.add("Math");
        javaLangClassNames.add("Module");
        javaLangClassNames.add("ModuleLayer");
        javaLangClassNames.add("ModuleLayer.Controller");
        javaLangClassNames.add("Number");
        javaLangClassNames.add("Object");
        javaLangClassNames.add("Package");
        javaLangClassNames.add("Process");
        javaLangClassNames.add("ProcessBuilder");
        javaLangClassNames.add("ProcessBuilder.Redirect");
        javaLangClassNames.add("Record");
        javaLangClassNames.add("Runtime");
        javaLangClassNames.add("Runtime.Version");
        javaLangClassNames.add("RuntimePermission");
        javaLangClassNames.add("SecurityManager");
        javaLangClassNames.add("Short");
        javaLangClassNames.add("StackTraceElement");
        javaLangClassNames.add("StackWalker");
        javaLangClassNames.add("StrictMath");
        javaLangClassNames.add("String");
        javaLangClassNames.add("StringBuffer");
        javaLangClassNames.add("StringBuilder");
        javaLangClassNames.add("System");
        javaLangClassNames.add("System.LoggerFinder");
        javaLangClassNames.add("Thread");
        javaLangClassNames.add("ThreadGroup");
        javaLangClassNames.add("ThreadLocal");
        javaLangClassNames.add("Throwable");
        javaLangClassNames.add("Void");
        javaLangClassNames.add("Character.UnicodeScript");
        javaLangClassNames.add("ProcessBuilder.Redirect.Type");
        javaLangClassNames.add("StackWalker.Option");
        javaLangClassNames.add("System.Logger.Level");
        javaLangClassNames.add("Thread.State");
        javaLangClassNames.add("ArithmeticException");
        javaLangClassNames.add("ArrayIndexOutOfBoundsException");
        javaLangClassNames.add("ArrayStoreException");
        javaLangClassNames.add("ClassCastException");
        javaLangClassNames.add("ClassNotFoundException");
        javaLangClassNames.add("CloneNotSupportedException");
        javaLangClassNames.add("EnumConstantNotPresentException");
        javaLangClassNames.add("Exception");
        javaLangClassNames.add("IllegalAccessException");
        javaLangClassNames.add("IllegalArgumentException");
        javaLangClassNames.add("IllegalCallerException");
        javaLangClassNames.add("IllegalMonitorStateException");
        javaLangClassNames.add("IllegalStateException");
        javaLangClassNames.add("IllegalThreadStateException");
        javaLangClassNames.add("IndexOutOfBoundsException");
        javaLangClassNames.add("InstantiationException");
        javaLangClassNames.add("InterruptedException");
        javaLangClassNames.add("LayerInstantiationException");
        javaLangClassNames.add("NegativeArraySizeException");
        javaLangClassNames.add("NoSuchFieldException");
        javaLangClassNames.add("NoSuchMethodException");
        javaLangClassNames.add("NullPointerException");
        javaLangClassNames.add("NumberFormatException");
        javaLangClassNames.add("ReflectiveOperationException");
        javaLangClassNames.add("RuntimeException");
        javaLangClassNames.add("SecurityException");
        javaLangClassNames.add("StringIndexOutOfBoundsException");
        javaLangClassNames.add("TypeNotPresentException");
        javaLangClassNames.add("UnsupportedOperationException");
        javaLangClassNames.add("AbstractMethodError");
        javaLangClassNames.add("AssertionError");
        javaLangClassNames.add("BootstrapMethodError");
        javaLangClassNames.add("ClassCircularityError");
        javaLangClassNames.add("ClassFormatError");
        javaLangClassNames.add("Error");
        javaLangClassNames.add("ExceptionInInitializerError");
        javaLangClassNames.add("IllegalAccessError");
        javaLangClassNames.add("IncompatibleClassChangeError");
        javaLangClassNames.add("InstantiationError");
        javaLangClassNames.add("InternalError");
        javaLangClassNames.add("LinkageError");
        javaLangClassNames.add("NoClassDefFoundError");
        javaLangClassNames.add("NoSuchFieldError");
        javaLangClassNames.add("NoSuchMethodError");
        javaLangClassNames.add("OutOfMemoryError");
        javaLangClassNames.add("StackOverflowError");
        javaLangClassNames.add("ThreadDeath");
        javaLangClassNames.add("UnknownError");
        javaLangClassNames.add("UnsatisfiedLinkError");
        javaLangClassNames.add("UnsupportedClassVersionError");
        javaLangClassNames.add("VerifyError");
        javaLangClassNames.add("VirtualMachineError");
        javaLangClassNames.add("Deprecated");
        javaLangClassNames.add("FunctionalInterface");
        javaLangClassNames.add("Override");
        javaLangClassNames.add("SafeVarargs");
        javaLangClassNames.add("SuppressWarnings");
        ImportHandler.standardPackages.put("java.lang", javaLangClassNames);
    }
    
    private static class NotFound
    {
    }
    
    private static class PrivilegedResourceExists implements PrivilegedAction<Boolean>
    {
        private final ClassLoader cl;
        private final String name;
        
        public PrivilegedResourceExists(final ClassLoader cl, final String name) {
            this.cl = cl;
            this.name = name;
        }
        
        @Override
        public Boolean run() {
            if (this.cl.getResource(this.name) == null) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
    }
}
