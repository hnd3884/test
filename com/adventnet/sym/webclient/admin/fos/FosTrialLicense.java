package com.adventnet.sym.webclient.admin.fos;

import java.text.DateFormat;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.adventnet.sym.server.fos.FailoverServerUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.security.CommonCryptoImpl;
import java.util.logging.Logger;

public class FosTrialLicense
{
    private static Logger logger;
    private static CommonCryptoImpl crypt;
    
    public static Boolean canOptTrial() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("R")) {
            return !isFosTrialFlagEnabled();
        }
        return Boolean.FALSE;
    }
    
    public static Boolean isFosTrialFlagEnabled() {
        try {
            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            if (new File(confFile).exists()) {
                final Properties fosProps = FileAccessUtil.readProperties(confFile);
                if (fosProps != null) {
                    final String isFosTrialed = fosProps.getProperty("isFosTrialed");
                    if (isFosTrialed != null) {
                        if (FosTrialLicense.crypt.decrypt(isFosTrialed).equalsIgnoreCase("true")) {
                            FosTrialLicense.logger.log(Level.INFO, " trial license available - true");
                            return Boolean.TRUE;
                        }
                        FosTrialLicense.logger.log(Level.INFO, " trial license available - false");
                        return Boolean.FALSE;
                    }
                }
            }
        }
        catch (final Exception exc) {
            FosTrialLicense.logger.log(Level.WARNING, " error while checking FOS trial enabled flag");
        }
        return Boolean.FALSE;
    }
    
    public static boolean getFosTrial() {
        try {
            FosTrialLicense.logger.log(Level.INFO, " FOS Trial license adapter");
            FailoverServerUtil.cleanUpFosEntries();
            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            if (new File(confFile).exists()) {
                final Properties fosProps = FileAccessUtil.readProperties(confFile);
                if (fosProps != null && fosProps.getProperty("FosTrialPeriod") != null && fosProps.getProperty("isFosTrialed") != null) {
                    fosProps.setProperty("isFosTrialed", FosTrialLicense.crypt.encrypt("true"));
                    final String encTrialperiod = fosProps.getProperty("FosTrialPeriod");
                    final int trialdays = Integer.parseInt(FosTrialLicense.crypt.decrypt(encTrialperiod));
                    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    final Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.add(5, trialdays);
                    final String dateString = sdf.format(c.getTime());
                    fosProps.setProperty("FosTrialValidity", FosTrialLicense.crypt.encrypt(dateString));
                    FileAccessUtil.storeProperties(fosProps, confFile, true);
                    FosTrialLicense.logger.log(Level.INFO, "Trial Opted - validity updated {0}", fosProps.toString());
                    return Boolean.TRUE;
                }
                FosTrialLicense.logger.log(Level.INFO, " FOS properties trialPeriod not available");
            }
        }
        catch (final Exception exc) {
            FosTrialLicense.logger.log(Level.WARNING, " error while getting FOS trial service {0}", exc.getMessage());
        }
        return Boolean.FALSE;
    }
    
    public static String getFosTrialValidity() throws Exception {
        String fosTrialValidity = null;
        final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
        if (new File(confFile).exists()) {
            final Properties fosProps = FileAccessUtil.readProperties(confFile);
            if (fosProps != null) {
                fosTrialValidity = fosProps.getProperty("FosTrialValidity");
                if (fosTrialValidity != null) {
                    return FosTrialLicense.crypt.decrypt(fosTrialValidity);
                }
            }
        }
        return fosTrialValidity;
    }
    
    public static long getFosTrialExpiryPeriod() {
        long dateDiff = -1L;
        try {
            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            if (new File(confFile).exists()) {
                final Properties fosProps = FileAccessUtil.readProperties(confFile);
                if (fosProps != null) {
                    final String fosTrialValidity = getFosTrialValidity();
                    if (fosTrialValidity != null) {
                        final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        final Date date = formatter.parse(fosTrialValidity);
                        final Date today = Calendar.getInstance().getTime();
                        dateDiff = SyMUtil.getDateDiff(today.getTime(), date.getTime());
                        FosTrialLicense.logger.log(Level.INFO, "FOS Trial validity {0}", dateDiff);
                        return dateDiff;
                    }
                }
            }
        }
        catch (final Exception exc) {
            FosTrialLicense.logger.log(Level.WARNING, " error while fetch FOS trial license period ", exc);
        }
        FosTrialLicense.logger.log(Level.INFO, " DB FOS Trial expiry period {0}", dateDiff);
        return dateDiff;
    }
    
    static {
        FosTrialLicense.logger = Logger.getLogger(FosTrialLicense.class.getName());
        FosTrialLicense.crypt = new CommonCryptoImpl();
    }
}
