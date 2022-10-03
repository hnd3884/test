package com.sun.jmx.mbeanserver;

import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.RuntimeOperationsException;
import javax.management.Attribute;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.JMRuntimeException;
import javax.management.AttributeNotFoundException;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanRegistration;
import javax.management.DynamicMBean;
import javax.management.MBeanServerDelegate;

final class MBeanServerDelegateImpl extends MBeanServerDelegate implements DynamicMBean, MBeanRegistration
{
    private static final String[] attributeNames;
    private static final MBeanAttributeInfo[] attributeInfos;
    private final MBeanInfo delegateInfo;
    
    public MBeanServerDelegateImpl() {
        this.delegateInfo = new MBeanInfo("javax.management.MBeanServerDelegate", "Represents  the MBean server from the management point of view.", MBeanServerDelegateImpl.attributeInfos, null, null, this.getNotificationInfo());
    }
    
    @Override
    public final ObjectName preRegister(final MBeanServer mBeanServer, final ObjectName objectName) throws Exception {
        if (objectName == null) {
            return MBeanServerDelegateImpl.DELEGATE_NAME;
        }
        return objectName;
    }
    
    @Override
    public final void postRegister(final Boolean b) {
    }
    
    @Override
    public final void preDeregister() throws Exception {
        throw new IllegalArgumentException("The MBeanServerDelegate MBean cannot be unregistered");
    }
    
    @Override
    public final void postDeregister() {
    }
    
    @Override
    public Object getAttribute(final String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
        try {
            if (s == null) {
                throw new AttributeNotFoundException("null");
            }
            if (s.equals("MBeanServerId")) {
                return this.getMBeanServerId();
            }
            if (s.equals("SpecificationName")) {
                return this.getSpecificationName();
            }
            if (s.equals("SpecificationVersion")) {
                return this.getSpecificationVersion();
            }
            if (s.equals("SpecificationVendor")) {
                return this.getSpecificationVendor();
            }
            if (s.equals("ImplementationName")) {
                return this.getImplementationName();
            }
            if (s.equals("ImplementationVersion")) {
                return this.getImplementationVersion();
            }
            if (s.equals("ImplementationVendor")) {
                return this.getImplementationVendor();
            }
            throw new AttributeNotFoundException("null");
        }
        catch (final AttributeNotFoundException ex) {
            throw ex;
        }
        catch (final JMRuntimeException ex2) {
            throw ex2;
        }
        catch (final SecurityException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new MBeanException(ex4, "Failed to get " + s);
        }
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        final String s = (attribute == null) ? null : attribute.getName();
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
        }
        this.getAttribute(s);
        throw new AttributeNotFoundException(s + " not accessible");
    }
    
    @Override
    public AttributeList getAttributes(final String[] array) {
        final String[] array2 = (array == null) ? MBeanServerDelegateImpl.attributeNames : array;
        final int length = array2.length;
        final AttributeList list = new AttributeList(length);
        for (int i = 0; i < length; ++i) {
            try {
                list.add(new Attribute(array2[i], this.getAttribute(array2[i])));
            }
            catch (final Exception ex) {
                if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                    JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerDelegateImpl.class.getName(), "getAttributes", "Attribute " + array2[i] + " not found");
                }
            }
        }
        return list;
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList list) {
        return new AttributeList(0);
    }
    
    @Override
    public Object invoke(final String s, final Object[] array, final String[] array2) throws MBeanException, ReflectionException {
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Operation name  cannot be null"), "Exception occurred trying to invoke the operation on the MBean");
        }
        throw new ReflectionException(new NoSuchMethodException(s), "The operation with name " + s + " could not be found");
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        return this.delegateInfo;
    }
    
    static {
        attributeNames = new String[] { "MBeanServerId", "SpecificationName", "SpecificationVersion", "SpecificationVendor", "ImplementationName", "ImplementationVersion", "ImplementationVendor" };
        attributeInfos = new MBeanAttributeInfo[] { new MBeanAttributeInfo("MBeanServerId", "java.lang.String", "The MBean server agent identification", true, false, false), new MBeanAttributeInfo("SpecificationName", "java.lang.String", "The full name of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("SpecificationVersion", "java.lang.String", "The version of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("SpecificationVendor", "java.lang.String", "The vendor of the JMX specification implemented by this product.", true, false, false), new MBeanAttributeInfo("ImplementationName", "java.lang.String", "The JMX implementation name (the name of this product)", true, false, false), new MBeanAttributeInfo("ImplementationVersion", "java.lang.String", "The JMX implementation version (the version of this product).", true, false, false), new MBeanAttributeInfo("ImplementationVendor", "java.lang.String", "the JMX implementation vendor (the vendor of this product).", true, false, false) };
    }
}
