package com.sun.xml.internal.ws.policy;

import java.util.Iterator;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.Collections;
import java.util.TreeSet;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import javax.xml.namespace.QName;
import java.util.Set;
import java.util.List;
import java.util.Comparator;

public final class AssertionSet implements Iterable<PolicyAssertion>, Comparable<AssertionSet>
{
    private static final AssertionSet EMPTY_ASSERTION_SET;
    private static final Comparator<PolicyAssertion> ASSERTION_COMPARATOR;
    private final List<PolicyAssertion> assertions;
    private final Set<QName> vocabulary;
    private final Collection<QName> immutableVocabulary;
    
    private AssertionSet(final List<PolicyAssertion> list) {
        this.vocabulary = new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR);
        this.immutableVocabulary = Collections.unmodifiableCollection((Collection<? extends QName>)this.vocabulary);
        assert list != null : LocalizationMessages.WSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL();
        this.assertions = list;
    }
    
    private AssertionSet(final Collection<AssertionSet> alternatives) {
        this.vocabulary = new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR);
        this.immutableVocabulary = Collections.unmodifiableCollection((Collection<? extends QName>)this.vocabulary);
        this.assertions = new LinkedList<PolicyAssertion>();
        for (final AssertionSet alternative : alternatives) {
            this.addAll(alternative.assertions);
        }
    }
    
    private boolean add(final PolicyAssertion assertion) {
        if (assertion == null) {
            return false;
        }
        if (this.assertions.contains(assertion)) {
            return false;
        }
        this.assertions.add(assertion);
        this.vocabulary.add(assertion.getName());
        return true;
    }
    
    private boolean addAll(final Collection<? extends PolicyAssertion> assertions) {
        boolean result = true;
        if (assertions != null) {
            for (final PolicyAssertion assertion : assertions) {
                result &= this.add(assertion);
            }
        }
        return result;
    }
    
    Collection<PolicyAssertion> getAssertions() {
        return this.assertions;
    }
    
    Collection<QName> getVocabulary() {
        return this.immutableVocabulary;
    }
    
    boolean isCompatibleWith(final AssertionSet alternative, final PolicyIntersector.CompatibilityMode mode) {
        boolean result = mode == PolicyIntersector.CompatibilityMode.LAX || this.vocabulary.equals(alternative.vocabulary);
        result = (result && this.areAssertionsCompatible(alternative, mode));
        result = (result && alternative.areAssertionsCompatible(this, mode));
        return result;
    }
    
    private boolean areAssertionsCompatible(final AssertionSet alternative, final PolicyIntersector.CompatibilityMode mode) {
    Label_0010:
        for (final PolicyAssertion thisAssertion : this.assertions) {
            if (mode == PolicyIntersector.CompatibilityMode.STRICT || !thisAssertion.isIgnorable()) {
                for (final PolicyAssertion thatAssertion : alternative.assertions) {
                    if (thisAssertion.isCompatibleWith(thatAssertion, mode)) {
                        continue Label_0010;
                    }
                }
                return false;
            }
        }
        return true;
    }
    
    public static AssertionSet createMergedAssertionSet(final Collection<AssertionSet> alternatives) {
        if (alternatives == null || alternatives.isEmpty()) {
            return AssertionSet.EMPTY_ASSERTION_SET;
        }
        final AssertionSet result = new AssertionSet(alternatives);
        Collections.sort(result.assertions, AssertionSet.ASSERTION_COMPARATOR);
        return result;
    }
    
    public static AssertionSet createAssertionSet(final Collection<? extends PolicyAssertion> assertions) {
        if (assertions == null || assertions.isEmpty()) {
            return AssertionSet.EMPTY_ASSERTION_SET;
        }
        final AssertionSet result = new AssertionSet(new LinkedList<PolicyAssertion>());
        result.addAll(assertions);
        Collections.sort(result.assertions, AssertionSet.ASSERTION_COMPARATOR);
        return result;
    }
    
    public static AssertionSet emptyAssertionSet() {
        return AssertionSet.EMPTY_ASSERTION_SET;
    }
    
    @Override
    public Iterator<PolicyAssertion> iterator() {
        return this.assertions.iterator();
    }
    
    public Collection<PolicyAssertion> get(final QName name) {
        final List<PolicyAssertion> matched = new LinkedList<PolicyAssertion>();
        if (this.vocabulary.contains(name)) {
            for (final PolicyAssertion assertion : this.assertions) {
                if (assertion.getName().equals(name)) {
                    matched.add(assertion);
                }
            }
        }
        return matched;
    }
    
    public boolean isEmpty() {
        return this.assertions.isEmpty();
    }
    
    public boolean contains(final QName assertionName) {
        return this.vocabulary.contains(assertionName);
    }
    
    @Override
    public int compareTo(final AssertionSet that) {
        if (this.equals(that)) {
            return 0;
        }
        final Iterator<QName> vIterator1 = this.getVocabulary().iterator();
        final Iterator<QName> vIterator2 = that.getVocabulary().iterator();
        while (vIterator1.hasNext()) {
            final QName entry1 = vIterator1.next();
            if (!vIterator2.hasNext()) {
                return 1;
            }
            final QName entry2 = vIterator2.next();
            final int result = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(entry1, entry2);
            if (result != 0) {
                return result;
            }
        }
        if (vIterator2.hasNext()) {
            return -1;
        }
        final Iterator<PolicyAssertion> pIterator1 = this.getAssertions().iterator();
        final Iterator<PolicyAssertion> pIterator2 = that.getAssertions().iterator();
        while (pIterator1.hasNext()) {
            final PolicyAssertion pa1 = pIterator1.next();
            if (!pIterator2.hasNext()) {
                return 1;
            }
            final PolicyAssertion pa2 = pIterator2.next();
            final int result2 = AssertionSet.ASSERTION_COMPARATOR.compare(pa1, pa2);
            if (result2 != 0) {
                return result2;
            }
        }
        if (pIterator2.hasNext()) {
            return -1;
        }
        return 1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssertionSet)) {
            return false;
        }
        final AssertionSet that = (AssertionSet)obj;
        boolean result = true;
        result = (result && this.vocabulary.equals(that.vocabulary));
        result = (result && this.assertions.size() == that.assertions.size() && this.assertions.containsAll(that.assertions));
        return result;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.vocabulary.hashCode();
        result = 37 * result + this.assertions.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        final String indent = PolicyUtils.Text.createIndent(indentLevel);
        final String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        buffer.append(indent).append("assertion set {").append(PolicyUtils.Text.NEW_LINE);
        if (this.assertions.isEmpty()) {
            buffer.append(innerIndent).append("no assertions").append(PolicyUtils.Text.NEW_LINE);
        }
        else {
            for (final PolicyAssertion assertion : this.assertions) {
                assertion.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
            }
        }
        buffer.append(indent).append('}');
        return buffer;
    }
    
    static {
        EMPTY_ASSERTION_SET = new AssertionSet(Collections.unmodifiableList((List<? extends PolicyAssertion>)new LinkedList<PolicyAssertion>()));
        ASSERTION_COMPARATOR = new Comparator<PolicyAssertion>() {
            @Override
            public int compare(final PolicyAssertion pa1, final PolicyAssertion pa2) {
                if (pa1.equals(pa2)) {
                    return 0;
                }
                int result = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(pa1.getName(), pa2.getName());
                if (result != 0) {
                    return result;
                }
                result = PolicyUtils.Comparison.compareNullableStrings(pa1.getValue(), pa2.getValue());
                if (result != 0) {
                    return result;
                }
                result = PolicyUtils.Comparison.compareBoolean(pa1.hasNestedAssertions(), pa2.hasNestedAssertions());
                if (result != 0) {
                    return result;
                }
                result = PolicyUtils.Comparison.compareBoolean(pa1.hasNestedPolicy(), pa2.hasNestedPolicy());
                if (result != 0) {
                    return result;
                }
                return Math.round(Math.signum((float)(pa1.hashCode() - pa2.hashCode())));
            }
        };
    }
}
