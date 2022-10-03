package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Iterator;
import java.util.HashSet;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Set;

public abstract class AbstractQNameValidator implements PolicyAssertionValidator
{
    private final Set<String> supportedDomains;
    private final Collection<QName> serverAssertions;
    private final Collection<QName> clientAssertions;
    
    protected AbstractQNameValidator(final Collection<QName> serverSideAssertions, final Collection<QName> clientSideAssertions) {
        this.supportedDomains = new HashSet<String>();
        if (serverSideAssertions != null) {
            this.serverAssertions = new HashSet<QName>(serverSideAssertions);
            for (final QName assertion : this.serverAssertions) {
                this.supportedDomains.add(assertion.getNamespaceURI());
            }
        }
        else {
            this.serverAssertions = new HashSet<QName>(0);
        }
        if (clientSideAssertions != null) {
            this.clientAssertions = new HashSet<QName>(clientSideAssertions);
            for (final QName assertion : this.clientAssertions) {
                this.supportedDomains.add(assertion.getNamespaceURI());
            }
        }
        else {
            this.clientAssertions = new HashSet<QName>(0);
        }
    }
    
    @Override
    public String[] declareSupportedDomains() {
        return this.supportedDomains.toArray(new String[this.supportedDomains.size()]);
    }
    
    @Override
    public Fitness validateClientSide(final PolicyAssertion assertion) {
        return this.validateAssertion(assertion, this.clientAssertions, this.serverAssertions);
    }
    
    @Override
    public Fitness validateServerSide(final PolicyAssertion assertion) {
        return this.validateAssertion(assertion, this.serverAssertions, this.clientAssertions);
    }
    
    private Fitness validateAssertion(final PolicyAssertion assertion, final Collection<QName> thisSideAssertions, final Collection<QName> otherSideAssertions) {
        final QName assertionName = assertion.getName();
        if (thisSideAssertions.contains(assertionName)) {
            return Fitness.SUPPORTED;
        }
        if (otherSideAssertions.contains(assertionName)) {
            return Fitness.UNSUPPORTED;
        }
        return Fitness.UNKNOWN;
    }
}
