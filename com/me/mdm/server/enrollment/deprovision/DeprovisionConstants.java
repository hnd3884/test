package com.me.mdm.server.enrollment.deprovision;

public class DeprovisionConstants
{
    public static final String IOS_DEVICE_COUNT = "IOSDeviceCount";
    public static final String SUCCESS = "success";
    public static final String SUCCESS_LIST = "SuccessList";
    public static final String FAILURE_LIST = "FailureList";
    public static final String ERROR_MSG = "ErrorMsg";
    public static final String DEVICE_NAME = "device_name";
    public static final String REMARKS = "REMARKS";
    public static final String MANAGED_STATUS = "MANAGED_STATUS";
    public static final String WIPE_PENDING = "WIPE_PENDING";
    public static final String COMPLETE_WIPE_REMARKS_I18KEY = "mdm.deprovision.complete_wipe_init";
    public static final String APNS_EXPIRED_REMARKS_I18KEY = "mdm.deprovision.apns_expired_deprovision_ios_device";
    public static final String REPAIRED_ACTIONLOG_I18KEY = "mdm.actionlog.deprovision.repaired";
    public static final String CORPORATE_WIPE_REMARKS_I18KEY = "mdm.deprovision.corporate_wipe_init";
    public static final String RETIRED_ACTIONLOG_I18KEY = "mdm.actionlog.deprovision.retired";
    public static final String IN_STOCK_ACTIONLOG_I18KEY = "mdm.actionlog.deprovision.stock";
    public static final String COMPLETE_WIPE_CMD_I18KEY = "dc.mdm.inv.remote_wipe";
    public static final String CORPORATE_WIPE_CMD_I18KEY = "dc.mdm.inv.corporate_wipe";
    public static final String DEPROVISION_SUCCESS_ACTIONLOG_I18KEY = "mdm.actionlog.deprovision.success_action_log";
    public static final String DEPROVISION_FAILED_PERSONAL_I18KEY = "mdm.api.error.deprovision_failed_personal_device";
    public static final String INTERNAL_SERVER_ERROR_I18KEY = "mdm.api.error.internal_server_error";
    public static final String DEVICE_NOT_IN_ENROLLED_STATE_I18KEY = "mdm.deprovision.error.not_in_enrolled_state";
    public static final String DEPROVISION_BULK_DEPROVISION_REMARK_I18KEY = "mdm.deprovision.bulk_deprovision_remarks";
    
    public class WipeReason
    {
        public static final int REPAIR_DEVICE = 1;
        public static final int EMPLOYEE_LEFT = 2;
        public static final int RETIRE_DEVICE = 3;
        public static final int OTHER_REASON = 4;
        public static final int JAILBROKEN_DEVICE = 5;
    }
    
    public class WipeType
    {
        public static final int COMPLETE_WIPE = 2;
        public static final int CORPORATE_WIPE = 1;
        public static final String CORPORATE_WIPE_STRING = "CorporateWipe";
    }
}
