package sun.reflect.annotation;

public class TypeNotPresentExceptionProxy extends ExceptionProxy
{
    private static final long serialVersionUID = 5565925172427947573L;
    String typeName;
    Throwable cause;
    
    public TypeNotPresentExceptionProxy(final String typeName, final Throwable cause) {
        this.typeName = typeName;
        this.cause = cause;
    }
    
    @Override
    protected RuntimeException generateException() {
        return new TypeNotPresentException(this.typeName, this.cause);
    }
}
