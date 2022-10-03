package com.me.ems.framework.common.api.v1.service;

import com.me.devicemanagement.framework.webclient.alert.AlertsAPI;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SortColumn;
import com.me.ems.framework.common.api.v1.model.EmailTemplateKeys;
import java.util.List;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.mailmanager.MailContentGeneratorUtil;
import java.io.File;
import java.net.URLEncoder;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.ems.framework.common.api.v1.model.EmailTemplate;
import java.util.logging.Logger;

public class EmailTemplateService
{
    private static String className;
    protected static Logger logger;
    
    public EmailTemplate getCustomerKeyDescription(final Long customerId, final Long alertConstantId) {
        final String methodName = "getCustomerKeyDescription";
        EmailTemplate dcEmailTemplate = new EmailTemplate();
        try {
            String subject = null;
            String description = null;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerEmailDCAlert"));
            final Column alertIdCol = Column.getColumn("CustomerEmailDCAlert", "ALERT_TYPE_ID");
            final Column customerCol = Column.getColumn("CustomerEmailDCAlert", "CUSTOMER_ID");
            final Criteria alertCriteria = new Criteria(alertIdCol, (Object)alertConstantId, 0);
            final Criteria customerCriteria = new Criteria(customerCol, (Object)customerId, 0);
            final Criteria criteria = alertCriteria.and(customerCriteria);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final Row row = dobj.getRow("CustomerEmailDCAlert");
                subject = (String)row.get("SUBJECT");
                description = (String)row.get("DESCRIPTION");
                dcEmailTemplate.setAlertID(alertConstantId);
                if (subject != null) {
                    dcEmailTemplate.setSubject(subject);
                }
                if (description != null) {
                    dcEmailTemplate.setDescription(description);
                }
                dcEmailTemplate.setAlertReconfigured(Boolean.TRUE);
            }
            else {
                dcEmailTemplate = this.getKeyDescription(alertConstantId);
                dcEmailTemplate.setAlertReconfigured(Boolean.FALSE);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EmailTemplateService.logger, EmailTemplateService.className, methodName, " Exception occured at AlertsUtil-getCustomerKeyDescription  ", ex);
        }
        SyMLogger.info(EmailTemplateService.logger, EmailTemplateService.className, methodName, "DCEmailTemplate from getCustomerKeyDescription" + dcEmailTemplate);
        return dcEmailTemplate;
    }
    
    public EmailTemplate getCustomerKeyDescription(final Long customerId, final Long alertConstantId, final Long technicianID) {
        final String methodName = "getCustomerKeyDescription";
        EmailTemplate dcEmailTemplate = new EmailTemplate();
        try {
            String subject = null;
            String description = null;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerTechEmailDCAlert"));
            final Column alertIdCol = Column.getColumn("CustomerTechEmailDCAlert", "ALERT_TYPE_ID");
            final Column cusCol = Column.getColumn("CustomerTechEmailDCAlert", "CUSTOMER_ID");
            final Column technicianColumn = Column.getColumn("CustomerTechEmailDCAlert", "TECH_ID");
            final Criteria alertCriteria = new Criteria(alertIdCol, (Object)alertConstantId, 0);
            final Criteria customerCriteria = new Criteria(cusCol, (Object)customerId, 0);
            final Criteria techCriteria = new Criteria(technicianColumn, (Object)technicianID, 0);
            final Criteria criteria = alertCriteria.and(customerCriteria).and(techCriteria);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("CustomerTechEmailDCAlert");
                subject = (String)row.get("SUBJECT");
                description = (String)row.get("DESCRIPTION");
                dcEmailTemplate.setAlertID(alertConstantId);
                if (subject != null) {
                    dcEmailTemplate.setSubject(subject);
                }
                if (description != null) {
                    dcEmailTemplate.setDescription(description);
                }
                dcEmailTemplate.setAlertReconfigured(Boolean.TRUE);
            }
            else {
                dcEmailTemplate = this.getKeyDescription(alertConstantId);
                dcEmailTemplate.setAlertReconfigured(Boolean.FALSE);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EmailTemplateService.logger, EmailTemplateService.className, methodName, " Exception occured at AlertsUtil-getCustomerKeyDescription  ", ex);
        }
        SyMLogger.info(EmailTemplateService.logger, EmailTemplateService.className, methodName, "Properties from getCustomerKeyDescription" + dcEmailTemplate);
        return dcEmailTemplate;
    }
    
    public EmailTemplate getKeyDescription(final Long alertConstantId) {
        final String methodName = "getKeyDescription";
        final EmailTemplate dcEmailTemplate = new EmailTemplate();
        try {
            String subject = null;
            String description = null;
            String subjectvariable = null;
            String descriptionvariable = "";
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EmailDCAlertTask"));
            final Column idColumn = Column.getColumn("EmailDCAlertTask", "ALERT_TYPE_ID");
            final Criteria alertCriteria = new Criteria(idColumn, (Object)alertConstantId, 0);
            query.setCriteria(alertCriteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("EmailDCAlertTask");
                subject = (String)row.get("SUBJECT");
                description = (String)row.get("DESCRIPTION");
                subjectvariable = (String)row.get("SUBJECT_VARIABLES");
                descriptionvariable = (String)row.get("DESCRIPTION_VARIABLES");
            }
            if (subject != null) {
                final Object[] arguments = (Object[])(subjectvariable.equalsIgnoreCase("") ? null : subjectvariable.split(","));
                subject = ((arguments != null) ? I18N.getMsg(subject, arguments) : I18N.getMsg(subject, new Object[0]));
                dcEmailTemplate.setSubject(URLEncoder.encode(subject, "UTF-8"));
            }
            if (description != null) {
                final Object[] arguments = (Object[])(descriptionvariable.equalsIgnoreCase("") ? null : descriptionvariable.split(","));
                description = ((arguments != null) ? I18N.getMsg(description, arguments) : I18N.getMsg(description, new Object[0]));
            }
            final String emaildescVar = this.getEmailValueToAlertType(alertConstantId);
            if (emaildescVar != null) {
                description += emaildescVar;
            }
            dcEmailTemplate.setDescription(URLEncoder.encode(description, "UTF-8"));
        }
        catch (final Exception ex) {
            SyMLogger.error(EmailTemplateService.logger, EmailTemplateService.className, methodName, " Exception occured at AlertsUtil-getKeyDescription  ", ex);
        }
        SyMLogger.info(EmailTemplateService.logger, EmailTemplateService.className, methodName, "Properties from getKeyDescription" + dcEmailTemplate);
        return dcEmailTemplate;
    }
    
    protected String getEmailValueToAlertType(final Long alertType) {
        final String methodName = "getEmailValueToAlertType";
        String desValues = null;
        try {
            final Criteria cAlertType = new Criteria(new Column("EmailValueToTypeRel", "ALERT_TYPE_ID"), (Object)alertType, 0);
            final DataObject DO = SyMUtil.getPersistence().get("EmailValueToTypeRel", cAlertType);
            if (DO != null && !DO.isEmpty()) {
                final Iterator item = DO.getRows("EmailValueToTypeRel");
                while (item.hasNext()) {
                    final Row row = item.next();
                    row.set("ALERT_VALUE", (Object)I18N.getMsg((String)row.get("ALERT_VALUE"), new Object[0]));
                    DO.updateRow(row);
                }
                final String emailXsl = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "xsl" + File.separator + "EmailAlertInfo.xsl";
                final MailContentGeneratorUtil mg = new MailContentGeneratorUtil();
                desValues = mg.getHTMLContent(emailXsl, DO, "Alert");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(EmailTemplateService.logger, EmailTemplateService.className, methodName, "Exception while getting the variables for alert ID " + alertType, e);
        }
        return desValues;
    }
    
    public List<EmailTemplateKeys> getTemplateKeys(final Long alertConstantId) {
        final String methodName = "getAlertKeyValueMap";
        List<EmailTemplateKeys> dcEmailTemplateKeysList = null;
        try {
            final SortColumn subSort = new SortColumn(Column.getColumn("AlertKey", "ALERT_KEY"), true);
            final Join join1 = new Join("AlertKeytoTypeRel", "DCAlertType", new String[] { "ALERT_TYPE_ID" }, new String[] { "ALERT_TYPE_ID" }, 2);
            final Join join2 = new Join("AlertKeytoTypeRel", "AlertKey", new String[] { "ALERT_KEY_ID" }, new String[] { "ALERT_KEY_ID" }, 2);
            final Column alertCol = Column.getColumn("AlertKeytoTypeRel", "ALERT_TYPE_ID");
            final Criteria alertCriteia = new Criteria(alertCol, (Object)alertConstantId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AlertKeytoTypeRel"));
            query.addJoin(join1);
            query.addJoin(join2);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addSortColumn(subSort);
            query.setCriteria(alertCriteia);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final Iterator iter = dobj.getRows("AlertKey");
                dcEmailTemplateKeysList = new ArrayList<EmailTemplateKeys>();
                while (iter.hasNext()) {
                    final EmailTemplateKeys dmEmailTemplateKey = new EmailTemplateKeys();
                    final Row row = iter.next();
                    dmEmailTemplateKey.setKey((String)row.get("ALERT_KEY"));
                    dmEmailTemplateKey.setDescription(I18N.getMsg((String)row.get("ALERT_KEY_DESCRIPTION"), new Object[0]));
                    dcEmailTemplateKeysList.add(dmEmailTemplateKey);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(EmailTemplateService.logger, EmailTemplateService.className, methodName, "Exception occured at DCAlertFormatAction-subMsgQuery  ", ex);
        }
        SyMLogger.info(EmailTemplateService.logger, EmailTemplateService.className, methodName, "LinkedHashMap from subMsgQuery" + dcEmailTemplateKeysList);
        return dcEmailTemplateKeysList;
    }
    
    public boolean isAlertTechnicianSegmented(final Long alertConstantID) {
        final AlertsAPI api = WebclientAPIFactoryProvider.getAlertAPI();
        try {
            return api == null || api.isAlertTechnicianSegmented(alertConstantID);
        }
        catch (final Exception e) {
            EmailTemplateService.logger.log(Level.SEVERE, "Exception occurred : " + e);
            return true;
        }
    }
    
    static {
        EmailTemplateService.className = EmailTemplateService.class.getName();
        EmailTemplateService.logger = Logger.getLogger(EmailTemplateService.className);
    }
}
