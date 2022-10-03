package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.api.fastinfoset.FastInfosetFeature;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import java.util.LinkedList;
import javax.xml.ws.WebServiceFeature;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;

public class FastInfosetFeatureConfigurator implements PolicyFeatureConfigurator
{
    public static final QName enabled;
    
    @Override
    public Collection<WebServiceFeature> getFeatures(final PolicyMapKey key, final PolicyMap policyMap) throws PolicyException {
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if (key != null && policyMap != null) {
            final Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (null != policy && policy.contains(EncodingConstants.OPTIMIZED_FI_SERIALIZATION_ASSERTION)) {
                for (final AssertionSet assertionSet : policy) {
                    for (final PolicyAssertion assertion : assertionSet) {
                        if (EncodingConstants.OPTIMIZED_FI_SERIALIZATION_ASSERTION.equals(assertion.getName())) {
                            final String value = assertion.getAttributeValue(FastInfosetFeatureConfigurator.enabled);
                            final boolean isFastInfosetEnabled = Boolean.valueOf(value.trim());
                            features.add(new FastInfosetFeature(isFastInfosetEnabled));
                        }
                    }
                }
            }
        }
        return features;
    }
    
    static {
        enabled = new QName("enabled");
    }
}
