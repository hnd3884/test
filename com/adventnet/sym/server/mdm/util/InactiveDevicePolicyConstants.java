package com.adventnet.sym.server.mdm.util;

public class InactiveDevicePolicyConstants
{
    public static final int NO_ACTION = 0;
    public static final int REMOVE_DEVICE = 1;
    public static final int RETIRE_DEVICE = 2;
    public static final int UNASSIGN_MDM_LICENSE_FOR_DEVICE = 3;
    public static final String INACTIVE_THRESHOLD_STRING = "InactiveThreshold";
    public static final String IDP_ACTION_THRESHOLD_STRING = "IDPActionThreshold";
    public static final int INACTIVE_DEVICE_PERIOD_DAYS = 7;
    public static final int IDP_ACTION_PERIOD_IN_DAYS = 90;
    public static final Long GRAPH_START_RANGE_DEFAULT;
    public static final Long INACTIVE_THRESHOLD_DEFAULT;
    public static final Long IDP_ACTION_THRESHOLD_DEFAULT;
    public static final String NO_ACTION_STRING = "mdm.idpAction.no_action_string";
    public static final String REMOVE_DEVICE_STRING = "mdm.idpAction.remove_device_string";
    public static final String RETIRE_DEVICE_STRING = "mdm.deprovision.retire_device";
    public static final String UNASSIGN_MDM_LICENSE_FOR_DEVICE_STRING = "mdm.idpAction.unassign_MDMLicense_for_device_str";
    public static final String UNASSIGN_MDM_LICENSE_TEXT = "mdm.enroll.unassign_mdm_license_for_device";
    public static final String INACTIVE_DEVICE_REMOVAL = "mdm.actionlog.enrollment.inactive_device_removal";
    public static final String INACTIVE_DEVICE_RETIRE = "mdm.actionlog.enrollment.inactive_device_retire";
    public static final String INACTIVE_DEVICE_LICENSE_UNASSIGN = "mdm.actionlog.enrollment.idp_license_unassign";
    public static final String UNASSIGN_INACTIVE_DEVICE_REMARKS = "mdm.enroll.unassign_inactive_device_remarks";
    public static final String INACTIVE_DEVICE_POLICY_LINK = "mdm.inactive_policy_link";
    public static final String INACTIVE_DEVICES_SCHEDULE_REPORT_LINK = "mdm.inactive_devices_schedule_report_link";
    public static final String INACTIVE_DEVICES_REMARKS = "mdm.enroll.inactive_device_remarks";
    public static final String ACTIVE_DEVICES_REMARKS = "dc.mdm.db.agent.enroll.agent_enroll_finished";
    
    static {
        GRAPH_START_RANGE_DEFAULT = 2592000000L;
        INACTIVE_THRESHOLD_DEFAULT = 86400000L;
        IDP_ACTION_THRESHOLD_DEFAULT = 0L;
    }
}
