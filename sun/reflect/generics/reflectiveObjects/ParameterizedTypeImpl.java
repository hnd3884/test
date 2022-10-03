package sun.reflect.generics.reflectiveObjects;

import java.util.Arrays;
import java.util.Objects;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

public class ParameterizedTypeImpl implements ParameterizedType
{
    private final Type[] actualTypeArguments;
    private final Class<?> rawType;
    private final Type ownerType;
    
    private ParameterizedTypeImpl(final Class<?> rawType, final Type[] actualTypeArguments, final Type type) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
        this.ownerType = ((type != null) ? type : rawType.getDeclaringClass());
        this.validateConstructorArguments();
    }
    
    private void validateConstructorArguments() {
        if (this.rawType.getTypeParameters().length != this.actualTypeArguments.length) {
            throw new MalformedParameterizedTypeException();
        }
        for (int i = 0; i < this.actualTypeArguments.length; ++i) {}
    }
    
    public static ParameterizedTypeImpl make(final Class<?> clazz, final Type[] array, final Type type) {
        return new ParameterizedTypeImpl(clazz, array, type);
    }
    
    @Override
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments.clone();
    }
    
    @Override
    public Class<?> getRawType() {
        return this.rawType;
    }
    
    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType parameterizedType = (ParameterizedType)o;
        if (this == parameterizedType) {
            return true;
        }
        final Type ownerType = parameterizedType.getOwnerType();
        final Type rawType = parameterizedType.getRawType();
        return Objects.equals(this.ownerType, ownerType) && Objects.equals(this.rawType, rawType) && Arrays.equals(this.actualTypeArguments, parameterizedType.getActualTypeArguments());
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.ownerType != null) {
            if (this.ownerType instanceof Class) {
                sb.append(((Class)this.ownerType).getName());
            }
            else {
                sb.append(this.ownerType.toString());
            }
            sb.append("$");
            if (this.ownerType instanceof ParameterizedTypeImpl) {
                sb.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
            }
            else {
                sb.append(this.rawType.getSimpleName());
            }
        }
        else {
            sb.append(this.rawType.getName());
        }
        if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
            sb.append("<");
            int n = 1;
            for (final Type type : this.actualTypeArguments) {
                if (n == 0) {
                    sb.append(", ");
                }
                sb.append(type.getTypeName());
                n = 0;
            }
            sb.append(">");
        }
        return sb.toString();
    }
}
