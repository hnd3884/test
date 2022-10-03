package org.msgpack.template.builder;

import java.lang.reflect.Type;
import org.msgpack.template.FieldOption;

public abstract class FieldEntry
{
    protected FieldOption option;
    
    public FieldEntry() {
        this(FieldOption.IGNORE);
    }
    
    public FieldEntry(final FieldOption option) {
        this.option = option;
    }
    
    public FieldOption getOption() {
        return this.option;
    }
    
    public void setOption(final FieldOption option) {
        this.option = option;
    }
    
    public boolean isAvailable() {
        return this.option != FieldOption.IGNORE;
    }
    
    public boolean isOptional() {
        return this.option == FieldOption.OPTIONAL;
    }
    
    public boolean isNotNullable() {
        return this.option == FieldOption.NOTNULLABLE;
    }
    
    public abstract String getName();
    
    public abstract Class<?> getType();
    
    public abstract Type getGenericType();
    
    public abstract Object get(final Object p0);
    
    public abstract void set(final Object p0, final Object p1);
    
    public String getJavaTypeName() {
        final Class<?> type = this.getType();
        if (type.isArray()) {
            return this.arrayTypeToString(type);
        }
        return type.getName();
    }
    
    public String arrayTypeToString(final Class<?> type) {
        int dim;
        Class<?> baseType;
        for (dim = 1, baseType = type.getComponentType(); baseType.isArray(); baseType = baseType.getComponentType(), ++dim) {}
        final StringBuilder sb = new StringBuilder();
        sb.append(baseType.getName());
        for (int i = 0; i < dim; ++i) {
            sb.append("[]");
        }
        return sb.toString();
    }
}
