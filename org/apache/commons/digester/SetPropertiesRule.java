package org.apache.commons.digester;

import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import java.util.HashMap;
import org.xml.sax.Attributes;

public class SetPropertiesRule extends Rule
{
    private String[] attributeNames;
    private String[] propertyNames;
    private boolean ignoreMissingProperty;
    
    public SetPropertiesRule(final Digester digester) {
        this();
    }
    
    public SetPropertiesRule() {
        this.ignoreMissingProperty = true;
    }
    
    public SetPropertiesRule(final String attributeName, final String propertyName) {
        this.ignoreMissingProperty = true;
        (this.attributeNames = new String[1])[0] = attributeName;
        (this.propertyNames = new String[1])[0] = propertyName;
    }
    
    public SetPropertiesRule(final String[] attributeNames, final String[] propertyNames) {
        this.ignoreMissingProperty = true;
        this.attributeNames = new String[attributeNames.length];
        for (int i = 0, size = attributeNames.length; i < size; ++i) {
            this.attributeNames[i] = attributeNames[i];
        }
        this.propertyNames = new String[propertyNames.length];
        for (int j = 0, size2 = propertyNames.length; j < size2; ++j) {
            this.propertyNames[j] = propertyNames[j];
        }
    }
    
    public void begin(final Attributes attributes) throws Exception {
        final HashMap values = new HashMap();
        int attNamesLength = 0;
        if (this.attributeNames != null) {
            attNamesLength = this.attributeNames.length;
        }
        int propNamesLength = 0;
        if (this.propertyNames != null) {
            propNamesLength = this.propertyNames.length;
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            final String value = attributes.getValue(i);
            int n = 0;
            while (n < attNamesLength) {
                if (name.equals(this.attributeNames[n])) {
                    if (n < propNamesLength) {
                        name = this.propertyNames[n];
                        break;
                    }
                    name = null;
                    break;
                }
                else {
                    ++n;
                }
            }
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Setting property '" + name + "' to '" + value + "'"));
            }
            if (!this.ignoreMissingProperty && name != null) {
                final Object top = this.digester.peek();
                final boolean test = PropertyUtils.isWriteable(top, name);
                if (!test) {
                    throw new NoSuchMethodException("Property " + name + " can't be set");
                }
            }
            if (name != null) {
                values.put(name, value);
            }
        }
        final Object top2 = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            if (top2 != null) {
                this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Set " + top2.getClass().getName() + " properties"));
            }
            else {
                this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Set NULL properties"));
            }
        }
        BeanUtils.populate(top2, (Map)values);
    }
    
    public void addAlias(final String attributeName, final String propertyName) {
        if (this.attributeNames == null) {
            (this.attributeNames = new String[1])[0] = attributeName;
            (this.propertyNames = new String[1])[0] = propertyName;
        }
        else {
            final int length = this.attributeNames.length;
            final String[] tempAttributes = new String[length + 1];
            for (int i = 0; i < length; ++i) {
                tempAttributes[i] = this.attributeNames[i];
            }
            tempAttributes[length] = attributeName;
            final String[] tempProperties = new String[length + 1];
            for (int j = 0; j < length && j < this.propertyNames.length; ++j) {
                tempProperties[j] = this.propertyNames[j];
            }
            tempProperties[length] = propertyName;
            this.propertyNames = tempProperties;
            this.attributeNames = tempAttributes;
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("SetPropertiesRule[");
        sb.append("]");
        return sb.toString();
    }
    
    public boolean isIgnoreMissingProperty() {
        return this.ignoreMissingProperty;
    }
    
    public void setIgnoreMissingProperty(final boolean ignoreMissingProperty) {
        this.ignoreMissingProperty = ignoreMissingProperty;
    }
}
