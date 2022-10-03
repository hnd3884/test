package com.me.mdm.server.adep;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.bouncycastle.cms.CMSException;
import org.json.JSONException;
import java.io.IOException;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.FileFacade;
import com.me.mdm.files.upload.FileUploadManager;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import java.util.logging.Level;
import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import java.util.logging.Logger;

public class ABMAuthTokenFacade
{
    private static ABMAuthTokenFacade facadeObj;
    public static Logger logger;
    
    public static ABMAuthTokenFacade getInstance() {
        if (ABMAuthTokenFacade.facadeObj == null) {
            ABMAuthTokenFacade.facadeObj = new ABMAuthTokenFacade();
        }
        return ABMAuthTokenFacade.facadeObj;
    }
    
    private String getDepNotifyEmail(final Long custId) throws Exception {
        final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil();
        return mailGenerator.getCustomerEMailAddress(custId, "MdM-DEP");
    }
    
    public JSONObject getNotifyEmailAddr(final JSONObject apiJsonObj) throws Exception {
        final Long customerID = APIUtil.getCustomerID(apiJsonObj);
        final Long tokenId = Long.valueOf(String.valueOf(apiJsonObj.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
        validateIfDepTokenExists(tokenId, customerID);
        final JSONObject response = new JSONObject();
        final JSONArray emailIdsJA = new JSONArray();
        response.put("server_name", (Object)DEPEnrollmentUtil.getDEPServerName(tokenId));
        response.put("server_id", (Object)tokenId);
        response.put("email", (Object)emailIdsJA);
        if (DEPEnrollmentUtil.getDEPEnrollmentStatus(customerID) != 3) {
            ABMAuthTokenFacade.logger.log(Level.INFO, "No Apple Enrollment ABM/ASM Profile is created. So returning empty email id array..");
            return response;
        }
        final String emailString = this.getDepNotifyEmail(customerID);
        if (emailString != null) {
            final String[] split;
            final String[] emailIds = split = emailString.split(",");
            for (final String eachEmailId : split) {
                emailIdsJA.put((Object)eachEmailId);
            }
        }
        response.put("email", (Object)emailIdsJA);
        return response;
    }
    
    public JSONObject createTokenId(final Long customerID) throws Exception {
        final boolean isApnsEmpty = APNsCertificateHandler.getAPNSCertificateDetails().isEmpty();
        if (isApnsEmpty) {
            throw new APIHTTPException("APNS101", new Object[0]);
        }
        final Long tokenId = AppleDEPAuthTokenHandler.getInstance().getNewDEPTokenId(customerID);
        AppleDEPCertificateHandler.getInstance().getDEPTokenPublicKeyPath(customerID, tokenId);
        final JSONObject result = new JSONObject();
        result.put("server_id", (Object)tokenId);
        return result;
    }
    
    public JSONObject saveDEPToken(final JSONObject apiJsonObj) throws Exception {
        final JSONObject response = new JSONObject();
        final Long customerID = APIUtil.getCustomerID(apiJsonObj);
        final Long userID = APIUtil.getUserID(apiJsonObj);
        final Long loginId = APIUtil.getLoginID(apiJsonObj);
        final Long tokenId = Long.valueOf(String.valueOf(apiJsonObj.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
        validateIfDepTokenExists(tokenId, customerID);
        final JSONObject apiBodyJson = apiJsonObj.getJSONObject("msg_body");
        if (!apiBodyJson.has("file_id")) {
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        final Long fileID = Long.valueOf(String.valueOf(apiBodyJson.get("file_id")));
        try {
            final String certifictateFileUpload = FileUploadManager.getFilePath(fileID);
            new FileFacade().writeFile(certifictateFileUpload, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(certifictateFileUpload));
            final File tokenFile = new File(certifictateFileUpload);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CERTIFICATE_FILE_UPLOAD", (Object)tokenFile);
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("DEP_TOKEN_ID", (Object)tokenId);
            jsonObject.put("USER_ID", (Object)userID);
            if (apiBodyJson.has("email")) {
                final JSONArray mailIds = apiBodyJson.getJSONArray("email");
                final StringBuilder builder = new StringBuilder();
                builder.append(mailIds.get(0));
                for (int i = 1; i < mailIds.length(); ++i) {
                    builder.append(",");
                    builder.append(mailIds.get(i));
                }
                jsonObject.put("email_id", (Object)builder.toString());
            }
            else if (this.getDepNotifyEmail(customerID) == null) {
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            AppleDEPAuthTokenHandler.getInstance().addOrReplaceDEPToken(jsonObject, loginId);
            response.put("success", true);
            response.put("server_name", (Object)DEPEnrollmentUtil.getDEPServerName(tokenId));
            response.put("server_id", (Object)tokenId);
            return response;
        }
        catch (final IOException e) {
            ABMAuthTokenFacade.logger.log(Level.SEVERE, "Exception while save ABM token.. IO exp..", e);
            throw new APIHTTPException("ABM002", new Object[0]);
        }
        catch (final JSONException e2) {
            ABMAuthTokenFacade.logger.log(Level.SEVERE, "Exception while save ABM token.. Json exp..", (Throwable)e2);
            throw new APIHTTPException("ABM004", new Object[0]);
        }
        catch (final CMSException e3) {
            ABMAuthTokenFacade.logger.log(Level.SEVERE, "Exception while save ABM token.. CMS exp..", (Throwable)e3);
            throw new APIHTTPException("ABM003", new Object[0]);
        }
        catch (final SyMException e4) {
            response.put("success", false);
            response.put("server_id", (Object)tokenId);
            final JSONObject errorDetails = ADEPServerSyncHandler.getInstance(tokenId, customerID).getErrorDetails();
            if (errorDetails != null) {
                response.put("error", (Object)errorDetails);
            }
            return response;
        }
        catch (final APIHTTPException e5) {
            throw e5;
        }
        catch (final Exception e6) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject removeDEPToken(final JSONObject apiJsonObj) throws Exception {
        try {
            final Long custId = APIUtil.getCustomerID(apiJsonObj);
            final Long tokenId = Long.valueOf(String.valueOf(apiJsonObj.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
            validateIfDepTokenExists(tokenId, custId);
            boolean forceDelete = false;
            if (apiJsonObj.has("msg_body")) {
                forceDelete = apiJsonObj.getJSONObject("msg_body").optBoolean("force_delete");
            }
            final JSONObject deleteDepServer = new JSONObject();
            deleteDepServer.put("DEP_TOKEN_ID", (Object)tokenId);
            deleteDepServer.put("CUSTOMER_ID", (Object)custId);
            deleteDepServer.put("force_delete", forceDelete);
            deleteDepServer.put("login_id", (Object)APIUtil.getLoginID(apiJsonObj));
            final JSONObject responseJson = AppleDEPRequestProcessor.deleteDEPToken(deleteDepServer);
            return responseJson;
        }
        catch (final Exception ex) {
            ABMAuthTokenFacade.logger.log(Level.SEVERE, "Exception in removing dep token ", ex);
            throw ex;
        }
    }
    
    public static void validateIfDepTokenExists(final Long tokenId, final Long customerId) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            sq.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
            final Criteria tokenCri = new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenId, 0);
            final Criteria customerCri = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            sq.setCriteria(customerCri.and(tokenCri));
            DataObject dataObject = null;
            dataObject = MDMUtil.getPersistenceLite().get(sq);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { tokenId });
            }
        }
        catch (final DataAccessException e) {
            ABMAuthTokenFacade.logger.log(Level.SEVERE, "Exception in validating depTokenId: ", e.getErrorString());
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        ABMAuthTokenFacade.facadeObj = null;
        ABMAuthTokenFacade.logger = Logger.getLogger("MDMEnrollment");
    }
}
