package org.owasp.esapi.reference.accesscontrol.policyloader;

import java.io.File;
import java.util.Collection;
import org.apache.commons.configuration.ConfigurationException;
import org.owasp.esapi.errors.AccessControlException;
import org.apache.commons.configuration.XMLConfiguration;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

public final class ACRPolicyFileLoader
{
    protected final Logger logger;
    
    public ACRPolicyFileLoader() {
        this.logger = ESAPI.getLogger("ACRPolicyFileLoader");
    }
    
    public PolicyDTO load() throws AccessControlException {
        final PolicyDTO policyDTO = new PolicyDTO();
        final File file = ESAPI.securityConfiguration().getResourceFile("ESAPI-AccessControlPolicy.xml");
        XMLConfiguration config;
        try {
            config = new XMLConfiguration(file);
        }
        catch (final ConfigurationException cex) {
            if (file == null) {
                throw new AccessControlException("Unable to load configuration file for the following: ESAPI-AccessControlPolicy.xml", "", (Throwable)cex);
            }
            throw new AccessControlException("Unable to load configuration file from the following location: " + file.getAbsolutePath(), "", (Throwable)cex);
        }
        final Object property = config.getProperty("AccessControlRules.AccessControlRule[@name]");
        this.logger.info(Logger.EVENT_SUCCESS, "Loading Property: " + property);
        int numberOfRules = 0;
        if (property instanceof Collection) {
            numberOfRules = ((Collection)property).size();
        }
        String ruleName = "";
        String ruleClass = "";
        Object rulePolicyParameter = null;
        int currentRule = 0;
        try {
            this.logger.info(Logger.EVENT_SUCCESS, "Number of rules: " + numberOfRules);
            for (currentRule = 0; currentRule < numberOfRules; ++currentRule) {
                this.logger.trace(Logger.EVENT_SUCCESS, "----");
                ruleName = config.getString("AccessControlRules.AccessControlRule(" + currentRule + ")[@name]");
                this.logger.trace(Logger.EVENT_SUCCESS, "Rule name: " + ruleName);
                ruleClass = config.getString("AccessControlRules.AccessControlRule(" + currentRule + ")[@class]");
                this.logger.trace(Logger.EVENT_SUCCESS, "Rule Class: " + ruleClass);
                rulePolicyParameter = this.getPolicyParameter(config, currentRule);
                this.logger.trace(Logger.EVENT_SUCCESS, "rulePolicyParameters: " + rulePolicyParameter);
                policyDTO.addAccessControlRule(ruleName, ruleClass, rulePolicyParameter);
            }
            this.logger.info(Logger.EVENT_SUCCESS, "policyDTO loaded: " + policyDTO);
        }
        catch (final Exception e) {
            throw new AccessControlException("Unable to load AccessControlRule parameter.  Rule number: " + currentRule + " Probably: Rule.name: " + ruleName + " Probably: Rule.class: " + ruleClass + e.getMessage(), "", e);
        }
        return policyDTO;
    }
    
    protected Object getPolicyParameter(final XMLConfiguration config, final int currentRule) throws ClassNotFoundException, IllegalAccessException, InstantiationException, Exception {
        final Object property = config.getProperty("AccessControlRules.AccessControlRule(" + currentRule + ").Parameters.Parameter[@name]");
        if (property == null) {
            return null;
        }
        int numberOfProperties = 0;
        if (property instanceof Collection) {
            numberOfProperties = ((Collection)property).size();
        }
        else {
            numberOfProperties = 1;
        }
        this.logger.info(Logger.EVENT_SUCCESS, "Number of properties: " + numberOfProperties);
        if (numberOfProperties < 1) {
            return null;
        }
        String parametersLoaderClassName = config.getString("AccessControlRules.AccessControlRule(" + currentRule + ").Parameters[@parametersLoader]");
        if ("".equals(parametersLoaderClassName) || parametersLoaderClassName == null) {
            parametersLoaderClassName = "org.owasp.esapi.reference.accesscontrol.policyloader.DynaBeanACRParameterLoader";
        }
        this.logger.info(Logger.EVENT_SUCCESS, "Parameters Loader:" + parametersLoaderClassName);
        final ACRParameterLoader acrParamaterLoader = (ACRParameterLoader)Class.forName(parametersLoaderClassName).newInstance();
        return acrParamaterLoader.getParameters(config, currentRule);
    }
}
