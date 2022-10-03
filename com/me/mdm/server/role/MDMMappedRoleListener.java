package com.me.mdm.server.role;

import org.apache.commons.collections.MultiHashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authorization.RoleEvent;
import java.util.logging.Logger;
import org.apache.commons.collections.MultiMap;
import com.me.devicemanagement.framework.server.authorization.RoleListener;

public class MDMMappedRoleListener implements RoleListener
{
    private static final MultiMap MAPPEDROLESFORMODULE;
    private static final Logger LOGGER;
    
    public void roleAdded(final RoleEvent customerEvent) {
        MDMMappedRoleListener.LOGGER.log(Level.INFO, "Role added event for mapper role listener.RoleId:{0}", customerEvent.roleID);
        this.addOtherMapperRoles(customerEvent.roleID);
    }
    
    public void roleDeleted(final RoleEvent customerEvent) {
    }
    
    public void roleUpdated(final RoleEvent customerEvent) {
        MDMMappedRoleListener.LOGGER.log(Level.INFO, "Role updated event for mapper role listener.RoleId:{0}", customerEvent.roleID);
        this.addOtherMapperRoles(customerEvent.roleID);
    }
    
    private void addOtherMapperRoles(final Long umRoleId) {
        try {
            MDMMappedRoleListener.LOGGER.log(Level.INFO, "Going to add other mapper role for umrole:{0}", umRoleId);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaRole"));
            selectQuery.addJoin(new Join("AaaRole", "UMModule", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            selectQuery.addJoin(new Join("UMModule", "UMRoleModuleRelation", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 1));
            selectQuery.addJoin(new Join("UMRoleModuleRelation", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"MDM_", 10, (boolean)Boolean.FALSE));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row umRoleRow = dataObject.getRow("UMRole", new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)umRoleId, 0));
                if (umRoleRow != null) {
                    final List tableList = new ArrayList();
                    tableList.add("UMRole");
                    tableList.add("UMRoleModuleRelation");
                    final DataObject configuredDataObject = dataObject.getDataObject(tableList, umRoleRow);
                    if (!configuredDataObject.isEmpty()) {
                        boolean isRowAdded = false;
                        final Iterator moduleIterator = configuredDataObject.getRows("UMRoleModuleRelation");
                        final List<Long> umModuleId = new ArrayList<Long>();
                        while (moduleIterator.hasNext()) {
                            final Row moduleRow = moduleIterator.next();
                            final Long moduleId = (Long)moduleRow.get("UM_MODULE_ID");
                            final Row roleRelRow = dataObject.getRow("UMModule", new Criteria(new Column("UMModule", "UM_MODULE_ID"), (Object)moduleId, 0));
                            final Long roleId = (Long)roleRelRow.get("ROLE_ID");
                            final Row roleRow = dataObject.getRow("AaaRole", new Criteria(new Column("AaaRole", "ROLE_ID"), (Object)roleId, 0));
                            final String roleName = (String)roleRow.get("NAME");
                            final List<String> mapperRole = (List<String>)MDMMappedRoleListener.MAPPEDROLESFORMODULE.get((Object)roleName);
                            if (mapperRole != null) {
                                final Iterator roleIterator = dataObject.getRows("AaaRole", new Criteria(new Column("AaaRole", "NAME"), (Object)mapperRole.toArray(), 8));
                                while (roleIterator.hasNext()) {
                                    final Row mappedRoleRow = roleIterator.next();
                                    final Long mapperRoleId = (Long)mappedRoleRow.get("ROLE_ID");
                                    final Row mapperModuleRow = dataObject.getRow("UMModule", new Criteria(new Column("UMModule", "ROLE_ID"), (Object)mapperRoleId, 0));
                                    umModuleId.add((Long)mapperModuleRow.get("UM_MODULE_ID"));
                                }
                            }
                        }
                        MDMMappedRoleListener.LOGGER.log(Level.INFO, "Going to add UMRoleModuleRelation:{0}", umModuleId);
                        for (final Long mappedModuleId : umModuleId) {
                            Row moduleMappedRole = configuredDataObject.getRow("UMRoleModuleRelation", new Criteria(new Column("UMRoleModuleRelation", "UM_MODULE_ID"), (Object)mappedModuleId, 0));
                            if (moduleMappedRole == null) {
                                moduleMappedRole = new Row("UMRoleModuleRelation");
                                moduleMappedRole.set("UM_MODULE_ID", (Object)mappedModuleId);
                                moduleMappedRole.set("UM_ROLE_ID", (Object)umRoleId);
                                configuredDataObject.addRow(moduleMappedRole);
                                isRowAdded = true;
                            }
                        }
                        if (isRowAdded) {
                            MDMUtil.getPersistenceLite().update(configuredDataObject);
                        }
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            MDMMappedRoleListener.LOGGER.log(Level.SEVERE, "Exception in add other role module", (Throwable)e);
        }
    }
    
    static {
        MAPPEDROLESFORMODULE = (MultiMap)new MultiHashMap();
        LOGGER = Logger.getLogger("MDMLogger");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Admin", (Object)"MDM_EncryptionMgmt_Admin");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Admin", (Object)"ModernMgmt_EncryptionMgmt_Admin");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Admin", (Object)"MDM_DataUsage_Admin");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Read", (Object)"MDM_DataUsage_Read");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Write", (Object)"MDM_DataUsage_Write");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Inventory_Admin", (Object)"MDM_DataUsage_Admin");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Inventory_Write", (Object)"MDM_Inventory_Write");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Inventory_Read", (Object)"MDM_DataUsage_Read");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Admin", (Object)"MDM_CertMgmt_Admin");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Read", (Object)"MDM_CertMgmt_Read");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Write", (Object)"MDM_CertMgmt_Write");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Admin", (Object)"ModernMgmt_CertMgmt_Admin");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Read", (Object)"ModernMgmt_CertMgmt_Read");
        MDMMappedRoleListener.MAPPEDROLESFORMODULE.put((Object)"MDM_Configurations_Write", (Object)"ModernMgmt_CertMgmt_Write");
    }
}
