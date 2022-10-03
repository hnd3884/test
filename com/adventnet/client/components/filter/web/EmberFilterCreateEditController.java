package com.adventnet.client.components.filter.web;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.ClientException;
import com.adventnet.i18n.I18N;
import com.adventnet.iam.xss.IAMEncoder;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Iterator;
import java.util.List;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import com.adventnet.client.util.DataUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.web.WebViewModel;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.components.cv.web.CVEditUtils;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;

public class EmberFilterCreateEditController extends FilterCreateEditController implements WebConstants
{
    @Override
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        final WebViewModel webViewModel = viewCtx.getModel();
        final DataObject viewConfigDO = webViewModel.getViewConfiguration();
        final HttpServletRequest request = viewCtx.getRequest();
        if (!viewConfigDO.containsTable("ACTableFilterListRel")) {
            throw new RuntimeException("Filter is not defined in this view " + viewCtx.getUniqueId());
        }
        final boolean isCustomFilter = (boolean)viewConfigDO.getValue("ACTableFilterListRel", 5, (Row)null);
        if (!isCustomFilter) {
            throw new RuntimeException("The defined filter is not a custom one");
        }
        final Long filterConfigListId = (Long)viewConfigDO.getValue("ACTableFilterListRel", 4, (Row)null);
        JSONArray criteriaDefn;
        if (filterConfigListId == null) {
            final DataObject dobj = LookUpUtil.getPersistence().get(CVEditUtils.getSQToFetchTablesAndColumns(viewCtx.getUniqueId()));
            criteriaDefn = this.getCriteriaDefnAsJSON(dobj);
        }
        else {
            criteriaDefn = this.getCriteriaDefnAsJSON(filterConfigListId, viewCtx);
        }
        request.setAttribute("CRITERIA_DEFN", (Object)criteriaDefn);
        if (WebClientUtil.getRequiredParameter("EVENT_TYPE", request).equals("Edit")) {
            final String filterName = WebClientUtil.getRequiredParameter("FILTERNAME", request);
            final Long listId = new Long(WebClientUtil.getRequiredParameter("LISTID", request));
            final Row r = new Row("ACFilter");
            r.set(1, (Object)listId);
            r.set(3, (Object)filterName);
            request.setAttribute("RELCRITERIALIST", (Object)getJSONForRelationalCriteria(r));
        }
    }
    
    private static JSONObject getJSONForRelationalCriteria(final Row r) throws Exception {
        final Row acFilterRow = LookUpUtil.getPersistence().get("ACFilter", r).getRow("ACFilter");
        JSONObject jsonObj = null;
        if (acFilterRow != null) {
            final String filterName = (String)acFilterRow.get(3);
            final String displayName = (String)acFilterRow.get(4);
            r.set(3, (Object)filterName);
            final Long criteriaId = (Long)acFilterRow.get(6);
            final DataObject criteriaDOB = FilterAPI.getCriteriaDOB(criteriaId);
            final JSONArray jsonArray = generateCriteriaList(criteriaDOB);
            jsonObj = new JSONObject();
            jsonObj.put("displayName", (Object)displayName);
            jsonObj.put("filterName", (Object)filterName);
            jsonObj.put("criteria", (Object)jsonArray);
            jsonObj.put("length", jsonArray.length());
            jsonObj.put("booleanOperator", (Object)CVEditUtils.getLogicalRepresentation(criteriaDOB).replaceAll("'", ""));
        }
        return jsonObj;
    }
    
    private static JSONArray generateCriteriaList(final DataObject dobj) throws Exception {
        final JSONArray scriptArr = new JSONArray();
        JSONObject criteriaJson = null;
        final List<Row> rowList = DataUtils.getSortedList(dobj, "ACRelationalCriteria", "RELATIONALCRITERIAID");
        for (final Row row : rowList) {
            criteriaJson = new JSONObject();
            final String tableName = (String)row.get("TABLEALIAS");
            final String columnName = (String)row.get("COLUMNNAME");
            criteriaJson.put("COLNAME", (Object)(tableName + "." + columnName));
            criteriaJson.put("COLDISPNAME", (Object)columnName);
            String value = StringEscapeUtils.unescapeJava((String)row.get(6));
            final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(tableName);
            final ColumnDefinition cdef = tdef.getColumnDefinitionByName(tdef.getDefinedColumnName(columnName));
            String dataType = null;
            if (cdef != null) {
                dataType = cdef.getDataType();
            }
            if ("DATE".equals(dataType) || (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7)) {
                value = value.substring(5, 7) + "/" + value.substring(8) + "/" + value.substring(0, 4);
                criteriaJson.put("COLVALUE", (Object)value);
            }
            else {
                criteriaJson.put("COLVALUE", MetaDataUtil.convert(value, dataType));
            }
            criteriaJson.put("COMPARATOR", (int)row.get(5));
            scriptArr.put((Object)criteriaJson);
        }
        return scriptArr;
    }
    
    @Override
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        String msg = null;
        int responseStatusCode = 200;
        String filterName = null;
        String filterTitle = request.getParameter("FILTERTITLE");
        try {
            if ("Edit".equals(eventType)) {
                filterName = this.editFilter(viewCtx, request);
                msg = "acl.filter.edit.success";
            }
            else if ("Add".equals(eventType)) {
                filterName = this.addFilter(viewCtx, request, response);
                msg = "acl.filter.created.success";
            }
            else {
                if (!"Delete".equals(eventType)) {
                    throw new IllegalArgumentException("Invalid eventType given. Supported : Edit/Add/Delete. All are case-sensitive.");
                }
                filterName = this.deleteFilter(viewCtx, request, response);
                msg = "acl.filter.delete.success";
            }
            filterTitle = ((filterTitle != null) ? filterTitle : filterName.replace('_', ' '));
            filterTitle = IAMEncoder.encodeHTML(filterTitle);
            msg = I18N.getMsg(msg, new Object[] { filterTitle });
        }
        catch (final ClientException ce) {
            if (ce.getErrorCode().equals("1001")) {
                msg = ce.getMessage();
                responseStatusCode = 400;
            }
            else {
                ce.printStackTrace();
                msg = I18N.getMsg(ce.getMessage(), new Object[] { filterTitle });
                responseStatusCode = 500;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            msg = I18N.getMsg(ex.getMessage(), new Object[] { filterTitle });
            responseStatusCode = 500;
        }
        WebViewAPI.writeInResponse(request, response, msg, responseStatusCode);
        return null;
    }
    
    private JSONArray getCriteriaDefnAsJSON(final DataObject dobj) throws Exception {
        final Map<String, String> dataTypeMap = new HashMap<String, String>();
        final Map<String, String> valueMap = new HashMap<String, String>();
        final Iterator<Row> tblIter = dobj.getRows("SelectTable");
        while (tblIter.hasNext()) {
            final Row row = tblIter.next();
            final TableDefinition def = MetaDataUtil.getTableDefinitionByName((String)row.get(3));
            final Iterator<Row> itr = dobj.getRows("SelectColumn", row);
            while (itr.hasNext()) {
                final Row temp_row = itr.next();
                final String dataType = def.getColumnDefinitionByName((String)temp_row.get(4)).getDataType();
                dataTypeMap.put((String)temp_row.get(3), getDefnType(dataType));
                valueMap.put((String)temp_row.get(3), row.get(2) + "." + temp_row.get(4));
            }
        }
        JSONObject obj = null;
        final JSONArray criteriaDefn = new JSONArray();
        final Iterator<Row> itr2 = dobj.getRows("ACColumnConfiguration");
        while (itr2.hasNext()) {
            final Row row2 = itr2.next();
            final String colAlias = (String)row2.get(3);
            final String tblCol = valueMap.get(colAlias);
            if (tblCol != null) {
                obj = new JSONObject();
                obj.put("COLNAME", (Object)tblCol);
                obj.put("DISPLAYNAME", row2.get(4));
                obj.put("TYPE", (Object)dataTypeMap.get(colAlias));
                criteriaDefn.put((Object)obj);
            }
        }
        return criteriaDefn;
    }
    
    private static String getDefnType(final String dataType) {
        switch (dataType) {
            case "CHAR":
            case "SCHAR": {
                return "CHAR";
            }
            case "BIGINT":
            case "INTEGER": {
                return "INTEGER";
            }
            case "FLOAT":
            case "DOUBLE": {
                return "FLOAT";
            }
            case "DATE":
            case "DATETIME": {
                return "DATE";
            }
            default: {
                throw new RuntimeException("Datatype not handled!!");
            }
        }
    }
    
    private JSONArray getCriteriaDefnAsJSON(final Long filterConfigListId, final ViewContext viewCtx) throws Exception {
        final Iterator<Row> iter = DataUtils.getSortedList(this.generateDO(filterConfigListId, null), "ACFilterConfig", "COLINDEX").iterator();
        final JSONArray criteriaArray = new JSONArray();
        while (iter.hasNext()) {
            final Row row = iter.next();
            criteriaArray.put((Object)this.getGeneratedJSON(row, viewCtx));
        }
        return criteriaArray;
    }
    
    private JSONObject getGeneratedJSON(final Row row, final ViewContext viewCtx) throws Exception {
        final JSONObject filterCriteriaConfig = new JSONObject();
        final String columnname = (String)row.get("COLNAME");
        filterCriteriaConfig.put("COLNAME", (Object)(row.get("TABLEALIAS") + "." + columnname));
        filterCriteriaConfig.put("DISPLAYNAME", (Object)I18N.getMsg((String)row.get("DISPLAYNAME"), new Object[0]));
        filterCriteriaConfig.put("TYPE", row.get("FILTERTYPE"));
        final String s2 = (String)row.get("SHOWALLOWEDVALUES");
        switch (s2) {
            case "FROMDD": {
                final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName((String)row.get("TABLENAME"));
                final List<String> allowList = tdef.getColumnDefinitionByName(columnname).getAllowedValues().getValueList();
                final JSONArray options = new JSONArray();
                for (final String s : allowList) {
                    final JSONObject obj = new JSONObject();
                    obj.put("optDisp", (Object)s);
                    obj.put("optVal", (Object)s);
                    options.put((Object)obj);
                }
                filterCriteriaConfig.put("allowedValues", (Object)options);
                break;
            }
            case "FROMTABLE": {
                final String allowedValTableName = (String)row.get("ALLOWEDVALTBL");
                final DataObject obje = LookUpUtil.getPersistence().get(allowedValTableName, this.getCriteriaForTable());
                final Iterator<Row> ite = obje.getRows(allowedValTableName);
                final JSONArray options = new JSONArray();
                while (ite.hasNext()) {
                    final Row ro = ite.next();
                    final JSONObject obj2 = new JSONObject();
                    obj2.put("optDisp", ro.get((String)row.get("ALLOWEDDISPCOL")));
                    obj2.put("optVal", ro.get((String)row.get("ALLOWEDVALCOL")));
                    options.put((Object)obj2);
                }
                filterCriteriaConfig.put("allowedValues", (Object)options);
                break;
            }
            case "CUSTOM": {
                final ArrayList<String>[] arrayList = this.getAllowedValues(row, viewCtx);
                if (arrayList == null) {
                    break;
                }
                filterCriteriaConfig.put("COLVALUE_OPTVAL", (Object)arrayList[0].toString());
                filterCriteriaConfig.put("COLVALUE_OPTDISP", (Object)arrayList[1].toString());
                break;
            }
        }
        return filterCriteriaConfig;
    }
}
