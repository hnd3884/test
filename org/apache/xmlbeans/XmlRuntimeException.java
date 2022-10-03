package org.apache.xmlbeans;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XmlRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private List _errors;
    
    public XmlRuntimeException(final String m) {
        super(m);
    }
    
    public XmlRuntimeException(final String m, final Throwable t) {
        super(m, t);
    }
    
    public XmlRuntimeException(final Throwable t) {
        super(t);
    }
    
    public XmlRuntimeException(final String m, final Throwable t, final Collection errors) {
        super(m, t);
        if (errors != null) {
            this._errors = Collections.unmodifiableList((List<?>)new ArrayList<Object>(errors));
        }
    }
    
    public XmlRuntimeException(final XmlError error) {
        this(error.toString(), null, error);
    }
    
    public XmlRuntimeException(final String m, final Throwable t, final XmlError error) {
        this(m, t, Collections.singletonList(error));
    }
    
    public XmlRuntimeException(final XmlException xmlException) {
        super(xmlException.getMessage(), xmlException.getCause());
        final Collection errors = xmlException.getErrors();
        if (errors != null) {
            this._errors = Collections.unmodifiableList((List<?>)new ArrayList<Object>(errors));
        }
    }
    
    public XmlError getError() {
        if (this._errors == null || this._errors.size() == 0) {
            return null;
        }
        return this._errors.get(0);
    }
    
    public Collection getErrors() {
        return this._errors;
    }
}
