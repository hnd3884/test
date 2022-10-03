package sun.reflect.generics.reflectiveObjects;

import java.util.Objects;
import java.lang.reflect.Type;
import java.lang.reflect.GenericArrayType;

public class GenericArrayTypeImpl implements GenericArrayType
{
    private final Type genericComponentType;
    
    private GenericArrayTypeImpl(final Type genericComponentType) {
        this.genericComponentType = genericComponentType;
    }
    
    public static GenericArrayTypeImpl make(final Type type) {
        return new GenericArrayTypeImpl(type);
    }
    
    @Override
    public Type getGenericComponentType() {
        return this.genericComponentType;
    }
    
    @Override
    public String toString() {
        final Type genericComponentType = this.getGenericComponentType();
        final StringBuilder sb = new StringBuilder();
        if (genericComponentType instanceof Class) {
            sb.append(((Class)genericComponentType).getName());
        }
        else {
            sb.append(genericComponentType.toString());
        }
        sb.append("[]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof GenericArrayType && Objects.equals(this.genericComponentType, ((GenericArrayType)o).getGenericComponentType());
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.genericComponentType);
    }
}
