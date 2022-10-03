package com.adventnet.ds.query;

import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.Locale;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.lang.reflect.Array;
import java.util.Arrays;
import org.json.JSONArray;
import java.util.List;
import org.json.JSONObject;
import java.util.Objects;

public class QueryToJsonConverter
{
    public static QueryToJsonConverter createNewQueryToJsonConverter() {
        try {
            return createNewQueryToJsonConverter((String)null);
        }
        catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static QueryToJsonConverter createNewQueryToJsonConverter(final String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (className == null) {
            return new QueryToJsonConverter();
        }
        final Class<? extends QueryToJsonConverter> clazz = (Class<? extends QueryToJsonConverter>)Thread.currentThread().getContextClassLoader().loadClass(className);
        return createNewQueryToJsonConverter(clazz);
    }
    
    protected static QueryToJsonConverter createNewQueryToJsonConverter(final Class<? extends QueryToJsonConverter> clazz) throws InstantiationException, IllegalAccessException {
        Objects.requireNonNull(clazz, "QueryToJsonConverter class cannot be null");
        return (QueryToJsonConverter)clazz.newInstance();
    }
    
    QueryToJsonConverter() {
    }
    
    public JSONObject fromQuery(final Query query) {
        if (query instanceof SelectQuery) {
            return this.fromSelectQuery((SelectQuery)query);
        }
        if (query instanceof UnionQuery) {
            return this.fromUnionQuery((UnionQuery)query);
        }
        throw new IllegalArgumentException();
    }
    
    protected JSONObject fromSelectQuery(final SelectQuery selectQuery) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("query_type", (Object)"selectQuery");
        jsonObject.put("table", (Object)this.fromSelectTable(selectQuery.getTableList().get(0)));
        jsonObject.put("selectColumns", (Object)this.fromColumns(selectQuery.getSelectColumns()));
        if (selectQuery.getCriteria() != null) {
            jsonObject.put("criteria", (Object)this.fromCriteria(selectQuery.getCriteria()));
        }
        if (selectQuery.getRange() != null) {
            jsonObject.put("range", (Object)this.fromRange(selectQuery.getRange()));
        }
        if (!selectQuery.getSortColumns().isEmpty()) {
            jsonObject.put("sortColumns", (Object)this.fromSortColumns(selectQuery.getSortColumns()));
        }
        if (!selectQuery.getGroupByColumns().isEmpty()) {
            jsonObject.put("groupByColumns", (Object)this.fromGroupByColumns(selectQuery.getGroupByColumns()));
        }
        else if (selectQuery.getGroupByClause() != null) {
            jsonObject.put("groupByClause", (Object)this.fromGroupByClause(selectQuery.getGroupByClause()));
        }
        if (!selectQuery.getJoins().isEmpty()) {
            jsonObject.put("join", (Object)this.fromJoins(selectQuery.getJoins()));
        }
        if (selectQuery.isDistinct()) {
            jsonObject.put("distinct", selectQuery.isDistinct());
        }
        if (selectQuery.getLockStatus()) {
            jsonObject.put("lock", selectQuery.getLockStatus());
        }
        if (selectQuery.isParallelSelect()) {
            jsonObject.put("parallel", selectQuery.isParallelSelect());
        }
        if (selectQuery.getParallelWorkers() > 0) {
            jsonObject.put("parallel_workers", selectQuery.getParallelWorkers());
        }
        return jsonObject;
    }
    
    public JSONArray fromJoins(final List<Join> joins) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < joins.size(); ++i) {
            jsonArray.put((Object)this.fromJoin(joins.get(i)));
        }
        return jsonArray;
    }
    
    public JSONObject fromJoin(final Join join) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("joinType", join.getJoinType());
        jsonObject.put("referencedTable", (Object)this.fromTable(join.getReferencedTable()));
        jsonObject.put("baseTable", (Object)this.fromTable(join.getBaseTable()));
        if (join.getCriteria() != null) {
            jsonObject.put("criteria", (Object)this.fromJoinCriteria(join.getCriteria()));
        }
        else {
            final JSONArray baseTableColumnsJsonArray = new JSONArray();
            final JSONArray referencedTableColumnsJsonArray = new JSONArray();
            final String[] baseTableColumns = join.getBaseTableColumns();
            final String[] referencedTableColumns = join.getReferencedTableColumns();
            for (int i = 0; i < baseTableColumns.length; ++i) {
                baseTableColumnsJsonArray.put((Object)baseTableColumns[i]);
                referencedTableColumnsJsonArray.put((Object)referencedTableColumns[i]);
            }
            jsonObject.put("baseTableColumns", (Object)baseTableColumnsJsonArray);
            jsonObject.put("referencedTableColumns", (Object)referencedTableColumnsJsonArray);
        }
        return jsonObject;
    }
    
    protected JSONObject fromJoinCriteria(final Criteria criteria) {
        return this.fromCriteria(criteria);
    }
    
    protected JSONObject fromGroupByClause(final GroupByClause groupByClause) {
        final JSONObject jsonObject = new JSONObject();
        final List groupByColumns = groupByClause.getGroupByColumns();
        if (!groupByColumns.isEmpty()) {
            jsonObject.put("columns", (Object)this.fromGroupByColumns(groupByColumns));
        }
        if (groupByClause.getCriteriaForHavingClause() != null) {
            jsonObject.put("havingCriteria", (Object)this.fromCriteria(groupByClause.getCriteriaForHavingClause()));
        }
        return jsonObject;
    }
    
    protected JSONArray fromGroupByColumns(final List groupByColumns) {
        final JSONArray groupByColumnsJson = new JSONArray();
        for (int i = 0; i < groupByColumns.size(); ++i) {
            final Object column = groupByColumns.get(i);
            if (column instanceof Column) {
                groupByColumnsJson.put((Object)this.fromColumn((Column)column));
            }
            else if (column instanceof GroupByColumn) {
                groupByColumnsJson.put((Object)this.fromGroupByColumn((GroupByColumn)column));
            }
        }
        return groupByColumnsJson;
    }
    
    protected JSONObject fromGroupByColumn(final GroupByColumn groupByColumn) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("column", (Object)this.fromColumn(groupByColumn.getGroupByColumn()));
        jsonObject.put("caseSensitive", groupByColumn.isCaseSensitive());
        return jsonObject;
    }
    
    protected JSONArray fromSortColumns(final List<SortColumn> sortColumns) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < sortColumns.size(); ++i) {
            jsonArray.put((Object)this.fromSortColumn(sortColumns.get(i)));
        }
        return jsonArray;
    }
    
    protected JSONObject fromSortColumn(final SortColumn sortColumn) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("column", (Object)this.fromColumn(sortColumn.getColumn()));
        jsonObject.put("isAscending", sortColumn.isAscending());
        jsonObject.put("isCaseSensitive", sortColumn.isCaseSensitive());
        jsonObject.put("isNullsFirst", (Object)sortColumn.isNullsFirst());
        return jsonObject;
    }
    
    protected JSONObject fromRange(final Range range) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", range.getStartIndex());
        jsonObject.put("count", range.getNumberOfObjects());
        return jsonObject;
    }
    
    public JSONObject fromCriteria(final Criteria criteria) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("negated", criteria.isNegate());
        if (criteria.getLeftCriteria() == null && criteria.getRightCriteria() == null) {
            jsonObject.put("criterion", (Object)this.fromSimpleCriteria(criteria));
        }
        else if (criteria.getLeftCriteria() != null) {
            jsonObject.put("leftCriteria", (Object)this.fromCriteria(criteria.getLeftCriteria()));
        }
        if (criteria.getRightCriteria() != null) {
            jsonObject.put("operator", (Object)criteria.getOperator());
            jsonObject.put("rightCriteria", (Object)this.fromCriteria(criteria.getRightCriteria()));
        }
        return jsonObject;
    }
    
    protected JSONObject fromSimpleCriteria(final Criteria criteria) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("column", (Object)this.fromColumn(criteria.getColumn()));
        jsonObject.put("comparator", criteria.getComparator());
        jsonObject.put("caseSensitive", criteria.isCaseSensitive());
        final Object value = criteria.getValue();
        if (value instanceof Column) {
            jsonObject.put("value", (Object)this.fromColumn((Column)value));
        }
        else if (value != null && value.getClass().isArray()) {
            final JSONArray jsonArray = new JSONArray();
            final List<String> primitiveArrayTypes = Arrays.asList("boolean[]", "byte[]", "char[]", "double[]", "float[]", "int[]", "long[]", "short[]");
            if (primitiveArrayTypes.contains(value.getClass().getCanonicalName())) {
                for (int i = 0; i < Array.getLength(value); ++i) {
                    jsonArray.put(Array.get(value, i));
                }
            }
            else {
                final Object[] objectArray = (Object[])value;
                for (int j = 0; j < objectArray.length; ++j) {
                    jsonArray.put(this.convertColumnOrValueObject(objectArray[j]));
                }
            }
            jsonObject.put("value", (Object)jsonArray);
        }
        else {
            jsonObject.put("value", this.convertValueObject(value));
        }
        return jsonObject;
    }
    
    protected Object convertColumnOrValueObject(final Object object) {
        if (object instanceof Column) {
            return this.fromColumn((Column)object);
        }
        return this.convertValueObject(object);
    }
    
    protected Object convertValueObject(Object object) {
        if (object instanceof Date && !(object instanceof java.sql.Date) && !(object instanceof Timestamp) && !(object instanceof Time)) {
            object = new java.sql.Date(((Date)object).getTime());
        }
        return object;
    }
    
    protected JSONArray fromColumns(final List<Column> columns) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < columns.size(); ++i) {
            jsonArray.put((Object)this.fromColumn(columns.get(i)));
        }
        return jsonArray;
    }
    
    public JSONObject fromColumn(final Column column) {
        if (column instanceof DerivedColumn) {
            return this.fromDerivedColumn((DerivedColumn)column);
        }
        if (column instanceof Function) {
            return this.fromFunction((Function)column);
        }
        if (column instanceof Operation) {
            return this.fromOperation((Operation)column);
        }
        if (column instanceof CaseExpression) {
            return this.fromCaseExpression((CaseExpression)column);
        }
        if (column instanceof LocaleColumn) {
            return this.fromLocaleColumn((LocaleColumn)column);
        }
        return this.fromSimpleColumn(column);
    }
    
    protected JSONObject fromSimpleColumn(final Column column) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("column_type", (Object)"column");
        jsonObject.put("tableAlias", (Object)column.getTableAlias());
        jsonObject.put("columnName", (Object)column.getColumnName());
        if (column.getColumnAlias() != null && !column.getColumnAlias().equals(column.getColumnName())) {
            jsonObject.put("columnAlias", (Object)column.getColumnAlias());
        }
        if (column.getColumnIndex() != -1) {
            jsonObject.put("columnIndex", column.getColumnIndex());
        }
        jsonObject.put("dataType", (Object)column.getDataType());
        final int type = column.getType();
        if (type != 1111) {
            jsonObject.put("type", type);
        }
        if (column.getFunction() != 0) {
            jsonObject.put("function", column.getFunction());
            jsonObject.put("function_column", (Object)this.fromColumn(column.getColumn()));
        }
        return jsonObject;
    }
    
    protected JSONObject fromLocaleColumn(final LocaleColumn localeColumn) {
        final JSONObject jsonObject = this.fromSimpleColumn(localeColumn);
        jsonObject.put("column_type", (Object)"locale_column");
        jsonObject.put("locale", (Object)localeColumn.getLocale().toLanguageTag());
        jsonObject.put("column", (Object)this.fromColumn(localeColumn.getColumn()));
        return jsonObject;
    }
    
    protected JSONObject fromCaseExpression(final CaseExpression caseExpression) {
        final JSONObject jsonObject = this.fromSimpleColumn(caseExpression);
        jsonObject.put("column_type", (Object)"case_expression");
        final List<CaseExpression.WhenExpr> whenExpressions = caseExpression.getWhenExpressions();
        if (!whenExpressions.isEmpty()) {
            jsonObject.put("when", (Object)this.fromWhenExpressions(whenExpressions));
        }
        final Object elseVal = caseExpression.getElseVal();
        if (elseVal != null) {
            if (elseVal instanceof Column) {
                jsonObject.put("elseVal", (Object)this.fromColumn((Column)elseVal));
            }
            else {
                jsonObject.put("elseVal", elseVal);
            }
        }
        final Criteria expr = caseExpression.getExpr();
        if (expr != null) {
            jsonObject.put("expr", (Object)this.fromWhenExpressionCriteria(expr));
        }
        jsonObject.put("withCriteria", caseExpression.useAddWhen_With_Criteria);
        return jsonObject;
    }
    
    protected JSONObject fromWhenExpressionCriteria(final Criteria expr) {
        return this.fromCriteria(expr);
    }
    
    protected JSONArray fromWhenExpressions(final List<CaseExpression.WhenExpr> whenExpressions) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < whenExpressions.size(); ++i) {
            jsonArray.put((Object)this.fromWhenExpression(whenExpressions.get(i)));
        }
        return jsonArray;
    }
    
    protected JSONObject fromWhenExpression(final CaseExpression.WhenExpr whenExpr) {
        final JSONObject jsonObject = new JSONObject();
        if (whenExpr.value != null) {
            if (whenExpr.value instanceof Column) {
                jsonObject.put("value", (Object)this.fromWhenExpressionColumn((Column)whenExpr.value));
            }
            else {
                jsonObject.put("value", whenExpr.value);
            }
        }
        if (whenExpr.expr != null) {
            if (whenExpr.expr instanceof Column) {
                jsonObject.put("expr", (Object)this.fromWhenExpressionColumn((Column)whenExpr.value));
            }
            else if (whenExpr.expr instanceof Criteria) {
                jsonObject.put("expr", (Object)this.fromWhenExpressionCriteria((Criteria)whenExpr.expr));
            }
            else {
                jsonObject.put("expr", whenExpr.expr);
            }
        }
        return jsonObject;
    }
    
    protected JSONObject fromWhenExpressionColumn(final Column column) {
        return this.fromColumn(column);
    }
    
    protected JSONObject fromOperation(final Operation operation) {
        final JSONObject jsonObject = this.fromSimpleColumn(operation);
        jsonObject.put("column_type", (Object)"operation");
        jsonObject.put("operationType", (Object)String.valueOf(operation.getOperation()));
        final Object lhsArg = operation.getLHSArgument();
        if (lhsArg instanceof Column) {
            jsonObject.put("lhsArg", (Object)this.fromColumn((Column)lhsArg));
        }
        else {
            jsonObject.put("lhsArg", lhsArg);
        }
        final Object rhsArg = operation.getRHSArgument();
        if (rhsArg instanceof Column) {
            jsonObject.put("rhsArg", (Object)this.fromColumn((Column)rhsArg));
        }
        else {
            jsonObject.put("rhsArg", rhsArg);
        }
        return jsonObject;
    }
    
    protected JSONObject fromFunction(final Function function) {
        final JSONObject jsonObject = this.fromSimpleColumn(function);
        jsonObject.put("column_type", (Object)"function");
        jsonObject.put("functionName", (Object)function.getFunctionName());
        final JSONArray argumentjsonArray = new JSONArray();
        final Object[] functionArgs = function.getFunctionArguments();
        for (int i = 0; i < functionArgs.length; ++i) {
            final Object obj = functionArgs[i];
            if (obj instanceof Column) {
                argumentjsonArray.put((Object)this.fromColumn((Column)obj));
            }
            else if (obj instanceof Function.ReservedParameter) {
                argumentjsonArray.put((Object)this.fromReservedParameter((Function.ReservedParameter)obj));
            }
            else {
                argumentjsonArray.put(obj);
            }
        }
        jsonObject.put("arguments", (Object)argumentjsonArray);
        return jsonObject;
    }
    
    protected JSONObject fromReservedParameter(final Function.ReservedParameter reservedParameter) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("reserved_parameter", (Object)reservedParameter.getParamValue());
        return jsonObject;
    }
    
    protected JSONObject fromDerivedColumn(final DerivedColumn derivedColumn) {
        final JSONObject jsonObject = this.fromSimpleColumn(derivedColumn);
        jsonObject.put("column_type", (Object)"derived_column");
        jsonObject.put("subQuery", (Object)this.fromQuery(derivedColumn.getSubQuery()));
        return jsonObject;
    }
    
    protected JSONObject fromSelectTable(final Table table) {
        return this.fromTable(table);
    }
    
    protected JSONObject fromTable(final Table table) {
        if (table instanceof DerivedTable) {
            return this.fromDerivedTable((DerivedTable)table);
        }
        return this.fromSimpleTable(table);
    }
    
    protected JSONObject fromSimpleTable(final Table table) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("tableName", (Object)table.getTableName());
        if (!Objects.equals(table.getTableName(), table.getTableAlias())) {
            jsonObject.put("tableAlias", (Object)table.getTableAlias());
        }
        return jsonObject;
    }
    
    protected JSONObject fromDerivedTable(final DerivedTable derivedTable) {
        final JSONObject jsonObject = this.fromSimpleTable(derivedTable);
        jsonObject.put("subQuery", (Object)this.fromQuery(derivedTable.getSubQuery()));
        return jsonObject;
    }
    
    protected JSONObject fromUnionQuery(final UnionQuery unionQuery) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("query_type", (Object)"unionQuery");
        jsonObject.put("leftQuery", (Object)this.fromQuery(unionQuery.getLeftQuery()));
        jsonObject.put("rightQuery", (Object)this.fromQuery(unionQuery.getRightQuery()));
        jsonObject.put("retainDuplicateRows", unionQuery.isRetainDuplicateRows());
        if (unionQuery.getRange() != null) {
            jsonObject.put("range", (Object)this.fromRange(unionQuery.getRange()));
        }
        if (!unionQuery.getSortColumns().isEmpty()) {
            jsonObject.put("sortColumns", (Object)this.fromSortColumns(unionQuery.getSortColumns()));
        }
        return jsonObject;
    }
    
    public Query toQuery(final JSONObject jsonObject) {
        if (jsonObject.get("query_type").equals("selectQuery")) {
            return this.toSelectQuery(jsonObject);
        }
        return this.toUnionQuery(jsonObject);
    }
    
    protected UnionQueryImpl toUnionQuery(final JSONObject jsonObject) {
        final Query leftQuery = this.toQuery(jsonObject.getJSONObject("leftQuery"));
        final Query rightQuery = this.toQuery(jsonObject.getJSONObject("rightQuery"));
        final boolean retainDuplicateRows = jsonObject.getBoolean("retainDuplicateRows");
        final UnionQueryImpl uq = new UnionQueryImpl(leftQuery, rightQuery, retainDuplicateRows);
        if (jsonObject.has("range")) {
            uq.setRange(this.toRange(jsonObject.getJSONObject("range")));
        }
        if (jsonObject.has("sortColumns")) {
            final JSONArray jsonArray = jsonObject.getJSONArray("sortColumns");
            final List<SortColumn> sortColumns = this.toSortColumns(jsonArray);
            sortColumns.forEach(uq::addSortColumn);
        }
        return uq;
    }
    
    protected List<SortColumn> toSortColumns(final JSONArray jsonArray) {
        final List<SortColumn> sortColumns = new ArrayList<SortColumn>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            sortColumns.add(this.toSortColumn(jsonArray.getJSONObject(i)));
        }
        return sortColumns;
    }
    
    protected SortColumn toSortColumn(final JSONObject jsonObject) {
        final Column column = this.toColumn(jsonObject.getJSONObject("column"));
        final boolean ascending = jsonObject.getBoolean("isAscending");
        boolean caseSensitive = false;
        if (jsonObject.has("isCaseSensitive")) {
            caseSensitive = jsonObject.getBoolean("isCaseSensitive");
        }
        Boolean isNullsFirst = null;
        if (jsonObject.has("isNullsFirst")) {
            isNullsFirst = jsonObject.getBoolean("isNullsFirst");
        }
        final SortColumn sortColumn = new SortColumn(column, ascending, caseSensitive, isNullsFirst);
        return sortColumn;
    }
    
    public Column toColumn(final JSONObject jsonObject) {
        final String string;
        final String columnType = string = jsonObject.getString("column_type");
        switch (string) {
            case "function": {
                return this.toFunction(jsonObject);
            }
            case "operation": {
                return this.toOperation(jsonObject);
            }
            case "derived_column": {
                return this.toDerivedColumn(jsonObject);
            }
            case "case_expression": {
                return this.toCaseExpression(jsonObject);
            }
            case "locale_column": {
                return this.toLocaleColumn(jsonObject);
            }
            default: {
                if (jsonObject.has("function")) {
                    final Column subColumn = this.generateColumn(jsonObject.getJSONObject("function_column"));
                    final Column column = this.generateColumn(jsonObject);
                    column.setFunction(jsonObject.getInt("function"));
                    column.setColumn(subColumn);
                    return column;
                }
                return this.generateColumn(jsonObject);
            }
        }
    }
    
    protected LocaleColumn toLocaleColumn(final JSONObject jsonObject) {
        final String localeLanguageTag = jsonObject.getString("locale");
        final Locale locale = Locale.forLanguageTag(localeLanguageTag);
        final Column column = this.toColumn(jsonObject.getJSONObject("column"));
        final LocaleColumn localeColumn = new LocaleColumn(column, locale);
        this.generateColumn(localeColumn, jsonObject);
        return localeColumn;
    }
    
    protected CaseExpression toCaseExpression(final JSONObject jsonObject) {
        String columnAlias;
        if (jsonObject.has("columnAlias")) {
            columnAlias = jsonObject.getString("columnAlias");
        }
        else {
            columnAlias = jsonObject.getString("columnName");
        }
        CaseExpression caseExpression;
        if (jsonObject.has("expr")) {
            final Criteria expr = this.tocaseExpressionCriteria(jsonObject.getJSONObject("expr"));
            caseExpression = new CaseExpression(expr, columnAlias);
        }
        else {
            caseExpression = new CaseExpression(columnAlias);
            boolean whenWithCriteria = false;
            if (jsonObject.has("withCriteria")) {
                whenWithCriteria = jsonObject.getBoolean("withCriteria");
            }
            if (whenWithCriteria) {
                caseExpression.useAddWhen_With_Criteria = true;
            }
            else {
                caseExpression.useAddWhen_WO_Criteria = true;
                caseExpression.useAddWhen_With_Criteria = false;
            }
        }
        this.generateColumn(caseExpression, jsonObject);
        if (jsonObject.has("when")) {
            final JSONArray jsonArray = jsonObject.getJSONArray("when");
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject jo = jsonArray.getJSONObject(i);
                Object value = null;
                if (jo.has("value")) {
                    value = this.getAsColumnOrObject(jo.get("value"));
                }
                if (caseExpression.useAddWhen_With_Criteria) {
                    Criteria expr2 = null;
                    if (jo.has("expr")) {
                        expr2 = this.toCriteria(jo.getJSONObject("expr"));
                    }
                    caseExpression.addWhen(expr2, value);
                }
                else {
                    Object expr3 = null;
                    if (jo.has("expr")) {
                        expr3 = this.getAsColumnOrObject(jo.get("expr"));
                    }
                    caseExpression.addWhen(expr3, value);
                }
            }
        }
        if (jsonObject.has("elseVal")) {
            caseExpression.elseVal(this.getAsColumnOrObject(jsonObject.get("elseVal")));
        }
        return caseExpression;
    }
    
    protected Criteria tocaseExpressionCriteria(final JSONObject jsonObject) {
        return this.toCriteria(jsonObject);
    }
    
    protected DerivedColumn toDerivedColumn(final JSONObject jsonObject) {
        final String columnName = jsonObject.getString("columnName");
        final Query subQuery = this.toQuery(jsonObject.getJSONObject("subQuery"));
        final DerivedColumn dc = new DerivedColumn(columnName, (SelectQuery)subQuery);
        this.generateColumn(dc, jsonObject);
        return dc;
    }
    
    protected Operation toOperation(final JSONObject jsonObject) {
        final Object lhsArg = this.getAsColumnOrObject(jsonObject.get("lhsArg"));
        final Object rhsArg = this.getAsColumnOrObject(jsonObject.get("rhsArg"));
        final Operation.operationType opType = Operation.operationType.valueOf(jsonObject.getString("operationType"));
        final Operation operation = new Operation(opType, lhsArg, rhsArg);
        this.generateColumn(operation, jsonObject);
        return operation;
    }
    
    protected Function toFunction(final JSONObject jsonObject) {
        final String functionName = jsonObject.getString("functionName");
        Object[] args;
        if (jsonObject.has("arguments")) {
            final JSONArray argumentjsonArray = jsonObject.getJSONArray("arguments");
            args = new Object[argumentjsonArray.length()];
            for (int i = 0; i < argumentjsonArray.length(); ++i) {
                args[i] = this.getAsColumnOrObjectOrReserved(argumentjsonArray.get(i));
            }
        }
        else {
            args = new Object[0];
        }
        final Function function = new Function(functionName, args);
        this.generateColumn(function, jsonObject);
        return function;
    }
    
    protected Column generateColumn(final JSONObject jsonObject) {
        final Column column = new Column();
        this.generateColumn(column, jsonObject);
        return column;
    }
    
    protected void generateColumn(final Column column, final JSONObject jsonObject) {
        if (jsonObject.has("tableAlias")) {
            column.setTableAlias(jsonObject.getString("tableAlias"));
        }
        if (jsonObject.has("columnName")) {
            column.setColumnName(jsonObject.getString("columnName"));
        }
        if (jsonObject.has("columnAlias")) {
            column.setColumnAlias(jsonObject.getString("columnAlias"));
        }
        else if (jsonObject.has("columnName")) {
            column.setColumnAlias(jsonObject.getString("columnName"));
        }
        if (jsonObject.has("columnIndex")) {
            column.setColumnIndex(jsonObject.getInt("columnIndex"));
        }
        if (jsonObject.has("dataType")) {
            column.setDataType(jsonObject.getString("dataType"));
        }
        if (jsonObject.has("type")) {
            column.setType(jsonObject.getInt("type"));
        }
    }
    
    protected Range toRange(final JSONObject jsonObject) {
        final int startIndex = jsonObject.getInt("from");
        final int numOfObjects = jsonObject.getInt("count");
        return new Range(startIndex, numOfObjects);
    }
    
    protected SelectQueryImpl toSelectQuery(final JSONObject jsonObject) {
        final Table table = this.toTable(jsonObject.getJSONObject("table"));
        final SelectQueryImpl sq = new SelectQueryImpl(table);
        final JSONArray selectColumnsJson = jsonObject.getJSONArray("selectColumns");
        final List<Column> selectColumns = this.toColumns(selectColumnsJson);
        selectColumns.forEach(sq::addSelectColumn);
        if (jsonObject.has("criteria")) {
            sq.setCriteria(this.toCriteria(jsonObject.getJSONObject("criteria")));
        }
        if (jsonObject.has("range")) {
            sq.setRange(this.toRange(jsonObject.getJSONObject("range")));
        }
        if (jsonObject.has("sortColumns")) {
            final JSONArray jsonArray = jsonObject.getJSONArray("sortColumns");
            final List<SortColumn> sortColumns = this.toSortColumns(jsonArray);
            sq.addSortColumns(sortColumns);
        }
        if (jsonObject.has("groupByClause")) {
            sq.setGroupByClause(this.toGroupByClause(jsonObject.getJSONObject("groupByClause")));
        }
        else if (jsonObject.has("groupByColumns")) {
            final JSONArray groupByColumnsJson = jsonObject.getJSONArray("groupByColumns");
            final List<?> groupByColumns = this.toGroupyByColumns(groupByColumnsJson);
            sq.addGroupByColumns(groupByColumns);
        }
        if (jsonObject.has("join")) {
            final JSONArray jsonArray = jsonObject.getJSONArray("join");
            final List<Join> joins = this.toJoins(jsonArray);
            joins.forEach(sq::addJoin);
        }
        if (jsonObject.has("distinct")) {
            sq.setDistinct(jsonObject.getBoolean("distinct"));
        }
        if (jsonObject.has("lock")) {
            sq.setLock(jsonObject.getBoolean("lock"));
        }
        if (jsonObject.has("parallel_workers")) {
            sq.setParallelSelect(jsonObject.getBoolean("parallel"), jsonObject.getInt("parallel_workers"));
        }
        return sq;
    }
    
    protected List<Column> toColumns(final JSONArray selectColumnsJson) {
        final List<Column> columns = new ArrayList<Column>(selectColumnsJson.length());
        for (int i = 0; i < selectColumnsJson.length(); ++i) {
            columns.add((Column)this.getAsColumnOrObject(selectColumnsJson.get(i)));
        }
        return columns;
    }
    
    public List<Join> toJoins(final JSONArray jsonArray) {
        final List<Join> joins = new ArrayList<Join>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject joinJson = jsonArray.getJSONObject(i);
            joins.add(this.toJoin(joinJson));
        }
        return joins;
    }
    
    public Join toJoin(final JSONObject jsonObject) {
        final Table referencedTable = this.toTable(jsonObject.getJSONObject("referencedTable"));
        final Table baseTable = this.toTable(jsonObject.getJSONObject("baseTable"));
        final int joinType = jsonObject.getInt("joinType");
        Join join;
        if (jsonObject.has("criteria")) {
            final Criteria criteria = this.toJoinCriteria(jsonObject.getJSONObject("criteria"));
            join = new Join(baseTable, referencedTable, criteria, joinType);
        }
        else {
            final JSONArray baseTableColumnsJsonArray = jsonObject.getJSONArray("baseTableColumns");
            final JSONArray referencedTableColumnsJsonArray = jsonObject.getJSONArray("referencedTableColumns");
            final String[] baseTableColumns = new String[baseTableColumnsJsonArray.length()];
            final String[] referencedTableColumns = new String[referencedTableColumnsJsonArray.length()];
            for (int i = 0; i < baseTableColumns.length; ++i) {
                baseTableColumns[i] = baseTableColumnsJsonArray.getString(i);
                referencedTableColumns[i] = referencedTableColumnsJsonArray.getString(i);
            }
            join = new Join(baseTable, referencedTable, baseTableColumns, referencedTableColumns, joinType);
        }
        return join;
    }
    
    protected Criteria toJoinCriteria(final JSONObject jsonObject) {
        return this.toCriteria(jsonObject);
    }
    
    protected List<?> toGroupyByColumns(final JSONArray groupByColumnsJson) {
        final List groupByColumns = new ArrayList(groupByColumnsJson.length());
        for (int i = 0; i < groupByColumnsJson.length(); ++i) {
            final Object columnObject = this.getAsColumnOrObject(groupByColumnsJson.get(i));
            if (columnObject instanceof Column) {
                groupByColumns.add(columnObject);
            }
            else {
                groupByColumns.add(this.toGroupyByColumn((JSONObject)columnObject));
            }
        }
        return groupByColumns;
    }
    
    protected GroupByColumn toGroupyByColumn(final JSONObject jsonObject) {
        final Column column = this.toColumn(jsonObject.getJSONObject("column"));
        final boolean caseSensitive = jsonObject.getBoolean("caseSensitive");
        return new GroupByColumn(column, caseSensitive);
    }
    
    protected GroupByClause toGroupByClause(final JSONObject jsonObject) {
        Criteria criteria = null;
        if (jsonObject.has("havingCriteria")) {
            criteria = this.toGroupByCriteria(jsonObject.getJSONObject("havingCriteria"));
        }
        final JSONArray groupByColumnsJson = jsonObject.getJSONArray("columns");
        final List groupByColumns = this.toGroupyByColumns(groupByColumnsJson);
        return new GroupByClause(groupByColumns, criteria);
    }
    
    protected Criteria toGroupByCriteria(final JSONObject jsonObject) {
        return this.toCriteria(jsonObject);
    }
    
    public Criteria toCriteria(final JSONObject jsonObject) {
        final Criteria c = new Criteria();
        if (jsonObject.has("negated") && jsonObject.getBoolean("negated")) {
            c.negate();
        }
        if (jsonObject.has("criterion")) {
            final JSONObject criterionJsonObject = jsonObject.getJSONObject("criterion");
            try {
                this.toCriterion(c, criterionJsonObject);
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if (jsonObject.has("leftCriteria")) {
            final Criteria leftCriteria = this.toCriteria(jsonObject.getJSONObject("leftCriteria"));
            c.setLeftCriteria(leftCriteria);
        }
        if (jsonObject.has("rightCriteria")) {
            final String operator = jsonObject.getString("operator");
            final Criteria rightCriteria = this.toCriteria(jsonObject.getJSONObject("rightCriteria"));
            c.setOperator(operator);
            c.setRightCriteria(rightCriteria);
        }
        return c;
    }
    
    protected void toCriterion(final Criteria criteria, final JSONObject jsonObject) throws Exception {
        final Column column = this.toColumn(jsonObject.getJSONObject("column"));
        final int comparator = jsonObject.getInt("comparator");
        final boolean caseSensitive = jsonObject.getBoolean("caseSensitive");
        Object value = null;
        if (jsonObject.has("value")) {
            final Object valueObject = jsonObject.get("value");
            if (valueObject instanceof JSONObject) {
                value = this.toColumn((JSONObject)valueObject);
            }
            else if (valueObject instanceof JSONArray) {
                final JSONArray valueArray = (JSONArray)valueObject;
                final Object[] values = new Object[valueArray.length()];
                boolean hasColumn = false;
                for (int i = 0; i < values.length; ++i) {
                    values[i] = (valueArray.isNull(i) ? null : this.getAsColumnOrObject(valueArray.get(i)));
                    if (!hasColumn && values[i] instanceof Column) {
                        hasColumn = true;
                    }
                }
                if (hasColumn) {
                    value = values;
                }
                else {
                    final String[] valuesAsArray = new String[values.length];
                    for (int j = 0; j < values.length; ++j) {
                        valuesAsArray[j] = ((values[j] == null) ? null : values[j].toString());
                    }
                    value = QueryUtil.getArray(valuesAsArray, column.getDataType());
                }
            }
            else if (valueObject instanceof String) {
                value = MetaDataUtil.convert((String)valueObject, column.getDataType());
            }
            else {
                value = valueObject;
            }
        }
        criteria.setCriterion(column, value, comparator, caseSensitive);
    }
    
    protected Table toTable(final JSONObject jsonObject) {
        if (jsonObject.has("subQuery")) {
            return this.toDerivedTable(jsonObject);
        }
        if (jsonObject.has("tableAlias")) {
            return new Table(jsonObject.getString("tableName"), jsonObject.getString("tableAlias"));
        }
        return new Table(jsonObject.getString("tableName"));
    }
    
    protected DerivedTable toDerivedTable(final JSONObject jsonObject) {
        final Query subQuery = this.toQuery(jsonObject.getJSONObject("subQuery"));
        String tableAlias;
        if (jsonObject.has("tableAlias")) {
            tableAlias = jsonObject.getString("tableAlias");
        }
        else {
            tableAlias = jsonObject.getString("tableName");
        }
        final DerivedTable derivedTable = new DerivedTable(tableAlias, subQuery);
        return derivedTable;
    }
    
    protected Object getAsColumnOrObjectOrReserved(final Object object) {
        if (object instanceof JSONObject && ((JSONObject)object).has("reserved_parameter")) {
            return new Function.ReservedParameter(((JSONObject)object).getString("reserved_parameter"));
        }
        return this.getAsColumnOrObject(object);
    }
    
    protected Object getAsColumnOrObject(final Object object) {
        if (object instanceof JSONObject && ((JSONObject)object).has("column_type")) {
            return this.toColumn((JSONObject)object);
        }
        return object;
    }
}
