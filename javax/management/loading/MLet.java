package javax.management.loading;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.io.IOException;
import javax.management.InstanceNotFoundException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import java.util.StringTokenizer;
import java.util.HashSet;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.File;
import java.util.Set;
import java.net.MalformedURLException;
import javax.management.ServiceNotFoundException;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.URLStreamHandlerFactory;
import java.util.Map;
import java.net.URL;
import javax.management.ObjectName;
import java.util.List;
import javax.management.MBeanServer;
import java.io.Externalizable;
import javax.management.MBeanRegistration;
import java.net.URLClassLoader;

public class MLet extends URLClassLoader implements MLetMBean, MBeanRegistration, Externalizable
{
    private static final long serialVersionUID = 3636148327800330130L;
    private MBeanServer server;
    private List<MLetContent> mletList;
    private String libraryDirectory;
    private ObjectName mletObjectName;
    private URL[] myUrls;
    private transient ClassLoaderRepository currentClr;
    private transient boolean delegateToCLR;
    private Map<String, Class<?>> primitiveClasses;
    
    public MLet() {
        this(new URL[0]);
    }
    
    public MLet(final URL[] array) {
        this(array, true);
    }
    
    public MLet(final URL[] array, final ClassLoader classLoader) {
        this(array, classLoader, true);
    }
    
    public MLet(final URL[] array, final ClassLoader classLoader, final URLStreamHandlerFactory urlStreamHandlerFactory) {
        this(array, classLoader, urlStreamHandlerFactory, true);
    }
    
    public MLet(final URL[] array, final boolean b) {
        super(array);
        this.server = null;
        this.mletList = new ArrayList<MLetContent>();
        this.mletObjectName = null;
        this.myUrls = null;
        (this.primitiveClasses = new HashMap<String, Class<?>>(8)).put(Boolean.TYPE.toString(), Boolean.class);
        this.primitiveClasses.put(Character.TYPE.toString(), Character.class);
        this.primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
        this.primitiveClasses.put(Short.TYPE.toString(), Short.class);
        this.primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
        this.primitiveClasses.put(Long.TYPE.toString(), Long.class);
        this.primitiveClasses.put(Float.TYPE.toString(), Float.class);
        this.primitiveClasses.put(Double.TYPE.toString(), Double.class);
        this.init(b);
    }
    
    public MLet(final URL[] array, final ClassLoader classLoader, final boolean b) {
        super(array, classLoader);
        this.server = null;
        this.mletList = new ArrayList<MLetContent>();
        this.mletObjectName = null;
        this.myUrls = null;
        (this.primitiveClasses = new HashMap<String, Class<?>>(8)).put(Boolean.TYPE.toString(), Boolean.class);
        this.primitiveClasses.put(Character.TYPE.toString(), Character.class);
        this.primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
        this.primitiveClasses.put(Short.TYPE.toString(), Short.class);
        this.primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
        this.primitiveClasses.put(Long.TYPE.toString(), Long.class);
        this.primitiveClasses.put(Float.TYPE.toString(), Float.class);
        this.primitiveClasses.put(Double.TYPE.toString(), Double.class);
        this.init(b);
    }
    
    public MLet(final URL[] array, final ClassLoader classLoader, final URLStreamHandlerFactory urlStreamHandlerFactory, final boolean b) {
        super(array, classLoader, urlStreamHandlerFactory);
        this.server = null;
        this.mletList = new ArrayList<MLetContent>();
        this.mletObjectName = null;
        this.myUrls = null;
        (this.primitiveClasses = new HashMap<String, Class<?>>(8)).put(Boolean.TYPE.toString(), Boolean.class);
        this.primitiveClasses.put(Character.TYPE.toString(), Character.class);
        this.primitiveClasses.put(Byte.TYPE.toString(), Byte.class);
        this.primitiveClasses.put(Short.TYPE.toString(), Short.class);
        this.primitiveClasses.put(Integer.TYPE.toString(), Integer.class);
        this.primitiveClasses.put(Long.TYPE.toString(), Long.class);
        this.primitiveClasses.put(Float.TYPE.toString(), Float.class);
        this.primitiveClasses.put(Double.TYPE.toString(), Double.class);
        this.init(b);
    }
    
    private void init(final boolean delegateToCLR) {
        this.delegateToCLR = delegateToCLR;
        try {
            this.libraryDirectory = System.getProperty("jmx.mlet.library.dir");
            if (this.libraryDirectory == null) {
                this.libraryDirectory = this.getTmpDir();
            }
        }
        catch (final SecurityException ex) {}
    }
    
    @Override
    public void addURL(final URL url) {
        if (!Arrays.asList(this.getURLs()).contains(url)) {
            super.addURL(url);
        }
    }
    
    @Override
    public void addURL(final String s) throws ServiceNotFoundException {
        try {
            final URL url = new URL(s);
            if (!Arrays.asList(this.getURLs()).contains(url)) {
                super.addURL(url);
            }
        }
        catch (final MalformedURLException ex) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "addUrl", "Malformed URL: " + s, ex);
            }
            throw new ServiceNotFoundException("The specified URL is malformed");
        }
    }
    
    @Override
    public URL[] getURLs() {
        return super.getURLs();
    }
    
    @Override
    public Set<Object> getMBeansFromURL(final URL url) throws ServiceNotFoundException {
        if (url == null) {
            throw new ServiceNotFoundException("The specified URL is null");
        }
        return this.getMBeansFromURL(url.toString());
    }
    
    @Override
    public Set<Object> getMBeansFromURL(String replace) throws ServiceNotFoundException {
        final String s = "getMBeansFromURL";
        if (this.server == null) {
            throw new IllegalStateException("This MLet MBean is not registered with an MBeanServer.");
        }
        if (replace == null) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "URL is null");
            throw new ServiceNotFoundException("The specified URL is null");
        }
        replace = replace.replace(File.separatorChar, '/');
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "<URL = " + replace + ">");
        }
        try {
            this.mletList = new MLetParser().parseURL(replace);
        }
        catch (final Exception ex) {
            final String string = "Problems while parsing URL [" + replace + "], got exception [" + ex.toString() + "]";
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, string);
            throw EnvHelp.initCause(new ServiceNotFoundException(string), ex);
        }
        if (this.mletList.size() == 0) {
            final String string2 = "File " + replace + " not found or MLET tag not defined in file";
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, string2);
            throw new ServiceNotFoundException(string2);
        }
        final HashSet set = new HashSet();
        for (final MLetContent mLetContent : this.mletList) {
            String s2 = mLetContent.getCode();
            if (s2 != null && s2.endsWith(".class")) {
                s2 = s2.substring(0, s2.length() - 6);
            }
            final String name = mLetContent.getName();
            URL url = mLetContent.getCodeBase();
            final String version = mLetContent.getVersion();
            final String serializedObject = mLetContent.getSerializedObject();
            final String jarFiles = mLetContent.getJarFiles();
            final URL documentBase = mLetContent.getDocumentBase();
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "\n\tMLET TAG     = " + mLetContent.getAttributes() + "\n\tCODEBASE     = " + url + "\n\tARCHIVE      = " + jarFiles + "\n\tCODE         = " + s2 + "\n\tOBJECT       = " + serializedObject + "\n\tNAME         = " + name + "\n\tVERSION      = " + version + "\n\tDOCUMENT URL = " + documentBase);
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(jarFiles, ",", false);
            while (stringTokenizer.hasMoreTokens()) {
                final String trim = stringTokenizer.nextToken().trim();
                if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "Load archive for codebase <" + url + ">, file <" + trim + ">");
                }
                try {
                    url = this.check(version, url, trim, mLetContent);
                }
                catch (final Exception ex2) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), s, "Got unexpected exception", ex2);
                    set.add(ex2);
                    continue;
                }
                try {
                    if (Arrays.asList(this.getURLs()).contains(new URL(url.toString() + trim))) {
                        continue;
                    }
                    this.addURL(url + trim);
                }
                catch (final MalformedURLException ex3) {}
            }
            if (s2 != null && serializedObject != null) {
                JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "CODE and OBJECT parameters cannot be specified at the same time in tag MLET");
                set.add(new Error("CODE and OBJECT parameters cannot be specified at the same time in tag MLET"));
            }
            else if (s2 == null && serializedObject == null) {
                JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "Either CODE or OBJECT parameter must be specified in tag MLET");
                set.add(new Error("Either CODE or OBJECT parameter must be specified in tag MLET"));
            }
            else {
                ObjectInstance objectInstance;
                try {
                    if (s2 != null) {
                        final List<String> parameterTypes = mLetContent.getParameterTypes();
                        final List<String> parameterValues = mLetContent.getParameterValues();
                        final ArrayList list = new ArrayList();
                        for (int i = 0; i < parameterTypes.size(); ++i) {
                            list.add(this.constructParameter(parameterValues.get(i), (String)parameterTypes.get(i)));
                        }
                        if (parameterTypes.isEmpty()) {
                            if (name == null) {
                                objectInstance = this.server.createMBean(s2, null, this.mletObjectName);
                            }
                            else {
                                objectInstance = this.server.createMBean(s2, new ObjectName(name), this.mletObjectName);
                            }
                        }
                        else {
                            final Object[] array = list.toArray();
                            final String[] array2 = new String[parameterTypes.size()];
                            parameterTypes.toArray(array2);
                            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                                final StringBuilder sb = new StringBuilder();
                                for (int j = 0; j < array2.length; ++j) {
                                    sb.append("\n\tSignature     = ").append(array2[j]).append("\t\nParams        = ").append(array[j]);
                                }
                                JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), s, sb.toString());
                            }
                            if (name == null) {
                                objectInstance = this.server.createMBean(s2, null, this.mletObjectName, array, array2);
                            }
                            else {
                                objectInstance = this.server.createMBean(s2, new ObjectName(name), this.mletObjectName, array, array2);
                            }
                        }
                    }
                    else {
                        final Object loadSerializedObject = this.loadSerializedObject(url, serializedObject);
                        if (name == null) {
                            this.server.registerMBean(loadSerializedObject, null);
                        }
                        else {
                            this.server.registerMBean(loadSerializedObject, new ObjectName(name));
                        }
                        objectInstance = new ObjectInstance(name, loadSerializedObject.getClass().getName());
                    }
                }
                catch (final ReflectionException ex4) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "ReflectionException", ex4);
                    set.add(ex4);
                    continue;
                }
                catch (final InstanceAlreadyExistsException ex5) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "InstanceAlreadyExistsException", ex5);
                    set.add(ex5);
                    continue;
                }
                catch (final MBeanRegistrationException ex6) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "MBeanRegistrationException", ex6);
                    set.add(ex6);
                    continue;
                }
                catch (final MBeanException ex7) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "MBeanException", ex7);
                    set.add(ex7);
                    continue;
                }
                catch (final NotCompliantMBeanException ex8) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "NotCompliantMBeanException", ex8);
                    set.add(ex8);
                    continue;
                }
                catch (final InstanceNotFoundException ex9) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "InstanceNotFoundException", ex9);
                    set.add(ex9);
                    continue;
                }
                catch (final IOException ex10) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "IOException", ex10);
                    set.add(ex10);
                    continue;
                }
                catch (final SecurityException ex11) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "SecurityException", ex11);
                    set.add(ex11);
                    continue;
                }
                catch (final Exception ex12) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "Exception", ex12);
                    set.add(ex12);
                    continue;
                }
                catch (final Error error) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s, "Error", error);
                    set.add(error);
                    continue;
                }
                set.add(objectInstance);
            }
        }
        return set;
    }
    
    @Override
    public synchronized String getLibraryDirectory() {
        return this.libraryDirectory;
    }
    
    @Override
    public synchronized void setLibraryDirectory(final String libraryDirectory) {
        this.libraryDirectory = libraryDirectory;
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer mBeanServer, ObjectName mletObjectName) throws Exception {
        this.setMBeanServer(mBeanServer);
        if (mletObjectName == null) {
            mletObjectName = new ObjectName(mBeanServer.getDefaultDomain() + ":" + "type=MLet");
        }
        return this.mletObjectName = mletObjectName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
    }
    
    @Override
    public void preDeregister() throws Exception {
    }
    
    @Override
    public void postDeregister() {
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("MLet.writeExternal");
    }
    
    @Override
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException, UnsupportedOperationException {
        throw new UnsupportedOperationException("MLet.readExternal");
    }
    
    public synchronized Class<?> loadClass(final String s, final ClassLoaderRepository currentClr) throws ClassNotFoundException {
        final ClassLoaderRepository currentClr2 = this.currentClr;
        try {
            this.currentClr = currentClr;
            return this.loadClass(s);
        }
        finally {
            this.currentClr = currentClr2;
        }
    }
    
    @Override
    protected Class<?> findClass(final String s) throws ClassNotFoundException {
        return this.findClass(s, this.currentClr);
    }
    
    Class<?> findClass(final String s, final ClassLoaderRepository classLoaderRepository) throws ClassNotFoundException {
        Class<?> clazz = null;
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", s);
        try {
            clazz = super.findClass(s);
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + s + " loaded through MLet classloader");
            }
        }
        catch (final ClassNotFoundException ex) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + s + " not found locally");
            }
        }
        if (clazz == null && this.delegateToCLR && classLoaderRepository != null) {
            try {
                if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + s + " : looking in CLR");
                }
                clazz = classLoaderRepository.loadClassBefore(this, s);
                if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + s + " loaded through the default classloader repository");
                }
            }
            catch (final ClassNotFoundException ex2) {
                if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + s + " not found in CLR");
                }
            }
        }
        if (clazz == null) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Failed to load class " + s);
            throw new ClassNotFoundException(s);
        }
        return clazz;
    }
    
    @Override
    protected String findLibrary(final String s) {
        final String s2 = "findLibrary";
        final String mapLibraryName = System.mapLibraryName(s);
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s2, "Search " + s + " in all JAR files");
        }
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s2, "loadLibraryAsResource(" + mapLibraryName + ")");
        }
        final String loadLibraryAsResource = this.loadLibraryAsResource(mapLibraryName);
        if (loadLibraryAsResource != null) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s2, mapLibraryName + " loaded, absolute path = " + loadLibraryAsResource);
            }
            return loadLibraryAsResource;
        }
        final String string = removeSpace(System.getProperty("os.name")) + File.separator + removeSpace(System.getProperty("os.arch")) + File.separator + removeSpace(System.getProperty("os.version")) + File.separator + "lib" + File.separator + mapLibraryName;
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s2, "loadLibraryAsResource(" + string + ")");
        }
        final String loadLibraryAsResource2 = this.loadLibraryAsResource(string);
        if (loadLibraryAsResource2 != null) {
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s2, string + " loaded, absolute path = " + loadLibraryAsResource2);
            }
            return loadLibraryAsResource2;
        }
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s2, s + " not found in any JAR file");
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), s2, "Search " + s + " along the path specified as the java.library.path property");
        }
        return null;
    }
    
    private String getTmpDir() {
        final String property = System.getProperty("java.io.tmpdir");
        if (property != null) {
            return property;
        }
        File tempFile = null;
        try {
            tempFile = File.createTempFile("tmp", "jmx");
            if (tempFile == null) {
                return null;
            }
            final File parentFile = tempFile.getParentFile();
            if (parentFile == null) {
                return null;
            }
            return parentFile.getAbsolutePath();
        }
        catch (final Exception ex) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to determine system temporary dir");
            return null;
        }
        finally {
            if (tempFile != null) {
                try {
                    if (!tempFile.delete()) {
                        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file");
                    }
                }
                catch (final Exception ex2) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temporary file", ex2);
                }
            }
        }
    }
    
    private synchronized String loadLibraryAsResource(final String s) {
        try {
            final InputStream resourceAsStream = this.getResourceAsStream(s.replace(File.separatorChar, '/'));
            if (resourceAsStream != null) {
                try {
                    final File file = new File(this.libraryDirectory);
                    file.mkdirs();
                    final File file2 = Files.createTempFile(file.toPath(), s + ".", null, (FileAttribute<?>[])new FileAttribute[0]).toFile();
                    file2.deleteOnExit();
                    final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    try {
                        final byte[] array = new byte[4096];
                        int read;
                        while ((read = resourceAsStream.read(array)) >= 0) {
                            fileOutputStream.write(array, 0, read);
                        }
                    }
                    finally {
                        fileOutputStream.close();
                    }
                    if (file2.exists()) {
                        return file2.getAbsolutePath();
                    }
                }
                finally {
                    resourceAsStream.close();
                }
            }
        }
        catch (final Exception ex) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadLibraryAsResource", "Failed to load library : " + s, ex);
            return null;
        }
        return null;
    }
    
    private static String removeSpace(final String s) {
        return s.trim().replace(" ", "");
    }
    
    protected URL check(final String s, final URL url, final String s2, final MLetContent mLetContent) throws Exception {
        return url;
    }
    
    private Object loadSerializedObject(final URL url, String replace) throws IOException, ClassNotFoundException {
        if (replace != null) {
            replace = replace.replace(File.separatorChar, '/');
        }
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "loadSerializedObject", url.toString() + replace);
        }
        final InputStream resourceAsStream = this.getResourceAsStream(replace);
        if (resourceAsStream != null) {
            try {
                final MLetObjectInputStream mLetObjectInputStream = new MLetObjectInputStream(resourceAsStream, this);
                final Object object = mLetObjectInputStream.readObject();
                mLetObjectInputStream.close();
                return object;
            }
            catch (final IOException ex) {
                if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Exception while deserializing " + replace, ex);
                }
                throw ex;
            }
            catch (final ClassNotFoundException ex2) {
                if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Exception while deserializing " + replace, ex2);
                }
                throw ex2;
            }
        }
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Error: File " + replace + " containing serialized object not found");
        }
        throw new Error("File " + replace + " containing serialized object not found");
    }
    
    private Object constructParameter(final String s, final String s2) {
        final Class clazz = this.primitiveClasses.get(s2);
        if (clazz != null) {
            try {
                return clazz.getConstructor(String.class).newInstance(s);
            }
            catch (final Exception ex) {
                JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "constructParameter", "Got unexpected exception", ex);
            }
        }
        if (s2.compareTo("java.lang.Boolean") == 0) {
            return Boolean.valueOf(s);
        }
        if (s2.compareTo("java.lang.Byte") == 0) {
            return new Byte(s);
        }
        if (s2.compareTo("java.lang.Short") == 0) {
            return new Short(s);
        }
        if (s2.compareTo("java.lang.Long") == 0) {
            return new Long(s);
        }
        if (s2.compareTo("java.lang.Integer") == 0) {
            return new Integer(s);
        }
        if (s2.compareTo("java.lang.Float") == 0) {
            return new Float(s);
        }
        if (s2.compareTo("java.lang.Double") == 0) {
            return new Double(s);
        }
        if (s2.compareTo("java.lang.String") == 0) {
            return s;
        }
        return s;
    }
    
    private synchronized void setMBeanServer(final MBeanServer server) {
        this.server = server;
        this.currentClr = AccessController.doPrivileged((PrivilegedAction<ClassLoaderRepository>)new PrivilegedAction<ClassLoaderRepository>() {
            @Override
            public ClassLoaderRepository run() {
                return server.getClassLoaderRepository();
            }
        });
    }
}
