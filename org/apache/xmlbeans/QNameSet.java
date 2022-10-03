package org.apache.xmlbeans;

import java.util.Iterator;
import java.util.Arrays;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import java.io.Serializable;

public final class QNameSet implements QNameSetSpecification, Serializable
{
    private static final long serialVersionUID = 1L;
    private final boolean _inverted;
    private final Set _includedURIs;
    private final Set _excludedQNames;
    private final Set _includedQNames;
    public static final QNameSet EMPTY;
    public static final QNameSet ALL;
    public static final QNameSet LOCAL;
    public static final QNameSet NONLOCAL;
    
    private static Set minSetCopy(final Set original) {
        if (original == null) {
            return null;
        }
        if (original.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        if (original.size() == 1) {
            return Collections.singleton((Object)original.iterator().next());
        }
        return new HashSet(original);
    }
    
    public static QNameSet forSets(final Set excludedURIs, final Set includedURIs, final Set excludedQNamesInIncludedURIs, final Set includedQNamesInExcludedURIs) {
        if (excludedURIs != null == (includedURIs != null)) {
            throw new IllegalArgumentException("Exactly one of excludedURIs and includedURIs must be null");
        }
        if (excludedURIs == null && includedURIs.isEmpty() && includedQNamesInExcludedURIs.isEmpty()) {
            return QNameSet.EMPTY;
        }
        if (includedURIs == null && excludedURIs.isEmpty() && excludedQNamesInIncludedURIs.isEmpty()) {
            return QNameSet.ALL;
        }
        if (excludedURIs == null && includedURIs.size() == 1 && includedURIs.contains("") && includedQNamesInExcludedURIs.isEmpty() && excludedQNamesInIncludedURIs.isEmpty()) {
            return QNameSet.LOCAL;
        }
        if (includedURIs == null && excludedURIs.size() == 1 && excludedURIs.contains("") && excludedQNamesInIncludedURIs.isEmpty() && includedQNamesInExcludedURIs.isEmpty()) {
            return QNameSet.NONLOCAL;
        }
        return new QNameSet(minSetCopy(excludedURIs), minSetCopy(includedURIs), minSetCopy(excludedQNamesInIncludedURIs), minSetCopy(includedQNamesInExcludedURIs));
    }
    
    public static QNameSet forArray(final QName[] includedQNames) {
        if (includedQNames == null) {
            throw new IllegalArgumentException("includedQNames cannot be null");
        }
        return new QNameSet(null, Collections.EMPTY_SET, Collections.EMPTY_SET, new HashSet(Arrays.asList(includedQNames)));
    }
    
    public static QNameSet forSpecification(final QNameSetSpecification spec) {
        if (spec instanceof QNameSet) {
            return (QNameSet)spec;
        }
        return forSets(spec.excludedURIs(), spec.includedURIs(), spec.excludedQNamesInIncludedURIs(), spec.includedQNamesInExcludedURIs());
    }
    
    public static QNameSet forWildcardNamespaceString(final String wildcard, final String targetURI) {
        return forSpecification(new QNameSetBuilder(wildcard, targetURI));
    }
    
    public static QNameSet singleton(final QName name) {
        return new QNameSet(null, Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.singleton(name));
    }
    
    private QNameSet(final Set excludedURIs, final Set includedURIs, final Set excludedQNamesInIncludedURIs, final Set includedQNamesInExcludedURIs) {
        if (includedURIs != null && excludedURIs == null) {
            this._inverted = false;
            this._includedURIs = includedURIs;
            this._excludedQNames = excludedQNamesInIncludedURIs;
            this._includedQNames = includedQNamesInExcludedURIs;
        }
        else {
            if (excludedURIs == null || includedURIs != null) {
                throw new IllegalArgumentException("Exactly one of excludedURIs and includedURIs must be null");
            }
            this._inverted = true;
            this._includedURIs = excludedURIs;
            this._excludedQNames = includedQNamesInExcludedURIs;
            this._includedQNames = excludedQNamesInIncludedURIs;
        }
    }
    
    private static String nsFromName(final QName xmlName) {
        final String ns = xmlName.getNamespaceURI();
        return (ns == null) ? "" : ns;
    }
    
    @Override
    public boolean contains(final QName name) {
        final boolean in = this._includedURIs.contains(nsFromName(name)) ? (!this._excludedQNames.contains(name)) : this._includedQNames.contains(name);
        return this._inverted ^ in;
    }
    
    @Override
    public boolean isAll() {
        return this._inverted && this._includedURIs.isEmpty() && this._includedQNames.isEmpty();
    }
    
    @Override
    public boolean isEmpty() {
        return !this._inverted && this._includedURIs.isEmpty() && this._includedQNames.isEmpty();
    }
    
    @Override
    public QNameSet intersect(final QNameSetSpecification set) {
        final QNameSetBuilder result = new QNameSetBuilder(this);
        result.restrict(set);
        return result.toQNameSet();
    }
    
    @Override
    public QNameSet union(final QNameSetSpecification set) {
        final QNameSetBuilder result = new QNameSetBuilder(this);
        result.addAll(set);
        return result.toQNameSet();
    }
    
    @Override
    public QNameSet inverse() {
        if (this == QNameSet.EMPTY) {
            return QNameSet.ALL;
        }
        if (this == QNameSet.ALL) {
            return QNameSet.EMPTY;
        }
        if (this == QNameSet.LOCAL) {
            return QNameSet.NONLOCAL;
        }
        if (this == QNameSet.NONLOCAL) {
            return QNameSet.LOCAL;
        }
        return new QNameSet(this.includedURIs(), this.excludedURIs(), this.includedQNamesInExcludedURIs(), this.excludedQNamesInIncludedURIs());
    }
    
    @Override
    public boolean containsAll(final QNameSetSpecification set) {
        return (this._inverted || set.excludedURIs() == null) && this.inverse().isDisjoint(set);
    }
    
    @Override
    public boolean isDisjoint(final QNameSetSpecification set) {
        if (this._inverted && set.excludedURIs() != null) {
            return false;
        }
        if (this._inverted) {
            return this.isDisjointImpl(set, this);
        }
        return this.isDisjointImpl(this, set);
    }
    
    private boolean isDisjointImpl(final QNameSetSpecification set1, final QNameSetSpecification set2) {
        final Set includeURIs = set1.includedURIs();
        final Set otherIncludeURIs = set2.includedURIs();
        if (otherIncludeURIs != null) {
            final Iterator i = includeURIs.iterator();
            while (i.hasNext()) {
                if (otherIncludeURIs.contains(i.next())) {
                    return false;
                }
            }
        }
        else {
            final Set otherExcludeURIs = set2.excludedURIs();
            final Iterator j = includeURIs.iterator();
            while (j.hasNext()) {
                if (!otherExcludeURIs.contains(j.next())) {
                    return false;
                }
            }
        }
        Iterator i = set1.includedQNamesInExcludedURIs().iterator();
        while (i.hasNext()) {
            if (set2.contains(i.next())) {
                return false;
            }
        }
        if (includeURIs.size() > 0) {
            i = set2.includedQNamesInExcludedURIs().iterator();
            while (i.hasNext()) {
                if (set1.contains(i.next())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public Set excludedURIs() {
        if (this._inverted) {
            return Collections.unmodifiableSet((Set<?>)this._includedURIs);
        }
        return null;
    }
    
    @Override
    public Set includedURIs() {
        if (!this._inverted) {
            return this._includedURIs;
        }
        return null;
    }
    
    @Override
    public Set excludedQNamesInIncludedURIs() {
        return Collections.unmodifiableSet((Set<?>)(this._inverted ? this._includedQNames : this._excludedQNames));
    }
    
    @Override
    public Set includedQNamesInExcludedURIs() {
        return Collections.unmodifiableSet((Set<?>)(this._inverted ? this._excludedQNames : this._includedQNames));
    }
    
    private String prettyQName(final QName name) {
        if (name.getNamespaceURI() == null) {
            return name.getLocalPart();
        }
        return name.getLocalPart() + "@" + name.getNamespaceURI();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("QNameSet");
        sb.append(this._inverted ? "-(" : "+(");
        Iterator i = this._includedURIs.iterator();
        while (i.hasNext()) {
            sb.append("+*@");
            sb.append(i.next());
            sb.append(", ");
        }
        i = this._excludedQNames.iterator();
        while (i.hasNext()) {
            sb.append("-");
            sb.append(this.prettyQName(i.next()));
            sb.append(", ");
        }
        i = this._includedQNames.iterator();
        while (i.hasNext()) {
            sb.append("+");
            sb.append(this.prettyQName(i.next()));
            sb.append(", ");
        }
        final int index = sb.lastIndexOf(", ");
        if (index > 0) {
            sb.setLength(index);
        }
        sb.append(')');
        return sb.toString();
    }
    
    static {
        EMPTY = new QNameSet(null, Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.EMPTY_SET);
        ALL = new QNameSet(Collections.EMPTY_SET, null, Collections.EMPTY_SET, Collections.EMPTY_SET);
        LOCAL = new QNameSet(null, Collections.singleton(""), Collections.EMPTY_SET, Collections.EMPTY_SET);
        NONLOCAL = new QNameSet(Collections.singleton(""), null, Collections.EMPTY_SET, Collections.EMPTY_SET);
    }
}
