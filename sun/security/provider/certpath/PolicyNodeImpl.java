package sun.security.provider.certpath;

import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.security.cert.PolicyQualifierInfo;
import java.util.HashSet;
import java.security.cert.PolicyNode;

final class PolicyNodeImpl implements PolicyNode
{
    private static final String ANY_POLICY = "2.5.29.32.0";
    private PolicyNodeImpl mParent;
    private HashSet<PolicyNodeImpl> mChildren;
    private String mValidPolicy;
    private HashSet<PolicyQualifierInfo> mQualifierSet;
    private boolean mCriticalityIndicator;
    private HashSet<String> mExpectedPolicySet;
    private boolean mOriginalExpectedPolicySet;
    private int mDepth;
    private boolean isImmutable;
    
    PolicyNodeImpl(final PolicyNodeImpl mParent, final String mValidPolicy, final Set<PolicyQualifierInfo> set, final boolean mCriticalityIndicator, final Set<String> set2, final boolean b) {
        this.isImmutable = false;
        this.mParent = mParent;
        this.mChildren = new HashSet<PolicyNodeImpl>();
        if (mValidPolicy != null) {
            this.mValidPolicy = mValidPolicy;
        }
        else {
            this.mValidPolicy = "";
        }
        if (set != null) {
            this.mQualifierSet = new HashSet<PolicyQualifierInfo>(set);
        }
        else {
            this.mQualifierSet = new HashSet<PolicyQualifierInfo>();
        }
        this.mCriticalityIndicator = mCriticalityIndicator;
        if (set2 != null) {
            this.mExpectedPolicySet = new HashSet<String>(set2);
        }
        else {
            this.mExpectedPolicySet = new HashSet<String>();
        }
        this.mOriginalExpectedPolicySet = !b;
        if (this.mParent != null) {
            this.mDepth = this.mParent.getDepth() + 1;
            this.mParent.addChild(this);
        }
        else {
            this.mDepth = 0;
        }
    }
    
    PolicyNodeImpl(final PolicyNodeImpl policyNodeImpl, final PolicyNodeImpl policyNodeImpl2) {
        this(policyNodeImpl, policyNodeImpl2.mValidPolicy, policyNodeImpl2.mQualifierSet, policyNodeImpl2.mCriticalityIndicator, policyNodeImpl2.mExpectedPolicySet, false);
    }
    
    @Override
    public PolicyNode getParent() {
        return this.mParent;
    }
    
    @Override
    public Iterator<PolicyNodeImpl> getChildren() {
        return Collections.unmodifiableSet((Set<? extends PolicyNodeImpl>)this.mChildren).iterator();
    }
    
    @Override
    public int getDepth() {
        return this.mDepth;
    }
    
    @Override
    public String getValidPolicy() {
        return this.mValidPolicy;
    }
    
    @Override
    public Set<PolicyQualifierInfo> getPolicyQualifiers() {
        return Collections.unmodifiableSet((Set<? extends PolicyQualifierInfo>)this.mQualifierSet);
    }
    
    @Override
    public Set<String> getExpectedPolicies() {
        return Collections.unmodifiableSet((Set<? extends String>)this.mExpectedPolicySet);
    }
    
    @Override
    public boolean isCritical() {
        return this.mCriticalityIndicator;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.asString());
        final Iterator<PolicyNodeImpl> iterator = this.mChildren.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
        }
        return sb.toString();
    }
    
    boolean isImmutable() {
        return this.isImmutable;
    }
    
    void setImmutable() {
        if (this.isImmutable) {
            return;
        }
        final Iterator<PolicyNodeImpl> iterator = this.mChildren.iterator();
        while (iterator.hasNext()) {
            iterator.next().setImmutable();
        }
        this.isImmutable = true;
    }
    
    private void addChild(final PolicyNodeImpl policyNodeImpl) {
        if (this.isImmutable) {
            throw new IllegalStateException("PolicyNode is immutable");
        }
        this.mChildren.add(policyNodeImpl);
    }
    
    void addExpectedPolicy(final String s) {
        if (this.isImmutable) {
            throw new IllegalStateException("PolicyNode is immutable");
        }
        if (this.mOriginalExpectedPolicySet) {
            this.mExpectedPolicySet.clear();
            this.mOriginalExpectedPolicySet = false;
        }
        this.mExpectedPolicySet.add(s);
    }
    
    void prune(final int n) {
        if (this.isImmutable) {
            throw new IllegalStateException("PolicyNode is immutable");
        }
        if (this.mChildren.size() == 0) {
            return;
        }
        final Iterator<PolicyNodeImpl> iterator = this.mChildren.iterator();
        while (iterator.hasNext()) {
            final PolicyNodeImpl policyNodeImpl = iterator.next();
            policyNodeImpl.prune(n);
            if (policyNodeImpl.mChildren.size() == 0 && n > this.mDepth + 1) {
                iterator.remove();
            }
        }
    }
    
    void deleteChild(final PolicyNode policyNode) {
        if (this.isImmutable) {
            throw new IllegalStateException("PolicyNode is immutable");
        }
        this.mChildren.remove(policyNode);
    }
    
    PolicyNodeImpl copyTree() {
        return this.copyTree(null);
    }
    
    private PolicyNodeImpl copyTree(final PolicyNodeImpl policyNodeImpl) {
        final PolicyNodeImpl policyNodeImpl2 = new PolicyNodeImpl(policyNodeImpl, this);
        final Iterator<PolicyNodeImpl> iterator = this.mChildren.iterator();
        while (iterator.hasNext()) {
            iterator.next().copyTree(policyNodeImpl2);
        }
        return policyNodeImpl2;
    }
    
    Set<PolicyNodeImpl> getPolicyNodes(final int n) {
        final HashSet set = new HashSet();
        this.getPolicyNodes(n, set);
        return set;
    }
    
    private void getPolicyNodes(final int n, final Set<PolicyNodeImpl> set) {
        if (this.mDepth == n) {
            set.add(this);
        }
        else {
            final Iterator<PolicyNodeImpl> iterator = this.mChildren.iterator();
            while (iterator.hasNext()) {
                iterator.next().getPolicyNodes(n, set);
            }
        }
    }
    
    Set<PolicyNodeImpl> getPolicyNodesExpected(final int n, final String s, final boolean b) {
        if (s.equals("2.5.29.32.0")) {
            return this.getPolicyNodes(n);
        }
        return this.getPolicyNodesExpectedHelper(n, s, b);
    }
    
    private Set<PolicyNodeImpl> getPolicyNodesExpectedHelper(final int n, final String s, final boolean b) {
        final HashSet set = new HashSet();
        if (this.mDepth < n) {
            final Iterator<PolicyNodeImpl> iterator = this.mChildren.iterator();
            while (iterator.hasNext()) {
                set.addAll(iterator.next().getPolicyNodesExpectedHelper(n, s, b));
            }
        }
        else if (b) {
            if (this.mExpectedPolicySet.contains("2.5.29.32.0")) {
                set.add(this);
            }
        }
        else if (this.mExpectedPolicySet.contains(s)) {
            set.add(this);
        }
        return set;
    }
    
    Set<PolicyNodeImpl> getPolicyNodesValid(final int n, final String s) {
        final HashSet set = new HashSet();
        if (this.mDepth < n) {
            final Iterator<PolicyNodeImpl> iterator = this.mChildren.iterator();
            while (iterator.hasNext()) {
                set.addAll(iterator.next().getPolicyNodesValid(n, s));
            }
        }
        else if (this.mValidPolicy.equals(s)) {
            set.add(this);
        }
        return set;
    }
    
    private static String policyToString(final String s) {
        if (s.equals("2.5.29.32.0")) {
            return "anyPolicy";
        }
        return s;
    }
    
    String asString() {
        if (this.mParent == null) {
            return "anyPolicy  ROOT\n";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getDepth(); ++i) {
            sb.append("  ");
        }
        sb.append(policyToString(this.getValidPolicy()));
        sb.append("  CRIT: ");
        sb.append(this.isCritical());
        sb.append("  EP: ");
        final Iterator<String> iterator = this.getExpectedPolicies().iterator();
        while (iterator.hasNext()) {
            sb.append(policyToString(iterator.next()));
            sb.append(" ");
        }
        sb.append(" (");
        sb.append(this.getDepth());
        sb.append(")\n");
        return sb.toString();
    }
}
