package com.adventnet.authentication.saml;

public class SamlException extends Exception
{
    public static final int SETTINGS_INVALID_SYNTAX = 1;
    public static final int SETTINGS_INVALID = 2;
    public static final int SETTINGS_FILE_NOT_FOUND = 3;
    public static final int CERT_NOT_FOUND = 4;
    public static final int PRIVATE_KEY_NOT_FOUND = 4;
    public static final int PUBLIC_CERT_FILE_NOT_FOUND = 5;
    public static final int PRIVATE_KEY_FILE_NOT_FOUND = 6;
    public static final int METADATA_SP_INVALID = 7;
    public static final int SAML_RESPONSE_NOT_FOUND = 8;
    public static final int SAML_LOGOUTMESSAGE_NOT_FOUND = 9;
    public static final int SAML_LOGOUTREQUEST_INVALID = 10;
    public static final int SAML_LOGOUTRESPONSE_INVALID = 11;
    public static final int SAML_SINGLE_LOGOUT_NOT_SUPPORTED = 12;
    public static final int INVALID_XML_FORMAT = 13;
    public static final int KEYINFO_NOT_FOUND_IN_ENCRYPTED_DATA = 14;
    public static final int UNSUPPORTED_RETRIEVAL_METHOD = 15;
    public static final int UNSUPPORTED_SAML_VERSION = 16;
    public static final int MISSING_ID = 17;
    public static final int MISSING_STATUS = 18;
    public static final int MISSING_STATUS_CODE = 19;
    public static final int EMPTY_STATUS = 20;
    public static final int FAILED_RESPONDER = 21;
    public static final int FAILED_REQUESTER = 22;
    public static final int STATUS_CODE_IS_NOT_SUCCESS = 23;
    public static final int WRONG_SIGNED_ELEMENT = 24;
    public static final int ID_NOT_FOUND_IN_SIGNED_ELEMENT = 25;
    public static final int DUPLICATED_ID_IN_SIGNED_ELEMENTS = 26;
    public static final int INVALID_SIGNED_ELEMENT = 27;
    public static final int DUPLICATED_REFERENCE_IN_SIGNED_ELEMENTS = 28;
    public static final int UNEXPECTED_SIGNED_ELEMENTS = 29;
    public static final int UNEXPECTED_REFERENCE = 30;
    public static final int WRONG_NUMBER_OF_SIGNATURES_IN_RESPONSE = 31;
    public static final int WRONG_NUMBER_OF_SIGNATURES_IN_ASSERTION = 32;
    public static final int NO_SIGNED_MESSAGE = 33;
    public static final int NO_SIGNED_ASSERTION = 34;
    public static final int NO_SIGNATURE_FOUND = 35;
    public static final int INVALID_SIGNATURE = 36;
    public static final int DUPLICATED_ATTRIBUTE_NAME_FOUND = 37;
    public static final int NO_NAMEID = 38;
    public static final int EMPTY_NAMEID = 39;
    public static final int SP_NAME_QUALIFIER_NAME_MISMATCH = 40;
    public static final int SESSION_EXPIRED = 41;
    public static final int WRONG_DESTINATION = 42;
    public static final int EMPTY_DESTINATION = 43;
    public static final int WRONG_ISSUER = 44;
    public static final int NO_ENCRYPTED_NAMEID = 45;
    public static final int ASSERTION_TOO_EARLY = 46;
    public static final int ASSERTION_EXPIRED = 47;
    public static final int ISSUER_MULTIPLE_IN_RESPONSE = 48;
    public static final int ISSUER_NOT_FOUND_IN_ASSERTION = 49;
    public static final int WRONG_INRESPONSETO = 50;
    public static final int REQUEST_EXPIRED = 51;
    private int errorCode;
    
    public SamlException(final String message, final int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public SamlException(final String message) {
        super(message);
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
}
