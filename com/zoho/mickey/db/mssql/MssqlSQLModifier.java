package com.zoho.mickey.db.mssql;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Locale;
import com.adventnet.ds.query.Range;
import java.util.logging.Logger;
import com.zoho.mickey.db.AbstractSQLModifier;

public class MssqlSQLModifier extends AbstractSQLModifier
{
    private static final Logger LOGGER;
    
    @Override
    public String getSQLForSelectWithRange(String sql, final Range range) throws QueryConstructionException {
        String sqlToReturn = sql;
        if (range != null) {
            StringBuilder sb = new StringBuilder();
            boolean isDistinctUsed = true;
            int numberOfRows = range.getNumberOfObjects();
            String selString = "SELECT DISTINCT";
            final String queryString = sql.toUpperCase(Locale.ENGLISH);
            int selectIndex = queryString.startsWith(selString) ? queryString.indexOf(selString) : -1;
            if (selectIndex < 0) {
                selString = "SELECT";
                selectIndex = queryString.indexOf(selString);
                isDistinctUsed = false;
            }
            final String ORDER_BY = "order by";
            int startIndex = range.getStartIndex();
            numberOfRows = ((numberOfRows < 0) ? 0 : numberOfRows);
            startIndex = ((startIndex < 1) ? 0 : startIndex);
            if (Boolean.valueOf(PersistenceInitializer.getConfigurationValue("use_top_for_range"))) {
                if (numberOfRows > 0) {
                    MssqlSQLModifier.LOGGER.warning(" TOP clause is being used to form range query in MSSQL Kindly use proper column alias if the same column name from different tables are added in select query.");
                    final String rangeSql = " TOP " + (startIndex + numberOfRows) + " ";
                    sb.append(selString + rangeSql);
                    sb.append(sql.substring(selectIndex + selString.length()));
                    sqlToReturn = sb.toString();
                }
            }
            else {
                final int index = sql.toLowerCase(Locale.ENGLISH).lastIndexOf(ORDER_BY);
                if (index <= 0) {
                    throw new QueryConstructionException("Cannot construct range query without 'ORDER BY' clause");
                }
                MssqlSQLModifier.LOGGER.log(Level.FINER, "query before modification ::{0}", sql);
                String orderByCols = sql.substring(index + ORDER_BY.length());
                orderByCols = orderByCols.trim();
                sql = sql.substring(0, index);
                final String origsql = sql.substring(selectIndex + selString.length());
                final int fromIdx = this.getIndexAfterFrom(origsql);
                final String orderByClause = this.getOrderByClause(origsql.substring(0, fromIdx), orderByCols, isDistinctUsed, false);
                final StringBuilder row_number = new StringBuilder();
                row_number.append(", ROW_NUMBER() OVER ( ").append(orderByClause).append(" ) AS ROW_NUM ");
                final StringBuilder rangeSql2 = new StringBuilder(" ORG_QUERY.ROW_NUM BETWEEN ");
                rangeSql2.append(startIndex);
                rangeSql2.append(" AND ");
                if (numberOfRows > 0) {
                    rangeSql2.append((startIndex < 1) ? numberOfRows : (startIndex - 1 + numberOfRows));
                }
                else {
                    rangeSql2.append(Long.MAX_VALUE);
                }
                sb = new StringBuilder();
                if (isDistinctUsed) {
                    sb.append("SELECT * FROM (");
                    sb.append("SELECT *");
                    sb.append((CharSequence)row_number);
                    sb.append(" from (");
                    sb.append(selString);
                    sb.append(" ");
                    sb.append(origsql);
                    sb.append(") AS  INNER_QUERY ");
                    sb.append(") AS ORG_QUERY ");
                    sb.append("WHERE ");
                    sb.append((CharSequence)rangeSql2);
                }
                else {
                    sb.append("SELECT * FROM (");
                    sb.append(selString);
                    sb.append(" ");
                    sb.append(origsql.substring(0, fromIdx));
                    sb.append((CharSequence)row_number);
                    sb.append(origsql.substring(fromIdx));
                    sb.append(") AS  ORG_QUERY ");
                    sb.append("WHERE ");
                    sb.append((CharSequence)rangeSql2);
                }
                sqlToReturn = sb.toString();
            }
        }
        MssqlSQLModifier.LOGGER.log(Level.FINER, " Final SQL {0}", sqlToReturn);
        return sqlToReturn;
    }
    
    private int getIndexAfterFrom(final String origsql) {
        final String select_sql = origsql.toLowerCase(Locale.ENGLISH);
        int openBraceIdx = select_sql.indexOf("(");
        int fromIdx = this.getMatchingPatternStartIndex(select_sql, " from+\\(| from |\"from ");
        int closeBraceIdx = 0;
        if (openBraceIdx != -1 && fromIdx > openBraceIdx && !select_sql.trim().startsWith("*")) {
            while (select_sql.indexOf("(", closeBraceIdx) != -1) {
                openBraceIdx = select_sql.indexOf("(", closeBraceIdx);
                final int fromIdxNew = this.getMatchingPatternStartIndex(select_sql.substring(closeBraceIdx, openBraceIdx), " from+\\(| from |\"from ");
                if (fromIdxNew != -1) {
                    break;
                }
                closeBraceIdx = this.getClosingBrace(select_sql, openBraceIdx);
            }
        }
        final int fromIdxNew = this.getMatchingPatternStartIndex(select_sql.substring(closeBraceIdx), " from+\\(| from |\"from ");
        if (fromIdxNew != -1) {
            fromIdx = closeBraceIdx + fromIdxNew + 1;
        }
        return fromIdx;
    }
    
    private int getMatchingPatternStartIndex(final String sql, final String regex) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            return matcher.start();
        }
        return -1;
    }
    
    private int getClosingBrace(final String sql, final int openBraceIdx) {
        int braceCnt = 1;
        for (int idx = openBraceIdx + 1; idx < sql.length(); ++idx) {
            final char charAtIdx = sql.charAt(idx);
            if (charAtIdx == '(') {
                ++braceCnt;
            }
            if (charAtIdx == ')' && --braceCnt == 0) {
                return idx;
            }
        }
        return -1;
    }
    
    private String getOrderByClause(String select_sql, final String orderByCols, final boolean isDistinctUsed, final boolean isUnion) {
        if (select_sql.trim().startsWith("*")) {
            return " ORDER BY " + orderByCols;
        }
        final String[] sortColumns = orderByCols.split(",");
        final String[] columns = new String[sortColumns.length];
        final String[] order = new String[sortColumns.length];
        int j = 0;
        for (String sortCol : sortColumns) {
            sortCol = sortCol.trim();
            final String[] colOrder = sortCol.trim().split(" ");
            columns[j] = colOrder[0];
            order[j] = ((colOrder.length > 1) ? colOrder[colOrder.length - 1] : "ASC");
            if (sortCol.startsWith("(")) {
                final int colEndIdx = this.getClosingBrace(sortCol, 0);
                columns[j] = sortCol.substring(0, colEndIdx + 1);
                order[j] = ((sortCol.length() - 1 <= colEndIdx) ? "ASC" : sortCol.substring(colEndIdx + 1));
            }
            ++j;
        }
        final Map<Object, String> colNameMap = new HashMap<Object, String>();
        int openBraceIdx = 1;
        int closeIdx = 0;
        int startIndex;
        int endIndex;
        for (int colIdx = 0; select_sql.indexOf("(", openBraceIdx) != -1; select_sql = select_sql.substring(0, startIndex + 1) + "column" + colIdx + select_sql.substring(endIndex), ++colIdx) {
            openBraceIdx = select_sql.indexOf("(", openBraceIdx);
            closeIdx = this.getClosingBrace(select_sql, openBraceIdx);
            startIndex = select_sql.lastIndexOf(",", openBraceIdx);
            startIndex = ((startIndex > 0) ? startIndex : 0);
            int nextFnIdx = select_sql.indexOf("(", closeIdx);
            if (nextFnIdx != -1) {
                while (nextFnIdx != -1 && select_sql.substring(closeIdx, nextFnIdx).indexOf(",") == -1) {
                    closeIdx = this.getClosingBrace(select_sql, nextFnIdx);
                    nextFnIdx = select_sql.indexOf("(", closeIdx);
                }
            }
            endIndex = select_sql.indexOf(",", closeIdx);
            endIndex = ((endIndex > 0) ? endIndex : select_sql.length());
            final String column = select_sql.substring(startIndex + 1, endIndex);
            colNameMap.put("column" + colIdx, column);
        }
        final StringBuilder orderByString = new StringBuilder(" ORDER BY ");
        MssqlSQLModifier.LOGGER.log(Level.FINER, "Query after Regex ::{0}", select_sql);
        final String[] selectColumns = select_sql.split(",");
        final Map<String, String> ColumnAliasVsName = new HashMap<String, String>();
        for (int idx = 0; idx < selectColumns.length; ++idx) {
            String columnName = selectColumns[idx].trim();
            columnName = ((colNameMap.get(columnName) != null) ? colNameMap.get(columnName) : columnName);
            final String[] columnNameAndAlias = columnName.trim().split(" ");
            if (columnNameAndAlias.length > 1) {
                ColumnAliasVsName.put(columnNameAndAlias[columnNameAndAlias.length - 1], columnNameAndAlias[0]);
            }
        }
        int cnt = 0;
        for (final String column2 : columns) {
            String sortCol2 = "";
            if (cnt != 0) {
                orderByString.append(", ");
            }
            try {
                final int ind = Integer.parseInt(column2) - 1;
                sortCol2 = selectColumns[ind];
            }
            catch (final NumberFormatException nfe) {
                sortCol2 = column2;
            }
            sortCol2 = sortCol2.trim();
            if (isDistinctUsed) {
                final String[] sortColumn = sortCol2.split(" ");
                String sort_column = sortColumn[sortColumn.length - 1];
                if (colNameMap.get(String.valueOf(sort_column)) != null) {
                    sort_column = colNameMap.get(String.valueOf(sort_column));
                }
                final int aliasIndex = sort_column.toLowerCase(Locale.ENGLISH).lastIndexOf(" as ");
                if (sort_column.split(" ").length > 1) {
                    sort_column = ((aliasIndex != -1) ? sort_column.substring(aliasIndex + 3) : sort_column.substring(sort_column.lastIndexOf(" ")));
                }
                final String[] temp = sort_column.split("\"\\.");
                if (temp.length > 1) {
                    sortCol2 = temp[temp.length - 1];
                    sortCol2 = (sortCol2.trim().endsWith(")") ? sortCol2.substring(0, sortCol2.length() - 1) : sortCol2);
                }
                else {
                    sortCol2 = sort_column;
                }
            }
            else {
                final StringTokenizer st = new StringTokenizer(sortCol2, " ", false);
                if (st.hasMoreTokens()) {
                    String sort_column = st.nextToken();
                    if (colNameMap.get(String.valueOf(sort_column)) != null) {
                        sort_column = colNameMap.get(String.valueOf(sort_column));
                        final int aliasIndex = sort_column.toLowerCase(Locale.ENGLISH).lastIndexOf(" as ");
                        sortCol2 = ((aliasIndex != -1) ? sort_column.substring(0, aliasIndex) : sort_column.substring(0, sort_column.lastIndexOf(" ")));
                    }
                    else if (isUnion) {
                        final Map<String, String> columnNameVsAlias = new HashMap<String, String>();
                        for (final Map.Entry<String, String> entry : ColumnAliasVsName.entrySet()) {
                            columnNameVsAlias.put(entry.getValue(), entry.getKey());
                        }
                        if (columnNameVsAlias.get(sort_column) != null) {
                            sortCol2 = columnNameVsAlias.get(sort_column);
                        }
                        else {
                            final int column_idx = sort_column.indexOf(".");
                            if (column_idx > 0) {
                                sortCol2 = sort_column.substring(column_idx + 1);
                            }
                            else {
                                sortCol2 = sort_column;
                            }
                        }
                    }
                    else {
                        sortCol2 = ((ColumnAliasVsName.get(sort_column) != null) ? ColumnAliasVsName.get(sort_column) : sort_column);
                    }
                }
            }
            orderByString.append(sortCol2).append(" ").append(order[cnt]);
            ++cnt;
        }
        MssqlSQLModifier.LOGGER.log(Level.FINE, "orderByString {0}", orderByString);
        return orderByString.toString();
    }
    
    @Override
    public String getSQLForUnionWithRange(String unionSQL, final Range range) throws QueryConstructionException {
        MssqlSQLModifier.LOGGER.log(Level.FINE, "plain unionsql string : {0}", unionSQL);
        final String ORDER_BY = "order by";
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM (");
        sb.append("SELECT * ");
        String row_number = ", ROW_NUMBER() OVER ( ";
        int index = unionSQL.toLowerCase(Locale.ENGLISH).lastIndexOf(ORDER_BY);
        String orderByCols = unionSQL.substring(index + ORDER_BY.length());
        orderByCols = orderByCols.trim();
        unionSQL = unionSQL.substring(0, index);
        final int union_index = unionSQL.toLowerCase(Locale.ENGLISH).indexOf("union");
        String leftQueryString = unionSQL.substring(0, union_index);
        final String select = "(SELECT";
        final String select2 = "(SELECT * FROM (SELECT";
        final String compare_query = leftQueryString.substring(0, select2.length());
        boolean append_query = false;
        append_query = compare_query.equals(select2);
        final int lastClosingIndex = leftQueryString.lastIndexOf(")") - 1;
        leftQueryString = (append_query ? leftQueryString.substring(select2.length(), leftQueryString.lastIndexOf(")", lastClosingIndex)) : leftQueryString.substring(select.length(), leftQueryString.lastIndexOf(")")));
        index = this.getIndexAfterFrom(leftQueryString);
        leftQueryString = leftQueryString.substring(0, index);
        orderByCols = this.getOrderByClause(leftQueryString, orderByCols, false, true);
        if (orderByCols != null) {
            row_number += orderByCols;
        }
        MssqlSQLModifier.LOGGER.log(Level.FINE, "added orderby in unionsql string : {0}", row_number);
        row_number += " ) AS NEW_ROW_NUM FROM ( ";
        sb.append(row_number);
        sb.append(unionSQL);
        sb.append(") NEW_ROW ");
        sb.append(") AS  ORG_QUERY ");
        if (range != null) {
            sb.append("WHERE ");
            int numberOfRows = range.getNumberOfObjects();
            int startIndex = range.getStartIndex();
            numberOfRows = ((numberOfRows < 0) ? 0 : numberOfRows);
            startIndex = ((startIndex < 1) ? 0 : startIndex);
            final StringBuilder rangeSql = new StringBuilder(" ORG_QUERY.NEW_ROW_NUM BETWEEN ");
            rangeSql.append(startIndex);
            rangeSql.append(" AND ");
            if (numberOfRows > 0) {
                rangeSql.append((startIndex < 1) ? numberOfRows : (startIndex - 1 + numberOfRows));
            }
            else {
                rangeSql.append(Long.MAX_VALUE);
            }
            sb.append((CharSequence)rangeSql);
        }
        MssqlSQLModifier.LOGGER.log(Level.FINE, "unionQuery : {0}", sb.toString());
        return sb.toString();
    }
    
    static {
        LOGGER = Logger.getLogger(MssqlSQLModifier.class.getName());
    }
}
