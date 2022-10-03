package javax.management;

import java.util.WeakHashMap;
import java.security.PrivilegedAction;
import java.security.AccessController;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import java.util.HashMap;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.StandardMBeanSupport;
import com.sun.jmx.mbeanserver.MXBeanSupport;
import com.sun.jmx.mbeanserver.Introspector;
import com.sun.jmx.mbeanserver.Util;
import java.util.Map;
import com.sun.jmx.mbeanserver.MBeanSupport;
import com.sun.jmx.mbeanserver.DescriptorCache;

public class StandardMBean implements DynamicMBean, MBeanRegistration
{
    private static final DescriptorCache descriptors;
    private volatile MBeanSupport<?> mbean;
    private volatile MBeanInfo cachedMBeanInfo;
    private static final Map<Class<?>, Boolean> mbeanInfoSafeMap;
    
    private <T> void construct(T cast, Class<T> clazz, final boolean b, final boolean b2) throws NotCompliantMBeanException {
        if (cast == null) {
            if (!b) {
                throw new IllegalArgumentException("implementation is null");
            }
            cast = Util.cast(this);
        }
        if (b2) {
            if (clazz == null) {
                clazz = Util.cast(Introspector.getMXBeanInterface(cast.getClass()));
            }
            this.mbean = new MXBeanSupport((T)cast, (Class<T>)clazz);
        }
        else {
            if (clazz == null) {
                clazz = Util.cast(Introspector.getStandardMBeanInterface(cast.getClass()));
            }
            this.mbean = new StandardMBeanSupport((T)cast, (Class<T>)clazz);
        }
    }
    
    public <T> StandardMBean(final T t, final Class<T> clazz) throws NotCompliantMBeanException {
        this.construct(t, clazz, false, false);
    }
    
    protected StandardMBean(final Class<?> clazz) throws NotCompliantMBeanException {
        this.construct(null, clazz, true, false);
    }
    
    public <T> StandardMBean(final T t, final Class<T> clazz, final boolean b) {
        try {
            this.construct(t, clazz, false, b);
        }
        catch (final NotCompliantMBeanException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    protected StandardMBean(final Class<?> clazz, final boolean b) {
        try {
            this.construct(null, clazz, true, b);
        }
        catch (final NotCompliantMBeanException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public void setImplementation(final Object o) throws NotCompliantMBeanException {
        if (o == null) {
            throw new IllegalArgumentException("implementation is null");
        }
        if (this.isMXBean()) {
            this.mbean = new MXBeanSupport((T)o, (Class<T>)Util.cast(this.getMBeanInterface()));
        }
        else {
            this.mbean = new StandardMBeanSupport((T)o, (Class<T>)Util.cast(this.getMBeanInterface()));
        }
    }
    
    public Object getImplementation() {
        return this.mbean.getResource();
    }
    
    public final Class<?> getMBeanInterface() {
        return this.mbean.getMBeanInterface();
    }
    
    public Class<?> getImplementationClass() {
        return this.mbean.getResource().getClass();
    }
    
    @Override
    public Object getAttribute(final String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return this.mbean.getAttribute(s);
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        this.mbean.setAttribute(attribute);
    }
    
    @Override
    public AttributeList getAttributes(final String[] array) {
        return this.mbean.getAttributes(array);
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList attributes) {
        return this.mbean.setAttributes(attributes);
    }
    
    @Override
    public Object invoke(final String s, final Object[] array, final String[] array2) throws MBeanException, ReflectionException {
        return this.mbean.invoke(s, array, array2);
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        try {
            final MBeanInfo cachedMBeanInfo = this.getCachedMBeanInfo();
            if (cachedMBeanInfo != null) {
                return cachedMBeanInfo;
            }
        }
        catch (final RuntimeException ex) {
            if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", "Failed to get cached MBeanInfo", ex);
            }
        }
        if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MISC_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "getMBeanInfo", "Building MBeanInfo for " + this.getImplementationClass().getName());
        }
        final MBeanSupport<?> mbean = this.mbean;
        final MBeanInfo mBeanInfo = mbean.getMBeanInfo();
        final MBeanInfo mBeanInfo2 = new MBeanInfo(this.getClassName(mBeanInfo), this.getDescription(mBeanInfo), this.getAttributes(mBeanInfo), this.getConstructors(mBeanInfo, mbean.getResource()), this.getOperations(mBeanInfo), this.getNotifications(mBeanInfo), this.getDescriptor(mBeanInfo, immutableInfo(this.getClass())));
        try {
            this.cacheMBeanInfo(mBeanInfo2);
        }
        catch (final RuntimeException ex2) {
            if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", "Failed to cache MBeanInfo", ex2);
            }
        }
        return mBeanInfo2;
    }
    
    protected String getClassName(final MBeanInfo mBeanInfo) {
        if (mBeanInfo == null) {
            return this.getImplementationClass().getName();
        }
        return mBeanInfo.getClassName();
    }
    
    protected String getDescription(final MBeanInfo mBeanInfo) {
        if (mBeanInfo == null) {
            return null;
        }
        return mBeanInfo.getDescription();
    }
    
    protected String getDescription(final MBeanFeatureInfo mBeanFeatureInfo) {
        if (mBeanFeatureInfo == null) {
            return null;
        }
        return mBeanFeatureInfo.getDescription();
    }
    
    protected String getDescription(final MBeanAttributeInfo mBeanAttributeInfo) {
        return this.getDescription((MBeanFeatureInfo)mBeanAttributeInfo);
    }
    
    protected String getDescription(final MBeanConstructorInfo mBeanConstructorInfo) {
        return this.getDescription((MBeanFeatureInfo)mBeanConstructorInfo);
    }
    
    protected String getDescription(final MBeanConstructorInfo mBeanConstructorInfo, final MBeanParameterInfo mBeanParameterInfo, final int n) {
        if (mBeanParameterInfo == null) {
            return null;
        }
        return mBeanParameterInfo.getDescription();
    }
    
    protected String getParameterName(final MBeanConstructorInfo mBeanConstructorInfo, final MBeanParameterInfo mBeanParameterInfo, final int n) {
        if (mBeanParameterInfo == null) {
            return null;
        }
        return mBeanParameterInfo.getName();
    }
    
    protected String getDescription(final MBeanOperationInfo mBeanOperationInfo) {
        return this.getDescription((MBeanFeatureInfo)mBeanOperationInfo);
    }
    
    protected int getImpact(final MBeanOperationInfo mBeanOperationInfo) {
        if (mBeanOperationInfo == null) {
            return 3;
        }
        return mBeanOperationInfo.getImpact();
    }
    
    protected String getParameterName(final MBeanOperationInfo mBeanOperationInfo, final MBeanParameterInfo mBeanParameterInfo, final int n) {
        if (mBeanParameterInfo == null) {
            return null;
        }
        return mBeanParameterInfo.getName();
    }
    
    protected String getDescription(final MBeanOperationInfo mBeanOperationInfo, final MBeanParameterInfo mBeanParameterInfo, final int n) {
        if (mBeanParameterInfo == null) {
            return null;
        }
        return mBeanParameterInfo.getDescription();
    }
    
    protected MBeanConstructorInfo[] getConstructors(final MBeanConstructorInfo[] array, final Object o) {
        if (array == null) {
            return null;
        }
        if (o != null && o != this) {
            return null;
        }
        return array;
    }
    
    MBeanNotificationInfo[] getNotifications(final MBeanInfo mBeanInfo) {
        return null;
    }
    
    Descriptor getDescriptor(final MBeanInfo mBeanInfo, final boolean b) {
        ImmutableDescriptor value;
        if (mBeanInfo == null || mBeanInfo.getDescriptor() == null || mBeanInfo.getDescriptor().getFieldNames().length == 0) {
            value = StandardMBean.descriptors.get(new ImmutableDescriptor(new String[] { "interfaceClassName=" + this.getMBeanInterface().getName(), "immutableInfo=" + b }));
        }
        else {
            final Descriptor descriptor = mBeanInfo.getDescriptor();
            final HashMap hashMap = new HashMap();
            for (final String s : descriptor.getFieldNames()) {
                if (s.equals("immutableInfo")) {
                    hashMap.put(s, Boolean.toString(b));
                }
                else {
                    hashMap.put(s, descriptor.getFieldValue(s));
                }
            }
            value = new ImmutableDescriptor(hashMap);
        }
        return value;
    }
    
    protected MBeanInfo getCachedMBeanInfo() {
        return this.cachedMBeanInfo;
    }
    
    protected void cacheMBeanInfo(final MBeanInfo cachedMBeanInfo) {
        this.cachedMBeanInfo = cachedMBeanInfo;
    }
    
    private boolean isMXBean() {
        return this.mbean.isMXBean();
    }
    
    private static <T> boolean identicalArrays(final T[] array, final T[] array2) {
        if (array == array2) {
            return true;
        }
        if (array == null || array2 == null || array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    private static <T> boolean equal(final T t, final T t2) {
        return t == t2 || (t != null && t2 != null && t.equals(t2));
    }
    
    private static MBeanParameterInfo customize(final MBeanParameterInfo mBeanParameterInfo, final String s, final String s2) {
        if (equal(s, mBeanParameterInfo.getName()) && equal(s2, mBeanParameterInfo.getDescription())) {
            return mBeanParameterInfo;
        }
        if (mBeanParameterInfo instanceof OpenMBeanParameterInfo) {
            return new OpenMBeanParameterInfoSupport(s, s2, ((OpenMBeanParameterInfo)mBeanParameterInfo).getOpenType(), mBeanParameterInfo.getDescriptor());
        }
        return new MBeanParameterInfo(s, mBeanParameterInfo.getType(), s2, mBeanParameterInfo.getDescriptor());
    }
    
    private static MBeanConstructorInfo customize(final MBeanConstructorInfo mBeanConstructorInfo, final String s, final MBeanParameterInfo[] array) {
        if (equal(s, mBeanConstructorInfo.getDescription()) && identicalArrays(array, mBeanConstructorInfo.getSignature())) {
            return mBeanConstructorInfo;
        }
        if (mBeanConstructorInfo instanceof OpenMBeanConstructorInfo) {
            return new OpenMBeanConstructorInfoSupport(mBeanConstructorInfo.getName(), s, paramsToOpenParams(array), mBeanConstructorInfo.getDescriptor());
        }
        return new MBeanConstructorInfo(mBeanConstructorInfo.getName(), s, array, mBeanConstructorInfo.getDescriptor());
    }
    
    private static MBeanOperationInfo customize(final MBeanOperationInfo mBeanOperationInfo, final String s, final MBeanParameterInfo[] array, final int n) {
        if (equal(s, mBeanOperationInfo.getDescription()) && identicalArrays(array, mBeanOperationInfo.getSignature()) && n == mBeanOperationInfo.getImpact()) {
            return mBeanOperationInfo;
        }
        if (mBeanOperationInfo instanceof OpenMBeanOperationInfo) {
            return new OpenMBeanOperationInfoSupport(mBeanOperationInfo.getName(), s, paramsToOpenParams(array), ((OpenMBeanOperationInfo)mBeanOperationInfo).getReturnOpenType(), n, mBeanOperationInfo.getDescriptor());
        }
        return new MBeanOperationInfo(mBeanOperationInfo.getName(), s, array, mBeanOperationInfo.getReturnType(), n, mBeanOperationInfo.getDescriptor());
    }
    
    private static MBeanAttributeInfo customize(final MBeanAttributeInfo mBeanAttributeInfo, final String s) {
        if (equal(s, mBeanAttributeInfo.getDescription())) {
            return mBeanAttributeInfo;
        }
        if (mBeanAttributeInfo instanceof OpenMBeanAttributeInfo) {
            return new OpenMBeanAttributeInfoSupport(mBeanAttributeInfo.getName(), s, ((OpenMBeanAttributeInfo)mBeanAttributeInfo).getOpenType(), mBeanAttributeInfo.isReadable(), mBeanAttributeInfo.isWritable(), mBeanAttributeInfo.isIs(), mBeanAttributeInfo.getDescriptor());
        }
        return new MBeanAttributeInfo(mBeanAttributeInfo.getName(), mBeanAttributeInfo.getType(), s, mBeanAttributeInfo.isReadable(), mBeanAttributeInfo.isWritable(), mBeanAttributeInfo.isIs(), mBeanAttributeInfo.getDescriptor());
    }
    
    private static OpenMBeanParameterInfo[] paramsToOpenParams(final MBeanParameterInfo[] array) {
        if (array instanceof OpenMBeanParameterInfo[]) {
            return (OpenMBeanParameterInfo[])array;
        }
        final OpenMBeanParameterInfoSupport[] array2 = new OpenMBeanParameterInfoSupport[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private MBeanConstructorInfo[] getConstructors(final MBeanInfo mBeanInfo, final Object o) {
        final MBeanConstructorInfo[] constructors = this.getConstructors(mBeanInfo.getConstructors(), o);
        if (constructors == null) {
            return null;
        }
        final int length = constructors.length;
        final MBeanConstructorInfo[] array = new MBeanConstructorInfo[length];
        for (int i = 0; i < length; ++i) {
            final MBeanConstructorInfo mBeanConstructorInfo = constructors[i];
            final MBeanParameterInfo[] signature = mBeanConstructorInfo.getSignature();
            MBeanParameterInfo[] array2;
            if (signature != null) {
                final int length2 = signature.length;
                array2 = new MBeanParameterInfo[length2];
                for (int j = 0; j < length2; ++j) {
                    final MBeanParameterInfo mBeanParameterInfo = signature[j];
                    array2[j] = customize(mBeanParameterInfo, this.getParameterName(mBeanConstructorInfo, mBeanParameterInfo, j), this.getDescription(mBeanConstructorInfo, mBeanParameterInfo, j));
                }
            }
            else {
                array2 = null;
            }
            array[i] = customize(mBeanConstructorInfo, this.getDescription(mBeanConstructorInfo), array2);
        }
        return array;
    }
    
    private MBeanOperationInfo[] getOperations(final MBeanInfo mBeanInfo) {
        final MBeanOperationInfo[] operations = mBeanInfo.getOperations();
        if (operations == null) {
            return null;
        }
        final int length = operations.length;
        final MBeanOperationInfo[] array = new MBeanOperationInfo[length];
        for (int i = 0; i < length; ++i) {
            final MBeanOperationInfo mBeanOperationInfo = operations[i];
            final MBeanParameterInfo[] signature = mBeanOperationInfo.getSignature();
            MBeanParameterInfo[] array2;
            if (signature != null) {
                final int length2 = signature.length;
                array2 = new MBeanParameterInfo[length2];
                for (int j = 0; j < length2; ++j) {
                    final MBeanParameterInfo mBeanParameterInfo = signature[j];
                    array2[j] = customize(mBeanParameterInfo, this.getParameterName(mBeanOperationInfo, mBeanParameterInfo, j), this.getDescription(mBeanOperationInfo, mBeanParameterInfo, j));
                }
            }
            else {
                array2 = null;
            }
            array[i] = customize(mBeanOperationInfo, this.getDescription(mBeanOperationInfo), array2, this.getImpact(mBeanOperationInfo));
        }
        return array;
    }
    
    private MBeanAttributeInfo[] getAttributes(final MBeanInfo mBeanInfo) {
        final MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
        if (attributes == null) {
            return null;
        }
        final int length = attributes.length;
        final MBeanAttributeInfo[] array = new MBeanAttributeInfo[length];
        for (int i = 0; i < length; ++i) {
            final MBeanAttributeInfo mBeanAttributeInfo = attributes[i];
            array[i] = customize(mBeanAttributeInfo, this.getDescription(mBeanAttributeInfo));
        }
        return array;
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer mBeanServer, final ObjectName objectName) throws Exception {
        this.mbean.register(mBeanServer, objectName);
        return objectName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
        if (!b) {
            this.mbean.unregister();
        }
    }
    
    @Override
    public void preDeregister() throws Exception {
    }
    
    @Override
    public void postDeregister() {
        this.mbean.unregister();
    }
    
    static boolean immutableInfo(final Class<? extends StandardMBean> clazz) {
        if (clazz == StandardMBean.class || clazz == StandardEmitterMBean.class) {
            return true;
        }
        synchronized (StandardMBean.mbeanInfoSafeMap) {
            Boolean value = StandardMBean.mbeanInfoSafeMap.get(clazz);
            if (value == null) {
                try {
                    value = AccessController.doPrivileged((PrivilegedAction<Boolean>)new MBeanInfoSafeAction(clazz));
                }
                catch (final Exception ex) {
                    value = false;
                }
                StandardMBean.mbeanInfoSafeMap.put(clazz, value);
            }
            return value;
        }
    }
    
    static boolean overrides(final Class<?> clazz, final Class<?> clazz2, final String s, final Class<?>... array) {
        Class<?> superclass = clazz;
        while (superclass != clazz2) {
            try {
                superclass.getDeclaredMethod(s, (Class[])array);
                return true;
            }
            catch (final NoSuchMethodException ex) {
                superclass = superclass.getSuperclass();
                continue;
            }
            break;
        }
        return false;
    }
    
    static {
        descriptors = DescriptorCache.getInstance(JMX.proof);
        mbeanInfoSafeMap = new WeakHashMap<Class<?>, Boolean>();
    }
    
    private static class MBeanInfoSafeAction implements PrivilegedAction<Boolean>
    {
        private final Class<?> subclass;
        
        MBeanInfoSafeAction(final Class<?> subclass) {
            this.subclass = subclass;
        }
        
        @Override
        public Boolean run() {
            if (StandardMBean.overrides(this.subclass, StandardMBean.class, "cacheMBeanInfo", MBeanInfo.class)) {
                return false;
            }
            if (StandardMBean.overrides(this.subclass, StandardMBean.class, "getCachedMBeanInfo", (Class<?>[])null)) {
                return false;
            }
            if (StandardMBean.overrides(this.subclass, StandardMBean.class, "getMBeanInfo", (Class<?>[])null)) {
                return false;
            }
            if (StandardEmitterMBean.class.isAssignableFrom(this.subclass) && StandardMBean.overrides(this.subclass, StandardEmitterMBean.class, "getNotificationInfo", (Class<?>[])null)) {
                return false;
            }
            return true;
        }
    }
}
