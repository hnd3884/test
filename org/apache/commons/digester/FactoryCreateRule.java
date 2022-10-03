package org.apache.commons.digester;

import org.xml.sax.Attributes;
import org.apache.commons.collections.ArrayStack;

public class FactoryCreateRule extends Rule
{
    private boolean ignoreCreateExceptions;
    private ArrayStack exceptionIgnoredStack;
    protected String attributeName;
    protected String className;
    protected ObjectCreationFactory creationFactory;
    
    public FactoryCreateRule(final Digester digester, final String className) {
        this(className);
    }
    
    public FactoryCreateRule(final Digester digester, final Class clazz) {
        this(clazz);
    }
    
    public FactoryCreateRule(final Digester digester, final String className, final String attributeName) {
        this(className, attributeName);
    }
    
    public FactoryCreateRule(final Digester digester, final Class clazz, final String attributeName) {
        this(clazz, attributeName);
    }
    
    public FactoryCreateRule(final Digester digester, final ObjectCreationFactory creationFactory) {
        this(creationFactory);
    }
    
    public FactoryCreateRule(final String className) {
        this(className, false);
    }
    
    public FactoryCreateRule(final Class clazz) {
        this(clazz, false);
    }
    
    public FactoryCreateRule(final String className, final String attributeName) {
        this(className, attributeName, false);
    }
    
    public FactoryCreateRule(final Class clazz, final String attributeName) {
        this(clazz, attributeName, false);
    }
    
    public FactoryCreateRule(final ObjectCreationFactory creationFactory) {
        this(creationFactory, false);
    }
    
    public FactoryCreateRule(final String className, final boolean ignoreCreateExceptions) {
        this(className, null, ignoreCreateExceptions);
    }
    
    public FactoryCreateRule(final Class clazz, final boolean ignoreCreateExceptions) {
        this(clazz, null, ignoreCreateExceptions);
    }
    
    public FactoryCreateRule(final String className, final String attributeName, final boolean ignoreCreateExceptions) {
        this.attributeName = null;
        this.className = null;
        this.creationFactory = null;
        this.className = className;
        this.attributeName = attributeName;
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }
    
    public FactoryCreateRule(final Class clazz, final String attributeName, final boolean ignoreCreateExceptions) {
        this(clazz.getName(), attributeName, ignoreCreateExceptions);
    }
    
    public FactoryCreateRule(final ObjectCreationFactory creationFactory, final boolean ignoreCreateExceptions) {
        this.attributeName = null;
        this.className = null;
        this.creationFactory = null;
        this.creationFactory = creationFactory;
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        if (this.ignoreCreateExceptions) {
            if (this.exceptionIgnoredStack == null) {
                this.exceptionIgnoredStack = new ArrayStack();
            }
            try {
                final Object instance = this.getFactory(attributes).createObject(attributes);
                if (this.digester.log.isDebugEnabled()) {
                    this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New " + instance.getClass().getName()));
                }
                this.digester.push(instance);
                this.exceptionIgnoredStack.push((Object)Boolean.FALSE);
            }
            catch (final Exception e) {
                if (this.digester.log.isInfoEnabled()) {
                    this.digester.log.info((Object)("[FactoryCreateRule] Create exception ignored: " + ((e.getMessage() == null) ? e.getClass().getName() : e.getMessage())));
                    if (this.digester.log.isDebugEnabled()) {
                        this.digester.log.debug((Object)"[FactoryCreateRule] Ignored exception:", (Throwable)e);
                    }
                }
                this.exceptionIgnoredStack.push((Object)Boolean.TRUE);
            }
        }
        else {
            final Object instance = this.getFactory(attributes).createObject(attributes);
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New " + instance.getClass().getName()));
            }
            this.digester.push(instance);
        }
    }
    
    public void end(final String namespace, final String name) throws Exception {
        if (this.ignoreCreateExceptions && this.exceptionIgnoredStack != null && !this.exceptionIgnoredStack.empty() && (boolean)this.exceptionIgnoredStack.pop()) {
            if (this.digester.log.isTraceEnabled()) {
                this.digester.log.trace((Object)"[FactoryCreateRule] No creation so no push so no pop");
            }
            return;
        }
        final Object top = this.digester.pop();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} Pop " + top.getClass().getName()));
        }
    }
    
    public void finish() throws Exception {
        if (this.attributeName != null) {
            this.creationFactory = null;
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("FactoryCreateRule[");
        sb.append("className=");
        sb.append(this.className);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        if (this.creationFactory != null) {
            sb.append(", creationFactory=");
            sb.append(this.creationFactory);
        }
        sb.append("]");
        return sb.toString();
    }
    
    protected ObjectCreationFactory getFactory(final Attributes attributes) throws Exception {
        if (this.creationFactory == null) {
            String realClassName = this.className;
            if (this.attributeName != null) {
                final String value = attributes.getValue(this.attributeName);
                if (value != null) {
                    realClassName = value;
                }
            }
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New factory " + realClassName));
            }
            final Class clazz = this.digester.getClassLoader().loadClass(realClassName);
            (this.creationFactory = clazz.newInstance()).setDigester(this.digester);
        }
        return this.creationFactory;
    }
}
