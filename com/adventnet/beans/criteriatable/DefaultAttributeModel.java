package com.adventnet.beans.criteriatable;

import java.util.EventListener;
import com.adventnet.beans.criteriatable.events.AttributeModelListener;
import com.adventnet.beans.criteriatable.events.AttributeModelEvent;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import javax.swing.event.EventListenerList;
import java.util.Vector;

public class DefaultAttributeModel implements AttributeModel
{
    Vector attributes;
    public static final String AND = "AND";
    public static final String OR = "OR";
    private static final int OPERATOR_COUNT = 2;
    private EventListenerList listeners;
    
    public DefaultAttributeModel() {
        this.listeners = new EventListenerList();
        this.attributes = new Vector();
        this.init();
    }
    
    public DefaultAttributeModel(final Vector attributes) {
        this.listeners = new EventListenerList();
        if (attributes != null) {
            this.attributes = attributes;
        }
    }
    
    private void init() {
        this.addAttribute(new Attribute("Subject", "subject", new String[] { "starts with", "ends with" }, new ComboValueEditor(new Object[] { "personal", "important", "confidance" })));
        this.addAttribute(new Attribute("Age", new Integer(23), Attribute.INTEGER_TYPE));
        this.addAttribute(new Attribute("Temperature", new Float(35.5), Attribute.FLOAT_TYPE));
        this.addAttribute(new Attribute("To", "to", Attribute.STRING_TYPE));
        this.addAttribute(new Attribute("Sent Date", new Date(), Attribute.DATE_TYPE));
        this.addAttribute(new Attribute("DOB", new Date(), Attribute.DATE_TYPE, new SpinnerValueEditor(new SpinnerDateModel())));
        this.addAttribute(new Attribute("State", "state", Attribute.BOOLEAN_TYPE));
    }
    
    public Attribute getAttribute(final int n) {
        return this.attributes.elementAt(n);
    }
    
    public int getAttributeCount() {
        return this.attributes.size();
    }
    
    public String getOperator(final int n) {
        if (n == 0) {
            return "AND";
        }
        if (n == 1) {
            return "OR";
        }
        return null;
    }
    
    public int getOperatorsCount() {
        return 2;
    }
    
    public String getGroupingElement(final int n) {
        return null;
    }
    
    public int getGroupingElementsCount() {
        return 0;
    }
    
    public void addAttribute(final Attribute attribute) {
        this.attributes.add(attribute);
        this.fireAttributeModelEvent(new AttributeModelEvent(this, attribute, 0));
    }
    
    public void removeAttribute(final Attribute attribute) {
        this.attributes.remove(attribute);
        this.fireAttributeModelEvent(new AttributeModelEvent(this, attribute, 1));
    }
    
    public void addAttributeModelListener(final AttributeModelListener attributeModelListener) {
        this.listeners.add(AttributeModelListener.class, attributeModelListener);
    }
    
    public void removeAttributeModelListener(final AttributeModelListener attributeModelListener) {
        this.listeners.remove(AttributeModelListener.class, attributeModelListener);
    }
    
    protected void fireAttributeModelEvent(final AttributeModelEvent attributeModelEvent) {
        final EventListener[] listeners = this.listeners.getListeners((Class<EventListener>)AttributeModelListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((AttributeModelListener)listeners[i]).attributeModelChanged(attributeModelEvent);
        }
    }
    
    public Attribute getAttributeByName(final String s) {
        for (int i = 0; i < this.attributes.size(); ++i) {
            final Attribute attribute = this.getAttribute(i);
            if (attribute.toString().equals(s)) {
                return attribute;
            }
        }
        return null;
    }
    
    public Attribute getAttributeByValue(final Object o) {
        for (int i = 0; i < this.attributes.size(); ++i) {
            final Attribute attribute = this.getAttribute(i);
            if (attribute.getValueObject().equals(o)) {
                return attribute;
            }
        }
        return null;
    }
}
