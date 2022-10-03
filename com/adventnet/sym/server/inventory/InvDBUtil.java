package com.adventnet.sym.server.inventory;

import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class InvDBUtil
{
    private static Logger logger;
    private static String sourceClass;
    private static InvDBUtil dbUtil;
    
    private InvDBUtil() {
    }
    
    public static InvDBUtil getInstance() {
        if (InvDBUtil.dbUtil == null) {
            InvDBUtil.dbUtil = new InvDBUtil();
        }
        return InvDBUtil.dbUtil;
    }
    
    public void updateInvParameter(final String paramName, final String paramValue) {
        final String sourceMethod = "updateInvParameter";
        try {
            final Column col = Column.getColumn("InvParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject invParamDO = SyMUtil.getPersistence().get("InvParams", criteria);
            if (invParamDO.isEmpty()) {
                final Row invParamRow = new Row("InvParams");
                invParamRow.set("PARAM_NAME", (Object)paramName);
                invParamRow.set("PARAM_VALUE", (Object)paramValue);
                invParamDO.addRow(invParamRow);
                SyMUtil.getPersistence().add(invParamDO);
                SyMLogger.debug(InvDBUtil.logger, InvDBUtil.sourceClass, sourceMethod, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                final Row invParamRow = invParamDO.getFirstRow("InvParams");
                invParamRow.set("PARAM_VALUE", (Object)paramValue);
                invParamDO.updateRow(invParamRow);
                SyMUtil.getPersistence().update(invParamDO);
                SyMLogger.debug(InvDBUtil.logger, InvDBUtil.sourceClass, sourceMethod, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(InvDBUtil.logger, InvDBUtil.sourceClass, sourceMethod, "Caught exception while updating Parameter:" + paramName + " in DB.", (Throwable)ex);
        }
    }
    
    public String getInvParameter(final Long technicianID, final String paramKey) {
        final String sourceMethod = "getInvParameter";
        try {
            Criteria criteria = new Criteria(Column.getColumn("InvTechParams", "PARAM_NAME"), (Object)paramKey, 0, false);
            criteria = criteria.and(new Criteria(Column.getColumn("InvTechParams", "TECH_ID"), (Object)technicianID, 0));
            final DataObject dobj = SyMUtil.getPersistence().get("InvTechParams", criteria);
            if (dobj.isEmpty()) {
                return null;
            }
            final Row row = dobj.getFirstRow("InvTechParams");
            final String paramValue = (String)row.get("PARAM_VALUE");
            SyMLogger.debug(InvDBUtil.logger, InvDBUtil.sourceClass, sourceMethod, "Value returned from getInvParameter for " + paramKey + " for the technician id " + technicianID + " is : " + paramValue);
            return paramValue;
        }
        catch (final Exception ex) {
            SyMLogger.error(InvDBUtil.logger, InvDBUtil.sourceClass, sourceMethod, "Exception while retrieving Inv Parameter :" + paramKey + " from DB.", (Throwable)ex);
            return null;
        }
    }
    
    static {
        InvDBUtil.logger = Logger.getLogger("InventoryLog");
        InvDBUtil.sourceClass = "InvDBUtil";
        InvDBUtil.dbUtil = null;
    }
}
