package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import java.util.LinkedList;
import javax.xml.ws.WebServiceFeature;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;

public class SelectOptimalEncodingFeatureConfigurator implements PolicyFeatureConfigurator
{
    public static final QName enabled;
    
    @Override
    public Collection<WebServiceFeature> getFeatures(final PolicyMapKey key, final PolicyMap policyMap) throws PolicyException {
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if (key != null && policyMap != null) {
            final Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (null != policy && policy.contains(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION)) {
                for (final AssertionSet assertionSet : policy) {
                    for (final PolicyAssertion assertion : assertionSet) {
                        if (EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION.equals(assertion.getName())) {
                            final String value = assertion.getAttributeValue(SelectOptimalEncodingFeatureConfigurator.enabled);
                            final boolean isSelectOptimalEncodingEnabled = value == null || Boolean.valueOf(value.trim());
                            features.add(new SelectOptimalEncodingFeature(isSelectOptimalEncodingEnabled));
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
