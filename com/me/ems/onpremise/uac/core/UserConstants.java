package com.me.ems.onpremise.uac.core;

import com.me.devicemanagement.framework.server.authentication.DCUserConstants;

public class UserConstants extends DCUserConstants
{
    public static final int COMPUTER_SCOPE_ALL = 0;
    public static final int COMPUTER_SCOPE_GROUP = 1;
    public static final int COMPUTER_SCOPE_RO = 2;
    public static final int DEVICE_SCOPE_ALL = 0;
    public static final int DEVICE_SCOPE_GROUP = 1;
    public static final int PROBE_SCOPE_NONE = 0;
    public static final int PROBE_SCOPE_ALL = 1;
    public static final int PROBE_SCOPE_SPECIFIC = 2;
    public static final String MD5_ALGORITHM = "MD5";
    public static final String BCRYPT_ALGORITHM = "bcrypt";
    public static final String LOCAL_AUTH = "localAuthentication";
    public static final String AD_AUTH = "adAuthentication";
    public static final String DEFAULT_LOCALE = "en_US";
    public static final String DOMAIN_NAME = "domainName";
    public static final String PREFIX = "prefix";
    public static final String LOGIN_ID = "loginId";
    public static final String LOGIN_NAME = "loginName";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String LOCAL = "local";
    public static final String ROLE_ID = "roleId";
    public static final String PASSWORD = "password";
    public static final String MAIL_ID = "mailId";
    public static final String MAIL_SENT = "mailSent";
    public static final String NEW_USER = "newUser";
    public static final String IS_EXISTING_ADMIN = "isExistingAdmin";
    public static final int TOKEN_CREATE_USER = 101;
    public static final int TOKEN_FORGET_PASSWORD = 102;
    public static final int USER_STATUS_ACTIVE = 0;
    public static final int USER_STATUS_INACTIVE = 1;
    public static final int USER_ACTIVED_SUCCESSFULLY = 1;
    public static final int USER_INACTIVE_MAIL_NOT_SENT = 2;
    public static final int USER_INACTIVE_MAIL_SENT = 3;
    public static final int USER_INACTIVE_MAIL_EXPIRED = 4;
    public static final int USER_FORGET_PASSWORD_MAIL_SENT = 5;
    public static final int USER_FORGET_PASSWORD_MAIL_NOT_SENT = 6;
    public static final int USER_FORGET_PASSWORD_MAIL_EXPIRED = 7;
    public static final int USERTOKEN_STATUS_ACTIVATED = 0;
    public static final int USERTOKEN_STATUS_VALID = 1;
    public static final String USER_ID = "userID";
    public static final String CUSTOMER_ID = "customerID";
    public static final String TECHNICIAN_ID = "technicianID";
    public static final String PRODUCT_NAME = "productName";
    public static final String ADMIN_EMAIL = "adminEmail";
    public static final String USER_EMAIL = "userEmail";
    public static final String NEW_PASSWORD_TOKEN = "newPasswordLink";
    public static final String RESET_PASSWORD_TOKEN = "resetPasswordLink";
    public static final String COMPANY_LOGO_LINK = "companyLogoLink";
    public static final String LINK_EXPIRY = "linkExpiry";
    public static final String LINK_EXPIRED = "linkExpired";
    public static final String NEW_USERMAIL_SERVER_FAILED = "newUserMailServerFailure";
    public static final String RESET_PASSWORDMAIL_SERVER_FAILED = "resetPasswordMailServerFailure";
    public static final String RESET_PASSWORDMAIL_INITIATED = "resetPasswordInitiated";
    public static final String CARE_NUMBERS_PROPERTIES = "CARE_NUMBERS_PROPERTIES";
    public static final String DEFAULT_TOLL_FREE = "DefaultTollFree";
    public static final String TOLL_FREE = "TollFree";
    public static final String TELEPHONE = "Telephone";
    public static final String DID = "DID";
    
    public static final class UserAlertConstant
    {
        public static final String USER_NAME = "$user_name$";
        public static final String USER_DOMAIN_NAME = "$domain_name$";
        public static final String PRODUCT_NAME = "$product_name$";
        public static final String ADMIN_EMAIL = "$admin_email$";
        public static final String CREATE_PASSWORD_LINK = "$create_password_link$";
        public static final String USER_ACTIVATION_CONTENT = "UserAccountActivation";
        public static final String RESET_PASSWORD_LINK = "$reset_password_link$";
        public static final String RESET_PASSWORD_CONTENT = "ResetAccountPassword";
        public static final String COMPANY_LOGO = "$company_logo$";
        public static final String LINK_EXPIRY_TIME = "$link_expiry_time$";
        public static final String ACCOUNT_CREATED = "$account_created$";
        public static final String PASSWORD_RESET = "$password_reset$";
        public static final String CHANGE_PASSWORD = "$change_password$";
        public static final String ACCOUNT = "$account$";
        public static final String EVENT_CODE = "eventCode";
        public static final String IS_AD_USER = "isADUser";
        public static final String ALERT_TYPE = "alertType";
        public static final Long NEW_USER_ACCOUNT_ACTIVATION;
        public static final Long USER_ACCOUNT_ACTIVATION_WITHOUT_DOMAIN;
        public static final Long AD_USER_ACCOUNT_ACTIVATION;
        public static final Long USER_ACCOUNT_PASSWORD_RESET;
        public static final Long RESET_PASSWORD_ON_EXPIRY;
        public static final Long THIRD_PARTY_RESET_PASSWORD;
        public static final Long CHANGE_PASSWORD_FOR_TECH;
        
        static {
            NEW_USER_ACCOUNT_ACTIVATION = 11110L;
            USER_ACCOUNT_ACTIVATION_WITHOUT_DOMAIN = 11111L;
            AD_USER_ACCOUNT_ACTIVATION = 11112L;
            USER_ACCOUNT_PASSWORD_RESET = 11113L;
            RESET_PASSWORD_ON_EXPIRY = 11114L;
            THIRD_PARTY_RESET_PASSWORD = 11115L;
            CHANGE_PASSWORD_FOR_TECH = 11116L;
        }
    }
}
