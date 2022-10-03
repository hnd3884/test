package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;

public class Elements extends JAXBElement<XhtmlElementType>
{
    private static final long serialVersionUID = 1L;
    
    public static Elements el(final String elementName) {
        return createElement(elementName);
    }
    
    public static Object val(final String elementName, final String value) {
        return createElement(elementName, value);
    }
    
    public Elements(final QName name, final Class<XhtmlElementType> clazz, final XhtmlElementType element) {
        super(name, clazz, element);
    }
    
    public Elements add(final Object... childNodes) {
        if (childNodes != null) {
            for (final Object childNode : childNodes) {
                this.getValue().getChildNodes().add(childNode);
            }
        }
        return this;
    }
    
    public Elements addChild(final Object child) {
        this.getValue().getChildNodes().add(child);
        return this;
    }
    
    private static Elements createElement(final String elementName) {
        try {
            final XhtmlElementType element = new XhtmlElementType();
            final Elements jaxbElement = new Elements(new QName("http://www.w3.org/1999/xhtml", elementName), XhtmlElementType.class, element);
            return jaxbElement;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static JAXBElement<XhtmlValueType> createElement(final String elementName, final String value) {
        try {
            final XhtmlValueType element = new XhtmlValueType();
            element.value = value;
            final JAXBElement<XhtmlValueType> jaxbElement = new JAXBElement<XhtmlValueType>(new QName("http://www.w3.org/1999/xhtml", elementName), XhtmlValueType.class, element);
            return jaxbElement;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
