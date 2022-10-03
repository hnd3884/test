package com.me.mdm.server.backup;

import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import org.json.JSONArray;

public class MDMAppsBackupListener implements MDMBackupListener
{
    @Override
    public JSONArray getFileBackupDetails() throws Exception {
        final JSONArray data = new JSONArray();
        final HashMap<String, Integer> appNameList = new HashMap<String, Integer>();
        final String directoryToCopy = "Apps";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "FILE_UPLOAD_SIZE"));
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdAppDetails", "CUSTOMER_ID"), true));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.mac", 1));
        selectQuery.setCriteria(criteria);
        int start = 1;
        int end;
        final int limit = end = 500;
        int counter;
        do {
            counter = 0;
            selectQuery.setRange(new Range(start, end));
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dmDataSetWrapper.next()) {
                ++counter;
                String appName = "" + dmDataSetWrapper.getValue("PROFILE_NAME");
                final String appVersion = "" + dmDataSetWrapper.getValue("APP_VERSION");
                appName = appName + "_" + appVersion;
                final String fileLoc = "" + dmDataSetWrapper.getValue("APP_FILE_LOC");
                final String fileName = MDMAppMgmtHandler.getInstance().getFileNameFromFilePath(fileLoc);
                final String fileExtn = fileName.substring(fileName.lastIndexOf("."));
                if (appName.endsWith(fileExtn)) {
                    appName = appName.substring(0, appName.lastIndexOf("."));
                }
                if (appNameList.containsKey(appName + fileExtn)) {
                    final int nextVersionNumber = appNameList.get(appName + fileExtn) + 1;
                    appNameList.put(appName + fileExtn, nextVersionNumber);
                    appName = appName + "_" + nextVersionNumber + fileExtn;
                }
                else {
                    appName += fileExtn;
                    appNameList.put(appName, 1);
                }
                final long fileSize = (long)dmDataSetWrapper.getValue("FILE_UPLOAD_SIZE");
                final long customerId = (long)dmDataSetWrapper.getValue("CUSTOMER_ID");
                final JSONObject fileDet = new JSONObject();
                fileDet.put("fileName", (Object)appName);
                fileDet.put("filePath", (Object)fileLoc);
                fileDet.put("fileSize", fileSize);
                fileDet.put("directoryToCopy", (Object)directoryToCopy);
                fileDet.put("customerId", customerId);
                data.put((Object)fileDet);
            }
            start += limit;
            end += limit;
        } while (counter == limit);
        return data;
    }
}
