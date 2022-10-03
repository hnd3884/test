package com.me.devicemanagement.onpremise.server.search;

import com.adventnet.authentication.util.AuthUtil;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.search.CompleteSearchUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Date;
import com.me.devicemanagement.framework.server.search.AdvSearchLogger;
import com.me.devicemanagement.framework.server.search.SearchConfiguration;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.search.SearchTrackAPI;

public class SearchTrackImpl implements SearchTrackAPI
{
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String GLOBAL_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private Logger logger;
    private static Logger advSearchErrorLogger;
    private String sourceClass;
    
    public SearchTrackImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "SearchTrackImpl";
    }
    
    public void updateDetails(final Properties searchQueryProp) throws Exception {
        final String selectedSearchTab = searchQueryProp.getProperty("src");
        final String selectedCategory = searchQueryProp.getProperty("category");
        if (selectedSearchTab.equalsIgnoreCase("sett") || selectedSearchTab.equalsIgnoreCase("docs")) {
            if (selectedCategory != null && !selectedCategory.isEmpty()) {
                final String[] selectedCategoryArray = selectedCategory.split(",");
                this.incrementSearchTrackData("filtersCount");
            }
            else {
                this.incrementSearchTrackData("loadMoreCount");
            }
        }
    }
    
    public void updateSearchUsageDays(final String dateStr) throws Exception {
        final Properties lastDateProperties = this.getSearchTrackParams("lastDateUsed");
        if (lastDateProperties.stringPropertyNames().size() > 1) {
            final Date lastDateInDB = this.getDateFromString(this.getDataFromDB("lastDateUsed"));
            final Date latestLastDate = this.getDateFromString(dateStr);
            if (latestLastDate.after(lastDateInDB)) {
                this.incrementSearchTrackData("totalDaysUsed");
                if (SearchConfiguration.getConfiguration().isSearchEnabled()) {
                    AdvSearchLogger.getInstance().printVersion("OnUpdate Search Usage Days");
                }
            }
        }
        this.addOrUpdateSearchTrackParams("lastDateUsed", dateStr);
    }
    
    public void incrementSearchTrackData(final String key) throws JSONException {
        this.incrementSearchTrackParams(key, 1);
    }
    
    public String getDataFromDB(final String key) {
        final Properties properties = this.getSearchTrackParams(key);
        return properties.getProperty(key);
    }
    
    public JSONObject getDataAsJsonObject() throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchTrackParams"));
        query.addSelectColumn(Column.getColumn("SearchTrackParams", "SEARCH_TRACK_PARAM_ID"));
        query.addSelectColumn(Column.getColumn("SearchTrackParams", "PARAM_NAME"));
        query.addSelectColumn(Column.getColumn("SearchTrackParams", "PARAM_VALUE"));
        final DataObject dataObject = DataAccess.get(query);
        final Iterator rows = dataObject.getRows("SearchTrackParams");
        while (rows.hasNext()) {
            final Row row = rows.next();
            jsonObject.put(String.valueOf(row.get("PARAM_NAME")), (Object)String.valueOf(row.get("PARAM_VALUE")));
        }
        final JSONObject mainIndexDirJson = CompleteSearchUtil.getMainIndexDirJson();
        final String docVersion = String.valueOf(mainIndexDirJson.get("docMainIndex"));
        final String staticVersion = String.valueOf(mainIndexDirJson.get("staticMainIndex"));
        jsonObject.put("staticMainIndex", (Object)staticVersion);
        jsonObject.put("docMainIndex", (Object)docVersion);
        return jsonObject;
    }
    
    private void incrementSearchTrackParams(final String paramName, final int incrementBy) {
        final String sourceMethod = "incrementSearchTrackParams - overloaded";
        try {
            final Column col = Column.getColumn("SearchTrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject searchParamsDO = SyMUtil.getPersistence().get("SearchTrackParams", criteria);
            if (searchParamsDO.isEmpty()) {
                this.addSearchTrackParams(paramName, String.valueOf(incrementBy), searchParamsDO);
            }
            else {
                String paramValue = String.valueOf(searchParamsDO.getFirstRow("SearchTrackParams").get("PARAM_VALUE"));
                try {
                    paramValue = String.valueOf(Integer.parseInt(paramValue) + incrementBy);
                }
                catch (final Exception e) {
                    paramValue = String.valueOf(incrementBy);
                }
                this.updateSearchTrackParams(paramName, paramValue, searchParamsDO);
            }
        }
        catch (final DataAccessException e2) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - incrementSearchTrackParams() :  ", (Throwable)e2);
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e2);
        }
    }
    
    private void addSearchTrackParams(final String paramName, final String paramValue, final DataObject searchParamsDO) {
        final String sourceMethod = "addSearchTrackParams";
        try {
            final Row paramRow = new Row("SearchTrackParams");
            paramRow.set("PARAM_NAME", (Object)paramName);
            paramRow.set("PARAM_VALUE", (Object)paramValue);
            paramRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            searchParamsDO.addRow(paramRow);
            SyMUtil.getPersistence().add(searchParamsDO);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Parameter added in DB:- param name: " + paramName + "  param value: " + paramValue);
        }
        catch (final DataAccessException e) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - addSearchTrackParams() :  ", (Throwable)e);
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
        }
    }
    
    private void updateSearchTrackParams(final String paramName, final String paramValue, final DataObject searchParamsDO) {
        final String sourceMethod = "addSearchTrackParams";
        try {
            final Row paramRow = searchParamsDO.getFirstRow("SearchTrackParams");
            paramRow.set("PARAM_VALUE", (Object)paramValue);
            paramRow.set("LAST_UPDATED_TIME", (Object)System.currentTimeMillis());
            searchParamsDO.updateRow(paramRow);
            SyMUtil.getPersistence().update(searchParamsDO);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
        }
        catch (final DataAccessException e) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - updateSearchTrackParams() :  ", (Throwable)e);
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
        }
    }
    
    private Properties getSearchTrackParams(final String paramName) {
        final String sourceMethod = "getSearchTrackParams";
        final Properties searchParamProps = new Properties();
        try {
            final Column col = Column.getColumn("SearchTrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject dobj = SyMUtil.getPersistence().get("SearchTrackParams", criteria);
            if (dobj.isEmpty()) {
                return searchParamProps;
            }
            final Row searchTrackParams = dobj.getFirstRow("SearchTrackParams");
            final String formattedDate = this.getDateFromTimestamp(new Date(Long.valueOf(String.valueOf(searchTrackParams.get("LAST_UPDATED_TIME")))));
            searchParamProps.setProperty(String.valueOf(searchTrackParams.get("PARAM_NAME")), String.valueOf(searchTrackParams.get("PARAM_VALUE")));
            if (formattedDate != null) {
                searchParamProps.setProperty("LAST_MODIFIED_TIME", formattedDate);
            }
        }
        catch (final Exception ex) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - getSearchTrackParams() :  ", ex);
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred.", (Throwable)ex);
        }
        return searchParamProps;
    }
    
    private String getDateFromTimestamp(final Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final String formattedDate = sdf.format(date);
        return formattedDate;
    }
    
    private void addOrUpdateSearchTrackParams(final String paramName, final String paramValue) {
        final String sourceMethod = "addOrUpdateSearchTrackParams";
        try {
            final Column col = Column.getColumn("SearchTrackParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, false);
            final DataObject searchParamsDO = SyMUtil.getPersistence().get("SearchTrackParams", criteria);
            if (searchParamsDO.isEmpty()) {
                this.addSearchTrackParams(paramName, paramValue, searchParamsDO);
            }
            else {
                this.updateSearchTrackParams(paramName, paramValue, searchParamsDO);
            }
        }
        catch (final DataAccessException e) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - addOrUpdateSearchTrackParams() :  ", (Throwable)e);
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
        }
    }
    
    private Date getDateFromString(final String dateStr) {
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = df.parse(dateStr);
        }
        catch (final ParseException e) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - getDateFromString() :  ", e);
            SyMLogger.error(this.logger, this.sourceClass, "getDateFromString", "Exception occurred.", (Throwable)e);
        }
        return date;
    }
    
    public void updateSelectedResultDetails(final String featureResultSelected, final String articleResultSelected, final JSONObject jObj) throws JSONException {
        if (featureResultSelected != null && !featureResultSelected.isEmpty()) {
            final String[] featureResultSelectedArray = featureResultSelected.split(",");
            final int length = featureResultSelectedArray.length;
            this.incrementSearchTrackParams("selSettResCount", length);
            jObj.put("overallSelectedFeaturesResultsCount", length);
        }
        if (articleResultSelected != null && !articleResultSelected.isEmpty()) {
            final String[] articleResultSelectedArray = articleResultSelected.split(",");
            final int length = articleResultSelectedArray.length;
            this.incrementSearchTrackParams("selDocResCount", length);
            jObj.put("overallSelectedArticlesResultsCount", length);
        }
    }
    
    private String getPatternFromUVHTable(final long genValue) throws DataAccessException {
        final Criteria criteria1 = new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)"SearchParams", 0, false);
        final Criteria criteria2 = new Criteria(Column.getColumn("UVHValues", "COLUMN_NAME"), (Object)"PARAM_ID", 0, false);
        final Criteria criteria3 = new Criteria(Column.getColumn("UVHValues", "GENVALUES"), (Object)genValue, 0, false);
        final Criteria criteria4 = criteria1.and(criteria2).and(criteria3);
        final DataObject dataObject = SyMUtil.getPersistence().get("UVHValues", criteria4);
        final Row row = dataObject.getFirstRow("UVHValues");
        return String.valueOf(row.get("PATTERN"));
    }
    
    public String getIdFromAdvSearchUVHPattern(final Long searchParamId) {
        String pattern = null;
        try {
            pattern = this.getPatternFromUVHTable(searchParamId);
            pattern = pattern.substring(pattern.lastIndexOf(":") + 1);
        }
        catch (final DataAccessException e) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchAction : Exception occurred - getIdFromAdvSearchUVHPattern() :  While getting the Search Param UHID data : " + searchParamId, (Throwable)e);
        }
        return pattern;
    }
    
    public void recordSearchParamCount(final long searchParamId) {
        try {
            final String uvhID = this.getIdFromAdvSearchUVHPattern(searchParamId);
            this.incrementSearchTrackData(uvhID);
        }
        catch (final Exception ex) {
            SearchTrackImpl.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchAction : Exception occurred - getSearchResults() :  While record the Metrack data : " + searchParamId, ex);
        }
    }
    
    public String encryptString(final String value) {
        return AuthUtil.encryptString(value);
    }
    
    static {
        SearchTrackImpl.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
    }
}
