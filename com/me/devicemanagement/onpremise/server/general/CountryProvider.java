package com.me.devicemanagement.onpremise.server.general;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import org.json.simple.parser.JSONParser;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.TimeZone;
import java.util.Locale;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class CountryProvider
{
    private static Logger logger;
    private static CountryProvider instance;
    public static String userSettings;
    public static String timezoneVSCountryCodeDBJsonPath;
    public static String ifModSinceKey;
    private static JSONObject timezoneVSCountryCodeDB;
    private static String[] timezoneIds;
    
    public CountryProvider() {
        this.loadTimeZoneIdVSCountryCodeDB();
    }
    
    public static CountryProvider getInstance() {
        if (CountryProvider.instance == null) {
            CountryProvider.instance = new CountryProvider();
        }
        return CountryProvider.instance;
    }
    
    public String readCountryFromCustomerInfoFile() {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            final File customerInfoFile = new File(System.getProperty("server.home") + File.separator + "logs" + File.separator + "customerInfo.txt");
            if (customerInfoFile.exists()) {
                fileReader = new FileReader(customerInfoFile);
                bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("Country=")) {
                        return line.substring("Country=".length()).trim();
                    }
                }
            }
            else {
                CountryProvider.logger.log(Level.INFO, "customerInfo.txt file not found");
            }
        }
        catch (final Exception e) {
            CountryProvider.logger.log(Level.SEVERE, "Exception occurred while fetch country from customer info file.", e);
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (final Exception e) {
                CountryProvider.logger.log(Level.WARNING, "Exception occurred while closing reader.", e);
            }
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (final Exception e2) {
                CountryProvider.logger.log(Level.WARNING, "Exception occurred while closing reader.", e2);
            }
        }
        return null;
    }
    
    public String countryCodeFromDefaultTimeZoneID() {
        String countryCode = Locale.getDefault().getCountry();
        try {
            String zoneId = TimeZone.getDefault().getID();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUserProfile", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUserProfile", "TIMEZONE"));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUser", "CREATEDTIME"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUserStatus", "USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUserStatus", "STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("UsersRoleMapping", "LOGIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("UsersRoleMapping", "UM_ROLE_ID"));
            selectQuery.addJoin(new Join("AaaUserProfile", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUserProfile", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            final Criteria userStatus = new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0);
            final Criteria role = new Criteria(new Column("UsersRoleMapping", "UM_ROLE_ID"), (Object)DBUtil.getUVHValue("UMRole:UM_ROLE_ID:1"), 0);
            selectQuery.setCriteria(userStatus.and(role));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("AaaUser", "CREATEDTIME"), true));
            selectQuery.setRange(new Range(0, 1));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AaaUserProfile");
                CountryProvider.logger.log(Level.INFO, "Administrator user ID which is create first : " + row);
                if (row.get("TIMEZONE") != null) {
                    zoneId = row.get("TIMEZONE").toString();
                }
            }
            CountryProvider.logger.log(Level.INFO, "Administrator user Zone ID which is create first : " + zoneId);
            if (CountryProvider.timezoneVSCountryCodeDB.containsKey((Object)zoneId)) {
                countryCode = CountryProvider.timezoneVSCountryCodeDB.get((Object)zoneId).toString();
            }
            else {
                CountryProvider.logger.log(Level.INFO, "There is country from timezoneVScountrycode db - zone id : " + zoneId);
            }
        }
        catch (final Exception e) {
            CountryProvider.logger.log(Level.INFO, "Exception occurred while determine country code from time zone id : ", e);
        }
        CountryProvider.logger.log(Level.INFO, "Country code : " + countryCode);
        return countryCode;
    }
    
    private void loadTimeZoneIdVSCountryCodeDB() {
        try {
            if (CountryProvider.timezoneVSCountryCodeDB == null) {
                CountryProvider.timezoneVSCountryCodeDB = (JSONObject)new JSONParser().parse(new String(FileAccessUtil.getFileAsByteArray(CountryProvider.timezoneVSCountryCodeDBJsonPath), "UTF-8"));
                final String[] timezoneIdsFromDB = new String[CountryProvider.timezoneVSCountryCodeDB.size()];
                final Set<String> keys = CountryProvider.timezoneVSCountryCodeDB.keySet();
                int i = 0;
                for (final String key : keys) {
                    timezoneIdsFromDB[i] = key;
                    ++i;
                }
                Arrays.sort(timezoneIdsFromDB);
                CountryProvider.timezoneIds = timezoneIdsFromDB;
                CountryProvider.logger.log(Level.INFO, "Timezone vs country code db loaded successfully.");
            }
            else {
                CountryProvider.logger.log(Level.INFO, "Timezone vs country code db already loaded.");
            }
        }
        catch (final Exception e) {
            CountryProvider.logger.log(Level.INFO, "Exception occurred while loading  timezone vs country code db : ", e);
        }
    }
    
    public void reinitializeTimeZoneIdVSCountryCodeDB() {
        CountryProvider.timezoneVSCountryCodeDB = null;
        this.loadTimeZoneIdVSCountryCodeDB();
    }
    
    public LinkedHashMap getTimeZoneStringFromTimeZoneDB() throws Exception {
        return SyMUtil.getTimeZoneString(CountryProvider.timezoneIds);
    }
    
    public void syncTimeZoneIdVSCountryCodeDBFromCRS() {
        try {
            final String dbUrl = ProductUrlLoader.getInstance().getValue("timezone_vs_country_db_url");
            final Properties headers = new Properties();
            final Properties userGeneralProps = FileAccessUtil.readProperties(CountryProvider.userSettings);
            if (userGeneralProps.containsKey(CountryProvider.ifModSinceKey) && userGeneralProps.getProperty(CountryProvider.ifModSinceKey).trim().length() > 0) {
                ((Hashtable<String, String>)headers).put("If-Modified-Since", userGeneralProps.getProperty(CountryProvider.ifModSinceKey).trim());
                CountryProvider.logger.log(Level.INFO, "Headers for crs download : " + headers);
            }
            final DownloadStatus downloadStatus = DownloadManager.getInstance().downloadFile(dbUrl, CountryProvider.timezoneVSCountryCodeDBJsonPath, (Properties)null, headers, new SSLValidationType[0]);
            if (downloadStatus.getStatus() == 0) {
                CountryProvider.logger.log(Level.INFO, "Time zone vs country db download success, Going to reinitialize exists db.");
                this.reinitializeTimeZoneIdVSCountryCodeDB();
                final Properties properties = new Properties();
                ((Hashtable<String, String>)properties).put(CountryProvider.ifModSinceKey, downloadStatus.getLastModifiedTime());
                FileAccessUtil.storeProperties(properties, CountryProvider.userSettings, true);
            }
            else if (downloadStatus.getStatus() == 10010) {
                CountryProvider.logger.log(Level.INFO, "Time zone vs country db not modified");
            }
            else {
                CountryProvider.logger.log(Level.WARNING, "Time zone vs country db download failed.");
            }
        }
        catch (final Exception e) {
            CountryProvider.logger.log(Level.INFO, "Unable to sync time zone vs country db from crs : ", e);
        }
    }
    
    static {
        CountryProvider.logger = Logger.getLogger(CountryProvider.class.getName());
        CountryProvider.instance = null;
        CountryProvider.userSettings = System.getProperty("server.home") + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "user_settings.conf";
        CountryProvider.timezoneVSCountryCodeDBJsonPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "timezoneid-vs-countrycode-db.json";
        CountryProvider.ifModSinceKey = "timezone_vs_countrycode_db_ifmodsince";
        CountryProvider.timezoneVSCountryCodeDB = null;
        CountryProvider.timezoneIds = null;
    }
}
