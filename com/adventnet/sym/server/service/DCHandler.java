package com.adventnet.sym.server.service;

import com.adventnet.sym.server.admin.SoMUtil;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Locale;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Logger;

public class DCHandler
{
    private static Logger logger;
    
    private DCHandler() {
    }
    
    public static void initialize() {
        firstInitialize();
        initializeCommonEnv();
        SyMUtil.writeInstallPropsInFile();
    }
    
    private static void firstInitialize() {
        final String isclientdataavailable = System.getProperty("isClientDataAlreadyAvailable");
        DCHandler.logger.log(Level.INFO, "firstInitialize isClientDataAlreadyAvailable from property:  {0}", isclientdataavailable);
        if (isclientdataavailable != null && isclientdataavailable.equals("false")) {
            updateDefaultAdminTimeZone();
        }
    }
    
    private static void updateDefaultAdminTimeZone() {
        try {
            final Locale userLocale = I18NUtil.getDefaultLocale();
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUserProfile");
            updateQuery.setUpdateColumn("TIMEZONE", (Object)SyMUtil.getDefaultTimeZoneID());
            updateQuery.setUpdateColumn("LANGUAGE_CODE", (Object)userLocale.getLanguage());
            updateQuery.setUpdateColumn("COUNTRY_CODE", (Object)userLocale.getCountry());
            DataAccess.update(updateQuery);
        }
        catch (final Exception e) {
            DCHandler.logger.log(Level.SEVERE, "Exception while updatating default admin time zone value...", e);
        }
    }
    
    public static void destroy() {
    }
    
    private static void initializeCommonEnv() {
        try {
            SoMUtil.getInstance().updateDCServerAgentInfo();
        }
        catch (final Exception ex) {
            DCHandler.logger.log(Level.WARNING, "Caught exception while initializing common env from EnvSetter...", ex);
        }
    }
    
    static {
        DCHandler.logger = Logger.getLogger(DCHandler.class.getName());
    }
}
