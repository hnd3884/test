package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.istack.internal.logging.Logger;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.SimpleAssertion;

public abstract class ManagementAssertion extends SimpleAssertion
{
    protected static final QName MANAGEMENT_ATTRIBUTE_QNAME;
    protected static final QName MONITORING_ATTRIBUTE_QNAME;
    private static final QName ID_ATTRIBUTE_QNAME;
    private static final QName START_ATTRIBUTE_QNAME;
    private static final Logger LOGGER;
    
    protected static <T extends ManagementAssertion> T getAssertion(final QName name, final PolicyMap policyMap, final QName serviceName, final QName portName, final Class<T> type) throws WebServiceException {
        try {
            PolicyAssertion assertion = null;
            if (policyMap != null) {
                final PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(serviceName, portName);
                final Policy policy = policyMap.getEndpointEffectivePolicy(key);
                if (policy != null) {
                    final Iterator<AssertionSet> assertionSets = policy.iterator();
                    if (assertionSets.hasNext()) {
                        final AssertionSet assertionSet = assertionSets.next();
                        final Iterator<PolicyAssertion> assertions = assertionSet.get(name).iterator();
                        if (assertions.hasNext()) {
                            assertion = assertions.next();
                        }
                    }
                }
            }
            return (T)((assertion == null) ? null : ((T)assertion.getImplementation(type)));
        }
        catch (final PolicyException ex) {
            throw ManagementAssertion.LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1001_FAILED_ASSERTION(name), ex));
        }
    }
    
    protected ManagementAssertion(final QName name, final AssertionData data, final Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
        super(data, assertionParameters);
        if (!name.equals(data.getName())) {
            throw ManagementAssertion.LOGGER.logSevereException(new AssertionCreationException(data, ManagementMessages.WSM_1002_EXPECTED_MANAGEMENT_ASSERTION(name)));
        }
        if (this.isManagementEnabled() && !data.containsAttribute(ManagementAssertion.ID_ATTRIBUTE_QNAME)) {
            throw ManagementAssertion.LOGGER.logSevereException(new AssertionCreationException(data, ManagementMessages.WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(name)));
        }
    }
    
    public String getId() {
        return this.getAttributeValue(ManagementAssertion.ID_ATTRIBUTE_QNAME);
    }
    
    public String getStart() {
        return this.getAttributeValue(ManagementAssertion.START_ATTRIBUTE_QNAME);
    }
    
    public abstract boolean isManagementEnabled();
    
    public Setting monitoringAttribute() {
        final String monitoring = this.getAttributeValue(ManagementAssertion.MONITORING_ATTRIBUTE_QNAME);
        Setting result = Setting.NOT_SET;
        if (monitoring != null) {
            if (monitoring.trim().toLowerCase().equals("on") || Boolean.parseBoolean(monitoring)) {
                result = Setting.ON;
            }
            else {
                result = Setting.OFF;
            }
        }
        return result;
    }
    
    static {
        MANAGEMENT_ATTRIBUTE_QNAME = new QName("management");
        MONITORING_ATTRIBUTE_QNAME = new QName("monitoring");
        ID_ATTRIBUTE_QNAME = new QName("id");
        START_ATTRIBUTE_QNAME = new QName("start");
        LOGGER = Logger.getLogger(ManagementAssertion.class);
    }
    
    public enum Setting
    {
        NOT_SET, 
        OFF, 
        ON;
    }
}
