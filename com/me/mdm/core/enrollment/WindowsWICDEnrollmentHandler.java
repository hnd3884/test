package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import java.util.List;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.core.xmlparser.XmlBeanUtil;
import com.me.mdm.core.windows.xmlbeans.WindowsProvisioningPackage;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;

public class WindowsWICDEnrollmentHandler extends AdminEnrollmentHandler
{
    public static String CREATE_PPKG_BAT_FILE_NAME;
    public static String CUSTOM_XML_FILE_NAME;
    public static String SERVER_CERT_FILE_NAME;
    public static String CURRENT_DIR_MACRO;
    public static String DOWNLOAD_ZIP_FILE_NAME;
    public static String FOLDER_PATH_IN_ZIP;
    
    public WindowsWICDEnrollmentHandler() {
        super(30, "WindowsICDDeviceForEnrollment", "WindowsICDEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateWindowsWICDEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    public static void deleteAdminEnrollmentTemplate(final Long loginID) throws DataAccessException {
        DataAccess.delete("WindowsICDEnrollmentTemplate", new Criteria(Column.getColumn("WindowsICDEnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0));
    }
    
    public JSONObject getWICDEnrollmentDetails(final Long userID, final Long customerID) throws Exception {
        final JSONObject wicdEnrollmentDetails = new JSONObject();
        try {
            final String httpsServerUrl = MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup();
            final String templateToken = new EnrollmentTemplateHandler().getTemplateTokenForUserId(userID, this.templateType, customerID);
            String discoveryServiceURL = httpsServerUrl + "/mdm/client/v1/wpdiscover/admin/" + customerID + "?templateToken=" + templateToken;
            final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
            if (generator != null) {
                final JSONObject json = new JSONObject();
                json.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(userID));
                json.put("TEMPLATE_TYPE", this.templateType);
                final APIKey key = generator.generateAPIKey(json);
                discoveryServiceURL = discoveryServiceURL + "&" + key.getAsURLParams();
            }
            wicdEnrollmentDetails.put("discoveryServiceUrl", (Object)discoveryServiceURL);
            wicdEnrollmentDetails.put("secret", (Object)templateToken);
            final Properties contactInfoProps = DMUserHandler.getContactInfoProp(userID);
            if (contactInfoProps.containsKey("EMAIL_ID") && !((Hashtable<K, String>)contactInfoProps).get("EMAIL_ID").trim().isEmpty()) {
                wicdEnrollmentDetails.put("technicianMailId", ((Hashtable<K, Object>)contactInfoProps).get("EMAIL_ID"));
            }
            final int certType = ApiFactoryProvider.getServerSettingsAPI().getCertificateType();
            wicdEnrollmentDetails.put("isThirdPartyCertificate", certType == 2);
            if (certType != 2) {
                wicdEnrollmentDetails.put("certificateName", (Object)SSLCertificateUtil.getInstance().getSSLCertificateHostName());
                wicdEnrollmentDetails.put("certificatePath", (Object)(WindowsWICDEnrollmentHandler.CURRENT_DIR_MACRO + WindowsWICDEnrollmentHandler.SERVER_CERT_FILE_NAME));
            }
            wicdEnrollmentDetails.put("authPolicy", (Object)"OnPremise");
            final WindowsProvisioningPackage winProvisioningPackage = WindowsProvisioningPackage.getProvisioningPackageBean(wicdEnrollmentDetails);
            final JSONObject beanUtilJSON = new JSONObject();
            beanUtilJSON.put("BEAN_OBJECT", (Object)winProvisioningPackage);
            beanUtilJSON.put("jaxb.formatted.output", (Object)Boolean.TRUE);
            beanUtilJSON.put("jaxb.fragment", (Object)Boolean.TRUE);
            beanUtilJSON.put("jaxb.encoding", (Object)"UTF-8");
            final JSONObject customProps = new JSONObject();
            customProps.put("com.sun.xml.internal.bind.xmlHeaders", (Object)"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            beanUtilJSON.put("customMarshallerProps", (Object)customProps);
            final XmlBeanUtil<WindowsProvisioningPackage> xmlBeanUtil = new XmlBeanUtil<WindowsProvisioningPackage>(beanUtilJSON);
            final String customizationXml = xmlBeanUtil.beanToXmlString();
            wicdEnrollmentDetails.put("customizationXml", (Object)customizationXml);
        }
        catch (final Exception ex) {
            Logger.getLogger(AppleConfiguratorEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wicdEnrollmentDetails;
    }
    
    @Override
    public Properties removeUnwantedMessages(final Properties messageProps) {
        if (messageProps != null) {
            final Properties pageMsgProps = ((Hashtable<K, Properties>)messageProps).get("MDM_ENROLLMENT_LAYER_MSG");
            if (pageMsgProps != null && pageMsgProps.containsKey("MULTI_MESSAGE_LIST")) {
                final ArrayList<ArrayList<Properties>> multiMessageList = ((Hashtable<K, ArrayList<ArrayList<Properties>>>)pageMsgProps).get("MULTI_MESSAGE_LIST");
                final ArrayList returnMultiMessageList = new ArrayList();
                for (final ArrayList<Properties> singleMessageList : multiMessageList) {
                    final ArrayList returnSingleMessageList = new ArrayList();
                    for (final Properties singleMessageProp : singleMessageList) {
                        final String msgName = ((Hashtable<K, String>)singleMessageProp).get("MSG_NAME");
                        if (!msgName.contains("APNS_")) {
                            returnSingleMessageList.add(singleMessageProp);
                        }
                    }
                    if (returnSingleMessageList.size() > 0) {
                        returnMultiMessageList.add(returnSingleMessageList);
                    }
                }
                if (returnMultiMessageList.size() > 0) {
                    ((Hashtable<String, ArrayList>)pageMsgProps).put("MULTI_MESSAGE_LIST", returnMultiMessageList);
                    ((Hashtable<String, Properties>)messageProps).put("MDM_ENROLLMENT_LAYER_MSG", pageMsgProps);
                }
                else {
                    messageProps.remove("MDM_ENROLLMENT_LAYER_MSG");
                }
            }
        }
        return messageProps;
    }
    
    public static String createZip(final JSONObject jsonObject) throws Exception {
        final String directoryToZipPath = String.valueOf(jsonObject.get("directoryToZipPath"));
        final String pathToZipFile = String.valueOf(jsonObject.get("pathToZipFile"));
        final String folderPathInsideZipFile = jsonObject.optString("folderPathInsideZipFile", "");
        final FileOutputStream zipFOS = new FileOutputStream(pathToZipFile);
        final ZipOutputStream zipOutputStream = new ZipOutputStream(zipFOS);
        final File dir = new File(directoryToZipPath);
        try {
            createZipRecursively(dir.listFiles(), folderPathInsideZipFile, zipOutputStream);
        }
        finally {
            zipOutputStream.close();
            zipFOS.close();
        }
        return pathToZipFile;
    }
    
    public static ZipOutputStream createZipRecursively(final File[] filesToZip, final String zipPath, final ZipOutputStream zipOutputStream) throws IOException {
        for (final File file : filesToZip) {
            if (file.isDirectory()) {
                createZipRecursively(file.listFiles(), zipPath + File.separator + file.getName(), zipOutputStream);
            }
            else {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    zipOutputStream.putNextEntry(new ZipEntry(zipPath + File.separator + file.getName()));
                    final byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    zipOutputStream.closeEntry();
                }
                finally {
                    fileInputStream.close();
                }
            }
        }
        return zipOutputStream;
    }
    
    public Boolean checkRedownloadMessageForWindowsWICD(final Properties msgProperty) {
        Boolean redownloadMessage = Boolean.FALSE;
        if (msgProperty != null) {
            final Properties mdmWicdMsg = ((Hashtable<K, Properties>)msgProperty).get("MDM_WICD_MSG");
            if (mdmWicdMsg != null) {
                final List multiMessageList = ((Hashtable<K, ArrayList>)mdmWicdMsg).get("MULTI_MESSAGE_LIST");
                if (!multiMessageList.isEmpty()) {
                    final ArrayList singleMsgList = multiMessageList.get(0);
                    if (singleMsgList != null) {
                        final Properties singleMsgProps = singleMsgList.get(0);
                        if (singleMsgProps != null && singleMsgProps.containsKey("MSG_NAME") && ((Hashtable<K, Object>)singleMsgProps).get("MSG_NAME").toString().equalsIgnoreCase("DOWNLOAD_PPKG_TOOL")) {
                            redownloadMessage = Boolean.TRUE;
                        }
                    }
                }
            }
        }
        return redownloadMessage;
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        return true;
    }
    
    static {
        WindowsWICDEnrollmentHandler.CREATE_PPKG_BAT_FILE_NAME = "createPPKG.bat";
        WindowsWICDEnrollmentHandler.CUSTOM_XML_FILE_NAME = "customizations.xml";
        WindowsWICDEnrollmentHandler.SERVER_CERT_FILE_NAME = "mdmServer.cer";
        WindowsWICDEnrollmentHandler.CURRENT_DIR_MACRO = "$(CURRENT_DIR)";
        WindowsWICDEnrollmentHandler.DOWNLOAD_ZIP_FILE_NAME = "ManageEngine_WinAdminEnroll.zip";
        WindowsWICDEnrollmentHandler.FOLDER_PATH_IN_ZIP = "ManageEngine_WinAdminEnroll";
    }
}
