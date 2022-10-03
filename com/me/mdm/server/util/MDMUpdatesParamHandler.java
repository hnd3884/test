package com.me.mdm.server.util;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class MDMUpdatesParamHandler
{
    private static Logger logger;
    
    public static String getMDMUpdParameter(final String paramKey) {
        String paramValue = null;
        try {
            final DataObject updParamsDO = DataAccess.get("MDMUpdateParams", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("MDMUpdateParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            final Row updParamRow = (updParamsDO == null) ? null : updParamsDO.getRow("MDMUpdateParams", criteria);
            if (updParamRow == null) {
                return null;
            }
            paramValue = (String)updParamRow.get("PARAM_VALUE");
        }
        catch (final Exception ex) {
            MDMUpdatesParamHandler.logger.log(Level.WARNING, ex, () -> "Caught exception while retrieving MDMUpdates Parameter:" + s + " from DB.");
        }
        return paramValue;
    }
    
    public static void addorUpdateMDMUpdParams(final String paramName, final String paramValue) {
        try {
            final DataObject updParamsDO = DataAccess.get("MDMUpdateParams", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("MDMUpdateParams", "PARAM_NAME"), (Object)paramName, 0, false);
            Row updParamRow = (updParamsDO == null) ? null : updParamsDO.getRow("MDMUpdateParams", criteria);
            if (updParamRow == null) {
                updParamRow = new Row("MDMUpdateParams");
                updParamRow.set("PARAM_NAME", (Object)paramName);
                updParamRow.set("PARAM_VALUE", (Object)paramValue);
                updParamsDO.addRow(updParamRow);
                MDMUpdatesParamHandler.logger.log(Level.FINER, "Parameter added in DB:- param name: {0}  param value: {1}", new Object[] { paramName, paramValue });
            }
            else {
                updParamRow.set("PARAM_VALUE", (Object)paramValue);
                updParamsDO.updateRow(updParamRow);
                MDMUpdatesParamHandler.logger.log(Level.FINER, "Parameter updated in DB:- param name: {0}  param value: {1}", new Object[] { paramName, paramValue });
            }
            MDMUtil.getPersistence().update(updParamsDO);
        }
        catch (final Exception ex) {
            MDMUpdatesParamHandler.logger.log(Level.WARNING, ex, () -> "Caught exception while updating Parameter:" + s + " in DB.");
        }
    }
    
    public static void deleteMDMUpdParameter(final String paramKey) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MDMUpdateParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            MDMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            MDMUpdatesParamHandler.logger.log(Level.WARNING, ex, () -> "Caught exception while deleting Upd Parameter:" + s + " from DB.");
        }
    }
    
    static {
        MDMUpdatesParamHandler.logger = Logger.getLogger("MDMLogger");
    }
}
