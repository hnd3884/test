package com.adventnet.sym.server.mdm.certificates.scepserver;

import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.logging.Logger;

public class ScepCertTemplateDB
{
    private static final Logger LOGGER;
    
    public static List<Long> getTemplatesMappedToServer(final Long serverID, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SCEPServers"));
        selectQuery.addJoin(new Join("SCEPServers", "SCEPServerToTemplate", new String[] { "SERVER_ID" }, new String[] { "SCEP_SERVER_ID" }, 2));
        selectQuery.addJoin(new Join("SCEPServerToTemplate", "Certificates", new String[] { "SCEP_CONFIG_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("SCEPServerToTemplate", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("SCEPServers", "SERVER_ID"), (Object)serverID, 0).and(new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final List<Long> tempateList = new ArrayList<Long>();
        final Iterator iterator = dataObject.getRows("SCEPServerToTemplate");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            tempateList.add((Long)row.get("SCEP_CONFIG_ID"));
        }
        return tempateList;
    }
    
    public static void updateTemplatesInDB(final List<Long> templates, final ScepServer scepServer) throws DataAccessException {
        ScepCertTemplateDB.LOGGER.log(Level.INFO, "ScepServerHandler: Updating Certificate templates (SCEP configurations) belonging to server {0}: {1}", new Object[] { scepServer.getScepServerId(), templates });
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("SCEPConfigurations");
        updateQuery.setUpdateColumn("URL", (Object)scepServer.getServerUrl());
        updateQuery.setUpdateColumn("NAME", (Object)scepServer.getServerName());
        if (scepServer.getCertificate() != null) {
            updateQuery.setUpdateColumn("CA_FINGER_PRINT", (Object)scepServer.getCertificate().getCertificateThumbprint());
        }
        else {
            updateQuery.setUpdateColumn("CA_FINGER_PRINT", (Object)null);
        }
        final Criteria templatesCriteria = new Criteria(Column.getColumn("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)templates.toArray(), 8);
        updateQuery.setCriteria(templatesCriteria);
        SyMUtil.getPersistenceLite().update(updateQuery);
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
