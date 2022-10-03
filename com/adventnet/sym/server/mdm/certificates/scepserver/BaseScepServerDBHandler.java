package com.adventnet.sym.server.mdm.certificates.scepserver;

import com.adventnet.ds.query.UpdateQuery;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseScepServerDBHandler
{
    private static final Logger LOGGER;
    
    private BaseScepServerDBHandler() {
    }
    
    public static void addScepServer(final ScepServer scepServer) throws DataAccessException {
        BaseScepServerDBHandler.LOGGER.log(Level.INFO, "ScepServerDBHandler: Adding SCEP server for customer {0}", new Object[] { scepServer.getCustomerId() });
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        final Row row = new Row("SCEPServers");
        row.set("SERVER_NAME", (Object)scepServer.getServerName());
        row.set("URL", (Object)scepServer.getServerUrl());
        if (scepServer.getCertificate() != null) {
            BaseScepServerDBHandler.LOGGER.log(Level.INFO, "ScepServerDBHandler: CA certificate uploaded: Certificate id: {0}", new Object[] { scepServer.getCertificate().getCertificateId() });
            row.set("CA_FINGER_PRINT", (Object)scepServer.getCertificate().getCertificateThumbprint());
            row.set("CA_CERTIFICATE_ID", (Object)scepServer.getCertificate().getCertificateId());
        }
        row.set("TYPE", (Object)scepServer.getServerType().type);
        row.set("CUSTOMER_ID", (Object)scepServer.getCustomerId());
        dataObject.addRow(row);
        SyMUtil.getPersistence().add(dataObject);
        final long scepServerId = (long)dataObject.getRow("SCEPServers").get("SERVER_ID");
        scepServer.setScepServerId(scepServerId);
        BaseScepServerDBHandler.LOGGER.log(Level.INFO, "ScepServerDBHandler: SCEP server details added for customer {0}, {1}", new Object[] { scepServer.getCustomerId(), scepServerId });
    }
    
    public static void updateScepServer(final ScepServer scepServer) throws DataAccessException {
        BaseScepServerDBHandler.LOGGER.log(Level.INFO, "ScepServerDBHandler: Updating SCEP server for customer {0}, Server {1}", new Object[] { scepServer.getCustomerId(), scepServer.getScepServerId() });
        final String serverName = scepServer.getServerName();
        final String serverUrl = scepServer.getServerUrl();
        final CredentialCertificate certificate = scepServer.getCertificate();
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("SCEPServers");
        updateQuery.setUpdateColumn("SERVER_NAME", (Object)serverName);
        updateQuery.setUpdateColumn("URL", (Object)serverUrl);
        if (certificate != null) {
            BaseScepServerDBHandler.LOGGER.log(Level.INFO, "ScepServerDBHandler: Updating CA certificate for server {0}, CA cert {1}", new Object[] { scepServer.getScepServerId(), scepServer.getCertificate().getCertificateId() });
            updateQuery.setUpdateColumn("CA_FINGER_PRINT", (Object)certificate.getCertificateThumbprint());
            updateQuery.setUpdateColumn("CA_CERTIFICATE_ID", (Object)certificate.getCertificateId());
        }
        else {
            updateQuery.setUpdateColumn("CA_FINGER_PRINT", (Object)null);
            updateQuery.setUpdateColumn("CA_CERTIFICATE_ID", (Object)null);
        }
        final Criteria customerIdCriteria = new Criteria(Column.getColumn("SCEPServers", "CUSTOMER_ID"), (Object)scepServer.getCustomerId(), 0);
        final Criteria serverIdCriteria = new Criteria(Column.getColumn("SCEPServers", "SERVER_ID"), (Object)scepServer.getScepServerId(), 0);
        updateQuery.setCriteria(customerIdCriteria.and(serverIdCriteria));
        SyMUtil.getPersistence().update(updateQuery);
        BaseScepServerDBHandler.LOGGER.log(Level.INFO, "ScepServerDBHandler: Scep server updated: {0}", new Object[] { scepServer.getScepServerId() });
    }
    
    public static void deleteScepServer(final long customerId, final long serverId) throws DataAccessException {
        BaseScepServerDBHandler.LOGGER.log(Level.INFO, "ScepServerDBHandler: Deleting Scep server: {0} for customer: {1}", new Object[] { serverId, customerId });
        final Criteria customerCriteria = new Criteria(new Column("SCEPServers", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria serverCriteria = new Criteria(Column.getColumn("SCEPServers", "SERVER_ID"), (Object)serverId, 0);
        SyMUtil.getPersistenceLite().delete(serverCriteria.and(customerCriteria));
    }
    
    public static DataObject getScepServers(final long customerId, Criteria criteria) throws DataAccessException {
        final Criteria customerCriteria = new Criteria(new Column("SCEPServers", "CUSTOMER_ID"), (Object)customerId, 0);
        criteria = ((criteria != null) ? criteria.and(customerCriteria) : customerCriteria);
        return SyMUtil.getPersistenceLite().get("SCEPServers", criteria);
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
