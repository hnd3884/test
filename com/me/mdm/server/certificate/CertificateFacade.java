package com.me.mdm.server.certificate;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONArray;
import java.io.IOException;
import org.json.JSONException;
import java.util.List;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.HashMap;
import java.util.ArrayList;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import com.me.mdm.files.FileFacade;
import java.io.InputStream;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Level;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CertificateFacade
{
    private static final Logger LOGGER;
    
    public JSONObject getCertificateDetails(final JSONObject request) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final Long customerId = APIUtil.getCustomerID(request);
        final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
        final DeltaTokenUtil deltaTokenUtil = apiUtil.getDeltaTokenForAPIRequest(request);
        if (deltaTokenUtil != null && System.currentTimeMillis() - deltaTokenUtil.getRequestTimestamp() > 36000000L) {
            throw new APIHTTPException("COM0021", new Object[0]);
        }
        final String search = APIUtil.getStringFilter(request, "search");
        final String identityString = APIUtil.getStringFilter(request, "identity");
        final Integer certificateType = APIUtil.getIntegerFilter(request, "type");
        Criteria identityCriteria = null;
        if (!MDMStringUtils.isEmpty(identityString)) {
            identityCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_PASSWORD"), (Object)"", 0);
            if (Boolean.parseBoolean(identityString)) {
                identityCriteria = identityCriteria.negate();
            }
        }
        if (!MDMStringUtils.isEmpty(search)) {
            final Criteria searchCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_DISPLAY_NAME"), (Object)search, 12, false);
            if (identityCriteria != null) {
                identityCriteria = identityCriteria.and(searchCriteria);
            }
            else {
                identityCriteria = searchCriteria;
            }
        }
        if (certificateType != -1) {
            final Criteria typeCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)certificateType, 0);
            if (identityCriteria != null) {
                identityCriteria = identityCriteria.and(typeCriteria);
            }
            else {
                identityCriteria = typeCriteria;
            }
        }
        else {
            final Criteria typeCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)0, 0);
            if (identityCriteria != null) {
                identityCriteria = identityCriteria.and(typeCriteria);
            }
            else {
                identityCriteria = typeCriteria;
            }
        }
        final Criteria notTrashedCriteria = new Criteria(Column.getColumn("Certificates", "IS_ACTIVE"), (Object)true, 0);
        if (identityCriteria != null) {
            identityCriteria = identityCriteria.and(notTrashedCriteria);
        }
        else {
            identityCriteria = notTrashedCriteria;
        }
        ProfileCertificateUtil.getInstance();
        final int certificateCount = ProfileCertificateUtil.getCertificateCount(customerId, identityCriteria);
        final JSONObject meta = new JSONObject();
        meta.put("total_record_count", certificateCount);
        final Range range = new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit());
        final JSONObject jsonObject = responseJSON;
        final String s = "certificates";
        final JSONUtil instance = JSONUtil.getInstance();
        ProfileCertificateUtil.getInstance();
        jsonObject.put(s, (Object)instance.changeJSONKeyCase(ProfileCertificateUtil.getCertificateDetails(customerId, identityCriteria, range), 2));
        responseJSON.put("metadata", (Object)meta);
        if (pagingUtil.getNextToken(certificateCount) == null || pagingUtil.getPreviousToken() == null) {}
        if (certificateCount != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(certificateCount);
            if (pagingJSON != null) {
                responseJSON.put("paging", (Object)pagingJSON);
            }
        }
        return responseJSON;
    }
    
    public JSONObject getCertificateDetail(final JSONObject request) throws Exception {
        final Long customerId = APIUtil.getCustomerID(request);
        final Long certificateId = APIUtil.getResourceID(request, "certificate_id");
        ProfileCertificateUtil.getInstance();
        final JSONObject certificateJSON = ProfileCertificateUtil.getCertificateDetail(customerId, certificateId);
        if (certificateJSON.length() == 0) {
            CertificateFacade.LOGGER.log(Level.INFO, "Unknown certificate ID");
            throw new APIHTTPException("COM0008", new Object[] { String.valueOf(certificateId) });
        }
        return JSONUtil.getInstance().changeJSONKeyCase(certificateJSON, 2);
    }
    
    public void downloadCertificate(final APIRequest apiRequest) throws Exception {
        final JSONObject requestJson = apiRequest.toJSONObject();
        final Long certificateId = APIUtil.getResourceID(requestJson, "certificate_id");
        final Long customerID = APIUtil.getCustomerID(requestJson);
        final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CredentialCertificateInfo"));
        selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"));
        final Criteria criteria = new Criteria(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certificateId, 0).and(new Criteria(Column.getColumn("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerID, 0));
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "certificate_id: " + certificateId });
        }
        final String cerFilename = (String)dataObject.getFirstValue("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME");
        final String certPath = cerFolder + File.separator + cerFilename;
        if (cerFilename != null) {
            final String contentType = ProfileCertificateUtil.getInstance().getCertificataeContentType(cerFilename);
            apiRequest.httpServletResponse.setContentType(contentType);
            apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + cerFilename);
            final InputStream is = ApiFactoryProvider.getFileAccessAPI().readFile(certPath);
            int read = 0;
            final byte[] bytes = new byte[4096];
            BufferedOutputStream buffOut = null;
            try {
                buffOut = new BufferedOutputStream((OutputStream)apiRequest.httpServletResponse.getOutputStream());
                while ((read = is.read(bytes)) != -1) {
                    buffOut.write(bytes, 0, read);
                }
                buffOut.flush();
            }
            catch (final Exception ex) {
                CertificateFacade.LOGGER.log(Level.SEVERE, "Exception in downloadCertificate", ex);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            finally {
                buffOut.close();
            }
        }
    }
    
    public JSONObject addCertificate(final JSONObject request) throws Exception {
        String filePath = null;
        try {
            String eventLogRemarks = "mdm.mgmt.certrepo.certificate_added";
            int eventConstant = 72427;
            final Long customerId = APIUtil.getCustomerID(request);
            final Long userID = APIUtil.getUserID(request);
            final JSONObject requestJSON = request.getJSONObject("msg_body");
            if (!APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(request, false) && requestJSON.has("old_certificate_id")) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            final Long fileId = JSONUtil.optLongForUVH(requestJSON, "certificate_file_upload", Long.valueOf(0L));
            final FileFacade facade = new FileFacade();
            filePath = facade.getLocalPathForFileID(fileId);
            final String password = requestJSON.optString("certificate_password", "");
            final JSONObject certificateJSON = new JSONObject();
            certificateJSON.put("CERTIFICATE_TYPE", requestJSON.get("certificate_type"));
            certificateJSON.put("CERTIFICATE_FILE_UPLOAD", (Object)filePath);
            certificateJSON.put("CERTIFICATE_PASSWORD", (Object)password);
            certificateJSON.put("CUSTOMER_ID", (Object)customerId);
            final JSONObject certificateAddedJSON = ProfileCertificateUtil.addCredentials(certificateJSON);
            final JSONObject statusJSON = certificateAddedJSON.getJSONObject("UpdateStatus");
            if (statusJSON.has("ERROR_CODE")) {
                final Long errorCode = statusJSON.optLong("ERROR_CODE");
                if (errorCode.equals(CredentialsMgmtAction.CERTIFICATE_ALREADY_EXIST_ERROR_CODE)) {
                    throw new APIHTTPException("CER0003", new Object[0]);
                }
                if (errorCode.equals(CredentialsMgmtAction.CERTIFICATE_PARSING_ERROR_CODE)) {
                    throw new APIHTTPException("CER0004", new Object[0]);
                }
                if (errorCode.equals(CredentialsMgmtAction.CERTIFICATE_EXPIRED_ERROR_CODE)) {
                    throw new APIHTTPException("CER0005", new Object[0]);
                }
                if (errorCode.equals(CredentialsMgmtAction.CERTIFICATE_PASSWORD_ERROR)) {
                    throw new APIHTTPException("CER0001", new Object[0]);
                }
                if (errorCode.equals(CredentialsMgmtAction.DIGICERT_DEPENDENCIES_MISSING)) {
                    throw new APIHTTPException("CERTAUTHDIGI001", new Object[0]);
                }
                if (errorCode.equals(CredentialsMgmtAction.RA_CERTIFICATE_MATCH_NOT_FOUND)) {
                    throw new APIHTTPException("CERTAUTHDIGI002", new Object[0]);
                }
            }
            final Long certificateId = JSONUtil.optLongForUVH(certificateAddedJSON, "CERTIFICATE_ID", Long.valueOf(0L));
            String eventLogArgs = "";
            if (requestJSON.has("old_certificate_id")) {
                eventLogRemarks = "mdm.mgmt.certrepo.certificate_modified";
                eventConstant = 72428;
                final Long oldCertID = requestJSON.getLong("old_certificate_id");
                ProfileCertificateUtil.getInstance();
                final JSONObject oldCertJSON = ProfileCertificateUtil.getCertificateDetail(customerId, oldCertID);
                if (oldCertJSON.length() == 0) {
                    CertificateFacade.LOGGER.log(Level.INFO, "Unknown certificate ID : {0}", oldCertID);
                    throw new APIHTTPException("COM0008", new Object[] { String.valueOf(oldCertID) });
                }
                eventLogArgs = oldCertJSON.optString("certificate_name") + "@@@";
                final List oldCertList = new ArrayList();
                oldCertList.add(oldCertID);
                final HashMap hashMap = new HashMap();
                hashMap.put(certificateId, oldCertList);
                final Boolean isRedistribute = requestJSON.optBoolean("redistribute_profiles", (boolean)Boolean.FALSE);
                if (isRedistribute) {
                    eventLogRemarks += "_associated";
                }
                ProfileCertificateUtil.getInstance().modifyCredentials(hashMap, customerId, userID, isRedistribute);
            }
            final JSONObject resourceJSON = new JSONObject();
            resourceJSON.put("certificate_id", (Object)certificateId);
            final JSONObject messageHeaderJSON = request.getJSONObject("msg_header");
            messageHeaderJSON.put("resource_identifier", (Object)resourceJSON);
            final JSONObject retJson = this.getCertificateDetail(request);
            eventLogArgs += retJson.optString("certificate_name");
            final List remarkList = new ArrayList();
            remarkList.add(eventLogArgs);
            MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(APIUtil.getLoginID(request)), eventLogRemarks, remarkList, customerId, System.currentTimeMillis());
            return retJson;
        }
        catch (final JSONException e) {
            CertificateFacade.LOGGER.log(Level.SEVERE, "Error during certificate modify ", (Throwable)e);
            throw new APIHTTPException("COM0005", (Object[])null);
        }
        catch (final IOException e2) {
            CertificateFacade.LOGGER.log(Level.SEVERE, "Error during certificate modify ", e2);
            throw new APIHTTPException("CER0002", new Object[0]);
        }
        finally {
            if (filePath != null) {
                new FileFacade().deleteFile(filePath);
            }
        }
    }
    
    public JSONObject deleteCertificates(final JSONObject request) throws Exception {
        try {
            String eventLogRemarks = "mdm.mgmt.certrepo.certificate_deleted";
            final int eventConstant = 72429;
            final Long customerId = APIUtil.getCustomerID(request);
            final Long userID = APIUtil.getUserID(request);
            final JSONObject requestJSON = request.getJSONObject("msg_body");
            final JSONArray certificateIds = requestJSON.getJSONArray("certificate_ids");
            final Boolean isRedistribute = requestJSON.optBoolean("redistribute_profiles", (boolean)Boolean.FALSE);
            if (isRedistribute) {
                eventLogRemarks += "_associated";
            }
            if (!APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(request, false)) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            final JSONObject certificateAddedJSON = ProfileCertificateUtil.getInstance().deleteCredentials(certificateIds, customerId, userID, isRedistribute);
            final JSONObject resourceJSON = new JSONObject();
            resourceJSON.put("certificate_ids", (Object)certificateAddedJSON.getJSONArray("CERTIFICATE_RESOURCE_ID"));
            final JSONObject messageHeaderJSON = request.getJSONObject("msg_header");
            final String arg = ProfileCertificateUtil.getInstance().getCertNames(certificateIds, customerId);
            final List remarkList = new ArrayList();
            remarkList.add(arg);
            MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(APIUtil.getLoginID(request)), eventLogRemarks, remarkList, customerId, System.currentTimeMillis());
            messageHeaderJSON.put("resource_identifier", (Object)resourceJSON);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", (Object)Boolean.TRUE);
            return jsonObject;
        }
        catch (final JSONException e) {
            CertificateFacade.LOGGER.log(Level.SEVERE, "Error during certificate modify ", (Throwable)e);
            throw new APIHTTPException("COM0005", (Object[])null);
        }
        catch (final IOException e2) {
            CertificateFacade.LOGGER.log(Level.SEVERE, "Error during certificate modify ", e2);
            throw new APIHTTPException("CER0002", new Object[0]);
        }
        catch (final Exception e3) {
            CertificateFacade.LOGGER.log(Level.SEVERE, "Error during certificate modify ", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject deleteCertificate(final JSONObject request) throws Exception {
        final Long customerId = APIUtil.getCustomerID(request);
        final Long certificateId = APIUtil.getResourceID(request, "certificate_id");
        ProfileCertificateUtil.getInstance();
        final JSONObject certificateJSON = ProfileCertificateUtil.getCertificateDetail(customerId, certificateId);
        if (certificateJSON.length() == 0) {
            CertificateFacade.LOGGER.log(Level.INFO, "Unknown certificate ID");
            throw new APIHTTPException("COM0008", new Object[] { String.valueOf(certificateId) });
        }
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("CertDeleteAllowed")) {
            final String certFileName = certificateJSON.getString("CERTIFICATE_FILE_NAME");
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("Certificates");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateId, 0));
            MDMUtil.getPersistenceLite().delete(deleteQuery);
            ProfileCertificateUtil.getInstance().removeCertificateFile(MDMUtil.getCredentialCertificateFolder(customerId) + File.separator + certFileName);
            CertificateFacade.LOGGER.log(Level.INFO, "Certificate is delete Cert ID {0} using the user ID {1} of the customer {2}", new Object[] { certificateId, APIUtil.getUserID(request), customerId });
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            return jsonObject;
        }
        throw new APIHTTPException("PAY0007", new Object[0]);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
