package com.adventnet.sym.webclient.mdm.certificate;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.logging.Level;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import com.me.mdm.server.certificate.CertificateMapping;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMCertificateTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMCertificateTableRetrieverAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final String expireDate = request.getParameter("expireDate");
        final String isActive = request.getParameter("expireDate");
        final String templateType = request.getParameter("serverType");
        final String serverID = request.getParameter("serverID");
        Boolean isActiveFlag = Boolean.TRUE;
        if (!MDMStringUtils.isEmpty(isActive)) {
            isActiveFlag = Boolean.valueOf(isActive);
        }
        final Criteria activeCriteria = new Criteria(Column.getColumn("Certificates", "IS_ACTIVE"), (Object)isActiveFlag, 0);
        Criteria customerCriteria = new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria certTypeCriteria = new Criteria(Column.getColumn("Certificates", "CERTIFICATE_TYPE"), (Object)0, 0);
        Criteria expireCriteria = null;
        if (expireDate != null && !expireDate.trim().isEmpty()) {
            final Date currentDate = new Date();
            final long currentDateInLong = currentDate.getTime();
            if (expireDate.equals("1")) {
                expireCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_NOTAFTER"), (Object)currentDateInLong, 7, false);
            }
            else if (expireDate.equals("2")) {
                final int currentDay = currentDate.getDay();
                final long expireWeek = currentDateInLong + (6 - currentDay);
                expireCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_NOTAFTER"), (Object)new Long[] { currentDateInLong, expireWeek }, 14, false);
            }
            else if (expireDate.equals("3")) {
                Calendar calendar = this.getCurrentDate(currentDate);
                calendar.set(5, calendar.getActualMinimum(5));
                calendar = this.setTimeToBeginningOfDay(calendar);
                final Date beginning = calendar.getTime();
                calendar = this.getCurrentDate(currentDate);
                calendar.set(5, calendar.getActualMaximum(5));
                this.setTimeToEndofDay(calendar);
                final Date end = calendar.getTime();
                expireCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_NOTAFTER"), (Object)new Long[] { beginning.getTime(), end.getTime() }, 14, false);
            }
            if (expireCriteria != null) {
                customerCriteria = customerCriteria.and(expireCriteria);
            }
        }
        if (!MDMStringUtils.isEmpty(templateType)) {
            final Integer tempalte = Integer.parseInt(templateType);
            customerCriteria = customerCriteria.and(new Criteria(Column.getColumn("SCEPServers", "TYPE"), (Object)tempalte, 0));
        }
        if (!MDMStringUtils.isEmpty(serverID)) {
            final Long serverIDLong = Long.parseLong(serverID);
            customerCriteria = customerCriteria.and(new Criteria(Column.getColumn("SCEPServers", "SERVER_ID"), (Object)serverIDLong, 0));
        }
        if (viewCtx.getUniqueId().equals("mdmCertDetails")) {
            customerCriteria = customerCriteria.and(certTypeCriteria);
        }
        selectQuery.setCriteria(customerCriteria.and(activeCriteria));
        super.setCriteria(selectQuery, viewCtx);
    }
    
    private Calendar setTimeToBeginningOfDay(final Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }
    
    private Calendar setTimeToEndofDay(final Calendar calendar) {
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar;
    }
    
    private Calendar getCurrentDate(final Date currentDate) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        return calendar;
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        final DMDataSetWrapper ds = null;
        try {
            final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
            final ArrayList<Long> list = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "Certificates.CERTIFICATE_RESOURCE_ID");
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final SelectQuery selectQuery = ProfileCertificateUtil.getInstance().getCertConfigSelectQuery(list);
            selectQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "RecentProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.removeSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
            DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final HashMap hashMap = new HashMap();
            final List cfgList = new ArrayList();
            for (final CertificateMapping certificateMapping : ProfileCertificateUtil.getInstance().getCertificateMap()) {
                if (dataObject.containsTable(certificateMapping.getTableName())) {
                    final HashMap tempMap = certificateMapping.getConfigDataItemID(dataObject);
                    for (final Long certID : tempMap.keySet()) {
                        final List tempList = tempMap.get(certID);
                        List mainList = hashMap.get(certID);
                        if (mainList == null) {
                            mainList = new ArrayList();
                        }
                        mainList.addAll(tempList);
                        cfgList.addAll(tempList);
                        hashMap.put(certID, mainList);
                    }
                }
            }
            final SelectQuery profileQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentPubProfileToColln"));
            profileQuery.addJoin(new Join("RecentPubProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            profileQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            profileQuery.addJoin(new Join("CfgDataToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            profileQuery.setCriteria(new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)cfgList.toArray(), 8).and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
            profileQuery.addSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            profileQuery.addSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"));
            profileQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "CONFIG_DATA_ID"));
            profileQuery.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
            dataObject = MDMUtil.getPersistenceLite().get(profileQuery);
            Iterator itr2 = hashMap.keySet().iterator();
            final HashMap profileCount = new HashMap();
            final HashMap certToCollnMap = new HashMap();
            final List totalcollnList = new ArrayList();
            while (itr2.hasNext()) {
                final Long certID2 = itr2.next();
                final List itemList = hashMap.get(certID2);
                final Iterator rowItr = dataObject.getRows("CfgDataToCollection", new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)itemList.toArray(), 8), new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
                final List collnList = new ArrayList();
                while (rowItr.hasNext()) {
                    final Row row = rowItr.next();
                    final Long collnID = (Long)row.get("COLLECTION_ID");
                    if (!collnList.contains(collnID)) {
                        collnList.add(collnID);
                    }
                    if (!totalcollnList.contains(collnID)) {
                        totalcollnList.add(collnID);
                    }
                }
                profileCount.put(certID2, collnList.size());
                certToCollnMap.put(certID2, collnList);
            }
            final SelectQuery deviceQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            deviceQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
            deviceQuery.addJoin(new Join("RecentProfileForResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deviceQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deviceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
            deviceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
            deviceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria collncriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)totalcollnList.toArray(), 8);
            final Criteria statuscriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)6, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            deviceQuery.setCriteria(criteria.and(collncriteria).and(statuscriteria).and(customerCriteria));
            RBDAUtil.getInstance().getRBDAQuery(deviceQuery);
            dataObject = MDMUtil.getPersistenceLite().get(deviceQuery);
            itr2 = certToCollnMap.keySet().iterator();
            final HashMap deviceMap = new HashMap();
            while (itr2.hasNext()) {
                final Long certId = itr2.next();
                final List collnList2 = certToCollnMap.get(certId);
                final Iterator devItr = dataObject.getRows("RecentProfileForResource", new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collnList2.toArray(), 8));
                int i = 0;
                while (devItr.hasNext()) {
                    ++i;
                    devItr.next();
                }
                deviceMap.put(certId, i);
            }
            viewCtx.getRequest().setAttribute("ASSOCIATED_PROFILES", (Object)profileCount);
            viewCtx.getRequest().setAttribute("ASSOCIATED_DEVICES", (Object)deviceMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while Add Group Names..", e);
        }
    }
}
