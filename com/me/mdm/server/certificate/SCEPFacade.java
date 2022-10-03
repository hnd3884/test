package com.me.mdm.server.certificate;

import com.adventnet.ds.query.DeleteQuery;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfilePayloadOperator;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.List;
import java.util.Collections;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerManager;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SCEPFacade
{
    public static final int INTERNAL_CA = 1;
    public static final int THIRD_PARTY_CA = 2;
    Logger logger;
    
    public SCEPFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getSCEPConfigurations(final JSONObject request) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final String requestUrl = APIUtil.getRequestURL(request);
        Long serverId = null;
        if (requestUrl.contains("/scep/servers")) {
            serverId = APIUtil.getResourceID(request, "server_id");
        }
        final Long customerID = APIUtil.getCustomerID(request);
        final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
        final DeltaTokenUtil deltaTokenUtil = apiUtil.getDeltaTokenForAPIRequest(request);
        if (deltaTokenUtil != null && System.currentTimeMillis() - deltaTokenUtil.getRequestTimestamp() > 36000000L) {
            throw new APIHTTPException("COM0021", new Object[0]);
        }
        Criteria criteria = new Criteria(Column.getColumn("Certificates", "IS_ACTIVE"), (Object)true, 0);
        final String search = APIUtil.getStringFilter(request, "search");
        if (!MDMStringUtils.isEmpty(search)) {
            criteria = criteria.and(new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIGURATION_NAME"), (Object)search, 12, false));
        }
        if (serverId != null) {
            final Criteria serverCriteria = new Criteria(Column.getColumn("SCEPServers", "SERVER_ID"), (Object)serverId, 0);
            if (criteria == null) {
                criteria = serverCriteria;
            }
            else {
                criteria = criteria.and(serverCriteria);
            }
        }
        final int scepCount = ProfileCertificateUtil.getSCEPCount(customerID, criteria);
        final JSONObject meta = new JSONObject();
        meta.put("total_record_count", scepCount);
        final Range range = new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit());
        responseJSON.put("scep", (Object)ProfileCertificateUtil.getSCEPConfigDetails(customerID, criteria, range));
        responseJSON.put("metadata", (Object)meta);
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(request.getJSONObject("msg_header").get("request_url")));
        if (pagingUtil.getNextToken(scepCount) == null || pagingUtil.getPreviousToken() == null) {}
        if (scepCount != 0) {
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(scepCount);
            if (pagingJSON != null) {
                responseJSON.put("paging", (Object)pagingJSON);
            }
        }
        return responseJSON;
    }
    
    public JSONObject getSCEPConfiguration(final JSONObject request) throws Exception {
        final Long customerID = APIUtil.getCustomerID(request);
        final Long scepID = this.getSCEPIDFromRequest(request);
        final JSONObject scepJSON = ProfileCertificateUtil.getSCEPConfigDetail(scepID, customerID);
        if (scepJSON == null || scepJSON.length() == 0) {
            throw new APIHTTPException("COM0008", new Object[] { scepID });
        }
        return scepJSON;
    }
    
    public JSONObject addSCEPConfiguration(final JSONObject request) throws Exception {
        final String eventLogRemarks = "mdm.mgmt.certrepo.template_added";
        final int eventConstant = 72427;
        final Long customerId = APIUtil.getCustomerID(request);
        final Long userID = APIUtil.getUserID(request);
        final JSONObject msgBodyJSON = request.getJSONObject("msg_body");
        final String requestUrl = APIUtil.getRequestURL(request);
        final JSONObject scepJSON = JSONUtil.getInstance().changeJSONKeyCase(msgBodyJSON, 1);
        Long serverId = null;
        this.logger.log(Level.INFO, "going to add SCEP sevrer with details {0}", request);
        if (requestUrl.contains("/scep/servers")) {
            serverId = APIUtil.getResourceID(request, "server_id");
            final ScepServer scepServer = ScepServerManager.getScepServer(customerId, serverId);
            scepJSON.put("TYPE", scepServer.getServerType().type);
            scepJSON.put("URL", (Object)scepServer.getServerUrl());
            scepJSON.put("NAME", (Object)scepServer.getServerName());
            if (scepServer.getCertificate() != null) {
                scepJSON.put("CA_FINGER_PRINT", (Object)scepServer.getCertificate().getCertificateThumbprint());
            }
        }
        this.validateSCEPJSON(scepJSON);
        scepJSON.put("CUSTOMER_ID", (Object)customerId);
        if (ProfileCertificateUtil.checkSCEPConfigName(String.valueOf(scepJSON.get("SCEP_CONFIGURATION_NAME")), scepJSON.optLong("SCEP_CONFIG_ID", 0L))) {
            throw new APIHTTPException("SCEP0001", new Object[0]);
        }
        long scepId;
        try {
            SyMUtil.getUserTransaction().begin();
            scepId = ProfileCertificateUtil.addorUpdateSCEPConfiguration(scepJSON);
            if (scepJSON.optInt("TYPE", -1) == ScepServerType.ADCS.type) {
                this.addOrUpdateChallengeCredentials(scepId, scepJSON);
            }
            if (serverId == null) {
                serverId = ProfileCertificateUtil.getInstance().getServerIDFromURL(scepJSON.getString("URL"), scepJSON.getString("SCEP_CONFIGURATION_NAME"), scepJSON.optString("CA_FINGER_PRINT"), customerId);
            }
            ProfileCertificateUtil.getInstance().addServerToTemplateMapping(serverId, scepId);
            final JSONObject settings = msgBodyJSON.optJSONObject("renewal_settings");
            if (settings != null) {
                ProfileCertificateUtil.getInstance().addOrUpdateSCEPRenewalSettings(scepId, userID, settings);
            }
            SyMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            SyMUtil.getUserTransaction().rollback();
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final JSONObject resourceJSON = new JSONObject();
        resourceJSON.put("scepsetting_id", scepId);
        final JSONObject messageHeaderJSON = request.getJSONObject("msg_header");
        messageHeaderJSON.put("resource_identifier", (Object)resourceJSON);
        this.logger.log(Level.INFO, "SCEP template is added and template ID is {0}", scepId);
        final JSONObject jsonObject = ProfileCertificateUtil.getSCEPConfigDetail(scepId, customerId);
        final String arg = jsonObject.optString("SCEP_CONFIGURATION_NAME".toLowerCase());
        MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(APIUtil.getLoginID(request)), eventLogRemarks, (List<Object>)Collections.singletonList(arg), customerId, System.currentTimeMillis());
        return jsonObject;
    }
    
    public JSONObject modifySCEPConfiguration(final JSONObject request) throws Exception {
        String eventLogRemarks = "mdm.mgmt.certrepo.template_modified";
        final int eventConstant = 72428;
        final Long customerId = APIUtil.getCustomerID(request);
        final Long userID = APIUtil.getUserID(request);
        Long scepId = this.getSCEPIDFromRequest(request);
        final JSONObject scepDetails = ProfileCertificateUtil.getSCEPConfigDetail(scepId, customerId);
        if (scepDetails == null || scepDetails.length() == 0) {
            throw new APIHTTPException("COM0008", new Object[] { String.valueOf(scepId) });
        }
        if (!APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(request, false)) {
            throw new APIHTTPException("COM0015", new Object[0]);
        }
        final JSONObject msgBodyJSON = request.getJSONObject("msg_body");
        final JSONObject scepJSON = JSONUtil.getInstance().changeJSONKeyCase(msgBodyJSON, 1);
        scepJSON.put("CUSTOMER_ID", (Object)customerId);
        scepJSON.put("SCEP_CONFIG_ID", (Object)scepId);
        final String requestURL = APIUtil.getRequestURL(request);
        this.logger.log(Level.INFO, "going to update  SCEP sevrer with details {0}", request);
        if (requestURL.contains("/scep/servers")) {
            final long serverId = APIUtil.getResourceID(request, "server_id");
            final ScepServer scepServer = ScepServerManager.getScepServer(customerId, serverId);
            scepJSON.put("URL", (Object)scepServer.getServerUrl());
            scepJSON.put("NAME", (Object)scepServer.getServerName());
            scepJSON.put("TYPE", scepServer.getServerType().type);
            if (scepServer.getCertificate() != null) {
                scepJSON.put("CA_FINGER_PRINT", (Object)scepServer.getCertificate().getCertificateThumbprint());
            }
        }
        this.validateSCEPJSON(scepJSON);
        if (ProfileCertificateUtil.checkSCEPConfigName(String.valueOf(scepJSON.get("SCEP_CONFIGURATION_NAME")), scepId)) {
            throw new APIHTTPException("SCEP0001", new Object[0]);
        }
        try {
            SyMUtil.getUserTransaction().begin();
            scepId = ProfileCertificateUtil.addorUpdateSCEPConfiguration(scepJSON);
            if (scepJSON.optInt("TYPE", -1) == ScepServerType.ADCS.type) {
                this.addOrUpdateChallengeCredentials(scepId, scepJSON);
            }
            final JSONObject settings = msgBodyJSON.optJSONObject("renewal_settings");
            if (settings != null) {
                ProfileCertificateUtil.getInstance().addOrUpdateSCEPRenewalSettings(scepId, userID, settings);
            }
            SyMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            SyMUtil.getUserTransaction().rollback();
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final List<Long> modifiedList = new ArrayList<Long>();
        modifiedList.add(scepId);
        final Boolean isRedistribute = msgBodyJSON.optBoolean("redistribute_profiles", (boolean)Boolean.FALSE);
        new ProfilePayloadOperator(ProfileCertificateUtil.getInstance().certificatesMapptedTableList, ProfileCertificateUtil.getInstance().unConfigureMap).rePublishPayloadProfiles(modifiedList, customerId, APIUtil.getUserID(request), isRedistribute, Boolean.TRUE);
        if (isRedistribute) {
            eventLogRemarks += "_associated";
        }
        final JSONObject resourceJSON = new JSONObject();
        resourceJSON.put("scepsetting_id", (Object)scepId);
        final JSONObject jsonObject = this.getSCEPConfiguration(request);
        final String arg = jsonObject.optString("SCEP_CONFIGURATION_NAME".toLowerCase());
        MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(APIUtil.getLoginID(request)), eventLogRemarks, (List<Object>)Collections.singletonList(arg), customerId, System.currentTimeMillis());
        return jsonObject;
    }
    
    private void addOrUpdateChallengeCredentials(final long scepId, final JSONObject scepJSON) throws DataAccessException {
        if (scepJSON.getInt("CHALLENGE_TYPE") == 2) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ScepDyChallengeCredentials"));
            selectQuery.addSelectColumn(Column.getColumn("ScepDyChallengeCredentials", "*"));
            final Criteria criteria = new Criteria(new Column("ScepDyChallengeCredentials", "SCEP_CONFIG_ID"), (Object)scepId, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                final Row row = new Row("ScepDyChallengeCredentials");
                row.set("SCEP_CONFIG_ID", (Object)scepId);
                this.getScepDyChallengeCredentialsRow(scepJSON, row);
                dataObject.addRow(row);
            }
            else {
                final Row row = dataObject.getFirstRow("ScepDyChallengeCredentials");
                row.set("SCEP_CONFIG_ID", (Object)scepId);
                this.getScepDyChallengeCredentialsRow(scepJSON, row);
                dataObject.updateRow(row);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        else {
            final Criteria criteria2 = new Criteria(new Column("ScepDyChallengeCredentials", "SCEP_CONFIG_ID"), (Object)scepId, 0);
            SyMUtil.getPersistence().delete(criteria2);
        }
    }
    
    private void getScepDyChallengeCredentialsRow(final JSONObject scepJSON, final Row row) {
        final String domain = scepJSON.optString("domain".toUpperCase(), (String)null);
        String domainUserName;
        final String username = domainUserName = scepJSON.getString("SCEP_ADMIN_CHALLENGE_USERNAME");
        if (!MDMStringUtils.isEmpty(domain)) {
            domainUserName = domain + "\\" + username;
        }
        row.set("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL", (Object)scepJSON.getString("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL"));
        row.set("SCEP_ADMIN_CHALLENGE_USERNAME", (Object)domainUserName);
        row.set("SCEP_ADMIN_CHALLENGE_PASSWORD", (Object)scepJSON.getString("SCEP_ADMIN_CHALLENGE_PASSWORD"));
    }
    
    private Long getSCEPIDFromRequest(final JSONObject request) throws APIHTTPException {
        try {
            final String requestURL = APIUtil.getRequestURL(request);
            if (requestURL.contains("/scep/servers")) {
                return APIUtil.getResourceID(request, "template_id");
            }
            return APIUtil.getResourceID(request, "scepsetting_id");
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
    }
    
    private void validateSCEPJSON(final JSONObject requestJSON) {
        try {
            final Integer challengeType = requestJSON.getInt("CHALLENGE_TYPE");
            if (challengeType == 1) {
                final String challenge = requestJSON.optString("CHALLENGE");
                if (MDMStringUtils.isEmpty(challenge)) {
                    throw new APIHTTPException("COM0005", new Object[] { "CHALLENGE".toLowerCase() });
                }
            }
            else if (challengeType == 2 && requestJSON.getInt("TYPE") == ScepServerType.ADCS.type) {
                final String challengeUrl = requestJSON.optString("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL");
                final String challengeUsername = requestJSON.optString("SCEP_ADMIN_CHALLENGE_USERNAME");
                final String challengePassword = requestJSON.optString("SCEP_ADMIN_CHALLENGE_PASSWORD");
                if (MDMStringUtils.isEmpty(challengeUrl) || MDMStringUtils.isEmpty(challengeUsername) || MDMStringUtils.isEmpty(challengePassword)) {
                    throw new APIHTTPException("COM0005", new Object[] { "CHALLENGE".toLowerCase() });
                }
                if (!new URL(challengeUrl).getProtocol().equals("https")) {
                    throw new APIHTTPException("SCEP0002", new Object[0]);
                }
            }
            final Integer subAltType = requestJSON.getInt("SUBJECT_ALTNAME_TYPE");
            if (subAltType != 0) {
                final String subAltValue = requestJSON.optString("SUBJECT_ALTNAME_VALUE");
                if (MDMStringUtils.isEmpty(subAltValue)) {
                    throw new APIHTTPException("COM0005", new Object[] { "SUBJECT_ALTNAME_VALUE".toLowerCase() });
                }
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final MalformedURLException e2) {
            throw new APIHTTPException("COM0005", new Object[] { "Challenge url" });
        }
    }
    
    public JSONObject deleteSCEPTemplate(final JSONObject request) throws Exception {
        String eventLogRemarks = "mdm.mgmt.certrepo.template_deleted";
        final int eventConstant = 72429;
        this.logger.log(Level.INFO, "going to delete SCEP tempalte {0}", request);
        final JSONObject response = new JSONObject();
        final JSONObject requestJSON = request.getJSONObject("msg_body");
        final JSONArray scepSettingIds = requestJSON.getJSONArray("template_ids");
        final Long customerID = APIUtil.getCustomerID(request);
        final Long userID = APIUtil.getUserID(request);
        final Boolean isRedistribute = requestJSON.optBoolean("redistribute_profiles", (boolean)Boolean.FALSE);
        if (!APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(request, false)) {
            throw new APIHTTPException("COM0015", new Object[0]);
        }
        if (isRedistribute) {
            eventLogRemarks += "_associated";
        }
        ProfileCertificateUtil.getInstance().deleteCredentials(scepSettingIds, customerID, userID, isRedistribute);
        final Long serverID = APIUtil.getResourceID(request, "server_id");
        final List idList = new ArrayList();
        for (int i = 0; i < scepSettingIds.length(); ++i) {
            idList.add(scepSettingIds.get(i));
        }
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("SCEPServerToTemplate");
        deleteQuery.addJoin(new Join("SCEPServerToTemplate", "SCEPServers", new String[] { "SCEP_SERVER_ID" }, new String[] { "SERVER_ID" }, 2));
        final Criteria serverCriteria = new Criteria(Column.getColumn("SCEPServerToTemplate", "SCEP_SERVER_ID"), (Object)serverID, 0);
        final Criteria temlateCriteria = new Criteria(Column.getColumn("SCEPServerToTemplate", "SCEP_CONFIG_ID"), (Object)idList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("SCEPServers", "CUSTOMER_ID"), (Object)customerID, 0);
        deleteQuery.setCriteria(serverCriteria.and(temlateCriteria).and(customerCriteria));
        MDMUtil.getPersistenceLite().delete(deleteQuery);
        final String arg = ProfileCertificateUtil.getInstance().getTemplateNames(scepSettingIds, customerID);
        final List remarkList = new ArrayList();
        remarkList.add(arg);
        MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(APIUtil.getLoginID(request)), eventLogRemarks, remarkList, customerID, System.currentTimeMillis());
        this.logger.log(Level.INFO, "the scep server templates were deleted");
        response.put("template_ids", (Object)scepSettingIds);
        return response;
    }
}
