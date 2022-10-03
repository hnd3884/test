package com.me.idps.core.crud;

import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.List;
import com.adventnet.ds.query.SortColumn;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class DomainDataProvider
{
    private static int isDmDomainSyncTDPresent;
    
    static DataObject getDomainDOFromDB(final Row domainRow) throws DataAccessException {
        Criteria domainNameCri = new Criteria(Column.getColumn("DMDomain", "NAME"), domainRow.get("NAME"), 0, false);
        domainNameCri = domainNameCri.and(Column.getColumn("DMDomain", "CUSTOMER_ID"), domainRow.get("CUSTOMER_ID"), 0);
        final Criteria networkCri = new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), domainRow.get("CLIENT_ID"), 0);
        final DataObject domainDO = SyMUtil.getPersistence().get("DMDomain", domainNameCri.and(networkCri));
        return domainDO;
    }
    
    static DataObject getNetworkDOFromDB(final Criteria networkCri) throws DataAccessException {
        final DataObject networkDO = SyMUtil.getPersistence().get("NetworkClient", networkCri);
        return networkDO;
    }
    
    private static DataObject getNetworkDomainDO(final Criteria criteria) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
        query.addSelectColumn(new Column((String)null, "*"));
        query.addJoin(new Join("DMDomain", "NetworkClient", new String[] { "CLIENT_ID" }, new String[] { "CLIENT_ID" }, 2));
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return SyMUtil.getPersistence().get(query);
    }
    
    private static Row getDomainRowFromDB(final String domainName, final Long clientID) throws DataAccessException {
        final Criteria domainNetworkCri = getNetworkDomainCriteria(domainName, clientID);
        final DataObject domainNetworkDO = getNetworkDomainDO(domainNetworkCri);
        return domainNetworkDO.getRow("DMDomain");
    }
    
    private static Criteria getNetworkDomainCriteria(final String domainName, final Long clientID) {
        Criteria domainNetworkCri = new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)domainName, 0, false);
        domainNetworkCri = domainNetworkCri.and(Column.getColumn("NetworkClient", "CLIENT_ID"), (Object)clientID, 0, false);
        return domainNetworkCri;
    }
    
    private static Long getNetworkClientId(final String clientName) throws DataAccessException {
        final Criteria networkCrit = new Criteria(Column.getColumn("NetworkClient", "NAME"), (Object)clientName, 0, false);
        final DataObject networkDO = getNetworkDOFromDB(networkCrit);
        if (networkDO.isEmpty()) {
            return null;
        }
        return (Long)networkDO.getFirstValue("NetworkClient", "CLIENT_ID");
    }
    
    private static Long getDomainID(final String domainName, final Long clientID) throws DataAccessException {
        final Row domainRow = getDomainRowFromDB(domainName, clientID);
        if (domainRow != null) {
            return (Long)domainRow.get("DOMAIN_ID");
        }
        return null;
    }
    
    public static SelectQuery getDMManagedDomainQuery(final Criteria queryCriteria) {
        final SelectQuery selectQuery = SyMUtil.formSelectQuery("DMDomain", queryCriteria, new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DMDomain", "NAME"), Column.getColumn("DMDomain", "DOMAIN_ID"), Column.getColumn("DMDomain", "CLIENT_ID"), Column.getColumn("DMDomain", "CUSTOMER_ID"), Column.getColumn("Credential", "CRD_USERNAME"), Column.getColumn("Credential", "CRD_PASSWORD"), Column.getColumn("Credential", "CREDENTIAL_ID"), Column.getColumn("Credential", "CRD_ENC_TYPE"), Column.getColumn("DMManagedDomain", "DC_NAME"), Column.getColumn("DMManagedDomain", "DOMAIN_ID"), Column.getColumn("DMManagedDomain", "AD_DOMAIN_NAME"), Column.getColumn("DMManagedDomainCredentialRel", "IS_ROOT"), Column.getColumn("DMManagedDomainCredentialRel", "DOMAIN_ID"), Column.getColumn("DMManagedDomainCredentialRel", "CREDENTIAL_ID"), Column.getColumn("DMManagedDomainCredentialRel", "VALIDATION_STATUS"))), (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new SortColumn(Column.getColumn("DMDomain", "DOMAIN_ID"), true))), new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DMDomain", "DMManagedDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2), new Join("DMManagedDomain", "DMManagedDomainCredentialRel", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2), new Join("DMManagedDomainCredentialRel", "Credential", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2))), (Criteria)null);
        if (getIsDmDomainSyncTDPresent() == 2) {
            selectQuery.addJoin(new Join("DMDomain", "DMDomainSyncDetails", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 1));
        }
        return selectQuery;
    }
    
    private static Criteria compileCriteriaList(final List<Criteria> criteriaList) {
        Criteria queryCriteria = null;
        for (int i = 0; i < criteriaList.size(); ++i) {
            final Criteria tempCriteria = criteriaList.get(i);
            if (queryCriteria == null) {
                queryCriteria = tempCriteria;
            }
            else {
                queryCriteria = queryCriteria.and(tempCriteria);
            }
        }
        return queryCriteria;
    }
    
    public static SelectQuery getDMManagedDomainQuery(final Long customerID, final String domainName, final String adDomainName, final List<Integer> networkClientIDs, final boolean onlyRoot) {
        final List<Criteria> criteriaList = new ArrayList<Criteria>();
        if (onlyRoot) {
            final Criteria rootCredentialCriteria = new Criteria(Column.getColumn("DMManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0);
            criteriaList.add(rootCredentialCriteria);
        }
        if (domainName != null) {
            final Criteria domainCriteria = new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)domainName, 0, false);
            criteriaList.add(domainCriteria);
        }
        if (adDomainName != null) {
            final Criteria adDomainCriteria = new Criteria(Column.getColumn("DMManagedDomain", "AD_DOMAIN_NAME"), (Object)adDomainName, 0, false);
            criteriaList.add(adDomainCriteria);
        }
        if (customerID != null) {
            final Criteria customerCriteria = new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0);
            criteriaList.add(customerCriteria);
        }
        if (networkClientIDs != null) {
            final List compiledNetworkList = new ArrayList();
            for (int i = 0; i < networkClientIDs.size(); ++i) {
                final Integer networkID = networkClientIDs.get(i);
                if (networkID != null) {
                    compiledNetworkList.add(networkID);
                }
            }
            if (compiledNetworkList.size() > 0) {
                final Criteria networkClientCriteria = new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)compiledNetworkList.toArray(), 8);
                criteriaList.add(networkClientCriteria);
            }
        }
        final Criteria queryCriteria = compileCriteriaList(criteriaList);
        return getDMManagedDomainQuery(queryCriteria);
    }
    
    public static SelectQuery getDMManagedDomainQuery(final Long customerID, final String domainName, final String adDomainName, final Integer clientID) {
        ArrayList networkList = null;
        if (clientID != null) {
            networkList = new ArrayList((Collection<? extends E>)Arrays.asList(clientID));
        }
        return getDMManagedDomainQuery(customerID, domainName, adDomainName, networkList, true);
    }
    
    public static int getIsDmDomainSyncTDPresent() {
        if (DomainDataProvider.isDmDomainSyncTDPresent == 0) {
            try {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName("DMDomainSyncDetails");
                if (td != null) {
                    DomainDataProvider.isDmDomainSyncTDPresent = 2;
                }
                else {
                    DomainDataProvider.isDmDomainSyncTDPresent = 1;
                }
            }
            catch (final Exception ex) {
                DomainDataProvider.isDmDomainSyncTDPresent = 1;
                IDPSlogger.ERR.log(Level.SEVERE, "looks like a IDPS is not yet fully supported here", ex);
            }
        }
        return DomainDataProvider.isDmDomainSyncTDPresent;
    }
    
    static {
        DomainDataProvider.isDmDomainSyncTDPresent = 0;
    }
}
