package com.me.devicemanagement.framework.server.authentication;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class UserMgmtUtil
{
    private static Logger logger;
    
    public static String getUserMgmtParameter(final String paramKey) {
        try {
            final Column col = Column.getColumn("UserMgmtParams", "PARAMS_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramKey, 0, false);
            final DataObject sharefolderDO = DataAccess.get("UserMgmtParams", criteria);
            if (sharefolderDO.isEmpty()) {
                return null;
            }
            final Row sfrow = sharefolderDO.getFirstRow("UserMgmtParams");
            final String paramValue = (String)sfrow.get("PARAMS_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            UserMgmtUtil.logger.log(Level.WARNING, "Caught exception while retrieving SoM Parameter:" + paramKey + " from DB.", ex);
            return null;
        }
    }
    
    public static void updateUserMgmtParameter(final String paramName, final String paramValue) {
        try {
            final Column col = Column.getColumn("UserMgmtParams", "PARAMS_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject userMgmtParamDO = DataAccess.get("UserMgmtParams", criteria);
            if (userMgmtParamDO.isEmpty()) {
                final Row userMgmtRow = new Row("UserMgmtParams");
                userMgmtRow.set("PARAMS_NAME", (Object)paramName);
                userMgmtRow.set("PARAMS_VALUE", (Object)paramValue);
                userMgmtParamDO.addRow(userMgmtRow);
                DataAccess.add(userMgmtParamDO);
                UserMgmtUtil.logger.log(Level.FINER, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                final Row userMgmtRow = userMgmtParamDO.getFirstRow("UserMgmtParams");
                userMgmtRow.set("PARAMS_VALUE", (Object)paramValue);
                userMgmtParamDO.updateRow(userMgmtRow);
                DataAccess.update(userMgmtParamDO);
                UserMgmtUtil.logger.log(Level.FINER, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
        }
        catch (final Exception ex) {
            UserMgmtUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter:" + paramName + " in DB.", ex);
        }
    }
    
    public static void addOrUpdateUserMgmtParameters(final Map<String, Object> dataMap) {
        try {
            final Set<String> paramNames = dataMap.keySet();
            final Column col = Column.getColumn("UserMgmtParams", "PARAMS_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramNames.toArray(), 8, false);
            final DataObject userMgmtParamDO = DataAccess.get("UserMgmtParams", criteria);
            for (final String paramName : paramNames) {
                final Row userMgmtRow = userMgmtParamDO.getRow("UserMgmtParams", new Criteria(col, (Object)paramName, 0, false));
                if (userMgmtRow != null) {
                    UserMgmtUtil.logger.log(Level.INFO, "param update", paramName);
                    userMgmtRow.set("PARAMS_VALUE", dataMap.get(paramName));
                    userMgmtParamDO.updateRow(userMgmtRow);
                }
                else {
                    UserMgmtUtil.logger.log(Level.INFO, "param add", paramName);
                    final Row addingRow = new Row("UserMgmtParams");
                    addingRow.set("PARAMS_NAME", (Object)paramName);
                    addingRow.set("PARAMS_VALUE", dataMap.get(paramName));
                    userMgmtParamDO.addRow(addingRow);
                }
            }
            DataAccess.update(userMgmtParamDO);
        }
        catch (final Exception ex) {
            UserMgmtUtil.logger.log(Level.SEVERE, "addOrUpdateUserMgmtParameter()- ", ex);
        }
    }
    
    public static void deleteUserMgmtParameter(final String[] paramName) {
        try {
            UserMgmtUtil.logger.log(Level.INFO, "deleteUserMgmtParameter()- params are :" + Arrays.toString(paramName));
            final Column col = Column.getColumn("UserMgmtParams", "PARAMS_NAME");
            if (paramName != null) {
                final Criteria criteria = new Criteria(col, (Object)paramName, 8, false);
                DataAccess.delete("UserMgmtParams", criteria);
            }
        }
        catch (final Exception ex) {
            UserMgmtUtil.logger.log(Level.WARNING, "Caught exception while deleting Parameter:" + paramName + " in DB.", ex);
        }
    }
    
    public static Map getUserMgmtParams(final Object[] paramNames) {
        UserMgmtUtil.logger.log(Level.INFO, "getUserMgmtParams():- params are :" + Arrays.toString(paramNames));
        final Map userMgmtParams = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)paramNames, 8, false);
            final DataObject userMgmtParamDO = SyMUtil.getPersistence().get("UserMgmtParams", criteria);
            if (!userMgmtParamDO.isEmpty()) {
                final Iterator rows = userMgmtParamDO.getRows("UserMgmtParams");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    userMgmtParams.put(row.get("PARAMS_NAME"), row.get("PARAMS_VALUE"));
                }
            }
        }
        catch (final Exception ex) {
            UserMgmtUtil.logger.log(Level.WARNING, "getUserMgmtParamValues():- Caught exception while retrieving userMgmtParams:", ex);
        }
        return userMgmtParams;
    }
    
    static {
        UserMgmtUtil.logger = Logger.getLogger(UserMgmtUtil.class.getName());
    }
}
