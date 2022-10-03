package com.me.devicemanagement.onpremise.start.util;

import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import java.io.File;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.start.DCConsoleOut;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChangeShareAccessUtil
{
    private static Logger logger;
    
    public static void main(final String[] args) {
        try {
            DCLogUtil.initLogger();
            ChangeShareAccessUtil.logger.log(Level.INFO, "Going to change SWRepository/PatchStore Location with the values " + args[0]);
            final String[] splitValues = checkCommandValues(args);
            changeShareAccessLocation(splitValues);
        }
        catch (final Exception ex) {
            DCConsoleOut.println("\n" + ex.getMessage() + "\n" + args[0]);
            System.exit(1);
        }
    }
    
    private static String[] checkCommandValues(final String[] parameters) throws Exception {
        String[] splitValues = new String[0];
        String[] splitAllValues = new String[4];
        int splitAllValuesIndex = 0;
        ChangeShareAccessUtil.logger.log(Level.INFO, "Before changing the location,Need to Check received commands are valid or not.");
        if (parameters.length > 0) {
            final String params = parameters[0];
            commandValidation(params);
            if (params.contains("=")) {
                if (params.contains(",")) {
                    final String[] splitParams = params.split(",");
                    for (int index = 0; index < splitParams.length; ++index) {
                        if (splitParams[index].contains("=")) {
                            splitValues = splitParams[index].split("=");
                            int splitIndex = 0;
                            while (splitIndex < splitValues.length) {
                                try {
                                    splitAllValues[splitAllValuesIndex] = splitValues[splitIndex];
                                    ++splitIndex;
                                    ++splitAllValuesIndex;
                                }
                                catch (final Exception ex) {
                                    ChangeShareAccessUtil.logger.log(Level.WARNING, "Exception occurred ", ex);
                                }
                            }
                        }
                        else {
                            invalidCommandException(parameters[0]);
                        }
                        ChangeShareAccessUtil.logger.log(Level.WARNING, "index " + index);
                    }
                }
                else {
                    splitAllValues = params.split("=");
                }
            }
        }
        else {
            invalidCommandException(parameters[0]);
        }
        return splitAllValues;
    }
    
    private static void changeShareAccessLocation(final String[] values) throws Exception {
        final String serverHome = System.getProperty("server.home");
        final HashMap hm = new HashMap();
        final String SWRepository = "SWRepository";
        final String store = "PatchStore";
        boolean duplicateFlagforSW = false;
        boolean duplicateFlagforStore = false;
        if (values.length > 0) {
            try {
                for (int index = 0; index < values.length; index += 2) {
                    final String key = values[index];
                    final String location = values[index + 1];
                    if (!key.equalsIgnoreCase(SWRepository) && !key.equalsIgnoreCase(store)) {
                        invalidCommandException(values[0]);
                    }
                    if (key.equalsIgnoreCase(SWRepository)) {
                        if (!duplicateFlagforSW) {
                            duplicateFlagforSW = true;
                        }
                        else {
                            duplicateCommandException(SWRepository);
                        }
                    }
                    if (key.equalsIgnoreCase(store)) {
                        if (!duplicateFlagforStore) {
                            duplicateFlagforStore = true;
                        }
                        else {
                            duplicateCommandException(store);
                        }
                    }
                    ChangeShareAccessUtil.logger.log(Level.WARNING, "Received command is valid. Going to change the location.");
                    if (location.equalsIgnoreCase("default")) {
                        hm.put(key, location);
                    }
                    else if (!location.equalsIgnoreCase("default") && WebServerUtil.hasWriteAccess(location)) {
                        hm.put(key, location);
                    }
                    else {
                        final String Exception = key + " location " + location + " was not reachable." + "\nProvide a reachable location with the necessary read and write permission for everyone.";
                        ChangeShareAccessUtil.logger.log(Level.WARNING, Exception);
                        DCConsoleOut.println(Exception);
                        System.exit(1);
                    }
                }
                boolean swRepositoryChangeFlag = false;
                boolean patchStoreChangeFlag = false;
                String swRepositoryCopyMsg = "Before starting the server, you must copy the data from the old Software Repository Location to ";
                String patchStoreCopyMsg = "Before starting the server, you must copy the data from the old PatchStore Location to ";
                final Properties webServerProps = WebServerUtil.getWebServerSettings();
                final Set<String> keys = hm.keySet();
                for (final String key2 : keys) {
                    if (key2.equalsIgnoreCase("SWRepository")) {
                        final String SWRepositoryLocation = hm.get(key2).toString();
                        if (SWRepositoryLocation.equalsIgnoreCase("default")) {
                            webServerProps.setProperty("swrepository.loc", (serverHome + File.separator + WebServerUtil.SWREPOSITROY_DEFAULT_LOCATION).replaceAll("\\\\", "/"));
                        }
                        else {
                            webServerProps.setProperty("swrepository.loc", SWRepositoryLocation.replaceAll("\\\\", "/"));
                        }
                        final String swRepositorySuccessMsg = "SW Repository location changed to " + webServerProps.getProperty("swrepository.loc") + " Successfully. ";
                        swRepositoryChangeFlag = true;
                        swRepositoryCopyMsg = swRepositoryCopyMsg + " " + webServerProps.getProperty("swrepository.loc");
                        ChangeShareAccessUtil.logger.log(Level.INFO, swRepositorySuccessMsg);
                        DCConsoleOut.println(swRepositorySuccessMsg);
                    }
                    if (key2.equalsIgnoreCase("PatchStore")) {
                        final String patchStoreLocation = hm.get(key2).toString();
                        if (patchStoreLocation.equalsIgnoreCase("default")) {
                            webServerProps.setProperty("store.loc", (serverHome + File.separator + WebServerUtil.PATCH_STORE_DEFAULT_LOCATION).replaceAll("\\\\", "/"));
                        }
                        else {
                            webServerProps.setProperty("store.loc", patchStoreLocation.replaceAll("\\\\", "/"));
                        }
                        final String storeSuccessMsg = "Patch Store location changed to " + webServerProps.getProperty("store.loc") + " Successfully. ";
                        patchStoreChangeFlag = true;
                        patchStoreCopyMsg = patchStoreCopyMsg + " " + webServerProps.getProperty("store.loc");
                        ChangeShareAccessUtil.logger.log(Level.INFO, storeSuccessMsg);
                        DCConsoleOut.println(storeSuccessMsg);
                    }
                }
                WebServerUtil.storeProperWebServerSettings(webServerProps);
                warningPrint(swRepositoryChangeFlag, swRepositoryCopyMsg, patchStoreChangeFlag, patchStoreCopyMsg);
            }
            catch (final Exception ex) {
                ChangeShareAccessUtil.logger.log(Level.WARNING, "Please contact support@zohocorp.com. We Unable to check reachability of the new location due to the Exception : ", ex);
                throw new Exception("Unable to check reachability of the new location. Please See <server-home>\\logs\\changeShareAccessLocation.log for finding more information about this issue.");
            }
        }
    }
    
    private static void commandValidation(final String params) throws Exception {
        final int equalCount = params.split("=").length - 1;
        final int commaCount = params.split(",").length - 1;
        if (equalCount < 1 || commaCount > 1) {
            invalidCommandException(params);
        }
        else if (equalCount > 1 && commaCount < 1) {
            invalidCommandException(params);
        }
        else if (equalCount > 2) {
            invalidCommandException(params);
        }
    }
    
    private static void invalidCommandException(final String command) throws Exception {
        final String Exception = "Unable to process the command: " + command + "\nThe command should be like: changeShareAccess.bat [options]\n[options] = SWRepository=location | PatchStore=location | SWRepository=location,PatchStore=location";
        ChangeShareAccessUtil.logger.log(Level.WARNING, Exception);
        DCConsoleOut.println(Exception);
        System.exit(1);
    }
    
    private static void duplicateCommandException(final String command) throws Exception {
        final String Exception = "Found Multiple entris of " + command + " \nThe command should be like: changeShareAccess.bat [options]\n[options] = SWRepository=location | PatchStore=location | SWRepository=location,PatchStore=location";
        ChangeShareAccessUtil.logger.log(Level.WARNING, Exception);
        DCConsoleOut.println(Exception);
        System.exit(1);
    }
    
    private static void warningPrint(final boolean swRepositoryChangeFlag, final String swRepositoryCopyMsg, final boolean patchStoreChangeFlag, final String patchStoreCopyMsg) {
        final String warningHeader = "************************************Warning************************************";
        final String warningFooter = "*******************************************************************************";
        try {
            if (swRepositoryChangeFlag || patchStoreChangeFlag) {
                DCConsoleOut.println("");
                DCConsoleOut.println(warningHeader);
                if (patchStoreChangeFlag) {
                    DCConsoleOut.println("");
                    DCConsoleOut.println(patchStoreCopyMsg);
                    ChangeShareAccessUtil.logger.log(Level.INFO, patchStoreCopyMsg);
                }
                if (swRepositoryChangeFlag) {
                    DCConsoleOut.println("");
                    DCConsoleOut.println(swRepositoryCopyMsg);
                    ChangeShareAccessUtil.logger.log(Level.INFO, swRepositoryCopyMsg);
                }
                DCConsoleOut.println("");
                DCConsoleOut.println(warningFooter);
                DCConsoleOut.println("");
            }
        }
        catch (final Exception ex) {
            ChangeShareAccessUtil.logger.log(Level.WARNING, "Exception while showing warning message before starting the server ", ex);
        }
    }
    
    static {
        ChangeShareAccessUtil.logger = Logger.getLogger(ChangeShareAccessUtil.class.getName());
    }
}
