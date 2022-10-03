package com.adventnet.sym.server.mdm.util;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class WakeUpUtil
{
    private static WakeUpUtil wakeUpUtil;
    private static final Integer WAKEUP_LOCK;
    private static Logger logger;
    public static final int YET_TO_WAKE = 0;
    public static final int WAKE_UP_SUCESS = 1;
    public static final int WAKE_UP_FAIL = 2;
    private static final int NO_OF_RECORD = 25;
    
    public static WakeUpUtil getInstance() {
        if (WakeUpUtil.wakeUpUtil == null) {
            WakeUpUtil.wakeUpUtil = new WakeUpUtil();
        }
        return WakeUpUtil.wakeUpUtil;
    }
    
    public static void addWakeupRequest(final Long resourceId, final int platformType) {
        try {
            final Criteria cRes = new Criteria(new Column("WakeUpDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            final DataObject dObj;
            synchronized (WakeUpUtil.WAKEUP_LOCK) {
                dObj = MDMUtil.getPersistence().get("WakeUpDevice", cRes);
            }
            if (dObj.isEmpty()) {
                final Row wakeUpRow = new Row("WakeUpDevice");
                wakeUpRow.set("RESOURCE_ID", (Object)resourceId);
                wakeUpRow.set("NOTIFICATION_TYPE", (Object)platformType);
                wakeUpRow.set("ADDED_TIME", (Object)MDMUtil.getCurrentTime());
                wakeUpRow.set("STATUS", (Object)0);
                dObj.addRow(wakeUpRow);
                synchronized (WakeUpUtil.WAKEUP_LOCK) {
                    MDMUtil.getPersistence().add(dObj);
                }
            }
        }
        catch (final Exception e) {
            WakeUpUtil.logger.log(Level.SEVERE, "Exception while adding the wakeup request", e);
        }
    }
    
    public static void updateWakeUpStatus(final List resourceList, final int status) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("WakeUpDevice");
            final Criteria cRes = new Criteria(new Column("WakeUpDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            uQuery.setCriteria(cRes);
            uQuery.setUpdateColumn("STATUS", (Object)status);
            synchronized (WakeUpUtil.WAKEUP_LOCK) {
                MDMUtil.getPersistence().update(uQuery);
            }
        }
        catch (final Exception e) {
            WakeUpUtil.logger.log(Level.SEVERE, "Exception while updating the wakeup status", e);
        }
    }
    
    public static void removeWakeUpRequest(final List resourceList) {
        final Criteria cRes = new Criteria(new Column("WakeUpDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        try {
            final DataObject dObj = MDMUtil.getPersistence().get("WakeUpDevice", cRes);
            if (!dObj.isEmpty()) {
                synchronized (WakeUpUtil.WAKEUP_LOCK) {
                    dObj.deleteRows("WakeUpDevice", cRes);
                    MDMUtil.getPersistence().update(dObj);
                }
            }
        }
        catch (final Exception ex) {
            WakeUpUtil.logger.log(Level.SEVERE, "Exception while removing request", ex);
        }
    }
    
    public List getDevicetoWakeUp(final int platformType) {
        final List resourceList = new ArrayList();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WakeUpDevice"));
        final Criteria cPlatform = new Criteria(new Column("WakeUpDevice", "NOTIFICATION_TYPE"), (Object)platformType, 0);
        final Criteria cStatus = new Criteria(new Column("WakeUpDevice", "STATUS"), (Object)0, 0);
        sQuery.setCriteria(cPlatform.and(cStatus));
        final SortColumn sortCol = new SortColumn(Column.getColumn("WakeUpDevice", "ADDED_TIME"), true);
        sQuery.addSortColumn(sortCol);
        sQuery.addSelectColumn(Column.getColumn("WakeUpDevice", "*"));
        final Range range = new Range(0, 25);
        sQuery.setRange(range);
        try {
            DataObject DO = null;
            synchronized (WakeUpUtil.WAKEUP_LOCK) {
                DO = MDMUtil.getPersistence().get(sQuery);
            }
            if (DO != null && !DO.isEmpty()) {
                final Iterator item = DO.getRows("WakeUpDevice");
                while (item.hasNext()) {
                    final Row row = item.next();
                    resourceList.add(row.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            WakeUpUtil.logger.log(Level.SEVERE, "Exception while getting the device for wakeup", ex);
        }
        return resourceList;
    }
    
    static {
        WakeUpUtil.wakeUpUtil = null;
        WAKEUP_LOCK = new Integer(1);
        WakeUpUtil.logger = Logger.getLogger("MDMLogger");
    }
}
