package com.me.mdm.server.enrollment.admin.windows;

import java.util.Hashtable;
import java.security.cert.X509Certificate;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.io.FileInputStream;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.FileWriter;
import com.me.mdm.certificate.CertificateHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.ByteArrayInputStream;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.WindowsWICDAssignUserCSVProcessor;
import com.me.mdm.core.enrollment.WindowsWICDEnrollmentHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentDownloadInterface;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;

public class WindowsICDAdminHandler extends BaseAdminEnrollmentHandler implements AdminEnrollmentDownloadInterface
{
    public WindowsICDAdminHandler(final Integer templateType) {
        super(templateType);
    }
    
    @Override
    protected AdminEnrollmentHandler getHandler() {
        return new WindowsWICDEnrollmentHandler();
    }
    
    @Override
    public String getOperationLabelForTemplate() {
        return new WindowsWICDAssignUserCSVProcessor().operationLabel;
    }
    
    @Override
    public JSONObject getEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject json = super.getEnrollmentDetails(requestJSON);
        final Properties contactInfoProps = DMUserHandler.getContactInfoProp(APIUtil.getUserID(requestJSON));
        if (contactInfoProps.containsKey("EMAIL_ID") && !((Hashtable<K, String>)contactInfoProps).get("EMAIL_ID").trim().isEmpty()) {
            final JSONObject additionalContext = new JSONObject();
            additionalContext.put("technicianMailId", ((Hashtable<K, Object>)contactInfoProps).get("EMAIL_ID"));
            json.put("additional_context", (Object)additionalContext);
        }
        return json;
    }
    
    @Override
    public String getFileDownloadPath(final JSONObject requestJSON) throws Exception {
        SyMUtil.updateSyMParameter("Admin_Enrollment_WICD_Tool_Download_Clicked", Boolean.TRUE.toString());
        final String winAdminEnrollPath = MDMMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "mdm" + File.separator + "windowsadminenroll";
        final String tempPath = winAdminEnrollPath + File.separator + "temp_" + MDMUtil.getCurrentTimeInMillis();
        final String zipPath = winAdminEnrollPath + File.separator + "WindowsWICD.zip";
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final Long userId = APIUtil.getUserID(requestJSON);
        final File tempDir = new File(tempPath);
        Boolean tempDirCreation = tempDir.exists();
        if (!tempDirCreation) {
            tempDirCreation = tempDir.mkdirs();
        }
        if (tempDirCreation) {
            final JSONObject wicdEnrollDetails = new WindowsWICDEnrollmentHandler().getWICDEnrollmentDetails(userId, customerID);
            final String customizationXmlStr = String.valueOf(wicdEnrollDetails.get("customizationXml"));
            FileAccessUtil.writeFileInServer(tempPath + File.separator + WindowsWICDEnrollmentHandler.CUSTOM_XML_FILE_NAME, (InputStream)new ByteArrayInputStream(customizationXmlStr.getBytes()));
            if (ApiFactoryProvider.getServerSettingsAPI().getCertificateType() != 2) {
                final X509Certificate serverCertificate = CertificateHandler.getInstance().getAppropriateCertificate();
                final PEMWriter certwriter = new PEMWriter((Writer)new FileWriter(tempPath + File.separator + WindowsWICDEnrollmentHandler.SERVER_CERT_FILE_NAME));
                certwriter.writeObject((Object)serverCertificate);
                certwriter.flush();
                certwriter.close();
            }
            final String pathToPPKGBatFile = winAdminEnrollPath + File.separator + WindowsWICDEnrollmentHandler.CREATE_PPKG_BAT_FILE_NAME;
            FileAccessUtil.writeFileInServer(tempPath + File.separator + WindowsWICDEnrollmentHandler.CREATE_PPKG_BAT_FILE_NAME, (InputStream)new FileInputStream(pathToPPKGBatFile));
            final JSONObject jsonProps = new JSONObject();
            jsonProps.put("directoryToZipPath", (Object)tempPath);
            jsonProps.put("pathToZipFile", (Object)zipPath);
            jsonProps.put("folderPathInsideZipFile", (Object)WindowsWICDEnrollmentHandler.FOLDER_PATH_IN_ZIP);
            new WindowsWICDEnrollmentHandler();
            WindowsWICDEnrollmentHandler.createZip(jsonProps);
            SyMUtil.updateSyMParameter("PPKG_DOWNLOADED_ALREADY", Boolean.TRUE.toString());
            MessageProvider.getInstance().hideMessage("DOWNLOAD_PPKG_TOOL");
            SyMUtil.updateSyMParameter("Admin_Enrollment_WICD_Tool_Download_Success", Boolean.TRUE.toString());
        }
        return zipPath;
    }
}
