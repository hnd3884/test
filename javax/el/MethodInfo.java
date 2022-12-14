package javax.el;

public class MethodInfo
{
    private final String name;
    private final Class<?>[] paramTypes;
    private final Class<?> returnType;
    
    public MethodInfo(final String name, final Class<?> returnType, final Class<?>[] paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<?> getReturnType() {
        return this.returnType;
    }
    
    public Class<?>[] getParamTypes() {
        return this.paramTypes;
    }
}
