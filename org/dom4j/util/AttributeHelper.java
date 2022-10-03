package org.dom4j.util;

import org.dom4j.Attribute;
import org.dom4j.QName;
import org.dom4j.Element;

public class AttributeHelper
{
    protected AttributeHelper() {
    }
    
    public static boolean booleanValue(final Element element, final String attributeName) {
        return booleanValue(element.attribute(attributeName));
    }
    
    public static boolean booleanValue(final Element element, final QName attributeQName) {
        return booleanValue(element.attribute(attributeQName));
    }
    
    protected static boolean booleanValue(final Attribute attribute) {
        if (attribute == null) {
            return false;
        }
        final Object value = attribute.getData();
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            final Boolean b = (Boolean)value;
            return b;
        }
        return "true".equalsIgnoreCase(value.toString());
    }
}
