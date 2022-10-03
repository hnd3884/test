package com.me.devicemanagement.onpremise.server.util;

import com.adventnet.persistence.DataAccessException;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.winaccess.WmiAccessProvider;
import java.util.logging.Logger;

public class AVTestUtil
{
    public static final int NOT_APPLICABLE = 0;
    public static final int NOT_DOWNLOADED_BAT = 1;
    public static final int SUCCESS = 2;
    public static final int SUCCESS_ON_NOTIFICATION = 3;
    public static final int FAILURE = 4;
    public static final int FAILURE_ON_NOTIFICATION = 5;
    public static final int NOT_ATTEMPTED_TEST = 6;
    public static final int TEST_TO_BE_REPEATED = 7;
    public static final int NO_AV_PRESENT = 8;
    public static final int AV_BANNER_NOT_DISPLAYED = 0;
    public static final int AV_INITIAL_BANNER = 1;
    public static final int AV_FAIL_CASE_BANNER = 2;
    public static final int AV_TEST_REPEAT_BANNER = 3;
    private static Logger logger;
    
    public static String avNameGetter() {
        try {
            AVTestUtil.logger.info("Getting names of AVs installed on server");
            final StringBuilder antiVirusStringBuilder = new StringBuilder();
            final Hashtable avDetails = WmiAccessProvider.getInstance().getAntiVirusDetails();
            if (avDetails != null && !avDetails.isEmpty()) {
                final Set keySet = avDetails.keySet();
                final Iterator iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    final String key = iterator.next().toString();
                    if (key.contains("AV_NAME")) {
                        final String avString = String.valueOf(avDetails.get(key));
                        antiVirusStringBuilder.append(avString).append(",");
                    }
                }
                if (antiVirusStringBuilder.lastIndexOf(",") != -1) {
                    return antiVirusStringBuilder.substring(0, antiVirusStringBuilder.lastIndexOf(","));
                }
                return antiVirusStringBuilder.toString();
            }
        }
        catch (final Exception e) {
            AVTestUtil.logger.log(Level.WARNING, "Error while getting av names", e);
        }
        return "";
    }
    
    public static int checkAvTestResultAndSetBanner() throws DataAccessException {
        AVTestUtil.logger.info("inside checkAvTestResultAndSetBanner() ");
        final File testBatFile = new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "Antivirus-Checker.bat");
        int status;
        if (!testBatFile.exists()) {
            AVTestUtil.logger.info("Batch file not downloaded");
            status = 1;
        }
        else {
            status = readAvTestOpData();
        }
        Boolean isFolderExcluded;
        if (status == 2 || status == 3) {
            AVTestUtil.logger.info("Test is successful");
            isFolderExcluded = true;
            SyMUtil.updateSyMParameter("AVbannerDisplay", Integer.toString(0));
        }
        else if (status == 4 || status == 5) {
            AVTestUtil.logger.info("Test has failed");
            isFolderExcluded = false;
            SyMUtil.updateSyMParameter("AVbannerDisplay", Integer.toString(2));
        }
        else if (status == 7) {
            isFolderExcluded = false;
            SyMUtil.updateSyMParameter("AVbannerDisplay", Integer.toString(3));
        }
        else {
            AVTestUtil.logger.info("User has not taken the test.");
            isFolderExcluded = false;
            SyMUtil.updateSyMParameter("AVbannerDisplay", Integer.toString(1));
        }
        writeDataToAVTestTable(isFolderExcluded, status);
        return status;
    }
    
    private static void writeDataToAVTestTable(final Boolean isFolderExcluded, final int status) {
    }
    
    private static int readAvTestOpData() {
        return 0;
    }
    
    static {
        AVTestUtil.logger = Logger.getLogger(AVTestUtil.class.getName());
    }
}
