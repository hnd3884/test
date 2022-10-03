package com.sun.xml.internal.ws.addressing.policy;

import java.util.Collections;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.logging.Level;
import javax.xml.ws.soap.AddressingFeature;
import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.PolicySubject;
import java.util.Collection;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;

public class AddressingPolicyMapConfigurator implements PolicyMapConfigurator
{
    private static final PolicyLogger LOGGER;
    
    @Override
    public Collection<PolicySubject> update(final PolicyMap policyMap, final SEIModel model, final WSBinding wsBinding) throws PolicyException {
        AddressingPolicyMapConfigurator.LOGGER.entering(policyMap, model, wsBinding);
        final Collection<PolicySubject> subjects = new ArrayList<PolicySubject>();
        if (policyMap != null) {
            final AddressingFeature addressingFeature = wsBinding.getFeature(AddressingFeature.class);
            if (AddressingPolicyMapConfigurator.LOGGER.isLoggable(Level.FINEST)) {
                AddressingPolicyMapConfigurator.LOGGER.finest("addressingFeature = " + addressingFeature);
            }
            if (addressingFeature != null && addressingFeature.isEnabled()) {
                this.addWsamAddressing(subjects, policyMap, model, addressingFeature);
            }
        }
        AddressingPolicyMapConfigurator.LOGGER.exiting(subjects);
        return subjects;
    }
    
    private void addWsamAddressing(final Collection<PolicySubject> subjects, final PolicyMap policyMap, final SEIModel model, final AddressingFeature addressingFeature) throws PolicyException {
        final QName bindingName = model.getBoundPortTypeName();
        final WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(bindingName);
        final Policy addressingPolicy = this.createWsamAddressingPolicy(bindingName, addressingFeature);
        final PolicySubject addressingPolicySubject = new PolicySubject(wsdlSubject, addressingPolicy);
        subjects.add(addressingPolicySubject);
        if (AddressingPolicyMapConfigurator.LOGGER.isLoggable(Level.FINE)) {
            AddressingPolicyMapConfigurator.LOGGER.fine("Added addressing policy with ID \"" + addressingPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
        }
    }
    
    private Policy createWsamAddressingPolicy(final QName bindingName, final AddressingFeature af) {
        final ArrayList<AssertionSet> assertionSets = new ArrayList<AssertionSet>(1);
        final ArrayList<PolicyAssertion> assertions = new ArrayList<PolicyAssertion>(1);
        final AssertionData addressingData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
        if (!af.isRequired()) {
            addressingData.setOptionalAttribute(true);
        }
        try {
            final AddressingFeature.Responses responses = af.getResponses();
            if (responses == AddressingFeature.Responses.ANONYMOUS) {
                final AssertionData nestedAsserData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                final PolicyAssertion nestedAsser = new AddressingAssertion(nestedAsserData, (AssertionSet)null);
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
            }
            else if (responses == AddressingFeature.Responses.NON_ANONYMOUS) {
                final AssertionData nestedAsserData = AssertionData.createAssertionData(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
                final PolicyAssertion nestedAsser = new AddressingAssertion(nestedAsserData, (AssertionSet)null);
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
            }
            else {
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(null)));
            }
        }
        catch (final NoSuchMethodError e) {
            assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(null)));
        }
        assertionSets.add(AssertionSet.createAssertionSet(assertions));
        return Policy.createPolicy(null, bindingName.getLocalPart() + "_WSAM_Addressing_Policy", assertionSets);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(AddressingPolicyMapConfigurator.class);
    }
    
    private static final class AddressingAssertion extends PolicyAssertion
    {
        AddressingAssertion(final AssertionData assertionData, final AssertionSet nestedAlternative) {
            super(assertionData, null, nestedAlternative);
        }
        
        AddressingAssertion(final AssertionData assertionData) {
            super(assertionData, null, null);
        }
    }
}
