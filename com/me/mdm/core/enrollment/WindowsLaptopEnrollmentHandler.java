package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import java.util.HashMap;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.FileWriter;
import com.me.mdm.certificate.CertificateHandler;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.Map;
import java.util.List;

public class WindowsLaptopEnrollmentHandler extends AdminEnrollmentHandler
{
    public static final List<String> BATCH_FILE_DYNAMIC_VARS;
    public static final Map<Integer, String> ERROR_CODE_TO_REMARKS;
    public static final String FOLDER_PATH_IN_ZIP = "ManageEngine_MDMLaptopEnrollment";
    public static final String DOWNLOAD_ZIP_FILE_NAME = "ManageEngine_MDMLaptopEnrollment.zip";
    public static final String BATCH_FILE_NAME = "enrollment.bat";
    public static final String SCRIPTS_FOLDER_NAME = "scripts";
    
    public WindowsLaptopEnrollmentHandler() {
        super(31, "WindowsLaptopDeviceForEnrollment", "WindowsLaptopEnrollmentTemplate");
    }
    
    @Override
    public void addorUpdateAdminEnrollmentTemplate(final JSONObject enrollmentTemplateJSON) throws Exception {
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        handler.addorUpdateWindowsLaptopEnrollmentTemplate(enrollmentTemplateJSON);
    }
    
    @Override
    public boolean isValidEnrollmentTemplate(final Long templateId) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("WindowsLaptopEnrollmentTemplate", "TEMPLATE_ID"), (Object)templateId, 0);
        final int recordCount = DBUtil.getRecordCount("WindowsLaptopEnrollmentTemplate", "LOGIN_ID", criteria);
        return recordCount > 0;
    }
    
    public static void deleteAdminEnrollmentTemplate(final Long loginID) throws DataAccessException {
        DataAccess.delete("WindowsLaptopEnrollmentTemplate", new Criteria(Column.getColumn("WindowsLaptopEnrollmentTemplate", "LOGIN_ID"), (Object)loginID, 0));
    }
    
    public JSONObject getLaptopEnrollmentDetails(final Long userID, final Long customerID) {
        final JSONObject laptopEnrollmentDetails = new JSONObject();
        try {
            final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            final String httpsServerUrl = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            laptopEnrollmentDetails.put("%HOST_URL%", (Object)httpsServerUrl);
            final String serverUrlforPort = MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup();
            String httpsPort = ((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT").toString();
            if (serverUrlforPort.split(":").length == 3) {
                httpsPort = serverUrlforPort.split(":")[serverUrlforPort.split(":").length - 1];
            }
            laptopEnrollmentDetails.put("%HTTPS_PORT%", (Object)httpsPort);
            laptopEnrollmentDetails.put("%CID%", (Object)String.valueOf(customerID));
            final String templateToken = new EnrollmentTemplateHandler().getTemplateTokenForUserId(userID, this.templateType, customerID);
            laptopEnrollmentDetails.put("%TEMPLATE_TOKEN%", (Object)templateToken);
            final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
            if (generator != null) {
                final JSONObject json = new JSONObject();
                json.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(userID));
                json.put("TEMPLATE_TYPE", this.templateType);
                final APIKey key = generator.generateAPIKey(json);
                laptopEnrollmentDetails.put("%ZAPIKEY%", (Object)key.getKeyValue());
            }
            final Properties contactInfoProps = DMUserHandler.getContactInfoProp(userID);
            if (contactInfoProps.containsKey("EMAIL_ID") && !((Hashtable<K, String>)contactInfoProps).get("EMAIL_ID").trim().isEmpty()) {
                laptopEnrollmentDetails.put("%EMAIL_ID%", ((Hashtable<K, Object>)contactInfoProps).get("EMAIL_ID"));
            }
            final int certType = ApiFactoryProvider.getServerSettingsAPI().getCertificateType();
            laptopEnrollmentDetails.put("isThirdPartyCertificate", certType == 2);
        }
        catch (final Exception ex) {
            Logger.getLogger(WindowsLaptopEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return laptopEnrollmentDetails;
    }
    
    public String createTemporaryZipFile(final JSONObject tempJSON) throws Exception {
        final Long userId = JSONUtil.optLongForUVH(tempJSON, "userID", (Long)null);
        final Long customerId = JSONUtil.optLongForUVH(tempJSON, "customerID", (Long)null);
        final String winAdminEnrollPath = String.valueOf(tempJSON.get("winAdminEnrollPath"));
        final String tempPath = String.valueOf(tempJSON.get("tempPath"));
        final String zipPath = String.valueOf(tempJSON.get("zipPath"));
        final List<String> fileList = new ArrayList<String>() {
            {
                this.add("enrollment.bat");
            }
        };
        final JSONObject laptopEnrollmentDetails = this.getLaptopEnrollmentDetails(userId, customerId);
        final File tempDir = new File(tempPath + File.separator + "scripts");
        Boolean tempDirCreation = tempDir.exists();
        if (!tempDirCreation) {
            tempDirCreation = tempDir.mkdirs();
        }
        if (tempDirCreation) {
            boolean fileCopy = Boolean.FALSE;
            for (final String fileName : fileList) {
                fileCopy = FileAccessUtil.copyFileWithinServer(winAdminEnrollPath + File.separator + fileName, tempPath + File.separator + fileName);
            }
            if (fileCopy) {
                fileCopy = FileAccessUtil.copyDirectoryWithinServer(winAdminEnrollPath + File.separator + "scripts", tempPath + File.separator + "scripts");
            }
            if (fileCopy) {
                final String adminEnrollBatFilePath = tempPath + File.separator + "scripts" + File.separator + "enrollment.bat";
                InputStream fileStream = null;
                byte[] batFileBytes = null;
                try {
                    fileStream = FileAccessUtil.readFileFromServer(adminEnrollBatFilePath);
                    batFileBytes = new byte[fileStream.available()];
                    fileStream.read(batFileBytes);
                }
                finally {
                    if (fileStream != null) {
                        fileStream.close();
                    }
                }
                String batFileContentAsString = new String(batFileBytes);
                for (final String dynamicVar : WindowsLaptopEnrollmentHandler.BATCH_FILE_DYNAMIC_VARS) {
                    batFileContentAsString = batFileContentAsString.replaceAll(dynamicVar, String.valueOf(laptopEnrollmentDetails.get(dynamicVar)));
                }
                FileAccessUtil.writeFileInServer(adminEnrollBatFilePath, (InputStream)new ByteArrayInputStream(batFileContentAsString.getBytes()));
                if (ApiFactoryProvider.getServerSettingsAPI().getCertificateType() != 2) {
                    final X509Certificate serverCertificate = CertificateHandler.getInstance().getAppropriateCertificate();
                    final PEMWriter certwriter = new PEMWriter((Writer)new FileWriter(tempPath + File.separator + "scripts" + File.separator + WindowsWICDEnrollmentHandler.SERVER_CERT_FILE_NAME));
                    certwriter.writeObject((Object)serverCertificate);
                    certwriter.flush();
                    certwriter.close();
                }
                final JSONObject jsonProps = new JSONObject();
                jsonProps.put("directoryToZipPath", (Object)tempPath);
                jsonProps.put("pathToZipFile", (Object)zipPath);
                jsonProps.put("folderPathInsideZipFile", (Object)"ManageEngine_MDMLaptopEnrollment");
                WindowsWICDEnrollmentHandler.createZip(jsonProps);
            }
        }
        return zipPath;
    }
    
    public Boolean checkRedownloadMessageForWindowsLaptop(final Properties msgProperty) {
        Boolean redownloadMessage = Boolean.FALSE;
        if (msgProperty != null) {
            final Properties mdmWicdMsg = ((Hashtable<K, Properties>)msgProperty).get("MDM_LAPTOP_ENROLL_MSG");
            if (mdmWicdMsg != null) {
                final List multiMessageList = ((Hashtable<K, ArrayList>)mdmWicdMsg).get("MULTI_MESSAGE_LIST");
                if (!multiMessageList.isEmpty()) {
                    final ArrayList singleMsgList = multiMessageList.get(0);
                    if (singleMsgList != null) {
                        final Properties singleMsgProps = singleMsgList.get(0);
                        if (singleMsgProps != null && singleMsgProps.containsKey("MSG_NAME") && ((Hashtable<K, Object>)singleMsgProps).get("MSG_NAME").toString().equalsIgnoreCase("DOWNLOAD_LAPTOP_TOOL")) {
                            redownloadMessage = Boolean.TRUE;
                        }
                    }
                }
            }
        }
        return redownloadMessage;
    }
    
    @Override
    public int getUnassignedDeviceCount(final Long customerID) throws Exception {
        final JSONObject criteriaValues = new JSONObject();
        criteriaValues.put("customerID", (Object)customerID);
        return MDMApiFactoryProvider.getMDMUtilAPI().getWindowsLaptopEnrollmentUnassignedCount(criteriaValues, this);
    }
    
    public int callSuperUnassignedDeviceCount(final Long customerID) throws Exception {
        return super.getUnassignedDeviceCount(customerID);
    }
    
    static {
        BATCH_FILE_DYNAMIC_VARS = new ArrayList<String>() {
            {
                this.add("%HOST_URL%");
                this.add("%HTTPS_PORT%");
                this.add("%CID%");
                this.add("%EMAIL_ID%");
                this.add("%TEMPLATE_TOKEN%");
                this.add("%ZAPIKEY%");
            }
        };
        ERROR_CODE_TO_REMARKS = new HashMap<Integer, String>() {
            {
                this.put(101, "mdm.db.win.device_already_registered");
                this.put(102, "mdm.db.win.device_failed_auth_with_server");
                this.put(103, "mdm.db.win.user_not_auth_to_enroll");
                this.put(104, "mdm.db.win.user_access_denied_to_cert");
                this.put(105, "mdm.db.unknown_error_enroll");
                this.put(106, "mdm.db.win.message_format_err_server");
                this.put(107, "mdm.enroll.assign_in_progess_remarks");
                this.put(108, "mdm.db.win.unsupported_device_type");
                this.put(109, "mdm.db.win.device_not_reg_with_ad");
                this.put(110, "mdm.db.win.server_not_reachable");
                this.put(111, "mdm.db.win.security_cert_date_invalid");
                this.put(112, "mdm.db.win.server_server_ssl_cert_invalid");
                this.put(113, "mdm.db.win.password_not_given");
                this.put(114, "mdm.db.win.server_not_reachable");
                this.put(115, "mdm.db.win.user_cap_reached");
                this.put(116, "mdm.db.win.unsupported_device_type");
                this.put(117, "mdm.db.win.license_state_blocking_enroll");
                this.put(118, "mdm.db.win.server_rejected_enroll_data");
                this.put(119, "mdm.db.win.server_insecure_redirect");
                this.put(120, "mdm.db.win.server_not_reachable");
                this.put(121, "mdm.db.win.error_exe");
            }
        };
    }
}
