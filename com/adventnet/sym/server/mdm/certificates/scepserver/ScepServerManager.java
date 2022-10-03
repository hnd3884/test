package com.adventnet.sym.server.mdm.certificates.scepserver;

import java.util.Collections;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import javax.transaction.SystemException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class ScepServerManager
{
    private static final Logger LOGGER;
    
    public static void addScepServer(final ScepServer scepServer, final long loginId) throws SystemException {
        try {
            SyMUtil.getUserTransaction().begin();
            ScepServerManager.LOGGER.log(Level.INFO, "Beginning the addition of scep server: type: {0}", new Object[] { scepServer.getServerType().type });
            BaseScepServerDBHandler.addScepServer(scepServer);
            if (scepServer.getServerType() != ScepServerType.GENERIC && scepServer.getServerType() != ScepServerType.ADCS && scepServer.getServerType() != ScepServerType.EJBCA) {
                ScepServerManager.LOGGER.log(Level.INFO, "Adding related scep server details");
                final ScepServerHandler scepServerHandler = ScepServerHandlerFactory.getScepServerHandler(scepServer.getServerType());
                scepServerHandler.addRelatedServerDetail(scepServer);
            }
            SyMUtil.getUserTransaction().commit();
            ScepServerManager.LOGGER.log(Level.INFO, "Scep server addition completed: server id: {1}", new Object[] { scepServer.getScepServerId() });
            auditScepServerEvent(loginId, scepServer.getCustomerId(), scepServer.getServerName(), 72427, "mdm.mgmt.certrepo.server_added");
        }
        catch (final Exception e) {
            final String eMessage = "ScepServerHandler: Exception while adding SCEP server details, Server type: " + scepServer.getServerType().type;
            ScepServerManager.LOGGER.log(Level.SEVERE, eMessage, e);
            SyMUtil.getUserTransaction().rollback();
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static void modifyScepServer(final ScepServer scepServer, final boolean isProfileRedistributionOpted, boolean isProfileRedistributionNeeded, final long userId, final long loginId) throws SystemException {
        try {
            SyMUtil.getUserTransaction().begin();
            ScepServerManager.LOGGER.log(Level.INFO, "Beginning the modification of scep server: {0}", new Object[] { scepServer.getScepServerId() });
            BaseScepServerDBHandler.updateScepServer(scepServer);
            if (scepServer.getServerType() != ScepServerType.GENERIC && scepServer.getServerType() != ScepServerType.ADCS && scepServer.getServerType() != ScepServerType.EJBCA) {
                ScepServerManager.LOGGER.log(Level.INFO, "Modifying related scep server details: {0}, type: {1}", new Object[] { scepServer.getScepServerId(), scepServer.getServerType().type });
                final ScepServerHandler scepServerHandler = ScepServerHandlerFactory.getScepServerHandler(scepServer.getServerType());
                final boolean isProfileRedistributionNeededForRelatedServer = scepServerHandler.modifyRelatedServerDetail(scepServer.getScepServerId(), scepServer.getCustomerId(), scepServer);
                isProfileRedistributionNeeded = (isProfileRedistributionNeeded || isProfileRedistributionNeededForRelatedServer);
            }
            boolean isProfileRedistributionDone = false;
            if (isProfileRedistributionNeeded || isProfileRedistributionOpted) {
                ScepServerManager.LOGGER.log(Level.INFO, "Modifying certificate templates: Server id: {0}, isProfileRedistributionNeeded: {1} isProfileRedistributionOpted: {2}", new Object[] { scepServer.getScepServerId(), isProfileRedistributionNeeded, isProfileRedistributionOpted });
                isProfileRedistributionDone = ScepCertTemplate.modifyCertificateTemplates(scepServer, isProfileRedistributionOpted, userId, loginId);
            }
            SyMUtil.getUserTransaction().commit();
            ScepServerManager.LOGGER.log(Level.INFO, "Scep server modification completed: Server id: {0}", new Object[] { scepServer.getScepServerId() });
            final String remark = isProfileRedistributionDone ? "mdm.mgmt.certrepo.server_modified_associated" : "mdm.mgmt.certrepo.server_modified";
            auditScepServerEvent(loginId, scepServer.getCustomerId(), scepServer.getServerName(), 72428, remark);
        }
        catch (final Exception e) {
            final String eMessage = "ScepServerHandler: Exception while modifying SCEP server details to DB: " + scepServer.getScepServerId();
            ScepServerManager.LOGGER.log(Level.SEVERE, eMessage, e);
            SyMUtil.getUserTransaction().rollback();
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static void deleteScepServer(final ScepServer scepServer, final boolean isProfileRedistributionOpted, final long userId, final long loginId) throws SystemException {
        try {
            SyMUtil.getUserTransaction().begin();
            ScepServerManager.LOGGER.log(Level.INFO, "Beginning the deletion of scep server: {0}", new Object[] { scepServer.getScepServerId() });
            final boolean isProfileRedistributionDone = ScepCertTemplate.trashCertificateTemplates(scepServer, isProfileRedistributionOpted, userId, loginId);
            if (scepServer.getServerType() != ScepServerType.GENERIC && scepServer.getServerType() != ScepServerType.ADCS && scepServer.getServerType() != ScepServerType.EJBCA) {
                ScepServerManager.LOGGER.log(Level.INFO, "Deleting related scep server details: {0}, type: {1}", new Object[] { scepServer.getScepServerId(), scepServer.getServerType().type });
                final ScepServerHandler scepServerHandler = ScepServerHandlerFactory.getScepServerHandler(scepServer.getServerType());
                scepServerHandler.deleteRelatedServerDetail(scepServer.getScepServerId());
            }
            BaseScepServerDBHandler.deleteScepServer(scepServer.getCustomerId(), scepServer.getScepServerId());
            SyMUtil.getUserTransaction().commit();
            ScepServerManager.LOGGER.log(Level.INFO, "Scep server deletion completed: Server id: {0}", new Object[] { scepServer.getScepServerId() });
            final String remark = isProfileRedistributionDone ? "mdm.mgmt.certrepo.server_deleted_associated" : "mdm.mgmt.certrepo.server_deleted";
            auditScepServerEvent(loginId, scepServer.getCustomerId(), scepServer.getServerName(), 72429, remark);
        }
        catch (final Exception e) {
            final String eMessage = "ScepServerHandler: Exception while deleting SCEP server details: " + scepServer.getScepServerId();
            ScepServerManager.LOGGER.log(Level.SEVERE, eMessage, e);
            SyMUtil.getUserTransaction().rollback();
        }
    }
    
    public static ScepServer getScepServer(final Long customerId, final Long serverID) throws DataAccessException {
        ScepServerManager.LOGGER.log(Level.INFO, "ScepServerHandler: Getting SCEP server details for customer: {0}, server: {1}", new Object[] { customerId, serverID });
        final Criteria scepServerIdCriteria = new Criteria(Column.getColumn("SCEPServers", "SERVER_ID"), (Object)serverID, 0);
        final List<ScepServer> scepServers = getScepServers(customerId, scepServerIdCriteria);
        if (scepServers.isEmpty()) {
            ScepServerManager.LOGGER.log(Level.INFO, "ScepServerHandler: SCEP server details not present for server: {0}", new Object[] { serverID });
            throw new APIHTTPException("COM0008", new Object[] { "Server id" });
        }
        return scepServers.get(0);
    }
    
    public static List<ScepServer> getScepServers(final Long customerId, final Criteria criteria) throws DataAccessException {
        ScepServerManager.LOGGER.log(Level.INFO, "ScepServerHandler: Getting SCEP server details for customer: {0}", new Object[] { customerId });
        final List<ScepServer> scepServers = new ArrayList<ScepServer>();
        final DataObject dataObject = BaseScepServerDBHandler.getScepServers(customerId, criteria);
        final Iterator iterator = dataObject.getRows("SCEPServers");
        while (iterator.hasNext()) {
            final Row scepServerRow = iterator.next();
            final long serverId = (long)scepServerRow.get("SERVER_ID");
            final int serverType = (int)scepServerRow.get("TYPE");
            final String serverUrl = (String)scepServerRow.get("URL");
            final String serverName = (String)scepServerRow.get("SERVER_NAME");
            ScepServerManager.LOGGER.log(Level.INFO, "ScepServerHandler: Getting Scep server details for server: Server Id- {0}", new Object[] { serverId });
            CredentialCertificate certificate = null;
            if (scepServerRow.get("CA_CERTIFICATE_ID") != null) {
                ScepServerManager.LOGGER.log(Level.INFO, "ScepServerHandler: CA certificate present for Scep server: {0}", new Object[] { serverId });
                final Long caCertificateId = (Long)scepServerRow.get("CA_CERTIFICATE_ID");
                certificate = ProfileCertificateUtil.getCACertDetails(customerId, caCertificateId);
            }
            final ScepServerType scepServerType = ScepServerUtil.getServerType(serverType);
            ScepServer scepServer;
            if (scepServerType != ScepServerType.GENERIC && scepServerType != ScepServerType.ADCS && scepServerType != ScepServerType.EJBCA) {
                final ScepServerHandler scepServerHandler = ScepServerHandlerFactory.getScepServerHandler(scepServerType);
                scepServer = scepServerHandler.getRelatedServerDetail(serverId);
            }
            else {
                scepServer = new ScepServer();
            }
            scepServer.setScepServerId(serverId);
            scepServer.setCustomerId(customerId);
            scepServer.setServerType(scepServerType);
            scepServer.setServerName(serverName);
            scepServer.setServerUrl(serverUrl);
            if (certificate != null) {
                scepServer.setCertificate(certificate);
            }
            scepServers.add(scepServer);
        }
        ScepServerManager.LOGGER.log(Level.INFO, "ScepServerHandler: Number of SCEP servers: {0}", new Object[] { scepServers.size() });
        return scepServers;
    }
    
    private static void auditScepServerEvent(final long loginId, final long customerId, final String serverName, final int eventId, final String remark) {
        ScepServerManager.LOGGER.log(Level.INFO, "ScepServerFacade: Auditing Scep server event for customer: {0}, Event id: {1}, Remark: {2}", new Object[] { customerId, eventId, remark });
        MDMEventLogHandler.getInstance().addEvent(eventId, DMUserHandler.getDCUser(Long.valueOf(loginId)), remark, (List<Object>)Collections.singletonList(serverName), customerId, System.currentTimeMillis());
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
