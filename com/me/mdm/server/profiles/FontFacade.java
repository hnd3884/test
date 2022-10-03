package com.me.mdm.server.profiles;

import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.profiles.font.FontDetailsHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.APIUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class FontFacade
{
    private Logger logger;
    
    public FontFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getAllFonts(final JSONObject request) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray fontArray = new JSONArray();
        try {
            final String search = APIUtil.getStringFilter(request, "search");
            final Long customerId = APIUtil.getCustomerID(request);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("FontDetails"));
            Criteria customerCriteria = new Criteria(new Column("FontDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            if (!MDMStringUtils.isEmpty(search)) {
                customerCriteria = customerCriteria.and(new Criteria(new Column("FontDetails", "NAME"), (Object)search, 12, false));
            }
            selectQuery.setCriteria(customerCriteria);
            final SelectQuery countQuery = (SelectQuery)selectQuery.clone();
            countQuery.addSelectColumn(Column.getColumn("FontDetails", "FONT_ID").distinct().count());
            final int count = DBUtil.getRecordCount(countQuery);
            if (count > 0) {
                selectQuery.addSelectColumn(new Column("FontDetails", "FONT_ID"));
                selectQuery.addSelectColumn(new Column("FontDetails", "FONT_NAME"));
                selectQuery.addSelectColumn(new Column("FontDetails", "NAME"));
                selectQuery.addSelectColumn(new Column("FontDetails", "FONT_FAMILY_NAME"));
                final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    responseJSON.put("paging", (Object)pagingJSON);
                }
                selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                selectQuery.addSortColumn(new SortColumn("FontDetails", "FONT_ID", true));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("FontDetails");
                    while (iterator.hasNext()) {
                        final JSONObject fontJSON = new JSONObject();
                        final Row fontRow = iterator.next();
                        final List columnList = fontRow.getColumns();
                        for (final Object column : columnList) {
                            final String columnName = (String)column;
                            final Object columnValue = fontRow.get(columnName);
                            if (columnValue != null) {
                                fontJSON.put(columnName.toLowerCase(), columnValue);
                            }
                        }
                        fontArray.put((Object)fontJSON);
                    }
                }
            }
            responseJSON.put("fonts", (Object)fontArray);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "error in getFonts()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
    
    public void deleteBulkFonts(final JSONObject request) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final Long userId = APIUtil.getUserID(request);
            final JSONObject responseJSON = request.getJSONObject("msg_body");
            final JSONArray fontIds = responseJSON.getJSONArray("font_ids");
            this.deleteFonts(fontIds, customerId, userId);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw ex2;
        }
    }
    
    private void deleteFonts(final JSONArray fontIds, final Long customerId, final Long userId) throws Exception {
        final List fontList = JSONUtil.getInstance().convertLongJSONArrayTOList(fontIds);
        final DataObject dataObject = this.getFontDO(fontList, customerId);
        if (!this.validateFontExist(fontList, dataObject)) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        if (!APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "All_Managed_Mobile_Devices" })) {
            throw new APIHTTPException("COM0013", new Object[0]);
        }
        final FontDetailsHandler handler = new FontDetailsHandler();
        try {
            handler.handleDeleteFonts(fontList, customerId, userId);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("FontDetails");
            final Criteria customerIdCriteria = new Criteria(new Column("FontDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria webClipIdCriteria = new Criteria(new Column("FontDetails", "FONT_ID"), (Object)fontList.toArray(), 8);
            deleteQuery.setCriteria(customerIdCriteria.and(webClipIdCriteria));
            MDMUtil.getPersistenceLite().delete(deleteQuery);
            handler.deleteFontsInFile(fontList, dataObject);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in delete webclips", ex);
            throw ex;
        }
    }
    
    private boolean validateFontExist(final List<Long> fontList, final DataObject dataObject) {
        if (!dataObject.isEmpty()) {
            final int size = dataObject.size("FontDetails");
            if (fontList.size() == size) {
                return true;
            }
        }
        return false;
    }
    
    private DataObject getFontDO(final List<Long> fontList, final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("FontDetails"));
        selectQuery.addSelectColumn(new Column("FontDetails", "FONT_ID"));
        selectQuery.addSelectColumn(new Column("FontDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(new Column("FontDetails", "FONT_TYPE"));
        final Criteria fontCriteria = new Criteria(new Column("FontDetails", "FONT_ID"), (Object)fontList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(new Column("FontDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(fontCriteria.and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return dataObject;
    }
    
    public JSONObject getFontDetails(final JSONObject request) {
        final JSONObject responseObject = new JSONObject();
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final Long fontId = APIUtil.getResourceID(request, "font_id");
            final boolean inProfile = APIUtil.getBooleanFilter(request, "in_profile");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("FontDetails"));
            final Criteria customerCriteria = new Criteria(new Column("FontDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria fontIdCriteria = new Criteria(new Column("FontDetails", "FONT_ID"), (Object)fontId, 0);
            selectQuery.setCriteria(customerCriteria.and(fontIdCriteria));
            selectQuery.addSelectColumn(new Column("FontDetails", "*"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row fontRow = dataObject.getRow("FontDetails");
                final List columnList = fontRow.getColumns();
                for (final Object column : columnList) {
                    final String columnName = (String)column;
                    final Object columnValue = fontRow.get(columnName);
                    if (columnValue != null) {
                        responseObject.put(columnName.toLowerCase(), columnValue);
                    }
                }
                if (inProfile) {
                    final List<Long> fontIds = new ArrayList<Long>();
                    fontIds.add(fontId);
                    final JSONObject fontObjects = new FontDetailsHandler().isFontInProfile(fontIds);
                    if (fontObjects.has(String.valueOf(fontId))) {
                        responseObject.put("profile_ids", fontObjects.get(String.valueOf(fontId)));
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getFontDetails", (Throwable)e);
        }
        return responseObject;
    }
}
