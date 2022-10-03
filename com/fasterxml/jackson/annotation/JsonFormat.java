package com.fasterxml.jackson.annotation;

import java.util.TimeZone;
import java.util.Locale;
import java.io.Serializable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonFormat {
    public static final String DEFAULT_LOCALE = "##default";
    public static final String DEFAULT_TIMEZONE = "##default";
    
    String pattern() default "";
    
    Shape shape() default Shape.ANY;
    
    String locale() default "##default";
    
    String timezone() default "##default";
    
    OptBoolean lenient() default OptBoolean.DEFAULT;
    
    Feature[] with() default {};
    
    Feature[] without() default {};
    
    public enum Shape
    {
        ANY, 
        NATURAL, 
        SCALAR, 
        ARRAY, 
        OBJECT, 
        NUMBER, 
        NUMBER_FLOAT, 
        NUMBER_INT, 
        STRING, 
        BOOLEAN, 
        BINARY;
        
        public boolean isNumeric() {
            return this == Shape.NUMBER || this == Shape.NUMBER_INT || this == Shape.NUMBER_FLOAT;
        }
        
        public boolean isStructured() {
            return this == Shape.OBJECT || this == Shape.ARRAY;
        }
    }
    
    public enum Feature
    {
        ACCEPT_SINGLE_VALUE_AS_ARRAY, 
        ACCEPT_CASE_INSENSITIVE_PROPERTIES, 
        ACCEPT_CASE_INSENSITIVE_VALUES, 
        WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, 
        WRITE_DATES_WITH_ZONE_ID, 
        WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, 
        WRITE_SORTED_MAP_ENTRIES, 
        ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
    }
    
    public static class Features
    {
        private final int _enabled;
        private final int _disabled;
        private static final Features EMPTY;
        
        private Features(final int e, final int d) {
            this._enabled = e;
            this._disabled = d;
        }
        
        public static Features empty() {
            return Features.EMPTY;
        }
        
        public static Features construct(final JsonFormat f) {
            return construct(f.with(), f.without());
        }
        
        public static Features construct(final Feature[] enabled, final Feature[] disabled) {
            int e = 0;
            for (final Feature f : enabled) {
                e |= 1 << f.ordinal();
            }
            int d = 0;
            for (final Feature f2 : disabled) {
                d |= 1 << f2.ordinal();
            }
            return new Features(e, d);
        }
        
        public Features withOverrides(final Features overrides) {
            if (overrides == null) {
                return this;
            }
            final int overrideD = overrides._disabled;
            final int overrideE = overrides._enabled;
            if (overrideD == 0 && overrideE == 0) {
                return this;
            }
            if (this._enabled == 0 && this._disabled == 0) {
                return overrides;
            }
            final int newE = (this._enabled & ~overrideD) | overrideE;
            final int newD = (this._disabled & ~overrideE) | overrideD;
            if (newE == this._enabled && newD == this._disabled) {
                return this;
            }
            return new Features(newE, newD);
        }
        
        public Features with(final Feature... features) {
            int e = this._enabled;
            for (final Feature f : features) {
                e |= 1 << f.ordinal();
            }
            return (e == this._enabled) ? this : new Features(e, this._disabled);
        }
        
        public Features without(final Feature... features) {
            int d = this._disabled;
            for (final Feature f : features) {
                d |= 1 << f.ordinal();
            }
            return (d == this._disabled) ? this : new Features(this._enabled, d);
        }
        
        public Boolean get(final Feature f) {
            final int mask = 1 << f.ordinal();
            if ((this._disabled & mask) != 0x0) {
                return Boolean.FALSE;
            }
            if ((this._enabled & mask) != 0x0) {
                return Boolean.TRUE;
            }
            return null;
        }
        
        @Override
        public String toString() {
            if (this == Features.EMPTY) {
                return "EMPTY";
            }
            return String.format("(enabled=0x%x,disabled=0x%x)", this._enabled, this._disabled);
        }
        
        @Override
        public int hashCode() {
            return this._disabled + this._enabled;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            final Features other = (Features)o;
            return other._enabled == this._enabled && other._disabled == this._disabled;
        }
        
        static {
            EMPTY = new Features(0, 0);
        }
    }
    
    public static class Value implements JacksonAnnotationValue<JsonFormat>, Serializable
    {
        private static final long serialVersionUID = 1L;
        private static final Value EMPTY;
        private final String _pattern;
        private final Shape _shape;
        private final Locale _locale;
        private final String _timezoneStr;
        private final Boolean _lenient;
        private final Features _features;
        private transient TimeZone _timezone;
        
        public Value() {
            this("", Shape.ANY, "", "", Features.empty(), null);
        }
        
        public Value(final JsonFormat ann) {
            this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), Features.construct(ann), ann.lenient().asBoolean());
        }
        
        public Value(final String p, final Shape sh, final String localeStr, final String tzStr, final Features f, final Boolean lenient) {
            this(p, sh, (localeStr == null || localeStr.length() == 0 || "##default".equals(localeStr)) ? null : new Locale(localeStr), (tzStr == null || tzStr.length() == 0 || "##default".equals(tzStr)) ? null : tzStr, null, f, lenient);
        }
        
        public Value(final String p, final Shape sh, final Locale l, final TimeZone tz, final Features f, final Boolean lenient) {
            this._pattern = ((p == null) ? "" : p);
            this._shape = ((sh == null) ? Shape.ANY : sh);
            this._locale = l;
            this._timezone = tz;
            this._timezoneStr = null;
            this._features = ((f == null) ? Features.empty() : f);
            this._lenient = lenient;
        }
        
        public Value(final String p, final Shape sh, final Locale l, final String tzStr, final TimeZone tz, final Features f, final Boolean lenient) {
            this._pattern = ((p == null) ? "" : p);
            this._shape = ((sh == null) ? Shape.ANY : sh);
            this._locale = l;
            this._timezone = tz;
            this._timezoneStr = tzStr;
            this._features = ((f == null) ? Features.empty() : f);
            this._lenient = lenient;
        }
        
        @Deprecated
        public Value(final String p, final Shape sh, final Locale l, final String tzStr, final TimeZone tz, final Features f) {
            this(p, sh, l, tzStr, tz, f, null);
        }
        
        @Deprecated
        public Value(final String p, final Shape sh, final String localeStr, final String tzStr, final Features f) {
            this(p, sh, localeStr, tzStr, f, null);
        }
        
        @Deprecated
        public Value(final String p, final Shape sh, final Locale l, final TimeZone tz, final Features f) {
            this(p, sh, l, tz, f, null);
        }
        
        public static final Value empty() {
            return Value.EMPTY;
        }
        
        public static Value merge(final Value base, final Value overrides) {
            return (base == null) ? overrides : base.withOverrides(overrides);
        }
        
        public static Value mergeAll(final Value... values) {
            Value result = null;
            for (final Value curr : values) {
                if (curr != null) {
                    result = ((result == null) ? curr : result.withOverrides(curr));
                }
            }
            return result;
        }
        
        public static final Value from(final JsonFormat ann) {
            return (ann == null) ? Value.EMPTY : new Value(ann);
        }
        
        public final Value withOverrides(final Value overrides) {
            if (overrides == null || overrides == Value.EMPTY || overrides == this) {
                return this;
            }
            if (this == Value.EMPTY) {
                return overrides;
            }
            String p = overrides._pattern;
            if (p == null || p.isEmpty()) {
                p = this._pattern;
            }
            Shape sh = overrides._shape;
            if (sh == Shape.ANY) {
                sh = this._shape;
            }
            Locale l = overrides._locale;
            if (l == null) {
                l = this._locale;
            }
            Features f = this._features;
            if (f == null) {
                f = overrides._features;
            }
            else {
                f = f.withOverrides(overrides._features);
            }
            Boolean lenient = overrides._lenient;
            if (lenient == null) {
                lenient = this._lenient;
            }
            String tzStr = overrides._timezoneStr;
            TimeZone tz;
            if (tzStr == null || tzStr.isEmpty()) {
                tzStr = this._timezoneStr;
                tz = this._timezone;
            }
            else {
                tz = overrides._timezone;
            }
            return new Value(p, sh, l, tzStr, tz, f, lenient);
        }
        
        public static Value forPattern(final String p) {
            return new Value(p, null, null, null, null, Features.empty(), null);
        }
        
        public static Value forShape(final Shape sh) {
            return new Value("", sh, null, null, null, Features.empty(), null);
        }
        
        public static Value forLeniency(final boolean lenient) {
            return new Value("", null, null, null, null, Features.empty(), lenient);
        }
        
        public Value withPattern(final String p) {
            return new Value(p, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
        }
        
        public Value withShape(final Shape s) {
            if (s == this._shape) {
                return this;
            }
            return new Value(this._pattern, s, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
        }
        
        public Value withLocale(final Locale l) {
            return new Value(this._pattern, this._shape, l, this._timezoneStr, this._timezone, this._features, this._lenient);
        }
        
        public Value withTimeZone(final TimeZone tz) {
            return new Value(this._pattern, this._shape, this._locale, null, tz, this._features, this._lenient);
        }
        
        public Value withLenient(final Boolean lenient) {
            if (lenient == this._lenient) {
                return this;
            }
            return new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, lenient);
        }
        
        public Value withFeature(final Feature f) {
            final Features newFeats = this._features.with(f);
            return (newFeats == this._features) ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
        }
        
        public Value withoutFeature(final Feature f) {
            final Features newFeats = this._features.without(f);
            return (newFeats == this._features) ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
        }
        
        @Override
        public Class<JsonFormat> valueFor() {
            return JsonFormat.class;
        }
        
        public String getPattern() {
            return this._pattern;
        }
        
        public Shape getShape() {
            return this._shape;
        }
        
        public Locale getLocale() {
            return this._locale;
        }
        
        public Boolean getLenient() {
            return this._lenient;
        }
        
        public boolean isLenient() {
            return Boolean.TRUE.equals(this._lenient);
        }
        
        public String timeZoneAsString() {
            if (this._timezone != null) {
                return this._timezone.getID();
            }
            return this._timezoneStr;
        }
        
        public TimeZone getTimeZone() {
            TimeZone tz = this._timezone;
            if (tz == null) {
                if (this._timezoneStr == null) {
                    return null;
                }
                tz = TimeZone.getTimeZone(this._timezoneStr);
                this._timezone = tz;
            }
            return tz;
        }
        
        public boolean hasShape() {
            return this._shape != Shape.ANY;
        }
        
        public boolean hasPattern() {
            return this._pattern != null && this._pattern.length() > 0;
        }
        
        public boolean hasLocale() {
            return this._locale != null;
        }
        
        public boolean hasTimeZone() {
            return this._timezone != null || (this._timezoneStr != null && !this._timezoneStr.isEmpty());
        }
        
        public boolean hasLenient() {
            return this._lenient != null;
        }
        
        public Boolean getFeature(final Feature f) {
            return this._features.get(f);
        }
        
        public Features getFeatures() {
            return this._features;
        }
        
        @Override
        public String toString() {
            return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s,features=%s)", this._pattern, this._shape, this._lenient, this._locale, this._timezoneStr, this._features);
        }
        
        @Override
        public int hashCode() {
            int hash = (this._timezoneStr == null) ? 1 : this._timezoneStr.hashCode();
            if (this._pattern != null) {
                hash ^= this._pattern.hashCode();
            }
            hash += this._shape.hashCode();
            if (this._lenient != null) {
                hash ^= this._lenient.hashCode();
            }
            if (this._locale != null) {
                hash += this._locale.hashCode();
            }
            hash ^= this._features.hashCode();
            return hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            final Value other = (Value)o;
            return this._shape == other._shape && this._features.equals(other._features) && _equal(this._lenient, other._lenient) && _equal(this._timezoneStr, other._timezoneStr) && _equal(this._pattern, other._pattern) && _equal(this._timezone, other._timezone) && _equal(this._locale, other._locale);
        }
        
        private static <T> boolean _equal(final T value1, final T value2) {
            if (value1 == null) {
                return value2 == null;
            }
            return value2 != null && value1.equals(value2);
        }
        
        static {
            EMPTY = new Value();
        }
    }
}
