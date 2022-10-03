package com.me.mdm.agent.handlers.windows;

import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.List;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.BaseMigrationUtil;

public class WindowsMigrationUtil extends BaseMigrationUtil
{
    static WindowsMigrationUtil windowsMigrationUtil;
    
    public static WindowsMigrationUtil getInstance() {
        if (WindowsMigrationUtil.windowsMigrationUtil == null) {
            WindowsMigrationUtil.windowsMigrationUtil = new WindowsMigrationUtil();
        }
        return WindowsMigrationUtil.windowsMigrationUtil;
    }
    
    public WindowsMigrationUtil() {
        this.platformType = 3;
    }
    
    public String getMigratedURL(final JSONObject jsonObject) throws Exception {
        return MDMApiFactoryProvider.getMDMUtilAPI().getHttpsServerBaseUrl() + "/mdm/client/v1/wpserver" + "?" + "cid=" + jsonObject.get("CUSTOMER_ID") + "&erid=" + jsonObject.get("ENROLLMENT_REQUEST_ID") + "&muid=" + jsonObject.get("MANAGED_USER_ID") + "&" + "encapiKey" + "=" + jsonObject.getString("encapiKey");
    }
    
    @Override
    protected void addMigrationCommandForDevice(final List resourceIDs, final int commandRepoType) {
        if (commandRepoType == 1) {
            DeviceCommandRepository.getInstance().addWindowsCommand(resourceIDs, "ServerURLReplace");
        }
        else if (commandRepoType == 2) {
            DeviceCommandRepository.getInstance().addWindowsCommand(resourceIDs, "WindowsNativeAppConfig");
        }
        super.addMigrationCommandForDevice(resourceIDs, commandRepoType);
    }
    
    @Override
    protected boolean isMigrationRequiredForURL(final Object param, final int cmdRepType) {
        if (cmdRepType == 1) {
            return super.isMigrationRequiredForURL(param, cmdRepType);
        }
        final HashMap hashMap = (HashMap)param;
        return hashMap.containsKey("encapiKey");
    }
    
    static {
        WindowsMigrationUtil.windowsMigrationUtil = null;
    }
}
