package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.agreement.srp.SRP6StandardGroups;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import java.util.Vector;

public class DefaultTlsSRPGroupVerifier implements TlsSRPGroupVerifier
{
    protected static final Vector DEFAULT_GROUPS;
    protected Vector groups;
    
    public DefaultTlsSRPGroupVerifier() {
        this(DefaultTlsSRPGroupVerifier.DEFAULT_GROUPS);
    }
    
    public DefaultTlsSRPGroupVerifier(final Vector groups) {
        this.groups = groups;
    }
    
    public boolean accept(final SRP6GroupParameters srp6GroupParameters) {
        for (int i = 0; i < this.groups.size(); ++i) {
            if (this.areGroupsEqual(srp6GroupParameters, (SRP6GroupParameters)this.groups.elementAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean areGroupsEqual(final SRP6GroupParameters srp6GroupParameters, final SRP6GroupParameters srp6GroupParameters2) {
        return srp6GroupParameters == srp6GroupParameters2 || (this.areParametersEqual(srp6GroupParameters.getN(), srp6GroupParameters2.getN()) && this.areParametersEqual(srp6GroupParameters.getG(), srp6GroupParameters2.getG()));
    }
    
    protected boolean areParametersEqual(final BigInteger bigInteger, final BigInteger bigInteger2) {
        return bigInteger == bigInteger2 || bigInteger.equals(bigInteger2);
    }
    
    static {
        (DEFAULT_GROUPS = new Vector()).addElement(SRP6StandardGroups.rfc5054_1024);
        DefaultTlsSRPGroupVerifier.DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_1536);
        DefaultTlsSRPGroupVerifier.DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_2048);
        DefaultTlsSRPGroupVerifier.DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_3072);
        DefaultTlsSRPGroupVerifier.DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_4096);
        DefaultTlsSRPGroupVerifier.DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_6144);
        DefaultTlsSRPGroupVerifier.DEFAULT_GROUPS.addElement(SRP6StandardGroups.rfc5054_8192);
    }
}
