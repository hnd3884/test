package com.me.mdm.server.windows.profile;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WpSCEPResponseProcessor
{
    public static Logger logger;
    private static WpSCEPResponseProcessor wpSCEPResponseProcessor;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_PENDING = 2;
    public static final int STATUS_UNKNOWN = 32;
    public static final int STATUS_FAILED = 16;
    public static final String ERR_CONNECTION_ABORT_SYNTAX_ERROR = "-2147012866";
    public static final String ERR_CONNECTION_HTTPS_URL_INVALID_CA = "-2147012851";
    public static final String ERR_INVALID_CHARS_IN_CERTIFICATE_NAMES = "-2146885597";
    public static final String ERR_TIME_MISMATCH_CERT_EXPIRED_ALREADY = "-2146762495";
    public static final String ERR_CONNECTION_NAME_NOT_RESOLVED = "-2147012889";
    public static final String ERR_CA_THUMBPRINT_INVALID = "-2146893822";
    public static final String ERR_CHALLENGE_PASSWORD_INVALID = "-2147467259";
    public static final String ERR_CONNECTION_GENERIC = "-2147012867";
    
    private WpSCEPResponseProcessor() {
    }
    
    public static WpSCEPResponseProcessor getInstance() {
        if (WpSCEPResponseProcessor.wpSCEPResponseProcessor == null) {
            WpSCEPResponseProcessor.wpSCEPResponseProcessor = new WpSCEPResponseProcessor();
        }
        return WpSCEPResponseProcessor.wpSCEPResponseProcessor;
    }
    
    public Integer processSCEPStatusCheckResponse(final JSONObject responseJSON) {
        Integer cumulativeStatus = 1;
        try {
            final Long resourceID = responseJSON.getLong("RESOURCE_ID");
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
            final String collectionId = String.valueOf(responseJSON.get("collectionID"));
            final JSONObject responseJSONValues = responseJSON.getJSONObject("scepStatusCheckJSON");
            final Iterator<String> responseJsonIter = responseJSONValues.keys();
            String errorCodeForFailedSCEP = "";
            String certThumbPrintSuccess = "";
            while (responseJsonIter.hasNext()) {
                final String scepConfigName = responseJsonIter.next();
                final JSONObject scepResponse = responseJSONValues.getJSONObject(scepConfigName);
                final String statusStr = String.valueOf(scepResponse.get("Status"));
                if (statusStr != null && !statusStr.trim().isEmpty()) {
                    final Integer status = Integer.parseInt(statusStr);
                    final String errorCode = String.valueOf(scepResponse.get("ErrorCode"));
                    switch (status) {
                        case 1: {
                            final String certThumbprint = String.valueOf(scepResponse.get("CertThumbPrint"));
                            certThumbPrintSuccess = certThumbPrintSuccess + certThumbprint + "##";
                            continue;
                        }
                        case 2: {
                            cumulativeStatus = 2;
                            continue;
                        }
                        case 16:
                        case 32: {
                            cumulativeStatus = 16;
                            errorCodeForFailedSCEP = errorCodeForFailedSCEP + scepConfigName + "@@" + errorCode + "##";
                            continue;
                        }
                    }
                }
            }
            String remarks = "dc.db.mdm.collection.Successfully_applied_policy";
            String remarksArgs = "";
            if (cumulativeStatus == 1) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
            }
            else if (cumulativeStatus == 16) {
                Integer dbErrorCode = null;
                for (final String errorCodeFailedSCEP : errorCodeForFailedSCEP.split("##")) {
                    final String scepConfigName2 = errorCodeFailedSCEP.split("@@")[0];
                    final String errorCode2 = errorCodeFailedSCEP.split("@@")[1];
                    remarksArgs = scepConfigName2;
                    final String s = errorCode2;
                    switch (s) {
                        case "-2147012866": {
                            remarks = "mdm.profile.scep.failed_csr_syntax_error";
                            dbErrorCode = 30004;
                            break;
                        }
                        case "-2147012851": {
                            remarks = "mdm.profile.scep.failed_invalid_ca";
                            dbErrorCode = 30000;
                            break;
                        }
                        case "-2146885597": {
                            remarks = "mdm.profile.scep.failed_invalid_names";
                            dbErrorCode = 30001;
                            break;
                        }
                        case "-2146762495": {
                            remarks = "mdm.profile.scep.failed_time_mismatch";
                            dbErrorCode = 30002;
                            break;
                        }
                        case "-2146893822": {
                            remarks = "mdm.profile.scep.failed_invalid_ca_thumbprint";
                            dbErrorCode = 30005;
                            break;
                        }
                        case "-2147467259": {
                            remarks = "mdm.profile.scep.failed_invalid_challenge";
                            dbErrorCode = 30006;
                            break;
                        }
                        case "-2147012889":
                        case "-2147012867": {
                            remarks = "mdm.profile.scep.failed_connection_dns";
                            dbErrorCode = 30003;
                            break;
                        }
                        default: {
                            remarks = "mdm.profile.scep.failed_unknown";
                            remarksArgs = remarksArgs + "@@@" + errorCode2 + "@@@" + UrlReplacementUtil.replaceUrlAndAppendTrackCode("$(mdmUrl)/how-to/logs-how-to.html?");
                            break;
                        }
                    }
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2024, resourceID, MDMEventConstant.DC_SYSTEM_USER, remarks, remarksArgs, customerID);
                }
                MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, Long.valueOf(collectionId), dbErrorCode);
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 11, remarks + "@@@" + remarksArgs);
            }
        }
        catch (final Exception exp) {
            WpSCEPResponseProcessor.logger.log(Level.SEVERE, "Error in processing WpSCEPStatusCheck response {0}", exp);
        }
        return cumulativeStatus;
    }
    
    static {
        WpSCEPResponseProcessor.logger = Logger.getLogger("MDMLogger");
        WpSCEPResponseProcessor.wpSCEPResponseProcessor = null;
    }
}
