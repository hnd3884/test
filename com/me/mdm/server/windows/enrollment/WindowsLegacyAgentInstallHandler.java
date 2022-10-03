package com.me.mdm.server.windows.enrollment;

import com.me.devicemanagement.framework.server.exception.SyMException;
import javax.xml.bind.JAXBException;
import org.json.JSONException;
import com.me.mdm.server.windows.profile.payload.WinDesktopAppPayload;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import com.me.mdm.server.windows.profile.payload.WindowsConfigurationPayload;
import java.util.List;
import com.me.mdm.server.windows.profile.payload.WinDesktopMSIAppPayload;
import com.me.mdm.server.windows.profile.payload.transform.DO2WindowsAppPayload;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WindowsLegacyAgentInstallHandler
{
    static WindowsLegacyAgentInstallHandler windowsLegacyAgentInstallHandler;
    
    public static WindowsLegacyAgentInstallHandler getInstance() {
        if (WindowsLegacyAgentInstallHandler.windowsLegacyAgentInstallHandler == null) {
            WindowsLegacyAgentInstallHandler.windowsLegacyAgentInstallHandler = new WindowsLegacyAgentInstallHandler();
        }
        return WindowsLegacyAgentInstallHandler.windowsLegacyAgentInstallHandler;
    }
    
    public void processLegacyAgentInstallation(final SyncMLMessage responseSyncML, final JSONObject jsonObject) throws JSONException, JAXBException, SyMException, ClassNotFoundException {
        final JSONObject agentDetails = jsonObject.getJSONObject("agentDetails");
        final JSONObject msiDetailsJSON = new JSONObject();
        msiDetailsJSON.put("downloadURL", (Object)String.valueOf(agentDetails.get("AgentDownloadUrl")));
        msiDetailsJSON.put("version", (Object)String.valueOf(agentDetails.get("AgentVersion")));
        msiDetailsJSON.put("productID", (Object)String.valueOf(agentDetails.get("AgentUniqueIdentifier")));
        msiDetailsJSON.put("fileHash", (Object)String.valueOf(agentDetails.get("SHA256FileHash")));
        msiDetailsJSON.put("commandLine", (Object)String.valueOf(agentDetails.get("CommandLineParams")));
        final String msiInstallJob = DO2WindowsAppPayload.createMSIInstallString(msiDetailsJSON);
        msiDetailsJSON.put("msiInstallJob", (Object)msiInstallJob);
        final WinDesktopAppPayload winDesktopAppPayload = new WinDesktopMSIAppPayload();
        winDesktopAppPayload.setCommandUUID("InstallLegacyAgent");
        winDesktopAppPayload.setPayloadType("InstallConfigPayload");
        winDesktopAppPayload.initializePayload(msiDetailsJSON, "install");
        winDesktopAppPayload.setURL(String.valueOf(msiDetailsJSON.get("downloadURL")), null);
        winDesktopAppPayload.enableSilentInstall();
        WindowsConfigurationPayload finalPayload = new WindowsConfigurationPayload();
        finalPayload = winDesktopAppPayload.getOSSpecificInstallPayload(finalPayload);
        finalPayload.setCommandUUID("InstallLegacyAgent");
        final SequenceRequestCommand sequenceRequestCommand = new SequenceRequestCommand();
        sequenceRequestCommand.setRequestCmdId("InstallLegacyAgent");
        sequenceRequestCommand.addRequestCmd(finalPayload.getAtomicPayloadContent());
        responseSyncML.getSyncBody().addRequestCmd(sequenceRequestCommand);
    }
    
    static {
        WindowsLegacyAgentInstallHandler.windowsLegacyAgentInstallHandler = null;
    }
}
