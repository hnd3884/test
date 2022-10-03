package com.me.idps.core.factory;

public enum IdpsFactoryConstant
{
    ZD_SYNC_IMPL("getZDimpl"), 
    GSUITE_IMPL("getGsuiteImpl"), 
    NATIVE_OP_IMPL("getNativeOPImpl"), 
    PRODUCT_IMPL("getIdpsProductImpl"), 
    DIRECTORY_DB_API("getIdpsProdEnvAPI"), 
    AZURE_OAUTH_IMPL("getAzureOauthImpl"), 
    ASSIST_OAUTH_IMPL("getAssistOauthImpl"), 
    CSEZ_ZD_OAUTH_IMPL("getCsezZdOauthImpl"), 
    AZURE_MAM_OAUTH_IMPL("getAzureMamOauthImpl"), 
    MIGRATION_TOOL_OAUTH_CHINA_TYPE("getMigrationToolOauthChinaImpl"), 
    MIGRATION_TOOL_OAUTH_OTHER_TYPE("getMigrationToolOauthOtherImpl");
    
    private String methodName;
    
    private IdpsFactoryConstant(final String methodName) {
        this.methodName = methodName;
    }
    
    String getInitializationMethod() {
        return this.methodName;
    }
}
