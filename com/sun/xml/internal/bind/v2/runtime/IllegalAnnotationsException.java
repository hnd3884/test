package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationsException extends JAXBException
{
    private final List<IllegalAnnotationException> errors;
    private static final long serialVersionUID = 1L;
    
    public IllegalAnnotationsException(final List<IllegalAnnotationException> errors) {
        super(errors.size() + " counts of IllegalAnnotationExceptions");
        assert !errors.isEmpty() : "there must be at least one error";
        this.errors = Collections.unmodifiableList((List<? extends IllegalAnnotationException>)new ArrayList<IllegalAnnotationException>(errors));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append('\n');
        for (final IllegalAnnotationException error : this.errors) {
            sb.append(error.toString()).append('\n');
        }
        return sb.toString();
    }
    
    public List<IllegalAnnotationException> getErrors() {
        return this.errors;
    }
    
    public static class Builder implements ErrorHandler
    {
        private final List<IllegalAnnotationException> list;
        
        public Builder() {
            this.list = new ArrayList<IllegalAnnotationException>();
        }
        
        @Override
        public void error(final IllegalAnnotationException e) {
            this.list.add(e);
        }
        
        public void check() throws IllegalAnnotationsException {
            if (this.list.isEmpty()) {
                return;
            }
            throw new IllegalAnnotationsException(this.list);
        }
    }
}
