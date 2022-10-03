package com.me.devicemanagement.framework.server.authorization.summaryserver.probe;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.authorization.RoleListenerHandler;
import com.me.devicemanagement.framework.server.authorization.RoleEvent;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DCUserConstants;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.authorization.RoleHandler;

public class PSRoleHandler extends RoleHandler
{
    public int addRoles(final String roleName, final Long roleID, final String desc, final String adminName, final String[] roleList) throws Exception {
        try {
            final Criteria checkCriteria = new Criteria(new Column("UMRole", "UM_ROLE_NAME"), (Object)roleName.trim(), 0, false);
            final DataObject dObject = SyMUtil.getPersistence().get("UMRole", checkCriteria);
            final Row row = dObject.getRow("UMRole", checkCriteria);
            if (row != null) {
                final int errorCode = row.get("STATUS").equals(DCUserConstants.HIDDEN_ROLE) ? 40017 : 40018;
                PSRoleHandler.logger.log(Level.WARNING, "Role name {0} already exists", roleName);
                return errorCode;
            }
            final WritableDataObject writableDataObject = new WritableDataObject();
            final Row roleRow = new Row("UMRole");
            roleRow.set("UM_ROLE_ID", (Object)roleID);
            roleRow.set("UM_ROLE_NAME", (Object)roleName);
            roleRow.set("ADMIN_NAME", (Object)adminName);
            roleRow.set("UM_ROLE_DESCRIPTION", (Object)desc);
            roleRow.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
            roleRow.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
            writableDataObject.addRow(roleRow);
            final Criteria criteria = new Criteria(new Column("UMModule", "ROLE_ID"), (Object)roleList, 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMModule"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> umModuleRow = dataObject.getRows("UMModule");
            final ArrayList<Object> dcModuleID = new ArrayList<Object>();
            while (umModuleRow.hasNext()) {
                final Row moduleRow = umModuleRow.next();
                final Long moduleID = (Long)moduleRow.get("UM_MODULE_ID");
                dcModuleID.add(moduleRow.get("DC_MODULE_ID"));
                final Row roleMapping = new Row("UMRoleModuleRelation");
                roleMapping.set("UM_MODULE_ID", (Object)moduleID);
                roleMapping.set("UM_ROLE_ID", roleRow.get("UM_ROLE_ID"));
                writableDataObject.addRow(roleMapping);
            }
            this.getHomePageViewDetails((DataObject)writableDataObject, dcModuleID);
            SyMUtil.getPersistence().add((DataObject)writableDataObject);
            final RoleEvent roleEvent = new RoleEvent((Long)writableDataObject.getFirstValue("UMRole", 1));
            RoleListenerHandler.getInstance().invokeRoleListeners(roleEvent, 1000);
            PSRoleHandler.logger.log(Level.WARNING, "Role : '" + roleName + "' has been successfully added");
            DCEventLogUtil.getInstance().addEvent(707, adminName, null, "dc.admin.uac.ROLE_ADD_SUCCESS", roleName, true);
            return 40015;
        }
        catch (final Exception e) {
            PSRoleHandler.logger.log(Level.WARNING, "Exception while create role time :", e);
            return 40016;
        }
    }
}
