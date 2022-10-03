package com.adventnet.sym.webclient.mdm.certificate;

import com.adventnet.ds.query.DerivedColumn;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.server.certificate.CertificateMapping;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMCertificateProfileRetriverAction extends MDMEmberTableRetrieverAction
{
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        selectQuery.addJoin(new Join("Collection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final SelectQuery wifiSelect = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        wifiSelect.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        wifiSelect.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        wifiSelect.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        wifiSelect.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID", "COLLECTION_ID"));
        final Column countColumn = new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID").distinct().count();
        countColumn.setColumnAlias("payload_count");
        wifiSelect.addSelectColumn(countColumn);
        final List groupByList = new ArrayList();
        groupByList.add(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
        final GroupByClause groupByClause = new GroupByClause(groupByList);
        wifiSelect.setGroupByClause(groupByClause);
        final SelectQuery vpnSelect = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        vpnSelect.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        vpnSelect.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        vpnSelect.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        vpnSelect.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID", "COLLECTION_ID"));
        vpnSelect.addSelectColumn(countColumn);
        vpnSelect.setGroupByClause(groupByClause);
        final SelectQuery ssoSelect = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        ssoSelect.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        ssoSelect.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        ssoSelect.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        ssoSelect.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID", "COLLECTION_ID"));
        ssoSelect.addSelectColumn(countColumn);
        ssoSelect.setGroupByClause(groupByClause);
        final SelectQuery exchangeSelect = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        exchangeSelect.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        exchangeSelect.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        exchangeSelect.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        exchangeSelect.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID", "COLLECTION_ID"));
        exchangeSelect.addSelectColumn(countColumn);
        exchangeSelect.setGroupByClause(groupByClause);
        final SelectQuery emailSelect = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        emailSelect.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        emailSelect.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        emailSelect.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        emailSelect.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID", "COLLECTION_ID"));
        emailSelect.addSelectColumn(countColumn);
        emailSelect.setGroupByClause(groupByClause);
        final SelectQuery scepSelect = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        scepSelect.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        scepSelect.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        scepSelect.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        scepSelect.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID", "COLLECTION_ID"));
        scepSelect.addSelectColumn(countColumn);
        scepSelect.setGroupByClause(groupByClause);
        final SelectQuery certSelect = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        certSelect.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        certSelect.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        certSelect.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        certSelect.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID", "COLLECTION_ID"));
        certSelect.addSelectColumn(countColumn);
        certSelect.setGroupByClause(groupByClause);
        for (final CertificateMapping certificateMapping : ProfileCertificateUtil.getInstance().getCertificateMap()) {
            final String tableName = certificateMapping.getTableName();
            SelectQuery curQuery = null;
            if (tableName.toLowerCase().contains("wifi")) {
                curQuery = wifiSelect;
            }
            else if (tableName.toLowerCase().contains("vpn")) {
                curQuery = vpnSelect;
            }
            else if (tableName.toLowerCase().contains("sso")) {
                curQuery = ssoSelect;
            }
            else if (tableName.toLowerCase().contains("email")) {
                curQuery = emailSelect;
            }
            else if (tableName.toLowerCase().contains("active")) {
                curQuery = exchangeSelect;
            }
            else if (tableName.toLowerCase().contains("scep")) {
                curQuery = scepSelect;
            }
            else if (tableName.toLowerCase().contains("cert")) {
                curQuery = certSelect;
            }
            certificateMapping.addCfgDataItemJoin(curQuery, 1);
            Criteria criteria = curQuery.getCriteria();
            if (criteria == null) {
                criteria = certificateMapping.getNotEmptyCriteria();
            }
            else {
                criteria = criteria.or(certificateMapping.getNotEmptyCriteria());
            }
            curQuery.setCriteria(criteria);
        }
        wifiSelect.setCriteria(wifiSelect.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        vpnSelect.setCriteria(vpnSelect.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        ssoSelect.setCriteria(ssoSelect.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        emailSelect.setCriteria(emailSelect.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        exchangeSelect.setCriteria(exchangeSelect.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        scepSelect.setCriteria(scepSelect.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        certSelect.setCriteria(certSelect.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
        final DerivedTable wifiDerived = new DerivedTable("wifi", (Query)wifiSelect);
        final DerivedTable vpnDerived = new DerivedTable("vpn", (Query)vpnSelect);
        final DerivedTable ssoDerived = new DerivedTable("sso", (Query)ssoSelect);
        final DerivedTable emailDerived = new DerivedTable("email", (Query)emailSelect);
        final DerivedTable exchangeDerived = new DerivedTable("exchange", (Query)exchangeSelect);
        final DerivedTable scepDerived = new DerivedTable("scep", (Query)scepSelect);
        final DerivedTable certDerived = new DerivedTable("cert", (Query)certSelect);
        selectQuery.addJoin(new Join(Table.getTable("Collection"), (Table)wifiDerived, new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join(Table.getTable("Collection"), (Table)vpnDerived, new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join(Table.getTable("Collection"), (Table)emailDerived, new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join(Table.getTable("Collection"), (Table)scepDerived, new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join(Table.getTable("Collection"), (Table)ssoDerived, new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join(Table.getTable("Collection"), (Table)certDerived, new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join(Table.getTable("Collection"), (Table)exchangeDerived, new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("wifi", "payload_count", "wifi.payload_count"));
        selectQuery.addSelectColumn(Column.getColumn("vpn", "payload_count", "vpn.payload_count"));
        selectQuery.addSelectColumn(Column.getColumn("sso", "payload_count", "sso.payload_count"));
        selectQuery.addSelectColumn(Column.getColumn("email", "payload_count", "email.payload_count"));
        selectQuery.addSelectColumn(Column.getColumn("exchange", "payload_count", "exchange.payload_count"));
        selectQuery.addSelectColumn(Column.getColumn("scep", "payload_count", "scep.payload_count"));
        selectQuery.addSelectColumn(Column.getColumn("cert", "payload_count", "cert.payload_count"));
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final Long certID = Long.parseLong(viewCtx.getRequest().getParameter("certificateId"));
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final List certIds = new ArrayList();
        certIds.add(certID);
        final SelectQuery criteriaQuery = ProfileCertificateUtil.getInstance().getCertConfigSelectQuery(certIds);
        criteriaQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        criteriaQuery.addJoin(new Join("CfgDataToCollection", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        criteriaQuery.removeSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        criteriaQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
        final DerivedColumn derivedColumn = new DerivedColumn("CertConfigDataItems", criteriaQuery);
        selectQuery.setCriteria(new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)derivedColumn, 8).and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
    }
}
