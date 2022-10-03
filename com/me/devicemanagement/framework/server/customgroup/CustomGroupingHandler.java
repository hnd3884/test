package com.me.devicemanagement.framework.server.customgroup;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Hashtable;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.resource.ResourceDataPopulator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.logging.Logger;

public class CustomGroupingHandler
{
    protected static Logger out;
    public static final int CUSTOM_GROUP_CTGY_STATIC = 1;
    public static final int CUSTOM_GROUP_TYPE_COMPUTER = 1;
    public static final int CUSTOM_GROUP_CTGY_DYNAMIC = 2;
    public static final int CUSTOM_GROUP_CTGY_STATIC_UNIQUE = 5;
    
    public static void updateDBTime(final Long resourceID) {
        try {
            final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
            final UpdateQuery s = (UpdateQuery)new UpdateQueryImpl("Resource");
            final Criteria c = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
            s.setCriteria(c);
            s.setUpdateColumn("DB_UPDATED_TIME", (Object)new Long(System.currentTimeMillis()));
            per.update(s);
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in updateDBTime....", e);
        }
    }
    
    public boolean removeMemberfromGroup(final Long groupId, final Long[] resourceIds) {
        boolean returnType = false;
        try {
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
            final Criteria resourceIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceIds, 8);
            final Criteria cri = groupIdCri.and(resourceIdCri);
            DataAccess.delete("CustomGroupMemberRel", cri);
            returnType = true;
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in removeMultipleMembersfromGroup....", e);
        }
        return returnType;
    }
    
    public void addOrUpdateCustomGroupExtn(final Long resourceID) {
        try {
            this.addOrUpdateCustomGroupExtn(resourceID, null);
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in addOrUpdateCustomGroupExtn method....", e);
        }
    }
    
    public void addOrUpdateCustomGroupExtn(final Long resourceID, final String description) {
        try {
            Long userId = null;
            try {
                userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            }
            catch (final Exception e) {
                CustomGroupingHandler.out.log(Level.WARNING, "Exception while getting the userId. System user woild be used instead");
            }
            if (userId == null) {
                userId = DMUserHandler.getUserID(EventConstant.DC_SYSTEM_USER);
            }
            this.addOrUpdateCustomGroupExtn(userId, resourceID, description);
        }
        catch (final Exception e2) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in addOrUpdateCustomGroupExtn....", e2);
        }
    }
    
    public void addOrUpdateCustomGroupExtn(final Long userId, final Long resourceID, final String description) throws DataAccessException {
        final Criteria cgResCri = new Criteria(Column.getColumn("CustomGroupExtn", "RESOURCE_ID"), (Object)resourceID, 0);
        final DataObject dObj = DataAccess.get("CustomGroupExtn", cgResCri);
        Row row = null;
        if (dObj.isEmpty()) {
            row = new Row("CustomGroupExtn");
            row.set("RESOURCE_ID", (Object)resourceID);
            row.set("GROUP_DESCRIPTION", (Object)description);
            row.set("CREATED_BY", (Object)userId);
            row.set("LAST_MODIFIED_BY", (Object)userId);
            dObj.addRow(row);
            SyMUtil.getPersistence().add(dObj);
        }
        else {
            row = dObj.getRow("CustomGroupExtn", cgResCri);
            row.set("GROUP_DESCRIPTION", (Object)description);
            row.set("LAST_MODIFIED_BY", (Object)userId);
            dObj.updateRow(row);
            SyMUtil.getPersistence().update(dObj);
        }
    }
    
    public Long getGroupResourceId(final String groupName, final Long customerID, final String domain) {
        Long resourceID = null;
        try {
            DataObject resourceDO = null;
            final Row row = new Row("Resource");
            row.set("NAME", (Object)groupName);
            row.set("DOMAIN_NETBIOS_NAME", (Object)domain);
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("RESOURCE_TYPE", (Object)new Integer(101));
            row.set("DB_ADDED_TIME", (Object)new Long(System.currentTimeMillis()));
            final ResourceDataPopulator poulator = new ResourceDataPopulator();
            resourceDO = ResourceDataPopulator.addOrUpdateResourceRow(row);
            final Row resRow = resourceDO.getRow("Resource");
            resourceID = (Long)resRow.get("RESOURCE_ID");
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in getCustomGroupResourceId....", e);
        }
        return resourceID;
    }
    
    public void addGroupMemberRel(final DataObject resourceDO, final Long resourceID, final Long[] resourceIds) {
        try {
            Row row = null;
            if (resourceIds != null) {
                for (int i = 0; i < resourceIds.length; ++i) {
                    row = new Row("CustomGroupMemberRel");
                    row.set("GROUP_RESOURCE_ID", (Object)resourceID);
                    row.set("MEMBER_RESOURCE_ID", (Object)resourceIds[i]);
                    try {
                        resourceDO.addRow(row);
                    }
                    catch (final DataAccessException e) {
                        CustomGroupingHandler.out.log(Level.WARNING, "resource id " + resourceIds[i] + " already exist");
                    }
                }
                DataAccess.update(resourceDO);
            }
        }
        catch (final Exception e2) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in addGroupMemberRel....", e2);
        }
    }
    
    public void closeConnection(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in closeConnection....", ex);
        }
    }
    
    public void addorUpdateGroup(final Long resourceID, final int groupType, final int groupCategory, final boolean isEditable, String groupDescription) {
        try {
            final Row row = new Row("CustomGroup");
            row.set("RESOURCE_ID", (Object)resourceID);
            row.set("GROUP_TYPE", (Object)groupType);
            row.set("GROUP_CATEGORY", (Object)groupCategory);
            if (groupDescription == null) {
                groupDescription = "--";
            }
            row.set("DESCRIPTION", (Object)groupDescription);
            row.set("IS_EDITABLE", (Object)isEditable);
            final Criteria customGroupUpdate = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject resourceDO = SyMUtil.getPersistence().get("CustomGroup", customGroupUpdate);
            if (resourceDO.isEmpty()) {
                resourceDO.addRow(row);
            }
            else {
                resourceDO.updateRow(row);
            }
            SyMUtil.getPersistence().update(resourceDO);
        }
        catch (final DataAccessException e) {
            CustomGroupingHandler.out.log(Level.WARNING, "DataAccessException occoured in addorUpdateCustomGroup", (Throwable)e);
        }
        catch (final Exception e2) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in addorUpdateCustomGroup....", e2);
        }
    }
    
    public void updateGroupDescription(final Long groupId, final String description) {
        try {
            final Criteria groupCri = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupId, 0);
            final DataObject resourceDO = SyMUtil.getPersistence().get("CustomGroup", groupCri);
            final Row groupRow = resourceDO.getRow("CustomGroup", groupCri);
            groupRow.set("DESCRIPTION", (Object)description);
            resourceDO.updateRow(groupRow);
            SyMUtil.getPersistence().update(resourceDO);
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in updateGroupDescription....", e);
        }
    }
    
    public List getResourceNameListFromDB(final Long[] resourceIds) {
        final List resourceNameList = new ArrayList();
        try {
            final Criteria resIdCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceIds, 8);
            final DataObject availableDO = DataAccess.get("Resource", resIdCriteria);
            final Iterator doIterator = availableDO.getRows("Resource");
            while (doIterator.hasNext()) {
                final Row r = doIterator.next();
                resourceNameList.add(r.get("NAME"));
            }
            CustomGroupingHandler.out.log(Level.FINE, "getResourceName list obtained from db : " + resourceNameList);
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in getGroupNameListFromDB....", e);
        }
        return resourceNameList;
    }
    
    public void removeResourcesFromAllGrp(final Long[] resourceIds) throws Exception {
        final Criteria crt = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceIds, 8);
        SyMUtil.getPersistence().delete(crt);
    }
    
    public static List getCustomGroups(final Integer groupType) {
        final List groupTypeList = new ArrayList();
        groupTypeList.add(groupType);
        return getCustomGroupsList(groupTypeList, 1);
    }
    
    public boolean addMembertoGroup(final Long groupId, final Long[] resourceIds) {
        boolean returnType = false;
        try {
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
            final Criteria resourceIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceIds, 8);
            final Criteria cri = groupIdCri.and(resourceIdCri);
            final DataObject dObj = SyMUtil.getPersistence().get("CustomGroupMemberRel", cri);
            dObj.deleteRows("CustomGroupMemberRel", cri);
            for (int i = 0; i < resourceIds.length; ++i) {
                final Row cgRelRow = new Row("CustomGroupMemberRel");
                cgRelRow.set("GROUP_RESOURCE_ID", (Object)groupId);
                cgRelRow.set("MEMBER_RESOURCE_ID", (Object)resourceIds[i]);
                dObj.addRow(cgRelRow);
            }
            SyMUtil.getPersistence().update(dObj);
            returnType = true;
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in addMembertoGroup....", e);
        }
        return returnType;
    }
    
    public static Long getCustomGroupID(final String customGroupName) {
        try {
            final Criteria nameCriteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)customGroupName, 0);
            final Criteria typeCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0);
            final Criteria criteria = nameCriteria.and(typeCriteria);
            final DataObject dobj = SyMUtil.getPersistence().get("Resource", criteria);
            if (!dobj.isEmpty() && dobj.containsTable("Resource")) {
                final Row row = dobj.getFirstRow("Resource");
                if (row != null) {
                    return (Long)row.get("RESOURCE_ID");
                }
            }
        }
        catch (final Exception ex) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occured while getting custom group ID for group " + customGroupName, ex);
        }
        return null;
    }
    
    public boolean deleteGroupResource(final Long resId, final Long customerId) {
        boolean returnType = false;
        CustomGroupingHandler.out.log(Level.INFO, "Inside deleteGroup .. resId : " + resId + " , customerID : " + customerId);
        try {
            final Criteria deleteCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resId, 0);
            SyMUtil.getPersistence().delete(deleteCriteria);
            returnType = true;
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in deleteCustomGroup....", e);
        }
        return returnType;
    }
    
    public static List getCustomGroupsList(final List groupTypeList, final Integer category) {
        List customGpList = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (groupTypeList != null && !groupTypeList.isEmpty() && category != null) {
                final Criteria typeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
                final Criteria categoryCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)category, 0);
                final Criteria cri = typeCri.and(categoryCri);
                selectQuery.setCriteria(cri);
            }
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            selectQuery.addSortColumn(sortCol);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            CustomGroupingHandler.out.log(Level.FINE, "Select Query to get Custom Group : {0}", selectQuery);
            customGpList = getCustomGroupDetailsList(selectQuery);
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in getCustomGroupsList....", e);
        }
        CustomGroupingHandler.out.log(Level.FINE, "Custom group list for group type {0} is : {1}", new Object[] { groupTypeList, customGpList });
        CustomGroupingHandler.out.log(Level.FINE, "Custom Groups Defined for Group Type {0} has been fetched", groupTypeList);
        return customGpList;
    }
    
    public static List getCustomGroupsList(final Criteria groupCri) {
        List customGpList = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (groupCri != null) {
                selectQuery.setCriteria(groupCri);
            }
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            selectQuery.addSortColumn(sortCol);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            CustomGroupingHandler.out.log(Level.FINE, "Select Query to get Custom Group : {0}", selectQuery);
            customGpList = getCustomGroupDetailsList(selectQuery);
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in getCustomGroupsList....", e);
        }
        return customGpList;
    }
    
    public static List getCustomGroupDetailsList(final SelectQuery selectQuery) {
        List customGpList = null;
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery(selectQuery);
            customGpList = new ArrayList();
            while (dmDataSetWrapper.next()) {
                final Hashtable customGpHash = new Hashtable();
                final Long groupId = (Long)dmDataSetWrapper.getValue("RESOURCE_ID");
                customGpHash.put("CUSTOM_GP_ID", dmDataSetWrapper.getValue("RESOURCE_ID"));
                customGpHash.put("CUSTOM_GP_NAME", dmDataSetWrapper.getValue("NAME"));
                customGpHash.put("CUSTOM_GP_TYPE", dmDataSetWrapper.getValue("GROUP_TYPE"));
                customGpHash.put("CUSTOM_GP_MEMBER_COUNT", getGroupMemberCount(groupId));
                customGpHash.put("CUSTOM_GP_CATEGORY", dmDataSetWrapper.getValue("GROUP_CATEGORY"));
                customGpList.add(customGpHash);
            }
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in getCustomGroupsList....", e);
        }
        return customGpList;
    }
    
    public static int getGroupMemberCount(final Long groupId) {
        int count = 0;
        try {
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
            count = DBUtil.getRecordCount("CustomGroupMemberRel", "MEMBER_RESOURCE_ID", groupIdCri);
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occured in getGroupMemberCount....", e);
        }
        return count;
    }
    
    public static List getGroupMemberIdList(final Long groupId) {
        List memberIdList = null;
        try {
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
            final DataObject memberIdDO = SyMUtil.getPersistence().get("CustomGroupMemberRel", groupIdCri);
            if (!memberIdDO.isEmpty()) {
                Long memberId = null;
                memberIdList = new ArrayList();
                final Iterator memberIdItr = memberIdDO.getRows("CustomGroupMemberRel");
                Row memberIdRow = null;
                while (memberIdItr.hasNext()) {
                    memberIdRow = memberIdItr.next();
                    memberId = (Long)memberIdRow.get("MEMBER_RESOURCE_ID");
                    memberIdList.add(memberId);
                }
            }
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in getGroupMemberIdList....", e);
        }
        return memberIdList;
    }
    
    public void deleteGroup(final Long resourceId, final Long customerId) {
        this.deleteGroupResource(resourceId, customerId);
    }
    
    public void deleteGroup(final Long[] resourceIds) {
        for (int resLen = resourceIds.length, i = 0; i < resLen; ++i) {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceIds[i]);
            this.deleteGroup(resourceIds[i], customerId);
        }
    }
    
    public static boolean isDeleteAllowed(final Long resId) throws Exception {
        final Criteria cr = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)resId, 0);
        final DataObject dataObj = SyMUtil.getPersistence().get("CustomGroup", cr);
        final Row r = dataObj.getRow("CustomGroup");
        final Boolean deletable = (Boolean)r.get("IS_EDITABLE");
        return deletable;
    }
    
    public static boolean isGroup(final Long resId) {
        boolean isGroup = false;
        try {
            final Criteria cr = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)resId, 0);
            final DataObject dataObj = SyMUtil.getPersistence().get("CustomGroup", cr);
            if (!dataObj.isEmpty()) {
                isGroup = true;
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(CustomGroupingHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return isGroup;
    }
    
    public static boolean isGroupOfCategory(final Long groupId, final int categoryType) {
        Boolean doesGroupBelongToCategory = Boolean.FALSE;
        try {
            final Criteria cr = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupId, 0);
            final Criteria grpCategoryCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)categoryType, 0);
            final DataObject dataObj = SyMUtil.getPersistence().get("CustomGroup", cr.and(grpCategoryCriteria));
            doesGroupBelongToCategory = !dataObj.isEmpty();
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(CustomGroupingHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return doesGroupBelongToCategory;
    }
    
    public boolean addMembertoGroups(final Long memberResID, final Long[] groupIDs) {
        try {
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupIDs, 8);
            final Criteria resourceIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)memberResID, 0);
            final Criteria cri = groupIdCri.and(resourceIdCri);
            final DataObject dObj = SyMUtil.getPersistence().get("CustomGroupMemberRel", cri);
            dObj.deleteRows("CustomGroupMemberRel", cri);
            for (int i = 0; i < groupIDs.length; ++i) {
                final Row cgRelRow = new Row("CustomGroupMemberRel");
                cgRelRow.set("GROUP_RESOURCE_ID", (Object)groupIDs[i]);
                cgRelRow.set("MEMBER_RESOURCE_ID", (Object)memberResID);
                dObj.addRow(cgRelRow);
            }
            SyMUtil.getPersistence().add(dObj);
            return true;
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in addMembertoGroup....", e);
            return false;
        }
    }
    
    public boolean removeMemberfromGroups(final Long memberResID, final Long[] groupIDs) {
        try {
            final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupIDs, 8);
            final Criteria memberResIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)memberResID, 0);
            final DeleteQuery delMemberRelQuery = (DeleteQuery)new DeleteQueryImpl("CustomGroupMemberRel");
            delMemberRelQuery.setCriteria(groupIdCri.and(memberResIdCri));
            SyMUtil.getPersistence().delete(delMemberRelQuery);
            return true;
        }
        catch (final Exception e) {
            CustomGroupingHandler.out.log(Level.WARNING, "Exception occoured in removeMultipleMembersfromGroup....", e);
            return false;
        }
    }
    
    static {
        CustomGroupingHandler.out = Logger.getLogger("CustomGroupLogger");
    }
}
