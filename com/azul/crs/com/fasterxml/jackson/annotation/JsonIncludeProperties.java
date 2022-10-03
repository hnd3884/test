package com.azul.crs.com.fasterxml.jackson.annotation;

import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIncludeProperties {
    String[] value() default {};
    
    public static class Value implements JacksonAnnotationValue<JsonIncludeProperties>, Serializable
    {
        private static final long serialVersionUID = 1L;
        protected static final Value ALL;
        protected final Set<String> _included;
        
        protected Value(final Set<String> included) {
            this._included = included;
        }
        
        public static Value from(final JsonIncludeProperties src) {
            if (src == null) {
                return Value.ALL;
            }
            return new Value(_asSet(src.value()));
        }
        
        public static Value all() {
            return Value.ALL;
        }
        
        @Override
        public Class<JsonIncludeProperties> valueFor() {
            return JsonIncludeProperties.class;
        }
        
        public Set<String> getIncluded() {
            return this._included;
        }
        
        public Value withOverrides(final Value overrides) {
            final Set<String> otherIncluded;
            if (overrides == null || (otherIncluded = overrides.getIncluded()) == null) {
                return this;
            }
            if (this._included == null) {
                return overrides;
            }
            final HashSet<String> toInclude = new HashSet<String>();
            for (final String incl : otherIncluded) {
                if (this._included.contains(incl)) {
                    toInclude.add(incl);
                }
            }
            return new Value(toInclude);
        }
        
        @Override
        public String toString() {
            return String.format("JsonIncludeProperties.Value(included=%s)", this._included);
        }
        
        @Override
        public int hashCode() {
            return (this._included == null) ? 0 : this._included.size();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o != null && o.getClass() == this.getClass() && _equals(this._included, ((Value)o)._included));
        }
        
        private static boolean _equals(final Set<String> a, final Set<String> b) {
            return (a == null) ? (b == null) : a.equals(b);
        }
        
        private static Set<String> _asSet(final String[] v) {
            if (v == null || v.length == 0) {
                return Collections.emptySet();
            }
            final Set<String> s = new HashSet<String>(v.length);
            for (final String str : v) {
                s.add(str);
            }
            return s;
        }
        
        static {
            ALL = new Value(null);
        }
    }
}
