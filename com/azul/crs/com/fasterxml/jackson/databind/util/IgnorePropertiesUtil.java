package com.azul.crs.com.fasterxml.jackson.databind.util;

import java.util.Collections;
import java.io.Serializable;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

public class IgnorePropertiesUtil
{
    public static boolean shouldIgnore(final Object value, final Collection<String> toIgnore, final Collection<String> toInclude) {
        if (toIgnore == null && toInclude == null) {
            return false;
        }
        if (toInclude == null) {
            return toIgnore.contains(value);
        }
        if (toIgnore == null) {
            return !toInclude.contains(value);
        }
        return !toInclude.contains(value) || toIgnore.contains(value);
    }
    
    public static Checker buildCheckerIfNeeded(final Set<String> toIgnore, final Set<String> toInclude) {
        if (toInclude == null && (toIgnore == null || toIgnore.isEmpty())) {
            return null;
        }
        return Checker.construct(toIgnore, toInclude);
    }
    
    public static Set<String> combineNamesToInclude(final Set<String> prevToInclude, final Set<String> newToInclude) {
        if (prevToInclude == null) {
            return newToInclude;
        }
        if (newToInclude == null) {
            return prevToInclude;
        }
        final Set<String> result = new HashSet<String>();
        for (final String prop : newToInclude) {
            if (prevToInclude.contains(prop)) {
                result.add(prop);
            }
        }
        return result;
    }
    
    public static final class Checker implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Set<String> _toIgnore;
        private final Set<String> _toInclude;
        
        private Checker(Set<String> toIgnore, final Set<String> toInclude) {
            if (toIgnore == null) {
                toIgnore = Collections.emptySet();
            }
            this._toIgnore = toIgnore;
            this._toInclude = toInclude;
        }
        
        public static Checker construct(final Set<String> toIgnore, final Set<String> toInclude) {
            return new Checker(toIgnore, toInclude);
        }
        
        public boolean shouldIgnore(final Object propertyName) {
            return (this._toInclude != null && !this._toInclude.contains(propertyName)) || this._toIgnore.contains(propertyName);
        }
    }
}
