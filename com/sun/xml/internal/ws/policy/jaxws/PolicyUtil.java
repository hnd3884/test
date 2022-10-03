package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.encoding.policy.SelectOptimalEncodingFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.FastInfosetFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.MtomFeatureConfigurator;
import com.sun.xml.internal.ws.addressing.policy.AddressingFeatureConfigurator;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.PolicyException;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import java.util.Iterator;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class PolicyUtil
{
    private static final PolicyLogger LOGGER;
    private static final Collection<PolicyFeatureConfigurator> CONFIGURATORS;
    
    public static <T> void addServiceProviders(final Collection<T> providers, final Class<T> service) {
        final Iterator<T> foundProviders = ServiceFinder.find(service).iterator();
        while (foundProviders.hasNext()) {
            providers.add(foundProviders.next());
        }
    }
    
    public static void configureModel(final WSDLModel model, final PolicyMap policyMap) throws PolicyException {
        PolicyUtil.LOGGER.entering(model, policyMap);
        for (final WSDLService service : model.getServices().values()) {
            for (final WSDLPort port : service.getPorts()) {
                final Collection<WebServiceFeature> features = getPortScopedFeatures(policyMap, service.getName(), port.getName());
                for (final WebServiceFeature feature : features) {
                    port.addFeature(feature);
                    port.getBinding().addFeature(feature);
                }
            }
        }
        PolicyUtil.LOGGER.exiting();
    }
    
    public static Collection<WebServiceFeature> getPortScopedFeatures(final PolicyMap policyMap, final QName serviceName, final QName portName) {
        PolicyUtil.LOGGER.entering(policyMap, serviceName, portName);
        final Collection<WebServiceFeature> features = new ArrayList<WebServiceFeature>();
        try {
            final PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(serviceName, portName);
            for (final PolicyFeatureConfigurator configurator : PolicyUtil.CONFIGURATORS) {
                final Collection<WebServiceFeature> additionalFeatures = configurator.getFeatures(key, policyMap);
                if (additionalFeatures != null) {
                    features.addAll(additionalFeatures);
                }
            }
        }
        catch (final PolicyException e) {
            throw new WebServiceException(e);
        }
        PolicyUtil.LOGGER.exiting(features);
        return features;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyUtil.class);
        (CONFIGURATORS = new LinkedList<PolicyFeatureConfigurator>()).add(new AddressingFeatureConfigurator());
        PolicyUtil.CONFIGURATORS.add(new MtomFeatureConfigurator());
        PolicyUtil.CONFIGURATORS.add(new FastInfosetFeatureConfigurator());
        PolicyUtil.CONFIGURATORS.add(new SelectOptimalEncodingFeatureConfigurator());
        addServiceProviders(PolicyUtil.CONFIGURATORS, PolicyFeatureConfigurator.class);
    }
}
