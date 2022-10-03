package com.adventnet.sym.webclient.mdm.group;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Collection;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDeviceGroupViewController extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDeviceGroupViewController() {
        this.logger = Logger.getLogger(MDMGroupTableRetrieverAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final long deviceId = Long.parseLong(request.getParameter("deviceId"));
            final String groupTypeFilterStr = request.getParameter("groupTypeFilter");
            request.setAttribute("loggedInUserId", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedUserToDevice", new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), 0).or(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), 0)), 1));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0), 1));
            Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
            deviceCriteria = MDMDBUtil.andCriteria(deviceCriteria, new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)MDMGroupHandler.getMDMGroupType().toArray(), 8));
            if (groupTypeFilterStr != null && groupTypeFilterStr != "0" && !groupTypeFilterStr.equalsIgnoreCase("all")) {
                Criteria groupTypeFilterCri = null;
                request.setAttribute("groupTypeFilter", (Object)groupTypeFilterStr);
                final Integer groupTypeFilter = Integer.valueOf(groupTypeFilterStr);
                if (groupTypeFilter == 1) {
                    final List groupList = MDMGroupHandler.getMDMGroupType();
                    groupTypeFilterCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupList.toArray(), 8);
                }
                else if (groupTypeFilter == 2) {
                    groupTypeFilterCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 0).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 0, false));
                }
                else if (groupTypeFilter == 3) {
                    groupTypeFilterCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 0).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 1, false));
                }
                deviceCriteria = MDMDBUtil.andCriteria(deviceCriteria, groupTypeFilterCri);
            }
            Criteria customerCriteria = new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)null, 0);
            customerCriteria = MDMDBUtil.orCriteria(customerCriteria, new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0));
            deviceCriteria = MDMDBUtil.andCriteria(deviceCriteria, customerCriteria);
            deviceCriteria = MDMDBUtil.andCriteria(deviceCriteria, new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)(-1L), 1));
            if (selectQuery.getCriteria() != null) {
                deviceCriteria = MDMDBUtil.andCriteria(deviceCriteria, selectQuery.getCriteria());
            }
            selectQuery.setCriteria(deviceCriteria);
            RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupTableRetrieverAction...{0}", e);
        }
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        try {
            final HashMap transformData = new HashMap();
            final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
            final ArrayList<Long> list = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "Resource.RESOURCE_ID");
            final Criteria cRes = new Criteria(new Column("DefaultGroupSettings", "GROUP_ID"), (Object)list.toArray(), 8);
            final long stime = System.currentTimeMillis();
            final List values = DBUtil.getDistinctColumnValue("DefaultGroupSettings", "GROUP_ID", cRes);
            this.logger.log(Level.INFO, "postModelFetch(): Query Execution Time - {0}", System.currentTimeMillis() - stime);
            transformData.put("SELF_ENROLL_DEFAULT_GROUP", values);
            final ArrayList<Long> userIdList = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "CreatedUser.USER_ID");
            final Criteria criteria = new Criteria(new Column("AaaLogin", "USER_ID"), (Object)userIdList.toArray(), 8, false);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final HashMap<Long, Long> hashMap = new HashMap<Long, Long>();
            final List createdLogInIdList = new ArrayList();
            if (dataObject.containsTable("AaaLogin")) {
                final Iterator<Row> rows = dataObject.getRows("AaaLogin");
                while (rows.hasNext()) {
                    final Row aaaLoginRow = rows.next();
                    final Long createdLogInId = (Long)aaaLoginRow.get("LOGIN_ID");
                    createdLogInIdList.add(createdLogInId);
                    hashMap.put((Long)aaaLoginRow.get("USER_ID"), createdLogInId);
                }
            }
            transformData.put("USER_LOGIN_MAP", hashMap);
            final Criteria aaaLoginIDCrit = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)createdLogInIdList.toArray(), 8, false);
            final DataObject adminDO = DMUserHandler.getLoginDOForAAARoleName("All_Managed_Mobile_Devices", aaaLoginIDCrit);
            final Iterator accountRows = adminDO.getRows("AaaAccount");
            final List adminAccessLoginId = DBUtil.getColumnValuesAsList(accountRows, "LOGIN_ID");
            transformData.put("ADMIN_LOGIN_ID", adminAccessLoginId);
            final Long customerID = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
            final HashMap domainMap = new HashMap();
            final ArrayList<String> domainList = (ArrayList<String>)dmWebClientCommonUtil.getColumnValues(viewCtx, "Resource.DOMAIN_NETBIOS_NAME");
            final HashSet<String> domainSet = new HashSet<String>(domainList);
            domainSet.remove("MDM");
            if (!domainSet.isEmpty()) {
                final SelectQuery domainSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
                domainSelectQuery.addJoin(new Join("DMDomain", "DMDomainSyncDetails", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 2));
                final Criteria domainNameCriteria = new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)domainSet.toArray(), 8, false).and(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0);
                domainSelectQuery.setCriteria(domainNameCriteria);
                domainSelectQuery.addSelectColumn(Column.getColumn("DMDomain", "NAME"));
                domainSelectQuery.addSelectColumn(Column.getColumn("DMDomain", "DOMAIN_ID"));
                domainSelectQuery.addSelectColumn(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"));
                domainSelectQuery.addSelectColumn(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"));
                final DataObject domainObject = MDMUtil.getPersistence().get(domainSelectQuery);
                if (domainObject != null) {
                    final Iterator<Row> domainRows = domainObject.getRows("DMDomain");
                    while (domainRows.hasNext()) {
                        final Row domainRow = domainRows.next();
                        final Long domainId = (Long)domainRow.get("DOMAIN_ID");
                        final Row dmSyncDetailRow = domainObject.getRow("DMDomainSyncDetails", new Criteria(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"), (Object)domainId, 0));
                        domainMap.put(domainRow.get("NAME"), dmSyncDetailRow.get("FETCH_STATUS"));
                    }
                }
            }
            transformData.put("DOMAIN_FETCH_STATUS_MAP", domainMap);
            viewCtx.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)transformData);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while process pre rendering..", e);
        }
    }
}
