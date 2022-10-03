package com.adventnet.client.components.filter.web;

import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.ClientException;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.i18n.I18N;
import java.util.Iterator;
import java.util.List;
import com.adventnet.client.util.DataUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.Row;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.components.cv.web.CVEditUtils;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class FilterCreateEditController extends DefaultViewController implements WebConstants
{
    private static final String FROM_DD = "FROMDD";
    private static final String FROM_TABLE = "FROMTABLE";
    private static final String FROM_CUSTOM = "CUSTOM";
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        final HttpServletRequest request = viewCtx.getRequest();
        final String viewName = viewCtx.getRequest().getParameter("VIEWNAME");
        final DataObject tobj = WebViewAPI.getViewConfiguration((Object)viewName);
        final Long type = (Long)tobj.getFirstValue("ACTableFilterListRel", "FILTERCONFIGLISTNAME");
        if (type == null) {
            final DataObject dobj = LookUpUtil.getPersistence().get(CVEditUtils.getSQToFetchTablesAndColumns(viewName));
            request.setAttribute("CRITERIA_DEFN", (Object)CVEditUtils.generateDefn(dobj));
        }
        else {
            request.setAttribute("CRITERIA_DEFN", (Object)this.stringQueryGenerateFilter(type, request, viewCtx));
        }
        if (WebClientUtil.getRequiredParameter("EVENT_TYPE", request).equals("Edit")) {
            final String filterName = WebClientUtil.getRequiredParameter("FILTERNAME", request);
            final Long listId = new Long(WebClientUtil.getRequiredParameter("LISTID", request));
            Row r = new Row("ACFilter");
            r.set(1, (Object)listId);
            r.set(3, (Object)filterName);
            r = LookUpUtil.getPersistence().get("ACFilter", r).getFirstRow("ACFilter");
            final Long criteriaId = (Long)r.get(6);
            final DataObject criteriaDOB = FilterAPI.getCriteriaDOB(criteriaId);
            request.setAttribute("FILTER", (Object)r);
            request.setAttribute("RELCRITERIALIST", (Object)CVEditUtils.generateCriteriaList(criteriaDOB));
            request.setAttribute("LOGICALOP", (Object)CVEditUtils.getLogicalRepresentation(criteriaDOB));
        }
    }
    
    public DataObject generateDO(final Long type, final ViewContext viewCtx) throws Exception {
        final Row r = new Row("ACFilterConfig");
        r.set("ID", (Object)type);
        return LookUpUtil.getPersistence().get("ACFilterConfig", r);
    }
    
    public Criteria getCriteriaForTable() {
        return null;
    }
    
    public StringBuilder stringQueryGenerateFilter(final Long type, final HttpServletRequest request, final ViewContext viewCtx) throws Exception {
        final StringBuilder strbuild = new StringBuilder("\nvar cr = new Criteria('ViewEditCriteria')");
        final List<Row> list = DataUtils.getSortedList(this.generateDO(type, viewCtx), "ACFilterConfig", "COLINDEX");
        for (final Row row : list) {
            this.getGeneratedScript(strbuild, row, viewCtx);
        }
        return strbuild;
    }
    
    public StringBuilder getGeneratedScript(final StringBuilder strbuild, final Row row, final ViewContext viewCtx) throws Exception {
        final String tablealias = (String)row.get("TABLEALIAS");
        final String tablename = (String)row.get("TABLENAME");
        final String columnname = (String)row.get("COLNAME");
        final String tabletablename = (String)row.get("ALLOWEDVALTBL");
        final String tablecolumnname = (String)row.get("ALLOWEDVALCOL");
        final String tabledispname = (String)row.get("ALLOWEDDISPCOL");
        final String colname = (String)row.get("COLNAME");
        final String displayname = (String)row.get("DISPLAYNAME");
        final String vartype = (String)row.get("FILTERTYPE");
        strbuild.append("\ncr.addDfn({COLNAME:'").append(tablealias + "." + colname).append("',DISPLAYNAME:'").append(I18N.getMsg(displayname, new Object[0]));
        final String allowValues = (String)row.get("SHOWALLOWEDVALUES");
        if (allowValues.equals("FROMDD")) {
            final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(tablename);
            final ColumnDefinition cdef = tdef.getColumnDefinitionByName(columnname);
            final List<String> allowList = cdef.getAllowedValues().getValueList();
            strbuild.append("',TYPE:'").append(vartype).append("_CRDEF").append("',COLVALUE_OPTVAL : [");
            for (int i = 0; i < allowList.size(); ++i) {
                if (i > 0) {
                    strbuild.append(',');
                }
                strbuild.append('\'').append(allowList.get(i)).append('\'');
            }
            strbuild.append("]});\n");
        }
        else if (allowValues.equals("FROMTABLE")) {
            final DataObject obje = LookUpUtil.getPersistence().get(tabletablename, this.getCriteriaForTable());
            final Iterator<Row> ite = obje.getRows(tabletablename);
            strbuild.append("',TYPE:'").append(vartype).append("_CRDEF").append("',COLVALUE_OPTVAL : [");
            final StringBuffer stbuffer = new StringBuffer();
            boolean first = true;
            while (ite.hasNext()) {
                if (!first) {
                    strbuild.append(',');
                    stbuffer.append(',');
                }
                first = false;
                final Row ro = ite.next();
                strbuild.append("'").append(ro.get(tablecolumnname)).append("'");
                stbuffer.append("'").append((String)ro.get(tabledispname)).append("'");
            }
            strbuild.append("]").append(",COLVALUE_OPTDISP :[ ").append(stbuffer).append("]});\n");
        }
        else if (allowValues.equals("CUSTOM")) {
            strbuild.append("',TYPE:'").append(vartype).append("_CRDEF").append("',COLVALUE_OPTVAL : [");
            final ArrayList<String>[] arrayList = this.getAllowedValues(row, viewCtx);
            for (int j = 0; j < arrayList[0].size(); ++j) {
                if (j > 0) {
                    strbuild.append(',');
                }
                strbuild.append('\'').append(arrayList[0].get(j)).append('\'');
            }
            final ArrayList<String> aList = new ArrayList<String>();
            for (int k = 0; k < arrayList[1].size(); ++k) {
                aList.add("'" + arrayList[1].get(k) + "'");
            }
            strbuild.append("]").append(",COLVALUE_OPTDISP :").append(aList).append("});\n");
        }
        else {
            strbuild.append("',TYPE:'").append(vartype).append("_CRDEF").append("'});\n");
        }
        return strbuild;
    }
    
    public ArrayList<String>[] getAllowedValues(final Row r, final ViewContext viewCtx) {
        return null;
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        String msg = null;
        boolean success = true;
        HashMap<String, String> hm = null;
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
            else if ("Delete".equals(eventType)) {
                filterName = this.deleteFilter(viewCtx, request, response);
                msg = "acl.filter.delete.success";
            }
            filterTitle = ((filterTitle != null) ? filterTitle : filterName.replace('_', ' '));
            msg = I18N.getMsg(msg, new Object[] { filterTitle });
            hm = new HashMap<String, String>();
            hm.put("FILTERNAME", filterName);
        }
        catch (final DataAccessException ex) {
            if (ex.getErrorCode() == 1001) {
                msg = I18N.getMsg("acl.filter.failure.alreadyexists", new Object[0]);
            }
            else {
                ex.printStackTrace();
                msg = I18N.getMsg(ex.getMessage(), new Object[] { filterTitle });
            }
            success = false;
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
            msg = I18N.getMsg(ex2.getMessage(), new Object[] { filterTitle });
            success = false;
        }
        return WebViewAPI.sendResponse(request, response, success, msg, (Map)hm);
    }
    
    public String editFilter(final ViewContext viewCtx, final HttpServletRequest request) throws Exception {
        final String filterTitle = WebClientUtil.getRequiredParameter("FILTERTITLE", request);
        final String genName = PersonalizationUtil.genNameFromTitle(filterTitle);
        final String filterName = WebClientUtil.getRequiredParameter("FILTERNAME", request);
        final Long listId = new Long(WebClientUtil.getRequiredParameter("LISTID", request));
        if (!filterName.equals(genName) && FilterAPI.isFilterPresent(listId, genName)) {
            throw new ClientException(I18N.getMsg("acl.filter.failure.alreadyexists", new Object[] { genName }));
        }
        final ArrayList<String> tableList = new ArrayList<String>();
        tableList.add("ACFilter");
        tableList.add("ACCriteria");
        Row r = new Row("ACFilter");
        r.set(1, (Object)listId);
        r.set(3, (Object)filterName);
        final DataObject filterDO = LookUpUtil.getPersistence().get((List)tableList, r);
        filterDO.deleteRow(filterDO.getFirstRow("ACCriteria"));
        final Object crId = CVEditUtils.addCriteriaToDO(request, filterDO);
        r = filterDO.getFirstRow("ACFilter");
        r.set(3, (Object)genName);
        r.set(4, (Object)filterTitle);
        r.set(6, crId);
        filterDO.updateRow(r);
        LookUpUtil.getPersistence().update(filterDO);
        return genName;
    }
    
    public String addFilter(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String filterTitle = WebClientUtil.getRequiredParameter("FILTERTITLE", request);
        final String filterName = PersonalizationUtil.genNameFromTitle(filterTitle);
        final Long listId = new Long(WebClientUtil.getRequiredParameter("LISTID", request));
        final Row r = new Row("ACFilterList");
        r.set(1, (Object)listId);
        final ArrayList<String> arList = new ArrayList<String>();
        arList.add("ACFilterList");
        arList.add("ACFilterGroup");
        arList.add("ACUserFilterGroup");
        final DataObject userGrpDOB = LookUpUtil.getPersistence().get((List)arList, r);
        final Row userGrpRow = userGrpDOB.getRow("ACUserFilterGroup");
        if (FilterAPI.isFilterPresent((Long)userGrpRow.get(1), filterName)) {
            throw new ClientException(I18N.getMsg("acl.filter.failure.alreadyexists", new Object[] { filterName }));
        }
        final DataObject dob = (DataObject)new WritableDataObject();
        final Object crId = CVEditUtils.addCriteriaToDO(request, dob);
        final Row filterRow = new Row("ACFilter");
        filterRow.set(1, (Object)listId);
        filterRow.set(2, userGrpRow.get(2));
        filterRow.set(3, (Object)filterName);
        filterRow.set(4, (Object)filterTitle);
        filterRow.set(6, crId);
        if (WebClientUtil.getAccountId() != -1L) {
            filterRow.set(8, (Object)WebClientUtil.getAccountId());
        }
        this.setMaxFilterIndex(filterRow);
        dob.addRow(filterRow);
        LookUpUtil.getPersistence().add(dob);
        return filterName;
    }
    
    public String deleteFilter(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String filterName = request.getParameter("FILTERNAME");
        final Long listId = Long.parseLong(request.getParameter("LISTID"));
        FilterAPI.deleteFilter(listId, filterName);
        return filterName;
    }
    
    public void setMaxFilterIndex(final Row filterRow) throws Exception {
        final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(new Table("ACFilter"));
        selQuery.addSelectColumn(new Column("ACFilter", "FILTERINDEX").maximum());
        Criteria cr = new Criteria(new Column("ACFilter", "LISTID"), filterRow.get(1), 0);
        cr = cr.and(new Criteria(new Column("ACFilter", "GROUPNAME"), filterRow.get(2), 0));
        selQuery.setCriteria(cr);
        Integer maxIndex = new Integer(1);
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)selQuery, conn);
            if (ds.next()) {
                maxIndex = (Integer)ds.getValue(1);
                if (maxIndex != null) {
                    maxIndex = new Integer(maxIndex + 1);
                }
            }
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (maxIndex == null) {
            maxIndex = new Integer(1);
        }
        filterRow.set("FILTERINDEX", (Object)maxIndex);
    }
}
