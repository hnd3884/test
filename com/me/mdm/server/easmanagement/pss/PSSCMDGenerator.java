package com.me.mdm.server.easmanagement.pss;

import java.util.List;
import com.me.mdm.server.easmanagement.EASMgmtDataHandler;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.easmanagement.EASMgmtConstants;

public class PSSCMDGenerator
{
    private static final int BATCH_SIZE = 100;
    private static PSSCMDGenerator easScriptGenerator;
    
    public static PSSCMDGenerator getInstance() {
        if (PSSCMDGenerator.easScriptGenerator == null) {
            PSSCMDGenerator.easScriptGenerator = new PSSCMDGenerator();
        }
        return PSSCMDGenerator.easScriptGenerator;
    }
    
    public String getPowerShellVersionCheckScript() {
        final String basePath = EASMgmtConstants.getDedicatedFolderPath(0L) + File.separator;
        String script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.POWERSHELL_VERSION_CHECK_SCRIPT);
        script = script.replace("{0}", basePath + "majorVersion");
        script = script.replace("{1}", basePath + "minorVersion");
        return script;
    }
    
    public String getFQDNresolveScript(final String fqdn, final String successResponseFilePath, final String exceptionTypeFilePath) {
        String script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.FQDN_RESOLVE_SCRIPT);
        script = script.replace("{0}", fqdn);
        script = script.replace("{1}", successResponseFilePath);
        script = script.replace("{2}", exceptionTypeFilePath);
        return script;
    }
    
    private String getConnectionURI(final String fqdn) {
        return "https://".concat(fqdn).concat("/PowerShell/");
    }
    
    private String getAuthModes(final String preferredAuthMode) {
        String authModes = "\"" + preferredAuthMode + "\"";
        final String[] authModesAr = { "Digest", "Kerberos", "Negotiate" };
        for (int i = 0; i < authModesAr.length; ++i) {
            if (!authModes.contains(authModesAr[i])) {
                authModes = authModes + " , \"" + authModesAr[i] + "\"";
            }
        }
        return authModes;
    }
    
    public String getSessionScriptFor(final Long easServerID, final String easAdminPassword, final String easAdminEmailAddress, final String fqdn, final String preferredAuthMode, final String timeStampFilePath) {
        String script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.SESSION_INTIATE_TASK_PROCESSOR_SCRIPT);
        script = script.replace("{0}", easAdminPassword);
        script = script.replace("{1}", easAdminEmailAddress);
        script = script.replace("{2}", this.getConnectionURI(fqdn));
        script = script.replace("{3}", this.getAuthModes(preferredAuthMode));
        script = script.replace("{4}", " -AllowRedirection");
        script = script.replace("{5}", timeStampFilePath);
        script = script.replace("{6}", EASMgmtConstants.getTaskScriptFileAddress(easServerID));
        script = script.replace("{7}", EASMgmtConstants.getExceptionTypeFileAddress(easServerID));
        script = script.replace("{8}", EASMgmtConstants.getErrorMessageFileAddress(easServerID));
        script = script.replace("{9}", EASMgmtConstants.CONVERT_TO_JSON_MODULE);
        script = script.replace("{10}", EASMgmtConstants.CONVERT_FROM_JSON_MODULE);
        script = script.replace("{11}", String.valueOf(easServerID));
        return script;
    }
    
    public String getEASServerDetailsScript(final Long easServerID) {
        String script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_DETAILS_SCRIPT);
        script = script.replace("{0}", EASMgmtConstants.getEASserverDetailsResultFile(easServerID));
        return script;
    }
    
    public String getEODetailsScript(final Long easServerID) {
        String script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_ONLINE_DETAILS_SCRIPT);
        script = script.replace("{0}", EASMgmtConstants.getEASserverDetailsResultFile(easServerID));
        return script;
    }
    
    public String getFullSyncScript(final Long easServerID, final int exchangeServerVersion) {
        String script = null;
        switch (exchangeServerVersion) {
            case 0: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_ONLINE_SYNC_SCRIPT);
                break;
            }
            case 14: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_10_SYNC_SCRIPT);
                break;
            }
            case 15: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_13_SYNC_SCRIPT);
                break;
            }
            case 16: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_16_SYNC_SCRIPT);
                break;
            }
            case 19: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_19_SYNC_SCRIPT);
                break;
            }
            default: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_10_SYNC_SCRIPT);
                break;
            }
        }
        script = script.replace("{0}", EASMgmtConstants.getDedicatedFolderPath(easServerID) + File.separator);
        script = script.replace("{1}", String.valueOf(100));
        script = script.replace("{2}", "END_OF_RESPONSE_FILE_NAME");
        return script;
    }
    
    public String getConditionalAccessScript(final JSONObject easTaskProps, final Integer exchangeServerVersion) {
        final Long easServerID = (Long)easTaskProps.get((Object)"EAS_SERVER_ID");
        final String fileAddress = (String)easTaskProps.getOrDefault((Object)"fileAddress", (Object)"");
        String script = null;
        switch (exchangeServerVersion) {
            case 0: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_ONLINE_CONDITIONAL_ACCESS_SCRIPT);
                break;
            }
            case 14: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_10_CONDITIONAL_ACCESS_SCRIPT);
                break;
            }
            case 15: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_13_CONDITIONAL_ACCESS_SCRIPT);
                break;
            }
            case 16: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_16_CONDITIONAL_ACCESS_SCRIPT);
                break;
            }
            case 19: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_19_CONDITIONAL_ACCESS_SCRIPT);
                break;
            }
            default: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_10_CONDITIONAL_ACCESS_SCRIPT);
                break;
            }
        }
        script = script.replace("{0}", fileAddress);
        script = script.replace("{1}", EASMgmtConstants.getDedicatedFolderPath(easServerID) + File.separator);
        script = script.replace("{2}", "EMAIL_ADDRESS");
        script = script.replace("{3}", "TO_BE_ALLOWED");
        script = script.replace("{4}", "EAS_DEVICE_IDENTIFIER");
        script = script.replace("{5}", "IS_MANAGED_USER_MAILBOX_EAS_GRACED");
        script = script.replace("{6}", "TO_BE_NOT_ALOLWED");
        script = script.replace("{7}", "TO_BE_BLOCKED");
        script = script.replace("{8}", String.valueOf(100));
        script = script.replace("{9}", "END_OF_RESPONSE_FILE_NAME");
        script = script.replace("{10}", "CLEAR_BLOCK_LIST");
        return script;
    }
    
    public String getDeleteDeviceScript(final Integer exchangeServerVersion, final String deviceGUID, final Long esMailboxDeviceID, final String expectedResultFileAddress) {
        String script = null;
        switch (exchangeServerVersion) {
            case 0: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_ONLINE_DELETE_DEVICE_SCRIPT);
                break;
            }
            case 14: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_10_DELETE_DEVICE_SCRIPT);
                break;
            }
            case 15: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_13_DELETE_DEVICE_SCRIPT);
                break;
            }
            case 16: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_16_DELETE_DEVICE_SCRIPT);
                break;
            }
            case 19: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_19_DELETE_DEVICE_SCRIPT);
                break;
            }
            default: {
                script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.EXCHANGE_SERVER_10_DELETE_DEVICE_SCRIPT);
                break;
            }
        }
        script = script.replace("{0}", deviceGUID);
        script = script.replace("{1}", esMailboxDeviceID.toString());
        script = script.replace("{2}", expectedResultFileAddress);
        return script;
    }
    
    public String closeSessionScript() {
        String script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.CLOSE_SESSION_SCRIPT);
        final String connectionURI = (String)EASMgmtDataHandler.getInstance().getExchangeServerDetails(false).get((Object)"CONNECTION_URI");
        script = script.replace("{0}", connectionURI);
        return script;
    }
    
    public String getEXOV2ModuleInstallationScript(final long esServerID, final String basePath, final List<String> responseFiles) {
        String script = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(EASMgmtConstants.INSTALL_EXO_V2_SCRIPT);
        script = script.replace("{0}", basePath + responseFiles.get(0));
        script = script.replace("{1}", basePath + responseFiles.get(1));
        script = script.replace("{2}", basePath + responseFiles.get(2));
        script = script.replace("{3}", basePath + responseFiles.get(3));
        script = script.replace("{4}", EASMgmtConstants.getExceptionTypeFileAddress(esServerID));
        return script;
    }
    
    static {
        PSSCMDGenerator.easScriptGenerator = null;
    }
}
