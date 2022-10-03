package org.dom4j.bean;

import org.dom4j.tree.DefaultAttribute;
import org.dom4j.Attribute;
import org.xml.sax.Attributes;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.DocumentFactory;

public class BeanDocumentFactory extends DocumentFactory
{
    private static BeanDocumentFactory singleton;
    
    public static DocumentFactory getInstance() {
        return BeanDocumentFactory.singleton;
    }
    
    public Element createElement(final QName qname) {
        final Object bean = this.createBean(qname);
        if (bean == null) {
            return new BeanElement(qname);
        }
        return new BeanElement(qname, bean);
    }
    
    public Element createElement(final QName qname, final Attributes attributes) {
        final Object bean = this.createBean(qname, attributes);
        if (bean == null) {
            return new BeanElement(qname);
        }
        return new BeanElement(qname, bean);
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        return new DefaultAttribute(qname, value);
    }
    
    protected Object createBean(final QName qname) {
        return null;
    }
    
    protected Object createBean(final QName qname, final Attributes attributes) {
        final String value = attributes.getValue("class");
        if (value != null) {
            try {
                final Class beanClass = Class.forName(value, true, BeanDocumentFactory.class.getClassLoader());
                return beanClass.newInstance();
            }
            catch (final Exception e) {
                this.handleException(e);
            }
        }
        return null;
    }
    
    protected void handleException(final Exception e) {
        System.out.println("#### Warning: couldn't create bean: " + e);
    }
    
    static {
        BeanDocumentFactory.singleton = new BeanDocumentFactory();
    }
}
