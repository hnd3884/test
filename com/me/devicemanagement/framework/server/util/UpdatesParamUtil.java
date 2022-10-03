package com.me.devicemanagement.framework.server.util;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class UpdatesParamUtil
{
    private static Logger logger;
    private static UpdatesParamUtil updatesParamUtil;
    
    protected UpdatesParamUtil() {
    }
    
    public static UpdatesParamUtil getInstance() {
        if (UpdatesParamUtil.updatesParamUtil == null) {
            UpdatesParamUtil.updatesParamUtil = new UpdatesParamUtil();
        }
        return UpdatesParamUtil.updatesParamUtil;
    }
    
    public static String getUpdParameter(final String paramKey) {
        try {
            final DataObject updParamsDO = DataAccess.get("UpdateParams", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("UpdateParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            final Row updParamRow = (updParamsDO == null) ? null : updParamsDO.getRow("UpdateParams", criteria);
            if (updParamRow == null) {
                return null;
            }
            final String paramValue = (String)updParamRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            UpdatesParamUtil.logger.log(Level.WARNING, "Caught exception while retrieving SyM Parameter:" + paramKey + " from DB.", ex);
            return null;
        }
    }
    
    public static void updateUpdParams(final String paramName, final String paramValue) {
        try {
            final DataObject updParamsDO = DataAccess.get("UpdateParams", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("UpdateParams", "PARAM_NAME"), (Object)paramName, 0, false);
            Row updParamRow = (updParamsDO == null) ? null : updParamsDO.getRow("UpdateParams", criteria);
            if (updParamRow == null) {
                updParamRow = new Row("UpdateParams");
                updParamRow.set("PARAM_NAME", (Object)paramName);
                updParamRow.set("PARAM_VALUE", (Object)paramValue);
                updParamsDO.addRow(updParamRow);
                UpdatesParamUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                updParamRow.set("PARAM_VALUE", (Object)paramValue);
                updParamsDO.updateRow(updParamRow);
                UpdatesParamUtil.logger.log(Level.FINER, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            SyMUtil.getPersistence().update(updParamsDO);
        }
        catch (final Exception ex) {
            UpdatesParamUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter:" + paramName + " in DB.", ex);
        }
    }
    
    public static void deleteUpdParameter(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("UpdateParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            UpdatesParamUtil.logger.log(Level.WARNING, "Caught exception while deleting Upd Parameter:" + paramKey + " from DB.", ex);
        }
    }
    
    static {
        UpdatesParamUtil.logger = Logger.getLogger(UpdatesParamUtil.class.getName());
        UpdatesParamUtil.updatesParamUtil = null;
    }
}
