package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.Policy;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.logging.Level;
import javax.xml.ws.soap.MTOMFeature;
import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.PolicySubject;
import java.util.Collection;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;

public class MtomPolicyMapConfigurator implements PolicyMapConfigurator
{
    private static final PolicyLogger LOGGER;
    
    @Override
    public Collection<PolicySubject> update(final PolicyMap policyMap, final SEIModel model, final WSBinding wsBinding) throws PolicyException {
        MtomPolicyMapConfigurator.LOGGER.entering(policyMap, model, wsBinding);
        final Collection<PolicySubject> subjects = new ArrayList<PolicySubject>();
        if (policyMap != null) {
            final MTOMFeature mtomFeature = wsBinding.getFeature(MTOMFeature.class);
            if (MtomPolicyMapConfigurator.LOGGER.isLoggable(Level.FINEST)) {
                MtomPolicyMapConfigurator.LOGGER.finest("mtomFeature = " + mtomFeature);
            }
            if (mtomFeature != null && mtomFeature.isEnabled()) {
                final QName bindingName = model.getBoundPortTypeName();
                final WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(bindingName);
                final Policy mtomPolicy = this.createMtomPolicy(bindingName);
                final PolicySubject mtomPolicySubject = new PolicySubject(wsdlSubject, mtomPolicy);
                subjects.add(mtomPolicySubject);
                if (MtomPolicyMapConfigurator.LOGGER.isLoggable(Level.FINEST)) {
                    MtomPolicyMapConfigurator.LOGGER.fine("Added MTOM policy with ID \"" + mtomPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
                }
            }
        }
        MtomPolicyMapConfigurator.LOGGER.exiting(subjects);
        return subjects;
    }
    
    private Policy createMtomPolicy(final QName bindingName) {
        final ArrayList<AssertionSet> assertionSets = new ArrayList<AssertionSet>(1);
        final ArrayList<PolicyAssertion> assertions = new ArrayList<PolicyAssertion>(1);
        assertions.add(new MtomAssertion());
        assertionSets.add(AssertionSet.createAssertionSet(assertions));
        return Policy.createPolicy(null, bindingName.getLocalPart() + "_MTOM_Policy", assertionSets);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(MtomPolicyMapConfigurator.class);
    }
    
    static class MtomAssertion extends PolicyAssertion
    {
        private static final AssertionData mtomData;
        
        MtomAssertion() {
            super(MtomAssertion.mtomData, null, null);
        }
        
        static {
            (mtomData = AssertionData.createAssertionData(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION)).setOptionalAttribute(true);
        }
    }
}
