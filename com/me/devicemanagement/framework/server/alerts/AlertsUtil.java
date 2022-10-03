package com.me.devicemanagement.framework.server.alerts;

import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.mailmanager.MailContentGeneratorUtil;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import com.adventnet.ds.query.SortColumn;
import java.net.URLEncoder;
import com.adventnet.i18n.I18N;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import java.util.logging.Logger;

public class AlertsUtil
{
    protected static Logger logger;
    public static AlertsUtil alertws;
    private String className;
    
    public AlertsUtil() {
        this.className = AlertsUtil.class.getName();
    }
    
    public static AlertsUtil getInstance() {
        if (AlertsUtil.alertws == null) {
            AlertsUtil.alertws = new AlertsUtil();
        }
        return AlertsUtil.alertws;
    }
    
    public Properties getCustomerKeyDescription(final long customerId, final Long alertConstantId) {
        final String methodName = "getCustomerKeyDescription";
        Properties subDescProp = null;
        try {
            String subject = null;
            String description = null;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerEmailDCAlert"));
            final Column alertIdCol = Column.getColumn("CustomerEmailDCAlert", "ALERT_TYPE_ID");
            final Column cusCol = Column.getColumn("CustomerEmailDCAlert", "CUSTOMER_ID");
            final Criteria criAlert = new Criteria(alertIdCol, (Object)alertConstantId, 0);
            final Criteria criCustomer = new Criteria(cusCol, (Object)customerId, 0);
            final Criteria cri = criAlert.and(criCustomer);
            query.setCriteria(cri);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            subDescProp = new Properties();
            if (!dobj.isEmpty()) {
                final Row row = dobj.getRow("CustomerEmailDCAlert");
                subject = (String)row.get("SUBJECT");
                description = (String)row.get("DESCRIPTION");
                if (subject != null) {
                    subDescProp.setProperty("subject", subject);
                }
                if (description != null) {
                    subDescProp.setProperty("description", description);
                }
                ((Hashtable<String, Boolean>)subDescProp).put("alertReConfigured", Boolean.TRUE);
            }
            else {
                subDescProp = this.getKeyDescription(alertConstantId);
                ((Hashtable<String, Boolean>)subDescProp).put("alertReConfigured", Boolean.FALSE);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, " Exception occured at AlertsUtil-getCustomerKeyDescription  ", ex);
        }
        SyMLogger.info(AlertsUtil.logger, this.className, methodName, "Properties from getCustomerKeyDescription" + subDescProp);
        return subDescProp;
    }
    
    public Properties getCustomerKeyDescription(final long customerId, final Long alertConstantId, final Long technicianID) {
        final String methodName = "getCustomerKeyDescription";
        Properties subDescProp = null;
        try {
            String subject = null;
            String description = null;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerTechEmailDCAlert"));
            final Column alertIdCol = Column.getColumn("CustomerTechEmailDCAlert", "ALERT_TYPE_ID");
            final Column cusCol = Column.getColumn("CustomerTechEmailDCAlert", "CUSTOMER_ID");
            final Column technicianColumn = Column.getColumn("CustomerTechEmailDCAlert", "TECH_ID");
            final Criteria criAlert = new Criteria(alertIdCol, (Object)alertConstantId, 0);
            final Criteria criCustomer = new Criteria(cusCol, (Object)customerId, 0);
            final Criteria criTechnician = new Criteria(technicianColumn, (Object)technicianID, 0);
            final Criteria cri = criAlert.and(criCustomer).and(criTechnician);
            query.setCriteria(cri);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            subDescProp = new Properties();
            if (!dobj.isEmpty()) {
                final Row row = dobj.getRow("CustomerTechEmailDCAlert");
                subject = (String)row.get("SUBJECT");
                description = (String)row.get("DESCRIPTION");
                if (subject != null) {
                    subDescProp.setProperty("subject", subject);
                }
                if (description != null) {
                    subDescProp.setProperty("description", description);
                }
                ((Hashtable<String, Boolean>)subDescProp).put("alertReConfigured", Boolean.TRUE);
            }
            else {
                subDescProp = this.getKeyDescription(alertConstantId);
                ((Hashtable<String, Boolean>)subDescProp).put("alertReConfigured", Boolean.FALSE);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, " Exception occured at AlertsUtil-getCustomerKeyDescription  ", ex);
        }
        SyMLogger.info(AlertsUtil.logger, this.className, methodName, "Properties from getCustomerKeyDescription" + subDescProp);
        return subDescProp;
    }
    
    public void addCustomerEmailRel(final long customerId, final Iterator itr) {
        final String methodName = "addCustomerEmailRel";
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
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception occured at AlertsUtil-addCustomerEmailRel  ", ex);
        }
    }
    
    public Properties getKeyDescription(final Long alertConstantId) {
        final String methodName = "getKeyDescription";
        Properties subDescProp = null;
        try {
            String subject = null;
            String description = null;
            String subjectvariable = null;
            String descriptionvariable = "";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EmailDCAlertTask"));
            final Column idCol = Column.getColumn("EmailDCAlertTask", "ALERT_TYPE_ID");
            final Criteria subDescri = new Criteria(idCol, (Object)alertConstantId, 0);
            query.setCriteria(subDescri);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            subDescProp = new Properties();
            if (!dobj.isEmpty()) {
                final Row row = dobj.getRow("EmailDCAlertTask");
                subject = (String)row.get("SUBJECT");
                description = (String)row.get("DESCRIPTION");
                subjectvariable = (String)row.get("SUBJECT_VARIABLES");
                descriptionvariable = (String)row.get("DESCRIPTION_VARIABLES");
            }
            if (subject != null) {
                if (!subjectvariable.equals("")) {
                    if (subjectvariable.contains(",")) {
                        final Object[] var = subjectvariable.split(",");
                        subject = I18N.getMsg(subject, var);
                    }
                    else {
                        subject = I18N.getMsg(subject, new Object[] { subjectvariable });
                    }
                }
                else {
                    subject = I18N.getMsg(subject, new Object[0]);
                }
                subDescProp.setProperty("subject", URLEncoder.encode(subject, "UTF-8"));
            }
            if (description != null) {
                if (!descriptionvariable.equals("")) {
                    final Object[] var = descriptionvariable.split(",");
                    description = I18N.getMsg(description, var);
                }
                else {
                    description = I18N.getMsg(description, new Object[0]);
                }
            }
            final String emaildescVar = this.getEmailValueToAlertType(alertConstantId);
            if (emaildescVar != null) {
                description += emaildescVar;
            }
            subDescProp.setProperty("description", URLEncoder.encode(description, "UTF-8"));
        }
        catch (final Exception ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, " Exception occured at AlertsUtil-getKeyDescription  ", ex);
        }
        SyMLogger.info(AlertsUtil.logger, this.className, methodName, "Properties from getKeyDescription" + subDescProp);
        return subDescProp;
    }
    
    protected String getEmailValueToAlertType(final Long alertType) {
        final String methodName = "getEmailValueToAlertType";
        String desValues = null;
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("EmailValueToTypeRel"));
            final Criteria cAlertType = new Criteria(new Column("EmailValueToTypeRel", "ALERT_TYPE_ID"), (Object)alertType, 0);
            selectQuery.setCriteria(cAlertType);
            selectQuery.addSelectColumn(Column.getColumn("EmailValueToTypeRel", "ALERT_VALUE"));
            selectQuery.addSelectColumn(Column.getColumn("EmailValueToTypeRel", "ALERT_TYPE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EmailValueToTypeRel", "ALERT_KEY"));
            selectQuery.addSelectColumn(Column.getColumn("EmailValueToTypeRel", "EMAIL_VALUE_KEY_ID"));
            selectQuery.addSortColumn(new SortColumn("EmailValueToTypeRel", "EMAIL_VALUE_KEY_ID", (boolean)Boolean.TRUE));
            final DataObject DO = SyMUtil.getPersistence().get((SelectQuery)selectQuery);
            if (DO != null && !DO.isEmpty()) {
                final Iterator item = DO.getRows("EmailValueToTypeRel");
                while (item.hasNext()) {
                    final Row row = item.next();
                    row.set("ALERT_VALUE", (Object)I18N.getMsg((String)row.get("ALERT_VALUE"), new Object[0]));
                    DO.updateRow(row);
                }
                final StringBuffer filePath = new StringBuffer();
                filePath.append(SyMUtil.getInstallationDir()).append(File.separator).append("conf").append(File.separator);
                String fileName = null;
                fileName = this.getCustomFormatFileName(alertType);
                if (StringUtils.isNotBlank(fileName)) {
                    filePath.append(fileName);
                }
                else {
                    filePath.append("DeviceManagementFramework").append(File.separator).append("xsl").append(File.separator).append("EmailAlertInfo.xsl");
                }
                final String emailXsl = filePath.toString();
                final MailContentGeneratorUtil mg = new MailContentGeneratorUtil();
                desValues = mg.getHTMLContent(emailXsl, DO, "Alert");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting the variables for alert ID " + alertType, e);
        }
        return desValues;
    }
    
    public String getCustomFormatFileName(final Long alertTypeId) {
        final String methodName = "getCustomFormatFileName";
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("DCAlertType"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("DCAlertType", "ALERT_TYPE_ID"), (Object)alertTypeId, 0));
            selectQuery.addSelectColumn(Column.getColumn("DCAlertType", "ALERT_TYPE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DCAlertType", "CUSTOM_FORMAT_FILE_PATH"));
            final DataObject dataObject = SyMUtil.getPersistence().get((SelectQuery)selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator rows = dataObject.getRows("DCAlertType");
                if (rows.hasNext()) {
                    return (String)rows.next().get("CUSTOM_FORMAT_FILE_PATH");
                }
            }
        }
        catch (final DataAccessException e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception occurred while getting custom format file path for " + alertTypeId, (Throwable)e);
        }
        return null;
    }
    
    public Long addAlert(final Long alertType, final String alertRemarks) {
        return this.addAlert(alertType, alertRemarks, null);
    }
    
    public Long addAlert(final Long alertType, final String alertRemarks, final Object remarksArgs, final Long customerID) {
        final String methodName = "addAlert";
        Long alertID;
        if (remarksArgs == null) {
            alertID = this.addAlert(alertType, alertRemarks, null);
        }
        else {
            alertID = this.addAlert(alertType, alertRemarks, remarksArgs);
        }
        try {
            final WritableDataObject dataObject = new WritableDataObject();
            final Row row = new Row("DCAlertCustomerRel");
            row.set("ALERT_ID", (Object)alertID);
            row.set("CUSTOMER_ID", (Object)customerID);
            dataObject.addRow(row);
            SyMUtil.getPersistence().add((DataObject)dataObject);
        }
        catch (final Exception e) {
            SyMLogger.warning(AlertsUtil.logger, this.className, methodName, "Exception occurred while adding customer and alert relation");
        }
        return alertID;
    }
    
    public Long addAlert(final Long alertType, final String alertRemarks, final Object remarksArgs) {
        final String methodName = "addAlert";
        Long alertID = null;
        SyMLogger.info(AlertsUtil.logger, this.className, methodName, "Going to add alert for alert type " + alertType);
        if (alertType == null || alertRemarks == null) {
            SyMLogger.warning(AlertsUtil.logger, this.className, methodName, "Alert Type or Alert Remarks is null.  Could not add alert.  Returning null");
            return null;
        }
        try {
            Row alertRow = this.getAlertRow(alertType, alertRemarks, remarksArgs);
            DataObject dobj = SyMUtil.getPersistence().constructDataObject();
            dobj.addRow(alertRow);
            dobj = SyMUtil.getPersistence().add(dobj);
            alertRow = dobj.getRow("DCAlert");
            alertID = (Long)alertRow.get("ALERT_ID");
        }
        catch (final Exception ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception occurred while adding alerts for alert type " + alertType, ex);
        }
        return alertID;
    }
    
    public List getModules() throws DataAccessException, Exception {
        return this.getModules((Criteria)null);
    }
    
    public List getModules(final Integer moduleID) throws DataAccessException, Exception {
        Criteria criteria = null;
        if (moduleID != null) {
            criteria = new Criteria(Column.getColumn("DCModule", "MODULE_ID"), (Object)moduleID, 0);
        }
        return this.getModules(criteria);
    }
    
    public List getModules(final List<Integer> moduleIDs) throws DataAccessException, Exception {
        Criteria criteria = null;
        if (!moduleIDs.isEmpty()) {
            criteria = new Criteria(Column.getColumn("DCModule", "MODULE_ID"), (Object)moduleIDs.toArray(), 8);
        }
        return this.getModules(criteria);
    }
    
    public List getModules(final Criteria criteria) throws DataAccessException, Exception {
        List modulesList = null;
        final DataObject dobj = SyMUtil.getPersistence().get("DCModule", criteria);
        if (dobj != null && dobj.containsTable("DCModule")) {
            final Iterator iterator = dobj.getRows("DCModule");
            modulesList = new ArrayList();
            final List excludedModuleList = this.getExcludedModuleList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long moduleID = (Long)row.get("MODULE_ID");
                final String moduleName = (String)row.get("MODULE_NAME");
                final Hashtable moduleHash = new Hashtable();
                moduleHash.put("MODULE_ID", moduleID);
                moduleHash.put("MODULE_NAME", I18N.getMsg(moduleName, new Object[0]));
                if (!excludedModuleList.contains(I18N.getMsg(moduleName, new Object[0]))) {
                    modulesList.add(moduleHash);
                }
            }
        }
        return modulesList;
    }
    
    private List getExcludedModuleList() throws Exception {
        final List module = new ArrayList();
        module.add(I18N.getMsg("dc.mdm.MDM", new Object[0]));
        return module;
    }
    
    public List getAlertTypes(final Integer moduleID) {
        final List<Integer> moduleIds = new ArrayList<Integer>();
        moduleIds.add(moduleID);
        return this.getAlertTypes(moduleIds);
    }
    
    public DataObject getAlertTypeDO(final List<Integer> moduleIDs) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCAlertType"));
        Criteria criteria = new Criteria(Column.getColumn("DCAlertType", "SHOW_ALERT"), (Object)Boolean.TRUE, 0);
        if (!moduleIDs.isEmpty()) {
            criteria = criteria.and(new Criteria(Column.getColumn("DCAlertType", "MODULE_ID"), (Object)moduleIDs.toArray(), 8));
        }
        criteria = this.getExclusionCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("DCAlertType", "*"));
        selectQuery.addSortColumn(new SortColumn("DCAlertType", "ALERT_TYPE", true));
        selectQuery.setCriteria(criteria);
        try {
            return SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            AlertsUtil.logger.log(Level.SEVERE, "Exception while fetching AlertTypeDO", ex);
            return (DataObject)new WritableDataObject();
        }
    }
    
    public List getAlertTypes(final List<Integer> moduleIDs) {
        final String methodName = "getAlertTypes";
        final List alertTypes = new ArrayList();
        try {
            final DataObject dataObject = this.getAlertTypeDO(moduleIDs);
            final Iterator iterator = dataObject.getRows("DCAlertType");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final long alertType = (long)row.get("ALERT_TYPE_ID");
                final String description = (String)row.get("ALERT_TYPE");
                final Hashtable alertTypeHash = new Hashtable();
                alertTypeHash.put("ALERT_TYPE", alertType);
                alertTypeHash.put("DESCRIPTION", I18N.getMsg(description, new Object[0]));
                alertTypes.add(alertTypeHash);
            }
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting alert types.", (Throwable)ex);
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, " Exception while getting the alert type", e);
        }
        return alertTypes;
    }
    
    private Criteria getExclusionCriteria(Criteria criteria) {
        final Criteria alertExclusionCriteria = new Criteria(Column.getColumn("DCAlertType", "ALERT_TYPE_ID"), (Object)AlertConstants.MONITOR_ADDED, 7).or(new Criteria(Column.getColumn("DCAlertType", "ALERT_TYPE_ID"), (Object)AlertConstants.MOTHERBOARD_REMOVED, 5));
        criteria = criteria.and(alertExclusionCriteria);
        return criteria;
    }
    
    protected Row getAlertRow(final Long alertType, final String alertRemarks) {
        return this.getAlertRow(alertType, alertRemarks, null);
    }
    
    protected Row getAlertRow(final Long alertType, final String alertRemarks, final Object remarksArgs) {
        final Row alertRow = new Row("DCAlert");
        alertRow.set("ALERT_TYPE_ID", (Object)alertType);
        alertRow.set("ALERT_REMARKS", (Object)alertRemarks);
        if (remarksArgs != null) {
            alertRow.set("ALERT_REMARKS_ARGS", remarksArgs);
        }
        alertRow.set("ALERT_TIMESTAMP", (Object)System.currentTimeMillis());
        return alertRow;
    }
    
    public LinkedHashMap getAlertKeyValueMap(final Long alertConstantId) {
        final String methodName = "getAlertKeyValueMap";
        LinkedHashMap hash = null;
        try {
            String subject = "";
            String message = "";
            final SortColumn subSort = new SortColumn(Column.getColumn("AlertKey", "ALERT_KEY"), true);
            final Join join1 = new Join("AlertKeytoTypeRel", "DCAlertType", new String[] { "ALERT_TYPE_ID" }, new String[] { "ALERT_TYPE_ID" }, 2);
            final Join join2 = new Join("AlertKeytoTypeRel", "AlertKey", new String[] { "ALERT_KEY_ID" }, new String[] { "ALERT_KEY_ID" }, 2);
            final Column alertCol = Column.getColumn("AlertKeytoTypeRel", "ALERT_TYPE_ID");
            final Criteria categorizeCri = new Criteria(alertCol, (Object)alertConstantId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AlertKeytoTypeRel"));
            query.addJoin(join1);
            query.addJoin(join2);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addSortColumn(subSort);
            query.setCriteria(categorizeCri);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final Iterator iter = dobj.getRows("AlertKey");
                hash = new LinkedHashMap();
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    subject = (String)row.get("ALERT_KEY");
                    message = I18N.getMsg((String)row.get("ALERT_KEY_DESCRIPTION"), new Object[0]);
                    hash.put(subject, message);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception occured at DCAlertFormatAction-subMsgQuery  ", ex);
        }
        SyMLogger.info(AlertsUtil.logger, this.className, methodName, "LinkedHashMap from subMsgQuery" + hash);
        return hash;
    }
    
    public void addorUpdateCustomerAlertTypeRel(final long customerId, final long alertTypeId, final boolean emailAlert) {
        final String methodName = "addorUpdateCustomerAlertTypeRel";
        try {
            final Column cusAleRelCusCol = Column.getColumn("CustomerAlertTypeRel", "CUSTOMER_ID");
            final Column cusAleRelATypeCol = Column.getColumn("CustomerAlertTypeRel", "ALERT_TYPE_ID");
            final Criteria cusCri = new Criteria(cusAleRelCusCol, (Object)customerId, 0);
            final Criteria aleTypeCri = new Criteria(cusAleRelATypeCol, (Object)alertTypeId, 0);
            final Criteria cri = cusCri.and(aleTypeCri);
            DataObject dObj = SyMUtil.getPersistence().get("CustomerAlertTypeRel", cri);
            if (dObj.isEmpty()) {
                final Row row = new Row("CustomerAlertTypeRel");
                dObj = SyMUtil.getPersistence().constructDataObject();
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("ALERT_TYPE_ID", (Object)alertTypeId);
                row.set("EMAIL_ALERT", (Object)emailAlert);
                dObj.addRow(row);
                SyMUtil.getPersistence().add(dObj);
            }
            else {
                SyMLogger.warning(AlertsUtil.logger, this.className, methodName, "AlertType DataObject before updating in DB : " + dObj);
                final Row row = dObj.getRow("CustomerAlertTypeRel", cri);
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("ALERT_TYPE_ID", (Object)alertTypeId);
                row.set("EMAIL_ALERT", (Object)emailAlert);
                dObj.updateRow(row);
                SyMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception occured at AlertsUtil-addorUpdateCustomerAlertTypeRel  ", ex);
        }
    }
    
    public DataObject addorUpdateCustomerTechnicianAlertTypeRel(final Long customerId, final ArrayList technicianList, final Long alertTypeId, final boolean emailAlert, final DataObject alertsDO) {
        final String methodName = "addorUpdateCustomerTechnicianAlertTypeRel";
        try {
            Row row = null;
            for (int index = 0; index < technicianList.size(); ++index) {
                row = new Row("CustomerTechAlertTypeRel");
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("TECH_ID", technicianList.get(index));
                row.set("ALERT_TYPE_ID", (Object)alertTypeId);
                row.set("EMAIL_ALERT", (Object)emailAlert);
                alertsDO.addRow(row);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception occured at AlertsUtil-addorUpdateCustomerAlertTypeRel  ", ex);
        }
        return alertsDO;
    }
    
    public void addOrUpdateAlertSettings(final Long paramValue, final String alertName, final int durTime) {
        DataObject notifyDO = null;
        try {
            notifyDO = getAlertSettingDO(alertName, paramValue);
            if (notifyDO.isEmpty()) {
                notifyDO = SyMUtil.getPersistence().constructDataObject();
                final Row childRow = new Row("AlertSettings");
                childRow.set("ALERT_NAME", (Object)alertName);
                childRow.set("ALERT_PARAM_ID", (Object)paramValue);
                childRow.set("PERIODIC_TIME", (Object)durTime);
                notifyDO.addRow(childRow);
                SyMUtil.getPersistence().add(notifyDO);
            }
            else {
                final Row childRow = notifyDO.getRow("AlertSettings");
                childRow.set("PERIODIC_TIME", (Object)durTime);
                notifyDO.updateRow(childRow);
                SyMUtil.getPersistence().update(notifyDO);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static DataObject getAlertSettingDO(final String alertName, final Long paramValue) throws Exception {
        Criteria crit = new Criteria(Column.getColumn("AlertSettings", "ALERT_PARAM_ID"), (Object)paramValue, 0);
        final Criteria crit2 = new Criteria(Column.getColumn("AlertSettings", "ALERT_NAME"), (Object)alertName, 0);
        crit = crit.and(crit2);
        final DataObject getNotifyDO = SyMUtil.getPersistence().get("AlertSettings", crit);
        return getNotifyDO;
    }
    
    public int getAlertDurTime(final Long paramValue, final String alertName) {
        final String methodName = "getAlertDurTime";
        int timeDuration = 0;
        try {
            Criteria crit = new Criteria(Column.getColumn("AlertSettings", "ALERT_PARAM_ID"), (Object)paramValue, 0);
            final Criteria crit2 = new Criteria(Column.getColumn("AlertSettings", "ALERT_NAME"), (Object)alertName, 0);
            crit = crit.and(crit2);
            final DataObject notifyDO = SyMUtil.getPersistence().get("AlertSettings", crit);
            if (!notifyDO.isEmpty()) {
                final Row row = notifyDO.getFirstRow("AlertSettings");
                timeDuration = (int)row.get("PERIODIC_TIME");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting the variables for paramValue " + paramValue + " and alertName " + alertName, e);
        }
        return timeDuration;
    }
    
    public void removeAlertSettings(final Long paramValue, final String alertName) {
        final String methodName = "removeAlertSettings";
        try {
            Row notifyRow = new Row("AlertSettings");
            notifyRow.set("ALERT_PARAM_ID", (Object)paramValue);
            notifyRow.set("ALERT_NAME", (Object)alertName);
            final DataObject notifyDO = SyMUtil.getPersistence().get("AlertSettings", notifyRow);
            if (notifyDO.isEmpty()) {
                return;
            }
            notifyRow = notifyDO.getFirstRow("AlertSettings");
            DataAccess.delete(notifyRow);
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting the variables for paramValue " + paramValue + " and alertName " + alertName, e);
        }
    }
    
    public static Long getAlertSettingsID(final Long paramValue, final String alertName) throws Exception {
        Long ALERT_SETTINGS_ID = null;
        try {
            Criteria crit = new Criteria(Column.getColumn("AlertSettings", "ALERT_PARAM_ID"), (Object)paramValue, 0);
            final Criteria crit2 = new Criteria(Column.getColumn("AlertSettings", "ALERT_NAME"), (Object)alertName, 0);
            crit = crit.and(crit2);
            final DataObject notifyDO = SyMUtil.getPersistence().get("AlertSettings", crit);
            if (!notifyDO.isEmpty()) {
                final Row row = notifyDO.getFirstRow("AlertSettings");
                ALERT_SETTINGS_ID = (Long)row.get("ALERT_SETTINGS_ID");
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return ALERT_SETTINGS_ID;
    }
    
    public String getAlertPluginClass(final String alertName) throws DataAccessException {
        final String methodName = "getAlertPluginClass";
        String pluginClass = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AlertPlugins", "ALERT_NAME"), (Object)alertName, 0);
            final DataObject alertPluginDO = SyMUtil.getPersistence().get("AlertPlugins", criteria);
            if (!alertPluginDO.isEmpty()) {
                final Row alertPluginRow = alertPluginDO.getFirstRow("AlertPlugins");
                pluginClass = (String)alertPluginRow.get("PLUGIN_CLASS_NAME");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting the variables for alertName " + alertName, e);
        }
        return pluginClass;
    }
    
    public void addOrUpdateAlertSettings(final Long paramValue, final String alertName, final int durTime, final int enableDays, final String email_addr) {
        final String methodName = "addOrUpdateAlertSettings";
        DataObject notifyDO = null;
        try {
            notifyDO = getAlertSettingDO(alertName, paramValue);
            if (notifyDO == null || notifyDO.isEmpty()) {
                notifyDO = SyMUtil.getPersistence().constructDataObject();
                final Row childRow = new Row("AlertSettings");
                childRow.set("ALERT_NAME", (Object)alertName);
                childRow.set("ALERT_PARAM_ID", (Object)paramValue);
                childRow.set("PERIODIC_TIME", (Object)durTime);
                childRow.set("ENABLED_DAYS", (Object)enableDays);
                childRow.set("EMAIL_ADDR", (Object)email_addr);
                notifyDO.addRow(childRow);
                SyMUtil.getPersistence().add(notifyDO);
            }
            else {
                final Row childRow = notifyDO.getRow("AlertSettings");
                childRow.set("PERIODIC_TIME", (Object)durTime);
                childRow.set("ENABLED_DAYS", (Object)enableDays);
                childRow.set("EMAIL_ADDR", (Object)email_addr);
                notifyDO.updateRow(childRow);
                SyMUtil.getPersistence().update(notifyDO);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting the variables for alertName " + alertName + " and paramValue " + paramValue + " and durTime " + durTime + " and enableDays " + enableDays + " and email_addr " + email_addr, e);
        }
    }
    
    public String getAlertEmailAddress(final String alertName, final Long paramValue) {
        final String methodName = "getAlertEmailAddress";
        String emailAddr = null;
        try {
            final DataObject notifyDO = getAlertSettingDO(alertName, paramValue);
            if (!notifyDO.isEmpty()) {
                final Row row = notifyDO.getFirstRow("AlertSettings");
                emailAddr = (String)row.get("EMAIL_ADDR");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting the variables for alertName " + alertName + " and paramValue " + paramValue, e);
        }
        return emailAddr;
    }
    
    public int getAlertEmailEnabledDays(final String alertName, final Long paramValue) {
        final String methodName = "getAlertEmailEnabledDays";
        int enabledDays = 0;
        try {
            final DataObject notifyDO = getAlertSettingDO(alertName, paramValue);
            if (!notifyDO.isEmpty()) {
                final Row row = notifyDO.getFirstRow("AlertSettings");
                enabledDays = (int)row.get("ENABLED_DAYS");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(AlertsUtil.logger, this.className, methodName, "Exception while getting the variables for alertName " + alertName + " and paramValue " + paramValue, e);
        }
        return enabledDays;
    }
    
    public void deleteAlertTemplate(final Long customerId, final Long alertConstantId) throws DataAccessException {
        Criteria customerCriteria = new Criteria(Column.getColumn("CustomerTechEmailDCAlert", "CUSTOMER_ID"), (Object)customerId, 0);
        Criteria alertCriteria = new Criteria(Column.getColumn("CustomerTechEmailDCAlert", "ALERT_TYPE_ID"), (Object)alertConstantId, 0);
        SyMUtil.getPersistence().delete(customerCriteria.and(alertCriteria));
        customerCriteria = new Criteria(Column.getColumn("CustomerEmailDCAlert", "CUSTOMER_ID"), (Object)customerId, 0);
        alertCriteria = new Criteria(Column.getColumn("CustomerEmailDCAlert", "ALERT_TYPE_ID"), (Object)alertConstantId, 0);
        SyMUtil.getPersistence().delete(customerCriteria.and(alertCriteria));
    }
    
    static {
        AlertsUtil.logger = Logger.getLogger("AlertsUtil");
        AlertsUtil.alertws = null;
    }
}
