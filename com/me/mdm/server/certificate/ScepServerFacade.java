package com.me.mdm.server.certificate;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerHandler;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerHandlerFactory;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepCertTemplateDB;
import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import javax.transaction.SystemException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerMapper;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerManager;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ScepServerFacade
{
    private final Logger logger;
    
    public ScepServerFacade() {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
    
    public JSONObject addScepServer(final long customerId, final long loginId, final JSONObject json) throws DataAccessException, SystemException {
        this.logger.log(Level.INFO, "Adding Scep server");
        final ScepServer scepServer = this.validateDataAndConstructScepServer(customerId, json, false);
        ScepServerManager.addScepServer(scepServer, loginId);
        final ScepServer scepServer2 = ScepServerManager.getScepServer(customerId, scepServer.getScepServerId());
        this.logger.log(Level.INFO, "Scep server addition completed: {0}", new Object[] { scepServer2.getScepServerId() });
        return ScepServerMapper.toJson(scepServer2);
    }
    
    public JSONObject modifyScepServer(final long customerId, final long userId, final long loginId, final long scepServerId, final JSONObject json) throws DataAccessException, SystemException {
        this.logger.log(Level.INFO, "Modifying Scep server: {0}", new Object[] { scepServerId });
        this.authorizeUser();
        final ScepServer scepServer = this.validateDataAndConstructScepServer(customerId, json, true);
        scepServer.setScepServerId(scepServerId);
        ScepServerManager.modifyScepServer(scepServer, this.isProfileRedistributionOpted(json), this.isProfileRedistributionNeeded(scepServer), userId, loginId);
        this.logger.log(Level.INFO, "Scep server modification completed: {0}", new Object[] { scepServerId });
        return new JSONObject().put("SERVER_ID", scepServerId);
    }
    
    public JSONObject deleteScepServer(final long customerId, final long userId, final long loginId, final long scepServerId, final JSONObject json) throws DataAccessException, SystemException {
        this.logger.log(Level.INFO, "Deleting Scep server: {0}", new Object[] { scepServerId });
        this.authorizeUser();
        final ScepServer scepServer = ScepServerManager.getScepServer(customerId, scepServerId);
        ScepServerManager.deleteScepServer(scepServer, this.isProfileRedistributionOpted(json), userId, loginId);
        this.logger.log(Level.INFO, "Scep server deletion completed: {0}", new Object[] { scepServerId });
        return new JSONObject().put("status", (Object)Boolean.TRUE);
    }
    
    public JSONObject getScepServer(final long customerID, final long serverID) throws DataAccessException {
        this.logger.log(Level.INFO, "Retrieving Scep server: {0}", new Object[] { serverID });
        final ScepServer scepServer = ScepServerManager.getScepServer(customerID, serverID);
        this.logger.log(Level.INFO, "Scep server: {0} retrieved", new Object[] { serverID });
        return ScepServerMapper.toJson(scepServer);
    }
    
    public JSONObject getScepServers(final long customerId, final JSONObject request) throws DataAccessException {
        final String search = APIUtil.getStringFilter(request, "search");
        final String type = APIUtil.getStringFilter(request, "type");
        final Criteria overAllCriteria = this.constructCriteriaForSearchAndTypeFilters(search, type);
        final List<ScepServer> scepServers = ScepServerManager.getScepServers(customerId, overAllCriteria);
        final JSONArray scepServersArray = new JSONArray();
        for (final ScepServer scepServer : scepServers) {
            this.logger.log(Level.INFO, "Scep server: {0}", new Object[] { scepServer.getScepServerId() });
            final JSONObject scepServerJson = ScepServerMapper.toJson(scepServer);
            final List<Long> templates = ScepCertTemplateDB.getTemplatesMappedToServer(scepServer.getScepServerId(), scepServer.getCustomerId());
            scepServerJson.put("template_count", templates.size());
            this.logger.log(Level.INFO, "No of templates configured for this server: {0}, size: {1}", new Object[] { scepServer.getScepServerId(), templates.size() });
            scepServersArray.put((Object)scepServerJson);
        }
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("scep_servers", (Object)scepServersArray);
        return responseJSON;
    }
    
    private Criteria constructCriteriaForSearchAndTypeFilters(final String search, final String type) {
        this.logger.log(Level.INFO, "Retrieving Scep server: Search - {0}, Type - {1}", new Object[] { search, type });
        Criteria overAllCriteria = null;
        if (!MDMStringUtils.isEmpty(type)) {
            final String[] split;
            final String[] types = split = type.split(",");
            for (final String t : split) {
                final ScepServerType scepServerType = ScepServerUtil.getServerType(Integer.parseInt(t));
                if (overAllCriteria != null) {
                    overAllCriteria = overAllCriteria.or(new Criteria(new Column("SCEPServers", "TYPE"), (Object)scepServerType.type, 0));
                }
                else {
                    overAllCriteria = new Criteria(new Column("SCEPServers", "TYPE"), (Object)scepServerType.type, 0);
                }
            }
        }
        if (!MDMStringUtils.isEmpty(search)) {
            final Criteria searchCriteria = new Criteria(new Column("SCEPServers", "SERVER_NAME"), (Object)search, 12, false);
            overAllCriteria = ((overAllCriteria != null) ? overAllCriteria.and(searchCriteria) : searchCriteria);
        }
        return overAllCriteria;
    }
    
    private boolean isProfileRedistributionOpted(final JSONObject json) {
        final JSONObject msgBody = json.getJSONObject("msg_body");
        return msgBody.optBoolean("redistribute_profiles", (boolean)Boolean.FALSE);
    }
    
    private boolean isProfileRedistributionNeeded(final ScepServer scepServer) throws DataAccessException {
        boolean isProfileRedistributionNeeded = false;
        final ScepServer existingScepServer = ScepServerManager.getScepServer(scepServer.getCustomerId(), scepServer.getScepServerId());
        if (!existingScepServer.getServerUrl().equals(scepServer.getServerUrl()) || !existingScepServer.getServerName().equals(scepServer.getServerName())) {
            isProfileRedistributionNeeded = true;
        }
        if (scepServer.getCertificate() == null && existingScepServer.getCertificate() != null) {
            isProfileRedistributionNeeded = true;
        }
        else if (scepServer.getCertificate() != null && existingScepServer.getCertificate() == null) {
            isProfileRedistributionNeeded = true;
        }
        else if (scepServer.getCertificate() != null && existingScepServer.getCertificate() != null && !scepServer.getCertificate().getCertificateId().equals(existingScepServer.getCertificate().getCertificateId())) {
            isProfileRedistributionNeeded = true;
        }
        return isProfileRedistributionNeeded;
    }
    
    private void authorizeUser() {
        final String[] rolesRequiredToPerformThisAction = { "All_Managed_Mobile_Devices" };
        if (!APIUtil.getNewInstance().checkRolesForCurrentUser(rolesRequiredToPerformThisAction)) {
            this.logger.log(Level.INFO, "Current user is not allowed to perform the action");
            throw new APIHTTPException("COM0015", new Object[0]);
        }
    }
    
    private ScepServer validateDataAndConstructScepServer(final long customerId, final JSONObject json, final boolean isModify) throws DataAccessException {
        this.validateIfRequiredDataAreProvided(customerId, json.getJSONObject("msg_body"));
        this.checkIfServerWithSameUrlAlreadyExists(customerId, json, isModify);
        final ScepServer scepServer = ScepServerMapper.convertJson(customerId, json.getJSONObject("msg_body"));
        if (isModify) {
            final Long scepServerId = APIUtil.getResourceID(json, "server_id");
            this.rejectIfServerTypeChanged(customerId, scepServer, scepServerId);
        }
        return scepServer;
    }
    
    private void validateIfRequiredDataAreProvided(final long customerId, final JSONObject msgBody) {
        final ScepServerType scepServerType = ScepServerUtil.getServerType(msgBody.getInt("type"));
        if (scepServerType != ScepServerType.GENERIC && scepServerType != ScepServerType.ADCS && scepServerType != ScepServerType.EJBCA) {
            this.logger.log(Level.INFO, "Scep server type: {0}", new Object[] { scepServerType.type });
            final ScepServerHandler scepServerHandler = ScepServerHandlerFactory.getScepServerHandler(scepServerType);
            scepServerHandler.validateRelatedServerDetail(customerId, msgBody);
        }
    }
    
    private void checkIfServerWithSameUrlAlreadyExists(final long customerId, final JSONObject json, final boolean isModify) throws DataAccessException {
        this.logger.log(Level.INFO, "ScepServerFacade: Checking if server with same URL already exists for customer {0}, isModify: {1}", new Object[] { customerId, isModify });
        final Criteria criteria = isModify ? this.getServerModificationCriteria(customerId, json) : this.getServerAdditionCriteria(customerId, json);
        final DataObject dataObject = DataAccess.get("SCEPServers", criteria);
        if (!dataObject.isEmpty()) {
            this.logger.log(Level.INFO, "ScepServerFacade: Same Server URL already exists for customer {0}", new Object[] { customerId });
            throw new APIHTTPException("CERTREPO001", new Object[0]);
        }
    }
    
    private void rejectIfServerTypeChanged(final long customerId, final ScepServer scepServer, final Long scepServerId) throws DataAccessException {
        final ScepServer existingScepServer = ScepServerManager.getScepServer(customerId, scepServerId);
        if (existingScepServer.getServerType().type != scepServer.getServerType().type) {
            throw new APIHTTPException("COM0005", new Object[] { "Type" });
        }
    }
    
    private Criteria getServerAdditionCriteria(final long customerId, final JSONObject json) {
        final JSONObject msgBody = json.getJSONObject("msg_body");
        final String scepServerUrl = msgBody.getString("url");
        final ScepServerType scepServerType = ScepServerUtil.getServerType(msgBody.getInt("type"));
        final Criteria serverUrlCriteria = new Criteria(new Column("SCEPServers", "URL"), (Object)scepServerUrl, 0);
        final Criteria serverTypeCriteria = new Criteria(new Column("SCEPServers", "TYPE"), (Object)scepServerType.type, 0);
        final Criteria customerIdCriteria = new Criteria(new Column("SCEPServers", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria overAllCriteria = customerIdCriteria.and(serverUrlCriteria).and(serverTypeCriteria);
        return overAllCriteria;
    }
    
    private Criteria getServerModificationCriteria(final Long customerId, final JSONObject json) {
        final Criteria criteria = this.getServerAdditionCriteria(customerId, json);
        final long scepServerId = APIUtil.getResourceID(json, "server_id");
        final Criteria serverIdNotEqualCriteria = new Criteria(new Column("SCEPServers", "SERVER_ID"), (Object)scepServerId, 1);
        return criteria.and(serverIdNotEqualCriteria);
    }
}
