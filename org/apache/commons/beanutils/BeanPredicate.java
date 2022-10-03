package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.collections.Predicate;

public class BeanPredicate implements Predicate
{
    private final Log log;
    private String propertyName;
    private Predicate predicate;
    
    public BeanPredicate(final String propertyName, final Predicate predicate) {
        this.log = LogFactory.getLog((Class)this.getClass());
        this.propertyName = propertyName;
        this.predicate = predicate;
    }
    
    public boolean evaluate(final Object object) {
        boolean evaluation = false;
        try {
            final Object propValue = PropertyUtils.getProperty(object, this.propertyName);
            evaluation = this.predicate.evaluate(propValue);
        }
        catch (final IllegalArgumentException e) {
            final String errorMsg = "Problem during evaluation.";
            this.log.error((Object)"ERROR: Problem during evaluation.", (Throwable)e);
            throw e;
        }
        catch (final IllegalAccessException e2) {
            final String errorMsg = "Unable to access the property provided.";
            this.log.error((Object)"Unable to access the property provided.", (Throwable)e2);
            throw new IllegalArgumentException("Unable to access the property provided.");
        }
        catch (final InvocationTargetException e3) {
            final String errorMsg = "Exception occurred in property's getter";
            this.log.error((Object)"Exception occurred in property's getter", (Throwable)e3);
            throw new IllegalArgumentException("Exception occurred in property's getter");
        }
        catch (final NoSuchMethodException e4) {
            final String errorMsg = "Property not found.";
            this.log.error((Object)"Property not found.", (Throwable)e4);
            throw new IllegalArgumentException("Property not found.");
        }
        return evaluation;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }
    
    public Predicate getPredicate() {
        return this.predicate;
    }
    
    public void setPredicate(final Predicate predicate) {
        this.predicate = predicate;
    }
}
