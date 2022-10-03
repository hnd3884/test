package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownAnnotationValueException extends UnknownEntityException
{
    private static final long serialVersionUID = 269L;
    private transient AnnotationValue av;
    private transient Object parameter;
    
    public UnknownAnnotationValueException(final AnnotationValue av, final Object parameter) {
        super("Unknown annotation value: " + av);
        this.av = av;
        this.parameter = parameter;
    }
    
    public AnnotationValue getUnknownAnnotationValue() {
        return this.av;
    }
    
    public Object getArgument() {
        return this.parameter;
    }
}
