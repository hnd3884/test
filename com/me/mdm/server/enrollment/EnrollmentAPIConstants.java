package com.me.mdm.server.enrollment;

public class EnrollmentAPIConstants
{
    public static final String OWNED_BY = "owned_by";
    public static final String ENROLL_ID = "enrol_id";
    public static final String EMAIL_ID = "email_id";
    public static final String MAIL_SENT = "mail_sent";
    public static final String IS_EDITABLE = "is_editable";
    public static final String RETRY_WAKE_UP = "retry_wake_up";
    public static final String RESEND_ENROLLMENT_REQUEST = "resend_enrollment_request";
    public static final String ENROLLMENT_STEPS = "enrollment_steps";
    public static final String REMARKS = "remarks";
    public static final String ACTION = "action";
    public static final String PARAMS = "params";
    public static final String ERID = "erid";
    public static final String REGENERATE_OTP = "regenerate_otp";
    public static final String BY_ADMIN = "by_admin";
    public static final String PLATFORM = "platform";
    public static final String SEND_NEW_REQUEST = "send_new_request";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String ASSIGN_USER = "assign_user";
    public static final String RE_ENROLL_DEVICE_ADMIN = "re_enroll_device_admin";
    public static final String RE_ENROLL_DEVICE_INVITATION = "re_enroll_device_invitation";
    public static final String TEMPLATE_TYPE = "template_type";
    public static final String IMEI = "imei";
    public static final String SLNO = "slno";
    public static final String EAS_ID = "eas_id";
    public static final String UDID = "udid";
    public static final String MANAGED_STATUS = "managed_status";
    public static final String DEVICE_NAME = "device_name";
    public static final String MOVE_TO_STOCK = "move_to_stock";
    public static final String RETIRE_DEVICE = "retire_device";
    public static final String REMOVE_DEVICE = "remove_device";
    public static final String CHANGE_USER = "change_user";
    public static final String RE_DIRECT_DC_SOM = "re_direct_dc_som";
    public static final String DEPROVISION_PAGE = "deprovision_page";
    public static final String WINDOWS_APP_ENROLL_MAIL = "windows_app_enroll_mail";
    public static final String REGAIN_IOS_DEVICE = "regain_ios_device";
    public static final String ACTIONS = "actions";
    public static final String IS_ENABLED = "is_enabled";
    public static final String IS_SMS_SETTINGS_CONFIGURED = "is_sms_settings_configured";
    public static final String SMS_CREDITS_REMAINING = "sms_credits_remaining";
    public static final String IS_SUPER_ADMIN_VERIFIED = "is_super_admin_verified";
    public static final String APPLE_ADMIN_ENROLLMENT_COUNT = "apple_admin_enrollment_count";
    public static final String APNS_REACHABLE = "apns_reachable";
    public static final String PORT_UNBLOCKED = "port_unblocked";
    public static final String FORCE_OVERRIDE = "force_override";
    public static final String WAITING_FOR_LICENSE_COUNT = "waiting_for_license_count";
    public static final String ENROLLED_COUNT = "enrolled_count";
    public static final String LICENSE_TYPE = "license_type";
    public static final String LICENSE_COUNT = "license_count";
    public static final String CUSTOMER_LICENSE_COUNT = "customer_license_count";
    public static final String MAX_THRESHOLD = "max_threshold";
    public static final String MIN_THRESHOLD = "min_threshold";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_ID = "customerId";
    public static final String ENROLLMENT_TYPE_CONSTANT = "enrollment_type_constant";
    public static final String UEM_WAITING_FOR_LICENSE_COUNT = "uem_waiting_for_license_count";
    public static final String IS_UEM_LIMIT_EXCEED = "is_uem_limit_exceed";
    
    public enum AwaitingLicenseType
    {
        UEM, 
        NON_UEM;
    }
}
