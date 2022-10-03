package com.adventnet.sym.webclient.mdm.knox;

import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.HashMap;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMKnoxDeviceTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMKnoxDeviceTableRetrieverAction() {
        this.logger = Logger.getLogger(MDMKnoxDeviceTableRetrieverAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            Criteria criteria = null;
            final HttpServletRequest request = viewCtx.getRequest();
            final String mdmGroupIdStr = request.getParameter("groupType");
            final String deviceStatus = request.getParameter("deviceStatus");
            final String containerStatus = request.getParameter("containerStatus");
            final String showLicensceDev = request.getParameter("showLicensceDev");
            final Long groupType = null;
            if (deviceStatus != null && deviceStatus.equalsIgnoreCase("all")) {
                final Criteria cdCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                final Criteria cdCriteriaOr = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)4, 0);
                criteria = cdCriteria.or(cdCriteriaOr);
            }
            else if (deviceStatus != null && deviceStatus.equalsIgnoreCase("4")) {
                request.setAttribute("deviceStatus", (Object)deviceStatus);
                final Criteria cdCriteria = criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)4, 0);
            }
            else if (deviceStatus == null || deviceStatus.equalsIgnoreCase("2")) {
                request.setAttribute("deviceStatus", (Object)deviceStatus);
                final Criteria cdCriteria = criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            }
            if (mdmGroupIdStr != null && !"all".equals(mdmGroupIdStr)) {
                request.setAttribute("groupType", (Object)mdmGroupIdStr);
                final Long mdmGroupId = new Long(mdmGroupIdStr);
                selectQuery.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                final Criteria cgCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)mdmGroupId, 0);
                criteria = criteria.and(cgCriteria);
            }
            if (containerStatus != null && !"all".equals(containerStatus)) {
                request.setAttribute("containerStatus", (Object)containerStatus);
                final Long mdmGroupId = new Long(containerStatus);
                final Criteria containerCriteria = new Criteria(Column.getColumn("ManagedKNOXContainer", "CONTAINER_STATUS"), (Object)mdmGroupId, 0);
                criteria = criteria.and(containerCriteria);
            }
            final String sh = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            if (showLicensceDev != null && !"false".equalsIgnoreCase(showLicensceDev)) {
                final Join knoxDivToLicRel = new Join("ManagedKNOXContainer", "KNOXDeviceToLicenseRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                selectQuery.addJoin(knoxDivToLicRel);
            }
            selectQuery.setCriteria(criteria);
            final Column column = new Column("MdDeviceInfo", "OS_VERSION");
            final Column majorVersionColumn = (Column)Column.createFunction("CONCAT", new Object[] { column, ".0" });
            majorVersionColumn.setType(12);
            final Criteria os4CategoryCriteria = new Criteria(majorVersionColumn, (Object)"4.*", 2);
            final Criteria os5CategoryCriteria = new Criteria(majorVersionColumn, (Object)"5.*", 2);
            final Criteria os6CategoryCriteria = new Criteria(majorVersionColumn, (Object)"6.*", 2);
            final Criteria os7CategoryCriteria = new Criteria(majorVersionColumn, (Object)"7.*", 2);
            final Criteria os8CategoryCriteria = new Criteria(majorVersionColumn, (Object)"8.*", 2);
            final Criteria os9CategoryCriteria = new Criteria(majorVersionColumn, (Object)"9.*", 2);
            final Criteria osCriteria = os4CategoryCriteria.or(os5CategoryCriteria).or(os6CategoryCriteria).or(os7CategoryCriteria).or(os8CategoryCriteria).or(os9CategoryCriteria);
            if (criteria != null) {
                criteria = criteria.and(osCriteria);
            }
            else {
                criteria = osCriteria;
            }
            final Criteria notDeviceOwner = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)false, 0);
            selectQuery.setCriteria(criteria.and(notDeviceOwner));
            final HashMap constants = new HashMap();
            constants.put("KNOX_ACTIVATION_STATUS_INITIATED", 20000);
            constants.put("KNOX_ACTIVATION_STATUS_ACTIVATED", 20001);
            constants.put("KNOX_ACTIVATION_STATUS_DEACTIVATED", 20002);
            constants.put("KNOX_ACTIVATION_STATUS_FAILED", 20003);
            constants.put("KNOX_ACTIVATION_STATUS_NOTAVAILABLE", 20004);
            request.setAttribute("knox", (Object)constants);
            final List mdmAndList = MDMGroupHandler.getCustomGroups();
            if (mdmAndList != null) {
                request.setAttribute("mdmAndList", (Object)mdmAndList);
            }
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final Integer iTotalKnoxEnabledDevices = KnoxUtil.getInstance().getTotalKnoxEnabledDevice(customerId);
            final Integer iTotalLicenseCount = KnoxUtil.getInstance().getTotalLicenseCount(customerId);
            final Integer iUsedLicenseCount = KnoxUtil.getInstance().getUsedLicenseCount(customerId);
            final Integer iRemainingLicenseCount = iTotalLicenseCount - iUsedLicenseCount;
            request.setAttribute("totalKnoxEnabledDevices", (Object)iTotalKnoxEnabledDevices);
            request.setAttribute("totalLicenseCount", (Object)iTotalLicenseCount);
            request.setAttribute("usedLicenseCount", (Object)iUsedLicenseCount);
            request.setAttribute("remainingLicenseCount", (Object)iRemainingLicenseCount);
            request.setAttribute("showLicensceDev", (Object)showLicensceDev);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupTableRetrieverAction...", e);
        }
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        try {
            final HashMap transformData = new HashMap();
            final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
            final ArrayList<Long> list = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "Resource.RESOURCE_ID");
            SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join resourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            query.addJoin(resourceJoin);
            query.addJoin(customGroupJoin);
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("Resource", "NAME"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            final Criteria cRes = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)list.toArray(), 8);
            final Criteria gRes = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)new int[] { 9, 8 }, 9);
            query.setCriteria(cRes.and(gRes));
            query = RBDAUtil.getInstance().getRBDAQuery(query);
            final long stime = System.currentTimeMillis();
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(query);
            this.logger.log(Level.INFO, "postModelFetch(): Query Execution Time - {0}", System.currentTimeMillis() - stime);
            final HashMap<Long, List> hashMap = new HashMap<Long, List>();
            final Iterator<Row> customGroupMemberRelRows = dataObject.getRows("CustomGroupMemberRel");
            while (customGroupMemberRelRows.hasNext()) {
                final Row customGroupMemberRelRow = customGroupMemberRelRows.next();
                final Long resourceID = (Long)customGroupMemberRelRow.get("MEMBER_RESOURCE_ID");
                final Iterator<Row> resourceIterator = dataObject.getRows("Resource", new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceID, 0), new Join("CustomGroupMemberRel", "Resource", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final List groupNames = DBUtil.getColumnValuesAsList((Iterator)resourceIterator, "NAME");
                if (!hashMap.containsKey(resourceID)) {
                    hashMap.put(resourceID, groupNames);
                }
            }
            transformData.put("ASSOCIATED_GROUP_NAMES", hashMap);
            viewCtx.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)transformData);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while process pre rendering..", e);
        }
    }
}
