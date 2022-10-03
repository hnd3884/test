package org.apache.xmlbeans;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

public class QNameSetBuilder implements QNameSetSpecification, Serializable
{
    private static final long serialVersionUID = 1L;
    private boolean _inverted;
    private Set _includedURIs;
    private Set _excludedQNames;
    private Set _includedQNames;
    private static final String[] EMPTY_STRINGARRAY;
    
    public QNameSetBuilder() {
        this._inverted = false;
        this._includedURIs = new HashSet();
        this._excludedQNames = new HashSet();
        this._includedQNames = new HashSet();
    }
    
    public QNameSetBuilder(final QNameSetSpecification set) {
        final Set includedURIs = set.includedURIs();
        if (includedURIs != null) {
            this._inverted = false;
            this._includedURIs = new HashSet(includedURIs);
            this._excludedQNames = new HashSet(set.excludedQNamesInIncludedURIs());
            this._includedQNames = new HashSet(set.includedQNamesInExcludedURIs());
        }
        else {
            this._inverted = true;
            this._includedURIs = new HashSet(set.excludedURIs());
            this._excludedQNames = new HashSet(set.includedQNamesInExcludedURIs());
            this._includedQNames = new HashSet(set.excludedQNamesInIncludedURIs());
        }
    }
    
    public QNameSetBuilder(final Set excludedURIs, final Set includedURIs, final Set excludedQNamesInIncludedURIs, final Set includedQNamesInExcludedURIs) {
        if (includedURIs != null && excludedURIs == null) {
            this._inverted = false;
            this._includedURIs = new HashSet(includedURIs);
            this._excludedQNames = new HashSet(excludedQNamesInIncludedURIs);
            this._includedQNames = new HashSet(includedQNamesInExcludedURIs);
        }
        else {
            if (excludedURIs == null || includedURIs != null) {
                throw new IllegalArgumentException("Exactly one of excludedURIs and includedURIs must be null");
            }
            this._inverted = true;
            this._includedURIs = new HashSet(excludedURIs);
            this._excludedQNames = new HashSet(includedQNamesInExcludedURIs);
            this._includedQNames = new HashSet(excludedQNamesInIncludedURIs);
        }
    }
    
    public QNameSetBuilder(String str, final String targetURI) {
        this();
        if (str == null) {
            str = "##any";
        }
        final String[] uri = splitList(str);
        for (int i = 0; i < uri.length; ++i) {
            String adduri = uri[i];
            if (adduri.startsWith("##")) {
                if (adduri.equals("##other")) {
                    if (targetURI == null) {
                        throw new IllegalArgumentException();
                    }
                    final QNameSetBuilder temp = new QNameSetBuilder();
                    temp.addNamespace(targetURI);
                    temp.addNamespace("");
                    temp.invert();
                    this.addAll(temp);
                    continue;
                }
                else {
                    if (adduri.equals("##any")) {
                        this.clear();
                        this.invert();
                        continue;
                    }
                    if (uri[i].equals("##targetNamespace")) {
                        if (targetURI == null) {
                            throw new IllegalArgumentException();
                        }
                        adduri = targetURI;
                    }
                    else if (uri[i].equals("##local")) {
                        adduri = "";
                    }
                }
            }
            this.addNamespace(adduri);
        }
    }
    
    private static String nsFromName(final QName QName) {
        final String ns = QName.getNamespaceURI();
        return (ns == null) ? "" : ns;
    }
    
    private static boolean isSpace(final char ch) {
        switch (ch) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static String[] splitList(final String s) {
        if (s.length() == 0) {
            return QNameSetBuilder.EMPTY_STRINGARRAY;
        }
        final List result = new ArrayList();
        int i = 0;
        int start = 0;
        while (true) {
            if (i < s.length() && isSpace(s.charAt(i))) {
                ++i;
            }
            else {
                if (i >= s.length()) {
                    break;
                }
                start = i;
                while (i < s.length() && !isSpace(s.charAt(i))) {
                    ++i;
                }
                result.add(s.substring(start, i));
            }
        }
        return result.toArray(QNameSetBuilder.EMPTY_STRINGARRAY);
    }
    
    private static void removeAllMatchingNs(final String uri, final Set qnameset) {
        final Iterator i = qnameset.iterator();
        while (i.hasNext()) {
            if (uri.equals(nsFromName(i.next()))) {
                i.remove();
            }
        }
    }
    
    private static void removeAllMatchingFirstOnly(final Set setFirst, final Set setSecond, final Set qnameset) {
        final Iterator i = qnameset.iterator();
        while (i.hasNext()) {
            final String ns = nsFromName(i.next());
            if (setFirst.contains(ns) && !setSecond.contains(ns)) {
                i.remove();
            }
        }
    }
    
    private static void removeAllMatchingBoth(final Set setFirst, final Set setSecond, final Set qnameset) {
        final Iterator i = qnameset.iterator();
        while (i.hasNext()) {
            final String ns = nsFromName(i.next());
            if (setFirst.contains(ns) && setSecond.contains(ns)) {
                i.remove();
            }
        }
    }
    
    private static void removeAllMatchingNeither(final Set setFirst, final Set setSecond, final Set qnameset) {
        final Iterator i = qnameset.iterator();
        while (i.hasNext()) {
            final String ns = nsFromName(i.next());
            if (!setFirst.contains(ns) && !setSecond.contains(ns)) {
                i.remove();
            }
        }
    }
    
    @Override
    public boolean contains(final QName name) {
        final boolean in = this._includedURIs.contains(nsFromName(name)) ? (!this._excludedQNames.contains(name)) : this._includedQNames.contains(name);
        return this._inverted ^ in;
    }
    
    @Override
    public boolean isAll() {
        return this._inverted && this._includedURIs.size() == 0 && this._includedQNames.size() == 0;
    }
    
    @Override
    public boolean isEmpty() {
        return !this._inverted && this._includedURIs.size() == 0 && this._includedQNames.size() == 0;
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
        return QNameSet.forSets(this.includedURIs(), this.excludedURIs(), this.includedQNamesInExcludedURIs(), this.excludedQNamesInIncludedURIs());
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
    
    public void clear() {
        this._inverted = false;
        this._includedURIs.clear();
        this._excludedQNames.clear();
        this._includedQNames.clear();
    }
    
    public void invert() {
        this._inverted = !this._inverted;
    }
    
    public void add(final QName qname) {
        if (!this._inverted) {
            this.addImpl(qname);
        }
        else {
            this.removeImpl(qname);
        }
    }
    
    public void addNamespace(final String uri) {
        if (!this._inverted) {
            this.addNamespaceImpl(uri);
        }
        else {
            this.removeNamespaceImpl(uri);
        }
    }
    
    public void addAll(final QNameSetSpecification set) {
        if (this._inverted) {
            this.removeAllImpl(set.includedURIs(), set.excludedURIs(), set.includedQNamesInExcludedURIs(), set.excludedQNamesInIncludedURIs());
        }
        else {
            this.addAllImpl(set.includedURIs(), set.excludedURIs(), set.includedQNamesInExcludedURIs(), set.excludedQNamesInIncludedURIs());
        }
    }
    
    public void remove(final QName qname) {
        if (this._inverted) {
            this.addImpl(qname);
        }
        else {
            this.removeImpl(qname);
        }
    }
    
    public void removeNamespace(final String uri) {
        if (this._inverted) {
            this.addNamespaceImpl(uri);
        }
        else {
            this.removeNamespaceImpl(uri);
        }
    }
    
    public void removeAll(final QNameSetSpecification set) {
        if (this._inverted) {
            this.addAllImpl(set.includedURIs(), set.excludedURIs(), set.includedQNamesInExcludedURIs(), set.excludedQNamesInIncludedURIs());
        }
        else {
            this.removeAllImpl(set.includedURIs(), set.excludedURIs(), set.includedQNamesInExcludedURIs(), set.excludedQNamesInIncludedURIs());
        }
    }
    
    public void restrict(final QNameSetSpecification set) {
        if (this._inverted) {
            this.addAllImpl(set.excludedURIs(), set.includedURIs(), set.excludedQNamesInIncludedURIs(), set.includedQNamesInExcludedURIs());
        }
        else {
            this.removeAllImpl(set.excludedURIs(), set.includedURIs(), set.excludedQNamesInIncludedURIs(), set.includedQNamesInExcludedURIs());
        }
    }
    
    private void addImpl(final QName qname) {
        if (this._includedURIs.contains(nsFromName(qname))) {
            this._excludedQNames.remove(qname);
        }
        else {
            this._includedQNames.add(qname);
        }
    }
    
    private void addNamespaceImpl(final String uri) {
        if (this._includedURIs.contains(uri)) {
            removeAllMatchingNs(uri, this._excludedQNames);
        }
        else {
            removeAllMatchingNs(uri, this._includedQNames);
            this._includedURIs.add(uri);
        }
    }
    
    private void addAllImpl(final Set includedURIs, final Set excludedURIs, final Set includedQNames, final Set excludedQNames) {
        final boolean exclude = excludedURIs != null;
        final Set specialURIs = exclude ? excludedURIs : includedURIs;
        Iterator i = this._excludedQNames.iterator();
        while (i.hasNext()) {
            final QName name = i.next();
            final String uri = nsFromName(name);
            if ((exclude ^ specialURIs.contains(uri)) && !excludedQNames.contains(name)) {
                i.remove();
            }
        }
        i = excludedQNames.iterator();
        while (i.hasNext()) {
            final QName name = i.next();
            final String uri = nsFromName(name);
            if (!this._includedURIs.contains(uri) && !this._includedQNames.contains(name)) {
                this._excludedQNames.add(name);
            }
        }
        i = includedQNames.iterator();
        while (i.hasNext()) {
            final QName name = i.next();
            final String uri = nsFromName(name);
            if (!this._includedURIs.contains(uri)) {
                this._includedQNames.add(name);
            }
            else {
                this._excludedQNames.remove(name);
            }
        }
        if (!exclude) {
            removeAllMatchingFirstOnly(includedURIs, this._includedURIs, this._includedQNames);
            this._includedURIs.addAll(includedURIs);
        }
        else {
            removeAllMatchingNeither(excludedURIs, this._includedURIs, this._includedQNames);
            i = this._includedURIs.iterator();
            while (i.hasNext()) {
                final String uri2 = i.next();
                if (!excludedURIs.contains(uri2)) {
                    i.remove();
                }
            }
            i = excludedURIs.iterator();
            while (i.hasNext()) {
                final String uri2 = i.next();
                if (!this._includedURIs.contains(uri2)) {
                    this._includedURIs.add(uri2);
                }
                else {
                    this._includedURIs.remove(uri2);
                }
            }
            final Set temp = this._excludedQNames;
            this._excludedQNames = this._includedQNames;
            this._includedQNames = temp;
            this._inverted = !this._inverted;
        }
    }
    
    private void removeImpl(final QName qname) {
        if (this._includedURIs.contains(nsFromName(qname))) {
            this._excludedQNames.add(qname);
        }
        else {
            this._includedQNames.remove(qname);
        }
    }
    
    private void removeNamespaceImpl(final String uri) {
        if (this._includedURIs.contains(uri)) {
            removeAllMatchingNs(uri, this._excludedQNames);
            this._includedURIs.remove(uri);
        }
        else {
            removeAllMatchingNs(uri, this._includedQNames);
        }
    }
    
    private void removeAllImpl(final Set includedURIs, final Set excludedURIs, final Set includedQNames, final Set excludedQNames) {
        final boolean exclude = excludedURIs != null;
        final Set specialURIs = exclude ? excludedURIs : includedURIs;
        Iterator i = this._includedQNames.iterator();
        while (i.hasNext()) {
            final QName name = i.next();
            final String uri = nsFromName(name);
            if (exclude ^ specialURIs.contains(uri)) {
                if (excludedQNames.contains(name)) {
                    continue;
                }
                i.remove();
            }
            else {
                if (!includedQNames.contains(name)) {
                    continue;
                }
                i.remove();
            }
        }
        i = includedQNames.iterator();
        while (i.hasNext()) {
            final QName name = i.next();
            final String uri = nsFromName(name);
            if (this._includedURIs.contains(uri)) {
                this._excludedQNames.add(name);
            }
        }
        i = excludedQNames.iterator();
        while (i.hasNext()) {
            final QName name = i.next();
            final String uri = nsFromName(name);
            if (this._includedURIs.contains(uri) && !this._excludedQNames.contains(name)) {
                this._includedQNames.add(name);
            }
        }
        if (exclude) {
            removeAllMatchingFirstOnly(this._includedURIs, excludedURIs, this._excludedQNames);
        }
        else {
            removeAllMatchingBoth(this._includedURIs, includedURIs, this._excludedQNames);
        }
        i = this._includedURIs.iterator();
        while (i.hasNext()) {
            if (exclude ^ specialURIs.contains(i.next())) {
                i.remove();
            }
        }
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
        sb.append("QNameSetBuilder");
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
    
    public QNameSet toQNameSet() {
        return QNameSet.forSpecification(this);
    }
    
    static {
        EMPTY_STRINGARRAY = new String[0];
    }
}
