package com.azul.crs.com.fasterxml.jackson.databind.deser.impl;

import java.lang.reflect.Member;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.HashMap;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.azul.crs.com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;

public class CreatorCollector
{
    protected static final int C_DEFAULT = 0;
    protected static final int C_STRING = 1;
    protected static final int C_INT = 2;
    protected static final int C_LONG = 3;
    protected static final int C_BIG_INTEGER = 4;
    protected static final int C_DOUBLE = 5;
    protected static final int C_BIG_DECIMAL = 6;
    protected static final int C_BOOLEAN = 7;
    protected static final int C_DELEGATE = 8;
    protected static final int C_PROPS = 9;
    protected static final int C_ARRAY_DELEGATE = 10;
    protected static final String[] TYPE_DESCS;
    protected final BeanDescription _beanDesc;
    protected final boolean _canFixAccess;
    protected final boolean _forceAccess;
    protected final AnnotatedWithParams[] _creators;
    protected int _explicitCreators;
    protected boolean _hasNonDefaultCreator;
    protected SettableBeanProperty[] _delegateArgs;
    protected SettableBeanProperty[] _arrayDelegateArgs;
    protected SettableBeanProperty[] _propertyBasedArgs;
    
    public CreatorCollector(final BeanDescription beanDesc, final MapperConfig<?> config) {
        this._creators = new AnnotatedWithParams[11];
        this._explicitCreators = 0;
        this._hasNonDefaultCreator = false;
        this._beanDesc = beanDesc;
        this._canFixAccess = config.canOverrideAccessModifiers();
        this._forceAccess = config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
    }
    
    public ValueInstantiator constructValueInstantiator(final DeserializationContext ctxt) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JavaType delegateType = this._computeDelegateType(ctxt, this._creators[8], this._delegateArgs);
        final JavaType arrayDelegateType = this._computeDelegateType(ctxt, this._creators[10], this._arrayDelegateArgs);
        final JavaType type = this._beanDesc.getType();
        final StdValueInstantiator inst = new StdValueInstantiator(config, type);
        inst.configureFromObjectSettings(this._creators[0], this._creators[8], delegateType, this._delegateArgs, this._creators[9], this._propertyBasedArgs);
        inst.configureFromArraySettings(this._creators[10], arrayDelegateType, this._arrayDelegateArgs);
        inst.configureFromStringCreator(this._creators[1]);
        inst.configureFromIntCreator(this._creators[2]);
        inst.configureFromLongCreator(this._creators[3]);
        inst.configureFromBigIntegerCreator(this._creators[4]);
        inst.configureFromDoubleCreator(this._creators[5]);
        inst.configureFromBigDecimalCreator(this._creators[6]);
        inst.configureFromBooleanCreator(this._creators[7]);
        return inst;
    }
    
    public void setDefaultCreator(final AnnotatedWithParams creator) {
        this._creators[0] = this._fixAccess(creator);
    }
    
    public void addStringCreator(final AnnotatedWithParams creator, final boolean explicit) {
        this.verifyNonDup(creator, 1, explicit);
    }
    
    public void addIntCreator(final AnnotatedWithParams creator, final boolean explicit) {
        this.verifyNonDup(creator, 2, explicit);
    }
    
    public void addLongCreator(final AnnotatedWithParams creator, final boolean explicit) {
        this.verifyNonDup(creator, 3, explicit);
    }
    
    public void addBigIntegerCreator(final AnnotatedWithParams creator, final boolean explicit) {
        this.verifyNonDup(creator, 4, explicit);
    }
    
    public void addDoubleCreator(final AnnotatedWithParams creator, final boolean explicit) {
        this.verifyNonDup(creator, 5, explicit);
    }
    
    public void addBigDecimalCreator(final AnnotatedWithParams creator, final boolean explicit) {
        this.verifyNonDup(creator, 6, explicit);
    }
    
    public void addBooleanCreator(final AnnotatedWithParams creator, final boolean explicit) {
        this.verifyNonDup(creator, 7, explicit);
    }
    
    public void addDelegatingCreator(final AnnotatedWithParams creator, final boolean explicit, final SettableBeanProperty[] injectables, final int delegateeIndex) {
        if (creator.getParameterType(delegateeIndex).isCollectionLikeType()) {
            if (this.verifyNonDup(creator, 10, explicit)) {
                this._arrayDelegateArgs = injectables;
            }
        }
        else if (this.verifyNonDup(creator, 8, explicit)) {
            this._delegateArgs = injectables;
        }
    }
    
    public void addPropertyCreator(final AnnotatedWithParams creator, final boolean explicit, final SettableBeanProperty[] properties) {
        if (this.verifyNonDup(creator, 9, explicit)) {
            if (properties.length > 1) {
                final HashMap<String, Integer> names = new HashMap<String, Integer>();
                for (int i = 0, len = properties.length; i < len; ++i) {
                    final String name = properties[i].getName();
                    if (!name.isEmpty() || properties[i].getInjectableValueId() == null) {
                        final Integer old = names.put(name, i);
                        if (old != null) {
                            throw new IllegalArgumentException(String.format("Duplicate creator property \"%s\" (index %s vs %d) for type %s ", name, old, i, ClassUtil.nameOf(this._beanDesc.getBeanClass())));
                        }
                    }
                }
            }
            this._propertyBasedArgs = properties;
        }
    }
    
    public boolean hasDefaultCreator() {
        return this._creators[0] != null;
    }
    
    public boolean hasDelegatingCreator() {
        return this._creators[8] != null;
    }
    
    public boolean hasPropertyBasedCreator() {
        return this._creators[9] != null;
    }
    
    private JavaType _computeDelegateType(final DeserializationContext ctxt, final AnnotatedWithParams creator, final SettableBeanProperty[] delegateArgs) throws JsonMappingException {
        if (!this._hasNonDefaultCreator || creator == null) {
            return null;
        }
        int ix = 0;
        if (delegateArgs != null) {
            for (int i = 0, len = delegateArgs.length; i < len; ++i) {
                if (delegateArgs[i] == null) {
                    ix = i;
                    break;
                }
            }
        }
        final DeserializationConfig config = ctxt.getConfig();
        JavaType baseType = creator.getParameterType(ix);
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (intr != null) {
            final AnnotatedParameter delegate = creator.getParameter(ix);
            final Object deserDef = intr.findDeserializer(delegate);
            if (deserDef != null) {
                final JsonDeserializer<Object> deser = ctxt.deserializerInstance(delegate, deserDef);
                baseType = baseType.withValueHandler(deser);
            }
            else {
                baseType = intr.refineDeserializationType(config, delegate, baseType);
            }
        }
        return baseType;
    }
    
    private <T extends AnnotatedMember> T _fixAccess(final T member) {
        if (member != null && this._canFixAccess) {
            ClassUtil.checkAndFixAccess((Member)member.getAnnotated(), this._forceAccess);
        }
        return member;
    }
    
    protected boolean verifyNonDup(final AnnotatedWithParams newOne, final int typeIndex, final boolean explicit) {
        final int mask = 1 << typeIndex;
        this._hasNonDefaultCreator = true;
        final AnnotatedWithParams oldOne = this._creators[typeIndex];
        if (oldOne != null) {
            boolean verify;
            if ((this._explicitCreators & mask) != 0x0) {
                if (!explicit) {
                    return false;
                }
                verify = true;
            }
            else {
                verify = !explicit;
            }
            if (verify && oldOne.getClass() == newOne.getClass()) {
                final Class<?> oldType = oldOne.getRawParameterType(0);
                final Class<?> newType = newOne.getRawParameterType(0);
                if (oldType == newType) {
                    if (this._isEnumValueOf(newOne)) {
                        return false;
                    }
                    if (!this._isEnumValueOf(oldOne)) {
                        this._reportDuplicateCreator(typeIndex, explicit, oldOne, newOne);
                    }
                }
                else {
                    if (newType.isAssignableFrom(oldType)) {
                        return false;
                    }
                    if (!oldType.isAssignableFrom(newType)) {
                        this._reportDuplicateCreator(typeIndex, explicit, oldOne, newOne);
                    }
                }
            }
        }
        if (explicit) {
            this._explicitCreators |= mask;
        }
        this._creators[typeIndex] = this._fixAccess(newOne);
        return true;
    }
    
    protected void _reportDuplicateCreator(final int typeIndex, final boolean explicit, final AnnotatedWithParams oldOne, final AnnotatedWithParams newOne) {
        throw new IllegalArgumentException(String.format("Conflicting %s creators: already had %s creator %s, encountered another: %s", CreatorCollector.TYPE_DESCS[typeIndex], explicit ? "explicitly marked" : "implicitly discovered", oldOne, newOne));
    }
    
    protected boolean _isEnumValueOf(final AnnotatedWithParams creator) {
        return ClassUtil.isEnumType(creator.getDeclaringClass()) && "valueOf".equals(creator.getName());
    }
    
    static {
        TYPE_DESCS = new String[] { "default", "from-String", "from-int", "from-long", "from-big-integer", "from-double", "from-big-decimal", "from-boolean", "delegate", "property-based", "array-delegate" };
    }
}
