package com.sun.org.glassfish.gmbal;

import javax.management.MBeanInfo;
import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.ReflectionException;
import javax.management.IntrospectionException;
import javax.management.InstanceNotFoundException;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import java.util.HashMap;
import javax.management.modelmbean.ModelMBeanInfo;
import java.util.Map;
import java.io.IOException;
import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public class AMXClient implements AMXMBeanInterface
{
    public static final ObjectName NULL_OBJECTNAME;
    private MBeanServerConnection server;
    private ObjectName oname;
    
    private static ObjectName makeObjectName(final String str) {
        try {
            return new ObjectName(str);
        }
        catch (final MalformedObjectNameException ex) {
            return null;
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AMXClient)) {
            return false;
        }
        final AMXClient other = (AMXClient)obj;
        return this.oname.equals(other.oname);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + ((this.oname != null) ? this.oname.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return "AMXClient[" + this.oname + "]";
    }
    
    private <T> T fetchAttribute(final String name, final Class<T> type) {
        try {
            final Object obj = this.server.getAttribute(this.oname, name);
            if (AMXClient.NULL_OBJECTNAME.equals(obj)) {
                return null;
            }
            return type.cast(obj);
        }
        catch (final JMException exc) {
            throw new GmbalException("Exception in fetchAttribute", exc);
        }
        catch (final IOException exc2) {
            throw new GmbalException("Exception in fetchAttribute", exc2);
        }
    }
    
    public AMXClient(final MBeanServerConnection server, final ObjectName oname) {
        this.server = server;
        this.oname = oname;
    }
    
    private AMXClient makeAMX(final ObjectName on) {
        if (on == null) {
            return null;
        }
        return new AMXClient(this.server, on);
    }
    
    @Override
    public String getName() {
        return this.fetchAttribute("Name", String.class);
    }
    
    @Override
    public Map<String, ?> getMeta() {
        try {
            final ModelMBeanInfo mbi = (ModelMBeanInfo)this.server.getMBeanInfo(this.oname);
            final Descriptor desc = mbi.getMBeanDescriptor();
            final Map<String, Object> result = new HashMap<String, Object>();
            for (final String str : desc.getFieldNames()) {
                result.put(str, desc.getFieldValue(str));
            }
            return result;
        }
        catch (final MBeanException ex) {
            throw new GmbalException("Exception in getMeta", ex);
        }
        catch (final RuntimeOperationsException ex2) {
            throw new GmbalException("Exception in getMeta", ex2);
        }
        catch (final InstanceNotFoundException ex3) {
            throw new GmbalException("Exception in getMeta", ex3);
        }
        catch (final IntrospectionException ex4) {
            throw new GmbalException("Exception in getMeta", ex4);
        }
        catch (final ReflectionException ex5) {
            throw new GmbalException("Exception in getMeta", ex5);
        }
        catch (final IOException ex6) {
            throw new GmbalException("Exception in getMeta", ex6);
        }
    }
    
    @Override
    public AMXClient getParent() {
        final ObjectName res = this.fetchAttribute("Parent", ObjectName.class);
        return this.makeAMX(res);
    }
    
    @Override
    public AMXClient[] getChildren() {
        final ObjectName[] onames = this.fetchAttribute("Children", ObjectName[].class);
        return this.makeAMXArray(onames);
    }
    
    private AMXClient[] makeAMXArray(final ObjectName[] onames) {
        final AMXClient[] result = new AMXClient[onames.length];
        int ctr = 0;
        for (final ObjectName on : onames) {
            result[ctr++] = this.makeAMX(on);
        }
        return result;
    }
    
    public Object getAttribute(final String attribute) {
        try {
            return this.server.getAttribute(this.oname, attribute);
        }
        catch (final MBeanException ex) {
            throw new GmbalException("Exception in getAttribute", ex);
        }
        catch (final AttributeNotFoundException ex2) {
            throw new GmbalException("Exception in getAttribute", ex2);
        }
        catch (final ReflectionException ex3) {
            throw new GmbalException("Exception in getAttribute", ex3);
        }
        catch (final InstanceNotFoundException ex4) {
            throw new GmbalException("Exception in getAttribute", ex4);
        }
        catch (final IOException ex5) {
            throw new GmbalException("Exception in getAttribute", ex5);
        }
    }
    
    public void setAttribute(final String name, final Object value) {
        final Attribute attr = new Attribute(name, value);
        this.setAttribute(attr);
    }
    
    public void setAttribute(final Attribute attribute) {
        try {
            this.server.setAttribute(this.oname, attribute);
        }
        catch (final InstanceNotFoundException ex) {
            throw new GmbalException("Exception in setAttribute", ex);
        }
        catch (final AttributeNotFoundException ex2) {
            throw new GmbalException("Exception in setAttribute", ex2);
        }
        catch (final InvalidAttributeValueException ex3) {
            throw new GmbalException("Exception in setAttribute", ex3);
        }
        catch (final MBeanException ex4) {
            throw new GmbalException("Exception in setAttribute", ex4);
        }
        catch (final ReflectionException ex5) {
            throw new GmbalException("Exception in setAttribute", ex5);
        }
        catch (final IOException ex6) {
            throw new GmbalException("Exception in setAttribute", ex6);
        }
    }
    
    public AttributeList getAttributes(final String[] attributes) {
        try {
            return this.server.getAttributes(this.oname, attributes);
        }
        catch (final InstanceNotFoundException ex) {
            throw new GmbalException("Exception in getAttributes", ex);
        }
        catch (final ReflectionException ex2) {
            throw new GmbalException("Exception in getAttributes", ex2);
        }
        catch (final IOException ex3) {
            throw new GmbalException("Exception in getAttributes", ex3);
        }
    }
    
    public AttributeList setAttributes(final AttributeList attributes) {
        try {
            return this.server.setAttributes(this.oname, attributes);
        }
        catch (final InstanceNotFoundException ex) {
            throw new GmbalException("Exception in setAttributes", ex);
        }
        catch (final ReflectionException ex2) {
            throw new GmbalException("Exception in setAttributes", ex2);
        }
        catch (final IOException ex3) {
            throw new GmbalException("Exception in setAttributes", ex3);
        }
    }
    
    public Object invoke(final String actionName, final Object[] params, final String[] signature) throws MBeanException, ReflectionException {
        try {
            return this.server.invoke(this.oname, actionName, params, signature);
        }
        catch (final InstanceNotFoundException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
        catch (final IOException ex2) {
            throw new GmbalException("Exception in invoke", ex2);
        }
    }
    
    public MBeanInfo getMBeanInfo() {
        try {
            return this.server.getMBeanInfo(this.oname);
        }
        catch (final InstanceNotFoundException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
        catch (final IntrospectionException ex2) {
            throw new GmbalException("Exception in invoke", ex2);
        }
        catch (final ReflectionException ex3) {
            throw new GmbalException("Exception in invoke", ex3);
        }
        catch (final IOException ex4) {
            throw new GmbalException("Exception in invoke", ex4);
        }
    }
    
    public ObjectName objectName() {
        return this.oname;
    }
    
    static {
        NULL_OBJECTNAME = makeObjectName("null:type=Null,name=Null");
    }
}
