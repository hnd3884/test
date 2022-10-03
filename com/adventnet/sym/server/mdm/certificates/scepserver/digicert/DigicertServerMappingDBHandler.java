package com.adventnet.sym.server.mdm.certificates.scepserver.digicert;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigicertServerMappingDBHandler
{
    private static final Logger LOGGER;
    
    public static DigicertServerMapping getDigicertServerMappingDetails(final long serverId) throws DataAccessException {
        DigicertServerMappingDBHandler.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Getting Digicert server mapping: {0}", new Object[] { serverId });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DigiCertServersMapping"));
        final Criteria serverCriteria = new Criteria(new Column("DigiCertServersMapping", "SERVER_ID"), (Object)serverId, 0);
        selectQuery.setCriteria(serverCriteria);
        selectQuery.addSelectColumn(new Column("DigiCertServersMapping", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row digicertRow = dataObject.getFirstRow("DigiCertServersMapping");
            final long raCertId = (long)digicertRow.get("RA_CERT_ID");
            final long templateId = (long)digicertRow.get("TEMPLATE_ID");
            return new DigicertServerMapping(serverId, raCertId, templateId);
        }
        throw new APIHTTPException("COM0005", new Object[] { "Server Id" });
    }
    
    public static void addDigicertServerMapping(final long scepServerId, final long raCertificateId, final long templateId) throws DataAccessException {
        DigicertServerMappingDBHandler.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Adding Digicert server mapping: RA Certificate id: {0}, Template id: {1} for server: {2}", new Object[] { raCertificateId, templateId, scepServerId });
        final DataObject dataObject = SyMUtil.getPersistenceLite().constructDataObject();
        final Row row = new Row("DigiCertServersMapping");
        row.set("SERVER_ID", (Object)scepServerId);
        row.set("RA_CERT_ID", (Object)raCertificateId);
        row.set("TEMPLATE_ID", (Object)templateId);
        dataObject.addRow(row);
        SyMUtil.getPersistence().add(dataObject);
    }
    
    public static void updateDigicertServerMapping(final long serverId, final long raCertificateId, final long templateId) throws DataAccessException {
        DigicertServerMappingDBHandler.LOGGER.log(Level.INFO, "DigicertScepServerHandler: Modifying Digicert server mapping: RA Certificate id: {0}, Template id: {1} for server: {2}", new Object[] { raCertificateId, templateId, serverId });
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DigiCertServersMapping");
        final Criteria serverIdCriteria = new Criteria(new Column("DigiCertServersMapping", "SERVER_ID"), (Object)serverId, 0);
        updateQuery.setCriteria(serverIdCriteria);
        updateQuery.setUpdateColumn("RA_CERT_ID", (Object)raCertificateId);
        updateQuery.setUpdateColumn("TEMPLATE_ID", (Object)templateId);
        SyMUtil.getPersistence().update(updateQuery);
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
