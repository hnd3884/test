package com.me.devicemanagement.framework.server.common;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DMModuleHandler
{
    private static Logger logger;
    private static DMModuleHandler dmModuleHandler;
    private static Boolean isOSDEnabled;
    public static final String OSD_ENABLED = "enableOSD";
    
    public static DMModuleHandler getInstance() {
        if (DMModuleHandler.dmModuleHandler == null) {
            DMModuleHandler.dmModuleHandler = new DMModuleHandler();
        }
        return DMModuleHandler.dmModuleHandler;
    }
    
    public static boolean isOSDEnabled() {
        if (DMModuleHandler.isOSDEnabled == null) {
            try {
                DMModuleHandler.logger.log(Level.FINEST, "-----Inside isOSDEnabled method, isOSDEnabled value null. Hence reading from general_properties file and set the value");
                final String osdEnabledPropertyValue = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("enableOSD");
                updateDMModuleStateFromDB("OSDeployer", DMModuleHandler.isOSDEnabled = (osdEnabledPropertyValue != null && osdEnabledPropertyValue.contains("true") && getDMModuleStateFromDB("OSDeployer")));
            }
            catch (final Exception e) {
                DMModuleHandler.logger.log(Level.WARNING, "Exception while getting isOSDEnabled property value..", e);
            }
        }
        else {
            DMModuleHandler.isOSDEnabled = getDMModuleStateFromDB("OSDeployer");
        }
        return DMModuleHandler.isOSDEnabled;
    }
    
    public static void setIsOSDEnabled(final Boolean isOSDEnabled) {
        DMModuleHandler.isOSDEnabled = isOSDEnabled;
    }
    
    public static boolean getDMModuleStateFromDB(final String moduleName) {
        boolean dmModuleState = false;
        try {
            final DataObject dmModuleDO = SyMUtil.getPersistence().get("DMModule", new Criteria(Column.getColumn("DMModule", "MODULE_NAME"), (Object)moduleName, 0));
            if (!dmModuleDO.isEmpty()) {
                dmModuleState = Boolean.parseBoolean(dmModuleDO.getFirstValue("DMModule", "IS_ENABLED").toString());
            }
        }
        catch (final Exception e) {
            DMModuleHandler.logger.log(Level.WARNING, "Exception while getting Module state from DB..", e);
        }
        return dmModuleState;
    }
    
    public static void updateOSDModuleStateFromDB(final Boolean status) throws DataAccessException {
        updateDMModuleStateFromDB("OSDeployer", status);
    }
    
    private static void updateDMModuleStateFromDB(final String moduleName, final Boolean enable) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DMModule");
        updateQuery.setUpdateColumn("IS_ENABLED", (Object)enable);
        updateQuery.setCriteria(new Criteria(Column.getColumn("DMModule", "MODULE_NAME"), (Object)moduleName, 0));
        DataAccess.update(updateQuery);
    }
    
    static {
        DMModuleHandler.logger = Logger.getLogger(DMModuleHandler.class.getName());
        DMModuleHandler.dmModuleHandler = null;
        DMModuleHandler.isOSDEnabled = null;
    }
}
