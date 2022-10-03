package com.me.mdm.server.msp.sync;

import java.util.Iterator;
import com.adventnet.ds.query.UpdateQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.Set;

public class AppsMoveToAllCustomerHandler extends BaseConfigurationsSyncEngine
{
    String appIdentifier;
    Set<String> appVersionsSet;
    
    AppsMoveToAllCustomerHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
        this.appVersionsSet = new HashSet<String>();
        this.appIdentifier = this.qData.optString("IDENTIFIER");
    }
    
    @Override
    public JSONObject getChildSpecificUVH(final Long customerId) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        final Criteria appIdCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)this.appIdentifier, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.addSelectColumn(new Column("MdPackage", "*"));
        selectQuery.setCriteria(appIdCriteria.and(customerCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row packageRow = dataObject.getFirstRow("MdPackage");
            jsonObject.put("PACKAGE_ID", packageRow.get("PACKAGE_ID"));
            jsonObject.put("CUSTOMER_ID", packageRow.get("CUSTOMER_ID"));
        }
        return jsonObject;
    }
    
    @Override
    public void setParentDO() throws Exception {
    }
    
    private DMDataSetWrapper getAllAvailableAppVersionsFor(final Long customerID) throws Exception {
        final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria appIdentifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)this.appIdentifier, 0);
        selectQuery.setCriteria(customerCriteria.and(appIdentifierCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        return dmDataSetWrapper;
    }
    
    private Boolean checkIfAppPresentInChildCustomer(final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria appIdentifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)this.appIdentifier, 0);
        selectQuery.setCriteria(customerCriteria.and(appIdentifierCriteria));
        selectQuery.setCriteria(appIdentifierCriteria.and(customerCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    private Boolean checkIfSpecificAppVersionPresentInChildCustomer(final DMDataSetWrapper dmDataSetWrapper, final Long childCustomerId) throws DataAccessException {
        final String appVersion = (String)dmDataSetWrapper.getValue("APP_VERSION");
        final String appVersionCode = (String)dmDataSetWrapper.getValue("APP_NAME_SHORT_VERSION");
        final String appIdentifier = (String)dmDataSetWrapper.getValue("IDENTIFIER");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
        selectQuery.addJoin(new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria appVersionCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)appVersion, 0);
        final Criteria appVersionCodeCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)appVersionCode, 0);
        final Criteria appIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)appIdentifier, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppDetails", "CUSTOMER_ID"), (Object)childCustomerId, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.setCriteria(appVersionCriteria.and(appVersionCodeCriteria).and(appIdentifierCriteria).and(customerCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    private void restoreAppIfInTrash() {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Profile");
            updateQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            updateQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            updateQuery.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)this.appIdentifier, 0, (boolean)AppsUtil.getInstance().getIsBundleIdCaseSenstive(this.platform));
            final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)this.platform, 0);
            updateQuery.setCriteria(identifierCriteria.and(platformCriteria));
            updateQuery.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)false);
            DataAccess.update(updateQuery);
        }
        catch (final Exception ex) {
            AppsMoveToAllCustomerHandler.logger.log(Level.SEVERE, "Exception while restoring app from trash in AppsMoveToAllCustomerHAndler", ex);
        }
    }
    
    @Override
    public void sync() {
        try {
            AppsMoveToAllCustomerHandler.logger.log(Level.INFO, "Inside sync AppMoveToAllCustomerHandler from customer {0}", new Object[] { this.customerId });
            this.restoreAppIfInTrash();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
            selectQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
            final DataObject parentCustomerDO = DataAccess.get(selectQuery);
            final Iterator<Row> iterator = parentCustomerDO.getRows("CustomerInfo");
            final Set<Long> appNewlyAddedCustomerSet = new HashSet<Long>();
            while (iterator.hasNext()) {
                final Row customerInfoRow = iterator.next();
                final Long parentCustomerId = (Long)customerInfoRow.get("CUSTOMER_ID");
                final DMDataSetWrapper dmDataSetWrapper = this.getAllAvailableAppVersionsFor(parentCustomerId);
                AppsMoveToAllCustomerHandler.logger.log(Level.INFO, "Iterating through all app versions {0} of customer {1}", new Object[] { dmDataSetWrapper, parentCustomerId });
                while (dmDataSetWrapper.next()) {
                    final String appVersion = (String)dmDataSetWrapper.getValue("APP_VERSION");
                    final String appVersionCode = (String)dmDataSetWrapper.getValue("APP_NAME_SHORT_VERSION");
                    final String appVersionUniqueIdentifier = appVersion + "@@@" + appVersionCode;
                    if (this.appVersionsSet.contains(appVersionUniqueIdentifier)) {
                        AppsMoveToAllCustomerHandler.logger.log(Level.INFO, "App version already iterated hence skipping {0} {1}", new Object[] { appVersionUniqueIdentifier, parentCustomerId });
                    }
                    else {
                        this.appVersionsSet.add(appVersionUniqueIdentifier);
                        AppsMoveToAllCustomerHandler.logger.log(Level.INFO, "New version found in customer {0} {1}", new Object[] { appVersionUniqueIdentifier, parentCustomerId });
                        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
                        selectQuery2.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
                        final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)parentCustomerId, 1);
                        selectQuery2.setCriteria(customerCriteria);
                        final DataObject childCustomerDO = DataAccess.get(selectQuery2);
                        final Iterator<Row> iterator2 = childCustomerDO.getRows("CustomerInfo");
                        while (iterator2.hasNext()) {
                            final Row childCustomerInfo = iterator2.next();
                            final Long childCustomerId = (Long)childCustomerInfo.get("CUSTOMER_ID");
                            AppsMoveToAllCustomerHandler.logger.log(Level.INFO, "Checking and adding app version for child customer {0}", new Object[] { childCustomerId });
                            if (!appNewlyAddedCustomerSet.contains(childCustomerId) && !this.checkIfAppPresentInChildCustomer(childCustomerId)) {
                                appNewlyAddedCustomerSet.add(childCustomerId);
                                AppsMoveToAllCustomerHandler.logger.log(Level.SEVERE, "Adding approved app version {0} for customer {1}", new Object[] { appVersionUniqueIdentifier, childCustomerId });
                                SyncConfigurationsUtil.addApprovedAppVersion((Long)dmDataSetWrapper.getValue("PACKAGE_ID"), childCustomerId);
                            }
                            else if (!this.checkIfSpecificAppVersionPresentInChildCustomer(dmDataSetWrapper, childCustomerId)) {
                                AppsMoveToAllCustomerHandler.logger.log(Level.SEVERE, "Adding new app version {0} for customer {1}", new Object[] { appVersionUniqueIdentifier, childCustomerId });
                                SyncConfigurationsUtil.addNonApprovedAppVersions((Long)dmDataSetWrapper.getValue("PACKAGE_ID"), (Long)dmDataSetWrapper.getValue("RELEASE_LABEL_ID"), childCustomerId);
                            }
                            else {
                                AppsMoveToAllCustomerHandler.logger.log(Level.INFO, "App version {0} already present in child customer {1}", new Object[] { appVersionUniqueIdentifier, childCustomerId });
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            AppsMoveToAllCustomerHandler.logger.log(Level.SEVERE, "Exception in AppMoveToAllCustomerHandler", ex);
        }
    }
}
