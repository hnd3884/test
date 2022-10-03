package com.me.mdm.server.apps.appupdatepolicy;

import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.ArrayList;
import com.me.mdm.server.profiles.api.model.ProfileAssociationToGroupModel;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.api.controller.IDauthorizer;

public class AppUpdatePolicyAuthorizer implements IDauthorizer
{
    Logger logger;
    
    public AppUpdatePolicyAuthorizer() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void authorize(final String customerIDstr, final Long userID, final String pathParam, final List<Object> idList) throws Exception {
        if (idList != null && !idList.isEmpty()) {
            final Long[] tempArray = this.convertStringListToLongAr(idList);
            final List tempList = new LinkedList(Arrays.asList(tempArray));
            switch (pathParam) {
                case "app_update_policy_id": {
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                    selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                    final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)Long.parseLong(customerIDstr), 0);
                    final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)12, 0);
                    final Criteria profileIdCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)tempArray, 8);
                    selectQuery.setCriteria(customerCriteria.and(profileTypeCriteria).and(profileIdCriteria));
                    selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
                    final DataObject dataObject = DataAccess.get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        final Iterator<Row> iterator = dataObject.getRows("Profile");
                        while (iterator.hasNext()) {
                            final Row row = iterator.next();
                            final Object profileId = row.get("PROFILE_ID");
                            tempList.remove(profileId);
                        }
                        break;
                    }
                    break;
                }
                case "package_ids": {
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
                    final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)Long.parseLong(customerIDstr), 0);
                    final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)tempArray, 8);
                    selectQuery.setCriteria(customerCriteria.and(packageCriteria));
                    selectQuery.addSelectColumn(new Column("MdPackage", "PACKAGE_ID"));
                    final DataObject dataObject2 = DataAccess.get(selectQuery);
                    if (!dataObject2.isEmpty()) {
                        final Iterator<Row> iterator2 = dataObject2.getRows("MdPackage");
                        while (iterator2.hasNext()) {
                            final Row row2 = iterator2.next();
                            final Long packageId = (Long)row2.get("PACKAGE_ID");
                            tempList.remove(packageId);
                        }
                        break;
                    }
                    break;
                }
                case "group_ids": {
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
                    selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Long.parseLong(customerIDstr), 0);
                    final Criteria packageCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)tempArray, 8);
                    selectQuery.setCriteria(customerCriteria.and(packageCriteria));
                    selectQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
                    final DataObject dataObject2 = DataAccess.get(selectQuery);
                    if (!dataObject2.isEmpty()) {
                        final Iterator<Row> iterator2 = dataObject2.getRows("Resource");
                        while (iterator2.hasNext()) {
                            final Row row2 = iterator2.next();
                            final Long packageId = (Long)row2.get("RESOURCE_ID");
                            tempList.remove(packageId);
                        }
                        break;
                    }
                    break;
                }
            }
            if (!tempList.isEmpty()) {
                this.logger.log(Level.SEVERE, "Exception in AppUpdatePolicyAuthorizer unknown id {0} for path param {1}", new Object[] { tempList, pathParam });
                throw new APIHTTPException("COM0008", new Object[] { tempList.toString() });
            }
        }
    }
    
    public void validateAppUpdatePolicyModel(final AppUpdatePolicyModel appUpdatePolicyModel) throws APIHTTPException {
        try {
            final Boolean isAllApps = appUpdatePolicyModel.getIsAllApps();
            final List packageList = appUpdatePolicyModel.getPackageList();
            if (!isAllApps && packageList.isEmpty()) {
                this.logger.log(Level.SEVERE, "Exception in validateAppUpdatePolicyModel isAllApps set {0} and packageList is empty", new Object[] { isAllApps });
                throw new APIHTTPException("COM0015", new Object[] { "All apps set false and App list is empty" });
            }
            this.authorize(String.valueOf(appUpdatePolicyModel.getCustomerId()), appUpdatePolicyModel.getUserId(), "package_ids", packageList);
            final Integer windowStartTime = (appUpdatePolicyModel.getWindowStartTime() == null) ? null : Integer.valueOf(Integer.parseInt(appUpdatePolicyModel.getWindowStartTime()));
            final Integer windowEndTime = (appUpdatePolicyModel.getWindowEndTime() == null) ? null : Integer.valueOf(Integer.parseInt(appUpdatePolicyModel.getWindowEndTime()));
            if (windowStartTime != null && windowEndTime != null && Math.abs(windowEndTime - windowStartTime) < 180) {
                this.logger.log(Level.SEVERE, "Exception in validateAppUpdatePolicyModel deployment window is less than 3 hrs. Window start time - {0} Window end time - {1}", new Object[] { windowStartTime, windowEndTime });
                throw new APIHTTPException("COM0015", new Object[] { "Deployment window can not be less than three hours" });
            }
            final Long policyId = appUpdatePolicyModel.getProfileId();
            Criteria profileCriteria = null;
            if (policyId != null) {
                profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)policyId, 1);
            }
            if (new ProfileHandler().checkProfileNameExist(appUpdatePolicyModel.getCustomerId(), appUpdatePolicyModel.getPolicyName(), 12, profileCriteria)) {
                throw new APIHTTPException("APP0040", new Object[0]);
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in validateAppUpdatePolicyModel", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void validateProfileAssociationGroupModel(final ProfileAssociationToGroupModel profileAssociationToGroupModel) throws APIHTTPException {
        try {
            List policyIds = profileAssociationToGroupModel.getProfileIds();
            if (policyIds == null || policyIds.isEmpty()) {
                policyIds = new ArrayList() {
                    {
                        this.add(profileAssociationToGroupModel.getProfileId());
                    }
                };
            }
            this.authorize(String.valueOf(profileAssociationToGroupModel.getCustomerId()), profileAssociationToGroupModel.getUserId(), "app_update_policy_id", policyIds);
            List groupIds = profileAssociationToGroupModel.getGroupIds();
            if (groupIds == null || groupIds.isEmpty()) {
                groupIds = new ArrayList() {
                    {
                        this.add(profileAssociationToGroupModel.getGroupId());
                    }
                };
            }
            this.authorize(String.valueOf(profileAssociationToGroupModel.getCustomerId()), profileAssociationToGroupModel.getUserId(), "group_ids", groupIds);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in validateProfileAssociationGroupModel", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void checkIfAppUpdatePolicyFeatureEnabled() throws APIHTTPException {
        final Boolean isFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableScheduleAppUpdates");
        if (!isFeatureEnabled) {
            throw new APIHTTPException("COM0015", new Object[] { "Feature un supported" });
        }
    }
}
