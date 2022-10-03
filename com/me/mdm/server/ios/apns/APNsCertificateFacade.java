package com.me.mdm.server.ios.apns;

import org.json.JSONArray;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import com.me.mdm.files.FileFacade;
import java.io.OutputStream;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import mdm.mdm.source.server.com.me.mdm.api.enrollment.apple.apns.file.ApnsFileFacade;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class APNsCertificateFacade
{
    Logger logger;
    private static APNsCertificateFacade facadeInstance;
    private static APNsCertificateHandler handlerInstance;
    public static final String CERTIFICATE_FILE_UPLOAD = "certificate_file_upload";
    public static final String NOTIFICATION_EMAIL = "notification_email";
    public static final String APPLE_ID = "apple_id";
    public static final String COMPANY_NAME = "company_name";
    public static final String IS_VENDORCSR_VALID = "is_vendorcsr_valid";
    public static final String IS_APPLE_DEVICES_ALREADY_ENROLLED = "is_apple_device_already_enrolled";
    public static final String ALLOW_TO_UPLOAD_APNS_PEM_FILE = "allow_to_upload_apns_pem_file";
    
    public APNsCertificateFacade() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static APNsCertificateFacade getFacadeInstance() {
        if (APNsCertificateFacade.facadeInstance == null || APNsCertificateFacade.handlerInstance == null) {
            APNsCertificateFacade.facadeInstance = new APNsCertificateFacade();
            APNsCertificateFacade.handlerInstance = APNsCertificateHandler.getInstance();
        }
        return APNsCertificateFacade.facadeInstance;
    }
    
    public JSONObject getApnsDetails(final JSONObject apiRequest) throws Exception {
        JSONObject apnsDetailsJO = APNsCertificateFacade.handlerInstance.getAPNSDetail();
        if (apnsDetailsJO == null) {
            apnsDetailsJO = new JSONObject();
        }
        else if (apnsDetailsJO.has("ERROR_CODE")) {
            final String apiErrorMsg = this.getApiErrorMsg(Integer.parseInt(apnsDetailsJO.get("ERROR_CODE").toString()));
            apnsDetailsJO.put("ERROR_MESSAGE", (Object)apiErrorMsg);
        }
        final Long customerId = APIUtil.optCustomerID(apiRequest);
        String companyName = "";
        if (customerId != -1L) {
            companyName = MDMApiFactoryProvider.getMDMUtilAPI().getOrgName(customerId);
            if (companyName.equals("ManageEngine")) {
                apnsDetailsJO.put("company_name", (Object)" ");
            }
            else {
                apnsDetailsJO.put("company_name", (Object)companyName);
            }
        }
        apnsDetailsJO.put("allow_to_upload_apns_pem_file", (Object)new ApnsFileFacade().isAllowedUploadApnsPemFile(APIUtil.getLoginID(apiRequest)));
        apnsDetailsJO.put("is_apple_device_already_enrolled", ManagedDeviceHandler.getInstance().getAppleManagedDeviceCount() > 0);
        return apnsDetailsJO;
    }
    
    private String getApiErrorMsg(final Integer apnsCertError) {
        if (apnsCertError == 1001) {
            return "APNS_Expired";
        }
        return "APNS_Revoked";
    }
    
    public JSONObject signCsrHandler(final APIRequest apiRequest) throws Exception {
        final JSONObject response = new JSONObject();
        try {
            final Long customerID = APIUtil.optCustomerID(apiRequest.toJSONObject());
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerID, "APPLE_APNS_MODULE", "MANUAL_SIGN_REQUEST");
            APNsCertificateFacade.handlerInstance.SignCSR(customerID);
            response.put("is_vendorcsr_valid", APNsCertificateFacade.handlerInstance.isVendorSignCSRValid());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in is Vendor Sign CSR Valid.. So returning False..", e);
            response.put("is_vendorcsr_valid", false);
        }
        return response;
    }
    
    public void downloadVendorSignedCsr(final APIRequest apiRequest) throws Exception {
        this.logger.log(Level.INFO, "Inside download Vendor Csr Api Request()");
        final Long customerId = APIUtil.optCustomerID(apiRequest.toJSONObject());
        boolean isSignedCsrDownloadable = false;
        try {
            isSignedCsrDownloadable = APNsCertificateFacade.handlerInstance.SignCSR(customerId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while Apns CSR signing..", ex);
        }
        String fileName = null;
        String signedCsrLocation = null;
        if (isSignedCsrDownloadable) {
            final JSONObject vendorSignedInfo = APNsCertificateFacade.handlerInstance.getVendorSignedInfo();
            signedCsrLocation = vendorSignedInfo.optString("VENDOR_SIGNED_CSR", (String)null);
            fileName = "VendorSignedCSR.plist";
        }
        if (signedCsrLocation != null) {
            this.downloadFile(apiRequest, fileName, signedCsrLocation);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "APPLE_APNS_MODULE", "PLIST_DOWNLOAD");
            this.logger.log(Level.INFO, "Vendor CSR Download successful..");
            return;
        }
        this.logger.log(Level.INFO, "Vendor CSR download.. File Not found..");
        throw new APIHTTPException("APNS013", new Object[0]);
    }
    
    public void downloadManualCsr(final APIRequest apiRequest) throws Exception {
        this.logger.log(Level.INFO, "Inside download Manual Csr Api Request()");
        final Long customerId = APIUtil.optCustomerID(apiRequest.toJSONObject());
        String csrLocation = null;
        String privateKeyLocation = null;
        String fileName = null;
        JSONObject csrInfo = null;
        try {
            csrInfo = APNsCertificateFacade.handlerInstance.getCSRInfo();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while retrieving csrInfo.. Going to try again..");
            APNsCertificateFacade.handlerInstance.SignCSR(customerId);
            csrInfo = APNsCertificateFacade.handlerInstance.getCSRInfo();
        }
        csrLocation = (String)csrInfo.get("CSR_LOCATION");
        privateKeyLocation = (String)csrInfo.get("PRIVATEKEY_LOCATION");
        fileName = "ManualSignCSR.csr";
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(csrLocation) || ApiFactoryProvider.getFileAccessAPI().getFileSize(csrLocation) == 0L || !ApiFactoryProvider.getFileAccessAPI().isFileExists(privateKeyLocation) || ApiFactoryProvider.getFileAccessAPI().getFileSize(privateKeyLocation) == 0L) {
            this.logger.log(Level.INFO, "Manual CSR download.. CSR file not found. Trying again..");
            APNsCertificateFacade.handlerInstance.SignCSR(customerId);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        this.downloadFile(apiRequest, fileName, csrLocation);
        MEMDMTrackParamManager.getInstance().incrementTrackValue(APIUtil.optCustomerID(apiRequest.toJSONObject()), "APPLE_APNS_MODULE", "MANUAL_SIGN_CSR_DOWNLOAD");
        this.logger.log(Level.INFO, "Manual CSR Download successful..");
    }
    
    private void downloadFile(final APIRequest apiRequest, final String fileName, final String filePath) throws Exception {
        apiRequest.httpServletResponse.setContentType("application/x-plist");
        apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        InputStream is = null;
        OutputStream os = null;
        try {
            is = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            int read = 0;
            final byte[] bytes = new byte[4096];
            os = (OutputStream)apiRequest.httpServletResponse.getOutputStream();
            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            os.flush();
        }
        finally {
            try {
                if (os != null) {
                    os.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception closing OutputStream os", e.getMessage());
            }
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception closing InputStream is", e.getMessage());
            }
        }
    }
    
    private void uploadApnsFile(final Long customerId, final String userName, final Long fileId) throws Exception {
        try {
            final JSONObject object = new JSONObject();
            this.logger.log(Level.INFO, "Uploading apns certificate..");
            final String filePath = FileFacade.getInstance().getLocalPathForFileID(fileId);
            final File cerFile = new File(filePath);
            if (!APIUtil.isAllowedCertificateMimeType(cerFile)) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            object.put("CERTIFICATE_FILE_UPLOAD", (Object)cerFile);
            object.put("USER_NAME", (Object)userName);
            object.put("CUSTOMER_ID", (Object)customerId);
            APNsCertificateFacade.handlerInstance.uploadAPNsCertificate(object);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "API Exception in uploadApnsApiRequest()..", e);
            throw e;
        }
        catch (final IOException e2) {
            throw new APIHTTPException(e2.getMessage(), new Object[0]);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception in uploadApnsApiRequest()..", e3);
            throw e3;
        }
    }
    
    public void addApnsCertificate(final JSONObject apiRequest) {
        this.logger.log(Level.INFO, "Inside Add Apns certificate..");
        try {
            final Long customerId = APIUtil.optCustomerID(apiRequest);
            final String userName = APIUtil.getUserName(apiRequest);
            final JSONObject params = apiRequest.getJSONObject("msg_body");
            this.uploadApnsFile(customerId, userName, params.getLong("certificate_file_upload"));
            String emailId = "";
            String appleId = "";
            appleId = params.get("apple_id").toString();
            final JSONArray mailIds = params.getJSONArray("notification_email");
            final StringBuilder builder = new StringBuilder();
            builder.append(mailIds.get(0));
            for (int i = 1; i < mailIds.length(); ++i) {
                builder.append(",");
                builder.append(mailIds.get(i));
            }
            emailId = builder.toString();
            final Long apnsCertID = APNsCertificateFacade.handlerInstance.getAPNSDetail().getLong("CERTIFICATE_ID");
            final HashMap apnsCertificateDetails = new HashMap();
            apnsCertificateDetails.put("EMAIL_ADDRESS", emailId);
            apnsCertificateDetails.put("APPLE_ID", appleId);
            APNsCertificateFacade.handlerInstance.addOrUpdateCertificateDetails(apnsCertificateDetails, apnsCertID);
            if (params.has("company_name")) {
                final String newCompanyName = String.valueOf(params.get("company_name"));
                MDMCustomerInfoUtil.getInstance().updateCompanyName(customerId, newCompanyName);
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "ApiHttpException in Add/renew Apns.. ", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in Add/renew Apns.. ", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void modifyApnsCertificate(final JSONObject apiRequest) throws Exception {
        this.logger.log(Level.INFO, "Inside Renew Apns certificate..");
        final Long customerId = APIUtil.optCustomerID(apiRequest);
        final String userName = APIUtil.getUserName(apiRequest);
        final JSONObject params = apiRequest.getJSONObject("msg_body");
        String emailId = "";
        String appleId = "";
        final Long apnsCertID = APNsCertificateFacade.handlerInstance.getAPNSDetail().optLong("CERTIFICATE_ID");
        final HashMap apnsCertificateDetails = new HashMap();
        if (apnsCertID == null || apnsCertID == 0L) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        if (params.has("notification_email")) {
            final JSONArray mailIds = params.getJSONArray("notification_email");
            final StringBuilder builder = new StringBuilder();
            builder.append(mailIds.get(0));
            for (int i = 1; i < mailIds.length(); ++i) {
                builder.append(",");
                builder.append(mailIds.get(i));
            }
            emailId = builder.toString();
            apnsCertificateDetails.put("EMAIL_ADDRESS", emailId);
        }
        if (params.has("apple_id")) {
            appleId = params.get("apple_id").toString();
            apnsCertificateDetails.put("APPLE_ID", appleId);
        }
        APNsCertificateFacade.handlerInstance.addOrUpdateCertificateDetails(apnsCertificateDetails, apnsCertID);
        if (params.has("certificate_file_upload")) {
            this.uploadApnsFile(customerId, userName, params.getLong("certificate_file_upload"));
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "APPLE_APNS_MODULE", "RENEW_APNS");
        }
        if (params.has("company_name")) {
            final String newCompanyName = String.valueOf(params.get("company_name"));
            MDMCustomerInfoUtil.getInstance().updateCompanyName(customerId, newCompanyName);
        }
    }
    
    public JSONObject removeAPNsCertificate(final JSONObject apiRequest) throws Exception {
        final Long customerId = APIUtil.optCustomerID(apiRequest);
        final String userName = APIUtil.getUserName(apiRequest);
        final JSONObject jsonObj = APNsCertificateFacade.handlerInstance.removeAPNsCertificate(userName, customerId);
        return jsonObj;
    }
    
    static {
        APNsCertificateFacade.facadeInstance = null;
        APNsCertificateFacade.handlerInstance = null;
    }
}
