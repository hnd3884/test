package com.me.mdm.core.auth;

import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;

public class MDMDeviceTokenGenerator
{
    private static final int MAX_RETRY_COUNT = 5;
    private static MDMDeviceTokenGenerator mdmDeviceTokenGenerator;
    
    protected MDMDeviceTokenGenerator() {
    }
    
    public static MDMDeviceTokenGenerator getInstance() {
        if (MDMDeviceTokenGenerator.mdmDeviceTokenGenerator == null) {
            MDMDeviceTokenGenerator.mdmDeviceTokenGenerator = new MDMDeviceTokenGenerator();
        }
        return MDMDeviceTokenGenerator.mdmDeviceTokenGenerator;
    }
    
    private String generateNewDeviceToken() {
        String deviceToken = null;
        Long erid = -1L;
        for (int i = 0; i < 5 && erid != null; ++i) {
            final SecureRandom random = new SecureRandom();
            deviceToken = new BigInteger(32, random).toString(16);
            try {
                erid = (Long)DBUtil.getValueFromDB("DeviceToken", "TOKEN_ENCRYPTED", (Object)deviceToken, "ENROLLMENT_REQUEST_ID");
            }
            catch (final Exception e) {
                Logger.getLogger(MDMDeviceTokenGenerator.class.getName()).log(Level.SEVERE, "Exception in Device Token generation", e);
            }
        }
        return deviceToken;
    }
    
    public final String generateDeviceToken(final Long erid) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceToken"));
            query.setCriteria(new Criteria(Column.getColumn("DeviceToken", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            query.addSelectColumn(Column.getColumn("DeviceToken", "*"));
            DataObject DO = MDMUtil.getPersistence().get(query);
            if (DO.isEmpty()) {
                final Row row = new Row("DeviceToken");
                row.set("ENROLLMENT_REQUEST_ID", (Object)erid);
                row.set("TOKEN_ENCRYPTED", (Object)MDMUtil.generateNewRandomToken("DeviceToken", "TOKEN_ENCRYPTED", "ENROLLMENT_REQUEST_ID"));
                DO = (DataObject)new WritableDataObject();
                DO.addRow(row);
                MDMUtil.getPersistence().add(DO);
                return (String)row.get("TOKEN_ENCRYPTED");
            }
            return (String)DO.getValue("DeviceToken", "TOKEN_ENCRYPTED", (Criteria)null);
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceTokenGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public final String addOrUpdateDeviceToken(final Long erid) {
        try {
            return this.addOrUpdateDeviceToken(erid, MDMUtil.generateNewRandomToken("DeviceToken", "TOKEN_ENCRYPTED", "ENROLLMENT_REQUEST_ID"));
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceTokenGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public final String addOrUpdateDeviceToken(final Long erid, final String deviceToken) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceToken"));
            query.setCriteria(new Criteria(Column.getColumn("DeviceToken", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            query.addSelectColumn(Column.getColumn("DeviceToken", "*"));
            DataObject DO = MDMUtil.getPersistence().get(query);
            if (DO.isEmpty()) {
                final Row row = new Row("DeviceToken");
                row.set("ENROLLMENT_REQUEST_ID", (Object)erid);
                row.set("TOKEN_ENCRYPTED", (Object)deviceToken);
                DO = (DataObject)new WritableDataObject();
                DO.addRow(row);
                MDMUtil.getPersistence().add(DO);
                return (String)row.get("TOKEN_ENCRYPTED");
            }
            final Row row = DO.getRow("DeviceToken");
            row.set("TOKEN_ENCRYPTED", (Object)deviceToken);
            DO.updateRow(row);
            MDMUtil.getPersistence().update(DO);
            return (String)row.get("TOKEN_ENCRYPTED");
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceTokenGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String getDeviceToken(final Long erid) {
        try {
            return (String)DBUtil.getValueFromDB("DeviceToken", "ENROLLMENT_REQUEST_ID", (Object)erid, "TOKEN_ENCRYPTED");
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceTokenGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public final Long[] getEridsForDeviceToken(final String deviceToken) {
        try {
            final ArrayList<Long> list = new ArrayList<Long>();
            final Iterator<Row> iterator = DBUtil.getRowsFromDB("DeviceToken", "TOKEN_ENCRYPTED", (Object)deviceToken);
            if (iterator != null) {
                while (iterator.hasNext()) {
                    list.add((Long)iterator.next().get("ENROLLMENT_REQUEST_ID"));
                }
            }
            final Long[] erids = new Long[list.size()];
            return list.toArray(erids);
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMDeviceTokenGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return new Long[0];
        }
    }
    
    static {
        MDMDeviceTokenGenerator.mdmDeviceTokenGenerator = null;
    }
}
