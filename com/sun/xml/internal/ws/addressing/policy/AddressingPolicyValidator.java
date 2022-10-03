package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;

public class AddressingPolicyValidator implements PolicyAssertionValidator
{
    private static final ArrayList<QName> supportedAssertions;
    private static final PolicyLogger LOGGER;
    
    @Override
    public Fitness validateClientSide(final PolicyAssertion assertion) {
        return AddressingPolicyValidator.supportedAssertions.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }
    
    @Override
    public Fitness validateServerSide(final PolicyAssertion assertion) {
        if (!AddressingPolicyValidator.supportedAssertions.contains(assertion.getName())) {
            return Fitness.UNKNOWN;
        }
        if (assertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
            final NestedPolicy nestedPolicy = assertion.getNestedPolicy();
            if (nestedPolicy != null) {
                boolean requiresAnonymousResponses = false;
                boolean requiresNonAnonymousResponses = false;
                for (final PolicyAssertion nestedAsser : nestedPolicy.getAssertionSet()) {
                    if (nestedAsser.getName().equals(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION)) {
                        requiresAnonymousResponses = true;
                    }
                    else {
                        if (!nestedAsser.getName().equals(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION)) {
                            AddressingPolicyValidator.LOGGER.warning("Found unsupported assertion:\n" + nestedAsser + "\nnested into assertion:\n" + assertion);
                            return Fitness.UNSUPPORTED;
                        }
                        requiresNonAnonymousResponses = true;
                    }
                }
                if (requiresAnonymousResponses && requiresNonAnonymousResponses) {
                    AddressingPolicyValidator.LOGGER.warning("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
                    return Fitness.INVALID;
                }
            }
        }
        return Fitness.SUPPORTED;
    }
    
    @Override
    public String[] declareSupportedDomains() {
        return new String[] { AddressingVersion.MEMBER.policyNsUri, AddressingVersion.W3C.policyNsUri, "http://www.w3.org/2007/05/addressing/metadata" };
    }
    
    static {
        (supportedAssertions = new ArrayList<QName>()).add(new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing"));
        AddressingPolicyValidator.supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
        AddressingPolicyValidator.supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
        AddressingPolicyValidator.supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
        LOGGER = PolicyLogger.getLogger(AddressingPolicyValidator.class);
    }
}
