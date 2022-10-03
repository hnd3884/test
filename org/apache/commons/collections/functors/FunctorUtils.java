package org.apache.commons.collections.functors;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.Closure;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections.Predicate;

class FunctorUtils
{
    static final String UNSAFE_SERIALIZABLE_PROPERTY = "org.apache.commons.collections.enableUnsafeSerialization";
    
    private FunctorUtils() {
    }
    
    static Predicate[] copy(final Predicate[] predicates) {
        if (predicates == null) {
            return null;
        }
        return predicates.clone();
    }
    
    static void validate(final Predicate[] predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate array must not be null");
        }
        for (int i = 0; i < predicates.length; ++i) {
            if (predicates[i] == null) {
                throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
            }
        }
    }
    
    static Predicate[] validate(final Collection predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate collection must not be null");
        }
        final Predicate[] preds = new Predicate[predicates.size()];
        int i = 0;
        final Iterator it = predicates.iterator();
        while (it.hasNext()) {
            preds[i] = it.next();
            if (preds[i] == null) {
                throw new IllegalArgumentException("The predicate collection must not contain a null predicate, index " + i + " was null");
            }
            ++i;
        }
        return preds;
    }
    
    static Closure[] copy(final Closure[] closures) {
        if (closures == null) {
            return null;
        }
        return closures.clone();
    }
    
    static void validate(final Closure[] closures) {
        if (closures == null) {
            throw new IllegalArgumentException("The closure array must not be null");
        }
        for (int i = 0; i < closures.length; ++i) {
            if (closures[i] == null) {
                throw new IllegalArgumentException("The closure array must not contain a null closure, index " + i + " was null");
            }
        }
    }
    
    static Transformer[] copy(final Transformer[] transformers) {
        if (transformers == null) {
            return null;
        }
        return transformers.clone();
    }
    
    static void validate(final Transformer[] transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("The transformer array must not be null");
        }
        for (int i = 0; i < transformers.length; ++i) {
            if (transformers[i] == null) {
                throw new IllegalArgumentException("The transformer array must not contain a null transformer, index " + i + " was null");
            }
        }
    }
    
    static void checkUnsafeSerialization(final Class clazz) {
        String unsafeSerializableProperty;
        try {
            unsafeSerializableProperty = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty("org.apache.commons.collections.enableUnsafeSerialization");
                }
            });
        }
        catch (final SecurityException ex) {
            unsafeSerializableProperty = null;
        }
        if (!"true".equalsIgnoreCase(unsafeSerializableProperty)) {
            throw new UnsupportedOperationException("Serialization support for " + clazz.getName() + " is disabled for security reasons. " + "To enable it set system property '" + "org.apache.commons.collections.enableUnsafeSerialization" + "' to 'true', " + "but you must ensure that your application does not de-serialize objects from untrusted sources.");
        }
    }
}
