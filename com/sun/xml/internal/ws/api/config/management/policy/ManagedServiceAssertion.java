package com.sun.xml.internal.ws.api.config.management.policy;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.istack.internal.logging.Logger;
import javax.xml.namespace.QName;

public class ManagedServiceAssertion extends ManagementAssertion
{
    public static final QName MANAGED_SERVICE_QNAME;
    private static final QName COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME;
    private static final QName COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME;
    private static final QName CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME;
    private static final QName CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME;
    private static final QName CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME;
    private static final QName CLASS_NAME_ATTRIBUTE_QNAME;
    private static final QName ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME;
    private static final Logger LOGGER;
    
    public static ManagedServiceAssertion getAssertion(final WSEndpoint endpoint) throws WebServiceException {
        ManagedServiceAssertion.LOGGER.entering(endpoint);
        final PolicyMap policyMap = endpoint.getPolicyMap();
        final ManagedServiceAssertion assertion = ManagementAssertion.getAssertion(ManagedServiceAssertion.MANAGED_SERVICE_QNAME, policyMap, endpoint.getServiceName(), endpoint.getPortName(), ManagedServiceAssertion.class);
        ManagedServiceAssertion.LOGGER.exiting(assertion);
        return assertion;
    }
    
    public ManagedServiceAssertion(final AssertionData data, final Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
        super(ManagedServiceAssertion.MANAGED_SERVICE_QNAME, data, assertionParameters);
    }
    
    @Override
    public boolean isManagementEnabled() {
        final String management = this.getAttributeValue(ManagedServiceAssertion.MANAGEMENT_ATTRIBUTE_QNAME);
        boolean result = true;
        if (management != null) {
            result = (management.trim().toLowerCase().equals("on") || Boolean.parseBoolean(management));
        }
        return result;
    }
    
    public long getEndpointDisposeDelay(final long defaultDelay) throws WebServiceException {
        long result = defaultDelay;
        final String delayText = this.getAttributeValue(ManagedServiceAssertion.ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME);
        if (delayText != null) {
            try {
                result = Long.parseLong(delayText);
            }
            catch (final NumberFormatException e) {
                throw ManagedServiceAssertion.LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(delayText), e));
            }
        }
        return result;
    }
    
    public Collection<ImplementationRecord> getCommunicationServerImplementations() {
        final Collection<ImplementationRecord> result = new LinkedList<ImplementationRecord>();
        final Iterator<PolicyAssertion> parameters = this.getParametersIterator();
        while (parameters.hasNext()) {
            final PolicyAssertion parameter = parameters.next();
            if (ManagedServiceAssertion.COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME.equals(parameter.getName())) {
                final Iterator<PolicyAssertion> implementations = parameter.getParametersIterator();
                if (!implementations.hasNext()) {
                    throw ManagedServiceAssertion.LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1005_EXPECTED_COMMUNICATION_CHILD()));
                }
                while (implementations.hasNext()) {
                    final PolicyAssertion implementation = implementations.next();
                    if (!ManagedServiceAssertion.COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME.equals(implementation.getName())) {
                        throw ManagedServiceAssertion.LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1004_EXPECTED_XML_TAG(ManagedServiceAssertion.COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME, implementation.getName())));
                    }
                    result.add(this.getImplementation(implementation));
                }
            }
        }
        return result;
    }
    
    public ImplementationRecord getConfiguratorImplementation() {
        return this.findImplementation(ManagedServiceAssertion.CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME);
    }
    
    public ImplementationRecord getConfigSaverImplementation() {
        return this.findImplementation(ManagedServiceAssertion.CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME);
    }
    
    public ImplementationRecord getConfigReaderImplementation() {
        return this.findImplementation(ManagedServiceAssertion.CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME);
    }
    
    private ImplementationRecord findImplementation(final QName implementationName) {
        final Iterator<PolicyAssertion> parameters = this.getParametersIterator();
        while (parameters.hasNext()) {
            final PolicyAssertion parameter = parameters.next();
            if (implementationName.equals(parameter.getName())) {
                return this.getImplementation(parameter);
            }
        }
        return null;
    }
    
    private ImplementationRecord getImplementation(final PolicyAssertion rootParameter) {
        final String className = rootParameter.getAttributeValue(ManagedServiceAssertion.CLASS_NAME_ATTRIBUTE_QNAME);
        final HashMap<QName, String> parameterMap = new HashMap<QName, String>();
        final Iterator<PolicyAssertion> implementationParameters = rootParameter.getParametersIterator();
        final Collection<NestedParameters> nestedParameters = new LinkedList<NestedParameters>();
        while (implementationParameters.hasNext()) {
            final PolicyAssertion parameterAssertion = implementationParameters.next();
            final QName parameterName = parameterAssertion.getName();
            if (parameterAssertion.hasParameters()) {
                final Map<QName, String> nestedParameterMap = new HashMap<QName, String>();
                final Iterator<PolicyAssertion> parameters = parameterAssertion.getParametersIterator();
                while (parameters.hasNext()) {
                    final PolicyAssertion parameter = parameters.next();
                    String value = parameter.getValue();
                    if (value != null) {
                        value = value.trim();
                    }
                    nestedParameterMap.put(parameter.getName(), value);
                }
                nestedParameters.add(new NestedParameters(parameterName, (Map)nestedParameterMap));
            }
            else {
                String value2 = parameterAssertion.getValue();
                if (value2 != null) {
                    value2 = value2.trim();
                }
                parameterMap.put(parameterName, value2);
            }
        }
        return new ImplementationRecord(className, parameterMap, nestedParameters);
    }
    
    static {
        MANAGED_SERVICE_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedService");
        COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementations");
        COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementation");
        CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfiguratorImplementation");
        CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigSaverImplementation");
        CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigReaderImplementation");
        CLASS_NAME_ATTRIBUTE_QNAME = new QName("className");
        ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME = new QName("endpointDisposeDelay");
        LOGGER = Logger.getLogger(ManagedServiceAssertion.class);
    }
    
    public static class ImplementationRecord
    {
        private final String implementation;
        private final Map<QName, String> parameters;
        private final Collection<NestedParameters> nestedParameters;
        
        protected ImplementationRecord(final String implementation, final Map<QName, String> parameters, final Collection<NestedParameters> nestedParameters) {
            this.implementation = implementation;
            this.parameters = parameters;
            this.nestedParameters = nestedParameters;
        }
        
        public String getImplementation() {
            return this.implementation;
        }
        
        public Map<QName, String> getParameters() {
            return this.parameters;
        }
        
        public Collection<NestedParameters> getNestedParameters() {
            return this.nestedParameters;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ImplementationRecord other = (ImplementationRecord)obj;
            if (this.implementation == null) {
                if (other.implementation == null) {
                    return (this.parameters == other.parameters || (this.parameters != null && this.parameters.equals(other.parameters))) && (this.nestedParameters == other.nestedParameters || (this.nestedParameters != null && this.nestedParameters.equals(other.nestedParameters)));
                }
            }
            else if (this.implementation.equals(other.implementation)) {
                return (this.parameters == other.parameters || (this.parameters != null && this.parameters.equals(other.parameters))) && (this.nestedParameters == other.nestedParameters || (this.nestedParameters != null && this.nestedParameters.equals(other.nestedParameters)));
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + ((this.implementation != null) ? this.implementation.hashCode() : 0);
            hash = 53 * hash + ((this.parameters != null) ? this.parameters.hashCode() : 0);
            hash = 53 * hash + ((this.nestedParameters != null) ? this.nestedParameters.hashCode() : 0);
            return hash;
        }
        
        @Override
        public String toString() {
            final StringBuilder text = new StringBuilder("ImplementationRecord: ");
            text.append("implementation = \"").append(this.implementation).append("\", ");
            text.append("parameters = \"").append(this.parameters).append("\", ");
            text.append("nested parameters = \"").append(this.nestedParameters).append("\"");
            return text.toString();
        }
    }
    
    public static class NestedParameters
    {
        private final QName name;
        private final Map<QName, String> parameters;
        
        private NestedParameters(final QName name, final Map<QName, String> parameters) {
            this.name = name;
            this.parameters = parameters;
        }
        
        public QName getName() {
            return this.name;
        }
        
        public Map<QName, String> getParameters() {
            return this.parameters;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final NestedParameters other = (NestedParameters)obj;
            if (this.name == null) {
                if (other.name == null) {
                    return this.parameters == other.parameters || (this.parameters != null && this.parameters.equals(other.parameters));
                }
            }
            else if (this.name.equals(other.name)) {
                return this.parameters == other.parameters || (this.parameters != null && this.parameters.equals(other.parameters));
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + ((this.name != null) ? this.name.hashCode() : 0);
            hash = 59 * hash + ((this.parameters != null) ? this.parameters.hashCode() : 0);
            return hash;
        }
        
        @Override
        public String toString() {
            final StringBuilder text = new StringBuilder("NestedParameters: ");
            text.append("name = \"").append(this.name).append("\", ");
            text.append("parameters = \"").append(this.parameters).append("\"");
            return text.toString();
        }
    }
}
