package javax.lang.model.type;

import javax.lang.model.UnknownEntityException;

public class UnknownTypeException extends UnknownEntityException
{
    private static final long serialVersionUID = 269L;
    private transient TypeMirror type;
    private transient Object parameter;
    
    public UnknownTypeException(final TypeMirror type, final Object parameter) {
        super("Unknown type: " + type);
        this.type = type;
        this.parameter = parameter;
    }
    
    public TypeMirror getUnknownType() {
        return this.type;
    }
    
    public Object getArgument() {
        return this.parameter;
    }
}
