package com.me.mdm.uem.mac;

import java.util.Properties;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.List;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import java.util.ArrayList;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.uem.ModernMgmtHandler;

public class MacModernMgmtHandler extends ModernMgmtHandler
{
    @Override
    protected JSONObject getAgentProps(final JSONObject commandJSON) {
        MacModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "MacModernMgmtHandler: The Agent props obtained for DC agent distribution is : {0}", new Object[] { commandJSON });
        JSONObject installDCPKG = null;
        try {
            commandJSON.put("uemPlatformType", 1);
            installDCPKG = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.GETLEGACY_AGENT_DETAILS, commandJSON);
            if (installDCPKG == null) {
                MacModernMgmtHandler.mdmEnrollmentLogger.log(Level.SEVERE, "Agent settings is not saved in SOM, so aborting DC agent distribution");
                return null;
            }
            new MacDCAgentHandler().addDCAgentToMDMRepoIfNotExistAlready(commandJSON.getLong("CUSTOMER_ID"));
        }
        catch (final Exception ex) {
            MacModernMgmtHandler.mdmEnrollmentLogger.log(Level.SEVERE, "Exception while checking for legacy agent installation {0}", ex);
        }
        return installDCPKG;
    }
    
    @Override
    protected void addAgentInstallCommand(final Long resourceID, final JSONObject commandProps) {
        try {
            MacModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "MacModernMgmtHandler: Agent props while distributing DC agent is : {0}", new Object[] { commandProps });
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceID);
            final long customerId = commandProps.getLong("CUSTOMER_ID");
            final Properties properties = AppsAutoDeployment.getInstance().getAppProfileDetails(customerId, resourceList, 1, 1, "InstallApplication");
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
        catch (final Exception e) {
            MacModernMgmtHandler.mdmEnrollmentLogger.log(Level.SEVERE, "Exception while sending MacOS DC agent to the device", e);
        }
    }
}
