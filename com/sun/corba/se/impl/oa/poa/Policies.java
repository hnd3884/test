package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.spi.extension.CopyObjectPolicy;
import com.sun.corba.se.spi.extension.ZeroPortPolicy;
import com.sun.corba.se.spi.extension.ServantCachingPolicy;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import java.util.BitSet;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.CORBA.Policy;
import java.util.Iterator;
import java.util.HashMap;

public final class Policies
{
    private static final int MIN_POA_POLICY_ID = 16;
    private static final int MAX_POA_POLICY_ID = 22;
    private static final int POLICY_TABLE_SIZE = 7;
    int defaultObjectCopierFactoryId;
    private HashMap policyMap;
    public static final Policies defaultPolicies;
    public static final Policies rootPOAPolicies;
    private int[] poaPolicyValues;
    
    private int getPolicyValue(final int n) {
        return this.poaPolicyValues[n - 16];
    }
    
    private void setPolicyValue(final int n, final int n2) {
        this.poaPolicyValues[n - 16] = n2;
    }
    
    private Policies(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        this.policyMap = new HashMap();
        this.poaPolicyValues = new int[] { n, n2, n3, n4, n5, n6, n7 };
    }
    
    private Policies() {
        this(0, 0, 0, 1, 1, 0, 0);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Policies[");
        int n = 1;
        final Iterator iterator = this.policyMap.values().iterator();
        while (iterator.hasNext()) {
            if (n != 0) {
                n = 0;
            }
            else {
                sb.append(",");
            }
            sb.append(iterator.next().toString());
        }
        sb.append("]");
        return sb.toString();
    }
    
    private int getPOAPolicyValue(final Policy policy) {
        if (policy instanceof ThreadPolicy) {
            return ((ThreadPolicy)policy).value().value();
        }
        if (policy instanceof LifespanPolicy) {
            return ((LifespanPolicy)policy).value().value();
        }
        if (policy instanceof IdUniquenessPolicy) {
            return ((IdUniquenessPolicy)policy).value().value();
        }
        if (policy instanceof IdAssignmentPolicy) {
            return ((IdAssignmentPolicy)policy).value().value();
        }
        if (policy instanceof ServantRetentionPolicy) {
            return ((ServantRetentionPolicy)policy).value().value();
        }
        if (policy instanceof RequestProcessingPolicy) {
            return ((RequestProcessingPolicy)policy).value().value();
        }
        if (policy instanceof ImplicitActivationPolicy) {
            return ((ImplicitActivationPolicy)policy).value().value();
        }
        return -1;
    }
    
    private void checkForPolicyError(final BitSet set) throws InvalidPolicy {
        for (short n = 0; n < set.length(); ++n) {
            if (set.get(n)) {
                throw new InvalidPolicy(n);
            }
        }
    }
    
    private void addToErrorSet(final Policy[] array, final int n, final BitSet set) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].policy_type() == n) {
                set.set(i);
                return;
            }
        }
    }
    
    Policies(final Policy[] array, final int defaultObjectCopierFactoryId) throws InvalidPolicy {
        this();
        this.defaultObjectCopierFactoryId = defaultObjectCopierFactoryId;
        if (array == null) {
            return;
        }
        final BitSet set = new BitSet(array.length);
        for (int i = 0; i < array.length; i = (short)(i + 1)) {
            final Policy policy = array[i];
            final int poaPolicyValue = this.getPOAPolicyValue(policy);
            final Integer n = new Integer(policy.policy_type());
            final Policy policy2 = this.policyMap.get(n);
            if (policy2 == null) {
                this.policyMap.put(n, policy);
            }
            if (poaPolicyValue >= 0) {
                this.setPolicyValue(n, poaPolicyValue);
                if (policy2 != null && this.getPOAPolicyValue(policy2) != poaPolicyValue) {
                    set.set(i);
                }
            }
        }
        if (!this.retainServants() && this.useActiveMapOnly()) {
            this.addToErrorSet(array, 21, set);
            this.addToErrorSet(array, 22, set);
        }
        if (this.isImplicitlyActivated()) {
            if (!this.retainServants()) {
                this.addToErrorSet(array, 20, set);
                this.addToErrorSet(array, 21, set);
            }
            if (!this.isSystemAssignedIds()) {
                this.addToErrorSet(array, 20, set);
                this.addToErrorSet(array, 19, set);
            }
        }
        this.checkForPolicyError(set);
    }
    
    public Policy get_effective_policy(final int n) {
        return this.policyMap.get(new Integer(n));
    }
    
    public final boolean isOrbControlledThreads() {
        return this.getPolicyValue(16) == 0;
    }
    
    public final boolean isSingleThreaded() {
        return this.getPolicyValue(16) == 1;
    }
    
    public final boolean isTransient() {
        return this.getPolicyValue(17) == 0;
    }
    
    public final boolean isPersistent() {
        return this.getPolicyValue(17) == 1;
    }
    
    public final boolean isUniqueIds() {
        return this.getPolicyValue(18) == 0;
    }
    
    public final boolean isMultipleIds() {
        return this.getPolicyValue(18) == 1;
    }
    
    public final boolean isUserAssignedIds() {
        return this.getPolicyValue(19) == 0;
    }
    
    public final boolean isSystemAssignedIds() {
        return this.getPolicyValue(19) == 1;
    }
    
    public final boolean retainServants() {
        return this.getPolicyValue(21) == 0;
    }
    
    public final boolean useActiveMapOnly() {
        return this.getPolicyValue(22) == 0;
    }
    
    public final boolean useDefaultServant() {
        return this.getPolicyValue(22) == 1;
    }
    
    public final boolean useServantManager() {
        return this.getPolicyValue(22) == 2;
    }
    
    public final boolean isImplicitlyActivated() {
        return this.getPolicyValue(20) == 0;
    }
    
    public final int servantCachingLevel() {
        final ServantCachingPolicy servantCachingPolicy = this.policyMap.get(new Integer(1398079488));
        if (servantCachingPolicy == null) {
            return 0;
        }
        return servantCachingPolicy.getType();
    }
    
    public final boolean forceZeroPort() {
        final ZeroPortPolicy zeroPortPolicy = this.policyMap.get(new Integer(1398079489));
        return zeroPortPolicy != null && zeroPortPolicy.forceZeroPort();
    }
    
    public final int getCopierId() {
        final CopyObjectPolicy copyObjectPolicy = this.policyMap.get(new Integer(1398079490));
        if (copyObjectPolicy != null) {
            return copyObjectPolicy.getValue();
        }
        return this.defaultObjectCopierFactoryId;
    }
    
    static {
        defaultPolicies = new Policies();
        rootPOAPolicies = new Policies(0, 0, 0, 1, 0, 0, 0);
    }
}
