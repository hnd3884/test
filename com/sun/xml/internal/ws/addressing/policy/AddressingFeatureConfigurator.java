package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.policy.NestedPolicy;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.policy.PolicyException;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import java.util.LinkedList;
import javax.xml.ws.WebServiceFeature;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;

public class AddressingFeatureConfigurator implements PolicyFeatureConfigurator
{
    private static final PolicyLogger LOGGER;
    private static final QName[] ADDRESSING_ASSERTIONS;
    
    @Override
    public Collection<WebServiceFeature> getFeatures(final PolicyMapKey key, final PolicyMap policyMap) throws PolicyException {
        AddressingFeatureConfigurator.LOGGER.entering(key, policyMap);
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if (key != null && policyMap != null) {
            final Policy policy = policyMap.getEndpointEffectivePolicy(key);
            for (final QName addressingAssertionQName : AddressingFeatureConfigurator.ADDRESSING_ASSERTIONS) {
                if (policy != null && policy.contains(addressingAssertionQName)) {
                    for (final AssertionSet assertionSet : policy) {
                        for (final PolicyAssertion assertion : assertionSet) {
                            if (assertion.getName().equals(addressingAssertionQName)) {
                                final WebServiceFeature feature = AddressingVersion.getFeature(addressingAssertionQName.getNamespaceURI(), true, !assertion.isOptional());
                                if (AddressingFeatureConfigurator.LOGGER.isLoggable(Level.FINE)) {
                                    AddressingFeatureConfigurator.LOGGER.fine("Added addressing feature \"" + feature + "\" for element \"" + key + "\"");
                                }
                                features.add(feature);
                            }
                        }
                    }
                }
            }
            if (policy != null && policy.contains(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
                for (final AssertionSet assertions2 : policy) {
                    for (final PolicyAssertion assertion2 : assertions2) {
                        if (assertion2.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
                            final NestedPolicy nestedPolicy = assertion2.getNestedPolicy();
                            boolean requiresAnonymousResponses = false;
                            boolean requiresNonAnonymousResponses = false;
                            if (nestedPolicy != null) {
                                requiresAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                                requiresNonAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
                            }
                            if (requiresAnonymousResponses && requiresNonAnonymousResponses) {
                                throw new WebServiceException("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
                            }
                            WebServiceFeature feature2;
                            try {
                                if (requiresAnonymousResponses) {
                                    feature2 = new AddressingFeature(true, !assertion2.isOptional(), AddressingFeature.Responses.ANONYMOUS);
                                }
                                else if (requiresNonAnonymousResponses) {
                                    feature2 = new AddressingFeature(true, !assertion2.isOptional(), AddressingFeature.Responses.NON_ANONYMOUS);
                                }
                                else {
                                    feature2 = new AddressingFeature(true, !assertion2.isOptional());
                                }
                            }
                            catch (final NoSuchMethodError e) {
                                throw AddressingFeatureConfigurator.LOGGER.logSevereException(new PolicyException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(AddressingFeature.class))), e));
                            }
                            if (AddressingFeatureConfigurator.LOGGER.isLoggable(Level.FINE)) {
                                AddressingFeatureConfigurator.LOGGER.fine("Added addressing feature \"" + feature2 + "\" for element \"" + key + "\"");
                            }
                            features.add(feature2);
                        }
                    }
                }
            }
        }
        AddressingFeatureConfigurator.LOGGER.exiting(features);
        return features;
    }
    
    private static String toJar(String url) {
        if (!url.startsWith("jar:")) {
            return url;
        }
        url = url.substring(4);
        return url.substring(0, url.lastIndexOf(33));
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(AddressingFeatureConfigurator.class);
        ADDRESSING_ASSERTIONS = new QName[] { new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing") };
    }
}
