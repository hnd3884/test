package org.bouncycastle.jce.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.security.cert.PolicyNode;

public class PKIXPolicyNode implements PolicyNode
{
    protected List children;
    protected int depth;
    protected Set expectedPolicies;
    protected PolicyNode parent;
    protected Set policyQualifiers;
    protected String validPolicy;
    protected boolean critical;
    
    public PKIXPolicyNode(final List children, final int depth, final Set expectedPolicies, final PolicyNode parent, final Set policyQualifiers, final String validPolicy, final boolean critical) {
        this.children = children;
        this.depth = depth;
        this.expectedPolicies = expectedPolicies;
        this.parent = parent;
        this.policyQualifiers = policyQualifiers;
        this.validPolicy = validPolicy;
        this.critical = critical;
    }
    
    public void addChild(final PKIXPolicyNode pkixPolicyNode) {
        this.children.add(pkixPolicyNode);
        pkixPolicyNode.setParent(this);
    }
    
    public Iterator getChildren() {
        return this.children.iterator();
    }
    
    public int getDepth() {
        return this.depth;
    }
    
    public Set getExpectedPolicies() {
        return this.expectedPolicies;
    }
    
    public PolicyNode getParent() {
        return this.parent;
    }
    
    public Set getPolicyQualifiers() {
        return this.policyQualifiers;
    }
    
    public String getValidPolicy() {
        return this.validPolicy;
    }
    
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }
    
    public boolean isCritical() {
        return this.critical;
    }
    
    public void removeChild(final PKIXPolicyNode pkixPolicyNode) {
        this.children.remove(pkixPolicyNode);
    }
    
    public void setCritical(final boolean critical) {
        this.critical = critical;
    }
    
    public void setParent(final PKIXPolicyNode parent) {
        this.parent = parent;
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }
    
    public String toString(final String s) {
        final StringBuffer sb = new StringBuffer();
        sb.append(s);
        sb.append(this.validPolicy);
        sb.append(" {\n");
        for (int i = 0; i < this.children.size(); ++i) {
            sb.append(((PKIXPolicyNode)this.children.get(i)).toString(s + "    "));
        }
        sb.append(s);
        sb.append("}\n");
        return sb.toString();
    }
    
    public Object clone() {
        return this.copy();
    }
    
    public PKIXPolicyNode copy() {
        final HashSet set = new HashSet();
        final Iterator iterator = this.expectedPolicies.iterator();
        while (iterator.hasNext()) {
            set.add(new String((String)iterator.next()));
        }
        final HashSet set2 = new HashSet();
        final Iterator iterator2 = this.policyQualifiers.iterator();
        while (iterator2.hasNext()) {
            set2.add(new String((String)iterator2.next()));
        }
        final PKIXPolicyNode parent = new PKIXPolicyNode(new ArrayList(), this.depth, set, null, set2, new String(this.validPolicy), this.critical);
        final Iterator iterator3 = this.children.iterator();
        while (iterator3.hasNext()) {
            final PKIXPolicyNode copy = ((PKIXPolicyNode)iterator3.next()).copy();
            copy.setParent(parent);
            parent.addChild(copy);
        }
        return parent;
    }
    
    public void setExpectedPolicies(final Set expectedPolicies) {
        this.expectedPolicies = expectedPolicies;
    }
}
