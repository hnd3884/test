package com.me.mdm.uem.windows;

import java.util.List;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.uem.ModernMgmtHandler;

public class WindowsModernMgmtHandler extends ModernMgmtHandler
{
    @Override
    protected JSONObject getAgentProps(final JSONObject commandJSON) {
        JSONObject installDCMSI = null;
        try {
            commandJSON.put("uemPlatformType", 3);
            installDCMSI = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.GETLEGACY_AGENT_DETAILS, commandJSON);
        }
        catch (final Exception ex) {
            WindowsModernMgmtHandler.mdmEnrollmentLogger.log(Level.SEVERE, "Exception while checking for legacy agent installation {0}", ex);
        }
        return installDCMSI;
    }
    
    @Override
    protected void addAgentInstallCommand(final Long resourceID, final JSONObject commandProps) {
        DeviceCommandRepository.getInstance().addWindowsCommand(Arrays.asList(resourceID), "InstallLegacyAgent");
    }
}
