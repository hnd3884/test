package com.adventnet.sym.server.mdm.apps.ios;

import java.util.Hashtable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Properties;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class IOSModifiedEnterpriseAppsUtil
{
    private static final Logger logger;
    
    private static String getEnterpriseAppsCustomSuffix() {
        return "(iosinternalenterpriseapp)";
    }
    
    private static boolean isEnterpriseSuffixRequired(final String identifier, final int platformType, final Long customerID) {
        boolean isEnterpriseSuffixRequired = false;
        try {
            final String tempIdentifier = identifier.replace(getEnterpriseAppsCustomSuffix(), "");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
            final Criteria appCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)new String[] { tempIdentifier, tempIdentifier + getEnterpriseAppsCustomSuffix() }, 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(appCriteria.and(platformCriteria).and(customerCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator appItr = dataObject.getRows("MdPackageToAppGroup");
                while (appItr.hasNext()) {
                    final Row packageRow = appItr.next();
                    final int packageType = (int)packageRow.get("PACKAGE_TYPE");
                    final Long appGroupID = (Long)packageRow.get("APP_GROUP_ID");
                    final Row appGroupRow = dataObject.getRow("MdAppGroupDetails", new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0));
                    final String existingAppIdentifier = (String)appGroupRow.get("IDENTIFIER");
                    if (packageType == 0 || packageType == 1) {
                        isEnterpriseSuffixRequired = true;
                        IOSModifiedEnterpriseAppsUtil.logger.log(Level.INFO, "The app : {0} is already available as a Store App.", new Object[] { existingAppIdentifier });
                    }
                    if (packageType == 2 && existingAppIdentifier.equals(tempIdentifier + getEnterpriseAppsCustomSuffix())) {
                        isEnterpriseSuffixRequired = true;
                        IOSModifiedEnterpriseAppsUtil.logger.log(Level.INFO, "The app: {0} is already available as an Enterprise App with a suffix");
                    }
                    if (isEnterpriseSuffixRequired) {
                        return isEnterpriseSuffixRequired;
                    }
                }
            }
        }
        catch (final Exception e) {
            IOSModifiedEnterpriseAppsUtil.logger.log(Level.SEVERE, "Exception in isAppAvailableAsStoreApp", e);
        }
        return isEnterpriseSuffixRequired;
    }
    
    public static JSONObject getMDMPropsForApp(String identifier, String appName, final boolean isEnterpriseApp, final Long customerID) {
        final JSONObject appMDMProps = new JSONObject();
        try {
            if (isEnterpriseApp && isEnterpriseSuffixRequired(identifier, 1, customerID)) {
                identifier = getCustomBundleIDForEnterpriseApp(identifier);
                appName = getCustomAppNameForEnterpriseApp(appName);
                appMDMProps.put("IDENTIFIER", (Object)identifier);
                appMDMProps.put("APP_NAME", (Object)appName);
            }
        }
        catch (final Exception e) {
            IOSModifiedEnterpriseAppsUtil.logger.log(Level.SEVERE, "Exception in verifyIfETSEnabledAndGetAppIdentifier", e);
        }
        return appMDMProps;
    }
    
    public static Properties getDistributedAppProps(final String identifier, final Long customerID, final Long resourceID) {
        final Properties distributedAppProps = new Properties();
        boolean isAppAvailableWithSuffix = false;
        boolean isActualIdentifierAvailable = false;
        try {
            final String tempIdentifier = identifier.replace(getEnterpriseAppsCustomSuffix(), "");
            final Criteria identifierCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)new String[] { getCustomBundleIDForEnterpriseApp(tempIdentifier), tempIdentifier }, 8);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria finalCriteria = identifierCri.and(customerCriteria).and(resCriteria);
            final DataObject appCatalogDo = AppsUtil.getAppCatalogAppGroupDetailsDO(finalCriteria);
            final Iterator appCatItr = appCatalogDo.getRows("MdAppCatalogToResource");
            while (appCatItr.hasNext()) {
                final Row appCatRow = appCatItr.next();
                final Long appGroupID = (Long)appCatRow.get("APP_GROUP_ID");
                final Row appGrpRow = appCatalogDo.getRow("MdAppGroupDetails", new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0));
                final Row packageRow = appCatalogDo.getRow("MdPackageToAppGroup", new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupID, 0));
                final int packageType = (int)packageRow.get("PACKAGE_TYPE");
                final String existingIdentifier = (String)appGrpRow.get("IDENTIFIER");
                if (existingIdentifier.equals(tempIdentifier)) {
                    isActualIdentifierAvailable = true;
                }
                if (existingIdentifier.equals(tempIdentifier + getEnterpriseAppsCustomSuffix()) && packageType == 2) {
                    isAppAvailableWithSuffix = true;
                }
            }
        }
        catch (final Exception e) {
            IOSModifiedEnterpriseAppsUtil.logger.log(Level.SEVERE, "Exception in isAppAvailableWithSuffix", e);
        }
        ((Hashtable<String, Boolean>)distributedAppProps).put("isAppAvailableWithSuffix", isAppAvailableWithSuffix);
        ((Hashtable<String, Boolean>)distributedAppProps).put("isActualIdentifierAvailable", isActualIdentifierAvailable);
        return distributedAppProps;
    }
    
    public static String getCustomBundleIDForEnterpriseApp(String identifier) {
        if (identifier != null && !identifier.contains(getEnterpriseAppsCustomSuffix())) {
            identifier += getEnterpriseAppsCustomSuffix();
        }
        return identifier;
    }
    
    public static String getCustomAppNameForEnterpriseApp(String appName) {
        if (appName != null && !appName.contains("(BETA)")) {
            appName += " (BETA)";
        }
        return appName;
    }
    
    public static String getOriginalBundleIDOfEnterpriseApp(String bundleID) {
        if (bundleID.contains(getEnterpriseAppsCustomSuffix())) {
            bundleID = bundleID.replace(getEnterpriseAppsCustomSuffix(), "");
        }
        return bundleID;
    }
    
    public static List getOriginalBundleIDList(final List appList) {
        if (appList != null && !appList.isEmpty()) {
            final List tempList = new ArrayList(appList);
            for (int i = 0; i < tempList.size(); ++i) {
                String tempBundleID = tempList.get(i);
                if (tempBundleID.contains(getEnterpriseAppsCustomSuffix())) {
                    appList.remove(tempBundleID);
                    tempBundleID = getOriginalBundleIDOfEnterpriseApp(tempBundleID);
                    if (!appList.contains(tempBundleID)) {
                        appList.add(tempBundleID);
                    }
                }
            }
        }
        return appList;
    }
    
    static {
        logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
