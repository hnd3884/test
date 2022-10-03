package com.me.mdm.framework.syncml.core.data;

import java.util.LinkedList;
import org.apache.axiom.om.OMNode;
import java.util.List;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import com.me.mdm.framework.syncml.xml.SyncMLMessage2XMLConverterException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Method;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.framework.syncml.annotations.SyncMLElement;

@SyncMLElement(xmlElementName = "Item")
public class Item
{
    private Location target;
    private Location source;
    private Meta meta;
    private Object data;
    
    @SyncMLElement(xmlElementName = "Target")
    public Location getTarget() {
        return this.target;
    }
    
    public void setTarget(final Location target) {
        this.target = target;
    }
    
    @SyncMLElement(xmlElementName = "Source")
    public Location getSource() {
        return this.source;
    }
    
    public void setSource(final Location source) {
        this.source = source;
    }
    
    @SyncMLElement(xmlElementName = "Meta")
    public Meta getMeta() {
        return this.meta;
    }
    
    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
    
    @SyncMLElement(xmlElementName = "Data")
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object data) {
        this.data = data;
    }
    
    public String transform() {
        final SyncMLElement currentElement = this.getClass().getAnnotation(SyncMLElement.class);
        final OMFactory fac = OMAbstractFactory.getOMFactory();
        final OMElement rootElement = fac.createOMElement(currentElement.xmlElementName(), (OMNamespace)null);
        try {
            for (final Method method : this.getSyncMLElementMethods(this)) {
                this.processChildElementAnnotations(method, this, rootElement);
            }
        }
        catch (final SyncMLMessage2XMLConverterException ex) {
            Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rootElement.toString();
    }
    
    private void processParentElementAnnotations(final Object source, final OMElement element) throws SyncMLMessage2XMLConverterException {
        for (final Method method : this.getSyncMLElementMethods(source)) {
            this.processChildElementAnnotations(method, source, element);
        }
    }
    
    private void processChildElementAnnotations(final Method method, final Object source, final OMElement currentElement) throws SyncMLMessage2XMLConverterException {
        method.setAccessible(true);
        final SyncMLElement syncmlMethod = method.getAnnotation(SyncMLElement.class);
        Object value = null;
        try {
            value = method.invoke(source, new Object[0]);
            if (value == null) {
                return;
            }
        }
        catch (final Exception e) {
            throw new SyncMLMessage2XMLConverterException(e);
        }
        if (value instanceof List) {
            final List values = (List)value;
            for (final Object obj : values) {
                final SyncMLElement innerField = obj.getClass().getAnnotation(SyncMLElement.class);
                if (obj instanceof String) {
                    final OMElement childElement = OMAbstractFactory.getOMFactory().createOMElement(syncmlMethod.xmlElementName(), (OMNamespace)null);
                    childElement.setText(obj.toString());
                    currentElement.addChild((OMNode)childElement);
                }
                else {
                    final OMElement innerElement = OMAbstractFactory.getOMFactory().createOMElement(innerField.xmlElementName(), (OMNamespace)null);
                    this.processParentElementAnnotations(obj, innerElement);
                    currentElement.addChild((OMNode)innerElement);
                }
            }
        }
        else if (value instanceof Boolean) {
            final OMElement childElement2 = OMAbstractFactory.getOMFactory().createOMElement(syncmlMethod.xmlElementName(), (OMNamespace)null);
            currentElement.addChild((OMNode)childElement2);
        }
        else if (value instanceof String) {
            final OMElement childElement2 = OMAbstractFactory.getOMFactory().createOMElement(syncmlMethod.xmlElementName(), (OMNamespace)null);
            childElement2.setText(value.toString());
            currentElement.addChild((OMNode)childElement2);
        }
        else {
            final OMElement innerElement2 = OMAbstractFactory.getOMFactory().createOMElement(syncmlMethod.xmlElementName(), (OMNamespace)null);
            this.processParentElementAnnotations(value, innerElement2);
            currentElement.addChild((OMNode)innerElement2);
        }
    }
    
    private LinkedList<Method> getSyncMLElementMethods(final Object source) {
        final LinkedList classesList = new LinkedList();
        Class<?> clazz = (source instanceof Class) ? ((Class)source) : source.getClass();
        do {
            classesList.add(0, clazz);
            clazz = clazz.getSuperclass();
        } while (!Object.class.equals(clazz));
        final LinkedList<Method> methods = new LinkedList<Method>();
        for (int i = 0; i < classesList.size(); ++i) {
            clazz = classesList.get(i);
            for (final Method method : clazz.getMethods()) {
                final SyncMLElement syncMLElementMethod = method.getAnnotation(SyncMLElement.class);
                if (syncMLElementMethod != null && !methods.contains(method)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.getTarget().getLocUri().equals(((Item)obj).getTarget().getLocUri());
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
