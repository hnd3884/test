package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.Policy;
import javax.xml.ws.soap.MTOMFeature;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import java.util.LinkedList;
import javax.xml.ws.WebServiceFeature;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;

public class MtomFeatureConfigurator implements PolicyFeatureConfigurator
{
    @Override
    public Collection<WebServiceFeature> getFeatures(final PolicyMapKey key, final PolicyMap policyMap) throws PolicyException {
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if (key != null && policyMap != null) {
            final Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (null != policy && policy.contains(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION)) {
                for (final AssertionSet assertionSet : policy) {
                    for (final PolicyAssertion assertion : assertionSet) {
                        if (EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION.equals(assertion.getName())) {
                            features.add(new MTOMFeature(true));
                        }
                    }
                }
            }
        }
        return features;
    }
}
