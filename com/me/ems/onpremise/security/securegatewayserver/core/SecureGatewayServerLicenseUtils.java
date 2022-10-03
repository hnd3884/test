package com.me.ems.onpremise.security.securegatewayserver.core;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.text.ParseException;
import java.text.DateFormat;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.security.CommonCryptoImpl;
import java.util.logging.Logger;

public class SecureGatewayServerLicenseUtils
{
    private static final Logger LOGGER;
    private static CommonCryptoImpl crypt;
    
    public static Boolean canOptTrial() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("R")) {
            return !isSGSTrialFlagEnabled();
        }
        return Boolean.FALSE;
    }
    
    public static Boolean enableSGSTrial() {
        SecureGatewayServerLicenseUtils.LOGGER.log(Level.INFO, " SGS Trial license adapter");
        SecureGatewayServerPropertiesUtils.cleanUpSGSEntries();
        if (SecureGatewayServerPropertiesUtils.isSGSPropetiesNotEmpty() && SecureGatewayServerPropertiesUtils.getSGSProperty("FwsTrialPeriod") != null && SecureGatewayServerPropertiesUtils.getSGSProperty("isFwsTrialed") != null) {
            SecureGatewayServerPropertiesUtils.setSGSProperty("isFwsTrialed", SecureGatewayServerLicenseUtils.crypt.encrypt("true"));
            final String encryptedTrialperiod = SecureGatewayServerPropertiesUtils.getSGSProperty("FwsTrialPeriod");
            final int trialDays = Integer.parseInt(SecureGatewayServerLicenseUtils.crypt.decrypt(encryptedTrialperiod));
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(5, trialDays);
            final String dateString = sdf.format(calendar.getTime());
            SecureGatewayServerPropertiesUtils.setSGSProperty("FwsTrialValidity", SecureGatewayServerLicenseUtils.crypt.encrypt(dateString));
            FwsUtil.regenerateProps();
            SecureGatewayServerLicenseUtils.LOGGER.log(Level.INFO, "Trial Opted - validity updated " + SecureGatewayServerPropertiesUtils.getAllSGSProperties());
            return Boolean.TRUE;
        }
        SecureGatewayServerLicenseUtils.LOGGER.log(Level.INFO, " SGS properties trialPeriod not available");
        return Boolean.FALSE;
    }
    
    public static long getSGSTrialExpiryPeriod() throws ParseException {
        long dateDiff = -1L;
        if (SecureGatewayServerPropertiesUtils.isSGSPropetiesNotEmpty()) {
            final String sgsTrialValidity = getSGSTrialValidity();
            if (sgsTrialValidity != null) {
                final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                final Date date = formatter.parse(sgsTrialValidity);
                final Date today = Calendar.getInstance().getTime();
                dateDiff = SyMUtil.getDateDiff(today.getTime(), date.getTime());
                SecureGatewayServerLicenseUtils.LOGGER.log(Level.INFO, "SGS Trial validity " + dateDiff);
                return dateDiff;
            }
        }
        SecureGatewayServerLicenseUtils.LOGGER.log(Level.INFO, " DB SGS Trial expiry period " + dateDiff);
        return dateDiff;
    }
    
    public static String getSGSTrialValidity() {
        String sgsTrialValidity = null;
        if (SecureGatewayServerPropertiesUtils.isSGSPropetiesNotEmpty()) {
            sgsTrialValidity = SecureGatewayServerPropertiesUtils.getSGSProperty("FwsTrialValidity");
            if (sgsTrialValidity != null) {
                return SecureGatewayServerLicenseUtils.crypt.decrypt(sgsTrialValidity);
            }
        }
        return sgsTrialValidity;
    }
    
    public static Boolean isSGSTrialFlagEnabled() {
        if (SecureGatewayServerPropertiesUtils.isSGSPropetiesNotEmpty()) {
            final String isSGSTrialed = SecureGatewayServerPropertiesUtils.getSGSProperty("isFwsTrialed");
            if (isSGSTrialed != null) {
                if (SecureGatewayServerLicenseUtils.crypt.decrypt(isSGSTrialed).equalsIgnoreCase("true")) {
                    SecureGatewayServerLicenseUtils.LOGGER.log(Level.INFO, " trial license available - true");
                    return Boolean.TRUE;
                }
                SecureGatewayServerLicenseUtils.LOGGER.log(Level.INFO, " trial license available - false");
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }
    
    public static DataObject getServerParamsDO(final String paramName) throws Exception {
        final Column col = Column.getColumn("ServerParams", "PARAM_NAME");
        final Criteria crit = new Criteria(col, (Object)paramName, 2, false);
        final DataObject serverParamsDObj = SyMUtil.getPersistence().get("ServerParams", crit);
        return serverParamsDObj;
    }
    
    static {
        LOGGER = Logger.getLogger("SecurityLogger");
        SecureGatewayServerLicenseUtils.crypt = new CommonCryptoImpl();
    }
}
