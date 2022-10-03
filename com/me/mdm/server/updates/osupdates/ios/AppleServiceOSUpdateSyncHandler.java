package com.me.mdm.server.updates.osupdates.ios;

import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import java.util.List;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import com.me.mdm.server.cache.CachedWebAPIRequestHandler;
import com.me.mdm.http.TrustStoreHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.JSONObject;

public class AppleServiceOSUpdateSyncHandler
{
    private JSONObject responseJSON;
    private static String latestVersion;
    private Logger mdmLogger;
    
    public AppleServiceOSUpdateSyncHandler() {
        this.responseJSON = new JSONObject();
        this.mdmLogger = Logger.getLogger("MDMLogger");
        try {
            final ArrayList<String> certificatePath = new ArrayList<String>();
            certificatePath.add(this.getAppleRootCAPath());
            final URL url = new URL(this.getAppleUpdateURL());
            final HttpURLConnection httpURLConnection = new TrustStoreHandler().getHttpURLConnection(url, certificatePath);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(1000);
            httpURLConnection.setDoInput(true);
            final String response = new CachedWebAPIRequestHandler().getCachedURLGETRequest(httpURLConnection, "OS_UPDATE_CACHED_KEY");
            this.responseJSON = new JSONObject(response);
        }
        catch (final Exception e) {
            this.mdmLogger.log(Level.SEVERE, "Exception in Apple service sync handler", e);
        }
    }
    
    private String getAppleUpdateURL() {
        return "https://gdmf.apple.com/v2/pmv";
    }
    
    private String getAppleRootCAPath() throws Exception {
        return SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "MDM" + File.separator + "certs" + File.separator + "apple" + File.separator + "appleRootCA.cer";
    }
    
    public List<Long> getResourcesApplicableForCheckingUpdate(final List<Long> resourceList) {
        final List<Long> applicableResourceList = new ArrayList<Long>(resourceList);
        try {
            final JSONObject latestVersionObject = this.getLatestVersionObject();
            final List supportedDevices = JSONUtil.convertJSONArrayToList(latestVersionObject.getJSONArray("SupportedDevices"));
            final String osVersion = latestVersionObject.getString("ProductVersion");
            final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "PRODUCT_NAME"), (Object)supportedDevices.toArray(), 8);
            final Criteria resourceListCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Column versionColumn = new Column("OSUpdates", "VERSION");
            final SelectQuery osUpdateGroupQuery = (SelectQuery)new SelectQueryImpl(new Table("OSUpdates"));
            osUpdateGroupQuery.addJoin(new Join("OSUpdates", "IOSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2));
            osUpdateGroupQuery.addSelectColumn(new Column("OSUpdates", "VERSION"));
            final List groupOSVersionList = new ArrayList();
            groupOSVersionList.add(versionColumn);
            final GroupByClause groupByClause = new GroupByClause(groupOSVersionList);
            osUpdateGroupQuery.setGroupByClause(groupByClause);
            final DMDataSetWrapper wrapper = DMDataSetWrapper.executeQuery((Object)osUpdateGroupQuery);
            final List<String> osVersionList = new ArrayList<String>();
            while (wrapper.next()) {
                osVersionList.add((String)wrapper.getValue("VERSION"));
            }
            this.mdmLogger.log(Level.INFO, "List of Managed Updates:{0}", new Object[] { osVersionList });
            Criteria criteria = null;
            if (!osVersionList.isEmpty()) {
                final List<String> greaterVersionList = this.getGreaterVersion(osVersionList, osVersion);
                if (!greaterVersionList.isEmpty()) {
                    for (final String greaterVersion : greaterVersionList) {
                        if (criteria == null) {
                            criteria = new Criteria(new Column("OSUpdates", "VERSION"), (Object)greaterVersion, 0).or(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)greaterVersion, 0));
                        }
                        else {
                            criteria = criteria.or(new Criteria(new Column("OSUpdates", "VERSION"), (Object)greaterVersion, 0).or(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)greaterVersion, 0)));
                        }
                    }
                }
            }
            final Criteria osVersionCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)osVersion, 0).or(new Criteria(new Column("OSUpdates", "VERSION"), (Object)osVersion, 0));
            if (criteria == null) {
                criteria = osVersionCriteria;
            }
            else {
                criteria = criteria.or(osVersionCriteria);
            }
            final SelectQuery selectQuery = this.getManagedResourceUpdateQuery();
            selectQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.setCriteria(modelCriteria.and(resourceListCriteria).and(criteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MdDeviceInfo");
                while (iterator.hasNext()) {
                    final Row deviceRow = iterator.next();
                    applicableResourceList.remove(deviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.mdmLogger.log(Level.SEVERE, "Exception in getResourcesApplicableForCheckingUpdate", e);
        }
        return applicableResourceList;
    }
    
    private JSONObject getLatestVersionObject() throws Exception {
        JSONObject latestOSObject = new JSONObject();
        final JSONObject assetJSON = this.responseJSON.getJSONObject("AssetSets");
        final JSONArray availableOSJSON = assetJSON.getJSONArray("iOS");
        final VersionChecker versionChecker = new VersionChecker();
        for (int i = 0; i < availableOSJSON.length(); ++i) {
            final JSONObject osupdateObject = availableOSJSON.getJSONObject(i);
            final String osVersion = osupdateObject.getString("ProductVersion");
            if (versionChecker.isGreaterOrEqual(osVersion, AppleServiceOSUpdateSyncHandler.latestVersion)) {
                final JSONArray supportedArray = osupdateObject.getJSONArray("SupportedDevices");
                final String supportedArrayString = supportedArray.toString();
                if (supportedArrayString.contains("iPhone") || supportedArrayString.contains("iPad") || supportedArrayString.contains("iPod")) {
                    latestOSObject = osupdateObject;
                }
                AppleServiceOSUpdateSyncHandler.latestVersion = osVersion;
            }
        }
        return latestOSObject;
    }
    
    private SelectQuery getManagedResourceUpdateQuery() {
        final SelectQuery selectQuery = new ResourceOSUpdateDataHandler().getDeviceManagedUpdateQuery();
        selectQuery.addJoin(new Join("OSUpdates", "IOSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceAvailableOSUpdates", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        return selectQuery;
    }
    
    private List<String> getGreaterVersion(final List<String> osVersionList, final String osVersion) {
        final List<String> greaterVersion = new ArrayList<String>();
        final VersionChecker versionChecker = new VersionChecker();
        for (final String deviceOSVersion : osVersionList) {
            if (versionChecker.isGreater(deviceOSVersion, osVersion)) {
                greaterVersion.add(deviceOSVersion);
            }
        }
        return greaterVersion;
    }
    
    static {
        AppleServiceOSUpdateSyncHandler.latestVersion = "10.0";
    }
}
