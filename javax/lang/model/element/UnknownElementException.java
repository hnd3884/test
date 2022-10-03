package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownElementException extends UnknownEntityException
{
    private static final long serialVersionUID = 269L;
    private transient Element element;
    private transient Object parameter;
    
    public UnknownElementException(final Element element, final Object parameter) {
        super("Unknown element: " + element);
        this.element = element;
        this.parameter = parameter;
    }
    
    public Element getUnknownElement() {
        return this.element;
    }
    
    public Object getArgument() {
        return this.parameter;
    }
}
