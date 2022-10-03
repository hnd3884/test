package com.me.devicemanagement.framework.server.alerts;

import java.io.IOException;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.mailmanager.EMailAddressHandler;
import org.json.JSONObject;
import java.io.File;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Hashtable;
import java.util.logging.Logger;

public class DCEMailAlertsHandler
{
    private static DCEMailAlertsHandler handler;
    private static final String MALERT_METADATA_HASH = "MALERT_METADATA_HASH";
    private static Logger logger;
    private static String sourceClass;
    
    private DCEMailAlertsHandler() {
    }
    
    public static DCEMailAlertsHandler getInstance() {
        if (DCEMailAlertsHandler.handler == null) {
            DCEMailAlertsHandler.handler = new DCEMailAlertsHandler();
        }
        final Hashtable mAlertMetaDataHash = (Hashtable)ApiFactoryProvider.getCacheAccessAPI().getCache("MALERT_METADATA_HASH", 2);
        if (mAlertMetaDataHash == null) {
            DCEMailAlertsHandler.handler.loadAlertMetaDataToInMemory();
        }
        return DCEMailAlertsHandler.handler;
    }
    
    public void loadAlertMetaDataToInMemory() {
        final String sourceMethod = "loadAlertMetaDataToInMemory";
        try {
            SyMLogger.info(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "Going to create the queues configured from DB.....");
            final String qmdTable = "DCEMailAlertMetaData";
            final DataObject qmdDO = SyMUtil.getPersistence().get(qmdTable, (Criteria)null);
            if (qmdDO.isEmpty()) {
                SyMLogger.info(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "No alert meta data is found in the table: " + qmdTable);
                return;
            }
            final Iterator qmdRows = qmdDO.getRows(qmdTable);
            while (qmdRows.hasNext()) {
                final Row qmdrow = qmdRows.next();
                try {
                    final DCEMailAlertMetaData alertMetaData = this.createAlertMetaData(qmdrow);
                    Hashtable mAlertMetaDataHash = (Hashtable)ApiFactoryProvider.getCacheAccessAPI().getCache("MALERT_METADATA_HASH", 2);
                    if (mAlertMetaDataHash == null) {
                        mAlertMetaDataHash = new Hashtable();
                    }
                    if (mAlertMetaDataHash.get(alertMetaData.alertName) == null) {
                        mAlertMetaDataHash.put(alertMetaData.alertName, alertMetaData);
                        ApiFactoryProvider.getCacheAccessAPI().putCache("MALERT_METADATA_HASH", mAlertMetaDataHash, 2);
                    }
                    else {
                        SyMLogger.warning(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, "DCEMailAlertMetaData", "MetaData for given name already exists." + alertMetaData);
                    }
                }
                catch (final Exception exp) {
                    SyMLogger.error(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "Exception occurred", exp);
                }
            }
        }
        catch (final Exception exp2) {
            SyMLogger.error(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "Exception occurred", exp2);
        }
    }
    
    private DCEMailAlertMetaData createAlertMetaData(final Row queueMetaDataRow) throws Exception {
        final DCEMailAlertMetaData metaData = new DCEMailAlertMetaData();
        metaData.alertName = (String)queueMetaDataRow.get("ALERT_NAME");
        metaData.alertDataHandlerName = (String)queueMetaDataRow.get("ALERT_DATA_CLASS_NAME");
        metaData.loggerName = (String)queueMetaDataRow.get("LOGGER_NAME");
        metaData.folderName = "webapps" + File.separator + "DesktopCentral" + File.separator + "patchmgmt";
        metaData.xslFileName = (String)queueMetaDataRow.get("ALERT_XSL_FILE_NAME");
        metaData.maxSize = (int)queueMetaDataRow.get("MAX_SIZE");
        return metaData;
    }
    
    public DCEMailAlertData createEMailAlertData(final String alertName, final String moduleName, final JSONObject jsonObj) throws Exception {
        final String sourceMethod = "createEMailAlertData";
        String xmlDataFileName = null;
        String xslFileName = null;
        String destinationFolder = null;
        DCEMailAlertMetaData mAlertMetaData = null;
        DCEMailAlertData mAlertData = null;
        DataObject dataObject = null;
        final Hashtable mAlertMetaDataHash = (Hashtable)ApiFactoryProvider.getCacheAccessAPI().getCache("MALERT_METADATA_HASH", 2);
        mAlertMetaData = mAlertMetaDataHash.get(alertName);
        mAlertData = (DCEMailAlertData)Class.forName(mAlertMetaData.alertDataHandlerName).newInstance();
        destinationFolder = SyMUtil.getInstallationDir() + File.separator + mAlertMetaData.folderName + File.separator + moduleName;
        if (jsonObj.has("TaskID")) {
            final String taskID = String.valueOf(jsonObj.get("TaskID"));
            destinationFolder = destinationFolder + File.separator + taskID;
            this.deleteTaskNotifyFiles(destinationFolder);
        }
        else if (jsonObj.has("collectionID")) {
            final String collectionID = String.valueOf(jsonObj.get("collectionID"));
            destinationFolder = SyMUtil.getInstallationDir() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + moduleName;
            destinationFolder = destinationFolder + File.separator + collectionID;
            this.deleteTaskNotifyFiles(destinationFolder);
        }
        ApiFactoryProvider.getFileAccessAPI().createDirectory(destinationFolder);
        SyMLogger.info(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, mAlertMetaData.toString());
        xmlDataFileName = destinationFolder + File.separator + alertName + ".xml";
        SyMLogger.info(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, xmlDataFileName);
        dataObject = mAlertData.createEmailAlertDO(jsonObj, dataObject);
        if (dataObject != null) {
            mAlertData.generateXMLDataFile(xmlDataFileName, dataObject);
        }
        mAlertData.htmlFile = destinationFolder + File.separator + alertName + ".html";
        xslFileName = SyMUtil.getInstallationDir() + File.separator + mAlertMetaData.xslFileName;
        final String i118nXSLFile = SyMUtil.createI18NxslFile(xslFileName, alertName + "-temp.xsl");
        if (!i118nXSLFile.equals("") && new File(xmlDataFileName).exists()) {
            DCXSLTTransformer.getInstance().generateHTMLFile(i118nXSLFile, xmlDataFileName, mAlertData.htmlFile);
        }
        else {
            SyMLogger.warning(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "HTML is not generated.  Mail Contents could not be generated.");
        }
        return mAlertData;
    }
    
    public void sendEMailAlerts(final String alertName, final String moduleName, final JSONObject jsonObj) throws Exception {
        final String sourceMethod = "sendEMailAlerts";
        MailDetails mDetails = null;
        DCEMailAlertData mAlertData = null;
        String[] atchFile = null;
        try {
            mAlertData = this.createEMailAlertData(alertName, moduleName, jsonObj);
            if (mAlertData.pdfFile != null) {
                atchFile = mAlertData.pdfFile.split(",");
            }
            if (mAlertData.htmlFile != null && !mAlertData.htmlFile.isEmpty() && ApiFactoryProvider.getFileAccessAPI().isFile(mAlertData.htmlFile)) {
                final byte[] fileData = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(mAlertData.htmlFile);
                final String mailContent = new String(fileData, "UTF-8");
                mDetails = EMailAddressHandler.getInstance().getMailAddressDetails(moduleName);
                mDetails.bodyContent = mailContent;
                mDetails.subject = (jsonObj.has("Subject") ? String.valueOf(jsonObj.get("Subject")) : mAlertData.mailSubject);
                mDetails.attachment = atchFile;
                final String email = String.valueOf(jsonObj.get("Email"));
                if (email != null && !email.isEmpty()) {
                    mDetails.toAddress = email;
                }
                ApiFactoryProvider.getMailSettingAPI().addToMailQueue(mDetails, 0);
            }
            else {
                SyMLogger.warning(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "Email Content is empty so, Not sending the mail alert : " + mAlertData.htmlFile);
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "Exception occurred during alert generation of : " + alertName, exp);
        }
    }
    
    public boolean deleteTaskNotifyFiles(final String destinationFolder) {
        final String sourceMethod = "deleteTaskNotifyFiles";
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(destinationFolder) && !ApiFactoryProvider.getFileAccessAPI().isDirectory(destinationFolder)) {
                return Boolean.FALSE;
            }
            return ApiFactoryProvider.getFileAccessAPI().deleteDirectory(destinationFolder);
        }
        catch (final IOException ex) {
            SyMLogger.error(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "File delete throws below IO Exception ::" + destinationFolder, ex);
            return Boolean.FALSE;
        }
        catch (final Exception ex2) {
            SyMLogger.error(DCEMailAlertsHandler.logger, DCEMailAlertsHandler.sourceClass, sourceMethod, "File delete throws below exception ::" + destinationFolder, ex2);
            return Boolean.FALSE;
        }
    }
    
    static {
        DCEMailAlertsHandler.handler = null;
        DCEMailAlertsHandler.logger = Logger.getLogger("emailalerts");
        DCEMailAlertsHandler.sourceClass = "DCEMailAlertsHandler";
    }
}
