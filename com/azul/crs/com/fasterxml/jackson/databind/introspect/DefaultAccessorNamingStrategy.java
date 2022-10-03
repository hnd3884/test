package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import com.azul.crs.com.fasterxml.jackson.databind.jdk14.JDK14Util;
import java.util.HashSet;
import java.util.Set;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import java.io.Serializable;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;

public class DefaultAccessorNamingStrategy extends AccessorNamingStrategy
{
    protected final MapperConfig<?> _config;
    protected final AnnotatedClass _forClass;
    protected final BaseNameValidator _baseNameValidator;
    protected final boolean _stdBeanNaming;
    protected final String _getterPrefix;
    protected final String _isGetterPrefix;
    protected final String _mutatorPrefix;
    
    protected DefaultAccessorNamingStrategy(final MapperConfig<?> config, final AnnotatedClass forClass, final String mutatorPrefix, final String getterPrefix, final String isGetterPrefix, final BaseNameValidator baseNameValidator) {
        this._config = config;
        this._forClass = forClass;
        this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
        this._mutatorPrefix = mutatorPrefix;
        this._getterPrefix = getterPrefix;
        this._isGetterPrefix = isGetterPrefix;
        this._baseNameValidator = baseNameValidator;
    }
    
    @Override
    public String findNameForIsGetter(final AnnotatedMethod am, final String name) {
        if (this._isGetterPrefix != null) {
            final Class<?> rt = am.getRawType();
            if ((rt == Boolean.class || rt == Boolean.TYPE) && name.startsWith(this._isGetterPrefix)) {
                return this._stdBeanNaming ? this.stdManglePropertyName(name, 2) : this.legacyManglePropertyName(name, 2);
            }
        }
        return null;
    }
    
    @Override
    public String findNameForRegularGetter(final AnnotatedMethod am, final String name) {
        if (this._getterPrefix != null && name.startsWith(this._getterPrefix)) {
            if ("getCallbacks".equals(name)) {
                if (this._isCglibGetCallbacks(am)) {
                    return null;
                }
            }
            else if ("getMetaClass".equals(name) && this._isGroovyMetaClassGetter(am)) {
                return null;
            }
            return this._stdBeanNaming ? this.stdManglePropertyName(name, this._getterPrefix.length()) : this.legacyManglePropertyName(name, this._getterPrefix.length());
        }
        return null;
    }
    
    @Override
    public String findNameForMutator(final AnnotatedMethod am, final String name) {
        if (this._mutatorPrefix != null && name.startsWith(this._mutatorPrefix)) {
            return this._stdBeanNaming ? this.stdManglePropertyName(name, this._mutatorPrefix.length()) : this.legacyManglePropertyName(name, this._mutatorPrefix.length());
        }
        return null;
    }
    
    @Override
    public String modifyFieldName(final AnnotatedField field, final String name) {
        return name;
    }
    
    protected String legacyManglePropertyName(final String basename, final int offset) {
        final int end = basename.length();
        if (end == offset) {
            return null;
        }
        char c = basename.charAt(offset);
        if (this._baseNameValidator != null && !this._baseNameValidator.accept(c, basename, offset)) {
            return null;
        }
        char d = Character.toLowerCase(c);
        if (c == d) {
            return basename.substring(offset);
        }
        final StringBuilder sb = new StringBuilder(end - offset);
        sb.append(d);
        for (int i = offset + 1; i < end; ++i) {
            c = basename.charAt(i);
            d = Character.toLowerCase(c);
            if (c == d) {
                sb.append(basename, i, end);
                break;
            }
            sb.append(d);
        }
        return sb.toString();
    }
    
    protected String stdManglePropertyName(final String basename, final int offset) {
        final int end = basename.length();
        if (end == offset) {
            return null;
        }
        final char c0 = basename.charAt(offset);
        if (this._baseNameValidator != null && !this._baseNameValidator.accept(c0, basename, offset)) {
            return null;
        }
        final char c2 = Character.toLowerCase(c0);
        if (c0 == c2) {
            return basename.substring(offset);
        }
        if (offset + 1 < end && Character.isUpperCase(basename.charAt(offset + 1))) {
            return basename.substring(offset);
        }
        final StringBuilder sb = new StringBuilder(end - offset);
        sb.append(c2);
        sb.append(basename, offset + 1, end);
        return sb.toString();
    }
    
    protected boolean _isCglibGetCallbacks(final AnnotatedMethod am) {
        final Class<?> rt = am.getRawType();
        if (rt.isArray()) {
            final Class<?> compType = rt.getComponentType();
            final String className = compType.getName();
            if (className.contains(".cglib")) {
                return className.startsWith("net.sf.cglib") || className.startsWith("org.hibernate.repackage.cglib") || className.startsWith("org.springframework.cglib");
            }
        }
        return false;
    }
    
    protected boolean _isGroovyMetaClassGetter(final AnnotatedMethod am) {
        return am.getRawType().getName().startsWith("groovy.lang");
    }
    
    public static class Provider extends AccessorNamingStrategy.Provider implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final String _setterPrefix;
        protected final String _withPrefix;
        protected final String _getterPrefix;
        protected final String _isGetterPrefix;
        protected final BaseNameValidator _baseNameValidator;
        
        public Provider() {
            this("set", "with", "get", "is", null);
        }
        
        protected Provider(final Provider p, final String setterPrefix, final String withPrefix, final String getterPrefix, final String isGetterPrefix) {
            this(setterPrefix, withPrefix, getterPrefix, isGetterPrefix, p._baseNameValidator);
        }
        
        protected Provider(final Provider p, final BaseNameValidator vld) {
            this(p._setterPrefix, p._withPrefix, p._getterPrefix, p._isGetterPrefix, vld);
        }
        
        protected Provider(final String setterPrefix, final String withPrefix, final String getterPrefix, final String isGetterPrefix, final BaseNameValidator vld) {
            this._setterPrefix = setterPrefix;
            this._withPrefix = withPrefix;
            this._getterPrefix = getterPrefix;
            this._isGetterPrefix = isGetterPrefix;
            this._baseNameValidator = vld;
        }
        
        public Provider withSetterPrefix(final String prefix) {
            return new Provider(this, prefix, this._withPrefix, this._getterPrefix, this._isGetterPrefix);
        }
        
        public Provider withBuilderPrefix(final String prefix) {
            return new Provider(this, this._setterPrefix, prefix, this._getterPrefix, this._isGetterPrefix);
        }
        
        public Provider withGetterPrefix(final String prefix) {
            return new Provider(this, this._setterPrefix, this._withPrefix, prefix, this._isGetterPrefix);
        }
        
        public Provider withIsGetterPrefix(final String prefix) {
            return new Provider(this, this._setterPrefix, this._withPrefix, this._getterPrefix, prefix);
        }
        
        public Provider withFirstCharAcceptance(final boolean allowLowerCaseFirstChar, final boolean allowNonLetterFirstChar) {
            return this.withBaseNameValidator(FirstCharBasedValidator.forFirstNameRule(allowLowerCaseFirstChar, allowNonLetterFirstChar));
        }
        
        public Provider withBaseNameValidator(final BaseNameValidator vld) {
            return new Provider(this, vld);
        }
        
        @Override
        public AccessorNamingStrategy forPOJO(final MapperConfig<?> config, final AnnotatedClass targetClass) {
            return new DefaultAccessorNamingStrategy(config, targetClass, this._setterPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
        }
        
        @Override
        public AccessorNamingStrategy forBuilder(final MapperConfig<?> config, final AnnotatedClass builderClass, final BeanDescription valueTypeDesc) {
            final AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
            final JsonPOJOBuilder.Value builderConfig = (ai == null) ? null : ai.findPOJOBuilderConfig(builderClass);
            final String mutatorPrefix = (builderConfig == null) ? this._withPrefix : builderConfig.withPrefix;
            return new DefaultAccessorNamingStrategy(config, builderClass, mutatorPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
        }
        
        @Override
        public AccessorNamingStrategy forRecord(final MapperConfig<?> config, final AnnotatedClass recordClass) {
            return new RecordNaming(config, recordClass);
        }
    }
    
    public static class FirstCharBasedValidator implements BaseNameValidator
    {
        private final boolean _allowLowerCaseFirstChar;
        private final boolean _allowNonLetterFirstChar;
        
        protected FirstCharBasedValidator(final boolean allowLowerCaseFirstChar, final boolean allowNonLetterFirstChar) {
            this._allowLowerCaseFirstChar = allowLowerCaseFirstChar;
            this._allowNonLetterFirstChar = allowNonLetterFirstChar;
        }
        
        public static BaseNameValidator forFirstNameRule(final boolean allowLowerCaseFirstChar, final boolean allowNonLetterFirstChar) {
            if (!allowLowerCaseFirstChar && !allowNonLetterFirstChar) {
                return null;
            }
            return new FirstCharBasedValidator(allowLowerCaseFirstChar, allowNonLetterFirstChar);
        }
        
        @Override
        public boolean accept(final char firstChar, final String basename, final int offset) {
            if (Character.isLetter(firstChar)) {
                return this._allowLowerCaseFirstChar || !Character.isLowerCase(firstChar);
            }
            return this._allowNonLetterFirstChar;
        }
    }
    
    public static class RecordNaming extends DefaultAccessorNamingStrategy
    {
        protected final Set<String> _fieldNames;
        
        public RecordNaming(final MapperConfig<?> config, final AnnotatedClass forClass) {
            super(config, forClass, null, "get", "is", null);
            this._fieldNames = new HashSet<String>();
            for (final String name : JDK14Util.getRecordFieldNames(forClass.getRawType())) {
                this._fieldNames.add(name);
            }
        }
        
        @Override
        public String findNameForRegularGetter(final AnnotatedMethod am, final String name) {
            if (this._fieldNames.contains(name)) {
                return name;
            }
            return super.findNameForRegularGetter(am, name);
        }
    }
    
    public interface BaseNameValidator
    {
        boolean accept(final char p0, final String p1, final int p2);
    }
}
