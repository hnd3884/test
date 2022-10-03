package com.me.framework.migration;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.WritableDataObject;
import java.util.Map;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.net.URL;
import java.io.File;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.adventnet.ds.query.Column;
import java.io.IOException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import org.xml.sax.SAXException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.List;

public class UpdateProductCodeUtil
{
    public static List tableList;
    public static Logger logger;
    private static HashMap patternVsValue;
    
    private static List addTablesInList() {
        final List list = new ArrayList();
        list.add("MsgToCustomerStatus");
        list.add("MsgToGlobalStatus");
        list.add("MsgGroupToPage");
        list.add("DCQuickLink");
        list.add("ErrorCodeToKBUrl");
        return list;
    }
    
    public static void updateProductCode(final List fileList) throws SAXException, DataAccessException, QueryConstructionException, IOException {
        for (final String filePath : fileList) {
            updateProductCode(filePath);
        }
    }
    
    public static void deleteProductCode() throws QueryConstructionException, DataAccessException, IOException, SAXException {
        for (final String tablename : UpdateProductCodeUtil.tableList) {
            final Column updCol = (Column)Column.createFunction("AND", new Object[] { Column.getColumn(tablename, "PRODUCT_CODE"), EMSProductUtil.getBitwiseValueForCurrentProduct() });
            updCol.setType(4);
            updCol.setTableAlias(tablename);
            final Criteria criteria = new Criteria(updCol, (Object)EMSProductUtil.getBitwiseValueForCurrentProduct(), 1).and(new Criteria(Column.getColumn(tablename, "PRODUCT_CODE"), (Object)0, 1));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tablename));
            selectQuery.addSelectColumn(Column.getColumn(tablename, "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator2 = dataObject.getRows(tablename);
            int count = 0;
            while (iterator2.hasNext()) {
                iterator2.next();
                ++count;
            }
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tablename);
            deleteQuery.setCriteria(criteria);
            DataAccess.delete(deleteQuery);
            UpdateProductCodeUtil.logger.log(Level.INFO, "deleted table " + tablename + "with row count " + count);
        }
    }
    
    private static Criteria getCriteriaForTables(final String tableName, final Row row) {
        final Criteria criteria = new Criteria();
        if (tableName.equals("DCQuickLink")) {
            final Object articleID = row.get("ARTICLE_ID");
            return new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_ID"), articleID, 0);
        }
        if (tableName.equals("ErrorCodeToKBUrl")) {
            final Object errorCode = row.get("ERROR_CODE");
            final Object kbUrl = row.get("KB_URL");
            final Criteria errorCodeCriteria = new Criteria(Column.getColumn("ErrorCodeToKBUrl", "ERROR_CODE"), errorCode, 0);
            final Criteria kbUrlCriteria = new Criteria(Column.getColumn("ErrorCodeToKBUrl", "KB_URL"), kbUrl, 0);
            return errorCodeCriteria.and(kbUrlCriteria);
        }
        if (tableName.equals("MsgGroupToPage")) {
            final Object msgGroupID = row.get("MSG_GROUP_ID");
            final Object msgpageID = row.get("MSG_PAGE_ID");
            final Object roleID = row.get("ROLE_ID");
            final Criteria msgGroupCriteria = new Criteria(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"), msgGroupID, 0);
            final Criteria msgPageCriteria = new Criteria(Column.getColumn("MsgGroupToPage", "MSG_PAGE_ID"), msgpageID, 0);
            final Criteria msgRoleCriteria = new Criteria(Column.getColumn("MsgGroupToPage", "ROLE_ID"), roleID, 0);
            return msgGroupCriteria.and(msgPageCriteria).and(msgRoleCriteria);
        }
        if (tableName.equals("MsgToGlobalStatus")) {
            final Object msgContentID = row.get("MSG_CONTENT_ID");
            return new Criteria(Column.getColumn("MsgToGlobalStatus", "MSG_CONTENT_ID"), msgContentID, 0);
        }
        if (tableName.equals("MsgToCustomerStatus")) {
            final Object msgContentID = row.get("MSG_CONTENT_ID");
            final Object customer_id = row.get("CUSTOMER_ID");
            return new Criteria(Column.getColumn("MsgToCustomerStatus", "MSG_CONTENT_ID"), msgContentID, 0).and(new Criteria(Column.getColumn("MsgToCustomerStatus", "CUSTOMER_ID"), customer_id, 0));
        }
        return criteria;
    }
    
    private static boolean checkRowAvailability(final Criteria criteria, final String tableName, final Row row) throws DataAccessException {
        if (tableName.equals("DCQuickLink")) {
            final Object articleID = row.get("ARTICLE_ID");
            if (!articleID.toString().contains("UVH")) {
                final DataObject data = getRowsFromDB(tableName, criteria);
                if (data.size("DCQuickLink") >= 0) {
                    return true;
                }
            }
        }
        else if (tableName.equals("MsgToGlobalStatus")) {
            final Object msgContentID = row.get("MSG_CONTENT_ID");
            if (!msgContentID.toString().contains("UVH")) {
                final DataObject data = getRowsFromDB(tableName, criteria);
                if (data.size("MsgToGlobalStatus") >= 0) {
                    return true;
                }
            }
        }
        else if (tableName.equals("MsgToCustomerStatus")) {
            final Object msgContentID = row.get("MSG_CONTENT_ID");
            final Object customer_id = row.get("CUSTOMER_ID");
            if (!msgContentID.toString().contains("UVH") && !customer_id.toString().contains("UVH")) {
                final DataObject data2 = getRowsFromDB(tableName, criteria);
                if (data2.size("MsgToCustomerStatus") >= 0) {
                    return true;
                }
            }
        }
        else if (tableName.equals("MsgGroupToPage")) {
            final Object msgGroupID = row.get("MSG_GROUP_ID");
            final Object msgpageID = row.get("MSG_PAGE_ID");
            final Object roleID = row.get("ROLE_ID");
            if (!msgpageID.toString().contains("UVH") && !roleID.toString().contains("UVH") && !msgGroupID.toString().contains("UVH")) {
                final DataObject data3 = getRowsFromDB(tableName, criteria);
                if (data3.size("MsgGroupToPage") >= 0) {
                    return true;
                }
            }
        }
        else if (tableName.equals("ErrorCodeToKBUrl")) {
            final Object errorCode = row.get("ERROR_CODE");
            final Object kbUrl = row.get("KB_URL");
            if (!errorCode.toString().contains("UVH") && !kbUrl.toString().contains("UVH")) {
                final DataObject data2 = getRowsFromDB(tableName, criteria);
                if (data2.size("ErrorCodeToKBUrl") >= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void updateProductCode(final String filePath) throws QueryConstructionException, DataAccessException, IOException, SAXException {
        final Map patternVsValue = getUvhPatternVsValue(filePath);
        UpdateProductCodeUtil.logger.log(Level.INFO, "into Update");
        final URL url = new URL("file:" + File.separator + File.separator + File.separator + System.getProperty("server.dir") + "\\" + filePath);
        UpdateProductCodeUtil.logger.log(Level.INFO, url.toString());
        final DataObject dataObject1 = Xml2DoConverter.transform(url, true, patternVsValue);
        for (final String tablename : UpdateProductCodeUtil.tableList) {
            if (dataObject1.containsTable(tablename)) {
                final Iterator countIter = dataObject1.getRows(tablename);
                while (countIter.hasNext()) {
                    final Row r = countIter.next();
                    final Criteria criteria = getCriteriaForTables(tablename, r);
                    if (criteria != null) {
                        if (checkRowAvailability(criteria, tablename, r)) {
                            updateProductCodeDB(r, tablename, criteria);
                            UpdateProductCodeUtil.logger.log(Level.INFO, "updated in " + tablename);
                        }
                        else {
                            addRowsInDB(r);
                            UpdateProductCodeUtil.logger.log(Level.INFO, "added in " + tablename);
                        }
                    }
                }
            }
        }
    }
    
    private static void addRowsInDB(final Row r) throws DataAccessException {
        final DataObject dataObject = (DataObject)new WritableDataObject();
        dataObject.addRow(r);
        DataAccess.update(dataObject);
        UpdateProductCodeUtil.logger.log(Level.INFO, "row is added in DB" + r);
    }
    
    private static DataObject getRowsFromDB(final String tableName, final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        selectQuery.addSelectColumn(Column.getColumn(tableName, "*"));
        selectQuery.setCriteria(criteria);
        return DataAccess.get(selectQuery);
    }
    
    private static void updateProductCodeDB(final Row r, final String tableName, final Criteria criteria) throws DataAccessException {
        final Integer productCode = (Integer)r.get("PRODUCT_CODE");
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl(tableName);
        updateQuery.setCriteria(criteria);
        updateQuery.setUpdateColumn("PRODUCT_CODE", (Object)productCode);
        DataAccess.update(updateQuery);
        UpdateProductCodeUtil.logger.log(Level.INFO, "updated row from updateProductCodeDB" + r);
        UpdateProductCodeUtil.logger.log(Level.INFO, "product Code in " + tableName + "updated Successfully");
    }
    
    private static Map getUvhPatternVsValue(String filePath) throws DataAccessException, QueryConstructionException {
        UpdateProductCodeUtil.patternVsValue = new HashMap();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("UVHValues"));
        query.addSelectColumn(new Column("UVHValues", "*"));
        query.addSelectColumn(new Column("ConfFile", "*"));
        filePath = "file:/${server.home}//" + filePath.replace("\\", "/");
        final Join join = new Join("UVHValues", "ConfFile", new String[] { "FILEID" }, new String[] { "FILEID" }, 2);
        query.addJoin(join);
        final Criteria criteria1 = new Criteria(Column.getColumn("ConfFile", "URL"), (Object)filePath, 0);
        query.setCriteria(criteria1);
        final DataObject dataObject = DataAccess.get(query);
        if (dataObject.size("UVHValues") > 0) {
            UpdateProductCodeUtil.logger.log(Level.INFO, "Inside getUVHValuePattern method");
            final Iterator keyiter = dataObject.get("UVHValues", "PATTERN");
            final Iterator valueiter = dataObject.get("UVHValues", "GENVALUES");
            while (keyiter.hasNext()) {
                UpdateProductCodeUtil.patternVsValue.put(keyiter.next(), valueiter.next());
            }
        }
        return UpdateProductCodeUtil.patternVsValue;
    }
    
    static {
        UpdateProductCodeUtil.logger = Logger.getLogger(UpdateProductCodeUtil.class.getName());
        UpdateProductCodeUtil.patternVsValue = null;
        UpdateProductCodeUtil.tableList = addTablesInList();
    }
}
