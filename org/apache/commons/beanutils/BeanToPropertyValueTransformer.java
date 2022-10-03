package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.collections.Transformer;

public class BeanToPropertyValueTransformer implements Transformer
{
    private final Log log;
    private String propertyName;
    private boolean ignoreNull;
    
    public BeanToPropertyValueTransformer(final String propertyName) {
        this(propertyName, false);
    }
    
    public BeanToPropertyValueTransformer(final String propertyName, final boolean ignoreNull) {
        this.log = LogFactory.getLog((Class)this.getClass());
        if (propertyName != null && propertyName.length() > 0) {
            this.propertyName = propertyName;
            this.ignoreNull = ignoreNull;
            return;
        }
        throw new IllegalArgumentException("propertyName cannot be null or empty");
    }
    
    public Object transform(final Object object) {
        Object propertyValue = null;
        try {
            propertyValue = PropertyUtils.getProperty(object, this.propertyName);
        }
        catch (final IllegalArgumentException e) {
            final String errorMsg = "Problem during transformation. Null value encountered in property path...";
            if (!this.ignoreNull) {
                final IllegalArgumentException iae = new IllegalArgumentException("Problem during transformation. Null value encountered in property path...");
                if (!BeanUtils.initCause(iae, e)) {
                    this.log.error((Object)"Problem during transformation. Null value encountered in property path...", (Throwable)e);
                }
                throw iae;
            }
            this.log.warn((Object)("WARNING: Problem during transformation. Null value encountered in property path..." + e));
        }
        catch (final IllegalAccessException e2) {
            final String errorMsg = "Unable to access the property provided.";
            final IllegalArgumentException iae = new IllegalArgumentException("Unable to access the property provided.");
            if (!BeanUtils.initCause(iae, e2)) {
                this.log.error((Object)"Unable to access the property provided.", (Throwable)e2);
            }
            throw iae;
        }
        catch (final InvocationTargetException e3) {
            final String errorMsg = "Exception occurred in property's getter";
            final IllegalArgumentException iae = new IllegalArgumentException("Exception occurred in property's getter");
            if (!BeanUtils.initCause(iae, e3)) {
                this.log.error((Object)"Exception occurred in property's getter", (Throwable)e3);
            }
            throw iae;
        }
        catch (final NoSuchMethodException e4) {
            final String errorMsg = "No property found for name [" + this.propertyName + "]";
            final IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
            if (!BeanUtils.initCause(iae, e4)) {
                this.log.error((Object)errorMsg, (Throwable)e4);
            }
            throw iae;
        }
        return propertyValue;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public boolean isIgnoreNull() {
        return this.ignoreNull;
    }
}
