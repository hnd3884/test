package com.azul.crs.com.fasterxml.jackson.databind.cfg;

import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class CoercionConfigs implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final int TARGET_TYPE_COUNT;
    protected CoercionAction _defaultAction;
    protected final MutableCoercionConfig _defaultCoercions;
    protected MutableCoercionConfig[] _perTypeCoercions;
    protected Map<Class<?>, MutableCoercionConfig> _perClassCoercions;
    
    public CoercionConfigs() {
        this(CoercionAction.TryConvert, new MutableCoercionConfig(), null, null);
    }
    
    protected CoercionConfigs(final CoercionAction defaultAction, final MutableCoercionConfig defaultCoercions, final MutableCoercionConfig[] perTypeCoercions, final Map<Class<?>, MutableCoercionConfig> perClassCoercions) {
        this._defaultCoercions = defaultCoercions;
        this._defaultAction = defaultAction;
        this._perTypeCoercions = perTypeCoercions;
        this._perClassCoercions = perClassCoercions;
    }
    
    public CoercionConfigs copy() {
        MutableCoercionConfig[] newPerType;
        if (this._perTypeCoercions == null) {
            newPerType = null;
        }
        else {
            final int size = this._perTypeCoercions.length;
            newPerType = new MutableCoercionConfig[size];
            for (int i = 0; i < size; ++i) {
                newPerType[i] = _copy(this._perTypeCoercions[i]);
            }
        }
        Map<Class<?>, MutableCoercionConfig> newPerClass;
        if (this._perClassCoercions == null) {
            newPerClass = null;
        }
        else {
            newPerClass = new HashMap<Class<?>, MutableCoercionConfig>();
            for (final Map.Entry<Class<?>, MutableCoercionConfig> entry : this._perClassCoercions.entrySet()) {
                newPerClass.put(entry.getKey(), entry.getValue().copy());
            }
        }
        return new CoercionConfigs(this._defaultAction, this._defaultCoercions.copy(), newPerType, newPerClass);
    }
    
    private static MutableCoercionConfig _copy(final MutableCoercionConfig src) {
        if (src == null) {
            return null;
        }
        return src.copy();
    }
    
    public MutableCoercionConfig defaultCoercions() {
        return this._defaultCoercions;
    }
    
    public MutableCoercionConfig findOrCreateCoercion(final LogicalType type) {
        if (this._perTypeCoercions == null) {
            this._perTypeCoercions = new MutableCoercionConfig[CoercionConfigs.TARGET_TYPE_COUNT];
        }
        MutableCoercionConfig config = this._perTypeCoercions[type.ordinal()];
        if (config == null) {
            config = (this._perTypeCoercions[type.ordinal()] = new MutableCoercionConfig());
        }
        return config;
    }
    
    public MutableCoercionConfig findOrCreateCoercion(final Class<?> type) {
        if (this._perClassCoercions == null) {
            this._perClassCoercions = new HashMap<Class<?>, MutableCoercionConfig>();
        }
        MutableCoercionConfig config = this._perClassCoercions.get(type);
        if (config == null) {
            config = new MutableCoercionConfig();
            this._perClassCoercions.put(type, config);
        }
        return config;
    }
    
    public CoercionAction findCoercion(final DeserializationConfig config, final LogicalType targetType, final Class<?> targetClass, final CoercionInputShape inputShape) {
        if (this._perClassCoercions != null && targetClass != null) {
            final MutableCoercionConfig cc = this._perClassCoercions.get(targetClass);
            if (cc != null) {
                final CoercionAction act = cc.findAction(inputShape);
                if (act != null) {
                    return act;
                }
            }
        }
        if (this._perTypeCoercions != null && targetType != null) {
            final MutableCoercionConfig cc = this._perTypeCoercions[targetType.ordinal()];
            if (cc != null) {
                final CoercionAction act = cc.findAction(inputShape);
                if (act != null) {
                    return act;
                }
            }
        }
        final CoercionAction act2 = this._defaultCoercions.findAction(inputShape);
        if (act2 != null) {
            return act2;
        }
        switch (inputShape) {
            case EmptyArray: {
                return config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT) ? CoercionAction.AsNull : CoercionAction.Fail;
            }
            case Float: {
                if (targetType == LogicalType.Integer) {
                    return config.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT) ? CoercionAction.TryConvert : CoercionAction.Fail;
                }
                break;
            }
            case Integer: {
                if (targetType == LogicalType.Enum && config.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                    return CoercionAction.Fail;
                }
                break;
            }
        }
        final boolean baseScalar = targetType == LogicalType.Float || targetType == LogicalType.Integer || targetType == LogicalType.Boolean || targetType == LogicalType.DateTime;
        if (baseScalar && !config.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            return CoercionAction.Fail;
        }
        if (inputShape != CoercionInputShape.EmptyString) {
            return this._defaultAction;
        }
        if (baseScalar || config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return CoercionAction.AsNull;
        }
        if (targetType == LogicalType.OtherScalar) {
            return CoercionAction.TryConvert;
        }
        return CoercionAction.Fail;
    }
    
    public CoercionAction findCoercionFromBlankString(final DeserializationConfig config, final LogicalType targetType, final Class<?> targetClass, final CoercionAction actionIfBlankNotAllowed) {
        Boolean acceptBlankAsEmpty = null;
        CoercionAction action = null;
        if (this._perClassCoercions != null && targetClass != null) {
            final MutableCoercionConfig cc = this._perClassCoercions.get(targetClass);
            if (cc != null) {
                acceptBlankAsEmpty = cc.getAcceptBlankAsEmpty();
                action = cc.findAction(CoercionInputShape.EmptyString);
            }
        }
        if (this._perTypeCoercions != null && targetType != null) {
            final MutableCoercionConfig cc = this._perTypeCoercions[targetType.ordinal()];
            if (cc != null) {
                if (acceptBlankAsEmpty == null) {
                    acceptBlankAsEmpty = cc.getAcceptBlankAsEmpty();
                }
                if (action == null) {
                    action = cc.findAction(CoercionInputShape.EmptyString);
                }
            }
        }
        if (acceptBlankAsEmpty == null) {
            acceptBlankAsEmpty = this._defaultCoercions.getAcceptBlankAsEmpty();
        }
        if (action == null) {
            action = this._defaultCoercions.findAction(CoercionInputShape.EmptyString);
        }
        if (!Boolean.TRUE.equals(acceptBlankAsEmpty)) {
            return actionIfBlankNotAllowed;
        }
        if (action != null) {
            return action;
        }
        return config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) ? CoercionAction.AsNull : CoercionAction.Fail;
    }
    
    static {
        TARGET_TYPE_COUNT = LogicalType.values().length;
    }
}
