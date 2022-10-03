package com.adventnet.sym.server.mdm.certificates.scep;

import com.adventnet.ds.query.Join;
import java.util.List;
import com.me.mdm.server.profiles.ProfilePayloadMapping;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.HashSet;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.Set;
import java.util.logging.Logger;

public class IdentityCertificateDBHandler
{
    private static final Logger LOGGER;
    
    private IdentityCertificateDBHandler() {
    }
    
    public static Set<Long> getIdentityCertsBelongingToCollection(final Long collectionId) throws DataAccessException {
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Getting identity certificates belonging to collection: {0}", new Object[] { collectionId });
        final SelectQuery identityCertQuery = constructIdentityCertsForCollectionQuery();
        identityCertQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject dataObject = SyMUtil.getPersistence().get(identityCertQuery);
        return getIdenityCertIdsFromVariousTables(dataObject);
    }
    
    public static Set<Long> filterScepIds(final Set<Long> identityCertIds) throws DataAccessException {
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Filtering scep ids: {0}", new Object[] { identityCertIds });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Certificates"));
        final Criteria isActiveCriteria = new Criteria(new Column("Certificates", "IS_ACTIVE"), (Object)true, 0);
        final Criteria scepTypeCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)1, 0);
        final Criteria identityCertIdsCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)identityCertIds.toArray(), 8);
        selectQuery.setCriteria(isActiveCriteria.and(scepTypeCriteria).and(identityCertIdsCriteria));
        selectQuery.addSelectColumn(new Column("Certificates", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        final Set<Long> scepIds = new HashSet<Long>();
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("Certificates");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long scepId = (Long)row.get("CERTIFICATE_RESOURCE_ID");
                scepIds.add(scepId);
            }
        }
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Filtered scep ids: {0}", new Object[] { identityCertIds });
        return scepIds;
    }
    
    private static Set<Long> getIdenityCertIdsFromVariousTables(final DataObject dataObject) throws DataAccessException {
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Getting identity certificate ids from various tables");
        final Set<Long> identityCertIds = new HashSet<Long>();
        final List<ProfilePayloadMapping> tableToIdentityCertColumnMappings = new ProfileCertificateUtil().getCertificateMap();
        if (!dataObject.isEmpty()) {
            for (final ProfilePayloadMapping tableToIdentityCertColumnMapping : tableToIdentityCertColumnMappings) {
                final String table = tableToIdentityCertColumnMapping.getTableName();
                final Iterator rows = dataObject.getRows(table);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    if (row != null && row.get(tableToIdentityCertColumnMapping.getColumnName(table)) != null) {
                        final Long identityCertId = (Long)row.get(tableToIdentityCertColumnMapping.getColumnName(table));
                        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Getting identity certificate id from table: {0}, identity cert id: {1}", new Object[] { table, identityCertId });
                        identityCertIds.add(identityCertId);
                    }
                }
            }
        }
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Finished getting identity certificate ids");
        return identityCertIds;
    }
    
    private static SelectQuery constructIdentityCertsForCollectionQuery() {
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Constructing select query with tables containing identity certificate columns");
        final SelectQuery identityCertsForCollectionQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        identityCertsForCollectionQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        identityCertsForCollectionQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
        identityCertsForCollectionQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
        joinTablesWithIdentityCertColumns(identityCertsForCollectionQuery);
        identityCertsForCollectionQuery.addSelectColumn(new Column((String)null, "*"));
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Successfully constructed select query with tables containing identity certificate columns");
        return identityCertsForCollectionQuery;
    }
    
    private static void joinTablesWithIdentityCertColumns(final SelectQuery identityCertsForCollectionQuery) {
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Joining tables containing identity certificate columns");
        final List<ProfilePayloadMapping> tableToIdentityCertColumnMappings = new ProfileCertificateUtil().getCertificateMap();
        for (final ProfilePayloadMapping tableToIdentityCertColumnMapping : tableToIdentityCertColumnMappings) {
            final String table = tableToIdentityCertColumnMapping.getTableName();
            if (!identityCertsForCollectionQuery.getTableList().contains(Table.getTable(table))) {
                tableToIdentityCertColumnMapping.addCfgDataItemJoin(identityCertsForCollectionQuery, 1);
            }
        }
        IdentityCertificateDBHandler.LOGGER.log(Level.INFO, "Successfully joined tables containing identity certificate columns");
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
