package com.me.devicemanagement.framework.server.roles.handler;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.DeleteQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class RoleHandlerImpl
{
    private Logger logger;
    private static RoleHandlerImpl roleHandler;
    
    public RoleHandlerImpl() {
        this.logger = Logger.getLogger(RoleHandlerImpl.class.getName());
    }
    
    public static synchronized RoleHandlerImpl getInstance() {
        if (RoleHandlerImpl.roleHandler == null) {
            RoleHandlerImpl.roleHandler = new RoleHandlerImpl();
        }
        return RoleHandlerImpl.roleHandler;
    }
    
    public void deleteRolesFromAAAAuthorizedTable(final String[] roles) {
        final String methodName = "deleteRolesFromAAAAuthorizedTable";
        try {
            final Criteria aaaRoleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)roles, 8);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AaaAuthorizedRole");
            deleteQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            deleteQuery.setCriteria(aaaRoleCriteria);
            SyMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occurred in Class : RoleHandler ::  Method : deleteRolesFromAAAAuthorizedTable.  Exception is :: " + exp.getMessage());
        }
    }
    
    public void populateRolesinAAAAuthorizedTable(final String[] roles) {
        final String methodName = "populateRolesinAAAAuthorizedTable";
        DataSet dataSet = null;
        Connection conn = null;
        try {
            final Criteria aaaRoleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)roles, 8);
            final Criteria roleJoinCriteria = new Criteria(Column.getColumn("AaaAuthorizedRole", "ROLE_ID"), (Object)Column.getColumn("AaaRole", "ROLE_ID"), 0);
            final Criteria accountJoinCriteria = new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)Column.getColumn("AaaAccount", "ACCOUNT_ID"), 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
            selectQuery.addJoin(new Join("AaaRole", "UMModule", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            selectQuery.addJoin(new Join("UMModule", "UMRoleModuleRelation", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2));
            selectQuery.addJoin(new Join("UMRoleModuleRelation", "UsersRoleMapping", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
            selectQuery.addJoin(new Join("UsersRoleMapping", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", roleJoinCriteria.and(accountJoinCriteria), 1));
            final Criteria aaaAuthorizedRoleCriteria = new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)null, 0);
            selectQuery.setCriteria(aaaRoleCriteria.and(aaaAuthorizedRoleCriteria));
            selectQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
            final DataObject aaaAuthorizedRoleDO = (DataObject)new WritableDataObject();
            conn = RelationalAPI.getInstance().getConnection();
            dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, conn);
            while (dataSet.next()) {
                final Row aaaAuthorizedRoleRow = new Row("AaaAuthorizedRole");
                aaaAuthorizedRoleRow.set("ACCOUNT_ID", (Object)dataSet.getValue("ACCOUNT_ID"));
                aaaAuthorizedRoleRow.set("ROLE_ID", (Object)dataSet.getValue("ROLE_ID"));
                aaaAuthorizedRoleDO.addRow(aaaAuthorizedRoleRow);
            }
            SyMUtil.getPersistence().add(aaaAuthorizedRoleDO);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occurred in Class : RoleHandler ::  Method : populateRolesinAAAAuthorizedTable.  Exception is :: " + exp.getMessage());
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.WARNING, "Unable to Close DataSet in Class : RoleHandler ::  Method : populateRolesinAAAAuthorizedTable.  Exception is :: " + exp.getMessage());
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.WARNING, "Unable to Close Connection in Class : RoleHandler ::  Method : populateRolesinAAAAuthorizedTable.  Exception is :: " + exp.getMessage());
            }
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception exp2) {
                this.logger.log(Level.WARNING, "Unable to Close DataSet in Class : RoleHandler ::  Method : populateRolesinAAAAuthorizedTable.  Exception is :: " + exp2.getMessage());
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp2) {
                this.logger.log(Level.WARNING, "Unable to Close Connection in Class : RoleHandler ::  Method : populateRolesinAAAAuthorizedTable.  Exception is :: " + exp2.getMessage());
            }
        }
    }
    
    private void deleteRemovedUserModuleRoles() {
        final String methodName = "deleteRemovedModuleRoles";
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AaaAuthorizedRole");
            deleteQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            deleteQuery.addJoin(new Join("AaaRole", "UMModule", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            deleteQuery.addJoin(new Join("UMModule", "DCUserModule", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            deleteQuery.addJoin(new Join("DCUserModule", "DCUserModuleExtn", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 1));
            final Criteria dcuserModuleExtnCriteria = new Criteria(Column.getColumn("DCUserModuleExtn", "MODULE_ID"), (Object)null, 0);
            deleteQuery.setCriteria(dcuserModuleExtnCriteria);
            SyMUtil.getPersistence().delete(deleteQuery);
            this.logger.log(Level.INFO, RelationalAPI.getInstance().getDeleteSQL(deleteQuery));
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occurred in Class : RoleHandler ::  Method : deleteRemovedModuleRoles.  Exception is :: " + exp.getMessage());
        }
    }
    
    private void populateNewlyAddedUserModuleRoles() {
        final String methodName = "populateAddedModuleRoles";
        DataSet dataSet = null;
        Connection conn = null;
        try {
            final Criteria roleJoinCriteria = new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), (Object)Column.getColumn("AaaAuthorizedRole", "ROLE_ID"), 0);
            final Criteria accountIdJoinCriteria = new Criteria(Column.getColumn("AaaAccount", "ACCOUNT_ID"), (Object)Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), 0);
            final Criteria aaaAuthorizedRoleCriteria = new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)null, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCUserModule"));
            selectQuery.addJoin(new Join("DCUserModule", "DCUserModuleExtn", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            selectQuery.addJoin(new Join("DCUserModuleExtn", "UMModule", new String[] { "MODULE_ID" }, new String[] { "DC_MODULE_ID" }, 2));
            selectQuery.addJoin(new Join("UMModule", "UMRoleModuleRelation", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2));
            selectQuery.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            selectQuery.addJoin(new Join("UMRoleModuleRelation", "UsersRoleMapping", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
            selectQuery.addJoin(new Join("UsersRoleMapping", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", roleJoinCriteria.and(accountIdJoinCriteria), 1));
            selectQuery.setCriteria(aaaAuthorizedRoleCriteria);
            selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            conn = RelationalAPI.getInstance().getConnection();
            dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, conn);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            while (dataSet.next()) {
                final Row aaaAuthorizedRoleRow = new Row("AaaAuthorizedRole");
                aaaAuthorizedRoleRow.set("ACCOUNT_ID", (Object)dataSet.getValue("ACCOUNT_ID"));
                aaaAuthorizedRoleRow.set("ROLE_ID", (Object)dataSet.getValue("ROLE_ID"));
                dataObject.addRow(aaaAuthorizedRoleRow);
            }
            this.logger.log(Level.INFO, RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
            SyMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occurred in Class : RoleHandler ::  Method : populateAddedModuleRoles.  Exception is :: " + exp.getMessage());
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.WARNING, "Unable to Close DataSet in Class : RoleHandler ::  Method : populateAddedModuleRoles.  Exception is :: " + exp.getMessage());
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.WARNING, "Unable to Close Connection in Class : RoleHandler ::  Method : populateAddedModuleRoles.  Exception is :: " + exp.getMessage());
            }
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception exp2) {
                this.logger.log(Level.WARNING, "Unable to Close DataSet in Class : RoleHandler ::  Method : populateAddedModuleRoles.  Exception is :: " + exp2.getMessage());
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception exp2) {
                this.logger.log(Level.WARNING, "Unable to Close Connection in Class : RoleHandler ::  Method : populateAddedModuleRoles.  Exception is :: " + exp2.getMessage());
            }
        }
    }
    
    public void handleUserModuleRoles() {
        this.deleteRemovedUserModuleRoles();
        this.populateNewlyAddedUserModuleRoles();
    }
    
    static {
        RoleHandlerImpl.roleHandler = null;
    }
}
