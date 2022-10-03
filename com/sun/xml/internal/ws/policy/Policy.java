package com.sun.xml.internal.ws.policy;

import java.util.Arrays;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.Comparator;
import java.util.TreeSet;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import javax.xml.namespace.QName;
import java.util.Set;
import java.util.List;

public class Policy implements Iterable<AssertionSet>
{
    private static final String POLICY_TOSTRING_NAME = "policy";
    private static final List<AssertionSet> NULL_POLICY_ASSERTION_SETS;
    private static final List<AssertionSet> EMPTY_POLICY_ASSERTION_SETS;
    private static final Set<QName> EMPTY_VOCABULARY;
    private static final Policy ANONYMOUS_NULL_POLICY;
    private static final Policy ANONYMOUS_EMPTY_POLICY;
    private String policyId;
    private String name;
    private NamespaceVersion nsVersion;
    private final List<AssertionSet> assertionSets;
    private final Set<QName> vocabulary;
    private final Collection<QName> immutableVocabulary;
    private final String toStringName;
    
    public static Policy createNullPolicy() {
        return Policy.ANONYMOUS_NULL_POLICY;
    }
    
    public static Policy createEmptyPolicy() {
        return Policy.ANONYMOUS_EMPTY_POLICY;
    }
    
    public static Policy createNullPolicy(final String name, final String policyId) {
        if (name == null && policyId == null) {
            return Policy.ANONYMOUS_NULL_POLICY;
        }
        return new Policy(name, policyId, Policy.NULL_POLICY_ASSERTION_SETS, Policy.EMPTY_VOCABULARY);
    }
    
    public static Policy createNullPolicy(final NamespaceVersion nsVersion, final String name, final String policyId) {
        if ((nsVersion == null || nsVersion == NamespaceVersion.getLatestVersion()) && name == null && policyId == null) {
            return Policy.ANONYMOUS_NULL_POLICY;
        }
        return new Policy(nsVersion, name, policyId, Policy.NULL_POLICY_ASSERTION_SETS, Policy.EMPTY_VOCABULARY);
    }
    
    public static Policy createEmptyPolicy(final String name, final String policyId) {
        if (name == null && policyId == null) {
            return Policy.ANONYMOUS_EMPTY_POLICY;
        }
        return new Policy(name, policyId, Policy.EMPTY_POLICY_ASSERTION_SETS, Policy.EMPTY_VOCABULARY);
    }
    
    public static Policy createEmptyPolicy(final NamespaceVersion nsVersion, final String name, final String policyId) {
        if ((nsVersion == null || nsVersion == NamespaceVersion.getLatestVersion()) && name == null && policyId == null) {
            return Policy.ANONYMOUS_EMPTY_POLICY;
        }
        return new Policy(nsVersion, name, policyId, Policy.EMPTY_POLICY_ASSERTION_SETS, Policy.EMPTY_VOCABULARY);
    }
    
    public static Policy createPolicy(final Collection<AssertionSet> sets) {
        if (sets == null || sets.isEmpty()) {
            return createNullPolicy();
        }
        return new Policy("policy", sets);
    }
    
    public static Policy createPolicy(final String name, final String policyId, final Collection<AssertionSet> sets) {
        if (sets == null || sets.isEmpty()) {
            return createNullPolicy(name, policyId);
        }
        return new Policy("policy", name, policyId, sets);
    }
    
    public static Policy createPolicy(final NamespaceVersion nsVersion, final String name, final String policyId, final Collection<AssertionSet> sets) {
        if (sets == null || sets.isEmpty()) {
            return createNullPolicy(nsVersion, name, policyId);
        }
        return new Policy(nsVersion, "policy", name, policyId, sets);
    }
    
    private Policy(final String name, final String policyId, final List<AssertionSet> assertionSets, final Set<QName> vocabulary) {
        this.nsVersion = NamespaceVersion.getLatestVersion();
        this.toStringName = "policy";
        this.name = name;
        this.policyId = policyId;
        this.assertionSets = assertionSets;
        this.vocabulary = vocabulary;
        this.immutableVocabulary = Collections.unmodifiableCollection((Collection<? extends QName>)this.vocabulary);
    }
    
    Policy(final String toStringName, final Collection<AssertionSet> sets) {
        this.nsVersion = NamespaceVersion.getLatestVersion();
        this.toStringName = toStringName;
        if (sets == null || sets.isEmpty()) {
            this.assertionSets = Policy.NULL_POLICY_ASSERTION_SETS;
            this.vocabulary = Policy.EMPTY_VOCABULARY;
            this.immutableVocabulary = Policy.EMPTY_VOCABULARY;
        }
        else {
            this.assertionSets = new LinkedList<AssertionSet>();
            this.vocabulary = new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR);
            this.immutableVocabulary = Collections.unmodifiableCollection((Collection<? extends QName>)this.vocabulary);
            this.addAll(sets);
        }
    }
    
    Policy(final String toStringName, final String name, final String policyId, final Collection<AssertionSet> sets) {
        this(toStringName, sets);
        this.name = name;
        this.policyId = policyId;
    }
    
    private Policy(final NamespaceVersion nsVersion, final String name, final String policyId, final List<AssertionSet> assertionSets, final Set<QName> vocabulary) {
        this.nsVersion = nsVersion;
        this.toStringName = "policy";
        this.name = name;
        this.policyId = policyId;
        this.assertionSets = assertionSets;
        this.vocabulary = vocabulary;
        this.immutableVocabulary = Collections.unmodifiableCollection((Collection<? extends QName>)this.vocabulary);
    }
    
    Policy(final NamespaceVersion nsVersion, final String toStringName, final Collection<AssertionSet> sets) {
        this.nsVersion = nsVersion;
        this.toStringName = toStringName;
        if (sets == null || sets.isEmpty()) {
            this.assertionSets = Policy.NULL_POLICY_ASSERTION_SETS;
            this.vocabulary = Policy.EMPTY_VOCABULARY;
            this.immutableVocabulary = Policy.EMPTY_VOCABULARY;
        }
        else {
            this.assertionSets = new LinkedList<AssertionSet>();
            this.vocabulary = new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR);
            this.immutableVocabulary = Collections.unmodifiableCollection((Collection<? extends QName>)this.vocabulary);
            this.addAll(sets);
        }
    }
    
    Policy(final NamespaceVersion nsVersion, final String toStringName, final String name, final String policyId, final Collection<AssertionSet> sets) {
        this(nsVersion, toStringName, sets);
        this.name = name;
        this.policyId = policyId;
    }
    
    private boolean add(final AssertionSet set) {
        if (set == null) {
            return false;
        }
        if (this.assertionSets.contains(set)) {
            return false;
        }
        this.assertionSets.add(set);
        this.vocabulary.addAll(set.getVocabulary());
        return true;
    }
    
    private boolean addAll(final Collection<AssertionSet> sets) {
        assert sets != null && !sets.isEmpty() : LocalizationMessages.WSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION();
        boolean result = true;
        for (final AssertionSet set : sets) {
            result &= this.add(set);
        }
        Collections.sort(this.assertionSets);
        return result;
    }
    
    Collection<AssertionSet> getContent() {
        return this.assertionSets;
    }
    
    public String getId() {
        return this.policyId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public NamespaceVersion getNamespaceVersion() {
        return this.nsVersion;
    }
    
    public String getIdOrName() {
        if (this.policyId != null) {
            return this.policyId;
        }
        return this.name;
    }
    
    public int getNumberOfAssertionSets() {
        return this.assertionSets.size();
    }
    
    @Override
    public Iterator<AssertionSet> iterator() {
        return this.assertionSets.iterator();
    }
    
    public boolean isNull() {
        return this.assertionSets.size() == 0;
    }
    
    public boolean isEmpty() {
        return this.assertionSets.size() == 1 && this.assertionSets.get(0).isEmpty();
    }
    
    public boolean contains(final String namespaceUri) {
        for (final QName entry : this.vocabulary) {
            if (entry.getNamespaceURI().equals(namespaceUri)) {
                return true;
            }
        }
        return false;
    }
    
    public Collection<QName> getVocabulary() {
        return this.immutableVocabulary;
    }
    
    public boolean contains(final QName assertionName) {
        return this.vocabulary.contains(assertionName);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Policy)) {
            return false;
        }
        final Policy that = (Policy)obj;
        boolean result = true;
        result = (result && this.vocabulary.equals(that.vocabulary));
        result = (result && this.assertionSets.size() == that.assertionSets.size() && this.assertionSets.containsAll(that.assertionSets));
        return result;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.vocabulary.hashCode();
        result = 37 * result + this.assertionSets.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        final String indent = PolicyUtils.Text.createIndent(indentLevel);
        final String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        final String innerDoubleIndent = PolicyUtils.Text.createIndent(indentLevel + 2);
        buffer.append(indent).append(this.toStringName).append(" {").append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("namespace version = '").append(this.nsVersion.name()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("id = '").append(this.policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("name = '").append(this.name).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("vocabulary {").append(PolicyUtils.Text.NEW_LINE);
        if (this.vocabulary.isEmpty()) {
            buffer.append(innerDoubleIndent).append("no entries").append(PolicyUtils.Text.NEW_LINE);
        }
        else {
            int index = 1;
            for (final QName entry : this.vocabulary) {
                buffer.append(innerDoubleIndent).append(index++).append(". entry = '").append(entry.getNamespaceURI()).append(':').append(entry.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
            }
        }
        buffer.append(innerIndent).append('}').append(PolicyUtils.Text.NEW_LINE);
        if (this.assertionSets.isEmpty()) {
            buffer.append(innerIndent).append("no assertion sets").append(PolicyUtils.Text.NEW_LINE);
        }
        else {
            for (final AssertionSet set : this.assertionSets) {
                set.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
            }
        }
        buffer.append(indent).append('}');
        return buffer;
    }
    
    static {
        NULL_POLICY_ASSERTION_SETS = Collections.unmodifiableList((List<? extends AssertionSet>)new LinkedList<AssertionSet>());
        EMPTY_POLICY_ASSERTION_SETS = Collections.unmodifiableList((List<? extends AssertionSet>)new LinkedList<AssertionSet>(Arrays.asList(AssertionSet.emptyAssertionSet())));
        EMPTY_VOCABULARY = Collections.unmodifiableSet((Set<? extends QName>)new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR));
        ANONYMOUS_NULL_POLICY = new Policy(null, null, Policy.NULL_POLICY_ASSERTION_SETS, Policy.EMPTY_VOCABULARY);
        ANONYMOUS_EMPTY_POLICY = new Policy(null, null, Policy.EMPTY_POLICY_ASSERTION_SETS, Policy.EMPTY_VOCABULARY);
    }
}
