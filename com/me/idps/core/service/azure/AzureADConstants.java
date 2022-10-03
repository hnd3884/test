package com.me.idps.core.service.azure;

class AzureADConstants
{
    static final int EQUALS_FILTER = 0;
    static final int STARTS_WITH_FILTER = 1;
    static final String OBJECTID = "objectId";
    static final String GRAPH_API_RESOURCE_URL = "https://graph.windows.net";
    static final String AUTHORITY = "https://login.microsoftonline.com/{0}/oauth2/token";
    static final String GROUPS_CONTEXT_PATH = "groups";
    static final String USERS_CONTEXT_PATH = "users";
    static final String DEVICES_CONTEXT_PATH = "devices";
    static final String API_VERSION = "api-version=1.6";
    static final String ERROR_CODE = "ERROR_CODE";
    static final String RESPONSE_DELTA_LING = "deltaLink";
    static final String DELTA_LINK = "aad.deltaLink";
    static final String PAGING_NEXT_LINK = "odata.nextLink";
    static final String DELTA_PAGING_NEXT_LINK = "aad.nextLink";
    static final String DISPLAY_NAME = "displayName";
    static final String VALUE = "value";
    static final String SKIP_TOKEN = "$skiptoken";
    static final String GROUP_IDS = "groupIds";
    static final String LIST_OF_OBJECTS = "LIST_OF_OBJECTS";
    static final String LAST_NAME = "surname";
    static final String DELETED_KEY = "aad.isDeleted";
    static final String SOFT_DELETE_KEY = "aad.isSoftDeleted";
    static final String DELETION_TIME_STAMP = "deletionTimeStamp";
    static final String DELTA_TOKEN = "DELTA_TOKEN";
    static final String MOBILE_NO = "mobile";
    static final String SOURCE_OBJ_GUID = "sourceObjectId";
    static final String SOURCE_OBJ_TYPE = "sourceObjectType";
    static final String TARGET_OBJ_GUID = "targetObjectId";
    static final String ACCOUNT_ENABLED = "accountEnabled";
    static final String MEMBER_REL_TYPE = "Member";
    static final String ASSOCIATION_TYPE = "associationType";
    static final String SOURCE_GROUP_TYPE = "Group";
    static final String DEVICE_TRUST_TYPE = "deviceTrustType";
    static final String DEVICE_OS_TYPE = "deviceOSType";
    static final String DEVICE_ID = "deviceId";
    static final String LAST_LOGON_TIME_AD_OBJ = "approximateLastLogonTimestamp";
    static final String LAST_LOGON_TIME = "lastLogonTime";
    static final String IS_COMPLIANT = "isCompliant";
    static final String IS_MANAGED = "isManaged";
    static final String AZURE_DEVICE_STATUS = "azureDeviceStatus";
    static final String PROFILE_TYPE = "profileType";
    static final String REGISTERED_OWNERS = "registeredOwners";
    static final String REGISTERED_USERS = "registeredUsers";
}
