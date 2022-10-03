package org.owasp.esapi;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.owasp.esapi.errors.ValidationException;
import java.util.HashMap;

public class ValidationErrorList
{
    private HashMap<String, ValidationException> errorList;
    
    public ValidationErrorList() {
        this.errorList = new HashMap<String, ValidationException>();
    }
    
    public void addError(final String context, final ValidationException vex) {
        if (context == null) {
            throw new RuntimeException("Context for cannot be null: " + vex.getLogMessage(), vex);
        }
        if (vex == null) {
            throw new RuntimeException("Context (" + context + ") cannot be null");
        }
        if (this.getError(context) != null) {
            throw new RuntimeException("Context (" + context + ") already exists, must be unique");
        }
        this.errorList.put(context, vex);
    }
    
    public List<ValidationException> errors() {
        return new ArrayList<ValidationException>(this.errorList.values());
    }
    
    public ValidationException getError(final String context) {
        if (context == null) {
            return null;
        }
        return this.errorList.get(context);
    }
    
    public boolean isEmpty() {
        return this.errorList.isEmpty();
    }
    
    public int size() {
        return this.errorList.size();
    }
}
