package com.me.devicemanagement.framework.server.mailmanager;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.StringTokenizer;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class EMailAddressHandler
{
    private static Logger logger;
    private static String sourceClass;
    private static EMailAddressHandler handler;
    
    public static EMailAddressHandler getInstance() {
        if (EMailAddressHandler.handler == null) {
            EMailAddressHandler.handler = new EMailAddressHandler();
        }
        return EMailAddressHandler.handler;
    }
    
    public void addOrUpdateEMailAddress(final String strEMailAddress, final String module) throws Exception {
        final String sourceMethod = "setEMailAddress";
        try {
            if (strEMailAddress == null) {
                SyMLogger.warning(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Sent E-Mail IDs : " + strEMailAddress);
                throw new Exception("No E-Mail ids sent for storage");
            }
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Calling getEMailAddressArray(...) using string : " + strEMailAddress);
            final String[] arrAddress = this.getEMailAddressArray(strEMailAddress);
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Array of E-Mail Ids : " + arrAddress);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EMailAddr"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "EMailAddr DataObject : " + dobj);
            if (dobj.isEmpty()) {
                for (int i = 0; i < arrAddress.length; ++i) {
                    final Row row = new Row("EMailAddr");
                    row.set("EMAIL_ADDR", (Object)arrAddress[i]);
                    row.set("MODULE", (Object)module);
                    row.set("SEND_MAIL", (Object)true);
                    dobj.addRow(row);
                }
                SyMUtil.getPersistence().add(dobj);
            }
            else {
                boolean isUpdated = false;
                boolean isAdded = false;
                final DataObject newDObj = SyMUtil.getPersistence().constructDataObject();
                for (int j = 0; j < arrAddress.length; ++j) {
                    final Column col1 = Column.getColumn("EMailAddr", "EMAIL_ADDR");
                    Criteria crit1 = new Criteria(col1, (Object)arrAddress[j], 0, false);
                    final Column col2 = Column.getColumn("EMailAddr", "MODULE");
                    final Criteria crit2 = new Criteria(col2, (Object)module, 0, false);
                    crit1 = crit1.and(crit2);
                    Row row2 = dobj.getRow("EMailAddr", crit1);
                    if (row2 == null) {
                        row2 = new Row("EMailAddr");
                        row2.set("EMAIL_ADDR", (Object)arrAddress[j]);
                        row2.set("MODULE", (Object)module);
                        row2.set("SEND_MAIL", (Object)true);
                        newDObj.addRow(row2);
                        if (!isAdded) {
                            isAdded = true;
                        }
                    }
                    else {
                        row2.set("SEND_MAIL", (Object)true);
                        dobj.updateRow(row2);
                        if (!isUpdated) {
                            isUpdated = true;
                        }
                    }
                }
                SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "EMailAddr DataObject before adding in DB : " + dobj);
                if (isAdded) {
                    SyMUtil.getPersistence().add(newDObj);
                }
                final Column col3 = Column.getColumn("EMailAddr", "MODULE");
                final Criteria crit3 = new Criteria(col3, (Object)module, 0, false);
                final Iterator iter = dobj.getRows("EMailAddr", crit3);
                while (iter.hasNext()) {
                    boolean isPresent = false;
                    final Row row3 = iter.next();
                    final String strMailID = (String)row3.get("EMAIL_ADDR");
                    for (int k = 0; k < arrAddress.length; ++k) {
                        if (strMailID.compareTo(arrAddress[k]) == 0) {
                            isPresent = true;
                            break;
                        }
                    }
                    if (!isPresent) {
                        row3.set("SEND_MAIL", (Object)false);
                        dobj.updateRow(row3);
                        if (isUpdated) {
                            continue;
                        }
                        isUpdated = true;
                    }
                }
                if (isUpdated) {
                    SyMUtil.getPersistence().update(dobj);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Exception occured!!", ex);
        }
    }
    
    public void addOrUpdateEMailAddress(final long customerId, final String strEMailAddress, final String module) throws Exception {
        final String sourceMethod = "setCustomerEMailAddress";
        try {
            Iterator itr = null;
            if (strEMailAddress == null) {
                SyMLogger.warning(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Sent E-Mail IDs : " + strEMailAddress);
                throw new Exception("No E-Mail ids sent for storage");
            }
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Calling getEMailAddressArray(...) using string : " + strEMailAddress);
            final String[] arrAddress = this.getEMailAddressArray(strEMailAddress);
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Array of E-Mail Ids : " + arrAddress);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EMailAddr"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Join join = new Join("EMailAddr", "CustomerEmailAddrRel", new String[] { "EMAIL_ADDR_ID" }, new String[] { "EMAIL_ADDR_ID" }, 2);
            final Column col = Column.getColumn("CustomerEmailAddrRel", "CUSTOMER_ID");
            final Criteria crit = new Criteria(col, (Object)customerId, 0, false);
            query.setCriteria(crit);
            query.addJoin(join);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "EMailAddr DataObject : " + dobj);
            if (dobj.isEmpty()) {
                for (int i = 0; i < arrAddress.length; ++i) {
                    final Row row = new Row("EMailAddr");
                    row.set("EMAIL_ADDR", (Object)arrAddress[i]);
                    row.set("MODULE", (Object)module);
                    row.set("SEND_MAIL", (Object)Boolean.TRUE);
                    dobj.addRow(row);
                }
                SyMUtil.getPersistence().add(dobj);
                itr = dobj.getRows("EMailAddr");
                this.addCustomerEmailRel(customerId, itr);
            }
            else {
                boolean isUpdated = Boolean.FALSE;
                boolean isAdded = false;
                final DataObject newDObj = SyMUtil.getPersistence().constructDataObject();
                for (int j = 0; j < arrAddress.length; ++j) {
                    final Column col2 = Column.getColumn("EMailAddr", "EMAIL_ADDR");
                    Criteria crit2 = new Criteria(col2, (Object)arrAddress[j], 0, false);
                    final Column col3 = Column.getColumn("EMailAddr", "MODULE");
                    final Criteria crit3 = new Criteria(col3, (Object)module, 0, false);
                    crit2 = crit2.and(crit3);
                    Row row2 = dobj.getRow("EMailAddr", crit2);
                    if (row2 == null) {
                        row2 = new Row("EMailAddr");
                        row2.set("EMAIL_ADDR", (Object)arrAddress[j]);
                        row2.set("MODULE", (Object)module);
                        row2.set("SEND_MAIL", (Object)Boolean.TRUE);
                        newDObj.addRow(row2);
                        if (!isAdded) {
                            isAdded = true;
                        }
                    }
                    else {
                        row2.set("SEND_MAIL", (Object)Boolean.TRUE);
                        dobj.updateRow(row2);
                        if (!isUpdated) {
                            isUpdated = Boolean.TRUE;
                        }
                    }
                }
                SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "EMailAddr DataObject before adding in DB : " + dobj);
                if (isAdded) {
                    SyMUtil.getPersistence().add(newDObj);
                    itr = newDObj.getRows("EMailAddr");
                    this.addCustomerEmailRel(customerId, itr);
                }
                final Column col4 = Column.getColumn("EMailAddr", "MODULE");
                final Criteria crit4 = new Criteria(col4, (Object)module, 0, false);
                final Iterator iter = dobj.getRows("EMailAddr", crit4);
                while (iter.hasNext()) {
                    boolean isPresent = false;
                    final Row row3 = iter.next();
                    final String strMailID = (String)row3.get("EMAIL_ADDR");
                    for (int k = 0; k < arrAddress.length; ++k) {
                        if (strMailID.compareTo(arrAddress[k]) == 0) {
                            isPresent = true;
                            break;
                        }
                    }
                    if (!isPresent) {
                        row3.set("SEND_MAIL", (Object)Boolean.FALSE);
                        dobj.updateRow(row3);
                        if (isUpdated) {
                            continue;
                        }
                        isUpdated = Boolean.TRUE;
                    }
                }
                if (isUpdated) {
                    SyMUtil.getPersistence().update(dobj);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Exception occured!!", ex);
        }
    }
    
    private String[] getEMailAddressArray(final String strMailAddress) throws Exception {
        final String sourceMethod = "getEMailAddressArray";
        if (strMailAddress == null) {
            SyMLogger.warning(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "String sent for parsing is null");
            return null;
        }
        SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Received EMail Address : " + strMailAddress);
        String[] arrMailAddress = null;
        try {
            final StringTokenizer st = new StringTokenizer(strMailAddress, ",");
            final int size = st.countTokens();
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "EMail Address count : " + size);
            arrMailAddress = new String[size];
            for (int j = 0; j < size; ++j) {
                final String str = st.nextToken();
                arrMailAddress[j] = str;
            }
            SyMLogger.debug(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Array of EMail Address : " + arrMailAddress);
        }
        catch (final Exception exp) {
            throw exp;
        }
        return arrMailAddress;
    }
    
    public MailDetails getMailAddressDetails(final String moduleName) {
        MailDetails mDetails = null;
        try {
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String fromName = mailSenderDetails.get("mail.fromName");
            final String fromAddress = mailSenderDetails.get("mail.fromAddress");
            final String toAddress = this.getEMailAddress(moduleName);
            mDetails = new MailDetails(fromAddress, toAddress);
            mDetails.senderDisplayName = fromName;
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return mDetails;
    }
    
    public String getEMailAddress(final String module) throws Exception {
        final String sourceMethod = "getEMailAddress";
        String strEMailAddr = null;
        try {
            final Column col1 = Column.getColumn("EMailAddr", "MODULE");
            final Criteria crit1 = new Criteria(col1, (Object)module, 0, false);
            final Column col2 = Column.getColumn("EMailAddr", "SEND_MAIL");
            final Criteria crit2 = new Criteria(col2, (Object)true, 0, false);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EMailAddr"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(crit1.and(crit2));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final StringBuffer buffer = new StringBuffer();
                final Iterator iter = dobj.getRows("EMailAddr");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    buffer.append((String)row.get("EMAIL_ADDR"));
                    buffer.append(",");
                }
                strEMailAddr = buffer.toString();
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Exception occured!!", ex);
        }
        return strEMailAddr;
    }
    
    public String getCustomerEMailAddress(final Long customerId, final String module) throws Exception {
        final String sourceMethod = "getCustomerEMailAddress";
        String strEMailAddr = null;
        try {
            final Column col1 = Column.getColumn("EMailAddr", "MODULE");
            final Criteria crit1 = new Criteria(col1, (Object)module, 0, false);
            final Column col2 = Column.getColumn("EMailAddr", "SEND_MAIL");
            final Criteria crit2 = new Criteria(col2, (Object)Boolean.TRUE, 0, false);
            final Column col3 = Column.getColumn("CustomerEmailAddrRel", "CUSTOMER_ID");
            final Criteria crit3 = new Criteria(col3, (Object)customerId, 0, false);
            final Criteria cri = crit1.and(crit2).and(crit3);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EMailAddr"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Join join = new Join("EMailAddr", "CustomerEmailAddrRel", new String[] { "EMAIL_ADDR_ID" }, new String[] { "EMAIL_ADDR_ID" }, 2);
            query.setCriteria(cri);
            query.addJoin(join);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final StringBuffer buffer = new StringBuffer();
                final Iterator iter = dobj.getRows("EMailAddr");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    buffer.append((String)row.get("EMAIL_ADDR"));
                    buffer.append(",");
                }
                strEMailAddr = buffer.toString();
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Exception occured!!", ex);
        }
        return strEMailAddr;
    }
    
    public void addCustomerEmailRel(final long customerId, final Iterator itr) {
        final String sourceMethod = "addCustomerEMailRel";
        try {
            DataObject dObj = null;
            while (itr.hasNext()) {
                final Row row = itr.next();
                final long emailAddrId = (long)row.get("EMAIL_ADDR_ID");
                final Criteria cusCri = new Criteria(Column.getColumn("CustomerEmailAddrRel", "CUSTOMER_ID"), (Object)customerId, 0);
                final Criteria emailCri = new Criteria(Column.getColumn("CustomerEmailAddrRel", "EMAIL_ADDR_ID"), (Object)emailAddrId, 0);
                final Criteria cri = cusCri.and(emailCri);
                dObj = SyMUtil.getPersistence().get("CustomerEmailAddrRel", cri);
                if (dObj.isEmpty()) {
                    dObj = SyMUtil.getPersistence().constructDataObject();
                    final Row newrow = new Row("CustomerEmailAddrRel");
                    newrow.set("CUSTOMER_ID", (Object)customerId);
                    newrow.set("EMAIL_ADDR_ID", (Object)emailAddrId);
                    dObj.addRow(newrow);
                    SyMUtil.getPersistence().update(dObj);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Exception occured at addCustomerEmailRel  ", ex);
        }
    }
    
    public void stopSendingMails(final String moduleName) throws Exception {
        final String sourceMethod = "stopSendingMails";
        try {
            final Column col = Column.getColumn("EMailAddr", "MODULE");
            final Criteria crit = new Criteria(col, (Object)moduleName, 0, false);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EMailAddr"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(crit);
            final DataObject mailDObj = SyMUtil.getPersistence().get(query);
            final Iterator iter = mailDObj.getRows("EMailAddr");
            while (iter.hasNext()) {
                final Row row = iter.next();
                row.set("SEND_MAIL", (Object)Boolean.FALSE);
            }
            SyMUtil.getPersistence().update(mailDObj);
        }
        catch (final Exception ex) {
            SyMLogger.error(EMailAddressHandler.logger, EMailAddressHandler.sourceClass, sourceMethod, "Exception occured : \n", ex);
        }
    }
    
    static {
        EMailAddressHandler.logger = Logger.getLogger("MailManager");
        EMailAddressHandler.sourceClass = "MailManager";
        EMailAddressHandler.handler = null;
    }
}
