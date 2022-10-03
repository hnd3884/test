package org.apache.commons.validator;

import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class ValidatorResult implements Serializable
{
    protected Map hAction;
    protected Field field;
    
    public ValidatorResult(final Field field) {
        this.hAction = new HashMap();
        this.field = null;
        this.field = field;
    }
    
    public void add(final String validatorName, final boolean result) {
        this.add(validatorName, result, null);
    }
    
    public void add(final String validatorName, final boolean result, final Object value) {
        this.hAction.put(validatorName, new ResultStatus(result, value));
    }
    
    public boolean containsAction(final String validatorName) {
        return this.hAction.containsKey(validatorName);
    }
    
    public boolean isValid(final String validatorName) {
        final ResultStatus status = this.hAction.get(validatorName);
        return status != null && status.isValid();
    }
    
    public Object getResult(final String validatorName) {
        final ResultStatus status = this.hAction.get(validatorName);
        return (status == null) ? null : status.getResult();
    }
    
    public Iterator getActions() {
        return Collections.unmodifiableMap((Map<?, ?>)this.hAction).keySet().iterator();
    }
    
    public Map getActionMap() {
        return Collections.unmodifiableMap((Map<?, ?>)this.hAction);
    }
    
    public Field getField() {
        return this.field;
    }
    
    protected class ResultStatus implements Serializable
    {
        private boolean valid;
        private Object result;
        
        public ResultStatus(final boolean valid, final Object result) {
            this.valid = false;
            this.result = null;
            this.valid = valid;
            this.result = result;
        }
        
        public boolean isValid() {
            return this.valid;
        }
        
        public void setValid(final boolean valid) {
            this.valid = valid;
        }
        
        public Object getResult() {
            return this.result;
        }
        
        public void setResult(final Object result) {
            this.result = result;
        }
    }
}
