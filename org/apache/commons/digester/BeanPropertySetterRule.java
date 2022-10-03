package org.apache.commons.digester;

import java.beans.PropertyDescriptor;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.DynaBean;

public class BeanPropertySetterRule extends Rule
{
    protected String propertyName;
    protected String bodyText;
    
    public BeanPropertySetterRule(final Digester digester, final String propertyName) {
        this(propertyName);
    }
    
    public BeanPropertySetterRule(final Digester digester) {
        this();
    }
    
    public BeanPropertySetterRule(final String propertyName) {
        this.propertyName = null;
        this.bodyText = null;
        this.propertyName = propertyName;
    }
    
    public BeanPropertySetterRule() {
        this((String)null);
    }
    
    public void body(final String namespace, final String name, final String text) throws Exception {
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[BeanPropertySetterRule]{" + this.digester.match + "} Called with text '" + text + "'"));
        }
        this.bodyText = text.trim();
    }
    
    public void end(final String namespace, final String name) throws Exception {
        String property = this.propertyName;
        if (property == null) {
            property = name;
        }
        final Object top = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[BeanPropertySetterRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " property " + property + " with text " + this.bodyText));
        }
        if (top instanceof DynaBean) {
            final DynaProperty desc = ((DynaBean)top).getDynaClass().getDynaProperty(property);
            if (desc == null) {
                throw new NoSuchMethodException("Bean has no property named " + property);
            }
        }
        else {
            final PropertyDescriptor desc2 = PropertyUtils.getPropertyDescriptor(top, property);
            if (desc2 == null) {
                throw new NoSuchMethodException("Bean has no property named " + property);
            }
        }
        BeanUtils.setProperty(top, property, (Object)this.bodyText);
    }
    
    public void finish() throws Exception {
        this.bodyText = null;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("BeanPropertySetterRule[");
        sb.append("propertyName=");
        sb.append(this.propertyName);
        sb.append("]");
        return sb.toString();
    }
}
