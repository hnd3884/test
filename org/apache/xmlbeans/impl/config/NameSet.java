package org.apache.xmlbeans.impl.config;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NameSet
{
    public static NameSet EMPTY;
    public static NameSet EVERYTHING;
    private boolean _isFinite;
    private Set _finiteSet;
    
    private NameSet(final boolean isFinite, final Set finiteSet) {
        this._isFinite = isFinite;
        this._finiteSet = finiteSet;
    }
    
    static NameSet newInstance(final boolean isFinite, final Set finiteSet) {
        if (finiteSet.size() != 0) {
            final Set fs = new HashSet();
            fs.addAll(finiteSet);
            return new NameSet(isFinite, fs);
        }
        if (isFinite) {
            return NameSet.EMPTY;
        }
        return NameSet.EVERYTHING;
    }
    
    private static Set intersectFiniteSets(final Set a, final Set b) {
        final Set intersection = new HashSet();
        while (a.iterator().hasNext()) {
            final String name = a.iterator().next();
            if (b.contains(name)) {
                intersection.add(name);
            }
        }
        return intersection;
    }
    
    public NameSet union(final NameSet with) {
        if (this._isFinite) {
            if (with._isFinite) {
                final Set union = new HashSet();
                union.addAll(this._finiteSet);
                union.addAll(with._finiteSet);
                return newInstance(true, union);
            }
            final Set subst = new HashSet();
            subst.addAll(with._finiteSet);
            subst.removeAll(this._finiteSet);
            return newInstance(false, subst);
        }
        else {
            if (with._isFinite) {
                final Set subst = new HashSet();
                subst.addAll(this._finiteSet);
                subst.removeAll(with._finiteSet);
                return newInstance(false, subst);
            }
            return newInstance(false, intersectFiniteSets(this._finiteSet, with._finiteSet));
        }
    }
    
    public NameSet intersect(final NameSet with) {
        if (this._isFinite) {
            if (with._isFinite) {
                return newInstance(true, intersectFiniteSets(this._finiteSet, with._finiteSet));
            }
            final Set subst = new HashSet();
            subst.addAll(this._finiteSet);
            subst.removeAll(with._finiteSet);
            return newInstance(false, subst);
        }
        else {
            if (with._isFinite) {
                final Set subst = new HashSet();
                subst.addAll(with._finiteSet);
                subst.removeAll(this._finiteSet);
                return newInstance(true, subst);
            }
            final Set union = new HashSet();
            union.addAll(this._finiteSet);
            union.addAll(with._finiteSet);
            return newInstance(false, union);
        }
    }
    
    public NameSet substractFrom(final NameSet from) {
        return from.substract(this);
    }
    
    public NameSet substract(final NameSet what) {
        if (this._isFinite) {
            if (what._isFinite) {
                final Set subst = new HashSet();
                subst.addAll(this._finiteSet);
                subst.removeAll(what._finiteSet);
                return newInstance(true, subst);
            }
            return newInstance(true, intersectFiniteSets(this._finiteSet, what._finiteSet));
        }
        else {
            if (what._isFinite) {
                final Set union = new HashSet();
                union.addAll(this._finiteSet);
                union.addAll(what._finiteSet);
                return newInstance(false, union);
            }
            final Set subst = new HashSet();
            subst.addAll(what._finiteSet);
            subst.removeAll(this._finiteSet);
            return newInstance(true, subst);
        }
    }
    
    public NameSet invert() {
        return newInstance(!this._isFinite, this._finiteSet);
    }
    
    public boolean contains(final String name) {
        if (this._isFinite) {
            return this._finiteSet.contains(name);
        }
        return !this._finiteSet.contains(name);
    }
    
    static {
        NameSet.EMPTY = new NameSet(true, Collections.EMPTY_SET);
        NameSet.EVERYTHING = new NameSet(false, Collections.EMPTY_SET);
    }
}
