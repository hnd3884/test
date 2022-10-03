package com.fasterxml.jackson.databind.jsontype;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

public class DefaultBaseTypeLimitingValidator extends PolymorphicTypeValidator implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Validity validateBaseType(final MapperConfig<?> config, final JavaType baseType) {
        if (this.isUnsafeBaseType(config, baseType)) {
            return Validity.DENIED;
        }
        return Validity.INDETERMINATE;
    }
    
    @Override
    public Validity validateSubClassName(final MapperConfig<?> config, final JavaType baseType, final String subClassName) {
        return Validity.INDETERMINATE;
    }
    
    @Override
    public Validity validateSubType(final MapperConfig<?> config, final JavaType baseType, final JavaType subType) {
        return this.isSafeSubType(config, baseType, subType) ? Validity.ALLOWED : Validity.DENIED;
    }
    
    protected boolean isUnsafeBaseType(final MapperConfig<?> config, final JavaType baseType) {
        return UnsafeBaseTypes.instance.isUnsafeBaseType(baseType.getRawClass());
    }
    
    protected boolean isSafeSubType(final MapperConfig<?> config, final JavaType baseType, final JavaType subType) {
        return true;
    }
    
    private static final class UnsafeBaseTypes
    {
        public static final UnsafeBaseTypes instance;
        private final Set<String> UNSAFE;
        
        private UnsafeBaseTypes() {
            (this.UNSAFE = new HashSet<String>()).add(Object.class.getName());
            this.UNSAFE.add(Closeable.class.getName());
            this.UNSAFE.add(Serializable.class.getName());
            this.UNSAFE.add(AutoCloseable.class.getName());
            this.UNSAFE.add(Cloneable.class.getName());
            this.UNSAFE.add("java.util.logging.Handler");
            this.UNSAFE.add("javax.naming.Referenceable");
            this.UNSAFE.add("javax.sql.DataSource");
        }
        
        public boolean isUnsafeBaseType(final Class<?> rawBaseType) {
            return this.UNSAFE.contains(rawBaseType.getName());
        }
        
        static {
            instance = new UnsafeBaseTypes();
        }
    }
}
