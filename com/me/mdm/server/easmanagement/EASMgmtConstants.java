package com.me.mdm.server.easmanagement;

import com.me.mdm.core.MDMConstants;
import java.io.File;

public class EASMgmtConstants
{
    public static final int EXCHANGE_ONLINE_VERSION = 0;
    public static final int EXCHANGE_10_VERSION = 14;
    public static final int EXCHANGE_13_VERSION = 15;
    public static final int EXCHANGE_16_VERSION = 16;
    public static final int EXCHANGE_19_VERSION = 19;
    private static final String RUN_DIR;
    private static final String EAS_FOLDER = "eas";
    private static final String POWERSHELL_SCRIPTS_DIR;
    private static final String EXCHANGE_SERVER_SCRIPTS_DIR;
    private static final String EXCHANGE_ONLINE = "eo";
    private static final String EXCHANGE_SERVER_10 = "es10";
    private static final String EXCHANGE_SERVER_13 = "es13";
    private static final String EXCHANGE_SERVER_16 = "es16";
    private static final String EXCHANGE_SERVER_19 = "es19";
    private static final String EAS_FOLDER_PATH;
    private static final String EXCEPTION_TYPE = "exceptionType";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SESSION_SCRIPT_FILE_NAME = "script.ps1";
    private static final String TASK_SCRIPT_FILE_NAME = "task.ps1";
    private static final String EAS_SERVER_DETAILS_JSON = "easServerDetails.json";
    public static final String END_OF_RESPONSE_FILE_NAME = "END_OF_RESPONSE_FILE_NAME";
    public static final String FINISHED_SYNCING = "Finished Syncing.. Processing data";
    public static final String NEED_TO_PERFORM_CONDITIONAL_ACCESS = "NEED_TO_PERFORM_CONDITIONAL_ACCESS";
    public static final String SEND_GRACE_MAILS = "SEND_GRACE_MAILS";
    public static final Integer POLICY_ENFORCEMENT_DONE;
    public static final Integer POLICY_APPLICATION_INPROGRESS;
    public static final Integer CEA_ROLLBACK_BUILD_NUMBER;
    public static final String TASK_TYPE = "TASK_TYPE";
    public static final String START_SESSION_REQUEST = "START_SESSION_REQUEST";
    public static final String EXCHANGE_SERVER_DETAILS_REQUEST = "EXCHANGE_SERVER_DETAILS_REQUEST";
    public static final String SYNC_REQUEST = "SYNC_REQUEST";
    public static final String CONDITIONAL_ACCESS_REQUEST = "FULL_CONDITIONAL_ACCESS_REQUEST";
    public static final String CEA_POLICY_REMOVAL = "CEA_POLICY_REMOVAL";
    public static final String CEA_REMOVAL = "CEA_REMOVAL";
    public static final String REMOVE_EAS_DEVICE = "REMOVE_EAS_DEVICE";
    public static final String ACKNOWLEDGEMENT = "ACKNOWLEDGEMENT";
    public static final String ACKNOWLEDGEMENT_TYPE = "ACKNOWLEDGEMENT_TYPE";
    public static final String RESPONSE = "RESPONSE";
    public static final String RESPONSE_STATUS = "response_status";
    public static final String READ_FROM_FILE = "readFromFile";
    public static final String FILE_ADDRESS = "fileAddress";
    public static final String EAS_RESPONSE = "easResponse";
    public static final String ENROLLMENT_TASK = "ENROLLMENT_TASK";
    public static final int ERROR_OCCURED = 0;
    public static final int SUCCESSFULLY_RETRIEVED_RESPONSE = 1;
    public static final int NOT_RECEIVED_RESPONSE = 2;
    public static final int DEAD_SESSION = 0;
    public static final int LIVE_SESSION = 1;
    public static final int SESSION_INITIATING = 2;
    public static final int EAS_DEVICE_ALLOWED = 0;
    public static final int EAS_DEVICE_BLOCKED = 1;
    public static final int EAS_DEVICE_QUARANTINED = 2;
    public static final int DEFAULT_ACCESS_LEVEL_ALLOW = 0;
    public static final String ALLOW_DEFAULT_ACCESS_LEVEL = "Allow";
    public static final int DEFAULT_ACCESS_LEVEL_BLOCK = 1;
    public static final String BLOCK_DEFAULT_ACCESS_LEVEL = "Block";
    public static final int DEFAULT_ACCESS_LEVEL_QUARANTINE = 2;
    public static final String QUARANTINE_DEFAULT_ACCESS_LEVEL = "Quarantine";
    public static final int SYNC_FINISHED = 0;
    public static final int SYNC_ONGOING = 1;
    public static final int FETCHING_SERVER_DETAILS = 2;
    public static final String INAPPROPRIATE_DEVICE_ACCESS_STATES = "INAPPROPRIATE_DEVICE_ACCESS_STATES";
    public static final String IS_MANAGED_USER_MAILBOX_EAS_GRACED = "IS_MANAGED_USER_MAILBOX_EAS_GRACED";
    public static final String IS_MANAGED_USER_MAILBOX_EAS_SELECTED = "IS_MANAGED_USER_MAILBOX_EAS_SELECTED";
    public static final String TO_BE_ALLOWED = "TO_BE_ALLOWED";
    public static final String TO_BE_NOT_ALOLWED = "TO_BE_NOT_ALOLWED";
    public static final String TO_BE_BLOCKED = "TO_BE_BLOCKED";
    public static final String EXPECTED_SUCCESS_RESULT_FILE = "EXPECTED_SUCCESS_RESULT_FILE";
    public static final String MANAGED = "managed";
    public static final int ALL_USERS = 1;
    public static final int SELECTED_USERS = 2;
    public static final String EAS_LOGGER = "EASMgmtLogger";
    public static final String EAS_SCHEDULER = "EASSchedule";
    public static final String EAS_HOST_CONNECTING = "mdm.cea.config.progress";
    public static final String FETCHING_MAILBOX_LIST = "mdm.cea.mailbox.fetch";
    public static final String SYNC_MAILBOX_DEVICE_DETAILS_COUNT = "mdm.cea.device.fetch";
    public static final String GRACE_PERIOD_EXPIRES_IN = "$days_to_expire$";
    public static final String TO_EMAIL_ADDRESS = "$user_emailid$";
    public static final String SELF_ENROLL_URL = "$self_enroll_url$";
    public static final String USER_NAME = "$eas_user_name$";
    public static final String DEVICE_LIST = "$exchange_blocked_devices$";
    public static final String MDM_EAS_UNAUTHORIZED_DEVICE = "MDM_EAS_UNAUTHORIZED_DEVICE";
    public static final String MDM_EAS_NOTIFY_USER = "MDM_EAS_NOTIFY_USER";
    public static final Integer PSSESSION_INTIATE_ERROR;
    public static final Integer NON_ADMIN_USER_ERROR;
    public static final Integer EAS_GENERIC_ERROR;
    public static final Integer PS_VERSION_ERROR_ID;
    public static final Integer UNRESOLVABLE_FQDN_ERROR_ID;
    public static final Integer SSL_REQUIRE_CERTIFICATE_ERROR_ID;
    public static final Integer BASIC_ATUTH_NOT_ENABLED_ERROR_ID;
    public static final Integer INVALID_CREDENTIALS_ERROR_ID;
    public static final Integer EXO_V2_PRE_REQUISITE;
    public static final Integer MIN_POWERSHELL_REQUIREMENT;
    public static final Integer EO_MAJOR_POWERSHELL_REQUIREMENT;
    public static final Integer EO_MINOR_POWERSHELL_REQUIREMENT;
    public static final int EAS_SERVER_DETAILS_EVENTID = 2076;
    public static final int FULL_SYNC_EVENTID = 2077;
    public static final int FULL_CONDITIONAL_ACCESS_EVENTID = 2078;
    public static final int ENROLLMENT_EVENTID = 2079;
    public static final String EAS_SERVER_DETAILS_FAIL = "mdm.cea.server.details.fail";
    public static final String EAS_SERVER_DETAILS_SUCCESS = "mdm.cea.server.details.success";
    public static final String FULL_SYNC_FAIL = "mdm.cea.sync.fail";
    public static final String FULL_SYNC_SUCCESS = "mdm.cea.sync.success";
    public static final String FULL_CONDITIONAL_ACCESS_FAIL = "mdm.cea.conditional.access.fail";
    public static final String FULL_CONDITIONAL_ACCESS_SUCCESS = "mdm.cea.conditional.access.success";
    public static final String ENROLLMENT_FAIL = "mdm.cea.enroll.fail";
    public static final String ENROLLMENT_SUCCESS = "dc.mdm.eas.eas_enrollment_success";
    public static final String CURRENTLY_LOGGED_IN_USER = "CURRENTLY_LOGGED_IN_USER";
    public static final String POWERSHELL_VERSION_CHECK_SCRIPT;
    public static final String CONVERT_TO_JSON_MODULE;
    public static final String CONVERT_FROM_JSON_MODULE;
    public static final String FQDN_RESOLVE_SCRIPT;
    public static final String SESSION_INTIATE_TASK_PROCESSOR_SCRIPT;
    public static final String SCRIPT_PREFIX;
    public static final String EXCHANGE_ONLINE_DETAILS_SCRIPT;
    public static final String EXCHANGE_SERVER_DETAILS_SCRIPT;
    public static final String EXCHANGE_SERVER_10_SYNC_SCRIPT;
    public static final String EXCHANGE_SERVER_13_SYNC_SCRIPT;
    public static final String EXCHANGE_SERVER_16_SYNC_SCRIPT;
    public static final String EXCHANGE_SERVER_19_SYNC_SCRIPT;
    public static final String EXCHANGE_ONLINE_SYNC_SCRIPT;
    public static final String EXCHANGE_SERVER_10_CONDITIONAL_ACCESS_SCRIPT;
    public static final String EXCHANGE_SERVER_13_CONDITIONAL_ACCESS_SCRIPT;
    public static final String EXCHANGE_SERVER_16_CONDITIONAL_ACCESS_SCRIPT;
    public static final String EXCHANGE_SERVER_19_CONDITIONAL_ACCESS_SCRIPT;
    public static final String EXCHANGE_ONLINE_CONDITIONAL_ACCESS_SCRIPT;
    public static final String INSTALL_EXO_V2_SCRIPT;
    public static final String EXCHANGE_SERVER_10_DELETE_DEVICE_SCRIPT;
    public static final String EXCHANGE_SERVER_13_DELETE_DEVICE_SCRIPT;
    public static final String EXCHANGE_SERVER_16_DELETE_DEVICE_SCRIPT;
    public static final String EXCHANGE_SERVER_19_DELETE_DEVICE_SCRIPT;
    public static final String EXCHANGE_ONLINE_DELETE_DEVICE_SCRIPT;
    public static final String CLOSE_SESSION_SCRIPT;
    public static final String EXCHANGE_SERVER_DETAILS = "EXCHANGE_SERVER_DETAILS";
    public static final String MAILBOX_COUNT = "MAILBOX_COUNT";
    public static final String MAILBOX_DEVICE_PARTNERSHIP_COUNT = "MAILBOX_DEVICE_PARTNERSHIP_COUNT";
    public static final String SELECTED_MAILBOX_COUNT = "SELECTED_MAILBOX_COUNT";
    public static final String EAS_PROFILE_SECURE_EMAIL_CLICK = "EAS_PROFILE_SECURE_EMAIL_CLICK";
    public static final String EAS_PROFILE_DONT_SECURE_EMAIL_CLICK = "EAS_PROFILE_DONT_SECURE_EMAIL_CLICK";
    public static final String EAS_PROFILE_DO_NOT_SHOW_CLICK = "EAS_PROFILE_DO_NOT_SHOW_CLICK";
    public static final String SESSION_STATUS = "SESSION_STATUS";
    public static final String LAST_SUCCESSFUL_SYNC_DATE_TIME = "LAST_SUCCESSFUL_SYNC_DATE_TIME";
    public static final String CLEAR_BLOCK_LIST = "CLEAR_BLOCK_LIST";
    public static final int CEA_ALLOWED = 1;
    public static final int CEA_BLOCKED = 2;
    public static final String ROLLBACK_BLOCKED_DEVICES = "ROLLBACK_BLOCKED_DEVICES";
    public static final String UPDATE_POLICY_SELECTION = "UPDATE_POLICY_SELECTION";
    public static final int MARKED_FOR_REMOVAL = 1;
    public static final int DEVICE_NOT_FOUND_FOR_REMOVAL = 2;
    public static final int DEVICE_NOT_REMOVED = -1;
    public static final String DEVICE_DELETION_LIMIT_REACHED = "DEVICE_DELETION_LIMIT_REACHED";
    public static final int SEND_ALL_MAILS = 3;
    public static final int SEND_FINAL_BLOCK_MAILS = 2;
    public static final int SEND_GRACE_PERIOD_MAILS = 1;
    public static final int SEND_NO_MAILS = 0;
    public static final int CEA_POLICY_SELECTION_LIMIT = 4000;
    
    public static String getDedicatedFolderPath(final Long easServerID) {
        return EASMgmtConstants.EAS_FOLDER_PATH + File.separator + String.valueOf(easServerID);
    }
    
    public static String getSessionScriptFilePath(final Long easServerID) {
        return getDedicatedFolderPath(easServerID) + File.separator + "script.ps1";
    }
    
    public static String getTaskScriptFileAddress(final Long easServerID) {
        return getDedicatedFolderPath(easServerID) + File.separator + "task.ps1";
    }
    
    public static String getExceptionTypeFileAddress(final Long easServerID) {
        return getDedicatedFolderPath(easServerID) + File.separator + "exceptionType";
    }
    
    public static String getErrorMessageFileAddress(final Long easServerID) {
        return getDedicatedFolderPath(easServerID) + File.separator + "errorMessage";
    }
    
    public static String getEASserverDetailsResultFile(final Long easServerID) {
        return getDedicatedFolderPath(easServerID) + File.separator + "easServerDetails.json";
    }
    
    static {
        RUN_DIR = System.getProperty("user.dir");
        POWERSHELL_SCRIPTS_DIR = MDMConstants.MDM_DIR + File.separator + "psscript";
        EXCHANGE_SERVER_SCRIPTS_DIR = EASMgmtConstants.POWERSHELL_SCRIPTS_DIR + File.separator + "exchangeserver";
        EAS_FOLDER_PATH = MDMConstants.MDM_DIR + File.separator + "eas";
        POLICY_ENFORCEMENT_DONE = 0;
        POLICY_APPLICATION_INPROGRESS = 1;
        CEA_ROLLBACK_BUILD_NUMBER = 92317;
        PSSESSION_INTIATE_ERROR = 51202;
        NON_ADMIN_USER_ERROR = 51208;
        EAS_GENERIC_ERROR = 51209;
        PS_VERSION_ERROR_ID = 51210;
        UNRESOLVABLE_FQDN_ERROR_ID = 51211;
        SSL_REQUIRE_CERTIFICATE_ERROR_ID = 51213;
        BASIC_ATUTH_NOT_ENABLED_ERROR_ID = 51214;
        INVALID_CREDENTIALS_ERROR_ID = 51215;
        EXO_V2_PRE_REQUISITE = 51216;
        MIN_POWERSHELL_REQUIREMENT = 2;
        EO_MAJOR_POWERSHELL_REQUIREMENT = 5;
        EO_MINOR_POWERSHELL_REQUIREMENT = 1;
        POWERSHELL_VERSION_CHECK_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "psVersionCheck.ps1";
        CONVERT_TO_JSON_MODULE = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "ConvertTo-JSON.psm1";
        CONVERT_FROM_JSON_MODULE = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "ConvertFrom-JSON.psm1";
        FQDN_RESOLVE_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "fqdnResolve.ps1";
        SESSION_INTIATE_TASK_PROCESSOR_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "session_starter_task_processor.ps1";
        SCRIPT_PREFIX = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "script_prefix.ps1";
        EXCHANGE_ONLINE_DETAILS_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "eo" + File.separator + "exchange_online_details.ps1";
        EXCHANGE_SERVER_DETAILS_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "exchange_server_details.ps1";
        EXCHANGE_SERVER_10_SYNC_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es10" + File.separator + "full_sync_script.ps1";
        EXCHANGE_SERVER_13_SYNC_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es13" + File.separator + "full_sync_script.ps1";
        EXCHANGE_SERVER_16_SYNC_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es16" + File.separator + "full_sync_script.ps1";
        EXCHANGE_SERVER_19_SYNC_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es19" + File.separator + "full_sync_script.ps1";
        EXCHANGE_ONLINE_SYNC_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "eo" + File.separator + "full_sync_script.ps1";
        EXCHANGE_SERVER_10_CONDITIONAL_ACCESS_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es10" + File.separator + "conditional_access.ps1";
        EXCHANGE_SERVER_13_CONDITIONAL_ACCESS_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es13" + File.separator + "conditional_access.ps1";
        EXCHANGE_SERVER_16_CONDITIONAL_ACCESS_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es16" + File.separator + "conditional_access.ps1";
        EXCHANGE_SERVER_19_CONDITIONAL_ACCESS_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es19" + File.separator + "conditional_access.ps1";
        EXCHANGE_ONLINE_CONDITIONAL_ACCESS_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "eo" + File.separator + "conditional_access.ps1";
        INSTALL_EXO_V2_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "eo" + File.separator + "exoV2preRequsities.ps1";
        EXCHANGE_SERVER_10_DELETE_DEVICE_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es10" + File.separator + "remove_device.ps1";
        EXCHANGE_SERVER_13_DELETE_DEVICE_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es13" + File.separator + "remove_device.ps1";
        EXCHANGE_SERVER_16_DELETE_DEVICE_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es16" + File.separator + "remove_device.ps1";
        EXCHANGE_SERVER_19_DELETE_DEVICE_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "es19" + File.separator + "remove_device.ps1";
        EXCHANGE_ONLINE_DELETE_DEVICE_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "eo" + File.separator + "remove_device.ps1";
        CLOSE_SESSION_SCRIPT = EASMgmtConstants.EXCHANGE_SERVER_SCRIPTS_DIR + File.separator + "close_session.ps1";
    }
}
