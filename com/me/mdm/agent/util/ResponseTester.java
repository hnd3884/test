package com.me.mdm.agent.util;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;

public class ResponseTester
{
    public static String getTestResponseString(final String functionalityFileName) throws Exception {
        try {
            final String path = MDMMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "test_responses" + File.separator + functionalityFileName;
            final byte[] encoded = Files.readAllBytes(Paths.get(path, new String[0]));
            final String testResponse = new String(encoded, "UTF-8");
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Printing getTestResponseString() {0}", testResponse);
            return testResponse;
        }
        catch (final Exception e) {
            throw new Exception("Provisoning Profile Test Response Processing Failed. See trace: ", e);
        }
    }
    
    public static Boolean isTestMode(final String functionalityFlag) {
        try {
            return Boolean.parseBoolean(MDMUtil.getInstance().getMDMApplicationProperties().getProperty(functionalityFlag, "false"));
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while isTestMode ", e);
            return Boolean.FALSE;
        }
    }
    
    public interface VPP_SERVICES_RESPONSE
    {
        public static final String FLAG = "VPPResponseTestMode";
    }
    
    public interface PROFILES_LIST
    {
        public static final String FLAG = "ProfileListResponseTestMode";
        public static final String FILENAME = "profilelisttestresponse.xml";
    }
    
    public interface PROVISIONING_PROFILES_LIST
    {
        public static final String FLAG = "ProvProfileListResponseTestMode";
        public static final String FILENAME = "provprofiletestresponse.xml";
    }
}
