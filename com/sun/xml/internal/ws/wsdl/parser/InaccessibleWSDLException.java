package com.sun.xml.internal.ws.wsdl.parser;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;

public class InaccessibleWSDLException extends WebServiceException
{
    private final List<Throwable> errors;
    private static final long serialVersionUID = 1L;
    
    public InaccessibleWSDLException(final List<Throwable> errors) {
        super(errors.size() + " counts of InaccessibleWSDLException.\n");
        assert !errors.isEmpty() : "there must be at least one error";
        this.errors = Collections.unmodifiableList((List<? extends Throwable>)new ArrayList<Throwable>(errors));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append('\n');
        for (final Throwable error : this.errors) {
            sb.append(error.toString()).append('\n');
        }
        return sb.toString();
    }
    
    public List<Throwable> getErrors() {
        return this.errors;
    }
    
    public static class Builder implements ErrorHandler
    {
        private final List<Throwable> list;
        
        public Builder() {
            this.list = new ArrayList<Throwable>();
        }
        
        @Override
        public void error(final Throwable e) {
            this.list.add(e);
        }
        
        public void check() throws InaccessibleWSDLException {
            if (this.list.isEmpty()) {
                return;
            }
            throw new InaccessibleWSDLException(this.list);
        }
    }
}
