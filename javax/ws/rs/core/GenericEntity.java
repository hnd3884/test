package javax.ws.rs.core;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericEntity<T>
{
    private final Class<?> rawType;
    private final Type type;
    private final T entity;
    
    protected GenericEntity(final T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The entity must not be null");
        }
        this.entity = entity;
        this.type = GenericType.getTypeArgument(this.getClass(), GenericEntity.class);
        this.rawType = entity.getClass();
    }
    
    public GenericEntity(final T entity, final Type genericType) {
        if (entity == null || genericType == null) {
            throw new IllegalArgumentException("Arguments must not be null.");
        }
        this.entity = entity;
        this.checkTypeCompatibility(this.rawType = entity.getClass(), genericType);
        this.type = genericType;
    }
    
    private void checkTypeCompatibility(final Class<?> c, final Type t) {
        if (t instanceof Class) {
            final Class<?> ct = (Class<?>)t;
            if (ct.isAssignableFrom(c)) {
                return;
            }
        }
        else {
            if (t instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)t;
                final Type rt = pt.getRawType();
                this.checkTypeCompatibility(c, rt);
                return;
            }
            if (c.isArray() && t instanceof GenericArrayType) {
                final GenericArrayType at = (GenericArrayType)t;
                final Type rt = at.getGenericComponentType();
                this.checkTypeCompatibility(c.getComponentType(), rt);
                return;
            }
        }
        throw new IllegalArgumentException("The type is incompatible with the class of the entity.");
    }
    
    public final Class<?> getRawType() {
        return this.rawType;
    }
    
    public final Type getType() {
        return this.type;
    }
    
    public final T getEntity() {
        return this.entity;
    }
    
    @Override
    public boolean equals(final Object obj) {
        final boolean result = this == obj;
        if (!result && obj instanceof GenericEntity) {
            final GenericEntity<?> that = (GenericEntity<?>)obj;
            return this.type.equals(that.type) && this.entity.equals(that.entity);
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return this.entity.hashCode() + this.type.hashCode() * 37 + 5;
    }
    
    @Override
    public String toString() {
        return "GenericEntity{" + this.entity.toString() + ", " + this.type.toString() + "}";
    }
}
