package com.adventnet.sym.server.mdm.apps;

import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import org.json.JSONObject;

public class FilterUsers
{
    public ArrayList filterDevicesByModels(final JSONObject jsonObject, final ArrayList resourceList) {
        ArrayList filteredList = new ArrayList();
        try {
            final Long appID = jsonObject.getLong("APP_ID");
            final int supportedDevice = MDMUtil.getInstance().getSupportedDevice(appID);
            if (supportedDevice != 1) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
                sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
                final Criteria resourceCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)supportedDevice, 1);
                final Criteria criteria = resourceCriteria.and(modelCriteria);
                sQuery.setCriteria(criteria);
                sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
                sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
                final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
                final Iterator iterator = dataObject.getRows("MdDeviceInfo");
                filteredList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID");
            }
        }
        catch (final Exception ex) {}
        return filteredList;
    }
    
    public ArrayList filterDevicesByLicense(final JSONObject jsonObject, final ArrayList resourceList) {
        ArrayList filteredList = new ArrayList();
        try {
            final Long appGroupId = jsonObject.getLong("APP_GROUP_ID");
            final Criteria resourceCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria criteria = resourceCriteria.and(appGroupCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get("MdAppCatalogToResource", criteria);
            final Iterator iterator = dataObject.getRows("MdAppCatalogToResource");
            filteredList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID");
        }
        catch (final Exception ex) {}
        return filteredList;
    }
    
    public ArrayList filterUsersByStatus(final JSONObject jsonObject, final List resourceList) {
        ArrayList filteredList = new ArrayList();
        try {
            final Long appGroupId = jsonObject.getLong("APP_GROUP_ID");
            final Criteria resourceCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria criteria = resourceCriteria.and(appGroupCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get("MdAppCatalogToResource", criteria);
            final Iterator iterator = dataObject.getRows("MdAppCatalogToResource");
            filteredList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID");
        }
        catch (final Exception ex) {}
        return filteredList;
    }
    
    public ArrayList filterDevicesByOperatingSystem(final JSONObject jsonObject, final ArrayList resourceList) {
        ArrayList filteredList = new ArrayList();
        try {
            final int typeOfAssignment = jsonObject.getInt("TYPE_OF_ASSIGNMENT");
            if (typeOfAssignment == 2) {
                DataObject dataObject = null;
                final Criteria cManagedResIdList = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria cios4 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.", 10);
                final Criteria cios5 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.", 10);
                final Criteria cios6 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.", 10);
                final Criteria cios7 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"7.", 10);
                final Criteria cios8 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.", 10);
                final Criteria ciosVersionCheck = cios4.or(cios5).or(cios6).or(cios7).or(cios8);
                final Criteria cNotSupDevice = cManagedResIdList.and(ciosVersionCheck);
                dataObject = MDMUtil.getPersistence().get("MdDeviceInfo", cNotSupDevice);
                final Iterator iterator = dataObject.getRows("MdDeviceInfo");
                filteredList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID");
            }
        }
        catch (final Exception ex) {}
        return filteredList;
    }
    
    public ArrayList filterGroupMembersByModels(final JSONObject jsonObject, final ArrayList resourceList, final ArrayList groupList) {
        ArrayList filteredList = new ArrayList();
        try {
            final Long appID = jsonObject.getLong("APP_ID");
            final int supportedDevice = MDMUtil.getInstance().getSupportedDevice(appID);
            if (supportedDevice != 1) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
                sQuery.addJoin(new Join("CustomGroupMemberRel", "MdDeviceInfo", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
                final Criteria groupCriteria = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
                final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)supportedDevice, 1);
                final Criteria criteria = groupCriteria.and(modelCriteria);
                sQuery.setCriteria(criteria);
                sQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
                final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
                final Iterator iterator = dataObject.getRows("MdDeviceInfo");
                filteredList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID");
            }
        }
        catch (final Exception ex) {}
        return filteredList;
    }
    
    public ArrayList filterGroupMembersByLicense(final JSONObject jsonObject, final ArrayList resourceList, final ArrayList groupList) {
        ArrayList filteredList = new ArrayList();
        try {
            final Long appGroupId = jsonObject.getLong("APP_GROUP_ID");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            sQuery.addJoin(new Join("CustomGroupMemberRel", "MdAppCatalogToResource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria groupCriteria = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)groupList.toArray(), 8);
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria criteria = groupCriteria.and(appGroupCriteria);
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            final Iterator iterator = dataObject.getRows("MdAppCatalogToResource");
            filteredList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID");
        }
        catch (final Exception ex) {}
        return filteredList;
    }
    
    public ArrayList filterGroupMembersByOperatingSystem(final JSONObject jsonObject, final ArrayList resourceList, final ArrayList groupList) {
        ArrayList filteredList = new ArrayList();
        try {
            final int typeOfAssignment = jsonObject.getInt("TYPE_OF_ASSIGNMENT");
            if (typeOfAssignment == 2) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
                sQuery.addJoin(new Join("CustomGroupMemberRel", "MdDeviceInfo", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final Criteria cManagedResIdList = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupList.toArray(), 8);
                final Criteria cios4 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.", 10);
                final Criteria cios5 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.", 10);
                final Criteria cios6 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.", 10);
                final Criteria cios7 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"7.", 10);
                final Criteria cios8 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.", 10);
                final Criteria ciosVersionCheck = cios4.or(cios5).or(cios6).or(cios7).or(cios8);
                final Criteria cNotSupDevice = cManagedResIdList.and(ciosVersionCheck);
                sQuery.setCriteria(cNotSupDevice);
                final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
                final Iterator iterator = dataObject.getRows("MdDeviceInfo");
                filteredList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "RESOURCE_ID");
            }
        }
        catch (final Exception ex) {}
        return filteredList;
    }
}
