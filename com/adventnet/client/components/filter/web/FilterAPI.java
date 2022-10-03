package com.adventnet.client.components.filter.web;

import java.util.Calendar;
import java.util.GregorianCalendar;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.persistence.Row;
import com.adventnet.client.util.StaticLists;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.client.cache.StaticCache;
import javax.swing.table.TableModel;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.util.web.WebConstants;

public class FilterAPI implements WebConstants
{
    private static SelectQuery fetchQuery;
    
    public static FilterModel getFilterModel(final Long filterListId, final long accountId) throws Exception {
        final String key = "ACFilterModel:" + filterListId;
        TableModel tm = (TableModel)StaticCache.getFromCache((Object)key);
        if (tm != null) {
            return new FilterModel(tm, accountId, filterListId);
        }
        if (FilterAPI.fetchQuery == null) {
            final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(new Table("ACFilterGroup"));
            selQuery.setRange(new Range(0, -1));
            synchronized (selQuery) {
                selQuery.addJoin(new Join("ACFilterGroup", "ACFilter", new String[] { "LISTID", "GROUPNAME" }, new String[] { "LISTID", "GROUPNAME" }, 2));
                selQuery.addJoin(new Join("ACFilter", "EmptyTableMessage", new String[] { "EMPTY_MESSAGE_ID" }, new String[] { "EMPTY_MESSAGE_ID" }, 1));
                selQuery.addJoin(new Join("EmptyTableMessage", "Menu", new String[] { "MENU_ID" }, new String[] { "MENUID_NO" }, 1));
                selQuery.addSelectColumn(new Column("ACFilterGroup", "GROUPNAME"));
                selQuery.addSelectColumn(new Column("ACFilterGroup", "DISPLAYNAME", "GRPDISPNAME"));
                final Column grpIndexCol = new Column("ACFilterGroup", "GRPINDEX");
                selQuery.addSelectColumn(grpIndexCol);
                selQuery.addSelectColumn(new Column("ACFilter", "FILTERNAME"));
                selQuery.addSelectColumn(new Column("ACFilter", "DISPLAYNAME", "FILTERDISPNAME"));
                final Column filterIndexCol = new Column("ACFilter", "FILTERINDEX");
                selQuery.addSelectColumn(filterIndexCol);
                selQuery.addSelectColumn(new Column("ACFilter", "CREATEDBY"));
                selQuery.addSelectColumn(new Column("ACFilter", "LISTID"));
                selQuery.addSelectColumn(new Column("ACFilter", "EMPTY_MESSAGE_ID", "EMPTY_DATA_MESSAGE_ID"));
                selQuery.addSelectColumn(new Column("EmptyTableMessage", "ICON_URL"));
                selQuery.addSelectColumn(new Column("EmptyTableMessage", "TITLE_TEXT"));
                selQuery.addSelectColumn(new Column("EmptyTableMessage", "MESSAGE_TEXT"));
                selQuery.addSelectColumn(new Column("EmptyTableMessage", "MENU_ID"));
                selQuery.addSelectColumn(new Column("Menu", "MENUID"));
                selQuery.addSortColumn(new SortColumn(grpIndexCol, true));
                selQuery.addSortColumn(new SortColumn(filterIndexCol, true));
            }
            FilterAPI.fetchQuery = selQuery;
        }
        final SelectQuery critQuery = (SelectQuery)FilterAPI.fetchQuery.clone();
        critQuery.setCriteria(new Criteria(new Column("ACFilterGroup", "LISTID"), (Object)filterListId, 0));
        tm = (TableModel)LookUpUtil.getCVManagerForTable().getData(new CustomViewRequest(critQuery)).getModel();
        StaticCache.addToCache((Object)key, (Object)tm, StaticLists.FILTER);
        return new FilterModel(tm, accountId, filterListId);
    }
    
    public static Object getFilter(final Long filterListId, final String filterName) throws Exception {
        final String key = "FILTER:" + filterListId + "_" + filterName;
        Object filter = StaticCache.getFromCache((Object)key);
        if (filter != null) {
            return filter;
        }
        Row r = new Row("ACFilter");
        r.set(1, (Object)filterListId);
        r.set(3, (Object)filterName);
        r = LookUpUtil.getPersistence().get("ACFilter", r).getFirstRow("ACFilter");
        final Long criteriaId = (Long)r.get(6);
        if (criteriaId != null) {
            filter = getCriteria(criteriaId);
            StaticCache.addToCache((Object)key, filter, StaticLists.ACRELCRITERIA);
        }
        else if (r.get(7) != null) {
            filter = QueryUtil.getSelectQuery((long)r.get(7));
            StaticCache.addToCache((Object)key, filter, QueryUtil.selectQueryTableList);
        }
        return filter;
    }
    
    public static DataObject getCriteriaDOB(final Long criteriaId) throws Exception {
        final Row r = new Row("ACCriteria");
        r.set("CRITERIAID", (Object)criteriaId);
        return LookUpUtil.getPersistence().get(StaticLists.ACRELCRITERIA, r);
    }
    
    public static Criteria getCriteria(final Long criteriaId) throws Exception {
        final DataObject critDOB = getCriteriaDOB(criteriaId);
        final Row r = critDOB.getFirstRow("ACCriteria");
        return formCriteria(r, critDOB.getRows("ACRelationalCriteria"));
    }
    
    private static Criteria formCriteria(final Row criteriaRow, final Iterator relCritItr) throws DataAccessException {
        final String logicalRep = (String)criteriaRow.get("LOGICALREPRESENTATION");
        if (logicalRep == null) {
            return null;
        }
        final StringTokenizer tok = new StringTokenizer(logicalRep, "()&|", true);
        final List<Row> relCritList = new ArrayList<Row>();
        while (relCritItr.hasNext()) {
            final Row relCritRow = relCritItr.next();
            relCritList.add(relCritRow);
        }
        return formCriteria(tok, relCritList);
    }
    
    private static Criteria getCriteria(final String singleCriteria, final List relCritList) throws DataAccessException {
        final long rcID = Long.parseLong(singleCriteria);
        for (int relCritSize = relCritList.size(), i = 0; i < relCritSize; ++i) {
            final Row relCrit = relCritList.get(i);
            if ((long)relCrit.get(2) == rcID) {
                int val = (int)relCrit.get(5);
                if (val < 40) {
                    final String tableName = (String)relCrit.get(3);
                    final String columnName = (String)relCrit.get(4);
                    final String displayvalue = (String)relCrit.get(6);
                    final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ACFilterConfig"));
                    final ArrayList selectColumnList = new ArrayList();
                    selectColumnList.add(new Column("ACFilterConfig", "CLASSNAME"));
                    selectColumnList.add(new Column("ACFilterConfig", "COLINDEX"));
                    selectColumnList.add(new Column("ACFilterConfig", "ID"));
                    sq.addSelectColumns((List)selectColumnList);
                    final Criteria criteria = new Criteria(new Column("ACFilterConfig", "COLNAME"), (Object)columnName, 0);
                    sq.setCriteria(criteria);
                    String className = null;
                    Iterator columnIterator = null;
                    try {
                        final DataObject criteriaObject = LookUpUtil.getPersistence().get(sq);
                        columnIterator = criteriaObject.get("ACFilterConfig", "CLASSNAME");
                    }
                    catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                    final boolean hasNext = columnIterator.hasNext();
                    if (columnIterator.hasNext()) {
                        className = columnIterator.next();
                    }
                    Criteria cri = null;
                    if (className != null) {
                        try {
                            final CustomCriteria customCri = (CustomCriteria)WebClientUtil.createInstance(className);
                            cri = customCri.getCustomCriteria(tableName, columnName, displayvalue);
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        cri = getCustomCriteria(relCrit, val);
                    }
                    return cri;
                }
                final String str = (String)relCrit.get("VALUE");
                final int month = Integer.parseInt(str.substring(5, 7));
                final int date = Integer.parseInt(str.substring(8));
                final int year = Integer.parseInt(str.substring(0, 4));
                final Calendar startDate = new GregorianCalendar(year, month - 1, date);
                final long longStartDate = startDate.getTimeInMillis();
                final long longEndDate = longStartDate + 86400000L;
                val %= 40;
                if (val == 14 || val == 15) {
                    return new Criteria(new Column((String)relCrit.get(3), (String)relCrit.get(4)), (Object)new long[] { longStartDate, longEndDate }, val, (boolean)relCrit.get(7));
                }
                if (val == 5) {
                    return new Criteria(new Column((String)relCrit.get(3), (String)relCrit.get(4)), (Object)longEndDate, val, (boolean)relCrit.get(7));
                }
                if (val == 7) {
                    return new Criteria(new Column((String)relCrit.get(3), (String)relCrit.get(4)), (Object)longStartDate, val, (boolean)relCrit.get(7));
                }
            }
        }
        return null;
    }
    
    public static Criteria getCustomCriteria(final Row relCrit, final int comparator) {
        return new Criteria(new Column((String)relCrit.get(3), (String)relCrit.get(4)), relCrit.get(6), comparator, (boolean)relCrit.get(7));
    }
    
    private static Criteria formCriteria(final StringTokenizer tok, final List<Row> relCritList) throws DataAccessException {
        boolean bool = false;
        if (!tok.hasMoreTokens()) {
            return null;
        }
        final String firstTok = tok.nextToken();
        if (!firstTok.equals("(")) {
            return getCriteria(firstTok, relCritList);
        }
        final Criteria leftCriteria = formCriteria(tok, relCritList);
        Criteria cri = null;
        while (tok.hasMoreTokens()) {
            final String operator = tok.nextToken();
            Criteria rightCriteria = null;
            if (!operator.equals(")")) {
                rightCriteria = formCriteria(tok, relCritList);
            }
            if (operator.equals("&")) {
                if (!bool) {
                    cri = leftCriteria.and(rightCriteria);
                    bool = true;
                }
                else {
                    cri = cri.and(rightCriteria);
                }
            }
            else {
                if (!operator.equals("|")) {
                    continue;
                }
                if (!bool) {
                    cri = leftCriteria.or(rightCriteria);
                    bool = true;
                }
                else {
                    cri = cri.or(rightCriteria);
                }
            }
        }
        return cri;
    }
    
    public static boolean isFilterPresent(final Long filterListId, final String filterName) throws Exception {
        final Row r = new Row("ACFilter");
        r.set(1, (Object)filterListId);
        final String customFilterName = filterName.toLowerCase();
        r.set("FILTERNAME", (Object)customFilterName);
        return LookUpUtil.getPersistence().get("ACFilter", r).containsTable("ACFilter");
    }
    
    public static void deleteFilter(final Long filterListId, final String filterName) throws Exception {
        Row r = new Row("ACFilter");
        r.set(1, (Object)filterListId);
        r.set("FILTERNAME", (Object)filterName);
        r = LookUpUtil.getPersistence().get("ACFilter", r).getFirstRow("ACFilter");
        LookUpUtil.getPersistence().delete(r);
    }
    
    static {
        FilterAPI.fetchQuery = null;
    }
}
