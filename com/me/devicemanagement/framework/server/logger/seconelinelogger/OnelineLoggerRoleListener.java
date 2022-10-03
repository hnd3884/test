package com.me.devicemanagement.framework.server.logger.seconelinelogger;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authorization.RoleEvent;
import com.me.devicemanagement.framework.server.authorization.RoleListener;

public class OnelineLoggerRoleListener implements RoleListener
{
    @Override
    public void roleAdded(final RoleEvent customerEvent) {
    }
    
    @Override
    public void roleDeleted(final RoleEvent customerEvent) {
    }
    
    @Override
    public void roleUpdated(final RoleEvent customerEvent) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UMRole"));
        selectQuery.addJoin(new Join("UMRole", "UsersRoleMapping", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
        selectQuery.addJoin(new Join("UsersRoleMapping", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
        selectQuery.setCriteria(new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)customerEvent.roleID, 0));
        try {
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AaaLogin");
                while (iterator.hasNext()) {
                    final Row roleMapping = iterator.next();
                    final Long userId = (Long)roleMapping.get("USER_ID");
                    OneLineLoggerThreadLocal.invalidateRoleNameInCache(userId.toString());
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(OnelineLoggerRoleListener.class.getName()).log(Level.WARNING, "Error while updating logged in user's role name in cache", ex);
            SecurityOneLineLogger.log("User_Management", "Role_Modification", "exception ->{1} occuredin role modification listener for the role {0}", new String[] { customerEvent.roleID.toString(), ex.getMessage() }, Level.SEVERE);
        }
    }
}
