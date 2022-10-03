package com.adventnet.client.components.cv.web;

import com.adventnet.client.components.table.web.TableUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.iam.xss.IAMEncoder;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Map;
import com.adventnet.client.util.DataUtils;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.StringTokenizer;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Enumeration;
import java.util.logging.Level;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import org.apache.struts.action.Action;

public class CVEditUtils extends Action implements WebConstants
{
    static Logger logger;
    
    public static Criteria getCriteria(final HttpServletRequest request) {
        final Enumeration enumeration = request.getParameterNames();
        String dummy = null;
        final String booleanoperator = request.getParameter("booleanoperator");
        final ArrayList searchtext = new ArrayList();
        final ArrayList searchfield = new ArrayList();
        final ArrayList arithmeticoperator = new ArrayList();
        final ArrayList textcondition = new ArrayList();
        final ArrayList actualconditionvector = new ArrayList();
        while (enumeration.hasMoreElements()) {
            dummy = enumeration.nextElement();
            if (dummy.indexOf("searchfield") >= 0 && dummy.indexOf("searchfieldtype") < 0) {
                searchfield.add(request.getParameter(dummy));
            }
            else {
                if (dummy.indexOf("searchfieldtype") < 0) {
                    continue;
                }
                final String text = request.getParameter(dummy);
                final String number = dummy.substring(dummy.indexOf("type") + 4);
                String actualdata = null;
                String actualcondition = null;
                Integer constant = null;
                if (text.equals("T")) {
                    actualdata = request.getParameter("searchtext" + number);
                    actualcondition = request.getParameter("textcondition" + number);
                }
                else if (text.equals("N")) {
                    actualdata = request.getParameter("searchnum" + number);
                    actualcondition = request.getParameter("numcondition" + number);
                }
                else if (text.equals("D")) {
                    actualdata = request.getParameter("searchdate" + number);
                    actualcondition = request.getParameter("datecondition" + number);
                }
                if (actualdata.equals("Start Date") || actualdata.equals("End Date")) {
                    actualdata = "${" + actualdata + "}";
                }
                searchtext.add(actualdata);
                actualconditionvector.add(actualcondition);
                constant = getQueryConstants(actualcondition);
                arithmeticoperator.add(constant);
            }
        }
        Criteria custom_view_criteria = null;
        final int searchsize = searchfield.size() - 1;
        final LinkedHashMap searchcolumnlist = getSearchFieldList(searchfield);
        String tablenamealone = null;
        String columnname = null;
        String conditionvalue = null;
        String obtainedvalue = null;
        for (int i = searchsize; i >= 0; --i) {
            tablenamealone = searchcolumnlist.get(searchfield.get(i));
            columnname = truncateTableName(searchfield.get(i));
            obtainedvalue = searchtext.get(i);
            conditionvalue = actualconditionvector.get(i);
            if (!columnname.equals("None") && !conditionvalue.equals("None") && !obtainedvalue.equals("")) {
                if (actualconditionvector.get(i).indexOf("starts") >= 0) {
                    obtainedvalue += "*";
                    searchtext.set(i, obtainedvalue);
                }
                else if (actualconditionvector.get(i).indexOf("ends") >= 0) {
                    obtainedvalue = "*" + obtainedvalue;
                    searchtext.set(i, obtainedvalue);
                }
                else if (actualconditionvector.get(i).indexOf("contain") >= 0) {
                    obtainedvalue = "*" + obtainedvalue + "*";
                    searchtext.set(i, obtainedvalue);
                }
                final Criteria cr = new Criteria(new Column(tablenamealone, columnname), (Object)obtainedvalue, (int)arithmeticoperator.get(i), false);
                if (i == searchsize || custom_view_criteria == null) {
                    custom_view_criteria = cr;
                }
                else if (booleanoperator.equalsIgnoreCase("and")) {
                    custom_view_criteria = custom_view_criteria.and(cr);
                }
                else if (booleanoperator.equalsIgnoreCase("or")) {
                    custom_view_criteria = custom_view_criteria.or(cr);
                }
            }
        }
        CVEditUtils.logger.log(Level.FINE, "searchfield vector = {0}", searchfield);
        CVEditUtils.logger.log(Level.FINE, "actualcondition vector = {0}", actualconditionvector);
        CVEditUtils.logger.log(Level.FINE, "arithmeticoperator vector = {0}", arithmeticoperator);
        CVEditUtils.logger.log(Level.FINE, "searchtext vector = {0}", searchtext);
        CVEditUtils.logger.log(Level.FINE, "booleanoperator  = {0}", booleanoperator);
        return custom_view_criteria;
    }
    
    private static LinkedHashMap getSearchFieldList(final ArrayList SearchField) {
        final LinkedHashMap SearchHash = new LinkedHashMap();
        String fullname = null;
        String tablename = null;
        for (int i = SearchField.size() - 1; i >= 0; --i) {
            fullname = SearchField.get(i);
            if (!fullname.equals("AGEINDAYS")) {
                tablename = truncateColumnName(fullname);
                SearchHash.put(fullname, tablename);
            }
        }
        return SearchHash;
    }
    
    private static String truncateColumnName(final String ColumnName) {
        if (ColumnName.indexOf(".") >= 0) {
            return ColumnName.substring(0, ColumnName.indexOf("."));
        }
        return ColumnName;
    }
    
    public static String truncateTableName(final String tableName) {
        if (tableName.indexOf(".") >= 0) {
            return tableName.substring(tableName.indexOf(".") + 1);
        }
        return tableName;
    }
    
    public static Integer getQueryConstants(final String comparator) {
        Integer constant = null;
        if (comparator.equals("is") || comparator.equals("=")) {
            constant = new Integer(0);
        }
        else if (comparator.equals("isn't") || comparator.equals("<>")) {
            constant = new Integer(1);
        }
        else if (comparator.equals("contains") || comparator.equals("starts with") || comparator.equals("ends with")) {
            constant = new Integer(2);
        }
        else if (comparator.equals("doesn't contain")) {
            constant = new Integer(3);
        }
        else if (comparator.equals("<")) {
            constant = new Integer(7);
        }
        else if (comparator.equals(">")) {
            constant = new Integer(5);
        }
        else if (comparator.equals("<=") || comparator.equals("is before")) {
            constant = new Integer(6);
        }
        else if (comparator.equals(">=") || comparator.equals("is after")) {
            constant = new Integer(4);
        }
        return constant;
    }
    
    public static HashMap getCriteriaMap(final Criteria cvCriteria, final LinkedHashMap column_info) throws Exception {
        final HashMap criteriaMap = getDummyCriteriaMap();
        if (cvCriteria != null) {
            updateCriteriaContents(cvCriteria, criteriaMap, column_info, 0);
        }
        return criteriaMap;
    }
    
    private static int updateCriteriaContents(final Criteria cvCriteria, final HashMap criteriaMap, final LinkedHashMap column_info, int operator_count) throws Exception {
        final ArrayList column_nameList = criteriaMap.get("column_name");
        final ArrayList column_valueList = criteriaMap.get("column_value");
        final ArrayList exactColumn_nameList = criteriaMap.get("exact_column_name");
        final ArrayList exactColumn_valueList = criteriaMap.get("exact_column_value");
        final ArrayList column_comparatorList = criteriaMap.get("column_comparator");
        final ArrayList numeric_comparatorList = criteriaMap.get("numeric_comparator");
        final ArrayList table_nameList = criteriaMap.get("table_name");
        final ArrayList operatorList = criteriaMap.get("logical_operator");
        final Criteria right_criteria = cvCriteria.getRightCriteria();
        final Criteria left_criteria = cvCriteria.getLeftCriteria();
        String nested_criteria_check = null;
        if (left_criteria == null && right_criteria == null) {
            final Column column_name = cvCriteria.getColumn();
            final int comparator = cvCriteria.getComparator();
            final Object column_value = cvCriteria.getValue();
            final String table_name_string = column_name.getTableAlias();
            final String column_name_string = discardTableName(column_name);
            String column_value_string = null;
            if (column_value != null) {
                column_value_string = getColumnValue((String)column_value);
            }
            String column_data_type = null;
            String comparator_string = null;
            if (column_value_string != null) {}
            if (column_name_string != null) {
                final TableDefinition def = MetaDataUtil.getTableDefinitionByName(table_name_string);
                column_data_type = getMappedDataType(def.getColumnDefinitionByName(column_name_string).getDataType());
                comparator_string = getComparatorString((String)column_value, comparator, column_data_type);
                column_nameList.add(column_info.get(table_name_string + "." + column_name_string));
                column_valueList.add(column_value_string);
                exactColumn_nameList.add(column_name.getColumnName());
                exactColumn_valueList.add(column_value);
                column_comparatorList.add(comparator_string);
                numeric_comparatorList.add(new Integer(comparator));
                table_nameList.add(table_name_string);
            }
            criteriaMap.put("column_name", column_nameList);
            criteriaMap.put("column_value", column_valueList);
            criteriaMap.put("exact_column_name", exactColumn_nameList);
            criteriaMap.put("exact_column_value", exactColumn_valueList);
            criteriaMap.put("column_comparator", column_comparatorList);
            criteriaMap.put("numeric_comparator", numeric_comparatorList);
            criteriaMap.put("table_name", table_nameList);
        }
        else if (left_criteria != null) {
            operator_count = updateCriteriaContents(left_criteria, criteriaMap, column_info, operator_count);
        }
        if (right_criteria != null) {
            nested_criteria_check = cvCriteria.getOperator();
            operatorList.add(nested_criteria_check);
            ++operator_count;
            operator_count = updateCriteriaContents(right_criteria, criteriaMap, column_info, operator_count);
        }
        if (operator_count > 0) {
            criteriaMap.put("logical_operator", operatorList);
            criteriaMap.put("logical_operator_count", new Integer(operator_count));
        }
        return operator_count;
    }
    
    private static HashMap getDummyCriteriaMap() {
        final HashMap<String, List> criteriaMap = new HashMap<String, List>();
        criteriaMap.put("column_name", new ArrayList());
        criteriaMap.put("column_value", new ArrayList());
        criteriaMap.put("exact_column_name", new ArrayList());
        criteriaMap.put("exact_column_value", new ArrayList());
        criteriaMap.put("column_comparator", new ArrayList());
        criteriaMap.put("numeric_comparator", new ArrayList());
        criteriaMap.put("table_name", new ArrayList());
        criteriaMap.put("logical_operator", new ArrayList());
        return criteriaMap;
    }
    
    public static String getComparatorString(final String value, final int comparator, final String datatype) {
        String comparatorString = "";
        if (datatype.equals("T") || datatype.equals("B")) {
            if (comparator == 0) {
                comparatorString = "is";
            }
            else if (comparator == 1) {
                comparatorString = "isn't";
            }
            if (comparator == 3) {
                comparatorString = "doesn't contain";
            }
            if (comparator == 2) {
                if (value.startsWith("*") && value.endsWith("*")) {
                    comparatorString = "contains";
                }
                else if (value.endsWith("*")) {
                    comparatorString = "starts with";
                }
                else if (value.startsWith("*")) {
                    comparatorString = "ends with";
                }
                else {
                    comparatorString = "contains";
                }
            }
        }
        else if (datatype.equals("D")) {
            if (comparator == 0) {
                comparatorString = "is";
            }
            else if (comparator == 1) {
                comparatorString = "isn't";
            }
            else if (comparator == 6) {
                comparatorString = "is before";
            }
            else if (comparator == 4) {
                comparatorString = "is after";
            }
        }
        else if (datatype.equals("N")) {
            if (comparator == 0) {
                comparatorString = "=";
            }
            else if (comparator == 1) {
                comparatorString = "<>";
            }
            else if (comparator == 4) {
                comparatorString = ">=";
            }
            else if (comparator == 5) {
                comparatorString = ">";
            }
            else if (comparator == 6) {
                comparatorString = "<=";
            }
            else if (comparator == 7) {
                comparatorString = "<";
            }
        }
        return comparatorString;
    }
    
    public static String discardTableName(final Column columnname) {
        String colname = columnname.getColumnName();
        if (colname.indexOf(".") > 0) {
            colname = colname.substring(colname.indexOf(".") + 1);
        }
        return colname;
    }
    
    public static String getMappedDataType(final String dataType) throws Exception {
        final String charectorType = "T";
        final String numericType = "N";
        final String dateType = "D";
        final String booleanType = "B";
        final String notSupportedType = "NOT_SUPPORTED_DATATYPE";
        if (dataType.equals("CHAR")) {
            return charectorType;
        }
        if (dataType.equals("INTEGER")) {
            return numericType;
        }
        if (dataType.equals("BIGINT")) {
            return numericType;
        }
        if (dataType.equals("DOUBLE")) {
            return numericType;
        }
        if (dataType.equals("FLOAT")) {
            return numericType;
        }
        if (dataType.equals("BOOLEAN")) {
            return booleanType;
        }
        if (dataType.equals("DATE")) {
            return dateType;
        }
        if (dataType.equals("DATETIME")) {
            return dateType;
        }
        if (dataType.equals("TIME")) {
            return dateType;
        }
        if (dataType.equals("TIMESTAMP")) {
            return dateType;
        }
        return notSupportedType;
    }
    
    public static String getColumnValue(final String value) {
        final StringTokenizer stk = new StringTokenizer(value, "*");
        String temporary = "";
        while (stk.hasMoreTokens()) {
            temporary = stk.nextToken();
        }
        return temporary;
    }
    
    public static void updateRequestWithCriteriaDetails(final HttpServletRequest request, final DataObject dobj, final Criteria curCr) throws Exception {
        updateRequestWithCriteriaDetails(request, dobj, curCr, "ACColumnConfiguration", "COLUMNALIAS", "DISPLAYNAME");
    }
    
    public static void updateRequestWithCriteriaDetails(final HttpServletRequest request, final DataObject dobj, final Criteria curCr, final String criteriaColListTableName, final String criteriaColListColumnName, final String criteriaColListColDispName) throws Exception {
        Row row = null;
        final LinkedHashMap<String, String> criteria_columns = new LinkedHashMap<String, String>();
        final LinkedHashMap<String, String> criteria_columns_datatype = new LinkedHashMap<String, String>();
        final LinkedHashMap<String, String> criteria_table_info = new LinkedHashMap<String, String>();
        final LinkedHashMap<String, String> criteria_column_info = new LinkedHashMap<String, String>();
        final Iterator<Row> itr = dobj.getRows(criteriaColListTableName);
        while (itr.hasNext()) {
            row = itr.next();
            criteria_columns.put((String)row.get(criteriaColListColumnName), (String)row.get(criteriaColListColDispName));
        }
        request.setAttribute("CRITERIA_COLUMNS", (Object)criteria_columns);
        String tableName = null;
        String dataType = null;
        TableDefinition def = null;
        Row temp_row = null;
        final List<Row> tableList = getIteratorAsList(dobj.getRows("SelectTable"));
        for (int i = 0; i < tableList.size(); ++i) {
            row = tableList.get(i);
            tableName = (String)row.get("TABLENAME");
            def = MetaDataUtil.getTableDefinitionByName(tableName);
            final Iterator<Row> itr2 = dobj.getRows("SelectColumn", row);
            while (itr2.hasNext()) {
                temp_row = itr2.next();
                dataType = def.getColumnDefinitionByName((String)temp_row.get("COLUMNNAME")).getDataType();
                criteria_columns_datatype.put((String)temp_row.get("COLUMNALIAS"), getMappedDataType(dataType));
                criteria_table_info.put((String)temp_row.get("COLUMNALIAS"), tableName + "." + temp_row.get("COLUMNNAME"));
                criteria_column_info.put(tableName + "." + temp_row.get("COLUMNNAME"), (String)temp_row.get("COLUMNALIAS"));
            }
        }
        request.setAttribute("CRITERIA_TABLE_INFO", (Object)criteria_table_info);
        request.setAttribute("CRITERIA_COLUMNS_DATATYPE", (Object)criteria_columns_datatype);
        request.setAttribute("CRITERIA_COLUMN_INFO", (Object)criteria_column_info);
        request.setAttribute("SELECTQUERY_ID", dobj.getFirstValue("SelectTable", "QUERYID"));
        request.setAttribute("CUSTOM_VIEW_DETAILS", (Object)((curCr == null) ? getDummyCriteriaMap() : getCriteriaMap(curCr, criteria_column_info)));
    }
    
    public static List<Row> getIteratorAsList(final Iterator<Row> itr) throws Exception {
        final List<Row> list = new ArrayList<Row>();
        while (itr.hasNext()) {
            list.add(itr.next());
        }
        return list;
    }
    
    public static SelectQuery getSQToFetchTablesAndColumns(final String viewName) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("SelectColumn"));
        query.addSelectColumn(new Column("SelectColumn", "*"));
        query.addSelectColumn(new Column("SelectTable", "*"));
        query.addSelectColumn(new Column("ACColumnConfiguration", "*"));
        query.addSortColumn(new SortColumn("ACColumnConfiguration", "COLUMNINDEX", true));
        query.addJoin(new Join("SelectColumn", "SelectTable", new String[] { "QUERYID", "TABLEALIAS" }, new String[] { "QUERYID", "TABLEALIAS" }, 2));
        query.addJoin(new Join("SelectColumn", "CustomViewConfiguration", new String[] { "QUERYID" }, new String[] { "QUERYID" }, 2));
        query.addJoin(new Join("CustomViewConfiguration", "ACTableViewConfig", new String[] { "CVID" }, new String[] { "CVNAME" }, 2));
        query.addJoin(new Join("ACTableViewConfig", "ACColumnConfiguration", new String[] { "COLUMNCONFIGLIST" }, new String[] { "CONFIGNAME" }, 2));
        Criteria cri = new Criteria(new Column("SelectColumn", "COLUMNALIAS"), (Object)new Column("ACColumnConfiguration", "COLUMNALIAS"), 0);
        cri = cri.and(new Column("ACTableViewConfig", "NAME"), (Object)WebViewAPI.getViewNameNo((Object)viewName), 0);
        query.setCriteria(cri);
        return query;
    }
    
    public static DataObject getCVConfigurationDO(final Object cvId) throws Exception {
        DataObject customviewConfigurationDO = null;
        final List peerslist = new ArrayList();
        peerslist.add("CustomViewConfiguration");
        peerslist.add("SelectQuery");
        final List DeepRetList = peerslist;
        final Criteria cvCondition = new Criteria(new Column("CustomViewConfiguration", 1), cvId, 0);
        customviewConfigurationDO = LookUpUtil.getRecord(peerslist, DeepRetList, cvCondition);
        return customviewConfigurationDO;
    }
    
    public static WritableDataObject getNewCustomViewConfig(final Object cvId) throws Exception {
        Row r = new Row("CustomViewConfiguration");
        r.set(1, cvId);
        final DataObject dataObj = LookUpUtil.getPersistence().get("CustomViewConfiguration", r);
        r = dataObj.getFirstRow("CustomViewConfiguration");
        final long queryId = (long)r.get("QUERYID");
        final WritableDataObject queryDO = (WritableDataObject)QueryUtil.getSelectQueryDO(queryId);
        final Object newId = new Row("SelectQuery").get("QUERYID");
        DataUtils.cascadeChangePKColumn((DataObject)queryDO, "SelectQuery", "QUERYID", newId);
        r.set("QUERYID", newId);
        queryDO.addRow(r);
        return queryDO;
    }
    
    public static String generateDefn(final DataObject dobj) throws Exception {
        final StringBuffer scriptBuf = new StringBuffer("\nvar cr = new Criteria('ViewEditCriteria')");
        final Map dataTypeMap = new HashMap();
        final Map valueMap = new HashMap();
        final Iterator tblIter = dobj.getRows("SelectTable");
        while (tblIter.hasNext()) {
            final Row row = tblIter.next();
            final TableDefinition def = MetaDataUtil.getTableDefinitionByName((String)row.get("TABLENAME"));
            final Iterator itr = dobj.getRows("SelectColumn", row);
            while (itr.hasNext()) {
                final Row temp_row = itr.next();
                final String dataType = def.getColumnDefinitionByName((String)temp_row.get("COLUMNNAME")).getDataType();
                final String defnType = getDefnType(dataType);
                if (defnType == null) {
                    continue;
                }
                dataTypeMap.put(temp_row.get("COLUMNALIAS"), defnType);
                valueMap.put(temp_row.get("COLUMNALIAS"), row.get("TABLEALIAS") + "." + temp_row.get("COLUMNNAME"));
            }
        }
        final Iterator itr2 = dobj.getRows("ACColumnConfiguration");
        while (itr2.hasNext()) {
            final Row row = itr2.next();
            final String colAlias = (String)row.get(3);
            final String dispName = (String)row.get(4);
            final String tblCol = valueMap.get(colAlias);
            if (tblCol != null) {
                scriptBuf.append("\ncr.addDfn({COLNAME:'").append(tblCol).append("',DISPLAYNAME:'").append(dispName).append("',TYPE:'").append(dataTypeMap.get(colAlias)).append("'});");
            }
        }
        return scriptBuf.toString();
    }
    
    public static String generateCriteriaList(final DataObject dobj) throws Exception {
        final StringBuffer scriptBuf = new StringBuffer("[");
        final List rowList = DataUtils.getSortedList(dobj, "ACRelationalCriteria", "RELATIONALCRITERIAID");
        for (int i = 0; i < rowList.size(); ++i) {
            final Row row = rowList.get(i);
            scriptBuf.append("\n{COLNAME:'").append(row.get(3)).append('.').append(row.get(4));
            String value = (String)row.get(6);
            value = StringEscapeUtils.unescapeJava(value);
            final String tableName = (String)row.get("TABLEALIAS");
            final String columnName = (String)row.get("COLUMNNAME");
            final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(tableName);
            final ColumnDefinition cdef = tdef.getColumnDefinitionByName(tdef.getDefinedColumnName(columnName));
            String dataType = null;
            if (cdef != null) {
                dataType = cdef.getDataType();
            }
            if ("DATE".equals(dataType)) {
                value = value.substring(5, 7) + "/" + value.substring(8) + "/" + value.substring(0, 4);
            }
            else if (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7) {
                value = value.substring(5, 7) + "/" + value.substring(8) + "/" + value.substring(0, 4);
            }
            int comparator = (int)row.get(5);
            if (value == null) {
                comparator += 30;
            }
            else {
                value = IAMEncoder.encodeJavaScript(value);
                scriptBuf.append("',COLVALUE:'").append(value);
            }
            scriptBuf.append("',COMPARATOR:'").append(comparator).append("'},");
        }
        scriptBuf.deleteCharAt(scriptBuf.length() - 1);
        scriptBuf.append("]");
        return scriptBuf.toString();
    }
    
    public static String getLogicalRepresentation(final DataObject dobj) throws Exception {
        return (((String)dobj.getFirstValue("ACCriteria", 2)).indexOf("&") > -1) ? "'and'" : "'or'";
    }
    
    public static Object addCriteriaToDO(final HttpServletRequest request, final DataObject criteriaDO) throws Exception {
        final String[] rowIds = request.getParameterValues("ROWIDX");
        final Row crRow = new Row("ACCriteria");
        final Object crId = crRow.get(1);
        criteriaDO.addRow(crRow);
        final StringBuilder logicalrep = new StringBuilder();
        if (rowIds.length > 1) {
            logicalrep.append('(');
        }
        final char separator = "or".equals(request.getParameter("booleanoperator")) ? '|' : '&';
        for (int i = 0; i < rowIds.length; ++i) {
            final Row relRow = new Row("ACRelationalCriteria");
            relRow.set(1, crId);
            relRow.set(2, (Object)i);
            String colName = request.getParameter("COLNAME_" + rowIds[i]);
            final int index = colName.indexOf(".");
            final String tblAlias = colName.substring(0, index);
            colName = colName.substring(index + 1);
            relRow.set(3, (Object)tblAlias);
            relRow.set(4, (Object)colName);
            relRow.set("CASESENSITIVE", (Object)false);
            final int comparator = Integer.parseInt(request.getParameter("COMPARATOR_" + rowIds[i]));
            String value = request.getParameter("COLVALUE_" + rowIds[i]);
            final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(tblAlias);
            final ColumnDefinition cdef = tdef.getColumnDefinitionByName(tdef.getDefinedColumnName(colName));
            String dataType = null;
            dataType = cdef.getDataType();
            if (dataType != null && dataType.contains("DATE")) {
                final String s1 = value.substring(6);
                final String s2 = value.substring(0, 2);
                final String s3 = value.substring(3, 5);
                value = s1 + "-" + s2 + "-" + s3;
            }
            else {
                value = TableUtil.escapeSpecialCharacters(value);
            }
            relRow.set(6, (Object)value);
            relRow.set(5, (Object)comparator);
            criteriaDO.addRow(relRow);
            logicalrep.append(i).append(separator);
        }
        logicalrep.deleteCharAt(logicalrep.length() - 1);
        if (rowIds.length > 1) {
            logicalrep.append(')');
        }
        crRow.set(2, (Object)logicalrep.toString());
        return crId;
    }
    
    private static String getDefnType(final String dataType) {
        if (dataType.equals("CHAR") || dataType.equals("SCHAR")) {
            return "CHAR_CRDEF";
        }
        if (dataType.equals("BIGINT") || dataType.equals("INTEGER")) {
            return "INTEGER_CRDEF";
        }
        if (dataType.equals("FLOAT") || dataType.equals("DOUBLE")) {
            return "FLOAT_CRDEF";
        }
        if (dataType.equals("DATE") || dataType.equals("DATETIME")) {
            return "DATE_CRDEF";
        }
        throw new RuntimeException("Datatype not handled!!");
    }
    
    static {
        CVEditUtils.logger = Logger.getLogger(CVEditUtils.class.getName());
    }
}
