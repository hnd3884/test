package com.me.mdm.server.profiles.ios;

public class IOSConfigPayloadConstants
{
    public static final String PAYLOAD_CONTENT = "PayloadContent";
    public static final String HAS_REMOVAL_PASSCODE = "HasRemovalPasscode";
    public static final String IS_ENCRYPTED = "IsEncrypted";
    public static final String IS_MANAGED = "IsManaged";
    public static final String PAYLOAD_IDENTIFIER = "PayloadIdentifier";
    public static final String PAYLOAD_TYPE = "PayloadType";
    public static final String PAYLOAD_VERSION = "PayloadVersion";
    public static final String PAYLOAD_DESCRIPTION = "PayloadDescription";
    public static final String PAYLOAD_DISPLAY_NAME = "PayloadDisplayName";
    public static final String PAYLOAD_ORGANIZATION = "PayloadOrganization";
    public static final String PAYLOAD_REM_DISALLOWED = "PayloadRemovalDisallowed";
    public static final String PAYLOAD_UUID = "PayloadUUID";
    public static final String DEFAULT_PAYLOAD_NAME = "iOS Configuration Payload";
    public static final String DEFAULT_PAYLOAD_DESC = "No description available";
    public static final String DEFAULT_PAYLOAD_IDENTIFIER = "Unknown";
    public static final String DEFAULT_PAYLOAD_ORG = "Unknown";
    public static final String PAYLOAD_TYPE_CONFIGURATION = "Configuration";
    public static final String PAYLOAD_TYPE_UNKNOWN = "Unknown";
    public static final Integer DEFAULT_PAYLOAD_VERSION;
    public static final String UNKNOWN_UUID = "unknown-uuid";
    public static final Integer INSTALLED_SOURCE_MDM;
    public static final Integer INSTALLED_SOURCE_UNKNOWN;
    
    static {
        DEFAULT_PAYLOAD_VERSION = 1;
        INSTALLED_SOURCE_MDM = 1;
        INSTALLED_SOURCE_UNKNOWN = 0;
    }
}
