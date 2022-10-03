package com.me.mdm.server.apps.tracks;

import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import java.util.List;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Map;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.DeleteQueryImpl;
import org.json.JSONObject;
import java.util.HashMap;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class AppTrackUtil
{
    public static Logger appMgmtLogger;
    
    public DataObject getLatestVersionsForApp(final Long customerId, final String identifier) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "BusinessStoreAppVersion", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppReleaseLabel", "ReleaseLabelToAppTrack", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 1));
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0);
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria activeTrackCriteria = new Criteria(new Column("ReleaseLabelToAppTrack", "STATUS"), (Object)0, 1);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(customerCriteria.and(appGroupCriteria).and(activeTrackCriteria));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public void fillLatestApplicableTrackVersions(final Long packageId, final Long appGroupId, final Long customerId, final JSONArray applicableVersions, final Long releaseLabelId) {
        final Map<String, String> versionCodeToName = new HashMap<String, String>();
        for (int i = 0; i < applicableVersions.length(); ++i) {
            final String versionCode = String.valueOf(((JSONObject)applicableVersions.get(i)).get("APP_NAME_SHORT_VERSION"));
            final String versionName = String.valueOf(((JSONObject)applicableVersions.get(i)).get("APP_VERSION"));
            versionCodeToName.put(versionCode, versionName);
        }
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("BusinessStoreAppVersion");
            final Criteria packageCriteria = new Criteria(new Column("BusinessStoreAppVersion", "PACKAGE_ID"), (Object)packageId, 0);
            final Criteria removableVersionCodeCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_NAME_SHORT_VERSION"), (Object)versionCodeToName.keySet().toArray(), 9);
            final Criteria removableVersionCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_VERSION"), (Object)versionCodeToName.values().toArray(), 9);
            final Criteria releaseLabelCriteria = new Criteria(new Column("BusinessStoreAppVersion", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            deleteQuery.setCriteria(packageCriteria.and(releaseLabelCriteria).and(removableVersionCriteria.or(removableVersionCodeCriteria)));
            MDMUtil.getPersistence().delete(deleteQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BusinessStoreAppVersion"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(packageCriteria);
            DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject == null || dataObject.isEmpty()) {
                dataObject = (DataObject)new WritableDataObject();
            }
            Criteria appVersionCriteria = null;
            Criteria appVersionCodeCriteria = null;
            final List<String> versionCodeList = new ArrayList<String>(versionCodeToName.keySet());
            for (int j = 0; j < versionCodeList.size(); ++j) {
                final String versionCode2 = versionCodeList.get(j);
                final String versionName2 = versionCodeToName.get(versionCode2);
                appVersionCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_VERSION"), (Object)versionName2, 0);
                appVersionCodeCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_NAME_SHORT_VERSION"), (Object)versionCode2, 0);
                final Row row = new Row("BusinessStoreAppVersion");
                row.set("PACKAGE_ID", (Object)packageId);
                row.set("APP_GROUP_ID", (Object)appGroupId);
                row.set("APP_NAME_SHORT_VERSION", (Object)versionCode2);
                row.set("APP_VERSION", (Object)versionName2);
                row.set("RELEASE_LABEL_ID", (Object)releaseLabelId);
                if (dataObject.getRow("BusinessStoreAppVersion", packageCriteria.and(appVersionCriteria.and(appVersionCodeCriteria).and(releaseLabelCriteria))) == null) {
                    dataObject.addRow(row);
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException e) {
            AppTrackUtil.appMgmtLogger.log(Level.WARNING, "Cannot update latest version for the packageId {0}, Reason :{1}", new Object[] { packageId, e });
        }
    }
    
    public Map<Long, String> getLatestTrackForCollection(final List<Long> collnList) {
        final Map<Long, String> collnToTrackMapping = new HashMap<Long, String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReleaseLabelToAppTrack"));
        selectQuery.addJoin(new Join("ReleaseLabelToAppTrack", "AppGroupToCollection", new String[] { "APP_GROUP_ID", "RELEASE_LABEL_ID" }, new String[] { "APP_GROUP_ID", "RELEASE_LABEL_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(new Column("AppCollnToReleaseLabelHistory", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("ReleaseLabelToAppTrack", "TRACK_ID"));
        selectQuery.addSelectColumn(new Column("ReleaseLabelToAppTrack", "RELEASE_LABEL_ID"));
        selectQuery.addSortColumn(new SortColumn("AppCollnToReleaseLabelHistory", "LABEL_ASSIGNED_TIME", false));
        final Criteria collnCriteria = new Criteria(new Column("AppCollnToReleaseLabelHistory", "COLLECTION_ID"), (Object)collnList.toArray(), 8);
        final Criteria activeTrackCriteria = new Criteria(new Column("ReleaseLabelToAppTrack", "STATUS"), (Object)1, 0);
        selectQuery.setCriteria(collnCriteria.and(activeTrackCriteria));
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dmDataSetWrapper.next()) {
                final Long collectionId = (Long)dmDataSetWrapper.getValue("COLLECTION_ID");
                if (!collnToTrackMapping.containsKey(collectionId)) {
                    collnToTrackMapping.put(collectionId, (String)dmDataSetWrapper.getValue("TRACK_ID"));
                }
            }
        }
        catch (final Exception e) {
            AppTrackUtil.appMgmtLogger.log(Level.SEVERE, "Cannot fetch track id details {0}", e);
        }
        return collnToTrackMapping;
    }
    
    public void deprecateAppTracks(final List deprecatedTracks, final Long appGroupId) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ReleaseLabelToAppTrack");
            updateQuery.addJoin(new Join("ReleaseLabelToAppTrack", "AppGroupToCollection", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            updateQuery.setUpdateColumn("STATUS", (Object)0);
            final Criteria appGroupCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria deprecatedTracksCriteria = new Criteria(new Column("ReleaseLabelToAppTrack", "TRACK_ID"), (Object)deprecatedTracks.toArray(), 8);
            updateQuery.setCriteria(appGroupCriteria.and(deprecatedTracksCriteria));
            MDMUtil.getPersistence().update(updateQuery);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("BusinessStoreAppVersion");
            deleteQuery.addJoin(new Join("BusinessStoreAppVersion", "ReleaseLabelToAppTrack", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            deleteQuery.addJoin(new Join("ReleaseLabelToAppTrack", "AppGroupToCollection", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            deleteQuery.setCriteria(deprecatedTracksCriteria);
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final DataAccessException e) {
            AppTrackUtil.appMgmtLogger.log(Level.SEVERE, "Cannot deprecate tracks with no versions ", (Throwable)e);
        }
    }
    
    public void resumeAppTracks(final String trackId, final Long appGroupId) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ReleaseLabelToAppTrack");
            updateQuery.addJoin(new Join("ReleaseLabelToAppTrack", "AppGroupToCollection", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            updateQuery.setUpdateColumn("STATUS", (Object)1);
            final Criteria appGroupCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria deprecatedTracksCriteria = new Criteria(new Column("ReleaseLabelToAppTrack", "TRACK_ID"), (Object)trackId, 0);
            updateQuery.setCriteria(appGroupCriteria.and(deprecatedTracksCriteria));
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final DataAccessException e) {
            AppTrackUtil.appMgmtLogger.log(Level.SEVERE, "Cannot resume tracks ", (Throwable)e);
        }
    }
    
    public Long getChannelForAppTrack(final Long appGroupId, final Long customerId, final String trackId, final String trackName) {
        Long releaseLabelId = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReleaseLabelToAppTrack"));
        selectQuery.addSelectColumn(new Column("ReleaseLabelToAppTrack", "*"));
        final Criteria appGroupIdCritiera = new Criteria(new Column("ReleaseLabelToAppTrack", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria trackIdCriteria = new Criteria(new Column("ReleaseLabelToAppTrack", "TRACK_ID"), (Object)trackId, 0);
        selectQuery.setCriteria(appGroupIdCritiera.and(trackIdCriteria));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ReleaseLabelToAppTrack");
                releaseLabelId = (Long)row.get("RELEASE_LABEL_ID");
            }
            else {
                releaseLabelId = AppVersionDBUtil.getInstance().addChannel(customerId, trackName);
            }
        }
        catch (final Exception e) {
            AppTrackUtil.appMgmtLogger.log(Level.WARNING, "Cannot fetch release channel details", e);
        }
        return releaseLabelId;
    }
    
    public JSONObject getBasicTrackDetails(final Long appGroupId, final String trackId) {
        final JSONObject apptrackDetails = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReleaseLabelToAppTrack"));
        selectQuery.addJoin(new Join("ReleaseLabelToAppTrack", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.addSelectColumn(new Column("AppReleaseLabel", "*"));
        final Criteria appGroupCriteria = new Criteria(new Column("ReleaseLabelToAppTrack", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria trackIdCriteria = new Criteria(new Column("ReleaseLabelToAppTrack", "TRACK_ID"), (Object)trackId, 0);
        selectQuery.setCriteria(appGroupCriteria.and(trackIdCriteria));
        try {
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSetWrapper.next()) {
                final String trackName = (String)dataSetWrapper.getValue("RELEASE_LABEL_DISPLAY_NAME");
                apptrackDetails.put("RELEASE_LABEL_DISPLAY_NAME", (Object)trackName);
            }
        }
        catch (final Exception e) {
            AppTrackUtil.appMgmtLogger.log(Level.SEVERE, "Cannot fetch app track details ", e);
        }
        return apptrackDetails;
    }
    
    public void updateReleaseLabelToAppTrack(final JSONObject appTrackReleaseLabel) throws DataAccessException {
        final String trackId = (String)appTrackReleaseLabel.get("TRACK_ID");
        final Long releaseLabelId = appTrackReleaseLabel.getLong("RELEASE_LABEL_ID");
        final Long customerId = appTrackReleaseLabel.getLong("CUSTOMER_ID");
        final Long appGroupId = appTrackReleaseLabel.getLong("APP_GROUP_ID");
        final String releaseLabelName = (String)appTrackReleaseLabel.get("RELEASE_LABEL_DISPLAY_NAME");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReleaseLabelToAppTrack"));
        final Criteria trackIdCriteria = new Criteria(Column.getColumn("ReleaseLabelToAppTrack", "TRACK_ID"), (Object)trackId, 0);
        final Criteria releaseLabelIdCriteria = new Criteria(Column.getColumn("ReleaseLabelToAppTrack", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("ReleaseLabelToAppTrack", "APP_GROUP_ID"), (Object)appGroupId, 0);
        selectQuery.setCriteria(trackIdCriteria.and(releaseLabelIdCriteria).and(appGroupCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ReleaseLabelToAppTrack", "*"));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        if (!dao.isEmpty()) {
            AppTrackUtil.appMgmtLogger.log(Level.INFO, "Already an entry for TRACK ID-{0} and RELEASE_LABEL_ID-{1} is found in the table RELEASELABELTOAPPTRACK ", new Object[] { trackId, releaseLabelId });
            final AppTrackEvent appTrackEvent = new AppTrackEvent(customerId, appGroupId, trackId);
            appTrackEvent.trackName = releaseLabelName;
            appTrackEvent.appName = appTrackReleaseLabel.optString("PROFILE_NAME");
            appTrackEvent.version = appTrackReleaseLabel.optString("APP_VERSION", "--");
            new AppTrackHandler().invokeOperation(appTrackEvent, 3);
        }
        else {
            final Row labelHistoryRow = new Row("ReleaseLabelToAppTrack");
            labelHistoryRow.set("TRACK_ID", (Object)trackId);
            labelHistoryRow.set("RELEASE_LABEL_ID", (Object)releaseLabelId);
            labelHistoryRow.set("STATUS", (Object)1);
            labelHistoryRow.set("APP_GROUP_ID", (Object)appGroupId);
            dao.addRow(labelHistoryRow);
            MDMUtil.getPersistence().add(dao);
            final AppTrackEvent appTrackEvent2 = new AppTrackEvent(customerId, appGroupId, trackId);
            appTrackEvent2.appName = (String)appTrackReleaseLabel.get("PROFILE_NAME");
            appTrackEvent2.trackName = (String)appTrackReleaseLabel.get("RELEASE_LABEL_DISPLAY_NAME");
            new AppTrackHandler().invokeOperation(appTrackEvent2, 1);
        }
    }
    
    static {
        AppTrackUtil.appMgmtLogger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
