package com.me.ems.summaryserver.common.util;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class ProbePropertyUtil
{
    private static Logger logger;
    
    public static String getProbeProperty(final String propertyName, final Long probeID) {
        try {
            Criteria criteria = new Criteria(Column.getColumn("ProbeProperties", "PROPERTY_NAME"), (Object)propertyName, 0, false);
            criteria = criteria.and(new Criteria(Column.getColumn("ProbeProperties", "PROBE_ID"), (Object)probeID, 0));
            final DataObject probePropsDO = DataAccess.get("ProbeProperties", criteria);
            final Row probePropsRow = probePropsDO.getRow("ProbeProperties");
            if (probePropsRow == null) {
                return null;
            }
            final String propertyValue = (String)probePropsRow.get("PROPERTY_VALUE");
            return propertyValue;
        }
        catch (final Exception ex) {
            ProbePropertyUtil.logger.log(Level.WARNING, "Caught exception while retrieving Probe property:" + propertyName + " from DB.", ex);
            return null;
        }
    }
    
    public static List<String> getAllAvailableProbeProperty(final String propertyName) {
        final List<String> result = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ProbeProperties", "PROPERTY_NAME"), (Object)propertyName, 0, false);
            final DataObject probePropsDO = DataAccess.get("ProbeProperties", criteria);
            final Iterator rows = probePropsDO.getRows("ProbeProperties");
            while (rows.hasNext()) {
                final Row probePropsRow = rows.next();
                result.add((String)probePropsRow.get("PROPERTY_VALUE"));
            }
        }
        catch (final Exception ex) {
            ProbePropertyUtil.logger.log(Level.WARNING, "Caught exception while retrieving Probe property:" + propertyName + " from DB.", ex);
        }
        return result;
    }
    
    public static void updateProbeProperty(final String propertyName, final String propertyValue) {
        final Long probeId = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
        try {
            Criteria criteria = new Criteria(Column.getColumn("ProbeProperties", "PROPERTY_NAME"), (Object)propertyName, 0, false);
            criteria = criteria.and(new Criteria(Column.getColumn("ProbeProperties", "PROBE_ID"), (Object)probeId, 0));
            final DataObject probePropsDO = DataAccess.get("ProbeProperties", criteria);
            Row probePropsRow = probePropsDO.getRow("ProbeProperties");
            if (probePropsRow == null) {
                probePropsRow = new Row("ProbeProperties");
                probePropsRow.set("PROPERTY_NAME", (Object)propertyName);
                probePropsRow.set("PROPERTY_VALUE", (Object)propertyValue);
                probePropsRow.set("DB_UPDATED_TIME", (Object)SyMUtil.getCurrentTime());
                probePropsRow.set("PROBE_ID", (Object)probeId);
                probePropsDO.addRow(probePropsRow);
                ProbePropertyUtil.logger.log(Level.FINER, "property added in DB:- param name: " + propertyName + "  param value: " + propertyValue);
            }
            else {
                probePropsRow.set("PROPERTY_VALUE", (Object)propertyValue);
                probePropsRow.set("DB_UPDATED_TIME", (Object)SyMUtil.getCurrentTime());
                probePropsDO.updateRow(probePropsRow);
                ProbePropertyUtil.logger.log(Level.FINER, "property updated in DB:- param name: " + propertyName + "  param value: " + propertyValue);
            }
            SyMUtil.getPersistence().update(probePropsDO);
        }
        catch (final Exception ex) {
            ProbePropertyUtil.logger.log(Level.WARNING, "Caught exception while updating probe property:" + propertyName + " in DB.", ex);
        }
    }
    
    public static void deleteProbeProperty(final String propertyName, final Long probeID) {
        try {
            Criteria criteria = new Criteria(Column.getColumn("ProbeProperties", "PROPERTY_NAME"), (Object)propertyName, 0, false);
            criteria = criteria.and(new Criteria(Column.getColumn("ProbeProperties", "PROBE_ID"), (Object)probeID, 0));
            SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            ProbePropertyUtil.logger.log(Level.WARNING, "Caught exception while deleting probe property:" + propertyName + " from DB.", ex);
        }
    }
    
    static {
        ProbePropertyUtil.logger = Logger.getLogger(ProbePropertyUtil.class.getName());
    }
}
