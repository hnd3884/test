package com.sun.xml.internal.ws.policy;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;

public final class NestedPolicy extends Policy
{
    private static final String NESTED_POLICY_TOSTRING_NAME = "nested policy";
    
    private NestedPolicy(final AssertionSet set) {
        super("nested policy", Arrays.asList(set));
    }
    
    private NestedPolicy(final String name, final String policyId, final AssertionSet set) {
        super("nested policy", name, policyId, Arrays.asList(set));
    }
    
    static NestedPolicy createNestedPolicy(final AssertionSet set) {
        return new NestedPolicy(set);
    }
    
    static NestedPolicy createNestedPolicy(final String name, final String policyId, final AssertionSet set) {
        return new NestedPolicy(name, policyId, set);
    }
    
    public AssertionSet getAssertionSet() {
        final Iterator<AssertionSet> iterator = this.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NestedPolicy)) {
            return false;
        }
        final NestedPolicy that = (NestedPolicy)obj;
        return super.equals(that);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    @Override
    StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        return super.toString(indentLevel, buffer);
    }
}
