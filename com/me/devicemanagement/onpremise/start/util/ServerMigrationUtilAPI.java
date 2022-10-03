package com.me.devicemanagement.onpremise.start.util;

public interface ServerMigrationUtilAPI
{
    void modifyProductStartupForMigrationEnabled() throws Exception;
    
    void modifyProductStartupForMigrationDisabled() throws Exception;
}
