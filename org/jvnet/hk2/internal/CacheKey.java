package org.jvnet.hk2.internal;

import org.glassfish.hk2.utilities.reflection.Pretty;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.api.Unqualified;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class CacheKey
{
    private final String removalName;
    private final Type lookupType;
    private final String name;
    private final Annotation[] qualifiers;
    private final Unqualified unqualified;
    private final int hashCode;
    
    public CacheKey(final Type lookupType, final String name, final Unqualified unqualified, final Annotation... qualifiers) {
        this.lookupType = lookupType;
        final Class<?> rawClass = ReflectionHelper.getRawClass(lookupType);
        if (rawClass != null) {
            this.removalName = rawClass.getName();
        }
        else {
            this.removalName = null;
        }
        this.name = name;
        if (qualifiers.length > 0) {
            this.qualifiers = qualifiers;
        }
        else {
            this.qualifiers = null;
        }
        this.unqualified = unqualified;
        int retVal = 0;
        if (lookupType != null) {
            retVal ^= lookupType.hashCode();
        }
        if (name != null) {
            retVal ^= name.hashCode();
        }
        for (final Annotation anno : qualifiers) {
            retVal ^= anno.hashCode();
        }
        if (unqualified != null) {
            retVal ^= -1;
            for (final Class<?> clazz : unqualified.value()) {
                retVal ^= clazz.hashCode();
            }
        }
        this.hashCode = retVal;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof CacheKey)) {
            return false;
        }
        final CacheKey other = (CacheKey)o;
        if (this.hashCode != other.hashCode) {
            return false;
        }
        if (!GeneralUtilities.safeEquals((Object)this.lookupType, (Object)other.lookupType)) {
            return false;
        }
        if (!GeneralUtilities.safeEquals((Object)this.name, (Object)other.name)) {
            return false;
        }
        if (this.qualifiers != null) {
            if (other.qualifiers == null) {
                return false;
            }
            if (this.qualifiers.length != other.qualifiers.length) {
                return false;
            }
            final boolean isEqual = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    for (int lcv = 0; lcv < CacheKey.this.qualifiers.length; ++lcv) {
                        if (!GeneralUtilities.safeEquals((Object)CacheKey.this.qualifiers[lcv], (Object)other.qualifiers[lcv])) {
                            return false;
                        }
                    }
                    return true;
                }
            });
            if (!isEqual) {
                return false;
            }
        }
        else if (other.qualifiers != null) {
            return false;
        }
        if (this.unqualified != null) {
            if (other.unqualified == null) {
                return false;
            }
            final Class<?>[] myClazzes = this.unqualified.value();
            final Class<?>[] otherClazzes = other.unqualified.value();
            if (myClazzes.length != otherClazzes.length) {
                return false;
            }
            for (int lcv = 0; lcv < myClazzes.length; ++lcv) {
                if (!GeneralUtilities.safeEquals((Object)myClazzes[lcv], (Object)otherClazzes[lcv])) {
                    return false;
                }
            }
        }
        else if (other.unqualified != null) {
            return false;
        }
        return true;
    }
    
    public boolean matchesRemovalName(final String name) {
        return this.removalName != null && name != null && this.removalName.equals(name);
    }
    
    @Override
    public String toString() {
        return "CacheKey(" + Pretty.type(this.lookupType) + "," + this.name + "," + ((this.qualifiers == null) ? 0 : this.qualifiers.length) + "," + System.identityHashCode(this) + "," + this.hashCode + ")";
    }
}
