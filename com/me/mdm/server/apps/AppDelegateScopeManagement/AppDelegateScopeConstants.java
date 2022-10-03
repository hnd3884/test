package com.me.mdm.server.apps.AppDelegateScopeManagement;

import java.util.Arrays;
import java.util.List;

public class AppDelegateScopeConstants
{
    public static final String DELEGATE_SCOPE_PERMISSION = "delegate_scope_permission_name";
    public static final String DELEGATE_SCOPE_STATE = "permission_state";
    public static final String DELEGATE_SCOPE_DETAILS = "appdelegatescopedetails";
    public static final List DELEGATE_SCOPE_LIST;
    
    static {
        DELEGATE_SCOPE_LIST = Arrays.asList("DELEGATION_APP_RESTRICTIONS", "DELEGATION_BLOCK_UNINSTALL", "DELEGATION_CERT_INSTALL", "DELEGATION_ENABLE_SYSTEM_APP", "DELEGATION_PACKAGE_ACCESS", "DELEGATION_PERMISSION_GRANT");
    }
    
    public static class APIConstants
    {
        public static final String APP_DELEGATE_SCOPE_DATA = "app_delegate_scope_data";
        public static final String APP_DELEGATE_SCOPE_PERMISSION = "delegate_scope_permission";
    }
    
    public static class DelegateScopeConfigConstants
    {
        public static final String DELEGATE_SCOPES = "DelegatedScopes";
        public static final String GRANTED_SCOPES = "GrantedScopes";
        public static final String DENIED_SCOPES = "DeniedScopes";
    }
}
