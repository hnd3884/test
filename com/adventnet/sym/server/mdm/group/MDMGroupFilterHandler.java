package com.adventnet.sym.server.mdm.group;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.me.mdm.server.tree.MDMTreeFilterHandler;

public class MDMGroupFilterHandler extends MDMTreeFilterHandler
{
    private static MDMGroupFilterHandler mdmGroupFilter;
    
    public static MDMGroupFilterHandler getInstance() {
        if (MDMGroupFilterHandler.mdmGroupFilter == null) {
            MDMGroupFilterHandler.mdmGroupFilter = new MDMGroupFilterHandler();
        }
        return MDMGroupFilterHandler.mdmGroupFilter;
    }
    
    public JSONObject getDeviceFilterGroup() {
        final JSONObject deviceFilterGroup = new JSONObject();
        deviceFilterGroup.put((Object)"FILTER_TYPE", (Object)100);
        deviceFilterGroup.put((Object)"FILTER_NAME", (Object)"dc.mdm.inv.devices");
        final JSONArray deviceMemberArray = new JSONArray();
        final JSONObject deviceMemberObj = new JSONObject();
        deviceMemberObj.put((Object)"FILTER_MEMBER_ID", (Object)101);
        deviceMemberObj.put((Object)"FILTER_MEMBER_NAME", (Object)"dc.mdm.group.not_associated_devices");
        deviceMemberArray.add((Object)deviceMemberObj);
        deviceFilterGroup.put((Object)"FILTER_MEMBERS", (Object)deviceMemberArray);
        return deviceFilterGroup;
    }
    
    public Criteria getFilterCriteria(final Long groupId, final JSONArray filterTreeJSON) {
        final Iterator filterItr = filterTreeJSON.iterator();
        JSONObject filterJSON = null;
        Criteria filterCri = null;
        int filterType = -1;
        int filterMemberId = -1;
        String filterMemberIdStr = null;
        Criteria groupUnssignedCri = null;
        Criteria modelTypeCri = null;
        Criteria platformCri = null;
        Criteria osVerCri = null;
        while (filterItr.hasNext()) {
            filterJSON = filterItr.next();
            filterType = Integer.parseInt((String)filterJSON.get((Object)"FILTER_TYPE"));
            switch (filterType) {
                case 100: {
                    filterMemberId = Integer.parseInt((String)filterJSON.get((Object)"FILTER_MEMBER_ID"));
                    final Criteria groupUnssignedNewCri = this.getDeviceGroupAssignedFilterCriteria(groupId, filterMemberId);
                    groupUnssignedCri = ((groupUnssignedCri == null) ? groupUnssignedNewCri : groupUnssignedCri.or(groupUnssignedNewCri));
                    continue;
                }
                case 2: {
                    filterMemberId = Integer.parseInt((String)filterJSON.get((Object)"FILTER_MEMBER_ID"));
                    final Criteria modelTypeNewCri = this.getModelTypeCriteria(filterMemberId);
                    modelTypeCri = ((modelTypeCri == null) ? modelTypeNewCri : modelTypeCri.or(modelTypeNewCri));
                    continue;
                }
                case 3: {
                    filterMemberIdStr = (String)filterJSON.get((Object)"FILTER_MEMBER_ID");
                    final Criteria osVerNewCri = this.getOSVersionCriteria(filterMemberIdStr);
                    osVerCri = ((osVerCri == null) ? osVerNewCri : osVerCri.or(osVerNewCri));
                    continue;
                }
                case 1: {
                    filterMemberId = Integer.parseInt((String)filterJSON.get((Object)"FILTER_MEMBER_ID"));
                    final Criteria platformNewCriteria = this.getPlatformCriteria(filterMemberId);
                    platformCri = ((platformCri == null) ? platformNewCriteria : platformCri.or(platformNewCriteria));
                    continue;
                }
            }
        }
        filterCri = ((filterCri == null) ? groupUnssignedCri : filterCri.and(groupUnssignedCri));
        filterCri = ((filterCri == null) ? modelTypeCri : filterCri.and(modelTypeCri));
        filterCri = ((filterCri == null) ? osVerCri : filterCri.and(osVerCri));
        filterCri = ((filterCri == null) ? platformCri : filterCri.and(platformCri));
        return filterCri;
    }
    
    private Criteria getDeviceGroupAssignedFilterCriteria(final Long groupId, final int filterMemberId) {
        Criteria groupUnssignedCri = null;
        if (filterMemberId == 101) {
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Criteria currentGroupExcludeCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 1);
            subQuery.setCriteria(currentGroupExcludeCri);
            subQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            final DerivedColumn dResCol = new DerivedColumn("MEMBER_RESOURCE_ID", subQuery);
            groupUnssignedCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)dResCol, 9);
        }
        return groupUnssignedCri;
    }
    
    private Criteria getModelTypeCriteria(final int modelType) {
        final Criteria modelTypeCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)modelType, 0);
        return modelTypeCri;
    }
    
    private Criteria getOSVersionCriteria(final String filterMemberId) {
        final Criteria osVerCri = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)filterMemberId, 0);
        return osVerCri;
    }
    
    private Criteria getPlatformCriteria(final int filterMemberId) {
        final Criteria osVerCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)filterMemberId, 0);
        return osVerCri;
    }
    
    static {
        MDMGroupFilterHandler.mdmGroupFilter = null;
    }
}
