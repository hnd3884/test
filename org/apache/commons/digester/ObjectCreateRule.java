package org.apache.commons.digester;

import org.xml.sax.Attributes;

public class ObjectCreateRule extends Rule
{
    protected String attributeName;
    protected String className;
    
    public ObjectCreateRule(final Digester digester, final String className) {
        this(className);
    }
    
    public ObjectCreateRule(final Digester digester, final Class clazz) {
        this(clazz);
    }
    
    public ObjectCreateRule(final Digester digester, final String className, final String attributeName) {
        this(className, attributeName);
    }
    
    public ObjectCreateRule(final Digester digester, final String attributeName, final Class clazz) {
        this(attributeName, clazz);
    }
    
    public ObjectCreateRule(final String className) {
        this(className, (String)null);
    }
    
    public ObjectCreateRule(final Class clazz) {
        this(clazz.getName(), (String)null);
    }
    
    public ObjectCreateRule(final String className, final String attributeName) {
        this.attributeName = null;
        this.className = null;
        this.className = className;
        this.attributeName = attributeName;
    }
    
    public ObjectCreateRule(final String attributeName, final Class clazz) {
        this(clazz.getName(), attributeName);
    }
    
    public void begin(final Attributes attributes) throws Exception {
        String realClassName = this.className;
        if (this.attributeName != null) {
            final String value = attributes.getValue(this.attributeName);
            if (value != null) {
                realClassName = value;
            }
        }
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[ObjectCreateRule]{" + this.digester.match + "}New " + realClassName));
        }
        final Class clazz = this.digester.getClassLoader().loadClass(realClassName);
        final Object instance = clazz.newInstance();
        this.digester.push(instance);
    }
    
    public void end() throws Exception {
        final Object top = this.digester.pop();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[ObjectCreateRule]{" + this.digester.match + "} Pop " + top.getClass().getName()));
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("ObjectCreateRule[");
        sb.append("className=");
        sb.append(this.className);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append("]");
        return sb.toString();
    }
}
