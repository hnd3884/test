package com.fasterxml.jackson.databind.jsontype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.List;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.util.Set;
import java.io.Serializable;

public class BasicPolymorphicTypeValidator extends Base implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Set<Class<?>> _invalidBaseTypes;
    protected final TypeMatcher[] _baseTypeMatchers;
    protected final NameMatcher[] _subTypeNameMatchers;
    protected final TypeMatcher[] _subClassMatchers;
    
    protected BasicPolymorphicTypeValidator(final Set<Class<?>> invalidBaseTypes, final TypeMatcher[] baseTypeMatchers, final NameMatcher[] subTypeNameMatchers, final TypeMatcher[] subClassMatchers) {
        this._invalidBaseTypes = invalidBaseTypes;
        this._baseTypeMatchers = baseTypeMatchers;
        this._subTypeNameMatchers = subTypeNameMatchers;
        this._subClassMatchers = subClassMatchers;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public Validity validateBaseType(final MapperConfig<?> ctxt, final JavaType baseType) {
        final Class<?> rawBase = baseType.getRawClass();
        if (this._invalidBaseTypes != null && this._invalidBaseTypes.contains(rawBase)) {
            return Validity.DENIED;
        }
        if (this._baseTypeMatchers != null) {
            for (final TypeMatcher m : this._baseTypeMatchers) {
                if (m.match(ctxt, rawBase)) {
                    return Validity.ALLOWED;
                }
            }
        }
        return Validity.INDETERMINATE;
    }
    
    @Override
    public Validity validateSubClassName(final MapperConfig<?> ctxt, final JavaType baseType, final String subClassName) throws JsonMappingException {
        if (this._subTypeNameMatchers != null) {
            for (final NameMatcher m : this._subTypeNameMatchers) {
                if (m.match(ctxt, subClassName)) {
                    return Validity.ALLOWED;
                }
            }
        }
        return Validity.INDETERMINATE;
    }
    
    @Override
    public Validity validateSubType(final MapperConfig<?> ctxt, final JavaType baseType, final JavaType subType) throws JsonMappingException {
        if (this._subClassMatchers != null) {
            final Class<?> subClass = subType.getRawClass();
            for (final TypeMatcher m : this._subClassMatchers) {
                if (m.match(ctxt, subClass)) {
                    return Validity.ALLOWED;
                }
            }
        }
        return Validity.INDETERMINATE;
    }
    
    public abstract static class TypeMatcher
    {
        public abstract boolean match(final MapperConfig<?> p0, final Class<?> p1);
    }
    
    public abstract static class NameMatcher
    {
        public abstract boolean match(final MapperConfig<?> p0, final String p1);
    }
    
    public static class Builder
    {
        protected Set<Class<?>> _invalidBaseTypes;
        protected List<TypeMatcher> _baseTypeMatchers;
        protected List<NameMatcher> _subTypeNameMatchers;
        protected List<TypeMatcher> _subTypeClassMatchers;
        
        protected Builder() {
        }
        
        public Builder allowIfBaseType(final Class<?> baseOfBase) {
            return this._appendBaseMatcher(new TypeMatcher() {
                @Override
                public boolean match(final MapperConfig<?> config, final Class<?> clazz) {
                    return baseOfBase.isAssignableFrom(clazz);
                }
            });
        }
        
        public Builder allowIfBaseType(final Pattern patternForBase) {
            return this._appendBaseMatcher(new TypeMatcher() {
                @Override
                public boolean match(final MapperConfig<?> config, final Class<?> clazz) {
                    return patternForBase.matcher(clazz.getName()).matches();
                }
            });
        }
        
        public Builder allowIfBaseType(final String prefixForBase) {
            return this._appendBaseMatcher(new TypeMatcher() {
                @Override
                public boolean match(final MapperConfig<?> config, final Class<?> clazz) {
                    return clazz.getName().startsWith(prefixForBase);
                }
            });
        }
        
        public Builder allowIfBaseType(final TypeMatcher matcher) {
            return this._appendBaseMatcher(matcher);
        }
        
        public Builder denyForExactBaseType(final Class<?> baseTypeToDeny) {
            if (this._invalidBaseTypes == null) {
                this._invalidBaseTypes = new HashSet<Class<?>>();
            }
            this._invalidBaseTypes.add(baseTypeToDeny);
            return this;
        }
        
        public Builder allowIfSubType(final Class<?> subTypeBase) {
            return this._appendSubClassMatcher(new TypeMatcher() {
                @Override
                public boolean match(final MapperConfig<?> config, final Class<?> clazz) {
                    return subTypeBase.isAssignableFrom(clazz);
                }
            });
        }
        
        public Builder allowIfSubType(final Pattern patternForSubType) {
            return this._appendSubNameMatcher(new NameMatcher() {
                @Override
                public boolean match(final MapperConfig<?> config, final String clazzName) {
                    return patternForSubType.matcher(clazzName).matches();
                }
            });
        }
        
        public Builder allowIfSubType(final String prefixForSubType) {
            return this._appendSubNameMatcher(new NameMatcher() {
                @Override
                public boolean match(final MapperConfig<?> config, final String clazzName) {
                    return clazzName.startsWith(prefixForSubType);
                }
            });
        }
        
        public Builder allowIfSubType(final TypeMatcher matcher) {
            return this._appendSubClassMatcher(matcher);
        }
        
        public Builder allowIfSubTypeIsArray() {
            return this._appendSubClassMatcher(new TypeMatcher() {
                @Override
                public boolean match(final MapperConfig<?> config, final Class<?> clazz) {
                    return clazz.isArray();
                }
            });
        }
        
        public BasicPolymorphicTypeValidator build() {
            return new BasicPolymorphicTypeValidator(this._invalidBaseTypes, (TypeMatcher[])((this._baseTypeMatchers == null) ? null : ((TypeMatcher[])this._baseTypeMatchers.toArray(new TypeMatcher[0]))), (NameMatcher[])((this._subTypeNameMatchers == null) ? null : ((NameMatcher[])this._subTypeNameMatchers.toArray(new NameMatcher[0]))), (TypeMatcher[])((this._subTypeClassMatchers == null) ? null : ((TypeMatcher[])this._subTypeClassMatchers.toArray(new TypeMatcher[0]))));
        }
        
        protected Builder _appendBaseMatcher(final TypeMatcher matcher) {
            if (this._baseTypeMatchers == null) {
                this._baseTypeMatchers = new ArrayList<TypeMatcher>();
            }
            this._baseTypeMatchers.add(matcher);
            return this;
        }
        
        protected Builder _appendSubNameMatcher(final NameMatcher matcher) {
            if (this._subTypeNameMatchers == null) {
                this._subTypeNameMatchers = new ArrayList<NameMatcher>();
            }
            this._subTypeNameMatchers.add(matcher);
            return this;
        }
        
        protected Builder _appendSubClassMatcher(final TypeMatcher matcher) {
            if (this._subTypeClassMatchers == null) {
                this._subTypeClassMatchers = new ArrayList<TypeMatcher>();
            }
            this._subTypeClassMatchers.add(matcher);
            return this;
        }
    }
}
