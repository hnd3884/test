package com.me.mdm.server.windows.apps;

import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.DeleteQuery;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import org.apache.commons.lang.StringEscapeUtils;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.apps.windows.BusinessStoreAPIAccess;
import com.me.mdm.server.apps.businessstore.EnterpriseBusinessStoreDBUtils;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.apps.businessstore.windows.WindowsStoreHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.windows.BusinessStoreSetupAPI;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONArray;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.files.FileFacade;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.io.File;
import java.util.Date;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import sun.misc.BASE64Decoder;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;

public class WpAppSettingsHandler
{
    public static final String WINDOWS_APP_DEFAULT_BG = "#0078d7";
    public static HashMap<String, HashMap<String, Criteria>> compatibilityCriteriaMap;
    public static Logger logger;
    private static WpAppSettingsHandler wpAETHandler;
    public static final String JSON_CONFIGURED = "JSON_CONFIGURED";
    public static final String XML_CONFIGURED = "XML_CONFIGURED";
    public static final String CONFIG_MAX_LENGTH = "CONFIG_MAX_LENGTH";
    private static List<String> bstoreParams;
    
    public static WpAppSettingsHandler getInstance() {
        if (WpAppSettingsHandler.wpAETHandler == null) {
            WpAppSettingsHandler.wpAETHandler = new WpAppSettingsHandler();
        }
        return WpAppSettingsHandler.wpAETHandler;
    }
    
    public void deleteAET(final Long customerID) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WpAppSettings"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "AET_FILE_PATH"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "APP_ENROLLMENT_TOKEN"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "APP_ENROLLMENT_TOKEN_PATH"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "APP_FILE_PATH"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "EMAIL_ADDRESS"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "ENTERPRISE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "EXPIRE_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CREATION_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CERT_EXPIRE_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CERT_FILE_PATH"));
            selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "DISTRIBUTE_TYPE"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("WpAppSettings");
                row.set("AET_FILE_PATH", (Object)null);
                row.set("APP_ENROLLMENT_TOKEN", (Object)null);
                row.set("APP_ENROLLMENT_TOKEN_PATH", (Object)"--");
                row.set("APP_ID", (Object)null);
                row.set("APP_FILE_PATH", (Object)null);
                row.set("EMAIL_ADDRESS", (Object)null);
                row.set("ENTERPRISE_ID", (Object)null);
                row.set("EXPIRE_TIME", (Object)(-1L));
                row.set("CREATION_TIME", (Object)(-1L));
                row.set("CERT_EXPIRE_TIME", (Object)(-1L));
                row.set("CERT_FILE_PATH", (Object)null);
                row.set("DISTRIBUTE_TYPE", (Object)(-1));
                dataObject.updateRow(row);
                MDMUtil.getPersistence().update(dataObject);
            }
            MessageProvider.getInstance().hideMessage("AET_ABOUT_TO_EXPIRE", customerID);
            MessageProvider.getInstance().hideMessage("AET_EXPIRED", customerID);
        }
        catch (final DataAccessException e) {
            WpAppSettingsHandler.logger.log(Level.WARNING, (Throwable)e, () -> "Coulnt delete AET for " + n);
        }
    }
    
    public JSONObject handleWpAppSettingsRequest(final JSONObject appSettingsDetails) throws JSONException {
        JSONObject wpAETJson = null;
        final String type = String.valueOf(appSettingsDetails.get("TYPE"));
        if (type.equals("1")) {
            wpAETJson = getInstance().handleAET(appSettingsDetails);
            final JSONObject certParams = new JSONObject();
            if (wpAETJson.opt("CERT_FILE_PATH") != null) {
                certParams.put("CERT_FILE_UPLOAD", wpAETJson.get("CERT_FILE_PATH"));
                certParams.put("EMAIL_ADDRESS", wpAETJson.get("EMAIL_ADDRESS"));
                wpAETJson = getInstance().handleCodeSigning(certParams, wpAETJson);
            }
        }
        else if (type.equals("2")) {
            wpAETJson = getInstance().handleCodeSigning(appSettingsDetails, wpAETJson);
        }
        return wpAETJson;
    }
    
    private Properties parseAET(final String localTempFile, final Long customerId, final String aetEmail) throws Exception {
        Properties windowsAETProp = null;
        String appAETFileName = null;
        try {
            final String aetFolderDestPath = this.getAETFolderPath(customerId);
            final String aetDBPath = this.getAETFolderDBPath(customerId);
            final HashMap aetFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(localTempFile, aetFolderDestPath, aetDBPath, false, true);
            String elementName = "";
            String aetTokenValue = "";
            String paramName = "";
            appAETFileName = aetFileSourceMap.get("destFileName");
            final String publishUrl = aetFileSourceMap.get("destDCFileName");
            final InputStream in = ApiFactoryProvider.getFileAccessAPI().getInputStream(appAETFileName);
            final XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
            final XMLStreamReader xmlStreamReader = factory.createXMLStreamReader(in);
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.getEventType() == 1) {
                    elementName = xmlStreamReader.getLocalName();
                    if (elementName.equalsIgnoreCase("parm")) {
                        paramName = xmlStreamReader.getAttributeValue(null, "name");
                        if (paramName.equalsIgnoreCase("EnrollmentToken")) {
                            aetTokenValue = xmlStreamReader.getAttributeValue(null, "value");
                        }
                    }
                }
                xmlStreamReader.next();
            }
            windowsAETProp = new Properties();
            ((Hashtable<String, Long>)windowsAETProp).put("CUSTOMER_ID", customerId);
            ((Hashtable<String, String>)windowsAETProp).put("EMAIL_ADDRESS", aetEmail);
            ((Hashtable<String, String>)windowsAETProp).put("APP_ENROLLMENT_TOKEN", aetTokenValue);
            ((Hashtable<String, String>)windowsAETProp).put("AET_FILE_PATH", publishUrl);
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in parseAET", ex);
            if (appAETFileName != null) {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(appAETFileName);
            }
        }
        return windowsAETProp;
    }
    
    private Properties parseCert(final String localTempFile, final Long customerId, final String expEmail) throws Exception {
        Properties windowsAETProp = null;
        String certFileName = null;
        try {
            final String certFolderDestPath = this.getCertFolderPath(customerId);
            final String certDBPath = this.getCertFolderDBPath(customerId);
            final JSONObject jsonObject = CredentialsMgmtAction.extractCertificateDetails(localTempFile, null);
            final HashMap aetFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(localTempFile, certFolderDestPath, certDBPath, false, true);
            certFileName = aetFileSourceMap.get("destFileName");
            final String publishUrl = aetFileSourceMap.get("destDCFileName");
            windowsAETProp = new Properties();
            ((Hashtable<String, Long>)windowsAETProp).put("CUSTOMER_ID", customerId);
            ((Hashtable<String, String>)windowsAETProp).put("EMAIL_ADDRESS", expEmail);
            ((Hashtable<String, Object>)windowsAETProp).put("CERT_EXPIRE_TIME", jsonObject.get("NotAfter"));
            ((Hashtable<String, Object>)windowsAETProp).put("CERT_SUBJECT", jsonObject.get("CERTIFICATE_SUBJECT_DN"));
            ((Hashtable<String, String>)windowsAETProp).put("CERT_FILE_PATH", publishUrl);
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in parseCert", ex);
            if (certFileName != null) {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(certFileName);
            }
        }
        return windowsAETProp;
    }
    
    private Properties fetchWPAETProp(final Properties windowsAETProp) {
        try {
            String enterpriseId = null;
            String elementName = "";
            String certificateStr = "";
            Date expiryDate = null;
            Long expiryDateL = null;
            Date creationDate = null;
            Long creationDateL = null;
            boolean saveNextCert = false;
            final String aetTokenValue = ((Hashtable<K, String>)windowsAETProp).get("APP_ENROLLMENT_TOKEN");
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] certBytes = decoder.decodeBuffer(aetTokenValue);
            InputStream certinputstream = new ByteArrayInputStream(certBytes);
            final XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
            final XMLStreamReader xmlStreamReader = factory.createXMLStreamReader(certinputstream);
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.getEventType() == 1) {
                    elementName = xmlStreamReader.getLocalName();
                    if (elementName.equalsIgnoreCase("EnterpriseId")) {
                        enterpriseId = xmlStreamReader.getAttributeValue(null, "Value");
                    }
                    else if (elementName.equalsIgnoreCase("X509SubjectName")) {
                        if (xmlStreamReader.getElementText().equals("CA")) {
                            saveNextCert = true;
                        }
                    }
                    else if (elementName.equalsIgnoreCase("X509Certificate")) {
                        certificateStr = xmlStreamReader.getElementText();
                        decoder = new BASE64Decoder();
                        certBytes = decoder.decodeBuffer(certificateStr);
                        certinputstream = new ByteArrayInputStream(certBytes);
                        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        final X509Certificate cert = (X509Certificate)cf.generateCertificate(certinputstream);
                        expiryDate = cert.getNotAfter();
                        expiryDateL = expiryDate.getTime();
                        creationDate = cert.getNotBefore();
                        creationDateL = creationDate.getTime();
                        if (saveNextCert) {
                            ((Hashtable<String, String>)windowsAETProp).put("CERT_FILE_PATH", this.persistCertificate(certBytes, ((Hashtable<K, Long>)windowsAETProp).get("CUSTOMER_ID")));
                            saveNextCert = false;
                        }
                    }
                }
                xmlStreamReader.next();
            }
            ((Hashtable<String, String>)windowsAETProp).put("ENTERPRISE_ID", enterpriseId);
            ((Hashtable<String, Long>)windowsAETProp).put("CREATION_TIME", creationDateL);
            ((Hashtable<String, Long>)windowsAETProp).put("EXPIRE_TIME", expiryDateL);
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in fetchWPAETProp", ex);
        }
        return windowsAETProp;
    }
    
    private String persistCertificate(final byte[] certificateContent, final Long customerID) throws Exception {
        final String fileName = this.getCertFolderPath(customerID) + File.separator + "temp" + File.separator + "CACert.cer";
        ApiFactoryProvider.getFileAccessAPI().writeFile(fileName, certificateContent);
        return fileName;
    }
    
    private DataObject addorUpdateWPAETDetails(final Properties windowsAETProp, final Long customerId) {
        DataObject dObj = null;
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            dObj = MDMUtil.getPersistence().get("WpAppSettings", cusCri);
            final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
            final String sUserName = hash.get("UserName");
            String sEventLogRemarks = "";
            final Object remarksArgs = null;
            Row wpCodeSignRow = null;
            if (dObj.isEmpty()) {
                wpCodeSignRow = new Row("WpAppSettings");
                wpCodeSignRow.set("CUSTOMER_ID", ((Hashtable<K, Object>)windowsAETProp).get("CUSTOMER_ID"));
                wpCodeSignRow.set("ENTERPRISE_ID", ((Hashtable<K, Object>)windowsAETProp).get("ENTERPRISE_ID"));
                wpCodeSignRow.set("APP_ENROLLMENT_TOKEN", ((Hashtable<K, Object>)windowsAETProp).get("APP_ENROLLMENT_TOKEN"));
                wpCodeSignRow.set("CREATION_TIME", ((Hashtable<K, Object>)windowsAETProp).get("CREATION_TIME"));
                wpCodeSignRow.set("EXPIRE_TIME", ((Hashtable<K, Object>)windowsAETProp).get("EXPIRE_TIME"));
                wpCodeSignRow.set("EMAIL_ADDRESS", ((Hashtable<K, Object>)windowsAETProp).get("EMAIL_ADDRESS"));
                wpCodeSignRow.set("AET_FILE_PATH", ((Hashtable<K, Object>)windowsAETProp).get("AET_FILE_PATH"));
                dObj.addRow(wpCodeSignRow);
                MDMUtil.getPersistence().add(dObj);
                sEventLogRemarks = "dc.mdm.actionlog.settings.AET_added_success";
            }
            else {
                wpCodeSignRow = dObj.getRow("WpAppSettings");
                wpCodeSignRow.set("ENTERPRISE_ID", ((Hashtable<K, Object>)windowsAETProp).get("ENTERPRISE_ID"));
                wpCodeSignRow.set("APP_ENROLLMENT_TOKEN", ((Hashtable<K, Object>)windowsAETProp).get("APP_ENROLLMENT_TOKEN"));
                wpCodeSignRow.set("CREATION_TIME", ((Hashtable<K, Object>)windowsAETProp).get("CREATION_TIME"));
                wpCodeSignRow.set("EXPIRE_TIME", ((Hashtable<K, Object>)windowsAETProp).get("EXPIRE_TIME"));
                wpCodeSignRow.set("EMAIL_ADDRESS", ((Hashtable<K, Object>)windowsAETProp).get("EMAIL_ADDRESS"));
                wpCodeSignRow.set("AET_FILE_PATH", ((Hashtable<K, Object>)windowsAETProp).get("AET_FILE_PATH"));
                dObj.updateRow(wpCodeSignRow);
                MDMUtil.getPersistence().update(dObj);
                sEventLogRemarks = "dc.mdm.actionlog.settings.AET_edit_success";
            }
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2035, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
            this.validateAETExpiry(customerId, false);
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateWpCodeSighningDetails", ex);
        }
        return dObj;
    }
    
    private DataObject addorUpdateWPCertDetails(final Properties windowsAETProp, final Long customerId) {
        DataObject dObj = null;
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            dObj = MDMUtil.getPersistence().get("WpAppSettings", cusCri);
            final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
            final String sUserName = hash.get("UserName");
            String sEventLogRemarks = "";
            final Object remarksArgs = null;
            Row wpCodeSignRow = null;
            if (dObj.isEmpty()) {
                wpCodeSignRow = new Row("WpAppSettings");
                wpCodeSignRow.set("CUSTOMER_ID", ((Hashtable<K, Object>)windowsAETProp).get("CUSTOMER_ID"));
                wpCodeSignRow.set("CERT_FILE_PATH", ((Hashtable<K, Object>)windowsAETProp).get("CERT_FILE_PATH"));
                wpCodeSignRow.set("CERT_EXPIRE_TIME", ((Hashtable<K, Object>)windowsAETProp).get("CERT_EXPIRE_TIME"));
                wpCodeSignRow.set("EMAIL_ADDRESS", ((Hashtable<K, Object>)windowsAETProp).get("EMAIL_ADDRESS"));
                dObj.addRow(wpCodeSignRow);
                MDMUtil.getPersistence().add(dObj);
                sEventLogRemarks = "dc.mdm.actionlog.settings.AET_added_success";
            }
            else {
                wpCodeSignRow = dObj.getRow("WpAppSettings");
                wpCodeSignRow.set("CERT_EXPIRE_TIME", ((Hashtable<K, Object>)windowsAETProp).get("CERT_EXPIRE_TIME"));
                wpCodeSignRow.set("EMAIL_ADDRESS", ((Hashtable<K, Object>)windowsAETProp).get("EMAIL_ADDRESS"));
                wpCodeSignRow.set("CERT_FILE_PATH", ((Hashtable<K, Object>)windowsAETProp).get("CERT_FILE_PATH"));
                dObj.updateRow(wpCodeSignRow);
                MDMUtil.getPersistence().update(dObj);
                sEventLogRemarks = "dc.mdm.actionlog.settings.AET_edit_success";
            }
            this.validateCertExpiry(customerId, false);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2035, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
        }
        catch (final DataAccessException e) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateWpCodeSighningDetails", (Throwable)e);
        }
        catch (final Exception e2) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateWpCodeSighningDetails", e2);
        }
        return dObj;
    }
    
    private void applnEnrollTokenUpdate(final Long customerId) {
        try {
            List resList = ManagedDeviceHandler.getInstance().getWindowsPhoneManagedDevicesForCustomer(customerId);
            resList = ManagedDeviceHandler.getInstance().getWindows81AndBelowManagedDeviceResourceIDs(resList);
            DeviceCommandRepository.getInstance().addWindowsCommand(resList, "AppEnrollmentToken");
            NotificationHandler.getInstance().SendNotification(resList, 3);
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.WARNING, "Exception occurred in applnEnrollTokenUpdate() method : {0}", ex);
        }
    }
    
    private void applnCodeSigningUpdate(final Long customerId) {
        try {
            List resList = ManagedDeviceHandler.getInstance().getWindowsPhoneManagedDevicesForCustomer(customerId);
            resList = ManagedDeviceHandler.getInstance().getDevicesEqualOrAboveOsVersion(resList, "10");
            DeviceCommandRepository.getInstance().addWindowsCommand(resList, "AppEnrollmentToken");
            NotificationHandler.getInstance().SendNotification(resList, 3);
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.WARNING, "Exception occurred in applnEnrollTokenUpdate() method : {0}", ex);
        }
    }
    
    private void updateAETExpiryDate(final Long customerId, final boolean isExpired) {
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("WpAppSettings", cusCri);
            Row wpCodeSignRow = null;
            if (!dObj.isEmpty()) {
                wpCodeSignRow = dObj.getRow("WpAppSettings");
                wpCodeSignRow.set("IS_EXPIRED", (Object)isExpired);
                dObj.updateRow(wpCodeSignRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in updateAETExpiryDate", ex);
        }
    }
    
    public Properties getWpAETDetails(final Long customerId) {
        Properties windowsAETProp = null;
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("WpAppSettings", cusCri);
            Long appId = null;
            String aetFilePath = null;
            String appFilePath = null;
            if (!dObj.isEmpty()) {
                windowsAETProp = new Properties();
                final Row wpCodeSignRow = dObj.getRow("WpAppSettings", cusCri);
                ((Hashtable<String, Object>)windowsAETProp).put("CUSTOMER_ID", wpCodeSignRow.get("CUSTOMER_ID"));
                if (wpCodeSignRow.get("ENTERPRISE_ID") != null) {
                    ((Hashtable<String, Object>)windowsAETProp).put("ENTERPRISE_ID", wpCodeSignRow.get("ENTERPRISE_ID"));
                    ((Hashtable<String, Object>)windowsAETProp).put("APP_ENROLLMENT_TOKEN", wpCodeSignRow.get("APP_ENROLLMENT_TOKEN"));
                    ((Hashtable<String, Object>)windowsAETProp).put("CREATION_TIME", wpCodeSignRow.get("CREATION_TIME"));
                    ((Hashtable<String, Object>)windowsAETProp).put("EXPIRE_TIME", wpCodeSignRow.get("EXPIRE_TIME"));
                    ((Hashtable<String, Object>)windowsAETProp).put("EMAIL_ADDRESS", wpCodeSignRow.get("EMAIL_ADDRESS"));
                    ((Hashtable<String, Object>)windowsAETProp).put("IS_EXPIRED", wpCodeSignRow.get("IS_EXPIRED"));
                    ((Hashtable<String, Object>)windowsAETProp).put("DISTRIBUTE_TYPE", wpCodeSignRow.get("DISTRIBUTE_TYPE"));
                }
                if (wpCodeSignRow.get("CERT_FILE_PATH") != null) {
                    ((Hashtable<String, Object>)windowsAETProp).put("CERT_EXPIRE_TIME", wpCodeSignRow.get("CERT_EXPIRE_TIME"));
                    ((Hashtable<String, Object>)windowsAETProp).put("CERT_FILE_PATH", wpCodeSignRow.get("CERT_FILE_PATH"));
                    ((Hashtable<String, Object>)windowsAETProp).put("EMAIL_ADDRESS", wpCodeSignRow.get("EMAIL_ADDRESS"));
                    final String certFilePath = (String)wpCodeSignRow.get("CERT_FILE_PATH");
                    final JSONObject jsonObject = CredentialsMgmtAction.extractCertificateDetails(certFilePath, null);
                    ((Hashtable<String, Object>)windowsAETProp).put("CERT_SUBJECT", jsonObject.get("CERTIFICATE_SUBJECT_DN"));
                    ((Hashtable<String, String>)windowsAETProp).put("CERT_FILE_NAME", certFilePath.substring(certFilePath.lastIndexOf(File.separator) + 1));
                }
                appId = (Long)wpCodeSignRow.get("APP_ID");
                aetFilePath = (String)wpCodeSignRow.get("AET_FILE_PATH");
                appFilePath = (String)wpCodeSignRow.get("APP_FILE_PATH");
                if (appId != null) {
                    ((Hashtable<String, Long>)windowsAETProp).put("APP_ID", appId);
                }
                if (aetFilePath != null) {
                    ((Hashtable<String, String>)windowsAETProp).put("AET_FILE_PATH", aetFilePath);
                    ((Hashtable<String, String>)windowsAETProp).put("AET_FILE_NAME", aetFilePath.substring(aetFilePath.lastIndexOf(File.separator) + 1));
                }
                if (appFilePath != null) {
                    ((Hashtable<String, String>)windowsAETProp).put("APP_FILE_PATH", appFilePath);
                    ((Hashtable<String, String>)windowsAETProp).put("APP_FILE_NAME", appFilePath.substring(appFilePath.lastIndexOf(File.separator) + 1));
                }
            }
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in getWpAETDetails", ex);
        }
        return windowsAETProp;
    }
    
    public boolean isAETUploaded(final Long customerId) {
        boolean isAET = false;
        try {
            final Criteria customerCri = new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("WpAppSettings", customerCri);
            if (!dObj.isEmpty()) {
                final Row row = dObj.getFirstRow("WpAppSettings");
                if (row.get("ENTERPRISE_ID") != null) {
                    isAET = true;
                }
            }
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in isAETUploaded", ex);
        }
        return isAET;
    }
    
    public Long getWpAETExpiryDate(final Long customerId) {
        Long expiryDate = null;
        try {
            expiryDate = (Long)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerId, "EXPIRE_TIME");
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in getWpAETExpiryDate", ex);
        }
        return expiryDate;
    }
    
    public Long getWpCertExpiryDate(final Long customerId) {
        Long expiryDate = null;
        try {
            expiryDate = (Long)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerId, "CERT_EXPIRE_TIME");
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in getWpAETExpiryDate", ex);
        }
        return expiryDate;
    }
    
    public Boolean isWpAETExpired(final Long customerId) {
        Boolean isExpired = null;
        try {
            isExpired = (Boolean)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerId, "IS_EXPIRED");
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in isWpCodeSignExpired", ex);
        }
        return isExpired;
    }
    
    public Boolean isCertExpired(final Long customerId) {
        Boolean isExpired = null;
        Long expTime = null;
        try {
            expTime = (Long)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerId, "CERT_EXPIRE_TIME");
            if (expTime != null) {
                Hashtable ht = new Hashtable();
                ht = DateTimeUtil.determine_From_To_Times("today");
                final Long today = ht.get("date1");
                if (today > expTime) {
                    isExpired = true;
                }
                else {
                    isExpired = false;
                }
            }
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in isWpCodeSignExpired", ex);
        }
        return isExpired;
    }
    
    public Boolean isWpAETExpiredNow(final Long customerID) {
        Boolean isExpired = Boolean.FALSE;
        try {
            final Long expireTime = (Long)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerID, "EXPIRE_TIME");
            if (expireTime != null && expireTime - MDMUtil.getCurrentTimeInMillis() <= 0L) {
                isExpired = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.SEVERE, "Exception in isWpAETExpiredNow");
        }
        return isExpired;
    }
    
    public void validateCertExpiry(final boolean sendMail) throws Exception {
        final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        if (customerIds != null) {
            for (final Long customerId : customerIds) {
                this.validateCertExpiry(customerId, sendMail);
            }
        }
    }
    
    private void validateAETExpiry(final Long customerId, final boolean sendMail) throws Exception {
        final Properties windowsAETProp = this.getWpAETDetails(customerId);
        if (windowsAETProp != null && windowsAETProp.getProperty("ENTERPRISE_ID") != null) {
            final Long expiryDate = ((Hashtable<K, Long>)windowsAETProp).get("EXPIRE_TIME");
            final String emailAddress = ((Hashtable<K, String>)windowsAETProp).get("EMAIL_ADDRESS");
            Hashtable ht = new Hashtable();
            ht = DateTimeUtil.determine_From_To_Times("today");
            final Long today = ht.get("date1");
            boolean isExpiry = false;
            if (today > expiryDate) {
                isExpiry = true;
            }
            else {
                MessageProvider.getInstance().hideMessage("AET_ABOUT_TO_EXPIRE", customerId);
                MessageProvider.getInstance().hideMessage("AET_EXPIRED", customerId);
            }
            this.updateAETExpiryDate(customerId, isExpiry);
            final Long alertDate = expiryDate - 7776000000L;
            if (today > alertDate) {
                isExpiry = true;
                if (sendMail && !MDMStringUtils.isEmpty(emailAddress)) {
                    final Long remaingDay = this.getAETExpiryPendingDays(expiryDate);
                    final Properties prop = new Properties();
                    ((Hashtable<String, String>)prop).put("$aet_expiry_date$", MDMUtil.getDate((long)expiryDate));
                    ((Hashtable<String, String>)prop).put("$aet_user_emailid$", emailAddress);
                    ((Hashtable<String, Long>)prop).put("$remaingDay$", remaingDay);
                    if (remaingDay > 0L) {
                        MessageProvider.getInstance().unhideMessage("AET_ABOUT_TO_EXPIRE", customerId);
                        MessageProvider.getInstance().hideMessage("AET_EXPIRED", customerId);
                        MDMMailNotificationHandler.getInstance().sendAETAboutToExpireMail(prop);
                    }
                    else {
                        MessageProvider.getInstance().unhideMessage("AET_EXPIRED", customerId);
                        MessageProvider.getInstance().hideMessage("AET_ABOUT_TO_EXPIRE", customerId);
                        MDMMailNotificationHandler.getInstance().sendAETExpiredMail(prop);
                    }
                }
            }
        }
    }
    
    private void validateCertExpiry(final Long customerId, final boolean sendMail) throws Exception {
        final Properties windowsAETProp = this.getWpAETDetails(customerId);
        if (windowsAETProp != null && windowsAETProp.getProperty("CERT_FILE_PATH") != null) {
            final Long expiryDate = ((Hashtable<K, Long>)windowsAETProp).get("CERT_EXPIRE_TIME");
            final String emailAddress = ((Hashtable<K, String>)windowsAETProp).get("EMAIL_ADDRESS");
            Hashtable ht = new Hashtable();
            ht = DateTimeUtil.determine_From_To_Times("today");
            final Long today = ht.get("date1");
            boolean isExpiry = false;
            if (today > expiryDate) {
                isExpiry = true;
            }
            else {
                MessageProvider.getInstance().hideMessage("CERT_ABOUT_TO_EXPIRE", customerId);
                MessageProvider.getInstance().hideMessage("CERT_EXPIRED", customerId);
            }
            final Long alertDate = expiryDate - 7776000000L;
            if (today > alertDate) {
                isExpiry = true;
                if (sendMail && !MDMStringUtils.isEmpty(emailAddress)) {
                    final Long remaingDay = this.getAETExpiryPendingDays(expiryDate);
                    final Properties prop = new Properties();
                    ((Hashtable<String, String>)prop).put("$cert_expiry_date$", MDMUtil.getDate((long)expiryDate));
                    ((Hashtable<String, String>)prop).put("$cert_user_emailid$", emailAddress);
                    ((Hashtable<String, Long>)prop).put("$remaingDay$", remaingDay);
                    if (ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount() > 0) {
                        if (remaingDay > 0L) {
                            MessageProvider.getInstance().unhideMessage("CERT_ABOUT_TO_EXPIRE", customerId);
                            MessageProvider.getInstance().hideMessage("CERT_EXPIRED", customerId);
                            MDMMailNotificationHandler.getInstance().sendCertAboutToExpireMail(prop);
                        }
                        else {
                            MessageProvider.getInstance().unhideMessage("CERT_EXPIRED", customerId);
                            MessageProvider.getInstance().hideMessage("CERT_ABOUT_TO_EXPIRE", customerId);
                            MDMMailNotificationHandler.getInstance().sendCertExpiredMail(prop);
                        }
                    }
                    else {
                        if (remaingDay > 0L) {
                            MDMMailNotificationHandler.getInstance().sendCertAboutToExpireMail(prop);
                        }
                        else {
                            MDMMailNotificationHandler.getInstance().sendCertExpiredMail(prop);
                        }
                        MessageProvider.getInstance().hideMessage("CERT_ABOUT_TO_EXPIRE", customerId);
                        MessageProvider.getInstance().hideMessage("CERT_EXPIRED", customerId);
                    }
                }
            }
        }
    }
    
    public Long getAETExpiryPendingDays(final Long expiryDate) {
        Long days = 0L;
        try {
            Hashtable ht = new Hashtable();
            ht = DateTimeUtil.determine_From_To_Times("today");
            final Long today = ht.get("date1");
            final Long diff = expiryDate - today;
            days = diff / 86400000L;
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return days;
    }
    
    public JSONObject handleCodeSigning(final JSONObject requestJSON, JSONObject wpAETJson) {
        try {
            final String localTempFile = requestJSON.optString("CERT_FILE_UPLOAD", (String)null);
            final String expEmail = requestJSON.optString("EMAIL_ADDRESS", "");
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            Properties windowsCertProp = null;
            if (localTempFile != null) {
                windowsCertProp = this.parseCert(localTempFile, customerId, expEmail);
            }
            else {
                windowsCertProp = getInstance().getWpAETDetails(customerId);
                ((Hashtable<String, String>)windowsCertProp).put("EMAIL_ADDRESS", expEmail);
            }
            if (windowsCertProp.get("CERT_EXPIRE_TIME") != null) {
                if (wpAETJson == null) {
                    wpAETJson = new JSONObject();
                }
                final Long expiryTime = ((Hashtable<K, Long>)windowsCertProp).get("CERT_EXPIRE_TIME");
                final String certExpireTimeStr = Utils.getEventTime(expiryTime);
                wpAETJson.put("CERT_EXPIRE_TIME", (Object)certExpireTimeStr);
                wpAETJson.put("CERT_SUBJECT", ((Hashtable<K, Object>)windowsCertProp).get("CERT_SUBJECT"));
                wpAETJson.put("EMAIL_ADDRESS", (Object)expEmail);
                if (expiryTime > MDMUtil.getCurrentTimeInMillis()) {
                    final DataObject dObj = this.addorUpdateWPCertDetails(windowsCertProp, customerId);
                    final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
                    final String sUserName = hash.get("UserName");
                    String sEventLogRemarks = "";
                    final Object remarksArgs = null;
                    if (!dObj.isEmpty()) {
                        this.applnCodeSigningUpdate(customerId);
                    }
                    else {
                        sEventLogRemarks = "dc.mdm.actionlog.settings.CSC_failure";
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2035, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
                    }
                }
                else {
                    final String date = SyMUtil.getDate((long)expiryTime);
                    if (wpAETJson == null) {
                        wpAETJson = new JSONObject();
                    }
                    wpAETJson.put("errorMessage", (Object)"Certificate has already expired");
                    wpAETJson.put("errorKey", (Object)"mdm.upload.cert_expired");
                    wpAETJson.put("errorKeyParams", (Object)date);
                }
            }
            MessageProvider.getInstance().hideMessage("WIN_APP_MGMT_NOT_CONFIGURED", customerId);
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.WARNING, " Exception in handleAET ", ex);
        }
        return wpAETJson;
    }
    
    public JSONObject handleAET(final JSONObject requestJSON) {
        JSONObject wpAETJson = null;
        String localTempFile = null;
        try {
            localTempFile = requestJSON.optString("AET_FILE_UPLOAD", (String)null);
            final String aetEmail = (String)requestJSON.get("EMAIL_ADDRESS");
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            Properties windowsAETProp = null;
            if (localTempFile != null) {
                windowsAETProp = this.parseAET(localTempFile, customerId, aetEmail);
                windowsAETProp = this.fetchWPAETProp(windowsAETProp);
            }
            else {
                windowsAETProp = getInstance().getWpAETDetails(customerId);
                ((Hashtable<String, String>)windowsAETProp).put("EMAIL_ADDRESS", aetEmail);
            }
            if (windowsAETProp.get("AET_FILE_PATH") != null) {
                final Long expiryTime = ((Hashtable<K, Long>)windowsAETProp).get("EXPIRE_TIME");
                if (expiryTime > MDMUtil.getCurrentTimeInMillis()) {
                    final DataObject dObj = this.addorUpdateWPAETDetails(windowsAETProp, customerId);
                    final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
                    final String sUserName = hash.get("UserName");
                    String sEventLogRemarks = "";
                    final Object remarksArgs = null;
                    if (!dObj.isEmpty()) {
                        final Row wpCodeSignRow = dObj.getFirstRow("WpAppSettings");
                        wpAETJson = new JSONObject();
                        wpAETJson.put("ENTERPRISE_ID", wpCodeSignRow.get("ENTERPRISE_ID"));
                        wpAETJson.put("CERT_FILE_PATH", ((Hashtable<K, Object>)windowsAETProp).get("CERT_FILE_PATH"));
                        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy hh:mm aaa");
                        final String creationTimeStr = dateFormat.format(wpCodeSignRow.get("CREATION_TIME"));
                        final String expireTimeStr = dateFormat.format(wpCodeSignRow.get("EXPIRE_TIME"));
                        wpAETJson.put("CREATION_TIME", (Object)creationTimeStr);
                        wpAETJson.put("EXPIRE_TIME", (Object)expireTimeStr);
                        wpAETJson.put("EMAIL_ADDRESS", wpCodeSignRow.get("EMAIL_ADDRESS"));
                        final Boolean isExpired = (Boolean)wpCodeSignRow.get("IS_EXPIRED");
                        if (isExpired != null && !isExpired) {
                            this.applnEnrollTokenUpdate(customerId);
                        }
                    }
                    else {
                        sEventLogRemarks = "dc.mdm.actionlog.settings.AET_failure";
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2035, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
                    }
                }
                else {
                    final String date = SyMUtil.getDate((long)expiryTime);
                    wpAETJson = new JSONObject();
                    wpAETJson.put("errorMessage", (Object)"AET has already expired");
                    wpAETJson.put("errorKey", (Object)"dc.mdm.aet_expired");
                    wpAETJson.put("errorKeyParams", (Object)date);
                }
            }
        }
        catch (final Exception ex) {
            WpAppSettingsHandler.logger.log(Level.WARNING, " Exception in handleAET ", ex);
        }
        finally {
            if (localTempFile != null) {
                FileFacade.getInstance().deleteFile(localTempFile);
            }
        }
        return wpAETJson;
    }
    
    public String getAETFolderPath(final Long customerId) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String aetFolder = webappsDir + File.separator + "mdm" + File.separator + "apprepository" + File.separator + customerId + File.separator + "AET";
        return aetFolder;
    }
    
    public String getCertFolderPath(final Long customerId) throws Exception {
        final String certFolder = ".." + File.separator + "mdm" + File.separator + "codesigningcertificates" + File.separator + customerId;
        return certFolder;
    }
    
    public String getAETFolderDBPath(final Long customerId) throws Exception {
        final String appRepositoryFolder = File.separator + "MDM" + File.separator + "apprepository" + File.separator + customerId + File.separator + "AET";
        return appRepositoryFolder;
    }
    
    public String getCertFolderDBPath(final Long customerId) throws Exception {
        final String appRepositoryFolder = ".." + File.separator + "mdm" + File.separator + "codesigningcertificates" + File.separator + customerId;
        return appRepositoryFolder;
    }
    
    public JSONObject createTemplateForConfigValues(final JSONArray params, final Long customerID) throws Exception {
        final JSONObject template = new JSONObject();
        final JSONArray configJSONArray = new JSONArray();
        final String maxLenght = CustomerParamsHandler.getInstance().getParameterValue("CONFIG_MAX_LENGTH", (long)customerID);
        int maxConfigParam = 0;
        if (maxLenght != null) {
            maxConfigParam = Integer.parseInt(maxLenght);
        }
        if (params.length() > maxConfigParam) {
            CustomerParamsHandler.getInstance().addOrUpdateParameter("CONFIG_MAX_LENGTH", params.length() + "", (long)customerID);
        }
        for (int i = 0; i < params.length(); ++i) {
            final JSONObject configJsonTemp = new JSONObject();
            final JSONObject curPair = params.getJSONObject(i);
            final String key = String.valueOf(curPair.get("key"));
            configJsonTemp.put("key", (Object)key);
            configJsonTemp.put("title", (Object)key);
            final Object value = curPair.get("value");
            final JSONObject defaultJson = new JSONObject();
            if (value instanceof String) {
                try {
                    new JSONObject((String)value);
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("JSON_CONFIGURED", "true", (long)customerID);
                }
                catch (final JSONException e) {
                    WpAppSettingsHandler.logger.log(Level.FINE, "not JSON");
                }
                catch (final Exception e2) {
                    WpAppSettingsHandler.logger.log(Level.FINE, "Exception in updating json configured");
                }
                configJsonTemp.put("restrictionType", (Object)"string");
                defaultJson.put("type", (Object)"string");
                defaultJson.put("valueString", value);
            }
            else if (value instanceof Boolean) {
                if (value.toString().equalsIgnoreCase("false") || value.toString().equalsIgnoreCase("true")) {
                    configJsonTemp.put("restrictionType", (Object)"bool");
                    defaultJson.put("type", (Object)"bool");
                    defaultJson.put("valueBool", value);
                }
                else {
                    configJsonTemp.put("restrictionType", (Object)"integer");
                    defaultJson.put("type", (Object)"integer");
                    defaultJson.put("valueInteger", value);
                }
            }
            else {
                configJsonTemp.put("restrictionType", (Object)value.getClass().getName());
                defaultJson.put("type", (Object)value.getClass().getName());
                defaultJson.put("valueOther", value);
            }
            configJsonTemp.put("defaultValue", (Object)defaultJson);
            configJSONArray.put((Object)configJsonTemp);
        }
        template.put("APP_CONFIG_FORM", (Object)new JSONObject().put("restrictions", (Object)configJSONArray));
        return template;
    }
    
    public JSONObject createTemplateForConfigValues(final JSONArray params) throws Exception {
        return this.createTemplateForConfigValues(params, CustomerInfoUtil.getInstance().getCustomerId());
    }
    
    public JSONObject addBusinessStoreDetails(final JSONObject params) throws Exception {
        final Long userID = params.getLong("USER_ID");
        final Long customerID = params.getLong("CustomerID");
        final String code = String.valueOf(params.get("Code"));
        final JSONObject appDetails = MDMApiFactoryProvider.getBusinessStoreAccess().getSaaSAppDetails(customerID, userID);
        appDetails.put("Code", (Object)code);
        final BusinessStoreSetupAPI businessStoreSetupAPI = new BusinessStoreSetupAPI();
        JSONObject tenantDetails = new JSONObject();
        try {
            tenantDetails = businessStoreSetupAPI.getBasicTenantInfo(appDetails);
            tenantDetails.put("CustomerID", (Object)customerID);
            this.addOrUpdateWindowsBusinessStoreDetails(tenantDetails);
            MessageProvider.getInstance().hideMessage("BUSINESS_STORE_NOT_CONFIGURED", customerID);
            MessageProvider.getInstance().hideMessage("WIN_APP_MGMT_NOT_CONFIGURED", customerID);
            MessageProvider.getInstance().hideMessage("BUSINESS_STORE_PROMO", customerID);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72506, null, MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo().get("UserName"), "dc.mdm.actionlog.bstore.add", tenantDetails.opt("displayName"), customerID);
        }
        catch (final Exception e) {
            final String eMsg = e.toString();
            if (!SyMUtil.isStringEmpty(eMsg) && eMsg.contains("AD021")) {
                throw new APIHTTPException("AD021", new Object[0]);
            }
            tenantDetails.put("Error", true);
            tenantDetails.put("Message", (Object)"Code Invalid");
            WpAppSettingsHandler.logger.log(Level.WARNING, "Error in Adding Business Store Details", e);
        }
        return tenantDetails;
    }
    
    public void addOrUpdateWindowsBusinessStoreDetails(final JSONObject params) throws JSONException, DataAccessException, SyMException {
        final Long customerID = params.getLong("CustomerID");
        final String tenantID = String.valueOf(params.get("TenantID"));
        final String displayName = String.valueOf(params.get("displayName"));
        final String domainName = String.valueOf(params.get("DomainName"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WpAppSettings"));
        final Criteria customerCriteria = new Criteria(new Column("WpAppSettings", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.addSelectColumn(new Column("WpAppSettings", "*"));
        selectQuery.setCriteria(customerCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("WpAppSettings");
            row.set("TENANT_ID", (Object)tenantID);
            row.set("DOMAIN_NAME", (Object)domainName);
            row.set("DISPLAY_NAME", (Object)displayName);
            dataObject.updateRow(row);
        }
        else {
            final Row row = new Row("WpAppSettings");
            row.set("TENANT_ID", (Object)tenantID);
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("DOMAIN_NAME", (Object)domainName);
            row.set("DISPLAY_NAME", (Object)displayName);
            dataObject.addRow(row);
        }
        MDMUtil.getPersistence().update(dataObject);
        new WindowsStoreHandler(null, customerID).addOrUpdateManagedStore(displayName, MDMUtil.getInstance().getLoggedInUserID());
    }
    
    public boolean isBstoreConfigured(final Long customerID) throws Exception {
        boolean isBstoreConfigured = false;
        final JSONObject jsonObject = new JSONObject();
        this.putBstoreData(jsonObject, customerID);
        if (jsonObject.has("domain_name")) {
            isBstoreConfigured = true;
        }
        return isBstoreConfigured;
    }
    
    public boolean isBstoreOrCSCConfigured(final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WpAppSettings"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "DOMAIN_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CERT_FILE_PATH"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("WpAppSettings");
            return row.get("CERT_FILE_PATH") != null || row.get("DOMAIN_NAME") != null;
        }
        return false;
    }
    
    public void putBstoreData(final JSONObject response, final Long customerID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WpAppSettings"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "DOMAIN_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "DISPLAY_NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("WpAppSettings");
            response.put("domain_name", row.get("DOMAIN_NAME"));
            response.put("organisation", row.get("DISPLAY_NAME"));
        }
        final String lastSync = CustomerParamsHandler.getInstance().getParameterValue("BSTORE_LAST_SYNC", (long)customerID);
        if (lastSync != null) {
            final JSONObject bstoreCount = this.getBstoreAppsCount(customerID);
            response.put("last_sync", (Object)lastSync);
            response.put("next_sync", (Object)CustomerParamsHandler.getInstance().getParameterValue("BSTORE_NEXT_SYNC", (long)customerID));
            response.put("offline_apps", bstoreCount.get("offline_apps"));
            response.put("online_apps", bstoreCount.get("online_apps"));
        }
    }
    
    public void putStoreDetails(final JSONObject response, final Long customerID, final Long userID) throws Exception {
        response.put("TenantID", DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerID, "TENANT_ID"));
        final JSONObject saasAppDetails = MDMApiFactoryProvider.getBusinessStoreAccess().getSaaSAppDetails(customerID, userID);
        response.put("ClientID", saasAppDetails.get("ClientID"));
        response.put("ClientSecret", saasAppDetails.get("ClientSecret"));
    }
    
    public JSONObject getAppInstalltionURL(final JSONObject request, final Long customerID) throws Exception {
        final Long appID = request.getLong("appID");
        final JSONObject response = new JSONObject();
        boolean success = true;
        String remarks = "";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppData"));
        selectQuery.addJoin(new Join("MdPackageToAppData", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appID, 0));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
        selectQuery.addSelectColumn(new Column("MdPackageToAppData", "CUSTOMIZED_APP_URL"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdPackageToAppData");
            String url = (String)row.get("APP_FILE_LOC");
            final String customizedAppURL = String.valueOf(row.get("CUSTOMIZED_APP_URL"));
            if (url == null || url.toLowerCase().contains("not available")) {
                final Row windowsRow = dataObject.getFirstRow("WindowsAppDetails");
                final JSONObject packageDetails = this.getStorePackageID(windowsRow);
                final JSONObject BstoreDetails = EnterpriseBusinessStoreDBUtils.getInstance().getBusinessStoreDetails(3, null, customerID, null);
                final String tenantID = (String)BstoreDetails.opt("TenantID");
                if (!MDMUtil.getInstance().isEmpty(tenantID)) {
                    final BusinessStoreAPIAccess businessStoreAPIAccess = new BusinessStoreAPIAccess();
                    businessStoreAPIAccess.initialise(BstoreDetails);
                    packageDetails.put("Type", (Object)"SpecificPackageDetails");
                    final JSONObject appDetails = businessStoreAPIAccess.getDataFromBusinessStore(packageDetails);
                    final JSONObject location = appDetails.optJSONObject("location");
                    if (location != null) {
                        url = String.valueOf(location.get("url"));
                        final String licenseID = (String)windowsRow.get("LICENSE_ID");
                        final String licenseBlob = (String)windowsRow.get("LICENSE_CONTENT");
                        if (licenseID != null && !licenseID.equals("")) {
                            response.put("LicenseID", (Object)licenseID);
                            response.put("LicenseBlob", (Object)licenseBlob);
                        }
                        final String arch = request.optString("PROCESSOR_ARCHITECTURE");
                        final Integer model = request.optInt("MODEL_TYPE", -1);
                        if (!MDMStringUtils.isEmpty(arch) && model != -1 && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotDistributeDependencies")) {
                            response.put("dependency", (Object)getInstance().getRequiredDependencyURLsForArchitecture(appDetails, arch, model, Boolean.TRUE));
                        }
                    }
                    else {
                        success = false;
                        url = "";
                        remarks = "mdm.windows.app.mdm_app_deactivated";
                    }
                }
                else {
                    success = false;
                    url = "";
                    remarks = "--";
                }
            }
            else {
                final HashMap hm = new HashMap();
                if (url.toLowerCase().endsWith(".msi")) {
                    response.put("isMSI", (Object)Boolean.TRUE);
                }
                hm.put("path", url);
                hm.put("IS_SERVER", false);
                hm.put("IS_AUTHTOKEN", true);
                hm.put("isApi", false);
                url = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthTokenAndUDID(hm);
                url = MDMAppMgmtHandler.getDynamicServerBaseURL() + url.replace('\\', '/');
                url = url.replaceAll("\\\\", "/");
            }
            url = StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml(url));
            response.put("success", success);
            response.put("url", (Object)url);
            response.put("CUSTOMIZED_APP_URL", (Object)customizedAppURL);
            response.put("remarks", (Object)remarks);
        }
        return response;
    }
    
    private JSONObject getStorePackageID(final Row row) throws Exception {
        final JSONObject appData = new JSONObject();
        appData.put("PackageFullName", row.get("PACKAGE_ID"));
        appData.put("PackageID", row.get("PRODUCT_ID"));
        appData.put("SKUID", row.get("SKU_ID"));
        return appData;
    }
    
    private static HashMap<String, HashMap<String, Criteria>> generateCompatibilityCriteria() {
        final HashMap<String, Criteria> modelHashMap = new HashMap<String, Criteria>();
        final Criteria smartPhoneCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"), (Object)new Integer[] { 8, 24 }, 8);
        final Criteria LaptopCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"), (Object)new Integer[] { 16, 24 }, 8);
        modelHashMap.put("SmartPhone", smartPhoneCriteria);
        modelHashMap.put("Laptop", LaptopCriteria);
        final HashMap<String, Criteria> architecture = new HashMap<String, Criteria>();
        final Criteria x86Criteria = new Criteria(Column.getColumn("MdPackageToAppData", "SUPPORTED_ARCH"), (Object)new Long[] { MDMCommonConstants.X86_ARCHITECTURE, MDMCommonConstants.ALL_ARCHITECTURE, MDMCommonConstants.X86_ARCHITECTURE + MDMCommonConstants.ARM_ARCHITECTURE, MDMCommonConstants.X86_ARCHITECTURE + MDMCommonConstants.X64_ARCHITECTURE, MDMCommonConstants.NEUTRAL_ARCHITECTURE }, 8);
        final Criteria x64Criteria = new Criteria(Column.getColumn("MdPackageToAppData", "SUPPORTED_ARCH"), (Object)new Long[] { MDMCommonConstants.X86_ARCHITECTURE, MDMCommonConstants.X64_ARCHITECTURE, MDMCommonConstants.ALL_ARCHITECTURE, MDMCommonConstants.X64_ARCHITECTURE + MDMCommonConstants.ARM_ARCHITECTURE, MDMCommonConstants.X86_ARCHITECTURE + MDMCommonConstants.X64_ARCHITECTURE, MDMCommonConstants.X86_ARCHITECTURE + MDMCommonConstants.ARM_ARCHITECTURE, MDMCommonConstants.NEUTRAL_ARCHITECTURE }, 8);
        final Criteria ARMCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "SUPPORTED_ARCH"), (Object)new Long[] { MDMCommonConstants.ARM_ARCHITECTURE, MDMCommonConstants.NEUTRAL_ARCHITECTURE, MDMCommonConstants.ALL_ARCHITECTURE, MDMCommonConstants.X64_ARCHITECTURE + MDMCommonConstants.ARM_ARCHITECTURE, MDMCommonConstants.X86_ARCHITECTURE + MDMCommonConstants.ARM_ARCHITECTURE }, 8);
        architecture.put("X86", x86Criteria);
        architecture.put("X64", x64Criteria);
        architecture.put("ARM", ARMCriteria);
        final HashMap<String, Criteria> minOS = new HashMap<String, Criteria>();
        final Criteria win10MinOs = new Criteria(Column.getColumn("MdPackageToAppData", "MIN_OS"), (Object)"10.", 10).or(new Criteria(Column.getColumn("MdPackageToAppData", "MIN_OS"), (Object)"8.", 10).or(new Criteria(Column.getColumn("MdPackageToAppData", "MIN_OS"), (Object)"6.", 10)));
        final Criteria win81MinOs = new Criteria(Column.getColumn("MdPackageToAppData", "MIN_OS"), (Object)"8.", 10).or(new Criteria(Column.getColumn("MdPackageToAppData", "MIN_OS"), (Object)"6.", 10));
        minOS.put("Win10", win10MinOs);
        minOS.put("Win81", win81MinOs);
        final HashMap<String, HashMap<String, Criteria>> criteriaMap = new HashMap<String, HashMap<String, Criteria>>();
        criteriaMap.put("Model", modelHashMap);
        criteriaMap.put("Architecture", architecture);
        criteriaMap.put("MinOS", minOS);
        return criteriaMap;
    }
    
    public Criteria getCriteriaForResource(final HashMap deviceMap) {
        final int modelType = deviceMap.get("MODEL_TYPE");
        final String devArch = deviceMap.get("PROCESSOR_ARCHITECTURE");
        final String osVersion = deviceMap.get("OS_VERSION");
        Criteria combinedCriteria;
        if (ManagedDeviceHandler.getInstance().isWindowsDesktopOSDevice(modelType)) {
            combinedCriteria = (Criteria)WpAppSettingsHandler.compatibilityCriteriaMap.get("Model").get("Laptop");
        }
        else {
            combinedCriteria = (Criteria)WpAppSettingsHandler.compatibilityCriteriaMap.get("Model").get("SmartPhone");
        }
        combinedCriteria = combinedCriteria.and((Criteria)WpAppSettingsHandler.compatibilityCriteriaMap.get("Architecture").get(devArch));
        if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
            combinedCriteria = combinedCriteria.and((Criteria)WpAppSettingsHandler.compatibilityCriteriaMap.get("MinOS").get("Win10"));
        }
        else {
            combinedCriteria = combinedCriteria.and((Criteria)WpAppSettingsHandler.compatibilityCriteriaMap.get("MinOS").get("Win81"));
        }
        return combinedCriteria;
    }
    
    public void syncBStoreAppsToRepository(final Long customerID) throws Exception {
        final JSONObject queueData = new JSONObject();
        queueData.put("PlatformType", 3);
        queueData.put("CustomerID", (Object)customerID);
        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        if (userId != null) {
            queueData.put("userID", (Object)userId);
        }
        else {
            queueData.put("userID", (Object)this.getBstoreAssociatedUser(customerID));
        }
        final CommonQueueData syncAppsData = new CommonQueueData();
        syncAppsData.setCustomerId(customerID);
        syncAppsData.setTaskName("SyncAppsTask");
        syncAppsData.setClassName("com.me.mdm.server.apps.businessstore.SyncAppsTask");
        syncAppsData.setJsonQueueData(queueData);
        WpAppSettingsHandler.logger.log(Level.INFO, "Starting Task to Sync Windows Business Store Apps for customer : {0}", customerID);
        CommonQueueUtil.getInstance().addToQueue(syncAppsData, CommonQueues.MDM_APP_MGMT);
    }
    
    public JSONObject getSyncStatus(final Long customerID) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        Criteria finalCriteria = null;
        for (final String paramName : WpAppSettingsHandler.bstoreParams) {
            final Criteria criteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)paramName, 0);
            if (finalCriteria == null) {
                finalCriteria = criteria;
            }
            else {
                finalCriteria = finalCriteria.or(criteria);
            }
        }
        final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0);
        finalCriteria = finalCriteria.and(customerCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerParams"));
        selectQuery.addSelectColumn(Column.getColumn("CustomerParams", "*"));
        selectQuery.setCriteria(finalCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            for (final String paramName2 : WpAppSettingsHandler.bstoreParams) {
                final Criteria criteria2 = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)paramName2, 0);
                final Row row = dataObject.getRow("CustomerParams", criteria2);
                if (row != null) {
                    jsonObject.put(paramName2, row.get("PARAM_VALUE"));
                }
            }
        }
        return jsonObject;
    }
    
    public void clearBstoreDetails(final Long customerID) throws Exception {
        final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
        jsonObject.put((Object)"BstoreSyncStatus", (Object)"No status");
        CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObject, (long)customerID);
    }
    
    public JSONObject removeBstoreAccount(final Long customerID) throws Exception {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WpAppSettings"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "DOMAIN_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("WpAppSettings", "TENANT_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("WpAppSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("WpAppSettings");
            response.put("TENANT_ID", row.get("TENANT_ID"));
            response.put("DISPLAY_NAME", row.get("DISPLAY_NAME"));
            response.put("DOMAIN_NAME", row.get("DOMAIN_NAME"));
            row.set("TENANT_ID", (Object)null);
            row.set("DISPLAY_NAME", (Object)null);
            row.set("DOMAIN_NAME", (Object)null);
            dataObject.updateRow(row);
            MDMUtil.getPersistence().update(dataObject);
        }
        final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("MDMResource");
        final Join mdmResJoin = new Join("MDMResource", "ManagedBusinessStore", new String[] { "RESOURCE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2);
        final Join resJoin = new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Criteria customerIdCrietria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria typeCriteria = new Criteria(new Column("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)BusinessStoreSyncConstants.BS_SERVICE_WBS, 0);
        dQuery.addJoin(mdmResJoin);
        dQuery.addJoin(resJoin);
        dQuery.setCriteria(customerIdCrietria.and(typeCriteria));
        MDMUtil.getPersistence().delete(dQuery);
        this.deleteCustomerParamsDataForBStore(customerID);
        if (ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount() > 0) {
            MessageProvider.getInstance().unhideMessage("BUSINESS_STORE_PROMO", customerID);
            if (getInstance().isBstoreConfigured(customerID)) {
                MessageProvider.getInstance().unhideMessage("WIN_APP_MGMT_NOT_CONFIGURED", customerID);
            }
        }
        new AppTrashModeHandler().moveAppsToTrash(this.getBStoreAppGroupIds(customerID), customerID);
        return response;
    }
    
    private void deleteCustomerParamsDataForBStore(final Long customerID) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("CustomerParams");
        final Criteria criteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)new String[] { "BSTORE_LAST_SYNC", "BSTORE_NEXT_SYNC", "BstoreSyncStatus", "FailureBStoreApps", "SuccuessBStoreApps", "TotalBStoreApps" }, 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0);
        deleteQuery.setCriteria(criteria.and(customerCriteria));
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    public JSONObject getBstoreAppsCount(final Long customerID) throws DataAccessException, JSONException, SQLException, QueryConstructionException {
        final JSONObject jsonObject = new JSONObject();
        final int offlineCount = 0;
        final int onlineCount = 0;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WindowsAppDetails"));
        selectQuery.addJoin(new Join("WindowsAppDetails", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("WindowsAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0)).and(new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0)));
        final Column countColumn = Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID").distinct().count();
        countColumn.setColumnAlias("APPS_COUNT");
        selectQuery.addSelectColumn(countColumn);
        final Column offlineColumn = Column.getColumn("WindowsAppDetails", "IS_OFFLINE_APP");
        selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "IS_OFFLINE_APP"));
        final List list = new ArrayList();
        list.add(offlineColumn);
        selectQuery.setGroupByClause(new GroupByClause(list));
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                Object cnt = dataSet.getValue("APPS_COUNT");
                if (cnt != null) {
                    cnt = Integer.parseInt(cnt.toString());
                }
                final Boolean type = (Boolean)dataSet.getValue("IS_OFFLINE_APP");
                if (type) {
                    jsonObject.put("offline_apps", cnt);
                }
                else {
                    jsonObject.put("online_apps", cnt);
                }
            }
        }
        catch (final Exception e) {
            WpAppSettingsHandler.logger.log(Level.WARNING, "error getBstoreAppsCount()   ", e);
        }
        if (!jsonObject.has("offline_apps")) {
            jsonObject.put("offline_apps", offlineCount);
        }
        if (!jsonObject.has("online_apps")) {
            jsonObject.put("online_apps", onlineCount);
        }
        return jsonObject;
    }
    
    public String replacePayloadContentWithLicense(String payload, final String licenseID, final String licenseBlob) {
        if (licenseID != null && !licenseID.equals("") && licenseBlob != null && !licenseBlob.equals("")) {
            payload = payload.replaceAll("%LicenseID%", licenseID);
            payload = payload.replaceAll("%LicenseBlob%", licenseBlob);
        }
        else {
            payload = payload.replaceAll("<Item><Target><LocURI>./(Device|User)/Vendor/MSFT/EnterpriseModernAppManagement/AppLicenses/StoreLicenses/%LicenseID%/AddLicense</LocURI></Target><Data>&lt;License Content=\"%LicenseBlob%\"&gt;&lt;/License&gt;</Data><Meta><Format>xml</Format></Meta></Item>", "");
            payload = payload.replaceAll("<Item><Target><LocURI>./(Device|User)/Vendor/MSFT/EnterpriseModernAppManagement/AppLicenses/StoreLicenses/%LicenseID%/AddLicense</LocURI></Target><Data>&lt;License Content=\"%LicenseBlob%\"/&gt;</Data><Meta><Format>xml</Format></Meta></Item>", "");
            payload = payload.replaceAll("<Item><Target><LocURI>./(Device|User)/Vendor/MSFT/EnterpriseModernAppManagement/AppLicenses/StoreLicenses/%LicenseID%/AddLicense</LocURI></Target><Data>&lt;License Content=\"%LicenseBlob%\"/></Data><Meta><Format>xml</Format></Meta></Item>", "");
        }
        return payload;
    }
    
    public List getBStoreAppGroupIds(final Long customerID) throws DataAccessException {
        final List appGroupIds = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
        final Criteria portalCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(portalCriteria.and(platformCriteria).and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdPackageToAppGroup");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long appgrpID = (Long)row.get("APP_GROUP_ID");
            if (!appGroupIds.contains(appgrpID)) {
                appGroupIds.add(appgrpID);
            }
        }
        return appGroupIds;
    }
    
    public Long getAppIDIfMSIApp(final Long collectionID) throws DataAccessException {
        Long appID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToCollection"));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Row row = dataObject.getFirstRow("MdPackageToAppData");
        if (((String)row.get("APP_FILE_LOC")).toLowerCase().endsWith(".msi")) {
            appID = (Long)row.get("APP_ID");
        }
        return appID;
    }
    
    Long getBstoreAssociatedUser(final Long customerID) throws DataAccessException {
        Long bstoreUserID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdPackage", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0)));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdPackage");
            bstoreUserID = (Long)row.get("PACKAGE_MODIFIED_BY");
        }
        return bstoreUserID;
    }
    
    public Boolean hasKioskProfileForPhones(final List resourceList, final List collnList, final Boolean isGroup) throws DataAccessException {
        Boolean hasKioskProfile = Boolean.FALSE;
        hasKioskProfile = (this.getResHavingKioskProfileForPhones(resourceList, collnList, isGroup).size() != 0);
        return hasKioskProfile;
    }
    
    public List getResHavingKioskProfileForPhones(List resourceList, final List collectionID, final Boolean isGroup) throws DataAccessException {
        final List resList = new ArrayList();
        if (isGroup) {
            resourceList = MDMGroupHandler.getMemberIdListForGroups(resourceList, 120);
        }
        if (resourceList != null) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ConfigData"));
            selectQuery.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("ConfigDataItem", "WindowsKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria collnCriteria = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID.toArray(), 8);
            final Criteria configCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)608, 0);
            selectQuery.setCriteria(collnCriteria.and(configCriteria));
            selectQuery.addSelectColumn(Column.getColumn("ConfigData", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final SelectQuery modelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
                modelQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                modelQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
                modelQuery.addSelectColumn(Column.getColumn("MdModelInfo", "*"));
                modelQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
                final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria Mobilecriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)1, 0);
                modelQuery.setCriteria(criteria.and(Mobilecriteria));
                final DataObject dataObject2 = MDMUtil.getPersistence().get(modelQuery);
                final Iterator iterator = dataObject2.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    resList.add(row.get("RESOURCE_ID"));
                }
            }
        }
        return resList;
    }
    
    public HashMap getAppsInstalledForResource(final Long resourceID) throws Exception {
        final HashMap hashMap = new HashMap();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            final List seenApps = new ArrayList();
            final List MSiApps = new ArrayList();
            final List storeApps = new ArrayList();
            final List nonStore = new ArrayList();
            while (ds.next()) {
                final String identifier = (String)ds.getValue("IDENTIFIER");
                final Boolean purchased = (Boolean)ds.getValue("IS_PURCHASED_FROM_PORTAL");
                final Boolean isModern = (Boolean)ds.getValue("IS_MODERN_APP");
                if (!seenApps.contains(identifier)) {
                    if (isModern) {
                        if (purchased) {
                            storeApps.add(identifier);
                        }
                        else {
                            nonStore.add(identifier);
                        }
                    }
                    else {
                        MSiApps.add(identifier);
                    }
                }
                seenApps.add(identifier);
            }
            hashMap.put("StoreApps", storeApps);
            hashMap.put("NonStoreApps", nonStore);
            hashMap.put("MSIApps", MSiApps);
        }
        catch (final Exception e) {
            WpAppSettingsHandler.logger.log(Level.WARNING, " error getAppsInstalledForResource() ", e);
        }
        return hashMap;
    }
    
    public Long getWindowsUWPAgentAppID(final Long customerID) throws DataAccessException {
        Long appID = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2", 0).and(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0))));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdAppToCollection");
            appID = (Long)row.get("APP_ID");
        }
        return appID;
    }
    
    public String getRequiredDependencyURLsForArchitecture(final JSONObject bstoreResponse, final String arch, final int deviceModel, final Boolean recurse) {
        try {
            final StringBuilder result = new StringBuilder();
            final String dependencyTemplate = "<Dependency PackageUri=\"%URL%\" />";
            final JSONArray dependencies = bstoreResponse.getJSONArray("frameworkDependencyPackages");
            String deviceModelString = (deviceModel == 3 || deviceModel == 3 || deviceModel == 4) ? "Windows.Desktop" : "Windows.Mobile";
            if (deviceModelString.equalsIgnoreCase("Windows.Desktop") && this.getPlatformofApp(bstoreResponse).equalsIgnoreCase("Windows.Windows8x")) {
                deviceModelString = "Windows.Windows8x";
            }
            for (int i = 0; i < dependencies.length(); ++i) {
                final JSONObject curDep = dependencies.getJSONObject(i);
                final String depPlatform = this.getPlatformofApp(curDep);
                if (depPlatform.equalsIgnoreCase(deviceModelString) || depPlatform.equalsIgnoreCase("Windows.Universal")) {
                    final List curArchs = this.getArchitectureofApp(curDep);
                    if (curArchs.contains(arch.toLowerCase()) || curArchs.contains("neutral") || (curArchs.contains("x86") && arch.equalsIgnoreCase("x64"))) {
                        result.append(dependencyTemplate.replaceAll("%URL%", StringEscapeUtils.escapeHtml(String.valueOf(curDep.getJSONObject("location").get("url")))));
                    }
                }
            }
            if (result.length() > 0) {
                return "<Dependencies>" + result.toString() + "</Dependencies>";
            }
            if (recurse && arch.equalsIgnoreCase("x64")) {
                return this.getRequiredDependencyURLsForArchitecture(bstoreResponse, "x86", deviceModel, Boolean.FALSE);
            }
        }
        catch (final Exception e) {
            WpAppSettingsHandler.logger.log(Level.WARNING, "couldnt add dependencies ", e);
        }
        return "";
    }
    
    private String getPlatformofApp(final JSONObject jsonObject) throws JSONException {
        return String.valueOf(jsonObject.getJSONArray("platforms").getJSONObject(0).get("platformName"));
    }
    
    private List getArchitectureofApp(final JSONObject jsonObject) throws JSONException {
        final JSONArray architectures = (JSONArray)jsonObject.get("architectures");
        final String[] temp = new String[architectures.length()];
        for (int i = 0; i < architectures.length(); ++i) {
            temp[i] = String.valueOf(architectures.get(i)).toLowerCase();
        }
        return Arrays.asList(temp);
    }
    
    static {
        WpAppSettingsHandler.compatibilityCriteriaMap = generateCompatibilityCriteria();
        WpAppSettingsHandler.logger = Logger.getLogger("MDMConfigLogger");
        WpAppSettingsHandler.wpAETHandler = null;
        WpAppSettingsHandler.bstoreParams = new ArrayList<String>() {
            {
                this.add("TotalBStoreApps");
                this.add("SuccuessBStoreApps");
                this.add("FailureBStoreApps");
                this.add("BSTORE_LAST_SYNC");
                this.add("BSTORE_NEXT_SYNC");
                this.add("BstoreSyncStatus");
            }
        };
    }
}
