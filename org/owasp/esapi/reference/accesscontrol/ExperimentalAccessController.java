package org.owasp.esapi.reference.accesscontrol;

import org.owasp.esapi.AccessControlRule;
import org.owasp.esapi.errors.AccessControlException;
import org.owasp.esapi.reference.accesscontrol.policyloader.PolicyDTO;
import org.owasp.esapi.reference.accesscontrol.policyloader.ACRPolicyFileLoader;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import java.util.Map;
import org.owasp.esapi.AccessController;

public class ExperimentalAccessController implements AccessController
{
    private Map ruleMap;
    protected final Logger logger;
    
    public ExperimentalAccessController(final Map ruleMap) {
        this.logger = ESAPI.getLogger("DefaultAccessController");
        this.ruleMap = ruleMap;
    }
    
    public ExperimentalAccessController() throws AccessControlException {
        this.logger = ESAPI.getLogger("DefaultAccessController");
        final ACRPolicyFileLoader policyDescriptor = new ACRPolicyFileLoader();
        final PolicyDTO policyDTO = policyDescriptor.load();
        this.ruleMap = policyDTO.getAccessControlRules();
    }
    
    @Override
    public boolean isAuthorized(final Object key, final Object runtimeParameter) {
        try {
            final AccessControlRule rule = this.ruleMap.get(key);
            if (rule == null) {
                throw new AccessControlException("Access Denied", "AccessControlRule was not found for key: " + key);
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(Logger.EVENT_SUCCESS, "Evaluating Authorization Rule \"" + key + "\" Using class: " + rule.getClass().getCanonicalName());
            }
            return rule.isAuthorized(runtimeParameter);
        }
        catch (final Exception e) {
            try {
                throw new AccessControlException("Access Denied", "An unhandled Exception was caught, so access is denied.", e);
            }
            catch (final AccessControlException ace) {
                return false;
            }
        }
    }
    
    @Override
    public void assertAuthorized(final Object key, final Object runtimeParameter) throws AccessControlException {
        boolean isAuthorized = false;
        try {
            final AccessControlRule rule = this.ruleMap.get(key);
            if (rule == null) {
                throw new AccessControlException("Access Denied", "AccessControlRule was not found for key: " + key);
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(Logger.EVENT_SUCCESS, "Asserting Authorization Rule \"" + key + "\" Using class: " + rule.getClass().getCanonicalName());
            }
            isAuthorized = rule.isAuthorized(runtimeParameter);
        }
        catch (final Exception e) {
            throw new AccessControlException("Access Denied", "An unhandled Exception was caught, so access is denied.AccessControlException.", e);
        }
        if (!isAuthorized) {
            throw new AccessControlException("Access Denied", "Access Denied for key: " + key + " runtimeParameter: " + runtimeParameter);
        }
    }
    
    @Override
    @Deprecated
    public void assertAuthorizedForData(final String action, final Object data) throws AccessControlException {
        this.assertAuthorized("AC 1.0 Data", new Object[] { action, data });
    }
    
    @Override
    @Deprecated
    public void assertAuthorizedForFile(final String filepath) throws AccessControlException {
        this.assertAuthorized("AC 1.0 File", new Object[] { filepath });
    }
    
    @Override
    @Deprecated
    public void assertAuthorizedForFunction(final String functionName) throws AccessControlException {
        this.assertAuthorized("AC 1.0 Function", new Object[] { functionName });
    }
    
    @Override
    @Deprecated
    public void assertAuthorizedForService(final String serviceName) throws AccessControlException {
        this.assertAuthorized("AC 1.0 Service", new Object[] { serviceName });
    }
    
    @Override
    @Deprecated
    public void assertAuthorizedForURL(final String url) throws AccessControlException {
        this.assertAuthorized("AC 1.0 URL", new Object[] { url });
    }
    
    @Override
    @Deprecated
    public boolean isAuthorizedForData(final String action, final Object data) {
        return this.isAuthorized("AC 1.0 Data", new Object[] { action, data });
    }
    
    @Override
    @Deprecated
    public boolean isAuthorizedForFile(final String filepath) {
        return this.isAuthorized("AC 1.0 File", new Object[] { filepath });
    }
    
    @Override
    @Deprecated
    public boolean isAuthorizedForFunction(final String functionName) {
        return this.isAuthorized("AC 1.0 Function", new Object[] { functionName });
    }
    
    @Override
    @Deprecated
    public boolean isAuthorizedForService(final String serviceName) {
        return this.isAuthorized("AC 1.0 Service", new Object[] { serviceName });
    }
    
    @Override
    @Deprecated
    public boolean isAuthorizedForURL(final String url) {
        return this.isAuthorized("AC 1.0 URL", new Object[] { url });
    }
}
