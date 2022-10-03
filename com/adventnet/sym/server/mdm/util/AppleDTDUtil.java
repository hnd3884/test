package com.adventnet.sym.server.mdm.util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class AppleDTDUtil
{
    private static byte[] appleDtd;
    private static Logger logger;
    public static String dtdUrl_1_0;
    
    private static String getAppleDtdFilePath() {
        final String appleDtdPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDM" + File.separator + "dtd" + File.separator + "propertyList_1_0.dtd";
        return appleDtdPath;
    }
    
    public static byte[] retrieveDataDefinition() throws IOException {
        AppleDTDUtil.logger.log(Level.INFO, "[Apple DTD] Loading DTD from file");
        final File appleDtdFile = new File(getAppleDtdFilePath());
        final byte[] appleDtd = Files.readAllBytes(appleDtdFile.toPath());
        AppleDTDUtil.logger.log(Level.INFO, "[Apple DTD] Successfully loaded Apple DTD");
        return appleDtd;
    }
    
    public static byte[] getDtdAsBytes() throws IOException {
        if (AppleDTDUtil.appleDtd == null) {
            AppleDTDUtil.logger.log(Level.INFO, "[Apple DTD] Apple DTD is Null. Going to load from file");
            AppleDTDUtil.appleDtd = retrieveDataDefinition();
        }
        AppleDTDUtil.logger.log(Level.INFO, "[Apple DTD] Returning Apple DTD as Bytes");
        return AppleDTDUtil.appleDtd;
    }
    
    static {
        AppleDTDUtil.appleDtd = null;
        AppleDTDUtil.logger = Logger.getLogger("MDMLogger");
        AppleDTDUtil.dtdUrl_1_0 = "http://www.apple.com/DTDs/PropertyList-1.0.dtd";
    }
}
