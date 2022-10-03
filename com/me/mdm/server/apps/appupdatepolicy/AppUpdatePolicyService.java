package com.me.mdm.server.apps.appupdatepolicy;

import com.me.mdm.api.paging.model.PagingResponse;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import java.util.ArrayList;
import com.me.mdm.server.device.api.model.MetaDataModel;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.paging.SearchUtil;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyListModel;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicySearchModel;
import java.util.List;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import org.json.simple.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.server.util.MDMTransactionManager;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import java.util.logging.Logger;

public class AppUpdatePolicyService
{
    Logger logger;
    
    public AppUpdatePolicyService() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public AppUpdatePolicyModel getAppUpdatePolicy(final Long profileId) {
        try {
            final AppUpdatePolicyModel appUpdatePolicyModel = new AppUpdatePolicyModel();
            final DataObject appUpdatePolicyDO = AppUpdatePolicyDBHandler.getInstance().getAppUpdatePolicyDO(profileId);
            AppUpdatePolicyProfileHandler.getInstance().getAppUpdatePolicyProfile(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicyHandler.getInstance().getAppUpdatePolicyConfiguration(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicyToPackageConfigHandler.getInstance().getAppUpdateConfigToPackageRelation(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicyToPackageListHandler.getInstance().setAppUpdatePolicyPackageList(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdateDeploymentPolicyHandler.getInstance().setAppUpdateDeploymentPolicy(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicySchedulerHandler.getInstance().getAppUpdateScheduler(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicyDistributedCountHandler.getInstance().getAppUpdatePolicyDistributedCount(appUpdatePolicyModel);
            appUpdatePolicyModel.setIsStoreAppPolicy();
            return appUpdatePolicyModel;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppUpdatePolicy", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addAppUpdatePolicy(final AppUpdatePolicyModel appUpdatePolicyModel) throws APIHTTPException {
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            mdmTransactionManager.begin();
            this.logger.log(Level.INFO, "add app update policy invoked");
            final DataObject appUpdatePolicyDO = (DataObject)new WritableDataObject();
            AppUpdatePolicyProfileHandler.getInstance().addOrUpdateAppUpdatePolicyProfile(appUpdatePolicyModel);
            AppUpdatePolicyHandler.getInstance().addAppUpdatePolicyConfiguration(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicyToPackageConfigHandler.getInstance().addAppUpdateConfigToPackageConfigRelation(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicyToPackageListHandler.getInstance().addAppUpdatePolicyToPackageListRelation(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdateDeploymentPolicyHandler.getInstance().addOrUpdateAppUpdateDeploymentPolicy(appUpdatePolicyModel, appUpdatePolicyDO);
            CollectionToAppUpdateConfigHandler.getInstance().addCollectionToAppUpdateConfigRelation(appUpdatePolicyModel, appUpdatePolicyDO);
            AppUpdatePolicySchedulerHandler.getInstance().addAppUpdateScheduler(appUpdatePolicyModel);
            AppUpdatePolicyCollnToScheduleRepoHandler.getInstance().addAppUpdatePolicyCollnToScheduleRepoRelation(appUpdatePolicyModel, appUpdatePolicyDO);
            MDMUtil.getPersistence().add(appUpdatePolicyDO);
            AppUpdatePolicyProfilePublishHandler.getInstance().publishAppUpdatePolicy(appUpdatePolicyModel);
            mdmTransactionManager.commit();
            final JSONObject secLog = new JSONObject();
            secLog.put((Object)"PROFILE_ID", (Object)appUpdatePolicyModel.getProfileId());
            secLog.put((Object)"COLLECTION_ID", (Object)appUpdatePolicyModel.getCollectionId());
            secLog.put((Object)"REMARKS", (Object)"create-success");
            MDMOneLineLogger.log(Level.INFO, "APP_UPDATE_POLICY_CREATED", secLog);
            this.logger.log(Level.INFO, "add app update policy completed ");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addAppUpdatePolicy", ex);
            try {
                mdmTransactionManager.rollBack();
            }
            catch (final Exception exception) {
                this.logger.log(Level.SEVERE, "Exception in rollback transaction addAppUpdatePolicy");
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateAppUpdatePolicy(final AppUpdatePolicyModel appUpdatePolicyModel) throws APIHTTPException {
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            mdmTransactionManager.begin();
            this.logger.log(Level.INFO, "update app update policy invoked with props {0}", appUpdatePolicyModel.getProfileId());
            final Long oldCollectionId = ProfileHandler.getRecentProfileCollectionID(appUpdatePolicyModel.getProfileId());
            AppUpdatePolicyModifySchedulerHandler.getInstance().validateAndRemoveExistingSchedule(appUpdatePolicyModel);
            this.addAppUpdatePolicy(appUpdatePolicyModel);
            final Long newCollectionId = ProfileHandler.getRecentProfileCollectionID(appUpdatePolicyModel.getProfileId());
            AppUpdatePolicyPostModificationListener.getInstance().invokePostPolicyModificationListener(appUpdatePolicyModel, oldCollectionId, newCollectionId);
            mdmTransactionManager.commit();
            final JSONObject secLog = new JSONObject();
            secLog.put((Object)"PROFILE_ID", (Object)appUpdatePolicyModel.getProfileId());
            secLog.put((Object)"COLLECTION_ID", (Object)appUpdatePolicyModel.getCollectionId());
            secLog.put((Object)"REMARKS", (Object)"update-success");
            MDMOneLineLogger.log(Level.INFO, "APP_UPDATE_POLICY_CREATED", secLog);
            this.logger.log(Level.INFO, "update app update policy completed with props {0}", appUpdatePolicyModel.getCollectionId());
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception in update app update policy ...");
            try {
                mdmTransactionManager.rollBack();
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in update app policy roll back");
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteAppUpdatePolicy(final AppUpdatePolicyModel appUpdatePolicyModel) throws APIHTTPException {
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            mdmTransactionManager.begin();
            final Boolean isStoreAppPolicy = AppUpdatePolicyDBHandler.getInstance().checkIfGivenPolicyIsStoreAppPolicy(appUpdatePolicyModel.getProfileId());
            if (isStoreAppPolicy) {
                this.logger.log(Level.INFO, "Store app policy being disassociated {0}", appUpdatePolicyModel.getProfileId());
                ExistingStoreAppPolicyHandler.getInstance().deleteExistingStoreAppPolicy(appUpdatePolicyModel.getCustomerId());
                mdmTransactionManager.commit();
                return;
            }
            AppUpdatePolicyModifySchedulerHandler.getInstance().validateAndRemoveExistingSchedule(appUpdatePolicyModel);
            final List<Long> collectionIdList = AppUpdatePolicyDBHandler.getInstance().getListOfCollectionsForGivenPolicy(appUpdatePolicyModel.getProfileId());
            final List<Long> policyIds = AppUpdatePolicyDBHandler.getInstance().getListOfPoliciesForGivenProfile(appUpdatePolicyModel.getProfileId());
            this.logger.log(Level.INFO, "Deleting policy Id {0}", appUpdatePolicyModel.getProfileId());
            DataAccess.delete("Profile", new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)appUpdatePolicyModel.getProfileId(), 0));
            this.logger.log(Level.INFO, "Deleting collections {0} of app update policy", collectionIdList);
            DataAccess.delete("Collection", new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)collectionIdList.toArray(), 8));
            this.logger.log(Level.INFO, "Deleting app update configurations {0} for app update policy", policyIds);
            DataAccess.delete("AutoAppUpdateConfigDetails", new Criteria(Column.getColumn("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)policyIds.toArray(), 8));
            mdmTransactionManager.commit();
            final JSONObject secLog = new JSONObject();
            secLog.put((Object)"PROFILE_ID", (Object)appUpdatePolicyModel.getProfileId());
            secLog.put((Object)"REMARKS", (Object)"delete-success");
            MDMOneLineLogger.log(Level.INFO, "APP_UPDATE_POLICY_CREATED", secLog);
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception in delete app update policy", exception);
            try {
                mdmTransactionManager.rollBack();
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception roll back in delete app update policy");
            }
        }
    }
    
    public AppUpdatePolicyListModel getAppUpdatePolicies(final AppUpdatePolicySearchModel appUpdatePolicySearchModel) {
        try {
            final Criteria searchCriteria = SearchUtil.setSearchCriteria(appUpdatePolicySearchModel);
            final PagingUtil pagingUtil = appUpdatePolicySearchModel.getPagingUtil();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCustomerRel", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentPubProfileToColln", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)appUpdatePolicySearchModel.getCustomerId(), 0);
            selectQuery.setCriteria(MDMDBUtil.andCriteria(searchCriteria, customerCriteria));
            selectQuery.addSelectColumn(new Column("Profile", "*"));
            selectQuery.addSelectColumn(new Column("AaaUser", "*"));
            final int totalCount = MDMDBUtil.getCount(selectQuery, "Profile", "PROFILE_ID");
            final AppUpdatePolicyListModel appUpdatePolicyListModel = new AppUpdatePolicyListModel();
            final MetaDataModel meta = new MetaDataModel();
            meta.setTotalCount(totalCount);
            appUpdatePolicyListModel.setMetadata(meta);
            final List<AppUpdatePolicyModel> appUpdatePolicyModelList = new ArrayList<AppUpdatePolicyModel>();
            if (totalCount > 0) {
                if (!appUpdatePolicySearchModel.isSelectAll()) {
                    final PagingResponse pagingJSON = pagingUtil.getPagingResponse(totalCount);
                    if (pagingJSON != null) {
                        appUpdatePolicyListModel.setPaging(pagingJSON);
                    }
                    selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                }
                final org.json.JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("profilename")) {
                        selectQuery.addSortColumn(new SortColumn("Profile", "PROFILE_NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    selectQuery.addSortColumn(new SortColumn("Profile", "PROFILE_ID", true));
                }
                final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
                while (dmDataSetWrapper.next()) {
                    final AppUpdatePolicyModel appUpdatePolicyModel = new AppUpdatePolicyModel();
                    appUpdatePolicyModel.setPolicyName((String)dmDataSetWrapper.getValue("PROFILE_NAME"));
                    appUpdatePolicyModel.setProfileId((Long)dmDataSetWrapper.getValue("PROFILE_ID"));
                    appUpdatePolicyModel.setCreatedUser((String)dmDataSetWrapper.getValue("FIRST_NAME"));
                    appUpdatePolicyModel.setModifiedTime((Long)dmDataSetWrapper.getValue("LAST_MODIFIED_TIME"));
                    appUpdatePolicyModel.setIsStoreAppPolicy();
                    appUpdatePolicyModelList.add(appUpdatePolicyModel);
                }
                appUpdatePolicyListModel.setAppUpdatePolicyModels(appUpdatePolicyModelList);
            }
            return appUpdatePolicyListModel;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in getAppUpdatePolicies", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
