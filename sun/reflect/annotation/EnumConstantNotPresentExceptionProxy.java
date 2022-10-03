package sun.reflect.annotation;

public class EnumConstantNotPresentExceptionProxy extends ExceptionProxy
{
    private static final long serialVersionUID = -604662101303187330L;
    Class<? extends Enum<?>> enumType;
    String constName;
    
    public EnumConstantNotPresentExceptionProxy(final Class<? extends Enum<?>> enumType, final String constName) {
        this.enumType = enumType;
        this.constName = constName;
    }
    
    @Override
    protected RuntimeException generateException() {
        return new EnumConstantNotPresentException(this.enumType, this.constName);
    }
}
