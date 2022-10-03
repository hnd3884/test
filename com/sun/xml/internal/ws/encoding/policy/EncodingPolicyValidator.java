package com.sun.xml.internal.ws.encoding.policy;

import java.util.Collection;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;

public class EncodingPolicyValidator implements PolicyAssertionValidator
{
    private static final ArrayList<QName> serverSideSupportedAssertions;
    private static final ArrayList<QName> clientSideSupportedAssertions;
    
    @Override
    public Fitness validateClientSide(final PolicyAssertion assertion) {
        return EncodingPolicyValidator.clientSideSupportedAssertions.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }
    
    @Override
    public Fitness validateServerSide(final PolicyAssertion assertion) {
        final QName assertionName = assertion.getName();
        if (EncodingPolicyValidator.serverSideSupportedAssertions.contains(assertionName)) {
            return Fitness.SUPPORTED;
        }
        if (EncodingPolicyValidator.clientSideSupportedAssertions.contains(assertionName)) {
            return Fitness.UNSUPPORTED;
        }
        return Fitness.UNKNOWN;
    }
    
    @Override
    public String[] declareSupportedDomains() {
        return new String[] { "http://schemas.xmlsoap.org/ws/2004/09/policy/optimizedmimeserialization", "http://schemas.xmlsoap.org/ws/2004/09/policy/encoding", "http://java.sun.com/xml/ns/wsit/2006/09/policy/encoding/client", "http://java.sun.com/xml/ns/wsit/2006/09/policy/fastinfoset/service" };
    }
    
    static {
        serverSideSupportedAssertions = new ArrayList<QName>(3);
        clientSideSupportedAssertions = new ArrayList<QName>(4);
        EncodingPolicyValidator.serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);
        EncodingPolicyValidator.serverSideSupportedAssertions.add(EncodingConstants.UTF816FFFE_CHARACTER_ENCODING_ASSERTION);
        EncodingPolicyValidator.serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_FI_SERIALIZATION_ASSERTION);
        EncodingPolicyValidator.clientSideSupportedAssertions.add(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION);
        EncodingPolicyValidator.clientSideSupportedAssertions.addAll(EncodingPolicyValidator.serverSideSupportedAssertions);
    }
}
