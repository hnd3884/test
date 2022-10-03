package com.adventnet.sym.server.customreport;

import java.util.Iterator;
import com.adventnet.sym.server.admin.SoMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.authentication.DCUserHandler;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.customreport.CustomReportDetails;

public class CreateQuery extends com.me.devicemanagement.framework.server.customreport.CreateQuery
{
    public SelectQuery getCRSelectQuery(final CustomReportDetails crd) {
        try {
            this.deleteCRViewDetails(crd);
            CacheManager.getCacheRepository().clearCachedData();
            final DataObject moduleDO = this.getModuleDO(crd.moduleID, crd.subModuleID, crd.selectColumnList);
            final DataObject criModuleDO = this.getCriModuleDO(crd.moduleID, crd.subModuleID, crd.criteriaList);
            final DataObject joinDO = this.getJoinDO(crd.subModuleID);
            final DataObject joinCriteriaDO = this.getJoinCriteriaDO(crd.subModuleID);
            final SelectQuery crTempQuery = this.getCRSelectQuery(moduleDO, joinDO, joinCriteriaDO, crd.selectColumnList, criModuleDO);
            final SelectQuery crQuery = this.addCriteria(crTempQuery, crd.criteriaList, criModuleDO);
            this.addManagedByDCCriteria(crQuery);
            this.addUserIDCriteria(crQuery);
            this.addTechIDCriteria(crQuery);
            final String actualSQL = RelationalAPI.getInstance().getSelectSQL((Query)crQuery);
            CreateQuery.logger.log(Level.INFO, "***********************************************************************************************\n");
            CreateQuery.logger.log(Level.INFO, "ActualSQL Query is {0}", actualSQL);
            CreateQuery.logger.log(Level.INFO, "***********************************************************************************************\n");
            return crQuery;
        }
        catch (final Exception e) {
            CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getCRSelectQuery:", e);
            return null;
        }
    }
    
    public void addUserIDCriteria(final SelectQuery selectQuery) {
        CreateQuery.logger.log(Level.INFO, "Processing addUserIDCriteria method");
        final Long loginID = DCUserHandler.getLoginId();
        if (loginID != null && !DCUserHandler.isUserInRole(loginID, "All_Managed_Computer")) {
            if (this.isTablePresentInQuery(selectQuery, "InvComputer")) {
                selectQuery.addJoin(new Join("InvComputer", "UserResourceMapping", new String[] { "COMPUTER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            final Criteria usercrit = new Criteria(Column.getColumn("UserResourceMapping", "LOGIN_ID"), (Object)loginID, 0);
            final Criteria existingCrit = selectQuery.getCriteria();
            if (existingCrit != null) {
                selectQuery.setCriteria(existingCrit.and(usercrit));
            }
            else {
                selectQuery.setCriteria(usercrit);
            }
        }
    }
    
    public void addManagedByDCCriteria(final SelectQuery selectQuery) {
        CreateQuery.logger.log(Level.INFO, "Processing addManagedByDCCriteria method");
        if (this.isTablePresentInQuery(selectQuery, "InvComputer")) {
            final Criteria existingCrit = selectQuery.getCriteria();
            if (!this.isTablePresentInQuery(selectQuery, "ManagedComputer")) {
                selectQuery.addJoin(new Join("InvComputer", "ManagedComputer", new String[] { "COMPUTER_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final Criteria managedCri = SoMUtil.getInstance().getScopeCriteria();
                if (existingCrit != null) {
                    selectQuery.setCriteria(existingCrit.and(managedCri));
                }
                else {
                    selectQuery.setCriteria(managedCri);
                }
            }
        }
    }
    
    public void addTechIDCriteria(final SelectQuery selectQuery) {
        CreateQuery.logger.log(Level.INFO, "Processing addTechIDCriteria method");
        final Long loginID = DCUserHandler.getLoginId();
        CreateQuery.logger.log(Level.INFO, "Technician ID:{0}", loginID);
        if (loginID != null) {
            final Criteria existingCrit = selectQuery.getCriteria();
            if (this.isTablePresentInQuery(selectQuery, "InvTechHWSummary")) {
                final Criteria techIDCriteria = new Criteria(Column.getColumn("InvTechHWSummary", "TECH_ID"), (Object)loginID, 0);
                if (existingCrit != null) {
                    selectQuery.setCriteria(existingCrit.and(techIDCriteria));
                }
                else {
                    selectQuery.setCriteria(techIDCriteria);
                }
            }
            else if (this.isTablePresentInQuery(selectQuery, "InvTechSWSummary")) {
                final Criteria techIDCriteria = new Criteria(Column.getColumn("InvTechSWSummary", "TECH_ID"), (Object)loginID, 0);
                if (existingCrit != null) {
                    selectQuery.setCriteria(existingCrit.and(techIDCriteria));
                }
                else {
                    selectQuery.setCriteria(techIDCriteria);
                }
            }
        }
    }
    
    public void addUserIDCriteria(final SelectQuery selectQuery, final Long loginID) {
        CreateQuery.logger.log(Level.INFO, "Processing addUserIDCriteria method with the known user id");
        if (loginID != null && !DCUserHandler.isUserInRole(loginID, "All_Managed_Computer")) {
            if (this.isTablePresentInQuery(selectQuery, "InvComputer")) {
                selectQuery.addJoin(new Join("InvComputer", "UserResourceMapping", new String[] { "COMPUTER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            final Criteria usercrit = new Criteria(Column.getColumn("UserResourceMapping", "LOGIN_ID"), (Object)loginID, 0);
            final Criteria existingCrit = selectQuery.getCriteria();
            if (existingCrit != null) {
                selectQuery.setCriteria(existingCrit.and(usercrit));
            }
            else {
                selectQuery.setCriteria(usercrit);
            }
        }
    }
    
    public boolean isTablePresentInQuery(final SelectQuery selectQuery, final String newJointable) {
        final Iterator it = selectQuery.getJoins().iterator();
        Boolean isTablePresent = Boolean.FALSE;
        while (it.hasNext()) {
            final Join join = it.next();
            final String table = join.getReferencedTableAlias();
            if (table.equalsIgnoreCase(newJointable) || join.getBaseTableAlias().equalsIgnoreCase(newJointable)) {
                isTablePresent = Boolean.TRUE;
                break;
            }
        }
        return isTablePresent;
    }
}
