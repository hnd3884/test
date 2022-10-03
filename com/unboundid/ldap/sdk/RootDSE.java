package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class RootDSE extends ReadOnlyEntry
{
    public static final String ATTR_ALT_SERVER = "altServer";
    public static final String ATTR_CHANGELOG_DN = "changelog";
    public static final String ATTR_FIRST_CHANGE_NUMBER = "firstChangeNumber";
    public static final String ATTR_LAST_CHANGE_NUMBER = "lastChangeNumber";
    public static final String ATTR_LAST_PURGED_CHANGE_NUMBER = "lastPurgedChangeNumber";
    public static final String ATTR_NAMING_CONTEXT = "namingContexts";
    public static final String ATTR_SUBSCHEMA_SUBENTRY = "subschemaSubentry";
    public static final String ATTR_SUPPORTED_AUTH_PASSWORD_STORAGE_SCHEME = "supportedAuthPasswordSchemes";
    public static final String ATTR_SUPPORTED_CONTROL = "supportedControl";
    public static final String ATTR_SUPPORTED_EXTENDED_OPERATION = "supportedExtension";
    public static final String ATTR_SUPPORTED_FEATURE = "supportedFeatures";
    public static final String ATTR_SUPPORTED_LDAP_VERSION = "supportedLDAPVersion";
    public static final String ATTR_SUPPORTED_SASL_MECHANISM = "supportedSASLMechanisms";
    public static final String ATTR_VENDOR_NAME = "vendorName";
    public static final String ATTR_VENDOR_VERSION = "vendorVersion";
    protected static final String[] REQUEST_ATTRS;
    private static final long serialVersionUID = -1678182563511570981L;
    
    public RootDSE(final Entry rootDSEEntry) {
        super(rootDSEEntry);
    }
    
    public static RootDSE getRootDSE(final LDAPInterface connection) throws LDAPException {
        final Entry rootDSEEntry = connection.getEntry("", RootDSE.REQUEST_ATTRS);
        if (rootDSEEntry == null) {
            return null;
        }
        return new RootDSE(rootDSEEntry);
    }
    
    public final String[] getAltServerURIs() {
        return this.getAttributeValues("altServer");
    }
    
    public final String getChangelogDN() {
        return this.getAttributeValue("changelog");
    }
    
    public final Long getFirstChangeNumber() {
        return this.getAttributeValueAsLong("firstChangeNumber");
    }
    
    public final Long getLastChangeNumber() {
        return this.getAttributeValueAsLong("lastChangeNumber");
    }
    
    public final Long getLastPurgedChangeNumber() {
        return this.getAttributeValueAsLong("lastPurgedChangeNumber");
    }
    
    public final String[] getNamingContextDNs() {
        return this.getAttributeValues("namingContexts");
    }
    
    public final String getSubschemaSubentryDN() {
        return this.getAttributeValue("subschemaSubentry");
    }
    
    public final String[] getSupportedAuthPasswordSchemeNames() {
        return this.getAttributeValues("supportedAuthPasswordSchemes");
    }
    
    public final boolean supportsAuthPasswordScheme(final String scheme) {
        return this.hasAttributeValue("supportedAuthPasswordSchemes", scheme);
    }
    
    public final String[] getSupportedControlOIDs() {
        return this.getAttributeValues("supportedControl");
    }
    
    public final boolean supportsControl(final String controlOID) {
        return this.hasAttributeValue("supportedControl", controlOID);
    }
    
    public final String[] getSupportedExtendedOperationOIDs() {
        return this.getAttributeValues("supportedExtension");
    }
    
    public final boolean supportsExtendedOperation(final String extendedOperationOID) {
        return this.hasAttributeValue("supportedExtension", extendedOperationOID);
    }
    
    public final String[] getSupportedFeatureOIDs() {
        return this.getAttributeValues("supportedFeatures");
    }
    
    public final boolean supportsFeature(final String featureOID) {
        return this.hasAttributeValue("supportedFeatures", featureOID);
    }
    
    public final int[] getSupportedLDAPVersions() {
        final String[] versionStrs = this.getAttributeValues("supportedLDAPVersion");
        if (versionStrs == null) {
            return null;
        }
        final int[] versions = new int[versionStrs.length];
        for (int i = 0; i < versionStrs.length; ++i) {
            try {
                versions[i] = Integer.parseInt(versionStrs[i]);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return null;
            }
        }
        return versions;
    }
    
    public final boolean supportsLDAPVersion(final int ldapVersion) {
        return this.hasAttributeValue("supportedLDAPVersion", String.valueOf(ldapVersion));
    }
    
    public final String[] getSupportedSASLMechanismNames() {
        return this.getAttributeValues("supportedSASLMechanisms");
    }
    
    public final boolean supportsSASLMechanism(final String mechanismName) {
        return this.hasAttributeValue("supportedSASLMechanisms", mechanismName);
    }
    
    public final String getVendorName() {
        return this.getAttributeValue("vendorName");
    }
    
    public final String getVendorVersion() {
        return this.getAttributeValue("vendorVersion");
    }
    
    static {
        REQUEST_ATTRS = new String[] { "*", "+", "altServer", "changelog", "firstChangeNumber", "lastChangeNumber", "lastPurgedChangeNumber", "namingContexts", "subschemaSubentry", "supportedAuthPasswordSchemes", "supportedControl", "supportedExtension", "supportedFeatures", "supportedLDAPVersion", "supportedSASLMechanisms", "vendorName", "vendorVersion" };
    }
}
