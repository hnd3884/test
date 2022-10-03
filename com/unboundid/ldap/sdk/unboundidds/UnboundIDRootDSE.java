package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.RootDSE;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UnboundIDRootDSE extends RootDSE
{
    public static final String ATTR_BASELINE_CONFIG_DIGEST = "baselineConfigurationDigest";
    public static final String ATTR_CONFIG_MODEL_DIGEST = "configurationModelDigest";
    public static final String ATTR_INSTANCE_NAME = "ds-instance-name";
    public static final String ATTR_PRIVATE_NAMING_CONTEXTS = "ds-private-naming-contexts";
    public static final String ATTR_STARTUP_UUID = "startupUUID";
    public static final String ATTR_SUPPORTED_OTP_DELIVERY_MECHANISM = "ds-supported-otp-delivery-mechanism";
    private static final String[] REQUEST_ATTRS;
    private static final long serialVersionUID = 2555047334281707615L;
    
    public UnboundIDRootDSE(final Entry rootDSEEntry) {
        super(rootDSEEntry);
    }
    
    public static UnboundIDRootDSE getRootDSE(final LDAPInterface connection) throws LDAPException {
        final Entry rootDSEEntry = connection.getEntry("", UnboundIDRootDSE.REQUEST_ATTRS);
        if (rootDSEEntry == null) {
            return null;
        }
        return new UnboundIDRootDSE(rootDSEEntry);
    }
    
    public String getBaselineConfigurationDigest() {
        return this.getAttributeValue("baselineConfigurationDigest");
    }
    
    public String getConfigurationModelDigest() {
        return this.getAttributeValue("configurationModelDigest");
    }
    
    public String getInstanceName() {
        return this.getAttributeValue("ds-instance-name");
    }
    
    public String[] getPrivateNamingContexts() {
        return this.getAttributeValues("ds-private-naming-contexts");
    }
    
    public String getStartupUUID() {
        return this.getAttributeValue("startupUUID");
    }
    
    public String[] getSupportedOTPDeliveryMechanisms() {
        return this.getAttributeValues("ds-supported-otp-delivery-mechanism");
    }
    
    public boolean supportsOTPDeliveryMechanism(final String mechanismName) {
        return this.hasAttributeValue("ds-supported-otp-delivery-mechanism", mechanismName);
    }
    
    static {
        final String[] superAttrs = RootDSE.REQUEST_ATTRS;
        System.arraycopy(superAttrs, 0, REQUEST_ATTRS = new String[superAttrs.length + 6], 0, superAttrs.length);
        int i = superAttrs.length;
        UnboundIDRootDSE.REQUEST_ATTRS[i++] = "baselineConfigurationDigest";
        UnboundIDRootDSE.REQUEST_ATTRS[i++] = "configurationModelDigest";
        UnboundIDRootDSE.REQUEST_ATTRS[i++] = "ds-instance-name";
        UnboundIDRootDSE.REQUEST_ATTRS[i++] = "ds-private-naming-contexts";
        UnboundIDRootDSE.REQUEST_ATTRS[i++] = "startupUUID";
        UnboundIDRootDSE.REQUEST_ATTRS[i++] = "ds-supported-otp-delivery-mechanism";
    }
}
