package com.me.devicemanagement.framework.server.alerts;

import javax.xml.transform.Transformer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.i18n.I18N;
import java.util.Enumeration;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.net.URLDecoder;
import java.util.List;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class AlertMailGeneratorUtil
{
    Logger logger;
    private String sourceClass;
    
    public AlertMailGeneratorUtil() {
        this.sourceClass = "AlertMailGeneratorUtil";
        this.logger = Logger.getLogger(this.sourceClass);
    }
    
    public AlertMailGeneratorUtil(final Logger log) {
        this.sourceClass = "AlertMailGeneratorUtil";
        this.logger = log;
    }
    
    public void sendMail(final Long alertType, final String module, final Long customerId, final Properties prop) {
        this.sendMail(alertType, module, customerId, Arrays.asList(prop));
    }
    
    public void sendMail(final Long alertType, final String module, final Long customerId, final List<Properties> propList) {
        final Properties prop = propList.get(0);
        final String sourceMethod = "sendMail";
        try {
            Properties subDescProp;
            if (prop.get("$tech_id$") != null) {
                final Long technicianID = Long.valueOf(((Hashtable<K, Object>)prop).get("$tech_id$").toString());
                subDescProp = AlertsUtil.getInstance().getCustomerKeyDescription(customerId, alertType, technicianID);
            }
            else {
                subDescProp = AlertsUtil.getInstance().getCustomerKeyDescription(customerId, alertType);
            }
            String subject = subDescProp.getProperty("subject");
            subject = URLDecoder.decode(subject, "UTF-8");
            String description = subDescProp.getProperty("description");
            description = URLDecoder.decode(description, "UTF-8");
            final String sUserEmail = ((Hashtable<K, String>)prop).get("$user_emailid$");
            final Boolean appendFooter = ((Hashtable<K, Boolean>)prop).get("appendFooter");
            description = this.getDescription(module, appendFooter, prop, description, alertType);
            subject = this.expandedSubDescription(prop, subject);
            description = this.constructDescriptionForMail(propList, description);
            if (subject == null || description == null) {
                return;
            }
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Mail Subject--" + subject);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Mail Description--" + description);
            String callBackHandler = null;
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS()) {
                callBackHandler = (String)DBUtil.getValueFromDB("MailAlertCallBackHandler", "ALERT_ID", alertType, "CALLBACK_HANDLER");
            }
            if (callBackHandler != null && callBackHandler.isEmpty()) {
                callBackHandler = null;
            }
            JSONObject additionalParams = new JSONObject();
            if (prop.containsKey("additionalParams") && prop.get("additionalParams") != null) {
                additionalParams = ((Hashtable<K, JSONObject>)prop).get("additionalParams");
            }
            final String[] attachmentFilePaths = ((Hashtable<K, String[]>)prop).get("$attachment-path$");
            if (attachmentFilePaths != null && attachmentFilePaths.length >= 0) {
                this.sendAlertMail(customerId, subject, description, module, sUserEmail, null, attachmentFilePaths, callBackHandler, additionalParams);
            }
            else {
                this.sendAlertMail(customerId, subject, description, module, sUserEmail, null, null, callBackHandler, additionalParams);
            }
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Alert is send for AlertType=" + alertType);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while sending mail for Alert type" + alertType, ex);
        }
    }
    
    private String constructDescriptionForMail(final List<Properties> propList, final String description) {
        final StringBuffer tempDescription = new StringBuffer();
        if (propList.size() > 1) {
            final int rowStart = description.indexOf("<tr class=\"data\">");
            final int rowEnd = description.indexOf("</tr>", rowStart) + "</tr>".length();
            final String dataRow = description.substring(rowStart, rowEnd);
            tempDescription.append(this.expandedSubDescription(propList.get(0), description));
            for (int i = 1; i < propList.size(); ++i) {
                tempDescription.insert(rowStart, this.expandedSubDescription(propList.get(i), dataRow));
            }
        }
        else {
            tempDescription.append(this.expandedSubDescription(propList.get(0), description));
        }
        return tempDescription.toString();
    }
    
    private String getDescription(final String module, final Boolean appendFooter, final Properties prop, String description, final Long alertType) {
        if (module != null && (!module.trim().startsWith("MDM") || (appendFooter != null && appendFooter))) {
            description = this.appendNote(description, alertType, prop);
        }
        else {
            description = ApiFactoryProvider.getMailSettingAPI().appendFooterNote(description, prop);
        }
        return description;
    }
    
    public void sendAlertMail(final long customerId, final String subject, final String description, final String module, String strToAddress, final Long technicianID, final String[] attachmentFilePaths, final String callBackHandler, final JSONObject additionaParams) {
        final String sourceMethod = "sendAlertsMail";
        try {
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String fromName = mailSenderDetails.get("mail.fromName");
            final String fromAddress = mailSenderDetails.get("mail.fromAddress");
            String emailId = null;
            if (technicianID == null) {
                emailId = this.getCustomerEMailAddress(customerId, module);
            }
            else {
                emailId = this.getCustomerEMailAddress(technicianID, customerId, module);
            }
            if (emailId != null) {
                if (strToAddress == null) {
                    strToAddress = emailId;
                }
                else {
                    strToAddress = strToAddress + "," + emailId;
                }
            }
            if (strToAddress == null || strToAddress.isEmpty()) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "E-Mail address is null.  Cant Proceed!!!");
                return;
            }
            final String strMessage = description;
            if (strMessage == null || "".equals(strMessage) || "null".equals(strMessage)) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Mail Body Contents is NULL.  Cant Proceed!!!");
                return;
            }
            final MailDetails mailDetails = new MailDetails(fromAddress, strToAddress);
            mailDetails.bodyContent = strMessage;
            mailDetails.senderDisplayName = fromName;
            mailDetails.subject = subject;
            mailDetails.callBackHandler = callBackHandler;
            mailDetails.additionalParams = additionaParams;
            boolean attachmentFlag = true;
            if (attachmentFilePaths != null) {
                for (int i = 0; i < attachmentFilePaths.length; ++i) {
                    if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(attachmentFilePaths[i]) || ApiFactoryProvider.getFileAccessAPI().isDirectory(attachmentFilePaths[i])) {
                        attachmentFlag = false;
                        SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, attachmentFilePaths[i] + " File doesn't exist. Hence Files are not attached");
                        break;
                    }
                }
                if (attachmentFlag) {
                    mailDetails.attachment = attachmentFilePaths;
                }
            }
            ApiFactoryProvider.getMailSettingAPI().addToMailQueue(mailDetails, 0);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Successfully added alert mail to mail queue");
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured at sendAlertMail", ex);
        }
    }
    
    public String getCustomerEMailAddress(final Long customerId, final String module) throws Exception {
        final String sourceMethod = "getCustomerEMailAddress";
        String strEMailAddr = null;
        try {
            final DataObject dobj = this.getCustomerEMailAddressDO(customerId, module);
            if (dobj != null && !dobj.isEmpty()) {
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
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured!!", ex);
        }
        return strEMailAddr;
    }
    
    public DataObject getCustomerEMailAddressDO(final Long customerId, final String module) throws Exception {
        final String sourceMethod = "getCustomerEMailAddressDO";
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
            return dobj;
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured!!", ex);
            return null;
        }
    }
    
    public String getCustomerEMailAddress(final Long technicianID, final Long customerId, final String module) throws Exception {
        final String sourceMethod = "getCustomerEMailAddress";
        String strEMailAddr = null;
        try {
            final DataObject dobj = this.getCustomerEMailAddressDO(technicianID, customerId, module);
            if (dobj != null && !dobj.isEmpty()) {
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
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured!!", ex);
        }
        return strEMailAddr;
    }
    
    public DataObject getCustomerEMailAddressDO(final Long technicianID, final Long customerId, final String module) throws Exception {
        final String sourceMethod = "getCustomerEMailAddressDO";
        try {
            final Column col1 = Column.getColumn("EMailAddr", "MODULE");
            final Criteria crit1 = new Criteria(col1, (Object)module, 0, false);
            final Column col2 = Column.getColumn("EMailAddr", "SEND_MAIL");
            final Criteria crit2 = new Criteria(col2, (Object)Boolean.TRUE, 0, false);
            Criteria cri = crit1.and(crit2);
            cri = cri.and(Column.getColumn("EMailAddrToCustTechRel", "LOGIN_ID"), (Object)technicianID, 0);
            cri = cri.and(Column.getColumn("EMailAddrToCustTechRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EMailAddr"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Join join = new Join("EMailAddr", "EMailAddrToCustTechRel", new String[] { "EMAIL_ADDR_ID" }, new String[] { "EMAIL_ADDR_ID" }, 2);
            query.setCriteria(cri);
            query.addJoin(join);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            return dobj;
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured!!", ex);
            return null;
        }
    }
    
    public String expandedSubDescription(final Properties prop, final String description) {
        String expDescri = "";
        try {
            StringBuffer expDescriBuff = new StringBuffer(description);
            final Enumeration enumeration = prop.keys();
            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final String value = ((Hashtable<K, Object>)prop).get(key).toString();
                expDescriBuff = this.replaceSubject(expDescriBuff, key, value);
            }
            expDescri = expDescriBuff.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured at expandedSubDescription", ex);
        }
        return expDescri;
    }
    
    public String appendNote(String description, final Long alertType, final Properties prop) {
        final String footer = "";
        String prodName = "";
        try {
            final Row row = DBUtil.getRowFromDB("EmailFooterToTypeRel", "ALERT_TYPE_ID", alertType);
            if (row != null) {
                final String emailFooter = (String)row.get("EMAIL_FOOTER");
                final String footerVariable = (String)row.get("FOOTER_VARIABLES");
                final Object[] var = footerVariable.split(",");
                description += I18N.getMsg(emailFooter, var);
            }
            String hostName = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
            if (!hostName.startsWith("http")) {
                hostName = "http://" + hostName + "/";
            }
            ((Hashtable<String, String>)prop).put("$baseUrl$", hostName);
            prodName = ProductUrlLoader.getInstance().getValue("displayname");
            if (prodName == null || prodName.equals("")) {
                prodName = " Endpoint Central";
            }
            ((Hashtable<String, String>)prop).put("$productName$", prodName);
            description = ApiFactoryProvider.getMailSettingAPI().appendFooterNote(description, prop);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception occured while appending footer" + e);
        }
        return description;
    }
    
    public StringBuffer replaceSubject(StringBuffer descriBuff, final String key, final String value) {
        try {
            for (int i = descriBuff.indexOf(key); i != -1; i = descriBuff.indexOf(key)) {
                descriBuff = descriBuff.replace(i, i + key.length(), value);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured at replaceSubject", ex);
        }
        return descriBuff;
    }
    
    public void setCustomerEMailAddress(final long customerId, final String strEMailAddress, final String module) throws Exception {
        final String sourceMethod = "setCustomerEMailAddress";
        try {
            Iterator itr = null;
            if (strEMailAddress == null) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Sent E-Mail IDs : " + strEMailAddress);
                throw new Exception("No E-Mail ids sent for storage");
            }
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Calling getEMailAddressArray(...) using string : " + strEMailAddress);
            final String[] arrAddress = this.getEMailAddressArray(strEMailAddress);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Array of E-Mail Ids : " + arrAddress);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EMailAddr"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Join join = new Join("EMailAddr", "CustomerEmailAddrRel", new String[] { "EMAIL_ADDR_ID" }, new String[] { "EMAIL_ADDR_ID" }, 2);
            final Column col = Column.getColumn("CustomerEmailAddrRel", "CUSTOMER_ID");
            final Criteria crit = new Criteria(col, (Object)customerId, 0, false);
            query.setCriteria(crit);
            query.addJoin(join);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "EMailAddr DataObject : " + dobj);
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
                AlertsUtil.getInstance().addCustomerEmailRel(customerId, itr);
            }
            else {
                boolean isUpdated = false;
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
                        final String emailAddr = (String)row2.get("EMAIL_ADDR");
                        if (!emailAddr.equals(arrAddress[j]) && emailAddr.equalsIgnoreCase(arrAddress[j])) {
                            row2.set("EMAIL_ADDR", (Object)arrAddress[j]);
                        }
                        row2.set("SEND_MAIL", (Object)Boolean.TRUE);
                        dobj.updateRow(row2);
                        if (!isUpdated) {
                            isUpdated = true;
                        }
                    }
                }
                SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "EMailAddr DataObject before adding in DB : " + dobj);
                if (isAdded) {
                    SyMUtil.getPersistence().add(newDObj);
                    itr = newDObj.getRows("EMailAddr");
                    AlertsUtil.getInstance().addCustomerEmailRel(customerId, itr);
                }
                final Column col4 = Column.getColumn("EMailAddr", "MODULE");
                final Criteria crit4 = new Criteria(col4, (Object)module, 0, false);
                final Iterator iter = dobj.getRows("EMailAddr", crit4);
                while (iter.hasNext()) {
                    boolean isPresent = false;
                    final Row row3 = iter.next();
                    final String strMailID = (String)row3.get("EMAIL_ADDR");
                    for (int k = 0; k < arrAddress.length; ++k) {
                        if (strMailID.compareToIgnoreCase(arrAddress[k]) == 0) {
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
                        isUpdated = true;
                    }
                }
                if (isUpdated) {
                    SyMUtil.getPersistence().update(dobj);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured!!", ex);
        }
    }
    
    public String[] getEMailAddressArray(final String strMailAddress) throws Exception {
        final String sourceMethod = "getEMailAddressArray";
        if (strMailAddress == null) {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "String sent for parsing is null");
            return null;
        }
        SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Received EMail Address : " + strMailAddress);
        final ArrayList arrMailAddress = new ArrayList();
        final ArrayList arrMail = new ArrayList();
        String[] arrayMailAdd = null;
        try {
            final StringTokenizer st = new StringTokenizer(strMailAddress, ",");
            final int size = st.countTokens();
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "EMail Address count : " + size);
            for (int j = 0; j < size; ++j) {
                String str = st.nextToken();
                str = str.trim();
                if (!arrMail.contains(str.toLowerCase())) {
                    arrMailAddress.add(str);
                    arrMail.add(str.toLowerCase());
                }
            }
            arrayMailAdd = new String[arrMailAddress.size()];
            arrMailAddress.toArray(arrayMailAdd);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Array of EMail Address : " + arrayMailAdd);
        }
        catch (final Exception exp) {
            throw exp;
        }
        return arrayMailAdd;
    }
    
    public String generateXMLFromHashMap(final HashMap hashMap, final String root, final String rootElementValue) {
        try {
            final DocumentBuilder docBuilder = DMSecurityUtil.getDocumentBuilder();
            final Document document = docBuilder.newDocument();
            final Element rootElement = document.createElement(root);
            document.appendChild(rootElement);
            final Element element = document.createElement(rootElementValue);
            rootElement.appendChild(element);
            for (final Object key : hashMap.keySet()) {
                final Attr attribute = document.createAttribute((String)key);
                attribute.setValue(hashMap.get(key));
                element.setAttributeNode(attribute);
            }
            final DOMSource domSource = new DOMSource(document);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final Transformer transformer = XMLUtils.getTransformerInstance();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch (final TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
