package com.me.ems.framework.common.core;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.EMSServiceUtil;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.audit.EventLogUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class ActionLogDAOUtil
{
    protected Logger logger;
    
    public ActionLogDAOUtil() {
        this.logger = Logger.getLogger(ActionLogDAOUtil.class.getName());
    }
    
    public List<Map<String, String>> getEventModuleList() throws Exception {
        final List<Map<String, String>> modulesList = new ArrayList<Map<String, String>>();
        final List<String> actionLogNames = new ArrayList<String>();
        actionLogNames.add("Vulnerability");
        actionLogNames.add("Misconfiguration");
        actionLogNames.add("Web Server Misconfiguration");
        actionLogNames.add("Network Device");
        try {
            final Map<String, String> actionLogModules = EventLogUtil.getInstance().getActionLogModules();
            if (CustomerInfoUtil.getInstance().isMSP()) {
                actionLogModules.remove("AD Reports");
                actionLogModules.put("SoM", I18N.getMsg("dc.common.Agent", new Object[0]));
                if (actionLogModules.containsKey("Network Device")) {
                    actionLogModules.remove("Network Device");
                }
            }
            for (final Map.Entry<String, String> moduleObject : actionLogModules.entrySet()) {
                final Map<String, String> moduleMap = new HashMap<String, String>(3);
                final String actionLogName = moduleObject.getKey();
                if (actionLogNames.contains(actionLogName)) {
                    if (!CustomerInfoUtil.isVMPProduct() && !EMSServiceUtil.isVulnerabilityEnabled()) {
                        continue;
                    }
                    moduleMap.put("value", actionLogName);
                    moduleMap.put("label", moduleObject.getValue());
                    modulesList.add(moduleMap);
                    if (CustomerInfoUtil.isVMPProduct() || !actionLogName.equalsIgnoreCase("Network Device")) {
                        continue;
                    }
                    modulesList.remove(moduleMap);
                }
                else {
                    moduleMap.put("value", actionLogName);
                    moduleMap.put("label", moduleObject.getValue());
                    modulesList.add(moduleMap);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "ActionLogViewer Exception: Exception while getting event module list", ex);
            throw ex;
        }
        return modulesList;
    }
    
    public List<Map<String, String>> getUsernamesList(final User user) {
        final List<Map<String, String>> dcUsernamesList = new ArrayList<Map<String, String>>();
        List<Hashtable> dcUsersList;
        if (user.isAdminUser()) {
            dcUsersList = DMUserHandler.getDCUsers();
        }
        else {
            final List customerIdsList = CustomerInfoUtil.getInstance().getCustomerIDsForLogIn(user.getLoginID());
            final Criteria customerCriteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerIdsList.toArray(), 8);
            dcUsersList = DMUserHandler.getDCUsers(customerCriteria);
        }
        for (final Hashtable dcUserTable : dcUsersList) {
            final Map<String, String> moduleMap = new HashMap<String, String>();
            final String username = dcUserTable.get("NAME").toString();
            moduleMap.put("label", username);
            moduleMap.put("value", username);
            dcUsernamesList.add(moduleMap);
        }
        return dcUsernamesList;
    }
    
    public List<Integer> isReasonBoxEnabledValues(final List customerIds) throws DataAccessException {
        final List<Integer> isReasonBoxEnabledList = new ArrayList<Integer>();
        final SelectQuery reasonBoxQuery = (SelectQuery)new SelectQueryImpl(new Table("RCSettings"));
        reasonBoxQuery.addSelectColumn(new Column("RCSettings", "IS_REASONBOX_ENABLED"));
        final Criteria custCrit = new Criteria(new Column("RCSettings", "CUSTOMER_ID"), (Object)customerIds.toArray(), 8);
        final DataObject resultDO = SyMUtil.getPersistence().get("RCSettings", custCrit);
        final Iterator rowItr = resultDO.getRows("RCSettings");
        while (rowItr.hasNext()) {
            final Row row = rowItr.next();
            isReasonBoxEnabledList.add((int)row.get("IS_REASONBOX_ENABLED"));
        }
        return isReasonBoxEnabledList;
    }
}
