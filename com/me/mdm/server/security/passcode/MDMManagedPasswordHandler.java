package com.me.mdm.server.security.passcode;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class MDMManagedPasswordHandler
{
    public static Logger logger;
    
    public static Long getMDMManagedPasswordID(final String password, final Long customerID, final Long userID) {
        try {
            final SelectQuery sql = getManagedPasswordQuery(customerID);
            sql.setCriteria(sql.getCriteria().and(new Criteria(new Column("MDMManagedPassword", "PASSWORD"), (Object)password, 0)));
            sql.addSelectColumn(new Column("MDMManagedPassword", "MANAGED_PASSWORD_ID"));
            DataObject daO = MDMUtil.getPersistence().get(sql);
            Row matchingRow = null;
            if (daO == null || daO.isEmpty()) {
                matchingRow = new Row("MDMManagedPassword");
                matchingRow.set("ADDED_BY", (Object)userID);
                matchingRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                matchingRow.set("PASSWORD", (Object)password);
                matchingRow.set("CUSTOMER_ID", (Object)customerID);
                daO = MDMUtil.getPersistence().constructDataObject();
                daO.addRow(matchingRow);
                MDMUtil.getPersistence().update(daO);
                MDMManagedPasswordHandler.logger.log(Level.INFO, "PasswordAdded CustomerID:{0} userID:{1} passwordID:{2}", new Object[] { customerID, userID, matchingRow.get("MANAGED_PASSWORD_ID") });
            }
            else {
                matchingRow = daO.getRow("MDMManagedPassword");
            }
            return (Long)matchingRow.get("MANAGED_PASSWORD_ID");
        }
        catch (final Exception ex) {
            MDMManagedPasswordHandler.logger.log(Level.SEVERE, "Exception in MDMManagedPasswordHandler.getMDMManagedPasswordID()", ex);
            return null;
        }
    }
    
    public static String getMDMManagedPassword(final Long passwordID, final Long customerID) {
        try {
            final SelectQuery sql = getManagedPasswordQuery(customerID);
            sql.setCriteria(sql.getCriteria().and(new Criteria(new Column("MDMManagedPassword", "MANAGED_PASSWORD_ID"), (Object)passwordID, 0)));
            sql.addSelectColumn(new Column("MDMManagedPassword", "PASSWORD"));
            final DataObject daO = MDMUtil.getPersistence().get(sql);
            Row matchingRow = null;
            if (daO != null && !daO.isEmpty()) {
                matchingRow = daO.getRow("MDMManagedPassword");
                return (String)matchingRow.get("PASSWORD");
            }
        }
        catch (final Exception ex) {
            MDMManagedPasswordHandler.logger.log(Level.SEVERE, "Exception in MDMManagedPasswordHandler.getMDMManagedPassword()", ex);
        }
        return null;
    }
    
    private static SelectQuery getManagedPasswordQuery(final Long customerID) {
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMManagedPassword"));
        sql.setCriteria(new Criteria(new Column("MDMManagedPassword", "CUSTOMER_ID"), (Object)customerID, 0));
        sql.addSelectColumn(new Column("MDMManagedPassword", "MANAGED_PASSWORD_ID"));
        return sql;
    }
    
    public static Map<String, String> getMDMManagedPasswords(final List<Long> passwordIDs, final Long customerID) {
        final Map<String, String> managedPasswords = new HashMap<String, String>();
        MDMManagedPasswordHandler.logger.log(Level.INFO, "Getting password for the given the given passwordIDs", new Object[] { passwordIDs });
        try {
            final SelectQuery selectQuery = getManagedPasswordQuery(customerID);
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("MDMManagedPassword", "MANAGED_PASSWORD_ID"), (Object)passwordIDs.toArray(), 8)));
            selectQuery.addSelectColumn(new Column("MDMManagedPassword", "PASSWORD"));
            selectQuery.addSelectColumn(new Column("MDMManagedPassword", "MANAGED_PASSWORD_ID"));
            final DataObject managedPasswordDO = MDMUtil.getPersistence().get(selectQuery);
            if (managedPasswordDO != null && !managedPasswordDO.isEmpty()) {
                for (final Long passwordId : passwordIDs) {
                    final Criteria passwordIDCriteria = new Criteria(Column.getColumn("MDMManagedPassword", "MANAGED_PASSWORD_ID"), (Object)passwordId, 0);
                    final Row matchingRow = managedPasswordDO.getRow("MDMManagedPassword", passwordIDCriteria);
                    if (matchingRow != null) {
                        final String passwordStr = (String)matchingRow.get("PASSWORD");
                        managedPasswords.put(passwordId.toString(), passwordStr);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MDMManagedPasswordHandler.logger.log(Level.SEVERE, "Exception in MDMManagedPasswordHandler.getMDMManagedPassword()", ex);
        }
        return managedPasswords;
    }
    
    public static Map<String, Long> getMDMManagedPasswordsOfCustomer(final Long customerID) {
        final Map<String, Long> managedPasswords = new HashMap<String, Long>();
        try {
            final SelectQuery selectQuery = getManagedPasswordQuery(customerID);
            selectQuery.addSelectColumn(new Column("MDMManagedPassword", "PASSWORD"));
            selectQuery.addSelectColumn(new Column("MDMManagedPassword", "MANAGED_PASSWORD_ID"));
            final DataObject managedPasswordDO = MDMUtil.getPersistence().get(selectQuery);
            if (managedPasswordDO != null && !managedPasswordDO.isEmpty()) {
                final Iterator managedPasswordRowsIterator = managedPasswordDO.getRows("MDMManagedPassword");
                while (managedPasswordRowsIterator.hasNext()) {
                    final Row managedPasswordRow = managedPasswordRowsIterator.next();
                    final String passwordStr = (String)managedPasswordRow.get("PASSWORD");
                    final Long passwordId = (Long)managedPasswordRow.get("MANAGED_PASSWORD_ID");
                    managedPasswords.put(passwordStr, passwordId);
                }
            }
        }
        catch (final Exception ex) {
            MDMManagedPasswordHandler.logger.log(Level.SEVERE, "Exception in MDMManagedPasswordHandler.getMDMManagedPassword()", ex);
        }
        return managedPasswords;
    }
    
    static {
        MDMManagedPasswordHandler.logger = Logger.getLogger("MDMDeviceSecurityLogger");
    }
}
