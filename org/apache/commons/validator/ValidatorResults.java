package org.apache.commons.validator;

import java.util.Iterator;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class ValidatorResults implements Serializable
{
    protected Map hResults;
    
    public ValidatorResults() {
        this.hResults = new HashMap();
    }
    
    public void merge(final ValidatorResults results) {
        this.hResults.putAll(results.hResults);
    }
    
    public void add(final Field field, final String validatorName, final boolean result) {
        this.add(field, validatorName, result, null);
    }
    
    public void add(final Field field, final String validatorName, final boolean result, final Object value) {
        ValidatorResult validatorResult = this.getValidatorResult(field.getKey());
        if (validatorResult == null) {
            validatorResult = new ValidatorResult(field);
            this.hResults.put(field.getKey(), validatorResult);
        }
        validatorResult.add(validatorName, result, value);
    }
    
    public void clear() {
        this.hResults.clear();
    }
    
    public boolean isEmpty() {
        return this.hResults.isEmpty();
    }
    
    public ValidatorResult getValidatorResult(final String key) {
        return this.hResults.get(key);
    }
    
    public Set getPropertyNames() {
        return Collections.unmodifiableSet(this.hResults.keySet());
    }
    
    public Map getResultValueMap() {
        final Map results = new HashMap();
        final Iterator i = this.hResults.keySet().iterator();
        while (i.hasNext()) {
            final String propertyKey = i.next();
            final ValidatorResult vr = this.getValidatorResult(propertyKey);
            final Iterator x = vr.getActions();
            while (x.hasNext()) {
                final String actionKey = x.next();
                final Object result = vr.getResult(actionKey);
                if (result != null && !(result instanceof Boolean)) {
                    results.put(propertyKey, result);
                }
            }
        }
        return results;
    }
}
