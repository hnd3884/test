package org.apache.xmlbeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class XmlException extends Exception
{
    private static final long serialVersionUID = 1L;
    private List _errors;
    
    public XmlException(final String m) {
        super(m);
    }
    
    public XmlException(final String m, final Throwable t) {
        super(m, t);
    }
    
    public XmlException(final Throwable t) {
        super(t);
    }
    
    public XmlException(final XmlError error) {
        this(error.toString(), null, error);
    }
    
    public XmlException(final String m, final Throwable t, final XmlError error) {
        this(m, t, Collections.singletonList(error));
    }
    
    public XmlException(final String m, final Throwable t, final Collection errors) {
        super(m, t);
        if (errors != null) {
            this._errors = Collections.unmodifiableList((List<?>)new ArrayList<Object>(errors));
        }
    }
    
    public XmlException(final XmlRuntimeException xmlRuntimeException) {
        super(xmlRuntimeException.getMessage(), xmlRuntimeException.getCause());
        final Collection errors = xmlRuntimeException.getErrors();
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
