package com.azul.crs.com.fasterxml.jackson.databind.cfg;

import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;

public final class ConstructorDetector implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final ConstructorDetector DEFAULT;
    public static final ConstructorDetector USE_PROPERTIES_BASED;
    public static final ConstructorDetector USE_DELEGATING;
    public static final ConstructorDetector EXPLICIT_ONLY;
    protected final SingleArgConstructor _singleArgMode;
    protected final boolean _requireCtorAnnotation;
    protected final boolean _allowJDKTypeCtors;
    
    protected ConstructorDetector(final SingleArgConstructor singleArgMode, final boolean requireCtorAnnotation, final boolean allowJDKTypeCtors) {
        this._singleArgMode = singleArgMode;
        this._requireCtorAnnotation = requireCtorAnnotation;
        this._allowJDKTypeCtors = allowJDKTypeCtors;
    }
    
    protected ConstructorDetector(final SingleArgConstructor singleArgMode) {
        this(singleArgMode, false, false);
    }
    
    public ConstructorDetector withSingleArgMode(final SingleArgConstructor singleArgMode) {
        return new ConstructorDetector(singleArgMode, this._requireCtorAnnotation, this._allowJDKTypeCtors);
    }
    
    public ConstructorDetector withRequireAnnotation(final boolean state) {
        return new ConstructorDetector(this._singleArgMode, state, this._allowJDKTypeCtors);
    }
    
    public ConstructorDetector withAllowJDKTypeConstructors(final boolean state) {
        return new ConstructorDetector(this._singleArgMode, this._requireCtorAnnotation, state);
    }
    
    public SingleArgConstructor singleArgMode() {
        return this._singleArgMode;
    }
    
    public boolean requireCtorAnnotation() {
        return this._requireCtorAnnotation;
    }
    
    public boolean allowJDKTypeConstructors() {
        return this._allowJDKTypeCtors;
    }
    
    public boolean singleArgCreatorDefaultsToDelegating() {
        return this._singleArgMode == SingleArgConstructor.DELEGATING;
    }
    
    public boolean singleArgCreatorDefaultsToProperties() {
        return this._singleArgMode == SingleArgConstructor.PROPERTIES;
    }
    
    public boolean shouldIntrospectorImplicitConstructors(final Class<?> rawType) {
        return !this._requireCtorAnnotation && (this._allowJDKTypeCtors || !ClassUtil.isJDKClass(rawType) || Throwable.class.isAssignableFrom(rawType));
    }
    
    static {
        DEFAULT = new ConstructorDetector(SingleArgConstructor.HEURISTIC);
        USE_PROPERTIES_BASED = new ConstructorDetector(SingleArgConstructor.PROPERTIES);
        USE_DELEGATING = new ConstructorDetector(SingleArgConstructor.DELEGATING);
        EXPLICIT_ONLY = new ConstructorDetector(SingleArgConstructor.REQUIRE_MODE);
    }
    
    public enum SingleArgConstructor
    {
        DELEGATING, 
        PROPERTIES, 
        HEURISTIC, 
        REQUIRE_MODE;
    }
}
