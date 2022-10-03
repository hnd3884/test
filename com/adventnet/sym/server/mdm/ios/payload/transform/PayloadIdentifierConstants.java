package com.adventnet.sym.server.mdm.ios.payload.transform;

public final class PayloadIdentifierConstants
{
    public static String MDM_DEP_TRUST_ROOT_CERTIFICATE_IDENTIFIER;
    public static String MDM_DEP_TRUST_PROFILE_IDENTIFIER;
    public static String MDM_INSTALATION_PROFILE_IDENTIFIER;
    public static String MDM_INSTALATION_PROFILE_ROOT_CERTIFICATE_IDENTIFIER;
    public static String MDM_INSTALATION_PROFILE_APNS_CERTIFICATE_IDENTIFIER;
    public static String MDM_INSTALATION_PROFILE_CONFIGURATION_IDENTIFIER;
    
    static {
        PayloadIdentifierConstants.MDM_DEP_TRUST_ROOT_CERTIFICATE_IDENTIFIER = "com.zohocorp.mdm.trustrootcert";
        PayloadIdentifierConstants.MDM_DEP_TRUST_PROFILE_IDENTIFIER = "com.zohocorp.mdm.trustrootcert";
        PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_IDENTIFIER = "com.zohocorp.mdm";
        PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_ROOT_CERTIFICATE_IDENTIFIER = "com.zohocorp.mdm.credential2";
        PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_APNS_CERTIFICATE_IDENTIFIER = "com.zohocorp.mdm.credential1";
        PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_CONFIGURATION_IDENTIFIER = "com.zohocorp.mdm.mdm1";
    }
}
