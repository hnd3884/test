package org.apache.xmlbeans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Node;
import javax.xml.namespace.QName;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class XmlBeans
{
    private static String XMLBEANS_TITLE;
    private static String XMLBEANS_VERSION;
    private static String XMLBEANS_VENDOR;
    private static final ThreadLocal _threadLocalLoaderQNameCache;
    private static final Method _getContextTypeLoaderMethod;
    private static final Method _getBuiltinSchemaTypeSystemMethod;
    private static final Method _getNoTypeMethod;
    private static final Method _typeLoaderBuilderMethod;
    private static final Method _compilationMethod;
    private static final Method _nodeToCursorMethod;
    private static final Method _nodeToXmlObjectMethod;
    private static final Method _nodeToXmlStreamMethod;
    private static final Method _streamToNodeMethod;
    private static final Constructor _pathResourceLoaderConstructor;
    private static final String HOLDER_CLASS_NAME = "TypeSystemHolder";
    private static final String TYPE_SYSTEM_FIELD = "typeSystem";
    public static SchemaType NO_TYPE;
    
    public static final String getTitle() {
        return XmlBeans.XMLBEANS_TITLE;
    }
    
    public static final String getVendor() {
        return XmlBeans.XMLBEANS_VENDOR;
    }
    
    public static final String getVersion() {
        return XmlBeans.XMLBEANS_VERSION;
    }
    
    public static void clearThreadLocals() {
        XmlBeans._threadLocalLoaderQNameCache.remove();
    }
    
    public static QNameCache getQNameCache() {
        final SoftReference softRef = XmlBeans._threadLocalLoaderQNameCache.get();
        QNameCache qnameCache = softRef.get();
        if (qnameCache == null) {
            qnameCache = new QNameCache(32);
            XmlBeans._threadLocalLoaderQNameCache.set(new SoftReference(qnameCache));
        }
        return qnameCache;
    }
    
    public static QName getQName(final String localPart) {
        return getQNameCache().getName("", localPart);
    }
    
    public static QName getQName(final String namespaceUri, final String localPart) {
        return getQNameCache().getName(namespaceUri, localPart);
    }
    
    private static RuntimeException causedException(final RuntimeException e, final Throwable cause) {
        e.initCause(cause);
        return e;
    }
    
    private static XmlException wrappedException(final Throwable e) {
        if (e instanceof XmlException) {
            return (XmlException)e;
        }
        return new XmlException(e.getMessage(), e);
    }
    
    private static final Constructor buildConstructor(final String className, final Class[] args) {
        try {
            return Class.forName(className, false, XmlBeans.class.getClassLoader()).getConstructor((Class<?>[])args);
        }
        catch (final Exception e) {
            throw causedException(new IllegalStateException("Cannot load constructor for " + className + ": verify that xbean.jar is on the classpath"), e);
        }
    }
    
    private static final Method buildMethod(final String className, final String methodName, final Class[] args) {
        try {
            return Class.forName(className, false, XmlBeans.class.getClassLoader()).getMethod(methodName, (Class<?>[])args);
        }
        catch (final Exception e) {
            throw causedException(new IllegalStateException("Cannot load " + methodName + ": verify that xbean.jar is on the classpath"), e);
        }
    }
    
    private static final Method buildNoArgMethod(final String className, final String methodName) {
        return buildMethod(className, methodName, new Class[0]);
    }
    
    private static final Method buildNodeMethod(final String className, final String methodName) {
        return buildMethod(className, methodName, new Class[] { Node.class });
    }
    
    private static Method buildGetContextTypeLoaderMethod() {
        return buildNoArgMethod("org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl", "getContextTypeLoader");
    }
    
    private static final Method buildGetBuiltinSchemaTypeSystemMethod() {
        return buildNoArgMethod("org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem", "get");
    }
    
    private static final Method buildGetNoTypeMethod() {
        return buildNoArgMethod("org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem", "getNoType");
    }
    
    private static final Method buildTypeLoaderBuilderMethod() {
        return buildMethod("org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl", "build", new Class[] { SchemaTypeLoader[].class, ResourceLoader.class, ClassLoader.class });
    }
    
    private static final Method buildCompilationMethod() {
        return buildMethod("org.apache.xmlbeans.impl.schema.SchemaTypeSystemCompiler", "compile", new Class[] { String.class, SchemaTypeSystem.class, XmlObject[].class, BindingConfig.class, SchemaTypeLoader.class, Filer.class, XmlOptions.class });
    }
    
    private static final Method buildNodeToCursorMethod() {
        return buildNodeMethod("org.apache.xmlbeans.impl.store.Locale", "nodeToCursor");
    }
    
    private static final Method buildNodeToXmlObjectMethod() {
        return buildNodeMethod("org.apache.xmlbeans.impl.store.Locale", "nodeToXmlObject");
    }
    
    private static final Method buildNodeToXmlStreamMethod() {
        return buildNodeMethod("org.apache.xmlbeans.impl.store.Locale", "nodeToXmlStream");
    }
    
    private static final Method buildStreamToNodeMethod() {
        return buildMethod("org.apache.xmlbeans.impl.store.Locale", "streamToNode", new Class[] { XMLStreamReader.class });
    }
    
    private static final Constructor buildPathResourceLoaderConstructor() {
        return buildConstructor("org.apache.xmlbeans.impl.schema.PathResourceLoader", new Class[] { File[].class });
    }
    
    public static String compilePath(final String pathExpr) throws XmlException {
        return compilePath(pathExpr, null);
    }
    
    public static String compilePath(final String pathExpr, final XmlOptions options) throws XmlException {
        return getContextTypeLoader().compilePath(pathExpr, options);
    }
    
    public static String compileQuery(final String queryExpr) throws XmlException {
        return compileQuery(queryExpr, null);
    }
    
    public static String compileQuery(final String queryExpr, final XmlOptions options) throws XmlException {
        return getContextTypeLoader().compileQuery(queryExpr, options);
    }
    
    public static SchemaTypeLoader getContextTypeLoader() {
        try {
            return (SchemaTypeLoader)XmlBeans._getContextTypeLoaderMethod.invoke(null, new Object[0]);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to SchemaTypeLoaderImpl.getContextTypeLoader(): verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static SchemaTypeSystem getBuiltinTypeSystem() {
        try {
            return (SchemaTypeSystem)XmlBeans._getBuiltinSchemaTypeSystemMethod.invoke(null, new Object[0]);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to BuiltinSchemaTypeSystem.get(): verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static XmlCursor nodeToCursor(final Node n) {
        try {
            return (XmlCursor)XmlBeans._nodeToCursorMethod.invoke(null, n);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to nodeToCursor verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static XmlObject nodeToXmlObject(final Node n) {
        try {
            return (XmlObject)XmlBeans._nodeToXmlObjectMethod.invoke(null, n);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to nodeToXmlObject verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static XMLStreamReader nodeToXmlStreamReader(final Node n) {
        try {
            return (XMLStreamReader)XmlBeans._nodeToXmlStreamMethod.invoke(null, n);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to nodeToXmlStreamReader verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static Node streamToNode(final XMLStreamReader xs) {
        try {
            return (Node)XmlBeans._streamToNodeMethod.invoke(null, xs);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to streamToNode verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static SchemaTypeLoader loadXsd(final XmlObject[] schemas) throws XmlException {
        return loadXsd(schemas, null);
    }
    
    public static SchemaTypeLoader loadXsd(final XmlObject[] schemas, final XmlOptions options) throws XmlException {
        try {
            final SchemaTypeSystem sts = (SchemaTypeSystem)XmlBeans._compilationMethod.invoke(null, null, null, schemas, null, getContextTypeLoader(), null, options);
            if (sts == null) {
                return null;
            }
            return typeLoaderUnion(new SchemaTypeLoader[] { sts, getContextTypeLoader() });
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to SchemaTypeLoaderImpl.forSchemaXml(): verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            throw wrappedException(e2.getCause());
        }
    }
    
    public static SchemaTypeSystem compileXsd(final XmlObject[] schemas, final SchemaTypeLoader typepath, final XmlOptions options) throws XmlException {
        return compileXmlBeans(null, null, schemas, null, typepath, null, options);
    }
    
    public static SchemaTypeSystem compileXsd(final SchemaTypeSystem system, final XmlObject[] schemas, final SchemaTypeLoader typepath, final XmlOptions options) throws XmlException {
        return compileXmlBeans(null, system, schemas, null, typepath, null, options);
    }
    
    public static SchemaTypeSystem compileXmlBeans(final String name, final SchemaTypeSystem system, final XmlObject[] schemas, final BindingConfig config, final SchemaTypeLoader typepath, final Filer filer, final XmlOptions options) throws XmlException {
        try {
            return (SchemaTypeSystem)XmlBeans._compilationMethod.invoke(null, name, system, schemas, config, (typepath != null) ? typepath : getContextTypeLoader(), filer, options);
        }
        catch (final IllegalAccessException e) {
            throw new IllegalStateException("No access to SchemaTypeLoaderImpl.forSchemaXml(): verify that version of xbean.jar is correct");
        }
        catch (final InvocationTargetException e2) {
            throw wrappedException(e2.getCause());
        }
    }
    
    public static SchemaTypeLoader typeLoaderUnion(final SchemaTypeLoader[] typeLoaders) {
        try {
            if (typeLoaders.length == 1) {
                return typeLoaders[0];
            }
            return (SchemaTypeLoader)XmlBeans._typeLoaderBuilderMethod.invoke(null, typeLoaders, null, null);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to SchemaTypeLoaderImpl: verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static SchemaTypeLoader typeLoaderForClassLoader(final ClassLoader loader) {
        try {
            return (SchemaTypeLoader)XmlBeans._typeLoaderBuilderMethod.invoke(null, null, null, loader);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to SchemaTypeLoaderImpl: verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static SchemaTypeLoader typeLoaderForResource(final ResourceLoader resourceLoader) {
        try {
            return (SchemaTypeLoader)XmlBeans._typeLoaderBuilderMethod.invoke(null, null, resourceLoader, null);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to SchemaTypeLoaderImpl: verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static SchemaTypeSystem typeSystemForClassLoader(final ClassLoader loader, final String stsName) {
        try {
            final ClassLoader cl = (loader == null) ? Thread.currentThread().getContextClassLoader() : loader;
            final Class clazz = cl.loadClass(stsName + "." + "TypeSystemHolder");
            final SchemaTypeSystem sts = (SchemaTypeSystem)clazz.getDeclaredField("typeSystem").get(null);
            if (sts == null) {
                throw new RuntimeException("SchemaTypeSystem is null for field typeSystem on class with name " + stsName + "." + "TypeSystemHolder" + ". Please verify the version of xbean.jar is correct.");
            }
            return sts;
        }
        catch (final ClassNotFoundException e) {
            throw causedException(new RuntimeException("Cannot load SchemaTypeSystem. Unable to load class with name " + stsName + "." + "TypeSystemHolder" + ". Make sure the generated binary files are on the classpath."), e);
        }
        catch (final NoSuchFieldException e2) {
            throw causedException(new RuntimeException("Cannot find field typeSystem on class " + stsName + "." + "TypeSystemHolder" + ". Please verify the version of xbean.jar is correct."), e2);
        }
        catch (final IllegalAccessException e3) {
            throw causedException(new RuntimeException("Field typeSystem on class " + stsName + "." + "TypeSystemHolder" + "is not accessible. Please verify the version of xbean.jar is correct."), e3);
        }
    }
    
    public static ResourceLoader resourceLoaderForPath(final File[] path) {
        try {
            return XmlBeans._pathResourceLoaderConstructor.newInstance(path);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to SchemaTypeLoaderImpl: verify that version of xbean.jar is correct"), e);
        }
        catch (final InstantiationException e2) {
            throw causedException(new IllegalStateException(e2.getMessage()), e2);
        }
        catch (final InvocationTargetException e3) {
            final Throwable t = e3.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    public static SchemaType typeForClass(final Class c) {
        if (c == null || !XmlObject.class.isAssignableFrom(c)) {
            return null;
        }
        try {
            final Field typeField = c.getField("type");
            if (typeField == null) {
                return null;
            }
            return (SchemaType)typeField.get(null);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private static SchemaType getNoType() {
        try {
            return (SchemaType)XmlBeans._getNoTypeMethod.invoke(null, new Object[0]);
        }
        catch (final IllegalAccessException e) {
            throw causedException(new IllegalStateException("No access to SchemaTypeLoaderImpl.getContextTypeLoader(): verify that version of xbean.jar is correct"), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
    }
    
    private XmlBeans() {
    }
    
    static {
        XmlBeans.XMLBEANS_TITLE = "org.apache.xmlbeans";
        XmlBeans.XMLBEANS_VERSION = "3.1.0";
        XmlBeans.XMLBEANS_VENDOR = "Apache Software Foundation";
        final Package pkg = XmlBeans.class.getPackage();
        if (pkg != null && pkg.getImplementationVersion() != null) {
            XmlBeans.XMLBEANS_TITLE = pkg.getImplementationTitle();
            XmlBeans.XMLBEANS_VERSION = pkg.getImplementationVersion();
            XmlBeans.XMLBEANS_VENDOR = pkg.getImplementationVendor();
        }
        _threadLocalLoaderQNameCache = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new SoftReference(new QNameCache(32));
            }
        };
        _getContextTypeLoaderMethod = buildGetContextTypeLoaderMethod();
        _getBuiltinSchemaTypeSystemMethod = buildGetBuiltinSchemaTypeSystemMethod();
        _getNoTypeMethod = buildGetNoTypeMethod();
        _typeLoaderBuilderMethod = buildTypeLoaderBuilderMethod();
        _compilationMethod = buildCompilationMethod();
        _nodeToCursorMethod = buildNodeToCursorMethod();
        _nodeToXmlObjectMethod = buildNodeToXmlObjectMethod();
        _nodeToXmlStreamMethod = buildNodeToXmlStreamMethod();
        _streamToNodeMethod = buildStreamToNodeMethod();
        _pathResourceLoaderConstructor = buildPathResourceLoaderConstructor();
        XmlBeans.NO_TYPE = getNoType();
    }
}
